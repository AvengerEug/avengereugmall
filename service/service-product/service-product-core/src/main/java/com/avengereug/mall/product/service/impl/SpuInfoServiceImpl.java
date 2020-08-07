package com.avengereug.mall.product.service.impl;

import com.avengereud.mall.es.client.ESClient;
import com.avengereug.mall.common.anno.GlobalTransactional;
import com.avengereug.mall.common.constants.ProductConstant;
import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
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
import com.avengereug.mall.to.SpuESTO;
import com.avengereug.mall.warehouse.feign.WareSkuClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

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

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareSkuClient wareSkuClient;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ESClient esClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(item -> item.eq("id", key).or().like("spu_name", key));
        }

        String catelogId = (String) params.get("catelogId");
        if (StringUtils.isNotEmpty(catelogId)) {
            Long catelogIdVal = Long.valueOf(catelogId);
            if (catelogIdVal > 0) {
                wrapper.eq("catelog_id", catelogIdVal);
            }
        }

        String status = (String)params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            Integer statusVal = Integer.valueOf(status);
            if (statusVal >= 0) {
                wrapper.eq("publish_status", statusVal);
            }
        }

        String brandId = (String) params.get("brandId");
        if (StringUtils.isNotEmpty(brandId)) {
            Long brandIdVal = Long.valueOf(brandId);
            if (brandIdVal > 0) {
                wrapper.eq("brand_id", brandIdVal);
            }
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    /**
     * 1. 存在分布式事务问题
     * 2. 校验传入数据的合法性
     *
     * 总结下开发这种大量保存，涉及到很多个表的debug策略，
     * 在每个表执行完保存逻辑后，应该到对应的表中去查看是否保存成功，
     * 但是由于，MySQL使用的事务默认隔离级别为：可重复读，这将导致
     * 我们在MySQL的客户端读取不到插入的数据，此时我们可以在当前
     * MySQL的会话中，设置事务隔离级别为可重复读，此时我们可以读取
     * 到还未提交的数据，其实这是一个脏读(读到别的事务的数据了)的现象。
     *
     * TODO 服务重启，服务间调用超时，其他已经保存的信息怎么办？
     * 分布式事务
     * 业务失败层面的校验
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

    @GlobalTransactional
    @Override
    public void up(Long spuId) {
        // 商品上架，大前提：拿到spu下面的所有sku，并组装SpuESTO对象交由ES处理
        List<SpuESTO> spuESTOList = new ArrayList<>();

        // 1、根据spuId，拿到所有sku信息
        List<SkuInfoEntity> skuInfoEntityList = skuInfoService.list(
                new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId)
        );

        // 2、 拿到spu信息
        SpuInfoEntity spuInfoEntity = this.getById(spuId);

        // 3、构建sku共用的Attrs
        List<SpuESTO.Attrs> spuESTOAttrsList = buildSpuESTOAttrsList(spuInfoEntity);

        // 4、构建sku共用的category, 根据spu中的categoryId拿到category的名称
        CategoryEntity categoryEntity = categoryService.getById(spuInfoEntity.getCatelogId());

        // 5、构建sku共用的branch, 根据spu中的品牌，拿到品牌id实体类，所有spu下的sku都共用品牌信息
        BrandEntity brandEntity = brandService.getById(spuInfoEntity.getBrandId());

        // 6、查询出所有sku的库存
        // 结合库存服务拿到此sku的库存  -> 根据skuId，校验它是否还有库存
        // TODO 可以优化成，查找出所有的sku的库存，以skuId为key，库存值为value，存入map中，这样就避免了循环中调用远程服务了
        // 目前的情况是：如果这个sku没有进行采购，那么它的库存就是null，这个时候其实没有必要查询的，
        // 期望结果是，在外面把所有的sku的库存信息都查出来，以skuId为key，库存值为value，存入map中
        // 最终在比较的时候，只需要根据skuId去map中去取数据，如果有数据，则说明有库存，否则，则没有库存
        Map<Long, Boolean> skuStockMap = null;

        try {
            List<Long> skuIds = skuInfoEntityList.stream().map(item -> item.getSkuId()).collect(Collectors.toList());
            RPCResult<Map<Long, Boolean>> rpcResult = wareSkuClient.innerHasStock(skuIds);
            skuStockMap = rpcResult.getResult();
        } catch (Exception e) {
            // 有可能因为网络问题、部分sku商品没有分配采购单，导致查询的为null
            logger.error("远程调用 获取sku库存api失败, 默认设置为没有库存", e);
        }

        for (SkuInfoEntity skuInfoEntity : skuInfoEntityList) {
            // 7、构建SpuESTO
            SpuESTO spuESTO = new SpuESTO();
            BeanUtils.copyProperties(skuInfoEntity, spuESTO);

            spuESTO.setSkuPrice(skuInfoEntity.getPrice());
            spuESTO.setSkuImg(skuInfoEntity.getSkuDefaultImg());

            if (skuStockMap == null) {
                spuESTO.setHasStock(false);
            } else {
                Boolean hasStock = skuStockMap.get(skuInfoEntity.getSkuId());
                spuESTO.setHasStock(hasStock == null ? false : hasStock);
            }

            // 商品热度，默认为0
            spuESTO.setHotScope(0L);
            spuESTO.setBrandName(brandEntity.getName());
            spuESTO.setBrandImg(brandEntity.getLogo());
            spuESTO.setCatelogName(categoryEntity.getName());
            spuESTO.setAttrs(spuESTOAttrsList);

            spuESTOList.add(spuESTO);
        }
        System.out.println(spuESTOList);

        // 7、将spuESTOList给service-es服务，添加文档
        // TODO 远程服务调用，需要保证接口幂等性
        RPCResult<Boolean> booleanR = esClient.indexSpu(spuESTOList);
        if (booleanR.getResult() && booleanR.getCode() == 0) {
            // 8、更新spu的状态为上架状态
            SpuInfoEntity entity = new SpuInfoEntity();
            entity.setId(spuId);
            entity.setPublishStatus(ProductConstant.PublishStatusEnum.UP.getCode());
            entity.setUpdateTime(new Date());

            baseMapper.updateById(entity);
        }

    }

    private List<SpuESTO.Attrs> buildSpuESTOAttrsList(SpuInfoEntity spuInfoEntity) {
        // 3.1 拿出当前spu对应分类下所有支持搜索的规格属性
        Set<Long> supportSearchAttrIds = attrService.list(
                new QueryWrapper<AttrEntity>().eq("catelog_id", spuInfoEntity.getCatelogId())
                        .eq("search_type", ProductConstant.AttrSearchTypeEnum.SUPPORT.getCode())
        ).stream().map(AttrEntity::getAttrId).collect(Collectors.toSet());
        // 3.2 拿到当前spu中拥有的规格参数
        List<ProductAttrValueEntity> ownedAttrsWithSpu = productAttrValueService.list(
                new QueryWrapper<ProductAttrValueEntity>()
                        .eq("spu_id", spuInfoEntity.getId())
        );
        // 3.3 构建SpuESTO中的Attrs (可以被检索的属性)，将ownedAttrsWithSpu中包含在supportSearchAttrIds的属性都拿出来
        return ownedAttrsWithSpu.stream()
                .filter(item -> supportSearchAttrIds.contains(item.getAttrId()))
                .map(item -> {
                    SpuESTO.Attrs attrs = new SpuESTO.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                }).collect(Collectors.toList());
    }

    private void saveSpuBounds(SpuSaveVO spuSaveVo, SpuInfoEntity spuInfo) {
        SpuBoundsTO spuBoundsTO = new SpuBoundsTO();
        BeanUtils.copyProperties(spuSaveVo.getBounds(), spuBoundsTO);
        spuBoundsTO.setSpuId(spuInfo.getId());
        spuBoundsClient.innerSave(spuBoundsTO);
    }

    private void saveBatchMemberPrice(Skus sku, SkuInfoEntity skuInfoEntity) {
        List<MemberPriceTO> memberPriceTOS = sku.getMemberPrice().stream()
                .map(item -> {
                    MemberPriceTO memberPriceTO = new MemberPriceTO();
                    memberPriceTO.setSkuId(skuInfoEntity.getSkuId());
                    BeanUtils.copyProperties(item, memberPriceTO);

                    return memberPriceTO;
                })
                .filter(item -> item.getPrice().compareTo(BigDecimal.ZERO) == 1)
                .collect(Collectors.toList());
        memberPriceClient.innerSaveBatch(memberPriceTOS);
    }

    private void saveSkuLadder(Skus sku, SkuInfoEntity skuInfoEntity) {
        // 打折，一般传入的是小于1的数，比如打8折，传入的数据是0.80
        if (sku.getFullCount() > 0 || -1 == sku.getDiscount().compareTo(BigDecimal.ZERO)) {
            SkuLadderTO skuLadderTo = new SkuLadderTO();
            skuLadderTo.setSkuId(skuInfoEntity.getSkuId());
            skuLadderTo.setCountStatus(sku.getCountStatus());
            skuLadderTo.setDiscount(sku.getDiscount());
            skuLadderTo.setFullCount(sku.getFullCount());
            // 设置折后价，在下订单时还要再设置一次
            BigDecimal priceAfterDiscount = sku.getPrice().multiply(sku.getDiscount());
            skuLadderTo.setPrice(priceAfterDiscount);

            skuLadderClient.innerSave(skuLadderTo);
        }
    }

    private void saveSkuFullReduction(Skus sku, SkuInfoEntity skuInfoEntity) {
        // 满足满多少件 减少多少钱的条件
        // BitDecimal的compareTo方法，
        // -1表示前者比后者小
        // 0 表示前者等于后者
        // 1 表示前者大于后者
        if (sku.getFullPrice().compareTo(BigDecimal.ZERO) == 1 || sku.getFullCount() > 0) {
            SkuFullReductionTO skuFullReductionTo = new SkuFullReductionTO();
            BeanUtils.copyProperties(sku, skuFullReductionTo);
            skuFullReductionTo.setSkuId(skuInfoEntity.getSkuId());
            skuFullReductionClient.innerSave(skuFullReductionTo);
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
        skuInfoEntity.setCatelogId(spuSaveVo.getCatelogId());
        skuInfoEntity.setBrandId(spuSaveVo.getBrandId());
        // 默认填写0
        skuInfoEntity.setSaleCount(0L);

        List<Images> imagesList = sku.getImages().stream()
                .filter(imageItem -> imageItem.getDefaultImg() == 1)
                .collect(Collectors.toList());
        Images defaultImg = imagesList.size() > 0 ? imagesList.get(0) : sku.getImages().get(0);
        skuInfoEntity.setSkuDefaultImg(defaultImg.getImgUrl());

        skuInfoService.save(skuInfoEntity);
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