package com.github.kancyframework.timewatcher.demo.listener;

import com.alibaba.fastjson.JSON;
import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.event.TimeWatchEvent;
import com.github.kancyframework.timewatcher.event.TimeWatchStoppedEvent;
import com.github.kancyframework.timewatcher.span.GuiWatchContextView;
import com.github.kancyframework.timewatcher.span.ImageWatchContextView;
import com.github.kancyframework.timewatcher.span.WatchContextView;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

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
        if (event instanceof TimeWatchStoppedEvent){
            TimeWatchStoppedEvent watchStoppedEvent = TimeWatchStoppedEvent.class.cast(event);
            ArrayList<Object> objects = new ArrayList<>();
            objects.add(watchStoppedEvent.getRootTimeWatchRecord());
            objects.addAll(watchStoppedEvent.getTimeWatchRecords());
            System.out.println(JSON.toJSONString(objects, true));

            WatchContext watchContext = event.getWatchContext();
            WatchContextView guiWatchContextView = new GuiWatchContextView(watchContext);
            guiWatchContextView.showView();
        }
    }
}
