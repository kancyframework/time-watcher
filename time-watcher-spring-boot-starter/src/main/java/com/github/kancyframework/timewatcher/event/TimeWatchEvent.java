package com.github.kancyframework.timewatcher.event;

import com.github.kancyframework.timewatcher.WatchContext;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * TimeWatchEvent
 *
 * @author huangchengkang
 * @date 2021/12/25 13:05
 */
public class TimeWatchEvent extends ApplicationEvent {

    private WatchContext watchContext;

    /**
     * 上下文id
     */
    private String contextId;

    /**
     * 上下文名称
     */
    private String contextName;

    /**
     * 跟踪id
     */
    private String traceId;

    /**
     * 开始时间
     */
    private Date startTime;

    public TimeWatchEvent(Object source, WatchContext watchContext) {
        super(source);
        this.watchContext = watchContext;
        this.contextName = watchContext.getContextName();
        this.contextId = watchContext.getContextId();
        this.traceId = watchContext.getTraceId();
        this.startTime = watchContext.getRootWatchRecord().getStartTime();
    }

    public WatchContext getWatchContext() {
        return watchContext;
    }

    public String getContextId() {
        return contextId;
    }

    public String getContextName() {
        return contextName;
    }

    public String getTraceId() {
        return traceId;
    }

    public Date getStartTime() {
        return startTime;
    }
}
