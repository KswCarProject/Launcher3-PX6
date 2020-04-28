package com.android.launcher3.model;

import android.os.UserHandle;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.widget.WidgetListRowEntry;
import java.util.ArrayList;
import java.util.concurrent.Executor;

public abstract class BaseModelUpdateTask implements LauncherModel.ModelUpdateTask {
    private static final boolean DEBUG_TASKS = false;
    private static final String TAG = "BaseModelUpdateTask";
    private AllAppsList mAllAppsList;
    private LauncherAppState mApp;
    private BgDataModel mDataModel;
    private LauncherModel mModel;
    private Executor mUiExecutor;

    public abstract void execute(LauncherAppState launcherAppState, BgDataModel bgDataModel, AllAppsList allAppsList);

    public void init(LauncherAppState app, LauncherModel model, BgDataModel dataModel, AllAppsList allAppsList, Executor uiExecutor) {
        this.mApp = app;
        this.mModel = model;
        this.mDataModel = dataModel;
        this.mAllAppsList = allAppsList;
        this.mUiExecutor = uiExecutor;
    }

    public final void run() {
        if (this.mModel.isModelLoaded()) {
            execute(this.mApp, this.mDataModel, this.mAllAppsList);
        }
    }

    public final void scheduleCallbackTask(LauncherModel.CallbackTask task) {
        this.mUiExecutor.execute(new Runnable(this.mModel.getCallback(), task) {
            private final /* synthetic */ LauncherModel.Callbacks f$1;
            private final /* synthetic */ LauncherModel.CallbackTask f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BaseModelUpdateTask.lambda$scheduleCallbackTask$0(BaseModelUpdateTask.this, this.f$1, this.f$2);
            }
        });
    }

    public static /* synthetic */ void lambda$scheduleCallbackTask$0(BaseModelUpdateTask baseModelUpdateTask, LauncherModel.Callbacks callbacks, LauncherModel.CallbackTask task) {
        LauncherModel.Callbacks cb = baseModelUpdateTask.mModel.getCallback();
        if (callbacks == cb && cb != null) {
            task.execute(callbacks);
        }
    }

    public ModelWriter getModelWriter() {
        return this.mModel.getWriter(false, false);
    }

    public void bindUpdatedShortcuts(final ArrayList<ShortcutInfo> updatedShortcuts, final UserHandle user) {
        if (!updatedShortcuts.isEmpty()) {
            scheduleCallbackTask(new LauncherModel.CallbackTask() {
                public void execute(LauncherModel.Callbacks callbacks) {
                    callbacks.bindShortcutsChanged(updatedShortcuts, user);
                }
            });
        }
    }

    public void bindDeepShortcuts(BgDataModel dataModel) {
        final MultiHashMap<ComponentKey, String> shortcutMapCopy = dataModel.deepShortcutMap.clone();
        scheduleCallbackTask(new LauncherModel.CallbackTask() {
            public void execute(LauncherModel.Callbacks callbacks) {
                callbacks.bindDeepShortcutMap(shortcutMapCopy);
            }
        });
    }

    public void bindUpdatedWidgets(BgDataModel dataModel) {
        final ArrayList<WidgetListRowEntry> widgets = dataModel.widgetsModel.getWidgetsList(this.mApp.getContext());
        scheduleCallbackTask(new LauncherModel.CallbackTask() {
            public void execute(LauncherModel.Callbacks callbacks) {
                callbacks.bindAllWidgets(widgets);
            }
        });
    }

    public void deleteAndBindComponentsRemoved(final ItemInfoMatcher matcher) {
        getModelWriter().deleteItemsFromDatabase(matcher);
        scheduleCallbackTask(new LauncherModel.CallbackTask() {
            public void execute(LauncherModel.Callbacks callbacks) {
                callbacks.bindWorkspaceComponentsRemoved(matcher);
            }
        });
    }
}
