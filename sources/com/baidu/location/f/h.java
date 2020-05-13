package com.baidu.location.f;

import java.util.List;

class h {
    public static String a = null;
    public int b = 0;
    private boolean c = false;
    private String d = "";
    private boolean e = false;
    private double f = 0.0d;
    private double g = 0.0d;

    public h(List<String> list, String str, String str2, String str3) {
        this.d = str3;
        d();
    }

    private boolean a(String str) {
        if (str == null || str.length() <= 8) {
            return false;
        }
        char c2 = 0;
        for (int i = 1; i < str.length() - 3; i++) {
            c2 ^= str.charAt(i);
        }
        return Integer.toHexString(c2).equalsIgnoreCase(str.substring(str.length() + -2, str.length()));
    }

    private void d() {
        if (a(this.d)) {
            String substring = this.d.substring(0, this.d.length() - 3);
            int i = 0;
            for (int i2 = 0; i2 < substring.length(); i2++) {
                if (substring.charAt(i2) == ',') {
                    i++;
                }
            }
            String[] split = substring.split(",", i + 1);
            if (split.length >= 6) {
                if (!split[2].equals("") && !split[split.length - 3].equals("") && !split[split.length - 2].equals("") && !split[split.length - 1].equals("")) {
                    try {
                        this.f = Double.valueOf(split[split.length - 3]).doubleValue();
                        this.g = Double.valueOf(split[split.length - 2]).doubleValue();
                    } catch (Exception e2) {
                    }
                    this.e = true;
                }
            } else {
                return;
            }
        }
        this.c = this.e;
    }

    public boolean a() {
        return this.c;
    }

    public double b() {
        return this.f;
    }

    public double c() {
        return this.g;
    }
}
