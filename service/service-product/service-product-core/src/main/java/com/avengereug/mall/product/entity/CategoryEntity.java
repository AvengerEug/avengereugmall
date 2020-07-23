package com.avengereug.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 商品三级分类
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
    @TableId
    private Long catId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父分类id
     */
    private Long parentCid;
    /**
     * 层级
     */
    private Integer catLevel;
    /**
     * 是否显示[0-不显示，1显示]
     *
     * TableLogic注解标识此字段是逻辑删除标志
     *
     * 当调用删除方法时，会执行如下SQL语句：
     * UPDATE pms_category SET show_status=0 WHERE cat_id IN ( ? ) AND show_status=1
     *
     * ==> 因为使用@TableLogic注解，指定了删除的值用0表示
     * 未删除的值用1表示
     * 所以逻辑删除时，会使用到这两个值delval和value
     *
     * 同时在查询时，也会携带value的值
     *
     */
    @TableLogic(delval = "0", value = "1")
    private Integer showStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图标地址
     */
    private String icon;
    /**
     * 计量单位
     */
    private String productUnit;
    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 表示当前字段在表中不存在
     */
    @TableField(exist = false)
    private List<CategoryEntity> children;

}
