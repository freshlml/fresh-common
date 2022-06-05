package com.fresh.common.component;

import java.util.List;

/**
 * @see Leaf
 * @see Composite
 * @param <T>
 */
public abstract class AbstractComponent<T> implements Component<T> {

    private T info;

    public AbstractComponent(T info) {
        //AssertUtils.ifNull(info, () -> "参数info不能为null", null);
        this.info = info;
    }
    public AbstractComponent() {}

    @Override
    public T getInfo() {
        return info;
    }

    @Override
    public List<Component<T>> getChilds() {
        return null;
    }

}
