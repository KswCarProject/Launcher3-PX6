package com.szchoiceway.index;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import com.szchoiceway.index.LauncherSettings;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class ItemInfo {
    static final int NO_ID = -1;
    int cellX = -1;
    int cellY = -1;
    long container = -1;
    int[] dropPos = null;
    long id = -1;
    int itemType;
    int minSpanX = 1;
    int minSpanY = 1;
    boolean requiresDbUpdate = false;
    int screen = -1;
    int spanX = 1;
    int spanY = 1;
    CharSequence title;

    ItemInfo() {
    }

    ItemInfo(ItemInfo info) {
        this.id = info.id;
        this.cellX = info.cellX;
        this.cellY = info.cellY;
        this.spanX = info.spanX;
        this.spanY = info.spanY;
        this.screen = info.screen;
        this.itemType = info.itemType;
        this.container = info.container;
        LauncherModel.checkItemInfo(this);
    }

    static String getPackageName(Intent intent) {
        if (intent != null) {
            String packageName = intent.getPackage();
            if (packageName == null && intent.getComponent() != null) {
                packageName = intent.getComponent().getPackageName();
            }
            if (packageName != null) {
                return packageName;
            }
        }
        return "";
    }

    /* access modifiers changed from: package-private */
    public void onAddToDatabase(ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType));
        values.put("container", Long.valueOf(this.container));
        values.put("screen", Integer.valueOf(this.screen));
        values.put("cellX", Integer.valueOf(this.cellX));
        values.put("cellY", Integer.valueOf(this.cellY));
        values.put("spanX", Integer.valueOf(this.spanX));
        values.put("spanY", Integer.valueOf(this.spanY));
    }

    /* access modifiers changed from: package-private */
    public void updateValuesWithCoordinates(ContentValues values, int cellX2, int cellY2) {
        values.put("cellX", Integer.valueOf(cellX2));
        values.put("cellY", Integer.valueOf(cellY2));
    }

    static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            Log.w("Favorite", "Could not write icon");
            return null;
        }
    }

    static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
            values.put("icon", flattenBitmap(bitmap));
        }
    }

    /* access modifiers changed from: package-private */
    public void unbind() {
    }

    public String toString() {
        return "Item(id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen=" + this.screen + " cellX=" + this.cellX + " cellY=" + this.cellY + " spanX=" + this.spanX + " spanY=" + this.spanY + " dropPos=" + this.dropPos + ")";
    }
}
