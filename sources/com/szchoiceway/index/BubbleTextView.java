package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class BubbleTextView extends TextView {
    static final float CORNER_RADIUS = 4.0f;
    static final float PADDING_H = 8.0f;
    static final float PADDING_V = 3.0f;
    static final int SHADOW_LARGE_COLOUR = -587202560;
    static final float SHADOW_LARGE_RADIUS = 4.0f;
    static final int SHADOW_SMALL_COLOUR = -872415232;
    static final float SHADOW_SMALL_RADIUS = 1.75f;
    static final float SHADOW_Y_OFFSET = 2.0f;
    private static final String TAG = "BubbleTextView";
    private Drawable mBackground;
    private boolean mBackgroundSizeChanged;
    private boolean mDidInvalidateForPressedState;
    private int mFocusedGlowColor;
    private int mFocusedOutlineColor;
    private CheckLongPressHelper mLongPressHelper;
    private final HolographicOutlineHelper mOutlineHelper = new HolographicOutlineHelper();
    private Paint mPaint;
    private int mPressedGlowColor;
    private Bitmap mPressedOrFocusedBackground;
    private int mPressedOutlineColor;
    private int mPrevAlpha = -1;
    private final RectF mRect = new RectF();
    private boolean mStayPressed;
    private final Canvas mTempCanvas = new Canvas();
    private final Rect mTempRect = new Rect();

    public BubbleTextView(Context context) {
        super(context);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.mLongPressHelper = new CheckLongPressHelper(this);
        this.mBackground = getBackground();
        int color = getContext().getResources().getColor(17170443);
        this.mPressedGlowColor = color;
        this.mPressedOutlineColor = color;
        this.mFocusedGlowColor = color;
        this.mFocusedOutlineColor = color;
        this.mPaint = new Paint(1);
        this.mPaint.setColor(-1306978023);
    }

    public void applyFromShortcutInfo(ShortcutInfo info, IconCache iconCache) {
        setCompoundDrawablesWithIntrinsicBounds((Drawable) null, new FastBitmapDrawable(info.getIcon(iconCache)), (Drawable) null, (Drawable) null);
        Log.i(TAG, "applyFromShortcutInfo: info.title");
        setText(info.title);
        setTag(info);
    }

    /* access modifiers changed from: protected */
    public boolean setFrame(int left, int top, int right, int bottom) {
        if (!(getLeft() == left && getRight() == right && getTop() == top && getBottom() == bottom)) {
            this.mBackgroundSizeChanged = true;
        }
        return super.setFrame(left, top, right, bottom);
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return who == this.mBackground || super.verifyDrawable(who);
    }

    public void setTag(Object tag) {
        if (tag != null) {
            LauncherModel.checkItemInfo((ItemInfo) tag);
        }
        super.setTag(tag);
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        boolean backgroundEmptyBefore;
        boolean backgroundEmptyNow;
        if (!isPressed()) {
            if (this.mPressedOrFocusedBackground == null) {
                backgroundEmptyBefore = true;
            } else {
                backgroundEmptyBefore = false;
            }
            if (!this.mStayPressed) {
                this.mPressedOrFocusedBackground = null;
            }
            if (isFocused()) {
                if (getLayout() == null) {
                    this.mPressedOrFocusedBackground = null;
                } else {
                    this.mPressedOrFocusedBackground = createGlowingOutline(this.mTempCanvas, this.mFocusedGlowColor, this.mFocusedOutlineColor);
                }
                this.mStayPressed = false;
                setCellLayoutPressedOrFocusedIcon();
            }
            if (this.mPressedOrFocusedBackground == null) {
                backgroundEmptyNow = true;
            } else {
                backgroundEmptyNow = false;
            }
            if (!backgroundEmptyBefore && backgroundEmptyNow) {
                setCellLayoutPressedOrFocusedIcon();
            }
        } else if (!this.mDidInvalidateForPressedState) {
            setCellLayoutPressedOrFocusedIcon();
        }
        Drawable d = this.mBackground;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
        super.drawableStateChanged();
    }

    private void drawWithPadding(Canvas destCanvas, int padding) {
        Rect clipRect = this.mTempRect;
        getDrawingRect(clipRect);
        clipRect.bottom = (getExtendedPaddingTop() - 3) + getLayout().getLineTop(0);
        destCanvas.save();
        destCanvas.scale(getScaleX(), getScaleY(), (float) ((getWidth() + padding) / 2), (float) ((getHeight() + padding) / 2));
        destCanvas.translate((float) ((-getScrollX()) + (padding / 2)), (float) ((-getScrollY()) + (padding / 2)));
        destCanvas.clipRect(clipRect, Region.Op.REPLACE);
        draw(destCanvas);
        destCanvas.restore();
    }

    private Bitmap createGlowingOutline(Canvas canvas, int outlineColor, int glowColor) {
        int padding = HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS;
        Bitmap b = Bitmap.createBitmap(getWidth() + padding, getHeight() + padding, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(b);
        drawWithPadding(canvas, padding);
        this.mOutlineHelper.applyExtraThickExpensiveOutlineWithBlur(b, canvas, glowColor, outlineColor);
        canvas.setBitmap((Bitmap) null);
        return b;
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        switch (event.getAction()) {
            case 0:
                if (this.mPressedOrFocusedBackground == null) {
                    this.mPressedOrFocusedBackground = createGlowingOutline(this.mTempCanvas, this.mPressedGlowColor, this.mPressedOutlineColor);
                }
                if (isPressed()) {
                    this.mDidInvalidateForPressedState = true;
                    setCellLayoutPressedOrFocusedIcon();
                } else {
                    this.mDidInvalidateForPressedState = false;
                }
                this.mLongPressHelper.postCheckForLongPress();
                break;
            case 1:
            case 3:
                if (!isPressed()) {
                    this.mPressedOrFocusedBackground = null;
                }
                this.mLongPressHelper.cancelLongPress();
                break;
        }
        return result;
    }

    /* access modifiers changed from: package-private */
    public void setStayPressed(boolean stayPressed) {
        this.mStayPressed = stayPressed;
        if (!stayPressed) {
            this.mPressedOrFocusedBackground = null;
        }
        setCellLayoutPressedOrFocusedIcon();
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: package-private */
    public void setCellLayoutPressedOrFocusedIcon() {
        ShortcutAndWidgetContainer parent;
        if ((getParent() instanceof ShortcutAndWidgetContainer) && (parent = (ShortcutAndWidgetContainer) getParent()) != null) {
            CellLayout layout = (CellLayout) parent.getParent();
            if (this.mPressedOrFocusedBackground == null) {
                this = null;
            }
            layout.setPressedOrFocusedIcon(this);
        }
    }

    /* access modifiers changed from: package-private */
    public void clearPressedOrFocusedBackground() {
        this.mPressedOrFocusedBackground = null;
        setCellLayoutPressedOrFocusedIcon();
    }

    /* access modifiers changed from: package-private */
    public Bitmap getPressedOrFocusedBackground() {
        return this.mPressedOrFocusedBackground;
    }

    /* access modifiers changed from: package-private */
    public int getPressedOrFocusedBackgroundPadding() {
        return HolographicOutlineHelper.MAX_OUTER_BLUR_RADIUS / 2;
    }

    public void draw(Canvas canvas) {
        Drawable background = this.mBackground;
        if (background != null) {
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            if (this.mBackgroundSizeChanged) {
                background.setBounds(0, 0, getRight() - getLeft(), getBottom() - getTop());
                this.mBackgroundSizeChanged = false;
            }
            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } else {
                canvas.translate((float) scrollX, (float) scrollY);
                background.draw(canvas);
                canvas.translate((float) (-scrollX), (float) (-scrollY));
            }
        }
        if (getCurrentTextColor() == getResources().getColor(17170445)) {
            getPaint().clearShadowLayer();
            super.draw(canvas);
            return;
        }
        super.draw(canvas);
        canvas.save(2);
        canvas.clipRect((float) getScrollX(), (float) (getScrollY() + getExtendedPaddingTop()), (float) (getScrollX() + getWidth()), (float) (getScrollY() + getHeight()), Region.Op.INTERSECT);
        super.draw(canvas);
        canvas.restore();
        Log.i("setShadowLayer", "*****setShadowLayer****");
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mBackground != null) {
            this.mBackground.setCallback(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mBackground != null) {
            this.mBackground.setCallback((Drawable.Callback) null);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onSetAlpha(int alpha) {
        if (this.mPrevAlpha == alpha) {
            return true;
        }
        this.mPrevAlpha = alpha;
        super.onSetAlpha(alpha);
        return true;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }
}
