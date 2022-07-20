package com.fresh.common.reflect.genericvar;

import java.util.List;

public class GenericClaimTest {

    //泛型变量声明
    class Bean1<T> {}

    class Bean2<T extends Number> {}

    class Bean3<T, R extends T> {}

    class Bean4<T, R extends List<T>> {}

    class Bean5<R extends List<String>> {}

    class Bean6<R extends List<String[]>> {}

    class Bean7<T, R extends List<T[]>> {}

    class Bean8<T, R extends List<List<T>>> {}

    class Bean9<R extends List<?>> {}

}
