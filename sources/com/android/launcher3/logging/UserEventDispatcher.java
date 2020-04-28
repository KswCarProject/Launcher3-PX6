package com.android.launcher3.logging;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.InstantAppResolver;
import java.util.Locale;
import java.util.UUID;

public class UserEventDispatcher {
    private static final boolean IS_VERBOSE = false;
    private static final int MAXIMUM_VIEW_HIERARCHY_LEVEL = 5;
    private static final String TAG = "UserEvent";
    private static final String UUID_STORAGE = "uuid";
    private long mActionDurationMillis;
    private boolean mAppOrTaskLaunch;
    private UserEventDelegate mDelegate;
    private long mElapsedContainerMillis;
    private long mElapsedSessionMillis;
    protected InstantAppResolver mInstantAppResolver;
    private boolean mIsInLandscapeMode;
    private boolean mIsInMultiWindowMode;
    private boolean mSessionStarted;
    private String mUuidStr;

    public interface LogContainerProvider {
        void fillInLogContainerData(View view, ItemInfo itemInfo, LauncherLogProto.Target target, LauncherLogProto.Target target2);
    }

    public interface UserEventDelegate {
        void modifyUserEvent(LauncherLogProto.LauncherEvent launcherEvent);
    }

    public static UserEventDispatcher newInstance(Context context, DeviceProfile dp, UserEventDelegate delegate) {
        SharedPreferences sharedPrefs = Utilities.getDevicePrefs(context);
        String uuidStr = sharedPrefs.getString(UUID_STORAGE, (String) null);
        if (uuidStr == null) {
            uuidStr = UUID.randomUUID().toString();
            sharedPrefs.edit().putString(UUID_STORAGE, uuidStr).apply();
        }
        UserEventDispatcher ued = (UserEventDispatcher) Utilities.getOverrideObject(UserEventDispatcher.class, context.getApplicationContext(), R.string.user_event_dispatcher_class);
        ued.mDelegate = delegate;
        ued.mIsInLandscapeMode = dp.isVerticalBarLayout();
        ued.mIsInMultiWindowMode = dp.isMultiWindowMode;
        ued.mUuidStr = uuidStr;
        ued.mInstantAppResolver = InstantAppResolver.newInstance(context);
        return ued;
    }

    public static UserEventDispatcher newInstance(Context context, DeviceProfile dp) {
        return newInstance(context, dp, (UserEventDelegate) null);
    }

    public static LogContainerProvider getLaunchProviderRecursive(@Nullable View v) {
        if (v == null) {
            return null;
        }
        ViewParent parent = v.getParent();
        int count = 5;
        while (true) {
            if (parent == null) {
                break;
            }
            int count2 = count - 1;
            if (count <= 0) {
                int i = count2;
                break;
            } else if ((parent instanceof LogContainerProvider) != 0) {
                return (LogContainerProvider) parent;
            } else {
                parent = parent.getParent();
                count = count2;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean fillInLogContainerData(LauncherLogProto.LauncherEvent event, @Nullable View v) {
        LogContainerProvider provider = getLaunchProviderRecursive(v);
        if (v == null || !(v.getTag() instanceof ItemInfo) || provider == null) {
            return false;
        }
        provider.fillInLogContainerData(v, (ItemInfo) v.getTag(), event.srcTarget[0], event.srcTarget[1]);
        return true;
    }

    public void logAppLaunch(View v, Intent intent) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), LoggerUtils.newItemTarget(v, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(event, v)) {
            if (this.mDelegate != null) {
                this.mDelegate.modifyUserEvent(event);
            }
            fillIntentInfo(event.srcTarget[0], intent);
        }
        dispatchUserEvent(event, intent);
        this.mAppOrTaskLaunch = true;
    }

    public void logActionTip(int actionType, int viewType) {
    }

    public void logTaskLaunchOrDismiss(int action, int direction, int taskIndex, ComponentKey componentKey) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newTarget(1));
        if (action == 3 || action == 4) {
            event.action.dir = direction;
        }
        event.srcTarget[0].itemType = 9;
        event.srcTarget[0].pageIndex = taskIndex;
        fillComponentInfo(event.srcTarget[0], componentKey.componentName);
        dispatchUserEvent(event, (Intent) null);
        this.mAppOrTaskLaunch = true;
    }

    /* access modifiers changed from: protected */
    public void fillIntentInfo(LauncherLogProto.Target target, Intent intent) {
        target.intentHash = intent.hashCode();
        fillComponentInfo(target, intent.getComponent());
    }

    private void fillComponentInfo(LauncherLogProto.Target target, ComponentName cn) {
        if (cn != null) {
            target.packageNameHash = (this.mUuidStr + cn.getPackageName()).hashCode();
            target.componentHash = (this.mUuidStr + cn.flattenToString()).hashCode();
        }
    }

    public void logNotificationLaunch(View v, PendingIntent intent) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), LoggerUtils.newItemTarget(v, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(event, v)) {
            LauncherLogProto.Target target = event.srcTarget[0];
            target.packageNameHash = (this.mUuidStr + intent.getCreatorPackage()).hashCode();
        }
        dispatchUserEvent(event, (Intent) null);
    }

    public void logActionCommand(int command, LauncherLogProto.Target srcTarget) {
        logActionCommand(command, srcTarget, (LauncherLogProto.Target) null);
    }

    public void logActionCommand(int command, int srcContainerType, int dstContainerType) {
        logActionCommand(command, LoggerUtils.newContainerTarget(srcContainerType), dstContainerType >= 0 ? LoggerUtils.newContainerTarget(dstContainerType) : null);
    }

    public void logActionCommand(int command, LauncherLogProto.Target srcTarget, LauncherLogProto.Target dstTarget) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(command), srcTarget);
        if (command != 5 || (!this.mAppOrTaskLaunch && this.mSessionStarted)) {
            if (dstTarget != null) {
                event.destTarget = new LauncherLogProto.Target[1];
                event.destTarget[0] = dstTarget;
                event.action.isStateChange = true;
            }
            dispatchUserEvent(event, (Intent) null);
            return;
        }
        this.mSessionStarted = false;
    }

    public void logActionCommand(int command, View itemView, int srcContainerType) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newCommandAction(command), LoggerUtils.newItemTarget(itemView, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        if (fillInLogContainerData(event, itemView)) {
            event.srcTarget[0].type = 3;
            event.srcTarget[0].containerType = srcContainerType;
        }
        dispatchUserEvent(event, (Intent) null);
    }

    public void logActionOnControl(int action, int controlType) {
        logActionOnControl(action, controlType, (View) null, -1);
    }

    public void logActionOnControl(int action, int controlType, int parentContainerType) {
        logActionOnControl(action, controlType, (View) null, parentContainerType);
    }

    public void logActionOnControl(int action, int controlType, @Nullable View controlInContainer) {
        logActionOnControl(action, controlType, controlInContainer, -1);
    }

    public void logActionOnControl(int action, int controlType, int parentContainer, int grandParentContainer) {
        dispatchUserEvent(LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newControlTarget(controlType), LoggerUtils.newContainerTarget(parentContainer), LoggerUtils.newContainerTarget(grandParentContainer)), (Intent) null);
    }

    public void logActionOnControl(int action, int controlType, @Nullable View controlInContainer, int parentContainerType) {
        LauncherLogProto.LauncherEvent event;
        if (controlInContainer != null || parentContainerType >= 0) {
            event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newTarget(2), LoggerUtils.newTarget(3));
        } else {
            event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newTarget(2));
        }
        event.srcTarget[0].controlType = controlType;
        if (controlInContainer != null) {
            fillInLogContainerData(event, controlInContainer);
        }
        if (parentContainerType >= 0) {
            event.srcTarget[1].containerType = parentContainerType;
        }
        if (action == 2) {
            event.actionDurationMillis = SystemClock.uptimeMillis() - this.mActionDurationMillis;
        }
        dispatchUserEvent(event, (Intent) null);
    }

    public void logActionTapOutside(LauncherLogProto.Target target) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(0), target);
        event.action.isOutside = true;
        dispatchUserEvent(event, (Intent) null);
    }

    public void logActionBounceTip(int containerType) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newAction(3), LoggerUtils.newContainerTarget(containerType));
        event.srcTarget[0].tipType = 1;
        dispatchUserEvent(event, (Intent) null);
    }

    public void logActionOnContainer(int action, int dir, int containerType) {
        logActionOnContainer(action, dir, containerType, 0);
    }

    public void logActionOnContainer(int action, int dir, int containerType, int pageIndex) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newContainerTarget(containerType));
        event.action.dir = dir;
        event.srcTarget[0].pageIndex = pageIndex;
        dispatchUserEvent(event, (Intent) null);
    }

    public void logStateChangeAction(int action, int dir, int srcChildTargetType, int srcParentContainerType, int dstContainerType, int pageIndex) {
        LauncherLogProto.LauncherEvent event;
        if (srcChildTargetType == 9) {
            event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newItemTarget(srcChildTargetType), LoggerUtils.newContainerTarget(srcParentContainerType));
        } else {
            event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), LoggerUtils.newContainerTarget(srcChildTargetType), LoggerUtils.newContainerTarget(srcParentContainerType));
        }
        event.destTarget = new LauncherLogProto.Target[1];
        event.destTarget[0] = LoggerUtils.newContainerTarget(dstContainerType);
        event.action.dir = dir;
        event.action.isStateChange = true;
        event.srcTarget[0].pageIndex = pageIndex;
        dispatchUserEvent(event, (Intent) null);
        resetElapsedContainerMillis("state changed");
    }

    public void logActionOnItem(int action, int dir, int itemType) {
        LauncherLogProto.Target itemTarget = LoggerUtils.newTarget(1);
        itemTarget.itemType = itemType;
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(action), itemTarget);
        event.action.dir = dir;
        dispatchUserEvent(event, (Intent) null);
    }

    public void logDeepShortcutsOpen(View icon) {
        LogContainerProvider provider = getLaunchProviderRecursive(icon);
        if (icon != null && (icon.getTag() instanceof ItemInfo)) {
            ItemInfo info = (ItemInfo) icon.getTag();
            LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(1), LoggerUtils.newItemTarget(info, this.mInstantAppResolver), LoggerUtils.newTarget(3));
            provider.fillInLogContainerData(icon, info, event.srcTarget[0], event.srcTarget[1]);
            dispatchUserEvent(event, (Intent) null);
            resetElapsedContainerMillis("deep shortcut open");
        }
    }

    public void logOverviewReorder() {
        dispatchUserEvent(LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(2), LoggerUtils.newContainerTarget(1), LoggerUtils.newContainerTarget(6)), (Intent) null);
    }

    public void logDragNDrop(DropTarget.DragObject dragObj, View dropTargetAsView) {
        LauncherLogProto.LauncherEvent event = LoggerUtils.newLauncherEvent(LoggerUtils.newTouchAction(2), LoggerUtils.newItemTarget(dragObj.originalDragInfo, this.mInstantAppResolver), LoggerUtils.newTarget(3));
        event.destTarget = new LauncherLogProto.Target[]{LoggerUtils.newItemTarget(dragObj.originalDragInfo, this.mInstantAppResolver), LoggerUtils.newDropTarget(dropTargetAsView)};
        dragObj.dragSource.fillInLogContainerData((View) null, dragObj.originalDragInfo, event.srcTarget[0], event.srcTarget[1]);
        if (dropTargetAsView instanceof LogContainerProvider) {
            ((LogContainerProvider) dropTargetAsView).fillInLogContainerData((View) null, dragObj.dragInfo, event.destTarget[0], event.destTarget[1]);
        }
        event.actionDurationMillis = SystemClock.uptimeMillis() - this.mActionDurationMillis;
        dispatchUserEvent(event, (Intent) null);
    }

    public final void resetElapsedContainerMillis(String reason) {
        this.mElapsedContainerMillis = SystemClock.uptimeMillis();
        if (IS_VERBOSE) {
            Log.d("UserEvent", "resetElapsedContainerMillis reason=" + reason);
        }
    }

    public final void startSession() {
        this.mSessionStarted = true;
        this.mElapsedSessionMillis = SystemClock.uptimeMillis();
        this.mElapsedContainerMillis = SystemClock.uptimeMillis();
    }

    public final void resetActionDurationMillis() {
        this.mActionDurationMillis = SystemClock.uptimeMillis();
    }

    public void dispatchUserEvent(LauncherLogProto.LauncherEvent ev, Intent intent) {
        this.mAppOrTaskLaunch = false;
        ev.isInLandscapeMode = this.mIsInLandscapeMode;
        ev.isInMultiWindowMode = this.mIsInMultiWindowMode;
        ev.elapsedContainerMillis = SystemClock.uptimeMillis() - this.mElapsedContainerMillis;
        ev.elapsedSessionMillis = SystemClock.uptimeMillis() - this.mElapsedSessionMillis;
        if (IS_VERBOSE) {
            String log = "\n-----------------------------------------------------\naction:" + LoggerUtils.getActionStr(ev.action);
            if (ev.srcTarget != null && ev.srcTarget.length > 0) {
                log = log + "\n Source " + getTargetsStr(ev.srcTarget);
            }
            if (ev.destTarget != null && ev.destTarget.length > 0) {
                log = log + "\n Destination " + getTargetsStr(ev.destTarget);
            }
            Log.d("UserEvent", (((log + String.format(Locale.US, "\n Elapsed container %d ms, session %d ms, action %d ms", new Object[]{Long.valueOf(ev.elapsedContainerMillis), Long.valueOf(ev.elapsedSessionMillis), Long.valueOf(ev.actionDurationMillis)})) + "\n isInLandscapeMode " + ev.isInLandscapeMode) + "\n isInMultiWindowMode " + ev.isInMultiWindowMode) + "\n\n");
        }
    }

    private static String getTargetsStr(LauncherLogProto.Target[] targets) {
        String result = "child:" + LoggerUtils.getTargetStr(targets[0]);
        for (int i = 1; i < targets.length; i++) {
            result = result + "\tparent:" + LoggerUtils.getTargetStr(targets[i]);
        }
        return result;
    }
}
