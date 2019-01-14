package com.meppy.expressions;

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
}
