package com.szchoiceway.index;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.szchoiceway.index.CellLayout;

public class Hotseat extends FrameLayout {
    private static final String TAG = "Hotseat";
    private int mAllAppsButtonRank;
    private int mCellCountX;
    private int mCellCountY;
    private CellLayout mContent;
    private boolean mIsLandscape;
    /* access modifiers changed from: private */
    public Launcher mLauncher;
    private boolean mTransposeLayoutWithOrientation;

    public Hotseat(Context context) {
        this(context, (AttributeSet) null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Hotseat, defStyle, 0);
        Resources r = context.getResources();
        this.mCellCountX = a.getInt(0, -1);
        this.mCellCountY = a.getInt(1, -1);
        this.mAllAppsButtonRank = r.getInteger(R.integer.hotseat_all_apps_index);
        this.mTransposeLayoutWithOrientation = r.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        this.mIsLandscape = false;
    }

    public void setup(Launcher launcher) {
        this.mLauncher = launcher;
        setOnKeyListener(new HotseatIconKeyEventListener());
    }

    /* access modifiers changed from: package-private */
    public CellLayout getLayout() {
        return this.mContent;
    }

    private boolean hasVerticalHotseat() {
        return this.mIsLandscape && this.mTransposeLayoutWithOrientation;
    }

    /* access modifiers changed from: package-private */
    public int getOrderInHotseat(int x, int y) {
        return hasVerticalHotseat() ? (this.mContent.getCountY() - y) - 1 : x;
    }

    /* access modifiers changed from: package-private */
    public int getCellXFromOrder(int rank) {
        if (hasVerticalHotseat()) {
            return 0;
        }
        return rank;
    }

    /* access modifiers changed from: package-private */
    public int getCellYFromOrder(int rank) {
        if (hasVerticalHotseat()) {
            return this.mContent.getCountY() - (rank + 1);
        }
        return 0;
    }

    public boolean isAllAppsButtonRank(int rank) {
        return rank == this.mAllAppsButtonRank;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        if (this.mCellCountX < 0) {
            this.mCellCountX = LauncherModel.getCellCountX();
        }
        if (this.mCellCountY < 0) {
            this.mCellCountY = LauncherModel.getCellCountY();
        }
        this.mContent = (CellLayout) findViewById(R.id.layout);
        this.mContent.setGridSize(this.mCellCountX, this.mCellCountY);
        this.mContent.setIsHotseat(true);
        resetLayout();
    }

    /* access modifiers changed from: package-private */
    public void resetLayout() {
        this.mContent.removeAllViewsInLayout();
        Context context = getContext();
        BubbleTextView allAppsButton = (BubbleTextView) LayoutInflater.from(context).inflate(R.layout.application, this.mContent, false);
        allAppsButton.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, context.getResources().getDrawable(R.drawable.all_apps_button_icon), (Drawable) null, (Drawable) null);
        allAppsButton.setContentDescription(context.getString(R.string.all_apps_button_label));
        allAppsButton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (Hotseat.this.mLauncher == null || (event.getAction() & 255) != 0) {
                    return false;
                }
                Hotseat.this.mLauncher.onTouchDownAllAppsButton(v);
                return false;
            }
        });
        allAppsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Hotseat.this.mLauncher != null) {
                    Hotseat.this.mLauncher.onClickAllAppsButton(v);
                }
            }
        });
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(getCellXFromOrder(this.mAllAppsButtonRank), getCellYFromOrder(this.mAllAppsButtonRank), 1, 1);
        lp.canReorder = false;
        this.mContent.addViewToCellLayout(allAppsButton, -1, 0, lp, true);
    }
}
