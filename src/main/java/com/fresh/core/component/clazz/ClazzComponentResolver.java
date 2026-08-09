package com.fresh.core.component.clazz;

import com.fresh.core.component.ComponentResolver;

import java.util.List;

/**
 * Class 类型的 Component 解析器
 * @see DefaultClazzComponentResolver
 */
public interface ClazzComponentResolver extends ComponentResolver<Class<?>> {

    /**
     * 获取 clazz 的所有 super class
     * @return 所有 super class
     */
    List<Class<?>> getAllSuperClass();

    /**
     * 获取 clazz 的所有 interfaces
     * @return 所有 interfaces
     */
    List<Class<?>> getAllInterfaces();

}
