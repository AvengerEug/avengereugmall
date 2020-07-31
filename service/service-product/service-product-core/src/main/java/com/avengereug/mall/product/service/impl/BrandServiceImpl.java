package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.BrandDao;
import com.avengereug.mall.product.entity.BrandEntity;
import com.avengereug.mall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("brand_id", key).or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    @GlobalTransactional
    @Override
    public void updateCascade(BrandEntity brand) {
        // 保证冗余字段数据的一致性
        this.updateById(brand);
        if (StringUtils.isNotEmpty(brand.getName())) {
            // 更新其他关联表的数据
            // 1. 更新分类与品牌关联关系的冗余字段
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());

            // TODO 还需要更新其他的冗余字段
        }
    }

    @Override
    public String getName(Long brandId) {

        return baseMapper.selectNameByBrandId(brandId);
    }

}