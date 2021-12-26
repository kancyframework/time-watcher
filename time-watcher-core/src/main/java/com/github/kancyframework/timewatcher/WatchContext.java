package com.github.kancyframework.timewatcher;

import com.github.kancyframework.timewatcher.span.TimeSpanFrame;
import com.github.kancyframework.timewatcher.span.TimeSpanImage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * WatchContext
 *
 * @author huangchengkang
 * @date 2021/12/24 20:01
 */
@Slf4j
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
     * 根部观测记录
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

    public void showReport(){
        if (isEnabled() && isStopped()){
            try {
                TimeSpanFrame.show(this);
            } catch (java.awt.HeadlessException e) {
                // Spring boot出现java.awt.HeadlessException的解决办法
                // -Djava.awt.headless=false
                // SpringApplicationBuilder.headless(false)
            } catch (Exception e){
                log.warn("WatchContext show fail : {}", e.getMessage());
            }
        } else {
            log.info("WatchContext did not enabled or stop ， please call stop()");
        }
    }

    public void saveReport(){
        saveReport(String.format("%s.png", getContextId()));
    }

    public void saveReport(String filePath){
        saveReport(new File(filePath));
    }

    public void saveReport(File file){
        if (isEnabled() && isStopped()){
            TimeSpanImage timeSpanImage = TimeSpanImage.create(this);
            timeSpanImage.save(file);
        }else {
            log.info("WatchContext did not enabled or stop ， please call stop()");
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
