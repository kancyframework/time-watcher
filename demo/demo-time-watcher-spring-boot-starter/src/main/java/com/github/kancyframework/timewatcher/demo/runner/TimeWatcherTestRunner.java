package com.github.kancyframework.timewatcher.demo.runner;

import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.annotation.TimeWatch;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * DingTalkClientDemo
 *
 * @author huangchengkang
 * @date 2021/11/17 20:36
 */
@Component
public class TimeWatcherTestRunner implements ApplicationRunner {

    @TimeWatch(noThrows = false)
    @Override
    public void run(ApplicationArguments args) {
        TimeWatcher.watch("runner-test-1", this::randomSleep);
        TimeWatcher.watch("runner-test-2", this::randomSleep);
        TimeWatcher.watch("runner-test-3", this::randomSleep);
    }

    private void randomSleep() {
        sleep(ThreadLocalRandom.current().nextInt(0,500));
    }

    private Object sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
