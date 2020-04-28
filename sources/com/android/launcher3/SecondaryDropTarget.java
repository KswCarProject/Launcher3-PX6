package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.Themes;
import java.net.URISyntaxException;

public class SecondaryDropTarget extends ButtonDropTarget implements OnAlarmListener {
    private static final long CACHE_EXPIRE_TIMEOUT = 5000;
    private static final String TAG = "SecondaryDropTarget";
    private final Alarm mCacheExpireAlarm;
    protected int mCurrentAccessibilityAction;
    private final ArrayMap<UserHandle, Boolean> mUninstallDisabledCache;

    public SecondaryDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SecondaryDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mUninstallDisabledCache = new ArrayMap<>(1);
        this.mCurrentAccessibilityAction = -1;
        this.mCacheExpireAlarm = new Alarm();
        this.mCacheExpireAlarm.setOnAlarmListener(this);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        setupUi(R.id.action_uninstall);
    }

    /* access modifiers changed from: protected */
    public void setupUi(int action) {
        if (action != this.mCurrentAccessibilityAction) {
            this.mCurrentAccessibilityAction = action;
            if (action == R.id.action_uninstall) {
                this.mHoverColor = getResources().getColor(R.color.uninstall_target_hover_tint);
                setDrawable(R.drawable.ic_uninstall_shadow);
                updateText(R.string.uninstall_drop_target_label);
                return;
            }
            this.mHoverColor = Themes.getColorAccent(getContext());
            setDrawable(R.drawable.ic_setup_shadow);
            updateText(R.string.gadget_setup_text);
        }
    }

    public void onAlarm(Alarm alarm) {
        this.mUninstallDisabledCache.clear();
    }

    public int getAccessibilityAction() {
        return this.mCurrentAccessibilityAction;
    }

    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target t = LoggerUtils.newTarget(2);
        t.controlType = this.mCurrentAccessibilityAction == R.id.action_uninstall ? 6 : 4;
        return t;
    }

    /* access modifiers changed from: protected */
    public boolean supportsDrop(ItemInfo info) {
        return supportsAccessibilityDrop(info, getViewUnderDrag(info));
    }

    public boolean supportsAccessibilityDrop(ItemInfo info, View view) {
        if (!(view instanceof AppWidgetHostView)) {
            setupUi(R.id.action_uninstall);
            Boolean uninstallDisabled = this.mUninstallDisabledCache.get(info.user);
            if (uninstallDisabled == null) {
                Bundle restrictions = ((UserManager) getContext().getSystemService("user")).getUserRestrictions(info.user);
                uninstallDisabled = Boolean.valueOf(restrictions.getBoolean("no_control_apps", false) || restrictions.getBoolean("no_uninstall_apps", false));
                this.mUninstallDisabledCache.put(info.user, uninstallDisabled);
            }
            this.mCacheExpireAlarm.setAlarm(CACHE_EXPIRE_TIMEOUT);
            if (uninstallDisabled.booleanValue()) {
                return false;
            }
            if (info instanceof ItemInfoWithIcon) {
                ItemInfoWithIcon iconInfo = (ItemInfoWithIcon) info;
                if ((iconInfo.runtimeStatusFlags & ItemInfoWithIcon.FLAG_SYSTEM_MASK) != 0) {
                    if ((iconInfo.runtimeStatusFlags & 128) != 0) {
                        return true;
                    }
                    return false;
                }
            }
            if (getUninstallTarget(info) != null) {
                return true;
            }
            return false;
        } else if (getReconfigurableWidgetId(view) == 0) {
            return false;
        } else {
            setupUi(R.id.action_reconfigure);
            return true;
        }
    }

    private ComponentName getUninstallTarget(ItemInfo item) {
        LauncherActivityInfo info;
        Intent intent = null;
        UserHandle user = null;
        if (item != null && item.itemType == 0) {
            intent = item.getIntent();
            user = item.user;
        }
        if (intent == null || (info = LauncherAppsCompat.getInstance(this.mLauncher).resolveActivity(intent, user)) == null || (info.getApplicationInfo().flags & 1) != 0) {
            return null;
        }
        return info.getComponentName();
    }

    public void onDrop(DropTarget.DragObject d, DragOptions options) {
        d.dragSource = new DeferredOnComplete(d.dragSource, getContext());
        super.onDrop(d, options);
    }

    public void completeDrop(DropTarget.DragObject d) {
        ComponentName target = performDropAction(getViewUnderDrag(d.dragInfo), d.dragInfo);
        if (d.dragSource instanceof DeferredOnComplete) {
            DeferredOnComplete deferred = (DeferredOnComplete) d.dragSource;
            if (target != null) {
                String unused = deferred.mPackageName = target.getPackageName();
                this.mLauncher.setOnResumeCallback(deferred);
                return;
            }
            deferred.sendFailure();
        }
    }

    private View getViewUnderDrag(ItemInfo info) {
        if (!(info instanceof LauncherAppWidgetInfo) || info.container != -100 || this.mLauncher.getWorkspace().getDragInfo() == null) {
            return null;
        }
        return this.mLauncher.getWorkspace().getDragInfo().cell;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0006, code lost:
        r0 = (android.appwidget.AppWidgetHostView) r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getReconfigurableWidgetId(android.view.View r5) {
        /*
            r4 = this;
            boolean r0 = r5 instanceof android.appwidget.AppWidgetHostView
            r1 = 0
            if (r0 != 0) goto L_0x0006
            return r1
        L_0x0006:
            r0 = r5
            android.appwidget.AppWidgetHostView r0 = (android.appwidget.AppWidgetHostView) r0
            android.appwidget.AppWidgetProviderInfo r2 = r0.getAppWidgetInfo()
            if (r2 == 0) goto L_0x002a
            android.content.ComponentName r3 = r2.configure
            if (r3 != 0) goto L_0x0014
            goto L_0x002a
        L_0x0014:
            android.content.Context r3 = r4.getContext()
            com.android.launcher3.LauncherAppWidgetProviderInfo r3 = com.android.launcher3.LauncherAppWidgetProviderInfo.fromProviderInfo(r3, r2)
            int r3 = r3.getWidgetFeatures()
            r3 = r3 & 1
            if (r3 != 0) goto L_0x0025
            return r1
        L_0x0025:
            int r1 = r0.getAppWidgetId()
            return r1
        L_0x002a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.SecondaryDropTarget.getReconfigurableWidgetId(android.view.View):int");
    }

    /* access modifiers changed from: protected */
    public ComponentName performDropAction(View view, ItemInfo info) {
        if (this.mCurrentAccessibilityAction == R.id.action_reconfigure) {
            int widgetId = getReconfigurableWidgetId(view);
            if (widgetId != 0) {
                this.mLauncher.getAppWidgetHost().startConfigActivity(this.mLauncher, widgetId, -1);
            }
            return null;
        }
        ComponentName cn = getUninstallTarget(info);
        if (cn == null) {
            Toast.makeText(this.mLauncher, R.string.uninstall_system_app_text, 0).show();
            return null;
        }
        try {
            this.mLauncher.startActivity(Intent.parseUri(this.mLauncher.getString(R.string.delete_package_intent), 0).setData(Uri.fromParts("package", cn.getPackageName(), cn.getClassName())).putExtra("android.intent.extra.USER", info.user));
            return cn;
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed to parse intent to start uninstall activity for item=" + info);
            return null;
        }
    }

    public void onAccessibilityDrop(View view, ItemInfo item) {
        performDropAction(view, item);
    }

    private class DeferredOnComplete implements DragSource, Launcher.OnResumeCallback {
        private final Context mContext;
        private DropTarget.DragObject mDragObject;
        private final DragSource mOriginal;
        /* access modifiers changed from: private */
        public String mPackageName;

        public DeferredOnComplete(DragSource original, Context context) {
            this.mOriginal = original;
            this.mContext = context;
        }

        public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
            this.mDragObject = d;
        }

        public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
            this.mOriginal.fillInLogContainerData(v, info, target, targetParent);
        }

        public void onLauncherResume() {
            if (LauncherAppsCompat.getInstance(this.mContext).getApplicationInfo(this.mPackageName, 8192, this.mDragObject.dragInfo.user) == null) {
                this.mDragObject.dragSource = this.mOriginal;
                this.mOriginal.onDropCompleted(SecondaryDropTarget.this, this.mDragObject, true);
                return;
            }
            sendFailure();
        }

        public void sendFailure() {
            this.mDragObject.dragSource = this.mOriginal;
            this.mDragObject.cancelled = true;
            this.mOriginal.onDropCompleted(SecondaryDropTarget.this, this.mDragObject, false);
        }
    }
}
