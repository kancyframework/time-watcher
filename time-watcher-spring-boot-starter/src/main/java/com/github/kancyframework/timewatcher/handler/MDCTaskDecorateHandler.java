package com.github.kancyframework.timewatcher.handler;

import org.slf4j.MDC;

import java.util.Map;

/**
 * TaskDecoratorHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 11:32
 */
public class MDCTaskDecorateHandler implements TaskDecorateHandler<Map<String, String>> {

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
    public Map<String, String> copyOfContext() {
        return MDC.getCopyOfContextMap();
    }

    /**
     * 装饰Task
     *
     * @param context
     */
    @Override
    public void decorate(Map<String, String> context) {
        MDC.setContextMap(context);
    }

    /**
     * 清除
     */
    @Override
    public void clear() {
        MDC.clear();
    }

}
