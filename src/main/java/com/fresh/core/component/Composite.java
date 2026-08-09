package com.fresh.core.component;

import com.fresh.core.component.clazz.ClazzComposite;
import com.fresh.core.utils.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示中间节点
 * @see ClazzComposite
 * @param <T> 泛型参数
 */
public class Composite<T> extends AbstractComponent<T> {

    private final List<Component<T>> composites = new ArrayList<>();

    public Composite(T entity) {
        super(entity);
    }
    public Composite() {}

    @Override
    public List<Component<T>> getAllChild() {
        return composites;
    }

    /**
     * @param child 节点，不应该为null
     * @throws NullPointerException if child is null
     */
    @Override
    public void addChild(Component<T> child) {
        Assert.notNull(child, "参数 child 不能为空");
        this.composites.add(child);
    }

}
