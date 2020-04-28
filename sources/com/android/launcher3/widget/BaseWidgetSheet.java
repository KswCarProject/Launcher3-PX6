package com.android.launcher3.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.graphics.ColorScrim;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.AbstractSlideInView;

abstract class BaseWidgetSheet extends AbstractSlideInView implements View.OnClickListener, View.OnLongClickListener, DragSource {
    protected final ColorScrim mColorScrim = ColorScrim.createExtractedColorScrim(this);
    private Toast mWidgetInstructionToast;

    /* access modifiers changed from: protected */
    public abstract int getElementsRowCount();

    public BaseWidgetSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public final void onClick(View v) {
        if (this.mWidgetInstructionToast != null) {
            this.mWidgetInstructionToast.cancel();
        }
        this.mWidgetInstructionToast = Toast.makeText(getContext(), Utilities.wrapForTts(getContext().getText(R.string.long_press_widget_to_add), getContext().getString(R.string.long_accessible_way_to_add)), 0);
        this.mWidgetInstructionToast.show();
    }

    public final boolean onLongClick(View v) {
        if (!ItemLongClickListener.canStartDrag(this.mLauncher)) {
            return false;
        }
        if (v instanceof WidgetCell) {
            return beginDraggingWidget((WidgetCell) v);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void setTranslationShift(float translationShift) {
        super.setTranslationShift(translationShift);
        this.mColorScrim.setProgress(1.0f - this.mTranslationShift);
    }

    private boolean beginDraggingWidget(WidgetCell v) {
        WidgetImageView image = v.getWidgetView();
        if (image.getBitmap() == null) {
            return false;
        }
        int[] loc = new int[2];
        this.mLauncher.getDragLayer().getLocationInDragLayer(image, loc);
        new PendingItemDragHelper(v).startDrag(image.getBitmapBounds(), image.getBitmap().getWidth(), image.getWidth(), new Point(loc[0], loc[1]), this, new DragOptions());
        close(true);
        return true;
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
    }

    /* access modifiers changed from: protected */
    public void onCloseComplete() {
        super.onCloseComplete();
        clearNavBarColor();
    }

    /* access modifiers changed from: protected */
    public void clearNavBarColor() {
        this.mLauncher.getSystemUiController().updateUiState(2, 0);
    }

    /* access modifiers changed from: protected */
    public void setupNavBarColor() {
        this.mLauncher.getSystemUiController().updateUiState(2, Themes.getAttrBoolean(this.mLauncher, R.attr.isMainColorDark) ? 2 : 1);
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        targetParent.containerType = 5;
        targetParent.cardinality = getElementsRowCount();
    }

    public final void logActionCommand(int command) {
        LauncherLogProto.Target target = LoggerUtils.newContainerTarget(5);
        target.cardinality = getElementsRowCount();
        this.mLauncher.getUserEventDispatcher().logActionCommand(command, target);
    }
}
