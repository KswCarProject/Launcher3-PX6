package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.content.pm.LauncherApps;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.DragEvent;
import android.view.View;
import android.widget.RemoteViews;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PendingAddItemInfo;
import com.android.launcher3.uioverrides.UiFactory;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingItemDragHelper;
import com.android.launcher3.widget.WidgetAddFlowHandler;

@TargetApi(26)
public class PinItemDragListener extends BaseItemDragListener {
    private final CancellationSignal mCancelSignal = new CancellationSignal();
    private final LauncherApps.PinItemRequest mRequest;

    public PinItemDragListener(LauncherApps.PinItemRequest request, Rect previewRect, int previewBitmapWidth, int previewViewWidth) {
        super(previewRect, previewBitmapWidth, previewViewWidth);
        this.mRequest = request;
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent event) {
        if (!this.mRequest.isValid()) {
            return false;
        }
        return super.onDragStart(event);
    }

    public boolean init(Launcher launcher, boolean alreadyOnHome) {
        super.init(launcher, alreadyOnHome);
        if (alreadyOnHome) {
            return false;
        }
        UiFactory.useFadeOutAnimationForLauncherStart(launcher, this.mCancelSignal);
        return false;
    }

    /* access modifiers changed from: protected */
    public PendingItemDragHelper createDragHelper() {
        PendingAddItemInfo item;
        if (this.mRequest.getRequestType() == 1) {
            item = new PendingAddShortcutInfo(new PinShortcutRequestActivityInfo(this.mRequest, this.mLauncher));
        } else {
            LauncherAppWidgetProviderInfo providerInfo = LauncherAppWidgetProviderInfo.fromProviderInfo(this.mLauncher, this.mRequest.getAppWidgetProviderInfo(this.mLauncher));
            final PinWidgetFlowHandler flowHandler = new PinWidgetFlowHandler(providerInfo, this.mRequest);
            item = new PendingAddWidgetInfo(providerInfo) {
                public WidgetAddFlowHandler getHandler() {
                    return flowHandler;
                }
            };
        }
        View view = new View(this.mLauncher);
        view.setTag(item);
        PendingItemDragHelper dragHelper = new PendingItemDragHelper(view);
        if (this.mRequest.getRequestType() == 2) {
            dragHelper.setPreview(getPreview(this.mRequest));
        }
        return dragHelper;
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        targetParent.containerType = 10;
    }

    /* access modifiers changed from: protected */
    public void postCleanup() {
        super.postCleanup();
        this.mCancelSignal.cancel();
    }

    public static RemoteViews getPreview(LauncherApps.PinItemRequest request) {
        Bundle extras = request.getExtras();
        if (extras == null || !(extras.get("appWidgetPreview") instanceof RemoteViews)) {
            return null;
        }
        return (RemoteViews) extras.get("appWidgetPreview");
    }
}
