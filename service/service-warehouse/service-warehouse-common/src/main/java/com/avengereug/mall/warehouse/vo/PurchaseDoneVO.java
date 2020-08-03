package com.avengereug.mall.warehouse.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PurchaseDoneVO {

    @NotNull
    private Long purchaseId;

    @Size(min = 1)
    private List<PurchaseItemDoneVO> purchaseDetailsItem;

}
