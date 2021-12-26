package com.github.kancyframework.timewatcher.span;

import com.github.kancyframework.timewatcher.WatchContext;
import com.github.kancyframework.timewatcher.WatchRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * TimeSpaner
 *
 * @author huangchengkang
 * @date 2021/12/25 21:26
 */
public class TimeSpaner {

    public static List<TimeSpan> getTimeSpans(WatchContext context) {
        List<WatchRecord> watchRecords = context.getWatchRecords();
        List<TimeSpan> timeSpans = new ArrayList<>(watchRecords.size());
        timeSpans.add(new WatchContextTimeSpan(0, context.getRootWatchRecord(), context));
        for (int i = 0; i < watchRecords.size(); i++) {
            WatchRecord watchRecord = watchRecords.get(i);
            timeSpans.add(new WatchContextTimeSpan(i + 1, watchRecord, context));
        }
        return timeSpans;
    }

}
