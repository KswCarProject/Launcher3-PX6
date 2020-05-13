package com.szchoiceway.index;

import android.content.ComponentName;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.szchoiceway.index.DropTarget;

public class InfoDropTarget extends ButtonDropTarget {
    private TransitionDrawable mDrawable;
    private ColorStateList mOriginalTextColor;

    public InfoDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InfoDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mOriginalTextColor = getTextColors();
        this.mHoverColor = getResources().getColor(R.color.info_target_hover_tint);
        this.mDrawable = (TransitionDrawable) getCurrentDrawable();
        if (this.mDrawable != null) {
            this.mDrawable.setCrossFadeEnabled(true);
        }
        if (getResources().getConfiguration().orientation == 2 && !LauncherApplication.isScreenLarge()) {
            setText("");
        }
    }

    private boolean isFromAllApps(DragSource source) {
        return source instanceof AppsCustomizePagedView;
    }

    public boolean acceptDrop(DropTarget.DragObject d) {
        ComponentName componentName = null;
        if (d.dragInfo instanceof ApplicationInfo) {
            componentName = ((ApplicationInfo) d.dragInfo).componentName;
        } else if (d.dragInfo instanceof ShortcutInfo) {
            componentName = ((ShortcutInfo) d.dragInfo).intent.getComponent();
        } else if (d.dragInfo instanceof PendingAddItemInfo) {
            componentName = ((PendingAddItemInfo) d.dragInfo).componentName;
        }
        if (componentName != null) {
            this.mLauncher.startApplicationDetailsActivity(componentName);
        }
        d.deferDragViewCleanupPostAnimation = false;
        return false;
    }

    public void onDragStart(DragSource source, Object info, int dragAction) {
        boolean isVisible = true;
        if (!isFromAllApps(source)) {
            isVisible = false;
        }
        this.mActive = isVisible;
        this.mDrawable.resetTransition();
        setTextColor(this.mOriginalTextColor);
        ((ViewGroup) getParent()).setVisibility(isVisible ? 0 : 8);
    }

    public void onDragEnd() {
        super.onDragEnd();
        this.mActive = false;
    }

    public void onDragEnter(DropTarget.DragObject d) {
        super.onDragEnter(d);
        this.mDrawable.startTransition(this.mTransitionDuration);
        setTextColor(this.mHoverColor);
    }

    public void onDragExit(DropTarget.DragObject d) {
        super.onDragExit(d);
        if (!d.dragComplete) {
            this.mDrawable.resetTransition();
            setTextColor(this.mOriginalTextColor);
        }
    }
}
