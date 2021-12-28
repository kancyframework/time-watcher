package com.github.kancyframework.timewatcher;

import com.github.kancyframework.timewatcher.annotation.Watcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadLocalRandom;

/**
 * QuickStart
 *
 * @author huangchengkang
 * @date 2021/12/28 15:58
 */
@RestController
public class QuickStartController {

    @Watcher(name = "quickStart", maxTotalCostMillis = 1000, maxCostMillis = 10, enabled = false)
    @GetMapping("/quick/start")
    public void quickStart() {
        TimeWatcher.watch("controller-test-1", this::randomSleep);
        TimeWatcher.watch("controller-test-2", this::randomSleep);
        TimeWatcher.watch("controller-test-3", this::randomSleep);
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
