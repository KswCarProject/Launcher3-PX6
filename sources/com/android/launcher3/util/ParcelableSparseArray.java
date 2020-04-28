package com.android.launcher3.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public class ParcelableSparseArray extends SparseArray<Parcelable> implements Parcelable {
    public static final Parcelable.Creator<ParcelableSparseArray> CREATOR = new Parcelable.Creator<ParcelableSparseArray>() {
        public ParcelableSparseArray createFromParcel(Parcel source) {
            ParcelableSparseArray array = new ParcelableSparseArray();
            ClassLoader loader = array.getClass().getClassLoader();
            int count = source.readInt();
            for (int i = 0; i < count; i++) {
                array.put(source.readInt(), source.readParcelable(loader));
            }
            return array;
        }

        public ParcelableSparseArray[] newArray(int size) {
            return new ParcelableSparseArray[size];
        }
    };

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        int count = size();
        dest.writeInt(count);
        for (int i = 0; i < count; i++) {
            dest.writeInt(keyAt(i));
            dest.writeParcelable((Parcelable) valueAt(i), 0);
        }
    }
}
