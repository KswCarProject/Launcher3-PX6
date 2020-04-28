package com.android.launcher3.accessibility;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.view.View;
import com.android.launcher3.AppInfo;
import com.android.launcher3.CellLayout;
import com.android.launcher3.FolderInfo;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.dragndrop.DragLayer;
import java.util.Iterator;

public class WorkspaceAccessibilityHelper extends DragAndDropAccessibilityDelegate {
    private final int[] mTempCords = new int[2];
    private final Rect mTempRect = new Rect();

    public WorkspaceAccessibilityHelper(CellLayout layout) {
        super(layout);
    }

    /* access modifiers changed from: protected */
    public int intersectsValidDropTarget(int id) {
        int mCountX = this.mView.getCountX();
        int mCountY = this.mView.getCountY();
        int x = id % mCountX;
        int y = id / mCountX;
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        if (dragInfo.dragType == LauncherAccessibilityDelegate.DragType.WIDGET && !this.mView.acceptsWidget()) {
            return -1;
        }
        if (dragInfo.dragType == LauncherAccessibilityDelegate.DragType.WIDGET) {
            int spanX = dragInfo.info.spanX;
            int spanY = dragInfo.info.spanY;
            boolean fits = false;
            int m = 0;
            while (m < spanX) {
                boolean fits2 = fits;
                for (int n = 0; n < spanY; n++) {
                    fits2 = true;
                    int x0 = x - m;
                    int y0 = y - n;
                    if (x0 >= 0 && y0 >= 0) {
                        boolean fits3 = true;
                        for (int i = x0; i < x0 + spanX && fits3; i++) {
                            int j = y0;
                            while (true) {
                                if (j >= y0 + spanY) {
                                    break;
                                } else if (i >= mCountX || j >= mCountY || this.mView.isOccupied(i, j)) {
                                    fits3 = false;
                                } else {
                                    j++;
                                }
                            }
                            fits3 = false;
                        }
                        if (fits3) {
                            return (mCountX * y0) + x0;
                        }
                        fits2 = fits3;
                    }
                }
                m++;
                fits = fits2;
            }
            return -1;
        }
        View child = this.mView.getChildAt(x, y);
        if (child == null || child == dragInfo.item) {
            return id;
        }
        if (dragInfo.dragType == LauncherAccessibilityDelegate.DragType.FOLDER) {
            return -1;
        }
        ItemInfo info = (ItemInfo) child.getTag();
        if ((info instanceof AppInfo) || (info instanceof FolderInfo) || (info instanceof ShortcutInfo)) {
            return id;
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public String getConfirmationForIconDrop(int id) {
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        View child = this.mView.getChildAt(id % this.mView.getCountX(), id / this.mView.getCountX());
        if (child == null || child == dragInfo.item) {
            return this.mContext.getString(R.string.item_moved);
        }
        ItemInfo info = (ItemInfo) child.getTag();
        if ((info instanceof AppInfo) || (info instanceof ShortcutInfo)) {
            return this.mContext.getString(R.string.folder_created);
        }
        if (info instanceof FolderInfo) {
            return this.mContext.getString(R.string.added_to_folder);
        }
        return "";
    }

    /* access modifiers changed from: protected */
    public void onPopulateNodeForVirtualView(int id, AccessibilityNodeInfoCompat node) {
        super.onPopulateNodeForVirtualView(id, node);
        DragLayer dragLayer = Launcher.getLauncher(this.mView.getContext()).getDragLayer();
        int[] iArr = this.mTempCords;
        this.mTempCords[1] = 0;
        iArr[0] = 0;
        float scale = dragLayer.getDescendantCoordRelativeToSelf(this.mView, this.mTempCords);
        node.getBoundsInParent(this.mTempRect);
        this.mTempRect.left = this.mTempCords[0] + ((int) (((float) this.mTempRect.left) * scale));
        this.mTempRect.right = this.mTempCords[0] + ((int) (((float) this.mTempRect.right) * scale));
        this.mTempRect.top = this.mTempCords[1] + ((int) (((float) this.mTempRect.top) * scale));
        this.mTempRect.bottom = this.mTempCords[1] + ((int) (((float) this.mTempRect.bottom) * scale));
        node.setBoundsInScreen(this.mTempRect);
    }

    /* access modifiers changed from: protected */
    public String getLocationDescriptionForIconDrop(int id) {
        int x = id % this.mView.getCountX();
        int y = id / this.mView.getCountX();
        LauncherAccessibilityDelegate.DragInfo dragInfo = this.mDelegate.getDragInfo();
        View child = this.mView.getChildAt(x, y);
        if (child == null || child == dragInfo.item) {
            return this.mView.getItemMoveDescription(x, y);
        }
        return getDescriptionForDropOver(child, this.mContext);
    }

    public static String getDescriptionForDropOver(View overChild, Context context) {
        ItemInfo info = (ItemInfo) overChild.getTag();
        if (info instanceof ShortcutInfo) {
            return context.getString(R.string.create_folder_with, new Object[]{info.title});
        } else if (!(info instanceof FolderInfo)) {
            return "";
        } else {
            if (TextUtils.isEmpty(info.title)) {
                ShortcutInfo firstItem = null;
                Iterator<ShortcutInfo> it = ((FolderInfo) info).contents.iterator();
                while (it.hasNext()) {
                    ShortcutInfo shortcut = it.next();
                    if (firstItem == null || firstItem.rank > shortcut.rank) {
                        firstItem = shortcut;
                    }
                }
                if (firstItem != null) {
                    return context.getString(R.string.add_to_folder_with_app, new Object[]{firstItem.title});
                }
            }
            return context.getString(R.string.add_to_folder, new Object[]{info.title});
        }
    }
}
