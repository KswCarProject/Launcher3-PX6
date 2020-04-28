package com.android.launcher3.util;

public class CellAndSpan {
    public int cellX = -1;
    public int cellY = -1;
    public int spanX = 1;
    public int spanY = 1;

    public CellAndSpan() {
    }

    public void copyFrom(CellAndSpan copy) {
        this.cellX = copy.cellX;
        this.cellY = copy.cellY;
        this.spanX = copy.spanX;
        this.spanY = copy.spanY;
    }

    public CellAndSpan(int cellX2, int cellY2, int spanX2, int spanY2) {
        this.cellX = cellX2;
        this.cellY = cellY2;
        this.spanX = spanX2;
        this.spanY = spanY2;
    }

    public String toString() {
        return "(" + this.cellX + ", " + this.cellY + ": " + this.spanX + ", " + this.spanY + ")";
    }
}
