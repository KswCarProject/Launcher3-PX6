package com.android.launcher3.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.popup.ArrowPopup;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.widget.WidgetsFullSheet;
import java.util.ArrayList;
import java.util.List;

public class OptionsPopupView extends ArrowPopup implements View.OnClickListener, View.OnLongClickListener {
    private final ArrayMap<View, OptionItem> mItemMap;
    private RectF mTargetRect;

    public OptionsPopupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OptionsPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mItemMap = new ArrayMap<>();
    }

    public void onClick(View view) {
        handleViewClick(view, 0);
    }

    public boolean onLongClick(View view) {
        return handleViewClick(view, 1);
    }

    private boolean handleViewClick(View view, int action) {
        OptionItem item = this.mItemMap.get(view);
        if (item == null) {
            return false;
        }
        if (item.mControlTypeForLog > 0) {
            logTap(action, item.mControlTypeForLog);
        }
        if (!item.mClickListener.onLongClick(view)) {
            return false;
        }
        close(true);
        return true;
    }

    private void logTap(int action, int controlType) {
        this.mLauncher.getUserEventDispatcher().logActionOnControl(action, controlType);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || this.mLauncher.getDragLayer().isEventOverView(this, ev)) {
            return false;
        }
        close(true);
        return true;
    }

    public void logActionCommand(int command) {
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 256) != 0;
    }

    /* access modifiers changed from: protected */
    public void getTargetObjectLocation(Rect outPos) {
        this.mTargetRect.roundOut(outPos);
    }

    public static void show(Launcher launcher, RectF targetRect, List<OptionItem> items) {
        OptionsPopupView popup = (OptionsPopupView) launcher.getLayoutInflater().inflate(R.layout.longpress_options_menu, launcher.getDragLayer(), false);
        popup.mTargetRect = targetRect;
        for (OptionItem item : items) {
            DeepShortcutView view = (DeepShortcutView) popup.inflateAndAdd(R.layout.system_shortcut, popup);
            view.getIconView().setBackgroundResource(item.mIconRes);
            view.getBubbleText().setText(item.mLabelRes);
            view.setDividerVisibility(4);
            view.setOnClickListener(popup);
            view.setOnLongClickListener(popup);
            popup.mItemMap.put(view, item);
        }
        popup.reorderAndShow(popup.getChildCount());
    }

    public static void showDefaultOptions(Launcher launcher, float x, float y) {
        float halfSize = launcher.getResources().getDimension(R.dimen.options_menu_thumb_size) / 2.0f;
        if (x < 0.0f || y < 0.0f) {
            x = (float) (launcher.getDragLayer().getWidth() / 2);
            y = (float) (launcher.getDragLayer().getHeight() / 2);
        }
        RectF target = new RectF(x - halfSize, y - halfSize, x + halfSize, y + halfSize);
        ArrayList<OptionItem> options = new ArrayList<>();
        options.add(new OptionItem(R.string.wallpaper_button_text, R.drawable.ic_wallpaper, 3, $$Lambda$Xmxd6hZohvH9mIvfgwHI39dIzk0.INSTANCE));
        options.add(new OptionItem(R.string.widget_button_text, R.drawable.ic_widget, 2, $$Lambda$pbN4MYZT_sQHhmIWZtgqyaKyewM.INSTANCE));
        options.add(new OptionItem(R.string.settings_button_text, R.drawable.ic_setting, 4, $$Lambda$DlDz7FocbPCmSLdhxmuqhjBc.INSTANCE));
        show(launcher, target, options);
    }

    public static boolean onWidgetsClicked(View view) {
        return openWidgets(Launcher.getLauncher(view.getContext()));
    }

    public static boolean openWidgets(Launcher launcher) {
        if (launcher.getPackageManager().isSafeMode()) {
            Toast.makeText(launcher, R.string.safemode_widget_error, 0).show();
            return false;
        }
        WidgetsFullSheet.show(launcher, true);
        return true;
    }

    public static boolean startSettings(View view) {
        Launcher launcher = Launcher.getLauncher(view.getContext());
        launcher.startActivity(new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(launcher.getPackageName()).addFlags(268435456));
        return true;
    }

    public static boolean startWallpaperPicker(View v) {
        Launcher launcher = Launcher.getLauncher(v.getContext());
        if (!Utilities.isWallpaperAllowed(launcher)) {
            Toast.makeText(launcher, R.string.msg_disabled_by_admin, 0).show();
            return false;
        }
        Intent intent = new Intent("android.intent.action.SET_WALLPAPER").putExtra(Utilities.EXTRA_WALLPAPER_OFFSET, launcher.getWorkspace().getWallpaperOffsetForCenterPage());
        intent.addFlags(32768);
        String pickerPackage = launcher.getString(R.string.wallpaper_picker_package);
        if (!TextUtils.isEmpty(pickerPackage)) {
            intent.setPackage(pickerPackage);
        } else {
            intent.putExtra(BaseDraggingActivity.INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION, true);
        }
        return launcher.startActivitySafely(v, intent, (ItemInfo) null);
    }

    public static class OptionItem {
        /* access modifiers changed from: private */
        public final View.OnLongClickListener mClickListener;
        /* access modifiers changed from: private */
        public final int mControlTypeForLog;
        /* access modifiers changed from: private */
        public final int mIconRes;
        /* access modifiers changed from: private */
        public final int mLabelRes;

        public OptionItem(int labelRes, int iconRes, int controlTypeForLog, View.OnLongClickListener clickListener) {
            this.mLabelRes = labelRes;
            this.mIconRes = iconRes;
            this.mControlTypeForLog = controlTypeForLog;
            this.mClickListener = clickListener;
        }
    }
}
