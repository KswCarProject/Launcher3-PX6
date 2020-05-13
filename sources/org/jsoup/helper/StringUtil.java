package org.jsoup.helper;

import java.util.Collection;
import java.util.Iterator;
import org.apache.http.message.TokenParser;

public final class StringUtil {
    private static final String[] padding = {"", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        ", "         ", "          "};

    public static String join(Collection strings, String sep) {
        return join(strings.iterator(), sep);
    }

    public static String join(Iterator strings, String sep) {
        if (!strings.hasNext()) {
            return "";
        }
        String start = strings.next().toString();
        if (!strings.hasNext()) {
            return start;
        }
        StringBuilder sb = new StringBuilder(64).append(start);
        while (strings.hasNext()) {
            sb.append(sep);
            sb.append(strings.next());
        }
        return sb.toString();
    }

    public static String padding(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("width must be > 0");
        } else if (width < padding.length) {
            return padding[width];
        } else {
            char[] out = new char[width];
            for (int i = 0; i < width; i++) {
                out[i] = TokenParser.SP;
            }
            return String.valueOf(out);
        }
    }

    public static boolean isBlank(String string) {
        if (string == null || string.length() == 0) {
            return true;
        }
        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!isWhitespace(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String string) {
        if (string == null || string.length() == 0) {
            return false;
        }
        int l = string.length();
        for (int i = 0; i < l; i++) {
            if (!Character.isDigit(string.codePointAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWhitespace(int c) {
        return c == 32 || c == 9 || c == 10 || c == 12 || c == 13;
    }

    public static String normaliseWhitespace(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        appendNormalisedWhitespace(sb, string, false);
        return sb.toString();
    }

    public static void appendNormalisedWhitespace(StringBuilder accum, String string, boolean stripLeading) {
        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        int len = string.length();
        int i = 0;
        while (i < len) {
            int c = string.codePointAt(i);
            if (!isWhitespace(c)) {
                accum.appendCodePoint(c);
                lastWasWhite = false;
                reachedNonWhite = true;
            } else if ((!stripLeading || reachedNonWhite) && !lastWasWhite) {
                accum.append(TokenParser.SP);
                lastWasWhite = true;
            }
            i += Character.charCount(c);
        }
    }

    public static boolean in(String needle, String... haystack) {
        for (String hay : haystack) {
            if (hay.equals(needle)) {
                return true;
            }
        }
        return false;
    }
}
