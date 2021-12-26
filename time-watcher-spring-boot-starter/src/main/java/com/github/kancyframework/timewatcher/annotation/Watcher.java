package com.github.kancyframework.timewatcher.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * TimeWatch
 * @see TimeWatcher
 *
 * @author huangchengkang
 * @date 2021/12/25 1:15
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@TimeWatcher
public @interface Watcher {

    /**
     * watch上下文名称
     * @return
     */
    @AliasFor(annotation = TimeWatcher.class)
    String value() default "";

    /**
     * watch上下文名称
     * @return
     */
    @AliasFor(annotation = TimeWatcher.class)
    String context() default "";

    /**
     * 是否启用
     * @return
     */
    @AliasFor(annotation = TimeWatcher.class)
    boolean enabled() default true;

    /**
     * 不抛出移除
     * @return
     */
    @AliasFor(annotation = TimeWatcher.class)
    boolean noThrows() default true;

    /**
     * 最大总耗时（毫秒）
     * @return
     */
    @AliasFor(annotation = TimeWatcher.class)
    long maxTotalCostMillis() default -1;

    /**
     * 每次Watch最大耗时（毫秒）
     * @return
     */
    @AliasFor(annotation = TimeWatcher.class)
    long maxCostMillis() default -1;
}
