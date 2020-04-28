package com.android.launcher3;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.graphics.BitmapInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Preconditions;
import com.android.launcher3.util.Provider;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

public class InstallShortcutReceiver extends BroadcastReceiver {
    private static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
    private static final String APPS_PENDING_INSTALL = "apps_to_install";
    private static final String APP_SHORTCUT_TYPE_KEY = "isAppShortcut";
    private static final String APP_WIDGET_TYPE_KEY = "isAppWidget";
    private static final boolean DBG = false;
    private static final String DEEPSHORTCUT_TYPE_KEY = "isDeepShortcut";
    public static final int FLAG_ACTIVITY_PAUSED = 1;
    public static final int FLAG_BULK_ADD = 4;
    public static final int FLAG_DRAG_AND_DROP = 4;
    public static final int FLAG_LOADER_RUNNING = 2;
    private static final String ICON_KEY = "icon";
    private static final String ICON_RESOURCE_NAME_KEY = "iconResource";
    private static final String ICON_RESOURCE_PACKAGE_NAME_KEY = "iconResourcePackage";
    private static final String LAUNCH_INTENT_KEY = "intent.launch";
    private static final int MSG_ADD_TO_QUEUE = 1;
    private static final int MSG_FLUSH_QUEUE = 2;
    private static final String NAME_KEY = "name";
    public static final int NEW_SHORTCUT_BOUNCE_DURATION = 450;
    public static final int NEW_SHORTCUT_STAGGER_DELAY = 85;
    private static final String TAG = "InstallShortcutReceiver";
    private static final String USER_HANDLE_KEY = "userHandle";
    private static final Handler sHandler = new Handler(LauncherModel.getWorkerLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Pair<Context, PendingInstallShortcutInfo> pair = (Pair) msg.obj;
                    String encoded = ((PendingInstallShortcutInfo) pair.second).encodeToString();
                    SharedPreferences prefs = Utilities.getPrefs((Context) pair.first);
                    Set<String> strings = prefs.getStringSet(InstallShortcutReceiver.APPS_PENDING_INSTALL, (Set) null);
                    Set<String> strings2 = strings != null ? new HashSet<>(strings) : new HashSet<>(1);
                    strings2.add(encoded);
                    prefs.edit().putStringSet(InstallShortcutReceiver.APPS_PENDING_INSTALL, strings2).apply();
                    return;
                case 2:
                    Context context = (Context) msg.obj;
                    LauncherModel model = LauncherAppState.getInstance(context).getModel();
                    if (model.getCallback() != null) {
                        ArrayList<Pair<ItemInfo, Object>> installQueue = new ArrayList<>();
                        SharedPreferences prefs2 = Utilities.getPrefs(context);
                        Set<String> strings3 = prefs2.getStringSet(InstallShortcutReceiver.APPS_PENDING_INSTALL, (Set) null);
                        if (strings3 != null) {
                            LauncherAppsCompat launcherApps = LauncherAppsCompat.getInstance(context);
                            for (String encoded2 : strings3) {
                                PendingInstallShortcutInfo info = InstallShortcutReceiver.decode(encoded2, context);
                                if (info != null) {
                                    String pkg = InstallShortcutReceiver.getIntentPackage(info.launchIntent);
                                    if (TextUtils.isEmpty(pkg) || launcherApps.isPackageEnabledForProfile(pkg, info.user)) {
                                        installQueue.add(info.getItemInfo());
                                    }
                                }
                            }
                            prefs2.edit().remove(InstallShortcutReceiver.APPS_PENDING_INSTALL).apply();
                            if (!installQueue.isEmpty()) {
                                model.addAndBindAddedWorkspaceItems(installQueue);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private static int sInstallQueueDisabledFlags = 0;

    public static void removeFromInstallQueue(Context context, HashSet<String> packageNames, UserHandle user) {
        if (!packageNames.isEmpty()) {
            Preconditions.assertWorkerThread();
            SharedPreferences sp = Utilities.getPrefs(context);
            Set<String> strings = sp.getStringSet(APPS_PENDING_INSTALL, (Set) null);
            if (!Utilities.isEmpty(strings)) {
                Set<String> newStrings = new HashSet<>(strings);
                Iterator<String> newStringsIter = newStrings.iterator();
                while (newStringsIter.hasNext()) {
                    try {
                        Decoder decoder = new Decoder(newStringsIter.next(), context);
                        if (packageNames.contains(getIntentPackage(decoder.launcherIntent)) && user.equals(decoder.user)) {
                            newStringsIter.remove();
                        }
                    } catch (URISyntaxException | JSONException e) {
                        Log.d(TAG, "Exception reading shortcut to add: " + e);
                        newStringsIter.remove();
                    }
                }
                sp.edit().putStringSet(APPS_PENDING_INSTALL, newStrings).apply();
            }
        }
    }

    public void onReceive(Context context, Intent data) {
        PendingInstallShortcutInfo info;
        if (!ACTION_INSTALL_SHORTCUT.equals(data.getAction()) || (info = createPendingInfo(context, data)) == null) {
            return;
        }
        if (info.isLauncherActivity() || new PackageManagerHelper(context).hasPermissionForActivity(info.launchIntent, (String) null)) {
            queuePendingShortcutInfo(info, context);
            return;
        }
        Log.e(TAG, "Ignoring malicious intent " + info.launchIntent.toUri(0));
    }

    private static boolean isValidExtraType(Intent intent, String key, Class type) {
        Object extra = intent.getParcelableExtra(key);
        return extra == null || type.isInstance(extra);
    }

    private static PendingInstallShortcutInfo createPendingInfo(Context context, Intent data) {
        if (!isValidExtraType(data, "android.intent.extra.shortcut.INTENT", Intent.class) || !isValidExtraType(data, "android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.class) || !isValidExtraType(data, "android.intent.extra.shortcut.ICON", Bitmap.class)) {
            return null;
        }
        PendingInstallShortcutInfo info = new PendingInstallShortcutInfo(data, Process.myUserHandle(), context);
        if (info.launchIntent == null || info.label == null) {
            return null;
        }
        return convertToLauncherActivityIfPossible(info);
    }

    public static ShortcutInfo fromShortcutIntent(Context context, Intent data) {
        PendingInstallShortcutInfo info = createPendingInfo(context, data);
        if (info == null) {
            return null;
        }
        return (ShortcutInfo) info.getItemInfo().first;
    }

    public static ShortcutInfo fromActivityInfo(LauncherActivityInfo info, Context context) {
        return (ShortcutInfo) new PendingInstallShortcutInfo(info, context).getItemInfo().first;
    }

    public static void queueShortcut(ShortcutInfoCompat info, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(info, context), context);
    }

    public static void queueWidget(AppWidgetProviderInfo info, int widgetId, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(info, widgetId, context), context);
    }

    public static void queueActivityInfo(LauncherActivityInfo activity, Context context) {
        queuePendingShortcutInfo(new PendingInstallShortcutInfo(activity, context), context);
    }

    public static HashSet<ShortcutKey> getPendingShortcuts(Context context) {
        HashSet<ShortcutKey> result = new HashSet<>();
        Set<String> strings = Utilities.getPrefs(context).getStringSet(APPS_PENDING_INSTALL, (Set) null);
        if (Utilities.isEmpty(strings)) {
            return result;
        }
        for (String encoded : strings) {
            try {
                Decoder decoder = new Decoder(encoded, context);
                if (decoder.optBoolean(DEEPSHORTCUT_TYPE_KEY)) {
                    result.add(ShortcutKey.fromIntent(decoder.launcherIntent, decoder.user));
                }
            } catch (URISyntaxException | JSONException e) {
                Log.d(TAG, "Exception reading shortcut to add: " + e);
            }
        }
        return result;
    }

    private static void queuePendingShortcutInfo(PendingInstallShortcutInfo info, Context context) {
        Message.obtain(sHandler, 1, Pair.create(context, info)).sendToTarget();
        flushInstallQueue(context);
    }

    public static void enableInstallQueue(int flag) {
        sInstallQueueDisabledFlags |= flag;
    }

    public static void disableAndFlushInstallQueue(int flag, Context context) {
        sInstallQueueDisabledFlags &= ~flag;
        flushInstallQueue(context);
    }

    static void flushInstallQueue(Context context) {
        if (sInstallQueueDisabledFlags == 0) {
            Message.obtain(sHandler, 2, context.getApplicationContext()).sendToTarget();
        }
    }

    static CharSequence ensureValidName(Context context, Intent intent, CharSequence name) {
        if (name != null) {
            return name;
        }
        try {
            PackageManager pm = context.getPackageManager();
            return pm.getActivityInfo(intent.getComponent(), 0).loadLabel(pm);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    private static class PendingInstallShortcutInfo {
        final LauncherActivityInfo activityInfo;
        final Intent data;
        final String label;
        final Intent launchIntent;
        final Context mContext;
        final AppWidgetProviderInfo providerInfo;
        final ShortcutInfoCompat shortcutInfo;
        final UserHandle user;

        public PendingInstallShortcutInfo(Intent data2, UserHandle user2, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = null;
            this.providerInfo = null;
            this.data = data2;
            this.user = user2;
            this.mContext = context;
            this.launchIntent = (Intent) data2.getParcelableExtra("android.intent.extra.shortcut.INTENT");
            this.label = data2.getStringExtra("android.intent.extra.shortcut.NAME");
        }

        public PendingInstallShortcutInfo(LauncherActivityInfo info, Context context) {
            this.activityInfo = info;
            this.shortcutInfo = null;
            this.providerInfo = null;
            this.data = null;
            this.user = info.getUser();
            this.mContext = context;
            this.launchIntent = AppInfo.makeLaunchIntent(info);
            this.label = info.getLabel().toString();
        }

        public PendingInstallShortcutInfo(ShortcutInfoCompat info, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = info;
            this.providerInfo = null;
            this.data = null;
            this.mContext = context;
            this.user = info.getUserHandle();
            this.launchIntent = info.makeIntent();
            this.label = info.getShortLabel().toString();
        }

        public PendingInstallShortcutInfo(AppWidgetProviderInfo info, int widgetId, Context context) {
            this.activityInfo = null;
            this.shortcutInfo = null;
            this.providerInfo = info;
            this.data = null;
            this.mContext = context;
            this.user = info.getProfile();
            this.launchIntent = new Intent().setComponent(info.provider).putExtra(LauncherSettings.Favorites.APPWIDGET_ID, widgetId);
            this.label = info.label;
        }

        public String encodeToString() {
            try {
                if (this.activityInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.APP_SHORTCUT_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.shortcutInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.DEEPSHORTCUT_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.providerInfo != null) {
                    return new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.APP_WIDGET_TYPE_KEY).value(true).key(InstallShortcutReceiver.USER_HANDLE_KEY).value(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(this.user)).endObject().toString();
                }
                if (this.launchIntent.getAction() == null) {
                    this.launchIntent.setAction("android.intent.action.VIEW");
                } else if (this.launchIntent.getAction().equals("android.intent.action.MAIN") && this.launchIntent.getCategories() != null && this.launchIntent.getCategories().contains("android.intent.category.LAUNCHER")) {
                    this.launchIntent.addFlags(270532608);
                }
                String name = InstallShortcutReceiver.ensureValidName(this.mContext, this.launchIntent, this.label).toString();
                Bitmap icon = (Bitmap) this.data.getParcelableExtra("android.intent.extra.shortcut.ICON");
                Intent.ShortcutIconResource iconResource = (Intent.ShortcutIconResource) this.data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
                JSONStringer json = new JSONStringer().object().key(InstallShortcutReceiver.LAUNCH_INTENT_KEY).value(this.launchIntent.toUri(0)).key(InstallShortcutReceiver.NAME_KEY).value(name);
                if (icon != null) {
                    byte[] iconByteArray = Utilities.flattenBitmap(icon);
                    json = json.key("icon").value(Base64.encodeToString(iconByteArray, 0, iconByteArray.length, 0));
                }
                if (iconResource != null) {
                    json = json.key("iconResource").value(iconResource.resourceName).key(InstallShortcutReceiver.ICON_RESOURCE_PACKAGE_NAME_KEY).value(iconResource.packageName);
                }
                return json.endObject().toString();
            } catch (JSONException e) {
                Log.d(InstallShortcutReceiver.TAG, "Exception when adding shortcut: " + e);
                return null;
            }
        }

        public Pair<ItemInfo, Object> getItemInfo() {
            if (this.activityInfo != null) {
                AppInfo appInfo = new AppInfo(this.mContext, this.activityInfo, this.user);
                final LauncherAppState app = LauncherAppState.getInstance(this.mContext);
                appInfo.title = "";
                app.getIconCache().getDefaultIcon(this.user).applyTo((ItemInfoWithIcon) appInfo);
                final ShortcutInfo si = appInfo.makeShortcut();
                if (Looper.myLooper() == LauncherModel.getWorkerLooper()) {
                    app.getIconCache().getTitleAndIcon(si, this.activityInfo, false);
                } else {
                    app.getModel().updateAndBindShortcutInfo(new Provider<ShortcutInfo>() {
                        public ShortcutInfo get() {
                            app.getIconCache().getTitleAndIcon(si, PendingInstallShortcutInfo.this.activityInfo, false);
                            return si;
                        }
                    });
                }
                return Pair.create(si, this.activityInfo);
            } else if (this.shortcutInfo != null) {
                ShortcutInfo si2 = new ShortcutInfo(this.shortcutInfo, this.mContext);
                LauncherIcons li = LauncherIcons.obtain(this.mContext);
                li.createShortcutIcon(this.shortcutInfo).applyTo((ItemInfoWithIcon) si2);
                li.recycle();
                return Pair.create(si2, this.shortcutInfo);
            } else if (this.providerInfo == null) {
                return Pair.create(InstallShortcutReceiver.createShortcutInfo(this.data, LauncherAppState.getInstance(this.mContext)), (Object) null);
            } else {
                LauncherAppWidgetProviderInfo info = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mContext, this.providerInfo);
                LauncherAppWidgetInfo widgetInfo = new LauncherAppWidgetInfo(this.launchIntent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, 0), info.provider);
                InvariantDeviceProfile idp = LauncherAppState.getIDP(this.mContext);
                widgetInfo.minSpanX = info.minSpanX;
                widgetInfo.minSpanY = info.minSpanY;
                widgetInfo.spanX = Math.min(info.spanX, idp.numColumns);
                widgetInfo.spanY = Math.min(info.spanY, idp.numRows);
                return Pair.create(widgetInfo, this.providerInfo);
            }
        }

        public boolean isLauncherActivity() {
            return this.activityInfo != null;
        }
    }

    /* access modifiers changed from: private */
    public static String getIntentPackage(Intent intent) {
        return intent.getComponent() == null ? intent.getPackage() : intent.getComponent().getPackageName();
    }

    /* access modifiers changed from: private */
    public static PendingInstallShortcutInfo decode(String encoded, Context context) {
        try {
            Decoder decoder = new Decoder(encoded, context);
            if (decoder.optBoolean(APP_SHORTCUT_TYPE_KEY)) {
                LauncherActivityInfo info = LauncherAppsCompat.getInstance(context).resolveActivity(decoder.launcherIntent, decoder.user);
                if (info == null) {
                    return null;
                }
                return new PendingInstallShortcutInfo(info, context);
            } else if (decoder.optBoolean(DEEPSHORTCUT_TYPE_KEY)) {
                List<ShortcutInfoCompat> si = DeepShortcutManager.getInstance(context).queryForFullDetails(decoder.launcherIntent.getPackage(), Arrays.asList(new String[]{decoder.launcherIntent.getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID)}), decoder.user);
                if (si.isEmpty()) {
                    return null;
                }
                return new PendingInstallShortcutInfo(si.get(0), context);
            } else if (decoder.optBoolean(APP_WIDGET_TYPE_KEY)) {
                int widgetId = decoder.launcherIntent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, 0);
                AppWidgetProviderInfo info2 = AppWidgetManager.getInstance(context).getAppWidgetInfo(widgetId);
                if (info2 != null && info2.provider.equals(decoder.launcherIntent.getComponent())) {
                    if (info2.getProfile().equals(decoder.user)) {
                        return new PendingInstallShortcutInfo(info2, widgetId, context);
                    }
                }
                return null;
            } else {
                Intent data = new Intent();
                data.putExtra("android.intent.extra.shortcut.INTENT", decoder.launcherIntent);
                data.putExtra("android.intent.extra.shortcut.NAME", decoder.getString(NAME_KEY));
                String iconBase64 = decoder.optString("icon");
                String iconResourceName = decoder.optString("iconResource");
                String iconResourcePackageName = decoder.optString(ICON_RESOURCE_PACKAGE_NAME_KEY);
                if (iconBase64 != null && !iconBase64.isEmpty()) {
                    byte[] iconArray = Base64.decode(iconBase64, 0);
                    data.putExtra("android.intent.extra.shortcut.ICON", BitmapFactory.decodeByteArray(iconArray, 0, iconArray.length));
                } else if (iconResourceName != null && !iconResourceName.isEmpty()) {
                    Intent.ShortcutIconResource iconResource = new Intent.ShortcutIconResource();
                    iconResource.resourceName = iconResourceName;
                    iconResource.packageName = iconResourcePackageName;
                    data.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", iconResource);
                }
                return new PendingInstallShortcutInfo(data, decoder.user, context);
            }
        } catch (URISyntaxException | JSONException e) {
            Log.d(TAG, "Exception reading shortcut to add: " + e);
            return null;
        }
    }

    private static class Decoder extends JSONObject {
        public final Intent launcherIntent;
        public final UserHandle user;

        private Decoder(String encoded, Context context) throws JSONException, URISyntaxException {
            super(encoded);
            UserHandle userHandle;
            this.launcherIntent = Intent.parseUri(getString(InstallShortcutReceiver.LAUNCH_INTENT_KEY), 0);
            if (has(InstallShortcutReceiver.USER_HANDLE_KEY)) {
                userHandle = UserManagerCompat.getInstance(context).getUserForSerialNumber(getLong(InstallShortcutReceiver.USER_HANDLE_KEY));
            } else {
                userHandle = Process.myUserHandle();
            }
            this.user = userHandle;
            if (this.user == null) {
                throw new JSONException("Invalid user");
            }
        }
    }

    private static PendingInstallShortcutInfo convertToLauncherActivityIfPossible(PendingInstallShortcutInfo original) {
        LauncherActivityInfo info;
        if (!original.isLauncherActivity() && Utilities.isLauncherAppTarget(original.launchIntent) && (info = LauncherAppsCompat.getInstance(original.mContext).resolveActivity(original.launchIntent, original.user)) != null) {
            return new PendingInstallShortcutInfo(info, original.mContext);
        }
        return original;
    }

    /* access modifiers changed from: private */
    public static ShortcutInfo createShortcutInfo(Intent data, LauncherAppState app) {
        Intent intent = (Intent) data.getParcelableExtra("android.intent.extra.shortcut.INTENT");
        String name = data.getStringExtra("android.intent.extra.shortcut.NAME");
        Parcelable bitmap = data.getParcelableExtra("android.intent.extra.shortcut.ICON");
        if (intent == null) {
            Log.e(TAG, "Can't construct ShorcutInfo with null intent");
            return null;
        }
        ShortcutInfo info = new ShortcutInfo();
        info.user = Process.myUserHandle();
        BitmapInfo iconInfo = null;
        LauncherIcons li = LauncherIcons.obtain(app.getContext());
        if (bitmap instanceof Bitmap) {
            iconInfo = li.createIconBitmap((Bitmap) bitmap);
        } else {
            Parcelable extra = data.getParcelableExtra("android.intent.extra.shortcut.ICON_RESOURCE");
            if (extra instanceof Intent.ShortcutIconResource) {
                info.iconResource = (Intent.ShortcutIconResource) extra;
                iconInfo = li.createIconBitmap(info.iconResource);
            }
        }
        li.recycle();
        if (iconInfo == null) {
            iconInfo = app.getIconCache().getDefaultIcon(info.user);
        }
        iconInfo.applyTo((ItemInfoWithIcon) info);
        info.title = Utilities.trim(name);
        info.contentDescription = UserManagerCompat.getInstance(app.getContext()).getBadgedLabelForUser(info.title, info.user);
        info.intent = intent;
        return info;
    }
}
