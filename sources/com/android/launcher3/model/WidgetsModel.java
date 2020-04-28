package com.android.launcher3.model;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import com.android.launcher3.AppFilter;
import com.android.launcher3.IconCache;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.widget.WidgetItemComparator;
import com.android.launcher3.widget.WidgetListRowEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WidgetsModel {
    private static final boolean DEBUG = false;
    private static final String TAG = "WidgetsModel";
    private AppFilter mAppFilter;
    private final MultiHashMap<PackageItemInfo, WidgetItem> mWidgetsList = new MultiHashMap<>();

    public synchronized ArrayList<WidgetListRowEntry> getWidgetsList(Context context) {
        ArrayList<WidgetListRowEntry> result;
        result = new ArrayList<>();
        AlphabeticIndexCompat indexer = new AlphabeticIndexCompat(context);
        WidgetItemComparator widgetComparator = new WidgetItemComparator();
        for (Map.Entry<PackageItemInfo, ArrayList<WidgetItem>> entry : this.mWidgetsList.entrySet()) {
            WidgetListRowEntry row = new WidgetListRowEntry(entry.getKey(), entry.getValue());
            row.titleSectionName = indexer.computeSectionName(row.pkgItem.title);
            Collections.sort(row.widgets, widgetComparator);
            result.add(row);
        }
        return result;
    }

    public void update(LauncherAppState app, @Nullable PackageUserKey packageUser) {
        Preconditions.assertWorkerThread();
        Context context = app.getContext();
        ArrayList<WidgetItem> widgetsAndShortcuts = new ArrayList<>();
        try {
            PackageManager pm = context.getPackageManager();
            InvariantDeviceProfile idp = app.getInvariantDeviceProfile();
            for (AppWidgetProviderInfo widgetInfo : AppWidgetManagerCompat.getInstance(context).getAllProviders(packageUser)) {
                widgetsAndShortcuts.add(new WidgetItem(LauncherAppWidgetProviderInfo.fromProviderInfo(context, widgetInfo), pm, idp));
            }
            for (ShortcutConfigActivityInfo info : LauncherAppsCompat.getInstance(context).getCustomShortcutActivityList(packageUser)) {
                widgetsAndShortcuts.add(new WidgetItem(info));
            }
            setWidgetsAndShortcuts(widgetsAndShortcuts, app, packageUser);
        } catch (Exception e) {
            if (!Utilities.isBinderSizeError(e)) {
                throw e;
            }
        }
        app.getWidgetCache().removeObsoletePreviews(widgetsAndShortcuts, packageUser);
    }

    private synchronized void setWidgetsAndShortcuts(ArrayList<WidgetItem> rawWidgetsShortcuts, LauncherAppState app, @Nullable PackageUserKey packageUser) {
        HashMap<String, PackageItemInfo> tmpPackageItemInfos = new HashMap<>();
        if (packageUser == null) {
            this.mWidgetsList.clear();
        } else {
            PackageItemInfo packageItem = null;
            Iterator it = this.mWidgetsList.keySet().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                PackageItemInfo item = (PackageItemInfo) it.next();
                if (item.packageName.equals(packageUser.mPackageName)) {
                    packageItem = item;
                    break;
                }
            }
            if (packageItem != null) {
                tmpPackageItemInfos.put(packageItem.packageName, packageItem);
                Iterator<WidgetItem> widgetItemIterator = ((ArrayList) this.mWidgetsList.get(packageItem)).iterator();
                while (widgetItemIterator.hasNext()) {
                    WidgetItem nextWidget = widgetItemIterator.next();
                    if (nextWidget.componentName.getPackageName().equals(packageUser.mPackageName) && nextWidget.user.equals(packageUser.mUser)) {
                        widgetItemIterator.remove();
                    }
                }
            }
        }
        InvariantDeviceProfile idp = app.getInvariantDeviceProfile();
        UserHandle myUser = Process.myUserHandle();
        Iterator<WidgetItem> it2 = rawWidgetsShortcuts.iterator();
        while (it2.hasNext()) {
            WidgetItem item2 = it2.next();
            if (item2.widgetInfo != null) {
                if ((item2.widgetInfo.getWidgetFeatures() & 2) == 0) {
                    int minSpanX = Math.min(item2.widgetInfo.spanX, item2.widgetInfo.minSpanX);
                    int minSpanY = Math.min(item2.widgetInfo.spanY, item2.widgetInfo.minSpanY);
                    if (minSpanX <= idp.numColumns) {
                        if (minSpanY > idp.numRows) {
                        }
                    }
                }
            }
            if (this.mAppFilter == null) {
                this.mAppFilter = AppFilter.newInstance(app.getContext());
            }
            if (this.mAppFilter.shouldShowApp(item2.componentName)) {
                String packageName = item2.componentName.getPackageName();
                PackageItemInfo pInfo = tmpPackageItemInfos.get(packageName);
                if (pInfo == null) {
                    pInfo = new PackageItemInfo(packageName);
                    pInfo.user = item2.user;
                    tmpPackageItemInfos.put(packageName, pInfo);
                } else if (!myUser.equals(pInfo.user)) {
                    pInfo.user = item2.user;
                }
                this.mWidgetsList.addToList(pInfo, item2);
            }
        }
        IconCache iconCache = app.getIconCache();
        for (PackageItemInfo p : tmpPackageItemInfos.values()) {
            iconCache.getTitleAndIconForApp(p, true);
        }
    }
}
