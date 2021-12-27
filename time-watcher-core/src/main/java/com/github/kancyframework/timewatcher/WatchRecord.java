package com.github.kancyframework.timewatcher;

import lombok.Data;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * WatchRecord
 *
 * @author huangchengkang
 * @date 2021/12/24 20:07
 */
@Data
public class WatchRecord {
    /**
     * 索引,根默认为0
     */
    private Integer watchIndex;

    /**
     * 父观测名称
     */
    private String parentWatchName;

    /**
     * 观测名称
     */
    private String watchName;

    /**
     * 线程名称
     */
    private String threadName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 停止时间
     */
    private Date stopTime;

    /**
     * 耗时（毫秒）
     */
    private Long costMillis;

    /**
     * 属性
     */
    private Map<String, Object> properties;


    /**
     * 开始记录
     */
    public void startRecord(){
        if (Objects.isNull(this.startTime)){
            this.startTime = new Date();
            this.threadName = Thread.currentThread().getName();
        }
    }

    /**
     * 停止记录
     */
    public void stopRecord(){
        if (Objects.isNull(this.stopTime) && Objects.nonNull(this.startTime)){
            this.stopTime = new Date();
            this.costMillis = this.stopTime.getTime() - this.startTime.getTime();
        }
    }

}
