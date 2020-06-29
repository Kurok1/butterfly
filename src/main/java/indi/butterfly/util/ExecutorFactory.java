package indi.butterfly.util;

import indi.butterfly.autoconfigure.ButterflyProperties;
import indi.butterfly.executor.IExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行器管理工厂,可以根据执行器id获取实例 {@link ExecutorFactory#getExecutor(String)}
 * 也可以根据执行器id获取执行器定义信息
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.09
 * @since 1.0.0
 * @see IExecutor
 * @see IExecutor#getExecutorId()
 */
public class ExecutorFactory {

    private static final Map<String, IExecutor> executorMap = new ConcurrentHashMap<>();

    private static final Map<String, ButterflyProperties.ExecutorDefinition> executorDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 清空所有实例映射
     */
    public static void clear() {
        executorMap.clear();
        executorDefinitionMap.clear();
    }

    /**
     * 注册一个实例映射,前提是该executorId未注册
     * @param executorId 执行器id
     * @param executor 执行器实例
     */
    public static void addExecutor(String executorId, IExecutor executor) {
        executorMap.putIfAbsent(executorId, executor);
    }

    /**
     * 注册一个执行器配置映射,前提是该executorId未注册
     * @param executorId 执行器id
     * @param definition 执行器配置
     */
    public static void addDefinition(String executorId, ButterflyProperties.ExecutorDefinition definition) {
        executorDefinitionMap.putIfAbsent(executorId, definition);
    }

    /**
     * 获取一个执行器实例
     * @param executorId 执行器id
     * @return 执行器id对于的实例
     */
    public static IExecutor getExecutor(String executorId) {
        return executorMap.get(executorId);
    }

    /**
     * 获取一个执行器配置
     * @param executorId 执行器id
     * @return 执行器id对于的配置
     */
    public static ButterflyProperties.ExecutorDefinition getDefinition(String executorId) { return executorDefinitionMap.get(executorId); }
}
