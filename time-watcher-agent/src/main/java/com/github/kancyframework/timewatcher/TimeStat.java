package com.github.kancyframework.timewatcher;

import lombok.extern.slf4j.Slf4j;

/**
 * TimeStat
 *
 * @author huangchengkang
 * @date 2021/12/30 17:52
 */
@Slf4j
public class TimeStat {
    static ThreadLocal<Long> t = new ThreadLocal<>();

    public static void start() {
        t.set(System.currentTimeMillis());
    }

    public static void end() {
        Long startTime = t.get();
        Long stopTime = System.currentTimeMillis();
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
        log.info("执行方法：{}.{}()，耗时：{}",stackTraceElement.getClassName(), stackTraceElement.getMethodName(), (stopTime - startTime));
    }
}