package com.android.launcher3.views;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;

public class BottomUserEducationView extends AbstractSlideInView implements Insettable {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    private static final String KEY_SHOWED_BOTTOM_USER_EDUCATION = "showed_bottom_user_education";
    private View mCloseButton;
    private final Rect mInsets;

    public BottomUserEducationView(Context context, AttributeSet attr) {
        this(context, attr, 0);
    }

    public BottomUserEducationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInsets = new Rect();
        this.mContent = this;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCloseButton = findViewById(R.id.close_bottom_user_tip);
        this.mCloseButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                BottomUserEducationView.this.handleClose(true);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setTranslationShift(this.mTranslationShift);
        expandTouchAreaOfCloseButton();
    }

    public void logActionCommand(int command) {
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 32) != 0;
    }

    public void setInsets(Rect insets) {
        int leftInset = insets.left - this.mInsets.left;
        int rightInset = insets.right - this.mInsets.right;
        int bottomInset = insets.bottom - this.mInsets.bottom;
        this.mInsets.set(insets);
        setPadding(getPaddingLeft() + leftInset, getPaddingTop(), getPaddingRight() + rightInset, getPaddingBottom() + bottomInset);
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        handleClose(animate, 200);
        if (animate) {
            this.mLauncher.getSharedPrefs().edit().putBoolean(KEY_SHOWED_BOTTOM_USER_EDUCATION, true).apply();
            AccessibilityManagerCompat.sendCustomAccessibilityEvent(this, 32, getContext().getString(R.string.bottom_work_tab_user_education_closed));
        }
    }

    private void open(boolean animate) {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            if (animate) {
                this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
                this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
                this.mOpenCloseAnimator.start();
                return;
            }
            setTranslationShift(0.0f);
        }
    }

    public static void showIfNeeded(Launcher launcher) {
        if (!launcher.getSharedPrefs().getBoolean(KEY_SHOWED_BOTTOM_USER_EDUCATION, false)) {
            BottomUserEducationView bottomUserEducationView = (BottomUserEducationView) LayoutInflater.from(launcher).inflate(R.layout.work_tab_bottom_user_education_view, launcher.getDragLayer(), false);
            launcher.getDragLayer().addView(bottomUserEducationView);
            bottomUserEducationView.open(true);
        }
    }

    private void expandTouchAreaOfCloseButton() {
        Rect hitRect = new Rect();
        this.mCloseButton.getHitRect(hitRect);
        hitRect.left -= this.mCloseButton.getWidth();
        hitRect.top -= this.mCloseButton.getHeight();
        hitRect.right += this.mCloseButton.getWidth();
        hitRect.bottom += this.mCloseButton.getHeight();
        ((View) this.mCloseButton.getParent()).setTouchDelegate(new TouchDelegate(hitRect, this.mCloseButton));
    }
}
