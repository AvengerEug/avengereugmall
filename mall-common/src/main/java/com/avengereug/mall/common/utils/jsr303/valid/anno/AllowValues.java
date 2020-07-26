package com.avengereug.mall.common.utils.jsr303.valid.anno;


import com.avengereug.mall.common.utils.jsr303.valid.constraint.AllowValueConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 校验只能为指定的值
 *
 * JSR303规范：
 * 每一个JSR303注解必须同时包含message、groups、payload方法
 *
 */
@Documented
// 此注解指定了AllowVal校验由具体的解析器来处理，一个JSR303规范的注解能使用多个ConstraintValidator来校验
@Constraint(validatedBy = { AllowValueConstraintValidator.class })
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
public @interface AllowValues {

    /**
     * 将会在Classpath路径下找ValidationMessages.properties
     * 文件中包含key为com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues的value
     *
     * @return
     */
    String message() default "{com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * 配置允许的值
     * @return
     */
    int[] value() default { };
}
