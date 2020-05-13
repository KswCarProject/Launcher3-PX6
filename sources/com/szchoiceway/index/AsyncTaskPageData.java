package com.szchoiceway.index;

import android.graphics.Bitmap;
import java.util.ArrayList;

/* compiled from: AppsCustomizePagedView */
class AsyncTaskPageData {
    AsyncTaskCallback doInBackgroundCallback;
    ArrayList<Bitmap> generatedImages = new ArrayList<>();
    ArrayList<Object> items;
    int maxImageHeight;
    int maxImageWidth;
    int page;
    AsyncTaskCallback postExecuteCallback;
    ArrayList<Bitmap> sourceImages;
    WidgetPreviewLoader widgetPreviewLoader;

    /* compiled from: AppsCustomizePagedView */
    enum Type {
        LoadWidgetPreviewData
    }

    AsyncTaskPageData(int p, ArrayList<Object> l, int cw, int ch, AsyncTaskCallback bgR, AsyncTaskCallback postR, WidgetPreviewLoader w) {
        this.page = p;
        this.items = l;
        this.maxImageWidth = cw;
        this.maxImageHeight = ch;
        this.doInBackgroundCallback = bgR;
        this.postExecuteCallback = postR;
        this.widgetPreviewLoader = w;
    }

    /* access modifiers changed from: package-private */
    public void cleanup(boolean cancelled) {
        if (this.generatedImages != null) {
            if (cancelled) {
                for (int i = 0; i < this.generatedImages.size(); i++) {
                    this.widgetPreviewLoader.recycleBitmap(this.items.get(i), this.generatedImages.get(i));
                }
            }
            this.generatedImages.clear();
        }
    }
}
