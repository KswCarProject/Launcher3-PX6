package com.android.launcher3.allapps.search;

import android.content.Context;
import android.graphics.Rect;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.Insettable;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.allapps.SearchUiManager;
import com.android.launcher3.allapps.search.AllAppsSearchBarController;
import com.android.launcher3.graphics.TintedDrawableSpan;
import com.android.launcher3.util.ComponentKey;
import java.util.ArrayList;

public class AppsSearchContainerLayout extends ExtendedEditText implements SearchUiManager, AllAppsSearchBarController.Callbacks, AllAppsStore.OnUpdateListener, Insettable {
    private AlphabeticalAppsList mApps;
    private AllAppsContainerView mAppsView;
    private final float mFixedTranslationY;
    private final Launcher mLauncher;
    private final float mMarginTopAdjusting;
    private final AllAppsSearchBarController mSearchBarController;
    private final SpannableStringBuilder mSearchQueryBuilder;

    public AppsSearchContainerLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public AppsSearchContainerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppsSearchContainerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLauncher = Launcher.getLauncher(context);
        this.mSearchBarController = new AllAppsSearchBarController();
        this.mSearchQueryBuilder = new SpannableStringBuilder();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
        this.mFixedTranslationY = getTranslationY();
        this.mMarginTopAdjusting = this.mFixedTranslationY - ((float) getPaddingTop());
        SpannableString spanned = new SpannableString("  " + getHint());
        spanned.setSpan(new TintedDrawableSpan(getContext(), R.drawable.ic_allapps_search), 0, 1, 34);
        setHint(spanned);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mLauncher.getAppsView().getAppsStore().addUpdateListener(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mLauncher.getAppsView().getAppsStore().removeUpdateListener(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        DeviceProfile dp = this.mLauncher.getDeviceProfile();
        int rowWidth = (View.MeasureSpec.getSize(widthMeasureSpec) - this.mAppsView.getActiveRecyclerView().getPaddingLeft()) - this.mAppsView.getActiveRecyclerView().getPaddingRight();
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((rowWidth - (DeviceProfile.calculateCellWidth(rowWidth, dp.inv.numHotseatIcons) - Math.round(((float) dp.iconSizePx) * 0.92f))) + getPaddingLeft() + getPaddingRight(), 1073741824), heightMeasureSpec);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        View parent = (View) getParent();
        setTranslationX((float) ((parent.getPaddingLeft() + ((((parent.getWidth() - parent.getPaddingLeft()) - parent.getPaddingRight()) - (right - left)) / 2)) - left));
    }

    public void initialize(AllAppsContainerView appsView) {
        this.mApps = appsView.getApps();
        this.mAppsView = appsView;
        this.mSearchBarController.initialize(new DefaultAppSearchAlgorithm(this.mApps.getApps()), this, this.mLauncher, this);
    }

    public void onAppsUpdated() {
        this.mSearchBarController.refreshSearchResult();
    }

    public void resetSearch() {
        this.mSearchBarController.reset();
    }

    public void preDispatchKeyEvent(KeyEvent event) {
        if (!this.mSearchBarController.isSearchFieldFocused() && event.getAction() == 0) {
            int unicodeChar = event.getUnicodeChar();
            if ((unicodeChar > 0 && !Character.isWhitespace(unicodeChar) && !Character.isSpaceChar(unicodeChar)) && TextKeyListener.getInstance().onKeyDown(this, this.mSearchQueryBuilder, event.getKeyCode(), event) && this.mSearchQueryBuilder.length() > 0) {
                this.mSearchBarController.focusSearchField();
            }
        }
    }

    public void onSearchResult(String query, ArrayList<ComponentKey> apps) {
        if (apps != null) {
            this.mApps.setOrderedFilter(apps);
            notifyResultChanged();
            this.mAppsView.setLastSearchQuery(query);
        }
    }

    public void clearSearchResult() {
        if (this.mApps.setOrderedFilter((ArrayList<ComponentKey>) null)) {
            notifyResultChanged();
        }
        this.mSearchQueryBuilder.clear();
        this.mSearchQueryBuilder.clearSpans();
        Selection.setSelection(this.mSearchQueryBuilder, 0);
        this.mAppsView.onClearSearchResult();
    }

    private void notifyResultChanged() {
        this.mAppsView.onSearchResultsChanged();
    }

    public void setInsets(Rect insets) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) getLayoutParams();
        mlp.topMargin = Math.round(Math.max(-this.mFixedTranslationY, ((float) insets.top) - this.mMarginTopAdjusting));
        requestLayout();
        if (this.mLauncher.getDeviceProfile().isVerticalBarLayout()) {
            this.mLauncher.getAllAppsController().setScrollRangeDelta(0.0f);
        } else {
            this.mLauncher.getAllAppsController().setScrollRangeDelta(((float) (insets.bottom + mlp.topMargin)) + this.mFixedTranslationY);
        }
    }
}
