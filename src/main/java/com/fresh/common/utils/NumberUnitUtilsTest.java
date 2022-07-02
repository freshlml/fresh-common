package com.fresh.common.utils;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public abstract class NumberUnitUtilsTest {

    public static void main(String argv[]) {

        toZs();
        System.out.println("##################");

        toBd();
        System.out.println("##################");

        parseTest();
        System.out.println("##################");

        testUnitNumber();
        System.out.println("#################");

        testDefaultUnitNumber();

    }


    //convertNumberToTargetClazz方法，targetClazz=整数
    private static void toZs() {

        //大 -> 小， 数值超界，报异常
        //Integer l2i = NumberUnitUtils.convertNumberToTargetClazz(123456789999L, Integer.class);
        //大 -> 小，数值未超界，正常
        Integer l2i = NumberUnitUtils.convertNumberToTargetClazz(12345, Integer.class);
        System.out.println(l2i);

        //BigInteger -> 整数, 数值未超界，正常
        Integer bi2i = NumberUnitUtils.convertNumberToTargetClazz(new BigInteger("213312"), Integer.class);
        System.out.println(bi2i);

        //BigDecimal -> 整数，得到精确的整数部分
        Integer bd2i = NumberUnitUtils.convertNumberToTargetClazz(new BigDecimal("123456789.23"), Integer.class);
        System.out.println(bd2i);

        //浮点数 -> 整数 ，非精确的，不常用
        Integer f2i = NumberUnitUtils.convertNumberToTargetClazz(123456789.23f, Integer.class);
        System.out.println(f2i);

    }

    //convertNumberToTargetClazz方法，targetClazz=BigDecimal
    private static void toBd() {

        //无小数
        System.out.println(NumberUnitUtils.convertNumberToTargetClazz(123, BigDecimal.class));

        //浮点数 -> BigDecimal，非精确
        System.out.println(NumberUnitUtils.convertNumberToTargetClazz(123456789.23f, BigDecimal.class));

    }

    private static void parseTest() {

        try {
            NumberUnitUtils.parseTextToTargetNumber("1321321312312312", Integer.class);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

        try {
            NumberUnitUtils.parseTextToTargetNumber("13213213123123121dfsd", BigInteger.class);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

        try {
            NumberUnitUtils.parseTextToTargetNumber("13213213123123121dfsd", Float.class);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

        try {
            Float r = NumberUnitUtils.parseTextToTargetNumber("132132131231231211231234123424323423423423.12312", Float.class);
            System.out.println(r);
        } catch (NumberFormatException e) {
            System.out.println(e);
        }

    }


    private static void testUnitNumber() {

        List<Integer> skips = new ArrayList<>();
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 5L, skips, 17));

        skips = new ArrayList<>();
        skips.add(1);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 11L, skips, 17));


        skips = new ArrayList<>();
        skips.add(1);
        skips.add(2);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 112L, skips, 17));


        skips = new ArrayList<>();
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 1126L, skips, 17));

        skips = new ArrayList<>();
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 10261L, skips, 17));
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 14261L, skips, 17));


        skips = new ArrayList<>();
        skips.add(5);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 142613L, skips, 17));

        skips = new ArrayList<>();
        skips.add(5);
        skips.add(6);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 1426133L, skips, 17));


        skips = new ArrayList<>();
        skips.add(5);
        skips.add(6);
        skips.add(7);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 31426133L, skips, 17));


        skips = new ArrayList<>();
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 431426133L, skips, 17));


        skips = new ArrayList<>();
        skips.add(9);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 2431426133L, skips, 17));


        skips = new ArrayList<>();
        skips.add(9);
        skips.add(10);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 12431426133L, skips, 17));


        skips = new ArrayList<>();
        skips.add(9);
        skips.add(10);
        skips.add(11);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 212431426133L, skips, 17));

        skips = new ArrayList<>();
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 2212431426133L, skips, 12));


        skips = new ArrayList<>();
        skips.add(13);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 12212431426133L, skips, 12));


        skips = new ArrayList<>();
        skips.add(13);
        skips.add(14);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 112212431426133L, skips, 12));


        skips = new ArrayList<>();
        skips.add(13);
        skips.add(14);
        skips.add(15);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 4112212431426133L, skips, 12));


        skips = new ArrayList<>();
        skips.add(13);
        skips.add(14);
        skips.add(15);
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 41112212431426133L, skips, 12));


    }

    private static void testDefaultUnitNumber() {

        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 5L));

        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 11L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 112L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 1126L));

        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 10261L));
        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 14261L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 142613L));

        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 1426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 31426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 2431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 12431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 212431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 2212431426133L));

        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 12212431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 112212431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 4112212431426133L));


        System.out.println(NumberUnitUtils.convertUnitNumber(0, 16, 41112212431426133L));

    }

}
