package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.ImageView;

public class HolographicViewHelper {
    private int mHighlightColor;
    private boolean mStatesUpdated;
    private final Canvas mTempCanvas = new Canvas();

    public HolographicViewHelper(Context context) {
        this.mHighlightColor = context.getResources().getColor(17170443);
    }

    /* access modifiers changed from: package-private */
    public void generatePressedFocusedStates(ImageView v) {
        if (!this.mStatesUpdated && v != null) {
            this.mStatesUpdated = true;
            Bitmap original = createOriginalImage(v, this.mTempCanvas);
            Bitmap outline = createPressImage(v, this.mTempCanvas);
            FastBitmapDrawable originalD = new FastBitmapDrawable(original);
            FastBitmapDrawable outlineD = new FastBitmapDrawable(outline);
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{16842919}, outlineD);
            states.addState(new int[]{16842908}, outlineD);
            states.addState(new int[0], originalD);
            v.setImageDrawable(states);
        }
    }

    /* access modifiers changed from: package-private */
    public void invalidatePressedFocusedStates(ImageView v) {
        this.mStatesUpdated = false;
        if (v != null) {
            v.invalidate();
        }
    }

    private Bitmap createOriginalImage(ImageView v, Canvas canvas) {
        Drawable d = v.getDrawable();
        Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        canvas.save();
        d.draw(canvas);
        canvas.restore();
        canvas.setBitmap((Bitmap) null);
        return b;
    }

    private Bitmap createPressImage(ImageView v, Canvas canvas) {
        Drawable d = v.getDrawable();
        Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        canvas.save();
        d.draw(canvas);
        canvas.restore();
        canvas.drawColor(this.mHighlightColor, PorterDuff.Mode.SRC_IN);
        canvas.setBitmap((Bitmap) null);
        return b;
    }
}
