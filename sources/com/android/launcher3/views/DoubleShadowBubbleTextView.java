package com.android.launcher3.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.R;

public class DoubleShadowBubbleTextView extends BubbleTextView {
    private final ShadowInfo mShadowInfo;

    public DoubleShadowBubbleTextView(Context context) {
        this(context, (AttributeSet) null);
    }

    public DoubleShadowBubbleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DoubleShadowBubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mShadowInfo = new ShadowInfo(context, attrs, defStyle);
        setShadowLayer(this.mShadowInfo.ambientShadowBlur, 0.0f, 0.0f, this.mShadowInfo.ambientShadowColor);
    }

    public void onDraw(Canvas canvas) {
        if (this.mShadowInfo.skipDoubleShadow(this)) {
            super.onDraw(canvas);
            return;
        }
        int alpha = Color.alpha(getCurrentTextColor());
        getPaint().setShadowLayer(this.mShadowInfo.ambientShadowBlur, 0.0f, 0.0f, ColorUtils.setAlphaComponent(this.mShadowInfo.ambientShadowColor, alpha));
        drawWithoutBadge(canvas);
        canvas.save();
        canvas.clipRect(getScrollX(), getScrollY() + getExtendedPaddingTop(), getScrollX() + getWidth(), getScrollY() + getHeight());
        getPaint().setShadowLayer(this.mShadowInfo.keyShadowBlur, 0.0f, this.mShadowInfo.keyShadowOffset, ColorUtils.setAlphaComponent(this.mShadowInfo.keyShadowColor, alpha));
        drawWithoutBadge(canvas);
        canvas.restore();
        drawBadgeIfNecessary(canvas);
    }

    public static class ShadowInfo {
        public final float ambientShadowBlur;
        public final int ambientShadowColor;
        public final float keyShadowBlur;
        public final int keyShadowColor;
        public final float keyShadowOffset;

        public ShadowInfo(Context c, AttributeSet attrs, int defStyle) {
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ShadowInfo, defStyle, 0);
            this.ambientShadowBlur = a.getDimension(0, 0.0f);
            this.ambientShadowColor = a.getColor(1, 0);
            this.keyShadowBlur = a.getDimension(2, 0.0f);
            this.keyShadowOffset = a.getDimension(4, 0.0f);
            this.keyShadowColor = a.getColor(3, 0);
            a.recycle();
        }

        public boolean skipDoubleShadow(TextView textView) {
            int textAlpha = Color.alpha(textView.getCurrentTextColor());
            int keyShadowAlpha = Color.alpha(this.keyShadowColor);
            int ambientShadowAlpha = Color.alpha(this.ambientShadowColor);
            if (textAlpha == 0 || (keyShadowAlpha == 0 && ambientShadowAlpha == 0)) {
                textView.getPaint().clearShadowLayer();
                return true;
            } else if (ambientShadowAlpha > 0) {
                textView.getPaint().setShadowLayer(this.ambientShadowBlur, 0.0f, 0.0f, ColorUtils.setAlphaComponent(this.ambientShadowColor, textAlpha));
                return true;
            } else if (keyShadowAlpha <= 0) {
                return false;
            } else {
                textView.getPaint().setShadowLayer(this.keyShadowBlur, 0.0f, this.keyShadowOffset, ColorUtils.setAlphaComponent(this.keyShadowColor, textAlpha));
                return true;
            }
        }
    }
}
