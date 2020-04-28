package com.android.launcher3;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import com.android.launcher3.util.SystemUiController;

public class LauncherRootView extends InsettableFrameLayout {
    private View mAlignedView;
    @ViewDebug.ExportedProperty(category = "launcher")
    private final Rect mConsumedInsets = new Rect();
    private final Launcher mLauncher;
    private final Paint mOpaquePaint = new Paint(1);
    private WindowStateListener mWindowStateListener;

    public interface WindowStateListener {
        void onWindowFocusChanged(boolean z);

        void onWindowVisibilityChanged(int i);
    }

    public LauncherRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOpaquePaint.setColor(-16777216);
        this.mOpaquePaint.setStyle(Paint.Style.FILL);
        this.mLauncher = Launcher.getLauncher(context);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        if (getChildCount() > 0) {
            this.mAlignedView = getChildAt(0);
        }
        super.onFinishInflate();
    }

    /* access modifiers changed from: protected */
    @TargetApi(23)
    public boolean fitSystemWindows(Rect insets) {
        this.mConsumedInsets.setEmpty();
        boolean drawInsetBar = false;
        int i = 0;
        if (this.mLauncher.isInMultiWindowModeCompat() && (insets.left > 0 || insets.right > 0 || insets.bottom > 0)) {
            this.mConsumedInsets.left = insets.left;
            this.mConsumedInsets.right = insets.right;
            this.mConsumedInsets.bottom = insets.bottom;
            insets = new Rect(0, insets.top, 0, 0);
            drawInsetBar = true;
        } else if ((insets.right > 0 || insets.left > 0) && (!Utilities.ATLEAST_MARSHMALLOW || ((ActivityManager) getContext().getSystemService(ActivityManager.class)).isLowRamDevice())) {
            this.mConsumedInsets.left = insets.left;
            this.mConsumedInsets.right = insets.right;
            insets = new Rect(0, insets.top, 0, insets.bottom);
            drawInsetBar = true;
        }
        SystemUiController systemUiController = this.mLauncher.getSystemUiController();
        if (drawInsetBar) {
            i = 2;
        }
        systemUiController.updateUiState(3, i);
        this.mLauncher.getDeviceProfile().updateInsets(insets);
        boolean resetState = !insets.equals(this.mInsets);
        setInsets(insets);
        if (this.mAlignedView != null) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.mAlignedView.getLayoutParams();
            if (!(lp.leftMargin == this.mConsumedInsets.left && lp.rightMargin == this.mConsumedInsets.right && lp.bottomMargin == this.mConsumedInsets.bottom)) {
                lp.leftMargin = this.mConsumedInsets.left;
                lp.rightMargin = this.mConsumedInsets.right;
                lp.topMargin = this.mConsumedInsets.top;
                lp.bottomMargin = this.mConsumedInsets.bottom;
                this.mAlignedView.setLayoutParams(lp);
            }
        }
        if (resetState) {
            this.mLauncher.getStateManager().reapplyState(true);
        }
        return true;
    }

    public void setInsets(Rect insets) {
        if (!insets.equals(this.mInsets)) {
            super.setInsets(insets);
        }
    }

    public void dispatchInsets() {
        this.mLauncher.getDeviceProfile().updateInsets(this.mInsets);
        super.setInsets(this.mInsets);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mConsumedInsets.right > 0) {
            int width = getWidth();
            canvas.drawRect((float) (width - this.mConsumedInsets.right), 0.0f, (float) width, (float) getHeight(), this.mOpaquePaint);
        }
        if (this.mConsumedInsets.left > 0) {
            canvas.drawRect(0.0f, 0.0f, (float) this.mConsumedInsets.left, (float) getHeight(), this.mOpaquePaint);
        }
        if (this.mConsumedInsets.bottom > 0) {
            int height = getHeight();
            canvas.drawRect(0.0f, (float) (height - this.mConsumedInsets.bottom), (float) getWidth(), (float) height, this.mOpaquePaint);
        }
    }

    public void setWindowStateListener(WindowStateListener listener) {
        this.mWindowStateListener = listener;
    }

    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (this.mWindowStateListener != null) {
            this.mWindowStateListener.onWindowFocusChanged(hasWindowFocus);
        }
    }

    /* access modifiers changed from: protected */
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (this.mWindowStateListener != null) {
            this.mWindowStateListener.onWindowVisibilityChanged(visibility);
        }
    }
}
