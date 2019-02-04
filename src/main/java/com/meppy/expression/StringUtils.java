package com.meppy.expression;

import java.nio.CharBuffer;

final class StringUtils {
    private StringUtils() {
    }

    static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    static String newString(char c, int count) {
        return CharBuffer.allocate(count).toString().replace('\0', c);
    }

    static boolean equalsIgnoreCase(String a, String b) {
         if (a == null) {
             return b == null;
         }

         return a.compareToIgnoreCase(b) == 0;
    }
}