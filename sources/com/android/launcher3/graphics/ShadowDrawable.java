package com.android.launcher3.graphics;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@TargetApi(26)
public class ShadowDrawable extends Drawable {
    private final Paint mPaint;
    private final ShadowDrawableState mState;

    public ShadowDrawable() {
        this(new ShadowDrawableState());
    }

    private ShadowDrawable(ShadowDrawableState state) {
        this.mPaint = new Paint(3);
        this.mState = state;
    }

    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        if (!bounds.isEmpty()) {
            if (this.mState.mLastDrawnBitmap == null) {
                regenerateBitmapCache();
            }
            canvas.drawBitmap(this.mState.mLastDrawnBitmap, (Rect) null, bounds, this.mPaint);
        }
    }

    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    public Drawable.ConstantState getConstantState() {
        return this.mState;
    }

    public int getOpacity() {
        return -3;
    }

    public int getIntrinsicHeight() {
        return this.mState.mIntrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.mState.mIntrinsicWidth;
    }

    public boolean canApplyTheme() {
        return this.mState.canApplyTheme();
    }

    public void applyTheme(Resources.Theme t) {
        TypedArray ta = t.obtainStyledAttributes(new int[]{R.attr.isWorkspaceDarkText});
        boolean isDark = ta.getBoolean(0, false);
        ta.recycle();
        if (this.mState.mIsDark != isDark) {
            this.mState.mIsDark = isDark;
            this.mState.mLastDrawnBitmap = null;
            invalidateSelf();
        }
    }

    private void regenerateBitmapCache() {
        Bitmap bitmap = Bitmap.createBitmap(this.mState.mIntrinsicWidth, this.mState.mIntrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable d = this.mState.mChildState.newDrawable().mutate();
        d.setBounds(this.mState.mShadowSize, this.mState.mShadowSize, this.mState.mIntrinsicWidth - this.mState.mShadowSize, this.mState.mIntrinsicHeight - this.mState.mShadowSize);
        d.setTint(this.mState.mIsDark ? this.mState.mDarkTintColor : -1);
        d.draw(canvas);
        if (!this.mState.mIsDark) {
            Paint paint = new Paint(3);
            paint.setMaskFilter(new BlurMaskFilter((float) this.mState.mShadowSize, BlurMaskFilter.Blur.NORMAL));
            int[] offset = new int[2];
            Bitmap shadow = bitmap.extractAlpha(paint, offset);
            paint.setMaskFilter((MaskFilter) null);
            paint.setColor(this.mState.mShadowColor);
            bitmap.eraseColor(0);
            canvas.drawBitmap(shadow, (float) offset[0], (float) offset[1], paint);
            d.draw(canvas);
        }
        if (Utilities.ATLEAST_OREO) {
            bitmap = bitmap.copy(Bitmap.Config.HARDWARE, false);
        }
        this.mState.mLastDrawnBitmap = bitmap;
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray a;
        super.inflate(r, parser, attrs, theme);
        if (theme == null) {
            a = r.obtainAttributes(attrs, R.styleable.ShadowDrawable);
        } else {
            a = theme.obtainStyledAttributes(attrs, R.styleable.ShadowDrawable, 0, 0);
        }
        try {
            Drawable d = a.getDrawable(0);
            if (d != null) {
                this.mState.mShadowColor = a.getColor(1, -16777216);
                this.mState.mShadowSize = a.getDimensionPixelSize(2, 0);
                this.mState.mDarkTintColor = a.getColor(3, -16777216);
                this.mState.mIntrinsicHeight = d.getIntrinsicHeight() + (this.mState.mShadowSize * 2);
                this.mState.mIntrinsicWidth = d.getIntrinsicWidth() + (this.mState.mShadowSize * 2);
                this.mState.mChangingConfigurations = d.getChangingConfigurations();
                this.mState.mChildState = d.getConstantState();
                return;
            }
            throw new XmlPullParserException("missing src attribute");
        } finally {
            a.recycle();
        }
    }

    private static class ShadowDrawableState extends Drawable.ConstantState {
        int mChangingConfigurations;
        Drawable.ConstantState mChildState;
        int mDarkTintColor;
        int mIntrinsicHeight;
        int mIntrinsicWidth;
        boolean mIsDark;
        Bitmap mLastDrawnBitmap;
        int mShadowColor;
        int mShadowSize;

        private ShadowDrawableState() {
        }

        public Drawable newDrawable() {
            return new ShadowDrawable(this);
        }

        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public boolean canApplyTheme() {
            return true;
        }
    }
}
