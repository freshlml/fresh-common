package com.fresh.common.placeholder;

import com.fresh.common.utils.AssertUtils;

/**
 * PlaceHolder解析器，对给定的String,解析替换其中的placeholder
 * eg: {@link SimplePlaceHolderResolverTest}
 */
public class SimplePlaceHolderResolver {

    private static final String DEFAULT_PREFIX = "${";
    private static final String DEFAULT_SUFFIX = "}";
    private static final String DEFAULT_VALUE_SEPARATOR = ":";

    /**
     * PlaceHolder 前缀
     */
    private final String prefix;
    /**
     * PlaceHolder 后缀
     */
    private final String suffix;
    /**
     * PlaceHolder 分隔符
     */
    private final String valueSeparator;
    /**
     * PlaceHolder不能解析时，是否忽略
     * default true, will原样返回
     * if false: were throw exception
     */
    private final Boolean ignoreUnresolvablePlaceHolder;

    /**
     * 默认构造SimplePlaceHolderResolver
     *with prefix="${" ; suffix="}" ; valueSeparator=":" ; ignoreUnresolvablePlaceHolder=true
     */
    public SimplePlaceHolderResolver() {
        this(DEFAULT_PREFIX, DEFAULT_SUFFIX, DEFAULT_VALUE_SEPARATOR, true);
    }

    /**
     *
     * @param prefix  前缀，如果为null，将使用DEFAULT_PREFIX
     * @param suffix   后缀，如果为null，将使用DEFAULT_SUFFIX
     * @param valueSeparator PlaceHolder 分隔符，如果为null，将使用DEFAULT_VALUE_SEPARATOR
     * @param ignoreUnresolvablePlaceHolder  PlaceHolder不能解析时，是否忽略，默认true
     */
    public SimplePlaceHolderResolver(String prefix, String suffix, String valueSeparator,
                                     Boolean ignoreUnresolvablePlaceHolder) {
        if(prefix != null) {
            this.prefix = prefix;
        } else {
            this.prefix = DEFAULT_PREFIX;
        }
        if(suffix != null) {
            this.suffix = suffix;
        } else {
            this.suffix = DEFAULT_SUFFIX;
        }
        if(valueSeparator != null) {
            this.valueSeparator = valueSeparator;
        } else {
            this.valueSeparator = DEFAULT_VALUE_SEPARATOR;
        }
        if(ignoreUnresolvablePlaceHolder != null) {
            this.ignoreUnresolvablePlaceHolder = ignoreUnresolvablePlaceHolder;
        } else {
            this.ignoreUnresolvablePlaceHolder = Boolean.TRUE;
        }
    }

    public String resolve(String value, PlaceHolderSourceValueResolver placeHolderSourceValueResolver) {
        AssertUtils.ifTrue(value == null, () -> "参数value不能为空", null);
        AssertUtils.ifTrue(placeHolderSourceValueResolver == null, () -> "参数placeHolderSourceValueResolver不能为空", null);
        int findPrefix = value.indexOf(prefix);

        if(findPrefix == -1) return value;
        StringBuilder result = new StringBuilder(value);

        while(findPrefix != -1) {
            int findRelativeSuffix = findRelativeSuffix(result, findPrefix);
            if(findRelativeSuffix != -1) {
                String placeHolder = result.substring(findPrefix + prefix.length(), findRelativeSuffix);

                String nestedResolvedPlaceHolder = resolve(placeHolder, placeHolderSourceValueResolver);

                int valueSeparatorIndex = nestedResolvedPlaceHolder.indexOf(valueSeparator);
                String placeHolderValue = null;
                if(valueSeparatorIndex == -1) {
                   placeHolderValue = placeHolderSourceValueResolver.sourceValue(nestedResolvedPlaceHolder);
                } else {
                    String nPlaceHolder = nestedResolvedPlaceHolder.substring(0, valueSeparatorIndex);
                    String defaultValue = nestedResolvedPlaceHolder.substring(valueSeparatorIndex + valueSeparator.length());
                    String nValue = placeHolderSourceValueResolver.sourceValue(nPlaceHolder);
                    if(nValue != null) {
                        placeHolderValue = nValue;
                    } else if(defaultValue==null || "".equals(defaultValue.trim()) || "null".equalsIgnoreCase(defaultValue.trim())) {
                        placeHolderValue = null;
                    } else {
                        placeHolderValue = defaultValue;
                    }
                }
                if(placeHolderValue != null) {
                    String mValue = resolve(placeHolderValue, placeHolderSourceValueResolver);
                    result.replace(findPrefix, findRelativeSuffix+suffix.length(), mValue);
                    findPrefix = result.indexOf(prefix, findPrefix + mValue.length());
                } else if(!ignoreUnresolvablePlaceHolder){
                    AssertUtils.ifTrue(true, () -> "参数value=["+result+"],存在不能解析的placeHolder:["+nestedResolvedPlaceHolder+"]", null);
                } else {
                    findPrefix = result.indexOf(prefix, findRelativeSuffix + suffix.length());
                }
            } else {
                break;
            }
        }

        return result.toString();
    }

    protected int findRelativeSuffix(StringBuilder value, int prefixStart) {
        int findPos = prefixStart + prefix.length();
        int predit = 1;
        while(predit != 0 && findPos<value.length()) {
            int nextPrefixPos = value.indexOf(prefix, findPos);
            int nextSuffixPos = value.indexOf(suffix, findPos);
            if(nextSuffixPos == -1) {
                break;
            } else if(nextPrefixPos == -1){
                predit--;
                findPos = nextSuffixPos + suffix.length();
            } else if(nextPrefixPos > nextSuffixPos) {
                predit--;
                findPos = nextSuffixPos + suffix.length();
            } else {
                predit++;
                findPos = nextPrefixPos + prefix.length();
            }
        }
        if(predit != 0) return -1;
        return findPos - suffix.length();
    }

}
