package com.avengereug.mall.product.vo;

import com.avengereug.mall.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrVO extends AttrEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 当前的attr所属哪个attrGroup
     */
    private Long attrGroupId;
}
