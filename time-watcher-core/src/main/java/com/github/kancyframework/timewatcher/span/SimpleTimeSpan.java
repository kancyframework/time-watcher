package com.github.kancyframework.timewatcher.span;

import java.util.UUID;

/**
 * SimpleTimeSpan
 *
 * @author huangchengkang
 * @date 2021/12/26 13:46
 */
public class SimpleTimeSpan extends TimeSpan{
    public SimpleTimeSpan(int index) {
        super(index);
    }

    @Override
    public String getSpanLabel() {
        return "";
    }

    @Override
    public String getRootSpanTimeLabel() {
        return "";
    }

    public String getFileName(){
        return String.format("耗时分析报告_%s.png", UUID.randomUUID().toString());
    }
}
