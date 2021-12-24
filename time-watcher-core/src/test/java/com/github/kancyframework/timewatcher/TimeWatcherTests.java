package com.github.kancyframework.timewatcher;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Test
 *
 * @author huangchengkang
 * @date 2021/12/24 22:28
 */
public class TimeWatcherTests {

    @Test
    public void test01(){
        TimeWatcher.enabled();
        TimeWatcher.startWatch("test");

        TimeWatcher.watch("test-1", ()-> sleep(100));
        TimeWatcher.watch("test-2", () -> sleep(100));
        TimeWatcher.watch("test-3", ()-> sleep(200));
        TimeWatcher.stopWatch();

        System.out.println(JSON.toJSONString(TimeWatcher.getWatchContext(), true));
        TimeWatcher.close();
    }

    @Test
    public void test02(){
        TimeWatcher.enabled();
        TimeWatcher.startWatch("test");

        TimeWatcher.watch("test1", ()-> sleep(100));
        TimeWatcher.watch(() -> {
            WatchContext watchContext = TimeWatcher.snapshotWatchContext();
            Arrays.asList("task1","task2", "task3").parallelStream().forEach(task->{
                TimeWatcher.transferWatchContext(watchContext);
                TimeWatcher.watch(task, ()-> doTask(task));
                TimeWatcher.close();
            });
        });
        TimeWatcher.watch("tes3", ()-> sleep(1000));

        TimeWatcher.stopWatch();
        System.out.println(JSON.toJSONString(TimeWatcher.getWatchContext()));

    }

    private Object doTask(String task) {
        WatchContext watchContext = TimeWatcher.copyOfWatchContext();
        Arrays.asList(task + "_1",task + "_2").parallelStream().forEach(t->{
            TimeWatcher.transferWatchContext(watchContext);
            TimeWatcher.watch(t, this::randomSleep);
            TimeWatcher.close();
        });
        return 0;
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
