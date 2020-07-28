package com.avengereug.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 属性分组
 *
 * 分类与属性组的关系：
 *
 *   分类 + 品牌 两个属性可以唯一确认一个spu，比如手机类别的华为品牌的手机就是一个spu，
 *   但是，我们其实可以定义一个分类就代表着一个spu，因为spu是商品的共有属性，而分类 + 品牌
 *   只是具体到了某个分类下的品牌。因此，我们可以确定，一个分类就能确定一个spu的基本属性。
 *   因为spu一个商品的共有属性，拿手机来举例子，手机可能包含
 *   主体，基本信息，主芯片，屏幕等属性等等。但同时，主体内部又包含 入网型号、品牌、产品名称属性等等。
 *   我们设主题、基本信息、主芯片为属性分组，主题内部包含的入网型号、品牌为属性分组中的属性。
 *   因此我们很容易想到，属性分组和属性是一对多的关系，但是属性也有可能会出现在多个分组中，
 *   所以我们可以得出：分组和属性是多对多的关系，因此会有一个属性分组与属性的关联关系中间表。
 *
 *   综上所述，我们可以确定分类有多个属性组，而属性组中有多少个属性，由中间表可知。
 *   进而关联到，一个分类内部有多少属性组，进而知道有哪些属性 <==> 一个spu有多少属性组, 进而知道有哪些属性
 *   举个栗子：一个手机分类包含主体、基本信息、主芯片的属性分组，而主题内部又包含入网型号、品牌等属性。
 *   ps: 属性的具体的值存储在pms_product_attr_value表中，(TODO 待确认为什么要分表?)
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 标识此字段不在db中, 为了保证在
     * 修改属性分组时的分类级联选择框中，
     * 能显示出当前属性分组是在哪个分类下
     */
    @TableField(exist = false)
    private List<Long> catelogPath;

}
