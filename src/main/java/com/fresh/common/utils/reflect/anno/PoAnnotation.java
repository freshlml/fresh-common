package com.fresh.common.utils.reflect.anno;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.PARAMETER})
@Repeatable(PosAnnotation.class)
public @interface PoAnnotation {
    String name();
}
