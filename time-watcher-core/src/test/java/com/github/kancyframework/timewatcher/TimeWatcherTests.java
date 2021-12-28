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

    public static void main(String[] args) {
        new TimeWatcherTests().test02();
        TimeWatcher.showGuiReport();
    }

    @Test
    public void test01(){
        TimeWatcher.enabled();
        TimeWatcher.start("test");
        TimeWatcher.watch("test-1", ()-> sleep(100));
        TimeWatcher.watch("test-2", () -> sleep(100));
        TimeWatcher.watch("test-3", ()-> sleep(200));
        TimeWatcher.watch("test-4", ()-> sleep(200));
        TimeWatcher.stopWatch();

        System.out.println(JSON.toJSONString(TimeWatcher.getWatchContext()));
    }

    @Test
    public void test02(){
        TimeWatcher.enabled();
        TimeWatcher.start("test");

        TimeWatcher.watch("test1", ()-> sleep(100));
        TimeWatcher.watch(() -> {
            WatchContext watchContext = TimeWatcher.snapshotWatchContext();
            Arrays.asList("task1","task2", "task3").parallelStream().forEach(task->{
                TimeWatcher.transferWatchContext(watchContext);
                TimeWatcher.watch(task, ()-> doTask(task));
                TimeWatcher.close();
            });
        });
        TimeWatcher.watch("tes3", ()-> sleep(100));

        int len = ThreadLocalRandom.current().nextInt(10, 30);
        for (int i = 4; i < len; i++) {
            TimeWatcher.watch("tes" + i, ()-> sleep(20));
        }

        TimeWatcher.stopWatch();
        System.out.println(JSON.toJSONString(TimeWatcher.getWatchContext()));

    }

    @Test
    public void test03(){
        TimeWatcher.enabled();
        TimeWatcher.start("test");

        TimeWatcher.watch("test1", ()-> sleep(100));

        TimeWatcher.watch("test-thread",() -> {
            WatchContext watchContext = TimeWatcher.snapshotWatchContext();
            for (String task : Arrays.asList("task1", "task2", "task3")) {
                Thread thread = new Thread(() -> {
                    TimeWatcher.transferWatchContext(watchContext);
                    TimeWatcher.watch(task, ()-> doTask(task));
                    TimeWatcher.close();
                });
                thread.start();
            }
        });

        TimeWatcher.watch("tes2", ()-> sleep(100));
        TimeWatcher.watch("tes3", ()-> sleep(1000));

        TimeWatcher.stop();
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
