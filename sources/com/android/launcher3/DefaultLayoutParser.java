package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.LauncherSettings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class DefaultLayoutParser extends AutoInstallsLayout {
    private static final String ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE = "com.android.launcher.action.APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE";
    private static final String ATTR_CONTAINER = "container";
    private static final String ATTR_FOLDER_ITEMS = "folderItems";
    private static final String ATTR_SCREEN = "screen";
    protected static final String ATTR_URI = "uri";
    private static final String TAG = "DefaultLayoutParser";
    private static final String TAG_APPWIDGET = "appwidget";
    protected static final String TAG_FAVORITE = "favorite";
    private static final String TAG_FAVORITES = "favorites";
    private static final String TAG_FOLDER = "folder";
    private static final String TAG_PARTNER_FOLDER = "partner-folder";
    protected static final String TAG_RESOLVE = "resolve";
    protected static final String TAG_SHORTCUT = "shortcut";

    public DefaultLayoutParser(Context context, AppWidgetHost appWidgetHost, AutoInstallsLayout.LayoutParserCallback callback, Resources sourceRes, int layoutId) {
        super(context, appWidgetHost, callback, sourceRes, layoutId, "favorites");
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, AutoInstallsLayout.TagParser> getFolderElementsMap() {
        return getFolderElementsMap(this.mSourceRes);
    }

    /* access modifiers changed from: package-private */
    public ArrayMap<String, AutoInstallsLayout.TagParser> getFolderElementsMap(Resources res) {
        ArrayMap<String, AutoInstallsLayout.TagParser> parsers = new ArrayMap<>();
        parsers.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        parsers.put(TAG_SHORTCUT, new UriShortcutParser(res));
        return parsers;
    }

    /* access modifiers changed from: protected */
    public ArrayMap<String, AutoInstallsLayout.TagParser> getLayoutElementsMap() {
        ArrayMap<String, AutoInstallsLayout.TagParser> parsers = new ArrayMap<>();
        parsers.put(TAG_FAVORITE, new AppShortcutWithUriParser());
        parsers.put(TAG_APPWIDGET, new AppWidgetParser());
        parsers.put(TAG_SHORTCUT, new UriShortcutParser(this.mSourceRes));
        parsers.put(TAG_RESOLVE, new ResolveParser());
        parsers.put(TAG_FOLDER, new MyFolderParser());
        parsers.put(TAG_PARTNER_FOLDER, new PartnerFolderParser());
        return parsers;
    }

    /* access modifiers changed from: protected */
    public void parseContainerAndScreen(XmlResourceParser parser, long[] out) {
        out[0] = -100;
        String strContainer = getAttributeValue(parser, "container");
        if (strContainer != null) {
            out[0] = Long.valueOf(strContainer).longValue();
        }
        out[1] = Long.parseLong(getAttributeValue(parser, "screen"));
    }

    public class AppShortcutWithUriParser extends AutoInstallsLayout.AppShortcutParser {
        public AppShortcutWithUriParser() {
            super();
        }

        public /* bridge */ /* synthetic */ long parseAndAdd(XmlResourceParser xmlResourceParser) {
            return super.parseAndAdd(xmlResourceParser);
        }

        /* access modifiers changed from: protected */
        public long invalidPackageOrClass(XmlResourceParser parser) {
            String uri = AutoInstallsLayout.getAttributeValue(parser, DefaultLayoutParser.ATTR_URI);
            if (TextUtils.isEmpty(uri)) {
                Log.e(DefaultLayoutParser.TAG, "Skipping invalid <favorite> with no component or uri");
                return -1;
            }
            try {
                Intent metaIntent = Intent.parseUri(uri, 0);
                ResolveInfo resolved = DefaultLayoutParser.this.mPackageManager.resolveActivity(metaIntent, 65536);
                List<ResolveInfo> appList = DefaultLayoutParser.this.mPackageManager.queryIntentActivities(metaIntent, 65536);
                if (wouldLaunchResolverActivity(resolved, appList)) {
                    ResolveInfo systemApp = getSingleSystemActivity(appList);
                    if (systemApp == null) {
                        Log.w(DefaultLayoutParser.TAG, "No preference or single system activity found for " + metaIntent.toString());
                        return -1;
                    }
                    resolved = systemApp;
                }
                ActivityInfo info = resolved.activityInfo;
                Intent intent = DefaultLayoutParser.this.mPackageManager.getLaunchIntentForPackage(info.packageName);
                if (intent == null) {
                    return -1;
                }
                intent.setFlags(270532608);
                return DefaultLayoutParser.this.addShortcut(info.loadLabel(DefaultLayoutParser.this.mPackageManager).toString(), intent, 0);
            } catch (URISyntaxException e) {
                Log.e(DefaultLayoutParser.TAG, "Unable to add meta-favorite: " + uri, e);
                return -1;
            }
        }

        private ResolveInfo getSingleSystemActivity(List<ResolveInfo> appList) {
            int N = appList.size();
            ResolveInfo systemResolve = null;
            int i = 0;
            while (i < N) {
                try {
                    if ((DefaultLayoutParser.this.mPackageManager.getApplicationInfo(appList.get(i).activityInfo.packageName, 0).flags & 1) != 0) {
                        if (systemResolve != null) {
                            return null;
                        }
                        systemResolve = appList.get(i);
                    }
                    i++;
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(DefaultLayoutParser.TAG, "Unable to get info about resolve results", e);
                    return null;
                }
            }
            return systemResolve;
        }

        private boolean wouldLaunchResolverActivity(ResolveInfo resolved, List<ResolveInfo> appList) {
            for (int i = 0; i < appList.size(); i++) {
                ResolveInfo tmp = appList.get(i);
                if (tmp.activityInfo.name.equals(resolved.activityInfo.name) && tmp.activityInfo.packageName.equals(resolved.activityInfo.packageName)) {
                    return false;
                }
            }
            return true;
        }
    }

    public class UriShortcutParser extends AutoInstallsLayout.ShortcutParser {
        public /* bridge */ /* synthetic */ long parseAndAdd(XmlResourceParser xmlResourceParser) {
            return super.parseAndAdd(xmlResourceParser);
        }

        public UriShortcutParser(Resources iconRes) {
            super(iconRes);
        }

        /* access modifiers changed from: protected */
        public Intent parseIntent(XmlResourceParser parser) {
            try {
                return Intent.parseUri(AutoInstallsLayout.getAttributeValue(parser, DefaultLayoutParser.ATTR_URI), 0);
            } catch (URISyntaxException e) {
                Log.w(DefaultLayoutParser.TAG, "Shortcut has malformed uri: " + null);
                return null;
            }
        }
    }

    public class ResolveParser implements AutoInstallsLayout.TagParser {
        private final AppShortcutWithUriParser mChildParser = new AppShortcutWithUriParser();

        public ResolveParser() {
        }

        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            int groupDepth = parser.getDepth();
            long addedId = -1;
            while (true) {
                int next = parser.next();
                int type = next;
                if (next == 3 && parser.getDepth() <= groupDepth) {
                    return addedId;
                }
                if (type == 2 && addedId <= -1) {
                    String fallback_item_name = parser.getName();
                    if (DefaultLayoutParser.TAG_FAVORITE.equals(fallback_item_name)) {
                        addedId = this.mChildParser.parseAndAdd(parser);
                    } else {
                        Log.e(DefaultLayoutParser.TAG, "Fallback groups can contain only favorites, found " + fallback_item_name);
                    }
                }
            }
        }
    }

    class PartnerFolderParser implements AutoInstallsLayout.TagParser {
        PartnerFolderParser() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
            r1 = r0.getResources();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public long parseAndAdd(android.content.res.XmlResourceParser r8) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
            /*
                r7 = this;
                com.android.launcher3.DefaultLayoutParser r0 = com.android.launcher3.DefaultLayoutParser.this
                android.content.pm.PackageManager r0 = r0.mPackageManager
                com.android.launcher3.Partner r0 = com.android.launcher3.Partner.get(r0)
                if (r0 == 0) goto L_0x0037
                android.content.res.Resources r1 = r0.getResources()
                java.lang.String r2 = "partner_folder"
                java.lang.String r3 = "xml"
                java.lang.String r4 = r0.getPackageName()
                int r2 = r1.getIdentifier(r2, r3, r4)
                if (r2 == 0) goto L_0x0037
                android.content.res.XmlResourceParser r3 = r1.getXml(r2)
                java.lang.String r4 = "folder"
                com.android.launcher3.AutoInstallsLayout.beginDocument(r3, r4)
                com.android.launcher3.AutoInstallsLayout$FolderParser r4 = new com.android.launcher3.AutoInstallsLayout$FolderParser
                com.android.launcher3.DefaultLayoutParser r5 = com.android.launcher3.DefaultLayoutParser.this
                com.android.launcher3.DefaultLayoutParser r6 = com.android.launcher3.DefaultLayoutParser.this
                android.util.ArrayMap r6 = r6.getFolderElementsMap(r1)
                r4.<init>(r6)
                long r5 = r4.parseAndAdd(r3)
                return r5
            L_0x0037:
                r1 = -1
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.DefaultLayoutParser.PartnerFolderParser.parseAndAdd(android.content.res.XmlResourceParser):long");
        }
    }

    class MyFolderParser extends AutoInstallsLayout.FolderParser {
        MyFolderParser() {
            super(DefaultLayoutParser.this);
        }

        public long parseAndAdd(XmlResourceParser parser) throws XmlPullParserException, IOException {
            int resId = AutoInstallsLayout.getAttributeResourceValue(parser, DefaultLayoutParser.ATTR_FOLDER_ITEMS, 0);
            if (resId != 0) {
                parser = DefaultLayoutParser.this.mSourceRes.getXml(resId);
                AutoInstallsLayout.beginDocument(parser, DefaultLayoutParser.TAG_FOLDER);
            }
            return super.parseAndAdd(parser);
        }
    }

    protected class AppWidgetParser extends AutoInstallsLayout.PendingWidgetParser {
        protected AppWidgetParser() {
            super();
        }

        /* access modifiers changed from: protected */
        public long verifyAndInsert(ComponentName cn, Bundle extras) {
            try {
                DefaultLayoutParser.this.mPackageManager.getReceiverInfo(cn, 0);
            } catch (Exception e) {
                cn = new ComponentName(DefaultLayoutParser.this.mPackageManager.currentToCanonicalPackageNames(new String[]{cn.getPackageName()})[0], cn.getClassName());
                try {
                    DefaultLayoutParser.this.mPackageManager.getReceiverInfo(cn, 0);
                } catch (Exception e2) {
                    Log.d(DefaultLayoutParser.TAG, "Can't find widget provider: " + cn.getClassName());
                    return -1;
                }
            }
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(DefaultLayoutParser.this.mContext);
            long insertedId = -1;
            try {
                int appWidgetId = DefaultLayoutParser.this.mAppWidgetHost.allocateAppWidgetId();
                if (!appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, cn)) {
                    Log.e(DefaultLayoutParser.TAG, "Unable to bind app widget id " + cn);
                    DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                    return -1;
                }
                DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_ID, Integer.valueOf(appWidgetId));
                DefaultLayoutParser.this.mValues.put(LauncherSettings.Favorites.APPWIDGET_PROVIDER, cn.flattenToString());
                DefaultLayoutParser.this.mValues.put("_id", Long.valueOf(DefaultLayoutParser.this.mCallback.generateNewItemId()));
                insertedId = DefaultLayoutParser.this.mCallback.insertAndCheck(DefaultLayoutParser.this.mDb, DefaultLayoutParser.this.mValues);
                if (insertedId < 0) {
                    DefaultLayoutParser.this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
                    return insertedId;
                }
                if (!extras.isEmpty()) {
                    Intent intent = new Intent(DefaultLayoutParser.ACTION_APPWIDGET_DEFAULT_WORKSPACE_CONFIGURE);
                    intent.setComponent(cn);
                    intent.putExtras(extras);
                    intent.putExtra(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId);
                    DefaultLayoutParser.this.mContext.sendBroadcast(intent);
                }
                return insertedId;
            } catch (RuntimeException ex) {
                Log.e(DefaultLayoutParser.TAG, "Problem allocating appWidgetId", ex);
            }
        }
    }
}
