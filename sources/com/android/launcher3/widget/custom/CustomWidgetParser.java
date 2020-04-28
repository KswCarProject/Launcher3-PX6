package com.android.launcher3.widget.custom;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.util.SparseArray;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import java.util.List;

public class CustomWidgetParser {
    private static List<LauncherAppWidgetProviderInfo> sCustomWidgets;
    private static SparseArray<ComponentName> sWidgetsIdMap;

    public static List<LauncherAppWidgetProviderInfo> getCustomWidgets(Context context) {
        if (sCustomWidgets == null) {
            parseCustomWidgets(context);
        }
        return sCustomWidgets;
    }

    public static int getWidgetIdForCustomProvider(Context context, ComponentName provider) {
        if (sWidgetsIdMap == null) {
            parseCustomWidgets(context);
        }
        int index = sWidgetsIdMap.indexOfValue(provider);
        if (index >= 0) {
            return -100 - sWidgetsIdMap.keyAt(index);
        }
        return 0;
    }

    public static LauncherAppWidgetProviderInfo getWidgetProvider(Context context, int widgetId) {
        if (sWidgetsIdMap == null || sCustomWidgets == null) {
            parseCustomWidgets(context);
        }
        ComponentName cn = sWidgetsIdMap.get(-100 - widgetId);
        for (LauncherAppWidgetProviderInfo info : sCustomWidgets) {
            if (info.provider.equals(cn)) {
                return info;
            }
        }
        return null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x009a, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:?, code lost:
        r6.addSuppressed(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a3, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a9, code lost:
        throw new java.lang.RuntimeException(r4);
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00a3 A[ExcHandler: IOException | XmlPullParserException (r4v1 'e' java.lang.Exception A[CUSTOM_DECLARE]), Splitter:B:5:0x002f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void parseCustomWidgets(android.content.Context r13) {
        /*
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.util.SparseArray r1 = new android.util.SparseArray
            r1.<init>()
            android.appwidget.AppWidgetManager r2 = android.appwidget.AppWidgetManager.getInstance(r13)
            android.os.UserHandle r3 = android.os.Process.myUserHandle()
            java.util.List r2 = r2.getInstalledProvidersForProfile(r3)
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x0021
            sCustomWidgets = r0
            sWidgetsIdMap = r1
            return
        L_0x0021:
            android.os.Parcel r3 = android.os.Parcel.obtain()
            r4 = 0
            java.lang.Object r5 = r2.get(r4)
            android.appwidget.AppWidgetProviderInfo r5 = (android.appwidget.AppWidgetProviderInfo) r5
            r5.writeToParcel(r3, r4)
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
            r6 = 2132082689(0x7f150001, float:1.98055E38)
            android.content.res.XmlResourceParser r5 = r5.getXml(r6)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
            r6 = 0
            int r7 = r5.getDepth()     // Catch:{ Throwable -> 0x008f }
        L_0x003f:
            int r8 = r5.next()     // Catch:{ Throwable -> 0x008f }
            r9 = r8
            r10 = 3
            if (r8 != r10) goto L_0x004d
            int r8 = r5.getDepth()     // Catch:{ Throwable -> 0x008f }
            if (r8 <= r7) goto L_0x007f
        L_0x004d:
            r8 = 1
            if (r9 == r8) goto L_0x007f
            r8 = 2
            if (r9 != r8) goto L_0x003f
            java.lang.String r8 = "widget"
            java.lang.String r10 = r5.getName()     // Catch:{ Throwable -> 0x008f }
            boolean r8 = r8.equals(r10)     // Catch:{ Throwable -> 0x008f }
            if (r8 == 0) goto L_0x003f
            android.util.AttributeSet r8 = android.util.Xml.asAttributeSet(r5)     // Catch:{ Throwable -> 0x008f }
            int[] r10 = com.android.launcher3.R.styleable.CustomAppWidgetProviderInfo     // Catch:{ Throwable -> 0x008f }
            android.content.res.TypedArray r8 = r13.obtainStyledAttributes(r8, r10)     // Catch:{ Throwable -> 0x008f }
            r3.setDataPosition(r4)     // Catch:{ Throwable -> 0x008f }
            com.android.launcher3.widget.custom.CustomAppWidgetProviderInfo r10 = newInfo(r8, r3, r13)     // Catch:{ Throwable -> 0x008f }
            r0.add(r10)     // Catch:{ Throwable -> 0x008f }
            r8.recycle()     // Catch:{ Throwable -> 0x008f }
            int r11 = r10.providerId     // Catch:{ Throwable -> 0x008f }
            android.content.ComponentName r12 = r10.provider     // Catch:{ Throwable -> 0x008f }
            r1.put(r11, r12)     // Catch:{ Throwable -> 0x008f }
            goto L_0x003f
        L_0x007f:
            if (r5 == 0) goto L_0x0084
            r5.close()     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
        L_0x0084:
            r3.recycle()
            sCustomWidgets = r0
            sWidgetsIdMap = r1
            return
        L_0x008d:
            r4 = move-exception
            goto L_0x0092
        L_0x008f:
            r4 = move-exception
            r6 = r4
            throw r6     // Catch:{ all -> 0x008d }
        L_0x0092:
            if (r5 == 0) goto L_0x00a2
            if (r6 == 0) goto L_0x009f
            r5.close()     // Catch:{ Throwable -> 0x009a, IOException | XmlPullParserException -> 0x00a3 }
            goto L_0x00a2
        L_0x009a:
            r7 = move-exception
            r6.addSuppressed(r7)     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
            goto L_0x00a2
        L_0x009f:
            r5.close()     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
        L_0x00a2:
            throw r4     // Catch:{ IOException | XmlPullParserException -> 0x00a3 }
        L_0x00a3:
            r4 = move-exception
            java.lang.RuntimeException r5 = new java.lang.RuntimeException
            r5.<init>(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.widget.custom.CustomWidgetParser.parseCustomWidgets(android.content.Context):void");
    }

    private static CustomAppWidgetProviderInfo newInfo(TypedArray a, Parcel parcel, Context context) {
        int providerId = a.getInt(9, 0);
        CustomAppWidgetProviderInfo info = new CustomAppWidgetProviderInfo(parcel, false, providerId);
        String packageName = context.getPackageName();
        info.provider = new ComponentName(packageName, LauncherAppWidgetProviderInfo.CLS_CUSTOM_WIDGET_PREFIX + providerId);
        info.label = a.getString(0);
        info.initialLayout = a.getResourceId(2, 0);
        info.icon = a.getResourceId(1, 0);
        info.previewImage = a.getResourceId(3, 0);
        info.resizeMode = a.getInt(4, 0);
        info.spanX = a.getInt(5, 1);
        info.spanY = a.getInt(8, 1);
        info.minSpanX = a.getInt(6, 1);
        info.minSpanY = a.getInt(7, 1);
        return info;
    }
}
