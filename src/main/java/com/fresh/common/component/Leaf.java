package com.fresh.common.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示叶子节点
 * @see ClazzLeaf
 * @param <T>
 */
public class Leaf<T> extends AbstractComponent<T> {

    public Leaf(T info) {
        super(info);
    }
    public Leaf() {}

    @Override
    public List<Component<T>> getChilds() {
        return new ArrayList<>();
    }

}
