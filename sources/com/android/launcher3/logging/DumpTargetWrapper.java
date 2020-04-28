package com.android.launcher3.logging;

import android.os.Process;
import android.text.TextUtils;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.model.nano.LauncherDumpProto;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DumpTargetWrapper {
    ArrayList<DumpTargetWrapper> children;
    LauncherDumpProto.DumpTarget node;

    public DumpTargetWrapper() {
        this.children = new ArrayList<>();
    }

    public DumpTargetWrapper(int containerType, int id) {
        this();
        this.node = newContainerTarget(containerType, id);
    }

    public DumpTargetWrapper(ItemInfo info) {
        this();
        this.node = newItemTarget(info);
    }

    public LauncherDumpProto.DumpTarget getDumpTarget() {
        return this.node;
    }

    public void add(DumpTargetWrapper child) {
        this.children.add(child);
    }

    public List<LauncherDumpProto.DumpTarget> getFlattenedList() {
        ArrayList<LauncherDumpProto.DumpTarget> list = new ArrayList<>();
        list.add(this.node);
        if (!this.children.isEmpty()) {
            Iterator<DumpTargetWrapper> it = this.children.iterator();
            while (it.hasNext()) {
                list.addAll(it.next().getFlattenedList());
            }
            list.add(this.node);
        }
        return list;
    }

    public LauncherDumpProto.DumpTarget newItemTarget(ItemInfo info) {
        LauncherDumpProto.DumpTarget dt = new LauncherDumpProto.DumpTarget();
        dt.type = 1;
        int i = info.itemType;
        if (i == 4) {
            dt.itemType = 2;
        } else if (i != 6) {
            switch (i) {
                case 0:
                    dt.itemType = 1;
                    break;
                case 1:
                    dt.itemType = 0;
                    break;
            }
        } else {
            dt.itemType = 3;
        }
        return dt;
    }

    public LauncherDumpProto.DumpTarget newContainerTarget(int type, int id) {
        LauncherDumpProto.DumpTarget dt = new LauncherDumpProto.DumpTarget();
        dt.type = 2;
        dt.containerType = type;
        dt.pageId = id;
        return dt;
    }

    public static String getDumpTargetStr(LauncherDumpProto.DumpTarget t) {
        if (t == null) {
            return "";
        }
        switch (t.type) {
            case 1:
                return getItemStr(t);
            case 2:
                String str = LoggerUtils.getFieldName(t.containerType, LauncherDumpProto.ContainerType.class);
                if (t.containerType == 1) {
                    return str + " id=" + t.pageId;
                } else if (t.containerType != 3) {
                    return str;
                } else {
                    return str + " grid(" + t.gridX + "," + t.gridY + ")";
                }
            default:
                return "UNKNOWN TARGET TYPE";
        }
    }

    private static String getItemStr(LauncherDumpProto.DumpTarget t) {
        String typeStr = LoggerUtils.getFieldName(t.itemType, LauncherDumpProto.ItemType.class);
        if (!TextUtils.isEmpty(t.packageName)) {
            typeStr = typeStr + ", package=" + t.packageName;
        }
        if (!TextUtils.isEmpty(t.component)) {
            typeStr = typeStr + ", component=" + t.component;
        }
        return typeStr + ", grid(" + t.gridX + "," + t.gridY + "), span(" + t.spanX + "," + t.spanY + "), pageIdx=" + t.pageId + " user=" + t.userType;
    }

    public LauncherDumpProto.DumpTarget writeToDumpTarget(ItemInfo info) {
        String str;
        String str2;
        LauncherDumpProto.DumpTarget dumpTarget = this.node;
        if (info.getTargetComponent() == null) {
            str = "";
        } else {
            str = info.getTargetComponent().flattenToString();
        }
        dumpTarget.component = str;
        LauncherDumpProto.DumpTarget dumpTarget2 = this.node;
        if (info.getTargetComponent() == null) {
            str2 = "";
        } else {
            str2 = info.getTargetComponent().getPackageName();
        }
        dumpTarget2.packageName = str2;
        if (info instanceof LauncherAppWidgetInfo) {
            this.node.component = ((LauncherAppWidgetInfo) info).providerName.flattenToString();
            this.node.packageName = ((LauncherAppWidgetInfo) info).providerName.getPackageName();
        }
        this.node.gridX = info.cellX;
        this.node.gridY = info.cellY;
        this.node.spanX = info.spanX;
        this.node.spanY = info.spanY;
        this.node.userType = info.user.equals(Process.myUserHandle()) ^ true ? 1 : 0;
        return this.node;
    }
}
