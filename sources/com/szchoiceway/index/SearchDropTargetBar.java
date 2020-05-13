package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import com.szchoiceway.index.DragController;

public class SearchDropTargetBar extends FrameLayout implements DragController.DragListener {
    private static final AccelerateInterpolator sAccelerateInterpolator = new AccelerateInterpolator();
    private static final int sTransitionInDuration = 200;
    private static final int sTransitionOutDuration = 175;
    private int mBarHeight;
    private boolean mDeferOnDragEnd;
    private ButtonDropTarget mDeleteDropTarget;
    private View mDropTargetBar;
    private ObjectAnimator mDropTargetBarAnim;
    private boolean mEnableDropDownDropTargets;
    private ButtonDropTarget mInfoDropTarget;
    private boolean mIsSearchBarHidden;
    private Drawable mPreviousBackground;

    public SearchDropTargetBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchDropTargetBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mDeferOnDragEnd = false;
    }

    public void setup(Launcher launcher, DragController dragController) {
        dragController.addDragListener(this);
        dragController.addDragListener(this.mInfoDropTarget);
        dragController.addDragListener(this.mDeleteDropTarget);
        dragController.addDropTarget(this.mInfoDropTarget);
        dragController.addDropTarget(this.mDeleteDropTarget);
        dragController.setFlingToDeleteDropTarget(this.mDeleteDropTarget);
        this.mInfoDropTarget.setLauncher(launcher);
        this.mDeleteDropTarget.setLauncher(launcher);
    }

    private void prepareStartAnimation(View v) {
        if (v != null) {
            v.setLayerType(2, (Paint) null);
        }
    }

    private void setupAnimation(ObjectAnimator anim, final View v) {
        anim.setInterpolator(sAccelerateInterpolator);
        anim.setDuration(200);
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (v != null) {
                    v.setLayerType(0, (Paint) null);
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mDropTargetBar = findViewById(R.id.drag_target_bar);
        if (this.mDropTargetBar != null) {
            this.mInfoDropTarget = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.info_target_text);
            this.mDeleteDropTarget = (ButtonDropTarget) this.mDropTargetBar.findViewById(R.id.delete_target_text);
        }
        this.mBarHeight = getResources().getDimensionPixelSize(R.dimen.qsb_bar_height);
        this.mInfoDropTarget.setSearchDropTargetBar(this);
        this.mDeleteDropTarget.setSearchDropTargetBar(this);
        this.mEnableDropDownDropTargets = getResources().getBoolean(R.bool.config_useDropTargetDownTransition);
        if (this.mEnableDropDownDropTargets) {
            this.mDropTargetBar.setTranslationY((float) (-this.mBarHeight));
            this.mDropTargetBarAnim = ObjectAnimator.ofFloat(this.mDropTargetBar, "translationY", new float[]{(float) (-this.mBarHeight), 0.0f});
        } else {
            this.mDropTargetBar.setAlpha(0.0f);
            this.mDropTargetBarAnim = ObjectAnimator.ofFloat(this.mDropTargetBar, "alpha", new float[]{0.0f, 1.0f});
        }
        setupAnimation(this.mDropTargetBarAnim, this.mDropTargetBar);
    }

    public void finishAnimations() {
        prepareStartAnimation(this.mDropTargetBar);
        this.mDropTargetBarAnim.reverse();
    }

    public void showSearchBar(boolean animated) {
        if (this.mIsSearchBarHidden) {
            this.mIsSearchBarHidden = false;
        }
    }

    public void hideSearchBar(boolean animated) {
        if (!this.mIsSearchBarHidden) {
            this.mIsSearchBarHidden = true;
        }
    }

    public int getTransitionInDuration() {
        return 200;
    }

    public int getTransitionOutDuration() {
        return sTransitionOutDuration;
    }

    public void onDragStart(DragSource source, Object info, int dragAction) {
        prepareStartAnimation(this.mDropTargetBar);
        this.mDropTargetBarAnim.start();
        if (!this.mIsSearchBarHidden) {
        }
    }

    public void deferOnDragEnd() {
        this.mDeferOnDragEnd = true;
    }

    public void onDragEnd() {
        if (!this.mDeferOnDragEnd) {
            prepareStartAnimation(this.mDropTargetBar);
            this.mDropTargetBarAnim.reverse();
            if (!this.mIsSearchBarHidden) {
            }
            return;
        }
        this.mDeferOnDragEnd = false;
    }

    public void onSearchPackagesChanged(boolean searchVisible, boolean voiceVisible) {
    }

    public Rect getSearchBarBounds() {
        return null;
    }
}
