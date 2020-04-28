package com.android.launcher3.util;

import android.util.Property;
import android.view.View;
import com.android.launcher3.LauncherSettings;

public class MultiValueAlpha {
    public static final Property<AlphaProperty, Float> VALUE = new Property<AlphaProperty, Float>(Float.TYPE, LauncherSettings.Settings.EXTRA_VALUE) {
        public Float get(AlphaProperty alphaProperty) {
            return Float.valueOf(alphaProperty.mValue);
        }

        public void set(AlphaProperty object, Float value) {
            object.setValue(value.floatValue());
        }
    };
    /* access modifiers changed from: private */
    public final AlphaProperty[] mMyProperties;
    /* access modifiers changed from: private */
    public int mValidMask = 0;
    /* access modifiers changed from: private */
    public final View mView;

    public MultiValueAlpha(View view, int size) {
        this.mView = view;
        this.mMyProperties = new AlphaProperty[size];
        for (int i = 0; i < size; i++) {
            int myMask = 1 << i;
            this.mValidMask |= myMask;
            this.mMyProperties[i] = new AlphaProperty(myMask);
        }
    }

    public AlphaProperty getProperty(int index) {
        return this.mMyProperties[index];
    }

    public class AlphaProperty {
        private final int mMyMask;
        private float mOthers = 1.0f;
        /* access modifiers changed from: private */
        public float mValue = 1.0f;

        AlphaProperty(int myMask) {
            this.mMyMask = myMask;
        }

        public void setValue(float value) {
            if (this.mValue != value) {
                if ((MultiValueAlpha.this.mValidMask & this.mMyMask) == 0) {
                    this.mOthers = 1.0f;
                    for (AlphaProperty prop : MultiValueAlpha.this.mMyProperties) {
                        if (prop != this) {
                            this.mOthers *= prop.mValue;
                        }
                    }
                }
                int unused = MultiValueAlpha.this.mValidMask = this.mMyMask;
                this.mValue = value;
                MultiValueAlpha.this.mView.setAlpha(this.mOthers * this.mValue);
            }
        }

        public float getValue() {
            return this.mValue;
        }
    }
}
