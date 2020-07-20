package com.avengereug.mall.warehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.avengereug.mall.common.utils.PageUtils;
import com.avengereug.mall.warehouse.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 10:59:40
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

