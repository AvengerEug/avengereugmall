package com.avengereug.mall.warehouse.vo;

import com.avengereug.mall.warehouse.entity.PurchaseDetailEntity;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PurchaseDetailSaveVO extends PurchaseDetailEntity {


    private Long purchaseId;
    /**
     * 采购商品id
     */
    @NotNull
    private Long skuId;
    /**
     * 采购数量
     */
    @NotNull
    private Integer skuNum;
    /**
     * 采购金额
     */
    private BigDecimal skuPrice;
    /**
     * 仓库id
     */
    @NotNull
    private Long wareId;
    /**
     * 状态[0新建，1已分配，2正在采购，3已完成，4采购失败]
     */
    @NotNull
    private Integer status;


}
