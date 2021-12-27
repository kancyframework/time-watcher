package com.github.kancyframework.timewatcher.demo.service;

import com.github.kancyframework.timewatcher.TimeWatcher;
import com.github.kancyframework.timewatcher.annotation.TimeWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * DemoService
 *
 * @author huangchengkang
 * @date 2021/12/25 21:17
 */
@Slf4j
@Service
public class DemoService {

    @TimeWatch(context = "DemoService.index")
    public String index() throws Exception{
        TimeWatcher.watch(()-> {
            try {
                Thread.sleep(120);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        TimeWatcher.watch(()-> {
            try {
                Thread.sleep(220);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return "index2";
    }
}
