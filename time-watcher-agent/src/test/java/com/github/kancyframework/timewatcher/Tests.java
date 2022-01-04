package com.github.kancyframework.timewatcher;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Tests
 *
 * Jvm启动参数：-noverify -javaagent:time-watcher-agent/target/time-watcher-agent.jar=com.github.kancyframework
 *
 * @author huangchengkang
 * @date 2021/12/30 16:17
 */
@Slf4j
public class Tests {

    public static void main(String[] args) {
        test01();
        test02();
    }

    private static void test02() {
        randomSleep();
    }

    private static void test01() {
        randomSleep();
    }

    private static void randomSleep(){
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(0,100));
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }
}
