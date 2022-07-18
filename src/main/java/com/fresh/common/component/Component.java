package com.fresh.common.component;

import java.util.List;

/**
 * 表示一个通用的节点
 * @see AbstractComponent
 * @see Leaf
 * @see Composite
 * @param <T> 泛型参数
 */
public interface Component<T> {

    /**
     * 获取当前节点的信息，可能为null
     * @return 当前节点
     */
    T getEntity();

    /**
     * 获取所有子节点,如果没有子节点，返回empty list
     * @return all child
     */
    List<Component<T>> getAllChild();

    /**
     * 增加子节点
     * @param child 节点，不应该为null
     */
    void addChild(Component<T> child);

}
