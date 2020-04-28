package com.android.launcher3.badge;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import com.android.launcher3.graphics.ShadowGenerator;

public class BadgeRenderer {
    private static final float DOT_SCALE = 0.6f;
    private static final float OFFSET_PERCENTAGE = 0.02f;
    private static final float SIZE_PERCENTAGE = 0.38f;
    private static final String TAG = "BadgeRenderer";
    private final Bitmap mBackgroundWithShadow;
    private final float mBitmapOffset;
    private final Paint mCirclePaint = new Paint(3);
    private final float mCircleRadius;
    private final float mDotCenterOffset;
    private final int mOffset;

    public BadgeRenderer(int iconSizePx) {
        this.mDotCenterOffset = ((float) iconSizePx) * SIZE_PERCENTAGE;
        this.mOffset = (int) (((float) iconSizePx) * OFFSET_PERCENTAGE);
        int size = (int) (this.mDotCenterOffset * DOT_SCALE);
        ShadowGenerator.Builder builder = new ShadowGenerator.Builder(0);
        this.mBackgroundWithShadow = builder.setupBlurForSize(size).createPill(size, size);
        this.mCircleRadius = builder.radius;
        this.mBitmapOffset = ((float) (-this.mBackgroundWithShadow.getHeight())) * 0.5f;
    }

    public void draw(Canvas canvas, int color, Rect iconBounds, float badgeScale, Point spaceForOffset) {
        if (iconBounds == null || spaceForOffset == null) {
            Log.e(TAG, "Invalid null argument(s) passed in call to draw.");
            return;
        }
        canvas.save();
        float badgeCenterX = ((float) iconBounds.right) - (this.mDotCenterOffset / 2.0f);
        float badgeCenterY = ((float) iconBounds.top) + (this.mDotCenterOffset / 2.0f);
        canvas.translate(((float) Math.min(this.mOffset, spaceForOffset.x)) + badgeCenterX, badgeCenterY - ((float) Math.min(this.mOffset, spaceForOffset.y)));
        canvas.scale(badgeScale, badgeScale);
        this.mCirclePaint.setColor(-16777216);
        canvas.drawBitmap(this.mBackgroundWithShadow, this.mBitmapOffset, this.mBitmapOffset, this.mCirclePaint);
        this.mCirclePaint.setColor(color);
        canvas.drawCircle(0.0f, 0.0f, this.mCircleRadius, this.mCirclePaint);
        canvas.restore();
    }
}
