package com.szchoiceway.index;

import android.os.AsyncTask;
import android.os.Process;
import com.szchoiceway.index.AsyncTaskPageData;

/* compiled from: AppsCustomizePagedView */
class AppsCustomizeAsyncTask extends AsyncTask<AsyncTaskPageData, Void, AsyncTaskPageData> {
    AsyncTaskPageData.Type dataType;
    int page;
    int threadPriority = 0;

    AppsCustomizeAsyncTask(int p, AsyncTaskPageData.Type ty) {
        this.page = p;
        this.dataType = ty;
    }

    /* access modifiers changed from: protected */
    public AsyncTaskPageData doInBackground(AsyncTaskPageData... params) {
        if (params.length != 1) {
            return null;
        }
        params[0].doInBackgroundCallback.run(this, params[0]);
        return params[0];
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(AsyncTaskPageData result) {
        result.postExecuteCallback.run(this, result);
    }

    /* access modifiers changed from: package-private */
    public void setThreadPriority(int p) {
        this.threadPriority = p;
    }

    /* access modifiers changed from: package-private */
    public void syncThreadPriority() {
        Process.setThreadPriority(this.threadPriority);
    }
}
