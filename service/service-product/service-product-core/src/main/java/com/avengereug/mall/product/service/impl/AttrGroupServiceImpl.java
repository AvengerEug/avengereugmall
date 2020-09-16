package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.constants.ProductConstant;
import com.avengereug.mall.product.entity.AttrAttrgroupRelationEntity;
import com.avengereug.mall.product.entity.AttrEntity;
import com.avengereug.mall.product.service.AttrAttrgroupRelationService;
import com.avengereug.mall.product.service.AttrService;
import com.avengereug.mall.product.vo.AttrGroupRelationVO;
import com.avengereug.mall.product.vo.SpuItemAttrGroupVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.AttrGroupDao;
import com.avengereug.mall.product.entity.AttrGroupEntity;
import com.avengereug.mall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                // like  %key%
                obj.eq("attr_group_id", key).or().like( "attr_group_name", key);
            });
        }

        // 如果传0  ==>  查询所有
        if (catelogId == 0) {
            return new PageUtils(this.page(new Query<AttrGroupEntity>().getPage(params), wrapper));
        } else {
            wrapper.eq("catelog_id", catelogId);
            return new PageUtils(this.page(new Query<AttrGroupEntity>().getPage(params), wrapper));
        }
    }

    @Override
    public List<AttrEntity> relationInfo(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrGroupId)
        );
        // 直接拿到所有的ids，再使用in去查找，不需要遍历每个item再查一次attrEntity
        List<Long> attrIds = list.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

        return attrIds.size() > 0 ? attrService.selectBatchIds(attrIds) : null;
    }

    @Override
    public void deleteRelation(List<AttrGroupRelationVO> relationVos) {
        // 1. 根据这两个字段删除 attrGroup记录

        List<AttrAttrgroupRelationEntity> entityList = relationVos.stream().map(item -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entity);
            return entity;
        }).collect(Collectors.toList());

        attrAttrgroupRelationService.deleteBatchRelation(entityList);

    }

    /**
     * 获取当前分组没有被关联的属性
     *
     * 1、当前分组只能关联自己所属的分类里的基本属性
     * 2、当前分组只能关联别的分组没有引用的基本属性
     * 2.1）、获取当前分类下的所有分组
     * 2.2）、获取所有分组关联的属性
     * 2.3）、从当前分类的所有属性中移除上述2.1和2.2的属性
     * @param params
     * @param attrGroupId
     * @return
     */
    @Override
    public PageUtils noRelationInfoPage(Map<String, Object> params, Long attrGroupId) {
        // 1. 获取attrGroup
        AttrGroupEntity attrGroupEntity = baseMapper.selectById(attrGroupId);
        IPage<AttrEntity> iPage = new Query<AttrEntity>().getPage(params);
        PageUtils pageUtils = new PageUtils(iPage);

        // 2. 拿到当前分组所属的分类，并根据分类去拿到当前分类下有哪些基本属性(分页)
        if (attrGroupEntity != null) {
            // 2.1 获取当前分类下的所有分组
            List<AttrGroupEntity> attrGroupEntities = baseMapper.selectList(
                    new QueryWrapper<AttrGroupEntity>()
                            .eq("catelog_id", attrGroupEntity.getCatelogId())
            );
            List<Long> attrGroupIds = attrGroupEntities.stream().map(item -> item.getAttrGroupId()).collect(Collectors.toList());

            // 2.2 获取查询出来分组关联的所有属性
            List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(
                    new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .in("attr_group_id", attrGroupIds)
            );
            List<Long> longList = relationEntities.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

            // 2.3 分页查出当前分类中包含的所有基础属性，且不包含longList中的id
            QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
            if (longList.size() > 0) {
                wrapper.notIn("attr_id", longList);
            }

            String key = (String) params.get("key");
            if (StringUtils.isNotEmpty(key)) {
                wrapper.and(item -> item.eq("attr_id", key).or().like("attr_name", key));
            }

            attrService.page(iPage, wrapper);
            pageUtils.setList(iPage.getRecords());
        }

        return pageUtils;
    }

    /**
     * 添加
     * @param attrGroupRelationVOS
     */
    @Override
    public void addRelation(List<AttrGroupRelationVO> attrGroupRelationVOS) {
        // 直接插入attrAttrGroup中间表即可
        List<AttrAttrgroupRelationEntity> entities = attrGroupRelationVOS.stream().map(item -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entity);
            return entity;
        }).collect(Collectors.toList());

        // 也可以调用this.saveBatch api
        attrAttrgroupRelationService.insertBatch(entities);
    }

    @Override
    public List<AttrEntity> findAttrByAttrGroupId(Long attrGroupId) {
        // 1、根据attrGroupId找出所有的attrId
        List<Long> attrIds = attrAttrgroupRelationService.findAttrIdsByAttrGroupId(attrGroupId);

        // 2、in查询所有的attr
        if (attrIds != null && attrIds.size() > 0) {

            List<AttrEntity> attrEntityList = attrService.list(
                    new QueryWrapper<AttrEntity>()
                            .in("attr_id", attrIds)
            );
            return attrEntityList;
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public List<SpuItemAttrGroupVO> getAttrGroupWithAttrsBySpuIdAndCategoryLog(Long spuId, Long catelogId) {
        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catelogId);
    }
}