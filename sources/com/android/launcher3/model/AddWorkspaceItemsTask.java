package com.android.launcher3.model;

import android.content.Context;
import android.util.Pair;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.AppInfo;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.InvariantDeviceProfile;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.GridOccupancy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AddWorkspaceItemsTask extends BaseModelUpdateTask {
    private final List<Pair<ItemInfo, Object>> mItemList;

    public AddWorkspaceItemsTask(List<Pair<ItemInfo, Object>> itemList) {
        this.mItemList = itemList;
    }

    public void execute(LauncherAppState app, BgDataModel dataModel, AllAppsList apps) {
        ItemInfo itemInfo;
        BgDataModel bgDataModel = dataModel;
        if (!this.mItemList.isEmpty()) {
            Context context = app.getContext();
            final ArrayList<ItemInfo> addedItemsFinal = new ArrayList<>();
            final ArrayList<Long> addedWorkspaceScreensFinal = new ArrayList<>();
            ArrayList<Long> workspaceScreens = LauncherModel.loadWorkspaceScreensDb(context);
            synchronized (dataModel) {
                List<ItemInfo> filteredItems = new ArrayList<>();
                for (Pair<ItemInfo, Object> entry : this.mItemList) {
                    ItemInfo item = (ItemInfo) entry.first;
                    if ((item.itemType != 0 && item.itemType != 1) || !shortcutExists(bgDataModel, item.getIntent(), item.user)) {
                        if (item.itemType == 0 && (item instanceof AppInfo)) {
                            item = ((AppInfo) item).makeShortcut();
                        }
                        if (item != null) {
                            filteredItems.add(item);
                        }
                    }
                }
                for (ItemInfo item2 : filteredItems) {
                    ItemInfo item3 = item2;
                    Pair<Long, int[]> coords = findSpaceForItem(app, dataModel, workspaceScreens, addedWorkspaceScreensFinal, item2.spanX, item2.spanY);
                    long screenId = ((Long) coords.first).longValue();
                    int[] cordinates = (int[]) coords.second;
                    if (!(item3 instanceof ShortcutInfo) && !(item3 instanceof FolderInfo)) {
                        if (!(item3 instanceof LauncherAppWidgetInfo)) {
                            if (item3 instanceof AppInfo) {
                                itemInfo = ((AppInfo) item3).makeShortcut();
                                getModelWriter().addItemToDatabase(itemInfo, -100, screenId, cordinates[0], cordinates[1]);
                                addedItemsFinal.add(itemInfo);
                            } else {
                                throw new RuntimeException("Unexpected info type");
                            }
                        }
                    }
                    itemInfo = item3;
                    getModelWriter().addItemToDatabase(itemInfo, -100, screenId, cordinates[0], cordinates[1]);
                    addedItemsFinal.add(itemInfo);
                }
            }
            updateScreens(context, workspaceScreens);
            if (!addedItemsFinal.isEmpty()) {
                scheduleCallbackTask(new LauncherModel.CallbackTask() {
                    public void execute(LauncherModel.Callbacks callbacks) {
                        ArrayList<ItemInfo> addAnimated = new ArrayList<>();
                        ArrayList<ItemInfo> addNotAnimated = new ArrayList<>();
                        if (!addedItemsFinal.isEmpty()) {
                            long lastScreenId = ((ItemInfo) addedItemsFinal.get(addedItemsFinal.size() - 1)).screenId;
                            Iterator it = addedItemsFinal.iterator();
                            while (it.hasNext()) {
                                ItemInfo i = (ItemInfo) it.next();
                                if (i.screenId == lastScreenId) {
                                    addAnimated.add(i);
                                } else {
                                    addNotAnimated.add(i);
                                }
                            }
                        }
                        callbacks.bindAppsAdded(addedWorkspaceScreensFinal, addNotAnimated, addAnimated);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateScreens(Context context, ArrayList<Long> workspaceScreens) {
        LauncherModel.updateWorkspaceScreenOrder(context, workspaceScreens);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00bc, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shortcutExists(com.android.launcher3.model.BgDataModel r13, android.content.Intent r14, android.os.UserHandle r15) {
        /*
            r12 = this;
            r0 = 1
            if (r14 != 0) goto L_0x0004
            return r0
        L_0x0004:
            android.content.ComponentName r1 = r14.getComponent()
            r2 = 0
            if (r1 == 0) goto L_0x003e
            android.content.ComponentName r1 = r14.getComponent()
            java.lang.String r1 = r1.getPackageName()
            java.lang.String r3 = r14.getPackage()
            if (r3 == 0) goto L_0x002c
            java.lang.String r3 = r14.toUri(r2)
            android.content.Intent r4 = new android.content.Intent
            r4.<init>(r14)
            r5 = 0
            android.content.Intent r4 = r4.setPackage(r5)
            java.lang.String r4 = r4.toUri(r2)
            goto L_0x0047
        L_0x002c:
            android.content.Intent r3 = new android.content.Intent
            r3.<init>(r14)
            android.content.Intent r3 = r3.setPackage(r1)
            java.lang.String r3 = r3.toUri(r2)
            java.lang.String r4 = r14.toUri(r2)
            goto L_0x0047
        L_0x003e:
            r1 = 0
            java.lang.String r3 = r14.toUri(r2)
            java.lang.String r4 = r14.toUri(r2)
        L_0x0047:
            boolean r5 = com.android.launcher3.Utilities.isLauncherAppTarget(r14)
            monitor-enter(r13)
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r6 = r13.itemsIdMap     // Catch:{ all -> 0x00c0 }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ all -> 0x00c0 }
        L_0x0052:
            boolean r7 = r6.hasNext()     // Catch:{ all -> 0x00c0 }
            if (r7 == 0) goto L_0x00be
            java.lang.Object r7 = r6.next()     // Catch:{ all -> 0x00c0 }
            com.android.launcher3.ItemInfo r7 = (com.android.launcher3.ItemInfo) r7     // Catch:{ all -> 0x00c0 }
            boolean r8 = r7 instanceof com.android.launcher3.ShortcutInfo     // Catch:{ all -> 0x00c0 }
            if (r8 == 0) goto L_0x00bd
            r8 = r7
            com.android.launcher3.ShortcutInfo r8 = (com.android.launcher3.ShortcutInfo) r8     // Catch:{ all -> 0x00c0 }
            android.content.Intent r9 = r7.getIntent()     // Catch:{ all -> 0x00c0 }
            if (r9 == 0) goto L_0x00bd
            android.os.UserHandle r9 = r8.user     // Catch:{ all -> 0x00c0 }
            boolean r9 = r9.equals(r15)     // Catch:{ all -> 0x00c0 }
            if (r9 == 0) goto L_0x00bd
            android.content.Intent r9 = new android.content.Intent     // Catch:{ all -> 0x00c0 }
            android.content.Intent r10 = r7.getIntent()     // Catch:{ all -> 0x00c0 }
            r9.<init>(r10)     // Catch:{ all -> 0x00c0 }
            android.graphics.Rect r10 = r14.getSourceBounds()     // Catch:{ all -> 0x00c0 }
            r9.setSourceBounds(r10)     // Catch:{ all -> 0x00c0 }
            java.lang.String r10 = r9.toUri(r2)     // Catch:{ all -> 0x00c0 }
            boolean r11 = r3.equals(r10)     // Catch:{ all -> 0x00c0 }
            if (r11 != 0) goto L_0x00bb
            boolean r11 = r4.equals(r10)     // Catch:{ all -> 0x00c0 }
            if (r11 == 0) goto L_0x0094
            goto L_0x00bb
        L_0x0094:
            if (r5 == 0) goto L_0x00bd
            boolean r11 = r8.isPromise()     // Catch:{ all -> 0x00c0 }
            if (r11 == 0) goto L_0x00bd
            r11 = 2
            boolean r11 = r8.hasStatusFlag(r11)     // Catch:{ all -> 0x00c0 }
            if (r11 == 0) goto L_0x00bd
            android.content.ComponentName r11 = r8.getTargetComponent()     // Catch:{ all -> 0x00c0 }
            if (r11 == 0) goto L_0x00bd
            if (r1 == 0) goto L_0x00bd
            android.content.ComponentName r11 = r8.getTargetComponent()     // Catch:{ all -> 0x00c0 }
            java.lang.String r11 = r11.getPackageName()     // Catch:{ all -> 0x00c0 }
            boolean r11 = r1.equals(r11)     // Catch:{ all -> 0x00c0 }
            if (r11 == 0) goto L_0x00bd
            monitor-exit(r13)     // Catch:{ all -> 0x00c0 }
            return r0
        L_0x00bb:
            monitor-exit(r13)     // Catch:{ all -> 0x00c0 }
            return r0
        L_0x00bd:
            goto L_0x0052
        L_0x00be:
            monitor-exit(r13)     // Catch:{ all -> 0x00c0 }
            return r2
        L_0x00c0:
            r0 = move-exception
            monitor-exit(r13)     // Catch:{ all -> 0x00c0 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AddWorkspaceItemsTask.shortcutExists(com.android.launcher3.model.BgDataModel, android.content.Intent, android.os.UserHandle):boolean");
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00f2, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.Pair<java.lang.Long, int[]> findSpaceForItem(com.android.launcher3.LauncherAppState r17, com.android.launcher3.model.BgDataModel r18, java.util.ArrayList<java.lang.Long> r19, java.util.ArrayList<java.lang.Long> r20, int r21, int r22) {
        /*
            r16 = this;
            r1 = r18
            r2 = r19
            android.util.LongSparseArray r0 = new android.util.LongSparseArray
            r0.<init>()
            r3 = r0
            monitor-enter(r18)
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r0 = r1.itemsIdMap     // Catch:{ all -> 0x00ed }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x00ed }
        L_0x0011:
            boolean r4 = r0.hasNext()     // Catch:{ all -> 0x00ed }
            if (r4 == 0) goto L_0x003e
            java.lang.Object r4 = r0.next()     // Catch:{ all -> 0x00ed }
            com.android.launcher3.ItemInfo r4 = (com.android.launcher3.ItemInfo) r4     // Catch:{ all -> 0x00ed }
            long r5 = r4.container     // Catch:{ all -> 0x00ed }
            r7 = -100
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x003d
            long r5 = r4.screenId     // Catch:{ all -> 0x00ed }
            java.lang.Object r5 = r3.get(r5)     // Catch:{ all -> 0x00ed }
            java.util.ArrayList r5 = (java.util.ArrayList) r5     // Catch:{ all -> 0x00ed }
            if (r5 != 0) goto L_0x003a
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x00ed }
            r6.<init>()     // Catch:{ all -> 0x00ed }
            r5 = r6
            long r6 = r4.screenId     // Catch:{ all -> 0x00ed }
            r3.put(r6, r5)     // Catch:{ all -> 0x00ed }
        L_0x003a:
            r5.add(r4)     // Catch:{ all -> 0x00ed }
        L_0x003d:
            goto L_0x0011
        L_0x003e:
            monitor-exit(r18)     // Catch:{ all -> 0x00ed }
            r4 = 0
            r0 = 2
            int[] r0 = new int[r0]
            r12 = 0
            int r13 = r19.size()
            boolean r6 = r19.isEmpty()
            r14 = 1
            r6 = r6 ^ r14
            r15 = r6
            if (r15 >= r13) goto L_0x0071
            java.lang.Object r6 = r2.get(r15)
            java.lang.Long r6 = (java.lang.Long) r6
            long r4 = r6.longValue()
            java.lang.Object r6 = r3.get(r4)
            r8 = r6
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            r6 = r16
            r7 = r17
            r9 = r0
            r10 = r21
            r11 = r22
            boolean r12 = r6.findNextAvailableIconSpaceInScreen(r7, r8, r9, r10, r11)
        L_0x0071:
            if (r12 != 0) goto L_0x009c
        L_0x0074:
            if (r14 >= r13) goto L_0x009c
            java.lang.Object r6 = r2.get(r14)
            java.lang.Long r6 = (java.lang.Long) r6
            long r4 = r6.longValue()
            java.lang.Object r6 = r3.get(r4)
            r8 = r6
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            r6 = r16
            r7 = r17
            r9 = r0
            r10 = r21
            r11 = r22
            boolean r6 = r6.findNextAvailableIconSpaceInScreen(r7, r8, r9, r10, r11)
            if (r6 == 0) goto L_0x0099
            r12 = 1
            goto L_0x009c
        L_0x0099:
            int r14 = r14 + 1
            goto L_0x0074
        L_0x009c:
            if (r12 != 0) goto L_0x00e2
            android.content.Context r6 = r17.getContext()
            android.content.ContentResolver r6 = r6.getContentResolver()
            java.lang.String r7 = "generate_new_screen_id"
            android.os.Bundle r6 = com.android.launcher3.LauncherSettings.Settings.call(r6, r7)
            java.lang.String r7 = "value"
            long r4 = r6.getLong(r7)
            java.lang.Long r6 = java.lang.Long.valueOf(r4)
            r2.add(r6)
            java.lang.Long r6 = java.lang.Long.valueOf(r4)
            r14 = r20
            r14.add(r6)
            java.lang.Object r6 = r3.get(r4)
            r8 = r6
            java.util.ArrayList r8 = (java.util.ArrayList) r8
            r6 = r16
            r7 = r17
            r9 = r0
            r10 = r21
            r11 = r22
            boolean r6 = r6.findNextAvailableIconSpaceInScreen(r7, r8, r9, r10, r11)
            if (r6 == 0) goto L_0x00da
            goto L_0x00e4
        L_0x00da:
            java.lang.RuntimeException r6 = new java.lang.RuntimeException
            java.lang.String r7 = "Can't find space to add the item"
            r6.<init>(r7)
            throw r6
        L_0x00e2:
            r14 = r20
        L_0x00e4:
            java.lang.Long r6 = java.lang.Long.valueOf(r4)
            android.util.Pair r6 = android.util.Pair.create(r6, r0)
            return r6
        L_0x00ed:
            r0 = move-exception
            r14 = r20
        L_0x00f0:
            monitor-exit(r18)     // Catch:{ all -> 0x00f2 }
            throw r0
        L_0x00f2:
            r0 = move-exception
            goto L_0x00f0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.AddWorkspaceItemsTask.findSpaceForItem(com.android.launcher3.LauncherAppState, com.android.launcher3.model.BgDataModel, java.util.ArrayList, java.util.ArrayList, int, int):android.util.Pair");
    }

    private boolean findNextAvailableIconSpaceInScreen(LauncherAppState app, ArrayList<ItemInfo> occupiedPos, int[] xy, int spanX, int spanY) {
        InvariantDeviceProfile profile = app.getInvariantDeviceProfile();
        GridOccupancy occupied = new GridOccupancy(profile.numColumns, profile.numRows);
        if (occupiedPos != null) {
            Iterator<ItemInfo> it = occupiedPos.iterator();
            while (it.hasNext()) {
                occupied.markCells(it.next(), true);
            }
        }
        return occupied.findVacantCell(xy, spanX, spanY);
    }
}
