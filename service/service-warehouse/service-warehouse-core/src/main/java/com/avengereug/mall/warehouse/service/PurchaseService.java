package com.avengereug.mall.warehouse.service;

import com.avengereug.mall.warehouse.vo.PurchaseMergeVo;
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

    void saveDetail(PurchaseEntity purchase);

    boolean updateById(PurchaseEntity entity);

    /**
     * 查找出没有被分配的采购单，status = 0或1
     * @param params
     * @return
     */
    PageUtils queryUnreceivePage(Map<String, Object> params);

    void mergePurchase(PurchaseMergeVo purchaseMergeVo);
}

