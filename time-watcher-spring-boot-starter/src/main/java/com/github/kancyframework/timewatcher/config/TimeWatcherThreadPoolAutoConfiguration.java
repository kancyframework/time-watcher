package com.github.kancyframework.timewatcher.config;

import com.github.kancyframework.timewatcher.handler.MdcThreadLocalHandler;
import com.github.kancyframework.timewatcher.handler.ThreadLocalHandler;
import com.github.kancyframework.timewatcher.properties.ThreadPoolConfig;
import com.github.kancyframework.timewatcher.properties.TimeWatchProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.stream.Collectors;

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
    private TimeWatchProperties timeWatchProperties = new TimeWatchProperties();

    @Autowired(required = false)
    private List<ThreadLocalHandler> threadLocalHandlers = new ArrayList<>();

    @Autowired
    private ApplicationContext applicationContext;


    @Bean
    @ConditionalOnMissingBean
    public MdcThreadLocalHandler mdcThreadLocalHandler() {
        return new MdcThreadLocalHandler();
    }

    @Bean(name = "timeWatcherExecutor")
    public TaskExecutor timeWatcherExecutor() {
        TimeWatchProperties properties = timeWatchProperties;
        if (Objects.isNull(properties)) {
            properties = new TimeWatchProperties();
        }

        ThreadPoolConfig taskProperties = properties.getTaskExecutor();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskProperties.getCorePoolSize());
        executor.setMaxPoolSize(taskProperties.getMaxPoolSize());
        executor.setQueueCapacity(taskProperties.getQueueCapacity());
        executor.setThreadNamePrefix(taskProperties.getThreadNamePrefix());
        executor.setKeepAliveSeconds(taskProperties.getKeepAliveSeconds());

        // TaskDecorator
        executor.setTaskDecorator(getTaskDecorator(taskProperties));
        // RejectedExecutionHandler
        executor.setRejectedExecutionHandler(getRejectedExecutionHandler(taskProperties));

        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    private RejectedExecutionHandler getRejectedExecutionHandler(ThreadPoolConfig taskProperties) {
        RejectedExecutionHandler rejectedExecutionHandler =
                findRejectedHandler(taskProperties.getRejectedHandlerBeanName());
        if (Objects.nonNull(rejectedExecutionHandler)) {
            return rejectedExecutionHandler;
        } else {
            return (runnable, pool) ->
                    log.warn("timeWatcherExecutor ??????????????????????????????????????????????????????{}????????????????????????{}",
                            pool.getActiveCount(), taskProperties.getQueueCapacity());
        }
    }

    private TaskDecorator getTaskDecorator(ThreadPoolConfig taskProperties) {
        TaskDecorator taskDecorator = findTaskDecorator(taskProperties.getTaskDecoratorBeanName());
        if (Objects.nonNull(taskDecorator)) {
            return taskDecorator;
        } else {
            return runnable -> {
                List<ThreadLocalHandler> handlers = threadLocalHandlers
                        .stream()
                        .filter(h -> taskProperties.getThreadLocalHandlers().getOrDefault(h.name(), true))
                        .collect(Collectors.toList());
                List<Object> contexts = handlers.stream()
                        .map(ThreadLocalHandler::copyCurrentThreadLocalContext)
                        .collect(Collectors.toList());

                return () -> {
                    try {
                        for (int i = 0; i < handlers.size(); i++) {
                            ThreadLocalHandler<Object> threadLocalHandler = handlers.get(i);
                            Object context = contexts.get(i);
                            if (Objects.nonNull(context)){
                                threadLocalHandler.setThreadLocalContext(contexts.get(i));
                            }
                        }
                        runnable.run();
                    } finally {
                        handlers.forEach(ThreadLocalHandler::clearThreadLocalContext);
                    }
                };
            };
        }
    }

    private RejectedExecutionHandler findRejectedHandler(String beanName) {
        if (!StringUtils.hasText(beanName)) {
            return null;
        }
        try {
            return applicationContext.getBean(beanName, RejectedExecutionHandler.class);
        } catch (BeansException e) {
            return null;
        }
    }

    private TaskDecorator findTaskDecorator(String beanName) {
        if (!StringUtils.hasText(beanName)) {
            return null;
        }
        try {
            return applicationContext.getBean(beanName, TaskDecorator.class);
        } catch (BeansException e) {
            return null;
        }
    }
}
