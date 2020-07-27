package com.avengereug.mall.product.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
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
        // 如果传0  ==>  查询所有
        if (catelogId == 0) {
            return new PageUtils(this.page(new Query<AttrGroupEntity>().getPage(params), new QueryWrapper<>()));
        }

        // 1.按照三级分类 和 key查询  ==> 前端传入key时，默认对应db中的attr_group_name模糊查找和attr_group_id精确查找
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId);

        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                // like  %key%
                obj.eq("attr_group_id", key).or().like( "attr_group_name", key);
            });
        }


        return new PageUtils(this.page(new Query<AttrGroupEntity>().getPage(params), wrapper));
    }
}