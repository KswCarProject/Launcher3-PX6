package com.android.launcher3.model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.MultiHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirstScreenBroadcast {
    private static final String ACTION_FIRST_SCREEN_ACTIVE_INSTALLS = "com.android.launcher3.action.FIRST_SCREEN_ACTIVE_INSTALLS";
    private static final boolean DEBUG = false;
    private static final String FOLDER_ITEM_EXTRA = "folderItem";
    private static final String HOTSEAT_ITEM_EXTRA = "hotseatItem";
    private static final String TAG = "FirstScreenBroadcast";
    private static final String VERIFICATION_TOKEN_EXTRA = "verificationToken";
    private static final String WIDGET_ITEM_EXTRA = "widgetItem";
    private static final String WORKSPACE_ITEM_EXTRA = "workspaceItem";
    private final MultiHashMap<String, String> mPackagesForInstaller;

    public FirstScreenBroadcast(HashMap<String, PackageInstaller.SessionInfo> sessionInfoForPackage) {
        this.mPackagesForInstaller = getPackagesForInstaller(sessionInfoForPackage);
    }

    private MultiHashMap<String, String> getPackagesForInstaller(HashMap<String, PackageInstaller.SessionInfo> sessionInfoForPackage) {
        MultiHashMap<String, String> packagesForInstaller = new MultiHashMap<>();
        for (Map.Entry<String, PackageInstaller.SessionInfo> entry : sessionInfoForPackage.entrySet()) {
            packagesForInstaller.addToList(entry.getValue().getInstallerPackageName(), entry.getKey());
        }
        return packagesForInstaller;
    }

    public void sendBroadcasts(Context context, List<ItemInfo> firstScreenItems) {
        for (Map.Entry<String, ArrayList<String>> entry : this.mPackagesForInstaller.entrySet()) {
            sendBroadcastToInstaller(context, entry.getKey(), entry.getValue(), firstScreenItems);
        }
    }

    private void sendBroadcastToInstaller(Context context, String installerPackageName, List<String> packages, List<ItemInfo> firstScreenItems) {
        Set<String> folderItems = new HashSet<>();
        Set<String> workspaceItems = new HashSet<>();
        Set<String> hotseatItems = new HashSet<>();
        Set<String> widgetItems = new HashSet<>();
        for (ItemInfo info : firstScreenItems) {
            if (info instanceof FolderInfo) {
                Iterator<ShortcutInfo> it = ((FolderInfo) info).contents.iterator();
                while (it.hasNext()) {
                    String folderItemInfoPackage = getPackageName(it.next());
                    if (folderItemInfoPackage != null && packages.contains(folderItemInfoPackage)) {
                        folderItems.add(folderItemInfoPackage);
                    }
                }
            }
            String packageName = getPackageName(info);
            if (packageName != null && packages.contains(packageName)) {
                if (info instanceof LauncherAppWidgetInfo) {
                    widgetItems.add(packageName);
                } else if (info.container == -101) {
                    hotseatItems.add(packageName);
                } else if (info.container == -100) {
                    workspaceItems.add(packageName);
                }
            }
        }
        context.sendBroadcast(new Intent(ACTION_FIRST_SCREEN_ACTIVE_INSTALLS).setPackage(installerPackageName).putStringArrayListExtra(FOLDER_ITEM_EXTRA, new ArrayList(folderItems)).putStringArrayListExtra(WORKSPACE_ITEM_EXTRA, new ArrayList(workspaceItems)).putStringArrayListExtra(HOTSEAT_ITEM_EXTRA, new ArrayList(hotseatItems)).putStringArrayListExtra(WIDGET_ITEM_EXTRA, new ArrayList(widgetItems)).putExtra(VERIFICATION_TOKEN_EXTRA, PendingIntent.getActivity(context, 0, new Intent(), 1073741824)));
    }

    private static String getPackageName(ItemInfo info) {
        if (info instanceof LauncherAppWidgetInfo) {
            LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) info;
            if (widgetInfo.providerName != null) {
                return widgetInfo.providerName.getPackageName();
            }
            return null;
        } else if (info.getTargetComponent() != null) {
            return info.getTargetComponent().getPackageName();
        } else {
            return null;
        }
    }

    private static void printList(String packageInstaller, String label, Set<String> packages) {
        for (String pkg : packages) {
            Log.d(TAG, packageInstaller + ":" + label + ":" + pkg);
        }
    }
}
