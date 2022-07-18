package com.fresh.common.component;

import com.fresh.common.component.clazz.ClazzComponentResolver;

import java.util.List;

/**
 * Component解析器
 * @see Component
 * @see AbstractComponentResolver
 * @see ClazzComponentResolver
 * @param <T> Component的泛型类型
 */
public interface ComponentResolver<T> {

    /**
     * 是否是叶子节点
     * @return true or false
     */
    boolean isLeaf();

    /**
     * 获取当前的节点的信息
     * @return T
     */
    T getEntity();

    /**
     * 获取所有子节点
     * @return 所有子节点
     */
    List<Component<T>> getAllChild();

}
