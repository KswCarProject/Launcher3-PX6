package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;

public class WidgetImageView extends View {
    private Drawable mBadge;
    private final int mBadgeMargin;
    private Bitmap mBitmap;
    private final RectF mDstRectF;
    private final Paint mPaint;

    public WidgetImageView(Context context) {
        this(context, (AttributeSet) null);
    }

    public WidgetImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaint = new Paint(3);
        this.mDstRectF = new RectF();
        this.mBadgeMargin = context.getResources().getDimensionPixelSize(R.dimen.profile_badge_margin);
    }

    public void setBitmap(Bitmap bitmap, Drawable badge) {
        this.mBitmap = bitmap;
        this.mBadge = badge;
        invalidate();
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mBitmap != null) {
            updateDstRectF();
            canvas.drawBitmap(this.mBitmap, (Rect) null, this.mDstRectF, this.mPaint);
            if (this.mBadge != null) {
                this.mBadge.draw(canvas);
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    private void updateDstRectF() {
        float myWidth = (float) getWidth();
        float myHeight = (float) getHeight();
        float bitmapWidth = (float) this.mBitmap.getWidth();
        float scale = bitmapWidth > myWidth ? myWidth / bitmapWidth : 1.0f;
        float scaledWidth = bitmapWidth * scale;
        float scaledHeight = ((float) this.mBitmap.getHeight()) * scale;
        this.mDstRectF.left = (myWidth - scaledWidth) / 2.0f;
        this.mDstRectF.right = (myWidth + scaledWidth) / 2.0f;
        if (scaledHeight > myHeight) {
            this.mDstRectF.top = 0.0f;
            this.mDstRectF.bottom = scaledHeight;
        } else {
            this.mDstRectF.top = (myHeight - scaledHeight) / 2.0f;
            this.mDstRectF.bottom = (myHeight + scaledHeight) / 2.0f;
        }
        if (this.mBadge != null) {
            Rect bounds = this.mBadge.getBounds();
            int left = Utilities.boundToRange((int) ((this.mDstRectF.right + ((float) this.mBadgeMargin)) - ((float) bounds.width())), this.mBadgeMargin, getWidth() - bounds.width());
            int top = Utilities.boundToRange((int) ((this.mDstRectF.bottom + ((float) this.mBadgeMargin)) - ((float) bounds.height())), this.mBadgeMargin, getHeight() - bounds.height());
            this.mBadge.setBounds(left, top, bounds.width() + left, bounds.height() + top);
        }
    }

    public Rect getBitmapBounds() {
        updateDstRectF();
        Rect rect = new Rect();
        this.mDstRectF.round(rect);
        return rect;
    }
}
