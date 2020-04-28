package com.android.launcher3.allapps;

import android.os.UserHandle;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.PromiseAppInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AllAppsStore {
    private final HashMap<ComponentKey, AppInfo> mComponentToAppMap = new HashMap<>();
    private boolean mDeferUpdates = false;
    private final ArrayList<ViewGroup> mIconContainers = new ArrayList<>();
    private PackageUserKey mTempKey = new PackageUserKey((String) null, (UserHandle) null);
    private final List<OnUpdateListener> mUpdateListeners = new ArrayList();
    private boolean mUpdatePending = false;

    public interface IconAction {
        void apply(BubbleTextView bubbleTextView);
    }

    public interface OnUpdateListener {
        void onAppsUpdated();
    }

    public Collection<AppInfo> getApps() {
        return this.mComponentToAppMap.values();
    }

    public ArrayList<AppInfo> getAppsList() {
        ArrayList<AppInfo> list = new ArrayList<>();
        for (AppInfo app : this.mComponentToAppMap.values()) {
            list.add(app);
        }
        return list;
    }

    public void setApps(List<AppInfo> apps) {
        this.mComponentToAppMap.clear();
        addOrUpdateApps(apps);
    }

    public AppInfo getApp(ComponentKey key) {
        return this.mComponentToAppMap.get(key);
    }

    public void setDeferUpdates(boolean deferUpdates) {
        if (this.mDeferUpdates != deferUpdates) {
            this.mDeferUpdates = deferUpdates;
            if (!this.mDeferUpdates && this.mUpdatePending) {
                notifyUpdate();
                this.mUpdatePending = false;
            }
        }
    }

    public void addOrUpdateApps(List<AppInfo> apps) {
        for (AppInfo app : apps) {
            this.mComponentToAppMap.put(app.toComponentKey(), app);
        }
        notifyUpdate();
    }

    public void removeApps(List<AppInfo> apps) {
        for (AppInfo app : apps) {
            this.mComponentToAppMap.remove(app.toComponentKey());
        }
        notifyUpdate();
    }

    private void notifyUpdate() {
        if (this.mDeferUpdates) {
            this.mUpdatePending = true;
            return;
        }
        int count = this.mUpdateListeners.size();
        for (int i = 0; i < count; i++) {
            this.mUpdateListeners.get(i).onAppsUpdated();
        }
    }

    public void addUpdateListener(OnUpdateListener listener) {
        this.mUpdateListeners.add(listener);
    }

    public void removeUpdateListener(OnUpdateListener listener) {
        this.mUpdateListeners.remove(listener);
    }

    public void registerIconContainer(ViewGroup container) {
        if (container != null) {
            this.mIconContainers.add(container);
        }
    }

    public void unregisterIconContainer(ViewGroup container) {
        this.mIconContainers.remove(container);
    }

    public void updateIconBadges(Set<PackageUserKey> updatedBadges) {
        updateAllIcons(new IconAction(updatedBadges) {
            private final /* synthetic */ Set f$1;

            {
                this.f$1 = r2;
            }

            public final void apply(BubbleTextView bubbleTextView) {
                AllAppsStore.lambda$updateIconBadges$0(AllAppsStore.this, this.f$1, bubbleTextView);
            }
        });
    }

    public static /* synthetic */ void lambda$updateIconBadges$0(AllAppsStore allAppsStore, Set updatedBadges, BubbleTextView child) {
        if (child.getTag() instanceof ItemInfo) {
            ItemInfo info = (ItemInfo) child.getTag();
            if (allAppsStore.mTempKey.updateFromItemInfo(info) && updatedBadges.contains(allAppsStore.mTempKey)) {
                child.applyBadgeState(info, true);
            }
        }
    }

    public void updatePromiseAppProgress(PromiseAppInfo app) {
        updateAllIcons(new IconAction() {
            public final void apply(BubbleTextView bubbleTextView) {
                AllAppsStore.lambda$updatePromiseAppProgress$1(PromiseAppInfo.this, bubbleTextView);
            }
        });
    }

    static /* synthetic */ void lambda$updatePromiseAppProgress$1(PromiseAppInfo app, BubbleTextView child) {
        if (child.getTag() == app) {
            child.applyProgressLevel(app.level);
        }
    }

    private void updateAllIcons(IconAction action) {
        for (int i = this.mIconContainers.size() - 1; i >= 0; i--) {
            ViewGroup parent = this.mIconContainers.get(i);
            int childCount = parent.getChildCount();
            for (int j = 0; j < childCount; j++) {
                View child = parent.getChildAt(j);
                if (child instanceof BubbleTextView) {
                    action.apply((BubbleTextView) child);
                }
            }
        }
    }
}
