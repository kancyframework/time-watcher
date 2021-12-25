package com.github.kancyframework.timewatcher.event;

import com.github.kancyframework.timewatcher.WatchContext;

/**
 * TimeWatchResultEvent
 *
 * @author huangchengkang
 * @date 2021/12/26 1:53
 */
public class TimeWatchResultEvent extends TimeWatchStoppedEvent{
    public TimeWatchResultEvent(Object source, WatchContext watchContext) {
        super(source, watchContext);
    }
}
