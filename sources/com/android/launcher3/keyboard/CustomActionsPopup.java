package com.android.launcher3.keyboard;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.PopupMenu;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.popup.PopupContainerWithArrow;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomActionsPopup implements PopupMenu.OnMenuItemClickListener {
    private final LauncherAccessibilityDelegate mDelegate;
    private final View mIcon;
    private final Launcher mLauncher;

    public CustomActionsPopup(Launcher launcher, View icon) {
        this.mLauncher = launcher;
        this.mIcon = icon;
        PopupContainerWithArrow container = PopupContainerWithArrow.getOpen(launcher);
        if (container != null) {
            this.mDelegate = container.getAccessibilityDelegate();
        } else {
            this.mDelegate = launcher.getAccessibilityDelegate();
        }
    }

    private List<AccessibilityNodeInfo.AccessibilityAction> getActionList() {
        if (this.mIcon == null || !(this.mIcon.getTag() instanceof ItemInfo)) {
            return Collections.EMPTY_LIST;
        }
        AccessibilityNodeInfo info = AccessibilityNodeInfo.obtain();
        this.mDelegate.addSupportedActions(this.mIcon, info, true);
        List<AccessibilityNodeInfo.AccessibilityAction> result = new ArrayList<>(info.getActionList());
        info.recycle();
        return result;
    }

    public boolean canShow() {
        return !getActionList().isEmpty();
    }

    public boolean show() {
        List<AccessibilityNodeInfo.AccessibilityAction> actions = getActionList();
        if (actions.isEmpty()) {
            return false;
        }
        PopupMenu popup = new PopupMenu(this.mLauncher, this.mIcon);
        popup.setOnMenuItemClickListener(this);
        Menu menu = popup.getMenu();
        for (AccessibilityNodeInfo.AccessibilityAction action : actions) {
            menu.add(0, action.getId(), 0, action.getLabel());
        }
        popup.show();
        return true;
    }

    public boolean onMenuItemClick(MenuItem menuItem) {
        return this.mDelegate.performAction(this.mIcon, (ItemInfo) this.mIcon.getTag(), menuItem.getItemId());
    }
}
