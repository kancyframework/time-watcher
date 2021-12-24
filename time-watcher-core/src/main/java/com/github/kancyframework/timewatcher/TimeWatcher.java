package com.github.kancyframework.timewatcher;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Watcher
 *
 * @author huangchengkang
 * @date 2021/12/24 20:13
 */
@Slf4j
public abstract class TimeWatcher {

    /**
     * 线程本地 - 统计相关
     */
    private static final ThreadLocal<WatchContext> watchContextThreadLocal = new ThreadLocal<WatchContext>(){
        @Override
        protected WatchContext initialValue() {
            return new SimpleWatchContext();
        }
    };

    private static void setEnabled(boolean enabled){
        getWatchContext().setEnabled(enabled);
    }

    public static void enabled(){
        setEnabled(true);
    }

    public static void disEnabled(){
        setEnabled(false);
    }

    public static boolean isEnabled(){
        return getWatchContext().isEnabled();
    }

    private static SimpleWatchContext getSimpleWatchContext(){
        return (SimpleWatchContext) watchContextThreadLocal.get();
    }

    public static WatchContext getWatchContext(){
        return watchContextThreadLocal.get();
    }

    private static void setWatchContext(WatchContext watchContext){
        watchContextThreadLocal.set(watchContext);
    }

    private static void clearWatchContext(){
        watchContextThreadLocal.remove();
    }

    public static void start(String contextName){
        startWatch(contextName);
    }

    public static void startWatch(String contextName){
        SimpleWatchContext watchContext = getSimpleWatchContext();
        if (Objects.isNull(watchContext.getContextId())){
            watchContext.start(contextName);
        }
    }

    public static void stop(){
        stopWatch();
    }

    public static void stopWatch(){
        SimpleWatchContext watchContext = getSimpleWatchContext();
        if (Objects.nonNull(watchContext)){
            watchContext.stop();
        }
    }

    public static void close(){
        closeWatch();
    }

    /**
     * 关闭
     */
    public static void closeWatch(){
        SimpleWatchContext watchContext = getSimpleWatchContext();
        if (watchContext.getReentry() <= 0){
            stopWatch();
            clearWatchContext();
        } else {
            watchContext.setReentry(watchContext.getReentry()-1);
        }
    }

    public static WatchContext copyOfWatchContext(){
        return snapshotWatchContext();
    }

    public static WatchContext snapshotWatchContext(){
        SimpleWatchContext currentWatchContext = getSimpleWatchContext();
        if (currentWatchContext.isEnabled()){
            return currentWatchContext.copy();
        }
        return currentWatchContext;
    }

    /**
     * 初始化子线程监视上下文
     *
     * @param parentWatchContext 父监视上下文
     */
    public static void transferWatchContext(WatchContext parentWatchContext){
        if (Objects.isNull(parentWatchContext)){
            return;
        }

        // 当前线程
        if (Objects.equals(parentWatchContext.getThreadId(), Thread.currentThread().getId())){
            SimpleWatchContext watchContext = getSimpleWatchContext();
            watchContext.setReentry(watchContext.getReentry()+1);
            setWatchContext(watchContext);
            return;
        }

        // 子线程
        SimpleWatchContext watchContext = new SimpleWatchContext();
        if (parentWatchContext.isEnabled()) {
            watchContext.setEnabled(true);
            watchContext.setParentContext(parentWatchContext);
            watchContext.start(String.format("%s:%s",
                    parentWatchContext.getContextName(), Thread.currentThread().getName()));
        }
        setWatchContext(watchContext);
    }

    public static <R> R watch(ProducerFunction<R> supplier){
        return watch("", supplier, new HashMap<>());
    }

    public static <R> R watch(String watchName, ProducerFunction<R> supplier){
        return watch(watchName, supplier, new HashMap<>());
    }

    public static <R> R watch(String watchName, ProducerFunction<R> supplier,
                              Map<String, Object> properties){
        SimpleWatchContext watchContext = getSimpleWatchContext();
        if (!watchContext.isEnabled()){
            return supplier.get();
        }

        if (Objects.isNull(watchName) || watchName.isEmpty()){
            watchName = supplier.getWatchMethodName();
        }

        R result = null;
        try {
            preWatch(watchName, properties);
            result = supplier.get();
        } finally {
            postWatch(watchName, properties);
        }
        return result;
    }

    public static void watch(ConsumerFunction<WatchContext> consumer){
        watch("", consumer, new HashMap<>());
    }

    public static void watch(String watchName, ConsumerFunction<WatchContext> consumer){
        watch(watchName, consumer, new HashMap<>());
    }

    public static void watch(String watchName, ConsumerFunction<WatchContext> consumer,
                              Map<String, Object> properties){
        watch(watchName,() -> {
            consumer.accept(getSimpleWatchContext());
            return 0;
        }, properties);
    }

    public static void watch(Function function){
        watch("", function, new HashMap<>());
    }

    public static void watch(String watchName,Function function){
        watch(watchName, function, new HashMap<>());
    }

    public static void watch(String watchName, Function function,
                             Map<String, Object> properties){
        watch(watchName,() -> {
            function.execute();
            return 0;
        }, properties);
    }

    private static void preWatch(String watchName, Map<String, Object> properties) {
        try {
            WatchRecord watchRecord = new WatchRecord();
            watchRecord.setWatchName(watchName);
            watchRecord.setProperties(properties);
            watchRecord.startRecord();

            SimpleWatchContext watchContext = getSimpleWatchContext();
            Deque<WatchRecord> watchRecordStack = watchContext.getWatchRecordStack();
            List<WatchRecord> watchRecords = watchContext.getWatchRecords();
            watchRecords.add(watchRecord);
            watchRecordStack.push(watchRecord);
        } catch (Exception e) {
            log.error("preWatch fail: watchName={} , properties={}\n{}", watchName, properties, e);
        }

    }

    private static void postWatch(String watchName, Map<String, Object> properties) {
        try {
            SimpleWatchContext watchContext = getSimpleWatchContext();
            Deque<WatchRecord> watchRecordStack = watchContext.getWatchRecordStack();

            // 出栈并且结束时间记录
            WatchRecord currentWatchRecord = watchRecordStack.pop();
            currentWatchRecord.stopRecord();
            // 取出父节点，设置父节点名称
            String parentWatchName = watchContext.getRootWatchRecord().getWatchName();
            currentWatchRecord.setParentWatchName(parentWatchName);
        } catch (Exception e) {
            log.error("postWatch fail: watchName={} , properties={}\n{}", watchName, properties, e);
        }
    }

    /**
     * Function
     *
     * @author huangchengkang
     * @date 2021/12/24 20:49
     */
    @FunctionalInterface
    public interface Function extends SerializableFunction {
        /**
         * 执行函数
         */
        void execute();
    }

    /**
     * SerializableWatchSupplier
     *
     * @author huangchengkang
     * @date 2021/12/24 20:49
     */
    @FunctionalInterface
    public interface ConsumerFunction<T> extends SerializableFunction {
        /**
         * Performs this operation on the given argument.
         *
         * @param t the input argument
         */
        void accept(T t);
    }


    /**
     * ProducerFunction
     *
     * @author huangchengkang
     * @date 2021/12/24 20:49
     */
    @FunctionalInterface
    public interface ProducerFunction<R> extends SerializableFunction {
        /**
         * Gets a result.
         *
         * @return a result
         */
        R get();
    }


    /**
     * SerializableFunction
     *
     * @author huangchengkang
     * @date 2021/12/24 20:46
     */
    public interface SerializableFunction extends Serializable {

        default SerializedLambda getSerializedLambda() throws Exception {
            Method writeReplaceMethod = getClass().getDeclaredMethod("writeReplace");
            if (Objects.nonNull(writeReplaceMethod)){
                writeReplaceMethod.setAccessible(true);
                Object serializedLambda = writeReplaceMethod.invoke(this);
                if (serializedLambda instanceof SerializedLambda){
                    return SerializedLambda.class.cast(serializedLambda);
                }
            }
            return null;
        }

        default String getWatchClassName() {
            try {
                SerializedLambda serializedLambda = getSerializedLambda();
                if (Objects.nonNull(serializedLambda)){
                    return serializedLambda.getImplClass();
                }
            } catch (Exception e) {
                // ignore
            }
            return null;
        }

        default String getWatchMethodName() {
            try {
                SerializedLambda serializedLambda = getSerializedLambda();
                if (Objects.nonNull(serializedLambda)){
                    String watchMethodName = serializedLambda.getImplMethodName();
                    if (Objects.nonNull(watchMethodName) && watchMethodName.startsWith("lambda$")){
                        watchMethodName = watchMethodName.split("[$]")[1];
                    }
                    return watchMethodName;
                }
            } catch (Exception e) {
                // ignore
            }
            return null;
        }
    }


}
