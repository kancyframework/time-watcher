package com.github.kancyframework.timewatcher.handler;

/**
 * ThreadLocalHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 11:32
 */
public interface ThreadLocalHandler<T> {

    /**
     * 名字
     * @return
     */
    default String name(){
        return null;
    }

    /**
     * 复制当前线程本地上下文
     *
     * @return {@link T}
     */
    T copyCurrentThreadLocalContext();

    /**
     * 设置线程本地上下文
     *
     * @param context 上下文
     */
    void setThreadLocalContext(T context);

    /**
     * 清除
     */
    void clearThreadLocalContext();
}
