package com.szchoiceway.index;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

public class PagedViewCellLayout extends ViewGroup implements Page {
    static final String TAG = "PagedViewCellLayout";
    private static int m_iModeSet = 0;
    /* access modifiers changed from: private */
    public static int m_iUITypeVer = 0;
    private int mAppsIconIndex;
    private int mCellCountX;
    private int mCellCountY;
    private int mCellHeight;
    private int mCellWidth;
    protected PagedViewCellLayoutChildren mChildren;
    private int mHeightGap;
    private int mMaxGap;
    private int mOriginalCellHeight;
    private int mOriginalCellWidth;
    private int mOriginalHeightGap;
    private int mOriginalWidthGap;
    private SysProviderOpt mSysProviderOpt;
    private int mWidthGap;
    private String xml_client;

    public PagedViewCellLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public PagedViewCellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewCellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mSysProviderOpt = null;
        this.xml_client = "";
        this.mAppsIconIndex = 0;
        this.mSysProviderOpt = ((LauncherApplication) context.getApplicationContext()).getProvider();
        m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, m_iUITypeVer);
        m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, m_iModeSet);
        this.xml_client = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_FACTORY_SET_CLIENT);
        this.mAppsIconIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_APPS_ICON_SELECT_INDEX, 0);
        setAlwaysDrawnWithCacheEnabled(false);
        Resources resources = context.getResources();
        if ("ALS".equalsIgnoreCase(this.xml_client) && this.mAppsIconIndex == 1) {
            int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_width_apple);
            this.mCellWidth = dimensionPixelSize;
            this.mOriginalCellWidth = dimensionPixelSize;
            int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_height_apple);
            this.mCellHeight = dimensionPixelSize2;
            this.mOriginalCellHeight = dimensionPixelSize2;
        } else if (LauncherApplication.get_m_iUITypeVer() == 41 && LauncherApplication.get_m_iModeSet() == 14) {
            int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_width_chuanqi_cusp);
            this.mCellWidth = dimensionPixelSize3;
            this.mOriginalCellWidth = dimensionPixelSize3;
            int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_height_chuanqi_cusp);
            this.mCellHeight = dimensionPixelSize4;
            this.mOriginalCellHeight = dimensionPixelSize4;
        } else {
            int dimensionPixelSize5 = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_width);
            this.mCellWidth = dimensionPixelSize5;
            this.mOriginalCellWidth = dimensionPixelSize5;
            int dimensionPixelSize6 = resources.getDimensionPixelSize(R.dimen.apps_customize_cell_height);
            this.mCellHeight = dimensionPixelSize6;
            this.mOriginalCellHeight = dimensionPixelSize6;
        }
        Log.i(TAG, "PagedViewCellLayout: mCellWidth = " + this.mCellWidth + ", mCellHeight = " + this.mCellHeight);
        this.mCellCountX = LauncherModel.getCellCountX();
        this.mCellCountY = LauncherModel.getCellCountY();
        this.mHeightGap = -1;
        this.mWidthGap = -1;
        this.mOriginalHeightGap = -1;
        this.mOriginalWidthGap = -1;
        this.mMaxGap = resources.getDimensionPixelSize(R.dimen.apps_customize_max_gap);
        this.mChildren = new PagedViewCellLayoutChildren(context);
        this.mChildren.setCellDimensions(this.mCellWidth, this.mCellHeight);
        this.mChildren.setGap(this.mWidthGap, this.mHeightGap);
        addView(this.mChildren);
    }

    public int getCellWidth() {
        return this.mCellWidth;
    }

    public int getCellHeight() {
        return this.mCellHeight;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params) {
        LayoutParams lp = params;
        if (lp.cellX < 0 || lp.cellX > this.mCellCountX - 1 || lp.cellY < 0 || lp.cellY > this.mCellCountY - 1) {
            return false;
        }
        if (lp.cellHSpan < 0) {
            lp.cellHSpan = this.mCellCountX;
        }
        if (lp.cellVSpan < 0) {
            lp.cellVSpan = this.mCellCountY;
        }
        if (LauncherApplication.get_m_iUITypeVer() == 41 && LauncherApplication.get_m_iModeSet() != 14) {
            Log.i(TAG, "addViewToCellLayout: 缩放...");
        }
        child.setId(childId);
        this.mChildren.addView(child, index, lp);
        return true;
    }

    public void removeAllViewsOnPage() {
        this.mChildren.removeAllViews();
        setLayerType(0, (Paint) null);
    }

    public void removeViewOnPageAt(int index) {
        this.mChildren.removeViewAt(index);
    }

    public void resetChildrenOnKeyListeners() {
        int childCount = this.mChildren.getChildCount();
        for (int j = 0; j < childCount; j++) {
            this.mChildren.getChildAt(j).setOnKeyListener((View.OnKeyListener) null);
        }
    }

    public int getPageChildCount() {
        return this.mChildren.getChildCount();
    }

    public PagedViewCellLayoutChildren getChildrenLayout() {
        return this.mChildren;
    }

    public View getChildOnPageAt(int i) {
        return this.mChildren.getChildAt(i);
    }

    public int indexOfChildOnPage(View v) {
        return this.mChildren.indexOfChild(v);
    }

    public int getCellCountX() {
        return this.mCellCountX;
    }

    public int getCellCountY() {
        return this.mCellCountY;
    }

    public View getCurFocusView() {
        AppsCustomizePagedView parent;
        if (getParent() == null || (parent = (AppsCustomizePagedView) getParent()) == null) {
            return null;
        }
        return parent.getCurFocusView();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == 0 || heightSpecMode == 0) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }
        int numWidthGaps = this.mCellCountX - 1;
        int numHeightGaps = this.mCellCountY - 1;
        if (this.mOriginalWidthGap < 0 || this.mOriginalHeightGap < 0) {
            int hSpace = (widthSpecSize - getPaddingLeft()) - getPaddingRight();
            int vSpace = (heightSpecSize - getPaddingTop()) - getPaddingBottom();
            int hFreeSpace = hSpace - (this.mCellCountX * this.mOriginalCellWidth);
            int vFreeSpace = vSpace - (this.mCellCountY * this.mOriginalCellHeight);
            this.mWidthGap = Math.min(this.mMaxGap, numWidthGaps > 0 ? hFreeSpace / numWidthGaps : 0);
            this.mHeightGap = Math.min(this.mMaxGap, numHeightGaps > 0 ? vFreeSpace / numHeightGaps : 0);
            Log.i(TAG, "onMeasure: mWidthGap = " + this.mWidthGap + ",mHeightGap = " + this.mHeightGap);
            this.mChildren.setGap(this.mWidthGap, this.mHeightGap);
        } else {
            this.mWidthGap = this.mOriginalWidthGap;
            this.mHeightGap = this.mOriginalHeightGap;
        }
        int newWidth = widthSpecSize;
        int newHeight = heightSpecSize;
        if (widthSpecMode == Integer.MIN_VALUE) {
            newWidth = getPaddingLeft() + getPaddingRight() + (this.mCellCountX * this.mCellWidth) + ((this.mCellCountX - 1) * this.mWidthGap);
            newHeight = getPaddingTop() + getPaddingBottom() + (this.mCellCountY * this.mCellHeight) + ((this.mCellCountY - 1) * this.mHeightGap);
            setMeasuredDimension(newWidth, newHeight);
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(View.MeasureSpec.makeMeasureSpec((newWidth - getPaddingLeft()) - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec((newHeight - getPaddingTop()) - getPaddingBottom(), 1073741824));
        }
        setMeasuredDimension(newWidth, newHeight);
    }

    /* access modifiers changed from: package-private */
    public int getContentWidth() {
        return getWidthBeforeFirstLayout() + getPaddingLeft() + getPaddingRight();
    }

    /* access modifiers changed from: package-private */
    public int getContentHeight() {
        if (this.mCellCountY <= 0) {
            return 0;
        }
        return (Math.max(0, this.mHeightGap) * (this.mCellCountY - 1)) + (this.mCellCountY * this.mCellHeight);
    }

    /* access modifiers changed from: package-private */
    public int getWidthBeforeFirstLayout() {
        if (this.mCellCountX <= 0) {
            return 0;
        }
        return (Math.max(0, this.mWidthGap) * (this.mCellCountX - 1)) + (this.mCellCountX * this.mCellWidth);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).layout(getPaddingLeft(), getPaddingTop(), (r - l) - getPaddingRight(), (b - t) - getPaddingBottom());
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        int count = getPageChildCount();
        if (count <= 0) {
            return result;
        }
        int bottom = getChildOnPageAt(count - 1).getBottom();
        if (((int) Math.ceil((double) (((float) getPageChildCount()) / ((float) getCellCountX())))) < getCellCountY()) {
            bottom += this.mCellHeight / 2;
        }
        return result || event.getY() < ((float) bottom);
    }

    public void enableCenteredContent(boolean enabled) {
        this.mChildren.enableCenteredContent(enabled);
    }

    /* access modifiers changed from: protected */
    public void setChildrenDrawingCacheEnabled(boolean enabled) {
        this.mChildren.setChildrenDrawingCacheEnabled(enabled);
    }

    public void setCellCount(int xCount, int yCount) {
        this.mCellCountX = xCount;
        this.mCellCountY = yCount;
        requestLayout();
    }

    public void setGap(int widthGap, int heightGap) {
        this.mWidthGap = widthGap;
        this.mOriginalWidthGap = widthGap;
        this.mHeightGap = heightGap;
        this.mOriginalHeightGap = heightGap;
        this.mChildren.setGap(widthGap, heightGap);
    }

    public int[] getCellCountForDimensions(int width, int height) {
        int smallerSize = Math.min(this.mCellWidth, this.mCellHeight);
        return new int[]{(width + smallerSize) / smallerSize, (height + smallerSize) / smallerSize};
    }

    /* access modifiers changed from: package-private */
    public void onDragChild(View child) {
        ((LayoutParams) child.getLayoutParams()).isDragging = true;
    }

    public int estimateCellHSpan(int width) {
        return Math.max(1, (this.mWidthGap + (width - (getPaddingLeft() + getPaddingRight()))) / (this.mCellWidth + this.mWidthGap));
    }

    public int estimateCellVSpan(int height) {
        return Math.max(1, (this.mHeightGap + (height - (getPaddingTop() + getPaddingBottom()))) / (this.mCellHeight + this.mHeightGap));
    }

    public int[] estimateCellPosition(int x, int y) {
        return new int[]{getPaddingLeft() + (this.mCellWidth * x) + (this.mWidthGap * x) + (this.mCellWidth / 2), getPaddingTop() + (this.mCellHeight * y) + (this.mHeightGap * y) + (this.mCellHeight / 2)};
    }

    public void calculateCellCount(int width, int height, int maxCellCountX, int maxCellCountY) {
        this.mCellCountX = Math.min(maxCellCountX, estimateCellHSpan(width));
        this.mCellCountY = Math.min(maxCellCountY, estimateCellVSpan(height));
        requestLayout();
    }

    public int estimateCellWidth(int hSpan) {
        return this.mCellWidth * hSpan;
    }

    public int estimateCellHeight(int vSpan) {
        return this.mCellHeight * vSpan;
    }

    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        @ViewDebug.ExportedProperty
        public int cellHSpan;
        @ViewDebug.ExportedProperty
        public int cellVSpan;
        @ViewDebug.ExportedProperty
        public int cellX;
        @ViewDebug.ExportedProperty
        public int cellY;
        public boolean isDragging;
        private Object mTag;
        @ViewDebug.ExportedProperty
        int x;
        @ViewDebug.ExportedProperty
        int y;

        public LayoutParams() {
            super(-1, -1);
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.cellX = source.cellX;
            this.cellY = source.cellY;
            this.cellHSpan = source.cellHSpan;
            this.cellVSpan = source.cellVSpan;
        }

        public LayoutParams(int cellX2, int cellY2, int cellHSpan2, int cellVSpan2) {
            super(-1, -1);
            this.cellX = cellX2;
            this.cellY = cellY2;
            this.cellHSpan = cellHSpan2;
            this.cellVSpan = cellVSpan2;
        }

        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap, int hStartPadding, int vStartPadding) {
            int myCellHSpan = this.cellHSpan;
            int myCellVSpan = this.cellVSpan;
            int myCellX = this.cellX;
            int myCellY = this.cellY;
            this.width = (((myCellHSpan * cellWidth) + ((myCellHSpan - 1) * widthGap)) - this.leftMargin) - this.rightMargin;
            this.height = (((myCellVSpan * cellHeight) + ((myCellVSpan - 1) * heightGap)) - this.topMargin) - this.bottomMargin;
            if (LauncherApplication.isScreenLarge()) {
                this.x = ((cellWidth + widthGap) * myCellX) + hStartPadding + this.leftMargin;
                this.y = ((cellHeight + heightGap) * myCellY) + vStartPadding + this.topMargin;
            } else {
                this.x = ((cellWidth + widthGap) * myCellX) + this.leftMargin;
                if (PagedViewCellLayout.m_iUITypeVer == 36 && myCellY == 1) {
                    this.y = ((cellHeight - 20) * myCellY) + this.topMargin;
                } else {
                    this.y = ((cellHeight + heightGap) * myCellY) + this.topMargin;
                }
            }
            Log.i(PagedViewCellLayout.TAG, "--->>> bbb cellWidth = " + cellWidth + ", cellHeigh = " + cellHeight + "\n, widthGap = " + widthGap + ", heightGap = " + heightGap + "\n, hStartPadding = " + hStartPadding + ", vStartPadding = " + vStartPadding + "\n, myCellHSpan = " + myCellHSpan + ", myCellVSpan = " + myCellVSpan + "\n, myCellX = " + myCellX + ", myCellY = " + myCellY + "\n, width = " + this.width + ", height = " + this.height + "\n, x = " + this.x + ", x = " + this.x + "\n, y = " + this.y + ", y = " + this.y + "\n, leftMargin = " + this.leftMargin + ", rightMargin = " + this.rightMargin + "\n, topMargin = " + this.topMargin + ", bottomMargin = " + this.bottomMargin);
        }

        public Object getTag() {
            return this.mTag;
        }

        public void setTag(Object tag) {
            this.mTag = tag;
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ", " + this.cellHSpan + ", " + this.cellVSpan + ")";
        }
    }
}
