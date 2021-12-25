package com.github.kancyframework.timewatcher.span;

import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.WatchRecord;

import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Span
 *
 * @author huangchengkang
 * @date 2021/12/25 8:34
 */
public class TimeSpan {

    public static final int MAX_WITH = 1800;
    public static final int LINE_HEIGHT = 30;
    public static final int LINE_SPACING = 5;

    public static final int MARGIN = 50;
    public static final int MARGIN_TOP = 10;

    private int index;
    private int x;
    private int y;
    private int with;
    private int height;

    private long costMillis;
    private String watchName;
    private String threadName;
    private String contextName;

    public WatchRecord record;
    public WatchContext context;

    public TimeSpan(WatchContext context, WatchRecord watchRecord, int index) {
        this.context = context;
        this.record = watchRecord;
        this.index = index;
        init();
    }

    private void init() {
        long startTs = context.getRootWatchRecord().getStartTime().getTime();
        long xtime = record.getStartTime().getTime();
        long total = context.getRootWatchRecord().getCostMillis();

        x = (int) (((xtime - startTs) * MAX_WITH / total) + MARGIN);
        y = (LINE_HEIGHT + LINE_SPACING) * index + MARGIN_TOP;
        with = (int) (record.getCostMillis() * MAX_WITH / total);
        height = LINE_HEIGHT;
    }

    public String getIndexLabel(){
        return String.format("[%d]", index);
    }

    public String getSpanLabel(){
        if (isFirst()){
            return getRootSpanLabel();
        }
        return String.format("%sms | %s | %s.%s",
                record.getCostMillis(),
                record.getThreadName(),
                getClassSimpleName(record.getProperties().get("__className__").toString()),
                record.getWatchName()
        );
    }

    private String getClassSimpleName(String className){
        if (Objects.isNull(className)){
            return null;
        }

        if (!className.contains(".")){
            return className;
        }
        return className.substring(className.lastIndexOf(".")+1);
    }

    public String getRootSpanTimeLabel() {
        if (isFirst()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return String.format("%s ~ %s",
                    sdf.format(context.getRootWatchRecord().getStartTime()),
                    sdf.format(context.getRootWatchRecord().getStopTime())
            );
        }
        return "";
    }

    private String getRootSpanLabel() {
        return String.format("%sms | %s | %s - %s.%s",
                record.getCostMillis(),
                record.getThreadName(),
                context.getContextName(),
                context.getRootWatchRecord().getProperties().get("__className__"),
                context.getRootWatchRecord().getProperties().get("__methodName__")
        );
    }

    public boolean isFirst(){
        return Objects.equals(index, 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWith() {
        return with;
    }

    public int getHeight() {
        return height;
    }
}
