package com.github.kancyframework.timewatcher.aspect;

import com.github.kancyframework.timewatcher.SimpleWatchContext;
import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.annotation.TimeWatch;
import com.github.kancyframework.timewatcher.event.TimeWatchResultEvent;
import com.github.kancyframework.timewatcher.event.TimeWatchStartedEvent;
import com.github.kancyframework.timewatcher.event.TimeWatchStoppedEvent;
import com.github.kancyframework.timewatcher.interceptor.TimeWatchInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * TimeWatchAspect
 *
 * @author huangchengkang
 * @date 2021/12/25 11:17
 */
@Slf4j
@Aspect
public class TimeWatchAspect {
    
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private List<TimeWatchInterceptor> timeWatchInterceptors = Collections.emptyList();

    /**
     * 切点
     */
    @Pointcut("@annotation(com.github.kancyframework.timewatcher.annotation.TimeWatch)")
    public void timeWatchPointCut() {
        // doNothing
    }

    /**
     * 环绕处理
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "timeWatchPointCut()")
    public Object timeWatchAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!timeWatchBefore(joinPoint)){
            return joinPoint.proceed();
        }

        Throwable throwable = null;
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            timeWatchReturn(joinPoint, throwable);
        }

    }

    private void timeWatchReturn(ProceedingJoinPoint joinPoint, Throwable throwable) {
        Method currentMethod = getCurrentMethod(joinPoint);
        TimeWatch annotation = currentMethod.getAnnotation(TimeWatch.class);
        if (!annotation.enabled()){
            return;
        }

        try {
            WatchContext watchContext = TimeWatcher.getWatchContext();
            if (watchContext.isEnabled()){
                // 停止Watch
                TimeWatcher.stop();
                // 后置处理
                for (TimeWatchInterceptor timeWatchInterceptor : timeWatchInterceptors) {
                    timeWatchInterceptor.postHandle(watchContext);
                }
                // 发送事件
                if (watchContext.isEnabled()){
                    TimeWatchStoppedEvent timeWatchStoppedEvent = new TimeWatchResultEvent(joinPoint, TimeWatcher.getWatchContext());
                    timeWatchStoppedEvent.setThrowable(throwable);
                    applicationContext.publishEvent(timeWatchStoppedEvent);
                }
            }
        } catch (Exception e) {
            if (!annotation.noThrows()){
                throw e;
            }
            log.error("timeWatchReturn fail: {}", e.getMessage());
        } finally {
            TimeWatcher.close();
        }

    }

    /**
     * 调用之前
     */
    public boolean timeWatchBefore(JoinPoint joinPoint) {
        Method currentMethod = getCurrentMethod(joinPoint);

        TimeWatch annotation = currentMethod.getAnnotation(TimeWatch.class);
        if (!annotation.enabled()){
            return false;
        }

        WatchContext watchContext = TimeWatcher.getWatchContext();
        if (watchContext.isEnabled()
                && Objects.nonNull(watchContext.getContextId())){
            return false;
        }

        // 前置开关拦截
        for (TimeWatchInterceptor timeWatchInterceptor : timeWatchInterceptors) {
            if (!timeWatchInterceptor.isEnabled(watchContext.getContextName())){
                watchContext.setEnabled(false);
                log.info("timeWatchInterceptor enabled is false , current watchContext : {}", watchContext.getContextName());
                return false;
            }
        }

        // 启用时设置注解的属性
        if (watchContext instanceof SimpleWatchContext){
            SimpleWatchContext simpleWatchContext = (SimpleWatchContext) watchContext;
            simpleWatchContext.setMaxTotalCostMillis(annotation.maxTotalCostMillis());
            simpleWatchContext.setMaxCostMillis(annotation.maxCostMillis());
        }

        try {
            TimeWatcher.enabled();
            TimeWatcher.start(getWatchContextName(currentMethod, annotation.context()));

            // 设置私有属性
            watchContext.putContextProperty("__root__", true);
            watchContext.putContextProperty("__className__", currentMethod.getDeclaringClass().getName());
            watchContext.putContextProperty("__methodName__", currentMethod.getName());
            watchContext.putContextProperty("__methodParameterCount__", currentMethod.getParameterCount());

            // 前置处理
            for (TimeWatchInterceptor timeWatchInterceptor : timeWatchInterceptors) {
                timeWatchInterceptor.preHandle(watchContext);
            }
            // 发送开始事件
            if (watchContext.isEnabled()){
                TimeWatchStartedEvent timeWatchStartedEvent = new TimeWatchStartedEvent(joinPoint, TimeWatcher.getWatchContext());
                applicationContext.publishEvent(timeWatchStartedEvent);
            }
        } catch (Exception e) {
            if (!annotation.noThrows()){
                throw e;
            }
            log.error("timeWatchBefore fail: {}", e.getMessage());
        }
        return true;
    }

    private String getWatchContextName(Method currentMethod, String contextName) {
        if (StringUtils.hasText(contextName)){
            return contextName;
        }

        String currentRequestUrl = getCurrentRequestUrl();
        if (StringUtils.hasText(currentRequestUrl)){
            contextName = String.format("url:%s", currentRequestUrl);
            return contextName;
        }

        String className = currentMethod.getDeclaringClass().getSimpleName();
        String methodName = currentMethod.getName();
        Integer paramCnt = currentMethod.getParameterCount();
        return String.format("method:%s.%s(%d)", className, methodName, paramCnt);
    }

    /**
     * 获取当前方法
     *
     * @param joinPoint 连接点
     * @return {@link Method}
     */
    private Method getCurrentMethod(JoinPoint joinPoint) {
        return ((MethodSignature) joinPoint.getSignature()).getMethod();
    }

    /**
     * 获取当前请求url
     *
     * @return {@link String}
     */
    private String getCurrentRequestUrl(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.nonNull(attributes)){
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            log.error("getCurrentRequestUrl error : {}", e.getMessage());
        }
        return null;
    }

}
