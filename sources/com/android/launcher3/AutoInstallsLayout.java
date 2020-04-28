package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.graphics.LauncherIcons;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParserException;

public class AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    static final String ACTION_LAUNCHER_CUSTOMIZATION = "android.autoinstalls.config.action.PLAY_AUTO_INSTALL";
    private static final String ATTR_CLASS_NAME = "className";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_KEY = "key";
    private static final String ATTR_PACKAGE_NAME = "packageName";
    private static final String ATTR_RANK = "rank";
    private static final String ATTR_SCREEN = "screen";
    private static final String ATTR_SPAN_X = "spanX";
    private static final String ATTR_SPAN_Y = "spanY";
    private static final String ATTR_TITLE = "title";
    private static final String ATTR_URL = "url";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_WORKSPACE = "workspace";
    private static final String ATTR_X = "x";
    private static final String ATTR_Y = "y";
    private static final String FORMATTED_LAYOUT_RES = "default_layout_%dx%d";
    private static final String FORMATTED_LAYOUT_RES_WITH_HOSTEAT = "default_layout_%dx%d_h%s";
    private static final String HOTSEAT_CONTAINER_NAME = LauncherSettings.Favorites.containerToString(LauncherSettings.Favorites.CONTAINER_HOTSEAT);
    private static final String LAYOUT_RES = "default_layout";
    private static final boolean LOGD = false;
    private static final String TAG = "AutoInstalls";
    private static final String TAG_APPWIDGET = "appwidget";
    private static final String TAG_APP_ICON = "appicon";
    private static final String TAG_AUTO_INSTALL = "autoinstall";
    private static final String TAG_EXTRA = "extra";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_INCLUDE = "include";
    private static final String TAG_SHORTCUT = "shortcut";
    private static final String TAG_WORKSPACE = "workspace";
    final AppWidgetHost mAppWidgetHost;
    protected final LayoutParserCallback mCallback;
    private final int mColumnCount;
    final Context mContext;
    protected SQLiteDatabase mDb;
    private final InvariantDeviceProfile mIdp;
    protected final int mLayoutId;
    protected final PackageManager mPackageManager;
    protected final String mRootTag;
    private final int mRowCount;
    protected final Resources mSourceRes;
    private final long[] mTemp = new long[2];
    final ContentValues mValues;

    public interface LayoutParserCallback {
        long generateNewItemId();

        long insertAndCheck(SQLiteDatabase sQLiteDatabase, ContentValues contentValues);
    }

    protected interface TagParser {
        long parseAndAdd(XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException;
    }

    static AutoInstallsLayout get(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback callback) {
        Pair<String, Resources> customizationApkInfo = Utilities.findSystemApk(ACTION_LAUNCHER_CUSTOMIZATION, context.getPackageManager());
        if (customizationApkInfo == null) {
            return null;
        }
        return get(context, (String) customizationApkInfo.first, (Resources) customizationApkInfo.second, appWidgetHost, callback);
    }

    static AutoInstallsLayout get(Context context, String pkg, Resources targetRes, AppWidgetHost appWidgetHost, LayoutParserCallback callback) {
        InvariantDeviceProfile grid = LauncherAppState.getIDP(context);
        String layoutName = String.format(Locale.ENGLISH, FORMATTED_LAYOUT_RES_WITH_HOSTEAT, new Object[]{Integer.valueOf(grid.numColumns), Integer.valueOf(grid.numRows), Integer.valueOf(grid.numHotseatIcons)});
        int layoutId = targetRes.getIdentifier(layoutName, "xml", pkg);
        if (layoutId == 0) {
            Log.d(TAG, "Formatted layout: " + layoutName + " not found. Trying layout without hosteat");
            layoutName = String.format(Locale.ENGLISH, FORMATTED_LAYOUT_RES, new Object[]{Integer.valueOf(grid.numColumns), Integer.valueOf(grid.numRows)});
            layoutId = targetRes.getIdentifier(layoutName, "xml", pkg);
        }
        if (layoutId == 0) {
            Log.d(TAG, "Formatted layout: " + layoutName + " not found. Trying the default layout");
            layoutId = targetRes.getIdentifier(LAYOUT_RES, "xml", pkg);
        }
        if (layoutId != 0) {
            return new AutoInstallsLayout(context, appWidgetHost, callback, targetRes, layoutId, "workspace");
        }
        Log.e(TAG, "Layout definition not found in package: " + pkg);
        return null;
    }

    public AutoInstallsLayout(Context context, AppWidgetHost appWidgetHost, LayoutParserCallback callback, Resources res, int layoutId, String rootTag) {
        this.mContext = context;
        this.mAppWidgetHost = appWidgetHost;
        this.mCallback = callback;
        this.mPackageManager = context.getPackageManager();
        this.mValues = new ContentValues();
        this.mRootTag = rootTag;
        this.mSourceRes = res;
        this.mLayoutId = layoutId;
        this.mIdp = LauncherAppState.getIDP(context);
        this.mRowCount = this.mIdp.numRows;
        this.mColumnCount = this.mIdp.numColumns;
    }

    public int loadLayout(SQLiteDatabase db, ArrayList<Long> screenIds) {
        this.mDb = db;
        try {
            return parseLayout(this.mLayoutId, screenIds);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing layout: " + e);
            return -1;
        }
    }

    /* access modifiers changed from: protected */
    public int parseLayout(int layoutId, ArrayList<Long> screenIds) throws XmlPullParserException, IOException {
        XmlResourceParser parser = this.mSourceRes.getXml(layoutId);
        beginDocument(parser, this.mRootTag);
        int depth = parser.getDepth();
        ArrayMap<String, TagParser> tagParserMap = getLayoutElementsMap();
        int count = 0;
        while (true) {
            int next = parser.next();
            int type = next;
            if ((next != 3 || parser.getDepth() > depth) && type != 1) {
                if (type == 2) {
                    count += parseAndAddNode(parser, tagParserMap, screenIds);
                }
            }
        }
        return count;
    }

    /* access modifiers changed from: protected */
    public void parseContainerAndScreen(XmlResourceParser parser, long[] out) {
        if (HOTSEAT_CONTAINER_NAME.equals(getAttributeValue(parser, "container"))) {
            out[0] = -101;
            out[1] = Long.parseLong(getAttributeValue(parser, "rank"));
            return;
        }
        out[0] = -100;
        out[1] = Long.parseLong(getAttributeValue(parser, "screen"));
    }

    /* access modifiers changed from: protected */
    public int parseAndAddNode(XmlResourceParser parser, ArrayMap<String, TagParser> tagParserMap, ArrayList<Long> screenIds) throws XmlPullParserException, IOException {
        if (TAG_INCLUDE.equals(parser.getName())) {
            int resId = getAttributeResourceValue(parser, "workspace", 0);
            if (resId != 0) {
                return parseLayout(resId, screenIds);
            }
            return 0;
        }
        this.mValues.clear();
        parseContainerAndScreen(parser, this.mTemp);
        long container = this.mTemp[0];
        long screenId = this.mTemp[1];
        this.mValues.put("container", Long.valueOf(container));
        this.mValues.put("screen", Long.valueOf(screenId));
        this.mValues.put(LauncherSettings.Favorites.CELLX, convertToDistanceFromEnd(getAttributeValue(parser, ATTR_X), this.mColumnCount));
        this.mValues.put(LauncherSettings.Favorites.CELLY, convertToDistanceFromEnd(getAttributeValue(parser, ATTR_Y), this.mRowCount));
        TagParser tagParser = tagParserMap.get(parser.getName());
        if (tagParser == null || tagParser.parseAndAdd(parser) < 0) {
            return 0;
        }
        if (!screenIds.contains(Long.valueOf(screenId)) && container == -100) {
            screenIds.add(Long.valueOf(screenId));
        }
        return 1;
    }

    /* access modifiers changed from: protected */
    public long addShortcut(String title, Intent intent, int type) {
        long id = this.mCallback.generateNewItemId();
        this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, intent.toUri(0));
        this.mValues.put("title", title);
        this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(type));
        this.mValues.put("spanX", 1);
        this.mValues.put("spanY", 1);
        this.mValues.put("_id", Long.valueOf(id));
        if (this.mCallback.insertAndCheck(this.mDb, this.mValues) < 0) {
            return -1;
        }
        return id;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getFolderElementsMap() {
        ArrayMap<String, TagParser> parsers = new ArrayMap<>();
        parsers.put(TAG_APP_ICON, new AppShortcutParser());
        parsers.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        parsers.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return parsers;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, TagParser> getLayoutElementsMap() {
        ArrayMap<String, TagParser> parsers = new ArrayMap<>();
        parsers.put(TAG_APP_ICON, new AppShortcutParser());
        parsers.put(TAG_AUTO_INSTALL, new AutoInstallParser());
        parsers.put(TAG_FOLDER, new FolderParser(this));
        parsers.put(TAG_APPWIDGET, new PendingWidgetParser());
        parsers.put(TAG_SHORTCUT, new ShortcutParser(this.mSourceRes));
        return parsers;
    }

    protected class AppShortcutParser implements TagParser {
        protected AppShortcutParser() {
        }

        public long parseAndAdd(XmlResourceParser parser) {
            ActivityInfo info;
            ComponentName cn;
            String packageName = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String className = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
                return invalidPackageOrClass(parser);
            }
            try {
                cn = new ComponentName(packageName, className);
                info = AutoInstallsLayout.this.mPackageManager.getActivityInfo(cn, 0);
            } catch (PackageManager.NameNotFoundException e) {
                try {
                    ComponentName cn2 = new ComponentName(AutoInstallsLayout.this.mPackageManager.currentToCanonicalPackageNames(new String[]{packageName})[0], className);
                    cn = cn2;
                    info = AutoInstallsLayout.this.mPackageManager.getActivityInfo(cn2, 0);
                } catch (PackageManager.NameNotFoundException e2) {
                    Log.e(AutoInstallsLayout.TAG, "Favorite not found: " + packageName + "/" + className);
                    return -1;
                }
            }
            return AutoInstallsLayout.this.addShortcut(info.loadLabel(AutoInstallsLayout.this.mPackageManager).toString(), new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER").setComponent(cn).setFlags(270532608), 0);
        }

        /* access modifiers changed from: protected */
        public long invalidPackageOrClass(XmlResourceParser parser) {
            Log.w(AutoInstallsLayout.TAG, "Skipping invalid <favorite> with no component");
            return -1;
        }
    }

    protected class AutoInstallParser implements TagParser {
        protected AutoInstallParser() {
        }

        public long parseAndAdd(XmlResourceParser parser) {
            String packageName = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String className = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, 2);
            return AutoInstallsLayout.this.addShortcut(AutoInstallsLayout.this.mContext.getString(R.string.package_state_unknown), new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER").setComponent(new ComponentName(packageName, className)).setFlags(270532608), 0);
        }
    }

    protected class ShortcutParser implements TagParser {
        private final Resources mIconRes;

        public ShortcutParser(Resources iconRes) {
            this.mIconRes = iconRes;
        }

        public long parseAndAdd(XmlResourceParser parser) {
            Intent intent;
            Drawable icon;
            int titleResId = AutoInstallsLayout.getAttributeResourceValue(parser, "title", 0);
            int iconId = AutoInstallsLayout.getAttributeResourceValue(parser, "icon", 0);
            if (titleResId == 0 || iconId == 0 || (intent = parseIntent(parser)) == null || (icon = this.mIconRes.getDrawable(iconId)) == null) {
                return -1;
            }
            LauncherIcons li = LauncherIcons.obtain(AutoInstallsLayout.this.mContext);
            AutoInstallsLayout.this.mValues.put("icon", Utilities.flattenBitmap(li.createBadgedIconBitmap(icon, Process.myUserHandle(), Build.VERSION.SDK_INT).icon));
            li.recycle();
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.mIconRes.getResourcePackageName(iconId));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.mIconRes.getResourceName(iconId));
            intent.setFlags(270532608);
            return AutoInstallsLayout.this.addShortcut(AutoInstallsLayout.this.mSourceRes.getString(titleResId), intent, 1);
        }

        /* access modifiers changed from: protected */
        public Intent parseIntent(XmlResourceParser parser) {
            String url = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_URL);
            if (TextUtils.isEmpty(url) || !Patterns.WEB_URL.matcher(url).matches()) {
                return null;
            }
            return new Intent("android.intent.action.VIEW", (Uri) null).setData(Uri.parse(url));
        }
    }

    protected class PendingWidgetParser implements TagParser {
        protected PendingWidgetParser() {
        }

        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            String packageName = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_PACKAGE_NAME);
            String className = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_CLASS_NAME);
            if (TextUtils.isEmpty(packageName) || TextUtils.isEmpty(className)) {
                return -1;
            }
            AutoInstallsLayout.this.mValues.put("spanX", AutoInstallsLayout.getAttributeValue(parser, "spanX"));
            AutoInstallsLayout.this.mValues.put("spanY", AutoInstallsLayout.getAttributeValue(parser, "spanY"));
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 4);
            Bundle extras = new Bundle();
            int widgetDepth = parser.getDepth();
            while (true) {
                int next = parser.next();
                int type = next;
                if (next == 3 && parser.getDepth() <= widgetDepth) {
                    return verifyAndInsert(new ComponentName(packageName, className), extras);
                }
                if (type == 2) {
                    if (AutoInstallsLayout.TAG_EXTRA.equals(parser.getName())) {
                        String key = AutoInstallsLayout.getAttributeValue(parser, AutoInstallsLayout.ATTR_KEY);
                        String value = AutoInstallsLayout.getAttributeValue(parser, "value");
                        if (key != null && value != null) {
                            extras.putString(key, value);
                        }
                    } else {
                        throw new RuntimeException("Widgets can contain only extras");
                    }
                }
            }
            throw new RuntimeException("Widget extras must have a key and value");
        }

        /* access modifiers changed from: protected */
        public long verifyAndInsert(ComponentName cn, Bundle extras) {
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, cn.flattenToString());
            AutoInstallsLayout.this.mValues.put(LauncherSettings.Favorites.RESTORED, 35);
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            if (!extras.isEmpty()) {
                AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.INTENT, new Intent().putExtras(extras).toUri(0));
            }
            long insertedId = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (insertedId < 0) {
                return -1;
            }
            return insertedId;
        }
    }

    protected class FolderParser implements TagParser {
        private final ArrayMap<String, TagParser> mFolderElements;

        public FolderParser(AutoInstallsLayout this$02) {
            this(this$02.getFolderElementsMap());
        }

        public FolderParser(ArrayMap<String, TagParser> elements) {
            this.mFolderElements = elements;
        }

        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            String title;
            String title2;
            int titleResId;
            String title3;
            XmlResourceParser xmlResourceParser = parser;
            int type = AutoInstallsLayout.getAttributeResourceValue(xmlResourceParser, "title", 0);
            if (type != 0) {
                title = AutoInstallsLayout.this.mSourceRes.getString(type);
            } else {
                title = AutoInstallsLayout.this.mContext.getResources().getString(R.string.folder_name);
            }
            AutoInstallsLayout.this.mValues.put("title", title);
            AutoInstallsLayout.this.mValues.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, 2);
            AutoInstallsLayout.this.mValues.put("spanX", 1);
            AutoInstallsLayout.this.mValues.put("spanY", 1);
            AutoInstallsLayout.this.mValues.put("_id", Long.valueOf(AutoInstallsLayout.this.mCallback.generateNewItemId()));
            long folderId = AutoInstallsLayout.this.mCallback.insertAndCheck(AutoInstallsLayout.this.mDb, AutoInstallsLayout.this.mValues);
            if (folderId < 0) {
                return -1;
            }
            ContentValues myValues = new ContentValues(AutoInstallsLayout.this.mValues);
            ArrayList<Long> folderItems = new ArrayList<>();
            int folderDepth = parser.getDepth();
            int rank = 0;
            while (true) {
                int next = parser.next();
                int type2 = next;
                if (next != 3) {
                    titleResId = type;
                    title2 = title;
                } else if (parser.getDepth() > folderDepth) {
                    titleResId = type;
                    title2 = title;
                } else {
                    long addedId = folderId;
                    if (folderItems.size() < 2) {
                        Uri uri = LauncherSettings.Favorites.getContentUri(folderId);
                        LauncherProvider.SqlArguments args = new LauncherProvider.SqlArguments(uri, (String) null, (String[]) null);
                        int i = type;
                        String str = title;
                        AutoInstallsLayout.this.mDb.delete(args.table, args.where, args.args);
                        if (folderItems.size() != 1) {
                            return -1;
                        }
                        ContentValues childValues = new ContentValues();
                        AutoInstallsLayout.copyInteger(myValues, childValues, "container");
                        AutoInstallsLayout.copyInteger(myValues, childValues, "screen");
                        AutoInstallsLayout.copyInteger(myValues, childValues, LauncherSettings.Favorites.CELLX);
                        AutoInstallsLayout.copyInteger(myValues, childValues, LauncherSettings.Favorites.CELLY);
                        long addedId2 = folderItems.get(0).longValue();
                        SQLiteDatabase sQLiteDatabase = AutoInstallsLayout.this.mDb;
                        StringBuilder sb = new StringBuilder();
                        Uri uri2 = uri;
                        sb.append("_id=");
                        sb.append(addedId2);
                        sQLiteDatabase.update(LauncherSettings.Favorites.TABLE_NAME, childValues, sb.toString(), (String[]) null);
                        return addedId2;
                    }
                    int titleResId2 = type;
                    String str2 = title;
                    return addedId;
                }
                if (type2 != 2) {
                    type = titleResId;
                    title3 = title2;
                } else {
                    AutoInstallsLayout.this.mValues.clear();
                    AutoInstallsLayout.this.mValues.put("container", Long.valueOf(folderId));
                    AutoInstallsLayout.this.mValues.put("rank", Integer.valueOf(rank));
                    TagParser tagParser = this.mFolderElements.get(parser.getName());
                    if (tagParser != null) {
                        long id = tagParser.parseAndAdd(xmlResourceParser);
                        if (id >= 0) {
                            folderItems.add(Long.valueOf(id));
                            rank++;
                        }
                        type = titleResId;
                        title3 = title2;
                    } else {
                        throw new RuntimeException("Invalid folder item " + parser.getName());
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:0:0x0000 A[LOOP_START, MTH_ENTER_BLOCK] */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:5:0x000e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void beginDocument(org.xmlpull.v1.XmlPullParser r4, java.lang.String r5) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
        L_0x0000:
            int r0 = r4.next()
            r1 = r0
            r2 = 2
            if (r0 == r2) goto L_0x000c
            r0 = 1
            if (r1 == r0) goto L_0x000c
            goto L_0x0000
        L_0x000c:
            if (r1 != r2) goto L_0x003c
            java.lang.String r0 = r4.getName()
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x0019
            return
        L_0x0019:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unexpected start tag: found "
            r2.append(r3)
            java.lang.String r3 = r4.getName()
            r2.append(r3)
            java.lang.String r3 = ", expected "
            r2.append(r3)
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r0.<init>(r2)
            throw r0
        L_0x003c:
            org.xmlpull.v1.XmlPullParserException r0 = new org.xmlpull.v1.XmlPullParserException
            java.lang.String r2 = "No start tag found"
            r0.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.AutoInstallsLayout.beginDocument(org.xmlpull.v1.XmlPullParser, java.lang.String):void");
    }

    private static String convertToDistanceFromEnd(String value, int endValue) {
        int x;
        if (TextUtils.isEmpty(value) || (x = Integer.parseInt(value)) >= 0) {
            return value;
        }
        return Integer.toString(endValue + x);
    }

    protected static String getAttributeValue(XmlResourceParser parser, String attribute) {
        String value = parser.getAttributeValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", attribute);
        if (value == null) {
            return parser.getAttributeValue((String) null, attribute);
        }
        return value;
    }

    protected static int getAttributeResourceValue(XmlResourceParser parser, String attribute, int defaultValue) {
        int value = parser.getAttributeResourceValue("http://schemas.android.com/apk/res-auto/com.android.launcher3", attribute, defaultValue);
        if (value == defaultValue) {
            return parser.getAttributeResourceValue((String) null, attribute, defaultValue);
        }
        return value;
    }

    static void copyInteger(ContentValues from, ContentValues to, String key) {
        to.put(key, from.getAsInteger(key));
    }
}
