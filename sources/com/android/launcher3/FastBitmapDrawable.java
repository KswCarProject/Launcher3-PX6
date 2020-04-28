package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.ActivityChooserView;
import android.util.Property;
import android.util.SparseArray;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.graphics.BitmapInfo;

public class FastBitmapDrawable extends Drawable {
    public static final int CLICK_FEEDBACK_DURATION = 200;
    private static final float DISABLED_BRIGHTNESS = 0.5f;
    private static final float DISABLED_DESATURATION = 1.0f;
    private static final float PRESSED_SCALE = 1.1f;
    private static final int REDUCED_FILTER_VALUE_SPACE = 48;
    private static final Property<FastBitmapDrawable, Float> SCALE = new Property<FastBitmapDrawable, Float>(Float.TYPE, "scale") {
        public Float get(FastBitmapDrawable fastBitmapDrawable) {
            return Float.valueOf(fastBitmapDrawable.mScale);
        }

        public void set(FastBitmapDrawable fastBitmapDrawable, Float value) {
            float unused = fastBitmapDrawable.mScale = value.floatValue();
            fastBitmapDrawable.invalidateSelf();
        }
    };
    private static final SparseArray<ColorFilter> sCachedFilter = new SparseArray<>();
    private static final ColorMatrix sTempBrightnessMatrix = new ColorMatrix();
    private static final ColorMatrix sTempFilterMatrix = new ColorMatrix();
    private int mAlpha;
    protected Bitmap mBitmap;
    private int mBrightness;
    private int mDesaturation;
    protected final int mIconColor;
    private boolean mIsDisabled;
    private boolean mIsPressed;
    protected final Paint mPaint;
    private int mPrevUpdateKey;
    /* access modifiers changed from: private */
    public float mScale;
    private ObjectAnimator mScaleAnimation;

    public FastBitmapDrawable(Bitmap b) {
        this(b, 0);
    }

    public FastBitmapDrawable(BitmapInfo info) {
        this(info.icon, info.color);
    }

    public FastBitmapDrawable(ItemInfoWithIcon info) {
        this(info.iconBitmap, info.iconColor);
    }

    protected FastBitmapDrawable(Bitmap b, int iconColor) {
        this.mPaint = new Paint(3);
        this.mScale = 1.0f;
        this.mDesaturation = 0;
        this.mBrightness = 0;
        this.mAlpha = 255;
        this.mPrevUpdateKey = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        this.mBitmap = b;
        this.mIconColor = iconColor;
        setFilterBitmap(true);
    }

    public final void draw(Canvas canvas) {
        if (this.mScaleAnimation != null) {
            int count = canvas.save();
            Rect bounds = getBounds();
            canvas.scale(this.mScale, this.mScale, bounds.exactCenterX(), bounds.exactCenterY());
            drawInternal(canvas, bounds);
            canvas.restoreToCount(count);
            return;
        }
        drawInternal(canvas, getBounds());
    }

    /* access modifiers changed from: protected */
    public void drawInternal(Canvas canvas, Rect bounds) {
        canvas.drawBitmap(this.mBitmap, (Rect) null, bounds, this.mPaint);
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return -3;
    }

    public void setAlpha(int alpha) {
        this.mAlpha = alpha;
        this.mPaint.setAlpha(alpha);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        this.mPaint.setFilterBitmap(filterBitmap);
        this.mPaint.setAntiAlias(filterBitmap);
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    public float getAnimatedScale() {
        if (this.mScaleAnimation == null) {
            return 1.0f;
        }
        return this.mScale;
    }

    public int getIntrinsicWidth() {
        return this.mBitmap.getWidth();
    }

    public int getIntrinsicHeight() {
        return this.mBitmap.getHeight();
    }

    public int getMinimumWidth() {
        return getBounds().width();
    }

    public int getMinimumHeight() {
        return getBounds().height();
    }

    public boolean isStateful() {
        return true;
    }

    public ColorFilter getColorFilter() {
        return this.mPaint.getColorFilter();
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] state) {
        boolean isPressed = false;
        int length = state.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (state[i] == 16842919) {
                isPressed = true;
                break;
            } else {
                i++;
            }
        }
        if (this.mIsPressed == isPressed) {
            return false;
        }
        this.mIsPressed = isPressed;
        if (this.mScaleAnimation != null) {
            this.mScaleAnimation.cancel();
            this.mScaleAnimation = null;
        }
        if (this.mIsPressed) {
            this.mScaleAnimation = ObjectAnimator.ofFloat(this, SCALE, new float[]{1.1f});
            this.mScaleAnimation.setDuration(200);
            this.mScaleAnimation.setInterpolator(Interpolators.ACCEL);
            this.mScaleAnimation.start();
        } else {
            this.mScale = 1.0f;
            invalidateSelf();
        }
        return true;
    }

    private void invalidateDesaturationAndBrightness() {
        float f = 0.0f;
        setDesaturation(this.mIsDisabled ? 1.0f : 0.0f);
        if (this.mIsDisabled) {
            f = 0.5f;
        }
        setBrightness(f);
    }

    public void setIsDisabled(boolean isDisabled) {
        if (this.mIsDisabled != isDisabled) {
            this.mIsDisabled = isDisabled;
            invalidateDesaturationAndBrightness();
        }
    }

    private void setDesaturation(float desaturation) {
        int newDesaturation = (int) Math.floor((double) (48.0f * desaturation));
        if (this.mDesaturation != newDesaturation) {
            this.mDesaturation = newDesaturation;
            updateFilter();
        }
    }

    public float getDesaturation() {
        return ((float) this.mDesaturation) / 48.0f;
    }

    private void setBrightness(float brightness) {
        int newBrightness = (int) Math.floor((double) (48.0f * brightness));
        if (this.mBrightness != newBrightness) {
            this.mBrightness = newBrightness;
            updateFilter();
        }
    }

    private float getBrightness() {
        return ((float) this.mBrightness) / 48.0f;
    }

    /* access modifiers changed from: protected */
    public void updateFilter() {
        boolean usePorterDuffFilter = false;
        int key = -1;
        if (this.mDesaturation > 0) {
            key = (this.mDesaturation << 16) | this.mBrightness;
        } else if (this.mBrightness > 0) {
            key = this.mBrightness | 65536;
            usePorterDuffFilter = true;
        }
        if (key != this.mPrevUpdateKey) {
            this.mPrevUpdateKey = key;
            if (key != -1) {
                ColorFilter filter = sCachedFilter.get(key);
                if (filter == null) {
                    float brightnessF = getBrightness();
                    int brightnessI = (int) (255.0f * brightnessF);
                    if (usePorterDuffFilter) {
                        filter = new PorterDuffColorFilter(Color.argb(brightnessI, 255, 255, 255), PorterDuff.Mode.SRC_ATOP);
                    } else {
                        sTempFilterMatrix.setSaturation(1.0f - getDesaturation());
                        if (this.mBrightness > 0) {
                            float scale = 1.0f - brightnessF;
                            float[] mat = sTempBrightnessMatrix.getArray();
                            mat[0] = scale;
                            mat[6] = scale;
                            mat[12] = scale;
                            mat[4] = (float) brightnessI;
                            mat[9] = (float) brightnessI;
                            mat[14] = (float) brightnessI;
                            sTempFilterMatrix.preConcat(sTempBrightnessMatrix);
                        }
                        filter = new ColorMatrixColorFilter(sTempFilterMatrix);
                    }
                    sCachedFilter.append(key, filter);
                }
                this.mPaint.setColorFilter(filter);
            } else {
                this.mPaint.setColorFilter((ColorFilter) null);
            }
            invalidateSelf();
        }
    }

    public Drawable.ConstantState getConstantState() {
        return new MyConstantState(this.mBitmap, this.mIconColor);
    }

    protected static class MyConstantState extends Drawable.ConstantState {
        protected final Bitmap mBitmap;
        protected final int mIconColor;

        public MyConstantState(Bitmap bitmap, int color) {
            this.mBitmap = bitmap;
            this.mIconColor = color;
        }

        public Drawable newDrawable() {
            return new FastBitmapDrawable(this.mBitmap, this.mIconColor);
        }

        public int getChangingConfigurations() {
            return 0;
        }
    }
}
