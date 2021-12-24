package com.github.kancyframework.timewatcher.properties;

import lombok.Data;

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
     * 启用
     */
    private boolean enabled = true;
    /**
     * 最大总耗时（毫秒）
     */
    private Long maxTotalCostMillis = -1L;
    /**
     * 每次Watch最大耗时（毫秒）
     */
    private Long maxCostMillis = -1L;
    /**
     * 备注
     */
    private String description;
    /**
     * 扩展属性
     */
    private Map<String, Object> properties;
}