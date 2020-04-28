package com.android.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.widget.FrameLayout;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.userevent.nano.LauncherLogProto;

public class Hotseat extends FrameLayout implements UserEventDispatcher.LogContainerProvider, Insettable {
    private CellLayout mContent;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mHasVerticalHotseat;
    private final Launcher mLauncher;

    public Hotseat(Context context) {
        this(context, (AttributeSet) null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLauncher = Launcher.getLauncher(context);
    }

    public CellLayout getLayout() {
        return this.mContent;
    }

    /* access modifiers changed from: package-private */
    public int getOrderInHotseat(int x, int y) {
        return this.mHasVerticalHotseat ? (this.mContent.getCountY() - y) - 1 : x;
    }

    /* access modifiers changed from: package-private */
    public int getCellXFromOrder(int rank) {
        if (this.mHasVerticalHotseat) {
            return 0;
        }
        return rank;
    }

    /* access modifiers changed from: package-private */
    public int getCellYFromOrder(int rank) {
        if (this.mHasVerticalHotseat) {
            return this.mContent.getCountY() - (rank + 1);
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (CellLayout) findViewById(R.id.layout);
    }

    /* access modifiers changed from: package-private */
    public void resetLayout(boolean hasVerticalHotseat) {
        this.mContent.removeAllViewsInLayout();
        this.mHasVerticalHotseat = hasVerticalHotseat;
        InvariantDeviceProfile idp = this.mLauncher.getDeviceProfile().inv;
        if (hasVerticalHotseat) {
            this.mContent.setGridSize(1, idp.numHotseatIcons);
        } else {
            this.mContent.setGridSize(idp.numHotseatIcons, 1);
        }
    }

    private /* synthetic */ void lambda$resetLayout$0(View v) {
        if (!this.mLauncher.isInState(LauncherState.ALL_APPS)) {
            this.mLauncher.getUserEventDispatcher().logActionOnControl(0, 1);
            this.mLauncher.getStateManager().goToState(LauncherState.ALL_APPS);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !this.mLauncher.getWorkspace().workspaceIconsCanBeDragged() && !this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag();
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        target.gridX = info.cellX;
        target.gridY = info.cellY;
        targetParent.containerType = 2;
    }

    public void setInsets(Rect insets) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) getLayoutParams();
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        if (grid.isVerticalBarLayout()) {
            lp.height = -1;
            if (grid.isSeascape()) {
                lp.gravity = 3;
                lp.width = grid.hotseatBarSizePx + insets.left;
            } else {
                lp.gravity = 5;
                lp.width = grid.hotseatBarSizePx + insets.right;
            }
        } else {
            lp.gravity = 80;
            lp.width = -1;
            lp.height = grid.hotseatBarSizePx + insets.bottom;
        }
        Rect padding = grid.getHotseatLayoutPadding();
        getLayout().setPadding(padding.left, padding.top, padding.right, padding.bottom);
        setLayoutParams(lp);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }
}
