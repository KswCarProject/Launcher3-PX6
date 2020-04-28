package com.android.launcher3.accessibility;

import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.notification.NotificationMainView;
import com.android.launcher3.shortcuts.DeepShortcutView;
import java.util.ArrayList;

public class ShortcutMenuAccessibilityDelegate extends LauncherAccessibilityDelegate {
    private static final int DISMISS_NOTIFICATION = 2131427432;

    public ShortcutMenuAccessibilityDelegate(Launcher launcher) {
        super(launcher);
        this.mActions.put(R.id.action_dismiss_notification, new AccessibilityNodeInfo.AccessibilityAction(R.id.action_dismiss_notification, launcher.getText(R.string.action_dismiss_notification)));
    }

    public void addSupportedActions(View host, AccessibilityNodeInfo info, boolean fromKeyboard) {
        if (host.getParent() instanceof DeepShortcutView) {
            info.addAction((AccessibilityNodeInfo.AccessibilityAction) this.mActions.get(R.id.action_add_to_workspace));
        } else if ((host instanceof NotificationMainView) && ((NotificationMainView) host).canChildBeDismissed()) {
            info.addAction((AccessibilityNodeInfo.AccessibilityAction) this.mActions.get(R.id.action_dismiss_notification));
        }
    }

    public boolean performAction(View host, ItemInfo item, int action) {
        if (action == R.id.action_add_to_workspace) {
            if (!(host.getParent() instanceof DeepShortcutView)) {
                return false;
            }
            int[] coordinates = new int[2];
            final ShortcutInfo finalInfo = ((DeepShortcutView) host.getParent()).getFinalInfo();
            final long findSpaceOnWorkspace = findSpaceOnWorkspace(item, coordinates);
            final int[] iArr = coordinates;
            this.mLauncher.getStateManager().goToState(LauncherState.NORMAL, true, new Runnable() {
                public void run() {
                    ShortcutMenuAccessibilityDelegate.this.mLauncher.getModelWriter().addItemToDatabase(finalInfo, -100, findSpaceOnWorkspace, iArr[0], iArr[1]);
                    ArrayList<ItemInfo> itemList = new ArrayList<>();
                    itemList.add(finalInfo);
                    ShortcutMenuAccessibilityDelegate.this.mLauncher.bindItems(itemList, true);
                    AbstractFloatingView.closeAllOpenViews(ShortcutMenuAccessibilityDelegate.this.mLauncher);
                    ShortcutMenuAccessibilityDelegate.this.announceConfirmation((int) R.string.item_added_to_workspace);
                }
            });
            return true;
        } else if (action != R.id.action_dismiss_notification || !(host instanceof NotificationMainView)) {
            return false;
        } else {
            ((NotificationMainView) host).onChildDismissed();
            announceConfirmation((int) R.string.notification_dismissed);
            return true;
        }
    }
}
