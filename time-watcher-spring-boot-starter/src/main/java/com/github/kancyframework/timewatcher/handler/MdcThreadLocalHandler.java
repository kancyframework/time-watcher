package com.github.kancyframework.timewatcher.handler;

import org.slf4j.MDC;

import java.util.Map;

/**
 * MdcThreadLocalHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 11:32
 */
public class MdcThreadLocalHandler implements ThreadLocalHandler<Map<String, String>> {

    /**
     * 名字
     *
     * @return
     */
    @Override
    public String name() {
        return "mdc";
    }

    /**
     * 准备上下文数据
     */
    @Override
    public Map<String, String> copyCurrentThreadLocalContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 装饰Task
     *
     * @param context
     */
    @Override
    public void setThreadLocalContext(Map<String, String> context) {
        MDC.setContextMap(context);
    }

    /**
     * 清除
     */
    @Override
    public void clearThreadLocalContext() {
        MDC.clear();
    }

}
