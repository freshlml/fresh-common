package com.fresh.common.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @see DefaultClazzComponentResolver
 * @param <T> 泛型参数
 */
public abstract class AbstractComponentResolver<T> implements ComponentResolver<T> {

    protected Component<T> component;

    public AbstractComponentResolver(Component<T> component) {
        this.component = Optional.ofNullable(component).orElse(new EmptyComponent());
    }

    @Override
    public boolean isLeaf() {
        return component.getChilds()==null || component.getChilds().size()==0;
    }

    @Override
    public T getInfo() {
        return component.getInfo();
    }

    @Override
    public List<Component<T>> getChilds() {
        return component.getChilds();
    }


    public class EmptyComponent extends AbstractComponent<T> {

        @Override
        public List<Component<T>> getChilds() {
            return new ArrayList<>();
        }
    }
}
