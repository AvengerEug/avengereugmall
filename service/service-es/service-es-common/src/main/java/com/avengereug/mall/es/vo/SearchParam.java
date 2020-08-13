package com.avengereug.mall.es.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * catalog3Id=225&keyword=小米&sort=saleCount_asc/desc&hasStock=0/1
 */
@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;

    /**
     * 品牌id,可以多选
     */
    private List<Long> brandId;

    /**
     * 三级分类id
     */
    private Long catalog3Id;

    /**
     * 排序条件：
     * sort = price_desc/asc
     * sort = saleCount_desc/asc
     * sort = hotScore_desc/asc
     */
    private String sort;

    /**
     * 是否显示有货  hasStock=0/1
     */
    private Integer hasStock;

    /**
     * 价格区间查询
     *
     * skuPrice
     *   1_500  => 1-500
     *   _500  => 小于500
     *   500_  => 大于500
     */
    private String skuPrice;

    /**
     * 按照属性进行筛选
     * attrs=1_3G:4G:5G&attrs=2_高清屏 
     *
     */
    private List<String> attrs;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 原生的所有查询条件
     */
    private String _queryString;
}