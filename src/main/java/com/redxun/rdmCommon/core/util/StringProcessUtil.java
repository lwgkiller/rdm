package com.redxun.rdmCommon.core.util;

public class StringProcessUtil {
    public static String toGetStringNotNull(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
}
