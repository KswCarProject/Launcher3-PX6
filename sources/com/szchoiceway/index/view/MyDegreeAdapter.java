package com.szchoiceway.index.view;

import android.graphics.Point;

public class MyDegreeAdapter {
    private static final double PI = 3.141592653589793d;

    enum _Quadrant {
        eQ_NONE,
        eQ_ONE,
        eQ_TWO,
        eQ_THREE,
        eQ_FOUR
    }

    public static _Quadrant GetQuadrant(Point point) {
        if (point.x == 0 || point.y == 0) {
            return _Quadrant.eQ_NONE;
        }
        if (point.x > 0) {
            if (point.y > 0) {
                return _Quadrant.eQ_ONE;
            }
            return _Quadrant.eQ_TWO;
        } else if (point.y < 0) {
            return _Quadrant.eQ_THREE;
        } else {
            return _Quadrant.eQ_FOUR;
        }
    }

    public static int GetRadianByPos(Point point) {
        return (int) (57.29577951308232d * GetRadianByPosEx(point));
    }

    private static double GetRadianByPosEx(Point point) {
        if (point.x == 0 && point.y == 0) {
            return 0.0d;
        }
        double dAngle = Math.asin(((double) point.x) / Math.sqrt((double) ((point.x * point.x) + (point.y * point.y))));
        switch (GetQuadrant(point)) {
            case eQ_NONE:
                if (point.x == 0 && point.y == 0) {
                    return 0.0d;
                }
                if (point.x == 0) {
                    if (point.y > 0) {
                        return 0.0d;
                    }
                    return PI;
                } else if (point.y != 0) {
                    return dAngle;
                } else {
                    if (point.x > 0) {
                        return 1.5707963267948966d;
                    }
                    return 4.71238899230957d;
                }
            case eQ_TWO:
                return PI - dAngle;
            case eQ_THREE:
                return PI - dAngle;
            case eQ_FOUR:
                return dAngle + 6.283185307179586d;
            default:
                return dAngle;
        }
    }
}
