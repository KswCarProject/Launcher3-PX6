package com.android.launcher3.allapps;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.pageindicators.PageIndicator;
import com.android.launcher3.util.Themes;

public class PersonalWorkSlidingTabStrip extends LinearLayout implements PageIndicator {
    private static final String KEY_SHOWED_PEEK_WORK_TAB = "showed_peek_work_tab";
    private static final int POSITION_PERSONAL = 0;
    private static final int POSITION_WORK = 1;
    private AllAppsContainerView mContainerView;
    private final Paint mDividerPaint;
    private int mIndicatorLeft = -1;
    private int mIndicatorRight = -1;
    private boolean mIsRtl;
    private int mLastActivePage = 0;
    private float mScrollOffset;
    private int mSelectedIndicatorHeight;
    private final Paint mSelectedIndicatorPaint;
    private int mSelectedPosition = 0;
    private final SharedPreferences mSharedPreferences;

    public PersonalWorkSlidingTabStrip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(0);
        setWillNotDraw(false);
        this.mSelectedIndicatorHeight = getResources().getDimensionPixelSize(R.dimen.all_apps_tabs_indicator_height);
        this.mSelectedIndicatorPaint = new Paint();
        this.mSelectedIndicatorPaint.setColor(Themes.getAttrColor(context, 16843829));
        this.mDividerPaint = new Paint();
        this.mDividerPaint.setColor(Themes.getAttrColor(context, 16843820));
        this.mDividerPaint.setStrokeWidth((float) getResources().getDimensionPixelSize(R.dimen.all_apps_divider_height));
        this.mSharedPreferences = Launcher.getLauncher(getContext()).getSharedPrefs();
        this.mIsRtl = Utilities.isRtl(getResources());
    }

    private void updateIndicatorPosition(float scrollOffset) {
        this.mScrollOffset = scrollOffset;
        updateIndicatorPosition();
    }

    private void updateTabTextColor(int pos) {
        this.mSelectedPosition = pos;
        int i = 0;
        while (i < getChildCount()) {
            ((Button) getChildAt(i)).setSelected(i == pos);
            i++;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        updateTabTextColor(this.mSelectedPosition);
        updateIndicatorPosition(this.mScrollOffset);
    }

    private void updateIndicatorPosition() {
        int left = -1;
        int right = -1;
        View leftTab = getLeftTab();
        if (leftTab != null) {
            left = (int) (((float) leftTab.getLeft()) + (((float) leftTab.getWidth()) * this.mScrollOffset));
            right = left + leftTab.getWidth();
        }
        setIndicatorPosition(left, right);
    }

    private View getLeftTab() {
        return getChildAt(this.mIsRtl ? 1 : 0);
    }

    private void setIndicatorPosition(int left, int right) {
        if (left != this.mIndicatorLeft || right != this.mIndicatorRight) {
            this.mIndicatorLeft = left;
            this.mIndicatorRight = right;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float y = ((float) getHeight()) - this.mDividerPaint.getStrokeWidth();
        canvas.drawLine((float) getPaddingLeft(), y, (float) (getWidth() - getPaddingRight()), y, this.mDividerPaint);
        canvas.drawRect((float) this.mIndicatorLeft, (float) (getHeight() - this.mSelectedIndicatorHeight), (float) this.mIndicatorRight, (float) getHeight(), this.mSelectedIndicatorPaint);
    }

    public void highlightWorkTabIfNecessary() {
        if (!this.mSharedPreferences.getBoolean(KEY_SHOWED_PEEK_WORK_TAB, false) && this.mLastActivePage == 0) {
            highlightWorkTab();
            this.mSharedPreferences.edit().putBoolean(KEY_SHOWED_PEEK_WORK_TAB, true).apply();
        }
    }

    private void highlightWorkTab() {
        View v = getChildAt(1);
        v.post(new Runnable(v) {
            private final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                PersonalWorkSlidingTabStrip.lambda$highlightWorkTab$0(this.f$0);
            }
        });
    }

    static /* synthetic */ void lambda$highlightWorkTab$0(View v) {
        v.setPressed(true);
        v.setPressed(false);
    }

    public void setScroll(int currentScroll, int totalScroll) {
        updateIndicatorPosition(((float) currentScroll) / ((float) totalScroll));
    }

    public void setActiveMarker(int activePage) {
        updateTabTextColor(activePage);
        if (!(this.mContainerView == null || this.mLastActivePage == activePage)) {
            this.mContainerView.onTabChanged(activePage);
        }
        this.mLastActivePage = activePage;
    }

    public void setContainerView(AllAppsContainerView containerView) {
        this.mContainerView = containerView;
    }

    public void setMarkersCount(int numMarkers) {
    }

    public boolean hasOverlappingRendering() {
        return false;
    }
}
