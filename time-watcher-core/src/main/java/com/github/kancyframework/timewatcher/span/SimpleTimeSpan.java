package com.github.kancyframework.timewatcher.span;

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
}
