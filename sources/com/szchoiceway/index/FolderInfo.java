package com.szchoiceway.index;

import android.content.ContentValues;
import com.szchoiceway.index.LauncherSettings;
import java.util.ArrayList;

class FolderInfo extends ItemInfo {
    ArrayList<ShortcutInfo> contents = new ArrayList<>();
    ArrayList<FolderListener> listeners = new ArrayList<>();
    boolean opened;

    interface FolderListener {
        void onAdd(ShortcutInfo shortcutInfo);

        void onItemsChanged();

        void onRemove(ShortcutInfo shortcutInfo);

        void onTitleChanged(CharSequence charSequence);
    }

    FolderInfo() {
        this.itemType = 2;
    }

    public void add(ShortcutInfo item) {
        this.contents.add(item);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onAdd(item);
        }
        itemsChanged();
    }

    public void remove(ShortcutInfo item) {
        this.contents.remove(item);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onRemove(item);
        }
        itemsChanged();
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onTitleChanged(title);
        }
    }

    /* access modifiers changed from: package-private */
    public void onAddToDatabase(ContentValues values) {
        super.onAddToDatabase(values);
        values.put(LauncherSettings.BaseLauncherColumns.TITLE, this.title.toString());
    }

    /* access modifiers changed from: package-private */
    public void addListener(FolderListener listener) {
        this.listeners.add(listener);
    }

    /* access modifiers changed from: package-private */
    public void removeListener(FolderListener listener) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    /* access modifiers changed from: package-private */
    public void itemsChanged() {
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onItemsChanged();
        }
    }

    /* access modifiers changed from: package-private */
    public void unbind() {
        super.unbind();
        this.listeners.clear();
    }
}
