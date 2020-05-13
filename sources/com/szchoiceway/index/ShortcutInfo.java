package com.szchoiceway.index;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import com.szchoiceway.index.LauncherSettings;
import java.util.ArrayList;
import java.util.Iterator;

class ShortcutInfo extends ItemInfo {
    boolean customIcon;
    Intent.ShortcutIconResource iconResource;
    Intent intent;
    private Bitmap mIcon;
    boolean usingFallbackIcon;

    ShortcutInfo() {
        this.itemType = 1;
    }

    public ShortcutInfo(ShortcutInfo info) {
        super(info);
        this.title = info.title.toString();
        this.intent = new Intent(info.intent);
        if (info.iconResource != null) {
            this.iconResource = new Intent.ShortcutIconResource();
            this.iconResource.packageName = info.iconResource.packageName;
            this.iconResource.resourceName = info.iconResource.resourceName;
        }
        this.mIcon = info.mIcon;
        this.customIcon = info.customIcon;
    }

    public ShortcutInfo(ApplicationInfo info) {
        super(info);
        this.title = info.title.toString();
        this.intent = new Intent(info.intent);
        this.customIcon = false;
    }

    public void setIcon(Bitmap b) {
        this.mIcon = b;
    }

    public Bitmap getIcon(IconCache iconCache) {
        if (this.mIcon == null) {
            updateIcon(iconCache);
        }
        return this.mIcon;
    }

    public void updateIcon(IconCache iconCache) {
        this.mIcon = iconCache.getIcon(this.intent);
        this.usingFallbackIcon = iconCache.isDefaultIcon(this.mIcon);
    }

    /* access modifiers changed from: package-private */
    public final void setActivity(ComponentName className, int launchFlags) {
        this.intent = new Intent("android.intent.action.MAIN");
        this.intent.addCategory("android.intent.category.LAUNCHER");
        this.intent.setComponent(className);
        this.intent.setFlags(launchFlags);
        this.itemType = 0;
    }

    /* access modifiers changed from: package-private */
    public void onAddToDatabase(ContentValues values) {
        String titleStr;
        String uri;
        super.onAddToDatabase(values);
        if (this.title != null) {
            titleStr = this.title.toString();
        } else {
            titleStr = null;
        }
        values.put(LauncherSettings.BaseLauncherColumns.TITLE, titleStr);
        if (this.intent != null) {
            uri = this.intent.toUri(0);
        } else {
            uri = null;
        }
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, uri);
        if (this.customIcon) {
            values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, 1);
            writeBitmap(values, this.mIcon);
            return;
        }
        if (!this.usingFallbackIcon) {
            writeBitmap(values, this.mIcon);
        }
        values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, 0);
        if (this.iconResource != null) {
            values.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.iconResource.packageName);
            values.put("iconResource", this.iconResource.resourceName);
        }
    }

    public String toString() {
        return "ShortcutInfo(title=" + this.title.toString() + "intent=" + this.intent + "id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen=" + this.screen + " cellX=" + this.cellX + " cellY=" + this.cellY + " spanX=" + this.spanX + " spanY=" + this.spanY + " dropPos=" + this.dropPos + ")";
    }

    public static void dumpShortcutInfoList(String tag, String label, ArrayList<ShortcutInfo> list) {
        Log.d(tag, label + " size=" + list.size());
        Iterator<ShortcutInfo> it = list.iterator();
        while (it.hasNext()) {
            ShortcutInfo info = it.next();
            Log.d(tag, "   title=\"" + info.title + " icon=" + info.mIcon + " customIcon=" + info.customIcon);
        }
    }
}
