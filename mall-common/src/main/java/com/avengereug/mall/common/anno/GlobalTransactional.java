package com.avengereug.mall.common.anno;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Transactional(rollbackFor = Exception.class)
public @interface GlobalTransactional {
}
