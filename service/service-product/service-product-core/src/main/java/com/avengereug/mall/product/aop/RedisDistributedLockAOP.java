package com.avengereug.mall.product.aop;

import com.avengereug.mall.common.utils.SpringContextHolder;
import com.avengereug.mall.product.anno.RedisDistributedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;

/**
 * redis分布式锁aop
 */
@Component
@Aspect
public class RedisDistributedLockAOP {


    @Autowired
    private RedissonClient redissonClient;

    /**
     * 定义了一个切点, 表示带了@RedisDistributedLock注解的才会被增强
     */
    @Pointcut("@annotation(com.avengereug.mall.product.anno.RedisDistributedLock)")
    public void pointcutAnnotation() {
    }

    @Around("pointcutAnnotation()")
    public Object aroundPointcutAnnotation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RedisDistributedLock redisDistributedLock = method.getAnnotation(RedisDistributedLock.class);

        RLock lock = redissonClient.getLock(redisDistributedLock.value());
        try {
            lock.lock();
            return proceedingJoinPoint.proceed();
        } finally {
            lock.unlock();
        }
    }

}
