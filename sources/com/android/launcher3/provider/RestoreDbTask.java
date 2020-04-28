package com.android.launcher3.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.provider.LauncherDbUtils;
import com.android.launcher3.util.LogConfig;

public class RestoreDbTask {
    private static final String INFO_COLUMN_DEFAULT_VALUE = "dflt_value";
    private static final String INFO_COLUMN_NAME = "name";
    private static final String RESTORE_TASK_PENDING = "restore_task_pending";
    private static final String TAG = "RestoreDbTask";

    public static boolean performRestore(LauncherProvider.DatabaseHelper helper) {
        LauncherDbUtils.SQLiteTransaction t;
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            t = new LauncherDbUtils.SQLiteTransaction(db);
            new RestoreDbTask().sanitizeDB(helper, db);
            t.commit();
            t.close();
            return true;
        } catch (Exception e) {
            FileLog.e(TAG, "Failed to verify db", e);
            return false;
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        throw th;
    }

    private void sanitizeDB(LauncherProvider.DatabaseHelper helper, SQLiteDatabase db) throws Exception {
        long oldProfileId = getDefaultProfileId(db);
        int itemsDeleted = db.delete(LauncherSettings.Favorites.TABLE_NAME, "profileId != ?", new String[]{Long.toString(oldProfileId)});
        if (itemsDeleted > 0) {
            FileLog.d(TAG, itemsDeleted + " items belonging to a managed profile, were deleted");
        }
        boolean keepAllIcons = Utilities.isPropertyEnabled(LogConfig.KEEP_ALL_ICONS);
        ContentValues values = new ContentValues();
        int i = 8;
        values.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf((keepAllIcons ? 8 : 0) | 1));
        db.update(LauncherSettings.Favorites.TABLE_NAME, values, (String) null, (String[]) null);
        if (!keepAllIcons) {
            i = 0;
        }
        values.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(i | 7));
        db.update(LauncherSettings.Favorites.TABLE_NAME, values, "itemType = ?", new String[]{Integer.toString(4)});
        long myProfileId = helper.getDefaultUserSerial();
        if (Utilities.longCompare(oldProfileId, myProfileId) != 0) {
            FileLog.d(TAG, "Changing primary user id from " + oldProfileId + " to " + myProfileId);
            migrateProfileId(db, myProfileId);
        }
    }

    /* access modifiers changed from: protected */
    public void migrateProfileId(SQLiteDatabase db, long newProfileId) {
        ContentValues values = new ContentValues();
        values.put(LauncherSettings.Favorites.PROFILE_ID, Long.valueOf(newProfileId));
        db.update(LauncherSettings.Favorites.TABLE_NAME, values, (String) null, (String[]) null);
        db.execSQL("ALTER TABLE favorites RENAME TO favorites_old;");
        LauncherSettings.Favorites.addTableToDb(db, newProfileId, false);
        db.execSQL("INSERT INTO favorites SELECT * FROM favorites_old;");
        db.execSQL("DROP TABLE favorites_old;");
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0037, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
        if (r0 != null) goto L_0x003d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003d, code lost:
        if (r1 != null) goto L_0x003f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0043, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0044, code lost:
        r1.addSuppressed(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0048, code lost:
        r0.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getDefaultProfileId(android.database.sqlite.SQLiteDatabase r6) throws java.lang.Exception {
        /*
            r5 = this;
            java.lang.String r0 = "PRAGMA table_info (favorites)"
            r1 = 0
            android.database.Cursor r0 = r6.rawQuery(r0, r1)
            java.lang.String r2 = "name"
            int r2 = r0.getColumnIndex(r2)     // Catch:{ Throwable -> 0x0039 }
        L_0x000d:
            boolean r3 = r0.moveToNext()     // Catch:{ Throwable -> 0x0039 }
            if (r3 == 0) goto L_0x002f
            java.lang.String r3 = "profileId"
            java.lang.String r4 = r0.getString(r2)     // Catch:{ Throwable -> 0x0039 }
            boolean r3 = r3.equals(r4)     // Catch:{ Throwable -> 0x0039 }
            if (r3 == 0) goto L_0x000d
            java.lang.String r3 = "dflt_value"
            int r3 = r0.getColumnIndex(r3)     // Catch:{ Throwable -> 0x0039 }
            long r3 = r0.getLong(r3)     // Catch:{ Throwable -> 0x0039 }
            if (r0 == 0) goto L_0x002e
            r0.close()
        L_0x002e:
            return r3
        L_0x002f:
            java.io.InvalidObjectException r3 = new java.io.InvalidObjectException     // Catch:{ Throwable -> 0x0039 }
            java.lang.String r4 = "Table does not have a profile id column"
            r3.<init>(r4)     // Catch:{ Throwable -> 0x0039 }
            throw r3     // Catch:{ Throwable -> 0x0039 }
        L_0x0037:
            r2 = move-exception
            goto L_0x003b
        L_0x0039:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0037 }
        L_0x003b:
            if (r0 == 0) goto L_0x004b
            if (r1 == 0) goto L_0x0048
            r0.close()     // Catch:{ Throwable -> 0x0043 }
            goto L_0x004b
        L_0x0043:
            r3 = move-exception
            r1.addSuppressed(r3)
            goto L_0x004b
        L_0x0048:
            r0.close()
        L_0x004b:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.provider.RestoreDbTask.getDefaultProfileId(android.database.sqlite.SQLiteDatabase):long");
    }

    public static boolean isPending(Context context) {
        return Utilities.getPrefs(context).getBoolean(RESTORE_TASK_PENDING, false);
    }

    public static void setPending(Context context, boolean isPending) {
        FileLog.d(TAG, "Restore data received through full backup " + isPending);
        Utilities.getPrefs(context).edit().putBoolean(RESTORE_TASK_PENDING, isPending).commit();
    }
}
