package com.github.kancyframework.timewatcher.interceptor;

import com.github.kancyframework.timewatcher.SimpleWatchContext;
import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.WatchRecord;
import com.github.kancyframework.timewatcher.properties.TimeWatchProperties;
import com.github.kancyframework.timewatcher.properties.WatcherConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ConfigEnabledTimeWatchInterceptor
 *
 * @author huangchengkang
 * @date 2021/12/25 4:38
 */
public class DefaultTimeWatchInterceptor extends AbstractTimeWatchInterceptor {

    @Autowired
    private Environment environment;

    @Autowired
    private TimeWatchProperties timeWatchProperties;

    /**
     * 是否启用
     *
     * @param contextName 上下文名称
     * @return boolean
     */
    @Override
    public boolean isEnabled(String contextName) {
        if (Objects.nonNull(timeWatchProperties) && Objects.nonNull(timeWatchProperties.getWatchers())){
            WatcherConfig watcherConfig = timeWatchProperties.getWatchers().get(contextName);
            if (Objects.nonNull(watcherConfig) && watcherConfig.isEnabled()){
                return true;
            }
        }
        String enabled = environment.getProperty(String.format("timewatcher.%s.enabled", contextName));
        if (StringUtils.hasText(enabled)){
            return enabled.equalsIgnoreCase("true");
        }
        // 不配置，默认启用
        return true;
    }

    /**
     * 前置处理
     *
     * @param context 上下文
     * @return boolean
     */
    @Override
    public void doPreHandle(WatchContext context) {
        if (Objects.isNull(timeWatchProperties)){
            return;
        }

        // 设置全局属性
        Map<String, Object> properties = timeWatchProperties.getProperties();
        if (Objects.nonNull(properties)){
            context.putContextProperty(properties);
        }

        // 设置私有属性
        WatcherConfig watcherConfig = findWatcherConfig(context.getContextName());
        if (Objects.nonNull(watcherConfig)){
            // 额外属性
            context.putContextProperty(watcherConfig.getProperties());
            // 耗时时间
            if (context instanceof SimpleWatchContext){
                SimpleWatchContext simpleWatchContext = (SimpleWatchContext) context;
                Long maxTotalCostMillis = simpleWatchContext.getMaxTotalCostMillis();
                Long maxCostMillis = simpleWatchContext.getMaxCostMillis();

                if (Objects.isNull(maxTotalCostMillis) || maxTotalCostMillis < 0){
                    simpleWatchContext.setMaxTotalCostMillis(watcherConfig.getMaxTotalCostMillis());
                }

                if (Objects.isNull(maxCostMillis) || maxCostMillis < 0){
                    simpleWatchContext.setMaxCostMillis(watcherConfig.getMaxCostMillis());
                }
            }
        }

        // 设置内置属性
        context.putContextProperty("__url__", getCurrentRequestUrl());

    }

    private WatcherConfig findWatcherConfig(String contextName) {
        if (Objects.isNull(timeWatchProperties)){
            return null;
        }

        Map<String, WatcherConfig> watchers = timeWatchProperties.getWatchers();
        if (Objects.isNull(watchers) || !watchers.containsKey(contextName)){
            WatcherConfig watcherConfig = new WatcherConfig();
            watcherConfig.setEnabled(timeWatchProperties.isEnabled());
            watcherConfig.setProperties(timeWatchProperties.getProperties());
            watcherConfig.setMaxCostMillis(timeWatchProperties.getMaxCostMillis());
            watcherConfig.setMaxTotalCostMillis(timeWatchProperties.getMaxTotalCostMillis());
            return watcherConfig;
        }
        return watchers.get(contextName);
    }

    /**
     * 后置处理
     *
     * @param context
     * @return
     */
    @Override
    public void doPostHandle(WatchContext context) {
        // 回填index
        context.getRootWatchRecord().setIndex(0);
        List<WatchRecord> records = context.getWatchRecords();
        if (Objects.nonNull(records)){
            for (int i = 0; i < records.size(); i++) {
                records.get(i).setIndex(i+1);
            }
        }

        // 设置内置属性
        context.putContextProperty("__watchSize__", Objects.isNull(records) ? 0 : records.size());

        // 处理完成后，将耗时较小的数据进行移除
        if (context instanceof SimpleWatchContext){
            SimpleWatchContext simpleWatchContext = (SimpleWatchContext) context;

            Long maxTotalCostMillis = simpleWatchContext.getMaxTotalCostMillis();
            if (maxTotalCostMillis > 0
                    && context.getRootWatchRecord().getCostMillis() < maxTotalCostMillis){
                TimeWatcher.close();
            }

            Long maxCostMillis = simpleWatchContext.getMaxCostMillis();
            if (maxCostMillis > 0){
                List<WatchRecord> watchRecords = context.getWatchRecords();
                if (Objects.nonNull(watchRecords)){
                    // 移除耗时较小的数据
                    watchRecords.removeIf(watchRecord ->watchRecord.getCostMillis() < maxCostMillis);
                }
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
