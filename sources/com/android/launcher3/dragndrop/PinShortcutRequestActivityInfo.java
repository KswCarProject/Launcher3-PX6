package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Process;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.IconCache;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.R;
import com.android.launcher3.compat.LauncherAppsCompatVO;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;

@TargetApi(26)
class PinShortcutRequestActivityInfo extends ShortcutConfigActivityInfo {
    private static final String DUMMY_COMPONENT_CLASS = "pinned-shortcut";
    private final Context mContext;
    private final ShortcutInfo mInfo;
    private final LauncherApps.PinItemRequest mRequest;

    public PinShortcutRequestActivityInfo(LauncherApps.PinItemRequest request, Context context) {
        super(new ComponentName(request.getShortcutInfo().getPackage(), DUMMY_COMPONENT_CLASS), request.getShortcutInfo().getUserHandle());
        this.mRequest = request;
        this.mInfo = request.getShortcutInfo();
        this.mContext = context;
    }

    public int getItemType() {
        return 6;
    }

    public CharSequence getLabel() {
        return this.mInfo.getShortLabel();
    }

    public Drawable getFullResIcon(IconCache cache) {
        Drawable d = ((LauncherApps) this.mContext.getSystemService(LauncherApps.class)).getShortcutIconDrawable(this.mInfo, LauncherAppState.getIDP(this.mContext).fillResIconDpi);
        if (d == null) {
            return new FastBitmapDrawable(cache.getDefaultIcon(Process.myUserHandle()));
        }
        return d;
    }

    public com.android.launcher3.ShortcutInfo createShortcutInfo() {
        return LauncherAppsCompatVO.createShortcutInfoFromPinItemRequest(this.mContext, this.mRequest, (long) (this.mContext.getResources().getInteger(R.integer.config_dropAnimMaxDuration) + 500 + 150));
    }

    public boolean startConfigActivity(Activity activity, int requestCode) {
        return false;
    }

    public boolean isPersistable() {
        return false;
    }
}
