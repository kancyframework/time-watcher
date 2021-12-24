package com.github.kancyframework.timewatcher.interceptor;

import com.github.kancyframework.timewatcher.WatchContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * AbstractTimeWatchInterceptor
 *
 * @author huangchengkang
 * @date 2021/12/25 4:57
 */
@Slf4j
public abstract class AbstractTimeWatchInterceptor implements TimeWatchInterceptor , Ordered {

    /**
     * 前置处理
     *
     * @param context 上下文
     * @return boolean
     */
    @Override
    public void preHandle(WatchContext context) {
        if (context.isEnabled()){
            doPreHandle(context);
        }
    }

    /**
     * 前置处理
     *
     * @param context 上下文
     * @return boolean
     */
    protected abstract void doPreHandle(WatchContext context);

    /**
     * 后置处理
     *
     * @param context
     * @return
     */
    @Override
    public void postHandle(WatchContext context) {
        if (context.isEnabled()){
            doPostHandle(context);
        }
    }
    /**
     * 后置处理
     *
     * @param context
     * @return
     */
    protected abstract void doPostHandle(WatchContext context);

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

    @Override
    public int getOrder() {
        return 0;
    }
}
