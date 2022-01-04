package com.github.kancyframework.timewatcher;

import lombok.Data;

/**
 * 事件
 * Event
 *
 * @author huangchengkang
 * @date 2021/12/31 0:33
 */
@Data
public class Event {
    /**
     * 上下文id
     */
    private String contextId;
    /**
     * 业务ID
     */
    private String bizId;
    /**
     * 跟踪id
     */
    private String traceId;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 方法args
     */
    private Object[] methodArgs;
    /**
     * 异常
     */
    private Throwable throwable;
    /**
     * 开始时间
     */
    private Long startTime;
    /**
     * 结束时间
     */
    private Long endTime;
}
