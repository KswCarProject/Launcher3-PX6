package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.launcher3.Alarm;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.CheckLongPressHelper;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.SimpleOnStylusPressListener;
import com.android.launcher3.StylusEventHelper;
import com.android.launcher3.Utilities;
import com.android.launcher3.badge.BadgeRenderer;
import com.android.launcher3.badge.FolderBadgeInfo;
import com.android.launcher3.dragndrop.BaseItemDragListener;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.util.ArrayList;
import java.util.List;

public class FolderIcon extends FrameLayout implements FolderInfo.FolderListener {
    private static final Property<FolderIcon, Float> BADGE_SCALE_PROPERTY = new Property<FolderIcon, Float>(Float.TYPE, "badgeScale") {
        public Float get(FolderIcon folderIcon) {
            return Float.valueOf(folderIcon.mBadgeScale);
        }

        public void set(FolderIcon folderIcon, Float value) {
            float unused = folderIcon.mBadgeScale = value.floatValue();
            folderIcon.invalidate();
        }
    };
    static final int DROP_IN_ANIMATION_DURATION = 400;
    private static final int ON_OPEN_DELAY = 800;
    public static final boolean SPRING_LOADING_ENABLED = true;
    static boolean sStaticValuesDirty = true;
    boolean mAnimating = false;
    PreviewBackground mBackground = new PreviewBackground();
    private boolean mBackgroundIsVisible = true;
    private FolderBadgeInfo mBadgeInfo = new FolderBadgeInfo();
    private BadgeRenderer mBadgeRenderer;
    /* access modifiers changed from: private */
    public float mBadgeScale;
    private List<BubbleTextView> mCurrentPreviewItems = new ArrayList();
    Folder mFolder;
    BubbleTextView mFolderName;
    private FolderInfo mInfo;
    Launcher mLauncher;
    private CheckLongPressHelper mLongPressHelper;
    OnAlarmListener mOnOpenListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            FolderIcon.this.mFolder.beginExternalDrag();
            FolderIcon.this.mFolder.animateOpen();
        }
    };
    private Alarm mOpenAlarm = new Alarm();
    /* access modifiers changed from: private */
    public PreviewItemManager mPreviewItemManager;
    ClippedFolderIconLayoutRule mPreviewLayoutRule;
    FolderIconPreviewVerifier mPreviewVerifier;
    private float mSlop;
    private StylusEventHelper mStylusEventHelper;
    private Rect mTempBounds = new Rect();
    private Point mTempSpaceForBadgeOffset = new Point();
    private PreviewItemDrawingParams mTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f);

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
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mPreviewLayoutRule = new ClippedFolderIconLayoutRule();
        this.mSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mPreviewItemManager = new PreviewItemManager(this);
    }

    public static FolderIcon fromXml(int resId, Launcher launcher, ViewGroup group, FolderInfo folderInfo) {
        DeviceProfile grid = launcher.getDeviceProfile();
        FolderIcon icon = (FolderIcon) LayoutInflater.from(group.getContext()).inflate(resId, group, false);
        icon.setClipToPadding(false);
        icon.mFolderName = (BubbleTextView) icon.findViewById(R.id.folder_icon_name);
        icon.mFolderName.setText(folderInfo.title);
        icon.mFolderName.setCompoundDrawablePadding(0);
        ((FrameLayout.LayoutParams) icon.mFolderName.getLayoutParams()).topMargin = grid.iconSizePx + grid.iconDrawablePaddingPx;
        icon.setTag(folderInfo);
        icon.setOnClickListener(ItemClickHandler.INSTANCE);
        icon.mInfo = folderInfo;
        icon.mLauncher = launcher;
        icon.mBadgeRenderer = launcher.getDeviceProfile().mBadgeRenderer;
        icon.setContentDescription(launcher.getString(R.string.folder_name_format, new Object[]{folderInfo.title}));
        Folder folder = Folder.fromXml(launcher);
        folder.setDragController(launcher.getDragController());
        folder.setFolderIcon(icon);
        folder.bind(folderInfo);
        icon.setFolder(folder);
        icon.setAccessibilityDelegate(launcher.getAccessibilityDelegate());
        folderInfo.addListener(icon);
        icon.setOnFocusChangeListener(launcher.mFocusHandler);
        return icon;
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        sStaticValuesDirty = true;
        return super.onSaveInstanceState();
    }

    public Folder getFolder() {
        return this.mFolder;
    }

    private void setFolder(Folder folder) {
        this.mFolder = folder;
        this.mPreviewVerifier = new FolderIconPreviewVerifier(this.mLauncher.getDeviceProfile().inv);
        updatePreviewItems(false);
    }

    private boolean willAcceptItem(ItemInfo item) {
        int itemType = item.itemType;
        if ((itemType == 0 || itemType == 1 || itemType == 6) && item != this.mInfo && !this.mFolder.isOpen()) {
            return true;
        }
        return false;
    }

    public boolean acceptDrop(ItemInfo dragInfo) {
        return !this.mFolder.isDestroyed() && willAcceptItem(dragInfo);
    }

    public void addItem(ShortcutInfo item) {
        addItem(item, true);
    }

    public void addItem(ShortcutInfo item, boolean animate) {
        this.mInfo.add(item, animate);
    }

    public void removeItem(ShortcutInfo item, boolean animate) {
        this.mInfo.remove(item, animate);
    }

    public void onDragEnter(ItemInfo dragInfo) {
        if (!this.mFolder.isDestroyed() && willAcceptItem(dragInfo)) {
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) getLayoutParams();
            this.mBackground.animateToAccept((CellLayout) getParent().getParent(), lp.cellX, lp.cellY);
            this.mOpenAlarm.setOnAlarmListener(this.mOnOpenListener);
            if ((dragInfo instanceof AppInfo) || (dragInfo instanceof ShortcutInfo) || (dragInfo instanceof PendingAddShortcutInfo)) {
                this.mOpenAlarm.setAlarm(800);
            }
        }
    }

    public Drawable prepareCreateAnimation(View destView) {
        return this.mPreviewItemManager.prepareCreateAnimation(destView);
    }

    public void performCreateAnimation(ShortcutInfo destInfo, View destView, ShortcutInfo srcInfo, DragView srcView, Rect dstRect, float scaleRelativeToDragLayer) {
        prepareCreateAnimation(destView);
        addItem(destInfo);
        this.mPreviewItemManager.createFirstItemAnimation(false, (Runnable) null).start();
        onDrop(srcInfo, srcView, dstRect, scaleRelativeToDragLayer, 1, false);
    }

    public void performDestroyAnimation(Runnable onCompleteRunnable) {
        this.mPreviewItemManager.createFirstItemAnimation(true, onCompleteRunnable).start();
    }

    public void onDragExit() {
        this.mBackground.animateToRest();
        this.mOpenAlarm.cancelAlarm();
    }

    /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
        java.lang.NullPointerException
        	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
        	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
        */
    private void onDrop(com.android.launcher3.ShortcutInfo r31, com.android.launcher3.dragndrop.DragView r32, android.graphics.Rect r33, float r34, int r35, boolean r36) {
        /*
            r30 = this;
            r0 = r30
            r1 = r31
            r15 = r32
            r2 = r35
            r3 = -1
            r1.cellX = r3
            r1.cellY = r3
            if (r15 == 0) goto L_0x014e
            com.android.launcher3.Launcher r3 = r0.mLauncher
            com.android.launcher3.dragndrop.DragLayer r14 = r3.getDragLayer()
            android.graphics.Rect r3 = new android.graphics.Rect
            r3.<init>()
            r13 = r3
            r14.getViewRectRelativeToSelf(r15, r13)
            r3 = r33
            if (r3 != 0) goto L_0x0052
            android.graphics.Rect r4 = new android.graphics.Rect
            r4.<init>()
            r3 = r4
            com.android.launcher3.Launcher r4 = r0.mLauncher
            com.android.launcher3.Workspace r4 = r4.getWorkspace()
            r4.setFinalTransitionTransform()
            float r5 = r30.getScaleX()
            float r6 = r30.getScaleY()
            r7 = 1065353216(0x3f800000, float:1.0)
            r0.setScaleX(r7)
            r0.setScaleY(r7)
            float r7 = r14.getDescendantRectRelativeToSelf(r0, r3)
            r0.setScaleX(r5)
            r0.setScaleY(r6)
            r4.resetTransitionTransform()
            r12 = r3
            r17 = r7
            goto L_0x0055
        L_0x0052:
            r17 = r34
            r12 = r3
        L_0x0055:
            int r3 = r2 + 1
            r4 = 4
            int r11 = java.lang.Math.min(r4, r3)
            r3 = 0
            r5 = 0
            r10 = 1
            if (r36 != 0) goto L_0x0063
            if (r2 < r4) goto L_0x00b7
        L_0x0063:
            java.util.ArrayList r6 = new java.util.ArrayList
            java.util.List<com.android.launcher3.BubbleTextView> r7 = r0.mCurrentPreviewItems
            r6.<init>(r7)
            r0.addItem(r1, r5)
            java.util.List<com.android.launcher3.BubbleTextView> r7 = r0.mCurrentPreviewItems
            r7.clear()
            java.util.List<com.android.launcher3.BubbleTextView> r7 = r0.mCurrentPreviewItems
            java.util.List r8 = r30.getPreviewItems()
            r7.addAll(r8)
            java.util.List<com.android.launcher3.BubbleTextView> r7 = r0.mCurrentPreviewItems
            boolean r7 = r6.equals(r7)
            if (r7 != 0) goto L_0x00b4
            r7 = r2
            r2 = 0
        L_0x0085:
            java.util.List<com.android.launcher3.BubbleTextView> r8 = r0.mCurrentPreviewItems
            int r8 = r8.size()
            if (r2 >= r8) goto L_0x00a3
            java.util.List<com.android.launcher3.BubbleTextView> r8 = r0.mCurrentPreviewItems
            java.lang.Object r8 = r8.get(r2)
            com.android.launcher3.BubbleTextView r8 = (com.android.launcher3.BubbleTextView) r8
            java.lang.Object r8 = r8.getTag()
            boolean r8 = r8.equals(r1)
            if (r8 == 0) goto L_0x00a0
            r7 = r2
        L_0x00a0:
            int r2 = r2 + 1
            goto L_0x0085
        L_0x00a3:
            com.android.launcher3.folder.PreviewItemManager r2 = r0.mPreviewItemManager
            r2.hidePreviewItem(r7, r10)
            com.android.launcher3.folder.PreviewItemManager r2 = r0.mPreviewItemManager
            java.util.List<com.android.launcher3.BubbleTextView> r8 = r0.mCurrentPreviewItems
            r2.onDrop(r6, r8, r1)
            r3 = 1
            r18 = r3
            r9 = r7
            goto L_0x00ba
        L_0x00b4:
            r0.removeItem(r1, r5)
        L_0x00b7:
            r9 = r2
            r18 = r3
        L_0x00ba:
            if (r18 != 0) goto L_0x00bf
            r30.addItem(r31)
        L_0x00bf:
            r2 = 2
            int[] r8 = new int[r2]
            float r19 = r0.getLocalCenterForIndex(r9, r11, r8)
            r3 = r8[r5]
            float r3 = (float) r3
            float r3 = r3 * r17
            int r3 = java.lang.Math.round(r3)
            r8[r5] = r3
            r3 = r8[r10]
            float r3 = (float) r3
            float r3 = r3 * r17
            int r3 = java.lang.Math.round(r3)
            r8[r10] = r3
            r3 = r8[r5]
            int r5 = r32.getMeasuredWidth()
            int r5 = r5 / r2
            int r3 = r3 - r5
            r5 = r8[r10]
            int r6 = r32.getMeasuredHeight()
            int r6 = r6 / r2
            int r5 = r5 - r6
            r12.offset(r3, r5)
            if (r9 >= r4) goto L_0x00f6
            r2 = 1056964608(0x3f000000, float:0.5)
            r6 = 1056964608(0x3f000000, float:0.5)
            goto L_0x00f8
        L_0x00f6:
            r2 = 0
            r6 = 0
        L_0x00f8:
            float r20 = r19 * r17
            r7 = 1065353216(0x3f800000, float:1.0)
            r16 = 1065353216(0x3f800000, float:1.0)
            r21 = 400(0x190, float:5.6E-43)
            android.view.animation.Interpolator r22 = com.android.launcher3.anim.Interpolators.DEACCEL_2
            android.view.animation.Interpolator r23 = com.android.launcher3.anim.Interpolators.ACCEL_2
            r24 = 0
            r25 = 0
            r26 = 0
            r2 = r14
            r3 = r32
            r4 = r13
            r5 = r12
            r27 = r8
            r8 = r16
            r28 = r9
            r9 = r20
            r10 = r20
            r29 = r11
            r11 = r21
            r21 = r12
            r12 = r22
            r22 = r13
            r13 = r23
            r23 = r14
            r14 = r24
            r15 = r25
            r16 = r26
            r2.animateView(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)
            com.android.launcher3.folder.Folder r2 = r0.mFolder
            r2.hideItem(r1)
            if (r18 != 0) goto L_0x0140
            com.android.launcher3.folder.PreviewItemManager r2 = r0.mPreviewItemManager
            r7 = r28
            r3 = 1
            r2.hidePreviewItem(r7, r3)
            goto L_0x0142
        L_0x0140:
            r7 = r28
        L_0x0142:
            r2 = r7
            com.android.launcher3.folder.FolderIcon$3 r3 = new com.android.launcher3.folder.FolderIcon$3
            r3.<init>(r2, r1)
            r4 = 400(0x190, double:1.976E-321)
            r0.postDelayed(r3, r4)
            goto L_0x0153
        L_0x014e:
            r30.addItem(r31)
            r17 = r34
        L_0x0153:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.FolderIcon.onDrop(com.android.launcher3.ShortcutInfo, com.android.launcher3.dragndrop.DragView, android.graphics.Rect, float, int, boolean):void");
    }

    public void onDrop(DropTarget.DragObject d, boolean itemReturnedOnFailedDrop) {
        ShortcutInfo shortcutInfo;
        if (d.dragInfo instanceof AppInfo) {
            shortcutInfo = ((AppInfo) d.dragInfo).makeShortcut();
        } else if (d.dragSource instanceof BaseItemDragListener) {
            shortcutInfo = new ShortcutInfo((ShortcutInfo) d.dragInfo);
        } else {
            shortcutInfo = (ShortcutInfo) d.dragInfo;
        }
        ShortcutInfo item = shortcutInfo;
        this.mFolder.notifyDrop();
        onDrop(item, d.dragView, (Rect) null, 1.0f, this.mInfo.contents.size(), itemReturnedOnFailedDrop);
    }

    public void setBadgeInfo(FolderBadgeInfo badgeInfo) {
        updateBadgeScale(this.mBadgeInfo.hasBadge(), badgeInfo.hasBadge());
        this.mBadgeInfo = badgeInfo;
    }

    public ClippedFolderIconLayoutRule getLayoutRule() {
        return this.mPreviewLayoutRule;
    }

    private void updateBadgeScale(boolean wasBadged, boolean isBadged) {
        float newBadgeScale = isBadged ? 1.0f : 0.0f;
        if (!(wasBadged ^ isBadged) || !isShown()) {
            this.mBadgeScale = newBadgeScale;
            invalidate();
            return;
        }
        createBadgeScaleAnimator(newBadgeScale).start();
    }

    public Animator createBadgeScaleAnimator(float... badgeScales) {
        return ObjectAnimator.ofFloat(this, BADGE_SCALE_PROPERTY, badgeScales);
    }

    public boolean hasBadge() {
        return this.mBadgeInfo != null && this.mBadgeInfo.hasBadge();
    }

    private float getLocalCenterForIndex(int index, int curNumItems, int[] center) {
        this.mTmpParams = this.mPreviewItemManager.computePreviewItemDrawingParams(Math.min(4, index), curNumItems, this.mTmpParams);
        this.mTmpParams.transX += (float) this.mBackground.basePreviewOffsetX;
        this.mTmpParams.transY += (float) this.mBackground.basePreviewOffsetY;
        float intrinsicIconSize = this.mPreviewItemManager.getIntrinsicIconSize();
        center[0] = Math.round(this.mTmpParams.transX + ((this.mTmpParams.scale * intrinsicIconSize) / 2.0f));
        center[1] = Math.round(this.mTmpParams.transY + ((this.mTmpParams.scale * intrinsicIconSize) / 2.0f));
        return this.mTmpParams.scale;
    }

    public void setFolderBackground(PreviewBackground bg) {
        this.mBackground = bg;
        this.mBackground.setInvalidateDelegate(this);
    }

    public void setBackgroundVisible(boolean visible) {
        this.mBackgroundIsVisible = visible;
        invalidate();
    }

    public PreviewBackground getFolderBackground() {
        return this.mBackground;
    }

    public PreviewItemManager getPreviewItemManager() {
        return this.mPreviewItemManager;
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        int saveCount;
        super.dispatchDraw(canvas);
        if (this.mBackgroundIsVisible) {
            this.mPreviewItemManager.recomputePreviewDrawingParams();
            if (!this.mBackground.drawingDelegated()) {
                this.mBackground.drawBackground(canvas);
            }
            if (this.mFolder != null) {
                if (this.mFolder.getItemCount() != 0 || this.mAnimating) {
                    if (canvas.isHardwareAccelerated()) {
                        saveCount = canvas.saveLayer(0.0f, 0.0f, (float) getWidth(), (float) getHeight(), (Paint) null);
                    } else {
                        saveCount = canvas.save();
                        canvas.clipPath(this.mBackground.getClipPath());
                    }
                    this.mPreviewItemManager.draw(canvas);
                    if (canvas.isHardwareAccelerated()) {
                        this.mBackground.clipCanvasHardware(canvas);
                    }
                    canvas.restoreToCount(saveCount);
                    if (!this.mBackground.drawingDelegated()) {
                        this.mBackground.drawBackgroundStroke(canvas);
                    }
                    drawBadge(canvas);
                }
            }
        }
    }

    public void drawBadge(Canvas canvas) {
        if ((this.mBadgeInfo != null && this.mBadgeInfo.hasBadge()) || this.mBadgeScale > 0.0f) {
            int offsetX = this.mBackground.getOffsetX();
            int offsetY = this.mBackground.getOffsetY();
            int previewSize = (int) (((float) this.mBackground.previewSize) * this.mBackground.mScale);
            this.mTempBounds.set(offsetX, offsetY, offsetX + previewSize, offsetY + previewSize);
            float badgeScale = Math.max(0.0f, this.mBadgeScale - this.mBackground.getScaleProgress());
            this.mTempSpaceForBadgeOffset.set(getWidth() - this.mTempBounds.right, this.mTempBounds.top);
            this.mBadgeRenderer.draw(canvas, this.mBackground.getBadgeColor(), this.mTempBounds, badgeScale, this.mTempSpaceForBadgeOffset);
        }
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

    public List<BubbleTextView> getPreviewItems() {
        return getPreviewItemsOnPage(0);
    }

    public List<BubbleTextView> getPreviewItemsOnPage(int page) {
        this.mPreviewVerifier.setFolderInfo(this.mFolder.getInfo());
        List<BubbleTextView> itemsToDisplay = new ArrayList<>();
        List<BubbleTextView> itemsOnPage = this.mFolder.getItemsOnPage(page);
        int numItems = itemsOnPage.size();
        for (int rank = 0; rank < numItems; rank++) {
            if (this.mPreviewVerifier.isItemInPreview(page, rank)) {
                itemsToDisplay.add(itemsOnPage.get(rank));
            }
            if (itemsToDisplay.size() == 4) {
                break;
            }
        }
        return itemsToDisplay;
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(@NonNull Drawable who) {
        return this.mPreviewItemManager.verifyDrawable(who) || super.verifyDrawable(who);
    }

    public void onItemsChanged(boolean animate) {
        updatePreviewItems(animate);
        invalidate();
        requestLayout();
    }

    private void updatePreviewItems(boolean animate) {
        this.mPreviewItemManager.updatePreviewItems(animate);
        this.mCurrentPreviewItems.clear();
        this.mCurrentPreviewItems.addAll(getPreviewItems());
    }

    public void prepareAutoUpdate() {
    }

    public void onAdd(ShortcutInfo item, int rank) {
        boolean wasBadged = this.mBadgeInfo.hasBadge();
        this.mBadgeInfo.addBadgeInfo(this.mLauncher.getBadgeInfoForItem(item));
        updateBadgeScale(wasBadged, this.mBadgeInfo.hasBadge());
        invalidate();
        requestLayout();
    }

    public void onRemove(ShortcutInfo item) {
        boolean wasBadged = this.mBadgeInfo.hasBadge();
        this.mBadgeInfo.subtractBadgeInfo(this.mLauncher.getBadgeInfoForItem(item));
        updateBadgeScale(wasBadged, this.mBadgeInfo.hasBadge());
        invalidate();
        requestLayout();
    }

    public void onTitleChanged(CharSequence title) {
        this.mFolderName.setText(title);
        setContentDescription(getContext().getString(R.string.folder_name_format, new Object[]{title}));
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (this.mStylusEventHelper.onMotionEvent(event)) {
            this.mLongPressHelper.cancelLongPress();
            return true;
        }
        switch (event.getAction()) {
            case 0:
                this.mLongPressHelper.postCheckForLongPress();
                break;
            case 1:
            case 3:
                this.mLongPressHelper.cancelLongPress();
                break;
            case 2:
                if (!Utilities.pointInView(this, event.getX(), event.getY(), this.mSlop)) {
                    this.mLongPressHelper.cancelLongPress();
                    break;
                }
                break;
        }
        return result;
    }

    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    public void removeListeners() {
        this.mInfo.removeListener(this);
        this.mInfo.removeListener(this.mFolder);
    }

    public void clearLeaveBehindIfExists() {
        ((CellLayout.LayoutParams) getLayoutParams()).canReorder = true;
        if (this.mInfo.container == -101) {
            ((CellLayout) getParent().getParent()).clearFolderLeaveBehind();
        }
    }

    public void drawLeaveBehindIfExists() {
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) getLayoutParams();
        lp.canReorder = false;
        if (this.mInfo.container == -101) {
            ((CellLayout) getParent().getParent()).setFolderLeaveBehindCell(lp.cellX, lp.cellY);
        }
    }

    public void onFolderClose(int currentPage) {
        this.mPreviewItemManager.onFolderClose(currentPage);
    }
}
