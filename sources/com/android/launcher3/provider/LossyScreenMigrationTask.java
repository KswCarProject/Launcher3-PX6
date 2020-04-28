package com.android.launcher3.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.model.GridSizeMigrationTask;
import com.android.launcher3.util.LongArrayMap;
import java.util.ArrayList;
import java.util.Iterator;

public class LossyScreenMigrationTask extends GridSizeMigrationTask {
    private final SQLiteDatabase mDb;
    private final LongArrayMap<GridSizeMigrationTask.DbEntry> mOriginalItems = new LongArrayMap<>();
    private final LongArrayMap<GridSizeMigrationTask.DbEntry> mUpdates = new LongArrayMap<>();

    protected LossyScreenMigrationTask(Context context, InvariantDeviceProfile idp, SQLiteDatabase db) {
        super(context, idp, getValidPackages(context), new Point(idp.numColumns, idp.numRows + 1), new Point(idp.numColumns, idp.numRows));
        this.mDb = db;
    }

    /* access modifiers changed from: protected */
    public Cursor queryWorkspace(String[] columns, String where) {
        return this.mDb.query(LauncherSettings.Favorites.TABLE_NAME, columns, where, (String[]) null, (String) null, (String) null, (String) null);
    }

    /* access modifiers changed from: protected */
    public void update(GridSizeMigrationTask.DbEntry item) {
        this.mUpdates.put(item.id, item.copy());
    }

    /* access modifiers changed from: protected */
    public ArrayList<GridSizeMigrationTask.DbEntry> loadWorkspaceEntries(long screen) {
        ArrayList<GridSizeMigrationTask.DbEntry> result = super.loadWorkspaceEntries(screen);
        Iterator<GridSizeMigrationTask.DbEntry> it = result.iterator();
        while (it.hasNext()) {
            GridSizeMigrationTask.DbEntry entry = it.next();
            this.mOriginalItems.put(entry.id, entry.copy());
            entry.cellY++;
            this.mUpdates.put(entry.id, entry.copy());
        }
        return result;
    }

    public void migrateScreen0() {
        migrateScreen(0);
        ContentValues tempValues = new ContentValues();
        Iterator<GridSizeMigrationTask.DbEntry> it = this.mUpdates.iterator();
        while (it.hasNext()) {
            GridSizeMigrationTask.DbEntry update = it.next();
            GridSizeMigrationTask.DbEntry org = (GridSizeMigrationTask.DbEntry) this.mOriginalItems.get(update.id);
            if (org.cellX != update.cellX || org.cellY != update.cellY || org.spanX != update.spanX || org.spanY != update.spanY) {
                tempValues.clear();
                update.addToContentValues(tempValues);
                this.mDb.update(LauncherSettings.Favorites.TABLE_NAME, tempValues, "_id = ?", new String[]{Long.toString(update.id)});
            }
        }
        Iterator it2 = this.mCarryOver.iterator();
        while (it2.hasNext()) {
            this.mEntryToRemove.add(Long.valueOf(((GridSizeMigrationTask.DbEntry) it2.next()).id));
        }
        if (!this.mEntryToRemove.isEmpty()) {
            this.mDb.delete(LauncherSettings.Favorites.TABLE_NAME, Utilities.createDbSelectionQuery("_id", this.mEntryToRemove), (String[]) null);
        }
    }
}
