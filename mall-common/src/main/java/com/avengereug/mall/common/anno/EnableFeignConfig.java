package com.avengereug.mall.common.anno;


import com.avengereug.mall.common.config.ConfigurableFeignConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(ConfigurableFeignConfig.class)
public @interface EnableFeignConfig {
}
