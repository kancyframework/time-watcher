package com.github.kancyframework.timewatcher.event;

import com.github.kancyframework.timewatcher.TimeWatchRecord;
import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.WatchRecord;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TimeWatchStopEvent
 *
 * @author huangchengkang
 * @date 2021/12/25 15:33
 */
public class TimeWatchStoppedEvent extends TimeWatchEvent {

    /**
     * 所有监视记录 （包含根监视记录）
     */
    private List<TimeWatchRecord> allTimeWatchRecords;

    /**
     * 根监视记录
     */
    private TimeWatchRecord rootTimeWatchRecord;

    /**
     * 监视记录集合
     */
    private List<TimeWatchRecord> timeWatchRecords;

    private Throwable throwable;

    public TimeWatchStoppedEvent(Object source, WatchContext watchContext) {
        super(source, watchContext);

        this.rootTimeWatchRecord = toTimeWatchRecord(watchContext.getRootWatchRecord(), watchContext);
        this.rootTimeWatchRecord.setRoot(true);

        this.timeWatchRecords = watchContext.getWatchRecords()
                .stream()
                .map(watchRecord -> toTimeWatchRecord(watchRecord, watchContext))
                .collect(Collectors.toList());

        this.allTimeWatchRecords = new ArrayList<>(this.timeWatchRecords.size()+1);
        this.allTimeWatchRecords.add(rootTimeWatchRecord);
        this.allTimeWatchRecords.addAll(timeWatchRecords);
    }


    private TimeWatchRecord toTimeWatchRecord(WatchRecord watchRecord, WatchContext watchContext){
        TimeWatchRecord timeWatchRecord = new TimeWatchRecord();
        BeanUtils.copyProperties(watchRecord, timeWatchRecord);
        setContextData(timeWatchRecord, watchContext);
        return timeWatchRecord;
    }

    private void setContextData(TimeWatchRecord timeWatchRecord, WatchContext watchContext) {
        timeWatchRecord.setContextId(watchContext.getContextId());
        timeWatchRecord.setTraceId(watchContext.getTraceId());
        timeWatchRecord.setContextName(watchContext.getContextName());
        timeWatchRecord.setRoot(false);
    }

    public List<TimeWatchRecord> getAllTimeWatchRecords() {
        return allTimeWatchRecords;
    }

    public TimeWatchRecord getRootTimeWatchRecord() {
        return rootTimeWatchRecord;
    }

    public List<TimeWatchRecord> getTimeWatchRecords() {
        return timeWatchRecords;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

}
