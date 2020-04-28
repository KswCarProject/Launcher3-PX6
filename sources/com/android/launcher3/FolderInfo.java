package com.android.launcher3;

import android.os.Process;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.util.ContentWriter;
import java.util.ArrayList;

public class FolderInfo extends ItemInfo {
    public static final int FLAG_ITEMS_SORTED = 1;
    public static final int FLAG_MULTI_PAGE_ANIMATION = 4;
    public static final int FLAG_WORK_FOLDER = 2;
    public static final int NO_FLAGS = 0;
    public ArrayList<ShortcutInfo> contents = new ArrayList<>();
    ArrayList<FolderListener> listeners = new ArrayList<>();
    public int options;

    public interface FolderListener {
        void onAdd(ShortcutInfo shortcutInfo, int i);

        void onItemsChanged(boolean z);

        void onRemove(ShortcutInfo shortcutInfo);

        void onTitleChanged(CharSequence charSequence);

        void prepareAutoUpdate();
    }

    public FolderInfo() {
        this.itemType = 2;
        this.user = Process.myUserHandle();
    }

    public void add(ShortcutInfo item, boolean animate) {
        add(item, this.contents.size(), animate);
    }

    public void add(ShortcutInfo item, int rank, boolean animate) {
        int i = 0;
        int rank2 = Utilities.boundToRange(rank, 0, this.contents.size());
        this.contents.add(rank2, item);
        while (true) {
            int i2 = i;
            if (i2 < this.listeners.size()) {
                this.listeners.get(i2).onAdd(item, rank2);
                i = i2 + 1;
            } else {
                itemsChanged(animate);
                return;
            }
        }
    }

    public void remove(ShortcutInfo item, boolean animate) {
        this.contents.remove(item);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onRemove(item);
        }
        itemsChanged(animate);
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onTitleChanged(title);
        }
    }

    public void onAddToDatabase(ContentWriter writer) {
        super.onAddToDatabase(writer);
        writer.put(LauncherSettings.BaseLauncherColumns.TITLE, this.title).put(LauncherSettings.Favorites.OPTIONS, Integer.valueOf(this.options));
    }

    public void addListener(FolderListener listener) {
        this.listeners.add(listener);
    }

    public void removeListener(FolderListener listener) {
        this.listeners.remove(listener);
    }

    public void itemsChanged(boolean animate) {
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onItemsChanged(animate);
        }
    }

    public void prepareAutoUpdate() {
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).prepareAutoUpdate();
        }
    }

    public boolean hasOption(int optionFlag) {
        return (this.options & optionFlag) != 0;
    }

    public void setOption(int option, boolean isEnabled, ModelWriter writer) {
        int oldOptions = this.options;
        if (isEnabled) {
            this.options |= option;
        } else {
            this.options &= ~option;
        }
        if (writer != null && oldOptions != this.options) {
            writer.updateItemInDatabase(this);
        }
    }
}
