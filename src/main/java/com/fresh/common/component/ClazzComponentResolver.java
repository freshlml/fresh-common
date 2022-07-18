package com.fresh.common.component;

import java.util.List;

/**
 * Class类型的Component解析器
 * @see DefaultClazzComponentResolver
 */
public interface ClazzComponentResolver extends ComponentResolver<Class<?>> {

    /**
     * 从Component中获取非interface
     * 因为此Component是从继承和实现中获取的(通过getSuperClass,getInterfaces)，所以此Component中
     * 只存在declared class, interface类型的Class object
     * @return super
     */
    List<Class<?>> getAllSuperClass();

    /**
     * 从Component中获取interface
     * 因为此Component是从继承和实现中获取的(通过getSuperClass,getInterfaces)，所以此Component中
     * 只存在declared class, interface类型的Class object
     * @return interfaces
     */
    List<Class<?>> getAllInterfaces();

}
