package com.szchoiceway.index;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.szchoiceway.index.DropTarget;

public class DeleteDropTarget extends ButtonDropTarget {
    private static int DELETE_ANIMATION_DURATION = 285;
    private static int FLING_DELETE_ANIMATION_DURATION = 350;
    private static float FLING_TO_DELETE_FRICTION = 0.035f;
    private static int MODE_FLING_DELETE_ALONG_VECTOR = 1;
    private static int MODE_FLING_DELETE_TO_TRASH = 0;
    private TransitionDrawable mCurrentDrawable;
    private final int mFlingDeleteMode;
    private ColorStateList mOriginalTextColor;
    private TransitionDrawable mRemoveDrawable;
    private TransitionDrawable mUninstallDrawable;

    public DeleteDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeleteDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mFlingDeleteMode = MODE_FLING_DELETE_ALONG_VECTOR;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mOriginalTextColor = getTextColors();
        Resources r = getResources();
        this.mHoverColor = r.getColor(R.color.delete_target_hover_tint);
        this.mUninstallDrawable = (TransitionDrawable) r.getDrawable(R.drawable.uninstall_target_selector);
        this.mRemoveDrawable = (TransitionDrawable) r.getDrawable(R.drawable.remove_target_selector);
        this.mRemoveDrawable.setCrossFadeEnabled(true);
        this.mUninstallDrawable.setCrossFadeEnabled(true);
        this.mCurrentDrawable = (TransitionDrawable) getCurrentDrawable();
        if (getResources().getConfiguration().orientation == 2 && !LauncherApplication.isScreenLarge()) {
            setText("");
        }
    }

    private boolean isAllAppsApplication(DragSource source, Object info) {
        return (source instanceof AppsCustomizePagedView) && (info instanceof ApplicationInfo);
    }

    private boolean isAllAppsWidget(DragSource source, Object info) {
        if ((source instanceof AppsCustomizePagedView) && (info instanceof PendingAddItemInfo)) {
            switch (((PendingAddItemInfo) info).itemType) {
                case 1:
                case 4:
                    return true;
            }
        }
        return false;
    }

    private boolean isDragSourceWorkspaceOrFolder(DropTarget.DragObject d) {
        return (d.dragSource instanceof Workspace) || (d.dragSource instanceof Folder);
    }

    private boolean isWorkspaceOrFolderApplication(DropTarget.DragObject d) {
        return isDragSourceWorkspaceOrFolder(d) && (d.dragInfo instanceof ShortcutInfo);
    }

    private boolean isWorkspaceOrFolderWidget(DropTarget.DragObject d) {
        return isDragSourceWorkspaceOrFolder(d) && (d.dragInfo instanceof LauncherAppWidgetInfo);
    }

    private boolean isWorkspaceFolder(DropTarget.DragObject d) {
        return (d.dragSource instanceof Workspace) && (d.dragInfo instanceof FolderInfo);
    }

    private void setHoverColor() {
        this.mCurrentDrawable.startTransition(this.mTransitionDuration);
        setTextColor(this.mHoverColor);
    }

    private void resetHoverColor() {
        this.mCurrentDrawable.resetTransition();
        setTextColor(this.mOriginalTextColor);
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        return true;
    }

    public void onDragStart(DragSource source, Object info, int dragAction) {
        boolean isVisible = true;
        boolean isUninstall = false;
        if (isAllAppsWidget(source, info)) {
            isVisible = false;
        }
        if (isAllAppsApplication(source, info)) {
            if ((((ApplicationInfo) info).flags & 1) != 0) {
                isUninstall = true;
            } else {
                isVisible = false;
            }
        }
        if (isUninstall) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(this.mUninstallDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            setCompoundDrawablesRelativeWithIntrinsicBounds(this.mRemoveDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
        }
        this.mCurrentDrawable = (TransitionDrawable) getCurrentDrawable();
        this.mActive = isVisible;
        resetHoverColor();
        ((ViewGroup) getParent()).setVisibility(isVisible ? 0 : 8);
        if (getText().length() > 0) {
            setText(isUninstall ? R.string.delete_target_uninstall_label : R.string.delete_target_label);
        }
    }

    public void onDragEnd() {
        super.onDragEnd();
        this.mActive = false;
    }

    public void onDragEnter(DropTarget.DragObject d) {
        super.onDragEnter(d);
        setHoverColor();
    }

    public void onDragExit(DropTarget.DragObject d) {
        super.onDragExit(d);
        if (!d.dragComplete) {
            resetHoverColor();
        } else {
            d.dragView.setColor(this.mHoverColor);
        }
    }

    private void animateToTrashAndCompleteDrop(DropTarget.DragObject d) {
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, from);
        Rect to = getIconRect(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(), this.mCurrentDrawable.getIntrinsicWidth(), this.mCurrentDrawable.getIntrinsicHeight());
        float scale = ((float) to.width()) / ((float) from.width());
        this.mSearchDropTargetBar.deferOnDragEnd();
        final DropTarget.DragObject dragObject = d;
        dragLayer.animateView(d.dragView, from, to, scale, 1.0f, 1.0f, 0.1f, 0.1f, DELETE_ANIMATION_DURATION, new DecelerateInterpolator(2.0f), new LinearInterpolator(), new Runnable() {
            public void run() {
                DeleteDropTarget.this.mSearchDropTargetBar.onDragEnd();
                DeleteDropTarget.this.mLauncher.exitSpringLoadedDragMode();
                DeleteDropTarget.this.completeDrop(dragObject);
            }
        }, 0, (View) null);
    }

    /* access modifiers changed from: private */
    public void completeDrop(DropTarget.DragObject d) {
        ItemInfo item = (ItemInfo) d.dragInfo;
        if (isAllAppsApplication(d.dragSource, item)) {
            this.mLauncher.startApplicationUninstallActivity((ApplicationInfo) item);
        } else if (isWorkspaceOrFolderApplication(d)) {
            LauncherModel.deleteItemFromDatabase(this.mLauncher, item);
        } else if (isWorkspaceFolder(d)) {
            FolderInfo folderInfo = (FolderInfo) item;
            this.mLauncher.removeFolder(folderInfo);
            LauncherModel.deleteFolderContentsFromDatabase(this.mLauncher, folderInfo);
        } else if (isWorkspaceOrFolderWidget(d)) {
            this.mLauncher.removeAppWidget((LauncherAppWidgetInfo) item);
            LauncherModel.deleteItemFromDatabase(this.mLauncher, item);
            final LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) item;
            final LauncherAppWidgetHost appWidgetHost = this.mLauncher.getAppWidgetHost();
            if (appWidgetHost != null) {
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        appWidgetHost.deleteAppWidgetId(launcherAppWidgetInfo.appWidgetId);
                    }
                }.start();
            }
        }
    }

    public void onDrop(DropTarget.DragObject d) {
        animateToTrashAndCompleteDrop(d);
    }

    private ValueAnimator.AnimatorUpdateListener createFlingToTrashAnimatorListener(DragLayer dragLayer, DropTarget.DragObject d, PointF vel, ViewConfiguration config) {
        Rect to = getIconRect(d.dragView.getMeasuredWidth(), d.dragView.getMeasuredHeight(), this.mCurrentDrawable.getIntrinsicWidth(), this.mCurrentDrawable.getIntrinsicHeight());
        Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, from);
        int offsetY = (int) (((float) (-from.top)) * Math.min(1.0f, Math.abs(vel.length()) / (((float) config.getScaledMaximumFlingVelocity()) / 2.0f)));
        int offsetX = (int) (((float) offsetY) / (vel.y / vel.x));
        final float y2 = (float) (from.top + offsetY);
        final float x2 = (float) (from.left + offsetX);
        final float x1 = (float) from.left;
        final float y1 = (float) from.top;
        final float x3 = (float) to.left;
        final float y3 = (float) to.top;
        final TimeInterpolator scaleAlphaInterpolator = new TimeInterpolator() {
            public float getInterpolation(float t) {
                return t * t * t * t * t * t * t * t;
            }
        };
        final DragLayer dragLayer2 = dragLayer;
        return new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                DragView dragView = (DragView) dragLayer2.getAnimatedView();
                float t = ((Float) animation.getAnimatedValue()).floatValue();
                float tp = scaleAlphaInterpolator.getInterpolation(t);
                float initialScale = dragView.getInitialScale();
                float scale = dragView.getScaleX();
                float x1o = ((1.0f - scale) * ((float) dragView.getMeasuredWidth())) / 2.0f;
                float y1o = ((1.0f - scale) * ((float) dragView.getMeasuredHeight())) / 2.0f;
                dragView.setTranslationX(((1.0f - t) * (1.0f - t) * (x1 - x1o)) + (2.0f * (1.0f - t) * t * (x2 - x1o)) + (t * t * x3));
                dragView.setTranslationY(((1.0f - t) * (1.0f - t) * (y1 - y1o)) + (2.0f * (1.0f - t) * t * (y2 - x1o)) + (t * t * y3));
                dragView.setScaleX((1.0f - tp) * initialScale);
                dragView.setScaleY((1.0f - tp) * initialScale);
                dragView.setAlpha(((1.0f - 0.5f) * (1.0f - tp)) + 0.5f);
            }
        };
    }

    private static class FlingAlongVectorAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final TimeInterpolator mAlphaInterpolator = new DecelerateInterpolator(0.75f);
        private DragLayer mDragLayer;
        private float mFriction;
        private Rect mFrom;
        private boolean mHasOffsetForScale;
        private long mPrevTime;
        private PointF mVelocity;

        public FlingAlongVectorAnimatorUpdateListener(DragLayer dragLayer, PointF vel, Rect from, long startTime, float friction) {
            this.mDragLayer = dragLayer;
            this.mVelocity = vel;
            this.mFrom = from;
            this.mPrevTime = startTime;
            this.mFriction = 1.0f - (dragLayer.getResources().getDisplayMetrics().density * friction);
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            DragView dragView = (DragView) this.mDragLayer.getAnimatedView();
            float t = ((Float) animation.getAnimatedValue()).floatValue();
            long curTime = AnimationUtils.currentAnimationTimeMillis();
            if (!this.mHasOffsetForScale) {
                this.mHasOffsetForScale = true;
                float scale = dragView.getScaleX();
                float xOffset = ((scale - 1.0f) * ((float) dragView.getMeasuredWidth())) / 2.0f;
                float yOffset = ((scale - 1.0f) * ((float) dragView.getMeasuredHeight())) / 2.0f;
                Rect rect = this.mFrom;
                rect.left = (int) (((float) rect.left) + xOffset);
                Rect rect2 = this.mFrom;
                rect2.top = (int) (((float) rect2.top) + yOffset);
            }
            Rect rect3 = this.mFrom;
            rect3.left = (int) (((float) rect3.left) + ((this.mVelocity.x * ((float) (curTime - this.mPrevTime))) / 1000.0f));
            Rect rect4 = this.mFrom;
            rect4.top = (int) (((float) rect4.top) + ((this.mVelocity.y * ((float) (curTime - this.mPrevTime))) / 1000.0f));
            dragView.setTranslationX((float) this.mFrom.left);
            dragView.setTranslationY((float) this.mFrom.top);
            dragView.setAlpha(1.0f - this.mAlphaInterpolator.getInterpolation(t));
            this.mVelocity.x *= this.mFriction;
            this.mVelocity.y *= this.mFriction;
            this.mPrevTime = curTime;
        }
    }

    private ValueAnimator.AnimatorUpdateListener createFlingAlongVectorAnimatorListener(DragLayer dragLayer, DropTarget.DragObject d, PointF vel, long startTime, int duration, ViewConfiguration config) {
        Rect from = new Rect();
        dragLayer.getViewRectRelativeToSelf(d.dragView, from);
        return new FlingAlongVectorAnimatorUpdateListener(dragLayer, vel, from, startTime, FLING_TO_DELETE_FRICTION);
    }

    public void onFlingToDelete(DropTarget.DragObject d, int x, int y, PointF vel) {
        boolean isAllApps = d.dragSource instanceof AppsCustomizePagedView;
        d.dragView.setColor(0);
        d.dragView.updateInitialScaleToCurrentScale();
        if (isAllApps) {
            resetHoverColor();
        }
        if (this.mFlingDeleteMode == MODE_FLING_DELETE_TO_TRASH) {
            this.mSearchDropTargetBar.deferOnDragEnd();
            this.mSearchDropTargetBar.finishAnimations();
        }
        ViewConfiguration config = ViewConfiguration.get(this.mLauncher);
        DragLayer dragLayer = this.mLauncher.getDragLayer();
        final int duration = FLING_DELETE_ANIMATION_DURATION;
        final long startTime = AnimationUtils.currentAnimationTimeMillis();
        AnonymousClass5 r0 = new TimeInterpolator() {
            private int mCount = -1;
            private float mOffset = 0.0f;

            public float getInterpolation(float t) {
                if (this.mCount < 0) {
                    this.mCount++;
                } else if (this.mCount == 0) {
                    this.mOffset = Math.min(0.5f, ((float) (AnimationUtils.currentAnimationTimeMillis() - startTime)) / ((float) duration));
                    this.mCount++;
                }
                return Math.min(1.0f, this.mOffset + t);
            }
        };
        ValueAnimator.AnimatorUpdateListener updateCb = null;
        if (this.mFlingDeleteMode == MODE_FLING_DELETE_TO_TRASH) {
            updateCb = createFlingToTrashAnimatorListener(dragLayer, d, vel, config);
        } else if (this.mFlingDeleteMode == MODE_FLING_DELETE_ALONG_VECTOR) {
            updateCb = createFlingAlongVectorAnimatorListener(dragLayer, d, vel, startTime, duration, config);
        }
        final boolean z = isAllApps;
        final DropTarget.DragObject dragObject = d;
        DragLayer dragLayer2 = dragLayer;
        dragLayer2.animateView(d.dragView, updateCb, duration, r0, new Runnable() {
            public void run() {
                if (!z) {
                    DeleteDropTarget.this.mLauncher.exitSpringLoadedDragMode();
                    DeleteDropTarget.this.completeDrop(dragObject);
                }
                DeleteDropTarget.this.mLauncher.getDragController().onDeferredEndFling(dragObject);
            }
        }, 0, (View) null);
    }
}
