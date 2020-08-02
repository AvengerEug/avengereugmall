package com.avengereug.mall.warehouse.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseMergeVo {

    /**
     * 需要将采购需求合并至哪个采购单
     */
    private Long purchaseId;

    /**
     * 被合并的采购需求
     */
    private List<Long> items;

}
