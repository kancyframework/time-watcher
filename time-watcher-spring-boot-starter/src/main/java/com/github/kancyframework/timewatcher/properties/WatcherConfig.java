package com.github.kancyframework.timewatcher.properties;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * WatcherConfig
 *
 * @author huangchengkang
 * @date 2021/12/25 5:15
 */
@Data
public class WatcherConfig{
    /**
     * 启用,默认true
     */
    private Boolean enabled;
    /**
     * 最大总耗时（毫秒）
     */
    private Long maxTotalCostMillis;
    /**
     * 每次Watch最大耗时（毫秒）
     */
    private Long maxCostMillis;
    /**
     * 不抛出异常,默认true
     */
    private Boolean noThrows;
    /**
     * 采样率,默认1.0
     */
    private Double sampleRate;
    /**
     * 备注
     */
    private String description;
    /**
     * 扩展属性
     */
    private Map<String, Object> properties = new HashMap<>();
}