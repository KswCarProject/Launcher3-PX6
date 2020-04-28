package com.android.launcher3.logging;

import android.util.ArrayMap;
import android.util.SparseArray;
import android.view.View;
import com.android.launcher3.AppInfo;
import com.android.launcher3.ButtonDropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.userevent.nano.LauncherLogExtensions;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.InstantAppResolver;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class LoggerUtils {
    private static final String UNKNOWN = "UNKNOWN";
    private static final ArrayMap<Class, SparseArray<String>> sNameCache = new ArrayMap<>();

    public static String getFieldName(int value, Class c) {
        SparseArray<String> cache;
        synchronized (sNameCache) {
            cache = sNameCache.get(c);
            if (cache == null) {
                cache = new SparseArray<>();
                for (Field f : c.getDeclaredFields()) {
                    if (f.getType() == Integer.TYPE && Modifier.isStatic(f.getModifiers())) {
                        try {
                            f.setAccessible(true);
                            cache.put(f.getInt((Object) null), f.getName());
                        } catch (IllegalAccessException e) {
                        }
                    }
                }
                sNameCache.put(c, cache);
            }
        }
        String result = cache.get(value);
        return result != null ? result : UNKNOWN;
    }

    public static String getActionStr(LauncherLogProto.Action action) {
        int i = action.type;
        if (i == 0) {
            String str = "" + getFieldName(action.touch, LauncherLogProto.Action.Touch.class);
            if (action.touch != 3 && action.touch != 4) {
                return str;
            }
            return str + " direction=" + getFieldName(action.dir, LauncherLogProto.Action.Direction.class);
        } else if (i != 2) {
            return getFieldName(action.type, LauncherLogProto.Action.Type.class);
        } else {
            return getFieldName(action.command, LauncherLogProto.Action.Command.class);
        }
    }

    public static String getTargetStr(LauncherLogProto.Target t) {
        String str;
        if (t == null) {
            return "";
        }
        switch (t.type) {
            case 1:
                str = getItemStr(t);
                break;
            case 2:
                str = getFieldName(t.controlType, LauncherLogProto.ControlType.class);
                break;
            case 3:
                str = getFieldName(t.containerType, LauncherLogProto.ContainerType.class);
                if (t.containerType != 1 && t.containerType != 2) {
                    if (t.containerType == 3) {
                        str = str + " grid(" + t.gridX + "," + t.gridY + ")";
                        break;
                    }
                } else {
                    str = str + " id=" + t.pageIndex;
                    break;
                }
                break;
            default:
                str = "" + "UNKNOWN TARGET TYPE";
                break;
        }
        if (t.tipType == 0) {
            return str;
        }
        return str + " " + getFieldName(t.tipType, LauncherLogProto.TipType.class);
    }

    private static String getItemStr(LauncherLogProto.Target t) {
        String typeStr = getFieldName(t.itemType, LauncherLogProto.ItemType.class);
        if (t.packageNameHash != 0) {
            typeStr = typeStr + ", packageHash=" + t.packageNameHash;
        }
        if (t.componentHash != 0) {
            typeStr = typeStr + ", componentHash=" + t.componentHash;
        }
        if (t.intentHash != 0) {
            typeStr = typeStr + ", intentHash=" + t.intentHash;
        }
        if (!((t.packageNameHash == 0 && t.componentHash == 0 && t.intentHash == 0) || t.itemType == 9)) {
            typeStr = typeStr + ", predictiveRank=" + t.predictedRank + ", grid(" + t.gridX + "," + t.gridY + "), span(" + t.spanX + "," + t.spanY + "), pageIdx=" + t.pageIndex;
        }
        if (t.itemType != 9) {
            return typeStr;
        }
        return typeStr + ", pageIdx=" + t.pageIndex;
    }

    public static LauncherLogProto.Target newItemTarget(int itemType) {
        LauncherLogProto.Target t = newTarget(1);
        t.itemType = itemType;
        return t;
    }

    public static LauncherLogProto.Target newItemTarget(View v, InstantAppResolver instantAppResolver) {
        if (v.getTag() instanceof ItemInfo) {
            return newItemTarget((ItemInfo) v.getTag(), instantAppResolver);
        }
        return newTarget(1);
    }

    public static LauncherLogProto.Target newItemTarget(ItemInfo info, InstantAppResolver instantAppResolver) {
        int i = 1;
        LauncherLogProto.Target t = newTarget(1);
        int i2 = info.itemType;
        if (i2 == 4) {
            t.itemType = 3;
        } else if (i2 != 6) {
            switch (i2) {
                case 0:
                    if (instantAppResolver != null && (info instanceof AppInfo) && instantAppResolver.isInstantApp((AppInfo) info)) {
                        i = 10;
                    }
                    t.itemType = i;
                    t.predictedRank = -100;
                    break;
                case 1:
                    t.itemType = 2;
                    break;
                case 2:
                    t.itemType = 4;
                    break;
            }
        } else {
            t.itemType = 5;
        }
        return t;
    }

    public static LauncherLogProto.Target newDropTarget(View v) {
        if (!(v instanceof ButtonDropTarget)) {
            return newTarget(3);
        }
        if (v instanceof ButtonDropTarget) {
            return ((ButtonDropTarget) v).getDropTargetForLogging();
        }
        return newTarget(2);
    }

    public static LauncherLogProto.Target newTarget(int targetType, LauncherLogExtensions.TargetExtension extension) {
        LauncherLogProto.Target t = new LauncherLogProto.Target();
        t.type = targetType;
        t.extension = extension;
        return t;
    }

    public static LauncherLogProto.Target newTarget(int targetType) {
        LauncherLogProto.Target t = new LauncherLogProto.Target();
        t.type = targetType;
        return t;
    }

    public static LauncherLogProto.Target newControlTarget(int controlType) {
        LauncherLogProto.Target t = newTarget(2);
        t.controlType = controlType;
        return t;
    }

    public static LauncherLogProto.Target newContainerTarget(int containerType) {
        LauncherLogProto.Target t = newTarget(3);
        t.containerType = containerType;
        return t;
    }

    public static LauncherLogProto.Action newAction(int type) {
        LauncherLogProto.Action a = new LauncherLogProto.Action();
        a.type = type;
        return a;
    }

    public static LauncherLogProto.Action newCommandAction(int command) {
        LauncherLogProto.Action a = newAction(2);
        a.command = command;
        return a;
    }

    public static LauncherLogProto.Action newTouchAction(int touch) {
        LauncherLogProto.Action a = newAction(0);
        a.touch = touch;
        return a;
    }

    public static LauncherLogProto.LauncherEvent newLauncherEvent(LauncherLogProto.Action action, LauncherLogProto.Target... srcTargets) {
        LauncherLogProto.LauncherEvent event = new LauncherLogProto.LauncherEvent();
        event.srcTarget = srcTargets;
        event.action = action;
        return event;
    }
}
