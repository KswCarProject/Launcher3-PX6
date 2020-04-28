package com.android.launcher3.touch;

import android.app.AlertDialog;
import android.appwidget.AppWidgetProviderInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppWidgetInfo;
import com.android.launcher3.LauncherAppWidgetProviderInfo;
import com.android.launcher3.PromiseAppInfo;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import com.android.launcher3.widget.WidgetAddFlowHandler;

public class ItemClickHandler {
    public static final View.OnClickListener INSTANCE = $$Lambda$ItemClickHandler$oPqR0koj5OQZ6VtyqmfcFGp5X0Q.INSTANCE;

    /* access modifiers changed from: private */
    public static void onClick(View v) {
        if (v.getWindowToken() != null) {
            Launcher launcher = Launcher.getLauncher(v.getContext());
            if (launcher.getWorkspace().isFinishedSwitchingState()) {
                Object tag = v.getTag();
                if (tag instanceof ShortcutInfo) {
                    onClickAppShortcut(v, (ShortcutInfo) tag, launcher);
                } else if (tag instanceof FolderInfo) {
                    if (v instanceof FolderIcon) {
                        onClickFolderIcon(v);
                    }
                } else if (tag instanceof AppInfo) {
                    startAppShortcutOrInfoActivity(v, (AppInfo) tag, launcher);
                } else if ((tag instanceof LauncherAppWidgetInfo) && (v instanceof PendingAppWidgetHostView)) {
                    onClickPendingWidget((PendingAppWidgetHostView) v, launcher);
                }
            }
        }
    }

    private static void onClickFolderIcon(View v) {
        Folder folder = ((FolderIcon) v).getFolder();
        if (!folder.isOpen() && !folder.isDestroyed()) {
            folder.animateOpen();
        }
    }

    private static void onClickPendingWidget(PendingAppWidgetHostView v, Launcher launcher) {
        boolean z = false;
        if (launcher.getPackageManager().isSafeMode()) {
            Toast.makeText(launcher, R.string.safemode_widget_error, 0).show();
            return;
        }
        LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) v.getTag();
        if (v.isReadyForClickSetup()) {
            LauncherAppWidgetProviderInfo appWidgetInfo = AppWidgetManagerCompat.getInstance(launcher).findProvider(info.providerName, info.user);
            if (appWidgetInfo != null) {
                WidgetAddFlowHandler addFlowHandler = new WidgetAddFlowHandler((AppWidgetProviderInfo) appWidgetInfo);
                if (!info.hasRestoreFlag(1)) {
                    addFlowHandler.startConfigActivity(launcher, info, 13);
                } else if (info.hasRestoreFlag(16)) {
                    addFlowHandler.startBindFlow(launcher, info.appWidgetId, info, 12);
                }
            }
        } else {
            String packageName = info.providerName.getPackageName();
            if (info.installProgress >= 0) {
                z = true;
            }
            onClickPendingAppItem(v, launcher, packageName, z);
        }
    }

    private static void onClickPendingAppItem(View v, Launcher launcher, String packageName, boolean downloadStarted) {
        if (downloadStarted) {
            startMarketIntentForPackage(v, launcher, packageName);
        } else {
            new AlertDialog.Builder(launcher).setTitle(R.string.abandoned_promises_title).setMessage(R.string.abandoned_promise_explanation).setPositiveButton(R.string.abandoned_search, new DialogInterface.OnClickListener(v, launcher, packageName) {
                private final /* synthetic */ View f$0;
                private final /* synthetic */ Launcher f$1;
                private final /* synthetic */ String f$2;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    ItemClickHandler.startMarketIntentForPackage(this.f$0, this.f$1, this.f$2);
                }
            }).setNeutralButton(R.string.abandoned_clean_this, new DialogInterface.OnClickListener(packageName) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    Launcher.this.getWorkspace().removeAbandonedPromise(this.f$1, Process.myUserHandle());
                }
            }).create().show();
        }
    }

    /* access modifiers changed from: private */
    public static void startMarketIntentForPackage(View v, Launcher launcher, String packageName) {
        launcher.startActivitySafely(v, new PackageManagerHelper(launcher).getMarketIntent(packageName), (ItemInfo) v.getTag());
    }

    private static void onClickAppShortcut(View v, ShortcutInfo shortcut, Launcher launcher) {
        if (!shortcut.isDisabled() || (shortcut.runtimeStatusFlags & 63 & -5 & -9) == 0) {
            if ((v instanceof BubbleTextView) != 0 && shortcut.hasPromiseIconUi()) {
                String packageName = shortcut.intent.getComponent() != null ? shortcut.intent.getComponent().getPackageName() : shortcut.intent.getPackage();
                if (!TextUtils.isEmpty(packageName)) {
                    onClickPendingAppItem(v, launcher, packageName, shortcut.hasStatusFlag(4));
                    return;
                }
            }
            startAppShortcutOrInfoActivity(v, shortcut, launcher);
        } else if (!TextUtils.isEmpty(shortcut.disabledMessage)) {
            Toast.makeText(launcher, shortcut.disabledMessage, 0).show();
        } else {
            int error = R.string.activity_not_available;
            if ((shortcut.runtimeStatusFlags & 1) != 0) {
                error = R.string.safemode_shortcut_error;
            } else if (!((shortcut.runtimeStatusFlags & 16) == 0 && (shortcut.runtimeStatusFlags & 32) == 0)) {
                error = R.string.shortcut_not_available;
            }
            Toast.makeText(launcher, error, 0).show();
        }
    }

    private static void startAppShortcutOrInfoActivity(View v, ItemInfo item, Launcher launcher) {
        Intent intent;
        if (item instanceof PromiseAppInfo) {
            intent = ((PromiseAppInfo) item).getMarketIntent(launcher);
        } else {
            intent = item.getIntent();
        }
        if (intent != null) {
            if ((item instanceof ShortcutInfo) && ((ShortcutInfo) item).hasStatusFlag(16) && intent.getAction() == "android.intent.action.VIEW") {
                intent = new Intent(intent);
                intent.setPackage((String) null);
            }
            launcher.startActivitySafely(v, intent, item);
            return;
        }
        throw new IllegalArgumentException("Input must have a valid intent");
    }
}
