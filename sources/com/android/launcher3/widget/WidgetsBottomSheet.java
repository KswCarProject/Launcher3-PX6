package com.android.launcher3.widget;

import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Insettable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.model.WidgetItem;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.PackageUserKey;
import java.util.List;

public class WidgetsBottomSheet extends BaseWidgetSheet implements Insettable {
    private static final int DEFAULT_CLOSE_DURATION = 200;
    private Rect mInsets;
    private ItemInfo mOriginalItemInfo;

    public /* bridge */ /* synthetic */ void fillInLogContainerData(View view, ItemInfo itemInfo, LauncherLogProto.Target target, LauncherLogProto.Target target2) {
        super.fillInLogContainerData(view, itemInfo, target, target2);
    }

    public /* bridge */ /* synthetic */ void onDropCompleted(View view, DropTarget.DragObject dragObject, boolean z) {
        super.onDropCompleted(view, dragObject, z);
    }

    public WidgetsBottomSheet(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetsBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        this.mInsets = new Rect();
        this.mContent = this;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        setTranslationShift(this.mTranslationShift);
    }

    public void populateAndShow(ItemInfo itemInfo) {
        this.mOriginalItemInfo = itemInfo;
        ((TextView) findViewById(R.id.title)).setText(getContext().getString(R.string.widgets_bottom_sheet_title, new Object[]{this.mOriginalItemInfo.title}));
        onWidgetsBound();
        this.mLauncher.getDragLayer().addView(this);
        this.mIsOpen = false;
        animateOpen();
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
        List<WidgetItem> widgets = this.mLauncher.getPopupDataProvider().getWidgetsForPackageUser(new PackageUserKey(this.mOriginalItemInfo.getTargetComponent().getPackageName(), this.mOriginalItemInfo.user));
        ViewGroup widgetRow = (ViewGroup) findViewById(R.id.widgets);
        ViewGroup widgetCells = (ViewGroup) widgetRow.findViewById(R.id.widgets_cell_list);
        widgetCells.removeAllViews();
        for (int i = 0; i < widgets.size(); i++) {
            WidgetCell widget = addItemCell(widgetCells);
            widget.applyFromCellItem(widgets.get(i), LauncherAppState.getInstance(this.mLauncher).getWidgetCache());
            widget.ensurePreview();
            widget.setVisibility(0);
            if (i < widgets.size() - 1) {
                addDivider(widgetCells);
            }
        }
        if (widgets.size() == 1) {
            ((LinearLayout.LayoutParams) widgetRow.getLayoutParams()).gravity = 1;
            return;
        }
        View leftPaddingView = LayoutInflater.from(getContext()).inflate(R.layout.widget_list_divider, widgetRow, false);
        leftPaddingView.getLayoutParams().width = Utilities.pxFromDp(16.0f, getResources().getDisplayMetrics());
        widgetCells.addView(leftPaddingView, 0);
    }

    private void addDivider(ViewGroup parent) {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_list_divider, parent, true);
    }

    private WidgetCell addItemCell(ViewGroup parent) {
        WidgetCell widget = (WidgetCell) LayoutInflater.from(getContext()).inflate(R.layout.widget_cell, parent, false);
        widget.setOnClickListener(this);
        widget.setOnLongClickListener(this);
        widget.setAnimatePreview(false);
        parent.addView(widget);
        return widget;
    }

    private void animateOpen() {
        if (!this.mIsOpen && !this.mOpenCloseAnimator.isRunning()) {
            this.mIsOpen = true;
            setupNavBarColor();
            this.mOpenCloseAnimator.setValues(new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(TRANSLATION_SHIFT, new float[]{0.0f})});
            this.mOpenCloseAnimator.setInterpolator(Interpolators.FAST_OUT_SLOW_IN);
            this.mOpenCloseAnimator.start();
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        handleClose(animate, 200);
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 4) != 0;
    }

    public void setInsets(Rect insets) {
        int leftInset = insets.left - this.mInsets.left;
        int rightInset = insets.right - this.mInsets.right;
        int bottomInset = insets.bottom - this.mInsets.bottom;
        this.mInsets.set(insets);
        setPadding(getPaddingLeft() + leftInset, getPaddingTop(), getPaddingRight() + rightInset, getPaddingBottom() + bottomInset);
    }

    /* access modifiers changed from: protected */
    public int getElementsRowCount() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return Pair.create(findViewById(R.id.title), getContext().getString(this.mIsOpen ? R.string.widgets_list : R.string.widgets_list_closed));
    }
}
