package com.fresh.common.reflect;

import java.lang.reflect.Field;

public class FieldTest<T, E extends Number> {

    private int i;
    private int[] iArray;
    private Obj obj;
    private Obj[] objArray;
    private T t;
    private E e;
    private GenericObj<T> genericObj1;
    private GenericObj<String> genericObj2;
    private T[] tArray;
    private E[] eArray;
    private GenericObj<T>[] genericObj1Array;
    private GenericObj<String>[] genericObj2Array;


    private static void getTypeTest() throws Exception {
        Field iField = FieldTest.class.getDeclaredField("i");
        Class<?> iFieldType = iField.getType();  //Class<int>

        Field iArrayField = FieldTest.class.getDeclaredField("iArray");
        Class<?> iArrayFieldType = iArrayField.getType();    //Class<int[]>

        Field objField = FieldTest.class.getDeclaredField("obj");
        Class<?> objFieldType = objField.getType();      //Class<Obj>

        Field objArrayField = FieldTest.class.getDeclaredField("objArray");
        Class<?> objArrayFieldType = objArrayField.getType();  //Class<Obj[]>

        Field tField = FieldTest.class.getDeclaredField("t");
        Class<?> tFieldType = tField.getType();       //Class<Object>

        Field eField = FieldTest.class.getDeclaredField("e");
        Class<?> eFieldType = eField.getType();       //Class<Number>

        Field genericObj1Field = FieldTest.class.getDeclaredField("genericObj1");
        Class<?> genericObj1FieldType = genericObj1Field.getType();  //Class<GenericObj>

        Field genericObj2Field = FieldTest.class.getDeclaredField("genericObj2");
        Class<?> genericObj2FieldType = genericObj2Field.getType();  //Class<GenericObj>

        Field tArrayField = FieldTest.class.getDeclaredField("tArray");
        Class<?> tArrayFieldType = tArrayField.getType();      //Class<Object[]>

        Field eArrayField = FieldTest.class.getDeclaredField("eArray");
        Class<?> eArrayFieldType = eArrayField.getType();          //Class<Number[]>

        Field genericObj1ArrayField = FieldTest.class.getDeclaredField("genericObj1Array");
        Class<?> genericObj1ArrayFieldType = genericObj1ArrayField.getType();  //Class<GenericObj[]>

        Field genericObj2ArrayField = FieldTest.class.getDeclaredField("genericObj2Array");
        Class<?> genericObj2ArrayFieldType = genericObj2ArrayField.getType();    //Class<GenericObj[]>

        System.out.println("  ");
    }


    public static void main(String argv[]) throws Exception {
        getTypeTest();


    }


    private static class Obj {}
    private static class GenericObj<T> {}

}
