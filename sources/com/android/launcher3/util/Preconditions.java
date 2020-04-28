package com.android.launcher3.util;

import android.os.Looper;

public class Preconditions {
    public static void assertNotNull(Object o) {
    }

    public static void assertWorkerThread() {
    }

    public static void assertUIThread() {
    }

    public static void assertNonUiThread() {
    }

    private static boolean isSameLooper(Looper looper) {
        return Looper.myLooper() == looper;
    }
}
