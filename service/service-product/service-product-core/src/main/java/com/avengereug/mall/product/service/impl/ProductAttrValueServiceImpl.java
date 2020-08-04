package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.anno.GlobalTransactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.ProductAttrValueDao;
import com.avengereug.mall.product.entity.ProductAttrValueEntity;
import com.avengereug.mall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @GlobalTransactional
    @Override
    public void updateSpuBaseAttr(Long spuId, List<ProductAttrValueEntity> productAttrValueEntity) {
        // 直接删除
        this.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        productAttrValueEntity.forEach( item -> {
            item.setSpuId(spuId);
        });

        this.saveBatch(productAttrValueEntity);
    }

}