package com.github.kancyframework.timewatcher;

import java.util.concurrent.ThreadLocalRandom;

/**
 * QuickStart
 *
 * @author huangchengkang
 * @date 2021/12/28 15:36
 */
public class QuickStart {
    public static void main(String[] args) {
        QuickStart quickStart = new QuickStart();
        quickStart.run();
    }

    private void run() {
        // 开始
        TimeWatcher.quickStart("test");

        // 观察
        TimeWatcher.watch("test-1", ()-> sleep(100));
        TimeWatcher.watch("test-2", () -> sleep(120));
        TimeWatcher.watch("test-3", this::randomSleep);
        TimeWatcher.watch("test-4", this::randomSleep);

        // 停止
        TimeWatcher.stop();

        // 显示GUI图形化报告
        TimeWatcher.showGuiReport();

        // 将图形化报告保存成文件
        TimeWatcher.saveImageReport();
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



