package com.fresh.common.component.clazz;

import com.fresh.common.component.ComponentResolver;

import java.util.List;

/**
 * Class类型的Component解析器
 * @see DefaultClazzComponentResolver
 */
public interface ClazzComponentResolver extends ComponentResolver<Class<?>> {

    /**
     * 获取clazz的所有super class
     * @return 所有super class
     */
    List<Class<?>> getAllSuperClass();

    /**
     * 获取clazz的所有interfaces
     * @return 所有interfaces
     */
    List<Class<?>> getAllInterfaces();

}
