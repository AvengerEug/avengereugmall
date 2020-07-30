package com.avengereug.mall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class AttrRespVO extends AttrVO {


    /**
     * 所属当前分类的名字
     */
    private String catelogName;

    /**
     * 所属当前分组的名字
     */
    private String groupName;

    /**
     * 当前属性所属分组的id
     */
    private Long attrGroupId;


    /**
     * 当前属性所述的category的路径
     */
    private List<Long> catelogPath;


}
