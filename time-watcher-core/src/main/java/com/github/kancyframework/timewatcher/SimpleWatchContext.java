package com.github.kancyframework.timewatcher;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import org.slf4j.MDC;

import java.util.*;

/**
 * SimpleWatchContext
 *
 * @author huangchengkang
 * @date 2021/12/24 20:18
 */
@Data
public class SimpleWatchContext extends WatchContext{

    /**
     * 观测记录栈
     */
    @JSONField(deserialize = false, serialize = false)
    private Deque<WatchRecord> watchRecordStack;

    /**
     * 重入次数
     */
    private int reentry;
    /**
     * 最大总耗时（毫秒）
     */
    private Long maxTotalCostMillis = -1L;
    /**
     * 每次Watch最大耗时（毫秒）
     */
    private Long maxCostMillis = -1L;
    /**
     * 处理失败不抛出异常
     */
    private Boolean noThrows;

    /**
     * 启动上下文
     * @param contextName
     */
    @Override
    protected void start(String contextName){
        this.setContextName(contextName);
        this.init();
    }

    private void init(){
        this.setContextId(UUID.randomUUID().toString());
        this.setThreadId(Thread.currentThread().getId());
        this.setWatchRecordStack(new ArrayDeque<>());
        this.setWatchRecords(new LinkedList<>());
        this.initTraceId();
        this.initRootWatchRecord();
    }

    private void initTraceId() {
        WatchContext parentWatchContext = getParentContext();
        if (Objects.nonNull(parentWatchContext)){
            this.setTraceId(parentWatchContext.getTraceId());
        }else {
            this.setTraceId(MDC.get("traceId"));
        }
    }

    private void initRootWatchRecord() {
        WatchContext parentWatchContext = getParentContext();
        WatchRecord rootWatchRecord = new WatchRecord();
        rootWatchRecord.setProperties(new HashMap<>());
        rootWatchRecord.setParentWatchName("root");
        rootWatchRecord.setWatchName("root");
        rootWatchRecord.startRecord();
        if (Objects.nonNull(parentWatchContext)){
            rootWatchRecord.setParentWatchName(parentWatchContext.getRootWatchRecord().getParentWatchName());
            rootWatchRecord.setWatchName(parentWatchContext.getRootWatchRecord().getWatchName());
            if (parentWatchContext instanceof SimpleWatchContext){
                SimpleWatchContext simpleWatchContext = (SimpleWatchContext) parentWatchContext;
                Deque<WatchRecord> stack = simpleWatchContext.getWatchRecordStack();
                if (!stack.isEmpty()){
                    WatchRecord watchRecord = stack.peek();
                    rootWatchRecord.setParentWatchName(watchRecord.getParentWatchName());
                    rootWatchRecord.setWatchName(watchRecord.getWatchName());
                }
            }
        }
        this.setRootWatchRecord(rootWatchRecord);
    }

    /**
     * 停止上下文
     */
    @Override
    protected void stop(){
        if (!canStop()){
            return;
        }
        // 开始停止上下文
        this.setStopped(true);
        if (this.isEnabled()){
            WatchRecord rootWatchRecord = this.getRootWatchRecord();
            rootWatchRecord.setStopTime(new Date());
            rootWatchRecord.setCostMillis(rootWatchRecord.getStopTime().getTime() - rootWatchRecord.getStartTime().getTime());

            // 将当前信息，复制到父类上下文
            WatchContext parentWatchContext = this.getParentContext();
            if (Objects.nonNull(parentWatchContext)){
                List<WatchRecord> parentWatchRecords = parentWatchContext.getWatchRecords();
                synchronized (parentWatchRecords){
                    parentWatchRecords.addAll(this.getWatchRecords());
                }
            }

        }
    }

    private boolean canStop(){
        return !isStopped() && getReentry() <= 0;
    }

    /**
     * 复制上下文
     * @return
     */
    @Override
    protected WatchContext copy(){
        SimpleWatchContext newWatchContext = new SimpleWatchContext();
        newWatchContext.setStopped(isStopped());
        newWatchContext.setEnabled(isEnabled());
        newWatchContext.setContextName(getContextName());
        newWatchContext.setParentContext(getParentContext());
        newWatchContext.setReentry(getReentry());
        newWatchContext.setContextId(getContextId());
        newWatchContext.setThreadId(getThreadId());
        newWatchContext.setWatchRecords(getWatchRecords());
        newWatchContext.setRootWatchRecord(getRootWatchRecord());
        newWatchContext.setWatchRecordStack(copyStack(getWatchRecordStack()));
        return newWatchContext;
    }

    private Deque<WatchRecord> copyStack(Deque<WatchRecord> stack){
        Deque<WatchRecord> newWatchRecordStack = new ArrayDeque<>();
        Iterator<WatchRecord> iterator = stack.iterator();
        while (iterator.hasNext()){
            newWatchRecordStack.push(iterator.next());
        }
        return newWatchRecordStack;
    }
}
