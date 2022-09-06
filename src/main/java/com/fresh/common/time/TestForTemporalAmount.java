package com.fresh.common.time;

import java.time.Period;
import java.time.temporal.ChronoUnit;

public class TestForTemporalAmount {


    public static void main(String argv[]) {

        test_period();

    }



    public static void test_period() {

        //根据year,month,day构造Period
        Period period1 = Period.of(5000, 115, 100);

        //根据正则字符串创建
        Period period2 = Period.parse("P101111Y-100M10D");

        System.out.println(period2.get(ChronoUnit.DAYS));
        System.out.println(period2.get(ChronoUnit.YEARS));

        Period period3 = Period.of(0, -13, 0).normalized();
        System.out.println(period3.getYears());  //-1
        System.out.println(period3.getMonths()); //-1

        System.out.println("------------test_period-----------");
    }





}
