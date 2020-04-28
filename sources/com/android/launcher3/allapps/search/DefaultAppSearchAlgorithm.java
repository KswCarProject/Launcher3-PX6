package com.android.launcher3.allapps.search;

import android.os.Handler;
import com.android.launcher3.AppInfo;
import com.android.launcher3.allapps.search.AllAppsSearchBarController;
import com.android.launcher3.util.ComponentKey;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

public class DefaultAppSearchAlgorithm implements SearchAlgorithm {
    private final List<AppInfo> mApps;
    protected final Handler mResultHandler = new Handler();

    public DefaultAppSearchAlgorithm(List<AppInfo> apps) {
        this.mApps = apps;
    }

    public void cancel(boolean interruptActiveRequests) {
        if (interruptActiveRequests) {
            this.mResultHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    public void doSearch(final String query, final AllAppsSearchBarController.Callbacks callback) {
        final ArrayList<ComponentKey> result = getTitleMatchResult(query);
        this.mResultHandler.post(new Runnable() {
            public void run() {
                callback.onSearchResult(query, result);
            }
        });
    }

    private ArrayList<ComponentKey> getTitleMatchResult(String query) {
        String queryTextLower = query.toLowerCase();
        ArrayList<ComponentKey> result = new ArrayList<>();
        StringMatcher matcher = StringMatcher.getInstance();
        for (AppInfo info : this.mApps) {
            if (matches(info, queryTextLower, matcher)) {
                result.add(info.toComponentKey());
            }
        }
        return result;
    }

    public static boolean matches(AppInfo info, String query, StringMatcher matcher) {
        int queryLength = query.length();
        String title = info.title.toString();
        int titleLength = title.length();
        if (titleLength < queryLength || queryLength <= 0) {
            return false;
        }
        int end = titleLength - queryLength;
        int nextType = Character.getType(title.codePointAt(0));
        int thisType = 0;
        int i = 0;
        while (i <= end) {
            int lastType = thisType;
            thisType = nextType;
            nextType = i < titleLength + -1 ? Character.getType(title.codePointAt(i + 1)) : 0;
            if (isBreak(thisType, lastType, nextType) && matcher.matches(query, title.substring(i, i + queryLength))) {
                return true;
            }
            i++;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0030, code lost:
        if (r4 == 1) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0033, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean isBreak(int r3, int r4, int r5) {
        /*
            r0 = 1
            if (r4 == 0) goto L_0x0036
            switch(r4) {
                case 12: goto L_0x0036;
                case 13: goto L_0x0036;
                case 14: goto L_0x0036;
                default: goto L_0x0006;
            }
        L_0x0006:
            r1 = 20
            if (r3 == r1) goto L_0x0035
            r1 = 0
            switch(r3) {
                case 1: goto L_0x002d;
                case 2: goto L_0x0024;
                case 3: goto L_0x0030;
                default: goto L_0x000e;
            }
        L_0x000e:
            switch(r3) {
                case 9: goto L_0x0015;
                case 10: goto L_0x0015;
                case 11: goto L_0x0015;
                default: goto L_0x0011;
            }
        L_0x0011:
            switch(r3) {
                case 24: goto L_0x0035;
                case 25: goto L_0x0035;
                case 26: goto L_0x0035;
                default: goto L_0x0014;
            }
        L_0x0014:
            return r1
        L_0x0015:
            r2 = 9
            if (r4 == r2) goto L_0x0022
            r2 = 10
            if (r4 == r2) goto L_0x0022
            r2 = 11
            if (r4 == r2) goto L_0x0022
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
            return r0
        L_0x0024:
            r2 = 5
            if (r4 > r2) goto L_0x002c
            if (r4 > 0) goto L_0x002a
            goto L_0x002c
        L_0x002a:
            r0 = 0
        L_0x002c:
            return r0
        L_0x002d:
            if (r5 != r0) goto L_0x0030
            return r0
        L_0x0030:
            if (r4 == r0) goto L_0x0033
            goto L_0x0034
        L_0x0033:
            r0 = 0
        L_0x0034:
            return r0
        L_0x0035:
            return r0
        L_0x0036:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.allapps.search.DefaultAppSearchAlgorithm.isBreak(int, int, int):boolean");
    }

    public static class StringMatcher {
        private static final char MAX_UNICODE = '￿';
        private final Collator mCollator = Collator.getInstance();

        StringMatcher() {
            this.mCollator.setStrength(0);
            this.mCollator.setDecomposition(1);
        }

        public boolean matches(String query, String target) {
            switch (this.mCollator.compare(query, target)) {
                case -1:
                    Collator collator = this.mCollator;
                    if (collator.compare(query + MAX_UNICODE, target) > -1) {
                        return true;
                    }
                    return false;
                case 0:
                    return true;
                default:
                    return false;
            }
        }

        public static StringMatcher getInstance() {
            return new StringMatcher();
        }
    }
}
