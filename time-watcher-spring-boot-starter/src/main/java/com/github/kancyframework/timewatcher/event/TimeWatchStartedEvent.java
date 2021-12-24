package com.github.kancyframework.timewatcher.event;

import com.github.kancyframework.timewatcher.WatchContext;

/**
 * TimeWatchStopEvent
 *
 * @author huangchengkang
 * @date 2021/12/25 11:33
 */
public class TimeWatchStartedEvent extends TimeWatchEvent {

    public TimeWatchStartedEvent(Object source, WatchContext watchContext) {
        super(source, watchContext);
    }
}
