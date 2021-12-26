package com.github.kancyframework.timewatcher.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * TimeWatcher
 *
 * @author huangchengkang
 * @date 2021/12/26 12:40
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Indexed
public @interface TimeWatcher {
    /**
     * watch上下文名称
     * @return
     */
    @AliasFor("context")
    String value() default "";

    /**
     * watch上下文名称
     * @return
     */
    @AliasFor("value")
    String context() default "";

    /**
     * 是否启用
     * @return
     */
    boolean enabled() default true;

    /**
     * 不抛出移除
     * @return
     */
    boolean noThrows() default true;

    /**
     * 最大总耗时（毫秒）
     * @return
     */
    long maxTotalCostMillis() default -1;

    /**
     * 每次Watch最大耗时（毫秒）
     * @return
     */
    long maxCostMillis() default -1;
}
