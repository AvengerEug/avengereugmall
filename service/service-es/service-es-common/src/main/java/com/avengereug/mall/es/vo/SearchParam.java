package com.avengereug.mall.es.vo;

import lombok.Data;

import java.util.List;

/**
 * 封装页面所有可能传递过来的查询条件
 * catelog3Id=225&keyword=小米&sort=saleCount_asc/desc&hasStock=0/1
 *
 *
 *
 *
 *
 * 在kibana的查询语句
 *
 * # catelog3Id=225&keyword=小米&sort=saleCount_asc/desc&hasStock=0/1
 * GET product/_search
 * {
 *   "query": {
 *     "bool": {
 *       "must": [
 *         {
 *           "match": {
 *             "skuTitle": "XR"
 *           }
 *         }
 *       ],
 *       "filter": [
 *         {
 *           "term": {
 *             "hasStock": false
 *           }
 *         },
 *         {
 *           "term": {
 *             "catelogId": "225"
 *           }
 *         },
 *         {
 *           "terms": {
 *             "brandId": [1, 2, 3, 4, 5]
 *           }
 *         },
 *         {
 *           "range": {
 *             "skuPrice": {
 *               "gte": 1,
 *               "lte": 4299.0
 *             }
 *           }
 *         },
 *         {
 *           "nested": {
 *             "path": "attrs",
 *             "query": {
 *               "bool": {
 *                 "must": [
 *                   {
 *                     "term": {
 *                       "attrs.attrId": {
 *                         "value": 3
 *                       }
 *                     }
 *                   },
 *                   {
 *                     "match_phrase": {
 *                       "attrs.attrValue": "Apple"
 *                     }
 *                   }
 *                 ]
 *               }
 *             }
 *           }
 *         }
 *       ]
 *     }
 *   },
 *   "sort": [
 *     {
 *       "skuPrice": {
 *         "order": "asc"
 *       }
 *     }
 *   ],
 *   "from": 0,
 *   "size": 20,
 *   "highlight": {
 *     "fields": {"skuTitle": {}},
 *     "pre_tags": "<strong style='color:red'>",
 *     "post_tags": "</strong>"
 *   }
 * }
 *
 *
 *  总结:
 *  非文本类型的精确查找使用term，字符串的精确查找可以在字段后面添加keywords或者
 *  在使用match_phrase精确查找文本类型的字段
 *
 *
 * 查询出来后，还要将这些商品的品牌、价格、基本属性给聚合起来
 *
 *
 */
@Data
public class SearchParam {

    /**
     * 页面传递过来的全文匹配关键字   v
     */
    private String keyword;

    /**
     * 品牌id,可以多选   v
     */
    private List<Long> brandId;

    /**
     * 三级分类id   v
     */
    private Long catelog3Id;

    /**
     * 排序条件：  v
     * sort = price_desc/asc
     * sort = saleCount_desc/asc
     * sort = hotScore_desc/asc
     */
    private String sort;

    /**
     * 是否显示有货  hasStock=0/1  v
     */
    private Integer hasStock;

    /**
     * 价格区间查询   v
     *
     * skuPrice
     *   1_500  => 1-500
     *   _500  => 小于500
     *   500_  => 大于500
     */
    private String skuPrice;

    /**
     * 按照属性进行筛选   V
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
