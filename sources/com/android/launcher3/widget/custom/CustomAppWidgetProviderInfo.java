package com.android.launcher3.widget.custom;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.Utilities;

public class CustomAppWidgetProviderInfo extends LauncherAppWidgetProviderInfo implements Parcelable {
    public static final Parcelable.Creator<CustomAppWidgetProviderInfo> CREATOR = new Parcelable.Creator<CustomAppWidgetProviderInfo>() {
        public CustomAppWidgetProviderInfo createFromParcel(Parcel parcel) {
            return new CustomAppWidgetProviderInfo(parcel, true, 0);
        }

        public CustomAppWidgetProviderInfo[] newArray(int size) {
            return new CustomAppWidgetProviderInfo[size];
        }
    };
    public final int providerId;

    protected CustomAppWidgetProviderInfo(Parcel parcel, boolean readSelf, int providerId2) {
        super(parcel);
        if (readSelf) {
            this.providerId = parcel.readInt();
            String readString = parcel.readString();
            this.provider = new ComponentName(readString, LauncherAppWidgetProviderInfo.CLS_CUSTOM_WIDGET_PREFIX + providerId2);
            this.label = parcel.readString();
            this.initialLayout = parcel.readInt();
            this.icon = parcel.readInt();
            this.previewImage = parcel.readInt();
            this.resizeMode = parcel.readInt();
            this.spanX = parcel.readInt();
            this.spanY = parcel.readInt();
            this.minSpanX = parcel.readInt();
            this.minSpanY = parcel.readInt();
            return;
        }
        this.providerId = providerId2;
    }

    public void initSpans(Context context) {
    }

    public String getLabel(PackageManager packageManager) {
        return Utilities.trim(this.label);
    }

    public String toString() {
        return "WidgetProviderInfo(" + this.provider + ")";
    }

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        out.writeInt(this.providerId);
        out.writeString(this.provider.getPackageName());
        out.writeString(this.label);
        out.writeInt(this.initialLayout);
        out.writeInt(this.icon);
        out.writeInt(this.previewImage);
        out.writeInt(this.resizeMode);
        out.writeInt(this.spanX);
        out.writeInt(this.spanY);
        out.writeInt(this.minSpanX);
        out.writeInt(this.minSpanY);
    }
}
