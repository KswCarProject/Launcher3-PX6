package com.android.launcher3.provider;

import android.appwidget.AppWidgetHost;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ProviderInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Process;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import com.android.launcher3.AutoInstallsLayout;
import com.android.launcher3.DefaultLayoutParser;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.model.GridSizeMigrationTask;
import com.android.launcher3.util.LongArrayMap;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

public class ImportDataTask {
    private static final int BATCH_INSERT_SIZE = 15;
    public static final String KEY_DATA_IMPORT_SRC_AUTHORITY = "data_import_src_authority";
    public static final String KEY_DATA_IMPORT_SRC_PKG = "data_import_src_pkg";
    private static final int MIN_ITEM_COUNT_FOR_SUCCESSFUL_MIGRATION = 6;
    private static final String TAG = "ImportDataTask";
    private final Context mContext;
    private int mHotseatSize;
    private int mMaxGridSizeX;
    private int mMaxGridSizeY;
    private final Uri mOtherFavoritesUri;
    private final Uri mOtherScreensUri;

    private ImportDataTask(Context context, String sourceAuthority) {
        this.mContext = context;
        this.mOtherScreensUri = Uri.parse("content://" + sourceAuthority + "/" + LauncherSettings.WorkspaceScreens.TABLE_NAME);
        this.mOtherFavoritesUri = Uri.parse("content://" + sourceAuthority + "/" + LauncherSettings.Favorites.TABLE_NAME);
    }

    public boolean importWorkspace() throws Exception {
        ArrayList<Long> allScreens = LauncherDbUtils.getScreenIdsFromCursor(this.mContext.getContentResolver().query(this.mOtherScreensUri, (String[]) null, (String) null, (String[]) null, LauncherSettings.WorkspaceScreens.SCREEN_RANK));
        FileLog.d(TAG, "Importing DB from " + this.mOtherFavoritesUri);
        if (allScreens.isEmpty()) {
            FileLog.e(TAG, "No data found to import");
            return false;
        }
        this.mMaxGridSizeY = 0;
        this.mMaxGridSizeX = 0;
        this.mHotseatSize = 0;
        ArrayList<ContentProviderOperation> screenOps = new ArrayList<>();
        int count = allScreens.size();
        LongSparseArray<Long> screenIdMap = new LongSparseArray<>(count);
        for (int i = 0; i < count; i++) {
            ContentValues v = new ContentValues();
            v.put("_id", Integer.valueOf(i));
            v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
            screenIdMap.put(allScreens.get(i).longValue(), Long.valueOf((long) i));
            screenOps.add(ContentProviderOperation.newInsert(LauncherSettings.WorkspaceScreens.CONTENT_URI).withValues(v).build());
        }
        this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, screenOps);
        importWorkspaceItems(allScreens.get(0).longValue(), screenIdMap);
        GridSizeMigrationTask.markForMigration(this.mContext, this.mMaxGridSizeX, this.mMaxGridSizeY, this.mHotseatSize);
        LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_CLEAR_EMPTY_DB_FLAG);
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0366, code lost:
        r48 = r3;
        r49 = r4;
        r50 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x0372, code lost:
        if (r0.getComponent() == null) goto L_0x037f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:?, code lost:
        r0.setPackage(r0.getComponent().getPackageName());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x0383, code lost:
        r5 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:?, code lost:
        r5.add(getPackage(r0));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0389, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x038a, code lost:
        r2 = r0;
        r7 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x038f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0390, code lost:
        r2 = r0;
        r7 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x0395, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0396, code lost:
        r5 = r27;
        r2 = r0;
        r7 = r25;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x039e, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x039f, code lost:
        r5 = r27;
        r2 = r0;
        r7 = r25;
        r9 = r31;
        r8 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x03aa, code lost:
        r48 = r3;
        r49 = r4;
        r50 = r7;
        r5 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:?, code lost:
        r9.put("_id", java.lang.Integer.valueOf(r13));
        r9.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ITEM_TYPE, java.lang.Integer.valueOf(r6));
        r9.put(com.android.launcher3.LauncherSettings.Favorites.CONTAINER, java.lang.Integer.valueOf(r2));
        r9.put(com.android.launcher3.LauncherSettings.Favorites.SCREEN, java.lang.Long.valueOf(r37));
        r9.put(com.android.launcher3.LauncherSettings.Favorites.CELLX, java.lang.Integer.valueOf(r16));
        r9.put(com.android.launcher3.LauncherSettings.Favorites.CELLY, java.lang.Integer.valueOf(r17));
        r9.put(com.android.launcher3.LauncherSettings.Favorites.SPANX, java.lang.Integer.valueOf(r18));
        r9.put(com.android.launcher3.LauncherSettings.Favorites.SPANY, java.lang.Integer.valueOf(r23));
        r4 = r26;
        r9.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.TITLE, r12.getString(r4));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0415, code lost:
        r7 = r25;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:?, code lost:
        r7.add(android.content.ContentProviderOperation.newInsert(com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI).withValues(r9).build());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x041a, code lost:
        if (r2 >= 0) goto L_0x0421;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:127:0x041c, code lost:
        r31 = r31 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x0421, code lost:
        r3 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:?, code lost:
        r51 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x042b, code lost:
        if (r7.size() < 15) goto L_0x043b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x042d, code lost:
        r1.mContext.getContentResolver().applyBatch(com.android.launcher3.LauncherProvider.AUTHORITY, r7);
        r7.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x043b, code lost:
        r26 = r4;
        r27 = r5;
        r25 = r7;
        r30 = r8;
        r16 = r9;
        r13 = r34;
        r2 = r39;
        r5 = r40;
        r8 = r41;
        r9 = r42;
        r4 = r43;
        r6 = r44;
        r3 = r45;
        r0 = r47;
        r28 = r48;
        r29 = r49;
        r7 = r50;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0460, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0461, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0464, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x0465, code lost:
        r2 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0468, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0469, code lost:
        r3 = r31;
        r2 = r0;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x046f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x0470, code lost:
        r2 = r0;
        r9 = r31;
        r8 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0478, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0479, code lost:
        r7 = r25;
        r3 = r31;
        r2 = r0;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0481, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0482, code lost:
        r7 = r25;
        r2 = r0;
        r9 = r31;
        r8 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x048c, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x048d, code lost:
        r7 = r25;
        r5 = r27;
        r3 = r31;
        r2 = r0;
        r10 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0497, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:0x0498, code lost:
        r7 = r25;
        r5 = r27;
        r2 = r0;
        r9 = r31;
        r8 = r43;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:176:0x0580, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x0581, code lost:
        r45 = r3;
        r7 = r25;
        r5 = r27;
        r3 = r31;
        r10 = null;
        r2 = r0;
        r43 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:178:0x058f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x0590, code lost:
        r45 = r3;
        r7 = r25;
        r5 = r27;
        r2 = r0;
        r9 = r31;
        r8 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0246, code lost:
        r37 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0248, code lost:
        r6 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x024c, code lost:
        if (r6 == 4) goto L_0x0305;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x024e, code lost:
        switch(r6) {
            case 0: goto L_0x02a2;
            case 1: goto L_0x02a2;
            case 2: goto L_0x028a;
            default: goto L_0x0251;
        };
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0251, code lost:
        com.android.launcher3.logging.FileLog.d(TAG, java.lang.String.format("Skipping item %d, not a valid type %d", new java.lang.Object[]{java.lang.Integer.valueOf(r13), java.lang.Integer.valueOf(r6)}));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0272, code lost:
        r47 = r0;
        r49 = r4;
        r50 = r7;
        r48 = r28;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x027a, code lost:
        r8 = r30;
        r9 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x028a, code lost:
        r4.put(r13, true);
        r47 = r0;
        r0 = new android.content.Intent();
        r3 = r28;
        r8 = r30;
        r9 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x02a2, code lost:
        r3 = android.content.Intent.parseUri(r12.getString(r14), 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x02b2, code lost:
        if (com.android.launcher3.Utilities.isLauncherAppTarget(r3) == false) goto L_0x02ba;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x02b4, code lost:
        r35 = 0;
        r9 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x02ba, code lost:
        r9 = r32;
        r9.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, r12.getString(r7));
        r9.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, r12.getString(r0));
        r35 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x02d0, code lost:
        r9.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.ICON, r12.getBlob(r15));
        r9.put(com.android.launcher3.LauncherSettings.BaseLauncherColumns.INTENT, r3.toUri(0));
        r8 = r30;
        r9.put(com.android.launcher3.LauncherSettings.Favorites.RANK, java.lang.Integer.valueOf(r12.getInt(r8)));
        r47 = r0;
        r9.put(com.android.launcher3.LauncherSettings.Favorites.RESTORED, 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x02fe, code lost:
        r0 = r3;
        r3 = r28;
        r6 = r35;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0305, code lost:
        r47 = r0;
        r8 = r30;
        r9 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:?, code lost:
        r9.put(com.android.launcher3.LauncherSettings.Favorites.RESTORED, 7);
        r3 = r28;
        r9.put(com.android.launcher3.LauncherSettings.Favorites.APPWIDGET_PROVIDER, r12.getString(r3));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0322, code lost:
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x0327, code lost:
        if (r2 != -101) goto L_0x03aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0329, code lost:
        if (r0 != null) goto L_0x0366;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x032b, code lost:
        r48 = r3;
        r49 = r4;
        r50 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:?, code lost:
        com.android.launcher3.logging.FileLog.d(TAG, java.lang.String.format("Skipping item %d, null intent on hotseat", new java.lang.Object[]{java.lang.Integer.valueOf(r13)}));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0348, code lost:
        r30 = r8;
        r16 = r9;
        r13 = r34;
        r2 = r39;
        r5 = r40;
        r8 = r41;
        r9 = r42;
        r4 = r43;
        r6 = r44;
        r3 = r45;
        r0 = r47;
        r28 = r48;
        r29 = r49;
        r7 = r50;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x058f A[ExcHandler: Throwable (r0v27 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:26:0x00de] */
    /* JADX WARNING: Removed duplicated region for block: B:209:0x064b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void importWorkspaceItems(long r53, android.util.LongSparseArray<java.lang.Long> r55) throws java.lang.Exception {
        /*
            r52 = this;
            r1 = r52
            android.content.Context r0 = r1.mContext
            com.android.launcher3.compat.UserManagerCompat r0 = com.android.launcher3.compat.UserManagerCompat.getInstance(r0)
            android.os.UserHandle r2 = android.os.Process.myUserHandle()
            long r2 = r0.getSerialNumberForUser(r2)
            java.lang.String r2 = java.lang.Long.toString(r2)
            r3 = 0
            android.content.Context r0 = r1.mContext
            android.content.ContentResolver r4 = r0.getContentResolver()
            android.net.Uri r5 = r1.mOtherFavoritesUri
            java.lang.String r7 = "profileId = ? AND container = -100 AND screen = ? AND cellY = 0"
            r0 = 2
            java.lang.String[] r8 = new java.lang.String[r0]
            r10 = 0
            r8[r10] = r2
            java.lang.String r6 = java.lang.Long.toString(r53)
            r11 = 1
            r8[r11] = r6
            r6 = 0
            r9 = 0
            android.database.Cursor r4 = r4.query(r5, r6, r7, r8, r9)
            boolean r6 = r4.moveToNext()     // Catch:{ Throwable -> 0x0662, all -> 0x065b }
            r3 = r6
            if (r4 == 0) goto L_0x003c
            r4.close()
        L_0x003c:
            java.util.ArrayList r4 = new java.util.ArrayList
            r6 = 15
            r4.<init>(r6)
            java.util.HashSet r7 = new java.util.HashSet
            r7.<init>()
            r8 = 0
            r9 = 0
            android.content.Context r12 = r1.mContext
            android.content.ContentResolver r13 = r12.getContentResolver()
            android.net.Uri r14 = r1.mOtherFavoritesUri
            r15 = 0
            java.lang.String r16 = "profileId = ?"
            java.lang.String[] r12 = new java.lang.String[r11]
            r12[r10] = r2
            java.lang.String r18 = "container"
            r17 = r12
            android.database.Cursor r12 = r13.query(r14, r15, r16, r17, r18)
            java.lang.String r13 = "_id"
            int r13 = r12.getColumnIndexOrThrow(r13)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r14 = "intent"
            int r14 = r12.getColumnIndexOrThrow(r14)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r15 = "title"
            int r15 = r12.getColumnIndexOrThrow(r15)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r5 = "container"
            int r5 = r12.getColumnIndexOrThrow(r5)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r6 = "itemType"
            int r6 = r12.getColumnIndexOrThrow(r6)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r0 = "appWidgetProvider"
            int r0 = r12.getColumnIndexOrThrow(r0)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r11 = "screen"
            int r11 = r12.getColumnIndexOrThrow(r11)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            java.lang.String r10 = "cellX"
            int r10 = r12.getColumnIndexOrThrow(r10)     // Catch:{ Throwable -> 0x0635, all -> 0x0623 }
            r22 = r2
            java.lang.String r2 = "cellY"
            int r2 = r12.getColumnIndexOrThrow(r2)     // Catch:{ Throwable -> 0x0618, all -> 0x0608 }
            r23 = r8
            java.lang.String r8 = "spanX"
            int r8 = r12.getColumnIndexOrThrow(r8)     // Catch:{ Throwable -> 0x05fd, all -> 0x05ee }
            r24 = r9
            java.lang.String r9 = "spanY"
            int r9 = r12.getColumnIndexOrThrow(r9)     // Catch:{ Throwable -> 0x05e2, all -> 0x05d5 }
            r25 = r4
            java.lang.String r4 = "rank"
            int r4 = r12.getColumnIndexOrThrow(r4)     // Catch:{ Throwable -> 0x05c8, all -> 0x05ba }
            r26 = r15
            java.lang.String r15 = "icon"
            int r15 = r12.getColumnIndexOrThrow(r15)     // Catch:{ Throwable -> 0x05c8, all -> 0x05ba }
            r27 = r7
            java.lang.String r7 = "iconPackage"
            int r7 = r12.getColumnIndexOrThrow(r7)     // Catch:{ Throwable -> 0x05ac, all -> 0x059d }
            r28 = r0
            java.lang.String r0 = "iconResource"
            int r0 = r12.getColumnIndexOrThrow(r0)     // Catch:{ Throwable -> 0x05ac, all -> 0x059d }
            android.util.SparseBooleanArray r16 = new android.util.SparseBooleanArray     // Catch:{ Throwable -> 0x05ac, all -> 0x059d }
            r16.<init>()     // Catch:{ Throwable -> 0x05ac, all -> 0x059d }
            r29 = r16
            android.content.ContentValues r16 = new android.content.ContentValues     // Catch:{ Throwable -> 0x05ac, all -> 0x059d }
            r16.<init>()     // Catch:{ Throwable -> 0x05ac, all -> 0x059d }
            r30 = r4
            r4 = r23
            r31 = r24
        L_0x00dc:
            r32 = r16
            boolean r16 = r12.moveToNext()     // Catch:{ Throwable -> 0x058f, all -> 0x0580 }
            if (r16 == 0) goto L_0x04ed
            r32.clear()     // Catch:{ Throwable -> 0x058f, all -> 0x04de }
            int r16 = r12.getInt(r13)     // Catch:{ Throwable -> 0x058f, all -> 0x04de }
            r33 = r16
            r34 = r13
            r13 = r33
            int r16 = java.lang.Math.max(r4, r13)     // Catch:{ Throwable -> 0x058f, all -> 0x04de }
            r4 = r16
            int r16 = r12.getInt(r6)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            r35 = r16
            int r16 = r12.getInt(r5)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            r36 = r16
            long r16 = r12.getLong(r11)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            r37 = r16
            int r16 = r12.getInt(r10)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            int r17 = r12.getInt(r2)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            int r18 = r12.getInt(r8)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            int r23 = r12.getInt(r9)     // Catch:{ Throwable -> 0x04cd, all -> 0x04be }
            r39 = r2
            r2 = r36
            switch(r2) {
                case -101: goto L_0x01c9;
                case -100: goto L_0x0134;
                default: goto L_0x0120;
            }
        L_0x0120:
            r43 = r4
            r40 = r5
            r44 = r6
            r41 = r8
            r42 = r9
            r8 = r37
            r4 = r29
            boolean r5 = r4.get(r2)     // Catch:{ Throwable -> 0x04af, all -> 0x04a4 }
            goto L_0x0200
        L_0x0134:
            r40 = r5
            r41 = r8
            r42 = r9
            r8 = r37
            r5 = r55
            java.lang.Object r24 = r5.get(r8)     // Catch:{ Throwable -> 0x01b9, all -> 0x01ac }
            java.lang.Long r24 = (java.lang.Long) r24     // Catch:{ Throwable -> 0x01b9, all -> 0x01ac }
            if (r24 != 0) goto L_0x017d
            r43 = r4
            java.lang.String r4 = "ImportDataTask"
            java.lang.String r5 = "Skipping item %d, type %d not on a valid screen %d"
            r44 = r6
            r6 = 3
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            java.lang.Integer r33 = java.lang.Integer.valueOf(r13)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r21 = 0
            r6[r21] = r33     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            java.lang.Integer r33 = java.lang.Integer.valueOf(r35)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r20 = 1
            r6[r20] = r33     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            java.lang.Long r33 = java.lang.Long.valueOf(r8)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r19 = 2
            r6[r19] = r33     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            java.lang.String r5 = java.lang.String.format(r5, r6)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            com.android.launcher3.logging.FileLog.d(r4, r5)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r47 = r0
            r45 = r3
            r50 = r7
            r48 = r28
            r49 = r29
            goto L_0x027a
        L_0x017d:
            r43 = r4
            r44 = r6
            long r4 = r24.longValue()     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r37 = r4
            if (r3 == 0) goto L_0x0191
            r4 = 0
            int r4 = (r37 > r4 ? 1 : (r37 == r4 ? 0 : -1))
            if (r4 != 0) goto L_0x0191
            int r17 = r17 + 1
        L_0x0191:
            int r4 = r1.mMaxGridSizeX     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            int r5 = r16 + r18
            int r4 = java.lang.Math.max(r4, r5)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r1.mMaxGridSizeX = r4     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            int r4 = r1.mMaxGridSizeY     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            int r5 = r17 + r23
            int r4 = java.lang.Math.max(r4, r5)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r1.mMaxGridSizeY = r4     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r45 = r3
            r4 = r29
            goto L_0x0248
        L_0x01ac:
            r0 = move-exception
            r43 = r4
            r2 = r0
            r45 = r3
            r7 = r25
            r5 = r27
            r10 = 0
            goto L_0x0649
        L_0x01b9:
            r0 = move-exception
            r43 = r4
            r2 = r0
            r45 = r3
            r7 = r25
            r5 = r27
            r9 = r31
            r8 = r43
            goto L_0x0641
        L_0x01c9:
            r43 = r4
            r40 = r5
            r44 = r6
            r41 = r8
            r42 = r9
            r8 = r37
            int r4 = r1.mHotseatSize     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            int r5 = (int) r8     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r6 = 1
            int r5 = r5 + r6
            int r4 = java.lang.Math.max(r4, r5)     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r1.mHotseatSize = r4     // Catch:{ Throwable -> 0x01f2, all -> 0x01e7 }
            r45 = r3
            r4 = r29
            goto L_0x0246
        L_0x01e7:
            r0 = move-exception
            r2 = r0
            r45 = r3
        L_0x01eb:
            r7 = r25
            r5 = r27
        L_0x01ef:
            r10 = 0
            goto L_0x0649
        L_0x01f2:
            r0 = move-exception
            r2 = r0
            r45 = r3
        L_0x01f6:
            r7 = r25
            r5 = r27
        L_0x01fa:
            r9 = r31
            r8 = r43
            goto L_0x0641
        L_0x0200:
            if (r5 != 0) goto L_0x0244
            java.lang.String r5 = "ImportDataTask"
            java.lang.String r6 = "Skipping item %d, type %d not in a valid folder %d"
            r45 = r3
            r3 = 3
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r24 = java.lang.Integer.valueOf(r13)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r21 = 0
            r3[r21] = r24     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r24 = java.lang.Integer.valueOf(r35)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r20 = 1
            r3[r20] = r24     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r24 = java.lang.Integer.valueOf(r2)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r19 = 2
            r3[r19] = r24     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r3 = java.lang.String.format(r6, r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            com.android.launcher3.logging.FileLog.d(r5, r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            goto L_0x0272
        L_0x022b:
            r0 = move-exception
            r45 = r3
            r2 = r0
            r7 = r25
            r5 = r27
            r10 = 0
            goto L_0x0649
        L_0x0236:
            r0 = move-exception
            r45 = r3
            r2 = r0
            r7 = r25
            r5 = r27
            r9 = r31
            r8 = r43
            goto L_0x0641
        L_0x0244:
            r45 = r3
        L_0x0246:
            r37 = r8
        L_0x0248:
            r3 = 0
            r5 = 4
            r6 = r35
            if (r6 == r5) goto L_0x0305
            switch(r6) {
                case 0: goto L_0x02a2;
                case 1: goto L_0x02a2;
                case 2: goto L_0x028a;
                default: goto L_0x0251;
            }     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
        L_0x0251:
            java.lang.String r5 = "ImportDataTask"
            java.lang.String r8 = "Skipping item %d, not a valid type %d"
            r46 = r3
            r9 = 2
            java.lang.Object[] r3 = new java.lang.Object[r9]     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r19 = java.lang.Integer.valueOf(r13)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r21 = 0
            r3[r21] = r19     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r19 = java.lang.Integer.valueOf(r6)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r20 = 1
            r3[r20] = r19     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r3 = java.lang.String.format(r8, r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            com.android.launcher3.logging.FileLog.d(r5, r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
        L_0x0272:
            r47 = r0
            r49 = r4
            r50 = r7
            r48 = r28
        L_0x027a:
            r8 = r30
            r9 = r32
            r19 = 0
            goto L_0x0348
        L_0x0282:
            r0 = move-exception
            r2 = r0
            goto L_0x01eb
        L_0x0286:
            r0 = move-exception
            r2 = r0
            goto L_0x01f6
        L_0x028a:
            r46 = r3
            r9 = 2
            r3 = 1
            r4.put(r13, r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            android.content.Intent r3 = new android.content.Intent     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r3.<init>()     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r47 = r0
            r0 = r3
            r3 = r28
            r8 = r30
            r9 = r32
            goto L_0x0325
        L_0x02a2:
            r46 = r3
            r9 = 2
            java.lang.String r3 = r12.getString(r14)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r5 = 0
            android.content.Intent r3 = android.content.Intent.parseUri(r3, r5)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            boolean r5 = com.android.launcher3.Utilities.isLauncherAppTarget(r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            if (r5 == 0) goto L_0x02ba
            r5 = 0
            r35 = r5
            r9 = r32
            goto L_0x02d0
        L_0x02ba:
            java.lang.String r5 = "iconPackage"
            java.lang.String r8 = r12.getString(r7)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r9 = r32
            r9.put(r5, r8)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r5 = "iconResource"
            java.lang.String r8 = r12.getString(r0)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r9.put(r5, r8)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r35 = r6
        L_0x02d0:
            java.lang.String r5 = "icon"
            byte[] r6 = r12.getBlob(r15)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r9.put(r5, r6)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r5 = "intent"
            r6 = 0
            java.lang.String r8 = r3.toUri(r6)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r9.put(r5, r8)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r5 = "rank"
            r8 = r30
            int r6 = r12.getInt(r8)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r9.put(r5, r6)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r5 = "restored"
            r47 = r0
            r6 = 1
            java.lang.Integer r0 = java.lang.Integer.valueOf(r6)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r9.put(r5, r0)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r0 = r3
            r3 = r28
            r6 = r35
            goto L_0x0325
        L_0x0305:
            r47 = r0
            r46 = r3
            r8 = r30
            r9 = r32
            java.lang.String r0 = "restored"
            r3 = 7
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ Throwable -> 0x0497, all -> 0x048c }
            r9.put(r0, r3)     // Catch:{ Throwable -> 0x0497, all -> 0x048c }
            java.lang.String r0 = "appWidgetProvider"
            r3 = r28
            java.lang.String r5 = r12.getString(r3)     // Catch:{ Throwable -> 0x0497, all -> 0x048c }
            r9.put(r0, r5)     // Catch:{ Throwable -> 0x0497, all -> 0x048c }
            r0 = r46
        L_0x0325:
            r5 = -101(0xffffffffffffff9b, float:NaN)
            if (r2 != r5) goto L_0x03aa
            if (r0 != 0) goto L_0x0366
            java.lang.String r5 = "ImportDataTask"
            r48 = r3
            java.lang.String r3 = "Skipping item %d, null intent on hotseat"
            r49 = r4
            r50 = r7
            r4 = 1
            java.lang.Object[] r7 = new java.lang.Object[r4]     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r13)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r19 = 0
            r7[r19] = r4     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r3 = java.lang.String.format(r3, r7)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            com.android.launcher3.logging.FileLog.d(r5, r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
        L_0x0348:
            r30 = r8
            r16 = r9
            r13 = r34
            r2 = r39
            r5 = r40
            r8 = r41
            r9 = r42
            r4 = r43
            r6 = r44
            r3 = r45
            r0 = r47
            r28 = r48
            r29 = r49
            r7 = r50
            goto L_0x00dc
        L_0x0366:
            r48 = r3
            r49 = r4
            r50 = r7
            r19 = 0
            android.content.ComponentName r3 = r0.getComponent()     // Catch:{ Throwable -> 0x039e, all -> 0x0395 }
            if (r3 == 0) goto L_0x037f
            android.content.ComponentName r3 = r0.getComponent()     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            java.lang.String r3 = r3.getPackageName()     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
            r0.setPackage(r3)     // Catch:{ Throwable -> 0x0286, all -> 0x0282 }
        L_0x037f:
            java.lang.String r3 = getPackage(r0)     // Catch:{ Throwable -> 0x039e, all -> 0x0395 }
            r5 = r27
            r5.add(r3)     // Catch:{ Throwable -> 0x038f, all -> 0x0389 }
            goto L_0x03b4
        L_0x0389:
            r0 = move-exception
            r2 = r0
            r7 = r25
            goto L_0x01ef
        L_0x038f:
            r0 = move-exception
            r2 = r0
            r7 = r25
            goto L_0x01fa
        L_0x0395:
            r0 = move-exception
            r5 = r27
            r2 = r0
            r7 = r25
            r10 = 0
            goto L_0x0649
        L_0x039e:
            r0 = move-exception
            r5 = r27
            r2 = r0
            r7 = r25
            r9 = r31
            r8 = r43
            goto L_0x0641
        L_0x03aa:
            r48 = r3
            r49 = r4
            r50 = r7
            r5 = r27
            r19 = 0
        L_0x03b4:
            java.lang.String r3 = "_id"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r13)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "itemType"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r6)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "container"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "screen"
            java.lang.Long r4 = java.lang.Long.valueOf(r37)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "cellX"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "cellY"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r17)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "spanX"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r18)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "spanY"
            java.lang.Integer r4 = java.lang.Integer.valueOf(r23)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            java.lang.String r3 = "title"
            r4 = r26
            java.lang.String r7 = r12.getString(r4)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r9.put(r3, r7)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            android.net.Uri r3 = com.android.launcher3.LauncherSettings.Favorites.CONTENT_URI     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            android.content.ContentProviderOperation$Builder r3 = android.content.ContentProviderOperation.newInsert(r3)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            android.content.ContentProviderOperation$Builder r3 = r3.withValues(r9)     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            android.content.ContentProviderOperation r3 = r3.build()     // Catch:{ Throwable -> 0x0481, all -> 0x0478 }
            r7 = r25
            r7.add(r3)     // Catch:{ Throwable -> 0x046f, all -> 0x0468 }
            if (r2 >= 0) goto L_0x0421
            r3 = r31
            int r31 = r3 + 1
            goto L_0x0423
        L_0x0421:
            r3 = r31
        L_0x0423:
            int r3 = r7.size()     // Catch:{ Throwable -> 0x0464, all -> 0x0460 }
            r51 = r0
            r0 = 15
            if (r3 < r0) goto L_0x043b
            android.content.Context r3 = r1.mContext     // Catch:{ Throwable -> 0x0464, all -> 0x0460 }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ Throwable -> 0x0464, all -> 0x0460 }
            java.lang.String r0 = com.android.launcher3.LauncherProvider.AUTHORITY     // Catch:{ Throwable -> 0x0464, all -> 0x0460 }
            r3.applyBatch(r0, r7)     // Catch:{ Throwable -> 0x0464, all -> 0x0460 }
            r7.clear()     // Catch:{ Throwable -> 0x0464, all -> 0x0460 }
        L_0x043b:
            r26 = r4
            r27 = r5
            r25 = r7
            r30 = r8
            r16 = r9
            r13 = r34
            r2 = r39
            r5 = r40
            r8 = r41
            r9 = r42
            r4 = r43
            r6 = r44
            r3 = r45
            r0 = r47
            r28 = r48
            r29 = r49
            r7 = r50
            goto L_0x00dc
        L_0x0460:
            r0 = move-exception
            r2 = r0
            goto L_0x01ef
        L_0x0464:
            r0 = move-exception
            r2 = r0
            goto L_0x01fa
        L_0x0468:
            r0 = move-exception
            r3 = r31
            r2 = r0
            r10 = 0
            goto L_0x0649
        L_0x046f:
            r0 = move-exception
            r3 = r31
            r2 = r0
            r9 = r3
            r8 = r43
            goto L_0x0641
        L_0x0478:
            r0 = move-exception
            r7 = r25
            r3 = r31
            r2 = r0
            r10 = 0
            goto L_0x0649
        L_0x0481:
            r0 = move-exception
            r7 = r25
            r3 = r31
            r2 = r0
            r9 = r3
            r8 = r43
            goto L_0x0641
        L_0x048c:
            r0 = move-exception
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r10 = 0
            goto L_0x0649
        L_0x0497:
            r0 = move-exception
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r9 = r3
            r8 = r43
            goto L_0x0641
        L_0x04a4:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            goto L_0x04ea
        L_0x04af:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r9 = r3
            r8 = r43
            goto L_0x059b
        L_0x04be:
            r0 = move-exception
            r45 = r3
            r43 = r4
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r10 = 0
            goto L_0x0649
        L_0x04cd:
            r0 = move-exception
            r45 = r3
            r43 = r4
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r9 = r3
            r8 = r43
            goto L_0x0641
        L_0x04de:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r43 = r4
        L_0x04ea:
            r10 = 0
            goto L_0x058d
        L_0x04ed:
            r45 = r3
            r7 = r25
            r5 = r27
            r3 = r31
            if (r12 == 0) goto L_0x04fa
            r12.close()
        L_0x04fa:
            java.lang.String r0 = "ImportDataTask"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r3)
            java.lang.String r6 = " items imported from external source"
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            com.android.launcher3.logging.FileLog.d(r0, r2)
            r0 = 6
            if (r3 < r0) goto L_0x0578
            boolean r0 = r7.isEmpty()
            if (r0 != 0) goto L_0x0527
            android.content.Context r0 = r1.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            java.lang.String r2 = com.android.launcher3.LauncherProvider.AUTHORITY
            r0.applyBatch(r2, r7)
            r7.clear()
        L_0x0527:
            android.content.Context r0 = r1.mContext
            com.android.launcher3.util.LongArrayMap r0 = com.android.launcher3.model.GridSizeMigrationTask.removeBrokenHotseatItems(r0)
            android.content.Context r2 = r1.mContext
            com.android.launcher3.InvariantDeviceProfile r2 = com.android.launcher3.LauncherAppState.getIDP(r2)
            int r2 = r2.numHotseatIcons
            int r6 = r0.size()
            if (r6 >= r2) goto L_0x0577
            com.android.launcher3.provider.ImportDataTask$HotseatParserCallback r6 = new com.android.launcher3.provider.ImportDataTask$HotseatParserCallback
            int r16 = r4 + 1
            r12 = r6
            r13 = r5
            r14 = r0
            r15 = r7
            r17 = r2
            r12.<init>(r13, r14, r15, r16, r17)
            com.android.launcher3.provider.ImportDataTask$HotseatLayoutParser r8 = new com.android.launcher3.provider.ImportDataTask$HotseatLayoutParser
            android.content.Context r9 = r1.mContext
            r8.<init>(r9, r6)
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            r10 = 0
            r8.loadLayout(r10, r9)
            int r8 = r0.size()
            r9 = 1
            int r8 = r8 - r9
            long r10 = r0.keyAt(r8)
            int r8 = (int) r10
            int r8 = r8 + r9
            r1.mHotseatSize = r8
            boolean r8 = r7.isEmpty()
            if (r8 != 0) goto L_0x0577
            android.content.Context r8 = r1.mContext
            android.content.ContentResolver r8 = r8.getContentResolver()
            java.lang.String r9 = com.android.launcher3.LauncherProvider.AUTHORITY
            r8.applyBatch(r9, r7)
        L_0x0577:
            return
        L_0x0578:
            java.lang.Exception r0 = new java.lang.Exception
            java.lang.String r2 = "Insufficient data"
            r0.<init>(r2)
            throw r0
        L_0x0580:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r3 = r31
            r10 = 0
            r2 = r0
            r43 = r4
        L_0x058d:
            goto L_0x0649
        L_0x058f:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r3 = r31
            r2 = r0
            r9 = r3
            r8 = r4
        L_0x059b:
            goto L_0x0641
        L_0x059d:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r10 = 0
            r2 = r0
            r43 = r23
            r31 = r24
            goto L_0x0649
        L_0x05ac:
            r0 = move-exception
            r45 = r3
            r7 = r25
            r5 = r27
            r2 = r0
            r8 = r23
            r9 = r24
            goto L_0x0641
        L_0x05ba:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r7 = r25
            r10 = 0
            r2 = r0
            r43 = r23
            r31 = r24
            goto L_0x0649
        L_0x05c8:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r7 = r25
            r2 = r0
            r8 = r23
            r9 = r24
            goto L_0x0641
        L_0x05d5:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r10 = 0
            r7 = r4
            r2 = r0
            r43 = r23
            r31 = r24
            goto L_0x0649
        L_0x05e2:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r7 = r4
            r2 = r0
            r8 = r23
            r9 = r24
            goto L_0x0641
        L_0x05ee:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r24 = r9
            r10 = 0
            r7 = r4
            r2 = r0
            r43 = r23
            r31 = r24
            goto L_0x0649
        L_0x05fd:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r24 = r9
            r7 = r4
            r2 = r0
            r8 = r23
            goto L_0x0641
        L_0x0608:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r23 = r8
            r24 = r9
            r10 = 0
            r7 = r4
            r2 = r0
            r43 = r23
            r31 = r24
            goto L_0x0649
        L_0x0618:
            r0 = move-exception
            r45 = r3
            r5 = r7
            r23 = r8
            r24 = r9
            r7 = r4
            r2 = r0
            goto L_0x0641
        L_0x0623:
            r0 = move-exception
            r22 = r2
            r45 = r3
            r5 = r7
            r23 = r8
            r24 = r9
            r10 = 0
            r7 = r4
            r2 = r0
            r43 = r23
            r31 = r24
            goto L_0x0649
        L_0x0635:
            r0 = move-exception
            r22 = r2
            r45 = r3
            r5 = r7
            r23 = r8
            r24 = r9
            r7 = r4
            r2 = r0
        L_0x0641:
            throw r2     // Catch:{ all -> 0x0642 }
        L_0x0642:
            r0 = move-exception
            r10 = r2
            r43 = r8
            r31 = r9
            r2 = r0
        L_0x0649:
            if (r12 == 0) goto L_0x065a
            if (r10 == 0) goto L_0x0657
            r12.close()     // Catch:{ Throwable -> 0x0651 }
            goto L_0x065a
        L_0x0651:
            r0 = move-exception
            r3 = r0
            r10.addSuppressed(r3)
            goto L_0x065a
        L_0x0657:
            r12.close()
        L_0x065a:
            throw r2
        L_0x065b:
            r0 = move-exception
            r22 = r2
            r10 = 0
            r2 = r0
            r5 = r10
            goto L_0x0669
        L_0x0662:
            r0 = move-exception
            r5 = r0
            r22 = r2
            throw r5     // Catch:{ all -> 0x0667 }
        L_0x0667:
            r0 = move-exception
            r2 = r0
        L_0x0669:
            if (r4 == 0) goto L_0x067a
            if (r5 == 0) goto L_0x0677
            r4.close()     // Catch:{ Throwable -> 0x0671 }
            goto L_0x067a
        L_0x0671:
            r0 = move-exception
            r6 = r0
            r5.addSuppressed(r6)
            goto L_0x067a
        L_0x0677:
            r4.close()
        L_0x067a:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.provider.ImportDataTask.importWorkspaceItems(long, android.util.LongSparseArray):void");
    }

    /* access modifiers changed from: private */
    public static String getPackage(Intent intent) {
        if (intent.getComponent() != null) {
            return intent.getComponent().getPackageName();
        }
        return intent.getPackage();
    }

    public static boolean performImportIfPossible(Context context) throws Exception {
        SharedPreferences devicePrefs = Utilities.getDevicePrefs(context);
        String sourcePackage = devicePrefs.getString(KEY_DATA_IMPORT_SRC_PKG, "");
        String sourceAuthority = devicePrefs.getString(KEY_DATA_IMPORT_SRC_AUTHORITY, "");
        if (TextUtils.isEmpty(sourcePackage) || TextUtils.isEmpty(sourceAuthority)) {
            return false;
        }
        devicePrefs.edit().remove(KEY_DATA_IMPORT_SRC_PKG).remove(KEY_DATA_IMPORT_SRC_AUTHORITY).commit();
        if (!LauncherSettings.Settings.call(context.getContentResolver(), LauncherSettings.Settings.METHOD_WAS_EMPTY_DB_CREATED).getBoolean(LauncherSettings.Settings.EXTRA_VALUE, false)) {
            return false;
        }
        for (ProviderInfo info : context.getPackageManager().queryContentProviders((String) null, context.getApplicationInfo().uid, 0)) {
            if (sourcePackage.equals(info.packageName)) {
                if ((info.applicationInfo.flags & 1) == 0) {
                    return false;
                }
                if (sourceAuthority.equals(info.authority) && (TextUtils.isEmpty(info.readPermission) || context.checkPermission(info.readPermission, Process.myPid(), Process.myUid()) == 0)) {
                    return new ImportDataTask(context, sourceAuthority).importWorkspace();
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static int getMyHotseatLayoutId(Context context) {
        return LauncherAppState.getIDP(context).numHotseatIcons <= 5 ? R.xml.dw_phone_hotseat : R.xml.dw_tablet_hotseat;
    }

    private static class HotseatLayoutParser extends DefaultLayoutParser {
        public HotseatLayoutParser(Context context, AutoInstallsLayout.LayoutParserCallback callback) {
            super(context, (AppWidgetHost) null, callback, context.getResources(), ImportDataTask.getMyHotseatLayoutId(context));
        }

        /* access modifiers changed from: protected */
        public ArrayMap<String, AutoInstallsLayout.TagParser> getLayoutElementsMap() {
            ArrayMap<String, AutoInstallsLayout.TagParser> parsers = new ArrayMap<>();
            parsers.put("favorite", new DefaultLayoutParser.AppShortcutWithUriParser());
            parsers.put("shortcut", new DefaultLayoutParser.UriShortcutParser(this.mSourceRes));
            parsers.put("resolve", new DefaultLayoutParser.ResolveParser());
            return parsers;
        }
    }

    private static class HotseatParserCallback implements AutoInstallsLayout.LayoutParserCallback {
        private final HashSet<String> mExistingApps;
        private final LongArrayMap<Object> mExistingItems;
        private final ArrayList<ContentProviderOperation> mOutOps;
        private final int mRequiredSize;
        private int mStartItemId;

        HotseatParserCallback(HashSet<String> existingApps, LongArrayMap<Object> existingItems, ArrayList<ContentProviderOperation> outOps, int startItemId, int requiredSize) {
            this.mExistingApps = existingApps;
            this.mExistingItems = existingItems;
            this.mOutOps = outOps;
            this.mRequiredSize = requiredSize;
            this.mStartItemId = startItemId;
        }

        public long generateNewItemId() {
            int i = this.mStartItemId;
            this.mStartItemId = i + 1;
            return (long) i;
        }

        public long insertAndCheck(SQLiteDatabase db, ContentValues values) {
            if (this.mExistingItems.size() >= this.mRequiredSize) {
                return 0;
            }
            try {
                Intent intent = Intent.parseUri(values.getAsString(LauncherSettings.BaseLauncherColumns.INTENT), 0);
                String pkg = ImportDataTask.getPackage(intent);
                if (pkg == null || this.mExistingApps.contains(pkg)) {
                    return 0;
                }
                this.mExistingApps.add(pkg);
                long screen = 0;
                while (this.mExistingItems.get(screen) != null) {
                    screen++;
                }
                this.mExistingItems.put(screen, intent);
                values.put(LauncherSettings.Favorites.SCREEN, Long.valueOf(screen));
                this.mOutOps.add(ContentProviderOperation.newInsert(LauncherSettings.Favorites.CONTENT_URI).withValues(values).build());
                return 0;
            } catch (URISyntaxException e) {
                return 0;
            }
        }
    }
}
