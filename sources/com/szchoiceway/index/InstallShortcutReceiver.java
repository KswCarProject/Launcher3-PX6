package com.szchoiceway.index;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

public class InstallShortcutReceiver extends BroadcastReceiver {
    public static final String ACTION_INSTALL_SHORTCUT = "com.szchoiceway.index.action.INSTALL_SHORTCUT";
    public static final String APPS_PENDING_INSTALL = "apps_to_install";
    public static final String DATA_INTENT_KEY = "intent.data";
    public static final String ICON_KEY = "icon";
    public static final String ICON_RESOURCE_NAME_KEY = "iconResource";
    public static final String ICON_RESOURCE_PACKAGE_NAME_KEY = "iconResourcePackage";
    private static final int INSTALL_SHORTCUT_IS_DUPLICATE = -1;
    private static final int INSTALL_SHORTCUT_NO_SPACE = -2;
    private static final int INSTALL_SHORTCUT_SUCCESSFUL = 0;
    public static final String LAUNCH_INTENT_KEY = "intent.launch";
    public static final String NAME_KEY = "name";
    public static final String NEW_APPS_LIST_KEY = "apps.new.list";
    public static final String NEW_APPS_PAGE_KEY = "apps.new.page";
    public static final int NEW_SHORTCUT_BOUNCE_DURATION = 450;
    public static final int NEW_SHORTCUT_STAGGER_DELAY = 75;
    public static final String SHORTCUT_MIMETYPE = "com.szchoiceway.index/shortcut";
    private static boolean mUseInstallQueue = false;
    /* access modifiers changed from: private */
    public static Object sLock = new Object();

    /* access modifiers changed from: private */
    public static void addToStringSet(SharedPreferences sharedPrefs, SharedPreferences.Editor editor, String key, String value) {
        Set<String> strings;
        Set<String> strings2 = sharedPrefs.getStringSet(key, (Set) null);
        if (strings2 == null) {
            strings = new HashSet<>(0);
        } else {
            strings = new HashSet<>(strings2);
        }
        strings.add(value);
        editor.putStringSet(key, strings);
    }

    private static void addToInstallQueue(SharedPreferences sharedPrefs, PendingInstallShortcutInfo info) {
        synchronized (sLock) {
            try {
                JSONStringer json = new JSONStringer().object().key(DATA_INTENT_KEY).value(info.data.toUri(0)).key(LAUNCH_INTENT_KEY).value(info.launchIntent.toUri(0)).key(NAME_KEY).value(info.name);
                if (info.icon != null) {
                    byte[] iconByteArray = ItemInfo.flattenBitmap(info.icon);
                    json = json.key("icon").value(Base64.encodeToString(iconByteArray, 0, iconByteArray.length, 0));
                }
                if (info.iconResource != null) {
                    json = json.key("iconResource").value(info.iconResource.resourceName).key(ICON_RESOURCE_PACKAGE_NAME_KEY).value(info.iconResource.packageName);
                }
                JSONStringer json2 = json.endObject();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                addToStringSet(sharedPrefs, editor, APPS_PENDING_INSTALL, json2.toString());
                editor.commit();
            } catch (JSONException e) {
                Log.d("InstallShortcutReceiver", "Exception when adding shortcut: " + e);
            }
        }
    }

    private static ArrayList<PendingInstallShortcutInfo> getAndClearInstallQueue(SharedPreferences sharedPrefs) {
        ArrayList<PendingInstallShortcutInfo> infos;
        synchronized (sLock) {
            Set<String> strings = sharedPrefs.getStringSet(APPS_PENDING_INSTALL, (Set) null);
            if (strings == null) {
                infos = new ArrayList<>();
            } else {
                infos = new ArrayList<>();
                for (String json : strings) {
                    try {
                        JSONObject object = (JSONObject) new JSONTokener(json).nextValue();
                        Intent data = Intent.parseUri(object.getString(DATA_INTENT_KEY), 0);
                        Intent launchIntent = Intent.parseUri(object.getString(LAUNCH_INTENT_KEY), 0);
                        String name = object.getString(NAME_KEY);
                        String iconBase64 = object.optString("icon");
                        String iconResourceName = object.optString("iconResource");
                        String iconResourcePackageName = object.optString(ICON_RESOURCE_PACKAGE_NAME_KEY);
                        if (iconBase64 != null && !iconBase64.isEmpty()) {
                            byte[] iconArray = Base64.decode(iconBase64, 0);
                            data.putExtra("android.intent.extra.shortcut.ICON", BitmapFactory.decodeByteArray(iconArray, 0, iconArray.length));
                        } else if (iconResourceName != null) {
                            if (!iconResourceName.isEmpty()) {
                                Intent.ShortcutIconResource iconResource = new Intent.ShortcutIconResource();
                                iconResource.resourceName = iconResourceName;
                                iconResource.packageName = iconResourcePackageName;
                                data.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", iconResource);
                            }
                        }
                        data.putExtra("android.intent.extra.shortcut.INTENT", launchIntent);
                        infos.add(new PendingInstallShortcutInfo(data, name, launchIntent));
                    } catch (JSONException e) {
                        Log.d("InstallShortcutReceiver", "Exception reading shortcut to add: " + e);
                    } catch (URISyntaxException e2) {
                        Log.d("InstallShortcutReceiver", "Exception reading shortcut to add: " + e2);
                    }
                }
                sharedPrefs.edit().putStringSet(APPS_PENDING_INSTALL, new HashSet()).commit();
            }
        }
        return infos;
    }

    private static class PendingInstallShortcutInfo {
        Intent data;
        Bitmap icon;
        Intent.ShortcutIconResource iconResource;
        Intent launchIntent;
        String name;

        public PendingInstallShortcutInfo(Intent rawData, String shortcutName, Intent shortcutIntent) {
            this.data = rawData;
            this.name = shortcutName;
            this.launchIntent = shortcutIntent;
        }
    }

    public void onReceive(Context context, Intent data) {
        Intent intent;
        boolean launcherNotLoaded;
        if (ACTION_INSTALL_SHORTCUT.equals(data.getAction()) && (intent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT")) != null) {
            String name = data.getStringExtra("android.intent.extra.shortcut.NAME");
            if (name == null) {
                try {
                    PackageManager pm = context.getPackageManager();
                    name = pm.getActivityInfo(intent.getComponent(), 0).loadLabel(pm).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    return;
                }
            }
            Bitmap icon = (Bitmap) data.getParcelableExtra("android.intent.extra.shortcut.ICON");
            Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
            if (LauncherModel.getCellCountX() <= 0 || LauncherModel.getCellCountY() <= 0) {
                launcherNotLoaded = true;
            } else {
                launcherNotLoaded = false;
            }
            PendingInstallShortcutInfo info = new PendingInstallShortcutInfo(data, name, intent);
            info.icon = icon;
            info.iconResource = iconResource;
            if (mUseInstallQueue || launcherNotLoaded) {
                addToInstallQueue(context.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0), info);
            } else {
                processInstallShortcut(context, info);
            }
        }
    }

    static void enableInstallQueue() {
        mUseInstallQueue = true;
    }

    static void disableAndFlushInstallQueue(Context context) {
        mUseInstallQueue = false;
        flushInstallQueue(context);
    }

    static void flushInstallQueue(Context context) {
        Iterator<PendingInstallShortcutInfo> iter = getAndClearInstallQueue(context.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0)).iterator();
        while (iter.hasNext()) {
            processInstallShortcut(context, iter.next());
        }
    }

    private static void processInstallShortcut(Context context, PendingInstallShortcutInfo pendingInfo) {
        SharedPreferences sp = context.getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0);
        Intent data = pendingInfo.data;
        Intent intent = pendingInfo.launchIntent;
        String name = pendingInfo.name;
        LauncherApplication app = (LauncherApplication) context.getApplicationContext();
        int[] result = {0};
        boolean found = false;
        synchronized (app) {
            app.getModel().flushWorkerThread();
            ArrayList<ItemInfo> items = LauncherModel.getItemsInLocalCoordinates(context);
            boolean exists = LauncherModel.shortcutExists(context, name, intent);
            for (int i = 0; i < 11 && !found; i++) {
                int si = ((i % 2 == 1 ? 1 : -1) * ((int) ((((float) i) / 2.0f) + 0.5f))) + 2;
                if (si >= 0 && si < 5) {
                    found = installShortcut(context, data, items, name, intent, si, exists, sp, result);
                }
            }
        }
        if (found) {
            return;
        }
        if (result[0] == -2) {
            Toast.makeText(context, context.getString(R.string.completely_out_of_space), 0).show();
        } else if (result[0] == -1) {
            Toast.makeText(context, context.getString(R.string.shortcut_duplicate, new Object[]{name}), 0).show();
        }
    }

    private static boolean installShortcut(Context context, Intent data, ArrayList<ItemInfo> items, String name, Intent intent, int screen, boolean shortcutExists, SharedPreferences sharedPrefs, int[] result) {
        int[] tmpCoordinates = new int[2];
        if (!findEmptyCell(context, items, tmpCoordinates, screen)) {
            result[0] = -2;
        } else if (intent != null) {
            if (intent.getAction() == null) {
                intent.setAction("android.intent.action.VIEW");
            } else if (intent.getAction().equals("android.intent.action.MAIN") && intent.getCategories() != null && intent.getCategories().contains("android.intent.category.LAUNCHER")) {
                intent.addFlags(270532608);
            }
            if (data.getBooleanExtra("duplicate", true) || !shortcutExists) {
                final SharedPreferences sharedPreferences = sharedPrefs;
                final int i = screen;
                final Intent intent2 = intent;
                new Thread("setNewAppsThread") {
                    public void run() {
                        synchronized (InstallShortcutReceiver.sLock) {
                            int newAppsScreen = sharedPreferences.getInt(InstallShortcutReceiver.NEW_APPS_PAGE_KEY, i);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            if (newAppsScreen == -1 || newAppsScreen == i) {
                                InstallShortcutReceiver.addToStringSet(sharedPreferences, editor, InstallShortcutReceiver.NEW_APPS_LIST_KEY, intent2.toUri(0));
                            }
                            editor.putInt(InstallShortcutReceiver.NEW_APPS_PAGE_KEY, i);
                            editor.commit();
                        }
                    }
                }.start();
                if (((LauncherApplication) context.getApplicationContext()).getModel().addShortcut(context, data, -100, screen, tmpCoordinates[0], tmpCoordinates[1], true) == null) {
                    return false;
                }
            } else {
                result[0] = -1;
            }
            return true;
        }
        return false;
    }

    private static boolean findEmptyCell(Context context, ArrayList<ItemInfo> items, int[] xy, int screen) {
        int xCount = LauncherModel.getCellCountX();
        int yCount = LauncherModel.getCellCountY();
        boolean[][] occupied = (boolean[][]) Array.newInstance(Boolean.TYPE, new int[]{xCount, yCount});
        for (int i = 0; i < items.size(); i++) {
            ItemInfo item = items.get(i);
            if (item.container == -100 && item.screen == screen) {
                int cellX = item.cellX;
                int cellY = item.cellY;
                int spanX = item.spanX;
                int spanY = item.spanY;
                int x = cellX;
                while (x >= 0 && x < cellX + spanX && x < xCount) {
                    int y = cellY;
                    while (y >= 0 && y < cellY + spanY && y < yCount) {
                        occupied[x][y] = true;
                        y++;
                    }
                    x++;
                }
            }
        }
        return CellLayout.findVacantCell(xy, 1, 1, xCount, yCount, occupied);
    }
}
