package com.android.launcher3.model;

import android.util.SparseArray;
import com.android.launcher3.util.IOUtils;
import java.io.File;
import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DbDowngradeHelper {
    private static final String KEY_DOWNGRADE_TO = "downgrade_to_";
    private static final String KEY_VERSION = "version";
    private static final String TAG = "DbDowngradeHelper";
    private final SparseArray<String[]> mStatements = new SparseArray<>();
    public final int version;

    private DbDowngradeHelper(int version2) {
        this.version = version2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0051, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        if (r2 != null) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005b, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005c, code lost:
        r2.addSuppressed(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0060, code lost:
        r1.close();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDowngrade(android.database.sqlite.SQLiteDatabase r7, int r8, int r9) {
        /*
            r6 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            int r1 = r8 + -1
        L_0x0007:
            if (r1 < r9) goto L_0x0030
            android.util.SparseArray<java.lang.String[]> r2 = r6.mStatements
            java.lang.Object r2 = r2.get(r1)
            java.lang.String[] r2 = (java.lang.String[]) r2
            if (r2 == 0) goto L_0x0019
            java.util.Collections.addAll(r0, r2)
            int r1 = r1 + -1
            goto L_0x0007
        L_0x0019:
            android.database.sqlite.SQLiteException r3 = new android.database.sqlite.SQLiteException
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Downgrade path not supported to version "
            r4.append(r5)
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4)
            throw r3
        L_0x0030:
            com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction r1 = new com.android.launcher3.provider.LauncherDbUtils$SQLiteTransaction
            r1.<init>(r7)
            r2 = 0
            java.util.Iterator r3 = r0.iterator()     // Catch:{ Throwable -> 0x0053 }
        L_0x003a:
            boolean r4 = r3.hasNext()     // Catch:{ Throwable -> 0x0053 }
            if (r4 == 0) goto L_0x004a
            java.lang.Object r4 = r3.next()     // Catch:{ Throwable -> 0x0053 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ Throwable -> 0x0053 }
            r7.execSQL(r4)     // Catch:{ Throwable -> 0x0053 }
            goto L_0x003a
        L_0x004a:
            r1.commit()     // Catch:{ Throwable -> 0x0053 }
            r1.close()
            return
        L_0x0051:
            r3 = move-exception
            goto L_0x0055
        L_0x0053:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0051 }
        L_0x0055:
            if (r2 == 0) goto L_0x0060
            r1.close()     // Catch:{ Throwable -> 0x005b }
            goto L_0x0063
        L_0x005b:
            r4 = move-exception
            r2.addSuppressed(r4)
            goto L_0x0063
        L_0x0060:
            r1.close()
        L_0x0063:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.DbDowngradeHelper.onDowngrade(android.database.sqlite.SQLiteDatabase, int, int):void");
    }

    public static DbDowngradeHelper parse(File file) throws JSONException, IOException {
        JSONObject obj = new JSONObject(new String(IOUtils.toByteArray(file)));
        DbDowngradeHelper helper = new DbDowngradeHelper(obj.getInt(KEY_VERSION));
        for (int version2 = helper.version - 1; version2 > 0; version2--) {
            if (obj.has(KEY_DOWNGRADE_TO + version2)) {
                JSONArray statements = obj.getJSONArray(KEY_DOWNGRADE_TO + version2);
                String[] parsed = new String[statements.length()];
                for (int i = 0; i < parsed.length; i++) {
                    parsed[i] = statements.getString(i);
                }
                helper.mStatements.put(version2, parsed);
            }
        }
        return helper;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0026, code lost:
        r3 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0027, code lost:
        r4 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002b, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x002c, code lost:
        r6 = r4;
        r4 = r3;
        r3 = r6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void updateSchemaFile(java.io.File r7, int r8, android.content.Context r9, int r10) {
        /*
            com.android.launcher3.model.DbDowngradeHelper r0 = parse(r7)     // Catch:{ Exception -> 0x000a }
            int r0 = r0.version     // Catch:{ Exception -> 0x000a }
            if (r0 < r8) goto L_0x0009
            return
        L_0x0009:
            goto L_0x000b
        L_0x000a:
            r0 = move-exception
        L_0x000b:
            java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0053 }
            r0.<init>(r7)     // Catch:{ IOException -> 0x0053 }
            r1 = 0
            android.content.res.Resources r2 = r9.getResources()     // Catch:{ Throwable -> 0x0042 }
            java.io.InputStream r2 = r2.openRawResource(r10)     // Catch:{ Throwable -> 0x0042 }
            com.android.launcher3.util.IOUtils.copy(r2, r0)     // Catch:{ Throwable -> 0x0029, all -> 0x0026 }
            if (r2 == 0) goto L_0x0022
            r2.close()     // Catch:{ Throwable -> 0x0042 }
        L_0x0022:
            r0.close()     // Catch:{ IOException -> 0x0053 }
            goto L_0x005b
        L_0x0026:
            r3 = move-exception
            r4 = r1
            goto L_0x002f
        L_0x0029:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002b }
        L_0x002b:
            r4 = move-exception
            r6 = r4
            r4 = r3
            r3 = r6
        L_0x002f:
            if (r2 == 0) goto L_0x003f
            if (r4 == 0) goto L_0x003c
            r2.close()     // Catch:{ Throwable -> 0x0037 }
            goto L_0x003f
        L_0x0037:
            r5 = move-exception
            r4.addSuppressed(r5)     // Catch:{ Throwable -> 0x0042 }
            goto L_0x003f
        L_0x003c:
            r2.close()     // Catch:{ Throwable -> 0x0042 }
        L_0x003f:
            throw r3     // Catch:{ Throwable -> 0x0042 }
        L_0x0040:
            r2 = move-exception
            goto L_0x0044
        L_0x0042:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0040 }
        L_0x0044:
            if (r1 == 0) goto L_0x004f
            r0.close()     // Catch:{ Throwable -> 0x004a }
            goto L_0x0052
        L_0x004a:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ IOException -> 0x0053 }
            goto L_0x0052
        L_0x004f:
            r0.close()     // Catch:{ IOException -> 0x0053 }
        L_0x0052:
            throw r2     // Catch:{ IOException -> 0x0053 }
        L_0x0053:
            r0 = move-exception
            java.lang.String r1 = "DbDowngradeHelper"
            java.lang.String r2 = "Error writing schema file"
            android.util.Log.e(r1, r2, r0)
        L_0x005b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.DbDowngradeHelper.updateSchemaFile(java.io.File, int, android.content.Context, int):void");
    }
}
