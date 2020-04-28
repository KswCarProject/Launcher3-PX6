package com.android.launcher3.util;

import android.view.View;
import android.view.ViewTreeObserver;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class ViewOnDrawExecutor implements Executor, ViewTreeObserver.OnDrawListener, Runnable, View.OnAttachStateChangeListener {
    private View mAttachedView;
    private boolean mCompleted;
    private boolean mFirstDrawCompleted;
    private Launcher mLauncher;
    private boolean mLoadAnimationCompleted;
    private final ArrayList<Runnable> mTasks = new ArrayList<>();

    public void attachTo(Launcher launcher) {
        attachTo(launcher, launcher.getWorkspace(), true);
    }

    public void attachTo(Launcher launcher, View attachedView, boolean waitForLoadAnimation) {
        this.mLauncher = launcher;
        this.mAttachedView = attachedView;
        this.mAttachedView.addOnAttachStateChangeListener(this);
        if (!waitForLoadAnimation) {
            this.mLoadAnimationCompleted = true;
        }
        attachObserver();
    }

    private void attachObserver() {
        if (!this.mCompleted) {
            this.mAttachedView.getViewTreeObserver().addOnDrawListener(this);
        }
    }

    public void execute(Runnable command) {
        this.mTasks.add(command);
        LauncherModel.setWorkerPriority(10);
    }

    public void onViewAttachedToWindow(View v) {
        attachObserver();
    }

    public void onViewDetachedFromWindow(View v) {
    }

    public void onDraw() {
        this.mFirstDrawCompleted = true;
        this.mAttachedView.post(this);
    }

    public void onLoadAnimationCompleted() {
        this.mLoadAnimationCompleted = true;
        if (this.mAttachedView != null) {
            this.mAttachedView.post(this);
        }
    }

    public void run() {
        if (this.mLoadAnimationCompleted && this.mFirstDrawCompleted && !this.mCompleted) {
            runAllTasks();
        }
    }

    public void markCompleted() {
        this.mTasks.clear();
        this.mCompleted = true;
        if (this.mAttachedView != null) {
            this.mAttachedView.getViewTreeObserver().removeOnDrawListener(this);
            this.mAttachedView.removeOnAttachStateChangeListener(this);
        }
        if (this.mLauncher != null) {
            this.mLauncher.clearPendingExecutor(this);
        }
        LauncherModel.setWorkerPriority(0);
    }

    /* access modifiers changed from: protected */
    public boolean isCompleted() {
        return this.mCompleted;
    }

    /* access modifiers changed from: protected */
    public void runAllTasks() {
        Iterator<Runnable> it = this.mTasks.iterator();
        while (it.hasNext()) {
            it.next().run();
        }
        markCompleted();
    }
}
