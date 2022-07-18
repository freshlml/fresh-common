package com.fresh.common.component;

import com.fresh.common.component.clazz.ClazzLeaf;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示叶子节点
 * @see ClazzLeaf
 * @param <T> 泛型参数
 */
public class Leaf<T> extends AbstractComponent<T> {

    public Leaf(T entity) {
        super(entity);
    }
    public Leaf() {}

    @Override
    public List<Component<T>> getAllChild() {
        return new ArrayList<>();
    }

    @Override
    public void addChild(Component<T> child) {
        //do nothing
    }
}
