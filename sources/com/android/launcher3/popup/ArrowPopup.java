package com.android.launcher3.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.CornerPathEffect;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.RevealOutlineAnimation;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.graphics.TriangleShape;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import java.util.ArrayList;
import java.util.Collections;

public abstract class ArrowPopup extends AbstractFloatingView {
    private final int mArrayOffset;
    private final View mArrow;
    protected boolean mDeferContainerRemoval;
    private final Rect mEndRect;
    private int mGravity;
    protected final LayoutInflater mInflater;
    protected boolean mIsAboveIcon;
    protected boolean mIsLeftAligned;
    protected final boolean mIsRtl;
    protected final Launcher mLauncher;
    protected Animator mOpenCloseAnimator;
    /* access modifiers changed from: private */
    public final float mOutlineRadius;
    private final Rect mStartRect;
    private final Rect mTempRect;

    /* access modifiers changed from: protected */
    public abstract void getTargetObjectLocation(Rect rect);

    public ArrowPopup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTempRect = new Rect();
        this.mStartRect = new Rect();
        this.mEndRect = new Rect();
        this.mInflater = LayoutInflater.from(context);
        this.mOutlineRadius = getResources().getDimension(R.dimen.bg_round_rect_radius);
        this.mLauncher = Launcher.getLauncher(context);
        this.mIsRtl = Utilities.isRtl(getResources());
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() {
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), ArrowPopup.this.mOutlineRadius);
            }
        });
        Resources resources = getResources();
        int arrowWidth = resources.getDimensionPixelSize(R.dimen.popup_arrow_width);
        int arrowHeight = resources.getDimensionPixelSize(R.dimen.popup_arrow_height);
        this.mArrow = new View(context);
        this.mArrow.setLayoutParams(new BaseDragLayer.LayoutParams(arrowWidth, arrowHeight));
        this.mArrayOffset = resources.getDimensionPixelSize(R.dimen.popup_arrow_vertical_offset);
    }

    public ArrowPopup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowPopup(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        if (animate) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    public <T extends View> T inflateAndAdd(int resId, ViewGroup container) {
        View view = this.mInflater.inflate(resId, container, false);
        container.addView(view);
        return view;
    }

    /* access modifiers changed from: protected */
    public void onInflationComplete(boolean isReversed) {
    }

    /* access modifiers changed from: protected */
    public void reorderAndShow(int viewsToFlip) {
        setVisibility(4);
        this.mIsOpen = true;
        this.mLauncher.getDragLayer().addView(this);
        orientAboutObject();
        boolean reverseOrder = this.mIsAboveIcon;
        if (reverseOrder) {
            int count = getChildCount();
            ArrayList<View> allViews = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                if (i == viewsToFlip) {
                    Collections.reverse(allViews);
                }
                allViews.add(getChildAt(i));
            }
            Collections.reverse(allViews);
            removeAllViews();
            for (int i2 = 0; i2 < count; i2++) {
                addView(allViews.get(i2));
            }
            orientAboutObject();
        }
        onInflationComplete(reverseOrder);
        Resources res = getResources();
        int arrowCenterOffset = res.getDimensionPixelSize(isAlignedWithStart() ? R.dimen.popup_arrow_horizontal_center_start : R.dimen.popup_arrow_horizontal_center_end);
        int halfArrowWidth = res.getDimensionPixelSize(R.dimen.popup_arrow_width) / 2;
        this.mLauncher.getDragLayer().addView(this.mArrow);
        BaseDragLayer.LayoutParams arrowLp = (BaseDragLayer.LayoutParams) this.mArrow.getLayoutParams();
        if (this.mIsLeftAligned) {
            this.mArrow.setX((getX() + ((float) arrowCenterOffset)) - ((float) halfArrowWidth));
        } else {
            this.mArrow.setX(((getX() + ((float) getMeasuredWidth())) - ((float) arrowCenterOffset)) - ((float) halfArrowWidth));
        }
        if (Gravity.isVertical(this.mGravity)) {
            this.mArrow.setVisibility(4);
        } else {
            ShapeDrawable arrowDrawable = new ShapeDrawable(TriangleShape.create((float) arrowLp.width, (float) arrowLp.height, true ^ this.mIsAboveIcon));
            Paint arrowPaint = arrowDrawable.getPaint();
            arrowPaint.setColor(Themes.getAttrColor(this.mLauncher, R.attr.popupColorPrimary));
            arrowPaint.setPathEffect(new CornerPathEffect((float) getResources().getDimensionPixelSize(R.dimen.popup_arrow_corner_radius)));
            this.mArrow.setBackground(arrowDrawable);
            this.mArrow.setElevation(getElevation());
        }
        this.mArrow.setPivotX((float) (arrowLp.width / 2));
        this.mArrow.setPivotY(this.mIsAboveIcon ? 0.0f : (float) arrowLp.height);
        animateOpen();
    }

    /* access modifiers changed from: protected */
    public boolean isAlignedWithStart() {
        return (this.mIsLeftAligned && !this.mIsRtl) || (!this.mIsLeftAligned && this.mIsRtl);
    }

    /* access modifiers changed from: protected */
    public void orientAboutObject() {
        int xOffset;
        boolean z;
        int x;
        int x2;
        measure(0, 0);
        int width = getMeasuredWidth();
        int extraVerticalSpace = this.mArrow.getLayoutParams().height + this.mArrayOffset + getResources().getDimensionPixelSize(R.dimen.popup_vertical_padding);
        int height = getMeasuredHeight() + extraVerticalSpace;
        getTargetObjectLocation(this.mTempRect);
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect insets = dragLayer.getInsets();
        int leftAlignedX = this.mTempRect.left;
        int rightAlignedX = this.mTempRect.right - width;
        int x3 = leftAlignedX;
        boolean canBeLeftAligned = (leftAlignedX + width) + insets.left < dragLayer.getRight() - insets.right;
        boolean canBeRightAligned = rightAlignedX > dragLayer.getLeft() + insets.left;
        if (!canBeLeftAligned || (this.mIsRtl && canBeRightAligned)) {
            x3 = rightAlignedX;
        }
        this.mIsLeftAligned = x3 == leftAlignedX;
        int iconWidth = this.mTempRect.width();
        Resources resources = getResources();
        if (isAlignedWithStart()) {
            xOffset = ((iconWidth / 2) - (resources.getDimensionPixelSize(R.dimen.deep_shortcut_icon_size) / 2)) - resources.getDimensionPixelSize(R.dimen.popup_padding_start);
        } else {
            xOffset = ((iconWidth / 2) - (resources.getDimensionPixelSize(R.dimen.deep_shortcut_drag_handle_size) / 2)) - resources.getDimensionPixelSize(R.dimen.popup_padding_end);
        }
        int xOffset2 = xOffset;
        int x4 = x3 + (this.mIsLeftAligned ? xOffset2 : -xOffset2);
        int iconHeight = this.mTempRect.height();
        int y = this.mTempRect.top - height;
        boolean z2 = canBeLeftAligned;
        this.mIsAboveIcon = y > dragLayer.getTop() + insets.top;
        if (!this.mIsAboveIcon) {
            y = this.mTempRect.top + iconHeight + extraVerticalSpace;
        }
        int x5 = x4 - insets.left;
        int y2 = y - insets.top;
        this.mGravity = 0;
        int i = extraVerticalSpace;
        if (y2 + height > dragLayer.getBottom() - insets.bottom) {
            this.mGravity = 16;
            int rightSide = (leftAlignedX + iconWidth) - insets.left;
            int i2 = height;
            int leftSide = (rightAlignedX - iconWidth) - insets.left;
            if (!this.mIsRtl) {
                int i3 = width;
                if (rightSide + width < dragLayer.getRight()) {
                    x = rightSide;
                    z = true;
                    this.mIsLeftAligned = true;
                } else {
                    x2 = leftSide;
                    this.mIsLeftAligned = false;
                    x5 = x2;
                    z = true;
                    this.mIsAboveIcon = z;
                }
            } else {
                if (leftSide > dragLayer.getLeft()) {
                    x2 = leftSide;
                    this.mIsLeftAligned = false;
                    x5 = x2;
                    z = true;
                    this.mIsAboveIcon = z;
                } else {
                    x = rightSide;
                    z = true;
                    this.mIsLeftAligned = true;
                }
            }
            x5 = x;
            this.mIsAboveIcon = z;
        } else {
            int i4 = height;
        }
        setX((float) x5);
        if (!Gravity.isVertical(this.mGravity)) {
            BaseDragLayer.LayoutParams lp = (BaseDragLayer.LayoutParams) getLayoutParams();
            BaseDragLayer.LayoutParams arrowLp = (BaseDragLayer.LayoutParams) this.mArrow.getLayoutParams();
            if (this.mIsAboveIcon) {
                lp.gravity = 80;
                arrowLp.gravity = 80;
                lp.bottomMargin = ((this.mLauncher.getDragLayer().getHeight() - y2) - getMeasuredHeight()) - insets.top;
                arrowLp.bottomMargin = ((lp.bottomMargin - arrowLp.height) - this.mArrayOffset) - insets.bottom;
                return;
            }
            lp.gravity = 48;
            arrowLp.gravity = 48;
            lp.topMargin = insets.top + y2;
            arrowLp.topMargin = ((lp.topMargin - insets.top) - arrowLp.height) - this.mArrayOffset;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (getTranslationX() + ((float) l) < 0.0f || getTranslationX() + ((float) r) > ((float) dragLayer.getWidth())) {
            this.mGravity |= 1;
        }
        if (Gravity.isHorizontal(this.mGravity)) {
            setX((float) ((dragLayer.getWidth() / 2) - (getMeasuredWidth() / 2)));
            this.mArrow.setVisibility(4);
        }
        if (Gravity.isVertical(this.mGravity)) {
            setY((float) ((dragLayer.getHeight() / 2) - (getMeasuredHeight() / 2)));
        }
    }

    private void animateOpen() {
        setVisibility(0);
        AnimatorSet openAnim = LauncherAnimUtils.createAnimatorSet();
        Resources res = getResources();
        long revealDuration = (long) res.getInteger(R.integer.config_popupOpenCloseDuration);
        TimeInterpolator revealInterpolator = new AccelerateDecelerateInterpolator();
        ValueAnimator revealAnim = createOpenCloseOutlineProvider().createRevealAnimator(this, false);
        revealAnim.setDuration(revealDuration);
        revealAnim.setInterpolator(revealInterpolator);
        Animator fadeIn = ObjectAnimator.ofFloat(this, ALPHA, new float[]{0.0f, 1.0f});
        fadeIn.setDuration(revealDuration);
        fadeIn.setInterpolator(revealInterpolator);
        openAnim.play(fadeIn);
        this.mArrow.setScaleX(0.0f);
        this.mArrow.setScaleY(0.0f);
        Animator arrowScale = ObjectAnimator.ofFloat(this.mArrow, LauncherAnimUtils.SCALE_PROPERTY, new float[]{1.0f}).setDuration((long) res.getInteger(R.integer.config_popupArrowOpenDuration));
        openAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ArrowPopup.this.announceAccessibilityChanges();
                ArrowPopup.this.mOpenCloseAnimator = null;
            }
        });
        this.mOpenCloseAnimator = openAnim;
        openAnim.playSequentially(new Animator[]{revealAnim, arrowScale});
        openAnim.start();
    }

    /* access modifiers changed from: protected */
    public void animateClose() {
        if (this.mIsOpen) {
            this.mEndRect.setEmpty();
            if (getOutlineProvider() instanceof RevealOutlineAnimation) {
                ((RevealOutlineAnimation) getOutlineProvider()).getOutline(this.mEndRect);
            }
            if (this.mOpenCloseAnimator != null) {
                this.mOpenCloseAnimator.cancel();
            }
            this.mIsOpen = false;
            AnimatorSet closeAnim = LauncherAnimUtils.createAnimatorSet();
            closeAnim.play(ObjectAnimator.ofFloat(this.mArrow, LauncherAnimUtils.SCALE_PROPERTY, new float[]{0.0f}));
            closeAnim.play(ObjectAnimator.ofFloat(this.mArrow, ALPHA, new float[]{0.0f}));
            Resources res = getResources();
            TimeInterpolator revealInterpolator = new AccelerateDecelerateInterpolator();
            ValueAnimator revealAnim = createOpenCloseOutlineProvider().createRevealAnimator(this, true);
            revealAnim.setInterpolator(revealInterpolator);
            closeAnim.play(revealAnim);
            Animator fadeOut = ObjectAnimator.ofFloat(this, ALPHA, new float[]{0.0f});
            fadeOut.setInterpolator(revealInterpolator);
            closeAnim.play(fadeOut);
            onCreateCloseAnimation(closeAnim);
            closeAnim.setDuration((long) res.getInteger(R.integer.config_popupOpenCloseDuration));
            closeAnim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ArrowPopup.this.mOpenCloseAnimator = null;
                    if (ArrowPopup.this.mDeferContainerRemoval) {
                        ArrowPopup.this.setVisibility(4);
                    } else {
                        ArrowPopup.this.closeComplete();
                    }
                }
            });
            this.mOpenCloseAnimator = closeAnim;
            closeAnim.start();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreateCloseAnimation(AnimatorSet anim) {
    }

    private RoundedRectRevealOutlineProvider createOpenCloseOutlineProvider() {
        int arrowCenterX = getResources().getDimensionPixelSize(this.mIsLeftAligned ^ this.mIsRtl ? R.dimen.popup_arrow_horizontal_center_start : R.dimen.popup_arrow_horizontal_center_end);
        if (!this.mIsLeftAligned) {
            arrowCenterX = getMeasuredWidth() - arrowCenterX;
        }
        int arrowCenterY = this.mIsAboveIcon ? getMeasuredHeight() : 0;
        this.mStartRect.set(arrowCenterX, arrowCenterY, arrowCenterX, arrowCenterY);
        if (this.mEndRect.isEmpty()) {
            this.mEndRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        }
        return new RoundedRectRevealOutlineProvider(this.mOutlineRadius, this.mOutlineRadius, this.mStartRect, this.mEndRect);
    }

    /* access modifiers changed from: protected */
    public void closeComplete() {
        if (this.mOpenCloseAnimator != null) {
            this.mOpenCloseAnimator.cancel();
            this.mOpenCloseAnimator = null;
        }
        this.mIsOpen = false;
        this.mDeferContainerRemoval = false;
        this.mLauncher.getDragLayer().removeView(this);
        this.mLauncher.getDragLayer().removeView(this.mArrow);
    }
}
