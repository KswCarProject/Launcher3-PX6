package com.android.launcher3.popup;

import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.util.InstantAppResolver;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.widget.WidgetsBottomSheet;

public abstract class SystemShortcut<T extends BaseDraggingActivity> extends ItemInfo {
    public final int iconResId;
    public final int labelResId;

    public abstract View.OnClickListener getOnClickListener(T t, ItemInfo itemInfo);

    public SystemShortcut(int iconResId2, int labelResId2) {
        this.iconResId = iconResId2;
        this.labelResId = labelResId2;
    }

    public static class Widgets extends SystemShortcut<Launcher> {
        public Widgets() {
            super(R.drawable.ic_widget, R.string.widget_button_text);
        }

        public View.OnClickListener getOnClickListener(Launcher launcher, ItemInfo itemInfo) {
            if (launcher.getPopupDataProvider().getWidgetsForPackageUser(new PackageUserKey(itemInfo.getTargetComponent().getPackageName(), itemInfo.user)) == null) {
                return null;
            }
            return new View.OnClickListener(itemInfo) {
                private final /* synthetic */ ItemInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    SystemShortcut.Widgets.lambda$getOnClickListener$0(Launcher.this, this.f$1, view);
                }
            };
        }

        static /* synthetic */ void lambda$getOnClickListener$0(Launcher launcher, ItemInfo itemInfo, View view) {
            AbstractFloatingView.closeAllOpenViews(launcher);
            ((WidgetsBottomSheet) launcher.getLayoutInflater().inflate(R.layout.widgets_bottom_sheet, launcher.getDragLayer(), false)).populateAndShow(itemInfo);
            launcher.getUserEventDispatcher().logActionOnControl(0, 2, view);
        }
    }

    public static class AppInfo extends SystemShortcut {
        public AppInfo() {
            super(R.drawable.ic_info_no_shadow, R.string.app_info_drop_target_label);
        }

        public View.OnClickListener getOnClickListener(BaseDraggingActivity activity, ItemInfo itemInfo) {
            return new View.OnClickListener(itemInfo) {
                private final /* synthetic */ ItemInfo f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    SystemShortcut.AppInfo.lambda$getOnClickListener$0(BaseDraggingActivity.this, this.f$1, view);
                }
            };
        }

        static /* synthetic */ void lambda$getOnClickListener$0(BaseDraggingActivity activity, ItemInfo itemInfo, View view) {
            dismissTaskMenuView(activity);
            new PackageManagerHelper(activity).startDetailsActivityForInfo(itemInfo, activity.getViewBounds(view), activity.getActivityLaunchOptionsAsBundle(view));
            activity.getUserEventDispatcher().logActionOnControl(0, 7, view);
        }
    }

    public static class Install extends SystemShortcut {
        public Install() {
            super(R.drawable.ic_install_no_shadow, R.string.install_drop_target_label);
        }

        public View.OnClickListener getOnClickListener(BaseDraggingActivity activity, ItemInfo itemInfo) {
            boolean enabled = true;
            boolean supportsWebUI = (itemInfo instanceof ShortcutInfo) && ((ShortcutInfo) itemInfo).hasStatusFlag(16);
            boolean isInstantApp = false;
            if (itemInfo instanceof com.android.launcher3.AppInfo) {
                isInstantApp = InstantAppResolver.newInstance(activity).isInstantApp((com.android.launcher3.AppInfo) itemInfo);
            }
            if (!supportsWebUI && !isInstantApp) {
                enabled = false;
            }
            if (!enabled) {
                return null;
            }
            return createOnClickListener(activity, itemInfo);
        }

        public View.OnClickListener createOnClickListener(BaseDraggingActivity activity, ItemInfo itemInfo) {
            return new View.OnClickListener(activity) {
                private final /* synthetic */ BaseDraggingActivity f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    SystemShortcut.Install.lambda$createOnClickListener$0(ItemInfo.this, this.f$1, view);
                }
            };
        }

        static /* synthetic */ void lambda$createOnClickListener$0(ItemInfo itemInfo, BaseDraggingActivity activity, View view) {
            activity.startActivitySafely(view, new PackageManagerHelper(view.getContext()).getMarketIntent(itemInfo.getTargetComponent().getPackageName()), itemInfo);
            AbstractFloatingView.closeAllOpenViews(activity);
        }
    }

    protected static void dismissTaskMenuView(BaseDraggingActivity activity) {
        AbstractFloatingView.closeOpenViews(activity, true, 399);
    }
}
