package com.android.launcher3.model;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.android.launcher3.AllAppsList;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import java.lang.ref.WeakReference;
import java.util.concurrent.Executor;

public class ModelPreload implements LauncherModel.ModelUpdateTask {
    private static final String TAG = "ModelPreload";
    private AllAppsList mAllAppsList;
    private LauncherAppState mApp;
    private BgDataModel mBgDataModel;
    private LauncherModel mModel;

    public final void init(LauncherAppState app, LauncherModel model, BgDataModel dataModel, AllAppsList allAppsList, Executor uiExecutor) {
        this.mApp = app;
        this.mModel = model;
        this.mBgDataModel = dataModel;
        this.mAllAppsList = allAppsList;
    }

    public final void run() {
        this.mModel.startLoaderForResultsIfNotLoaded(new LoaderResults(this.mApp, this.mBgDataModel, this.mAllAppsList, 0, (WeakReference<LauncherModel.Callbacks>) null));
        Log.d(TAG, "Preload completed : " + this.mModel.isModelLoaded());
        onComplete(this.mModel.isModelLoaded());
    }

    @WorkerThread
    public void onComplete(boolean isSuccess) {
    }

    public void start(Context context) {
        LauncherAppState.getInstance(context).getModel().enqueueModelUpdateTask(this);
    }
}
