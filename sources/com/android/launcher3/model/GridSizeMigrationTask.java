package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.CancellationSignal;
import android.support.v7.widget.ActivityChooserView;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.PackageInstallerCompat;
import com.android.launcher3.util.GridOccupancy;
import com.android.launcher3.util.LongArrayMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public class GridSizeMigrationTask {
    private static final boolean DEBUG = true;
    public static boolean ENABLED = Utilities.ATLEAST_NOUGAT;
    private static final String KEY_MIGRATION_SRC_HOTSEAT_COUNT = "migration_src_hotseat_count";
    private static final String KEY_MIGRATION_SRC_WORKSPACE_SIZE = "migration_src_workspace_size";
    private static final String TAG = "GridSizeMigrationTask";
    private static final float WT_APPLICATION = 0.8f;
    private static final float WT_FOLDER_FACTOR = 0.5f;
    private static final float WT_SHORTCUT = 1.0f;
    private static final float WT_WIDGET_FACTOR = 0.6f;
    private static final float WT_WIDGET_MIN = 2.0f;
    protected final ArrayList<DbEntry> mCarryOver = new ArrayList<>();
    private final Context mContext;
    private final int mDestHotseatSize;
    protected final ArrayList<Long> mEntryToRemove = new ArrayList<>();
    private final InvariantDeviceProfile mIdp;
    private final boolean mShouldRemoveX;
    private final boolean mShouldRemoveY;
    private final int mSrcHotseatSize;
    private final int mSrcX;
    private final int mSrcY;
    private final ContentValues mTempValues = new ContentValues();
    /* access modifiers changed from: private */
    public final int mTrgX;
    /* access modifiers changed from: private */
    public final int mTrgY;
    private final ArrayList<ContentProviderOperation> mUpdateOperations = new ArrayList<>();
    private final HashSet<String> mValidPackages;

    protected GridSizeMigrationTask(Context context, InvariantDeviceProfile idp, HashSet<String> validPackages, Point sourceSize, Point targetSize) {
        this.mContext = context;
        this.mValidPackages = validPackages;
        this.mIdp = idp;
        this.mSrcX = sourceSize.x;
        this.mSrcY = sourceSize.y;
        this.mTrgX = targetSize.x;
        this.mTrgY = targetSize.y;
        boolean z = false;
        this.mShouldRemoveX = this.mTrgX < this.mSrcX;
        this.mShouldRemoveY = this.mTrgY < this.mSrcY ? true : z;
        this.mDestHotseatSize = -1;
        this.mSrcHotseatSize = -1;
    }

    protected GridSizeMigrationTask(Context context, InvariantDeviceProfile idp, HashSet<String> validPackages, int srcHotseatSize, int destHotseatSize) {
        this.mContext = context;
        this.mIdp = idp;
        this.mValidPackages = validPackages;
        this.mSrcHotseatSize = srcHotseatSize;
        this.mDestHotseatSize = destHotseatSize;
        this.mTrgY = -1;
        this.mTrgX = -1;
        this.mSrcY = -1;
        this.mSrcX = -1;
        this.mShouldRemoveY = false;
        this.mShouldRemoveX = false;
    }

    private boolean applyOperations() throws Exception {
        if (!this.mUpdateOperations.isEmpty()) {
            this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, this.mUpdateOperations);
        }
        if (!this.mEntryToRemove.isEmpty()) {
            Log.d(TAG, "Removing items: " + TextUtils.join(", ", this.mEntryToRemove));
            this.mContext.getContentResolver().delete(LauncherSettings.Favorites.CONTENT_URI, Utilities.createDbSelectionQuery("_id", this.mEntryToRemove), (String[]) null);
        }
        return !this.mUpdateOperations.isEmpty() || !this.mEntryToRemove.isEmpty();
    }

    /* access modifiers changed from: protected */
    public boolean migrateHotseat() throws Exception {
        ArrayList<DbEntry> items = loadHotseatEntries();
        int requiredCount = this.mDestHotseatSize;
        while (items.size() > requiredCount) {
            DbEntry toRemove = items.get(items.size() / 2);
            Iterator<DbEntry> it = items.iterator();
            while (it.hasNext()) {
                DbEntry entry = it.next();
                if (entry.weight < toRemove.weight) {
                    toRemove = entry;
                }
            }
            this.mEntryToRemove.add(Long.valueOf(toRemove.id));
            items.remove(toRemove);
        }
        int newScreenId = 0;
        Iterator<DbEntry> it2 = items.iterator();
        while (it2.hasNext()) {
            DbEntry entry2 = it2.next();
            if (entry2.screenId != ((long) newScreenId)) {
                entry2.screenId = (long) newScreenId;
                entry2.cellX = newScreenId;
                entry2.cellY = 0;
                update(entry2);
            }
            newScreenId++;
        }
        return applyOperations();
    }

    /* access modifiers changed from: protected */
    public boolean migrateWorkspace() throws Exception {
        ArrayList<Long> allScreens = LauncherModel.loadWorkspaceScreensDb(this.mContext);
        if (!allScreens.isEmpty()) {
            Iterator<Long> it = allScreens.iterator();
            while (it.hasNext()) {
                long screenId = it.next().longValue();
                Log.d(TAG, "Migrating " + screenId);
                migrateScreen(screenId);
            }
            if (!this.mCarryOver.isEmpty()) {
                LongArrayMap<DbEntry> itemMap = new LongArrayMap<>();
                Iterator<DbEntry> it2 = this.mCarryOver.iterator();
                while (it2.hasNext()) {
                    DbEntry e = it2.next();
                    itemMap.put(e.id, e);
                }
                do {
                    OptimalPlacementSolution optimalPlacementSolution = new OptimalPlacementSolution(new GridOccupancy(this.mTrgX, this.mTrgY), deepCopy(this.mCarryOver), 0, true);
                    optimalPlacementSolution.find();
                    if (optimalPlacementSolution.finalPlacedItems.size() > 0) {
                        long newScreenId = LauncherSettings.Settings.call(this.mContext.getContentResolver(), LauncherSettings.Settings.METHOD_NEW_SCREEN_ID).getLong(LauncherSettings.Settings.EXTRA_VALUE);
                        allScreens.add(Long.valueOf(newScreenId));
                        Iterator<DbEntry> it3 = optimalPlacementSolution.finalPlacedItems.iterator();
                        while (it3.hasNext()) {
                            DbEntry item = it3.next();
                            if (this.mCarryOver.remove(itemMap.get(item.id))) {
                                item.screenId = newScreenId;
                                update(item);
                            } else {
                                throw new Exception("Unable to find matching items");
                            }
                        }
                    } else {
                        throw new Exception("None of the items can be placed on an empty screen");
                    }
                } while (!this.mCarryOver.isEmpty());
                Uri uri = LauncherSettings.WorkspaceScreens.CONTENT_URI;
                this.mUpdateOperations.add(ContentProviderOperation.newDelete(uri).build());
                int count = allScreens.size();
                for (int i = 0; i < count; i++) {
                    ContentValues v = new ContentValues();
                    v.put("_id", Long.valueOf(allScreens.get(i).longValue()));
                    v.put(LauncherSettings.WorkspaceScreens.SCREEN_RANK, Integer.valueOf(i));
                    this.mUpdateOperations.add(ContentProviderOperation.newInsert(uri).withValues(v).build());
                }
            }
            return applyOperations();
        }
        throw new Exception("Unable to get workspace screens");
    }

    /* access modifiers changed from: protected */
    public void migrateScreen(long screenId) {
        long j = screenId;
        int startY = j == 0 ? 1 : 0;
        ArrayList<DbEntry> items = loadWorkspaceEntries(screenId);
        float[] outLoss = new float[2];
        ArrayList<DbEntry> finalItems = null;
        float moveWt = Float.MAX_VALUE;
        float removeWt = Float.MAX_VALUE;
        int removedRow = Integer.MAX_VALUE;
        int removedCol = Integer.MAX_VALUE;
        int removedCol2 = 0;
        while (true) {
            int x = removedCol2;
            if (x >= this.mSrcX) {
                break;
            }
            int y = this.mSrcY - 1;
            int removedCol3 = removedCol;
            int removedRow2 = removedRow;
            float removeWt2 = removeWt;
            ArrayList<DbEntry> finalItems2 = finalItems;
            float moveWt2 = moveWt;
            while (true) {
                int y2 = y;
                if (y2 < startY) {
                    break;
                }
                int y3 = y2;
                ArrayList<DbEntry> itemsOnScreen = tryRemove(x, y2, startY, deepCopy(items), outLoss);
                if (outLoss[0] < removeWt2 || (outLoss[0] == removeWt2 && outLoss[1] < moveWt2)) {
                    float removeWt3 = outLoss[0];
                    removeWt2 = removeWt3;
                    moveWt2 = outLoss[1];
                    removedCol3 = this.mShouldRemoveX ? x : removedCol3;
                    removedRow2 = this.mShouldRemoveY ? y3 : removedRow2;
                    finalItems2 = itemsOnScreen;
                }
                if (!this.mShouldRemoveY) {
                    break;
                }
                y = y3 - 1;
            }
            removeWt = removeWt2;
            moveWt = moveWt2;
            removedCol = removedCol3;
            removedRow = removedRow2;
            finalItems = finalItems2;
            if (!this.mShouldRemoveX) {
                break;
            }
            removedCol2 = x + 1;
        }
        float removeWt4 = removeWt;
        ArrayList<DbEntry> finalItems3 = finalItems;
        Log.d(TAG, String.format("Removing row %d, column %d on screen %d", new Object[]{Integer.valueOf(removedRow), Integer.valueOf(removedCol), Long.valueOf(screenId)}));
        LongArrayMap longArrayMap = new LongArrayMap();
        Iterator<DbEntry> it = deepCopy(items).iterator();
        while (it.hasNext()) {
            DbEntry e = it.next();
            longArrayMap.put(e.id, e);
        }
        Iterator<DbEntry> it2 = finalItems3.iterator();
        while (it2.hasNext()) {
            DbEntry item = it2.next();
            longArrayMap.remove(item.id);
            if (!item.columnsSame((DbEntry) longArrayMap.get(item.id))) {
                update(item);
            }
        }
        Iterator it3 = longArrayMap.iterator();
        while (it3.hasNext()) {
            this.mCarryOver.add((DbEntry) it3.next());
        }
        if (this.mCarryOver.isEmpty() || removeWt4 != 0.0f) {
            int i = startY;
            return;
        }
        GridOccupancy occupied = new GridOccupancy(this.mTrgX, this.mTrgY);
        float[] fArr = outLoss;
        int startY2 = startY;
        occupied.markCells(0, 0, this.mTrgX, startY, true);
        Iterator<DbEntry> it4 = finalItems3.iterator();
        while (it4.hasNext()) {
            occupied.markCells((ItemInfo) it4.next(), true);
        }
        GridOccupancy gridOccupancy = occupied;
        OptimalPlacementSolution placement = new OptimalPlacementSolution(occupied, deepCopy(this.mCarryOver), startY2, true);
        placement.find();
        if (placement.lowestWeightLoss == 0.0f) {
            Iterator<DbEntry> it5 = placement.finalPlacedItems.iterator();
            while (it5.hasNext()) {
                DbEntry item2 = it5.next();
                item2.screenId = j;
                update(item2);
            }
            this.mCarryOver.clear();
        }
    }

    /* access modifiers changed from: protected */
    public void update(DbEntry item) {
        this.mTempValues.clear();
        item.addToContentValues(this.mTempValues);
        this.mUpdateOperations.add(ContentProviderOperation.newUpdate(LauncherSettings.Favorites.getContentUri(item.id)).withValues(this.mTempValues).build());
    }

    private ArrayList<DbEntry> tryRemove(int col, int row, int startY, ArrayList<DbEntry> items, float[] outLoss) {
        GridOccupancy occupied = new GridOccupancy(this.mTrgX, this.mTrgY);
        occupied.markCells(0, 0, this.mTrgX, startY, true);
        boolean z = this.mShouldRemoveX;
        int i = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        int col2 = z ? col : ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        if (this.mShouldRemoveY) {
            i = row;
        }
        int row2 = i;
        ArrayList<DbEntry> finalItems = new ArrayList<>();
        ArrayList<DbEntry> removedItems = new ArrayList<>();
        Iterator<DbEntry> it = items.iterator();
        while (it.hasNext()) {
            DbEntry item = it.next();
            if ((item.cellX > col2 || item.spanX + item.cellX <= col2) && (item.cellY > row2 || item.spanY + item.cellY <= row2)) {
                if (item.cellX > col2) {
                    item.cellX--;
                }
                if (item.cellY > row2) {
                    item.cellY--;
                }
                finalItems.add(item);
                occupied.markCells((ItemInfo) item, true);
            } else {
                removedItems.add(item);
                if (item.cellX >= col2) {
                    item.cellX--;
                }
                if (item.cellY >= row2) {
                    item.cellY--;
                }
            }
        }
        OptimalPlacementSolution placement = new OptimalPlacementSolution(this, occupied, removedItems, startY);
        placement.find();
        finalItems.addAll(placement.finalPlacedItems);
        outLoss[0] = placement.lowestWeightLoss;
        outLoss[1] = placement.lowestMoveCost;
        return finalItems;
    }

    private class OptimalPlacementSolution {
        ArrayList<DbEntry> finalPlacedItems;
        private final boolean ignoreMove;
        private final ArrayList<DbEntry> itemsToPlace;
        float lowestMoveCost;
        float lowestWeightLoss;
        private final GridOccupancy occupied;
        private final int startY;

        public OptimalPlacementSolution(GridSizeMigrationTask gridSizeMigrationTask, GridOccupancy occupied2, ArrayList<DbEntry> itemsToPlace2, int startY2) {
            this(occupied2, itemsToPlace2, startY2, false);
        }

        public OptimalPlacementSolution(GridOccupancy occupied2, ArrayList<DbEntry> itemsToPlace2, int startY2, boolean ignoreMove2) {
            this.lowestWeightLoss = Float.MAX_VALUE;
            this.lowestMoveCost = Float.MAX_VALUE;
            this.occupied = occupied2;
            this.itemsToPlace = itemsToPlace2;
            this.ignoreMove = ignoreMove2;
            this.startY = startY2;
            Collections.sort(this.itemsToPlace);
        }

        public void find() {
            find(0, 0.0f, 0.0f, new ArrayList());
        }

        public void find(int index, float weightLoss, float moveCost, ArrayList<DbEntry> itemsPlaced) {
            float f;
            int myW;
            float f2;
            int newX;
            int dist;
            int i = index;
            float weightLoss2 = weightLoss;
            float f3 = moveCost;
            ArrayList<DbEntry> arrayList = itemsPlaced;
            if (weightLoss2 >= this.lowestWeightLoss) {
                return;
            }
            if (weightLoss2 == this.lowestWeightLoss && f3 >= this.lowestMoveCost) {
                return;
            }
            if (i >= this.itemsToPlace.size()) {
                this.lowestWeightLoss = weightLoss2;
                this.lowestMoveCost = f3;
                this.finalPlacedItems = GridSizeMigrationTask.deepCopy(itemsPlaced);
                return;
            }
            DbEntry me = this.itemsToPlace.get(i);
            int myX = me.cellX;
            int myY = me.cellY;
            ArrayList<DbEntry> itemsIncludingMe = new ArrayList<>(itemsPlaced.size() + 1);
            itemsIncludingMe.addAll(arrayList);
            itemsIncludingMe.add(me);
            if (me.spanX > 1 || me.spanY > 1) {
                int myW2 = me.spanX;
                int myH = me.spanY;
                for (int y = this.startY; y < GridSizeMigrationTask.this.mTrgY; y++) {
                    int x = 0;
                    while (x < GridSizeMigrationTask.this.mTrgX) {
                        float newMoveCost = moveCost;
                        if (x != myX) {
                            me.cellX = x;
                            f = 1.0f;
                            newMoveCost += 1.0f;
                        } else {
                            f = 1.0f;
                        }
                        if (y != myY) {
                            me.cellY = y;
                            newMoveCost += f;
                        }
                        if (this.ignoreMove) {
                            newMoveCost = moveCost;
                        }
                        if (this.occupied.isRegionVacant(x, y, myW2, myH)) {
                            this.occupied.markCells((ItemInfo) me, true);
                            find(i + 1, weightLoss2, newMoveCost, itemsIncludingMe);
                            this.occupied.markCells((ItemInfo) me, false);
                        }
                        if (myW2 > me.minSpanX && this.occupied.isRegionVacant(x, y, myW2 - 1, myH)) {
                            me.spanX--;
                            this.occupied.markCells((ItemInfo) me, true);
                            find(i + 1, weightLoss2, newMoveCost + 1.0f, itemsIncludingMe);
                            this.occupied.markCells((ItemInfo) me, false);
                            me.spanX++;
                        }
                        if (myH > me.minSpanY && this.occupied.isRegionVacant(x, y, myW2, myH - 1)) {
                            me.spanY--;
                            this.occupied.markCells((ItemInfo) me, true);
                            find(i + 1, weightLoss2, newMoveCost + 1.0f, itemsIncludingMe);
                            this.occupied.markCells((ItemInfo) me, false);
                            me.spanY++;
                        }
                        if (myH <= me.minSpanY || myW2 <= me.minSpanX) {
                            myW = myW2;
                        } else {
                            myW = myW2;
                            if (this.occupied.isRegionVacant(x, y, myW2 - 1, myH - 1)) {
                                me.spanX--;
                                me.spanY--;
                                this.occupied.markCells((ItemInfo) me, true);
                                find(i + 1, weightLoss2, GridSizeMigrationTask.WT_WIDGET_MIN + newMoveCost, itemsIncludingMe);
                                this.occupied.markCells((ItemInfo) me, false);
                                me.spanX++;
                                me.spanY++;
                            }
                        }
                        me.cellX = myX;
                        me.cellY = myY;
                        x++;
                        myW2 = myW;
                    }
                }
                try {
                    find(i + 1, me.weight + weightLoss2, f3, arrayList);
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                int newDistance = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                int newX2 = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                int newY = ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
                int y2 = this.startY;
                while (y2 < GridSizeMigrationTask.this.mTrgY) {
                    int newDistance2 = newDistance;
                    for (int x2 = 0; x2 < GridSizeMigrationTask.this.mTrgX; x2++) {
                        if (!this.occupied.cells[x2][y2]) {
                            if (this.ignoreMove) {
                                newX = newX2;
                                dist = 0;
                            } else {
                                newX = newX2;
                                dist = ((me.cellX - x2) * (me.cellX - x2)) + ((me.cellY - y2) * (me.cellY - y2));
                            }
                            if (dist < newDistance2) {
                                newX2 = x2;
                                newY = y2;
                                newDistance2 = dist;
                            }
                        } else {
                            newX = newX2;
                        }
                        newX2 = newX;
                    }
                    int i2 = newX2;
                    y2++;
                    newDistance = newDistance2;
                }
                if (newX2 >= GridSizeMigrationTask.this.mTrgX || newY >= GridSizeMigrationTask.this.mTrgY) {
                    for (int i3 = i + 1; i3 < this.itemsToPlace.size(); i3++) {
                        weightLoss2 += this.itemsToPlace.get(i3).weight;
                    }
                    find(this.itemsToPlace.size(), me.weight + weightLoss2, f3, arrayList);
                    return;
                }
                float newMoveCost2 = moveCost;
                if (newX2 != myX) {
                    me.cellX = newX2;
                    f2 = 1.0f;
                    newMoveCost2 += 1.0f;
                } else {
                    f2 = 1.0f;
                }
                if (newY != myY) {
                    me.cellY = newY;
                    newMoveCost2 += f2;
                }
                if (this.ignoreMove) {
                    newMoveCost2 = moveCost;
                }
                this.occupied.markCells((ItemInfo) me, true);
                find(i + 1, weightLoss2, newMoveCost2, itemsIncludingMe);
                this.occupied.markCells((ItemInfo) me, false);
                me.cellX = myX;
                me.cellY = myY;
                if (i + 1 < this.itemsToPlace.size() && this.itemsToPlace.get(i + 1).weight >= me.weight && !this.ignoreMove) {
                    find(i + 1, me.weight + weightLoss2, f3, arrayList);
                }
            }
        }
    }

    private ArrayList<DbEntry> loadHotseatEntries() {
        Cursor c = this.mContext.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, new String[]{"_id", LauncherSettings.BaseLauncherColumns.ITEM_TYPE, LauncherSettings.BaseLauncherColumns.INTENT, LauncherSettings.Favorites.SCREEN}, "container = -101", (String[]) null, (String) null, (CancellationSignal) null);
        int indexId = c.getColumnIndexOrThrow("_id");
        int indexItemType = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
        int indexIntent = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
        int indexScreen = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SCREEN);
        ArrayList<DbEntry> entries = new ArrayList<>();
        while (c.moveToNext()) {
            DbEntry entry = new DbEntry();
            entry.id = c.getLong(indexId);
            entry.itemType = c.getInt(indexItemType);
            entry.screenId = c.getLong(indexScreen);
            if (entry.screenId >= ((long) this.mSrcHotseatSize)) {
                this.mEntryToRemove.add(Long.valueOf(entry.id));
            } else {
                try {
                    int i = entry.itemType;
                    if (i != 6) {
                        switch (i) {
                            case 0:
                            case 1:
                                break;
                            case 2:
                                int total = getFolderItemsCount(entry.id);
                                if (total != 0) {
                                    entry.weight = ((float) total) * 0.5f;
                                    break;
                                } else {
                                    throw new Exception("Folder is empty");
                                }
                            default:
                                throw new Exception("Invalid item type");
                        }
                    }
                    verifyIntent(c.getString(indexIntent));
                    entry.weight = entry.itemType == 0 ? WT_APPLICATION : 1.0f;
                    entries.add(entry);
                } catch (Exception e) {
                    Log.d(TAG, "Removing item " + entry.id, e);
                    this.mEntryToRemove.add(Long.valueOf(entry.id));
                }
            }
        }
        c.close();
        return entries;
    }

    /* access modifiers changed from: protected */
    public ArrayList<DbEntry> loadWorkspaceEntries(long screen) {
        int indexItemType;
        int indexId;
        int indexAppWidgetId;
        ArrayList<DbEntry> entries;
        long j = screen;
        String[] strArr = {"_id", LauncherSettings.BaseLauncherColumns.ITEM_TYPE, LauncherSettings.Favorites.CELLX, LauncherSettings.Favorites.CELLY, LauncherSettings.Favorites.SPANX, LauncherSettings.Favorites.SPANY, LauncherSettings.BaseLauncherColumns.INTENT, LauncherSettings.Favorites.APPWIDGET_PROVIDER, LauncherSettings.Favorites.APPWIDGET_ID};
        Cursor c = queryWorkspace(strArr, "container = -100 AND screen = " + j);
        int indexId2 = c.getColumnIndexOrThrow("_id");
        int indexItemType2 = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ITEM_TYPE);
        int indexCellX = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLX);
        int indexCellY = c.getColumnIndexOrThrow(LauncherSettings.Favorites.CELLY);
        int indexSpanX = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANX);
        int indexSpanY = c.getColumnIndexOrThrow(LauncherSettings.Favorites.SPANY);
        int indexIntent = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.INTENT);
        int indexAppWidgetProvider = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_PROVIDER);
        int indexAppWidgetId2 = c.getColumnIndexOrThrow(LauncherSettings.Favorites.APPWIDGET_ID);
        ArrayList<DbEntry> entries2 = new ArrayList<>();
        while (true) {
            ArrayList<DbEntry> entries3 = entries2;
            if (c.moveToNext()) {
                DbEntry entry = new DbEntry();
                int indexAppWidgetId3 = indexAppWidgetId2;
                ArrayList<DbEntry> entries4 = entries3;
                entry.id = c.getLong(indexId2);
                entry.itemType = c.getInt(indexItemType2);
                entry.cellX = c.getInt(indexCellX);
                entry.cellY = c.getInt(indexCellY);
                entry.spanX = c.getInt(indexSpanX);
                entry.spanY = c.getInt(indexSpanY);
                entry.screenId = j;
                try {
                    int i = entry.itemType;
                    if (i != 4) {
                        if (i != 6) {
                            switch (i) {
                                case 0:
                                case 1:
                                    break;
                                case 2:
                                    int total = getFolderItemsCount(entry.id);
                                    if (total != 0) {
                                        entry.weight = ((float) total) * 0.5f;
                                        break;
                                    } else {
                                        throw new Exception("Folder is empty");
                                    }
                                default:
                                    try {
                                        throw new Exception("Invalid item type");
                                    } catch (Exception e) {
                                        e = e;
                                        indexAppWidgetId = indexAppWidgetId3;
                                        entries = entries4;
                                        StringBuilder sb = new StringBuilder();
                                        indexId = indexId2;
                                        sb.append("Removing item ");
                                        indexItemType = indexItemType2;
                                        sb.append(entry.id);
                                        Log.d(TAG, sb.toString(), e);
                                        this.mEntryToRemove.add(Long.valueOf(entry.id));
                                        entries2 = entries;
                                        indexAppWidgetId2 = indexAppWidgetId;
                                        indexId2 = indexId;
                                        indexItemType2 = indexItemType;
                                        j = screen;
                                    }
                            }
                        }
                        verifyIntent(c.getString(indexIntent));
                        entry.weight = entry.itemType == 0 ? WT_APPLICATION : 1.0f;
                        indexAppWidgetId = indexAppWidgetId3;
                    } else {
                        String provider = c.getString(indexAppWidgetProvider);
                        verifyPackage(ComponentName.unflattenFromString(provider).getPackageName());
                        String str = provider;
                        entry.weight = Math.max(WT_WIDGET_MIN, ((float) entry.spanX) * WT_WIDGET_FACTOR * ((float) entry.spanY));
                        indexAppWidgetId = indexAppWidgetId3;
                        try {
                            int widgetId = c.getInt(indexAppWidgetId);
                            LauncherAppWidgetProviderInfo pInfo = AppWidgetManagerCompat.getInstance(this.mContext).getLauncherAppWidgetInfo(widgetId);
                            Point spans = null;
                            if (pInfo != null) {
                                try {
                                    spans = pInfo.getMinSpans();
                                } catch (Exception e2) {
                                    e = e2;
                                    entries = entries4;
                                    StringBuilder sb2 = new StringBuilder();
                                    indexId = indexId2;
                                    sb2.append("Removing item ");
                                    indexItemType = indexItemType2;
                                    sb2.append(entry.id);
                                    Log.d(TAG, sb2.toString(), e);
                                    this.mEntryToRemove.add(Long.valueOf(entry.id));
                                    entries2 = entries;
                                    indexAppWidgetId2 = indexAppWidgetId;
                                    indexId2 = indexId;
                                    indexItemType2 = indexItemType;
                                    j = screen;
                                }
                            }
                            if (spans != null) {
                                int i2 = widgetId;
                                entry.minSpanX = spans.x > 0 ? spans.x : entry.spanX;
                                entry.minSpanY = spans.y > 0 ? spans.y : entry.spanY;
                            } else {
                                entry.minSpanY = 2;
                                entry.minSpanX = 2;
                            }
                            LauncherAppWidgetProviderInfo launcherAppWidgetProviderInfo = pInfo;
                            if (entry.minSpanX > this.mTrgX || entry.minSpanY > this.mTrgY) {
                                entries = entries4;
                                try {
                                    Point point = spans;
                                    throw new Exception("Widget can't be resized down to fit the grid");
                                } catch (Exception e3) {
                                    e = e3;
                                    StringBuilder sb22 = new StringBuilder();
                                    indexId = indexId2;
                                    sb22.append("Removing item ");
                                    indexItemType = indexItemType2;
                                    sb22.append(entry.id);
                                    Log.d(TAG, sb22.toString(), e);
                                    this.mEntryToRemove.add(Long.valueOf(entry.id));
                                    entries2 = entries;
                                    indexAppWidgetId2 = indexAppWidgetId;
                                    indexId2 = indexId;
                                    indexItemType2 = indexItemType;
                                    j = screen;
                                }
                            }
                        } catch (Exception e4) {
                            e = e4;
                            entries = entries4;
                            StringBuilder sb222 = new StringBuilder();
                            indexId = indexId2;
                            sb222.append("Removing item ");
                            indexItemType = indexItemType2;
                            sb222.append(entry.id);
                            Log.d(TAG, sb222.toString(), e);
                            this.mEntryToRemove.add(Long.valueOf(entry.id));
                            entries2 = entries;
                            indexAppWidgetId2 = indexAppWidgetId;
                            indexId2 = indexId;
                            indexItemType2 = indexItemType;
                            j = screen;
                        }
                    }
                    entries = entries4;
                    entries.add(entry);
                    indexId = indexId2;
                    indexItemType = indexItemType2;
                } catch (Exception e5) {
                    e = e5;
                    indexAppWidgetId = indexAppWidgetId3;
                    entries = entries4;
                    StringBuilder sb2222 = new StringBuilder();
                    indexId = indexId2;
                    sb2222.append("Removing item ");
                    indexItemType = indexItemType2;
                    sb2222.append(entry.id);
                    Log.d(TAG, sb2222.toString(), e);
                    this.mEntryToRemove.add(Long.valueOf(entry.id));
                    entries2 = entries;
                    indexAppWidgetId2 = indexAppWidgetId;
                    indexId2 = indexId;
                    indexItemType2 = indexItemType;
                    j = screen;
                }
                entries2 = entries;
                indexAppWidgetId2 = indexAppWidgetId;
                indexId2 = indexId;
                indexItemType2 = indexItemType;
                j = screen;
            } else {
                int i3 = indexItemType2;
                ArrayList<DbEntry> entries5 = entries3;
                int i4 = indexAppWidgetId2;
                c.close();
                return entries5;
            }
        }
    }

    private int getFolderItemsCount(long folderId) {
        String[] strArr = {"_id", LauncherSettings.BaseLauncherColumns.INTENT};
        Cursor c = queryWorkspace(strArr, "container = " + folderId);
        int total = 0;
        while (c.moveToNext()) {
            try {
                verifyIntent(c.getString(1));
                total++;
            } catch (Exception e) {
                this.mEntryToRemove.add(Long.valueOf(c.getLong(0)));
            }
        }
        c.close();
        return total;
    }

    /* access modifiers changed from: protected */
    public Cursor queryWorkspace(String[] columns, String where) {
        return this.mContext.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, columns, where, (String[]) null, (String) null, (CancellationSignal) null);
    }

    private void verifyIntent(String intentStr) throws Exception {
        Intent intent = Intent.parseUri(intentStr, 0);
        if (intent.getComponent() != null) {
            verifyPackage(intent.getComponent().getPackageName());
        } else if (intent.getPackage() != null) {
            verifyPackage(intent.getPackage());
        }
    }

    private void verifyPackage(String packageName) throws Exception {
        if (!this.mValidPackages.contains(packageName)) {
            throw new Exception("Package not available");
        }
    }

    protected static class DbEntry extends ItemInfo implements Comparable<DbEntry> {
        public float weight;

        public DbEntry copy() {
            DbEntry entry = new DbEntry();
            entry.copyFrom(this);
            entry.weight = this.weight;
            entry.minSpanX = this.minSpanX;
            entry.minSpanY = this.minSpanY;
            return entry;
        }

        public int compareTo(DbEntry another) {
            if (this.itemType == 4) {
                if (another.itemType == 4) {
                    return (another.spanY * another.spanX) - (this.spanX * this.spanY);
                }
                return -1;
            } else if (another.itemType == 4) {
                return 1;
            } else {
                return Float.compare(another.weight, this.weight);
            }
        }

        public boolean columnsSame(DbEntry org) {
            return org.cellX == this.cellX && org.cellY == this.cellY && org.spanX == this.spanX && org.spanY == this.spanY && org.screenId == this.screenId;
        }

        public void addToContentValues(ContentValues values) {
            values.put(LauncherSettings.Favorites.SCREEN, Long.valueOf(this.screenId));
            values.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX));
            values.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY));
            values.put(LauncherSettings.Favorites.SPANX, Integer.valueOf(this.spanX));
            values.put(LauncherSettings.Favorites.SPANY, Integer.valueOf(this.spanY));
        }
    }

    /* access modifiers changed from: private */
    public static ArrayList<DbEntry> deepCopy(ArrayList<DbEntry> src) {
        ArrayList<DbEntry> dup = new ArrayList<>(src.size());
        Iterator<DbEntry> it = src.iterator();
        while (it.hasNext()) {
            dup.add(it.next().copy());
        }
        return dup;
    }

    private static Point parsePoint(String point) {
        String[] split = point.split(",");
        return new Point(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }

    private static String getPointString(int x, int y) {
        return String.format(Locale.ENGLISH, "%d,%d", new Object[]{Integer.valueOf(x), Integer.valueOf(y)});
    }

    public static void markForMigration(Context context, int gridX, int gridY, int hotseatSize) {
        Utilities.getPrefs(context).edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, getPointString(gridX, gridY)).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, hotseatSize).apply();
    }

    public static boolean migrateGridIfNeeded(Context context) {
        SharedPreferences prefs = Utilities.getPrefs(context);
        InvariantDeviceProfile idp = LauncherAppState.getIDP(context);
        String gridSizeString = getPointString(idp.numColumns, idp.numRows);
        if (gridSizeString.equals(prefs.getString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, "")) && idp.numHotseatIcons == prefs.getInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons)) {
            return true;
        }
        long migrationStartTime = System.currentTimeMillis();
        boolean dbChanged = false;
        try {
            HashSet<String> validPackages = getValidPackages(context);
            int srcHotseatCount = prefs.getInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons);
            if (srcHotseatCount != idp.numHotseatIcons) {
                dbChanged = new GridSizeMigrationTask(context, LauncherAppState.getIDP(context), validPackages, srcHotseatCount, idp.numHotseatIcons).migrateHotseat();
            }
            Point targetSize = new Point(idp.numColumns, idp.numRows);
            try {
                if (new MultiStepMigrationTask(validPackages, context).migrate(parsePoint(prefs.getString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, gridSizeString)), targetSize)) {
                    dbChanged = true;
                }
                if (dbChanged) {
                    Cursor c = context.getContentResolver().query(LauncherSettings.Favorites.CONTENT_URI, (String[]) null, (String) null, (String[]) null, (String) null);
                    boolean hasData = c.moveToNext();
                    c.close();
                    if (!hasData) {
                        throw new Exception("Removed every thing during grid resize");
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Workspace migration completed in ");
                HashSet<String> hashSet = validPackages;
                Point point = targetSize;
                sb.append(System.currentTimeMillis() - migrationStartTime);
                Log.v(TAG, sb.toString());
                prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, gridSizeString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
                return true;
            } catch (Exception e) {
                e = e;
                try {
                    Log.e(TAG, "Error during grid migration", e);
                    Log.v(TAG, "Workspace migration completed in " + (System.currentTimeMillis() - migrationStartTime));
                    prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, gridSizeString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
                    return false;
                } catch (Throwable th) {
                    e = th;
                    Log.v(TAG, "Workspace migration completed in " + (System.currentTimeMillis() - migrationStartTime));
                    prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, gridSizeString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
                    throw e;
                }
            }
        } catch (Exception e2) {
            e = e2;
            Context context2 = context;
            Log.e(TAG, "Error during grid migration", e);
            Log.v(TAG, "Workspace migration completed in " + (System.currentTimeMillis() - migrationStartTime));
            prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, gridSizeString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
            return false;
        } catch (Throwable th2) {
            e = th2;
            Context context3 = context;
            Log.v(TAG, "Workspace migration completed in " + (System.currentTimeMillis() - migrationStartTime));
            prefs.edit().putString(KEY_MIGRATION_SRC_WORKSPACE_SIZE, gridSizeString).putInt(KEY_MIGRATION_SRC_HOTSEAT_COUNT, idp.numHotseatIcons).apply();
            throw e;
        }
    }

    protected static HashSet<String> getValidPackages(Context context) {
        HashSet<String> validPackages = new HashSet<>();
        for (PackageInfo info : context.getPackageManager().getInstalledPackages(8192)) {
            validPackages.add(info.packageName);
        }
        validPackages.addAll(PackageInstallerCompat.getInstance(context).updateAndGetActiveSessionCache().keySet());
        return validPackages;
    }

    public static LongArrayMap<Object> removeBrokenHotseatItems(Context context) throws Exception {
        GridSizeMigrationTask task = new GridSizeMigrationTask(context, LauncherAppState.getIDP(context), getValidPackages(context), (int) ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED, (int) ActivityChooserView.ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
        ArrayList<DbEntry> items = task.loadHotseatEntries();
        task.applyOperations();
        LongArrayMap<Object> positions = new LongArrayMap<>();
        Iterator<DbEntry> it = items.iterator();
        while (it.hasNext()) {
            DbEntry item = it.next();
            positions.put(item.screenId, item);
        }
        return positions;
    }

    protected static class MultiStepMigrationTask {
        private final Context mContext;
        private final HashSet<String> mValidPackages;

        public MultiStepMigrationTask(HashSet<String> validPackages, Context context) {
            this.mValidPackages = validPackages;
            this.mContext = context;
        }

        public boolean migrate(Point sourceSize, Point targetSize) throws Exception {
            boolean dbChanged = false;
            if (!targetSize.equals(sourceSize)) {
                if (sourceSize.x < targetSize.x) {
                    sourceSize.x = targetSize.x;
                }
                if (sourceSize.y < targetSize.y) {
                    sourceSize.y = targetSize.y;
                }
                while (!targetSize.equals(sourceSize)) {
                    Point nextSize = new Point(sourceSize);
                    if (targetSize.x < nextSize.x) {
                        nextSize.x--;
                    }
                    if (targetSize.y < nextSize.y) {
                        nextSize.y--;
                    }
                    if (runStepTask(sourceSize, nextSize)) {
                        dbChanged = true;
                    }
                    sourceSize.set(nextSize.x, nextSize.y);
                }
            }
            return dbChanged;
        }

        /* access modifiers changed from: protected */
        public boolean runStepTask(Point sourceSize, Point nextSize) throws Exception {
            return new GridSizeMigrationTask(this.mContext, LauncherAppState.getIDP(this.mContext), this.mValidPackages, sourceSize, nextSize).migrateWorkspace();
        }
    }
}
