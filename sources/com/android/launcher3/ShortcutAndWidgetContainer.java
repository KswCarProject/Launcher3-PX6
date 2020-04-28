package com.android.launcher3;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.widget.LauncherAppWidgetHostView;

public class ShortcutAndWidgetContainer extends ViewGroup {
    static final String TAG = "ShortcutAndWidgetContainer";
    private int mCellHeight;
    private int mCellWidth;
    private final int mContainerType;
    private int mCountX;
    private boolean mInvertIfRtl = false;
    private Launcher mLauncher;
    private final int[] mTmpCellXY = new int[2];
    private final WallpaperManager mWallpaperManager;

    public ShortcutAndWidgetContainer(Context context, int containerType) {
        super(context);
        this.mLauncher = Launcher.getLauncher(context);
        this.mWallpaperManager = WallpaperManager.getInstance(context);
        this.mContainerType = containerType;
    }

    public void setCellDimensions(int cellWidth, int cellHeight, int countX, int countY) {
        this.mCellWidth = cellWidth;
        this.mCellHeight = cellHeight;
        this.mCountX = countX;
    }

    public View getChildAt(int x, int y) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
            if (lp.cellX <= x && x < lp.cellX + lp.cellHSpan && lp.cellY <= y && y < lp.cellY + lp.cellVSpan) {
                return child;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                measureChild(child);
            }
        }
    }

    public void setupLp(View child) {
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        if (child instanceof LauncherAppWidgetHostView) {
            DeviceProfile profile = this.mLauncher.getDeviceProfile();
            lp.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, profile.appWidgetScale.x, profile.appWidgetScale.y);
            return;
        }
        lp.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX);
    }

    public void setInvertIfRtl(boolean invert) {
        this.mInvertIfRtl = invert;
    }

    public int getCellContentHeight() {
        return Math.min(getMeasuredHeight(), this.mLauncher.getDeviceProfile().getCellHeight(this.mContainerType));
    }

    public void measureChild(View child) {
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
        DeviceProfile profile = this.mLauncher.getDeviceProfile();
        if (child instanceof LauncherAppWidgetHostView) {
            lp.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX, profile.appWidgetScale.x, profile.appWidgetScale.y);
        } else {
            lp.setup(this.mCellWidth, this.mCellHeight, invertLayoutHorizontally(), this.mCountX);
            int cellPaddingY = (int) Math.max(0.0f, ((float) (lp.height - getCellContentHeight())) / 2.0f);
            int cellPaddingX = this.mContainerType == 0 ? profile.workspaceCellPaddingXPx : (int) (((float) profile.edgeMarginPx) / 2.0f);
            child.setPadding(cellPaddingX, cellPaddingY, cellPaddingX, 0);
        }
        child.measure(View.MeasureSpec.makeMeasureSpec(lp.width, 1073741824), View.MeasureSpec.makeMeasureSpec(lp.height, 1073741824));
    }

    public boolean invertLayoutHorizontally() {
        return this.mInvertIfRtl && Utilities.isRtl(getResources());
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) child.getLayoutParams();
                if (child instanceof LauncherAppWidgetHostView) {
                    LauncherAppWidgetHostView lahv = (LauncherAppWidgetHostView) child;
                    DeviceProfile profile = this.mLauncher.getDeviceProfile();
                    float scaleX = profile.appWidgetScale.x;
                    float scaleY = profile.appWidgetScale.y;
                    lahv.setScaleToFit(Math.min(scaleX, scaleY));
                    lahv.setTranslationForCentering((-(((float) lp.width) - (((float) lp.width) * scaleX))) / 2.0f, (-(((float) lp.height) - (((float) lp.height) * scaleY))) / 2.0f);
                }
                int childLeft = lp.x;
                int childTop = lp.y;
                child.layout(childLeft, childTop, lp.width + childLeft, lp.height + childTop);
                if (lp.dropped) {
                    lp.dropped = false;
                    int[] cellXY = this.mTmpCellXY;
                    getLocationOnScreen(cellXY);
                    this.mWallpaperManager.sendWallpaperCommand(getWindowToken(), "android.home.drop", cellXY[0] + childLeft + (lp.width / 2), cellXY[1] + childTop + (lp.height / 2), 0, (Bundle) null);
                }
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0 && getAlpha() == 0.0f) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (child != null) {
            Rect r = new Rect();
            child.getDrawingRect(r);
            requestRectangleOnScreen(r);
        }
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).cancelLongPress();
        }
    }
}
