package com.github.kancyframework.timewatcher;

import lombok.Data;

/**
 * TimeWatchRecord
 *
 * @author huangchengkang
 * @date 2021/12/25 11:42
 */
@Data
public class TimeWatchRecord extends WatchRecord{
    /**
     * 上下文id
     */
    private String contextId;

    /**
     * 跟踪id
     */
    private String traceId;

    /**
     * 上下文名称
     */
    private String contextName;

}