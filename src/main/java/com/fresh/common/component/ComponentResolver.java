package com.fresh.common.component;

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
     * 获取当前的节点信息
     * @return T
     */
    T getInfo();

    /**
     * 获取子节点
     * @return childs
     */
    List<Component<T>> getChilds();

}
