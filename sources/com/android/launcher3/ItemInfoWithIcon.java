package com.android.launcher3;

import android.graphics.Bitmap;

public abstract class ItemInfoWithIcon extends ItemInfo {
    public static final int FLAG_ADAPTIVE_ICON = 256;
    public static final int FLAG_DISABLED_BY_PUBLISHER = 16;
    public static final int FLAG_DISABLED_LOCKED_USER = 32;
    public static final int FLAG_DISABLED_MASK = 63;
    public static final int FLAG_DISABLED_NOT_AVAILABLE = 2;
    public static final int FLAG_DISABLED_QUIET_USER = 8;
    public static final int FLAG_DISABLED_SAFEMODE = 1;
    public static final int FLAG_DISABLED_SUSPENDED = 4;
    public static final int FLAG_ICON_BADGED = 512;
    public static final int FLAG_SYSTEM_MASK = 192;
    public static final int FLAG_SYSTEM_NO = 128;
    public static final int FLAG_SYSTEM_YES = 64;
    public Bitmap iconBitmap;
    public int iconColor;
    public int runtimeStatusFlags = 0;
    public boolean usingLowResIcon;

    protected ItemInfoWithIcon() {
    }

    protected ItemInfoWithIcon(ItemInfoWithIcon info) {
        super(info);
        this.iconBitmap = info.iconBitmap;
        this.iconColor = info.iconColor;
        this.usingLowResIcon = info.usingLowResIcon;
        this.runtimeStatusFlags = info.runtimeStatusFlags;
    }

    public boolean isDisabled() {
        return (this.runtimeStatusFlags & 63) != 0;
    }
}
