package com.github.kancyframework.timewatcher.annotation;

import org.junit.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * AnnotationTests
 *
 * @author huangchengkang
 * @date 2021/12/26 23:08
 */
public class AnnotationTests {

    @Test
    public void test(){
        Method method1 = ReflectionUtils.findMethod(getClass(), "test1");
        Method method2 = ReflectionUtils.findMethod(getClass(), "test2");
        Method method3 = ReflectionUtils.findMethod(getClass(), "test3");

        System.out.println(AnnotationUtils.findAnnotation(method1, TimeWatcher.class));
        System.out.println(AnnotationUtils.findAnnotation(method2, TimeWatcher.class));

        System.out.println(AnnotatedElementUtils.findMergedAnnotation(method1, TimeWatcher.class));
        System.out.println(AnnotatedElementUtils.findMergedAnnotation(method2, TimeWatcher.class));
        System.out.println(AnnotatedElementUtils.findMergedAnnotation(method3, TimeWatcher.class));
    }

    @TimeWatch(context = "test1", maxCostMillis = 78)
    public void test1(){

    }

    @Watcher(name = "test2", maxCostMillis = 45, maxTotalCostMillis = 900)
    public void test2(){

    }

    @TimeWatcher(value = "test1", maxCostMillis = 23)
    public void test3(){

    }
}
