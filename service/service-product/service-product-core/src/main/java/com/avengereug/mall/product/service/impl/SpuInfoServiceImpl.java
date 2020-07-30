package com.avengereug.mall.product.service.impl;

import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.coupon.feign.MemberPriceClient;
import com.avengereug.mall.coupon.feign.SkuFullReductionClient;
import com.avengereug.mall.coupon.feign.SkuLadderClient;
import com.avengereug.mall.coupon.feign.SpuBoundsClient;
import com.avengereug.mall.coupon.to.MemberPriceTO;
import com.avengereug.mall.coupon.to.SkuFullReductionTO;
import com.avengereug.mall.coupon.to.SkuLadderTO;
import com.avengereug.mall.coupon.to.SpuBoundsTO;
import com.avengereug.mall.product.entity.*;
import com.avengereug.mall.product.service.*;
import com.avengereug.mall.product.vo.spusave.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.product.dao.SpuInfoDao;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    private static final Logger logger = LoggerFactory.getLogger(SpuInfoServiceImpl.class);

    private static final String PMS_SALE_ATTR = "saleAttr";
    private static final String PMS_SKU_INFO = "skuInfo";
    private static final String PMS_SKU_IMAGES = "skuImages";
    private static final String SMS_SKU_LADDER = "skuLadder";
    private static final String SMS_SPU_BOUNDS = "spuBoundsd";

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService saveSkuBaseInfo;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuFullReductionClient skuFullReductionClient;

    @Autowired
    private SkuLadderClient skuLadderClient;

    @Autowired
    private MemberPriceClient memberPriceClient;

    @Autowired
    private SpuBoundsClient spuBoundsClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 1. 存在分布式事务问题
     * 2. 教研传入数据的合法性
     *
     * @param spuSaveVo
     */
    @GlobalTransactional
    @Override
    public void save(SpuSaveVO spuSaveVo) {
        // 1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfo = saveSpuBaseInfo(spuSaveVo);

        // 2、保存spu的描述图片 pms_spu_info_desc(多张大图), 用逗号隔开
        saveSpuInfoDesc(spuInfo.getId(), spuSaveVo.getDecript());

        // 3、保存spu的图片集 pms_sku_images
        saveBatchSpuImages(spuInfo.getId(), spuSaveVo.getImages());

        // 4、保存spu的基本属性(规格参数) pms_product_attr_value
        saveBatchSkuBaseAttrValue(spuInfo.getId(), spuSaveVo.getBaseAttrs());

        // 5、保存当前spu对应的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.stream().forEach(sku -> {

                // 5.1）、保存sku基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = saveSkuInfoEntity(spuSaveVo, spuInfo, sku);

                // 5.2）、保存sku销售属性 pms_sku_sale_attr_value
                saveBatchSkuSaleAttrValue(sku, skuInfoEntity);

                // 5.3）、保存sku图片集 pms_sku_images
                saveBatchSkuImages(sku, skuInfoEntity);

                // 跨库存储
                // 5.4）、保存sku优惠、满减等信息(跨库存储: mall_sms库)  ->
                // 5.4.1）、sms_sku_full_reduction: 满减优惠，满多少件，减多少钱
                saveSkuFullReduction(sku, skuInfoEntity);

                // 跨库存储
                // 5.4.2）、sms_sku_ladder: 打折优惠，满多少件，打多少折
                saveSkuLadder(sku, skuInfoEntity);

                // 跨库存储
                // 5.4.3）、sms_member_price: 一个商品在不同的会员消费时会存在不同的价格
                saveBatchMemberPrice(sku, skuInfoEntity);

            });
        }

        // 跨库存储
        // 6、保存spu的积分信息：mall_sms库 -> sms_spu_bounds
        saveSpuBounds(spuSaveVo, spuInfo);
    }

    private void saveSpuBounds(SpuSaveVO spuSaveVo, SpuInfoEntity spuInfo) {
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfo.getId());
        spuBoundsClient.saveInner(spuBoundsTO);
    }

    private void saveBatchMemberPrice(Skus sku, SkuInfoEntity skuInfoEntity) {
        List<MemberPriceTO> memberPriceTOS = sku.getMemberPrice().stream()
                .map(item -> {
                    MemberPriceTO memberPriceTO = new MemberPriceTO();
                    memberPriceTO.setSkuId(skuInfoEntity.getSkuId());
                    BeanUtils.copyProperties(item, memberPriceTO);

                    return memberPriceTO;
                })
                .filter(item -> !item.getPrice().equals(BigDecimal.ZERO))
                .collect(Collectors.toList());
        memberPriceClient.saveBatchInner(memberPriceTOS);
    }

    private void saveSkuLadder(Skus sku, SkuInfoEntity skuInfoEntity) {
        if (!sku.getDiscount().equals(BigDecimal.ZERO)) {
            SkuLadderTO skuLadderTo = new SkuLadderTO();
            skuLadderTo.setSkuId(skuInfoEntity.getSkuId());
            skuLadderTo.setCountStatus(sku.getCountStatus());
            skuLadderTo.setDiscount(sku.getDiscount());
            skuLadderTo.setFullCount(sku.getFullCount());
            // 设置折后价
            BigDecimal priceAfterDiscount = sku.getPrice().multiply(sku.getDiscount());
            skuLadderTo.setPrice(priceAfterDiscount);

            skuLadderClient.saveInner(skuLadderTo);
        }
    }

    private void saveSkuFullReduction(Skus sku, SkuInfoEntity skuInfoEntity) {
        if (!sku.getReducePrice().equals(BigDecimal.ZERO)) {
            SkuFullReductionTO skuFullReductionTo = new SkuFullReductionTO();
            BeanUtils.copyProperties(sku, skuFullReductionTo);
            skuFullReductionTo.setSkuId(skuInfoEntity.getSkuId());
            skuFullReductionClient.saveInner(skuFullReductionTo);
        }
    }

    private void saveBatchSkuImages(Skus sku, SkuInfoEntity skuInfoEntity) {
        // 过滤掉传入的无效图片url
        List<SkuImagesEntity> skuImagesEntityList = sku.getImages().stream()
                .filter(item -> StringUtils.isNotEmpty(item.getImgUrl()))
                .map(item -> {
            SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
            skuImagesEntity.setSkuId(skuInfoEntity.getSkuId());
            BeanUtils.copyProperties(item, skuImagesEntity);
            return skuImagesEntity;
        }).collect(Collectors.toList());
        skuImagesService.saveBatch(skuImagesEntityList);
    }

    private void saveBatchSkuSaleAttrValue(Skus sku, SkuInfoEntity skuInfoEntity) {
        List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = sku.getAttr().stream().map(skuSaleAttrItem -> {
            SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
            skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
            BeanUtils.copyProperties(skuSaleAttrItem, skuSaleAttrValueEntity);
            return skuSaleAttrValueEntity;
        }).collect(Collectors.toList());
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
    }

    private SkuInfoEntity saveSkuInfoEntity(SpuSaveVO spuSaveVo, SpuInfoEntity spuInfo, Skus sku) {
        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
        BeanUtils.copyProperties(sku, skuInfoEntity);
        skuInfoEntity.setSpuId(spuInfo.getId());
        skuInfoEntity.setCatalogId(spuSaveVo.getCatalogId());
        skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
        // 默认填写0
        skuInfoEntity.setSaleCount(0L);

        List<Images> imagesList = sku.getImages().stream()
                .filter(imageItem -> imageItem.getDefaultImg() == 1)
                .collect(Collectors.toList());
        Images defaultImg = imagesList.size() > 0 ? imagesList.get(0) : sku.getImages().get(0);
        skuInfoEntity.setSkuDefaultImg(defaultImg.getImgUrl());

        saveSkuBaseInfo.save(skuInfoEntity);
        return skuInfoEntity;
    }

    private void saveBatchSkuBaseAttrValue(Long spuId, List<BaseAttrs> baseAttrs) {
        List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity entity = new ProductAttrValueEntity();
            entity.setSpuId(spuId);
            entity.setAttrId(item.getAttrId());
            entity.setQuickShow(item.getShowDesc());
            entity.setAttrValue(item.getAttrValues());

            Long attrId = item.getAttrId();
            AttrEntity attrEntity = attrService.getById(attrId);
            if (attrEntity != null) {
                entity.setAttrName(attrEntity.getAttrName());
            }

            return entity;
        }).collect(Collectors.toList());

        productAttrValueService.saveBatch(productAttrValueEntityList);
    }

    private void saveBatchSpuImages(Long spuId, List<String> images) {
        List<SpuImagesEntity> spuImagesEntityList = images.stream().map(item -> {
            SpuImagesEntity entity = new SpuImagesEntity();
            // TODO 名字是截取的, 规则待确认
            // entity.setImgName();
            entity.setSpuId(spuId);
            entity.setImgUrl(item);
            return entity;
        }).collect(Collectors.toList());

        spuImagesService.saveBatch(spuImagesEntityList);
    }

    private void saveSpuInfoDesc(Long spuId, List<String> decript) {
        SpuInfoDescEntity entity = new SpuInfoDescEntity();
        entity.setDecript(String.join(",", decript));
        entity.setSpuId(spuId);

        spuInfoDescService.save(entity);
    }

    private SpuInfoEntity saveSpuBaseInfo(SpuSaveVO spuSaveVo) {
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.save(spuInfo);
        return spuInfo;
    }

}