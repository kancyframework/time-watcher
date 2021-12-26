package com.github.kancyframework.timewatcher.demo.aspect;

import java.lang.annotation.*;

/**
 * Test
 *
 * @author huangchengkang
 * @date 2021/12/26 18:55
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Test {

}
