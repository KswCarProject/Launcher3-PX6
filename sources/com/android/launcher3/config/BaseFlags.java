package com.android.launcher3.config;

abstract class BaseFlags {
    public static final boolean ALL_APPS_TABS_ENABLED = true;
    public static final String AUTHORITY = "com.android.launcher3.settings".intern();
    public static final boolean ENABLE_CUSTOM_WIDGETS = false;
    public static final boolean GO_DISABLE_WIDGETS = false;
    public static final boolean IS_DOGFOOD_BUILD = false;
    public static final boolean LAUNCHER3_DIRECT_SCROLL = true;
    public static final boolean LAUNCHER3_PROMISE_APPS_IN_ALL_APPS = false;
    public static final boolean LAUNCHER3_SPRING_ICONS = true;
    public static final boolean NO_ALL_APPS_ICON = true;
    public static final boolean OVERVIEW_USE_SCREENSHOT_ORIENTATION = true;
    public static final boolean QSB_ON_FIRST_SCREEN = true;

    BaseFlags() {
    }
}
