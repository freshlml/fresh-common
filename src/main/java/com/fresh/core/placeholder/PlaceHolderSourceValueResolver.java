package com.fresh.core.placeholder;

/**
 * PlaceHolder 属性值获取器
 */
@FunctionalInterface
public interface PlaceHolderSourceValueResolver {
    String sourceValue(String key);
}
