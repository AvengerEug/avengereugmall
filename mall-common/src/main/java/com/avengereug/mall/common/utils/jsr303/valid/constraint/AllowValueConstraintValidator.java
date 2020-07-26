package com.avengereug.mall.common.utils.jsr303.valid.constraint;

import com.avengereug.mall.common.utils.jsr303.valid.anno.AllowValues;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class AllowValueConstraintValidator implements ConstraintValidator<AllowValues, Integer> {

    private Set<Integer> set = new HashSet<>();

    /**
     * 获取到注解初始化的值，并存入set中
     * @param constraintAnnotation
     */
    @Override
    public void initialize(AllowValues constraintAnnotation) {
        int[] value = constraintAnnotation.value();
        if (value.length > 0) {
            for (int i : value) {
                set.add(i);
            }
        }
    }

    /**
     * 校验传入的value包含在 @AllowValues中指定的数组中
     *
     * @param value
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }


}
