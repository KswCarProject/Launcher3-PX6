package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetProviderInfo;
import android.content.pm.LauncherApps;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.widget.WidgetAddFlowHandler;

@TargetApi(26)
public class PinWidgetFlowHandler extends WidgetAddFlowHandler implements Parcelable {
    public static final Parcelable.Creator<PinWidgetFlowHandler> CREATOR = new Parcelable.Creator<PinWidgetFlowHandler>() {
        public PinWidgetFlowHandler createFromParcel(Parcel source) {
            return new PinWidgetFlowHandler(source);
        }

        public PinWidgetFlowHandler[] newArray(int size) {
            return new PinWidgetFlowHandler[size];
        }
    };
    private final LauncherApps.PinItemRequest mRequest;

    public PinWidgetFlowHandler(AppWidgetProviderInfo providerInfo, LauncherApps.PinItemRequest request) {
        super(providerInfo);
        this.mRequest = request;
    }

    protected PinWidgetFlowHandler(Parcel parcel) {
        super(parcel);
        this.mRequest = (LauncherApps.PinItemRequest) LauncherApps.PinItemRequest.CREATOR.createFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        this.mRequest.writeToParcel(parcel, i);
    }

    public boolean startConfigActivity(Launcher launcher, int appWidgetId, ItemInfo info, int requestCode) {
        Bundle extras = new Bundle();
        extras.putInt(LauncherSettings.Favorites.APPWIDGET_ID, appWidgetId);
        this.mRequest.accept(extras);
        return false;
    }

    public boolean needsConfigure() {
        return false;
    }
}
