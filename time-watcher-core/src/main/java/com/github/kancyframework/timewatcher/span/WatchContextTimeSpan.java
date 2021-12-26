package com.github.kancyframework.timewatcher.span;

import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.WatchRecord;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Objects;

/**
 * WatchContextTimeSpan
 *
 * @author huangchengkang
 * @date 2021/12/26 13:37
 */
public class WatchContextTimeSpan extends TimeSpan{
    private final WatchRecord watchRecord;
    private final WatchContext watchContext;

    public WatchContextTimeSpan(int index, WatchRecord watchRecord, WatchContext watchContext) {
        super(index);
        this.watchRecord = watchRecord;
        this.watchContext = watchContext;
        init();
    }

    protected void init() {
        long startTs = watchContext.getRootWatchRecord().getStartTime().getTime();
        long xtime = watchRecord.getStartTime().getTime();
        long total = watchContext.getRootWatchRecord().getCostMillis();

        x = (int) (((xtime - startTs) * MAX_WITH / total) + MARGIN);
        y = (LINE_HEIGHT + LINE_SPACING) * index + MARGIN_TOP;
        with = (int) (watchRecord.getCostMillis() * MAX_WITH / total);
        height = LINE_HEIGHT;
    }

    @Override
    public String getSpanLabel(){
        if (isFirst()){
            return getRootSpanLabel();
        }
        return String.format("%sms | %s | %s",
                watchRecord.getCostMillis(),
                watchRecord.getThreadName(),
                getWatchNameLabel()
        );
    }

    private String getWatchNameLabel(){
        Map<String, Object> properties = watchRecord.getProperties();
        if (Objects.isNull(properties)){
            return watchRecord.getWatchName();
        }
        Object className = properties.get("__className__");
        if (Objects.isNull(className)){
            return watchRecord.getWatchName();
        }else {
            String classSimpleName = getClassSimpleName(className.toString());
            return String.format("%s.%s", classSimpleName, watchRecord.getWatchName());
        }
    }

    private String getClassSimpleName(String className){
        if (Objects.isNull(className) || "null".equalsIgnoreCase(className)){
            return null;
        }

        if (!className.contains(".")){
            return className;
        }
        return className.substring(className.lastIndexOf(".")+1);
    }

    @Override
    public String getRootSpanTimeLabel() {
        if (isFirst()){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            return String.format("%s ~ %s",
                    sdf.format(watchContext.getRootWatchRecord().getStartTime()),
                    sdf.format(watchContext.getRootWatchRecord().getStopTime())
            );
        }
        return "";
    }

    private String getRootSpanLabel() {
        return String.format("%sms | %s | %s%s%s",
                watchRecord.getCostMillis(),
                watchRecord.getThreadName(),
                watchContext.getContextName(),
                getClassAndMethodLabel(),
                getTraceIdLabel()
        );
    }

    private String  getTraceIdLabel(){
        return Objects.isNull(watchContext.getTraceId()) ? "" : String.format(" ( %s )", watchContext.getContextId());
    }

    private String getClassAndMethodLabel(){
        Map<String, Object> properties = watchContext.getRootWatchRecord().getProperties();
        if (Objects.nonNull(properties)){
            Object className = properties.get("__className__");
            Object methodName = properties.get("__methodName__");
            if (Objects.nonNull(className) && Objects.nonNull(className)){
                return String.format(" [%s.%s]", className, methodName);
            }
            if (Objects.nonNull(className)){
                return String.format(" [%s]", className);
            }
            if (Objects.nonNull(methodName)){
                return String.format(" [%s]", methodName);
            }
        }
        return "";
    }
}
