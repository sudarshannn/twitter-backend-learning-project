package com.example.demo;


import java.util.HashMap;
import java.util.Map;

public class Months {
    private static Map<String,String> months = new HashMap<>();

    static  {
        months.put("Jan","01");
        months.put("Feb","02");
        months.put("Mar","03");
        months.put("Apr","04");
        months.put("May","05");
        months.put("Jun","06");
        months.put("Jul","07");
        months.put("Aug","08");
        months.put("Sep","09");
        months.put("Oct","10");
        months.put("Nov","11");
        months.put("Dec","12");
    }

    public static String getMonthNumber(String s) {
        return months.get(s);
    }
}
