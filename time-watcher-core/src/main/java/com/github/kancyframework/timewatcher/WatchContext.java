package com.github.kancyframework.timewatcher;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WatchContext
 *
 * @author huangchengkang
 * @date 2021/12/24 20:01
 */
@Data
public abstract class WatchContext {
    /**
     * 是否可用
     */
    private boolean enabled;

    /**
     * 是否已停止
     */
    private boolean stopped;

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
     * 线程id
     */
    private Long threadId;

    /**
     * 父上下文
     */
    private WatchContext parentContext;

    /**
     * 跟部观测记录
     */
    private WatchRecord rootWatchRecord;

    /**
     * 观测记录
     */
    private List<WatchRecord> watchRecords;

    /**
     * 放置上下文属性
     *
     * @param key 键
     * @param value 值
     */
    public void putContextProperty(String key, Object value){
        if (Objects.nonNull(rootWatchRecord)){
            Map<String, Object> properties = rootWatchRecord.getProperties();
            properties.put(key, value);
        }
    }

    /**
     * 放置上下文属性
     *
     * @param properties 属性集合
     */
    public void putContextProperty(Map<String, Object> properties){
        if (Objects.nonNull(rootWatchRecord)
                && Objects.nonNull(properties)){
            rootWatchRecord.getProperties().putAll(properties);
        }
    }

    /**
     * 启动上下文
     * @param contextName
     */
    protected abstract void start(String contextName);

    /**
     * 停止上下文
     */
    protected abstract void stop();

    /**
     * 复制上下文
     * @return
     */
    protected abstract WatchContext copy();
}
