package com.github.kancyframework.timewatcher.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * TimeWatchProperties
 *
 * @author huangchengkang
 * @date 2021/12/25 4:46
 */
@Data
@ConfigurationProperties(prefix = "timewatcher")
@EnableConfigurationProperties(TimeWatchProperties.class)
public class TimeWatchProperties {
    /**
     * timewatcher功能开关
     */
    private Boolean enabled;
    /**
     * watcher配置
     */
    private Map<String, WatcherConfig> watchers = new HashMap<>();
    /**
     * 全局:最大总耗时（毫秒）
     */
    private Long maxTotalCostMillis;
    /**
     * 全局:每次Watch最大耗时（毫秒）
     */
    private Long maxCostMillis;
    /**
     * 全局:不抛出异常
     */
    private Boolean noThrows;
    /**
     * 全局:采样率
     */
    private Double sampleRate;

    /**
     * 全局:扩展属性
     */
    private Map<String, Object> properties = new HashMap<>();

    /**
     * jdbc名称
     */
    private String jdbcTemplateBeanName = "timewatcherJdbcTemplate";

    /**
     * jdbc报告的表名
     */
    private String jdbcReportTableName = "t_timewatcher_report";
    /**
     * jdbc报告明细的表名
     */
    private String jdbcReportInfoTableName = "t_timewatcher_report_info";

    /**
     * resultHandler开关
     */
    private Map<String, Boolean> resultHandlers = new HashMap<>();

    /**
     * 线程池属性
     */
    @NestedConfigurationProperty
    private ThreadPoolConfig taskExecutor = new ThreadPoolConfig();


}
