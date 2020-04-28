package com.android.launcher3;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.util.ContentWriter;

public class ItemInfo {
    public static final int NO_ID = -1;
    public int cellX;
    public int cellY;
    public long container;
    public CharSequence contentDescription;
    public long id;
    public int itemType;
    public int minSpanX;
    public int minSpanY;
    public int rank;
    public long screenId;
    public int spanX;
    public int spanY;
    public CharSequence title;
    public UserHandle user;

    public ItemInfo() {
        this.id = -1;
        this.container = -1;
        this.screenId = -1;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.rank = 0;
        this.user = Process.myUserHandle();
    }

    ItemInfo(ItemInfo info) {
        this.id = -1;
        this.container = -1;
        this.screenId = -1;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.rank = 0;
        copyFrom(info);
        LauncherModel.checkItemInfo(this);
    }

    public void copyFrom(ItemInfo info) {
        this.id = info.id;
        this.cellX = info.cellX;
        this.cellY = info.cellY;
        this.spanX = info.spanX;
        this.spanY = info.spanY;
        this.rank = info.rank;
        this.screenId = info.screenId;
        this.itemType = info.itemType;
        this.container = info.container;
        this.user = info.user;
        this.contentDescription = info.contentDescription;
    }

    public Intent getIntent() {
        return null;
    }

    public ComponentName getTargetComponent() {
        Intent intent = getIntent();
        if (intent != null) {
            return intent.getComponent();
        }
        return null;
    }

    public void writeToValues(ContentWriter writer) {
        writer.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType)).put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(this.container)).put(LauncherSettings.Favorites.SCREEN, Long.valueOf(this.screenId)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY)).put(LauncherSettings.Favorites.SPANX, Integer.valueOf(this.spanX)).put(LauncherSettings.Favorites.SPANY, Integer.valueOf(this.spanY)).put(LauncherSettings.Favorites.RANK, Integer.valueOf(this.rank));
    }

    public void readFromValues(ContentValues values) {
        this.itemType = values.getAsInteger(LauncherSettings.BaseLauncherColumns.ITEM_TYPE).intValue();
        this.container = values.getAsLong(LauncherSettings.Favorites.CONTAINER).longValue();
        this.screenId = values.getAsLong(LauncherSettings.Favorites.SCREEN).longValue();
        this.cellX = values.getAsInteger(LauncherSettings.Favorites.CELLX).intValue();
        this.cellY = values.getAsInteger(LauncherSettings.Favorites.CELLY).intValue();
        this.spanX = values.getAsInteger(LauncherSettings.Favorites.SPANX).intValue();
        this.spanY = values.getAsInteger(LauncherSettings.Favorites.SPANY).intValue();
        this.rank = values.getAsInteger(LauncherSettings.Favorites.RANK).intValue();
    }

    public void onAddToDatabase(ContentWriter writer) {
        if (this.screenId != -201) {
            writeToValues(writer);
            writer.put(LauncherSettings.Favorites.PROFILE_ID, this.user);
            return;
        }
        throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
    }

    public final String toString() {
        return getClass().getSimpleName() + "(" + dumpProperties() + ")";
    }

    /* access modifiers changed from: protected */
    public String dumpProperties() {
        return "id=" + this.id + " type=" + LauncherSettings.Favorites.itemTypeToString(this.itemType) + " container=" + LauncherSettings.Favorites.containerToString((int) this.container) + " screen=" + this.screenId + " cell(" + this.cellX + "," + this.cellY + ") span(" + this.spanX + "," + this.spanY + ") minSpan(" + this.minSpanX + "," + this.minSpanY + ") rank=" + this.rank + " user=" + this.user + " title=" + this.title;
    }

    public boolean isDisabled() {
        return false;
    }
}
