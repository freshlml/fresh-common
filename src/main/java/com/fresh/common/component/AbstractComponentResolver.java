package com.fresh.common.component;

import com.fresh.common.component.clazz.DefaultClazzComponentResolver;

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
        return component.getAllChild()==null || component.getAllChild().size()==0;
    }

    @Override
    public T getEntity() {
        return component.getEntity();
    }

    @Override
    public List<Component<T>> getAllChild() {
        return component.getAllChild();
    }


    public class EmptyComponent extends AbstractComponent<T> {

        @Override
        public List<Component<T>> getAllChild() {
            return new ArrayList<>();
        }

        @Override
        public void addChild(Component<T> child) {
            //do nothing
        }
    }
}
