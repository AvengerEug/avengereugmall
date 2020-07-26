package com.avengereug.mall.product.entity;

import com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues;
import com.avengereug.mall.common.utils.jsr303.valid.group.SaveGroup;
import com.avengereug.mall.common.utils.jsr303.valid.group.UpdateGroup;
import com.avengereug.mall.common.utils.jsr303.valid.group.UpdateStatusGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * 若controller中，使用的是@Validated, 且指定了分组，
 * 那么只有在JSR303注解中指定了相同分组的校验才会生效
 *
 * 如果使用的是@Validated, 且未指定分组，
 * 那么，在JSR303字段中未指定分组的校验才会生效
 *
 *
 * 分组校验是spring提供的功能，功能更加强大
 *
 * 
 * @author avengerEug
 * @email eugenesumarry@163.com
 * @date 2020-07-20 11:11:22
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @NotNull(message = "修改必须指定品牌id", groups = {UpdateGroup.class, UpdateStatusGroup.class})
    @Null(message = "新增不能指定品牌id", groups = {SaveGroup.class})
    @TableId
    private Long brandId;
    /**
     * 品牌名
     *
     * @NotBlank注解不满足条件时的错误提示可以在ValidationMessages.properties文件中查看，
     * 或者在ValidationMessages_zh_CN.properties中查看中文版本的提示，
     * 如果也不满意的话，可以在注解中动态添加错误提示消息
     *
     */
    @NotBlank(message = "品牌名不能为空", groups = { UpdateGroup.class, SaveGroup.class })
    private String name;
    /**
     * 品牌logo地址
     */
    @NotBlank(message = "logo不能为空", groups = { UpdateGroup.class, SaveGroup.class })
    @URL(message = "logo必须是一个合法的URL地址", groups = { UpdateGroup.class, SaveGroup.class })
    private String logo;
    /**
     * 介绍
     */
    @NotBlank(groups = { UpdateGroup.class, SaveGroup.class })
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     *
     * 使用自定义注解，表示前端传来的数据只能为0或1
     *
     */
    @NotNull(groups = { UpdateGroup.class, SaveGroup.class })
    @AllowValues(value = {0, 1}, groups = { UpdateGroup.class, SaveGroup.class, UpdateStatusGroup.class })
    private Integer showStatus;
    /**
     * 检索首字母
     *
     * js中的正则为：/^[a-zA-Z]$/
     * 在java中，不需要前后的/
     */
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = { UpdateGroup.class, SaveGroup.class })
    private String firstLetter;
    /**
     * 排序
     */
    @Min(value = 0, message = "排序必须大于等于0", groups = { UpdateGroup.class, SaveGroup.class })
    @NotNull(groups = { UpdateGroup.class, SaveGroup.class })
    private Integer sort;

}
