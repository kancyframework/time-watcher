package com.github.kancyframework.timewatcher.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * TestAspect
 *
 * @author huangchengkang
 * @date 2021/12/26 18:55
 */
@Slf4j
@Aspect
@Order(1)
@Component
public class TestAspect {
    /**
     * 切点
     */
    @Pointcut("@annotation(com.github.kancyframework.timewatcher.demo.aspect.Test)")
    public void testPointCut() {
        // doNothing
    }

    @Before("testPointCut()")
    public void testBefore(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
