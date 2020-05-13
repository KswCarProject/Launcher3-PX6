package org.apache.http.client.utils;

import java.util.StringTokenizer;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
@Deprecated
public class Rfc3492Idn implements Idn {
    private static final String ACE_PREFIX = "xn--";
    private static final int base = 36;
    private static final int damp = 700;
    private static final char delimiter = '-';
    private static final int initial_bias = 72;
    private static final int initial_n = 128;
    private static final int skew = 38;
    private static final int tmax = 26;
    private static final int tmin = 1;

    private int adapt(int delta, int numpoints, boolean firsttime) {
        int d;
        int d2 = delta;
        if (firsttime) {
            d = d2 / damp;
        } else {
            d = d2 / 2;
        }
        int d3 = d + (d / numpoints);
        int k = 0;
        while (d3 > 455) {
            d3 /= 35;
            k += 36;
        }
        return ((d3 * 36) / (d3 + 38)) + k;
    }

    private int digit(char c) {
        if (c >= 'A' && c <= 'Z') {
            return c - 'A';
        }
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }
        if (c >= '0' && c <= '9') {
            return (c - '0') + 26;
        }
        throw new IllegalArgumentException("illegal digit: " + c);
    }

    public String toUnicode(String punycode) {
        StringBuilder unicode = new StringBuilder(punycode.length());
        StringTokenizer tok = new StringTokenizer(punycode, ".");
        while (tok.hasMoreTokens()) {
            String t = tok.nextToken();
            if (unicode.length() > 0) {
                unicode.append('.');
            }
            if (t.startsWith(ACE_PREFIX)) {
                t = decode(t.substring(4));
            }
            unicode.append(t);
        }
        return unicode.toString();
    }

    /* access modifiers changed from: protected */
    public String decode(String s) {
        int t;
        String input = s;
        int n = 128;
        int i = 0;
        int bias = initial_bias;
        StringBuilder output = new StringBuilder(input.length());
        int lastdelim = input.lastIndexOf(45);
        if (lastdelim != -1) {
            output.append(input.subSequence(0, lastdelim));
            input = input.substring(lastdelim + 1);
        }
        while (!input.isEmpty()) {
            int oldi = i;
            int w = 1;
            int k = 36;
            while (!input.isEmpty()) {
                char c = input.charAt(0);
                input = input.substring(1);
                int digit = digit(c);
                i += digit * w;
                if (k <= bias + 1) {
                    t = 1;
                } else if (k >= bias + 26) {
                    t = 26;
                } else {
                    t = k - bias;
                }
                if (digit < t) {
                    break;
                }
                w *= 36 - t;
                k += 36;
            }
            bias = adapt(i - oldi, output.length() + 1, oldi == 0);
            n += i / (output.length() + 1);
            int i2 = i % (output.length() + 1);
            output.insert(i2, (char) n);
            i = i2 + 1;
        }
        return output.toString();
    }
}
