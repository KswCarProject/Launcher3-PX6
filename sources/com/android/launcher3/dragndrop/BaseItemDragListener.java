package com.android.launcher3.dragndrop;

import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.R;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.states.InternalStateHandler;
import com.android.launcher3.widget.PendingItemDragHelper;
import java.util.UUID;

public abstract class BaseItemDragListener extends InternalStateHandler implements View.OnDragListener, DragSource, DragOptions.PreDragCondition {
    public static final String EXTRA_PIN_ITEM_DRAG_LISTENER = "pin_item_drag_listener";
    private static final String MIME_TYPE_PREFIX = "com.android.launcher3.drag_and_drop/";
    private static final String TAG = "BaseItemDragListener";
    private DragController mDragController;
    private long mDragStartTime;
    private final String mId = UUID.randomUUID().toString();
    protected Launcher mLauncher;
    private final int mPreviewBitmapWidth;
    private final Rect mPreviewRect;
    private final int mPreviewViewWidth;

    /* access modifiers changed from: protected */
    public abstract PendingItemDragHelper createDragHelper();

    public BaseItemDragListener(Rect previewRect, int previewBitmapWidth, int previewViewWidth) {
        this.mPreviewRect = previewRect;
        this.mPreviewBitmapWidth = previewBitmapWidth;
        this.mPreviewViewWidth = previewViewWidth;
    }

    public String getMimeType() {
        return MIME_TYPE_PREFIX + this.mId;
    }

    public boolean init(Launcher launcher, boolean alreadyOnHome) {
        AbstractFloatingView.closeAllOpenViews(launcher, alreadyOnHome);
        launcher.getStateManager().goToState(LauncherState.NORMAL, alreadyOnHome);
        launcher.getDragLayer().setOnDragListener(this);
        launcher.getRotationHelper().setStateHandlerRequest(2);
        this.mLauncher = launcher;
        this.mDragController = launcher.getDragController();
        return false;
    }

    public boolean onDrag(View view, DragEvent event) {
        if (this.mLauncher == null || this.mDragController == null) {
            postCleanup();
            return false;
        } else if (event.getAction() != 1) {
            return this.mDragController.onDragEvent(this.mDragStartTime, event);
        } else {
            if (onDragStart(event)) {
                return true;
            }
            postCleanup();
            return false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean onDragStart(DragEvent event) {
        ClipDescription desc = event.getClipDescription();
        if (desc == null || !desc.hasMimeType(getMimeType())) {
            Log.e(TAG, "Someone started a dragAndDrop before us.");
            return false;
        }
        Point downPos = new Point((int) event.getX(), (int) event.getY());
        DragOptions options = new DragOptions();
        options.systemDndStartPoint = downPos;
        options.preDragCondition = this;
        createDragHelper().startDrag(new Rect(this.mPreviewRect), this.mPreviewBitmapWidth, this.mPreviewViewWidth, downPos, this, options);
        this.mDragStartTime = SystemClock.uptimeMillis();
        return true;
    }

    public boolean shouldStartDrag(double distanceDragged) {
        return !this.mLauncher.isWorkspaceLocked();
    }

    public void onPreDragStart(DropTarget.DragObject dragObject) {
        this.mLauncher.getDragLayer().setAlpha(1.0f);
        dragObject.dragView.setColor(this.mLauncher.getResources().getColor(R.color.delete_target_hover_tint));
    }

    public void onPreDragEnd(DropTarget.DragObject dragObject, boolean dragStarted) {
        if (dragStarted) {
            dragObject.dragView.setColor(0);
        }
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
        postCleanup();
    }

    /* access modifiers changed from: protected */
    public void postCleanup() {
        clearReference();
        if (this.mLauncher != null) {
            Intent newIntent = new Intent(this.mLauncher.getIntent());
            newIntent.removeExtra(EXTRA_PIN_ITEM_DRAG_LISTENER);
            this.mLauncher.setIntent(newIntent);
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public final void run() {
                BaseItemDragListener.this.removeListener();
            }
        });
    }

    public void removeListener() {
        if (this.mLauncher != null) {
            this.mLauncher.getRotationHelper().setStateHandlerRequest(0);
            this.mLauncher.getDragLayer().setOnDragListener((View.OnDragListener) null);
        }
    }
}
