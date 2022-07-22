package com.fresh.common.reflect;

import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;


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

    public static void main(String argv[]) throws Exception {
        getTypeTest();

        getTest();

        setTest();
    }

    private static class Obj {}
    private static class GenericObj<T> {}

    private static void getTypeTest() throws Exception {
        Field iField = FieldTest.class.getDeclaredField("i");
        Class<?> iFieldType = iField.getType();  //Class<int>
        Type iFieldGenericType = iField.getGenericType(); //Class<int>

        Field iArrayField = FieldTest.class.getDeclaredField("iArray");
        Class<?> iArrayFieldType = iArrayField.getType();    //Class<int[]>
        Type iArrayFieldGenericType = iArrayField.getGenericType(); //Class<int[]>

        Field objField = FieldTest.class.getDeclaredField("obj");
        Class<?> objFieldType = objField.getType();      //Class<Obj>
        Type objFieldGenericType = objField.getGenericType(); //Class<Obj>

        Field objArrayField = FieldTest.class.getDeclaredField("objArray");
        Class<?> objArrayFieldType = objArrayField.getType();  //Class<Obj[]>
        Type objArrayFieldGenericType = objArrayField.getGenericType(); //Class<Obj[]>

        Field tField = FieldTest.class.getDeclaredField("t");
        Class<?> tFieldType = tField.getType();       //Class<Object>
        Type tFieldGenericType = tField.getGenericType(); //TypeVariable

        Field eField = FieldTest.class.getDeclaredField("e");
        Class<?> eFieldType = eField.getType();       //Class<Number>
        Type eFieldGenericType = eField.getGenericType(); //TypeVariable

        Field genericObj1Field = FieldTest.class.getDeclaredField("genericObj1");
        Class<?> genericObj1FieldType = genericObj1Field.getType();  //Class<GenericObj>
        Type genericObj1FieldGenericType = genericObj1Field.getGenericType(); //ParameterizedType

        Field genericObj2Field = FieldTest.class.getDeclaredField("genericObj2");
        Class<?> genericObj2FieldType = genericObj2Field.getType();  //Class<GenericObj>
        Type genericObj2FieldGenericTye = genericObj2Field.getGenericType(); //ParameterizedType

        Field tArrayField = FieldTest.class.getDeclaredField("tArray");
        Class<?> tArrayFieldType = tArrayField.getType();      //Class<Object[]>
        Type tArrayFieldGenericType = tArrayField.getGenericType(); //GenericArrayType

        Field eArrayField = FieldTest.class.getDeclaredField("eArray");
        Class<?> eArrayFieldType = eArrayField.getType();          //Class<Number[]>
        Type eArrayFieldGenericType = eArrayField.getGenericType(); //GenericArrayType

        Field genericObj1ArrayField = FieldTest.class.getDeclaredField("genericObj1Array");
        Class<?> genericObj1ArrayFieldType = genericObj1ArrayField.getType();  //Class<GenericObj[]>
        Type genericObj1ArrayFieldGenericType = genericObj1ArrayField.getGenericType(); //GenericArrayType

        Field genericObj2ArrayField = FieldTest.class.getDeclaredField("genericObj2Array");
        Class<?> genericObj2ArrayFieldType = genericObj2ArrayField.getType();    //Class<GenericObj[]>
        Type genericObj2ArrayFieldGenericType = genericObj2ArrayField.getGenericType(); //GenericArrayType

        System.out.println("----------getTypeTest--------\n");
    }



    private static class Field_Super {
        public String name = "super";

        private String private_name = "private_name";
    }
    private static class GetFieldTest extends Field_Super {

    }
    private static void getTest() throws NoSuchFieldException {

        //找到Field_Super的Field
        Field super_private_field = Field_Super.class.getDeclaredField("private_name");
        try {
            //在GetFieldTest的实例对象上获取此Field值
            super_private_field.setAccessible(true);
            System.out.println(super_private_field.get(new GetFieldTest()));

            //在Field_Super的实例对象上获取此Field值
            //super_private_field.setAccessible(true);
            //System.out.println(super_private_field.get(new Field_Super()));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        System.out.println("--------getTest---------");

    }

    private static class SetFieldTest {
        public static final String s_f = "s_f";

    }
    private static void setTest() throws NoSuchFieldException {

        //static final field
        Field s_f_field = SetFieldTest.class.getDeclaredField("s_f");
        try {
            s_f_field.setAccessible(true);

            s_f_field.set(new SetFieldTest(), "new_s_f");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


        System.out.println("---------setTest---------");
    }


}
