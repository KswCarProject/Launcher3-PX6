package com.android.launcher3.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.UserHandle;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.widget.WidgetAddFlowHandler;

public class PendingRequestArgs extends ItemInfo implements Parcelable {
    public static final Parcelable.Creator<PendingRequestArgs> CREATOR = new Parcelable.Creator<PendingRequestArgs>() {
        public PendingRequestArgs createFromParcel(Parcel source) {
            return new PendingRequestArgs(source);
        }

        public PendingRequestArgs[] newArray(int size) {
            return new PendingRequestArgs[size];
        }
    };
    private static final int TYPE_APP_WIDGET = 2;
    private static final int TYPE_INTENT = 1;
    private static final int TYPE_NONE = 0;
    private final int mArg1;
    private final Parcelable mObject;
    private final int mObjectType;

    public PendingRequestArgs(ItemInfo info) {
        this.mArg1 = 0;
        this.mObjectType = 0;
        this.mObject = null;
        copyFrom(info);
    }

    private PendingRequestArgs(int arg1, int objectType, Parcelable object) {
        this.mArg1 = arg1;
        this.mObjectType = objectType;
        this.mObject = object;
    }

    public PendingRequestArgs(Parcel parcel) {
        readFromValues((ContentValues) ContentValues.CREATOR.createFromParcel(parcel));
        this.user = (UserHandle) parcel.readParcelable((ClassLoader) null);
        this.mArg1 = parcel.readInt();
        this.mObjectType = parcel.readInt();
        this.mObject = parcel.readParcelable(getClass().getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        ContentValues itemValues = new ContentValues();
        writeToValues(new ContentWriter(itemValues, (Context) null));
        itemValues.writeToParcel(dest, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeInt(this.mArg1);
        dest.writeInt(this.mObjectType);
        dest.writeParcelable(this.mObject, flags);
    }

    public WidgetAddFlowHandler getWidgetHandler() {
        if (this.mObjectType == 2) {
            return (WidgetAddFlowHandler) this.mObject;
        }
        return null;
    }

    public int getWidgetId() {
        if (this.mObjectType == 2) {
            return this.mArg1;
        }
        return 0;
    }

    public Intent getPendingIntent() {
        if (this.mObjectType == 1) {
            return (Intent) this.mObject;
        }
        return null;
    }

    public int getRequestCode() {
        if (this.mObjectType == 1) {
            return this.mArg1;
        }
        return 0;
    }

    public static PendingRequestArgs forWidgetInfo(int appWidgetId, WidgetAddFlowHandler widgetHandler, ItemInfo info) {
        PendingRequestArgs args = new PendingRequestArgs(appWidgetId, 2, widgetHandler);
        args.copyFrom(info);
        return args;
    }

    public static PendingRequestArgs forIntent(int requestCode, Intent intent, ItemInfo info) {
        PendingRequestArgs args = new PendingRequestArgs(requestCode, 1, intent);
        args.copyFrom(info);
        return args;
    }
}
