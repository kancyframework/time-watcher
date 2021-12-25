package com.github.kancyframework.timewatcher.handler;

/**
 * TimeWatchResultHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 1:57
 */
public interface SyncTimeWatchResultHandler extends TimeWatchResultHandler {
    /**
     * 是否异步处理
     *
     * @return
     */
    @Override
    default boolean isAsync(){
        return false;
    }
}
