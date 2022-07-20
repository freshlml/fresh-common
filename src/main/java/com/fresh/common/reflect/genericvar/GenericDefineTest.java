package com.fresh.common.reflect.genericvar;

import java.util.List;
import java.util.Map;

public class GenericDefineTest {

    //泛型变量定义
    class Bean1<T, R> {
        String s;
        T r;
        R[] rs;
        Map<? extends T[], List<? extends R>> mp;
    }

}
