package com.android.launcher3.graphics;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

@TargetApi(24)
public class FixedScaleDrawable extends DrawableWrapper {
    private static final float LEGACY_ICON_SCALE = 0.46669f;
    private float mScaleX = LEGACY_ICON_SCALE;
    private float mScaleY = LEGACY_ICON_SCALE;

    public FixedScaleDrawable() {
        super(new ColorDrawable());
    }

    public void draw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.scale(this.mScaleX, this.mScaleY, getBounds().exactCenterX(), getBounds().exactCenterY());
        super.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) {
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) {
    }

    public void setScale(float scale) {
        float h = (float) getIntrinsicHeight();
        float w = (float) getIntrinsicWidth();
        this.mScaleX = scale * LEGACY_ICON_SCALE;
        this.mScaleY = LEGACY_ICON_SCALE * scale;
        if (h > w && w > 0.0f) {
            this.mScaleX *= w / h;
        } else if (w > h && h > 0.0f) {
            this.mScaleY *= h / w;
        }
    }
}
