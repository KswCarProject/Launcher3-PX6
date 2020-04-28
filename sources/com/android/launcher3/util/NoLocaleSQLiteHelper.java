package com.android.launcher3.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.launcher3.Utilities;

public abstract class NoLocaleSQLiteHelper extends SQLiteOpenHelper {
    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public NoLocaleSQLiteHelper(Context context, String name, int version) {
        super(Utilities.ATLEAST_P ? context : new NoLocalContext(context), name, (SQLiteDatabase.CursorFactory) null, version);
        if (Utilities.ATLEAST_P) {
            setOpenParams(new SQLiteDatabase.OpenParams.Builder().addOpenFlags(16).build());
        }
    }

    private static class NoLocalContext extends ContextWrapper {
        public NoLocalContext(Context base) {
            super(base);
        }

        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            return super.openOrCreateDatabase(name, mode | 16, factory, errorHandler);
        }
    }
}
