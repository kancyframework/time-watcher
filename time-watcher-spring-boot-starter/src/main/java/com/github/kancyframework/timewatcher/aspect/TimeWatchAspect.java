package com.github.kancyframework.timewatcher.aspect;

import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.WatchContext;
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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
public class TimeWatchAspect implements Ordered {
    
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired(required = false)
    private List<TimeWatchInterceptor> timeWatchInterceptors = new ArrayList<>();

    /**
     * 切点
     */
    @Pointcut("@annotation(com.github.kancyframework.timewatcher.annotation.TimeWatcher) || " +
              "@annotation(com.github.kancyframework.timewatcher.annotation.Watcher)")
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
        if (!canTimeWatch(joinPoint)){
            try {
                return joinPoint.proceed();
            } finally {
                WatchContext watchContext = TimeWatcher.getWatchContext();
                if (!watchContext.isEnabled()){
                    TimeWatcher.close();
                }
            }
        }

        Throwable throwable = null;
        try {
            timeWatchBefore(joinPoint);
            return joinPoint.proceed();
        } catch (Throwable e) {
            throwable = e;
            throw e;
        } finally {
            timeWatchReturn(joinPoint, throwable);
        }

    }

    private boolean canTimeWatch(JoinPoint joinPoint) {
        try {
            // 提前拦截
            WatchContext watchContext = TimeWatcher.getWatchContext();
            if (watchContext.isEnabled() && Objects.nonNull(watchContext.getContextId())){
                return false;
            }

            // 前置开关拦截
            Method currentMethod = getCurrentMethod(joinPoint);
            String watchContextName = getWatchContextName(currentMethod);
            for (TimeWatchInterceptor timeWatchInterceptor : timeWatchInterceptors) {
                if (!timeWatchInterceptor.isEnabled(watchContextName, currentMethod)){
                    log.info("timeWatchInterceptor enabled is false , current method : {}.{}(..)",
                            currentMethod.getDeclaringClass().getSimpleName(), currentMethod.getName());
                    return false;
                }
            }
            watchContext.setContextName(watchContextName);
        } catch (Exception e) {
            log.info("canTimeWatch fail : {}", e.getMessage());
        }
        return true;
    }

    private void timeWatchReturn(ProceedingJoinPoint joinPoint, Throwable throwable) {
        Method currentMethod = getCurrentMethod(joinPoint);
        try {
            WatchContext watchContext = TimeWatcher.getWatchContext();
            if (watchContext.isEnabled()){
                // 停止Watch
                TimeWatcher.stop();
                // 后置处理
                for (TimeWatchInterceptor timeWatchInterceptor : timeWatchInterceptors) {
                    timeWatchInterceptor.postHandle(watchContext, currentMethod, joinPoint.getArgs());
                }
                // 发送事件
                if (watchContext.isEnabled()){
                    TimeWatchStoppedEvent timeWatchStoppedEvent = new TimeWatchResultEvent(joinPoint, watchContext);
                    timeWatchStoppedEvent.setThrowable(throwable);
                    applicationContext.publishEvent(timeWatchStoppedEvent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("timeWatchReturn fail: {}", e.getMessage());
        } finally {
            TimeWatcher.close();
        }

    }

    /**
     * 调用之前
     */
    public void timeWatchBefore(JoinPoint joinPoint) {
        try {
            WatchContext watchContext = TimeWatcher.getWatchContext();
            Method currentMethod = getCurrentMethod(joinPoint);
            TimeWatcher.enabled();
            TimeWatcher.start(watchContext.getContextName());

            // 前置处理
            for (TimeWatchInterceptor timeWatchInterceptor : timeWatchInterceptors) {
                timeWatchInterceptor.preHandle(watchContext, currentMethod, joinPoint.getArgs());
            }

            // 发送开始事件
            if (watchContext.isEnabled()){
                TimeWatchStartedEvent timeWatchStartedEvent = new TimeWatchStartedEvent(joinPoint, watchContext);
                applicationContext.publishEvent(timeWatchStartedEvent);
            }
        } catch (Exception e) {
            log.error("timeWatchBefore fail: {}", e.getMessage());
        }
    }

    private String getWatchContextName(Method currentMethod) {
        com.github.kancyframework.timewatcher.annotation.TimeWatcher annotation
                = AnnotatedElementUtils.findMergedAnnotation(currentMethod,
                com.github.kancyframework.timewatcher.annotation.TimeWatcher.class);

        String contextName = annotation.value();

        if (StringUtils.hasText(contextName)){
            return contextName;
        }

        // 如果是controller
        if (Objects.nonNull(AnnotationUtils.findAnnotation(currentMethod.getDeclaringClass(), Controller.class))){
            String currentRequestUrl = getCurrentRequestUrl();
            if (StringUtils.hasText(currentRequestUrl)){
                contextName = String.format("url:%s", currentRequestUrl);
                return contextName;
            }
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

    @Override
    public int getOrder() {
        // 最高优先级，整体统计时间包含其他低优先级切面执行的时间
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
