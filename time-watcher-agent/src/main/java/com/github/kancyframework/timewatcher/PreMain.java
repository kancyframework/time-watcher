package com.github.kancyframework.timewatcher;

import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;
import java.util.Collections;

/**
 * TimeWatcherPreMain
 *
 * @author huangchengkang
 * @date 2021/12/30 14:40
 */
@Slf4j
public class PreMain {

    /**
     * agentArgs 是 premain 函数得到的程序参数，通过 -javaagent 传入。这个参数是个字符串，如果程序参数有多个，需要程序自行解析这个字符串。
     * inst 是一个 java.lang.instrument.Instrumentation 的实例，由 JVM 自动传入。
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("=========================== timewatcher javaagent入口 ===========================");
        premain(agentArgs);
        inst.addTransformer(new Transformer(Collections.singletonList(agentArgs)));
        log.info("=========================== timewatcher javaagent 入口执行完毕 ===========================");
    }

    /**
     * 带有 Instrumentation 参数的 premain 优先级高于不带此参数的 premain。
     * 如果存在带 Instrumentation 参数的 premain，不带此参数的 premain 将被忽略。
     */
    public static void premain(String agentArgs) {
        log.info("timewatcher java agent 参数为: {}" , agentArgs);
    }

}
