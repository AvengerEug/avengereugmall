package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.product.entity.SkuImagesEntity;
import com.avengereug.mall.product.entity.SpuInfoDescEntity;
import com.avengereug.mall.product.service.*;
import com.avengereug.mall.product.vo.SkuItemSaleAttrVO;
import com.avengereug.mall.product.vo.SkuItemVO;
import com.avengereug.mall.product.vo.SpuItemAttrGroupVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.SkuInfoDao;
import com.avengereug.mall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService attrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();

        /**
         * key: '华为',
         * catelogId: 0,
         * brandId: 0,
         * min: 0,
         * max: 0
         */
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(item -> item.eq("sku_id", key).or().like("sku_name", key));
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catelog_id", catelogId);
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("brand_id", brandId);
        }

        String min = (String) params.get("min");
        if (StringUtils.isNotEmpty(min) && Integer.valueOf(min) > 0) {
            wrapper.ge("price", new BigDecimal(min));
        }

        String max = (String) params.get("max");
        if (StringUtils.isNotEmpty(max) && Integer.valueOf(max) > 0) {
            wrapper.le("price", new BigDecimal(max));
        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }


    @Override
    public SkuItemVO item(Long skuId) {
        SkuItemVO skuItemVO = new SkuItemVO();
        // 1、sku基本信息  --> pms_sku_info
        SkuInfoEntity skuInfoEntity = getById(skuId);
        skuItemVO.setInfo(skuInfoEntity);
        Long spuId = skuInfoEntity.getSpuId();
        Long categoryId = skuInfoEntity.getCatelogId();

        // 2、sku图片信息 --> pms_sku_images
        List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
        skuItemVO.setImages(images);

        // 3、sku对应的spu销售属性
        List<SkuItemSaleAttrVO> skuItemSaleAttrVOs = attrValueService.getSaleAttrsBySpuId(spuId);
        skuItemVO.setSaleAttr(skuItemSaleAttrVOs);

        // 4、spu商品介绍
        SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(spuId);
        skuItemVO.setDesp(spuInfoDescEntity);

        // 5、spu的规格参数信息
        List<SpuItemAttrGroupVO> spuItemAttrGroupVOS = attrGroupService.getAttrGroupWithAttrsBySpuIdAndCategoryLog(spuId, categoryId);
        skuItemVO.setGroupAttrs(spuItemAttrGroupVOS);

        return skuItemVO;
    }
}