package com.szchoiceway.index;

import android.net.Uri;
import android.provider.BaseColumns;

class LauncherSettings {

    interface BaseLauncherColumns extends BaseColumns {
        public static final String ICON = "icon";
        public static final String ICON_PACKAGE = "iconPackage";
        public static final String ICON_RESOURCE = "iconResource";
        public static final String ICON_TYPE = "iconType";
        public static final int ICON_TYPE_BITMAP = 1;
        public static final int ICON_TYPE_RESOURCE = 0;
        public static final String INTENT = "intent";
        public static final String ITEM_TYPE = "itemType";
        public static final int ITEM_TYPE_APPLICATION = 0;
        public static final int ITEM_TYPE_SHORTCUT = 1;
        public static final String TITLE = "title";
    }

    LauncherSettings() {
    }

    static final class Favorites implements BaseLauncherColumns {
        static final String APPWIDGET_ID = "appWidgetId";
        static final String CELLX = "cellX";
        static final String CELLY = "cellY";
        static final String CONTAINER = "container";
        static final int CONTAINER_DESKTOP = -100;
        static final int CONTAINER_HOTSEAT = -101;
        static final Uri CONTENT_URI = Uri.parse("content://com.szchoiceway.index.settings/favorites?notify=true");
        static final Uri CONTENT_URI_NO_NOTIFICATION = Uri.parse("content://com.szchoiceway.index.settings/favorites?notify=false");
        static final String DISPLAY_MODE = "displayMode";
        @Deprecated
        static final String IS_SHORTCUT = "isShortcut";
        static final int ITEM_TYPE_APPWIDGET = 4;
        static final int ITEM_TYPE_FOLDER = 2;
        static final int ITEM_TYPE_LIVE_FOLDER = 3;
        static final int ITEM_TYPE_WIDGET_CLOCK = 1000;
        static final int ITEM_TYPE_WIDGET_PHOTO_FRAME = 1002;
        static final int ITEM_TYPE_WIDGET_SEARCH = 1001;
        static final String SCREEN = "screen";
        static final String SPANX = "spanX";
        static final String SPANY = "spanY";
        static final String URI = "uri";

        Favorites() {
        }

        static Uri getContentUri(long id, boolean notify) {
            return Uri.parse("content://com.szchoiceway.index.settings/favorites/" + id + "?" + "notify" + "=" + notify);
        }
    }
}
