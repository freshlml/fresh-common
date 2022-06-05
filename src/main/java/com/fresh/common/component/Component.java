package com.fresh.common.component;

import java.util.List;

/**
 * 表示一个通用的节点
 * @see AbstractComponent
 * @see Leaf
 * @see Composite
 * @param <T>
 */
public interface Component<T> {

    /**
     * 获取当前的节点信息
     * @return
     */
    T getInfo();

    /**
     * 获取子节点
     * @return
     */
    List<Component<T>> getChilds();

}
