package com.android.launcher3;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.uioverrides.DisplayRotationListener;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.views.BaseDragLayer;

public abstract class BaseDraggingActivity extends BaseActivity implements WallpaperColorInfo.OnChangeListener {
    public static final Object AUTO_CANCEL_ACTION_MODE = new Object();
    public static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";
    private static final String TAG = "BaseDraggingActivity";
    private ActionMode mCurrentActionMode;
    protected boolean mIsSafeModeEnabled;
    private OnStartCallback mOnStartCallback;
    private DisplayRotationListener mRotationListener;
    private int mThemeRes = R.style.AppTheme;

    public interface OnStartCallback<T extends BaseDraggingActivity> {
        void onActivityStart(T t);
    }

    public abstract ActivityOptions getActivityLaunchOptions(View view);

    public abstract BadgeInfo getBadgeInfoForItem(ItemInfo itemInfo);

    public abstract BaseDragLayer getDragLayer();

    public abstract <T extends View> T getOverviewPanel();

    public abstract View getRootView();

    public abstract void invalidateParent(ItemInfo itemInfo);

    /* access modifiers changed from: protected */
    public abstract void reapplyUi();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsSafeModeEnabled = getPackageManager().isSafeMode();
        this.mRotationListener = new DisplayRotationListener(this, new Runnable() {
            public final void run() {
                BaseDraggingActivity.this.onDeviceRotationChanged();
            }
        });
        WallpaperColorInfo wallpaperColorInfo = WallpaperColorInfo.getInstance(this);
        wallpaperColorInfo.addOnChangeListener(this);
        int themeRes = getThemeRes(wallpaperColorInfo);
        if (themeRes != this.mThemeRes) {
            this.mThemeRes = themeRes;
            setTheme(themeRes);
        }
    }

    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        if (this.mThemeRes != getThemeRes(wallpaperColorInfo)) {
            recreate();
        }
    }

    /* access modifiers changed from: protected */
    public int getThemeRes(WallpaperColorInfo wallpaperColorInfo) {
        return wallpaperColorInfo.isDark() ? wallpaperColorInfo.supportsDarkText() ? R.style.AppTheme_Dark_DarkText : R.style.AppTheme_Dark : wallpaperColorInfo.supportsDarkText() ? R.style.AppTheme_DarkText : R.style.AppTheme;
    }

    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        this.mCurrentActionMode = mode;
    }

    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        this.mCurrentActionMode = null;
    }

    public boolean finishAutoCancelActionMode() {
        if (this.mCurrentActionMode == null || AUTO_CANCEL_ACTION_MODE != this.mCurrentActionMode.getTag()) {
            return false;
        }
        this.mCurrentActionMode.finish();
        return true;
    }

    public static BaseDraggingActivity fromContext(Context context) {
        if (context instanceof BaseDraggingActivity) {
            return (BaseDraggingActivity) context;
        }
        return (BaseDraggingActivity) ((ContextWrapper) context).getBaseContext();
    }

    public Rect getViewBounds(View v) {
        int[] pos = new int[2];
        v.getLocationOnScreen(pos);
        return new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight());
    }

    public final Bundle getActivityLaunchOptionsAsBundle(View v) {
        ActivityOptions activityOptions = getActivityLaunchOptions(v);
        if (activityOptions == null) {
            return null;
        }
        return activityOptions.toBundle();
    }

    public boolean startActivitySafely(View v, Intent intent, ItemInfo item) {
        if (!this.mIsSafeModeEnabled || Utilities.isSystemApp(this, intent)) {
            UserHandle user = null;
            Bundle optsBundle = v != null && !intent.hasExtra(INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION) ? getActivityLaunchOptionsAsBundle(v) : null;
            if (item != null) {
                user = item.user;
            }
            intent.addFlags(268435456);
            if (v != null) {
                intent.setSourceBounds(getViewBounds(v));
            }
            try {
                if (Utilities.ATLEAST_MARSHMALLOW && (item instanceof ShortcutInfo) && (item.itemType == 1 || item.itemType == 6) && !((ShortcutInfo) item).isPromise()) {
                    startShortcutIntentSafely(intent, optsBundle, item);
                } else {
                    if (user != null) {
                        if (!user.equals(Process.myUserHandle())) {
                            LauncherAppsCompat.getInstance(this).startActivityForProfile(intent.getComponent(), user, intent.getSourceBounds(), optsBundle);
                        }
                    }
                    startActivity(intent, optsBundle);
                }
                getUserEventDispatcher().logAppLaunch(v, intent);
                return true;
            } catch (ActivityNotFoundException | SecurityException e) {
                Toast.makeText(this, R.string.activity_not_found, 0).show();
                Log.e(TAG, "Unable to launch. tag=" + item + " intent=" + intent, e);
                return false;
            }
        } else {
            Toast.makeText(this, R.string.safemode_shortcut_error, 0).show();
            return false;
        }
    }

    private void startShortcutIntentSafely(Intent intent, Bundle optsBundle, ItemInfo info) {
        StrictMode.VmPolicy oldPolicy;
        try {
            oldPolicy = StrictMode.getVmPolicy();
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
            if (info.itemType == 6) {
                String id = ((ShortcutInfo) info).getDeepShortcutId();
                DeepShortcutManager.getInstance(this).startShortcut(intent.getPackage(), id, intent.getSourceBounds(), optsBundle, info.user);
            } else {
                startActivity(intent, optsBundle);
            }
            StrictMode.setVmPolicy(oldPolicy);
        } catch (SecurityException e) {
            if (!onErrorStartingShortcut(intent, info)) {
                throw e;
            }
        } catch (Throwable th) {
            StrictMode.setVmPolicy(oldPolicy);
            throw th;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onErrorStartingShortcut(Intent intent, ItemInfo info) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        if (this.mOnStartCallback != null) {
            this.mOnStartCallback.onActivityStart(this);
            this.mOnStartCallback = null;
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        WallpaperColorInfo.getInstance(this).removeOnChangeListener(this);
        this.mRotationListener.disable();
    }

    public <T extends BaseDraggingActivity> void setOnStartCallback(OnStartCallback<T> callback) {
        this.mOnStartCallback = callback;
    }

    /* access modifiers changed from: protected */
    public void onDeviceProfileInitiated() {
        if (this.mDeviceProfile.isVerticalBarLayout()) {
            this.mRotationListener.enable();
            this.mDeviceProfile.updateIsSeascape(getWindowManager());
            return;
        }
        this.mRotationListener.disable();
    }

    /* access modifiers changed from: private */
    public void onDeviceRotationChanged() {
        if (this.mDeviceProfile.updateIsSeascape(getWindowManager())) {
            reapplyUi();
        }
    }
}
