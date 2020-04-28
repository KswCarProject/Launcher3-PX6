package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import com.android.launcher3.DropTarget;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate;
import com.android.launcher3.accessibility.FolderAccessibilityHelper;
import com.android.launcher3.accessibility.WorkspaceAccessibilityHelper;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.graphics.DragPreviewProvider;
import com.android.launcher3.util.CellAndSpan;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.ParcelableSparseArray;
import com.android.launcher3.util.Themes;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

public class CellLayout extends ViewGroup {
    private static final int[] BACKGROUND_STATE_ACTIVE = {16842914};
    private static final int[] BACKGROUND_STATE_DEFAULT = EMPTY_STATE_SET;
    private static final boolean DEBUG_VISUALIZE_OCCUPIED = false;
    private static final boolean DESTRUCTIVE_REORDER = false;
    public static final int FOLDER = 2;
    public static final int FOLDER_ACCESSIBILITY_DRAG = 1;
    public static final int HOTSEAT = 1;
    private static final int INVALID_DIRECTION = -100;
    private static final boolean LOGD = false;
    public static final int MODE_ACCEPT_DROP = 4;
    public static final int MODE_DRAG_OVER = 1;
    public static final int MODE_ON_DROP = 2;
    public static final int MODE_ON_DROP_EXTERNAL = 3;
    public static final int MODE_SHOW_REORDER_HINT = 0;
    private static final int REORDER_ANIMATION_DURATION = 150;
    private static final float REORDER_PREVIEW_MAGNITUDE = 0.12f;
    private static final String TAG = "CellLayout";
    public static final int WORKSPACE = 0;
    public static final int WORKSPACE_ACCESSIBILITY_DRAG = 2;
    private static final Paint sPaint = new Paint();
    private final Drawable mBackground;
    @ViewDebug.ExportedProperty(category = "launcher")
    int mCellHeight;
    @ViewDebug.ExportedProperty(category = "launcher")
    int mCellWidth;
    private final float mChildScale;
    private final int mContainerType;
    /* access modifiers changed from: private */
    @ViewDebug.ExportedProperty(category = "launcher")
    public int mCountX;
    /* access modifiers changed from: private */
    @ViewDebug.ExportedProperty(category = "launcher")
    public int mCountY;
    private final int[] mDirectionVector;
    private final int[] mDragCell;
    final float[] mDragOutlineAlphas;
    private final InterruptibleInOutAnimator[] mDragOutlineAnims;
    private int mDragOutlineCurrent;
    private final Paint mDragOutlinePaint;
    final Rect[] mDragOutlines;
    private boolean mDragging;
    private boolean mDropPending;
    private final TimeInterpolator mEaseOutInterpolator;
    private int mFixedCellHeight;
    private int mFixedCellWidth;
    private int mFixedHeight;
    private int mFixedWidth;
    private final ArrayList<PreviewBackground> mFolderBackgrounds;
    final PreviewBackground mFolderLeaveBehind;
    private View.OnTouchListener mInterceptTouchListener;
    private final ArrayList<View> mIntersectingViews;
    private boolean mIsDragOverlapping;
    private boolean mItemPlacementDirty;
    private final Launcher mLauncher;
    private GridOccupancy mOccupied;
    private final Rect mOccupiedRect;
    final int[] mPreviousReorderDirection;
    final ArrayMap<LayoutParams, Animator> mReorderAnimators;
    final float mReorderPreviewAnimationMagnitude;
    final ArrayMap<View, ReorderPreviewAnimation> mShakeAnimators;
    private final ShortcutAndWidgetContainer mShortcutsAndWidgets;
    private final StylusEventHelper mStylusEventHelper;
    final int[] mTempLocation;
    private final Rect mTempRect;
    private final Stack<Rect> mTempRectStack;
    private GridOccupancy mTmpOccupied;
    final int[] mTmpPoint;
    private DragAndDropAccessibilityDelegate mTouchHelper;
    private boolean mUseTouchHelper;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ContainerType {
    }

    public CellLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Context context2 = context;
        int i = 0;
        this.mDropPending = false;
        this.mTmpPoint = new int[2];
        this.mTempLocation = new int[2];
        this.mFolderBackgrounds = new ArrayList<>();
        this.mFolderLeaveBehind = new PreviewBackground();
        this.mFixedWidth = -1;
        this.mFixedHeight = -1;
        this.mIsDragOverlapping = false;
        this.mDragOutlines = new Rect[4];
        this.mDragOutlineAlphas = new float[this.mDragOutlines.length];
        this.mDragOutlineAnims = new InterruptibleInOutAnimator[this.mDragOutlines.length];
        this.mDragOutlineCurrent = 0;
        this.mDragOutlinePaint = new Paint();
        this.mReorderAnimators = new ArrayMap<>();
        this.mShakeAnimators = new ArrayMap<>();
        this.mItemPlacementDirty = false;
        this.mDragCell = new int[2];
        this.mDragging = false;
        this.mChildScale = 1.0f;
        this.mIntersectingViews = new ArrayList<>();
        this.mOccupiedRect = new Rect();
        this.mDirectionVector = new int[2];
        this.mPreviousReorderDirection = new int[2];
        this.mTempRect = new Rect();
        this.mUseTouchHelper = false;
        this.mTempRectStack = new Stack<>();
        TypedArray a = context2.obtainStyledAttributes(attrs, R.styleable.CellLayout, defStyle, 0);
        this.mContainerType = a.getInteger(0, 0);
        a.recycle();
        setWillNotDraw(false);
        setClipToPadding(false);
        this.mLauncher = Launcher.getLauncher(context);
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        this.mCellHeight = -1;
        this.mCellWidth = -1;
        this.mFixedCellHeight = -1;
        this.mFixedCellWidth = -1;
        this.mCountX = grid.inv.numColumns;
        this.mCountY = grid.inv.numRows;
        this.mOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTmpOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mPreviousReorderDirection[0] = -100;
        this.mPreviousReorderDirection[1] = -100;
        this.mFolderLeaveBehind.delegateCellX = -1;
        this.mFolderLeaveBehind.delegateCellY = -1;
        setAlwaysDrawnWithCacheEnabled(false);
        Resources res = getResources();
        this.mBackground = res.getDrawable(R.drawable.bg_celllayout);
        this.mBackground.setCallback(this);
        this.mBackground.setAlpha(0);
        this.mReorderPreviewAnimationMagnitude = ((float) grid.iconSizePx) * REORDER_PREVIEW_MAGNITUDE;
        this.mEaseOutInterpolator = Interpolators.DEACCEL_2_5;
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
        for (int i2 = 0; i2 < this.mDragOutlines.length; i2++) {
            this.mDragOutlines[i2] = new Rect(-1, -1, -1, -1);
        }
        this.mDragOutlinePaint.setColor(Themes.getAttrColor(context2, R.attr.workspaceTextColor));
        int duration = res.getInteger(R.integer.config_dragOutlineFadeTime);
        float toAlphaValue = (float) res.getInteger(R.integer.config_dragOutlineMaxAlpha);
        Arrays.fill(this.mDragOutlineAlphas, 0.0f);
        while (true) {
            int i3 = i;
            if (i3 < this.mDragOutlineAnims.length) {
                int i4 = i3;
                final InterruptibleInOutAnimator anim = new InterruptibleInOutAnimator(this, (long) duration, 0.0f, toAlphaValue);
                anim.getAnimator().setInterpolator(this.mEaseOutInterpolator);
                final int thisIndex = i4;
                anim.getAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (((Bitmap) anim.getTag()) == null) {
                            animation.cancel();
                            return;
                        }
                        CellLayout.this.mDragOutlineAlphas[thisIndex] = ((Float) animation.getAnimatedValue()).floatValue();
                        CellLayout.this.invalidate(CellLayout.this.mDragOutlines[thisIndex]);
                    }
                });
                anim.getAnimator().addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (((Float) ((ValueAnimator) animation).getAnimatedValue()).floatValue() == 0.0f) {
                            anim.setTag((Object) null);
                        }
                    }
                });
                this.mDragOutlineAnims[i4] = anim;
                i = i4 + 1;
            } else {
                this.mShortcutsAndWidgets = new ShortcutAndWidgetContainer(context2, this.mContainerType);
                this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
                this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
                addView(this.mShortcutsAndWidgets);
                return;
            }
        }
    }

    public void enableAccessibleDrag(boolean enable, int dragType) {
        this.mUseTouchHelper = enable;
        if (!enable) {
            ViewCompat.setAccessibilityDelegate(this, (AccessibilityDelegateCompat) null);
            setImportantForAccessibility(2);
            getShortcutsAndWidgets().setImportantForAccessibility(2);
            setOnClickListener((View.OnClickListener) null);
        } else {
            if (dragType == 2 && !(this.mTouchHelper instanceof WorkspaceAccessibilityHelper)) {
                this.mTouchHelper = new WorkspaceAccessibilityHelper(this);
            } else if (dragType == 1 && !(this.mTouchHelper instanceof FolderAccessibilityHelper)) {
                this.mTouchHelper = new FolderAccessibilityHelper(this);
            }
            ViewCompat.setAccessibilityDelegate(this, this.mTouchHelper);
            setImportantForAccessibility(1);
            getShortcutsAndWidgets().setImportantForAccessibility(1);
            setOnClickListener(this.mTouchHelper);
        }
        if (getParent() != null) {
            getParent().notifySubtreeAccessibilityStateChanged(this, this, 1);
        }
    }

    public boolean dispatchHoverEvent(MotionEvent event) {
        if (!this.mUseTouchHelper || !this.mTouchHelper.dispatchHoverEvent(event)) {
            return super.dispatchHoverEvent(event);
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.mUseTouchHelper) {
            return true;
        }
        if (this.mInterceptTouchListener == null || !this.mInterceptTouchListener.onTouch(this, ev)) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = super.onTouchEvent(ev);
        if (!this.mLauncher.isInState(LauncherState.OVERVIEW) || !this.mStylusEventHelper.onMotionEvent(ev)) {
            return handled;
        }
        return true;
    }

    public void enableHardwareLayer(boolean hasLayer) {
        this.mShortcutsAndWidgets.setLayerType(hasLayer ? 2 : 0, sPaint);
    }

    public void setCellDimensions(int width, int height) {
        this.mCellWidth = width;
        this.mFixedCellWidth = width;
        this.mCellHeight = height;
        this.mFixedCellHeight = height;
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
    }

    public void setGridSize(int x, int y) {
        this.mCountX = x;
        this.mCountY = y;
        this.mOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTmpOccupied = new GridOccupancy(this.mCountX, this.mCountY);
        this.mTempRectStack.clear();
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
        requestLayout();
    }

    public void setInvertIfRtl(boolean invert) {
        this.mShortcutsAndWidgets.setInvertIfRtl(invert);
    }

    public void setDropPending(boolean pending) {
        this.mDropPending = pending;
    }

    public boolean isDropPending() {
        return this.mDropPending;
    }

    /* access modifiers changed from: package-private */
    public void setIsDragOverlapping(boolean isDragOverlapping) {
        if (this.mIsDragOverlapping != isDragOverlapping) {
            this.mIsDragOverlapping = isDragOverlapping;
            this.mBackground.setState(this.mIsDragOverlapping ? BACKGROUND_STATE_ACTIVE : BACKGROUND_STATE_DEFAULT);
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        ParcelableSparseArray jail = getJailedArray(container);
        super.dispatchSaveInstanceState(jail);
        container.put(R.id.cell_layout_jail_id, jail);
    }

    /* access modifiers changed from: protected */
    public void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        super.dispatchRestoreInstanceState(getJailedArray(container));
    }

    private ParcelableSparseArray getJailedArray(SparseArray<Parcelable> container) {
        Parcelable parcelable = container.get(R.id.cell_layout_jail_id);
        return parcelable instanceof ParcelableSparseArray ? (ParcelableSparseArray) parcelable : new ParcelableSparseArray();
    }

    public boolean getIsDragOverlapping() {
        return this.mIsDragOverlapping;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.mBackground.getAlpha() > 0) {
            this.mBackground.draw(canvas);
        }
        Paint paint = this.mDragOutlinePaint;
        for (int i = 0; i < this.mDragOutlines.length; i++) {
            float alpha = this.mDragOutlineAlphas[i];
            if (alpha > 0.0f) {
                paint.setAlpha((int) (0.5f + alpha));
                canvas.drawBitmap((Bitmap) this.mDragOutlineAnims[i].getTag(), (Rect) null, this.mDragOutlines[i], paint);
            }
        }
        for (int i2 = 0; i2 < this.mFolderBackgrounds.size(); i2++) {
            PreviewBackground bg = this.mFolderBackgrounds.get(i2);
            cellToPoint(bg.delegateCellX, bg.delegateCellY, this.mTempLocation);
            canvas.save();
            canvas.translate((float) this.mTempLocation[0], (float) this.mTempLocation[1]);
            bg.drawBackground(canvas);
            if (!bg.isClipping) {
                bg.drawBackgroundStroke(canvas);
            }
            canvas.restore();
        }
        if (this.mFolderLeaveBehind.delegateCellX >= 0 && this.mFolderLeaveBehind.delegateCellY >= 0) {
            cellToPoint(this.mFolderLeaveBehind.delegateCellX, this.mFolderLeaveBehind.delegateCellY, this.mTempLocation);
            canvas.save();
            canvas.translate((float) this.mTempLocation[0], (float) this.mTempLocation[1]);
            this.mFolderLeaveBehind.drawLeaveBehind(canvas);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        for (int i = 0; i < this.mFolderBackgrounds.size(); i++) {
            PreviewBackground bg = this.mFolderBackgrounds.get(i);
            if (bg.isClipping) {
                cellToPoint(bg.delegateCellX, bg.delegateCellY, this.mTempLocation);
                canvas.save();
                canvas.translate((float) this.mTempLocation[0], (float) this.mTempLocation[1]);
                bg.drawBackgroundStroke(canvas);
                canvas.restore();
            }
        }
    }

    public void addFolderBackground(PreviewBackground bg) {
        this.mFolderBackgrounds.add(bg);
    }

    public void removeFolderBackground(PreviewBackground bg) {
        this.mFolderBackgrounds.remove(bg);
    }

    public void setFolderLeaveBehindCell(int x, int y) {
        View child = getChildAt(x, y);
        this.mFolderLeaveBehind.setup(this.mLauncher, (View) null, child.getMeasuredWidth(), child.getPaddingTop());
        this.mFolderLeaveBehind.delegateCellX = x;
        this.mFolderLeaveBehind.delegateCellY = y;
        invalidate();
    }

    public void clearFolderLeaveBehind() {
        this.mFolderLeaveBehind.delegateCellX = -1;
        this.mFolderLeaveBehind.delegateCellY = -1;
        invalidate();
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void restoreInstanceState(SparseArray<Parcelable> states) {
        try {
            dispatchRestoreInstanceState(states);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "Ignoring an error while restoring a view instance state", ex);
        }
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).cancelLongPress();
        }
    }

    public void setOnInterceptTouchListener(View.OnTouchListener listener) {
        this.mInterceptTouchListener = listener;
    }

    public int getCountX() {
        return this.mCountX;
    }

    public int getCountY() {
        return this.mCountY;
    }

    public boolean acceptsWidget() {
        return this.mContainerType == 0;
    }

    public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells) {
        LayoutParams lp = params;
        if (child instanceof BubbleTextView) {
            ((BubbleTextView) child).setTextVisibility(this.mContainerType != 1);
        }
        child.setScaleX(1.0f);
        child.setScaleY(1.0f);
        if (lp.cellX < 0 || lp.cellX > this.mCountX - 1 || lp.cellY < 0 || lp.cellY > this.mCountY - 1) {
            return false;
        }
        if (lp.cellHSpan < 0) {
            lp.cellHSpan = this.mCountX;
        }
        if (lp.cellVSpan < 0) {
            lp.cellVSpan = this.mCountY;
        }
        child.setId(childId);
        this.mShortcutsAndWidgets.addView(child, index, lp);
        if (markCells) {
            markCellsAsOccupiedForView(child);
        }
        return true;
    }

    public void removeAllViews() {
        this.mOccupied.clear();
        this.mShortcutsAndWidgets.removeAllViews();
    }

    public void removeAllViewsInLayout() {
        if (this.mShortcutsAndWidgets.getChildCount() > 0) {
            this.mOccupied.clear();
            this.mShortcutsAndWidgets.removeAllViewsInLayout();
        }
    }

    public void removeView(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeView(view);
    }

    public void removeViewAt(int index) {
        markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(index));
        this.mShortcutsAndWidgets.removeViewAt(index);
    }

    public void removeViewInLayout(View view) {
        markCellsAsUnoccupiedForView(view);
        this.mShortcutsAndWidgets.removeViewInLayout(view);
    }

    public void removeViews(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i));
        }
        this.mShortcutsAndWidgets.removeViews(start, count);
    }

    public void removeViewsInLayout(int start, int count) {
        for (int i = start; i < start + count; i++) {
            markCellsAsUnoccupiedForView(this.mShortcutsAndWidgets.getChildAt(i));
        }
        this.mShortcutsAndWidgets.removeViewsInLayout(start, count);
    }

    public void pointToCellExact(int x, int y, int[] result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        result[0] = (x - hStartPadding) / this.mCellWidth;
        result[1] = (y - vStartPadding) / this.mCellHeight;
        int xAxis = this.mCountX;
        int yAxis = this.mCountY;
        if (result[0] < 0) {
            result[0] = 0;
        }
        if (result[0] >= xAxis) {
            result[0] = xAxis - 1;
        }
        if (result[1] < 0) {
            result[1] = 0;
        }
        if (result[1] >= yAxis) {
            result[1] = yAxis - 1;
        }
    }

    /* access modifiers changed from: package-private */
    public void pointToCellRounded(int x, int y, int[] result) {
        pointToCellExact((this.mCellWidth / 2) + x, (this.mCellHeight / 2) + y, result);
    }

    /* access modifiers changed from: package-private */
    public void cellToPoint(int cellX, int cellY, int[] result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        result[0] = (this.mCellWidth * cellX) + hStartPadding;
        result[1] = (this.mCellHeight * cellY) + vStartPadding;
    }

    /* access modifiers changed from: package-private */
    public void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }

    /* access modifiers changed from: package-private */
    public void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        result[0] = (this.mCellWidth * cellX) + hStartPadding + ((this.mCellWidth * spanX) / 2);
        result[1] = (this.mCellHeight * cellY) + vStartPadding + ((this.mCellHeight * spanY) / 2);
    }

    /* access modifiers changed from: package-private */
    public void regionToRect(int cellX, int cellY, int spanX, int spanY, Rect result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        int left = (this.mCellWidth * cellX) + hStartPadding;
        int top = (this.mCellHeight * cellY) + vStartPadding;
        result.set(left, top, (this.mCellWidth * spanX) + left, (this.mCellHeight * spanY) + top);
    }

    public float getDistanceFromCell(float x, float y, int[] cell) {
        cellToCenterPoint(cell[0], cell[1], this.mTmpPoint);
        return (float) Math.hypot((double) (x - ((float) this.mTmpPoint[0])), (double) (y - ((float) this.mTmpPoint[1])));
    }

    public int getCellWidth() {
        return this.mCellWidth;
    }

    public int getCellHeight() {
        return this.mCellHeight;
    }

    public void setFixedSize(int width, int height) {
        this.mFixedWidth = width;
        this.mFixedHeight = height;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int childWidthSize = widthSize - (getPaddingLeft() + getPaddingRight());
        int childHeightSize = heightSize - (getPaddingTop() + getPaddingBottom());
        if (this.mFixedCellWidth < 0 || this.mFixedCellHeight < 0) {
            int cw = DeviceProfile.calculateCellWidth(childWidthSize, this.mCountX);
            int ch = DeviceProfile.calculateCellHeight(childHeightSize, this.mCountY);
            if (!(cw == this.mCellWidth && ch == this.mCellHeight)) {
                this.mCellWidth = cw;
                this.mCellHeight = ch;
                this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mCountX, this.mCountY);
            }
        }
        int newWidth = childWidthSize;
        int newHeight = childHeightSize;
        if (this.mFixedWidth > 0 && this.mFixedHeight > 0) {
            newWidth = this.mFixedWidth;
            newHeight = this.mFixedHeight;
        } else if (widthSpecMode == 0 || heightSpecMode == 0) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }
        this.mShortcutsAndWidgets.measure(View.MeasureSpec.makeMeasureSpec(newWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(newHeight, 1073741824));
        int maxWidth = this.mShortcutsAndWidgets.getMeasuredWidth();
        int maxHeight = this.mShortcutsAndWidgets.getMeasuredHeight();
        if (this.mFixedWidth <= 0 || this.mFixedHeight <= 0) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            setMeasuredDimension(maxWidth, maxHeight);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft() + ((int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f)));
        int right = ((r - l) - getPaddingRight()) - ((int) Math.ceil((double) (((float) getUnusedHorizontalSpace()) / 2.0f)));
        int top = getPaddingTop();
        int bottom = (b - t) - getPaddingBottom();
        this.mShortcutsAndWidgets.layout(left, top, right, bottom);
        this.mBackground.getPadding(this.mTempRect);
        this.mBackground.setBounds((left - this.mTempRect.left) - getPaddingLeft(), (top - this.mTempRect.top) - getPaddingTop(), this.mTempRect.right + right + getPaddingRight(), this.mTempRect.bottom + bottom + getPaddingBottom());
    }

    public int getUnusedHorizontalSpace() {
        return ((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - (this.mCountX * this.mCellWidth);
    }

    public Drawable getScrimBackground() {
        return this.mBackground;
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mBackground;
    }

    public ShortcutAndWidgetContainer getShortcutsAndWidgets() {
        return this.mShortcutsAndWidgets;
    }

    public View getChildAt(int x, int y) {
        return this.mShortcutsAndWidgets.getChildAt(x, y);
    }

    public boolean animateChildToPosition(View child, int cellX, int cellY, int duration, int delay, boolean permanent, boolean adjustOccupied) {
        int oldY;
        final View view = child;
        int i = cellX;
        int i2 = cellY;
        ShortcutAndWidgetContainer clc = getShortcutsAndWidgets();
        if (clc.indexOfChild(view) != -1) {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            ItemInfo info = (ItemInfo) child.getTag();
            if (this.mReorderAnimators.containsKey(lp)) {
                this.mReorderAnimators.get(lp).cancel();
                this.mReorderAnimators.remove(lp);
            }
            int oldX = lp.x;
            int oldY2 = lp.y;
            if (adjustOccupied) {
                GridOccupancy occupied = permanent ? this.mOccupied : this.mTmpOccupied;
                occupied.markCells(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, false);
                oldY = oldY2;
                occupied.markCells(cellX, cellY, lp.cellHSpan, lp.cellVSpan, true);
            } else {
                oldY = oldY2;
            }
            lp.isLockedToGrid = true;
            if (permanent) {
                info.cellX = i;
                lp.cellX = i;
                info.cellY = i2;
                lp.cellY = i2;
            } else {
                lp.tmpCellX = i;
                lp.tmpCellY = i2;
            }
            clc.setupLp(view);
            lp.isLockedToGrid = false;
            int newX = lp.x;
            int newY = lp.y;
            lp.x = oldX;
            int oldY3 = oldY;
            lp.y = oldY3;
            if (oldX == newX && oldY3 == newY) {
                lp.isLockedToGrid = true;
                return true;
            }
            ValueAnimator va = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
            int newX2 = newX;
            va.setDuration((long) duration);
            this.mReorderAnimators.put(lp, va);
            ValueAnimator va2 = va;
            final LayoutParams layoutParams = lp;
            int oldY4 = oldY3;
            final int oldY5 = oldX;
            int newY2 = newY;
            final int newY3 = newX2;
            ValueAnimator va3 = va2;
            final int i3 = oldY4;
            AnonymousClass3 r11 = r0;
            int i4 = newX2;
            final int i5 = newY2;
            int i6 = oldX;
            final View view2 = child;
            AnonymousClass3 r0 = new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float r = ((Float) animation.getAnimatedValue()).floatValue();
                    layoutParams.x = (int) (((1.0f - r) * ((float) oldY5)) + (((float) newY3) * r));
                    layoutParams.y = (int) (((1.0f - r) * ((float) i3)) + (((float) i5) * r));
                    view2.requestLayout();
                }
            };
            va3.addUpdateListener(r11);
            va3.addListener(new AnimatorListenerAdapter() {
                boolean cancelled = false;

                public void onAnimationEnd(Animator animation) {
                    if (!this.cancelled) {
                        lp.isLockedToGrid = true;
                        view.requestLayout();
                    }
                    if (CellLayout.this.mReorderAnimators.containsKey(lp)) {
                        CellLayout.this.mReorderAnimators.remove(lp);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    this.cancelled = true;
                }
            });
            va3.setStartDelay((long) delay);
            va3.start();
            return true;
        }
        int i7 = delay;
        return false;
    }

    /* access modifiers changed from: package-private */
    public void visualizeDropLocation(View v, DragPreviewProvider outlineProvider, int cellX, int cellY, int spanX, int spanY, boolean resize, DropTarget.DragObject dragObject) {
        Bitmap dragOutline;
        Rect r;
        int top;
        int left;
        View view = v;
        DragPreviewProvider dragPreviewProvider = outlineProvider;
        int i = cellX;
        int i2 = cellY;
        DropTarget.DragObject dragObject2 = dragObject;
        int oldDragCellX = this.mDragCell[0];
        int oldDragCellY = this.mDragCell[1];
        if (dragPreviewProvider != null && dragPreviewProvider.generatedDragOutline != null) {
            Bitmap dragOutline2 = dragPreviewProvider.generatedDragOutline;
            if (i == oldDragCellX && i2 == oldDragCellY) {
                Bitmap bitmap = dragOutline2;
                return;
            }
            Point dragOffset = dragObject2.dragView.getDragVisualizeOffset();
            Rect dragRegion = dragObject2.dragView.getDragRegion();
            this.mDragCell[0] = i;
            this.mDragCell[1] = i2;
            int oldIndex = this.mDragOutlineCurrent;
            this.mDragOutlineAnims[oldIndex].animateOut();
            this.mDragOutlineCurrent = (oldIndex + 1) % this.mDragOutlines.length;
            Rect r2 = this.mDragOutlines[this.mDragOutlineCurrent];
            if (resize) {
                Rect r3 = r2;
                int i3 = oldIndex;
                Point point = dragOffset;
                dragOutline = dragOutline2;
                cellToRect(cellX, cellY, spanX, spanY, r3);
                if (view instanceof LauncherAppWidgetHostView) {
                    DeviceProfile profile = this.mLauncher.getDeviceProfile();
                    r = r3;
                    Utilities.shrinkRect(r, profile.appWidgetScale.x, profile.appWidgetScale.y);
                } else {
                    r = r3;
                }
            } else {
                Point dragOffset2 = dragOffset;
                dragOutline = dragOutline2;
                r = r2;
                int[] topLeft = this.mTmpPoint;
                cellToPoint(i, i2, topLeft);
                int left2 = topLeft[0];
                int top2 = topLeft[1];
                if (view != null && dragOffset2 == null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    int left3 = left2 + lp.leftMargin;
                    top = top2 + lp.topMargin + (((this.mCellHeight * spanY) - dragOutline.getHeight()) / 2);
                    left = left3 + (((this.mCellWidth * spanX) - dragOutline.getWidth()) / 2);
                    int[] iArr = topLeft;
                } else if (dragOffset2 == null || dragRegion == null) {
                    left = left2 + (((this.mCellWidth * spanX) - dragOutline.getWidth()) / 2);
                    top = top2 + (((this.mCellHeight * spanY) - dragOutline.getHeight()) / 2);
                } else {
                    left = left2 + dragOffset2.x + (((this.mCellWidth * spanX) - dragRegion.width()) / 2);
                    int cHeight = getShortcutsAndWidgets().getCellContentHeight();
                    int[] iArr2 = topLeft;
                    top = top2 + dragOffset2.y + ((int) Math.max(0.0f, ((float) (this.mCellHeight - cHeight)) / 2.0f));
                }
                r.set(left, top, dragOutline.getWidth() + left, dragOutline.getHeight() + top);
            }
            Utilities.scaleRectAboutCenter(r, 1.0f);
            this.mDragOutlineAnims[this.mDragOutlineCurrent].setTag(dragOutline);
            this.mDragOutlineAnims[this.mDragOutlineCurrent].animateIn();
            if (dragObject2.stateAnnouncer != null) {
                dragObject2.stateAnnouncer.announce(getItemMoveDescription(i, i2));
            }
        }
    }

    @SuppressLint({"StringFormatMatches"})
    public String getItemMoveDescription(int cellX, int cellY) {
        if (this.mContainerType == 1) {
            return getContext().getString(R.string.move_to_hotseat_position, new Object[]{Integer.valueOf(Math.max(cellX, cellY) + 1)});
        }
        return getContext().getString(R.string.move_to_empty_cell, new Object[]{Integer.valueOf(cellY + 1), Integer.valueOf(cellX + 1)});
    }

    public void clearDragOutlines() {
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestVacantArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, int[] result, int[] resultSpan) {
        return findNearestArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, true, result, resultSpan);
    }

    private void lazyInitTempRectStack() {
        if (this.mTempRectStack.isEmpty()) {
            for (int i = 0; i < this.mCountX * this.mCountY; i++) {
                this.mTempRectStack.push(new Rect());
            }
        }
    }

    private void recycleTempRects(Stack<Rect> used) {
        while (!used.isEmpty()) {
            this.mTempRectStack.push(used.pop());
        }
    }

    private int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, boolean ignoreOccupied, int[] result, int[] resultSpan) {
        int pixelY2;
        int pixelX2;
        int[] bestXY;
        Rect bestRect;
        Rect bestRect2;
        int ySize;
        int xSize;
        Rect bestRect3;
        int[] bestXY2;
        int i = minSpanX;
        int i2 = minSpanY;
        int i3 = spanX;
        int i4 = spanY;
        lazyInitTempRectStack();
        int pixelX3 = (int) (((float) pixelX) - (((float) (this.mCellWidth * (i3 - 1))) / 2.0f));
        int pixelY3 = (int) (((float) pixelY) - (((float) (this.mCellHeight * (i4 - 1))) / 2.0f));
        int[] bestXY3 = result != null ? result : new int[2];
        Rect bestRect4 = new Rect(-1, -1, -1, -1);
        Stack<Rect> validRegions = new Stack<>();
        int countX = this.mCountX;
        int countY = this.mCountY;
        if (i <= 0 || i2 <= 0 || i3 <= 0 || i4 <= 0 || i3 < i) {
            int i5 = pixelY3;
            Rect rect = bestRect4;
            return bestXY3;
        } else if (i4 < i2) {
            int i6 = pixelX3;
            int i7 = pixelY3;
            Rect rect2 = bestRect4;
            return bestXY3;
        } else {
            double bestDistance = Double.MAX_VALUE;
            int y = 0;
            while (y < countY - (i2 - 1)) {
                int x = 0;
                while (x < countX - (i - 1)) {
                    int i8 = -1;
                    if (ignoreOccupied) {
                        int i9 = 0;
                        while (true) {
                            int ySize2 = i8;
                            int i10 = i9;
                            if (i10 < i) {
                                int j = 0;
                                while (true) {
                                    int j2 = j;
                                    if (j2 >= i2) {
                                        break;
                                    } else if (this.mOccupied.cells[x + i10][y + j2]) {
                                        pixelX2 = pixelX3;
                                        pixelY2 = pixelY3;
                                        bestXY = bestXY3;
                                        bestRect = bestRect4;
                                        break;
                                    } else {
                                        j = j2 + 1;
                                        int j3 = minSpanX;
                                        i2 = minSpanY;
                                    }
                                }
                            } else {
                                xSize = minSpanX;
                                int ySize3 = minSpanY;
                                boolean incX = true;
                                boolean hitMaxX = xSize >= i3;
                                boolean hitMaxY = ySize3 >= i4;
                                while (true) {
                                    if (hitMaxX && hitMaxY) {
                                        break;
                                    }
                                    if (!incX || hitMaxX) {
                                        bestXY2 = bestXY3;
                                        bestRect3 = bestRect4;
                                        if (!hitMaxY) {
                                            for (int i11 = 0; i11 < xSize; i11++) {
                                                if (y + ySize3 > countY - 1 || this.mOccupied.cells[x + i11][y + ySize3]) {
                                                    hitMaxY = true;
                                                }
                                            }
                                            if (!hitMaxY) {
                                                ySize3++;
                                            }
                                        }
                                    } else {
                                        boolean hitMaxX2 = hitMaxX;
                                        int j4 = 0;
                                        while (true) {
                                            int j5 = j4;
                                            if (j5 >= ySize3) {
                                                break;
                                            }
                                            int[] bestXY4 = bestXY3;
                                            Rect bestRect5 = bestRect4;
                                            if (x + xSize > countX - 1 || this.mOccupied.cells[x + xSize][y + j5]) {
                                                hitMaxX2 = true;
                                            }
                                            j4 = j5 + 1;
                                            bestXY3 = bestXY4;
                                            bestRect4 = bestRect5;
                                        }
                                        bestXY2 = bestXY3;
                                        bestRect3 = bestRect4;
                                        if (!hitMaxX2) {
                                            xSize++;
                                        }
                                        hitMaxX = hitMaxX2;
                                    }
                                    hitMaxX |= xSize >= i3;
                                    hitMaxY |= ySize3 >= i4;
                                    incX = !incX;
                                    bestXY3 = bestXY2;
                                    bestRect4 = bestRect3;
                                }
                                boolean hitMaxX3 = xSize >= i3;
                                ySize = ySize3;
                                bestXY = bestXY3;
                                bestRect2 = bestRect4;
                            }
                            i9 = i10 + 1;
                            i8 = ySize2;
                            i = minSpanX;
                            i2 = minSpanY;
                        }
                    } else {
                        ySize = -1;
                        bestXY = bestXY3;
                        bestRect2 = bestRect4;
                        xSize = -1;
                    }
                    int[] cellXY = this.mTmpPoint;
                    cellToCenterPoint(x, y, cellXY);
                    Rect currentRect = this.mTempRectStack.pop();
                    currentRect.set(x, y, x + xSize, y + ySize);
                    boolean contained = false;
                    Iterator it = validRegions.iterator();
                    while (true) {
                        if (it.hasNext()) {
                            if (((Rect) it.next()).contains(currentRect)) {
                                contained = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    validRegions.push(currentRect);
                    pixelX2 = pixelX3;
                    pixelY2 = pixelY3;
                    double distance = Math.hypot((double) (cellXY[0] - pixelX3), (double) (cellXY[1] - pixelY3));
                    if (distance > bestDistance || contained) {
                        bestRect = bestRect2;
                        if (!currentRect.contains(bestRect)) {
                            x++;
                            bestRect4 = bestRect;
                            bestXY3 = bestXY;
                            pixelX3 = pixelX2;
                            pixelY3 = pixelY2;
                            i = minSpanX;
                            i2 = minSpanY;
                            i3 = spanX;
                            i4 = spanY;
                        }
                    } else {
                        bestRect = bestRect2;
                    }
                    bestDistance = distance;
                    bestXY[0] = x;
                    bestXY[1] = y;
                    if (resultSpan != null) {
                        resultSpan[0] = xSize;
                        resultSpan[1] = ySize;
                    }
                    bestRect.set(currentRect);
                    x++;
                    bestRect4 = bestRect;
                    bestXY3 = bestXY;
                    pixelX3 = pixelX2;
                    pixelY3 = pixelY2;
                    i = minSpanX;
                    i2 = minSpanY;
                    i3 = spanX;
                    i4 = spanY;
                }
                int pixelX4 = pixelX3;
                int i12 = pixelY3;
                int[] iArr = bestXY3;
                Rect rect3 = bestRect4;
                y++;
                pixelX3 = pixelX4;
                i = minSpanX;
                i2 = minSpanY;
                i3 = spanX;
                i4 = spanY;
            }
            int i13 = pixelY3;
            int[] bestXY5 = bestXY3;
            Rect rect4 = bestRect4;
            if (bestDistance == Double.MAX_VALUE) {
                bestXY5[0] = -1;
                bestXY5[1] = -1;
            }
            recycleTempRects(validRegions);
            return bestXY5;
        }
    }

    private int[] findNearestArea(int cellX, int cellY, int spanX, int spanY, int[] direction, boolean[][] occupied, boolean[][] blockOccupied, int[] result) {
        int i = spanX;
        int i2 = spanY;
        int[] bestXY = result != null ? result : new int[2];
        int countX = this.mCountX;
        int countY = this.mCountY;
        int bestDirectionScore = Integer.MIN_VALUE;
        float bestDistance = Float.MAX_VALUE;
        int y = 0;
        while (y < countY - (i2 - 1)) {
            int bestDirectionScore2 = bestDirectionScore;
            float bestDistance2 = bestDistance;
            int x = 0;
            while (x < countX - (i - 1)) {
                int i3 = 0;
                while (true) {
                    if (i3 < i) {
                        int j = 0;
                        while (true) {
                            int j2 = j;
                            if (j2 < i2) {
                                if (occupied[x + i3][y + j2] && (blockOccupied == null || blockOccupied[i3][j2])) {
                                    break;
                                }
                                j = j2 + 1;
                                int j3 = spanX;
                            } else {
                                break;
                            }
                        }
                    } else {
                        float distance = (float) Math.hypot((double) (x - cellX), (double) (y - cellY));
                        int[] curDirection = this.mTmpPoint;
                        computeDirectionVector((float) (x - cellX), (float) (y - cellY), curDirection);
                        int curDirectionScore = (direction[0] * curDirection[0]) + (direction[1] * curDirection[1]);
                        if (Float.compare(distance, bestDistance2) < 0 || (Float.compare(distance, bestDistance2) == 0 && curDirectionScore > bestDirectionScore2)) {
                            bestDistance2 = distance;
                            bestDirectionScore2 = curDirectionScore;
                            bestXY[0] = x;
                            bestXY[1] = y;
                        }
                    }
                    i3++;
                    i = spanX;
                }
                x++;
                i = spanX;
                i2 = spanY;
            }
            y++;
            bestDistance = bestDistance2;
            bestDirectionScore = bestDirectionScore2;
            i = spanX;
            i2 = spanY;
        }
        if (bestDistance == Float.MAX_VALUE) {
            bestXY[0] = -1;
            bestXY[1] = -1;
        }
        return bestXY;
    }

    private boolean addViewToTempLocation(View v, Rect rectOccupiedByPotentialDrop, int[] direction, ItemConfiguration currentState) {
        CellAndSpan c = currentState.map.get(v);
        boolean success = false;
        this.mTmpOccupied.markCells(c, false);
        this.mTmpOccupied.markCells(rectOccupiedByPotentialDrop, true);
        int[] iArr = direction;
        findNearestArea(c.cellX, c.cellY, c.spanX, c.spanY, iArr, this.mTmpOccupied.cells, (boolean[][]) null, this.mTempLocation);
        if (this.mTempLocation[0] >= 0 && this.mTempLocation[1] >= 0) {
            c.cellX = this.mTempLocation[0];
            c.cellY = this.mTempLocation[1];
            success = true;
        }
        this.mTmpOccupied.markCells(c, true);
        return success;
    }

    private class ViewCluster {
        static final int BOTTOM = 8;
        static final int LEFT = 1;
        static final int RIGHT = 4;
        static final int TOP = 2;
        final int[] bottomEdge = new int[CellLayout.this.mCountX];
        final Rect boundingRect = new Rect();
        boolean boundingRectDirty;
        final PositionComparator comparator = new PositionComparator();
        final ItemConfiguration config;
        int dirtyEdges;
        final int[] leftEdge = new int[CellLayout.this.mCountY];
        final int[] rightEdge = new int[CellLayout.this.mCountY];
        final int[] topEdge = new int[CellLayout.this.mCountX];
        final ArrayList<View> views;

        public ViewCluster(ArrayList<View> views2, ItemConfiguration config2) {
            this.views = (ArrayList) views2.clone();
            this.config = config2;
            resetEdges();
        }

        /* access modifiers changed from: package-private */
        public void resetEdges() {
            for (int i = 0; i < CellLayout.this.mCountX; i++) {
                this.topEdge[i] = -1;
                this.bottomEdge[i] = -1;
            }
            for (int i2 = 0; i2 < CellLayout.this.mCountY; i2++) {
                this.leftEdge[i2] = -1;
                this.rightEdge[i2] = -1;
            }
            this.dirtyEdges = 15;
            this.boundingRectDirty = true;
        }

        /* access modifiers changed from: package-private */
        public void computeEdge(int which) {
            int count = this.views.size();
            for (int i = 0; i < count; i++) {
                CellAndSpan cs = this.config.map.get(this.views.get(i));
                if (which == 4) {
                    int right = cs.cellX + cs.spanX;
                    for (int j = cs.cellY; j < cs.cellY + cs.spanY; j++) {
                        if (right > this.rightEdge[j]) {
                            this.rightEdge[j] = right;
                        }
                    }
                } else if (which != 8) {
                    switch (which) {
                        case 1:
                            int top = cs.cellX;
                            for (int j2 = cs.cellY; j2 < cs.cellY + cs.spanY; j2++) {
                                if (top < this.leftEdge[j2] || this.leftEdge[j2] < 0) {
                                    this.leftEdge[j2] = top;
                                }
                            }
                            break;
                        case 2:
                            int top2 = cs.cellY;
                            for (int j3 = cs.cellX; j3 < cs.cellX + cs.spanX; j3++) {
                                if (top2 < this.topEdge[j3] || this.topEdge[j3] < 0) {
                                    this.topEdge[j3] = top2;
                                }
                            }
                            break;
                    }
                } else {
                    int bottom = cs.cellY + cs.spanY;
                    for (int j4 = cs.cellX; j4 < cs.cellX + cs.spanX; j4++) {
                        if (bottom > this.bottomEdge[j4]) {
                            this.bottomEdge[j4] = bottom;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isViewTouchingEdge(View v, int whichEdge) {
            CellAndSpan cs = this.config.map.get(v);
            if ((this.dirtyEdges & whichEdge) == whichEdge) {
                computeEdge(whichEdge);
                this.dirtyEdges &= ~whichEdge;
            }
            if (whichEdge == 4) {
                for (int i = cs.cellY; i < cs.cellY + cs.spanY; i++) {
                    if (this.rightEdge[i] == cs.cellX) {
                        return true;
                    }
                }
                return false;
            } else if (whichEdge != 8) {
                switch (whichEdge) {
                    case 1:
                        for (int i2 = cs.cellY; i2 < cs.cellY + cs.spanY; i2++) {
                            if (this.leftEdge[i2] == cs.cellX + cs.spanX) {
                                return true;
                            }
                        }
                        return false;
                    case 2:
                        for (int i3 = cs.cellX; i3 < cs.cellX + cs.spanX; i3++) {
                            if (this.topEdge[i3] == cs.cellY + cs.spanY) {
                                return true;
                            }
                        }
                        return false;
                    default:
                        return false;
                }
            } else {
                for (int i4 = cs.cellX; i4 < cs.cellX + cs.spanX; i4++) {
                    if (this.bottomEdge[i4] == cs.cellY) {
                        return true;
                    }
                }
                return false;
            }
        }

        /* access modifiers changed from: package-private */
        public void shift(int whichEdge, int delta) {
            Iterator<View> it = this.views.iterator();
            while (it.hasNext()) {
                CellAndSpan c = this.config.map.get(it.next());
                if (whichEdge != 4) {
                    switch (whichEdge) {
                        case 1:
                            c.cellX -= delta;
                            break;
                        case 2:
                            c.cellY -= delta;
                            break;
                        default:
                            c.cellY += delta;
                            break;
                    }
                } else {
                    c.cellX += delta;
                }
            }
            resetEdges();
        }

        public void addView(View v) {
            this.views.add(v);
            resetEdges();
        }

        public Rect getBoundingRect() {
            if (this.boundingRectDirty) {
                this.config.getBoundingRectForViews(this.views, this.boundingRect);
            }
            return this.boundingRect;
        }

        class PositionComparator implements Comparator<View> {
            int whichEdge = 0;

            PositionComparator() {
            }

            public int compare(View left, View right) {
                CellAndSpan l = ViewCluster.this.config.map.get(left);
                CellAndSpan r = ViewCluster.this.config.map.get(right);
                int i = this.whichEdge;
                if (i == 4) {
                    return l.cellX - r.cellX;
                }
                switch (i) {
                    case 1:
                        return (r.cellX + r.spanX) - (l.cellX + l.spanX);
                    case 2:
                        return (r.cellY + r.spanY) - (l.cellY + l.spanY);
                    default:
                        return l.cellY - r.cellY;
                }
            }
        }

        public void sortConfigurationForEdgePush(int edge) {
            this.comparator.whichEdge = edge;
            Collections.sort(this.config.sortedViews, this.comparator);
        }
    }

    private boolean pushViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop, int[] direction, View dragView, ItemConfiguration currentState) {
        int pushDistance;
        int whichEdge;
        Rect rect = rectOccupiedByPotentialDrop;
        ItemConfiguration itemConfiguration = currentState;
        ViewCluster cluster = new ViewCluster(views, itemConfiguration);
        Rect clusterRect = cluster.getBoundingRect();
        boolean fail = false;
        if (direction[0] < 0) {
            whichEdge = 1;
            pushDistance = clusterRect.right - rect.left;
        } else if (direction[0] > 0) {
            whichEdge = 4;
            pushDistance = rect.right - clusterRect.left;
        } else if (direction[1] < 0) {
            whichEdge = 2;
            pushDistance = clusterRect.bottom - rect.top;
        } else {
            whichEdge = 8;
            pushDistance = rect.bottom - clusterRect.top;
        }
        if (pushDistance <= 0) {
            return false;
        }
        Iterator<View> it = views.iterator();
        while (it.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration.map.get(it.next()), false);
        }
        currentState.save();
        cluster.sortConfigurationForEdgePush(whichEdge);
        while (pushDistance > 0 && !fail) {
            Iterator<View> it2 = itemConfiguration.sortedViews.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    View view = dragView;
                    break;
                }
                View v = it2.next();
                if (cluster.views.contains(v)) {
                    View view2 = dragView;
                } else if (v != dragView && cluster.isViewTouchingEdge(v, whichEdge)) {
                    if (!((LayoutParams) v.getLayoutParams()).canReorder) {
                        fail = true;
                        break;
                    }
                    cluster.addView(v);
                    this.mTmpOccupied.markCells(itemConfiguration.map.get(v), false);
                }
                Rect rect2 = rectOccupiedByPotentialDrop;
            }
            pushDistance--;
            cluster.shift(whichEdge, 1);
            Rect rect3 = rectOccupiedByPotentialDrop;
        }
        View view3 = dragView;
        boolean foundSolution = false;
        Rect clusterRect2 = cluster.getBoundingRect();
        if (fail || clusterRect2.left < 0 || clusterRect2.right > this.mCountX || clusterRect2.top < 0 || clusterRect2.bottom > this.mCountY) {
            currentState.restore();
        } else {
            foundSolution = true;
        }
        Iterator<View> it3 = cluster.views.iterator();
        while (it3.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration.map.get(it3.next()), true);
        }
        return foundSolution;
    }

    private boolean addViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop, int[] direction, View dragView, ItemConfiguration currentState) {
        ItemConfiguration itemConfiguration = currentState;
        if (views.size() == 0) {
            return true;
        }
        boolean success = false;
        Rect boundingRect = new Rect();
        itemConfiguration.getBoundingRectForViews(views, boundingRect);
        Iterator<View> it = views.iterator();
        while (it.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration.map.get(it.next()), false);
        }
        GridOccupancy blockOccupied = new GridOccupancy(boundingRect.width(), boundingRect.height());
        int top = boundingRect.top;
        int left = boundingRect.left;
        Iterator<View> it2 = views.iterator();
        while (it2.hasNext()) {
            View v = it2.next();
            CellAndSpan c = itemConfiguration.map.get(v);
            CellAndSpan cellAndSpan = c;
            View view = v;
            blockOccupied.markCells(c.cellX - left, c.cellY - top, c.spanX, c.spanY, true);
        }
        this.mTmpOccupied.markCells(rectOccupiedByPotentialDrop, true);
        int i = left;
        int i2 = top;
        GridOccupancy gridOccupancy = blockOccupied;
        findNearestArea(boundingRect.left, boundingRect.top, boundingRect.width(), boundingRect.height(), direction, this.mTmpOccupied.cells, blockOccupied.cells, this.mTempLocation);
        if (this.mTempLocation[0] >= 0 && this.mTempLocation[1] >= 0) {
            int deltaX = this.mTempLocation[0] - boundingRect.left;
            int deltaY = this.mTempLocation[1] - boundingRect.top;
            Iterator<View> it3 = views.iterator();
            while (it3.hasNext()) {
                CellAndSpan c2 = itemConfiguration.map.get(it3.next());
                c2.cellX += deltaX;
                c2.cellY += deltaY;
            }
            success = true;
        }
        Iterator<View> it4 = views.iterator();
        while (it4.hasNext()) {
            this.mTmpOccupied.markCells(itemConfiguration.map.get(it4.next()), true);
        }
        return success;
    }

    private boolean attemptPushInDirection(ArrayList<View> intersectingViews, Rect occupied, int[] direction, View ignoreView, ItemConfiguration solution) {
        if (Math.abs(direction[0]) + Math.abs(direction[1]) > 1) {
            int temp = direction[1];
            direction[1] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[1] = temp;
            int temp2 = direction[0];
            direction[0] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = temp2;
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
            int temp3 = direction[1];
            direction[1] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[1] = temp3;
            int temp4 = direction[0];
            direction[0] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = temp4;
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
        } else if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
            return true;
        } else {
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
            int temp5 = direction[1];
            direction[1] = direction[0];
            direction[0] = temp5;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
            int temp6 = direction[1];
            direction[1] = direction[0];
            direction[0] = temp6;
        }
        return false;
    }

    private boolean rearrangementExists(int cellX, int cellY, int spanX, int spanY, int[] direction, View ignoreView, ItemConfiguration solution) {
        CellAndSpan c;
        int i = cellX;
        int i2 = cellY;
        View view = ignoreView;
        ItemConfiguration itemConfiguration = solution;
        if (i < 0 || i2 < 0) {
            int[] iArr = direction;
            return false;
        }
        this.mIntersectingViews.clear();
        this.mOccupiedRect.set(i, i2, i + spanX, i2 + spanY);
        if (!(view == null || (c = itemConfiguration.map.get(view)) == null)) {
            c.cellX = i;
            c.cellY = i2;
        }
        Rect r0 = new Rect(i, i2, i + spanX, i2 + spanY);
        Rect r1 = new Rect();
        Iterator<View> it = itemConfiguration.map.keySet().iterator();
        while (it.hasNext()) {
            View child = it.next();
            if (child != view) {
                CellAndSpan c2 = itemConfiguration.map.get(child);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                Iterator<View> it2 = it;
                r1.set(c2.cellX, c2.cellY, c2.cellX + c2.spanX, c2.cellY + c2.spanY);
                if (Rect.intersects(r0, r1)) {
                    if (!lp.canReorder) {
                        return false;
                    }
                    this.mIntersectingViews.add(child);
                }
                it = it2;
                int i3 = cellX;
            }
        }
        itemConfiguration.intersectingViews = new ArrayList<>(this.mIntersectingViews);
        if (attemptPushInDirection(this.mIntersectingViews, this.mOccupiedRect, direction, ignoreView, solution)) {
            return true;
        }
        if (addViewsToTempLocation(this.mIntersectingViews, this.mOccupiedRect, direction, ignoreView, solution)) {
            return true;
        }
        Iterator<View> it3 = this.mIntersectingViews.iterator();
        while (it3.hasNext()) {
            if (!addViewToTempLocation(it3.next(), this.mOccupiedRect, direction, itemConfiguration)) {
                return false;
            }
        }
        int[] iArr2 = direction;
        return true;
    }

    private void computeDirectionVector(float deltaX, float deltaY, int[] result) {
        double angle = Math.atan((double) (deltaY / deltaX));
        result[0] = 0;
        result[1] = 0;
        if (Math.abs(Math.cos(angle)) > 0.5d) {
            result[0] = (int) Math.signum(deltaX);
        }
        if (Math.abs(Math.sin(angle)) > 0.5d) {
            result[1] = (int) Math.signum(deltaY);
        }
    }

    private ItemConfiguration findReorderSolution(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, int[] direction, View dragView, boolean decX, ItemConfiguration solution) {
        int i = minSpanY;
        int i2 = spanX;
        int i3 = spanY;
        ItemConfiguration itemConfiguration = solution;
        copyCurrentStateToSolution(itemConfiguration, false);
        this.mOccupied.copyTo(this.mTmpOccupied);
        int i4 = spanX;
        int i5 = spanY;
        int[] result = findNearestArea(pixelX, pixelY, i4, i5, new int[2]);
        if (rearrangementExists(result[0], result[1], i4, i5, direction, dragView, solution)) {
            itemConfiguration.isSolution = true;
            itemConfiguration.cellX = result[0];
            itemConfiguration.cellY = result[1];
            itemConfiguration.spanX = i2;
            itemConfiguration.spanY = i3;
        } else if (i2 > minSpanX && (i == i3 || decX)) {
            return findReorderSolution(pixelX, pixelY, minSpanX, minSpanY, i2 - 1, spanY, direction, dragView, false, solution);
        } else if (i3 > i) {
            return findReorderSolution(pixelX, pixelY, minSpanX, minSpanY, spanX, i3 - 1, direction, dragView, true, solution);
        } else {
            itemConfiguration.isSolution = false;
        }
        return itemConfiguration;
    }

    private void copyCurrentStateToSolution(ItemConfiguration solution, boolean temp) {
        CellAndSpan c;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (temp) {
                c = new CellAndSpan(lp.tmpCellX, lp.tmpCellY, lp.cellHSpan, lp.cellVSpan);
            } else {
                c = new CellAndSpan(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan);
            }
            solution.add(child, c);
        }
    }

    private void copySolutionToTempState(ItemConfiguration solution, View dragView) {
        this.mTmpOccupied.clear();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i);
            if (child != dragView) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                CellAndSpan c = solution.map.get(child);
                if (c != null) {
                    lp.tmpCellX = c.cellX;
                    lp.tmpCellY = c.cellY;
                    lp.cellHSpan = c.spanX;
                    lp.cellVSpan = c.spanY;
                    this.mTmpOccupied.markCells(c, true);
                }
            }
        }
        this.mTmpOccupied.markCells((CellAndSpan) solution, true);
    }

    private void animateItemsToSolution(ItemConfiguration solution, View dragView, boolean commitDragView) {
        CellAndSpan c;
        ItemConfiguration itemConfiguration = solution;
        GridOccupancy occupied = this.mTmpOccupied;
        occupied.clear();
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= childCount) {
                break;
            }
            View child = this.mShortcutsAndWidgets.getChildAt(i2);
            if (!(child == dragView || (c = itemConfiguration.map.get(child)) == null)) {
                animateChildToPosition(child, c.cellX, c.cellY, 150, 0, false, false);
                occupied.markCells(c, true);
            }
            i = i2 + 1;
        }
        View view = dragView;
        if (commitDragView) {
            occupied.markCells((CellAndSpan) itemConfiguration, true);
        }
    }

    private void beginOrAdjustReorderPreviewAnimations(ItemConfiguration solution, View dragView, int delay, int mode) {
        ItemConfiguration itemConfiguration = solution;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < childCount) {
                View child = this.mShortcutsAndWidgets.getChildAt(i2);
                if (child != dragView) {
                    CellAndSpan c = itemConfiguration.map.get(child);
                    boolean skip = mode == 0 && itemConfiguration.intersectingViews != null && !itemConfiguration.intersectingViews.contains(child);
                    LayoutParams lp = (LayoutParams) child.getLayoutParams();
                    if (c != null && !skip) {
                        int i3 = lp.cellX;
                        int i4 = lp.cellY;
                        int i5 = c.cellX;
                        int i6 = c.cellY;
                        LayoutParams layoutParams = lp;
                        int i7 = i6;
                        CellAndSpan cellAndSpan = c;
                        new ReorderPreviewAnimation(this, child, mode, i3, i4, i5, i7, c.spanX, c.spanY).animate();
                    }
                }
                i = i2 + 1;
            } else {
                return;
            }
        }
    }

    class ReorderPreviewAnimation {
        private static final float CHILD_DIVIDEND = 4.0f;
        private static final int HINT_DURATION = 650;
        public static final int MODE_HINT = 0;
        public static final int MODE_PREVIEW = 1;
        private static final int PREVIEW_DURATION = 300;
        Animator a;
        final View child;
        float finalDeltaX;
        float finalDeltaY;
        final float finalScale;
        float initDeltaX;
        float initDeltaY;
        float initScale;
        final int mode;
        boolean repeating = false;
        final /* synthetic */ CellLayout this$0;

        public ReorderPreviewAnimation(CellLayout this$02, View child2, int mode2, int cellX0, int cellY0, int cellX1, int cellY1, int spanX, int spanY) {
            CellLayout cellLayout = this$02;
            int i = mode2;
            this.this$0 = cellLayout;
            int i2 = spanX;
            int i3 = spanY;
            this$02.regionToCenterPoint(cellX0, cellY0, i2, i3, cellLayout.mTmpPoint);
            int x0 = cellLayout.mTmpPoint[0];
            int i4 = 1;
            int y0 = cellLayout.mTmpPoint[1];
            this$02.regionToCenterPoint(cellX1, cellY1, i2, i3, cellLayout.mTmpPoint);
            int x1 = cellLayout.mTmpPoint[0];
            int y1 = cellLayout.mTmpPoint[1];
            int dX = x1 - x0;
            int dY = y1 - y0;
            this.child = child2;
            this.mode = i;
            setInitialAnimationValues(false);
            this.finalScale = (1.0f - (CHILD_DIVIDEND / ((float) child2.getWidth()))) * this.initScale;
            this.finalDeltaX = this.initDeltaX;
            this.finalDeltaY = this.initDeltaY;
            int dir = i == 0 ? -1 : i4;
            if (!(dX == dY && dX == 0)) {
                if (dY == 0) {
                    this.finalDeltaX += ((float) (-dir)) * Math.signum((float) dX) * cellLayout.mReorderPreviewAnimationMagnitude;
                } else if (dX == 0) {
                    this.finalDeltaY += ((float) (-dir)) * Math.signum((float) dY) * cellLayout.mReorderPreviewAnimationMagnitude;
                } else {
                    double angle = Math.atan((double) (((float) dY) / ((float) dX)));
                    int i5 = x1;
                    int i6 = y1;
                    int i7 = x0;
                    this.finalDeltaX += (float) ((int) (((double) (((float) (-dir)) * Math.signum((float) dX))) * Math.abs(Math.cos(angle) * ((double) cellLayout.mReorderPreviewAnimationMagnitude))));
                    int i8 = dX;
                    this.finalDeltaY += (float) ((int) (((double) (((float) (-dir)) * Math.signum((float) dY))) * Math.abs(Math.sin(angle) * ((double) cellLayout.mReorderPreviewAnimationMagnitude))));
                    return;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setInitialAnimationValues(boolean restoreOriginalValues) {
            if (!restoreOriginalValues) {
                this.initScale = this.child.getScaleX();
                this.initDeltaX = this.child.getTranslationX();
                this.initDeltaY = this.child.getTranslationY();
            } else if (this.child instanceof LauncherAppWidgetHostView) {
                LauncherAppWidgetHostView lahv = (LauncherAppWidgetHostView) this.child;
                this.initScale = lahv.getScaleToFit();
                this.initDeltaX = lahv.getTranslationForCentering().x;
                this.initDeltaY = lahv.getTranslationForCentering().y;
            } else {
                this.initScale = 1.0f;
                this.initDeltaX = 0.0f;
                this.initDeltaY = 0.0f;
            }
        }

        /* access modifiers changed from: package-private */
        public void animate() {
            boolean noMovement = this.finalDeltaX == this.initDeltaX && this.finalDeltaY == this.initDeltaY;
            if (this.this$0.mShakeAnimators.containsKey(this.child)) {
                this.this$0.mShakeAnimators.get(this.child).cancel();
                this.this$0.mShakeAnimators.remove(this.child);
                if (noMovement) {
                    completeAnimationImmediately();
                    return;
                }
            }
            if (!noMovement) {
                ValueAnimator va = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
                this.a = va;
                if (!Utilities.isPowerSaverPreventingAnimation(this.this$0.getContext())) {
                    va.setRepeatMode(2);
                    va.setRepeatCount(-1);
                }
                va.setDuration(this.mode == 0 ? 650 : 300);
                va.setStartDelay((long) ((int) (Math.random() * 60.0d)));
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float r = ((Float) animation.getAnimatedValue()).floatValue();
                        float r1 = (ReorderPreviewAnimation.this.mode != 0 || !ReorderPreviewAnimation.this.repeating) ? r : 1.0f;
                        float x = (ReorderPreviewAnimation.this.finalDeltaX * r1) + ((1.0f - r1) * ReorderPreviewAnimation.this.initDeltaX);
                        float y = (ReorderPreviewAnimation.this.finalDeltaY * r1) + ((1.0f - r1) * ReorderPreviewAnimation.this.initDeltaY);
                        ReorderPreviewAnimation.this.child.setTranslationX(x);
                        ReorderPreviewAnimation.this.child.setTranslationY(y);
                        float s = (ReorderPreviewAnimation.this.finalScale * r) + ((1.0f - r) * ReorderPreviewAnimation.this.initScale);
                        ReorderPreviewAnimation.this.child.setScaleX(s);
                        ReorderPreviewAnimation.this.child.setScaleY(s);
                    }
                });
                va.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationRepeat(Animator animation) {
                        ReorderPreviewAnimation.this.setInitialAnimationValues(true);
                        ReorderPreviewAnimation.this.repeating = true;
                    }
                });
                this.this$0.mShakeAnimators.put(this.child, this);
                va.start();
            }
        }

        private void cancel() {
            if (this.a != null) {
                this.a.cancel();
            }
        }

        /* access modifiers changed from: package-private */
        public void completeAnimationImmediately() {
            if (this.a != null) {
                this.a.cancel();
            }
            setInitialAnimationValues(true);
            this.a = LauncherAnimUtils.ofPropertyValuesHolder(this.child, new PropertyListBuilder().scale(this.initScale).translationX(this.initDeltaX).translationY(this.initDeltaY).build()).setDuration(150);
            this.a.setInterpolator(new DecelerateInterpolator(1.5f));
            this.a.start();
        }
    }

    private void completeAndClearReorderPreviewAnimations() {
        for (ReorderPreviewAnimation a : this.mShakeAnimators.values()) {
            a.completeAnimationImmediately();
        }
        this.mShakeAnimators.clear();
    }

    private void commitTempPlacement() {
        int i;
        this.mTmpOccupied.copyTo(this.mOccupied);
        long screenId = this.mLauncher.getWorkspace().getIdForScreen(this);
        int container = -100;
        if (this.mContainerType == 1) {
            screenId = -1;
            container = LauncherSettings.Favorites.CONTAINER_HOTSEAT;
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < childCount) {
                View child = this.mShortcutsAndWidgets.getChildAt(i3);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                ItemInfo info = (ItemInfo) child.getTag();
                if (info != null) {
                    boolean requiresDbUpdate = (info.cellX == lp.tmpCellX && info.cellY == lp.tmpCellY && info.spanX == lp.cellHSpan && info.spanY == lp.cellVSpan) ? false : true;
                    int i4 = lp.tmpCellX;
                    lp.cellX = i4;
                    info.cellX = i4;
                    int i5 = lp.tmpCellY;
                    lp.cellY = i5;
                    info.cellY = i5;
                    info.spanX = lp.cellHSpan;
                    info.spanY = lp.cellVSpan;
                    if (requiresDbUpdate) {
                        int i6 = info.cellX;
                        int i7 = info.cellY;
                        int i8 = info.spanX;
                        int i9 = info.spanY;
                        ItemInfo itemInfo = info;
                        LayoutParams layoutParams = lp;
                        i = i3;
                        this.mLauncher.getModelWriter().modifyItemInDatabase(info, (long) container, screenId, i6, i7, i8, i9);
                        i2 = i + 1;
                    }
                }
                i = i3;
                i2 = i + 1;
            } else {
                return;
            }
        }
    }

    private void setUseTempCoords(boolean useTempCoords) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()).useTmpCoords = useTempCoords;
        }
    }

    private ItemConfiguration findConfigurationNoShuffle(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, View dragView, ItemConfiguration solution) {
        ItemConfiguration itemConfiguration = solution;
        int[] result = new int[2];
        int[] resultSpan = new int[2];
        findNearestVacantArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, result, resultSpan);
        if (result[0] < 0 || result[1] < 0) {
            itemConfiguration.isSolution = false;
        } else {
            copyCurrentStateToSolution(itemConfiguration, false);
            itemConfiguration.cellX = result[0];
            itemConfiguration.cellY = result[1];
            itemConfiguration.spanX = resultSpan[0];
            itemConfiguration.spanY = resultSpan[1];
            itemConfiguration.isSolution = true;
        }
        return itemConfiguration;
    }

    private void getDirectionVectorForDrop(int dragViewCenterX, int dragViewCenterY, int spanX, int spanY, View dragView, int[] resultDirection) {
        int i = spanX;
        int i2 = spanY;
        int[] iArr = resultDirection;
        int[] targetDestination = new int[2];
        int i3 = spanX;
        int i4 = spanY;
        findNearestArea(dragViewCenterX, dragViewCenterY, i3, i4, targetDestination);
        Rect dragRect = new Rect();
        regionToRect(targetDestination[0], targetDestination[1], i3, i4, dragRect);
        dragRect.offset(dragViewCenterX - dragRect.centerX(), dragViewCenterY - dragRect.centerY());
        Rect dropRegionRect = new Rect();
        Rect dropRegionRect2 = dropRegionRect;
        getViewsIntersectingRegion(targetDestination[0], targetDestination[1], i3, i4, dragView, dropRegionRect, this.mIntersectingViews);
        int dropRegionSpanX = dropRegionRect2.width();
        int dropRegionSpanY = dropRegionRect2.height();
        Rect dropRegionRect3 = dropRegionRect2;
        Rect dropRegionRect4 = dropRegionRect3;
        regionToRect(dropRegionRect3.left, dropRegionRect3.top, dropRegionRect3.width(), dropRegionRect3.height(), dropRegionRect3);
        int deltaX = (dropRegionRect4.centerX() - dragViewCenterX) / i;
        int deltaY = (dropRegionRect4.centerY() - dragViewCenterY) / i2;
        if (dropRegionSpanX == this.mCountX || i == this.mCountX) {
            deltaX = 0;
        }
        if (dropRegionSpanY == this.mCountY || i2 == this.mCountY) {
            deltaY = 0;
        }
        if (deltaX == 0 && deltaY == 0) {
            iArr[0] = 1;
            iArr[1] = 0;
            return;
        }
        computeDirectionVector((float) deltaX, (float) deltaY, iArr);
    }

    private void getViewsIntersectingRegion(int cellX, int cellY, int spanX, int spanY, View dragView, Rect boundingRect, ArrayList<View> intersectingViews) {
        int i = cellX;
        int i2 = cellY;
        Rect rect = boundingRect;
        if (rect != null) {
            rect.set(cellX, i2, i + spanX, i2 + spanY);
        }
        intersectingViews.clear();
        Rect r0 = new Rect(cellX, i2, i + spanX, i2 + spanY);
        Rect r1 = new Rect();
        int count = this.mShortcutsAndWidgets.getChildCount();
        int i3 = 0;
        while (i3 < count) {
            View child = this.mShortcutsAndWidgets.getChildAt(i3);
            if (child != dragView) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                r1.set(lp.cellX, lp.cellY, lp.cellX + lp.cellHSpan, lp.cellY + lp.cellVSpan);
                if (Rect.intersects(r0, r1)) {
                    this.mIntersectingViews.add(child);
                    if (rect != null) {
                        rect.union(r1);
                    }
                }
            }
            i3++;
            int i4 = cellX;
            int i5 = cellY;
        }
        View view = dragView;
    }

    /* access modifiers changed from: package-private */
    public boolean isNearestDropLocationOccupied(int pixelX, int pixelY, int spanX, int spanY, View dragView, int[] result) {
        int[] result2 = findNearestArea(pixelX, pixelY, spanX, spanY, result);
        getViewsIntersectingRegion(result2[0], result2[1], spanX, spanY, dragView, (Rect) null, this.mIntersectingViews);
        return true ^ this.mIntersectingViews.isEmpty();
    }

    /* access modifiers changed from: package-private */
    public void revertTempState() {
        completeAndClearReorderPreviewAnimations();
        if (isItemPlacementDirty()) {
            int count = this.mShortcutsAndWidgets.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = this.mShortcutsAndWidgets.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.tmpCellX != lp.cellX || lp.tmpCellY != lp.cellY) {
                    lp.tmpCellX = lp.cellX;
                    lp.tmpCellY = lp.cellY;
                    animateChildToPosition(child, lp.cellX, lp.cellY, 150, 0, false, false);
                }
            }
            setItemPlacementDirty(false);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean createAreaForResize(int cellX, int cellY, int spanX, int spanY, View dragView, int[] direction, boolean commit) {
        View view = dragView;
        boolean z = commit;
        int[] pixelXY = new int[2];
        int i = spanX;
        int i2 = spanY;
        regionToCenterPoint(cellX, cellY, i, i2, pixelXY);
        ItemConfiguration swapSolution = findReorderSolution(pixelXY[0], pixelXY[1], i, i2, spanX, spanY, direction, dragView, true, new ItemConfiguration());
        setUseTempCoords(true);
        if (swapSolution != null && swapSolution.isSolution) {
            copySolutionToTempState(swapSolution, view);
            setItemPlacementDirty(true);
            animateItemsToSolution(swapSolution, view, z);
            if (z) {
                commitTempPlacement();
                completeAndClearReorderPreviewAnimations();
                setItemPlacementDirty(false);
            } else {
                beginOrAdjustReorderPreviewAnimations(swapSolution, view, 150, 1);
            }
            this.mShortcutsAndWidgets.requestLayout();
        }
        return swapSolution.isSolution;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00fa, code lost:
        if (r13 == 3) goto L_0x00fe;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int[] performReorder(int r20, int r21, int r22, int r23, int r24, int r25, android.view.View r26, int[] r27, int[] r28, int r29) {
        /*
            r19 = this;
            r11 = r19
            r12 = r26
            r13 = r29
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r24
            r4 = r25
            r5 = r27
            int[] r14 = r0.findNearestArea(r1, r2, r3, r4, r5)
            r15 = 2
            if (r28 != 0) goto L_0x001e
            int[] r0 = new int[r15]
            r16 = r0
            goto L_0x0020
        L_0x001e:
            r16 = r28
        L_0x0020:
            r10 = 3
            r9 = 1
            r8 = 0
            if (r13 == r15) goto L_0x002a
            if (r13 == r10) goto L_0x002a
            r0 = 4
            if (r13 != r0) goto L_0x004f
        L_0x002a:
            int[] r0 = r11.mPreviousReorderDirection
            r0 = r0[r8]
            r1 = -100
            if (r0 == r1) goto L_0x004f
            int[] r0 = r11.mDirectionVector
            int[] r2 = r11.mPreviousReorderDirection
            r2 = r2[r8]
            r0[r8] = r2
            int[] r0 = r11.mDirectionVector
            int[] r2 = r11.mPreviousReorderDirection
            r2 = r2[r9]
            r0[r9] = r2
            if (r13 == r15) goto L_0x0046
            if (r13 != r10) goto L_0x0070
        L_0x0046:
            int[] r0 = r11.mPreviousReorderDirection
            r0[r8] = r1
            int[] r0 = r11.mPreviousReorderDirection
            r0[r9] = r1
            goto L_0x0070
        L_0x004f:
            int[] r6 = r11.mDirectionVector
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r24
            r4 = r25
            r5 = r26
            r0.getDirectionVectorForDrop(r1, r2, r3, r4, r5, r6)
            int[] r0 = r11.mPreviousReorderDirection
            int[] r1 = r11.mDirectionVector
            r1 = r1[r8]
            r0[r8] = r1
            int[] r0 = r11.mPreviousReorderDirection
            int[] r1 = r11.mDirectionVector
            r1 = r1[r9]
            r0[r9] = r1
        L_0x0070:
            int[] r7 = r11.mDirectionVector
            r17 = 1
            com.android.launcher3.CellLayout$ItemConfiguration r6 = new com.android.launcher3.CellLayout$ItemConfiguration
            r5 = 0
            r6.<init>()
            r0 = r19
            r1 = r20
            r2 = r21
            r3 = r22
            r4 = r23
            r15 = r5
            r5 = r24
            r18 = r6
            r6 = r25
            r8 = r26
            r9 = r17
            r10 = r18
            com.android.launcher3.CellLayout$ItemConfiguration r9 = r0.findReorderSolution(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            com.android.launcher3.CellLayout$ItemConfiguration r8 = new com.android.launcher3.CellLayout$ItemConfiguration
            r8.<init>()
            r7 = r26
            com.android.launcher3.CellLayout$ItemConfiguration r0 = r0.findConfigurationNoShuffle(r1, r2, r3, r4, r5, r6, r7, r8)
            r1 = 0
            boolean r2 = r9.isSolution
            if (r2 == 0) goto L_0x00b1
            int r2 = r9.area()
            int r3 = r0.area()
            if (r2 < r3) goto L_0x00b1
            r1 = r9
            goto L_0x00b6
        L_0x00b1:
            boolean r2 = r0.isSolution
            if (r2 == 0) goto L_0x00b6
            r1 = r0
        L_0x00b6:
            r2 = -1
            if (r13 != 0) goto L_0x00dc
            if (r1 == 0) goto L_0x00d1
            r3 = 0
            r11.beginOrAdjustReorderPreviewAnimations(r1, r12, r3, r3)
            int r2 = r1.cellX
            r14[r3] = r2
            int r2 = r1.cellY
            r4 = 1
            r14[r4] = r2
            int r2 = r1.spanX
            r16[r3] = r2
            int r2 = r1.spanY
            r16[r4] = r2
            goto L_0x00db
        L_0x00d1:
            r3 = 0
            r4 = 1
            r16[r4] = r2
            r16[r3] = r2
            r14[r4] = r2
            r14[r3] = r2
        L_0x00db:
            return r14
        L_0x00dc:
            r3 = 0
            r4 = 1
            r5 = 1
            r11.setUseTempCoords(r4)
            if (r1 == 0) goto L_0x0122
            int r2 = r1.cellX
            r14[r3] = r2
            int r2 = r1.cellY
            r14[r4] = r2
            int r2 = r1.spanX
            r16[r3] = r2
            int r2 = r1.spanY
            r16[r4] = r2
            if (r13 == r4) goto L_0x00fd
            r2 = 2
            if (r13 == r2) goto L_0x00fd
            r2 = 3
            if (r13 != r2) goto L_0x012b
            goto L_0x00fe
        L_0x00fd:
            r2 = 3
        L_0x00fe:
            r11.copySolutionToTempState(r1, r12)
            r11.setItemPlacementDirty(r4)
            r6 = 2
            if (r13 != r6) goto L_0x0109
            r7 = 1
            goto L_0x010a
        L_0x0109:
            r7 = 0
        L_0x010a:
            r11.animateItemsToSolution(r1, r12, r7)
            if (r13 == r6) goto L_0x0118
            if (r13 != r2) goto L_0x0112
            goto L_0x0118
        L_0x0112:
            r2 = 150(0x96, float:2.1E-43)
            r11.beginOrAdjustReorderPreviewAnimations(r1, r12, r2, r4)
            goto L_0x012b
        L_0x0118:
            r19.commitTempPlacement()
            r19.completeAndClearReorderPreviewAnimations()
            r11.setItemPlacementDirty(r3)
            goto L_0x012b
        L_0x0122:
            r5 = 0
            r16[r4] = r2
            r16[r3] = r2
            r14[r4] = r2
            r14[r3] = r2
        L_0x012b:
            r2 = 2
            if (r13 == r2) goto L_0x0130
            if (r5 != 0) goto L_0x0133
        L_0x0130:
            r11.setUseTempCoords(r3)
        L_0x0133:
            com.android.launcher3.ShortcutAndWidgetContainer r2 = r11.mShortcutsAndWidgets
            r2.requestLayout()
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.CellLayout.performReorder(int, int, int, int, int, int, android.view.View, int[], int[], int):int[]");
    }

    /* access modifiers changed from: package-private */
    public void setItemPlacementDirty(boolean dirty) {
        this.mItemPlacementDirty = dirty;
    }

    /* access modifiers changed from: package-private */
    public boolean isItemPlacementDirty() {
        return this.mItemPlacementDirty;
    }

    private static class ItemConfiguration extends CellAndSpan {
        ArrayList<View> intersectingViews;
        boolean isSolution;
        final ArrayMap<View, CellAndSpan> map;
        private final ArrayMap<View, CellAndSpan> savedMap;
        final ArrayList<View> sortedViews;

        private ItemConfiguration() {
            this.map = new ArrayMap<>();
            this.savedMap = new ArrayMap<>();
            this.sortedViews = new ArrayList<>();
            this.isSolution = false;
        }

        /* access modifiers changed from: package-private */
        public void save() {
            for (View v : this.map.keySet()) {
                this.savedMap.get(v).copyFrom(this.map.get(v));
            }
        }

        /* access modifiers changed from: package-private */
        public void restore() {
            for (View v : this.savedMap.keySet()) {
                this.map.get(v).copyFrom(this.savedMap.get(v));
            }
        }

        /* access modifiers changed from: package-private */
        public void add(View v, CellAndSpan cs) {
            this.map.put(v, cs);
            this.savedMap.put(v, new CellAndSpan());
            this.sortedViews.add(v);
        }

        /* access modifiers changed from: package-private */
        public int area() {
            return this.spanX * this.spanY;
        }

        /* access modifiers changed from: package-private */
        public void getBoundingRectForViews(ArrayList<View> views, Rect outRect) {
            boolean first = true;
            Iterator<View> it = views.iterator();
            while (it.hasNext()) {
                CellAndSpan c = this.map.get(it.next());
                if (first) {
                    outRect.set(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
                    first = false;
                } else {
                    outRect.union(c.cellX, c.cellY, c.cellX + c.spanX, c.cellY + c.spanY);
                }
            }
        }
    }

    public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, spanX, spanY, false, result, (int[]) null);
    }

    /* access modifiers changed from: package-private */
    public boolean existsEmptyCell() {
        return findCellForSpan((int[]) null, 1, 1);
    }

    public boolean findCellForSpan(int[] cellXY, int spanX, int spanY) {
        if (cellXY == null) {
            cellXY = new int[2];
        }
        return this.mOccupied.findVacantCell(cellXY, spanX, spanY);
    }

    /* access modifiers changed from: package-private */
    public void onDragEnter() {
        this.mDragging = true;
    }

    /* access modifiers changed from: package-private */
    public void onDragExit() {
        if (this.mDragging) {
            this.mDragging = false;
        }
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        this.mDragOutlineCurrent = (this.mDragOutlineCurrent + 1) % this.mDragOutlineAnims.length;
        revertTempState();
        setIsDragOverlapping(false);
    }

    /* access modifiers changed from: package-private */
    public void onDropChild(View child) {
        if (child != null) {
            ((LayoutParams) child.getLayoutParams()).dropped = true;
            child.requestLayout();
            markCellsAsOccupiedForView(child);
        }
    }

    public void cellToRect(int cellX, int cellY, int cellHSpan, int cellVSpan, Rect resultRect) {
        int cellWidth = this.mCellWidth;
        int cellHeight = this.mCellHeight;
        int x = (cellX * cellWidth) + getPaddingLeft();
        int y = (cellY * cellHeight) + getPaddingTop();
        resultRect.set(x, y, x + (cellHSpan * cellWidth), y + (cellVSpan * cellHeight));
    }

    public void markCellsAsOccupiedForView(View view) {
        if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            this.mOccupied.markCells(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, true);
        }
    }

    public void markCellsAsUnoccupiedForView(View view) {
        if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            this.mOccupied.markCells(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, false);
        }
    }

    public int getDesiredWidth() {
        return getPaddingLeft() + getPaddingRight() + (this.mCountX * this.mCellWidth);
    }

    public int getDesiredHeight() {
        return getPaddingTop() + getPaddingBottom() + (this.mCountY * this.mCellHeight);
    }

    public boolean isOccupied(int x, int y) {
        if (x < this.mCountX && y < this.mCountY) {
            return this.mOccupied.cells[x][y];
        }
        throw new RuntimeException("Position exceeds the bound of this CellLayout");
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
        public boolean canReorder;
        @ViewDebug.ExportedProperty
        public int cellHSpan;
        @ViewDebug.ExportedProperty
        public int cellVSpan;
        @ViewDebug.ExportedProperty
        public int cellX;
        @ViewDebug.ExportedProperty
        public int cellY;
        boolean dropped;
        public boolean isLockedToGrid;
        public int tmpCellX;
        public int tmpCellY;
        public boolean useTmpCoords;
        @ViewDebug.ExportedProperty
        public int x;
        @ViewDebug.ExportedProperty
        public int y;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellHSpan = 1;
            this.cellVSpan = 1;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellX = source.cellX;
            this.cellY = source.cellY;
            this.cellHSpan = source.cellHSpan;
            this.cellVSpan = source.cellVSpan;
        }

        public LayoutParams(int cellX2, int cellY2, int cellHSpan2, int cellVSpan2) {
            super(-1, -1);
            this.isLockedToGrid = true;
            this.canReorder = true;
            this.cellX = cellX2;
            this.cellY = cellY2;
            this.cellHSpan = cellHSpan2;
            this.cellVSpan = cellVSpan2;
        }

        public void setup(int cellWidth, int cellHeight, boolean invertHorizontally, int colCount) {
            setup(cellWidth, cellHeight, invertHorizontally, colCount, 1.0f, 1.0f);
        }

        public void setup(int cellWidth, int cellHeight, boolean invertHorizontally, int colCount, float cellScaleX, float cellScaleY) {
            if (this.isLockedToGrid) {
                int myCellHSpan = this.cellHSpan;
                int myCellVSpan = this.cellVSpan;
                int myCellX = this.useTmpCoords ? this.tmpCellX : this.cellX;
                int myCellY = this.useTmpCoords ? this.tmpCellY : this.cellY;
                if (invertHorizontally) {
                    myCellX = (colCount - myCellX) - this.cellHSpan;
                }
                this.width = (int) (((((float) (myCellHSpan * cellWidth)) / cellScaleX) - ((float) this.leftMargin)) - ((float) this.rightMargin));
                this.height = (int) (((((float) (myCellVSpan * cellHeight)) / cellScaleY) - ((float) this.topMargin)) - ((float) this.bottomMargin));
                this.x = (myCellX * cellWidth) + this.leftMargin;
                this.y = (myCellY * cellHeight) + this.topMargin;
            }
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ")";
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getWidth() {
            return this.width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return this.height;
        }

        public void setX(int x2) {
            this.x = x2;
        }

        public int getX() {
            return this.x;
        }

        public void setY(int y2) {
            this.y = y2;
        }

        public int getY() {
            return this.y;
        }
    }

    public static final class CellInfo extends CellAndSpan {
        public final View cell;
        final long container;
        final long screenId;

        public CellInfo(View v, ItemInfo info) {
            this.cellX = info.cellX;
            this.cellY = info.cellY;
            this.spanX = info.spanX;
            this.spanY = info.spanY;
            this.cell = v;
            this.screenId = info.screenId;
            this.container = info.container;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Cell[view=");
            sb.append(this.cell == null ? "null" : this.cell.getClass());
            sb.append(", x=");
            sb.append(this.cellX);
            sb.append(", y=");
            sb.append(this.cellY);
            sb.append("]");
            return sb.toString();
        }
    }

    public boolean hasReorderSolution(ItemInfo itemInfo) {
        ItemInfo itemInfo2 = itemInfo;
        int[] cellPoint = new int[2];
        char c = 0;
        int cellX = 0;
        while (true) {
            int cellX2 = cellX;
            if (cellX2 < getCountX()) {
                int cellY = 0;
                while (true) {
                    int cellY2 = cellY;
                    if (cellY2 >= getCountY()) {
                        break;
                    }
                    cellToPoint(cellX2, cellY2, cellPoint);
                    int cellY3 = cellY2;
                    if (findReorderSolution(cellPoint[c], cellPoint[1], itemInfo2.minSpanX, itemInfo2.minSpanY, itemInfo2.spanX, itemInfo2.spanY, this.mDirectionVector, (View) null, true, new ItemConfiguration()).isSolution) {
                        return true;
                    }
                    cellY = cellY3 + 1;
                    c = 0;
                }
            } else {
                return false;
            }
            cellX = cellX2 + 1;
            c = 0;
        }
    }

    public boolean isRegionVacant(int x, int y, int spanX, int spanY) {
        return this.mOccupied.isRegionVacant(x, y, spanX, spanY);
    }
}
