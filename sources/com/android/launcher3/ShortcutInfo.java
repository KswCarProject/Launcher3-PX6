package com.android.launcher3;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.util.ContentWriter;

public class ShortcutInfo extends ItemInfoWithIcon {
    public static final int DEFAULT = 0;
    public static final int FLAG_AUTOINSTALL_ICON = 2;
    public static final int FLAG_INSTALL_SESSION_ACTIVE = 4;
    public static final int FLAG_RESTORED_ICON = 1;
    public static final int FLAG_RESTORE_STARTED = 8;
    public static final int FLAG_SUPPORTS_WEB_UI = 16;
    public CharSequence disabledMessage;
    public Intent.ShortcutIconResource iconResource;
    public Intent intent;
    private int mInstallProgress;
    public int status;

    public ShortcutInfo() {
        this.itemType = 1;
    }

    public ShortcutInfo(ShortcutInfo info) {
        super(info);
        this.title = info.title;
        this.intent = new Intent(info.intent);
        this.iconResource = info.iconResource;
        this.status = info.status;
        this.mInstallProgress = info.mInstallProgress;
    }

    public ShortcutInfo(AppInfo info) {
        super(info);
        this.title = Utilities.trim(info.title);
        this.intent = new Intent(info.intent);
    }

    @TargetApi(24)
    public ShortcutInfo(ShortcutInfoCompat shortcutInfo, Context context) {
        this.user = shortcutInfo.getUserHandle();
        this.itemType = 6;
        updateFromDeepShortcutInfo(shortcutInfo, context);
    }

    public void onAddToDatabase(ContentWriter writer) {
        super.onAddToDatabase(writer);
        writer.put(LauncherSettings.BaseLauncherColumns.TITLE, this.title).put(LauncherSettings.BaseLauncherColumns.INTENT, getIntent()).put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(this.status));
        if (!this.usingLowResIcon) {
            writer.putIcon(this.iconBitmap, this.user);
        }
        if (this.iconResource != null) {
            writer.put(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE, this.iconResource.packageName).put(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE, this.iconResource.resourceName);
        }
    }

    public Intent getIntent() {
        return this.intent;
    }

    public boolean hasStatusFlag(int flag) {
        return (this.status & flag) != 0;
    }

    public final boolean isPromise() {
        return hasStatusFlag(3);
    }

    public boolean hasPromiseIconUi() {
        return isPromise() && !hasStatusFlag(16);
    }

    public int getInstallProgress() {
        return this.mInstallProgress;
    }

    public void setInstallProgress(int progress) {
        this.mInstallProgress = progress;
        this.status |= 4;
    }

    public void updateFromDeepShortcutInfo(ShortcutInfoCompat shortcutInfo, Context context) {
        this.intent = shortcutInfo.makeIntent();
        this.title = shortcutInfo.getShortLabel();
        CharSequence label = shortcutInfo.getLongLabel();
        if (TextUtils.isEmpty(label)) {
            label = shortcutInfo.getShortLabel();
        }
        this.contentDescription = UserManagerCompat.getInstance(context).getBadgedLabelForUser(label, this.user);
        if (shortcutInfo.isEnabled()) {
            this.runtimeStatusFlags &= -17;
        } else {
            this.runtimeStatusFlags |= 16;
        }
        this.disabledMessage = shortcutInfo.getDisabledMessage();
    }

    public String getDeepShortcutId() {
        if (this.itemType == 6) {
            return getIntent().getStringExtra(ShortcutInfoCompat.EXTRA_SHORTCUT_ID);
        }
        return null;
    }

    public ComponentName getTargetComponent() {
        ComponentName cn = super.getTargetComponent();
        if (cn != null || (this.itemType != 1 && !hasStatusFlag(16))) {
            return cn;
        }
        String pkg = this.intent.getPackage();
        if (pkg == null) {
            return null;
        }
        return new ComponentName(pkg, IconCache.EMPTY_CLASS_NAME);
    }
}
