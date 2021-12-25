package com.github.kancyframework.timewatcher.config;

import com.github.kancyframework.timewatcher.properties.ThreadPoolConfig;
import com.github.kancyframework.timewatcher.properties.TimeWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;

/**
 * TimeWatcherThreadPoolConfig
 *
 * @author huangchengkang
 * @date 2021/12/26 12:21
 */
@Slf4j
@ConditionalOnProperty(prefix = "timewatcher", name = "enabled", matchIfMissing = true)
@Configuration
public class TimeWatcherThreadPoolAutoConfiguration {

    @Autowired(required = false)
    private TimeWatchProperties timeWatchProperties;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean(name = "timeWatcherExecutor")
    public TaskExecutor timeWatcherExecutor() {
        TimeWatchProperties properties = timeWatchProperties;
        if (Objects.isNull(properties)){
            properties = new TimeWatchProperties();
        }

        ThreadPoolConfig taskProperties = properties.getTaskExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskProperties.getQueueCapacity());
        executor.setThreadNamePrefix(taskProperties.getThreadNamePrefix());

        TaskDecorator taskDecorator = findTaskDecorator(taskProperties.getTaskDecoratorBeanName());
        if (Objects.nonNull(taskDecorator)){
            executor.setTaskDecorator(taskDecorator);
        }

        RejectedExecutionHandler rejectedExecutionHandler =
                findRejectedHandler(taskProperties.getRejectedHandlerBeanName());
        if (Objects.nonNull(taskDecorator)){
            executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        }else {
            executor.setRejectedExecutionHandler((r, pool) ->
                    log.warn("timeWatcherExecutor 任务挤压太多了，当前任务数：{}，异常队列容量：{}",
                        pool.getTaskCount(), taskProperties.getQueueCapacity()));
        }
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    private RejectedExecutionHandler findRejectedHandler(String beanName) {
        if (!StringUtils.hasText(beanName)){
            return null;
        }
        try {
            return applicationContext.getBean(beanName, RejectedExecutionHandler.class);
        } catch (BeansException e) {
            return null;
        }
    }

    private TaskDecorator findTaskDecorator(String beanName) {
        if (!StringUtils.hasText(beanName)){
            return null;
        }
        try {
            return applicationContext.getBean(beanName, TaskDecorator.class);
        } catch (BeansException e) {
            return null;
        }
    }
}
