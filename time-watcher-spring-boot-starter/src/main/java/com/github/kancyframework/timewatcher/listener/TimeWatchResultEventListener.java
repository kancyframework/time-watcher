package com.github.kancyframework.timewatcher.listener;

import com.github.kancyframework.timewatcher.event.TimeWatchResultEvent;
import com.github.kancyframework.timewatcher.handler.TimeWatchResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.TaskExecutor;

import java.util.List;
import java.util.Objects;

/**
 * TimeWatchResultEventListener
 *
 * @author huangchengkang
 * @date 2021/12/26 1:53
 */
public class TimeWatchResultEventListener implements ApplicationListener<TimeWatchResultEvent> {

    @Autowired
    private TaskExecutor timeWatcherExecutor;

    @Autowired(required = false)
    private List<TimeWatchResultHandler> timeWatchResultHandlers;

    @Override
    public void onApplicationEvent(TimeWatchResultEvent timeWatchResultEvent) {
        if (Objects.isNull(timeWatchResultHandlers)){
            return;
        }

        timeWatchResultHandlers.stream()
                .filter(h->h.isSupported(timeWatchResultEvent.getContextName()))
                .forEach(h->{
                    if (h.isAsync()){
                        timeWatcherExecutor.execute(() -> h.handle(timeWatchResultEvent));
                    } else {
                        h.handle(timeWatchResultEvent);
                    }
            });
    }
}
