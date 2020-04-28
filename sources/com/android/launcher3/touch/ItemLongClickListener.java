package com.android.launcher3.touch;

import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.util.PendingRequestArgs;

public class ItemLongClickListener {
    public static View.OnLongClickListener INSTANCE_ALL_APPS = $$Lambda$ItemLongClickListener$pr3KEnHIBNhjVshSoZRttm8yUa8.INSTANCE;
    public static View.OnLongClickListener INSTANCE_WORKSPACE = $$Lambda$ItemLongClickListener$n_ku6Bnp7SQnCFIT2R46R_RyW8.INSTANCE;

    /* access modifiers changed from: private */
    public static boolean onWorkspaceItemLongClick(View v) {
        Launcher launcher = Launcher.getLauncher(v.getContext());
        if (!canStartDrag(launcher)) {
            return false;
        }
        if ((!launcher.isInState(LauncherState.NORMAL) && !launcher.isInState(LauncherState.OVERVIEW)) || !(v.getTag() instanceof ItemInfo)) {
            return false;
        }
        launcher.setWaitingForResult((PendingRequestArgs) null);
        beginDrag(v, launcher, (ItemInfo) v.getTag(), new DragOptions());
        return true;
    }

    public static void beginDrag(View v, Launcher launcher, ItemInfo info, DragOptions dragOptions) {
        Folder folder;
        if (info.container >= 0 && (folder = Folder.getOpen(launcher)) != null) {
            if (!folder.getItemsInReadingOrder().contains(v)) {
                folder.close(true);
            } else {
                folder.startDrag(v, dragOptions);
                return;
            }
        }
        launcher.getWorkspace().startDrag(new CellLayout.CellInfo(v, info), dragOptions);
    }

    /* access modifiers changed from: private */
    public static boolean onAllAppsItemLongClick(final View v) {
        Launcher launcher = Launcher.getLauncher(v.getContext());
        if (!canStartDrag(launcher)) {
            return false;
        }
        if ((!launcher.isInState(LauncherState.ALL_APPS) && !launcher.isInState(LauncherState.OVERVIEW)) || launcher.getWorkspace().isSwitchingState()) {
            return false;
        }
        final DragController dragController = launcher.getDragController();
        dragController.addDragListener(new DragController.DragListener() {
            public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
                v.setVisibility(4);
            }

            public void onDragEnd() {
                v.setVisibility(0);
                dragController.removeDragListener(this);
            }
        });
        DeviceProfile grid = launcher.getDeviceProfile();
        DragOptions options = new DragOptions();
        options.intrinsicIconScaleFactor = ((float) grid.allAppsIconSizePx) / ((float) grid.iconSizePx);
        launcher.getWorkspace().beginDragShared(v, launcher.getAppsView(), options);
        return false;
    }

    public static boolean canStartDrag(Launcher launcher) {
        if (launcher != null && !launcher.isWorkspaceLocked() && !launcher.getDragController().isDragging()) {
            return true;
        }
        return false;
    }
}
