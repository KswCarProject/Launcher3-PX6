package com.szchoiceway.index;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.pm.ComponentInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PagedViewIconCache {
    private final HashMap<Key, Bitmap> mIconOutlineCache = new HashMap<>();

    public static class Key {
        private final ComponentName mComponentName;
        private final Type mType;

        public enum Type {
            ApplicationInfoKey,
            AppWidgetProviderInfoKey,
            ResolveInfoKey
        }

        public Key(ApplicationInfo info) {
            this.mComponentName = info.componentName;
            this.mType = Type.ApplicationInfoKey;
        }

        public Key(ResolveInfo info) {
            ComponentInfo ci = info.activityInfo != null ? info.activityInfo : info.serviceInfo;
            this.mComponentName = new ComponentName(ci.packageName, ci.name);
            this.mType = Type.ResolveInfoKey;
        }

        public Key(AppWidgetProviderInfo info) {
            this.mComponentName = info.provider;
            this.mType = Type.AppWidgetProviderInfoKey;
        }

        private ComponentName getComponentName() {
            return this.mComponentName;
        }

        public boolean isKeyType(Type t) {
            return this.mType == t;
        }

        public boolean equals(Object o) {
            if (o instanceof Key) {
                return this.mComponentName.equals(((Key) o).mComponentName);
            }
            return super.equals(o);
        }

        public int hashCode() {
            return getComponentName().hashCode();
        }
    }

    public void clear() {
        for (Key key : this.mIconOutlineCache.keySet()) {
            this.mIconOutlineCache.get(key).recycle();
        }
        this.mIconOutlineCache.clear();
    }

    private void retainAll(HashSet<Key> keysToKeep, Key.Type t) {
        HashSet<Key> keysToRemove = new HashSet<>(this.mIconOutlineCache.keySet());
        keysToRemove.removeAll(keysToKeep);
        Iterator<Key> it = keysToRemove.iterator();
        while (it.hasNext()) {
            Key key = it.next();
            if (key.isKeyType(t)) {
                this.mIconOutlineCache.get(key).recycle();
                this.mIconOutlineCache.remove(key);
            }
        }
    }

    public void retainAllApps(ArrayList<ApplicationInfo> keys) {
        HashSet<Key> keysSet = new HashSet<>();
        Iterator<ApplicationInfo> it = keys.iterator();
        while (it.hasNext()) {
            keysSet.add(new Key(it.next()));
        }
        retainAll(keysSet, Key.Type.ApplicationInfoKey);
    }

    public void retainAllShortcuts(List<ResolveInfo> keys) {
        HashSet<Key> keysSet = new HashSet<>();
        for (ResolveInfo info : keys) {
            keysSet.add(new Key(info));
        }
        retainAll(keysSet, Key.Type.ResolveInfoKey);
    }

    public void retainAllAppWidgets(List<AppWidgetProviderInfo> keys) {
        HashSet<Key> keysSet = new HashSet<>();
        for (AppWidgetProviderInfo info : keys) {
            keysSet.add(new Key(info));
        }
        retainAll(keysSet, Key.Type.AppWidgetProviderInfoKey);
    }

    public void addOutline(Key key, Bitmap b) {
        this.mIconOutlineCache.put(key, b);
    }

    public void removeOutline(Key key) {
        if (this.mIconOutlineCache.containsKey(key)) {
            this.mIconOutlineCache.get(key).recycle();
            this.mIconOutlineCache.remove(key);
        }
    }

    public Bitmap getOutline(Key key) {
        return this.mIconOutlineCache.get(key);
    }
}
