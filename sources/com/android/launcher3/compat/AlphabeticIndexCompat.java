package com.android.launcher3.compat;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.AlphabeticIndex;
import android.os.LocaleList;
import android.util.Log;
import com.android.launcher3.Utilities;
import java.lang.reflect.Method;
import java.util.Locale;

public class AlphabeticIndexCompat {
    private static final String MID_DOT = "∙";
    private static final String TAG = "AlphabeticIndexCompat";
    private final BaseIndex mBaseIndex;
    private final String mDefaultMiscLabel;

    public AlphabeticIndexCompat(Context context) {
        BaseIndex index = null;
        try {
            if (Utilities.ATLEAST_NOUGAT) {
                index = new AlphabeticIndexVN(context);
            }
        } catch (Exception e) {
            Log.d(TAG, "Unable to load the system index", e);
        }
        if (index == null) {
            try {
                index = new AlphabeticIndexV16(context);
            } catch (Exception e2) {
                Log.d(TAG, "Unable to load the system index", e2);
            }
        }
        this.mBaseIndex = index == null ? new BaseIndex() : index;
        if (context.getResources().getConfiguration().locale.getLanguage().equals(Locale.JAPANESE.getLanguage())) {
            this.mDefaultMiscLabel = "他";
        } else {
            this.mDefaultMiscLabel = MID_DOT;
        }
    }

    public String computeSectionName(CharSequence cs) {
        String s = Utilities.trim(cs);
        String sectionName = this.mBaseIndex.getBucketLabel(this.mBaseIndex.getBucketIndex(s));
        if (!Utilities.trim(sectionName).isEmpty() || s.length() <= 0) {
            return sectionName;
        }
        int c = s.codePointAt(0);
        if (Character.isDigit(c)) {
            return "#";
        }
        if (Character.isLetter(c)) {
            return this.mDefaultMiscLabel;
        }
        return MID_DOT;
    }

    private static class BaseIndex {
        private static final String BUCKETS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ-";
        private static final int UNKNOWN_BUCKET_INDEX = (BUCKETS.length() - 1);

        private BaseIndex() {
        }

        /* access modifiers changed from: protected */
        public int getBucketIndex(String s) {
            if (s.isEmpty()) {
                return UNKNOWN_BUCKET_INDEX;
            }
            int index = BUCKETS.indexOf(s.substring(0, 1).toUpperCase());
            if (index != -1) {
                return index;
            }
            return UNKNOWN_BUCKET_INDEX;
        }

        /* access modifiers changed from: protected */
        public String getBucketLabel(int index) {
            return BUCKETS.substring(index, index + 1);
        }
    }

    private static class AlphabeticIndexV16 extends BaseIndex {
        private Object mAlphabeticIndex;
        private Method mGetBucketIndexMethod;
        private Method mGetBucketLabelMethod;

        public AlphabeticIndexV16(Context context) throws Exception {
            super();
            Locale curLocale = context.getResources().getConfiguration().locale;
            Class clazz = Class.forName("libcore.icu.AlphabeticIndex");
            this.mGetBucketIndexMethod = clazz.getDeclaredMethod("getBucketIndex", new Class[]{String.class});
            this.mGetBucketLabelMethod = clazz.getDeclaredMethod("getBucketLabel", new Class[]{Integer.TYPE});
            this.mAlphabeticIndex = clazz.getConstructor(new Class[]{Locale.class}).newInstance(new Object[]{curLocale});
            if (!curLocale.getLanguage().equals(Locale.ENGLISH.getLanguage())) {
                clazz.getDeclaredMethod("addLabels", new Class[]{Locale.class}).invoke(this.mAlphabeticIndex, new Object[]{Locale.ENGLISH});
            }
        }

        /* access modifiers changed from: protected */
        public int getBucketIndex(String s) {
            try {
                return ((Integer) this.mGetBucketIndexMethod.invoke(this.mAlphabeticIndex, new Object[]{s})).intValue();
            } catch (Exception e) {
                e.printStackTrace();
                return super.getBucketIndex(s);
            }
        }

        /* access modifiers changed from: protected */
        public String getBucketLabel(int index) {
            try {
                return (String) this.mGetBucketLabelMethod.invoke(this.mAlphabeticIndex, new Object[]{Integer.valueOf(index)});
            } catch (Exception e) {
                e.printStackTrace();
                return super.getBucketLabel(index);
            }
        }
    }

    @TargetApi(24)
    private static class AlphabeticIndexVN extends BaseIndex {
        private final AlphabeticIndex.ImmutableIndex mAlphabeticIndex;

        public AlphabeticIndexVN(Context context) {
            super();
            LocaleList locales = context.getResources().getConfiguration().getLocales();
            int localeCount = locales.size();
            AlphabeticIndex indexBuilder = new AlphabeticIndex(localeCount == 0 ? Locale.ENGLISH : locales.get(0));
            for (int i = 1; i < localeCount; i++) {
                indexBuilder.addLabels(new Locale[]{locales.get(i)});
            }
            indexBuilder.addLabels(new Locale[]{Locale.ENGLISH});
            this.mAlphabeticIndex = indexBuilder.buildImmutableIndex();
        }

        /* access modifiers changed from: protected */
        public int getBucketIndex(String s) {
            return this.mAlphabeticIndex.getBucketIndex(s);
        }

        /* access modifiers changed from: protected */
        public String getBucketLabel(int index) {
            return this.mAlphabeticIndex.getBucket(index).getLabel();
        }
    }
}
