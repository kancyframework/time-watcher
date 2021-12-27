package com.github.kancyframework.timewatcher.interceptor;

import com.github.kancyframework.timewatcher.SimpleWatchContext;
import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.WatchRecord;
import com.github.kancyframework.timewatcher.properties.WatcherConfig;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * InnerTimeWatchInterceptor
 *
 * @author huangchengkang
 * @date 2021/12/27 13:52
 */
public class InnerTimeWatchInterceptor extends AbstractTimeWatchInterceptor{

    /**
     * 前置处理
     *
     * @param context 上下文
     * @param interceptMethod
     * @param args
     * @return boolean
     */
    @Override
    protected void doPreHandle(WatchContext context, Method interceptMethod, Object[] args) {
        // 设置内置属性
        context.putContextProperty("__url__", getCurrentRequestUrl());
        context.putContextProperty("__className__", interceptMethod.getDeclaringClass().getName());
        context.putContextProperty("__methodName__", interceptMethod.getName());
        context.putContextProperty("__methodParameterCount__", interceptMethod.getParameterCount());

        // 设置配置属性
        putContextProperties(context);
    }

    private void putContextProperties(WatchContext context) {
        // 设置属性
        WatcherConfig watcherConfig = getCurrentWatcherConfig();
        context.putContextProperty(watcherConfig.getProperties());

        // 耗时时间
        if (context instanceof SimpleWatchContext){
            SimpleWatchContext simpleWatchContext = (SimpleWatchContext) context;
            Long maxTotalCostMillis = simpleWatchContext.getMaxTotalCostMillis();
            Long maxCostMillis = simpleWatchContext.getMaxCostMillis();
            Boolean noThrows = simpleWatchContext.getNoThrows();
            if (Objects.isNull(maxTotalCostMillis) || maxTotalCostMillis < 0){
                simpleWatchContext.setMaxTotalCostMillis(watcherConfig.getMaxTotalCostMillis());
            }
            if (Objects.isNull(maxCostMillis) || maxCostMillis < 0){
                simpleWatchContext.setMaxCostMillis(watcherConfig.getMaxCostMillis());
            }
            if (Objects.isNull(noThrows)){
                simpleWatchContext.setNoThrows(watcherConfig.getNoThrows());
            }
        }
    }


    /**
     * 后置处理
     *
     * @param context
     * @param interceptMethod
     * @param args
     * @return
     */
    @Override
    protected void doPostHandle(WatchContext context, Method interceptMethod, Object[] args) {
        // 回填index
        fillWatchRecordIndex(context);

        // 处理完成后，将耗时较小的数据进行移除
        removeIfShortCost(context);
    }

    /**
     * 将耗时较少的数据进行移除,无需后续分析
     * @param context 上下文
     */
    private void removeIfShortCost(WatchContext context) {
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

    /**
     * 设置观测序号
     *
     * @param context 上下文
     */
    private void fillWatchRecordIndex(WatchContext context) {
        List<WatchRecord> records = context.getWatchRecords();
        // 1.设置观测序号
        context.getRootWatchRecord().setWatchIndex(0);
        for (int i = 0; i < records.size(); i++) {
            records.get(i).setWatchIndex(i+1);
        }
        // 2.设置内置属性
        context.putContextProperty("__watchSize__", context.getWatchRecords().size());
    }


    /**
     * 是否启用
     *
     * @param contextName 上下文名称
     * @return boolean
     */
    @Override
    public boolean isEnabled(String contextName) {
        WatcherConfig watcherConfig = getCurrentWatcherConfig();
        if (!watcherConfig.getEnabled()){
            return false;
        }

        // 采样率
        double sampleRate = watcherConfig.getSampleRate();
        if (sampleRate <= 0){
            return false;
        }
        if (sampleRate >= 1.0){
            return true;
        }
        return ThreadLocalRandom.current().nextDouble() < sampleRate + 0.0001;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
