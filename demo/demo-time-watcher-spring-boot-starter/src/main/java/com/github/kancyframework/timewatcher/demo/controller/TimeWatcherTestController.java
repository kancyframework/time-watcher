package com.github.kancyframework.timewatcher.demo.controller;

import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.annotation.Watcher;
import com.github.kancyframework.timewatcher.demo.aspect.Test;
import com.github.kancyframework.timewatcher.demo.service.DemoService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private DemoService demoService;

    @Watcher
    @GetMapping("/index")
    public Object index() throws Exception {
        TimeWatcher.watch("controller-test-1", this::randomSleep);
        TimeWatcher.watch("controller-test-2", this::randomSleep);
        demoService.index();
        TimeWatcher.watch("controller-test-3", this::randomSleep);
        return "index";
    }

    @Test
    @Watcher(name = "hello", maxCostMillis = 9)
    @GetMapping("/hello")
    public Object hello() throws Exception {
        TimeWatcher.watch("controller-test-1", this::randomSleep);
        TimeWatcher.watch("controller-test-2", ()->sleep(10));
        TimeWatcher.watch("controller-test-3", this::randomSleep);
        return "hello";
    }

    @Test
    @Watcher(name = "post", maxCostMillis = 9)
    @PostMapping("/post")
    public Object post(@RequestBody PostData data) throws Exception {
        TimeWatcher.watch("controller-post-1", this::randomSleep);
        TimeWatcher.watch("controller-post-2", ()->sleep(10));
        TimeWatcher.watch("controller-post-3", this::randomSleep);
        return "post";
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

    @Data
    static class PostData{
        private String name;
        private Integer age;
        private byte[] bytes;
    }
}
