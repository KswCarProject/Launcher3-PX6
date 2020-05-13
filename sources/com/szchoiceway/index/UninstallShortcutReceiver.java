package com.szchoiceway.index;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.widget.Toast;
import com.szchoiceway.index.LauncherSettings;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class UninstallShortcutReceiver extends BroadcastReceiver {
    private static final String ACTION_UNINSTALL_SHORTCUT = "com.szchoiceway.index.action.UNINSTALL_SHORTCUT";
    private static ArrayList<PendingUninstallShortcutInfo> mUninstallQueue = new ArrayList<>();
    private static boolean mUseUninstallQueue = false;

    private static class PendingUninstallShortcutInfo {
        Intent data;

        public PendingUninstallShortcutInfo(Intent rawData) {
            this.data = rawData;
        }
    }

    public void onReceive(Context context, Intent data) {
        if (ACTION_UNINSTALL_SHORTCUT.equals(data.getAction())) {
            PendingUninstallShortcutInfo info = new PendingUninstallShortcutInfo(data);
            if (mUseUninstallQueue) {
                mUninstallQueue.add(info);
            } else {
                processUninstallShortcut(context, info);
            }
        }
    }

    static void enableUninstallQueue() {
        mUseUninstallQueue = true;
    }

    static void disableAndFlushUninstallQueue(Context context) {
        mUseUninstallQueue = false;
        Iterator<PendingUninstallShortcutInfo> iter = mUninstallQueue.iterator();
        while (iter.hasNext()) {
            processUninstallShortcut(context, iter.next());
            iter.remove();
        }
    }

    private static void processUninstallShortcut(Context context, PendingUninstallShortcutInfo pendingInfo) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0);
        Intent data = pendingInfo.data;
        synchronized (((LauncherApplication) context.getApplicationContext())) {
            removeShortcut(context, data, sharedPrefs);
        }
    }

    private static void removeShortcut(Context context, Intent data, SharedPreferences sharedPrefs) {
        boolean appRemoved;
        Intent intent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        String name = data.getStringExtra("android.intent.extra.shortcut.NAME");
        boolean duplicate = data.getBooleanExtra("duplicate", true);
        if (intent != null && name != null) {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(LauncherSettings.Favorites.CONTENT_URI, new String[]{"_id", LauncherSettings.BaseLauncherColumns.INTENT}, "title=?", new String[]{name}, (String) null);
            int intentIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
            int idIndex = c.getColumnIndexOrThrow("_id");
            boolean changed = false;
            while (c.moveToNext()) {
                try {
                    try {
                        if (intent.filterEquals(Intent.parseUri(c.getString(intentIndex), 0))) {
                            cr.delete(LauncherSettings.Favorites.getContentUri(c.getLong(idIndex), false), (String) null, (String[]) null);
                            changed = true;
                            if (!duplicate) {
                                break;
                            }
                        } else {
                            continue;
                        }
                    } catch (URISyntaxException e) {
                    }
                } finally {
                    c.close();
                }
            }
            if (changed) {
                cr.notifyChange(LauncherSettings.Favorites.CONTENT_URI, (ContentObserver) null);
                Toast.makeText(context, context.getString(R.string.shortcut_uninstalled, new Object[]{name}), 0).show();
            }
            Set<String> newApps = sharedPrefs.getStringSet(InstallShortcutReceiver.NEW_APPS_LIST_KEY, new HashSet());
            synchronized (newApps) {
                do {
                    appRemoved = newApps.remove(intent.toUri(0).toString());
                } while (appRemoved);
            }
            if (appRemoved) {
                final Set<String> set = newApps;
                final SharedPreferences sharedPreferences = sharedPrefs;
                new Thread("setNewAppsThread-remove") {
                    public void run() {
                        synchronized (set) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putStringSet(InstallShortcutReceiver.NEW_APPS_LIST_KEY, set);
                            if (set.isEmpty()) {
                                editor.putInt(InstallShortcutReceiver.NEW_APPS_PAGE_KEY, -1);
                            }
                            editor.commit();
                        }
                    }
                }.start();
            }
        }
    }
}
