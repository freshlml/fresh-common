package com.fresh.common.component.clazz;

import com.fresh.common.component.Composite;

/**
 * Class类型的中间节点
 */
public class ClazzComposite extends Composite<Class<?>> {

    public ClazzComposite(Class<?> info) {
        super(info);
    }

    public ClazzComposite() {}

}
