package com.github.kancyframework.timewatcher.handler;

import com.alibaba.fastjson.JSON;
import com.github.kancyframework.timewatcher.TimeWatchRecord;
import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.event.TimeWatchResultEvent;
import com.github.kancyframework.timewatcher.properties.TimeWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * JdbcTimeWatchResultHandler
 *
 * @author huangchengkang
 * @date 2021/12/27 10:49
 */
@Slf4j
@ConditionalOnClass(JdbcTemplate.class)
public class JdbcTimeWatchResultHandler implements TimeWatchResultHandler ,
        InitializingBean, ApplicationContextAware {

    private volatile boolean enabled = true;

    private ApplicationContext applicationContext;

    private JdbcTemplate jdbcTemplate;

    private final TimeWatchProperties properties;

    public JdbcTimeWatchResultHandler(TimeWatchProperties properties) {
        this.properties = properties;
    }

    /**
     * 是否支持处理
     *
     * @param contextName
     * @return
     */
    @Override
    public boolean isSupported(String contextName) {
        return true;
    }

    /**
     * 处理
     *
     * @param result
     */
    @Override
    public void handle(TimeWatchResultEvent result) {
        if (!isEnabled()){
            return;
        }
        try {
            doHandle(result);
        } catch (Exception e) {
            log.warn("保存报告数据失败: {} , contextName={}, contextId={}", e.getMessage(),
                    result.getContextName(), result.getContextId());
        }
    }

    private void doHandle(TimeWatchResultEvent result) {
        // 记录明细
        List<TimeWatchRecord> allTimeWatchRecords = result.getAllTimeWatchRecords();
        jdbcTemplate.batchUpdate("insert into "+getReportInfoTableName()+" (context_id,context_name,biz_id,parent_watch_name,watch_name,watch_index,cost_millis,start_time,stop_time,thread_name,trace_id,properties,is_root) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?)", allTimeWatchRecords, allTimeWatchRecords.size(), (ps, timeWatchRecord) -> {
                    ps.setObject(1, timeWatchRecord.getContextId());
                    ps.setObject(2, timeWatchRecord.getContextName());
                    ps.setObject(3, timeWatchRecord.getBizId());
                    ps.setObject(4, timeWatchRecord.getParentWatchName());
                    ps.setObject(5, timeWatchRecord.getWatchName());
                    ps.setObject(6, timeWatchRecord.getWatchIndex());
                    ps.setObject(7, timeWatchRecord.getCostMillis());
                    ps.setObject(8, timeWatchRecord.getStartTime());
                    ps.setObject(9, timeWatchRecord.getStopTime());
                    ps.setObject(10, timeWatchRecord.getThreadName());
                    ps.setObject(11, timeWatchRecord.getTraceId());
                    ps.setObject(12, getPropertiesJsonString(timeWatchRecord.getProperties()));
                    ps.setObject(13, timeWatchRecord.isRoot()?1:0);
                });

        // 记录报告
        WatchContext watchContext = result.getWatchContext();
        byte[] reportBytes = watchContext.getReportBytes();
        jdbcTemplate.update("insert into "+getReportTableName()+" (context_id,context_name,biz_id,report_type,report_data) values (?,?,?,?,?)",
                result.getContextId(), result.getContextName(),getBizId(watchContext), 1, reportBytes);
    }

    private Object getBizId(WatchContext watchContext) {
        if (StringUtils.hasText(watchContext.getBizId())){
            return watchContext.getBizId();
        }
        return watchContext.getContextId();
    }

    private Object getPropertiesJsonString(Map<String, Object> properties) {
        if (Objects.isNull(properties)){
            return "{}";
        }
        return JSON.toJSONString(properties);
    }

    @Override
    public void afterPropertiesSet() {
        try {
            init();
        } catch (Exception e) {
            log.info("JdbcTimeWatchResultHandler is disabled, fail : {}", e.getMessage());
            setEnabled(false);
        }
    }

    private void init() {

        if (!properties.getResultHandlers().getOrDefault("jdbc", false)){
            log.info("JdbcTimeWatchResultHandler is disabled!");
            setEnabled(false);
            return;
        }

        Map<String, JdbcTemplate> jdbcTemplateMap = applicationContext.getBeansOfType(JdbcTemplate.class);
        if (CollectionUtils.isEmpty(jdbcTemplateMap)){
            setEnabled(false);
            return;
        }

        if (jdbcTemplateMap.size() == 1){
            this.jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
        }else {
            try {
                // 优先按照指定bean查找
                this.jdbcTemplate = applicationContext.getBean(properties.getJdbcTemplateBeanName(), JdbcTemplate.class);
            } catch (BeansException e) {
                // 按primary
                this.jdbcTemplate = applicationContext.getBean(JdbcTemplate.class);
            }
        }

        // 创建表
        createTableTimewatcherInfo();
        createTableTimewatcherReport();
    }

    private void createTableTimewatcherReport() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `"+getReportTableName()+"` ( " +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键', " +
                "  `context_id` varchar(64) NOT NULL COMMENT '上下文ID', " +
                "  `context_name` varchar(100) DEFAULT NULL COMMENT '上下文名称', " +
                "  `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID', " +
                "  `report_type` tinyint(4) DEFAULT '1' COMMENT '1:png图片', " +
                "  `report_data` blob COMMENT '报告数据', " +
                "  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                "  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                "  PRIMARY KEY (`id`), " +
                "  KEY `idx_context_id` (`context_id`), " +
                "  KEY `idx_created_at` (`created_at`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    private void createTableTimewatcherInfo() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS `"+getReportInfoTableName()+"` ( " +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键', " +
                "  `context_id` varchar(64) DEFAULT NULL COMMENT '上下文ID', " +
                "  `context_name` varchar(100) DEFAULT NULL COMMENT '上下文名称', " +
                "  `biz_id` varchar(64) DEFAULT NULL COMMENT '业务ID', " +
                "  `parent_watch_name` varchar(100) DEFAULT NULL COMMENT '父观测名称', " +
                "  `watch_name` varchar(100) DEFAULT NULL COMMENT '观测名称', " +
                "  `watch_index` int(11) DEFAULT NULL COMMENT '观测索引', " +
                "  `cost_millis` bigint(11) DEFAULT NULL COMMENT '耗时毫秒', " +
                "  `start_time` datetime DEFAULT NULL COMMENT '开始时间', " +
                "  `stop_time` datetime DEFAULT NULL COMMENT '结束时间', " +
                "  `thread_name` varchar(100) DEFAULT NULL COMMENT '线程名称', " +
                "  `trace_id` varchar(64) DEFAULT NULL COMMENT '追踪ID', " +
                "  `properties` text COMMENT '扩展属性', " +
                "  `is_root` tinyint(1) DEFAULT '0' COMMENT '是否根节点(1:是 0:否)', " +
                "  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', " +
                "  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', " +
                "  PRIMARY KEY (`id`), " +
                "  KEY `uniq_context_id_watch_index` (`context_id`,`watch_index`), " +
                "  KEY `idx_biz_id` (`biz_id`), " +
                "  KEY `idx_trace_id` (`trace_id`), " +
                "  KEY `idx_watch_name` (`watch_name`), " +
                "  KEY `idx_created_at` (`created_at`) " +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    }

    private String getReportTableName(){
        return properties.getJdbcReportTableName();
    }

    private String getReportInfoTableName(){
        return properties.getJdbcReportInfoTableName();
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
