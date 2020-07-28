package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.product.entity.AttrAttrgroupRelationEntity;
import com.avengereug.mall.product.entity.AttrEntity;
import com.avengereug.mall.product.service.AttrAttrgroupRelationService;
import com.avengereug.mall.product.service.AttrService;
import org.apache.commons.lang.StringUtils;
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

import com.avengereug.mall.product.dao.AttrGroupDao;
import com.avengereug.mall.product.entity.AttrGroupEntity;
import com.avengereug.mall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {


    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrService attrService;

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
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId)
        );

        return list.stream().map((item) -> attrService.getById(item.getAttrId())).collect(Collectors.toList());

    }
}