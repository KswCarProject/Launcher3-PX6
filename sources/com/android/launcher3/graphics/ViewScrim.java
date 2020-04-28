package com.android.launcher3.graphics;

import android.graphics.Canvas;
import android.support.v4.app.NotificationCompat;
import android.util.Property;
import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.R;

public abstract class ViewScrim<T extends View> {
    public static Property<ViewScrim, Float> PROGRESS = new Property<ViewScrim, Float>(Float.TYPE, NotificationCompat.CATEGORY_PROGRESS) {
        public Float get(ViewScrim viewScrim) {
            return Float.valueOf(viewScrim.mProgress);
        }

        public void set(ViewScrim object, Float value) {
            object.setProgress(value.floatValue());
        }
    };
    protected float mProgress = 0.0f;
    protected final T mView;

    public abstract void draw(Canvas canvas, int i, int i2);

    public ViewScrim(T view) {
        this.mView = view;
    }

    public void attach() {
        this.mView.setTag(R.id.view_scrim, this);
    }

    public void setProgress(float progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            onProgressChanged();
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onProgressChanged() {
    }

    public void invalidate() {
        ViewParent parent = this.mView.getParent();
        if (parent != null) {
            ((View) parent).invalidate();
        }
    }

    public static ViewScrim get(View view) {
        return (ViewScrim) view.getTag(R.id.view_scrim);
    }
}
