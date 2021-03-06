package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.common.constants.ProductConstant;
import com.avengereug.mall.product.entity.*;
import com.avengereug.mall.product.service.*;
import com.avengereug.mall.product.vo.AttrRespVO;
import com.avengereug.mall.product.vo.AttrVO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.AttrDao;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {


    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @GlobalTransactional
    @Override
    public void saveDetail(AttrVO attr) {
        // 1. 插入attr
        AttrEntity attrEntity = new AttrEntity();
        // 因为在为属性分组关联属性时，只能关联当前分类中没有被关联的属性，所以保证了一个属性只能在一个分组中存在。
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);

        // 只有基础属性，才更新group中间表
        if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attr.getAttrType() &&
                (attr.getAttrGroupId() != null || attr.getAttrId() != null)) {
            // 2. 插入属性分组与属性关联关系表中的属性分组信息
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            entity.setAttrGroupId(attr.getAttrGroupId());
            // 此id在mybatis插入后，会自动填充至对象中。
            entity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationService.save(entity);
        }
    }

    @Override
    public PageUtils queryBaseAttrListPage(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");

        if (catelogId > 0) {
            wrapper.eq("catelog_id", catelogId);
        }

        if (StringUtils.isNotEmpty(type)) {
            wrapper.eq("attr_type",
                    ProductConstant.AttrEnum.ATTR_TYPE_BASE.getAlias().equals(type) ?
                    ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                    ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        }

        if (StringUtils.isNotEmpty(key)) {
            // 根据key和attrName进行筛选
            wrapper.and(item -> {
                item.eq("attr_id", key).or().like("attr_name", key);
            });

        }

        // 开始分页查询
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                wrapper
        );

        // 组装attrEntity，使之包含属于哪个组，属于哪个分类
        PageUtils pageUtils = new PageUtils(page);
        List<AttrRespVO> attrRespVOS = page.getRecords().stream().map(attrEntity -> {
            AttrRespVO attrRespVo = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVo);

            if (ObjectUtils.isNotEmpty(attrEntity.getCatelogId())) {
                CategoryEntity categoryEntity = categoryService.getById(attrEntity.getCatelogId());
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            // 只有基础属性才有分组
            if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.getAlias().equals(type)) {
                // 再根据groupId和catelogId来查找它的名称
                // 因为在为属性分组关联属性时，只能关联当前分类中没有被关联的属性，所以保证了一个属性只能在一个分组中存在。
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
                );

                // 因为在创建属性时，可以不指定所属分组，所以查询时，这里有可能查出来的为null
                if (attrAttrgroupRelationEntity != null && attrAttrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrAttrgroupRelationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }

            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(attrRespVOS);

        return pageUtils;
    }

    @Override
    public AttrRespVO getAttrRespVoById(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);

        AttrRespVO attrRespVo = new AttrRespVO();
        BeanUtils.copyProperties(attrEntity, attrRespVo);


        // 填充catelogPath
        if (attrEntity.getCatelogId() != null) {
            List<Long> catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
            attrRespVo.setCatelogPath(catelogPath);
        }

        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            // 填充当前属性所属分组
            // 因为在为属性分组关联属性时，只能关联当前分类中没有被关联的属性，所以保证了一个属性只能在一个分组中存在。
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
            );
            if (relationEntity != null) {
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
            }
        }

        return attrRespVo;
    }

    @GlobalTransactional
    @Override
    public void updateDetail(AttrVO attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        // 1. 更新自己，
        this.updateById(attrEntity);


        // 只有基础属性，才更新group中间表
        if (ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() == attrVo.getAttrType()) {
            // 2. 更新所属的分组
            // 2.1）、当前属性一开始创建时，未指定分组，此时更新添加了分组，这时就是要插入
            // 2.2）、当前属性一开始创建时，指定了分组，此时更新修改了分组，这时就是更新
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrVo.getAttrId());
            int count = attrAttrgroupRelationService.count(
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
            );
            if (count > 0) {
                attrAttrgroupRelationService.update(relationEntity,
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId())
                );
            } else {
                attrAttrgroupRelationService.save(relationEntity);
            }
        }
    }

    @Override
    public List<AttrEntity> selectBatchIds(List<Long> attrIds) {
        return baseMapper.selectBatchIds(attrIds);
    }

    @Override
    public List<ProductAttrValueEntity> getSpuBaseAttr(Long spuId) {
        return productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId)
        );
    }

}