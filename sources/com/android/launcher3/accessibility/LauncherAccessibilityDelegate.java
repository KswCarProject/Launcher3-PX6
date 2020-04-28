package com.android.launcher3.accessibility;

import android.app.AlertDialog;
import android.appwidget.AppWidgetProviderInfo;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.AppInfo;
import com.android.launcher3.AppWidgetResizeFrame;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DropTarget;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherState;
import com.android.launcher3.LauncherStateManager;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Workspace;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import java.util.ArrayList;

public class LauncherAccessibilityDelegate extends View.AccessibilityDelegate implements DragController.DragListener {
    protected static final int ADD_TO_WORKSPACE = 2131427421;
    public static final int DEEP_SHORTCUTS = 2131427431;
    protected static final int MOVE = 2131427440;
    protected static final int MOVE_TO_WORKSPACE = 2131427443;
    public static final int RECONFIGURE = 2131427444;
    public static final int REMOVE = 2131427445;
    protected static final int RESIZE = 2131427446;
    public static final int SHORTCUTS_AND_NOTIFICATIONS = 2131427447;
    private static final String TAG = "LauncherAccessibilityDelegate";
    public static final int UNINSTALL = 2131427449;
    protected final SparseArray<AccessibilityNodeInfo.AccessibilityAction> mActions = new SparseArray<>();
    private DragInfo mDragInfo = null;
    final Launcher mLauncher;

    public static class DragInfo {
        public DragType dragType;
        public ItemInfo info;
        public View item;
    }

    public enum DragType {
        ICON,
        FOLDER,
        WIDGET
    }

    public LauncherAccessibilityDelegate(Launcher launcher) {
        this.mLauncher = launcher;
        this.mActions.put(R.id.action_remove, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_remove, launcher.getText(R.string.remove_drop_target_label)));
        this.mActions.put(R.id.action_uninstall, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_uninstall, launcher.getText(R.string.uninstall_drop_target_label)));
        this.mActions.put(R.id.action_reconfigure, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_reconfigure, launcher.getText(R.string.gadget_setup_text)));
        this.mActions.put(R.id.action_add_to_workspace, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_add_to_workspace, launcher.getText(R.string.action_add_to_workspace)));
        this.mActions.put(R.id.action_move, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move, launcher.getText(R.string.action_move)));
        this.mActions.put(R.id.action_move_to_workspace, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_move_to_workspace, launcher.getText(R.string.action_move_to_workspace)));
        this.mActions.put(R.id.action_resize, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_resize, launcher.getText(R.string.action_resize)));
        this.mActions.put(R.id.action_deep_shortcuts, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_deep_shortcuts, launcher.getText(R.string.action_deep_shortcut)));
        this.mActions.put(R.id.action_shortcuts_and_notifications, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_deep_shortcuts, launcher.getText(R.string.shortcuts_menu_with_notifications_description)));
    }

    public void addAccessibilityAction(int action, int actionLabel) {
        this.mActions.put(action, new AccessibilityNodeInfo.AccessibilityAction(action, this.mLauncher.getText(actionLabel)));
    }

    public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(host, info);
        addSupportedActions(host, info, false);
    }

    public void addSupportedActions(View host, AccessibilityNodeInfo info, boolean fromKeyboard) {
        if (host.getTag() instanceof ItemInfo) {
            ItemInfo item = (ItemInfo) host.getTag();
            if (!fromKeyboard && DeepShortcutManager.supportsShortcuts(item)) {
                info.addAction(this.mActions.get(NotificationListener.getInstanceIfConnected() != null ? R.id.action_shortcuts_and_notifications : R.id.action_deep_shortcuts));
            }
            for (ButtonDropTarget target : this.mLauncher.getDropTargetBar().getDropTargets()) {
                if (target.supportsAccessibilityDrop(item, host)) {
                    info.addAction(this.mActions.get(target.getAccessibilityAction()));
                }
            }
            if (!fromKeyboard && ((item instanceof ShortcutInfo) || (item instanceof LauncherAppWidgetInfo) || (item instanceof FolderInfo))) {
                info.addAction(this.mActions.get(R.id.action_move));
                if (item.container >= 0) {
                    info.addAction(this.mActions.get(R.id.action_move_to_workspace));
                } else if ((item instanceof LauncherAppWidgetInfo) && !getSupportedResizeActions(host, (LauncherAppWidgetInfo) item).isEmpty()) {
                    info.addAction(this.mActions.get(R.id.action_resize));
                }
            }
            if ((item instanceof AppInfo) || (item instanceof PendingAddItemInfo)) {
                info.addAction(this.mActions.get(R.id.action_add_to_workspace));
            }
        }
    }

    public boolean performAccessibilityAction(View host, int action, Bundle args) {
        if (!(host.getTag() instanceof ItemInfo) || !performAction(host, (ItemInfo) host.getTag(), action)) {
            return super.performAccessibilityAction(host, action, args);
        }
        return true;
    }

    public boolean performAction(View host, ItemInfo item, int action) {
        final View view = host;
        final ItemInfo itemInfo = item;
        int i = action;
        if (i == R.id.action_move) {
            beginAccessibleDrag(host, item);
        } else if (i == R.id.action_add_to_workspace) {
            int[] coordinates = new int[2];
            long screenId = findSpaceOnWorkspace(itemInfo, coordinates);
            LauncherStateManager stateManager = this.mLauncher.getStateManager();
            LauncherState launcherState = LauncherState.NORMAL;
            final ItemInfo itemInfo2 = item;
            final long j = screenId;
            AnonymousClass1 r10 = r0;
            final int[] iArr = coordinates;
            AnonymousClass1 r0 = new Runnable() {
                public void run() {
                    if (itemInfo2 instanceof AppInfo) {
                        ShortcutInfo info = ((AppInfo) itemInfo2).makeShortcut();
                        LauncherAccessibilityDelegate.this.mLauncher.getModelWriter().addItemToDatabase(info, -100, j, iArr[0], iArr[1]);
                        ArrayList<ItemInfo> itemList = new ArrayList<>();
                        itemList.add(info);
                        LauncherAccessibilityDelegate.this.mLauncher.bindItems(itemList, true);
                    } else if (itemInfo2 instanceof PendingAddItemInfo) {
                        PendingAddItemInfo info2 = (PendingAddItemInfo) itemInfo2;
                        Workspace workspace = LauncherAccessibilityDelegate.this.mLauncher.getWorkspace();
                        workspace.snapToPage(workspace.getPageIndexForScreenId(j));
                        LauncherAccessibilityDelegate.this.mLauncher.addPendingItem(info2, -100, j, iArr, info2.spanX, info2.spanY);
                    }
                    LauncherAccessibilityDelegate.this.announceConfirmation((int) R.string.item_added_to_workspace);
                }
            };
            stateManager.goToState(launcherState, true, (Runnable) r10);
            return true;
        } else if (i == R.id.action_move_to_workspace) {
            Folder folder = Folder.getOpen(this.mLauncher);
            folder.close(true);
            ShortcutInfo info = (ShortcutInfo) itemInfo;
            folder.getInfo().remove(info, false);
            int[] coordinates2 = new int[2];
            this.mLauncher.getModelWriter().moveItemInDatabase(info, -100, findSpaceOnWorkspace(itemInfo, coordinates2), coordinates2[0], coordinates2[1]);
            new Handler().post(new Runnable() {
                public void run() {
                    ArrayList<ItemInfo> itemList = new ArrayList<>();
                    itemList.add(itemInfo);
                    LauncherAccessibilityDelegate.this.mLauncher.bindItems(itemList, true);
                    LauncherAccessibilityDelegate.this.announceConfirmation((int) R.string.item_moved);
                }
            });
        } else if (i == R.id.action_resize) {
            final LauncherAppWidgetInfo info2 = (LauncherAppWidgetInfo) itemInfo;
            final ArrayList<Integer> actions = getSupportedResizeActions(view, info2);
            CharSequence[] labels = new CharSequence[actions.size()];
            for (int i2 = 0; i2 < actions.size(); i2++) {
                labels[i2] = this.mLauncher.getText(actions.get(i2).intValue());
            }
            new AlertDialog.Builder(this.mLauncher).setTitle(R.string.action_resize).setItems(labels, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LauncherAccessibilityDelegate.this.performResizeAction(((Integer) actions.get(which)).intValue(), view, info2);
                    dialog.dismiss();
                }
            }).show();
            return true;
        } else if (i == R.id.action_deep_shortcuts) {
            return PopupContainerWithArrow.showForIcon((BubbleTextView) view) != null;
        } else {
            ButtonDropTarget[] dropTargets = this.mLauncher.getDropTargetBar().getDropTargets();
            int length = dropTargets.length;
            int i3 = 0;
            while (i3 < length) {
                ButtonDropTarget dropTarget = dropTargets[i3];
                if (!dropTarget.supportsAccessibilityDrop(itemInfo, view) || i != dropTarget.getAccessibilityAction()) {
                    i3++;
                } else {
                    dropTarget.onAccessibilityDrop(view, itemInfo);
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<Integer> getSupportedResizeActions(View host, LauncherAppWidgetInfo info) {
        ArrayList<Integer> actions = new ArrayList<>();
        AppWidgetProviderInfo providerInfo = ((LauncherAppWidgetHostView) host).getAppWidgetInfo();
        if (providerInfo == null) {
            return actions;
        }
        CellLayout layout = (CellLayout) host.getParent().getParent();
        if ((providerInfo.resizeMode & 1) != 0) {
            if (layout.isRegionVacant(info.cellX + info.spanX, info.cellY, 1, info.spanY) || layout.isRegionVacant(info.cellX - 1, info.cellY, 1, info.spanY)) {
                actions.add(Integer.valueOf(R.string.action_increase_width));
            }
            if (info.spanX > info.minSpanX && info.spanX > 1) {
                actions.add(Integer.valueOf(R.string.action_decrease_width));
            }
        }
        if ((providerInfo.resizeMode & 2) != 0) {
            if (layout.isRegionVacant(info.cellX, info.cellY + info.spanY, info.spanX, 1) || layout.isRegionVacant(info.cellX, info.cellY - 1, info.spanX, 1)) {
                actions.add(Integer.valueOf(R.string.action_increase_height));
            }
            if (info.spanY > info.minSpanY && info.spanY > 1) {
                actions.add(Integer.valueOf(R.string.action_decrease_height));
            }
        }
        return actions;
    }

    /* access modifiers changed from: package-private */
    public void performResizeAction(int action, View host, LauncherAppWidgetInfo info) {
        int i = action;
        View view = host;
        LauncherAppWidgetInfo launcherAppWidgetInfo = info;
        CellLayout.LayoutParams lp = (CellLayout.LayoutParams) host.getLayoutParams();
        CellLayout layout = (CellLayout) host.getParent().getParent();
        layout.markCellsAsUnoccupiedForView(view);
        if (i == R.string.action_increase_width) {
            if ((host.getLayoutDirection() == 1 && layout.isRegionVacant(launcherAppWidgetInfo.cellX - 1, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) || !layout.isRegionVacant(launcherAppWidgetInfo.cellX + launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.cellY, 1, launcherAppWidgetInfo.spanY)) {
                lp.cellX--;
                launcherAppWidgetInfo.cellX--;
            }
            lp.cellHSpan++;
            launcherAppWidgetInfo.spanX++;
        } else if (i == R.string.action_decrease_width) {
            lp.cellHSpan--;
            launcherAppWidgetInfo.spanX--;
        } else if (i == R.string.action_increase_height) {
            if (!layout.isRegionVacant(launcherAppWidgetInfo.cellX, launcherAppWidgetInfo.cellY + launcherAppWidgetInfo.spanY, launcherAppWidgetInfo.spanX, 1)) {
                lp.cellY--;
                launcherAppWidgetInfo.cellY--;
            }
            lp.cellVSpan++;
            launcherAppWidgetInfo.spanY++;
        } else if (i == R.string.action_decrease_height) {
            lp.cellVSpan--;
            launcherAppWidgetInfo.spanY--;
        }
        layout.markCellsAsOccupiedForView(view);
        Rect sizeRange = new Rect();
        AppWidgetResizeFrame.getWidgetSizeRanges(this.mLauncher, launcherAppWidgetInfo.spanX, launcherAppWidgetInfo.spanY, sizeRange);
        ((LauncherAppWidgetHostView) view).updateAppWidgetSize((Bundle) null, sizeRange.left, sizeRange.top, sizeRange.right, sizeRange.bottom);
        host.requestLayout();
        this.mLauncher.getModelWriter().updateItemInDatabase(launcherAppWidgetInfo);
        announceConfirmation(this.mLauncher.getString(R.string.widget_resized, new Object[]{Integer.valueOf(launcherAppWidgetInfo.spanX), Integer.valueOf(launcherAppWidgetInfo.spanY)}));
    }

    /* access modifiers changed from: package-private */
    public void announceConfirmation(int resId) {
        announceConfirmation(this.mLauncher.getResources().getString(resId));
    }

    /* access modifiers changed from: package-private */
    public void announceConfirmation(String confirmation) {
        this.mLauncher.getDragLayer().announceForAccessibility(confirmation);
    }

    public boolean isInAccessibleDrag() {
        return this.mDragInfo != null;
    }

    public DragInfo getDragInfo() {
        return this.mDragInfo;
    }

    public void handleAccessibleDrop(View clickedTarget, Rect dropLocation, String confirmation) {
        if (isInAccessibleDrag()) {
            int[] loc = new int[2];
            if (dropLocation == null) {
                loc[0] = clickedTarget.getWidth() / 2;
                loc[1] = clickedTarget.getHeight() / 2;
            } else {
                loc[0] = dropLocation.centerX();
                loc[1] = dropLocation.centerY();
            }
            this.mLauncher.getDragLayer().getDescendantCoordRelativeToSelf(clickedTarget, loc);
            this.mLauncher.getDragController().completeAccessibleDrag(loc);
            if (!TextUtils.isEmpty(confirmation)) {
                announceConfirmation(confirmation);
            }
        }
    }

    public void beginAccessibleDrag(View item, ItemInfo info) {
        this.mDragInfo = new DragInfo();
        this.mDragInfo.info = info;
        this.mDragInfo.item = item;
        this.mDragInfo.dragType = DragType.ICON;
        if (info instanceof FolderInfo) {
            this.mDragInfo.dragType = DragType.FOLDER;
        } else if (info instanceof LauncherAppWidgetInfo) {
            this.mDragInfo.dragType = DragType.WIDGET;
        }
        Rect pos = new Rect();
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(item, pos);
        this.mLauncher.getDragController().prepareAccessibleDrag(pos.centerX(), pos.centerY());
        this.mLauncher.getDragController().addDragListener(this);
        DragOptions options = new DragOptions();
        options.isAccessibleDrag = true;
        ItemLongClickListener.beginDrag(item, this.mLauncher, info, options);
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
    }

    public void onDragEnd() {
        this.mLauncher.getDragController().removeDragListener(this);
        this.mDragInfo = null;
    }

    /* access modifiers changed from: protected */
    public long findSpaceOnWorkspace(ItemInfo info, int[] outCoordinates) {
        Workspace workspace = this.mLauncher.getWorkspace();
        ArrayList<Long> workspaceScreens = workspace.getScreenOrder();
        int screenIndex = workspace.getCurrentPage();
        long screenId = workspaceScreens.get(screenIndex).longValue();
        boolean found = ((CellLayout) workspace.getPageAt(screenIndex)).findCellForSpan(outCoordinates, info.spanX, info.spanY);
        int screenIndex2 = 0;
        while (!found && screenIndex2 < workspaceScreens.size()) {
            screenId = workspaceScreens.get(screenIndex2).longValue();
            found = ((CellLayout) workspace.getPageAt(screenIndex2)).findCellForSpan(outCoordinates, info.spanX, info.spanY);
            screenIndex2++;
        }
        if (found) {
            return screenId;
        }
        workspace.addExtraEmptyScreen();
        long screenId2 = workspace.commitExtraEmptyScreen();
        if (!workspace.getScreenWithId(screenId2).findCellForSpan(outCoordinates, info.spanX, info.spanY)) {
            Log.wtf(TAG, "Not enough space on an empty screen");
        }
        return screenId2;
    }
}
