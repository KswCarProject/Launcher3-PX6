package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import com.szchoiceway.index.DropTarget;
import com.szchoiceway.index.FolderIcon;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

public class CellLayout extends ViewGroup {
    private static final boolean DEBUG_VISUALIZE_OCCUPIED = false;
    private static final boolean DESTRUCTIVE_REORDER = false;
    private static final int INVALID_DIRECTION = -100;
    static final int LANDSCAPE = 0;
    public static final int MODE_ACCEPT_DROP = 3;
    public static final int MODE_DRAG_OVER = 0;
    public static final int MODE_ON_DROP = 1;
    public static final int MODE_ON_DROP_EXTERNAL = 2;
    static final int PORTRAIT = 1;
    private static final int REORDER_ANIMATION_DURATION = 150;
    private static final float REORDER_HINT_MAGNITUDE = 0.12f;
    static final String TAG = "CellLayout";
    private static final PorterDuffXfermode sAddBlendMode = new PorterDuffXfermode(PorterDuff.Mode.ADD);
    private static final Paint sPaint = new Paint();
    private Drawable mActiveGlowBackground;
    private float mBackgroundAlpha;
    private float mBackgroundAlphaMultiplier;
    private Rect mBackgroundRect;
    private int mCellHeight;
    private final CellInfo mCellInfo;
    private int mCellWidth;
    /* access modifiers changed from: private */
    public int mCountX;
    /* access modifiers changed from: private */
    public int mCountY;
    private int[] mDirectionVector;
    private final int[] mDragCell;
    private final Point mDragCenter;
    private DropTarget.DragEnforcer mDragEnforcer;
    /* access modifiers changed from: private */
    public float[] mDragOutlineAlphas;
    private InterruptibleInOutAnimator[] mDragOutlineAnims;
    private int mDragOutlineCurrent;
    private final Paint mDragOutlinePaint;
    /* access modifiers changed from: private */
    public Rect[] mDragOutlines;
    private boolean mDragging;
    private TimeInterpolator mEaseOutInterpolator;
    private int[] mFolderLeaveBehindCell;
    private ArrayList<FolderIcon.FolderRingAnimator> mFolderOuterRings;
    private int mForegroundAlpha;
    private int mForegroundPadding;
    private Rect mForegroundRect;
    private int mHeightGap;
    private float mHotseatScale;
    private View.OnTouchListener mInterceptTouchListener;
    private ArrayList<View> mIntersectingViews;
    private boolean mIsDragOverlapping;
    private boolean mIsHotseat;
    private boolean mItemPlacementDirty;
    private boolean mLastDownOnOccupiedCell;
    private Launcher mLauncher;
    private int mMaxGap;
    private Drawable mNormalBackground;
    boolean[][] mOccupied;
    private Rect mOccupiedRect;
    private int mOriginalHeightGap;
    private int mOriginalWidthGap;
    private Drawable mOverScrollForegroundDrawable;
    private Drawable mOverScrollLeft;
    private Drawable mOverScrollRight;
    private BubbleTextView mPressedOrFocusedIcon;
    int[] mPreviousReorderDirection;
    private final Rect mRect;
    /* access modifiers changed from: private */
    public HashMap<LayoutParams, Animator> mReorderAnimators;
    /* access modifiers changed from: private */
    public float mReorderHintAnimationMagnitude;
    private boolean mScrollingTransformsDirty;
    /* access modifiers changed from: private */
    public HashMap<View, ReorderHintAnimation> mShakeAnimators;
    private ShortcutAndWidgetContainer mShortcutsAndWidgets;
    int[] mTempLocation;
    private final Stack<Rect> mTempRectStack;
    boolean[][] mTmpOccupied;
    /* access modifiers changed from: private */
    public final int[] mTmpPoint;
    private final int[] mTmpXY;
    private int mWidthGap;
    Rect temp;

    public CellLayout(Context context) {
        this(context, (AttributeSet) null);
    }

    public CellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScrollingTransformsDirty = false;
        this.mRect = new Rect();
        this.mCellInfo = new CellInfo();
        this.mTmpXY = new int[2];
        this.mTmpPoint = new int[2];
        this.mTempLocation = new int[2];
        this.mLastDownOnOccupiedCell = false;
        this.mFolderOuterRings = new ArrayList<>();
        this.mFolderLeaveBehindCell = new int[]{-1, -1};
        this.mForegroundAlpha = 0;
        this.mBackgroundAlphaMultiplier = 1.0f;
        this.mIsDragOverlapping = false;
        this.mDragCenter = new Point();
        this.mDragOutlines = new Rect[4];
        this.mDragOutlineAlphas = new float[this.mDragOutlines.length];
        this.mDragOutlineAnims = new InterruptibleInOutAnimator[this.mDragOutlines.length];
        this.mDragOutlineCurrent = 0;
        this.mDragOutlinePaint = new Paint();
        this.mReorderAnimators = new HashMap<>();
        this.mShakeAnimators = new HashMap<>();
        this.mItemPlacementDirty = false;
        this.mDragCell = new int[2];
        this.mDragging = false;
        this.mIsHotseat = false;
        this.mHotseatScale = 1.0f;
        this.mIntersectingViews = new ArrayList<>();
        this.mOccupiedRect = new Rect();
        this.mDirectionVector = new int[2];
        this.mPreviousReorderDirection = new int[2];
        this.temp = new Rect();
        this.mTempRectStack = new Stack<>();
        this.mDragEnforcer = new DropTarget.DragEnforcer(context);
        setWillNotDraw(false);
        setClipToPadding(false);
        this.mLauncher = (Launcher) context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CellLayout, defStyle, 0);
        this.mCellWidth = a.getDimensionPixelSize(0, 10);
        this.mCellHeight = a.getDimensionPixelSize(1, 10);
        int dimensionPixelSize = a.getDimensionPixelSize(2, 0);
        this.mOriginalWidthGap = dimensionPixelSize;
        this.mWidthGap = dimensionPixelSize;
        int dimensionPixelSize2 = a.getDimensionPixelSize(3, 0);
        this.mOriginalHeightGap = dimensionPixelSize2;
        this.mHeightGap = dimensionPixelSize2;
        this.mMaxGap = a.getDimensionPixelSize(4, 0);
        this.mCountX = LauncherModel.getCellCountX();
        this.mCountY = LauncherModel.getCellCountY();
        this.mOccupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.mCountX, this.mCountY});
        this.mTmpOccupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.mCountX, this.mCountY});
        this.mPreviousReorderDirection[0] = INVALID_DIRECTION;
        this.mPreviousReorderDirection[1] = INVALID_DIRECTION;
        a.recycle();
        setAlwaysDrawnWithCacheEnabled(false);
        Resources res = getResources();
        this.mHotseatScale = ((float) res.getInteger(R.integer.hotseat_item_scale_percentage)) / 100.0f;
        this.mNormalBackground = res.getDrawable(R.drawable.homescreen_blue_normal_holo);
        this.mActiveGlowBackground = res.getDrawable(R.drawable.homescreen_blue_strong_holo);
        this.mOverScrollLeft = res.getDrawable(R.drawable.overscroll_glow_left);
        this.mOverScrollRight = res.getDrawable(R.drawable.overscroll_glow_right);
        this.mForegroundPadding = res.getDimensionPixelSize(R.dimen.workspace_overscroll_drawable_padding);
        this.mReorderHintAnimationMagnitude = REORDER_HINT_MAGNITUDE * ((float) res.getDimensionPixelSize(R.dimen.app_icon_size));
        this.mNormalBackground.setFilterBitmap(true);
        this.mActiveGlowBackground.setFilterBitmap(true);
        this.mEaseOutInterpolator = new DecelerateInterpolator(2.5f);
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
        for (int i = 0; i < this.mDragOutlines.length; i++) {
            this.mDragOutlines[i] = new Rect(-1, -1, -1, -1);
        }
        int duration = res.getInteger(R.integer.config_dragOutlineFadeTime);
        float toAlphaValue = (float) res.getInteger(R.integer.config_dragOutlineMaxAlpha);
        Arrays.fill(this.mDragOutlineAlphas, 0.0f);
        for (int i2 = 0; i2 < this.mDragOutlineAnims.length; i2++) {
            final InterruptibleInOutAnimator anim = new InterruptibleInOutAnimator(this, (long) duration, 0.0f, toAlphaValue);
            anim.getAnimator().setInterpolator(this.mEaseOutInterpolator);
            final int i3 = i2;
            anim.getAnimator().addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (((Bitmap) anim.getTag()) == null) {
                        animation.cancel();
                        return;
                    }
                    CellLayout.this.mDragOutlineAlphas[i3] = ((Float) animation.getAnimatedValue()).floatValue();
                    CellLayout.this.invalidate(CellLayout.this.mDragOutlines[i3]);
                }
            });
            anim.getAnimator().addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (((Float) ((ValueAnimator) animation).getAnimatedValue()).floatValue() == 0.0f) {
                        anim.setTag((Object) null);
                    }
                }
            });
            this.mDragOutlineAnims[i2] = anim;
        }
        this.mBackgroundRect = new Rect();
        this.mForegroundRect = new Rect();
        this.mShortcutsAndWidgets = new ShortcutAndWidgetContainer(context);
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX);
        addView(this.mShortcutsAndWidgets);
    }

    static int widthInPortrait(Resources r, int numCells) {
        int cellWidth = r.getDimensionPixelSize(R.dimen.workspace_cell_width);
        return ((numCells - 1) * Math.min(r.getDimensionPixelSize(R.dimen.workspace_width_gap), r.getDimensionPixelSize(R.dimen.workspace_height_gap))) + (cellWidth * numCells);
    }

    static int heightInLandscape(Resources r, int numCells) {
        int cellHeight = r.getDimensionPixelSize(R.dimen.workspace_cell_height);
        return ((numCells - 1) * Math.min(r.getDimensionPixelSize(R.dimen.workspace_width_gap), r.getDimensionPixelSize(R.dimen.workspace_height_gap))) + (cellHeight * numCells);
    }

    public void enableHardwareLayers() {
        this.mShortcutsAndWidgets.setLayerType(2, sPaint);
    }

    public void disableHardwareLayers() {
        this.mShortcutsAndWidgets.setLayerType(0, sPaint);
    }

    public void buildHardwareLayer() {
        this.mShortcutsAndWidgets.buildLayer();
    }

    public float getChildrenScale() {
        if (this.mIsHotseat) {
            return this.mHotseatScale;
        }
        return 0.9f;
    }

    public void setGridSize(int x, int y) {
        this.mCountX = x;
        this.mCountY = y;
        this.mOccupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.mCountX, this.mCountY});
        this.mTmpOccupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{this.mCountX, this.mCountY});
        this.mTempRectStack.clear();
        this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX);
        requestLayout();
    }

    public void setInvertIfRtl(boolean invert) {
        this.mShortcutsAndWidgets.setInvertIfRtl(invert);
    }

    private void invalidateBubbleTextView(BubbleTextView icon) {
        Log.i(TAG, "invalidateBubbleTextView: ");
        int padding = icon.getPressedOrFocusedBackgroundPadding();
        invalidate((icon.getLeft() + getPaddingLeft()) - padding, (icon.getTop() + getPaddingTop()) - padding, icon.getRight() + getPaddingLeft() + padding, icon.getBottom() + getPaddingTop() + padding);
    }

    /* access modifiers changed from: package-private */
    public void setOverScrollAmount(float r, boolean left) {
        if (left && this.mOverScrollForegroundDrawable != this.mOverScrollLeft) {
            this.mOverScrollForegroundDrawable = this.mOverScrollLeft;
        } else if (!left && this.mOverScrollForegroundDrawable != this.mOverScrollRight) {
            this.mOverScrollForegroundDrawable = this.mOverScrollRight;
        }
        this.mForegroundAlpha = Math.round(255.0f * r);
        this.mOverScrollForegroundDrawable.setAlpha(this.mForegroundAlpha);
        invalidate();
    }

    /* access modifiers changed from: package-private */
    public void setPressedOrFocusedIcon(BubbleTextView icon) {
        BubbleTextView oldIcon = this.mPressedOrFocusedIcon;
        this.mPressedOrFocusedIcon = icon;
        if (oldIcon != null) {
            invalidateBubbleTextView(oldIcon);
        }
        if (this.mPressedOrFocusedIcon != null) {
            invalidateBubbleTextView(this.mPressedOrFocusedIcon);
        }
    }

    /* access modifiers changed from: package-private */
    public void setIsDragOverlapping(boolean isDragOverlapping) {
        if (this.mIsDragOverlapping != isDragOverlapping) {
            this.mIsDragOverlapping = isDragOverlapping;
            invalidate();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getIsDragOverlapping() {
        return this.mIsDragOverlapping;
    }

    /* access modifiers changed from: protected */
    public void setOverscrollTransformsDirty(boolean dirty) {
        this.mScrollingTransformsDirty = dirty;
    }

    /* access modifiers changed from: protected */
    public void resetOverscrollTransforms() {
        if (this.mScrollingTransformsDirty) {
            setOverscrollTransformsDirty(false);
            setTranslationX(0.0f);
            setRotationY(0.0f);
            setOverScrollAmount(0.0f, false);
            setPivotX((float) (getMeasuredWidth() / 2));
            setPivotY((float) (getMeasuredHeight() / 2));
        }
    }

    public void scaleRect(Rect r, float scale) {
        if (scale != 1.0f) {
            r.left = (int) ((((float) r.left) * scale) + 0.5f);
            r.top = (int) ((((float) r.top) * scale) + 0.5f);
            r.right = (int) ((((float) r.right) * scale) + 0.5f);
            r.bottom = (int) ((((float) r.bottom) * scale) + 0.5f);
        }
    }

    /* access modifiers changed from: package-private */
    public void scaleRectAboutCenter(Rect in, Rect out, float scale) {
        int cx = in.centerX();
        int cy = in.centerY();
        out.set(in);
        out.offset(-cx, -cy);
        scaleRect(out, scale);
        out.offset(cx, cy);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        Drawable bg;
        if (this.mBackgroundAlpha > 0.0f) {
            if (this.mIsDragOverlapping) {
                bg = this.mActiveGlowBackground;
            } else {
                bg = this.mNormalBackground;
            }
            bg.setAlpha((int) (this.mBackgroundAlpha * this.mBackgroundAlphaMultiplier * 255.0f));
            bg.setBounds(this.mBackgroundRect);
            bg.draw(canvas);
        }
        Paint paint = this.mDragOutlinePaint;
        for (int i = 0; i < this.mDragOutlines.length; i++) {
            float alpha = this.mDragOutlineAlphas[i];
            if (alpha > 0.0f) {
                scaleRectAboutCenter(this.mDragOutlines[i], this.temp, getChildrenScale());
                paint.setAlpha((int) (0.5f + alpha));
                Canvas canvas2 = canvas;
                canvas2.drawBitmap((Bitmap) this.mDragOutlineAnims[i].getTag(), (Rect) null, this.temp, paint);
            }
        }
        if (this.mPressedOrFocusedIcon != null) {
            int padding = this.mPressedOrFocusedIcon.getPressedOrFocusedBackgroundPadding();
            Bitmap b = this.mPressedOrFocusedIcon.getPressedOrFocusedBackground();
            if (b != null) {
                canvas.drawBitmap(b, (float) ((this.mPressedOrFocusedIcon.getLeft() + getPaddingLeft()) - padding), (float) ((this.mPressedOrFocusedIcon.getTop() + getPaddingTop()) - padding), (Paint) null);
            }
        }
        int previewOffset = FolderIcon.FolderRingAnimator.sPreviewSize;
        for (int i2 = 0; i2 < this.mFolderOuterRings.size(); i2++) {
            FolderIcon.FolderRingAnimator fra = this.mFolderOuterRings.get(i2);
            Drawable d = FolderIcon.FolderRingAnimator.sSharedOuterRingDrawable;
            int width = (int) fra.getOuterRingSize();
            int height = width;
            cellToPoint(fra.mCellX, fra.mCellY, this.mTempLocation);
            int centerX = this.mTempLocation[0] + (this.mCellWidth / 2);
            int centerY = this.mTempLocation[1] + (previewOffset / 2);
            canvas.save();
            canvas.translate((float) (centerX - (width / 2)), (float) (centerY - (height / 2)));
            d.setBounds(0, 0, width, height);
            d.draw(canvas);
            canvas.restore();
            Drawable d2 = FolderIcon.FolderRingAnimator.sSharedInnerRingDrawable;
            int width2 = (int) fra.getInnerRingSize();
            cellToPoint(fra.mCellX, fra.mCellY, this.mTempLocation);
            int centerX2 = this.mTempLocation[0] + (this.mCellWidth / 2);
            int centerY2 = this.mTempLocation[1] + (previewOffset / 2);
            canvas.save();
            canvas.translate((float) (centerX2 - (width2 / 2)), (float) (centerY2 - (width2 / 2)));
            d2.setBounds(0, 0, width2, width2);
            d2.draw(canvas);
            canvas.restore();
        }
        if (this.mFolderLeaveBehindCell[0] >= 0 && this.mFolderLeaveBehindCell[1] >= 0) {
            Drawable d3 = FolderIcon.sSharedFolderLeaveBehind;
            int width3 = d3.getIntrinsicWidth();
            int height2 = d3.getIntrinsicHeight();
            cellToPoint(this.mFolderLeaveBehindCell[0], this.mFolderLeaveBehindCell[1], this.mTempLocation);
            int centerX3 = this.mTempLocation[0] + (this.mCellWidth / 2);
            int centerY3 = this.mTempLocation[1] + (previewOffset / 2);
            canvas.save();
            canvas.translate((float) (centerX3 - (width3 / 2)), (float) (centerY3 - (width3 / 2)));
            d3.setBounds(0, 0, width3, height2);
            d3.draw(canvas);
            canvas.restore();
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mForegroundAlpha > 0) {
            this.mOverScrollForegroundDrawable.setBounds(this.mForegroundRect);
            Paint p = ((NinePatchDrawable) this.mOverScrollForegroundDrawable).getPaint();
            p.setXfermode(sAddBlendMode);
            this.mOverScrollForegroundDrawable.draw(canvas);
            p.setXfermode((Xfermode) null);
        }
    }

    public void showFolderAccept(FolderIcon.FolderRingAnimator fra) {
        this.mFolderOuterRings.add(fra);
    }

    public void hideFolderAccept(FolderIcon.FolderRingAnimator fra) {
        if (this.mFolderOuterRings.contains(fra)) {
            this.mFolderOuterRings.remove(fra);
        }
        invalidate();
    }

    public void setFolderLeaveBehindCell(int x, int y) {
        this.mFolderLeaveBehindCell[0] = x;
        this.mFolderLeaveBehindCell[1] = y;
        invalidate();
    }

    public void clearFolderLeaveBehind() {
        this.mFolderLeaveBehindCell[0] = -1;
        this.mFolderLeaveBehindCell[1] = -1;
        invalidate();
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }

    public void restoreInstanceState(SparseArray<Parcelable> states) {
        dispatchRestoreInstanceState(states);
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

    /* access modifiers changed from: package-private */
    public int getCountX() {
        return this.mCountX;
    }

    /* access modifiers changed from: package-private */
    public int getCountY() {
        return this.mCountY;
    }

    public void setIsHotseat(boolean isHotseat) {
        this.mIsHotseat = isHotseat;
    }

    public boolean addViewToCellLayout(View child, int index, int childId, LayoutParams params, boolean markCells) {
        LayoutParams lp = params;
        if (child instanceof BubbleTextView) {
            ((BubbleTextView) child).setTextColor(getResources().getColor(R.color.workspace_icon_text_color));
        }
        child.setScaleX(getChildrenScale());
        child.setScaleY(getChildrenScale());
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
        clearOccupiedCells();
        this.mShortcutsAndWidgets.removeAllViews();
    }

    public void removeAllViewsInLayout() {
        if (this.mShortcutsAndWidgets.getChildCount() > 0) {
            clearOccupiedCells();
            this.mShortcutsAndWidgets.removeAllViewsInLayout();
        }
    }

    public void removeViewWithoutMarkingCells(View view) {
        this.mShortcutsAndWidgets.removeView(view);
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mCellInfo.screen = ((ViewGroup) getParent()).indexOfChild(this);
    }

    public void setTagToCellInfoForPoint(int touchX, int touchY) {
        CellInfo cellInfo = this.mCellInfo;
        Rect frame = this.mRect;
        int x = touchX + getScrollX();
        int y = touchY + getScrollY();
        boolean found = false;
        int i = this.mShortcutsAndWidgets.getChildCount() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            View child = this.mShortcutsAndWidgets.getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if ((child.getVisibility() == 0 || child.getAnimation() != null) && lp.isLockedToGrid) {
                child.getHitRect(frame);
                float scale = child.getScaleX();
                frame = new Rect(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
                frame.offset(getPaddingLeft(), getPaddingTop());
                frame.inset((int) ((((float) frame.width()) * (1.0f - scale)) / 2.0f), (int) ((((float) frame.height()) * (1.0f - scale)) / 2.0f));
                if (frame.contains(x, y)) {
                    cellInfo.cell = child;
                    cellInfo.cellX = lp.cellX;
                    cellInfo.cellY = lp.cellY;
                    cellInfo.spanX = lp.cellHSpan;
                    cellInfo.spanY = lp.cellVSpan;
                    found = true;
                    break;
                }
            }
            i--;
        }
        this.mLastDownOnOccupiedCell = found;
        if (!found) {
            int[] cellXY = this.mTmpXY;
            pointToCellExact(x, y, cellXY);
            cellInfo.cell = null;
            cellInfo.cellX = cellXY[0];
            cellInfo.cellY = cellXY[1];
            cellInfo.spanX = 1;
            cellInfo.spanY = 1;
        }
        setTag(cellInfo);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == 0) {
            clearTagCellInfo();
        }
        if (this.mInterceptTouchListener != null && this.mInterceptTouchListener.onTouch(this, ev)) {
            return true;
        }
        if (action == 0) {
            setTagToCellInfoForPoint((int) ev.getX(), (int) ev.getY());
        }
        return false;
    }

    private void clearTagCellInfo() {
        CellInfo cellInfo = this.mCellInfo;
        cellInfo.cell = null;
        cellInfo.cellX = -1;
        cellInfo.cellY = -1;
        cellInfo.spanX = 0;
        cellInfo.spanY = 0;
        setTag(cellInfo);
    }

    public CellInfo getTag() {
        return (CellInfo) super.getTag();
    }

    /* access modifiers changed from: package-private */
    public void pointToCellExact(int x, int y, int[] result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        result[0] = (x - hStartPadding) / (this.mCellWidth + this.mWidthGap);
        result[1] = (y - vStartPadding) / (this.mCellHeight + this.mHeightGap);
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
        result[0] = ((this.mCellWidth + this.mWidthGap) * cellX) + hStartPadding;
        result[1] = ((this.mCellHeight + this.mHeightGap) * cellY) + vStartPadding;
    }

    /* access modifiers changed from: package-private */
    public void cellToCenterPoint(int cellX, int cellY, int[] result) {
        regionToCenterPoint(cellX, cellY, 1, 1, result);
    }

    /* access modifiers changed from: package-private */
    public void regionToCenterPoint(int cellX, int cellY, int spanX, int spanY, int[] result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        result[0] = ((this.mCellWidth + this.mWidthGap) * cellX) + hStartPadding + (((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) / 2);
        result[1] = ((this.mCellHeight + this.mHeightGap) * cellY) + vStartPadding + (((this.mCellHeight * spanY) + ((spanY - 1) * this.mHeightGap)) / 2);
    }

    /* access modifiers changed from: package-private */
    public void regionToRect(int cellX, int cellY, int spanX, int spanY, Rect result) {
        int hStartPadding = getPaddingLeft();
        int vStartPadding = getPaddingTop();
        int left = hStartPadding + ((this.mCellWidth + this.mWidthGap) * cellX);
        int top = vStartPadding + ((this.mCellHeight + this.mHeightGap) * cellY);
        result.set(left, top, (this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap) + left, (this.mCellHeight * spanY) + ((spanY - 1) * this.mHeightGap) + top);
    }

    public float getDistanceFromCell(float x, float y, int[] cell) {
        cellToCenterPoint(cell[0], cell[1], this.mTmpPoint);
        return (float) Math.sqrt(Math.pow((double) (x - ((float) this.mTmpPoint[0])), 2.0d) + Math.pow((double) (y - ((float) this.mTmpPoint[1])), 2.0d));
    }

    /* access modifiers changed from: package-private */
    public int getCellWidth() {
        return this.mCellWidth;
    }

    /* access modifiers changed from: package-private */
    public int getCellHeight() {
        return this.mCellHeight;
    }

    /* access modifiers changed from: package-private */
    public int getWidthGap() {
        return this.mWidthGap;
    }

    /* access modifiers changed from: package-private */
    public int getHeightGap() {
        return this.mHeightGap;
    }

    /* access modifiers changed from: package-private */
    public Rect getContentRect(Rect r) {
        if (r == null) {
            r = new Rect();
        }
        int left = getPaddingLeft();
        int top = getPaddingTop();
        r.set(left, top, ((getWidth() + left) - getPaddingLeft()) - getPaddingRight(), ((getHeight() + top) - getPaddingTop()) - getPaddingBottom());
        return r;
    }

    static void getMetrics(Rect metrics, Resources res, int measureWidth, int measureHeight, int countX, int countY, int orientation) {
        int cellWidth;
        int cellHeight;
        int widthGap;
        int heightGap;
        int paddingLeft;
        int paddingRight;
        int paddingTop;
        int paddingBottom;
        int numWidthGaps = countX - 1;
        int numHeightGaps = countY - 1;
        int maxGap = res.getDimensionPixelSize(R.dimen.workspace_max_gap);
        if (orientation == 0) {
            cellWidth = res.getDimensionPixelSize(R.dimen.workspace_cell_width_land);
            cellHeight = res.getDimensionPixelSize(R.dimen.workspace_cell_height_land);
            if (LauncherApplication.SetUIType() == 5) {
                widthGap = res.getDimensionPixelSize(R.dimen.workspace_width_gap_land_keshangui4);
            } else {
                widthGap = res.getDimensionPixelSize(R.dimen.workspace_width_gap_land);
            }
            heightGap = res.getDimensionPixelSize(R.dimen.workspace_height_gap_land);
            paddingLeft = res.getDimensionPixelSize(R.dimen.cell_layout_left_padding_land);
            paddingRight = res.getDimensionPixelSize(R.dimen.cell_layout_right_padding_land);
            paddingTop = res.getDimensionPixelSize(R.dimen.cell_layout_top_padding_land);
            paddingBottom = res.getDimensionPixelSize(R.dimen.cell_layout_bottom_padding_land);
        } else {
            cellWidth = res.getDimensionPixelSize(R.dimen.workspace_cell_width_port);
            cellHeight = res.getDimensionPixelSize(R.dimen.workspace_cell_height_port);
            widthGap = res.getDimensionPixelSize(R.dimen.workspace_width_gap_port);
            heightGap = res.getDimensionPixelSize(R.dimen.workspace_height_gap_port);
            paddingLeft = res.getDimensionPixelSize(R.dimen.cell_layout_left_padding_port);
            paddingRight = res.getDimensionPixelSize(R.dimen.cell_layout_right_padding_port);
            paddingTop = res.getDimensionPixelSize(R.dimen.cell_layout_top_padding_port);
            paddingBottom = res.getDimensionPixelSize(R.dimen.cell_layout_bottom_padding_port);
        }
        if (widthGap < 0 || heightGap < 0) {
            int vFreeSpace = ((measureHeight - paddingTop) - paddingBottom) - (countY * cellHeight);
            widthGap = Math.min(maxGap, numWidthGaps > 0 ? (((measureWidth - paddingLeft) - paddingRight) - (countX * cellWidth)) / numWidthGaps : 0);
            heightGap = Math.min(maxGap, numHeightGaps > 0 ? vFreeSpace / numHeightGaps : 0);
        }
        metrics.set(cellWidth, cellHeight, widthGap, heightGap);
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
        int numWidthGaps = this.mCountX - 1;
        int numHeightGaps = this.mCountY - 1;
        if (this.mOriginalWidthGap < 0 || this.mOriginalHeightGap < 0) {
            int hSpace = (widthSpecSize - getPaddingLeft()) - getPaddingRight();
            int vSpace = (heightSpecSize - getPaddingTop()) - getPaddingBottom();
            int hFreeSpace = hSpace - (this.mCountX * this.mCellWidth);
            int vFreeSpace = vSpace - (this.mCountY * this.mCellHeight);
            this.mWidthGap = Math.min(this.mMaxGap, numWidthGaps > 0 ? hFreeSpace / numWidthGaps : 0);
            this.mHeightGap = Math.min(this.mMaxGap, numHeightGaps > 0 ? vFreeSpace / numHeightGaps : 0);
            this.mShortcutsAndWidgets.setCellDimensions(this.mCellWidth, this.mCellHeight, this.mWidthGap, this.mHeightGap, this.mCountX);
        } else {
            this.mWidthGap = this.mOriginalWidthGap;
            this.mHeightGap = this.mOriginalHeightGap;
        }
        int newWidth = widthSpecSize;
        int newHeight = heightSpecSize;
        if (widthSpecMode == Integer.MIN_VALUE) {
            newWidth = getPaddingLeft() + getPaddingRight() + (this.mCountX * this.mCellWidth) + ((this.mCountX - 1) * this.mWidthGap);
            newHeight = getPaddingTop() + getPaddingBottom() + (this.mCountY * this.mCellHeight) + ((this.mCountY - 1) * this.mHeightGap);
            setMeasuredDimension(newWidth, newHeight);
        }
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(View.MeasureSpec.makeMeasureSpec((newWidth - getPaddingLeft()) - getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec((newHeight - getPaddingTop()) - getPaddingBottom(), 1073741824));
        }
        setMeasuredDimension(newWidth, newHeight);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).layout(getPaddingLeft(), getPaddingTop(), (r - l) - getPaddingRight(), (b - t) - getPaddingBottom());
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mBackgroundRect.set(0, 0, w, h);
        this.mForegroundRect.set(this.mForegroundPadding, this.mForegroundPadding, w - this.mForegroundPadding, h - this.mForegroundPadding);
    }

    /* access modifiers changed from: protected */
    public void setChildrenDrawingCacheEnabled(boolean enabled) {
        this.mShortcutsAndWidgets.setChildrenDrawingCacheEnabled(enabled);
    }

    /* access modifiers changed from: protected */
    public void setChildrenDrawnWithCacheEnabled(boolean enabled) {
        this.mShortcutsAndWidgets.setChildrenDrawnWithCacheEnabled(enabled);
    }

    public float getBackgroundAlpha() {
        return this.mBackgroundAlpha;
    }

    public void setBackgroundAlphaMultiplier(float multiplier) {
        if (this.mBackgroundAlphaMultiplier != multiplier) {
            this.mBackgroundAlphaMultiplier = multiplier;
            invalidate();
        }
    }

    public float getBackgroundAlphaMultiplier() {
        return this.mBackgroundAlphaMultiplier;
    }

    public void setBackgroundAlpha(float alpha) {
        if (this.mBackgroundAlpha != alpha) {
            this.mBackgroundAlpha = alpha;
            invalidate();
        }
    }

    public void setShortcutAndWidgetAlpha(float alpha) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).setAlpha(alpha);
        }
    }

    public ShortcutAndWidgetContainer getShortcutsAndWidgets() {
        if (getChildCount() > 0) {
            return (ShortcutAndWidgetContainer) getChildAt(0);
        }
        return null;
    }

    public View getChildAt(int x, int y) {
        return this.mShortcutsAndWidgets.getChildAt(x, y);
    }

    public boolean animateChildToPosition(final View child, int cellX, int cellY, int duration, int delay, boolean permanent, boolean adjustOccupied) {
        ShortcutAndWidgetContainer clc = getShortcutsAndWidgets();
        boolean[][] occupied = this.mOccupied;
        if (!permanent) {
            occupied = this.mTmpOccupied;
        }
        if (clc.indexOfChild(child) == -1) {
            return false;
        }
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        ItemInfo info = (ItemInfo) child.getTag();
        if (this.mReorderAnimators.containsKey(lp)) {
            this.mReorderAnimators.get(lp).cancel();
            this.mReorderAnimators.remove(lp);
        }
        final int oldX = lp.x;
        final int oldY = lp.y;
        if (adjustOccupied) {
            occupied[lp.cellX][lp.cellY] = false;
            occupied[cellX][cellY] = true;
        }
        lp.isLockedToGrid = true;
        if (permanent) {
            info.cellX = cellX;
            lp.cellX = cellX;
            info.cellY = cellY;
            lp.cellY = cellY;
        } else {
            lp.tmpCellX = cellX;
            lp.tmpCellY = cellY;
        }
        clc.setupLp(lp);
        lp.isLockedToGrid = false;
        final int newX = lp.x;
        final int newY = lp.y;
        lp.x = oldX;
        lp.y = oldY;
        if (oldX == newX && oldY == newY) {
            lp.isLockedToGrid = true;
            return true;
        }
        ValueAnimator va = LauncherAnimUtils.ofFloat(child, 0.0f, 1.0f);
        va.setDuration((long) duration);
        this.mReorderAnimators.put(lp, va);
        final View view = child;
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float r = ((Float) animation.getAnimatedValue()).floatValue();
                lp.x = (int) (((1.0f - r) * ((float) oldX)) + (((float) newX) * r));
                lp.y = (int) (((1.0f - r) * ((float) oldY)) + (((float) newY) * r));
                view.requestLayout();
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            boolean cancelled = false;

            public void onAnimationEnd(Animator animation) {
                if (!this.cancelled) {
                    lp.isLockedToGrid = true;
                    child.requestLayout();
                }
                if (CellLayout.this.mReorderAnimators.containsKey(lp)) {
                    CellLayout.this.mReorderAnimators.remove(lp);
                }
            }

            public void onAnimationCancel(Animator animation) {
                this.cancelled = true;
            }
        });
        va.setStartDelay((long) delay);
        va.start();
        return true;
    }

    /* access modifiers changed from: package-private */
    public void estimateDropCell(int originX, int originY, int spanX, int spanY, int[] result) {
        int countX = this.mCountX;
        int countY = this.mCountY;
        pointToCellRounded(originX, originY, result);
        int rightOverhang = (result[0] + spanX) - countX;
        if (rightOverhang > 0) {
            result[0] = result[0] - rightOverhang;
        }
        result[0] = Math.max(0, result[0]);
        int bottomOverhang = (result[1] + spanY) - countY;
        if (bottomOverhang > 0) {
            result[1] = result[1] - bottomOverhang;
        }
        result[1] = Math.max(0, result[1]);
    }

    /* access modifiers changed from: package-private */
    public void visualizeDropLocation(View v, Bitmap dragOutline, int originX, int originY, int cellX, int cellY, int spanX, int spanY, boolean resize, Point dragOffset, Rect dragRegion) {
        int left;
        int top;
        int oldDragCellX = this.mDragCell[0];
        int oldDragCellY = this.mDragCell[1];
        if (v == null || dragOffset != null) {
            this.mDragCenter.set(originX, originY);
        } else {
            this.mDragCenter.set((v.getWidth() / 2) + originX, (v.getHeight() / 2) + originY);
        }
        if (dragOutline != null || v != null) {
            if (cellX != oldDragCellX || cellY != oldDragCellY) {
                this.mDragCell[0] = cellX;
                this.mDragCell[1] = cellY;
                int[] topLeft = this.mTmpPoint;
                cellToPoint(cellX, cellY, topLeft);
                int left2 = topLeft[0];
                int top2 = topLeft[1];
                if (v != null && dragOffset == null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                    int left3 = left2 + lp.leftMargin;
                    top = top2 + lp.topMargin + ((v.getHeight() - dragOutline.getHeight()) / 2);
                    left = left3 + ((((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) - dragOutline.getWidth()) / 2);
                } else if (dragOffset == null || dragRegion == null) {
                    left = left2 + ((((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) - dragOutline.getWidth()) / 2);
                    top = top2 + ((((this.mCellHeight * spanY) + ((spanY - 1) * this.mHeightGap)) - dragOutline.getHeight()) / 2);
                } else {
                    left = left2 + dragOffset.x + ((((this.mCellWidth * spanX) + ((spanX - 1) * this.mWidthGap)) - dragRegion.width()) / 2);
                    top = top2 + dragOffset.y;
                }
                int oldIndex = this.mDragOutlineCurrent;
                this.mDragOutlineAnims[oldIndex].animateOut();
                this.mDragOutlineCurrent = (oldIndex + 1) % this.mDragOutlines.length;
                Rect r = this.mDragOutlines[this.mDragOutlineCurrent];
                r.set(left, top, dragOutline.getWidth() + left, dragOutline.getHeight() + top);
                if (resize) {
                    cellToRect(cellX, cellY, spanX, spanY, r);
                }
                this.mDragOutlineAnims[this.mDragOutlineCurrent].setTag(dragOutline);
                this.mDragOutlineAnims[this.mDragOutlineCurrent].animateIn();
            }
        }
    }

    public void clearDragOutlines() {
        this.mDragOutlineAnims[this.mDragOutlineCurrent].animateOut();
        int[] iArr = this.mDragCell;
        this.mDragCell[1] = -1;
        iArr[0] = -1;
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestVacantArea(int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestVacantArea(pixelX, pixelY, spanX, spanY, (View) null, result);
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestVacantArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, int[] result, int[] resultSpan) {
        return findNearestVacantArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, (View) null, result, resultSpan);
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, View ignoreView, boolean ignoreOccupied, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, spanX, spanY, ignoreView, ignoreOccupied, result, (int[]) null, this.mOccupied);
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

    /* access modifiers changed from: package-private */
    public int[] findNearestArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, View ignoreView, boolean ignoreOccupied, int[] result, int[] resultSpan, boolean[][] occupied) {
        int[] bestXY;
        lazyInitTempRectStack();
        markCellsAsUnoccupiedForView(ignoreView, occupied);
        int pixelX2 = (int) (((float) pixelX) - (((float) ((this.mCellWidth + this.mWidthGap) * (spanX - 1))) / 2.0f));
        int pixelY2 = (int) (((float) pixelY) - (((float) ((this.mCellHeight + this.mHeightGap) * (spanY - 1))) / 2.0f));
        if (result != null) {
            bestXY = result;
        } else {
            bestXY = new int[2];
        }
        double bestDistance = Double.MAX_VALUE;
        Rect bestRect = new Rect(-1, -1, -1, -1);
        Stack<Rect> validRegions = new Stack<>();
        int countX = this.mCountX;
        int countY = this.mCountY;
        if (minSpanX > 0 && minSpanY > 0 && spanX > 0 && spanY > 0 && spanX >= minSpanX && spanY >= minSpanY) {
            for (int y = 0; y < countY - (minSpanY - 1); y++) {
                int x = 0;
                while (x < countX - (minSpanX - 1)) {
                    int ySize = -1;
                    int xSize = -1;
                    if (ignoreOccupied) {
                        int i = 0;
                        while (true) {
                            if (i < minSpanX) {
                                for (int j = 0; j < minSpanY; j++) {
                                    if (occupied[x + i][y + j]) {
                                        break;
                                    }
                                }
                                i++;
                            } else {
                                xSize = minSpanX;
                                ySize = minSpanY;
                                boolean incX = true;
                                boolean hitMaxX = xSize >= spanX;
                                boolean hitMaxY = ySize >= spanY;
                                while (true) {
                                    if (hitMaxX && hitMaxY) {
                                        break;
                                    }
                                    if (incX && !hitMaxX) {
                                        for (int j2 = 0; j2 < ySize; j2++) {
                                            if (x + xSize > countX - 1 || occupied[x + xSize][y + j2]) {
                                                hitMaxX = true;
                                            }
                                        }
                                        if (!hitMaxX) {
                                            xSize++;
                                        }
                                    } else if (!hitMaxY) {
                                        for (int i2 = 0; i2 < xSize; i2++) {
                                            if (y + ySize > countY - 1 || occupied[x + i2][y + ySize]) {
                                                hitMaxY = true;
                                            }
                                        }
                                        if (!hitMaxY) {
                                            ySize++;
                                        }
                                    }
                                    hitMaxX |= xSize >= spanX;
                                    hitMaxY |= ySize >= spanY;
                                    if (!incX) {
                                        incX = true;
                                    } else {
                                        incX = false;
                                    }
                                }
                                if (xSize >= spanX) {
                                }
                                if (ySize >= spanY) {
                                }
                            }
                        }
                        x++;
                    }
                    int[] cellXY = this.mTmpXY;
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
                    double distance = Math.sqrt(Math.pow((double) (cellXY[0] - pixelX2), 2.0d) + Math.pow((double) (cellXY[1] - pixelY2), 2.0d));
                    if ((distance > bestDistance || contained) && !currentRect.contains(bestRect)) {
                        x++;
                    } else {
                        bestDistance = distance;
                        bestXY[0] = x;
                        bestXY[1] = y;
                        if (resultSpan != null) {
                            resultSpan[0] = xSize;
                            resultSpan[1] = ySize;
                        }
                        bestRect.set(currentRect);
                        x++;
                    }
                }
            }
            markCellsAsOccupiedForView(ignoreView, occupied);
            if (bestDistance == Double.MAX_VALUE) {
                bestXY[0] = -1;
                bestXY[1] = -1;
            }
            recycleTempRects(validRegions);
        }
        return bestXY;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00af, code lost:
        r18 = r26[0];
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int[] findNearestArea(int r22, int r23, int r24, int r25, int[] r26, boolean[][] r27, boolean[][] r28, int[] r29) {
        /*
            r21 = this;
            if (r29 == 0) goto L_0x0048
            r6 = r29
        L_0x0004:
            r5 = 2139095039(0x7f7fffff, float:3.4028235E38)
            r4 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r21
            int r7 = r0.mCountX
            r0 = r21
            int r8 = r0.mCountY
            r17 = 0
        L_0x0013:
            int r18 = r25 + -1
            int r18 = r8 - r18
            r0 = r17
            r1 = r18
            if (r0 >= r1) goto L_0x00e2
            r16 = 0
        L_0x001f:
            int r18 = r24 + -1
            int r18 = r7 - r18
            r0 = r16
            r1 = r18
            if (r0 >= r1) goto L_0x00de
            r14 = 0
        L_0x002a:
            r0 = r24
            if (r14 >= r0) goto L_0x0055
            r15 = 0
        L_0x002f:
            r0 = r25
            if (r15 >= r0) goto L_0x0052
            int r18 = r16 + r14
            r18 = r27[r18]
            int r19 = r17 + r15
            boolean r18 = r18[r19]
            if (r18 == 0) goto L_0x004f
            if (r28 == 0) goto L_0x0045
            r18 = r28[r14]
            boolean r18 = r18[r15]
            if (r18 == 0) goto L_0x004f
        L_0x0045:
            int r16 = r16 + 1
            goto L_0x001f
        L_0x0048:
            r18 = 2
            r0 = r18
            int[] r6 = new int[r0]
            goto L_0x0004
        L_0x004f:
            int r15 = r15 + 1
            goto L_0x002f
        L_0x0052:
            int r14 = r14 + 1
            goto L_0x002a
        L_0x0055:
            int r18 = r16 - r22
            int r19 = r16 - r22
            int r18 = r18 * r19
            int r19 = r17 - r23
            int r20 = r17 - r23
            int r19 = r19 * r20
            int r18 = r18 + r19
            r0 = r18
            double r0 = (double) r0
            r18 = r0
            double r18 = java.lang.Math.sqrt(r18)
            r0 = r18
            float r12 = (float) r0
            r0 = r21
            int[] r9 = r0.mTmpPoint
            int r18 = r16 - r22
            r0 = r18
            float r0 = (float) r0
            r18 = r0
            int r19 = r17 - r23
            r0 = r19
            float r0 = (float) r0
            r19 = r0
            r0 = r21
            r1 = r18
            r2 = r19
            r0.computeDirectionVector(r1, r2, r9)
            r18 = 0
            r18 = r26[r18]
            r19 = 0
            r19 = r9[r19]
            int r18 = r18 * r19
            r19 = 1
            r19 = r26[r19]
            r20 = 1
            r20 = r9[r20]
            int r19 = r19 * r20
            int r10 = r18 + r19
            r13 = 0
            r18 = 0
            r18 = r26[r18]
            r19 = 0
            r19 = r9[r19]
            r0 = r18
            r1 = r19
            if (r0 != r1) goto L_0x00dc
            r18 = 0
            r18 = r26[r18]
            r19 = 0
            r19 = r9[r19]
            r0 = r18
            r1 = r19
            if (r0 != r1) goto L_0x00dc
            r11 = 1
        L_0x00be:
            if (r11 != 0) goto L_0x00c2
            if (r13 != 0) goto L_0x00c8
        L_0x00c2:
            int r18 = java.lang.Float.compare(r12, r5)
            if (r18 < 0) goto L_0x00d0
        L_0x00c8:
            int r18 = java.lang.Float.compare(r12, r5)
            if (r18 != 0) goto L_0x0045
            if (r10 <= r4) goto L_0x0045
        L_0x00d0:
            r5 = r12
            r4 = r10
            r18 = 0
            r6[r18] = r16
            r18 = 1
            r6[r18] = r17
            goto L_0x0045
        L_0x00dc:
            r11 = 0
            goto L_0x00be
        L_0x00de:
            int r17 = r17 + 1
            goto L_0x0013
        L_0x00e2:
            r18 = 2139095039(0x7f7fffff, float:3.4028235E38)
            int r18 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r18 != 0) goto L_0x00f5
            r18 = 0
            r19 = -1
            r6[r18] = r19
            r18 = 1
            r19 = -1
            r6[r18] = r19
        L_0x00f5:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.CellLayout.findNearestArea(int, int, int, int, int[], boolean[][], boolean[][], int[]):int[]");
    }

    private boolean addViewToTempLocation(View v, Rect rectOccupiedByPotentialDrop, int[] direction, ItemConfiguration currentState) {
        CellAndSpan c = currentState.map.get(v);
        boolean success = false;
        markCellsForView(c.x, c.y, c.spanX, c.spanY, this.mTmpOccupied, false);
        markCellsForRect(rectOccupiedByPotentialDrop, this.mTmpOccupied, true);
        findNearestArea(c.x, c.y, c.spanX, c.spanY, direction, this.mTmpOccupied, (boolean[][]) null, this.mTempLocation);
        if (this.mTempLocation[0] >= 0 && this.mTempLocation[1] >= 0) {
            c.x = this.mTempLocation[0];
            c.y = this.mTempLocation[1];
            success = true;
        }
        markCellsForView(c.x, c.y, c.spanX, c.spanY, this.mTmpOccupied, true);
        return success;
    }

    private class ViewCluster {
        static final int BOTTOM = 3;
        static final int LEFT = 0;
        static final int RIGHT = 2;
        static final int TOP = 1;
        int[] bottomEdge = new int[CellLayout.this.mCountX];
        boolean bottomEdgeDirty;
        Rect boundingRect = new Rect();
        boolean boundingRectDirty;
        PositionComparator comparator = new PositionComparator();
        ItemConfiguration config;
        int[] leftEdge = new int[CellLayout.this.mCountY];
        boolean leftEdgeDirty;
        int[] rightEdge = new int[CellLayout.this.mCountY];
        boolean rightEdgeDirty;
        int[] topEdge = new int[CellLayout.this.mCountX];
        boolean topEdgeDirty;
        ArrayList<View> views;

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
            this.leftEdgeDirty = true;
            this.rightEdgeDirty = true;
            this.bottomEdgeDirty = true;
            this.topEdgeDirty = true;
            this.boundingRectDirty = true;
        }

        /* access modifiers changed from: package-private */
        public void computeEdge(int which, int[] edge) {
            int count = this.views.size();
            for (int i = 0; i < count; i++) {
                CellAndSpan cs = this.config.map.get(this.views.get(i));
                switch (which) {
                    case 0:
                        int left = cs.x;
                        for (int j = cs.y; j < cs.y + cs.spanY; j++) {
                            if (left < edge[j] || edge[j] < 0) {
                                edge[j] = left;
                            }
                        }
                        break;
                    case 1:
                        int top = cs.y;
                        for (int j2 = cs.x; j2 < cs.x + cs.spanX; j2++) {
                            if (top < edge[j2] || edge[j2] < 0) {
                                edge[j2] = top;
                            }
                        }
                        break;
                    case 2:
                        int right = cs.x + cs.spanX;
                        for (int j3 = cs.y; j3 < cs.y + cs.spanY; j3++) {
                            if (right > edge[j3]) {
                                edge[j3] = right;
                            }
                        }
                        break;
                    case 3:
                        int bottom = cs.y + cs.spanY;
                        for (int j4 = cs.x; j4 < cs.x + cs.spanX; j4++) {
                            if (bottom > edge[j4]) {
                                edge[j4] = bottom;
                            }
                        }
                        break;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isViewTouchingEdge(View v, int whichEdge) {
            CellAndSpan cs = this.config.map.get(v);
            int[] edge = getEdge(whichEdge);
            switch (whichEdge) {
                case 0:
                    for (int i = cs.y; i < cs.y + cs.spanY; i++) {
                        if (edge[i] == cs.x + cs.spanX) {
                            return true;
                        }
                    }
                    break;
                case 1:
                    for (int i2 = cs.x; i2 < cs.x + cs.spanX; i2++) {
                        if (edge[i2] == cs.y + cs.spanY) {
                            return true;
                        }
                    }
                    break;
                case 2:
                    for (int i3 = cs.y; i3 < cs.y + cs.spanY; i3++) {
                        if (edge[i3] == cs.x) {
                            return true;
                        }
                    }
                    break;
                case 3:
                    for (int i4 = cs.x; i4 < cs.x + cs.spanX; i4++) {
                        if (edge[i4] == cs.y) {
                            return true;
                        }
                    }
                    break;
            }
            return false;
        }

        /* access modifiers changed from: package-private */
        public void shift(int whichEdge, int delta) {
            Iterator<View> it = this.views.iterator();
            while (it.hasNext()) {
                CellAndSpan c = this.config.map.get(it.next());
                switch (whichEdge) {
                    case 0:
                        c.x -= delta;
                        break;
                    case 1:
                        c.y -= delta;
                        break;
                    case 2:
                        c.x += delta;
                        break;
                    default:
                        c.y += delta;
                        break;
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
                boolean first = true;
                Iterator<View> it = this.views.iterator();
                while (it.hasNext()) {
                    CellAndSpan c = this.config.map.get(it.next());
                    if (first) {
                        this.boundingRect.set(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
                        first = false;
                    } else {
                        this.boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
                    }
                }
            }
            return this.boundingRect;
        }

        public int[] getEdge(int which) {
            switch (which) {
                case 0:
                    return getLeftEdge();
                case 1:
                    return getTopEdge();
                case 2:
                    return getRightEdge();
                default:
                    return getBottomEdge();
            }
        }

        public int[] getLeftEdge() {
            if (this.leftEdgeDirty) {
                computeEdge(0, this.leftEdge);
            }
            return this.leftEdge;
        }

        public int[] getRightEdge() {
            if (this.rightEdgeDirty) {
                computeEdge(2, this.rightEdge);
            }
            return this.rightEdge;
        }

        public int[] getTopEdge() {
            if (this.topEdgeDirty) {
                computeEdge(1, this.topEdge);
            }
            return this.topEdge;
        }

        public int[] getBottomEdge() {
            if (this.bottomEdgeDirty) {
                computeEdge(3, this.bottomEdge);
            }
            return this.bottomEdge;
        }

        class PositionComparator implements Comparator<View> {
            int whichEdge = 0;

            PositionComparator() {
            }

            public int compare(View left, View right) {
                CellAndSpan l = ViewCluster.this.config.map.get(left);
                CellAndSpan r = ViewCluster.this.config.map.get(right);
                switch (this.whichEdge) {
                    case 0:
                        return (r.x + r.spanX) - (l.x + l.spanX);
                    case 1:
                        return (r.y + r.spanY) - (l.y + l.spanY);
                    case 2:
                        return l.x - r.x;
                    default:
                        return l.y - r.y;
                }
            }
        }

        public void sortConfigurationForEdgePush(int edge) {
            this.comparator.whichEdge = edge;
            Collections.sort(this.config.sortedViews, this.comparator);
        }
    }

    private boolean pushViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop, int[] direction, View dragView, ItemConfiguration currentState) {
        int whichEdge;
        int pushDistance;
        ViewCluster cluster = new ViewCluster(views, currentState);
        Rect clusterRect = cluster.getBoundingRect();
        boolean fail = false;
        if (direction[0] < 0) {
            whichEdge = 0;
            pushDistance = clusterRect.right - rectOccupiedByPotentialDrop.left;
        } else if (direction[0] > 0) {
            whichEdge = 2;
            pushDistance = rectOccupiedByPotentialDrop.right - clusterRect.left;
        } else if (direction[1] < 0) {
            whichEdge = 1;
            pushDistance = clusterRect.bottom - rectOccupiedByPotentialDrop.top;
        } else {
            whichEdge = 3;
            pushDistance = rectOccupiedByPotentialDrop.bottom - clusterRect.top;
        }
        if (pushDistance <= 0) {
            return false;
        }
        Iterator<View> it = views.iterator();
        while (it.hasNext()) {
            CellAndSpan c = currentState.map.get(it.next());
            markCellsForView(c.x, c.y, c.spanX, c.spanY, this.mTmpOccupied, false);
        }
        currentState.save();
        cluster.sortConfigurationForEdgePush(whichEdge);
        while (pushDistance > 0 && !fail) {
            Iterator<View> it2 = currentState.sortedViews.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                View v = it2.next();
                if (!cluster.views.contains(v) && v != dragView && cluster.isViewTouchingEdge(v, whichEdge)) {
                    if (!((LayoutParams) v.getLayoutParams()).canReorder) {
                        fail = true;
                        break;
                    }
                    cluster.addView(v);
                    CellAndSpan c2 = currentState.map.get(v);
                    markCellsForView(c2.x, c2.y, c2.spanX, c2.spanY, this.mTmpOccupied, false);
                }
            }
            pushDistance--;
            cluster.shift(whichEdge, 1);
        }
        boolean foundSolution = false;
        Rect clusterRect2 = cluster.getBoundingRect();
        if (fail || clusterRect2.left < 0 || clusterRect2.right > this.mCountX || clusterRect2.top < 0 || clusterRect2.bottom > this.mCountY) {
            currentState.restore();
        } else {
            foundSolution = true;
        }
        Iterator<View> it3 = cluster.views.iterator();
        while (it3.hasNext()) {
            CellAndSpan c3 = currentState.map.get(it3.next());
            markCellsForView(c3.x, c3.y, c3.spanX, c3.spanY, this.mTmpOccupied, true);
        }
        return foundSolution;
    }

    private boolean addViewsToTempLocation(ArrayList<View> views, Rect rectOccupiedByPotentialDrop, int[] direction, View dragView, ItemConfiguration currentState) {
        if (views.size() == 0) {
            return true;
        }
        boolean success = false;
        Rect boundingRect = null;
        Iterator<View> it = views.iterator();
        while (it.hasNext()) {
            CellAndSpan c = currentState.map.get(it.next());
            if (boundingRect == null) {
                boundingRect = new Rect(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            } else {
                boundingRect.union(c.x, c.y, c.x + c.spanX, c.y + c.spanY);
            }
        }
        Iterator<View> it2 = views.iterator();
        while (it2.hasNext()) {
            CellAndSpan c2 = currentState.map.get(it2.next());
            markCellsForView(c2.x, c2.y, c2.spanX, c2.spanY, this.mTmpOccupied, false);
        }
        boolean[][] blockOccupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{boundingRect.width(), boundingRect.height()});
        int top = boundingRect.top;
        int left = boundingRect.left;
        Iterator<View> it3 = views.iterator();
        while (it3.hasNext()) {
            CellAndSpan c3 = currentState.map.get(it3.next());
            markCellsForView(c3.x - left, c3.y - top, c3.spanX, c3.spanY, blockOccupied, true);
        }
        markCellsForRect(rectOccupiedByPotentialDrop, this.mTmpOccupied, true);
        findNearestArea(boundingRect.left, boundingRect.top, boundingRect.width(), boundingRect.height(), direction, this.mTmpOccupied, blockOccupied, this.mTempLocation);
        if (this.mTempLocation[0] >= 0 && this.mTempLocation[1] >= 0) {
            int deltaX = this.mTempLocation[0] - boundingRect.left;
            int deltaY = this.mTempLocation[1] - boundingRect.top;
            Iterator<View> it4 = views.iterator();
            while (it4.hasNext()) {
                CellAndSpan c4 = currentState.map.get(it4.next());
                c4.x += deltaX;
                c4.y += deltaY;
            }
            success = true;
        }
        Iterator<View> it5 = views.iterator();
        while (it5.hasNext()) {
            CellAndSpan c5 = currentState.map.get(it5.next());
            markCellsForView(c5.x, c5.y, c5.spanX, c5.spanY, this.mTmpOccupied, true);
        }
        return success;
    }

    private void markCellsForRect(Rect r, boolean[][] occupied, boolean value) {
        markCellsForView(r.left, r.top, r.width(), r.height(), occupied, value);
    }

    private boolean attemptPushInDirection(ArrayList<View> intersectingViews, Rect occupied, int[] direction, View ignoreView, ItemConfiguration solution) {
        if (Math.abs(direction[0]) + Math.abs(direction[1]) > 1) {
            int temp2 = direction[1];
            direction[1] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[1] = temp2;
            int temp3 = direction[0];
            direction[0] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = temp3;
            direction[0] = direction[0] * -1;
            direction[1] = direction[1] * -1;
            int temp4 = direction[1];
            direction[1] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[1] = temp4;
            int temp5 = direction[0];
            direction[0] = 0;
            if (pushViewsToTempLocation(intersectingViews, occupied, direction, ignoreView, solution)) {
                return true;
            }
            direction[0] = temp5;
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
            int temp6 = direction[1];
            direction[1] = direction[0];
            direction[0] = temp6;
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
            int temp7 = direction[1];
            direction[1] = direction[0];
            direction[0] = temp7;
        }
        return false;
    }

    private boolean rearrangementExists(int cellX, int cellY, int spanX, int spanY, int[] direction, View ignoreView, ItemConfiguration solution) {
        CellAndSpan c;
        if (cellX < 0 || cellY < 0) {
            return false;
        }
        this.mIntersectingViews.clear();
        this.mOccupiedRect.set(cellX, cellY, cellX + spanX, cellY + spanY);
        if (!(ignoreView == null || (c = solution.map.get(ignoreView)) == null)) {
            c.x = cellX;
            c.y = cellY;
        }
        Rect r0 = new Rect(cellX, cellY, cellX + spanX, cellY + spanY);
        Rect r1 = new Rect();
        for (View child : solution.map.keySet()) {
            if (child != ignoreView) {
                CellAndSpan c2 = solution.map.get(child);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                r1.set(c2.x, c2.y, c2.x + c2.spanX, c2.y + c2.spanY);
                if (!Rect.intersects(r0, r1)) {
                    continue;
                } else if (!lp.canReorder) {
                    return false;
                } else {
                    this.mIntersectingViews.add(child);
                }
            }
        }
        if (attemptPushInDirection(this.mIntersectingViews, this.mOccupiedRect, direction, ignoreView, solution)) {
            return true;
        }
        if (addViewsToTempLocation(this.mIntersectingViews, this.mOccupiedRect, direction, ignoreView, solution)) {
            return true;
        }
        Iterator<View> it = this.mIntersectingViews.iterator();
        while (it.hasNext()) {
            if (!addViewToTempLocation(it.next(), this.mOccupiedRect, direction, solution)) {
                return false;
            }
        }
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

    private void copyOccupiedArray(boolean[][] occupied) {
        for (int i = 0; i < this.mCountX; i++) {
            for (int j = 0; j < this.mCountY; j++) {
                occupied[i][j] = this.mOccupied[i][j];
            }
        }
    }

    /* access modifiers changed from: package-private */
    public ItemConfiguration simpleSwap(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, int[] direction, View dragView, boolean decX, ItemConfiguration solution) {
        copyCurrentStateToSolution(solution, false);
        copyOccupiedArray(this.mTmpOccupied);
        int[] result = findNearestArea(pixelX, pixelY, spanX, spanY, new int[2]);
        if (rearrangementExists(result[0], result[1], spanX, spanY, direction, dragView, solution)) {
            solution.isSolution = true;
            solution.dragViewX = result[0];
            solution.dragViewY = result[1];
            solution.dragViewSpanX = spanX;
            solution.dragViewSpanY = spanY;
            return solution;
        } else if (spanX > minSpanX && (minSpanY == spanY || decX)) {
            return simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX - 1, spanY, direction, dragView, false, solution);
        } else if (spanY > minSpanY) {
            return simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY - 1, direction, dragView, true, solution);
        } else {
            solution.isSolution = false;
            return solution;
        }
    }

    private void copyCurrentStateToSolution(ItemConfiguration solution, boolean temp2) {
        CellAndSpan c;
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (temp2) {
                c = new CellAndSpan(lp.tmpCellX, lp.tmpCellY, lp.cellHSpan, lp.cellVSpan);
            } else {
                c = new CellAndSpan(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan);
            }
            solution.add(child, c);
        }
    }

    private void copySolutionToTempState(ItemConfiguration solution, View dragView) {
        for (int i = 0; i < this.mCountX; i++) {
            for (int j = 0; j < this.mCountY; j++) {
                this.mTmpOccupied[i][j] = false;
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i2);
            if (child != dragView) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                CellAndSpan c = solution.map.get(child);
                if (c != null) {
                    lp.tmpCellX = c.x;
                    lp.tmpCellY = c.y;
                    lp.cellHSpan = c.spanX;
                    lp.cellVSpan = c.spanY;
                    markCellsForView(c.x, c.y, c.spanX, c.spanY, this.mTmpOccupied, true);
                }
            }
        }
        markCellsForView(solution.dragViewX, solution.dragViewY, solution.dragViewSpanX, solution.dragViewSpanY, this.mTmpOccupied, true);
    }

    private void animateItemsToSolution(ItemConfiguration solution, View dragView, boolean commitDragView) {
        CellAndSpan c;
        boolean[][] occupied = this.mTmpOccupied;
        for (int i = 0; i < this.mCountX; i++) {
            for (int j = 0; j < this.mCountY; j++) {
                occupied[i][j] = false;
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i2);
            if (!(child == dragView || (c = solution.map.get(child)) == null)) {
                animateChildToPosition(child, c.x, c.y, REORDER_ANIMATION_DURATION, 0, false, false);
                markCellsForView(c.x, c.y, c.spanX, c.spanY, occupied, true);
            }
        }
        if (commitDragView) {
            markCellsForView(solution.dragViewX, solution.dragViewY, solution.dragViewSpanX, solution.dragViewSpanY, occupied, true);
        }
    }

    private void beginOrAdjustHintAnimations(ItemConfiguration solution, View dragView, int delay) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i);
            if (child != dragView) {
                CellAndSpan c = solution.map.get(child);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (c != null) {
                    new ReorderHintAnimation(child, lp.cellX, lp.cellY, c.x, c.y, c.spanX, c.spanY).animate();
                }
            }
        }
    }

    class ReorderHintAnimation {
        private static final int DURATION = 300;
        Animator a;
        View child;
        float finalDeltaX = 0.0f;
        float finalDeltaY = 0.0f;
        float finalScale;
        float initDeltaX;
        float initDeltaY;
        float initScale;

        public ReorderHintAnimation(View child2, int cellX0, int cellY0, int cellX1, int cellY1, int spanX, int spanY) {
            CellLayout.this.regionToCenterPoint(cellX0, cellY0, spanX, spanY, CellLayout.this.mTmpPoint);
            int x0 = CellLayout.this.mTmpPoint[0];
            int y0 = CellLayout.this.mTmpPoint[1];
            CellLayout.this.regionToCenterPoint(cellX1, cellY1, spanX, spanY, CellLayout.this.mTmpPoint);
            int dX = CellLayout.this.mTmpPoint[0] - x0;
            int dY = CellLayout.this.mTmpPoint[1] - y0;
            if (!(dX == dY && dX == 0)) {
                if (dY == 0) {
                    this.finalDeltaX = (-Math.signum((float) dX)) * CellLayout.this.mReorderHintAnimationMagnitude;
                } else if (dX == 0) {
                    this.finalDeltaY = (-Math.signum((float) dY)) * CellLayout.this.mReorderHintAnimationMagnitude;
                } else {
                    double angle = Math.atan((double) (((float) dY) / ((float) dX)));
                    this.finalDeltaX = (float) ((int) (((double) (-Math.signum((float) dX))) * Math.abs(Math.cos(angle) * ((double) CellLayout.this.mReorderHintAnimationMagnitude))));
                    this.finalDeltaY = (float) ((int) (((double) (-Math.signum((float) dY))) * Math.abs(Math.sin(angle) * ((double) CellLayout.this.mReorderHintAnimationMagnitude))));
                }
            }
            this.initDeltaX = child2.getTranslationX();
            this.initDeltaY = child2.getTranslationY();
            this.finalScale = CellLayout.this.getChildrenScale() - (4.0f / ((float) child2.getWidth()));
            this.initScale = child2.getScaleX();
            this.child = child2;
        }

        /* access modifiers changed from: package-private */
        public void animate() {
            if (CellLayout.this.mShakeAnimators.containsKey(this.child)) {
                ((ReorderHintAnimation) CellLayout.this.mShakeAnimators.get(this.child)).cancel();
                CellLayout.this.mShakeAnimators.remove(this.child);
                if (this.finalDeltaX == 0.0f && this.finalDeltaY == 0.0f) {
                    completeAnimationImmediately();
                    return;
                }
            }
            if (this.finalDeltaX != 0.0f || this.finalDeltaY != 0.0f) {
                ValueAnimator va = LauncherAnimUtils.ofFloat(this.child, 0.0f, 1.0f);
                this.a = va;
                va.setRepeatMode(2);
                va.setRepeatCount(-1);
                va.setDuration(300);
                va.setStartDelay((long) ((int) (Math.random() * 60.0d)));
                va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float r = ((Float) animation.getAnimatedValue()).floatValue();
                        float x = (ReorderHintAnimation.this.finalDeltaX * r) + ((1.0f - r) * ReorderHintAnimation.this.initDeltaX);
                        float y = (ReorderHintAnimation.this.finalDeltaY * r) + ((1.0f - r) * ReorderHintAnimation.this.initDeltaY);
                        ReorderHintAnimation.this.child.setTranslationX(x);
                        ReorderHintAnimation.this.child.setTranslationY(y);
                        float s = (ReorderHintAnimation.this.finalScale * r) + ((1.0f - r) * ReorderHintAnimation.this.initScale);
                        ReorderHintAnimation.this.child.setScaleX(s);
                        ReorderHintAnimation.this.child.setScaleY(s);
                    }
                });
                va.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationRepeat(Animator animation) {
                        ReorderHintAnimation.this.initDeltaX = 0.0f;
                        ReorderHintAnimation.this.initDeltaY = 0.0f;
                        ReorderHintAnimation.this.initScale = CellLayout.this.getChildrenScale();
                    }
                });
                CellLayout.this.mShakeAnimators.put(this.child, this);
                va.start();
            }
        }

        private void cancel() {
            if (this.a != null) {
                this.a.cancel();
            }
        }

        /* access modifiers changed from: private */
        public void completeAnimationImmediately() {
            if (this.a != null) {
                this.a.cancel();
            }
            AnimatorSet s = LauncherAnimUtils.createAnimatorSet();
            this.a = s;
            s.playTogether(new Animator[]{LauncherAnimUtils.ofFloat(this.child, "scaleX", CellLayout.this.getChildrenScale()), LauncherAnimUtils.ofFloat(this.child, "scaleY", CellLayout.this.getChildrenScale()), LauncherAnimUtils.ofFloat(this.child, "translationX", 0.0f), LauncherAnimUtils.ofFloat(this.child, "translationY", 0.0f)});
            s.setDuration(150);
            s.setInterpolator(new DecelerateInterpolator(1.5f));
            s.start();
        }
    }

    private void completeAndClearReorderHintAnimations() {
        for (ReorderHintAnimation a : this.mShakeAnimators.values()) {
            a.completeAnimationImmediately();
        }
        this.mShakeAnimators.clear();
    }

    private void commitTempPlacement() {
        for (int i = 0; i < this.mCountX; i++) {
            for (int j = 0; j < this.mCountY; j++) {
                this.mOccupied[i][j] = this.mTmpOccupied[i][j];
            }
        }
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i2);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            ItemInfo info = (ItemInfo) child.getTag();
            if (info != null) {
                if (!(info.cellX == lp.tmpCellX && info.cellY == lp.tmpCellY && info.spanX == lp.cellHSpan && info.spanY == lp.cellVSpan)) {
                    info.requiresDbUpdate = true;
                }
                int i3 = lp.tmpCellX;
                lp.cellX = i3;
                info.cellX = i3;
                int i4 = lp.tmpCellY;
                lp.cellY = i4;
                info.cellY = i4;
                info.spanX = lp.cellHSpan;
                info.spanY = lp.cellVSpan;
            }
        }
        this.mLauncher.getWorkspace().updateItemLocationsInDatabase(this);
    }

    public void setUseTempCoords(boolean useTempCoords) {
        int childCount = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((LayoutParams) this.mShortcutsAndWidgets.getChildAt(i).getLayoutParams()).useTmpCoords = useTempCoords;
        }
    }

    /* access modifiers changed from: package-private */
    public ItemConfiguration findConfigurationNoShuffle(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, View dragView, ItemConfiguration solution) {
        int[] result = new int[2];
        int[] resultSpan = new int[2];
        findNearestVacantArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, (View) null, result, resultSpan);
        if (result[0] < 0 || result[1] < 0) {
            solution.isSolution = false;
        } else {
            copyCurrentStateToSolution(solution, false);
            solution.dragViewX = result[0];
            solution.dragViewY = result[1];
            solution.dragViewSpanX = resultSpan[0];
            solution.dragViewSpanY = resultSpan[1];
            solution.isSolution = true;
        }
        return solution;
    }

    public void prepareChildForDrag(View child) {
        markCellsAsUnoccupiedForView(child);
    }

    private void getDirectionVectorForDrop(int dragViewCenterX, int dragViewCenterY, int spanX, int spanY, View dragView, int[] resultDirection) {
        int[] targetDestination = new int[2];
        findNearestArea(dragViewCenterX, dragViewCenterY, spanX, spanY, targetDestination);
        Rect dragRect = new Rect();
        regionToRect(targetDestination[0], targetDestination[1], spanX, spanY, dragRect);
        dragRect.offset(dragViewCenterX - dragRect.centerX(), dragViewCenterY - dragRect.centerY());
        Rect dropRegionRect = new Rect();
        getViewsIntersectingRegion(targetDestination[0], targetDestination[1], spanX, spanY, dragView, dropRegionRect, this.mIntersectingViews);
        int dropRegionSpanX = dropRegionRect.width();
        int dropRegionSpanY = dropRegionRect.height();
        regionToRect(dropRegionRect.left, dropRegionRect.top, dropRegionRect.width(), dropRegionRect.height(), dropRegionRect);
        int deltaX = (dropRegionRect.centerX() - dragViewCenterX) / spanX;
        int deltaY = (dropRegionRect.centerY() - dragViewCenterY) / spanY;
        if (dropRegionSpanX == this.mCountX || spanX == this.mCountX) {
            deltaX = 0;
        }
        if (dropRegionSpanY == this.mCountY || spanY == this.mCountY) {
            deltaY = 0;
        }
        if (deltaX == 0 && deltaY == 0) {
            resultDirection[0] = 1;
            resultDirection[1] = 0;
            return;
        }
        computeDirectionVector((float) deltaX, (float) deltaY, resultDirection);
    }

    private void getViewsIntersectingRegion(int cellX, int cellY, int spanX, int spanY, View dragView, Rect boundingRect, ArrayList<View> intersectingViews) {
        if (boundingRect != null) {
            boundingRect.set(cellX, cellY, cellX + spanX, cellY + spanY);
        }
        intersectingViews.clear();
        Rect r0 = new Rect(cellX, cellY, cellX + spanX, cellY + spanY);
        Rect r1 = new Rect();
        int count = this.mShortcutsAndWidgets.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = this.mShortcutsAndWidgets.getChildAt(i);
            if (child != dragView) {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                r1.set(lp.cellX, lp.cellY, lp.cellX + lp.cellHSpan, lp.cellY + lp.cellVSpan);
                if (Rect.intersects(r0, r1)) {
                    this.mIntersectingViews.add(child);
                    if (boundingRect != null) {
                        boundingRect.union(r1);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isNearestDropLocationOccupied(int pixelX, int pixelY, int spanX, int spanY, View dragView, int[] result) {
        int[] result2 = findNearestArea(pixelX, pixelY, spanX, spanY, result);
        getViewsIntersectingRegion(result2[0], result2[1], spanX, spanY, dragView, (Rect) null, this.mIntersectingViews);
        return !this.mIntersectingViews.isEmpty();
    }

    /* access modifiers changed from: package-private */
    public void revertTempState() {
        if (isItemPlacementDirty()) {
            int count = this.mShortcutsAndWidgets.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = this.mShortcutsAndWidgets.getChildAt(i);
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.tmpCellX != lp.cellX || lp.tmpCellY != lp.cellY) {
                    lp.tmpCellX = lp.cellX;
                    lp.tmpCellY = lp.cellY;
                    animateChildToPosition(child, lp.cellX, lp.cellY, REORDER_ANIMATION_DURATION, 0, false, false);
                }
            }
            completeAndClearReorderHintAnimations();
            setItemPlacementDirty(false);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean createAreaForResize(int cellX, int cellY, int spanX, int spanY, View dragView, int[] direction, boolean commit) {
        int[] pixelXY = new int[2];
        regionToCenterPoint(cellX, cellY, spanX, spanY, pixelXY);
        ItemConfiguration swapSolution = simpleSwap(pixelXY[0], pixelXY[1], spanX, spanY, spanX, spanY, direction, dragView, true, new ItemConfiguration());
        setUseTempCoords(true);
        if (swapSolution != null && swapSolution.isSolution) {
            copySolutionToTempState(swapSolution, dragView);
            setItemPlacementDirty(true);
            animateItemsToSolution(swapSolution, dragView, commit);
            if (commit) {
                commitTempPlacement();
                completeAndClearReorderHintAnimations();
                setItemPlacementDirty(false);
            } else {
                beginOrAdjustHintAnimations(swapSolution, dragView, REORDER_ANIMATION_DURATION);
            }
            this.mShortcutsAndWidgets.requestLayout();
        }
        return swapSolution.isSolution;
    }

    /* access modifiers changed from: package-private */
    public int[] createArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, View dragView, int[] result, int[] resultSpan, int mode) {
        int[] result2 = findNearestArea(pixelX, pixelY, spanX, spanY, result);
        if (resultSpan == null) {
            resultSpan = new int[2];
        }
        if ((mode == 1 || mode == 2 || mode == 3) && this.mPreviousReorderDirection[0] != INVALID_DIRECTION) {
            this.mDirectionVector[0] = this.mPreviousReorderDirection[0];
            this.mDirectionVector[1] = this.mPreviousReorderDirection[1];
            if (mode == 1 || mode == 2) {
                this.mPreviousReorderDirection[0] = INVALID_DIRECTION;
                this.mPreviousReorderDirection[1] = INVALID_DIRECTION;
            }
        } else {
            getDirectionVectorForDrop(pixelX, pixelY, spanX, spanY, dragView, this.mDirectionVector);
            this.mPreviousReorderDirection[0] = this.mDirectionVector[0];
            this.mPreviousReorderDirection[1] = this.mDirectionVector[1];
        }
        ItemConfiguration swapSolution = simpleSwap(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, this.mDirectionVector, dragView, true, new ItemConfiguration());
        ItemConfiguration noShuffleSolution = findConfigurationNoShuffle(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, dragView, new ItemConfiguration());
        ItemConfiguration finalSolution = null;
        if (swapSolution.isSolution && swapSolution.area() >= noShuffleSolution.area()) {
            finalSolution = swapSolution;
        } else if (noShuffleSolution.isSolution) {
            finalSolution = noShuffleSolution;
        }
        boolean foundSolution = true;
        setUseTempCoords(true);
        if (finalSolution != null) {
            result2[0] = finalSolution.dragViewX;
            result2[1] = finalSolution.dragViewY;
            resultSpan[0] = finalSolution.dragViewSpanX;
            resultSpan[1] = finalSolution.dragViewSpanY;
            if (mode == 0 || mode == 1 || mode == 2) {
                copySolutionToTempState(finalSolution, dragView);
                setItemPlacementDirty(true);
                animateItemsToSolution(finalSolution, dragView, mode == 1);
                if (mode == 1 || mode == 2) {
                    commitTempPlacement();
                    completeAndClearReorderHintAnimations();
                    setItemPlacementDirty(false);
                } else {
                    beginOrAdjustHintAnimations(finalSolution, dragView, REORDER_ANIMATION_DURATION);
                }
            }
        } else {
            foundSolution = false;
            resultSpan[1] = -1;
            resultSpan[0] = -1;
            result2[1] = -1;
            result2[0] = -1;
        }
        if (mode == 1 || !foundSolution) {
            setUseTempCoords(false);
        }
        this.mShortcutsAndWidgets.requestLayout();
        return result2;
    }

    /* access modifiers changed from: package-private */
    public void setItemPlacementDirty(boolean dirty) {
        this.mItemPlacementDirty = dirty;
    }

    /* access modifiers changed from: package-private */
    public boolean isItemPlacementDirty() {
        return this.mItemPlacementDirty;
    }

    private class ItemConfiguration {
        int dragViewSpanX;
        int dragViewSpanY;
        int dragViewX;
        int dragViewY;
        boolean isSolution;
        HashMap<View, CellAndSpan> map;
        private HashMap<View, CellAndSpan> savedMap;
        ArrayList<View> sortedViews;

        private ItemConfiguration() {
            this.map = new HashMap<>();
            this.savedMap = new HashMap<>();
            this.sortedViews = new ArrayList<>();
            this.isSolution = false;
        }

        /* access modifiers changed from: package-private */
        public void save() {
            for (View v : this.map.keySet()) {
                this.map.get(v).copy(this.savedMap.get(v));
            }
        }

        /* access modifiers changed from: package-private */
        public void restore() {
            for (View v : this.savedMap.keySet()) {
                this.savedMap.get(v).copy(this.map.get(v));
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
            return this.dragViewSpanX * this.dragViewSpanY;
        }
    }

    private class CellAndSpan {
        int spanX;
        int spanY;
        int x;
        int y;

        public CellAndSpan() {
        }

        public void copy(CellAndSpan copy) {
            copy.x = this.x;
            copy.y = this.y;
            copy.spanX = this.spanX;
            copy.spanY = this.spanY;
        }

        public CellAndSpan(int x2, int y2, int spanX2, int spanY2) {
            this.x = x2;
            this.y = y2;
            this.spanX = spanX2;
            this.spanY = spanY2;
        }

        public String toString() {
            return "(" + this.x + ", " + this.y + ": " + this.spanX + ", " + this.spanY + ")";
        }
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestVacantArea(int pixelX, int pixelY, int spanX, int spanY, View ignoreView, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, ignoreView, true, result);
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestVacantArea(int pixelX, int pixelY, int minSpanX, int minSpanY, int spanX, int spanY, View ignoreView, int[] result, int[] resultSpan) {
        return findNearestArea(pixelX, pixelY, minSpanX, minSpanY, spanX, spanY, ignoreView, true, result, resultSpan, this.mOccupied);
    }

    /* access modifiers changed from: package-private */
    public int[] findNearestArea(int pixelX, int pixelY, int spanX, int spanY, int[] result) {
        return findNearestArea(pixelX, pixelY, spanX, spanY, (View) null, false, result);
    }

    /* access modifiers changed from: package-private */
    public boolean existsEmptyCell() {
        return findCellForSpan((int[]) null, 1, 1);
    }

    /* access modifiers changed from: package-private */
    public boolean findCellForSpan(int[] cellXY, int spanX, int spanY) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, -1, -1, (View) null, this.mOccupied);
    }

    /* access modifiers changed from: package-private */
    public boolean findCellForSpanIgnoring(int[] cellXY, int spanX, int spanY, View ignoreView) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, -1, -1, ignoreView, this.mOccupied);
    }

    /* access modifiers changed from: package-private */
    public boolean findCellForSpanThatIntersects(int[] cellXY, int spanX, int spanY, int intersectX, int intersectY) {
        return findCellForSpanThatIntersectsIgnoring(cellXY, spanX, spanY, intersectX, intersectY, (View) null, this.mOccupied);
    }

    /* access modifiers changed from: package-private */
    public boolean findCellForSpanThatIntersectsIgnoring(int[] cellXY, int spanX, int spanY, int intersectX, int intersectY, View ignoreView, boolean[][] occupied) {
        markCellsAsUnoccupiedForView(ignoreView, occupied);
        boolean foundCell = false;
        while (true) {
            int startX = 0;
            if (intersectX >= 0) {
                startX = Math.max(0, intersectX - (spanX - 1));
            }
            int endX = this.mCountX - (spanX - 1);
            if (intersectX >= 0) {
                endX = Math.min(endX, (spanX == 1 ? 1 : 0) + intersectX + (spanX - 1));
            }
            int startY = 0;
            if (intersectY >= 0) {
                startY = Math.max(0, intersectY - (spanY - 1));
            }
            int endY = this.mCountY - (spanY - 1);
            if (intersectY >= 0) {
                endY = Math.min(endY, (spanY == 1 ? 1 : 0) + intersectY + (spanY - 1));
            }
            for (int y = startY; y < endY && !foundCell; y++) {
                int x = startX;
                while (true) {
                    if (x >= endX) {
                        break;
                    }
                    for (int i = 0; i < spanX; i++) {
                        int j = 0;
                        while (j < spanY) {
                            if (occupied[x + i][y + j]) {
                                x = x + i + 1;
                            } else {
                                j++;
                            }
                        }
                    }
                    if (cellXY != null) {
                        cellXY[0] = x;
                        cellXY[1] = y;
                    }
                    foundCell = true;
                }
            }
            if (intersectX == -1 && intersectY == -1) {
                markCellsAsOccupiedForView(ignoreView, occupied);
                return foundCell;
            }
            intersectX = -1;
            intersectY = -1;
        }
    }

    /* access modifiers changed from: package-private */
    public void onDragEnter() {
        this.mDragEnforcer.onDragEnter();
        this.mDragging = true;
    }

    /* access modifiers changed from: package-private */
    public void onDragExit() {
        this.mDragEnforcer.onDragExit();
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
        }
    }

    public void cellToRect(int cellX, int cellY, int cellHSpan, int cellVSpan, Rect resultRect) {
        int cellWidth = this.mCellWidth;
        int cellHeight = this.mCellHeight;
        int widthGap = this.mWidthGap;
        int heightGap = this.mHeightGap;
        int x = getPaddingLeft() + ((cellWidth + widthGap) * cellX);
        int y = getPaddingTop() + ((cellHeight + heightGap) * cellY);
        Rect rect = resultRect;
        rect.set(x, y, x + (cellHSpan * cellWidth) + ((cellHSpan - 1) * widthGap), y + (cellVSpan * cellHeight) + ((cellVSpan - 1) * heightGap));
    }

    public int[] rectToCell(int width, int height, int[] result) {
        return rectToCell(getResources(), width, height, result);
    }

    public static int[] rectToCell(Resources resources, int width, int height, int[] result) {
        int smallerSize = Math.min(resources.getDimensionPixelSize(R.dimen.workspace_cell_width), resources.getDimensionPixelSize(R.dimen.workspace_cell_height));
        int spanX = (int) Math.ceil((double) (((float) width) / ((float) smallerSize)));
        int spanY = (int) Math.ceil((double) (((float) height) / ((float) smallerSize)));
        if (result == null) {
            return new int[]{spanX, spanY};
        }
        result[0] = spanX;
        result[1] = spanY;
        return result;
    }

    public int[] cellSpansToSize(int hSpans, int vSpans) {
        return new int[]{(this.mCellWidth * hSpans) + ((hSpans - 1) * this.mWidthGap), (this.mCellHeight * vSpans) + ((vSpans - 1) * this.mHeightGap)};
    }

    public void calculateSpans(ItemInfo info) {
        int minWidth;
        int minHeight;
        if (info instanceof LauncherAppWidgetInfo) {
            minWidth = ((LauncherAppWidgetInfo) info).minWidth;
            minHeight = ((LauncherAppWidgetInfo) info).minHeight;
        } else if (info instanceof PendingAddWidgetInfo) {
            minWidth = ((PendingAddWidgetInfo) info).minWidth;
            minHeight = ((PendingAddWidgetInfo) info).minHeight;
        } else {
            info.spanY = 1;
            info.spanX = 1;
            return;
        }
        int[] spans = rectToCell(minWidth, minHeight, (int[]) null);
        info.spanX = spans[0];
        info.spanY = spans[1];
    }

    public boolean getVacantCell(int[] vacant, int spanX, int spanY) {
        return findVacantCell(vacant, spanX, spanY, this.mCountX, this.mCountY, this.mOccupied);
    }

    static boolean findVacantCell(int[] vacant, int spanX, int spanY, int xCount, int yCount, boolean[][] occupied) {
        boolean available;
        boolean available2;
        int y = 0;
        while (y < yCount) {
            int x = 0;
            while (x < xCount) {
                if (!occupied[x][y]) {
                    available = true;
                } else {
                    available = false;
                }
                for (int i = x; i < (x + spanX) - 1 && x < xCount; i++) {
                    for (int j = y; j < (y + spanY) - 1 && y < yCount; j++) {
                        if (!available2 || occupied[i][j]) {
                            available2 = false;
                        } else {
                            available2 = true;
                        }
                        if (!available2) {
                            break;
                        }
                    }
                }
                if (available2) {
                    vacant[0] = x;
                    vacant[1] = y;
                    return true;
                }
                x++;
            }
            y++;
        }
        return false;
    }

    private void clearOccupiedCells() {
        for (int x = 0; x < this.mCountX; x++) {
            for (int y = 0; y < this.mCountY; y++) {
                this.mOccupied[x][y] = false;
            }
        }
    }

    public void onMove(View view, int newCellX, int newCellY, int newSpanX, int newSpanY) {
        markCellsAsUnoccupiedForView(view);
        markCellsForView(newCellX, newCellY, newSpanX, newSpanY, this.mOccupied, true);
    }

    public void markCellsAsOccupiedForView(View view) {
        markCellsAsOccupiedForView(view, this.mOccupied);
    }

    public void markCellsAsOccupiedForView(View view, boolean[][] occupied) {
        if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            markCellsForView(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, occupied, true);
        }
    }

    public void markCellsAsUnoccupiedForView(View view) {
        markCellsAsUnoccupiedForView(view, this.mOccupied);
    }

    public void markCellsAsUnoccupiedForView(View view, boolean[][] occupied) {
        if (view != null && view.getParent() == this.mShortcutsAndWidgets) {
            LayoutParams lp = (LayoutParams) view.getLayoutParams();
            markCellsForView(lp.cellX, lp.cellY, lp.cellHSpan, lp.cellVSpan, occupied, false);
        }
    }

    private void markCellsForView(int cellX, int cellY, int spanX, int spanY, boolean[][] occupied, boolean value) {
        if (cellX >= 0 && cellY >= 0) {
            int x = cellX;
            while (x < cellX + spanX && x < this.mCountX) {
                int y = cellY;
                while (y < cellY + spanY && y < this.mCountY) {
                    occupied[x][y] = value;
                    y++;
                }
                x++;
            }
        }
    }

    public int getDesiredWidth() {
        return getPaddingLeft() + getPaddingRight() + (this.mCountX * this.mCellWidth) + (Math.max(this.mCountX - 1, 0) * this.mWidthGap);
    }

    public int getDesiredHeight() {
        return getPaddingTop() + getPaddingBottom() + (this.mCountY * this.mCellHeight) + (Math.max(this.mCountY - 1, 0) * this.mHeightGap);
    }

    public boolean isOccupied(int x, int y) {
        if (x < this.mCountX && y < this.mCountY) {
            return this.mOccupied[x][y];
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

    public static class CellLayoutAnimationController extends LayoutAnimationController {
        public CellLayoutAnimationController(Animation animation, float delay) {
            super(animation, delay);
        }

        /* access modifiers changed from: protected */
        public long getDelayForView(View view) {
            return (long) ((int) (Math.random() * 150.0d));
        }
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
        int x;
        @ViewDebug.ExportedProperty
        int y;

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

        public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap, boolean invertHorizontally, int colCount) {
            if (this.isLockedToGrid) {
                int myCellHSpan = this.cellHSpan;
                int myCellVSpan = this.cellVSpan;
                int myCellX = this.useTmpCoords ? this.tmpCellX : this.cellX;
                int myCellY = this.useTmpCoords ? this.tmpCellY : this.cellY;
                if (invertHorizontally) {
                    myCellX = (colCount - myCellX) - this.cellHSpan;
                }
                this.width = (((myCellHSpan * cellWidth) + ((myCellHSpan - 1) * widthGap)) - this.leftMargin) - this.rightMargin;
                this.height = (((myCellVSpan * cellHeight) + ((myCellVSpan - 1) * heightGap)) - this.topMargin) - this.bottomMargin;
                this.x = ((cellWidth + widthGap) * myCellX) + this.leftMargin;
                this.y = ((cellHeight + heightGap) * myCellY) + this.topMargin;
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

    static final class CellInfo {
        View cell;
        int cellX = -1;
        int cellY = -1;
        long container;
        int screen;
        int spanX;
        int spanY;

        CellInfo() {
        }

        public String toString() {
            return "Cell[view=" + (this.cell == null ? "null" : this.cell.getClass()) + ", x=" + this.cellX + ", y=" + this.cellY + "]";
        }
    }

    public boolean lastDownOnOccupiedCell() {
        return this.mLastDownOnOccupiedCell;
    }
}
