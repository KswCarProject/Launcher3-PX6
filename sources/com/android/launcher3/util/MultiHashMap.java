package com.android.launcher3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MultiHashMap<K, V> extends HashMap<K, ArrayList<V>> {
    public MultiHashMap() {
    }

    public MultiHashMap(int size) {
        super(size);
    }

    public void addToList(K key, V value) {
        ArrayList<V> list = (ArrayList) get(key);
        if (list == null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(value);
            put(key, arrayList);
            return;
        }
        list.add(value);
    }

    public MultiHashMap<K, V> clone() {
        MultiHashMap<K, V> map = new MultiHashMap<>(size());
        for (Map.Entry<K, ArrayList<V>> entry : entrySet()) {
            map.put(entry.getKey(), new ArrayList(entry.getValue()));
        }
        return map;
    }
}
