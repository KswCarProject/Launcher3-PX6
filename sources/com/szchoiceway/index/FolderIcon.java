package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.szchoiceway.index.CellLayout;
import com.szchoiceway.index.DropTarget;
import com.szchoiceway.index.FolderInfo;
import java.util.ArrayList;

public class FolderIcon extends LinearLayout implements FolderInfo.FolderListener {
    private static final int CONSUMPTION_ANIMATION_DURATION = 100;
    private static final int DROP_IN_ANIMATION_DURATION = 400;
    private static final int FINAL_ITEM_ANIMATION_DURATION = 200;
    private static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
    private static final float INNER_RING_GROWTH_FACTOR = 0.15f;
    private static final int NUM_ITEMS_IN_PREVIEW = 6;
    private static final float OUTER_RING_GROWTH_FACTOR = 0.3f;
    private static final float PERSPECTIVE_SCALE_FACTOR = 0.35f;
    private static final float PERSPECTIVE_SHIFT_FACTOR = 0.24f;
    public static Drawable sSharedFolderLeaveBehind = null;
    /* access modifiers changed from: private */
    public static boolean sStaticValuesDirty = true;
    /* access modifiers changed from: private */
    public PreviewItemDrawingParams mAnimParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0);
    boolean mAnimating = false;
    private int mAvailableSpaceInPreview;
    private float mBaselineIconScale;
    private int mBaselineIconSize;
    /* access modifiers changed from: private */
    public Folder mFolder;
    private BubbleTextView mFolderName;
    FolderRingAnimator mFolderRingAnimator = null;
    /* access modifiers changed from: private */
    public ArrayList<ShortcutInfo> mHiddenItems = new ArrayList<>();
    private FolderInfo mInfo;
    private int mIntrinsicIconSize;
    private Launcher mLauncher;
    private CheckLongPressHelper mLongPressHelper;
    private float mMaxPerspectiveShift;
    private PreviewItemDrawingParams mParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0);
    /* access modifiers changed from: private */
    public ImageView mPreviewBackground;
    private int mPreviewOffsetX;
    private int mPreviewOffsetY;
    private int mTotalWidth = -1;

    public FolderIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FolderIcon(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.mLongPressHelper = new CheckLongPressHelper(this);
    }

    public boolean isDropEnabled() {
        return !((Workspace) ((ViewGroup) ((ViewGroup) getParent()).getParent()).getParent()).isSmall();
    }

    static FolderIcon fromXml(int resId, Launcher launcher, ViewGroup group, FolderInfo folderInfo, IconCache iconCache) {
        FolderIcon icon = (FolderIcon) LayoutInflater.from(launcher).inflate(resId, group, false);
        icon.mFolderName = (BubbleTextView) icon.findViewById(R.id.folder_icon_name);
        icon.mFolderName.setText(folderInfo.title);
        icon.mPreviewBackground = (ImageView) icon.findViewById(R.id.preview_background);
        icon.setTag(folderInfo);
        icon.setOnClickListener(launcher);
        icon.mInfo = folderInfo;
        icon.mLauncher = launcher;
        icon.setContentDescription(String.format(launcher.getString(R.string.folder_name_format), new Object[]{folderInfo.title}));
        Folder folder = Folder.fromXml(launcher);
        folder.setDragController(launcher.getDragController());
        folder.setFolderIcon(icon);
        folder.bind(folderInfo);
        icon.mFolder = folder;
        icon.mFolderRingAnimator = new FolderRingAnimator(launcher, icon);
        folderInfo.addListener(icon);
        return icon;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        sStaticValuesDirty = true;
        return super.onSaveInstanceState();
    }

    public static class FolderRingAnimator {
        public static int sPreviewPadding = -1;
        public static int sPreviewSize = -1;
        public static Drawable sSharedInnerRingDrawable = null;
        public static Drawable sSharedOuterRingDrawable = null;
        private ValueAnimator mAcceptAnimator;
        /* access modifiers changed from: private */
        public CellLayout mCellLayout;
        public int mCellX;
        public int mCellY;
        public FolderIcon mFolderIcon = null;
        public Drawable mInnerRingDrawable = null;
        public float mInnerRingSize;
        private ValueAnimator mNeutralAnimator;
        public Drawable mOuterRingDrawable = null;
        public float mOuterRingSize;

        public FolderRingAnimator(Launcher launcher, FolderIcon folderIcon) {
            this.mFolderIcon = folderIcon;
            Resources res = launcher.getResources();
            this.mOuterRingDrawable = res.getDrawable(R.drawable.wenjianjia_n);
            this.mInnerRingDrawable = res.getDrawable(R.drawable.wenjianjia_n);
            if (FolderIcon.sStaticValuesDirty) {
                sPreviewSize = res.getDimensionPixelSize(R.dimen.folder_preview_size);
                sPreviewPadding = res.getDimensionPixelSize(R.dimen.folder_preview_padding);
                sSharedOuterRingDrawable = res.getDrawable(R.drawable.wenjianjia_d);
                sSharedInnerRingDrawable = res.getDrawable(R.drawable.wenjianjia_n);
                FolderIcon.sSharedFolderLeaveBehind = res.getDrawable(R.drawable.portal_ring_rest);
                boolean unused = FolderIcon.sStaticValuesDirty = false;
            }
        }

        public void animateToAcceptState() {
            if (this.mNeutralAnimator != null) {
                this.mNeutralAnimator.cancel();
            }
            this.mAcceptAnimator = LauncherAnimUtils.ofFloat(this.mCellLayout, 0.0f, 1.0f);
            this.mAcceptAnimator.setDuration(100);
            final int previewSize = sPreviewSize;
            this.mAcceptAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float percent = ((Float) animation.getAnimatedValue()).floatValue();
                    FolderRingAnimator.this.mOuterRingSize = ((FolderIcon.OUTER_RING_GROWTH_FACTOR * percent) + 1.0f) * ((float) previewSize);
                    FolderRingAnimator.this.mInnerRingSize = ((FolderIcon.INNER_RING_GROWTH_FACTOR * percent) + 1.0f) * ((float) previewSize);
                    if (FolderRingAnimator.this.mCellLayout != null) {
                        FolderRingAnimator.this.mCellLayout.invalidate();
                    }
                }
            });
            this.mAcceptAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    if (FolderRingAnimator.this.mFolderIcon != null) {
                        FolderRingAnimator.this.mFolderIcon.mPreviewBackground.setVisibility(4);
                    }
                }
            });
            this.mAcceptAnimator.start();
        }

        public void animateToNaturalState() {
            if (this.mAcceptAnimator != null) {
                this.mAcceptAnimator.cancel();
            }
            this.mNeutralAnimator = LauncherAnimUtils.ofFloat(this.mCellLayout, 0.0f, 1.0f);
            this.mNeutralAnimator.setDuration(100);
            final int previewSize = sPreviewSize;
            this.mNeutralAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float percent = ((Float) animation.getAnimatedValue()).floatValue();
                    FolderRingAnimator.this.mOuterRingSize = (((1.0f - percent) * FolderIcon.OUTER_RING_GROWTH_FACTOR) + 1.0f) * ((float) previewSize);
                    FolderRingAnimator.this.mInnerRingSize = (((1.0f - percent) * FolderIcon.INNER_RING_GROWTH_FACTOR) + 1.0f) * ((float) previewSize);
                    if (FolderRingAnimator.this.mCellLayout != null) {
                        FolderRingAnimator.this.mCellLayout.invalidate();
                    }
                }
            });
            this.mNeutralAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (FolderRingAnimator.this.mCellLayout != null) {
                        FolderRingAnimator.this.mCellLayout.hideFolderAccept(FolderRingAnimator.this);
                    }
                    if (FolderRingAnimator.this.mFolderIcon != null) {
                        FolderRingAnimator.this.mFolderIcon.mPreviewBackground.setVisibility(0);
                    }
                }
            });
            this.mNeutralAnimator.start();
        }

        public void getCell(int[] loc) {
            loc[0] = this.mCellX;
            loc[1] = this.mCellY;
        }

        public void setCell(int x, int y) {
            this.mCellX = x;
            this.mCellY = y;
        }

        public void setCellLayout(CellLayout layout) {
            this.mCellLayout = layout;
        }

        public float getOuterRingSize() {
            return this.mOuterRingSize;
        }

        public float getInnerRingSize() {
            return this.mInnerRingSize;
        }
    }

    /* access modifiers changed from: package-private */
    public Folder getFolder() {
        return this.mFolder;
    }

    /* access modifiers changed from: package-private */
    public FolderInfo getFolderInfo() {
        return this.mInfo;
    }

    private boolean willAcceptItem(ItemInfo item) {
        int itemType = item.itemType;
        if ((itemType == 0 || itemType == 1) && !this.mFolder.isFull() && item != this.mInfo && !this.mInfo.opened) {
            return true;
        }
        return false;
    }

    public boolean acceptDrop(Object dragInfo) {
        return !this.mFolder.isDestroyed() && willAcceptItem((ItemInfo) dragInfo);
    }

    public void addItem(ShortcutInfo item) {
        this.mInfo.add(item);
    }

    public void onDragEnter(Object dragInfo) {
        if (!this.mFolder.isDestroyed() && willAcceptItem((ItemInfo) dragInfo)) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) getLayoutParams();
            CellLayout layout = (CellLayout) getParent().getParent();
            this.mFolderRingAnimator.setCell(lp.cellX, lp.cellY);
            this.mFolderRingAnimator.setCellLayout(layout);
            this.mFolderRingAnimator.animateToAcceptState();
            layout.showFolderAccept(this.mFolderRingAnimator);
        }
    }

    public void onDragOver(Object dragInfo) {
    }

    public void performCreateAnimation(ShortcutInfo destInfo, View destView, ShortcutInfo srcInfo, DragView srcView, Rect dstRect, float scaleRelativeToDragLayer, Runnable postAnimationRunnable) {
        Drawable animateDrawable = ((TextView) destView).getCompoundDrawables()[1];
        computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(), destView.getMeasuredWidth());
        animateFirstItem(animateDrawable, INITIAL_ITEM_ANIMATION_DURATION, false, (Runnable) null);
        addItem(destInfo);
        onDrop(srcInfo, srcView, dstRect, scaleRelativeToDragLayer, 1, postAnimationRunnable, (DropTarget.DragObject) null);
    }

    public void performDestroyAnimation(View finalView, Runnable onCompleteRunnable) {
        Drawable animateDrawable = ((TextView) finalView).getCompoundDrawables()[1];
        computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(), finalView.getMeasuredWidth());
        animateFirstItem(animateDrawable, 200, true, onCompleteRunnable);
    }

    public void onDragExit(Object dragInfo) {
        onDragExit();
    }

    public void onDragExit() {
        this.mFolderRingAnimator.animateToNaturalState();
    }

    private void onDrop(ShortcutInfo item, DragView animateView, Rect finalRect, float scaleRelativeToDragLayer, int index, Runnable postAnimationRunnable, DropTarget.DragObject d) {
        item.cellX = -1;
        item.cellY = -1;
        if (animateView != null) {
            DragLayer dragLayer = this.mLauncher.getDragLayer();
            Rect from = new Rect();
            dragLayer.getViewRectRelativeToSelf(animateView, from);
            Rect to = finalRect;
            if (to == null) {
                to = new Rect();
                Workspace workspace = this.mLauncher.getWorkspace();
                workspace.setFinalTransitionTransform((CellLayout) getParent().getParent());
                float scaleX = getScaleX();
                float scaleY = getScaleY();
                setScaleX(1.0f);
                setScaleY(1.0f);
                scaleRelativeToDragLayer = dragLayer.getDescendantRectRelativeToSelf(this, to);
                setScaleX(scaleX);
                setScaleY(scaleY);
                workspace.resetTransitionTransform((CellLayout) getParent().getParent());
            }
            int[] center = new int[2];
            float scale = getLocalCenterForIndex(index, center);
            center[0] = Math.round(((float) center[0]) * scaleRelativeToDragLayer);
            center[1] = Math.round(((float) center[1]) * scaleRelativeToDragLayer);
            to.offset(center[0] - (animateView.getMeasuredWidth() / 2), center[1] - (animateView.getMeasuredHeight() / 2));
            float finalScale = scale * scaleRelativeToDragLayer;
            dragLayer.animateView(animateView, from, to, index < 6 ? 0.5f : 0.0f, 1.0f, 1.0f, finalScale, finalScale, 400, new DecelerateInterpolator(2.0f), new AccelerateInterpolator(2.0f), postAnimationRunnable, 0, (View) null);
            addItem(item);
            this.mHiddenItems.add(item);
            this.mFolder.hideItem(item);
            final ShortcutInfo shortcutInfo = item;
            postDelayed(new Runnable() {
                public void run() {
                    FolderIcon.this.mHiddenItems.remove(shortcutInfo);
                    FolderIcon.this.mFolder.showItem(shortcutInfo);
                    FolderIcon.this.invalidate();
                }
            }, 400);
            return;
        }
        addItem(item);
    }

    public void onDrop(DropTarget.DragObject d) {
        ShortcutInfo item;
        if (d.dragInfo instanceof ApplicationInfo) {
            item = ((ApplicationInfo) d.dragInfo).makeShortcut();
        } else {
            item = (ShortcutInfo) d.dragInfo;
        }
        this.mFolder.notifyDrop();
        onDrop(item, d.dragView, (Rect) null, 1.0f, this.mInfo.contents.size(), d.postAnimationRunnable, d);
    }

    public DropTarget getDropTargetDelegate(DropTarget.DragObject d) {
        return null;
    }

    private void computePreviewDrawingParams(int drawableSize, int totalSize) {
        if (this.mIntrinsicIconSize != drawableSize || this.mTotalWidth != totalSize) {
            this.mIntrinsicIconSize = drawableSize;
            this.mTotalWidth = totalSize;
            this.mAvailableSpaceInPreview = FolderRingAnimator.sPreviewSize - (FolderRingAnimator.sPreviewPadding * 2);
            this.mBaselineIconScale = (1.0f * ((float) ((int) (((float) (this.mAvailableSpaceInPreview / 2)) * 1.8f)))) / ((float) ((int) (((float) this.mIntrinsicIconSize) * 1.24f)));
            this.mBaselineIconSize = (int) (((float) this.mIntrinsicIconSize) * this.mBaselineIconScale);
            this.mMaxPerspectiveShift = ((float) this.mBaselineIconSize) * PERSPECTIVE_SHIFT_FACTOR;
            this.mPreviewOffsetX = 23;
            this.mPreviewOffsetY = 25;
        }
    }

    private void computePreviewDrawingParams(Drawable d) {
        computePreviewDrawingParams(d.getIntrinsicWidth(), getMeasuredWidth());
    }

    class PreviewItemDrawingParams {
        Drawable drawable;
        int overlayAlpha;
        float scale;
        float transX;
        float transY;

        PreviewItemDrawingParams(float transX2, float transY2, float scale2, int overlayAlpha2) {
            this.transX = transX2;
            this.transY = transY2;
            this.scale = scale2;
            this.overlayAlpha = overlayAlpha2;
        }
    }

    private float getLocalCenterForIndex(int index, int[] center) {
        this.mParams = computePreviewItemDrawingParams(Math.min(6, index), this.mParams);
        this.mParams.transX += (float) this.mPreviewOffsetX;
        this.mParams.transY += (float) this.mPreviewOffsetY;
        float offsetX = this.mParams.transX + ((this.mParams.scale * ((float) this.mIntrinsicIconSize)) / 2.0f);
        float offsetY = this.mParams.transY + ((this.mParams.scale * ((float) this.mIntrinsicIconSize)) / 2.0f);
        center[0] = Math.round(offsetX);
        center[1] = Math.round(offsetY);
        return this.mParams.scale;
    }

    private PreviewItemDrawingParams computePreviewItemDrawingParams(int index, PreviewItemDrawingParams params) {
        if (params == null) {
            return new PreviewItemDrawingParams((float) ((index % 3) * 38), (float) ((index / 3) * 40), OUTER_RING_GROWTH_FACTOR, 100);
        }
        params.transX = (float) ((index % 3) * 37);
        params.transY = (float) ((index / 3) * 40);
        params.scale = OUTER_RING_GROWTH_FACTOR;
        params.overlayAlpha = 0;
        return params;
    }

    private void drawPreviewItem(Canvas canvas, PreviewItemDrawingParams params) {
        canvas.save();
        canvas.translate(params.transX + ((float) this.mPreviewOffsetX), params.transY + ((float) this.mPreviewOffsetY));
        canvas.scale(params.scale, params.scale);
        Drawable d = params.drawable;
        if (d != null) {
            d.setBounds(0, 0, this.mIntrinsicIconSize, this.mIntrinsicIconSize);
            d.setFilterBitmap(true);
            d.setColorFilter(Color.argb(params.overlayAlpha, 0, 0, 0), PorterDuff.Mode.SRC_ATOP);
            d.draw(canvas);
            d.clearColorFilter();
            d.setFilterBitmap(false);
        }
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mFolder != null) {
            if (this.mFolder.getItemCount() != 0 || this.mAnimating) {
                ArrayList<View> items = this.mFolder.getItemsInReadingOrder();
                if (this.mAnimating) {
                    computePreviewDrawingParams(this.mAnimParams.drawable);
                } else {
                    computePreviewDrawingParams(((TextView) items.get(0)).getCompoundDrawables()[1]);
                }
                int nItemsInPreview = Math.min(items.size(), 6);
                if (!this.mAnimating) {
                    for (int i = 0; i < nItemsInPreview; i++) {
                        TextView v = (TextView) items.get(i);
                        if (!this.mHiddenItems.contains(v.getTag())) {
                            Drawable d = v.getCompoundDrawables()[1];
                            this.mParams = computePreviewItemDrawingParams(i, this.mParams);
                            this.mParams.drawable = d;
                            drawPreviewItem(canvas, this.mParams);
                        }
                    }
                    return;
                }
                drawPreviewItem(canvas, this.mAnimParams);
            }
        }
    }

    private void animateFirstItem(Drawable d, int duration, boolean reverse, final Runnable onCompleteRunnable) {
        final PreviewItemDrawingParams finalParams = computePreviewItemDrawingParams(0, (PreviewItemDrawingParams) null);
        final float transX0 = (float) ((this.mAvailableSpaceInPreview - d.getIntrinsicWidth()) / 2);
        final float transY0 = (float) ((this.mAvailableSpaceInPreview - d.getIntrinsicHeight()) / 2);
        this.mAnimParams.drawable = d;
        ValueAnimator va = LauncherAnimUtils.ofFloat(this, 0.0f, 1.0f);
        final boolean z = reverse;
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = ((Float) animation.getAnimatedValue()).floatValue();
                if (z) {
                    progress = 1.0f - progress;
                    FolderIcon.this.mPreviewBackground.setAlpha(progress);
                }
                FolderIcon.this.mAnimParams.transX = transX0 + ((finalParams.transX - transX0) * progress);
                FolderIcon.this.mAnimParams.transY = transY0 + ((finalParams.transY - transY0) * progress);
                FolderIcon.this.mAnimParams.scale = ((finalParams.scale - 1.0f) * progress) + 1.0f;
                FolderIcon.this.invalidate();
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                FolderIcon.this.mAnimating = true;
            }

            public void onAnimationEnd(Animator animation) {
                FolderIcon.this.mAnimating = false;
                if (onCompleteRunnable != null) {
                    onCompleteRunnable.run();
                }
            }
        });
        va.setDuration((long) duration);
        va.start();
    }

    public void setTextVisible(boolean visible) {
        if (visible) {
            this.mFolderName.setVisibility(0);
        } else {
            this.mFolderName.setVisibility(4);
        }
    }

    public boolean getTextVisible() {
        return this.mFolderName.getVisibility() == 0;
    }

    public void onItemsChanged() {
        invalidate();
        requestLayout();
    }

    public void onAdd(ShortcutInfo item) {
        invalidate();
        requestLayout();
    }

    public void onRemove(ShortcutInfo item) {
        invalidate();
        requestLayout();
    }

    public void onTitleChanged(CharSequence title) {
        this.mFolderName.setText(title.toString());
        setContentDescription(String.format(getContext().getString(R.string.folder_name_format), new Object[]{title}));
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        switch (event.getAction()) {
            case 0:
                this.mLongPressHelper.postCheckForLongPress();
                break;
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                break;
        }
        return result;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }
}
