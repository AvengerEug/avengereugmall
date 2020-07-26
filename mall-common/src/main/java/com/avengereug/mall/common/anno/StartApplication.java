package com.avengereug.mall.common.anno;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.lang.annotation.*;

/**
 * 因为每个模块的包名不一致，而@ComponentScan
 * 不支持继承(无@Inherited注解),
 * 而@SpringBootApplication有@Inherited注解
 * 就相当于把@SpringBootApplication注解的功能
 * 给扩展了。
 *
 * 为什么要扩展它呢？
 * 因为maven多模块下，有可能每个模块的项目路口类
 * 所处的路径不一致，导致不能扫描到其他模块的spring
 * 相关的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootApplication
@ComponentScan("com.avengereug.mall")
public @interface StartApplication {
}
