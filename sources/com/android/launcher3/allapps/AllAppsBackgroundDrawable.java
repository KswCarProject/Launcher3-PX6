package com.android.launcher3.allapps;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.util.Themes;

public class AllAppsBackgroundDrawable extends Drawable {
    private ObjectAnimator mBackgroundAnim;
    protected final TransformedImageDrawable mHand;
    private final int mHeight;
    protected final TransformedImageDrawable[] mIcons;
    private final int mWidth;

    protected static class TransformedImageDrawable {
        private int mAlpha;
        private int mGravity;
        private Drawable mImage;
        private float mXPercent;
        private float mYPercent;

        public TransformedImageDrawable(Context context, int resourceId, float xPct, float yPct, int gravity) {
            this.mImage = context.getDrawable(resourceId);
            this.mXPercent = xPct;
            this.mYPercent = yPct;
            this.mGravity = gravity;
        }

        public void setAlpha(int alpha) {
            this.mImage.setAlpha(alpha);
            this.mAlpha = alpha;
        }

        public int getAlpha() {
            return this.mAlpha;
        }

        public void updateBounds(Rect bounds) {
            int width = this.mImage.getIntrinsicWidth();
            int height = this.mImage.getIntrinsicHeight();
            int left = bounds.left + ((int) (this.mXPercent * ((float) bounds.width())));
            int top = bounds.top + ((int) (this.mYPercent * ((float) bounds.height())));
            if ((this.mGravity & 1) == 1) {
                left -= width / 2;
            }
            if ((this.mGravity & 16) == 16) {
                top -= height / 2;
            }
            this.mImage.setBounds(left, top, left + width, top + height);
        }

        public void draw(Canvas canvas) {
            this.mImage.draw(canvas);
        }

        public Rect getBounds() {
            return this.mImage.getBounds();
        }
    }

    public AllAppsBackgroundDrawable(Context context) {
        Resources res = context.getResources();
        this.mWidth = res.getDimensionPixelSize(R.dimen.all_apps_background_canvas_width);
        this.mHeight = res.getDimensionPixelSize(R.dimen.all_apps_background_canvas_height);
        Context context2 = new ContextThemeWrapper(context, Themes.getAttrBoolean(context, R.attr.isMainColorDark) ? R.style.AllAppsEmptySearchBackground_Dark : R.style.AllAppsEmptySearchBackground);
        this.mHand = new TransformedImageDrawable(context2, R.drawable.ic_all_apps_bg_hand, 0.575f, 0.0f, 1);
        this.mIcons = new TransformedImageDrawable[4];
        Context context3 = context2;
        this.mIcons[0] = new TransformedImageDrawable(context3, R.drawable.ic_all_apps_bg_icon_1, 0.375f, 0.0f, 1);
        this.mIcons[1] = new TransformedImageDrawable(context3, R.drawable.ic_all_apps_bg_icon_2, 0.3125f, 0.2f, 1);
        this.mIcons[2] = new TransformedImageDrawable(context3, R.drawable.ic_all_apps_bg_icon_3, 0.475f, 0.26f, 1);
        this.mIcons[3] = new TransformedImageDrawable(context3, R.drawable.ic_all_apps_bg_icon_4, 0.7f, 0.125f, 1);
    }

    public void animateBgAlpha(float finalAlpha, int duration) {
        int finalAlphaI = (int) (255.0f * finalAlpha);
        if (getAlpha() != finalAlphaI) {
            this.mBackgroundAnim = cancelAnimator(this.mBackgroundAnim);
            this.mBackgroundAnim = ObjectAnimator.ofInt(this, LauncherAnimUtils.DRAWABLE_ALPHA, new int[]{finalAlphaI});
            this.mBackgroundAnim.setDuration((long) duration);
            this.mBackgroundAnim.start();
        }
    }

    public void setBgAlpha(float finalAlpha) {
        int finalAlphaI = (int) (255.0f * finalAlpha);
        if (getAlpha() != finalAlphaI) {
            this.mBackgroundAnim = cancelAnimator(this.mBackgroundAnim);
            setAlpha(finalAlphaI);
        }
    }

    public int getIntrinsicWidth() {
        return this.mWidth;
    }

    public int getIntrinsicHeight() {
        return this.mHeight;
    }

    public void draw(Canvas canvas) {
        this.mHand.draw(canvas);
        for (TransformedImageDrawable draw : this.mIcons) {
            draw.draw(canvas);
        }
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.mHand.updateBounds(bounds);
        for (TransformedImageDrawable updateBounds : this.mIcons) {
            updateBounds.updateBounds(bounds);
        }
        invalidateSelf();
    }

    public void setAlpha(int alpha) {
        this.mHand.setAlpha(alpha);
        for (TransformedImageDrawable alpha2 : this.mIcons) {
            alpha2.setAlpha(alpha);
        }
        invalidateSelf();
    }

    public int getAlpha() {
        return this.mHand.getAlpha();
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public int getOpacity() {
        return -3;
    }

    private ObjectAnimator cancelAnimator(ObjectAnimator animator) {
        if (animator == null) {
            return null;
        }
        animator.cancel();
        return null;
    }
}
