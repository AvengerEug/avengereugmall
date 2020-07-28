package com.avengereug.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 品牌分类关联
 *
 * 1个分类 可以对应多个品牌， eg: 手机分类可以对应小米、华为、苹果
 * 一个品牌可以对应多个分类，eg: 小米可以对应手机分类，也可以对应平板分类和电脑分类
 *
 * 所以会存在一个分类与品牌的关联中间表，由关联中间表的信息可知当前的分类有哪些品牌，当前的品牌有哪些分类，
 * 同时关联中间表中冗余了品牌的名称、分类的名称，这将导致，我们在更新品牌或者分类时，要级联更新关联关系中间表的品牌名称和分类名称
 *
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelationEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @TableId
    private Long id;
    /**
     * 品牌id
     */
    private Long brandId;
    /**
     * 分类id
     */
    private Long catelogId;
    /**
     * 
     */
    private String brandName;
    /**
     * 
     */
    private String catelogName;

}
