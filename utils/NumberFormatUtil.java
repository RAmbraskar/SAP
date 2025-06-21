package com.ea.utils;

public class NumberFormatUtil {

    private NumberFormatUtil() {
    }

    public static String calculateRatio(String num) {
        double f = Double.parseDouble(num);
        double test = f - 100.0;
        return "%s00".formatted(String.valueOf(test));
    }

    public static String decimalFormat3(String num) {
        return String.format("%.3f",Double.parseDouble(num));
    }
}
