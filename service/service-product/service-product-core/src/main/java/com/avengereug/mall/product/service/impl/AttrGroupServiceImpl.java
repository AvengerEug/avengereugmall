package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.constants.ProductConstant;
import com.avengereug.mall.product.dao.AttrAttrgroupRelationDao;
import com.avengereug.mall.product.entity.AttrAttrgroupRelationEntity;
import com.avengereug.mall.product.entity.AttrEntity;
import com.avengereug.mall.product.service.AttrAttrgroupRelationService;
import com.avengereug.mall.product.service.AttrService;
import com.avengereug.mall.product.vo.AttrGroupRelationVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

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
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrGroupId)
        );
        // 直接拿到所有的ids，再使用in去查找，不需要遍历每个item再查一次attrEntity
        List<Long> attrIds = list.stream().map(item -> item.getAttrId()).collect(Collectors.toList());

        return (List<AttrEntity>) attrService.listByIds(attrIds);
    }

    @Override
    public void deleteRelation(List<AttrGroupRelationVo> relationVos) {
        // 1. 根据这两个字段删除 attrGroup记录

        List<AttrAttrgroupRelationEntity> entityList = relationVos.stream().map(item -> {
            AttrAttrgroupRelationEntity entity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, entity);
            return entity;
        }).collect(Collectors.toList());

        attrAttrgroupRelationDao.deleteBatchRelation(entityList);

    }
}