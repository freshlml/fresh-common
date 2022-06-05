package com.fresh.common.utils;

public class TestForInnerClazz {

    private String str;


    public PubInnerClazz newPubInnerClazz() {
        return new PubInnerClazz();//构造public inner class
    }
    public Class<PubInnerClazz> getPubInnerClazzClass() {
        return PubInnerClazz.class; //public inner class的Class Object
    }

    public class PubInnerClazz {

    }
    public Class<NonPubInnerClazz> getNonPubInnerClazzClass() {
        return NonPubInnerClazz.class;
    }

    //private inner class 只能在此类中使用
    private class NonPubInnerClazz {

    }
    public void useNonPubInnerClazz() {
        NonPubInnerClazz nonPubInnerClazz = new NonPubInnerClazz();
        Class<NonPubInnerClazz> nonPubInnerClazzClass = NonPubInnerClazz.class;
        System.out.println(nonPubInnerClazzClass.getName()); //com.sc.common.utils.TestForInnerClazz$NonPubInnerClazz
        System.out.println(nonPubInnerClazzClass.getDeclaringClass().getName()); //com.sc.common.utils.TestForInnerClazz
    }

    public static class PubStaticInnerClazz {

    }

    public void useNonPubStaticInnerClazz() {
        NonPubStaticInnerClazz nonPubStaticInnerClazz = new NonPubStaticInnerClazz();
        Class<NonPubStaticInnerClazz> nonPubStaticInnerClazzClass = NonPubStaticInnerClazz.class;
        System.out.println(nonPubStaticInnerClazzClass.getName()); //com.sc.common.utils.TestForInnerClazz$NonPubStaticInnerClazz
        System.out.println(nonPubStaticInnerClazzClass.getDeclaringClass().getName()); //com.sc.common.utils.TestForInnerClazz

    }
    static class NonPubStaticInnerClazz {

    }


}
