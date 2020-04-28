package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.FloatArrayEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.android.launcher3.DropTarget;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.Themes;

public abstract class ButtonDropTarget extends TextView implements DropTarget, DragController.DragListener, View.OnClickListener {
    private static final int DRAG_VIEW_DROP_DURATION = 285;
    public static final int TOOLTIP_DEFAULT = 0;
    public static final int TOOLTIP_LEFT = 1;
    public static final int TOOLTIP_RIGHT = 2;
    private static final int[] sTempCords = new int[2];
    private boolean mAccessibleDrag;
    protected boolean mActive;
    private int mBottomDragPadding;
    private AnimatorSet mCurrentColorAnim;
    ColorMatrix mCurrentFilter;
    private final int mDragDistanceThreshold;
    protected Drawable mDrawable;
    protected DropTargetBar mDropTargetBar;
    ColorMatrix mDstFilter;
    protected int mHoverColor;
    protected final Launcher mLauncher;
    protected ColorStateList mOriginalTextColor;
    ColorMatrix mSrcFilter;
    protected CharSequence mText;
    private boolean mTextVisible;
    private PopupWindow mToolTip;
    private int mToolTipLocation;

    public abstract void completeDrop(DropTarget.DragObject dragObject);

    public abstract int getAccessibilityAction();

    public abstract LauncherLogProto.Target getDropTargetForLogging();

    public abstract void onAccessibilityDrop(View view, ItemInfo itemInfo);

    public abstract boolean supportsAccessibilityDrop(ItemInfo itemInfo, View view);

    /* access modifiers changed from: protected */
    public abstract boolean supportsDrop(ItemInfo itemInfo);

    public ButtonDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHoverColor = 0;
        this.mTextVisible = true;
        this.mLauncher = Launcher.getLauncher(context);
        Resources resources = getResources();
        this.mBottomDragPadding = resources.getDimensionPixelSize(R.dimen.drop_target_drag_padding);
        this.mDragDistanceThreshold = resources.getDimensionPixelSize(R.dimen.drag_distanceThreshold);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mText = getText();
        this.mOriginalTextColor = getTextColors();
        setContentDescription(this.mText);
    }

    /* access modifiers changed from: protected */
    public void updateText(int resId) {
        setText(resId);
        this.mText = getText();
        setContentDescription(this.mText);
    }

    /* access modifiers changed from: protected */
    public void setDrawable(int resId) {
        if (this.mTextVisible) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
            this.mDrawable = getCompoundDrawablesRelative()[0];
            return;
        }
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, resId, 0, 0);
        this.mDrawable = getCompoundDrawablesRelative()[1];
    }

    public void setDropTargetBar(DropTargetBar dropTargetBar) {
        this.mDropTargetBar = dropTargetBar;
    }

    private void hideTooltip() {
        if (this.mToolTip != null) {
            this.mToolTip.dismiss();
            this.mToolTip = null;
        }
    }

    public final void onDragEnter(DropTarget.DragObject d) {
        if (!d.accessibleDrag && !this.mTextVisible) {
            hideTooltip();
            TextView message = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.drop_target_tool_tip, (ViewGroup) null);
            message.setText(this.mText);
            this.mToolTip = new PopupWindow(message, -2, -2);
            int x = 0;
            int y = 0;
            if (this.mToolTipLocation != 0) {
                y = -getMeasuredHeight();
                message.measure(0, 0);
                if (this.mToolTipLocation == 1) {
                    x = (-getMeasuredWidth()) - (message.getMeasuredWidth() / 2);
                } else {
                    x = (getMeasuredWidth() / 2) + (message.getMeasuredWidth() / 2);
                }
            }
            this.mToolTip.showAsDropDown(this, x, y);
        }
        d.dragView.setColor(this.mHoverColor);
        animateTextColor(this.mHoverColor);
        if (d.stateAnnouncer != null) {
            d.stateAnnouncer.cancel();
        }
        sendAccessibilityEvent(4);
    }

    public void onDragOver(DropTarget.DragObject d) {
    }

    /* access modifiers changed from: protected */
    public void resetHoverColor() {
        animateTextColor(this.mOriginalTextColor.getDefaultColor());
    }

    private void animateTextColor(int targetColor) {
        if (this.mCurrentColorAnim != null) {
            this.mCurrentColorAnim.cancel();
        }
        this.mCurrentColorAnim = new AnimatorSet();
        this.mCurrentColorAnim.setDuration(120);
        if (this.mSrcFilter == null) {
            this.mSrcFilter = new ColorMatrix();
            this.mDstFilter = new ColorMatrix();
            this.mCurrentFilter = new ColorMatrix();
        }
        int defaultTextColor = this.mOriginalTextColor.getDefaultColor();
        Themes.setColorChangeOnMatrix(defaultTextColor, getTextColor(), this.mSrcFilter);
        Themes.setColorChangeOnMatrix(defaultTextColor, targetColor, this.mDstFilter);
        ValueAnimator anim1 = ValueAnimator.ofObject(new FloatArrayEvaluator(this.mCurrentFilter.getArray()), new Object[]{this.mSrcFilter.getArray(), this.mDstFilter.getArray()});
        anim1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ButtonDropTarget.lambda$animateTextColor$0(ButtonDropTarget.this, valueAnimator);
            }
        });
        this.mCurrentColorAnim.play(anim1);
        this.mCurrentColorAnim.play(ObjectAnimator.ofArgb(this, "textColor", new int[]{targetColor}));
        this.mCurrentColorAnim.start();
    }

    public static /* synthetic */ void lambda$animateTextColor$0(ButtonDropTarget buttonDropTarget, ValueAnimator anim) {
        buttonDropTarget.mDrawable.setColorFilter(new ColorMatrixColorFilter(buttonDropTarget.mCurrentFilter));
        buttonDropTarget.invalidate();
    }

    public final void onDragExit(DropTarget.DragObject d) {
        hideTooltip();
        if (!d.dragComplete) {
            d.dragView.setColor(0);
            resetHoverColor();
            return;
        }
        d.dragView.setColor(this.mHoverColor);
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        this.mActive = supportsDrop(dragObject.dragInfo);
        ButtonDropTarget buttonDropTarget = null;
        this.mDrawable.setColorFilter((ColorFilter) null);
        if (this.mCurrentColorAnim != null) {
            this.mCurrentColorAnim.cancel();
            this.mCurrentColorAnim = null;
        }
        setTextColor(this.mOriginalTextColor);
        setVisibility(this.mActive ? 0 : 8);
        this.mAccessibleDrag = options.isAccessibleDrag;
        if (this.mAccessibleDrag) {
            buttonDropTarget = this;
        }
        setOnClickListener(buttonDropTarget);
    }

    public final boolean acceptDrop(DropTarget.DragObject dragObject) {
        return supportsDrop(dragObject.dragInfo);
    }

    public boolean isDropEnabled() {
        return this.mActive && (this.mAccessibleDrag || this.mLauncher.getDragController().getDistanceDragged() >= ((float) this.mDragDistanceThreshold));
    }

    public void onDragEnd() {
        this.mActive = false;
        setOnClickListener((View.OnClickListener) null);
    }

    public void onDrop(DropTarget.DragObject d, DragOptions options) {
        DropTarget.DragObject dragObject = d;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(dragObject.dragView, from);
        Rect to = getIconRect(d);
        float scale = ((float) to.width()) / ((float) from.width());
        this.mDropTargetBar.deferOnDragEnd();
        Rect rect = from;
        dragLayer.animateView(dragObject.dragView, from, to, scale, 1.0f, 1.0f, 0.1f, 0.1f, DRAG_VIEW_DROP_DURATION, Interpolators.DEACCEL_2, Interpolators.LINEAR, new Runnable(dragObject) {
            private final /* synthetic */ DropTarget.DragObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ButtonDropTarget.lambda$onDrop$1(ButtonDropTarget.this, this.f$1);
            }
        }, 0, (View) null);
    }

    public static /* synthetic */ void lambda$onDrop$1(ButtonDropTarget buttonDropTarget, DropTarget.DragObject d) {
        buttonDropTarget.completeDrop(d);
        buttonDropTarget.mDropTargetBar.onDragEnd();
        buttonDropTarget.mLauncher.getStateManager().goToState(LauncherState.NORMAL);
    }

    public void prepareAccessibilityDrop() {
    }

    public void getHitRectRelativeToDragLayer(Rect outRect) {
        super.getHitRect(outRect);
        outRect.bottom += this.mBottomDragPadding;
        int[] iArr = sTempCords;
        sTempCords[1] = 0;
        iArr[0] = 0;
        this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(this, sTempCords);
        outRect.offsetTo(sTempCords[0], sTempCords[1]);
    }

    public Rect getIconRect(DropTarget.DragObject dragObject) {
        int left;
        int right;
        int viewWidth = dragObject.dragView.getMeasuredWidth();
        int viewHeight = dragObject.dragView.getMeasuredHeight();
        int drawableWidth = this.mDrawable.getIntrinsicWidth();
        int drawableHeight = this.mDrawable.getIntrinsicHeight();
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect to = new Rect();
        dragLayer.getViewRectRelativeToSelf(this, to);
        int width = drawableWidth;
        int height = drawableHeight;
        if (Utilities.isRtl(getResources())) {
            right = to.right - getPaddingRight();
            left = right - width;
        } else {
            left = getPaddingLeft() + to.left;
            right = left + width;
        }
        int top = to.top + ((getMeasuredHeight() - height) / 2);
        to.set(left, top, right, top + height);
        to.offset((-(viewWidth - width)) / 2, (-(viewHeight - height)) / 2);
        return to;
    }

    public void onClick(View v) {
        this.mLauncher.getAccessibilityDelegate().handleAccessibleDrop(this, (Rect) null, (String) null);
    }

    public int getTextColor() {
        return getTextColors().getDefaultColor();
    }

    public void setTextVisible(boolean isVisible) {
        String newText = isVisible ? this.mText : "";
        if (this.mTextVisible != isVisible || !TextUtils.equals(newText, getText())) {
            this.mTextVisible = isVisible;
            setText(newText);
            if (this.mTextVisible) {
                setCompoundDrawablesRelativeWithIntrinsicBounds(this.mDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
            } else {
                setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, this.mDrawable, (Drawable) null, (Drawable) null);
            }
        }
    }

    public void setToolTipLocation(int location) {
        this.mToolTipLocation = location;
        hideTooltip();
    }

    public boolean isTextTruncated(int availableWidth) {
        return !this.mText.equals(TextUtils.ellipsize(this.mText, getPaint(), (float) (availableWidth - (((getPaddingLeft() + getPaddingRight()) + this.mDrawable.getIntrinsicWidth()) + getCompoundDrawablePadding())), TextUtils.TruncateAt.END));
    }
}
