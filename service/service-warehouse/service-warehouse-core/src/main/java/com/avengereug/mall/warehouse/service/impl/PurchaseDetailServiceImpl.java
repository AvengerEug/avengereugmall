package com.avengereug.mall.warehouse.service.impl;

import com.avengereug.mall.common.utils.R;
import com.avengereug.mall.common.utils.RPCResult;
import com.avengereug.mall.product.entity.SkuInfoEntity;
import com.avengereug.mall.product.feign.SkuInfoClient;
import com.avengereug.mall.product.vo.SkuInfoEntityVO;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.common.utils.Query;

import com.avengereug.mall.warehouse.dao.PurchaseDetailDao;
import com.avengereug.mall.warehouse.entity.PurchaseDetailEntity;
import com.avengereug.mall.warehouse.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseDetailServiceImpl.class);

    @Autowired
    private SkuInfoClient skuInfoClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();

        /**
         * key=1&status=2&wareId=1
         */
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(item -> item.eq("id", key).or().like("sku_id", key));
        }

        String status = (String) params.get("status");
        if (StringUtils.isNotEmpty(status)) {
            wrapper.eq("status", status);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(PurchaseDetailEntity purchaseDetail) {

        // 计算采购单的价格
        Long skuId = purchaseDetail.getSkuId();
        RPCResult<SkuInfoEntityVO> info = skuInfoClient.infoInner(skuId);
        if (R.SUCCESS_CODE != info.getCode()) {
            logger.error("调用商品微服务获取skuInfoEntity失败，", info.getMsg());
        }

        SkuInfoEntityVO rpcResult = info.getResult();
        purchaseDetail.setSkuPrice(rpcResult.getPrice());

        this.save(purchaseDetail);
    }

    @Override
    public void updateStatusByPurchaseId(int statusCode, Long id) {
        PurchaseDetailEntity entity = new PurchaseDetailEntity();
        entity.setStatus(statusCode);

        this.baseMapper.update(entity, new UpdateWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
    }

}