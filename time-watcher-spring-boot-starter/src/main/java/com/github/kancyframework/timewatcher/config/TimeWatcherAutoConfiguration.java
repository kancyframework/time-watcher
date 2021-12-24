package com.github.kancyframework.timewatcher.config;

import com.github.kancyframework.timewatcher.aspect.TimeWatchAspect;
import com.github.kancyframework.timewatcher.interceptor.DefaultTimeWatchInterceptor;
import com.github.kancyframework.timewatcher.properties.TimeWatchProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TimeWatcherAutoConfiguration
 *
 * @author huangchengkang
 * @date 2021/12/25 11:12
 */
@ConditionalOnProperty(prefix = "timewatcher", name = "enabled", matchIfMissing = true)
@Configuration
public class TimeWatcherAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TimeWatchProperties timeWatchProperties(){
        return new TimeWatchProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public TimeWatchAspect timeWatchAspect(){
        return new TimeWatchAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultTimeWatchInterceptor defaultTimeWatchInterceptor(){
        return new DefaultTimeWatchInterceptor();
    }
}
