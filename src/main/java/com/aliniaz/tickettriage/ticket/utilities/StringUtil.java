package com.aliniaz.tickettriage.ticket.utilities;

import java.util.Locale;

public class StringUtil {
    private StringUtil() {
    }

    public static String normalize(String text) {
        return text == null ? "" : text.toLowerCase(Locale.ROOT);
    }

    public static boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }
}
