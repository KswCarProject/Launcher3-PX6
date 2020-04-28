package com.android.launcher3.states;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.MainThreadExecutor;
import java.lang.ref.WeakReference;

public abstract class InternalStateHandler extends Binder {
    public static final String EXTRA_STATE_HANDLER = "launcher.state_handler";
    private static final Scheduler sScheduler = new Scheduler();

    /* access modifiers changed from: protected */
    public abstract boolean init(Launcher launcher, boolean z);

    public final Intent addToIntent(Intent intent) {
        Bundle extras = new Bundle();
        extras.putBinder(EXTRA_STATE_HANDLER, this);
        intent.putExtras(extras);
        return intent;
    }

    public final void initWhenReady() {
        sScheduler.schedule(this);
    }

    public boolean clearReference() {
        return sScheduler.clearReference(this);
    }

    public static boolean hasPending() {
        return sScheduler.hasPending();
    }

    public static boolean handleCreate(Launcher launcher, Intent intent) {
        return handleIntent(launcher, intent, false, false);
    }

    public static boolean handleNewIntent(Launcher launcher, Intent intent, boolean alreadyOnHome) {
        return handleIntent(launcher, intent, alreadyOnHome, true);
    }

    private static boolean handleIntent(Launcher launcher, Intent intent, boolean alreadyOnHome, boolean explicitIntent) {
        boolean result = false;
        if (!(intent == null || intent.getExtras() == null)) {
            IBinder stateBinder = intent.getExtras().getBinder(EXTRA_STATE_HANDLER);
            if (stateBinder instanceof InternalStateHandler) {
                if (!((InternalStateHandler) stateBinder).init(launcher, alreadyOnHome)) {
                    intent.getExtras().remove(EXTRA_STATE_HANDLER);
                }
                result = true;
            }
        }
        if (result || explicitIntent) {
            return result;
        }
        return sScheduler.initIfPending(launcher, alreadyOnHome);
    }

    private static class Scheduler implements Runnable {
        private MainThreadExecutor mMainThreadExecutor;
        private WeakReference<InternalStateHandler> mPendingHandler;

        private Scheduler() {
            this.mPendingHandler = new WeakReference<>((Object) null);
        }

        public void schedule(InternalStateHandler handler) {
            synchronized (this) {
                this.mPendingHandler = new WeakReference<>(handler);
                if (this.mMainThreadExecutor == null) {
                    this.mMainThreadExecutor = new MainThreadExecutor();
                }
            }
            this.mMainThreadExecutor.execute(this);
        }

        public void run() {
            LauncherAppState app = LauncherAppState.getInstanceNoCreate();
            if (app != null) {
                LauncherModel.Callbacks cb = app.getModel().getCallback();
                if (cb instanceof Launcher) {
                    Launcher launcher = (Launcher) cb;
                    initIfPending(launcher, launcher.isStarted());
                }
            }
        }

        public boolean initIfPending(Launcher launcher, boolean alreadyOnHome) {
            InternalStateHandler pendingHandler = (InternalStateHandler) this.mPendingHandler.get();
            if (pendingHandler == null) {
                return false;
            }
            if (pendingHandler.init(launcher, alreadyOnHome)) {
                return true;
            }
            clearReference(pendingHandler);
            return true;
        }

        public boolean clearReference(InternalStateHandler handler) {
            synchronized (this) {
                if (this.mPendingHandler.get() != handler) {
                    return false;
                }
                this.mPendingHandler.clear();
                return true;
            }
        }

        public boolean hasPending() {
            return this.mPendingHandler.get() != null;
        }
    }
}
