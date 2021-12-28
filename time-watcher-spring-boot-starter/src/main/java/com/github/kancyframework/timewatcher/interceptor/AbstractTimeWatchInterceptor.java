package com.github.kancyframework.timewatcher.interceptor;

import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.annotation.TimeWatcher;
import com.github.kancyframework.timewatcher.properties.TimeWatchProperties;
import com.github.kancyframework.timewatcher.properties.WatcherConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * AbstractTimeWatchInterceptor
 *
 * @author huangchengkang
 * @date 2021/12/25 4:57
 */
@Slf4j
public abstract class AbstractTimeWatchInterceptor implements TimeWatchInterceptor,Ordered {

    private static final LocalVariableTableParameterNameDiscoverer parameterNameDiscoverer
            = new LocalVariableTableParameterNameDiscoverer();

    private static final ThreadLocal<Data> dataThreadLocal = ThreadLocal.withInitial(Data::new);

    @Autowired
    protected TimeWatchProperties properties;

    /**
     * 是否启用
     *
     * @param contextName 上下文名称
     * @param interceptMethod
     * @return boolean
     */
    @Override
    public final boolean isEnabled(String contextName, Method interceptMethod) {
        try {
            putContextName(contextName);
            putInterceptMethod(interceptMethod);
            return isEnabled(contextName);
        } finally {
            dataThreadLocal.remove();
        }
    }

    protected boolean isEnabled(String contextName){
        return true;
    }

    /**
     * 前置处理
     *
     * @param context         上下文
     * @param interceptMethod 拦截方法
     * @param args            参数
     */
    @Override
    public final void preHandle(WatchContext context, Method interceptMethod, Object[] args){
        if (context.isEnabled()){
            putContextName(context.getContextName());
            putWatcherContext(context);
            putInterceptMethod(interceptMethod);
            putInterceptMethodArgs(args);
            doPreHandle(context, interceptMethod, args);
        }
    }

    /**
     * 前置处理
     *
     * @param context         上下文
     * @param interceptMethod 拦截方法
     * @param args            参数
     */
    protected abstract void doPreHandle(WatchContext context, Method interceptMethod, Object[] args);

    /**
     * 后置处理
     *
     * @param context         上下文
     * @param interceptMethod 拦截方法
     * @param args            参数
     * @return
     */
    @Override
    public final void postHandle(WatchContext context, Method interceptMethod, Object[] args) {
        if (context.isEnabled()){
            try {
                putContextName(context.getContextName());
                putWatcherContext(context);
                putInterceptMethod(interceptMethod);
                putInterceptMethodArgs(args);
                doPostHandle(context, interceptMethod, args);
            } finally {
                dataThreadLocal.remove();
            }
        }
    }

    /**
     * 后置处理
     *
     * @param context         上下文
     * @param interceptMethod 拦截方法
     * @param args            参数
     */
    protected abstract void doPostHandle(WatchContext context, Method interceptMethod, Object[] args);

    /**
     * 获取当前监视程序配置
     *
     * @return {@link WatcherConfig}
     */
    protected WatcherConfig getCurrentWatcherConfig(){
        Data data = dataThreadLocal.get();
        WatcherConfig watcherConfig = data.getWatcherConfig();
        if (Objects.isNull(watcherConfig)){
            watcherConfig = findWatcherConfig(data.getContextName(), data.getInterceptMethod());
            data.setWatcherConfig(watcherConfig);
        }
        return watcherConfig;
    }

    /**
     * 查找WatcherConfig配置
     * 优先级：
     *  局部 > 全局
     *  配置 > 注解
     * @param contextName
     * @param interceptMethod
     * @return
     */
    protected WatcherConfig findWatcherConfig(String contextName, Method interceptMethod) {
        WatcherConfig watcherConfig = new WatcherConfig();
        TimeWatcher annotation = AnnotatedElementUtils.findMergedAnnotation(interceptMethod, TimeWatcher.class);
        watcherConfig.setEnabled(annotation.enabled());
        watcherConfig.setMaxTotalCostMillis(annotation.maxTotalCostMillis());
        watcherConfig.setMaxCostMillis(annotation.maxCostMillis());
        watcherConfig.setSampleRate(annotation.sampleRate());
        watcherConfig.setNoThrows(annotation.noThrows());

        watcherConfig.getProperties().putAll(properties.getProperties());
        if (Objects.nonNull(properties.getMaxTotalCostMillis())){
            watcherConfig.setMaxTotalCostMillis(properties.getMaxTotalCostMillis());
        }
        if (Objects.nonNull(properties.getMaxCostMillis())){
            watcherConfig.setMaxCostMillis(properties.getMaxCostMillis());
        }
        if (Objects.nonNull(properties.getSampleRate())){
            watcherConfig.setSampleRate(properties.getSampleRate());
        }
        if (Objects.nonNull(properties.getNoThrows())){
            watcherConfig.setNoThrows(properties.getNoThrows());
        }

        String watcherConfigKey = getWatcherConfigKey(contextName);
        WatcherConfig config = properties.getWatchers().get(watcherConfigKey);
        if (Objects.nonNull(config)){
            watcherConfig.getProperties().putAll(config.getProperties());
            if (Objects.nonNull(config.getEnabled())){
                watcherConfig.setEnabled(config.getEnabled());
            }
            if (Objects.nonNull(config.getMaxTotalCostMillis())){
                watcherConfig.setMaxTotalCostMillis(config.getMaxTotalCostMillis());
            }
            if (Objects.nonNull(config.getMaxCostMillis())){
                watcherConfig.setMaxCostMillis(config.getMaxCostMillis());
            }
            if (Objects.nonNull(config.getSampleRate())){
                watcherConfig.setSampleRate(config.getSampleRate());
            }
            if (Objects.nonNull(config.getNoThrows())){
                watcherConfig.setNoThrows(config.getNoThrows());
            }
        }

        // 全局属性
        if (Objects.equals(properties.getEnabled(),Boolean.FALSE)){
            watcherConfig.setEnabled(properties.getEnabled());
        }

        return watcherConfig;
    }

    protected String getWatcherConfigKey(String contextName){
        String watcherConfigKey = contextName;
        if (contextName.startsWith("url:/")){
            watcherConfigKey =  contextName.replace("url:","url")
                    .replace("/", "-");
            return watcherConfigKey;
        }
        return watcherConfigKey;
    }


    private void putInterceptMethod(Method interceptMethod) {
        dataThreadLocal.get().setInterceptMethod(interceptMethod);
    }

    private void putContextName(String contextName) {
        dataThreadLocal.get().setContextName(contextName);
    }


    private void putInterceptMethodArgs(Object[] args) {
        dataThreadLocal.get().setInterceptMethodArgs(args);
    }

    private void putWatcherContext(WatchContext watchContext) {
        dataThreadLocal.get().setWatchContext(watchContext);
    }

    /**
     * 获取当前Http请求
     *
     * @return {@link String}
     */
    protected final HttpServletRequest getCurrentHttpRequest(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.nonNull(attributes)){
                HttpServletRequest request = attributes.getRequest();
                return request;
            }
        } catch (Exception e) {
            log.error("getCurrentHttpRequest error : {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前请求url
     *
     * @return {@link String}
     */
    protected final String getCurrentRequestUrl(){
        try {
            HttpServletRequest currentHttpRequest = getCurrentHttpRequest();
            if (Objects.nonNull(currentHttpRequest)){
                return currentHttpRequest.getRequestURI();
            }
        } catch (Exception e) {
            log.error("getCurrentRequestUrl error : {}", e.getMessage());
        }
        return null;
    }

    protected Map<String, Object> getInterceptMethodArgs(Method interceptMethod, Object[] args) {
        if (interceptMethod.getParameterCount() == 0){
            return Collections.emptyMap();
        }
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(interceptMethod);
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < parameterNames.length; i++) {
            Object object = args[i];
            if (Objects.nonNull(object)){
                map.put(parameterNames[i], object);
            }
        }
        return map;
    }

    protected Map<String, Object> getCurrentRequestQueryMap() {
        try {
            HttpServletRequest currentHttpRequest = getCurrentHttpRequest();
            if (Objects.nonNull(currentHttpRequest)){
                String queryString = currentHttpRequest.getQueryString();
                Map<String, Object> map = new HashMap<>();
                if (StringUtils.hasText(queryString)){
                    String[] kvs = queryString.split("&");
                    for (String kvStr : kvs) {
                        String[] kv = kvStr.split("=",2);
                        if (StringUtils.hasText(kv[0]) && StringUtils.hasText(kv[1])){
                            map.put(kv[0], kv[1]);
                        }
                    }
                }
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    static class Data {
        private WatchContext watchContext;
        private WatcherConfig watcherConfig;
        private String contextName;
        private Method interceptMethod;
        private Object[] interceptMethodArgs;

        public WatchContext getWatchContext() {
            return watchContext;
        }

        public void setWatchContext(WatchContext watchContext) {
            this.watchContext = watchContext;
        }

        public WatcherConfig getWatcherConfig() {
            return watcherConfig;
        }

        public void setWatcherConfig(WatcherConfig watcherConfig) {
            this.watcherConfig = watcherConfig;
        }

        public String getContextName() {
            return contextName;
        }

        public void setContextName(String contextName) {
            this.contextName = contextName;
        }

        public Method getInterceptMethod() {
            return interceptMethod;
        }

        public void setInterceptMethod(Method interceptMethod) {
            this.interceptMethod = interceptMethod;
        }

        public Object[] getInterceptMethodArgs() {
            return interceptMethodArgs;
        }

        public void setInterceptMethodArgs(Object[] interceptMethodArgs) {
            this.interceptMethodArgs = interceptMethodArgs;
        }
    }
}
