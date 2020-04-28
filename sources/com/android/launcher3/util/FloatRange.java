package com.android.launcher3.util;

public class FloatRange {
    public float end;
    public float start;

    public FloatRange() {
    }

    public FloatRange(float s, float e) {
        set(s, e);
    }

    public void set(float s, float e) {
        this.start = s;
        this.end = e;
    }

    public boolean contains(float value) {
        return value >= this.start && value <= this.end;
    }
}
