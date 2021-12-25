package com.github.kancyframework.timewatcher.demo.listener;

import com.github.kancyframework.timewatcher.event.TimeWatchEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * TimeWatchEventListener
 *
 * @author huangchengkang
 * @date 2021/12/25 3:04
 */
@Component
public class TimeWatchEventListener implements ApplicationListener<TimeWatchEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Async
    @Override
    public void onApplicationEvent(TimeWatchEvent event) {
        System.err.println(event);
    }
}
