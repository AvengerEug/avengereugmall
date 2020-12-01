package com.avengereug.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * spu信息
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Data
@TableName("pms_spu_info")
public class SpuInfoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    @TableId
    private Long id;
    /**
     * 商品名称
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名
     */
    @TableField(exist = false)
    private String brandName;

    /**
     * 
     */
    private BigDecimal weight;
    /**
     * 上架状态[0 - 新建，1 - 上架, 2 - 下架]
     */
    private Integer publishStatus;
    /**
     * 
     */
    private Date createTime;
    /**
     * 
     */
    private Date updateTime;

}
