package com.android.launcher3;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.android.launcher3.DropTarget;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.userevent.nano.LauncherLogProto;

public class DeleteDropTarget extends ButtonDropTarget {
    private int mControlType;

    public DeleteDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeleteDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mControlType = 0;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHoverColor = getResources().getColor(R.color.delete_target_hover_tint);
        setDrawable(R.drawable.ic_remove_shadow);
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        super.onDragStart(dragObject, options);
        setTextBasedOnDragSource(dragObject.dragInfo);
        setControlTypeBasedOnDragSource(dragObject.dragInfo);
    }

    public boolean supportsAccessibilityDrop(ItemInfo info, View view) {
        return (info instanceof ShortcutInfo) || (info instanceof LauncherAppWidgetInfo) || (info instanceof FolderInfo);
    }

    public int getAccessibilityAction() {
        return R.id.action_remove;
    }

    /* access modifiers changed from: protected */
    public boolean supportsDrop(ItemInfo info) {
        return true;
    }

    private void setTextBasedOnDragSource(ItemInfo item) {
        if (!TextUtils.isEmpty(this.mText)) {
            this.mText = getResources().getString(item.id != -1 ? R.string.remove_drop_target_label : 17039360);
            requestLayout();
        }
    }

    private void setControlTypeBasedOnDragSource(ItemInfo item) {
        this.mControlType = item.id != -1 ? 5 : 14;
    }

    public void completeDrop(DropTarget.DragObject d) {
        ItemInfo item = d.dragInfo;
        if ((d.dragSource instanceof Workspace) || (d.dragSource instanceof Folder)) {
            onAccessibilityDrop((View) null, item);
        }
    }

    public void onAccessibilityDrop(View view, ItemInfo item) {
        this.mLauncher.removeItem(view, item, true);
        this.mLauncher.getWorkspace().stripEmptyScreens();
        this.mLauncher.getDragLayer().announceForAccessibility(getContext().getString(R.string.item_removed));
    }

    public LauncherLogProto.Target getDropTargetForLogging() {
        LauncherLogProto.Target t = LoggerUtils.newTarget(2);
        t.controlType = this.mControlType;
        return t;
    }
}
