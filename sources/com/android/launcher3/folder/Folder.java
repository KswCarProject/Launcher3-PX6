package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ActionMode;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Alarm;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.OnAlarmListener;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.AccessibleDragListenerAdapter;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Folder extends AbstractFloatingView implements DragSource, View.OnLongClickListener, DropTarget, FolderInfo.FolderListener, TextView.OnEditorActionListener, View.OnFocusChangeListener, DragController.DragListener, ExtendedEditText.OnBackKeyListener {
    private static final int FOLDER_NAME_ANIMATION_DURATION = 633;
    private static final float ICON_OVERSCROLL_WIDTH_FACTOR = 0.45f;
    public static final Comparator<ItemInfo> ITEM_POS_COMPARATOR = new Comparator<ItemInfo>() {
        public int compare(ItemInfo lhs, ItemInfo rhs) {
            if (lhs.rank != rhs.rank) {
                return lhs.rank - rhs.rank;
            }
            if (lhs.cellY != rhs.cellY) {
                return lhs.cellY - rhs.cellY;
            }
            return lhs.cellX - rhs.cellX;
        }
    };
    private static final int MIN_CONTENT_DIMEN = 5;
    private static final int ON_EXIT_CLOSE_DELAY = 400;
    private static final int REORDER_DELAY = 250;
    public static final int RESCROLL_DELAY = 900;
    public static final int SCROLL_HINT_DURATION = 500;
    public static final int SCROLL_LEFT = 0;
    public static final int SCROLL_NONE = -1;
    public static final int SCROLL_RIGHT = 1;
    static final int STATE_ANIMATING = 1;
    static final int STATE_NONE = -1;
    static final int STATE_OPEN = 2;
    static final int STATE_SMALL = 0;
    private static final String TAG = "Launcher.Folder";
    private static String sDefaultFolderName;
    private static String sHintText;
    private static final Rect sTempRect = new Rect();
    FolderPagedView mContent;
    /* access modifiers changed from: private */
    public AnimatorSet mCurrentAnimator;
    private View mCurrentDragView;
    int mCurrentScrollDir = -1;
    private boolean mDeleteFolderOnDropCompleted = false;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mDestroyed;
    protected DragController mDragController;
    private boolean mDragInProgress = false;
    int mEmptyCellRank;
    FolderIcon mFolderIcon;
    float mFolderIconPivotX;
    float mFolderIconPivotY;
    public ExtendedEditText mFolderName;
    /* access modifiers changed from: private */
    public View mFooter;
    private int mFooterHeight;
    public FolderInfo mInfo;
    /* access modifiers changed from: private */
    public boolean mIsEditingName = false;
    private boolean mIsExternalDrag;
    private boolean mItemAddedBackToSelfViaIcon = false;
    final ArrayList<View> mItemsInReadingOrder = new ArrayList<>();
    boolean mItemsInvalidated = false;
    protected final Launcher mLauncher;
    private final Alarm mOnExitAlarm = new Alarm();
    OnAlarmListener mOnExitAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.completeDragExit();
        }
    };
    private final Alarm mOnScrollHintAlarm = new Alarm();
    /* access modifiers changed from: private */
    public PageIndicatorDots mPageIndicator;
    int mPrevTargetRank;
    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mRearrangeOnClose = false;
    private final Alarm mReorderAlarm = new Alarm();
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.mContent.realTimeReorder(Folder.this.mEmptyCellRank, Folder.this.mTargetRank);
            Folder.this.mEmptyCellRank = Folder.this.mTargetRank;
        }
    };
    private int mScrollAreaOffset;
    int mScrollHintDir = -1;
    final Alarm mScrollPauseAlarm = new Alarm();
    @ViewDebug.ExportedProperty(category = "launcher", mapping = {@ViewDebug.IntToString(from = -1, to = "STATE_NONE"), @ViewDebug.IntToString(from = 0, to = "STATE_SMALL"), @ViewDebug.IntToString(from = 1, to = "STATE_ANIMATING"), @ViewDebug.IntToString(from = 2, to = "STATE_OPEN")})
    int mState = -1;
    private boolean mSuppressFolderDeletion = false;
    int mTargetRank;

    public Folder(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAlwaysDrawnWithCacheEnabled(false);
        Resources res = getResources();
        if (sDefaultFolderName == null) {
            sDefaultFolderName = res.getString(R.string.folder_name);
        }
        if (sHintText == null) {
            sHintText = res.getString(R.string.folder_hint_text);
        }
        this.mLauncher = Launcher.getLauncher(context);
        setFocusableInTouchMode(true);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (FolderPagedView) findViewById(R.id.folder_content);
        this.mContent.setFolder(this);
        this.mPageIndicator = (PageIndicatorDots) findViewById(R.id.folder_page_indicator);
        this.mFolderName = (ExtendedEditText) findViewById(R.id.folder_name);
        this.mFolderName.setOnBackKeyListener(this);
        this.mFolderName.setOnFocusChangeListener(this);
        if (!Utilities.ATLEAST_MARSHMALLOW) {
            this.mFolderName.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            });
        }
        this.mFolderName.setOnEditorActionListener(this);
        this.mFolderName.setSelectAllOnFocus(true);
        this.mFolderName.setInputType((this.mFolderName.getInputType() & -32769 & -524289) | 8192);
        this.mFolderName.forceDisableSuggestions(true);
        this.mFooter = findViewById(R.id.folder_footer);
        this.mFooter.measure(0, 0);
        this.mFooterHeight = this.mFooter.getMeasuredHeight();
    }

    public boolean onLongClick(View v) {
        if (!this.mLauncher.isDraggingEnabled()) {
            return true;
        }
        return startDrag(v, new DragOptions());
    }

    public boolean startDrag(View v, DragOptions options) {
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            this.mEmptyCellRank = ((ShortcutInfo) tag).rank;
            this.mCurrentDragView = v;
            this.mDragController.addDragListener(this);
            if (options.isAccessibleDrag) {
                this.mDragController.addDragListener(new AccessibleDragListenerAdapter(this.mContent, 1) {
                    /* access modifiers changed from: protected */
                    public void enableAccessibleDrag(boolean enable) {
                        super.enableAccessibleDrag(enable);
                        Folder.this.mFooter.setImportantForAccessibility(enable ? 4 : 0);
                    }
                });
            }
            this.mLauncher.getWorkspace().beginDragShared(v, this, options);
        }
        return true;
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        Throwable th;
        if (dragObject.dragSource == this) {
            this.mContent.removeItem(this.mCurrentDragView);
            if (dragObject.dragInfo instanceof ShortcutInfo) {
                this.mItemsInvalidated = true;
                SuppressInfoChanges s = new SuppressInfoChanges();
                try {
                    this.mInfo.remove((ShortcutInfo) dragObject.dragInfo, true);
                    s.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            this.mDragInProgress = true;
            this.mItemAddedBackToSelfViaIcon = false;
            return;
        }
        return;
        throw th;
    }

    public void onDragEnd() {
        if (this.mIsExternalDrag && this.mDragInProgress) {
            completeDragExit();
        }
        this.mDragInProgress = false;
        this.mDragController.removeDragListener(this);
    }

    public boolean isEditingName() {
        return this.mIsEditingName;
    }

    public void startEditingFolderName() {
        post(new Runnable() {
            public void run() {
                Folder.this.mFolderName.setHint("");
                boolean unused = Folder.this.mIsEditingName = true;
            }
        });
    }

    public boolean onBackKey() {
        String newTitle = this.mFolderName.getText().toString();
        this.mInfo.setTitle(newTitle);
        this.mLauncher.getModelWriter().updateItemInDatabase(this.mInfo);
        this.mFolderName.setHint(sDefaultFolderName.contentEquals(newTitle) ? sHintText : null);
        AccessibilityManagerCompat.sendCustomAccessibilityEvent(this, 32, getContext().getString(R.string.folder_renamed, new Object[]{newTitle}));
        this.mFolderName.clearFocus();
        Selection.setSelection(this.mFolderName.getText(), 0, 0);
        this.mIsEditingName = false;
        return true;
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != 6) {
            return false;
        }
        this.mFolderName.dispatchBackKey();
        return true;
    }

    public FolderIcon getFolderIcon() {
        return this.mFolderIcon;
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    public void setFolderIcon(FolderIcon icon) {
        this.mFolderIcon = icon;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        requestFocus();
        super.onAttachedToWindow();
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return true;
    }

    public View focusSearch(int direction) {
        return FocusFinder.getInstance().findNextFocus(this, (View) null, direction);
    }

    public FolderInfo getInfo() {
        return this.mInfo;
    }

    /* access modifiers changed from: package-private */
    public void bind(FolderInfo info) {
        this.mInfo = info;
        ArrayList<ShortcutInfo> children = info.contents;
        Collections.sort(children, ITEM_POS_COMPARATOR);
        this.mContent.bindItems(children);
        if (((BaseDragLayer.LayoutParams) getLayoutParams()) == null) {
            BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(0, 0);
            lp.customPosition = true;
            setLayoutParams(lp);
        }
        centerAboutIcon();
        this.mItemsInvalidated = true;
        updateTextViewFocus();
        this.mInfo.addListener(this);
        if (!sDefaultFolderName.contentEquals(this.mInfo.title)) {
            this.mFolderName.setText(this.mInfo.title);
            this.mFolderName.setHint((CharSequence) null);
        } else {
            this.mFolderName.setText("");
            this.mFolderName.setHint(sHintText);
        }
        this.mFolderIcon.post(new Runnable() {
            public void run() {
                if (Folder.this.getItemCount() <= 1) {
                    Folder.this.replaceFolderWithFinalItem();
                }
            }
        });
    }

    @SuppressLint({"InflateParams"})
    static Folder fromXml(Launcher launcher) {
        return (Folder) launcher.getLayoutInflater().inflate(R.layout.user_folder_icon_normalized, (ViewGroup) null);
    }

    private void startAnimation(final AnimatorSet a) {
        if (this.mCurrentAnimator != null && this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.cancel();
        }
        a.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                Folder.this.mState = 1;
                AnimatorSet unused = Folder.this.mCurrentAnimator = a;
            }

            public void onAnimationEnd(Animator animation) {
                AnimatorSet unused = Folder.this.mCurrentAnimator = null;
            }
        });
        a.start();
    }

    public void animateOpen() {
        Folder openFolder = getOpen(this.mLauncher);
        if (!(openFolder == null || openFolder == this)) {
            openFolder.close(true);
        }
        this.mIsOpen = true;
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        if (getParent() == null) {
            dragLayer.addView(this);
            this.mDragController.addDropTarget(this);
        }
        this.mContent.completePendingPageChanges();
        if (!this.mDragInProgress) {
            this.mContent.snapToPageImmediately(0);
        }
        this.mDeleteFolderOnDropCompleted = false;
        centerAboutIcon();
        AnimatorSet anim = new FolderAnimationManager(this, true).getAnimator();
        anim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                Folder.this.mFolderIcon.setBackgroundVisible(false);
                Folder.this.mFolderIcon.drawLeaveBehindIfExists();
            }

            public void onAnimationEnd(Animator animation) {
                Folder.this.mState = 2;
                Folder.this.announceAccessibilityChanges();
                Folder.this.mLauncher.getUserEventDispatcher().resetElapsedContainerMillis("folder opened");
                Folder.this.mContent.setFocusOnFirstChild();
            }
        });
        if (this.mContent.getPageCount() <= 1 || this.mInfo.hasOption(4)) {
            this.mFolderName.setTranslationX(0.0f);
        } else {
            float translation = (((float) ((this.mContent.getDesiredWidth() - this.mFooter.getPaddingLeft()) - this.mFooter.getPaddingRight())) - this.mFolderName.getPaint().measureText(this.mFolderName.getText().toString())) / 2.0f;
            this.mFolderName.setTranslationX(this.mContent.mIsRtl ? -translation : translation);
            this.mPageIndicator.prepareEntryAnimation();
            final boolean updateAnimationFlag = true ^ this.mDragInProgress;
            anim.addListener(new AnimatorListenerAdapter() {
                @SuppressLint({"InlinedApi"})
                public void onAnimationEnd(Animator animation) {
                    Folder.this.mFolderName.animate().setDuration(633).translationX(0.0f).setInterpolator(AnimationUtils.loadInterpolator(Folder.this.mLauncher, AndroidResources.FAST_OUT_SLOW_IN));
                    Folder.this.mPageIndicator.playEntryAnimation();
                    if (updateAnimationFlag) {
                        Folder.this.mInfo.setOption(4, true, Folder.this.mLauncher.getModelWriter());
                    }
                }
            });
        }
        this.mPageIndicator.stopAllAnimations();
        startAnimation(anim);
        if (this.mDragController.isDragging()) {
            this.mDragController.forceTouchMove();
        }
        this.mContent.verifyVisibleHighResIcons(this.mContent.getNextPage());
    }

    public void beginExternalDrag() {
        this.mEmptyCellRank = this.mContent.allocateRankForNewItem();
        this.mIsExternalDrag = true;
        this.mDragInProgress = true;
        this.mDragController.addDragListener(this);
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 1) != 0;
    }

    /* access modifiers changed from: protected */
    public void handleClose(boolean animate) {
        this.mIsOpen = false;
        if (isEditingName()) {
            this.mFolderName.dispatchBackKey();
        }
        if (this.mFolderIcon != null) {
            this.mFolderIcon.clearLeaveBehindIfExists();
        }
        if (animate) {
            animateClosed();
        } else {
            closeComplete(false);
            post(new Runnable() {
                public final void run() {
                    Folder.this.announceAccessibilityChanges();
                }
            });
        }
        this.mLauncher.getDragLayer().sendAccessibilityEvent(32);
    }

    private void animateClosed() {
        AnimatorSet a = new FolderAnimationManager(this, false).getAnimator();
        a.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                Folder.this.closeComplete(true);
                Folder.this.announceAccessibilityChanges();
            }
        });
        startAnimation(a);
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        String str;
        FolderPagedView folderPagedView = this.mContent;
        if (this.mIsOpen) {
            str = this.mContent.getAccessibilityDescription();
        } else {
            str = getContext().getString(R.string.folder_closed);
        }
        return Pair.create(folderPagedView, str);
    }

    /* access modifiers changed from: private */
    public void closeComplete(boolean wasAnimated) {
        DragLayer parent = (DragLayer) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
        this.mDragController.removeDropTarget(this);
        clearFocus();
        if (this.mFolderIcon != null) {
            this.mFolderIcon.setVisibility(0);
            this.mFolderIcon.setBackgroundVisible(true);
            this.mFolderIcon.mFolderName.setTextVisibility(true);
            if (wasAnimated) {
                this.mFolderIcon.mBackground.fadeInBackgroundShadow();
                this.mFolderIcon.mBackground.animateBackgroundStroke();
                this.mFolderIcon.onFolderClose(this.mContent.getCurrentPage());
                if (this.mFolderIcon.hasBadge()) {
                    this.mFolderIcon.createBadgeScaleAnimator(0.0f, 1.0f).start();
                }
                this.mFolderIcon.requestFocus();
            }
        }
        if (this.mRearrangeOnClose) {
            rearrangeChildren();
            this.mRearrangeOnClose = false;
        }
        if (getItemCount() <= 1) {
            if (!this.mDragInProgress && !this.mSuppressFolderDeletion) {
                replaceFolderWithFinalItem();
            } else if (this.mDragInProgress) {
                this.mDeleteFolderOnDropCompleted = true;
            }
        }
        this.mSuppressFolderDeletion = false;
        clearDragInfo();
        this.mState = 0;
        this.mContent.setCurrentPage(0);
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        int itemType = d.dragInfo.itemType;
        return itemType == 0 || itemType == 1 || itemType == 6;
    }

    public void onDragEnter(DropTarget.DragObject d) {
        this.mPrevTargetRank = -1;
        this.mOnExitAlarm.cancelAlarm();
        this.mScrollAreaOffset = (d.dragView.getDragRegionWidth() / 2) - d.xOffset;
    }

    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    private int getTargetRank(DropTarget.DragObject d, float[] recycle) {
        float[] recycle2 = d.getVisualCenter(recycle);
        return this.mContent.findNearestArea(((int) recycle2[0]) - getPaddingLeft(), ((int) recycle2[1]) - getPaddingTop());
    }

    public void onDragOver(DropTarget.DragObject d) {
        if (!this.mScrollPauseAlarm.alarmPending()) {
            float[] r = new float[2];
            this.mTargetRank = getTargetRank(d, r);
            if (this.mTargetRank != this.mPrevTargetRank) {
                this.mReorderAlarm.cancelAlarm();
                this.mReorderAlarm.setOnAlarmListener(this.mReorderAlarmListener);
                this.mReorderAlarm.setAlarm(250);
                this.mPrevTargetRank = this.mTargetRank;
                if (d.stateAnnouncer != null) {
                    d.stateAnnouncer.announce(getContext().getString(R.string.move_to_position, new Object[]{Integer.valueOf(this.mTargetRank + 1)}));
                }
            }
            float x = r[0];
            int currentPage = this.mContent.getNextPage();
            float cellOverlap = ((float) this.mContent.getCurrentCellLayout().getCellWidth()) * ICON_OVERSCROLL_WIDTH_FACTOR;
            boolean isOutsideLeftEdge = x < cellOverlap;
            boolean isOutsideRightEdge = x > ((float) getWidth()) - cellOverlap;
            if (currentPage > 0 && (!this.mContent.mIsRtl ? isOutsideLeftEdge : isOutsideRightEdge)) {
                showScrollHint(0, d);
            } else if (currentPage >= this.mContent.getPageCount() - 1 || (!this.mContent.mIsRtl ? !isOutsideRightEdge : !isOutsideLeftEdge)) {
                this.mOnScrollHintAlarm.cancelAlarm();
                if (this.mScrollHintDir != -1) {
                    this.mContent.clearScrollHint();
                    this.mScrollHintDir = -1;
                }
            } else {
                showScrollHint(1, d);
            }
        }
    }

    private void showScrollHint(int direction, DropTarget.DragObject d) {
        if (this.mScrollHintDir != direction) {
            this.mContent.showScrollHint(direction);
            this.mScrollHintDir = direction;
        }
        if (!this.mOnScrollHintAlarm.alarmPending() || this.mCurrentScrollDir != direction) {
            this.mCurrentScrollDir = direction;
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mOnScrollHintAlarm.setOnAlarmListener(new OnScrollHintListener(d));
            this.mOnScrollHintAlarm.setAlarm(500);
            this.mReorderAlarm.cancelAlarm();
            this.mTargetRank = this.mEmptyCellRank;
        }
    }

    public void completeDragExit() {
        if (this.mIsOpen) {
            close(true);
            this.mRearrangeOnClose = true;
        } else if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
            clearDragInfo();
        }
    }

    private void clearDragInfo() {
        this.mCurrentDragView = null;
        this.mIsExternalDrag = false;
    }

    public void onDragExit(DropTarget.DragObject d) {
        if (!d.dragComplete) {
            this.mOnExitAlarm.setOnAlarmListener(this.mOnExitAlarmListener);
            this.mOnExitAlarm.setAlarm(400);
        }
        this.mReorderAlarm.cancelAlarm();
        this.mOnScrollHintAlarm.cancelAlarm();
        this.mScrollPauseAlarm.cancelAlarm();
        if (this.mScrollHintDir != -1) {
            this.mContent.clearScrollHint();
            this.mScrollHintDir = -1;
        }
    }

    public void prepareAccessibilityDrop() {
        if (this.mReorderAlarm.alarmPending()) {
            this.mReorderAlarm.cancelAlarm();
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x008c, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0090, code lost:
        if (r0 != null) goto L_0x0092;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0096, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0097, code lost:
        r0.addSuppressed(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x009b, code lost:
        r5.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDropCompleted(android.view.View r8, com.android.launcher3.DropTarget.DragObject r9, boolean r10) {
        /*
            r7 = this;
            r0 = 0
            r1 = 1
            if (r10 == 0) goto L_0x0012
            boolean r2 = r7.mDeleteFolderOnDropCompleted
            if (r2 == 0) goto L_0x004c
            boolean r2 = r7.mItemAddedBackToSelfViaIcon
            if (r2 != 0) goto L_0x004c
            if (r8 == r7) goto L_0x004c
            r7.replaceFolderWithFinalItem()
            goto L_0x004c
        L_0x0012:
            com.android.launcher3.ItemInfo r2 = r9.dragInfo
            com.android.launcher3.ShortcutInfo r2 = (com.android.launcher3.ShortcutInfo) r2
            android.view.View r3 = r7.mCurrentDragView
            if (r3 == 0) goto L_0x0025
            android.view.View r3 = r7.mCurrentDragView
            java.lang.Object r3 = r3.getTag()
            if (r3 != r2) goto L_0x0025
            android.view.View r3 = r7.mCurrentDragView
            goto L_0x002b
        L_0x0025:
            com.android.launcher3.folder.FolderPagedView r3 = r7.mContent
            android.view.View r3 = r3.createNewView(r2)
        L_0x002b:
            java.util.ArrayList r4 = r7.getItemsInReadingOrder()
            int r5 = r2.rank
            r4.add(r5, r3)
            com.android.launcher3.folder.FolderPagedView r5 = r7.mContent
            int r6 = r4.size()
            r5.arrangeChildren(r4, r6)
            r7.mItemsInvalidated = r1
            com.android.launcher3.folder.Folder$SuppressInfoChanges r5 = new com.android.launcher3.folder.Folder$SuppressInfoChanges
            r5.<init>()
            com.android.launcher3.folder.FolderIcon r6 = r7.mFolderIcon     // Catch:{ Throwable -> 0x008e }
            r6.onDrop(r9, r1)     // Catch:{ Throwable -> 0x008e }
            r5.close()
        L_0x004c:
            if (r8 == r7) goto L_0x0067
            com.android.launcher3.Alarm r2 = r7.mOnExitAlarm
            boolean r2 = r2.alarmPending()
            if (r2 == 0) goto L_0x0067
            com.android.launcher3.Alarm r2 = r7.mOnExitAlarm
            r2.cancelAlarm()
            if (r10 != 0) goto L_0x005f
            r7.mSuppressFolderDeletion = r1
        L_0x005f:
            com.android.launcher3.Alarm r1 = r7.mScrollPauseAlarm
            r1.cancelAlarm()
            r7.completeDragExit()
        L_0x0067:
            r1 = 0
            r7.mDeleteFolderOnDropCompleted = r1
            r7.mDragInProgress = r1
            r7.mItemAddedBackToSelfViaIcon = r1
            r7.mCurrentDragView = r0
            r7.updateItemLocationsInDatabaseBatch()
            int r0 = r7.getItemCount()
            com.android.launcher3.folder.FolderPagedView r2 = r7.mContent
            int r2 = r2.itemsPerPage()
            if (r0 > r2) goto L_0x008b
            com.android.launcher3.FolderInfo r0 = r7.mInfo
            r2 = 4
            com.android.launcher3.Launcher r3 = r7.mLauncher
            com.android.launcher3.model.ModelWriter r3 = r3.getModelWriter()
            r0.setOption(r2, r1, r3)
        L_0x008b:
            return
        L_0x008c:
            r1 = move-exception
            goto L_0x0090
        L_0x008e:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x008c }
        L_0x0090:
            if (r0 == 0) goto L_0x009b
            r5.close()     // Catch:{ Throwable -> 0x0096 }
            goto L_0x009e
        L_0x0096:
            r6 = move-exception
            r0.addSuppressed(r6)
            goto L_0x009e
        L_0x009b:
            r5.close()
        L_0x009e:
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.folder.Folder.onDropCompleted(android.view.View, com.android.launcher3.DropTarget$DragObject, boolean):void");
    }

    private void updateItemLocationsInDatabaseBatch() {
        ArrayList<View> list = getItemsInReadingOrder();
        ArrayList<ItemInfo> items = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ItemInfo info = (ItemInfo) list.get(i).getTag();
            info.rank = i;
            items.add(info);
        }
        this.mLauncher.getModelWriter().moveItemsInDatabase(items, this.mInfo.id, 0);
    }

    public void notifyDrop() {
        if (this.mDragInProgress) {
            this.mItemAddedBackToSelfViaIcon = true;
        }
    }

    public boolean isDropEnabled() {
        return this.mState != 1;
    }

    private void centerAboutIcon() {
        int top;
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        BaseDragLayer.LayoutParams lp = (BaseDragLayer.LayoutParams) getLayoutParams();
        DragLayer parent = (DragLayer) this.mLauncher.findViewById(R.id.drag_layer);
        int width = getFolderWidth();
        int height = getFolderHeight();
        parent.getDescendantRectRelativeToSelf(this.mFolderIcon, sTempRect);
        int centeredLeft = sTempRect.centerX() - (width / 2);
        int centeredTop = sTempRect.centerY() - (height / 2);
        if (this.mLauncher.getStateManager().getState().overviewUi) {
            this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this.mLauncher.getOverviewPanel(), sTempRect);
        } else {
            this.mLauncher.getWorkspace().getPageAreaRelativeToDragLayer(sTempRect);
        }
        int left = Math.min(Math.max(sTempRect.left, centeredLeft), sTempRect.right - width);
        int top2 = Math.min(Math.max(sTempRect.top, centeredTop), sTempRect.bottom - height);
        int distFromEdgeOfScreen = this.mLauncher.getWorkspace().getPaddingLeft() + getPaddingLeft();
        if (grid.isPhone && grid.availableWidthPx - width < distFromEdgeOfScreen * 4) {
            left = (grid.availableWidthPx - width) / 2;
        } else if (width >= sTempRect.width()) {
            left = sTempRect.left + ((sTempRect.width() - width) / 2);
        }
        if (height >= sTempRect.height()) {
            top = sTempRect.top + ((sTempRect.height() - height) / 2);
        } else {
            Rect folderBounds = grid.getAbsoluteOpenFolderBounds();
            left = Math.max(folderBounds.left, Math.min(left, folderBounds.right - width));
            top = Math.max(folderBounds.top, Math.min(top2, folderBounds.bottom - height));
        }
        int folderPivotX = (width / 2) + (centeredLeft - left);
        int folderPivotY = (height / 2) + (centeredTop - top);
        setPivotX((float) folderPivotX);
        setPivotY((float) folderPivotY);
        DeviceProfile deviceProfile = grid;
        DragLayer dragLayer = parent;
        this.mFolderIconPivotX = (float) ((int) (((float) this.mFolderIcon.getMeasuredWidth()) * ((((float) folderPivotX) * 1.0f) / ((float) width))));
        this.mFolderIconPivotY = (float) ((int) (((float) this.mFolderIcon.getMeasuredHeight()) * ((((float) folderPivotY) * 1.0f) / ((float) height))));
        lp.width = width;
        lp.height = height;
        lp.x = left;
        lp.y = top;
    }

    public float getPivotXForIconAnimation() {
        return this.mFolderIconPivotX;
    }

    public float getPivotYForIconAnimation() {
        return this.mFolderIconPivotY;
    }

    private int getContentAreaHeight() {
        DeviceProfile grid = this.mLauncher.getDeviceProfile();
        return Math.max(Math.min((grid.availableHeightPx - grid.getTotalWorkspacePadding().y) - this.mFooterHeight, this.mContent.getDesiredHeight()), 5);
    }

    private int getContentAreaWidth() {
        return Math.max(this.mContent.getDesiredWidth(), 5);
    }

    private int getFolderWidth() {
        return getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth();
    }

    private int getFolderHeight() {
        return getFolderHeight(getContentAreaHeight());
    }

    private int getFolderHeight(int contentAreaHeight) {
        return getPaddingTop() + getPaddingBottom() + contentAreaHeight + this.mFooterHeight;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int contentWidth = getContentAreaWidth();
        int contentHeight = getContentAreaHeight();
        int contentAreaWidthSpec = View.MeasureSpec.makeMeasureSpec(contentWidth, 1073741824);
        int contentAreaHeightSpec = View.MeasureSpec.makeMeasureSpec(contentHeight, 1073741824);
        this.mContent.setFixedSize(contentWidth, contentHeight);
        this.mContent.measure(contentAreaWidthSpec, contentAreaHeightSpec);
        if (this.mContent.getChildCount() > 0) {
            int cellIconGap = (this.mContent.getPageAt(0).getCellWidth() - this.mLauncher.getDeviceProfile().iconSizePx) / 2;
            this.mFooter.setPadding(this.mContent.getPaddingLeft() + cellIconGap, this.mFooter.getPaddingTop(), this.mContent.getPaddingRight() + cellIconGap, this.mFooter.getPaddingBottom());
        }
        this.mFooter.measure(contentAreaWidthSpec, View.MeasureSpec.makeMeasureSpec(this.mFooterHeight, 1073741824));
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentWidth, getFolderHeight(contentHeight));
    }

    public void rearrangeChildren() {
        rearrangeChildren(-1);
    }

    public void rearrangeChildren(int itemCount) {
        ArrayList<View> views = getItemsInReadingOrder();
        this.mContent.arrangeChildren(views, Math.max(itemCount, views.size()));
        this.mItemsInvalidated = true;
    }

    public int getItemCount() {
        return this.mContent.getItemCount();
    }

    /* access modifiers changed from: package-private */
    public void replaceFolderWithFinalItem() {
        Runnable onCompleteRunnable = new Runnable() {
            public void run() {
                int itemCount = Folder.this.mInfo.contents.size();
                if (itemCount <= 1) {
                    View newIcon = null;
                    if (itemCount == 1) {
                        CellLayout cellLayout = Folder.this.mLauncher.getCellLayout(Folder.this.mInfo.container, Folder.this.mInfo.screenId);
                        ShortcutInfo finalItem = Folder.this.mInfo.contents.remove(0);
                        newIcon = Folder.this.mLauncher.createShortcut(cellLayout, finalItem);
                        Folder.this.mLauncher.getModelWriter().addOrMoveItemInDatabase(finalItem, Folder.this.mInfo.container, Folder.this.mInfo.screenId, Folder.this.mInfo.cellX, Folder.this.mInfo.cellY);
                    }
                    Folder.this.mLauncher.removeItem(Folder.this.mFolderIcon, Folder.this.mInfo, true);
                    if (Folder.this.mFolderIcon instanceof DropTarget) {
                        Folder.this.mDragController.removeDropTarget((DropTarget) Folder.this.mFolderIcon);
                    }
                    if (newIcon != null) {
                        Folder.this.mLauncher.getWorkspace().addInScreenFromBind(newIcon, Folder.this.mInfo);
                        newIcon.requestFocus();
                    }
                }
            }
        };
        if (this.mContent.getLastItem() != null) {
            this.mFolderIcon.performDestroyAnimation(onCompleteRunnable);
        } else {
            onCompleteRunnable.run();
        }
        this.mDestroyed = true;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public void updateTextViewFocus() {
        View firstChild = this.mContent.getFirstItem();
        final View lastChild = this.mContent.getLastItem();
        if (firstChild != null && lastChild != null) {
            this.mFolderName.setNextFocusDownId(lastChild.getId());
            this.mFolderName.setNextFocusRightId(lastChild.getId());
            this.mFolderName.setNextFocusLeftId(lastChild.getId());
            this.mFolderName.setNextFocusUpId(lastChild.getId());
            this.mFolderName.setNextFocusForwardId(firstChild.getId());
            setNextFocusDownId(firstChild.getId());
            setNextFocusRightId(firstChild.getId());
            setNextFocusLeftId(firstChild.getId());
            setNextFocusUpId(firstChild.getId());
            setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    boolean isShiftPlusTab = true;
                    if (keyCode != 61 || !event.hasModifiers(1)) {
                        isShiftPlusTab = false;
                    }
                    if (!isShiftPlusTab || !Folder.this.isFocused()) {
                        return false;
                    }
                    return lastChild.requestFocus();
                }
            });
        }
    }

    public void onDrop(DropTarget.DragObject d, DragOptions options) {
        ShortcutInfo si;
        View currentDragView;
        Throwable th;
        Throwable th2;
        Throwable th3;
        DropTarget.DragObject dragObject = d;
        if (!this.mContent.rankOnCurrentPage(this.mEmptyCellRank)) {
            this.mTargetRank = getTargetRank(dragObject, (float[]) null);
            this.mReorderAlarmListener.onAlarm(this.mReorderAlarm);
            this.mOnScrollHintAlarm.cancelAlarm();
            this.mScrollPauseAlarm.cancelAlarm();
        }
        this.mContent.completePendingPageChanges();
        PendingAddShortcutInfo pasi = dragObject.dragInfo instanceof PendingAddShortcutInfo ? (PendingAddShortcutInfo) dragObject.dragInfo : null;
        ShortcutInfo pasiSi = pasi != null ? pasi.activityInfo.createShortcutInfo() : null;
        if (pasi == null || pasiSi != null) {
            if (pasiSi != null) {
                si = pasiSi;
            } else if (dragObject.dragInfo instanceof AppInfo) {
                si = ((AppInfo) dragObject.dragInfo).makeShortcut();
            } else {
                si = (ShortcutInfo) dragObject.dragInfo;
            }
            if (this.mIsExternalDrag) {
                View currentDragView2 = this.mContent.createAndAddViewForRank(si, this.mEmptyCellRank);
                this.mLauncher.getModelWriter().addOrMoveItemInDatabase(si, this.mInfo.id, 0, si.cellX, si.cellY);
                if (dragObject.dragSource != this) {
                    updateItemLocationsInDatabaseBatch();
                }
                this.mIsExternalDrag = false;
                currentDragView = currentDragView2;
            } else {
                currentDragView = this.mCurrentDragView;
                this.mContent.addViewForRank(currentDragView, si, this.mEmptyCellRank);
            }
            if (dragObject.dragView.hasDrawn()) {
                float scaleX = getScaleX();
                float scaleY = getScaleY();
                setScaleX(1.0f);
                setScaleY(1.0f);
                this.mLauncher.getDragLayer().animateViewIntoPosition(dragObject.dragView, currentDragView, (View) null);
                setScaleX(scaleX);
                setScaleY(scaleY);
            } else {
                dragObject.deferDragViewCleanupPostAnimation = false;
                currentDragView.setVisibility(0);
            }
            this.mItemsInvalidated = true;
            rearrangeChildren();
            SuppressInfoChanges s = new SuppressInfoChanges();
            try {
                this.mInfo.add(si, false);
                s.close();
            } catch (Throwable th4) {
                th2.addSuppressed(th4);
            }
        } else {
            pasi.container = this.mInfo.id;
            pasi.rank = this.mEmptyCellRank;
            this.mLauncher.addPendingItem(pasi, pasi.container, pasi.screenId, (int[]) null, pasi.spanX, pasi.spanY);
            dragObject.deferDragViewCleanupPostAnimation = false;
            this.mRearrangeOnClose = true;
        }
        this.mDragInProgress = false;
        if (this.mContent.getPageCount() > 1) {
            this.mInfo.setOption(4, true, this.mLauncher.getModelWriter());
        }
        this.mLauncher.getStateManager().goToState(LauncherState.NORMAL, 500);
        if (dragObject.stateAnnouncer != null) {
            dragObject.stateAnnouncer.completeAction(R.string.item_moved);
            return;
        }
        return;
        throw th3;
    }

    public void hideItem(ShortcutInfo info) {
        getViewForInfo(info).setVisibility(4);
    }

    public void showItem(ShortcutInfo info) {
        getViewForInfo(info).setVisibility(0);
    }

    public void onAdd(ShortcutInfo item, int rank) {
        View view = this.mContent.createAndAddViewForRank(item, rank);
        this.mLauncher.getModelWriter().addOrMoveItemInDatabase(item, this.mInfo.id, 0, item.cellX, item.cellY);
        ArrayList<View> items = new ArrayList<>(getItemsInReadingOrder());
        items.add(rank, view);
        this.mContent.arrangeChildren(items, items.size());
        this.mItemsInvalidated = true;
    }

    public void onRemove(ShortcutInfo item) {
        this.mItemsInvalidated = true;
        this.mContent.removeItem(getViewForInfo(item));
        if (this.mState == 1) {
            this.mRearrangeOnClose = true;
        } else {
            rearrangeChildren();
        }
        if (getItemCount() > 1) {
            return;
        }
        if (this.mIsOpen) {
            close(true);
        } else {
            replaceFolderWithFinalItem();
        }
    }

    private View getViewForInfo(final ShortcutInfo item) {
        return this.mContent.iterateOverItems(new Workspace.ItemOperator() {
            public boolean evaluate(ItemInfo info, View view) {
                return info == item;
            }
        });
    }

    public void onItemsChanged(boolean animate) {
        updateTextViewFocus();
    }

    public void prepareAutoUpdate() {
        close(false);
    }

    public void onTitleChanged(CharSequence title) {
    }

    public ArrayList<View> getItemsInReadingOrder() {
        if (this.mItemsInvalidated) {
            this.mItemsInReadingOrder.clear();
            this.mContent.iterateOverItems(new Workspace.ItemOperator() {
                public boolean evaluate(ItemInfo info, View view) {
                    Folder.this.mItemsInReadingOrder.add(view);
                    return false;
                }
            });
            this.mItemsInvalidated = false;
        }
        return this.mItemsInReadingOrder;
    }

    public List<BubbleTextView> getItemsOnPage(int page) {
        ArrayList<View> allItems = getItemsInReadingOrder();
        int lastPage = this.mContent.getPageCount() - 1;
        int totalItemsInFolder = allItems.size();
        int itemsPerPage = this.mContent.itemsPerPage();
        int numItemsOnCurrentPage = page == lastPage ? totalItemsInFolder - (itemsPerPage * page) : itemsPerPage;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + numItemsOnCurrentPage, allItems.size());
        List<BubbleTextView> itemsOnCurrentPage = new ArrayList<>(numItemsOnCurrentPage);
        for (int i = startIndex; i < endIndex; i++) {
            itemsOnCurrentPage.add((BubbleTextView) allItems.get(i));
        }
        return itemsOnCurrentPage;
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (v != this.mFolderName) {
            return;
        }
        if (hasFocus) {
            startEditingFolderName();
        } else {
            this.mFolderName.dispatchBackKey();
        }
    }

    public void getHitRectRelativeToDragLayer(Rect outRect) {
        getHitRect(outRect);
        outRect.left -= this.mScrollAreaOffset;
        outRect.right += this.mScrollAreaOffset;
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        target.gridX = info.cellX;
        target.gridY = info.cellY;
        target.pageIndex = this.mContent.getCurrentPage();
        targetParent.containerType = 3;
    }

    private class OnScrollHintListener implements OnAlarmListener {
        private final DropTarget.DragObject mDragObject;

        OnScrollHintListener(DropTarget.DragObject object) {
            this.mDragObject = object;
        }

        public void onAlarm(Alarm alarm) {
            if (Folder.this.mCurrentScrollDir == 0) {
                Folder.this.mContent.scrollLeft();
                Folder.this.mScrollHintDir = -1;
            } else if (Folder.this.mCurrentScrollDir == 1) {
                Folder.this.mContent.scrollRight();
                Folder.this.mScrollHintDir = -1;
            } else {
                return;
            }
            Folder.this.mCurrentScrollDir = -1;
            Folder.this.mScrollPauseAlarm.setOnAlarmListener(new OnScrollFinishedListener(this.mDragObject));
            Folder.this.mScrollPauseAlarm.setAlarm(900);
        }
    }

    private class OnScrollFinishedListener implements OnAlarmListener {
        private final DropTarget.DragObject mDragObject;

        OnScrollFinishedListener(DropTarget.DragObject object) {
            this.mDragObject = object;
        }

        public void onAlarm(Alarm alarm) {
            Folder.this.onDragOver(this.mDragObject);
        }
    }

    private class SuppressInfoChanges implements AutoCloseable {
        SuppressInfoChanges() {
            Folder.this.mInfo.removeListener(Folder.this);
        }

        public void close() {
            Folder.this.mInfo.addListener(Folder.this);
            Folder.this.updateTextViewFocus();
        }
    }

    public static Folder getOpen(Launcher launcher) {
        return (Folder) getOpenView(launcher, 1);
    }

    public void logActionCommand(int command) {
        this.mLauncher.getUserEventDispatcher().logActionCommand(command, (View) getFolderIcon(), 3);
    }

    public boolean onBackPressed() {
        if (isEditingName()) {
            this.mFolderName.dispatchBackKey();
            return true;
        }
        super.onBackPressed();
        return true;
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            DragLayer dl = this.mLauncher.getDragLayer();
            if (isEditingName()) {
                if (dl.isEventOverView(this.mFolderName, ev)) {
                    return false;
                }
                this.mFolderName.dispatchBackKey();
                return true;
            } else if (!dl.isEventOverView(this, ev)) {
                if (!this.mLauncher.getAccessibilityDelegate().isInAccessibleDrag()) {
                    this.mLauncher.getUserEventDispatcher().logActionTapOutside(LoggerUtils.newContainerTarget(3));
                    close(true);
                    return true;
                } else if (!dl.isEventOverView(this.mLauncher.getDropTargetBar(), ev)) {
                    return true;
                }
            }
        }
        return false;
    }
}
