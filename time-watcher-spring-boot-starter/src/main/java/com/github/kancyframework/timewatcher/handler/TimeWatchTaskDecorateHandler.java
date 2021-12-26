package com.github.kancyframework.timewatcher.handler;

/**
 * TaskDecoratorHandler
 *
 * @author huangchengkang
 * @date 2021/12/26 11:32
 */
public interface TimeWatchTaskDecorateHandler<T> {

    /**
     * 名字
     * @return
     */
    default String name(){
        return null;
    }

    /**
     * 准备上下文数据
     */
    T copyOfContext();

    /**
     * 装饰Task
     * @param context
     */
    void decorate(T context);

    /**
     * 清除
     */
    void clear();
}
