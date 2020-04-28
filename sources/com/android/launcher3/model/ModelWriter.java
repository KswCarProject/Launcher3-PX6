package com.android.launcher3.model;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LooperExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Executor;

public class ModelWriter {
    private static final String TAG = "ModelWriter";
    /* access modifiers changed from: private */
    public final BgDataModel mBgDataModel;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final boolean mHasVerticalHotseat;
    /* access modifiers changed from: private */
    public final LauncherModel mModel;
    /* access modifiers changed from: private */
    public final Handler mUiHandler;
    /* access modifiers changed from: private */
    public final boolean mVerifyChanges;
    private final Executor mWorkerExecutor = new LooperExecutor(LauncherModel.getWorkerLooper());

    public ModelWriter(Context context, LauncherModel model, BgDataModel dataModel, boolean hasVerticalHotseat, boolean verifyChanges) {
        this.mContext = context;
        this.mModel = model;
        this.mBgDataModel = dataModel;
        this.mHasVerticalHotseat = hasVerticalHotseat;
        this.mVerifyChanges = verifyChanges;
        this.mUiHandler = new Handler(Looper.getMainLooper());
    }

    private void updateItemInfoProps(ItemInfo item, long container, long screenId, int cellX, int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        if (container == -101) {
            item.screenId = this.mHasVerticalHotseat ? (long) ((LauncherAppState.getIDP(this.mContext).numHotseatIcons - cellY) - 1) : (long) cellX;
        } else {
            item.screenId = screenId;
        }
    }

    public void addOrMoveItemInDatabase(ItemInfo item, long container, long screenId, int cellX, int cellY) {
        if (item.container == -1) {
            addItemToDatabase(item, container, screenId, cellX, cellY);
        } else {
            moveItemInDatabase(item, container, screenId, cellX, cellY);
        }
    }

    /* access modifiers changed from: private */
    public void checkItemInfoLocked(long itemId, ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo modelItem = (ItemInfo) this.mBgDataModel.itemsIdMap.get(itemId);
        if (modelItem != null && item != modelItem) {
            if ((modelItem instanceof ShortcutInfo) && (item instanceof ShortcutInfo)) {
                ShortcutInfo modelShortcut = (ShortcutInfo) modelItem;
                ShortcutInfo shortcut = (ShortcutInfo) item;
                if (modelShortcut.title.toString().equals(shortcut.title.toString()) && modelShortcut.intent.filterEquals(shortcut.intent) && modelShortcut.id == shortcut.id && modelShortcut.itemType == shortcut.itemType && modelShortcut.container == shortcut.container && modelShortcut.screenId == shortcut.screenId && modelShortcut.cellX == shortcut.cellX && modelShortcut.cellY == shortcut.cellY && modelShortcut.spanX == shortcut.spanX && modelShortcut.spanY == shortcut.spanY) {
                    return;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("item: ");
            sb.append(item != null ? item.toString() : "null");
            sb.append("modelItem: ");
            sb.append(modelItem != null ? modelItem.toString() : "null");
            sb.append("Error: ItemInfo passed to checkItemInfo doesn't match original");
            RuntimeException e = new RuntimeException(sb.toString());
            if (stackTrace != null) {
                e.setStackTrace(stackTrace);
            }
            throw e;
        }
    }

    public void moveItemInDatabase(ItemInfo item, long container, long screenId, int cellX, int cellY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        this.mWorkerExecutor.execute(new UpdateItemRunnable(item, new ContentWriter(this.mContext).put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY)).put(LauncherSettings.Favorites.RANK, Integer.valueOf(item.rank)).put(LauncherSettings.Favorites.SCREEN, Long.valueOf(item.screenId))));
    }

    public void moveItemsInDatabase(ArrayList<ItemInfo> items, long container, int screen) {
        ArrayList<ItemInfo> arrayList = items;
        ArrayList<ContentValues> contentValues = new ArrayList<>();
        int count = items.size();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < count) {
                ItemInfo item = arrayList.get(i2);
                updateItemInfoProps(item, container, (long) screen, item.cellX, item.cellY);
                ContentValues values = new ContentValues();
                values.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container));
                values.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX));
                values.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY));
                values.put(LauncherSettings.Favorites.RANK, Integer.valueOf(item.rank));
                values.put(LauncherSettings.Favorites.SCREEN, Long.valueOf(item.screenId));
                contentValues.add(values);
                i = i2 + 1;
            } else {
                int i3 = screen;
                this.mWorkerExecutor.execute(new UpdateItemsRunnable(arrayList, contentValues));
                return;
            }
        }
    }

    public void modifyItemInDatabase(ItemInfo item, long container, long screenId, int cellX, int cellY, int spanX, int spanY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        item.spanX = spanX;
        item.spanY = spanY;
        this.mWorkerExecutor.execute(new UpdateItemRunnable(item, new ContentWriter(this.mContext).put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY)).put(LauncherSettings.Favorites.RANK, Integer.valueOf(item.rank)).put(LauncherSettings.Favorites.SPANX, Integer.valueOf(item.spanX)).put(LauncherSettings.Favorites.SPANY, Integer.valueOf(item.spanY)).put(LauncherSettings.Favorites.SCREEN, Long.valueOf(item.screenId))));
    }

    public void updateItemInDatabase(ItemInfo item) {
        ContentWriter writer = new ContentWriter(this.mContext);
        item.onAddToDatabase(writer);
        this.mWorkerExecutor.execute(new UpdateItemRunnable(item, writer));
    }

    public void addItemToDatabase(ItemInfo item, long container, long screenId, int cellX, int cellY) {
        ItemInfo itemInfo = item;
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        ContentWriter writer = new ContentWriter(this.mContext);
        ContentResolver cr = this.mContext.getContentResolver();
        item.onAddToDatabase(writer);
        itemInfo.id = LauncherSettings.Settings.call(cr, LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getLong(LauncherSettings.Settings.EXTRA_VALUE);
        writer.put("_id", Long.valueOf(itemInfo.id));
        ModelVerifier verifier = new ModelVerifier();
        this.mWorkerExecutor.execute(new Runnable(cr, writer, item, new Throwable().getStackTrace(), verifier) {
            private final /* synthetic */ ContentResolver f$1;
            private final /* synthetic */ ContentWriter f$2;
            private final /* synthetic */ ItemInfo f$3;
            private final /* synthetic */ StackTraceElement[] f$4;
            private final /* synthetic */ ModelWriter.ModelVerifier f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                ModelWriter.lambda$addItemToDatabase$0(ModelWriter.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    public static /* synthetic */ void lambda$addItemToDatabase$0(ModelWriter modelWriter, ContentResolver cr, ContentWriter writer, ItemInfo item, StackTraceElement[] stackTrace, ModelVerifier verifier) {
        cr.insert(LauncherSettings.Favorites.CONTENT_URI, writer.getValues(modelWriter.mContext));
        synchronized (modelWriter.mBgDataModel) {
            modelWriter.checkItemInfoLocked(item.id, item, stackTrace);
            modelWriter.mBgDataModel.addItem(modelWriter.mContext, item, true);
            verifier.verifyModel();
        }
    }

    public void deleteItemFromDatabase(ItemInfo item) {
        deleteItemsFromDatabase((Iterable<? extends ItemInfo>) Arrays.asList(new ItemInfo[]{item}));
    }

    public void deleteItemsFromDatabase(ItemInfoMatcher matcher) {
        deleteItemsFromDatabase((Iterable<? extends ItemInfo>) matcher.filterItemInfos(this.mBgDataModel.itemsIdMap));
    }

    public void deleteItemsFromDatabase(Iterable<? extends ItemInfo> items) {
        this.mWorkerExecutor.execute(new Runnable(items, new ModelVerifier()) {
            private final /* synthetic */ Iterable f$1;
            private final /* synthetic */ ModelWriter.ModelVerifier f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ModelWriter.lambda$deleteItemsFromDatabase$1(ModelWriter.this, this.f$1, this.f$2);
            }
        });
    }

    public static /* synthetic */ void lambda$deleteItemsFromDatabase$1(ModelWriter modelWriter, Iterable items, ModelVerifier verifier) {
        Iterator it = items.iterator();
        while (it.hasNext()) {
            ItemInfo item = (ItemInfo) it.next();
            modelWriter.mContext.getContentResolver().delete(LauncherSettings.Favorites.getContentUri(item.id), (String) null, (String[]) null);
            modelWriter.mBgDataModel.removeItem(modelWriter.mContext, item);
            verifier.verifyModel();
        }
    }

    public void deleteFolderAndContentsFromDatabase(FolderInfo info) {
        this.mWorkerExecutor.execute(new Runnable(info, new ModelVerifier()) {
            private final /* synthetic */ FolderInfo f$1;
            private final /* synthetic */ ModelWriter.ModelVerifier f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                ModelWriter.lambda$deleteFolderAndContentsFromDatabase$2(ModelWriter.this, this.f$1, this.f$2);
            }
        });
    }

    public static /* synthetic */ void lambda$deleteFolderAndContentsFromDatabase$2(ModelWriter modelWriter, FolderInfo info, ModelVerifier verifier) {
        ContentResolver cr = modelWriter.mContext.getContentResolver();
        Uri uri = LauncherSettings.Favorites.CONTENT_URI;
        cr.delete(uri, "container=" + info.id, (String[]) null);
        modelWriter.mBgDataModel.removeItem(modelWriter.mContext, (Iterable<? extends ItemInfo>) info.contents);
        info.contents.clear();
        cr.delete(LauncherSettings.Favorites.getContentUri(info.id), (String) null, (String[]) null);
        modelWriter.mBgDataModel.removeItem(modelWriter.mContext, info);
        verifier.verifyModel();
    }

    private class UpdateItemRunnable extends UpdateItemBaseRunnable {
        private final ItemInfo mItem;
        private final long mItemId;
        private final ContentWriter mWriter;

        UpdateItemRunnable(ItemInfo item, ContentWriter writer) {
            super();
            this.mItem = item;
            this.mWriter = writer;
            this.mItemId = item.id;
        }

        public void run() {
            ModelWriter.this.mContext.getContentResolver().update(LauncherSettings.Favorites.getContentUri(this.mItemId), this.mWriter.getValues(ModelWriter.this.mContext), (String) null, (String[]) null);
            updateItemArrays(this.mItem, this.mItemId);
        }
    }

    private class UpdateItemsRunnable extends UpdateItemBaseRunnable {
        private final ArrayList<ItemInfo> mItems;
        private final ArrayList<ContentValues> mValues;

        UpdateItemsRunnable(ArrayList<ItemInfo> items, ArrayList<ContentValues> values) {
            super();
            this.mValues = values;
            this.mItems = items;
        }

        public void run() {
            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
            int count = this.mItems.size();
            for (int i = 0; i < count; i++) {
                ItemInfo item = this.mItems.get(i);
                long itemId = item.id;
                ops.add(ContentProviderOperation.newUpdate(LauncherSettings.Favorites.getContentUri(itemId)).withValues(this.mValues.get(i)).build());
                updateItemArrays(item, itemId);
            }
            try {
                ModelWriter.this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, ops);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private abstract class UpdateItemBaseRunnable implements Runnable {
        private final StackTraceElement[] mStackTrace = new Throwable().getStackTrace();
        private final ModelVerifier mVerifier = new ModelVerifier();

        UpdateItemBaseRunnable() {
        }

        /* access modifiers changed from: protected */
        public void updateItemArrays(ItemInfo item, long itemId) {
            synchronized (ModelWriter.this.mBgDataModel) {
                ModelWriter.this.checkItemInfoLocked(itemId, item, this.mStackTrace);
                if (!(item.container == -100 || item.container == -101 || ModelWriter.this.mBgDataModel.folders.containsKey(item.container))) {
                    Log.e(ModelWriter.TAG, "item: " + item + " container being set to: " + item.container + ", not in the list of folders");
                }
                ItemInfo modelItem = (ItemInfo) ModelWriter.this.mBgDataModel.itemsIdMap.get(itemId);
                if (modelItem == null || !(modelItem.container == -100 || modelItem.container == -101)) {
                    ModelWriter.this.mBgDataModel.workspaceItems.remove(modelItem);
                } else {
                    int i = modelItem.itemType;
                    if (i != 6) {
                        switch (i) {
                            case 0:
                            case 1:
                            case 2:
                                break;
                        }
                    }
                    if (!ModelWriter.this.mBgDataModel.workspaceItems.contains(modelItem)) {
                        ModelWriter.this.mBgDataModel.workspaceItems.add(modelItem);
                    }
                }
                this.mVerifier.verifyModel();
            }
        }
    }

    public class ModelVerifier {
        final int startId;

        ModelVerifier() {
            this.startId = ModelWriter.this.mBgDataModel.lastBindId;
        }

        /* access modifiers changed from: package-private */
        public void verifyModel() {
            if (ModelWriter.this.mVerifyChanges && ModelWriter.this.mModel.getCallback() != null) {
                ModelWriter.this.mUiHandler.post(new Runnable(ModelWriter.this.mBgDataModel.lastBindId) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ModelWriter.ModelVerifier.lambda$verifyModel$0(ModelWriter.ModelVerifier.this, this.f$1);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$verifyModel$0(ModelVerifier modelVerifier, int executeId) {
            LauncherModel.Callbacks callbacks;
            if (ModelWriter.this.mBgDataModel.lastBindId <= executeId && executeId != modelVerifier.startId && (callbacks = ModelWriter.this.mModel.getCallback()) != null) {
                callbacks.rebindModel();
            }
        }
    }
}
