package com.szchoiceway.index;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SysVarProvider extends ContentProvider {
    private static final int SYSTEMVAR = 2;
    private static final int SYSTEMVARS = 1;
    private static final String TAG = "SysVarProvider";
    private static final UriMatcher URI_MATCHER = new UriMatcher(-1);
    private SysVarDbMan mDbManager;

    static {
        URI_MATCHER.addURI("com.szchoiceway.eventcenter.SysVarProvider", "SysVar", 1);
        URI_MATCHER.addURI("com.szchoiceway.eventcenter.SysVarProvider", "SysVar/#", 2);
    }

    public boolean onCreate() {
        this.mDbManager = new SysVarDbMan(getContext());
        Log.i(TAG, "mDbManager *** = " + this.mDbManager);
        return true;
    }

    public void shutdown() {
        super.shutdown();
        if (this.mDbManager != null) {
            this.mDbManager.closeDB();
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = -1;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                if (this.mDbManager != null) {
                    count = this.mDbManager.deleteSysVar(selection, selectionArgs);
                    break;
                }
                break;
            case 2:
                String[] args = {String.valueOf(ContentUris.parseId(uri))};
                if (this.mDbManager != null) {
                    count = this.mDbManager.deleteSysVar(" _id = ?", args);
                    break;
                }
                break;
        }
        Log.i(TAG, "---->>delete count = " + count);
        return count;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        Uri resultUri = null;
        long id = 0;
        if (URI_MATCHER.match(uri) == 1) {
            if (this.mDbManager != null) {
                id = this.mDbManager.insertSysVar(values);
            }
            resultUri = ContentUris.withAppendedId(uri, id);
        }
        Log.i(TAG, "---->>insert id = " + id);
        return resultUri;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                if (this.mDbManager != null) {
                    cursor = this.mDbManager.querySysVar(projection, selection, selectionArgs, sortOrder);
                    break;
                }
                break;
            case 2:
                String[] args = {String.valueOf(ContentUris.parseId(uri))};
                if (this.mDbManager != null) {
                    cursor = this.mDbManager.querySysVar(projection, " _id = ?", args, sortOrder);
                    break;
                }
                break;
        }
        Log.i(TAG, "---->>query Count = " + cursor.getCount());
        return cursor;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = -1;
        switch (URI_MATCHER.match(uri)) {
            case 1:
                count = this.mDbManager.updateSysVar(values, selection, selectionArgs);
                break;
            case 2:
                count = this.mDbManager.updateSysVar(values, "_id = ?", new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
        }
        Log.i(TAG, "---->>update Count = " + count);
        return count;
    }

    class SysVarDbMan {
        SQLiteDatabase database = null;
        private DBHelper helper = null;

        public SysVarDbMan(Context context) {
            this.helper = new DBHelper(context);
            this.database = this.helper.getWritableDatabase();
        }

        public void closeDB() {
            if (this.database != null) {
                this.database.close();
                this.database = null;
            }
        }

        public long insertSysVar(ContentValues values) {
            try {
                if (this.database != null) {
                    return this.database.insert("sysvar", (String) null, values);
                }
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        public int deleteSysVar(String whereClause, String[] whereArgs) {
            try {
                if (this.database != null) {
                    return this.database.delete("sysvar", whereClause, whereArgs);
                }
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        public int updateSysVar(ContentValues values, String whereClause, String[] whereArgs) {
            try {
                if (this.database != null) {
                    return this.database.update("sysvar", values, whereClause, whereArgs);
                }
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }

        public Cursor querySysVar(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
            try {
                if (this.database != null) {
                    return this.database.query(true, "sysvar", projection, selection, selectionArgs, (String) null, (String) null, sortOrder, (String) null);
                }
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        class DBHelper extends SQLiteOpenHelper {
            private static final String DATABASE_NAME = "SysVar.db";
            private static final int DATABASE_VERSION = 1;

            public DBHelper(Context context) {
                super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 1);
            }

            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE IF NOT EXISTS sysvar (keyname VARCHAR PRIMARY KEY, keyvalue VARCHAR)");
            }

            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        }
    }
}
