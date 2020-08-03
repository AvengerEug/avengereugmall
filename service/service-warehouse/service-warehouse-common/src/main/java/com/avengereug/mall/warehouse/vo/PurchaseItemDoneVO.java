package com.avengereug.mall.warehouse.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PurchaseItemDoneVO {

    @NotNull
    private Long purchaseDetailId;

    @NotNull
    private Integer status;

    private String reason;

    @NotNull
    @Min(0)
    private Integer actualSkuNum;
}
