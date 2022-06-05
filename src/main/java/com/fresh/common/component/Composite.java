package com.fresh.common.component;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示中间节点
 * @see ClazzComposite
 * @param <T>
 */
public class Composite<T> extends AbstractComponent<T> {

    private final List<Component<T>> clazzParents = new ArrayList<>();

    public Composite(T info) {
        super(info);
    }
    public Composite() {}

    @Override
    public List<Component<T>> getChilds() {
        return clazzParents;
    }

    public void addClazzParent(Component clazz) {
        if(clazz != null) clazzParents.add(clazz);
    }

}
