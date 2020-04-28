package com.android.launcher3.util;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

public class ActivityResultInfo implements Parcelable {
    public static final Parcelable.Creator<ActivityResultInfo> CREATOR = new Parcelable.Creator<ActivityResultInfo>() {
        public ActivityResultInfo createFromParcel(Parcel source) {
            return new ActivityResultInfo(source);
        }

        public ActivityResultInfo[] newArray(int size) {
            return new ActivityResultInfo[size];
        }
    };
    public final Intent data;
    public final int requestCode;
    public final int resultCode;

    public ActivityResultInfo(int requestCode2, int resultCode2, Intent data2) {
        this.requestCode = requestCode2;
        this.resultCode = resultCode2;
        this.data = data2;
    }

    private ActivityResultInfo(Parcel parcel) {
        this.requestCode = parcel.readInt();
        this.resultCode = parcel.readInt();
        this.data = parcel.readInt() != 0 ? (Intent) Intent.CREATOR.createFromParcel(parcel) : null;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.requestCode);
        dest.writeInt(this.resultCode);
        if (this.data != null) {
            dest.writeInt(1);
            this.data.writeToParcel(dest, flags);
            return;
        }
        dest.writeInt(0);
    }
}
