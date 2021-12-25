package com.github.kancyframework.timewatcher.handler;

import com.github.kancyframework.timewatcher.event.TimeWatchResultEvent;

/**
 * TimeWatchResultHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 1:57
 */
public interface TimeWatchResultHandler {

    /**
     * 是否支持处理
     * @return
     */
    default boolean isSupported(String contextName){
        return false;
    }

    /**
     * 处理
     * @param result
     */
    void handle(TimeWatchResultEvent result);


    /**
     * 是否异步处理
     * @return
     */
    default boolean isAsync(){
        return true;
    }
}
