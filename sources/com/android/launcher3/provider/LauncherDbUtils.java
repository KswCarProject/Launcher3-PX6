package com.android.launcher3.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import java.util.ArrayList;
import java.util.Collection;

public class LauncherDbUtils {
    private static final String TAG = "LauncherDbUtils";

    public static boolean prepareScreenZeroToHostQsb(Context context, SQLiteDatabase db) {
        SQLiteTransaction t;
        try {
            t = new SQLiteTransaction(db);
            ArrayList<Long> screenIds = getScreenIdsFromCursor(db.query(LauncherSettings.WorkspaceScreens.TABLE_NAME, (String[]) null, (String) null, (String[]) null, (String) null, (String) null, LauncherSettings.WorkspaceScreens.SCREEN_RANK));
            if (screenIds.isEmpty()) {
                t.commit();
                t.close();
                return true;
            }
            if (screenIds.get(0).longValue() != 0) {
                if (screenIds.indexOf(0L) > -1) {
                    long newScreenId = 1;
                    while (screenIds.indexOf(Long.valueOf(newScreenId)) > -1) {
                        newScreenId++;
                    }
                    renameScreen(db, 0, newScreenId);
                }
                renameScreen(db, screenIds.get(0).longValue(), 0);
            }
            if (DatabaseUtils.queryNumEntries(db, LauncherSettings.Favorites.TABLE_NAME, "container = -100 and screen = 0 and cellY = 0") == 0) {
                t.commit();
                t.close();
                return true;
            }
            new LossyScreenMigrationTask(context, LauncherAppState.getIDP(context), db).migrateScreen0();
            t.commit();
            t.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to update workspace size", e);
            return false;
        } catch (Throwable th) {
            r2.addSuppressed(th);
        }
        throw th;
    }

    private static void renameScreen(SQLiteDatabase db, long oldScreen, long newScreen) {
        String[] whereParams = {Long.toString(oldScreen)};
        ContentValues values = new ContentValues();
        values.put("_id", Long.valueOf(newScreen));
        db.update(LauncherSettings.WorkspaceScreens.TABLE_NAME, values, "_id = ?", whereParams);
        values.clear();
        values.put(LauncherSettings.Favorites.SCREEN, Long.valueOf(newScreen));
        db.update(LauncherSettings.Favorites.TABLE_NAME, values, "container = -100 and screen = ?", whereParams);
    }

    public static ArrayList<Long> getScreenIdsFromCursor(Cursor sc) {
        try {
            return (ArrayList) iterateCursor(sc, sc.getColumnIndexOrThrow("_id"), new ArrayList());
        } finally {
            sc.close();
        }
    }

    public static <T extends Collection<Long>> T iterateCursor(Cursor c, int columnIndex, T out) {
        while (c.moveToNext()) {
            out.add(Long.valueOf(c.getLong(columnIndex)));
        }
        return out;
    }

    public static class SQLiteTransaction implements AutoCloseable {
        private final SQLiteDatabase mDb;

        public SQLiteTransaction(SQLiteDatabase db) {
            this.mDb = db;
            db.beginTransaction();
        }

        public void commit() {
            this.mDb.setTransactionSuccessful();
        }

        public void close() {
            this.mDb.endTransaction();
        }
    }
}
