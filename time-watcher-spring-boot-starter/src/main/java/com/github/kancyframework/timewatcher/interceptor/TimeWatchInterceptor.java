package com.github.kancyframework.timewatcher.interceptor;

import com.github.kancyframework.timewatcher.WatchContext;

import java.lang.reflect.Method;

/**
 * TimeWatchInterceptor
 *
 * @author huangchengkang
 * @date 2021/12/25 14:07
 */
public interface TimeWatchInterceptor {

    /**
     * 是否启用
     *
     * @param contextName 上下文名称
     * @return boolean
     */
    default boolean isEnabled(String contextName, Method method){
        return true;
    }

    /**
     * 前置处理
     *
     * @param context 上下文
     * @return boolean
     */
    default void preHandle(WatchContext context, Method method, Object[] args){

    }

    /**
     * 后置处理
     * @param context
     * @return
     */
    default void postHandle(WatchContext context, Method method, Object[] args){

    }
}
