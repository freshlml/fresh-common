package com.fresh.core.component;

import com.fresh.core.component.clazz.ClazzComposite;
import com.fresh.core.utils.AssertUtils;

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

    @Override
    public void addChild(Component<T> child) {
        AssertUtils.notNull(child, "参数child不能为null");
        this.composites.add(child);
    }

}
