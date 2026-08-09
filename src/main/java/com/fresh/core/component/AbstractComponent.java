package com.fresh.core.component;

/**
 * @see Leaf
 * @see Composite
 * @param <T> 泛型参数
 */
public abstract class AbstractComponent<T> implements Component<T> {

    private T entity;

    public AbstractComponent(T entity) {
        this.entity = entity;
    }
    public AbstractComponent() {}

    @Override
    public T getEntity() {
        return entity;
    }

}
