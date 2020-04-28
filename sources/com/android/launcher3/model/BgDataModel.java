package com.android.launcher3.model;

import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import android.util.MutableInt;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.logging.DumpTargetWrapper;
import com.android.launcher3.model.nano.LauncherDumpProto;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.LongArrayMap;
import com.android.launcher3.util.MultiHashMap;
import com.google.protobuf.nano.MessageNano;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BgDataModel {
    private static final String TAG = "BgDataModel";
    public final ArrayList<LauncherAppWidgetInfo> appWidgets = new ArrayList<>();
    public final MultiHashMap<ComponentKey, String> deepShortcutMap = new MultiHashMap<>();
    public final LongArrayMap<FolderInfo> folders = new LongArrayMap<>();
    public boolean hasShortcutHostPermission;
    public final LongArrayMap<ItemInfo> itemsIdMap = new LongArrayMap<>();
    public int lastBindId = 0;
    public final Map<ShortcutKey, MutableInt> pinnedShortcutCounts = new HashMap();
    public final WidgetsModel widgetsModel = new WidgetsModel();
    public final ArrayList<ItemInfo> workspaceItems = new ArrayList<>();
    public final ArrayList<Long> workspaceScreens = new ArrayList<>();

    public synchronized void clear() {
        this.workspaceItems.clear();
        this.appWidgets.clear();
        this.folders.clear();
        this.itemsIdMap.clear();
        this.workspaceScreens.clear();
        this.pinnedShortcutCounts.clear();
        this.deepShortcutMap.clear();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x01e5, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void dump(java.lang.String r7, java.io.FileDescriptor r8, java.io.PrintWriter r9, java.lang.String[] r10) {
        /*
            r6 = this;
            monitor-enter(r6)
            java.util.List r0 = java.util.Arrays.asList(r10)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = "--proto"
            boolean r0 = r0.contains(r1)     // Catch:{ all -> 0x01e6 }
            if (r0 == 0) goto L_0x0012
            r6.dumpProto(r7, r8, r9, r10)     // Catch:{ all -> 0x01e6 }
            monitor-exit(r6)
            return
        L_0x0012:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r0.<init>()     // Catch:{ all -> 0x01e6 }
            r0.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = "Data Model:"
            r0.append(r1)     // Catch:{ all -> 0x01e6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r0)     // Catch:{ all -> 0x01e6 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r0.<init>()     // Catch:{ all -> 0x01e6 }
            r0.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = " ---- workspace screens: "
            r0.append(r1)     // Catch:{ all -> 0x01e6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e6 }
            r9.print(r0)     // Catch:{ all -> 0x01e6 }
            r0 = 0
            r1 = 0
        L_0x003c:
            java.util.ArrayList<java.lang.Long> r2 = r6.workspaceScreens     // Catch:{ all -> 0x01e6 }
            int r2 = r2.size()     // Catch:{ all -> 0x01e6 }
            if (r1 >= r2) goto L_0x0067
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            java.lang.String r3 = " "
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            java.util.ArrayList<java.lang.Long> r3 = r6.workspaceScreens     // Catch:{ all -> 0x01e6 }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x01e6 }
            java.lang.Long r3 = (java.lang.Long) r3     // Catch:{ all -> 0x01e6 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01e6 }
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e6 }
            r9.print(r2)     // Catch:{ all -> 0x01e6 }
            int r1 = r1 + 1
            goto L_0x003c
        L_0x0067:
            r9.println()     // Catch:{ all -> 0x01e6 }
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r1.<init>()     // Catch:{ all -> 0x01e6 }
            r1.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = " ---- workspace items "
            r1.append(r2)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r1)     // Catch:{ all -> 0x01e6 }
            r1 = 0
        L_0x007f:
            java.util.ArrayList<com.android.launcher3.ItemInfo> r2 = r6.workspaceItems     // Catch:{ all -> 0x01e6 }
            int r2 = r2.size()     // Catch:{ all -> 0x01e6 }
            r3 = 9
            if (r1 >= r2) goto L_0x00ad
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            r2.append(r7)     // Catch:{ all -> 0x01e6 }
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r3 = r6.workspaceItems     // Catch:{ all -> 0x01e6 }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.ItemInfo r3 = (com.android.launcher3.ItemInfo) r3     // Catch:{ all -> 0x01e6 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x01e6 }
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r2)     // Catch:{ all -> 0x01e6 }
            int r1 = r1 + 1
            goto L_0x007f
        L_0x00ad:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r1.<init>()     // Catch:{ all -> 0x01e6 }
            r1.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = " ---- appwidget items "
            r1.append(r2)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r1)     // Catch:{ all -> 0x01e6 }
            r1 = 0
        L_0x00c2:
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r2 = r6.appWidgets     // Catch:{ all -> 0x01e6 }
            int r2 = r2.size()     // Catch:{ all -> 0x01e6 }
            if (r1 >= r2) goto L_0x00ee
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            r2.append(r7)     // Catch:{ all -> 0x01e6 }
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r4 = r6.appWidgets     // Catch:{ all -> 0x01e6 }
            java.lang.Object r4 = r4.get(r1)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.LauncherAppWidgetInfo r4 = (com.android.launcher3.LauncherAppWidgetInfo) r4     // Catch:{ all -> 0x01e6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01e6 }
            r2.append(r4)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r2)     // Catch:{ all -> 0x01e6 }
            int r1 = r1 + 1
            goto L_0x00c2
        L_0x00ee:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r1.<init>()     // Catch:{ all -> 0x01e6 }
            r1.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = " ---- folder items "
            r1.append(r2)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r1)     // Catch:{ all -> 0x01e6 }
            r1 = 0
        L_0x0103:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r2 = r6.folders     // Catch:{ all -> 0x01e6 }
            int r2 = r2.size()     // Catch:{ all -> 0x01e6 }
            if (r1 >= r2) goto L_0x012f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            r2.append(r7)     // Catch:{ all -> 0x01e6 }
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r4 = r6.folders     // Catch:{ all -> 0x01e6 }
            java.lang.Object r4 = r4.valueAt(r1)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.FolderInfo r4 = (com.android.launcher3.FolderInfo) r4     // Catch:{ all -> 0x01e6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01e6 }
            r2.append(r4)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r2)     // Catch:{ all -> 0x01e6 }
            int r1 = r1 + 1
            goto L_0x0103
        L_0x012f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r1.<init>()     // Catch:{ all -> 0x01e6 }
            r1.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = " ---- items id map "
            r1.append(r2)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r1)     // Catch:{ all -> 0x01e6 }
            r1 = 0
        L_0x0144:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r2 = r6.itemsIdMap     // Catch:{ all -> 0x01e6 }
            int r2 = r2.size()     // Catch:{ all -> 0x01e6 }
            if (r1 >= r2) goto L_0x0170
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            r2.append(r7)     // Catch:{ all -> 0x01e6 }
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r4 = r6.itemsIdMap     // Catch:{ all -> 0x01e6 }
            java.lang.Object r4 = r4.valueAt(r1)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.ItemInfo r4 = (com.android.launcher3.ItemInfo) r4     // Catch:{ all -> 0x01e6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01e6 }
            r2.append(r4)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r2)     // Catch:{ all -> 0x01e6 }
            int r1 = r1 + 1
            goto L_0x0144
        L_0x0170:
            int r1 = r10.length     // Catch:{ all -> 0x01e6 }
            if (r1 <= 0) goto L_0x01e4
            r0 = r10[r0]     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = "--all"
            boolean r0 = android.text.TextUtils.equals(r0, r1)     // Catch:{ all -> 0x01e6 }
            if (r0 == 0) goto L_0x01e4
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r0.<init>()     // Catch:{ all -> 0x01e6 }
            r0.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r1 = "shortcuts"
            r0.append(r1)     // Catch:{ all -> 0x01e6 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x01e6 }
            r9.println(r0)     // Catch:{ all -> 0x01e6 }
            com.android.launcher3.util.MultiHashMap<com.android.launcher3.util.ComponentKey, java.lang.String> r0 = r6.deepShortcutMap     // Catch:{ all -> 0x01e6 }
            java.util.Collection r0 = r0.values()     // Catch:{ all -> 0x01e6 }
            java.util.Iterator r0 = r0.iterator()     // Catch:{ all -> 0x01e6 }
        L_0x019b:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x01e6 }
            if (r1 == 0) goto L_0x01e4
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x01e6 }
            java.util.ArrayList r1 = (java.util.ArrayList) r1     // Catch:{ all -> 0x01e6 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r2.<init>()     // Catch:{ all -> 0x01e6 }
            r2.append(r7)     // Catch:{ all -> 0x01e6 }
            java.lang.String r3 = "  "
            r2.append(r3)     // Catch:{ all -> 0x01e6 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x01e6 }
            r9.print(r2)     // Catch:{ all -> 0x01e6 }
            java.util.Iterator r2 = r1.iterator()     // Catch:{ all -> 0x01e6 }
        L_0x01bf:
            boolean r3 = r2.hasNext()     // Catch:{ all -> 0x01e6 }
            if (r3 == 0) goto L_0x01e0
            java.lang.Object r3 = r2.next()     // Catch:{ all -> 0x01e6 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x01e6 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01e6 }
            r4.<init>()     // Catch:{ all -> 0x01e6 }
            r4.append(r3)     // Catch:{ all -> 0x01e6 }
            java.lang.String r5 = ", "
            r4.append(r5)     // Catch:{ all -> 0x01e6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x01e6 }
            r9.print(r4)     // Catch:{ all -> 0x01e6 }
            goto L_0x01bf
        L_0x01e0:
            r9.println()     // Catch:{ all -> 0x01e6 }
            goto L_0x019b
        L_0x01e4:
            monitor-exit(r6)
            return
        L_0x01e6:
            r7 = move-exception
            monitor-exit(r6)
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.dump(java.lang.String, java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    private synchronized void dumpProto(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        synchronized (this) {
            int i = 0;
            DumpTargetWrapper hotseat = new DumpTargetWrapper(2, 0);
            LongArrayMap longArrayMap = new LongArrayMap();
            for (int i2 = 0; i2 < this.workspaceScreens.size(); i2++) {
                longArrayMap.put(this.workspaceScreens.get(i2).longValue(), new DumpTargetWrapper(1, i2));
            }
            for (int i3 = 0; i3 < this.folders.size(); i3++) {
                FolderInfo fInfo = (FolderInfo) this.folders.valueAt(i3);
                DumpTargetWrapper dtw = new DumpTargetWrapper(3, this.folders.size());
                dtw.writeToDumpTarget(fInfo);
                Iterator<ShortcutInfo> it = fInfo.contents.iterator();
                while (it.hasNext()) {
                    ShortcutInfo sInfo = it.next();
                    DumpTargetWrapper child = new DumpTargetWrapper(sInfo);
                    child.writeToDumpTarget(sInfo);
                    dtw.add(child);
                }
                if (fInfo.container == -101) {
                    hotseat.add(dtw);
                } else if (fInfo.container == -100) {
                    ((DumpTargetWrapper) longArrayMap.get(fInfo.screenId)).add(dtw);
                }
            }
            for (int i4 = 0; i4 < this.workspaceItems.size(); i4++) {
                ItemInfo info = this.workspaceItems.get(i4);
                if (!(info instanceof FolderInfo)) {
                    DumpTargetWrapper dtw2 = new DumpTargetWrapper(info);
                    dtw2.writeToDumpTarget(info);
                    if (info.container == -101) {
                        hotseat.add(dtw2);
                    } else if (info.container == -100) {
                        ((DumpTargetWrapper) longArrayMap.get(info.screenId)).add(dtw2);
                    }
                }
            }
            for (int i5 = 0; i5 < this.appWidgets.size(); i5++) {
                ItemInfo info2 = this.appWidgets.get(i5);
                DumpTargetWrapper dtw3 = new DumpTargetWrapper(info2);
                dtw3.writeToDumpTarget(info2);
                if (info2.container == -101) {
                    hotseat.add(dtw3);
                } else if (info2.container == -100) {
                    ((DumpTargetWrapper) longArrayMap.get(info2.screenId)).add(dtw3);
                }
            }
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(hotseat.getFlattenedList());
            for (int i6 = 0; i6 < longArrayMap.size(); i6++) {
                arrayList.addAll(((DumpTargetWrapper) longArrayMap.valueAt(i6)).getFlattenedList());
            }
            if (Arrays.asList(args).contains("--debug")) {
                while (true) {
                    int i7 = i;
                    if (i7 < arrayList.size()) {
                        StringBuilder sb = new StringBuilder();
                        String str = prefix;
                        sb.append(prefix);
                        sb.append(DumpTargetWrapper.getDumpTargetStr((LauncherDumpProto.DumpTarget) arrayList.get(i7)));
                        writer.println(sb.toString());
                        i = i7 + 1;
                    } else {
                        String str2 = prefix;
                        PrintWriter printWriter = writer;
                        return;
                    }
                }
            } else {
                String str3 = prefix;
                PrintWriter printWriter2 = writer;
                LauncherDumpProto.LauncherImpression proto = new LauncherDumpProto.LauncherImpression();
                proto.targets = new LauncherDumpProto.DumpTarget[arrayList.size()];
                while (true) {
                    int i8 = i;
                    if (i8 >= arrayList.size()) {
                        break;
                    }
                    proto.targets[i8] = (LauncherDumpProto.DumpTarget) arrayList.get(i8);
                    i = i8 + 1;
                }
                try {
                    new FileOutputStream(fd).write(MessageNano.toByteArray(proto));
                    Log.d(TAG, MessageNano.toByteArray(proto).length + "Bytes");
                } catch (IOException e) {
                    Log.e(TAG, "Exception writing dumpsys --proto", e);
                }
            }
        }
        return;
    }

    public synchronized void removeItem(Context context, ItemInfo... items) {
        removeItem(context, (Iterable<? extends ItemInfo>) Arrays.asList(items));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002b, code lost:
        if (r4 == 0) goto L_0x002d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void removeItem(android.content.Context r6, java.lang.Iterable<? extends com.android.launcher3.ItemInfo> r7) {
        /*
            r5 = this;
            monitor-enter(r5)
            java.util.Iterator r0 = r7.iterator()     // Catch:{ all -> 0x0062 }
        L_0x0005:
            boolean r1 = r0.hasNext()     // Catch:{ all -> 0x0062 }
            if (r1 == 0) goto L_0x0060
            java.lang.Object r1 = r0.next()     // Catch:{ all -> 0x0062 }
            com.android.launcher3.ItemInfo r1 = (com.android.launcher3.ItemInfo) r1     // Catch:{ all -> 0x0062 }
            int r2 = r1.itemType     // Catch:{ all -> 0x0062 }
            switch(r2) {
                case 0: goto L_0x0052;
                case 1: goto L_0x0052;
                case 2: goto L_0x0045;
                case 3: goto L_0x0016;
                case 4: goto L_0x003f;
                case 5: goto L_0x003f;
                case 6: goto L_0x0017;
                default: goto L_0x0016;
            }     // Catch:{ all -> 0x0062 }
        L_0x0016:
            goto L_0x0058
        L_0x0017:
            com.android.launcher3.shortcuts.ShortcutKey r2 = com.android.launcher3.shortcuts.ShortcutKey.fromItemInfo(r1)     // Catch:{ all -> 0x0062 }
            java.util.Map<com.android.launcher3.shortcuts.ShortcutKey, android.util.MutableInt> r3 = r5.pinnedShortcutCounts     // Catch:{ all -> 0x0062 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x0062 }
            android.util.MutableInt r3 = (android.util.MutableInt) r3     // Catch:{ all -> 0x0062 }
            if (r3 == 0) goto L_0x002d
            int r4 = r3.value     // Catch:{ all -> 0x0062 }
            int r4 = r4 + -1
            r3.value = r4     // Catch:{ all -> 0x0062 }
            if (r4 != 0) goto L_0x0052
        L_0x002d:
            java.util.HashSet r4 = com.android.launcher3.InstallShortcutReceiver.getPendingShortcuts(r6)     // Catch:{ all -> 0x0062 }
            boolean r4 = r4.contains(r2)     // Catch:{ all -> 0x0062 }
            if (r4 != 0) goto L_0x0052
            com.android.launcher3.shortcuts.DeepShortcutManager r4 = com.android.launcher3.shortcuts.DeepShortcutManager.getInstance(r6)     // Catch:{ all -> 0x0062 }
            r4.unpinShortcut(r2)     // Catch:{ all -> 0x0062 }
            goto L_0x0052
        L_0x003f:
            java.util.ArrayList<com.android.launcher3.LauncherAppWidgetInfo> r2 = r5.appWidgets     // Catch:{ all -> 0x0062 }
            r2.remove(r1)     // Catch:{ all -> 0x0062 }
            goto L_0x0058
        L_0x0045:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.FolderInfo> r2 = r5.folders     // Catch:{ all -> 0x0062 }
            long r3 = r1.id     // Catch:{ all -> 0x0062 }
            r2.remove(r3)     // Catch:{ all -> 0x0062 }
            java.util.ArrayList<com.android.launcher3.ItemInfo> r2 = r5.workspaceItems     // Catch:{ all -> 0x0062 }
            r2.remove(r1)     // Catch:{ all -> 0x0062 }
            goto L_0x0058
        L_0x0052:
            java.util.ArrayList<com.android.launcher3.ItemInfo> r2 = r5.workspaceItems     // Catch:{ all -> 0x0062 }
            r2.remove(r1)     // Catch:{ all -> 0x0062 }
        L_0x0058:
            com.android.launcher3.util.LongArrayMap<com.android.launcher3.ItemInfo> r2 = r5.itemsIdMap     // Catch:{ all -> 0x0062 }
            long r3 = r1.id     // Catch:{ all -> 0x0062 }
            r2.remove(r3)     // Catch:{ all -> 0x0062 }
            goto L_0x0005
        L_0x0060:
            monitor-exit(r5)
            return
        L_0x0062:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.model.BgDataModel.removeItem(android.content.Context, java.lang.Iterable):void");
    }

    public synchronized void addItem(Context context, ItemInfo item, boolean newItem) {
        this.itemsIdMap.put(item.id, item);
        switch (item.itemType) {
            case 0:
            case 1:
                break;
            case 2:
                this.folders.put(item.id, (FolderInfo) item);
                this.workspaceItems.add(item);
                break;
            case 4:
            case 5:
                this.appWidgets.add((LauncherAppWidgetInfo) item);
                break;
            case 6:
                ShortcutKey pinnedShortcut = ShortcutKey.fromItemInfo(item);
                MutableInt count = this.pinnedShortcutCounts.get(pinnedShortcut);
                if (count == null) {
                    count = new MutableInt(1);
                    this.pinnedShortcutCounts.put(pinnedShortcut, count);
                } else {
                    count.value++;
                }
                if (newItem && count.value == 1) {
                    DeepShortcutManager.getInstance(context).pinShortcut(pinnedShortcut);
                    break;
                }
        }
        if (item.container != -100) {
            if (item.container != -101) {
                if (!newItem) {
                    findOrMakeFolder(item.container).add((ShortcutInfo) item, false);
                } else if (!this.folders.containsKey(item.container)) {
                    Log.e(TAG, "adding item: " + item + " to a folder that  doesn't exist");
                }
            }
        }
        this.workspaceItems.add(item);
    }

    public synchronized FolderInfo findOrMakeFolder(long id) {
        FolderInfo folderInfo;
        folderInfo = (FolderInfo) this.folders.get(id);
        if (folderInfo == null) {
            folderInfo = new FolderInfo();
            this.folders.put(id, folderInfo);
        }
        return folderInfo;
    }

    public synchronized void updateDeepShortcutMap(String packageName, UserHandle user, List<ShortcutInfoCompat> shortcuts) {
        if (packageName != null) {
            try {
                Iterator<ComponentKey> keysIter = this.deepShortcutMap.keySet().iterator();
                while (keysIter.hasNext()) {
                    ComponentKey next = keysIter.next();
                    if (next.componentName.getPackageName().equals(packageName) && next.user.equals(user)) {
                        keysIter.remove();
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        for (ShortcutInfoCompat shortcut : shortcuts) {
            if (shortcut.isEnabled() && (shortcut.isDeclaredInManifest() || shortcut.isDynamic())) {
                this.deepShortcutMap.addToList(new ComponentKey(shortcut.getActivity(), shortcut.getUserHandle()), shortcut.getId());
            }
        }
    }
}
