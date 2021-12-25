package com.github.kancyframework.timewatcher.demo.handler;

import com.github.kancyframework.timewatcher.event.TimeWatchResultEvent;
import com.github.kancyframework.timewatcher.handler.TimeWatchResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ApiTimeWatchResultHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 2:52
 */
@Slf4j
@Component
public class ApiTimeWatchResultHandler implements TimeWatchResultHandler {
    /**
     * 是否支持处理
     *
     * @param contextName
     * @return
     */
    @Override
    public boolean isSupported(String contextName) {
        return contextName.startsWith("url:");
    }

    /**
     * 处理
     *
     * @param result
     */
    @Override
    public void handle(TimeWatchResultEvent result) {
        result.getWatchContext().save();
        log.info("save 成功：{}", result.getContextId());
    }
}
