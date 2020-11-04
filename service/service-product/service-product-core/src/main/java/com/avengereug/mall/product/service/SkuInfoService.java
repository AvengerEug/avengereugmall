package com.avengereug.mall.product.service;

import com.avengereug.mall.product.vo.SkuItemVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.product.entity.SkuInfoEntity;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    SkuItemVO item(Long skuId) throws ExecutionException, InterruptedException;

    BigDecimal getPrice(Long skuId);
}

