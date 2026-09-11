package com.nnp.dashboard.utils;

public class LogUtils {
    private LogUtils() {
        /* This utility class should not be instantiated */
    }

    public static String sanitizeForLog(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString().replace("\r", "").replace("\n", "");
    }
}
