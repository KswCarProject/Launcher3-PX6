package com.android.launcher3.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.UserHandle;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;

public class ContentWriter {
    private CommitParams mCommitParams;
    private final Context mContext;
    private Bitmap mIcon;
    private UserHandle mUser;
    private final ContentValues mValues;

    public ContentWriter(Context context, CommitParams commitParams) {
        this(context);
        this.mCommitParams = commitParams;
    }

    public ContentWriter(Context context) {
        this(new ContentValues(), context);
    }

    public ContentWriter(ContentValues values, Context context) {
        this.mValues = values;
        this.mContext = context;
    }

    public ContentWriter put(String key, Integer value) {
        this.mValues.put(key, value);
        return this;
    }

    public ContentWriter put(String key, Long value) {
        this.mValues.put(key, value);
        return this;
    }

    public ContentWriter put(String key, String value) {
        this.mValues.put(key, value);
        return this;
    }

    public ContentWriter put(String key, CharSequence value) {
        this.mValues.put(key, value == null ? null : value.toString());
        return this;
    }

    public ContentWriter put(String key, Intent value) {
        this.mValues.put(key, value == null ? null : value.toUri(0));
        return this;
    }

    public ContentWriter putIcon(Bitmap value, UserHandle user) {
        this.mIcon = value;
        this.mUser = user;
        return this;
    }

    public ContentWriter put(String key, UserHandle user) {
        return put(key, Long.valueOf(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(user)));
    }

    public ContentValues getValues(Context context) {
        Preconditions.assertNonUiThread();
        if (this.mIcon != null && !LauncherAppState.getInstance(context).getIconCache().isDefaultIcon(this.mIcon, this.mUser)) {
            this.mValues.put(LauncherSettings.BaseLauncherColumns.ICON, Utilities.flattenBitmap(this.mIcon));
            this.mIcon = null;
        }
        return this.mValues;
    }

    public int commit() {
        if (this.mCommitParams != null) {
            return this.mContext.getContentResolver().update(this.mCommitParams.mUri, getValues(this.mContext), this.mCommitParams.mWhere, this.mCommitParams.mSelectionArgs);
        }
        return 0;
    }

    public static final class CommitParams {
        String[] mSelectionArgs;
        final Uri mUri = LauncherSettings.Favorites.CONTENT_URI;
        String mWhere;

        public CommitParams(String where, String[] selectionArgs) {
            this.mWhere = where;
            this.mSelectionArgs = selectionArgs;
        }
    }
}
