package com.github.kancyframework.timewatcher.properties;

import lombok.Data;

/**
 * TaskExecutorConfig
 *
 * @author huangchengkang
 * @date 2021/12/26 2:12
 */
@Data
public class ThreadPoolConfig {

    /**
     * 核心池大小(默认:CPU核数)
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /**
     * 最大池大小(默认:CPU核数 x 2)
     */
    private int maxPoolSize = corePoolSize * 2;
    /**
     * 队列容量
     */
    private int queueCapacity = 1000;
    /**
     * 任务装饰程序bean名称
     */
    private String taskDecoratorBeanName;
    /**
     * 拒绝的处理程序bean名称
     */
    private String rejectedHandlerBeanName;
    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "timewatcher-";
}
