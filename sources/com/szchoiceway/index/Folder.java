package com.szchoiceway.index;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.szchoiceway.index.CellLayout;
import com.szchoiceway.index.DragLayer;
import com.szchoiceway.index.DropTarget;
import com.szchoiceway.index.FolderInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class Folder extends LinearLayout implements DragSource, View.OnClickListener, View.OnLongClickListener, DropTarget, FolderInfo.FolderListener, TextView.OnEditorActionListener, View.OnFocusChangeListener {
    private static final int ON_EXIT_CLOSE_DELAY = 800;
    private static final int REORDER_ANIMATION_DURATION = 230;
    static final int STATE_ANIMATING = 1;
    static final int STATE_NONE = -1;
    static final int STATE_OPEN = 2;
    static final int STATE_SMALL = 0;
    private static final String TAG = "Launcher.Folder";
    private static String sDefaultFolderName;
    private static String sHintText;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
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
    };
    protected CellLayout mContent;
    private ShortcutInfo mCurrentDragInfo;
    private View mCurrentDragView;
    private boolean mDeleteFolderOnDropCompleted = false;
    private boolean mDestroyed;
    protected DragController mDragController;
    private boolean mDragInProgress = false;
    /* access modifiers changed from: private */
    public int[] mEmptyCell = new int[2];
    private int mExpandDuration;
    /* access modifiers changed from: private */
    public FolderIcon mFolderIcon;
    private float mFolderIconPivotX;
    private float mFolderIconPivotY;
    FolderEditText mFolderName;
    private int mFolderNameHeight;
    private final IconCache mIconCache;
    private Drawable mIconDrawable;
    private final LayoutInflater mInflater;
    protected FolderInfo mInfo;
    private InputMethodManager mInputMethodManager;
    private boolean mIsEditingName = false;
    private boolean mItemAddedBackToSelfViaIcon = false;
    private ArrayList<View> mItemsInReadingOrder = new ArrayList<>();
    boolean mItemsInvalidated = false;
    protected Launcher mLauncher;
    private int mMaxCountX;
    private int mMaxCountY;
    private int mMaxNumItems;
    private Alarm mOnExitAlarm = new Alarm();
    OnAlarmListener mOnExitAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.completeDragExit();
        }
    };
    private int[] mPreviousTargetCell = new int[2];
    private boolean mRearrangeOnClose = false;
    private Alarm mReorderAlarm = new Alarm();
    OnAlarmListener mReorderAlarmListener = new OnAlarmListener() {
        public void onAlarm(Alarm alarm) {
            Folder.this.realTimeReorder(Folder.this.mEmptyCell, Folder.this.mTargetCell);
        }
    };
    /* access modifiers changed from: private */
    public int mState = -1;
    private boolean mSuppressFolderDeletion = false;
    boolean mSuppressOnAdd = false;
    /* access modifiers changed from: private */
    public int[] mTargetCell = new int[2];
    private Rect mTempRect = new Rect();

    public Folder(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAlwaysDrawnWithCacheEnabled(false);
        this.mInflater = LayoutInflater.from(context);
        this.mIconCache = ((LauncherApplication) context.getApplicationContext()).getIconCache();
        Resources res = getResources();
        this.mMaxCountX = res.getInteger(R.integer.folder_max_count_x);
        this.mMaxCountY = res.getInteger(R.integer.folder_max_count_y);
        this.mMaxNumItems = res.getInteger(R.integer.folder_max_num_items);
        if (this.mMaxCountX < 0 || this.mMaxCountY < 0 || this.mMaxNumItems < 0) {
            this.mMaxCountX = LauncherModel.getCellCountX();
            this.mMaxCountY = LauncherModel.getCellCountY();
            this.mMaxNumItems = this.mMaxCountX * this.mMaxCountY;
        }
        this.mInputMethodManager = (InputMethodManager) getContext().getSystemService("input_method");
        this.mExpandDuration = res.getInteger(R.integer.config_folderAnimDuration);
        if (sDefaultFolderName == null) {
            sDefaultFolderName = res.getString(R.string.folder_name);
        }
        if (sHintText == null) {
            sHintText = res.getString(R.string.folder_hint_text);
        }
        this.mLauncher = (Launcher) context;
        setFocusableInTouchMode(true);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mContent = (CellLayout) findViewById(R.id.folder_content);
        this.mContent.setGridSize(0, 0);
        this.mContent.getShortcutsAndWidgets().setMotionEventSplittingEnabled(false);
        this.mContent.setInvertIfRtl(true);
        this.mFolderName = (FolderEditText) findViewById(R.id.folder_name);
        this.mFolderName.setFolder(this);
        this.mFolderName.setOnFocusChangeListener(this);
        this.mFolderName.measure(0, 0);
        this.mFolderNameHeight = this.mFolderName.getMeasuredHeight();
        this.mFolderName.setCustomSelectionActionModeCallback(this.mActionModeCallback);
        this.mFolderName.setOnEditorActionListener(this);
        this.mFolderName.setSelectAllOnFocus(true);
        this.mFolderName.setInputType(this.mFolderName.getInputType() | 524288 | 8192);
    }

    public void onClick(View v) {
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo item = (ShortcutInfo) tag;
            int[] pos = new int[2];
            v.getLocationOnScreen(pos);
            item.intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight()));
            this.mLauncher.startActivitySafely(v, item.intent, item);
        }
    }

    public boolean onLongClick(View v) {
        if (!this.mLauncher.isDraggingEnabled()) {
            return true;
        }
        Object tag = v.getTag();
        if (tag instanceof ShortcutInfo) {
            ShortcutInfo item = (ShortcutInfo) tag;
            if (!v.isInTouchMode()) {
                return false;
            }
            this.mLauncher.dismissFolderCling((View) null);
            this.mLauncher.getWorkspace().onDragStartedWithItem(v);
            this.mLauncher.getWorkspace().beginDragShared(v, this);
            this.mIconDrawable = ((TextView) v).getCompoundDrawables()[1];
            this.mCurrentDragInfo = item;
            this.mEmptyCell[0] = item.cellX;
            this.mEmptyCell[1] = item.cellY;
            this.mCurrentDragView = v;
            this.mContent.removeView(this.mCurrentDragView);
            this.mInfo.remove(this.mCurrentDragInfo);
            this.mDragInProgress = true;
            this.mItemAddedBackToSelfViaIcon = false;
        }
        return true;
    }

    public boolean isEditingName() {
        return this.mIsEditingName;
    }

    public void startEditingFolderName() {
        this.mFolderName.setHint("");
        this.mIsEditingName = true;
    }

    public void dismissEditingName() {
        this.mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
        doneEditingFolderName(true);
    }

    public void doneEditingFolderName(boolean commit) {
        this.mFolderName.setHint(sHintText);
        String newTitle = this.mFolderName.getText().toString();
        this.mInfo.setTitle(newTitle);
        LauncherModel.updateItemInDatabase(this.mLauncher, this.mInfo);
        if (commit) {
            sendCustomAccessibilityEvent(32, String.format(getContext().getString(R.string.folder_renamed), new Object[]{newTitle}));
        }
        requestFocus();
        Selection.setSelection(this.mFolderName.getText(), 0, 0);
        this.mIsEditingName = false;
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != 6) {
            return false;
        }
        dismissEditingName();
        return true;
    }

    public View getEditTextRegion() {
        return this.mFolderName;
    }

    public Drawable getDragDrawable() {
        return this.mIconDrawable;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        return true;
    }

    public void setDragController(DragController dragController) {
        this.mDragController = dragController;
    }

    /* access modifiers changed from: package-private */
    public void setFolderIcon(FolderIcon icon) {
        this.mFolderIcon = icon;
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return true;
    }

    /* access modifiers changed from: package-private */
    public FolderInfo getInfo() {
        return this.mInfo;
    }

    private class GridComparator implements Comparator<ShortcutInfo> {
        int mNumCols;

        public GridComparator(int numCols) {
            this.mNumCols = numCols;
        }

        public int compare(ShortcutInfo lhs, ShortcutInfo rhs) {
            return ((lhs.cellY * this.mNumCols) + lhs.cellX) - ((rhs.cellY * this.mNumCols) + rhs.cellX);
        }
    }

    private void placeInReadingOrder(ArrayList<ShortcutInfo> items) {
        int maxX = 0;
        int count = items.size();
        for (int i = 0; i < count; i++) {
            ShortcutInfo item = items.get(i);
            if (item.cellX > maxX) {
                maxX = item.cellX;
            }
        }
        Collections.sort(items, new GridComparator(maxX + 1));
        int countX = this.mContent.getCountX();
        for (int i2 = 0; i2 < count; i2++) {
            ShortcutInfo item2 = items.get(i2);
            item2.cellX = i2 % countX;
            item2.cellY = i2 / countX;
        }
    }

    /* access modifiers changed from: package-private */
    public void bind(FolderInfo info) {
        this.mInfo = info;
        ArrayList<ShortcutInfo> children = info.contents;
        ArrayList<ShortcutInfo> overflow = new ArrayList<>();
        setupContentForNumItems(children.size());
        placeInReadingOrder(children);
        int count = 0;
        for (int i = 0; i < children.size(); i++) {
            ShortcutInfo child = children.get(i);
            if (!createAndAddShortcut(child)) {
                overflow.add(child);
            } else {
                count++;
            }
        }
        setupContentForNumItems(count);
        Iterator<ShortcutInfo> it = overflow.iterator();
        while (it.hasNext()) {
            ShortcutInfo item = it.next();
            this.mInfo.remove(item);
            LauncherModel.deleteItemFromDatabase(this.mLauncher, item);
        }
        this.mItemsInvalidated = true;
        updateTextViewFocus();
        this.mInfo.addListener(this);
        if (!sDefaultFolderName.contentEquals(this.mInfo.title)) {
            this.mFolderName.setText(this.mInfo.title);
        } else {
            this.mFolderName.setText("");
        }
        updateItemLocationsInDatabase();
    }

    static Folder fromXml(Context context) {
        return (Folder) LayoutInflater.from(context).inflate(R.layout.user_folder, (ViewGroup) null);
    }

    private void positionAndSizeAsIcon() {
        if (getParent() instanceof DragLayer) {
            setScaleX(0.8f);
            setScaleY(0.8f);
            setAlpha(0.0f);
            this.mState = 0;
        }
    }

    public void animateOpen() {
        positionAndSizeAsIcon();
        if (getParent() instanceof DragLayer) {
            centerAboutIcon();
            ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("alpha", new float[]{1.0f}), PropertyValuesHolder.ofFloat("scaleX", new float[]{1.0f}), PropertyValuesHolder.ofFloat("scaleY", new float[]{1.0f}));
            oa.addListener(new AnimatorListenerAdapter() {
                public void onAnimationStart(Animator animation) {
                    Folder.this.sendCustomAccessibilityEvent(32, String.format(Folder.this.getContext().getString(R.string.folder_opened), new Object[]{Integer.valueOf(Folder.this.mContent.getCountX()), Integer.valueOf(Folder.this.mContent.getCountY())}));
                    int unused = Folder.this.mState = 1;
                }

                public void onAnimationEnd(Animator animation) {
                    int unused = Folder.this.mState = 2;
                    Folder.this.setLayerType(0, (Paint) null);
                    Cling cling = Folder.this.mLauncher.showFirstRunFoldersCling();
                    if (cling != null) {
                        cling.bringToFront();
                    }
                    Folder.this.setFocusOnFirstChild();
                }
            });
            oa.setDuration((long) this.mExpandDuration);
            setLayerType(2, (Paint) null);
            oa.start();
        }
    }

    /* access modifiers changed from: private */
    public void sendCustomAccessibilityEvent(int type, String text) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent event = AccessibilityEvent.obtain(type);
            onInitializeAccessibilityEvent(event);
            event.getText().add(text);
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

    /* access modifiers changed from: private */
    public void setFocusOnFirstChild() {
        View firstChild = this.mContent.getChildAt(0, 0);
        if (firstChild != null) {
            firstChild.requestFocus();
        }
    }

    public void animateClosed() {
        if (getParent() instanceof DragLayer) {
            ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat("alpha", new float[]{0.0f}), PropertyValuesHolder.ofFloat("scaleX", new float[]{0.9f}), PropertyValuesHolder.ofFloat("scaleY", new float[]{0.9f}));
            oa.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Folder.this.onCloseComplete();
                    Folder.this.setLayerType(0, (Paint) null);
                    int unused = Folder.this.mState = 0;
                }

                public void onAnimationStart(Animator animation) {
                    Folder.this.sendCustomAccessibilityEvent(32, Folder.this.getContext().getString(R.string.folder_closed));
                    int unused = Folder.this.mState = 1;
                }
            });
            oa.setDuration((long) this.mExpandDuration);
            setLayerType(2, (Paint) null);
            oa.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyDataSetChanged() {
        this.mContent.removeAllViewsInLayout();
        bind(this.mInfo);
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        int itemType = ((ItemInfo) d.dragInfo).itemType;
        if ((itemType == 0 || itemType == 1) && !isFull()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean findAndSetEmptyCells(ShortcutInfo item) {
        int[] emptyCell = new int[2];
        if (!this.mContent.findCellForSpan(emptyCell, item.spanX, item.spanY)) {
            return false;
        }
        item.cellX = emptyCell[0];
        item.cellY = emptyCell[1];
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean createAndAddShortcut(ShortcutInfo item) {
        int i = 0;
        TextView textView = (TextView) this.mInflater.inflate(R.layout.application, this, false);
        textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, new FastBitmapDrawable(item.getIcon(this.mIconCache)), (Drawable) null, (Drawable) null);
        textView.setText(item.title);
        textView.setTag(item);
        textView.setOnClickListener(this);
        textView.setOnLongClickListener(this);
        if (this.mContent.getChildAt(item.cellX, item.cellY) != null || item.cellX < 0 || item.cellY < 0 || item.cellX >= this.mContent.getCountX() || item.cellY >= this.mContent.getCountY()) {
            Log.e(TAG, "Folder order not properly persisted during bind");
            if (!findAndSetEmptyCells(item)) {
                return false;
            }
        }
        CellLayout.LayoutParams lp = new CellLayout.LayoutParams(item.cellX, item.cellY, item.spanX, item.spanY);
        textView.setOnKeyListener(new FolderKeyEventListener());
        CellLayout cellLayout = this.mContent;
        if (0 == 0) {
            i = -1;
        }
        cellLayout.addViewToCellLayout(textView, i, (int) item.id, lp, true);
        return true;
    }

    public void onDragEnter(DropTarget.DragObject d) {
        this.mPreviousTargetCell[0] = -1;
        this.mPreviousTargetCell[1] = -1;
        this.mOnExitAlarm.cancelAlarm();
    }

    /* access modifiers changed from: package-private */
    public boolean readingOrderGreaterThan(int[] v1, int[] v2) {
        return v1[1] > v2[1] || (v1[1] == v2[1] && v1[0] > v2[0]);
    }

    /* access modifiers changed from: private */
    public void realTimeReorder(int[] empty, int[] target) {
        int delay = 0;
        float delayAmount = 30.0f;
        if (readingOrderGreaterThan(target, empty)) {
            int y = empty[0] >= this.mContent.getCountX() + -1 ? empty[1] + 1 : empty[1];
            while (y <= target[1]) {
                int startX = y == empty[1] ? empty[0] + 1 : 0;
                int endX = y < target[1] ? this.mContent.getCountX() - 1 : target[0];
                for (int x = startX; x <= endX; x++) {
                    if (this.mContent.animateChildToPosition(this.mContent.getChildAt(x, y), empty[0], empty[1], REORDER_ANIMATION_DURATION, delay, true, true)) {
                        empty[0] = x;
                        empty[1] = y;
                        delay = (int) (((float) delay) + delayAmount);
                        delayAmount = (float) (((double) delayAmount) * 0.9d);
                    }
                }
                y++;
            }
            return;
        }
        int y2 = empty[0] == 0 ? empty[1] - 1 : empty[1];
        while (y2 >= target[1]) {
            int startX2 = y2 == empty[1] ? empty[0] - 1 : this.mContent.getCountX() - 1;
            int endX2 = y2 > target[1] ? 0 : target[0];
            for (int x2 = startX2; x2 >= endX2; x2--) {
                if (this.mContent.animateChildToPosition(this.mContent.getChildAt(x2, y2), empty[0], empty[1], REORDER_ANIMATION_DURATION, delay, true, true)) {
                    empty[0] = x2;
                    empty[1] = y2;
                    delay = (int) (((float) delay) + delayAmount);
                    delayAmount = (float) (((double) delayAmount) * 0.9d);
                }
            }
            y2--;
        }
    }

    public boolean isLayoutRtl() {
        return getLayoutDirection() == 1;
    }

    public void onDragOver(DropTarget.DragObject d) {
        float[] r = getDragViewVisualCenter(d.x, d.y, d.xOffset, d.yOffset, d.dragView, (float[]) null);
        this.mTargetCell = this.mContent.findNearestArea((int) r[0], (int) r[1], 1, 1, this.mTargetCell);
        if (isLayoutRtl()) {
            this.mTargetCell[0] = (this.mContent.getCountX() - this.mTargetCell[0]) - 1;
        }
        if (this.mTargetCell[0] != this.mPreviousTargetCell[0] || this.mTargetCell[1] != this.mPreviousTargetCell[1]) {
            this.mReorderAlarm.cancelAlarm();
            this.mReorderAlarm.setOnAlarmListener(this.mReorderAlarmListener);
            this.mReorderAlarm.setAlarm(150);
            this.mPreviousTargetCell[0] = this.mTargetCell[0];
            this.mPreviousTargetCell[1] = this.mTargetCell[1];
        }
    }

    private float[] getDragViewVisualCenter(int x, int y, int xOffset, int yOffset, DragView dragView, float[] recycle) {
        float[] res;
        if (recycle == null) {
            res = new float[2];
        } else {
            res = recycle;
        }
        res[0] = (float) ((dragView.getDragRegion().width() / 2) + (x - xOffset));
        res[1] = (float) ((dragView.getDragRegion().height() / 2) + (y - yOffset));
        return res;
    }

    public void completeDragExit() {
        this.mLauncher.closeFolder();
        this.mCurrentDragInfo = null;
        this.mCurrentDragView = null;
        this.mSuppressOnAdd = false;
        this.mRearrangeOnClose = true;
    }

    public void onDragExit(DropTarget.DragObject d) {
        if (!d.dragComplete) {
            this.mOnExitAlarm.setOnAlarmListener(this.mOnExitAlarmListener);
            this.mOnExitAlarm.setAlarm(800);
        }
        this.mReorderAlarm.cancelAlarm();
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean isFlingToDelete, boolean success) {
        if (!success) {
            setupContentForNumItems(getItemCount());
            this.mFolderIcon.onDrop(d);
        } else if (this.mDeleteFolderOnDropCompleted && !this.mItemAddedBackToSelfViaIcon) {
            replaceFolderWithFinalItem();
        }
        if (target != this && this.mOnExitAlarm.alarmPending()) {
            this.mOnExitAlarm.cancelAlarm();
            if (!success) {
                this.mSuppressFolderDeletion = true;
            }
            completeDragExit();
        }
        this.mDeleteFolderOnDropCompleted = false;
        this.mDragInProgress = false;
        this.mItemAddedBackToSelfViaIcon = false;
        this.mCurrentDragInfo = null;
        this.mCurrentDragView = null;
        this.mSuppressOnAdd = false;
        updateItemLocationsInDatabase();
    }

    public boolean supportsFlingToDelete() {
        return true;
    }

    public void onFlingToDelete(DropTarget.DragObject d, int x, int y, PointF vec) {
    }

    public void onFlingToDeleteCompleted() {
    }

    private void updateItemLocationsInDatabase() {
        ArrayList<View> list = getItemsInReadingOrder();
        for (int i = 0; i < list.size(); i++) {
            ItemInfo info = (ItemInfo) list.get(i).getTag();
            LauncherModel.moveItemInDatabase(this.mLauncher, info, this.mInfo.id, 0, info.cellX, info.cellY);
        }
    }

    public void notifyDrop() {
        if (this.mDragInProgress) {
            this.mItemAddedBackToSelfViaIcon = true;
        }
    }

    public boolean isDropEnabled() {
        return true;
    }

    public DropTarget getDropTargetDelegate(DropTarget.DragObject d) {
        return null;
    }

    private void setupContentDimensions(int count) {
        ArrayList<View> list = getItemsInReadingOrder();
        int countX = this.mContent.getCountX();
        int countY = this.mContent.getCountY();
        boolean done = false;
        while (!done) {
            int oldCountX = countX;
            int oldCountY = countY;
            if (countX * countY < count) {
                if ((countX <= countY || countY == this.mMaxCountY) && countX < this.mMaxCountX) {
                    countX++;
                } else if (countY < this.mMaxCountY) {
                    countY++;
                }
                if (countY == 0) {
                    countY++;
                }
            } else if ((countY - 1) * countX >= count && countY >= countX) {
                countY = Math.max(0, countY - 1);
            } else if ((countX - 1) * countY >= count) {
                countX = Math.max(0, countX - 1);
            }
            if (countX == oldCountX && countY == oldCountY) {
                done = true;
            } else {
                done = false;
            }
        }
        this.mContent.setGridSize(countX, countY);
        arrangeChildren(list);
    }

    public boolean isFull() {
        return getItemCount() >= this.mMaxNumItems;
    }

    private void centerAboutIcon() {
        DragLayer.LayoutParams lp = (DragLayer.LayoutParams) getLayoutParams();
        int width = getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth();
        int height = getPaddingTop() + getPaddingBottom() + this.mContent.getDesiredHeight() + this.mFolderNameHeight;
        DragLayer parent = (DragLayer) this.mLauncher.findViewById(R.id.drag_layer);
        float scale = parent.getDescendantRectRelativeToSelf(this.mFolderIcon, this.mTempRect);
        int centeredLeft = ((int) (((float) this.mTempRect.left) + ((((float) this.mTempRect.width()) * scale) / 2.0f))) - (width / 2);
        int centeredTop = ((int) (((float) this.mTempRect.top) + ((((float) this.mTempRect.height()) * scale) / 2.0f))) - (height / 2);
        int currentPage = this.mLauncher.getWorkspace().getCurrentPage();
        this.mLauncher.getWorkspace().setFinalScrollForPageChange(currentPage);
        ShortcutAndWidgetContainer boundingLayout = ((CellLayout) this.mLauncher.getWorkspace().getChildAt(currentPage)).getShortcutsAndWidgets();
        Rect bounds = new Rect();
        parent.getDescendantRectRelativeToSelf(boundingLayout, bounds);
        this.mLauncher.getWorkspace().resetFinalScrollForPageChange(currentPage);
        int left = Math.min(Math.max(bounds.left, centeredLeft), (bounds.left + bounds.width()) - width);
        int top = Math.min(Math.max(bounds.top, centeredTop), (bounds.top + bounds.height()) - height);
        if (width >= bounds.width()) {
            left = bounds.left + ((bounds.width() - width) / 2);
        }
        if (height >= bounds.height()) {
            top = bounds.top + ((bounds.height() - height) / 2);
        }
        int folderPivotX = (width / 2) + (centeredLeft - left);
        int folderPivotY = (height / 2) + (centeredTop - top);
        setPivotX((float) folderPivotX);
        setPivotY((float) folderPivotY);
        this.mFolderIconPivotX = (float) ((int) (((float) this.mFolderIcon.getMeasuredWidth()) * ((1.0f * ((float) folderPivotX)) / ((float) width))));
        this.mFolderIconPivotY = (float) ((int) (((float) this.mFolderIcon.getMeasuredHeight()) * ((1.0f * ((float) folderPivotY)) / ((float) height))));
        lp.width = width;
        lp.height = height;
        lp.x = left;
        lp.y = top;
    }

    /* access modifiers changed from: package-private */
    public float getPivotXForIconAnimation() {
        return this.mFolderIconPivotX;
    }

    /* access modifiers changed from: package-private */
    public float getPivotYForIconAnimation() {
        return this.mFolderIconPivotY;
    }

    private void setupContentForNumItems(int count) {
        setupContentDimensions(count);
        if (((DragLayer.LayoutParams) getLayoutParams()) == null) {
            DragLayer.LayoutParams lp = new DragLayer.LayoutParams(0, 0);
            lp.customPosition = true;
            setLayoutParams(lp);
        }
        centerAboutIcon();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getPaddingLeft() + getPaddingRight() + this.mContent.getDesiredWidth();
        int height = getPaddingTop() + getPaddingBottom() + this.mContent.getDesiredHeight() + this.mFolderNameHeight;
        int contentWidthSpec = View.MeasureSpec.makeMeasureSpec(this.mContent.getDesiredWidth(), 1073741824);
        this.mContent.measure(contentWidthSpec, View.MeasureSpec.makeMeasureSpec(this.mContent.getDesiredHeight(), 1073741824));
        this.mFolderName.measure(contentWidthSpec, View.MeasureSpec.makeMeasureSpec(this.mFolderNameHeight, 1073741824));
        setMeasuredDimension(width, height);
    }

    private void arrangeChildren(ArrayList<View> list) {
        int i;
        int[] vacant = new int[2];
        if (list == null) {
            list = getItemsInReadingOrder();
        }
        this.mContent.removeAllViews();
        for (int i2 = 0; i2 < list.size(); i2++) {
            View v = list.get(i2);
            this.mContent.getVacantCell(vacant, 1, 1);
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) v.getLayoutParams();
            lp.cellX = vacant[0];
            lp.cellY = vacant[1];
            ItemInfo info = (ItemInfo) v.getTag();
            if (!(info.cellX == vacant[0] && info.cellY == vacant[1])) {
                info.cellX = vacant[0];
                info.cellY = vacant[1];
                LauncherModel.addOrMoveItemInDatabase(this.mLauncher, info, this.mInfo.id, 0, info.cellX, info.cellY);
            }
            CellLayout cellLayout = this.mContent;
            if (0 != 0) {
                i = 0;
            } else {
                i = -1;
            }
            cellLayout.addViewToCellLayout(v, i, (int) info.id, lp, true);
        }
        this.mItemsInvalidated = true;
    }

    public int getItemCount() {
        return this.mContent.getShortcutsAndWidgets().getChildCount();
    }

    public View getItemAt(int index) {
        return this.mContent.getShortcutsAndWidgets().getChildAt(index);
    }

    /* access modifiers changed from: private */
    public void onCloseComplete() {
        DragLayer parent = (DragLayer) getParent();
        if (parent != null) {
            parent.removeView(this);
        }
        this.mDragController.removeDropTarget(this);
        clearFocus();
        this.mFolderIcon.requestFocus();
        if (this.mRearrangeOnClose) {
            setupContentForNumItems(getItemCount());
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
    }

    private void replaceFolderWithFinalItem() {
        Runnable onCompleteRunnable = new Runnable() {
            public void run() {
                CellLayout cellLayout = Folder.this.mLauncher.getCellLayout(Folder.this.mInfo.container, Folder.this.mInfo.screen);
                View child = null;
                if (Folder.this.getItemCount() == 1) {
                    ShortcutInfo finalItem = Folder.this.mInfo.contents.get(0);
                    View child2 = Folder.this.mLauncher.createShortcut(R.layout.application, cellLayout, finalItem);
                    LauncherModel.addOrMoveItemInDatabase(Folder.this.mLauncher, finalItem, Folder.this.mInfo.container, Folder.this.mInfo.screen, Folder.this.mInfo.cellX, Folder.this.mInfo.cellY);
                    child = child2;
                }
                if (Folder.this.getItemCount() <= 1) {
                    LauncherModel.deleteItemFromDatabase(Folder.this.mLauncher, Folder.this.mInfo);
                    cellLayout.removeView(Folder.this.mFolderIcon);
                    if (Folder.this.mFolderIcon instanceof DropTarget) {
                        Folder.this.mDragController.removeDropTarget((DropTarget) Folder.this.mFolderIcon);
                    }
                    Folder.this.mLauncher.removeFolder(Folder.this.mInfo);
                }
                if (child != null) {
                    Folder.this.mLauncher.getWorkspace().addInScreen(child, Folder.this.mInfo.container, Folder.this.mInfo.screen, Folder.this.mInfo.cellX, Folder.this.mInfo.cellY, Folder.this.mInfo.spanX, Folder.this.mInfo.spanY);
                }
            }
        };
        View finalChild = getItemAt(0);
        if (finalChild != null) {
            this.mFolderIcon.performDestroyAnimation(finalChild, onCompleteRunnable);
        }
        this.mDestroyed = true;
    }

    /* access modifiers changed from: package-private */
    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    private void updateTextViewFocus() {
        View lastChild = getItemAt(getItemCount() - 1);
        getItemAt(getItemCount() - 1);
        if (lastChild != null) {
            this.mFolderName.setNextFocusDownId(lastChild.getId());
            this.mFolderName.setNextFocusRightId(lastChild.getId());
            this.mFolderName.setNextFocusLeftId(lastChild.getId());
            this.mFolderName.setNextFocusUpId(lastChild.getId());
        }
    }

    public void onDrop(DropTarget.DragObject d) {
        ShortcutInfo item;
        if (d.dragInfo instanceof ApplicationInfo) {
            item = ((ApplicationInfo) d.dragInfo).makeShortcut();
            item.spanX = 1;
            item.spanY = 1;
        } else {
            item = (ShortcutInfo) d.dragInfo;
        }
        if (item == this.mCurrentDragInfo) {
            ShortcutInfo si = (ShortcutInfo) this.mCurrentDragView.getTag();
            CellLayout.LayoutParams lp = (CellLayout.LayoutParams) this.mCurrentDragView.getLayoutParams();
            int i = this.mEmptyCell[0];
            lp.cellX = i;
            si.cellX = i;
            int i2 = this.mEmptyCell[1];
            lp.cellY = i2;
            si.cellX = i2;
            this.mContent.addViewToCellLayout(this.mCurrentDragView, -1, (int) item.id, lp, true);
            if (d.dragView.hasDrawn()) {
                this.mLauncher.getDragLayer().animateViewIntoPosition(d.dragView, this.mCurrentDragView);
            } else {
                d.deferDragViewCleanupPostAnimation = false;
                this.mCurrentDragView.setVisibility(0);
            }
            this.mItemsInvalidated = true;
            setupContentDimensions(getItemCount());
            this.mSuppressOnAdd = true;
        }
        this.mInfo.add(item);
    }

    public void hideItem(ShortcutInfo info) {
        getViewForInfo(info).setVisibility(4);
    }

    public void showItem(ShortcutInfo info) {
        getViewForInfo(info).setVisibility(0);
    }

    public void onAdd(ShortcutInfo item) {
        this.mItemsInvalidated = true;
        if (!this.mSuppressOnAdd) {
            if (!findAndSetEmptyCells(item)) {
                setupContentForNumItems(getItemCount() + 1);
                findAndSetEmptyCells(item);
            }
            createAndAddShortcut(item);
            LauncherModel.addOrMoveItemInDatabase(this.mLauncher, item, this.mInfo.id, 0, item.cellX, item.cellY);
        }
    }

    public void onRemove(ShortcutInfo item) {
        this.mItemsInvalidated = true;
        if (item != this.mCurrentDragInfo) {
            this.mContent.removeView(getViewForInfo(item));
            if (this.mState == 1) {
                this.mRearrangeOnClose = true;
            } else {
                setupContentForNumItems(getItemCount());
            }
            if (getItemCount() <= 1) {
                replaceFolderWithFinalItem();
            }
        }
    }

    private View getViewForInfo(ShortcutInfo item) {
        for (int j = 0; j < this.mContent.getCountY(); j++) {
            for (int i = 0; i < this.mContent.getCountX(); i++) {
                View v = this.mContent.getChildAt(i, j);
                if (v.getTag() == item) {
                    return v;
                }
            }
        }
        return null;
    }

    public void onItemsChanged() {
        updateTextViewFocus();
    }

    public void onTitleChanged(CharSequence title) {
    }

    public ArrayList<View> getItemsInReadingOrder() {
        if (this.mItemsInvalidated) {
            this.mItemsInReadingOrder.clear();
            for (int j = 0; j < this.mContent.getCountY(); j++) {
                for (int i = 0; i < this.mContent.getCountX(); i++) {
                    View v = this.mContent.getChildAt(i, j);
                    if (v != null) {
                        this.mItemsInReadingOrder.add(v);
                    }
                }
            }
            this.mItemsInvalidated = false;
        }
        return this.mItemsInReadingOrder;
    }

    public void getLocationInDragLayer(int[] loc) {
        this.mLauncher.getDragLayer().getLocationInDragLayer(this, loc);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (v == this.mFolderName && hasFocus) {
            startEditingFolderName();
        }
    }
}
