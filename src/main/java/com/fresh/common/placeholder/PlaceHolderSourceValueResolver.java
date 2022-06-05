package com.fresh.common.placeholder;

/**
 * PlaceHolder属性值获取器
 */
@FunctionalInterface
public interface PlaceHolderSourceValueResolver {
    String sourceValue(String key);
}
