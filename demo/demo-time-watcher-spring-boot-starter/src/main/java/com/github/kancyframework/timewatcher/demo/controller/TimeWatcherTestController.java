package com.github.kancyframework.timewatcher.demo.controller;

import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.annotation.TimeWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TimeWatcherTestController
 *
 * @author huangchengkang
 * @date 2021/12/25 2:55
 */
@RestController
public class TimeWatcherTestController {

    @TimeWatch
    @GetMapping("/index")
    public Object index(){
        TimeWatcher.watch("controller-test-1", this::randomSleep);
        TimeWatcher.watch("controller-test-2", this::randomSleep);
        TimeWatcher.watch("controller-test-3", this::randomSleep);
        return "index";
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
