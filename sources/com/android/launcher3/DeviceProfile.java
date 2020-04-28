package com.android.launcher3;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.android.launcher3.badge.BadgeRenderer;
import com.android.launcher3.graphics.IconNormalizer;

public class DeviceProfile {
    private static final float MAX_HORIZONTAL_PADDING_PERCENT = 0.14f;
    private static final int PORTRAIT_TABLET_LEFT_RIGHT_PADDING_MULTIPLIER = 4;
    private static final float TALL_DEVICE_ASPECT_RATIO_THRESHOLD = 2.0f;
    public int allAppsCellHeightPx;
    public int allAppsIconDrawablePaddingPx;
    public int allAppsIconSizePx;
    public float allAppsIconTextSizePx;
    public final PointF appWidgetScale = new PointF(1.0f, 1.0f);
    public final int availableHeightPx;
    public final int availableWidthPx;
    public int cellHeightPx;
    public final int cellLayoutBottomPaddingPx;
    public final int cellLayoutPaddingLeftRightPx;
    public int cellWidthPx;
    public final int defaultPageSpacingPx;
    public final Rect defaultWidgetPadding;
    public final int desiredWorkspaceLeftRightMarginPx;
    public int dropTargetBarSizePx;
    public final int edgeMarginPx;
    public int folderCellHeightPx;
    public int folderCellWidthPx;
    public int folderChildDrawablePaddingPx;
    public int folderChildIconSizePx;
    public int folderChildTextSizePx;
    public int folderIconOffsetYPx;
    public int folderIconSizePx;
    public final int heightPx;
    public int hotseatBarBottomPaddingPx;
    public final int hotseatBarSidePaddingEndPx;
    public final int hotseatBarSidePaddingStartPx;
    public int hotseatBarSizePx;
    public final int hotseatBarTopPaddingPx;
    public int hotseatCellHeightPx;
    public int iconDrawablePaddingOriginalPx;
    public int iconDrawablePaddingPx;
    public int iconSizePx;
    public int iconTextSizePx;
    public final InvariantDeviceProfile inv;
    public final boolean isLandscape;
    public final boolean isLargeTablet;
    public final boolean isMultiWindowMode;
    public final boolean isPhone;
    public final boolean isTablet;
    public BadgeRenderer mBadgeRenderer;
    private final Rect mHotseatPadding = new Rect();
    private final Rect mInsets = new Rect();
    private boolean mIsSeascape;
    private final int topWorkspacePadding;
    public final boolean transposeLayoutWithOrientation;
    private final int verticalDragHandleOverlapWorkspace;
    public final int verticalDragHandleSizePx;
    public final int widthPx;
    public int workspaceCellPaddingXPx;
    public final Rect workspacePadding = new Rect();
    public float workspaceSpringLoadShrinkFactor;
    public final int workspaceSpringLoadedBottomSpace;

    public interface OnDeviceProfileChangeListener {
        void onDeviceProfileChanged(DeviceProfile deviceProfile);
    }

    public DeviceProfile(Context context, InvariantDeviceProfile inv2, Point minSize, Point maxSize, int width, int height, boolean isLandscape2, boolean isMultiWindowMode2) {
        int i;
        InvariantDeviceProfile invariantDeviceProfile = inv2;
        Point point = minSize;
        Point point2 = maxSize;
        boolean z = isLandscape2;
        boolean z2 = isMultiWindowMode2;
        this.inv = invariantDeviceProfile;
        this.isLandscape = z;
        this.isMultiWindowMode = z2;
        this.widthPx = width;
        this.heightPx = height;
        if (z) {
            this.availableWidthPx = point2.x;
            this.availableHeightPx = point.y;
        } else {
            this.availableWidthPx = point.x;
            this.availableHeightPx = point2.y;
        }
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        this.isTablet = res.getBoolean(R.bool.is_tablet);
        this.isLargeTablet = res.getBoolean(R.bool.is_large_tablet);
        this.isPhone = !this.isTablet && !this.isLargeTablet;
        boolean isTallDevice = Float.compare(((float) Math.max(this.widthPx, this.heightPx)) / ((float) Math.min(this.widthPx, this.heightPx)), TALL_DEVICE_ASPECT_RATIO_THRESHOLD) >= 0;
        this.transposeLayoutWithOrientation = res.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
        Context context2 = getContext(context, isVerticalBarLayout() ? 2 : 1);
        Resources res2 = context2.getResources();
        this.defaultWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context2, new ComponentName(context2.getPackageName(), getClass().getName()), (Rect) null);
        this.edgeMarginPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin);
        this.desiredWorkspaceLeftRightMarginPx = isVerticalBarLayout() ? 0 : this.edgeMarginPx;
        this.cellLayoutPaddingLeftRightPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_cell_layout_padding) * ((isVerticalBarLayout() || !this.isTablet) ? 1 : 4);
        this.cellLayoutBottomPaddingPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_cell_layout_bottom_padding);
        this.verticalDragHandleSizePx = res2.getDimensionPixelSize(R.dimen.vertical_drag_handle_size);
        this.verticalDragHandleOverlapWorkspace = res2.getDimensionPixelSize(R.dimen.vertical_drag_handle_overlap_workspace);
        this.defaultPageSpacingPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_workspace_page_spacing);
        this.topWorkspacePadding = res2.getDimensionPixelSize(R.dimen.dynamic_grid_workspace_top_padding);
        this.iconDrawablePaddingOriginalPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_icon_drawable_padding);
        this.dropTargetBarSizePx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_drop_target_size);
        this.workspaceSpringLoadedBottomSpace = res2.getDimensionPixelSize(R.dimen.dynamic_grid_min_spring_loaded_space);
        this.workspaceCellPaddingXPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_cell_padding_x);
        this.hotseatBarTopPaddingPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_top_padding);
        this.hotseatBarBottomPaddingPx = (isTallDevice ? 0 : res2.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_bottom_non_tall_padding)) + res2.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_bottom_padding);
        this.hotseatBarSidePaddingEndPx = res2.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_side_padding);
        this.hotseatBarSidePaddingStartPx = (!z2 || !isVerticalBarLayout()) ? 0 : this.edgeMarginPx;
        if (isVerticalBarLayout()) {
            i = Utilities.pxFromDp(invariantDeviceProfile.iconSize, dm) + this.hotseatBarSidePaddingStartPx + this.hotseatBarSidePaddingEndPx;
        } else {
            i = res2.getDimensionPixelSize(R.dimen.dynamic_grid_hotseat_size) + this.hotseatBarTopPaddingPx + this.hotseatBarBottomPaddingPx;
        }
        this.hotseatBarSizePx = i;
        updateAvailableDimensions(dm, res2);
        if (!isVerticalBarLayout() && this.isPhone && isTallDevice) {
            int extraSpace = ((getCellSize().y - this.iconSizePx) - (this.iconDrawablePaddingPx * 2)) - this.verticalDragHandleSizePx;
            this.hotseatBarSizePx += extraSpace;
            this.hotseatBarBottomPaddingPx += extraSpace;
            updateAvailableDimensions(dm, res2);
        }
        updateWorkspacePadding();
        this.mBadgeRenderer = new BadgeRenderer(this.iconSizePx);
    }

    public DeviceProfile copy(Context context) {
        Point size = new Point(this.availableWidthPx, this.availableHeightPx);
        return new DeviceProfile(context, this.inv, size, size, this.widthPx, this.heightPx, this.isLandscape, this.isMultiWindowMode);
    }

    public DeviceProfile getMultiWindowProfile(Context context, Point mwSize) {
        mwSize.set(Math.min(this.availableWidthPx, mwSize.x), Math.min(this.availableHeightPx, mwSize.y));
        DeviceProfile deviceProfile = new DeviceProfile(context, this.inv, mwSize, mwSize, mwSize.x, mwSize.y, this.isLandscape, true);
        if (((float) (((deviceProfile.getCellSize().y - deviceProfile.iconSizePx) - this.iconDrawablePaddingPx) - deviceProfile.iconTextSizePx)) < ((float) (deviceProfile.iconDrawablePaddingPx * 2))) {
            deviceProfile.adjustToHideWorkspaceLabels();
        }
        deviceProfile.appWidgetScale.set(((float) deviceProfile.getCellSize().x) / ((float) getCellSize().x), ((float) deviceProfile.getCellSize().y) / ((float) getCellSize().y));
        deviceProfile.updateWorkspacePadding();
        return deviceProfile;
    }

    public DeviceProfile getFullScreenProfile() {
        return this.isLandscape ? this.inv.landscapeProfile : this.inv.portraitProfile;
    }

    private void adjustToHideWorkspaceLabels() {
        this.iconTextSizePx = 0;
        this.iconDrawablePaddingPx = 0;
        this.cellHeightPx = this.iconSizePx;
        this.allAppsCellHeightPx = this.allAppsIconSizePx + this.allAppsIconDrawablePaddingPx + Utilities.calculateTextHeight(this.allAppsIconTextSizePx) + (this.allAppsIconDrawablePaddingPx * (isVerticalBarLayout() ? 2 : 1) * 2);
    }

    private void updateAvailableDimensions(DisplayMetrics dm, Resources res) {
        updateIconSize(1.0f, res, dm);
        float usedHeight = (float) (this.cellHeightPx * this.inv.numRows);
        int maxHeight = this.availableHeightPx - getTotalWorkspacePadding().y;
        if (usedHeight > ((float) maxHeight)) {
            updateIconSize(((float) maxHeight) / usedHeight, res, dm);
        }
        updateAvailableFolderCellDimensions(dm, res);
    }

    private void updateIconSize(float scale, Resources res, DisplayMetrics dm) {
        boolean isVerticalLayout = isVerticalBarLayout();
        this.iconSizePx = (int) (((float) Utilities.pxFromDp(isVerticalLayout ? this.inv.landscapeIconSize : this.inv.iconSize, dm)) * scale);
        this.iconTextSizePx = (int) (((float) Utilities.pxFromSp(this.inv.iconTextSize, dm)) * scale);
        this.iconDrawablePaddingPx = (int) (((float) this.iconDrawablePaddingOriginalPx) * scale);
        this.cellHeightPx = this.iconSizePx + this.iconDrawablePaddingPx + Utilities.calculateTextHeight((float) this.iconTextSizePx);
        int cellYPadding = (getCellSize().y - this.cellHeightPx) / 2;
        if (this.iconDrawablePaddingPx > cellYPadding && !isVerticalLayout && !this.isMultiWindowMode) {
            this.cellHeightPx -= this.iconDrawablePaddingPx - cellYPadding;
            this.iconDrawablePaddingPx = cellYPadding;
        }
        this.cellWidthPx = this.iconSizePx + this.iconDrawablePaddingPx;
        this.allAppsIconTextSizePx = (float) this.iconTextSizePx;
        this.allAppsIconSizePx = this.iconSizePx;
        this.allAppsIconDrawablePaddingPx = this.iconDrawablePaddingPx;
        this.allAppsCellHeightPx = getCellSize().y;
        if (isVerticalLayout) {
            adjustToHideWorkspaceLabels();
        }
        if (isVerticalLayout) {
            this.hotseatBarSizePx = this.iconSizePx + this.hotseatBarSidePaddingStartPx + this.hotseatBarSidePaddingEndPx;
        }
        this.hotseatCellHeightPx = this.iconSizePx;
        if (!isVerticalLayout) {
            this.workspaceSpringLoadShrinkFactor = Math.min(((float) res.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage)) / 100.0f, 1.0f - (((float) (this.dropTargetBarSizePx + this.workspaceSpringLoadedBottomSpace)) / ((float) (((this.availableHeightPx - this.hotseatBarSizePx) - this.verticalDragHandleSizePx) - this.topWorkspacePadding))));
        } else {
            this.workspaceSpringLoadShrinkFactor = ((float) res.getInteger(R.integer.config_workspaceSpringLoadShrinkPercentage)) / 100.0f;
        }
        this.folderIconSizePx = IconNormalizer.getNormalizedCircleSize(this.iconSizePx);
        this.folderIconOffsetYPx = (this.iconSizePx - this.folderIconSizePx) / 2;
    }

    private void updateAvailableFolderCellDimensions(DisplayMetrics dm, Resources res) {
        updateFolderCellSize(1.0f, dm, res);
        int folderMargin = this.edgeMarginPx;
        Point totalWorkspacePadding = getTotalWorkspacePadding();
        int i = this.folderCellHeightPx * this.inv.numFolderRows;
        float f = (float) ((this.availableHeightPx - totalWorkspacePadding.y) - folderMargin);
        float scale = Math.min(((float) ((this.availableWidthPx - totalWorkspacePadding.x) - folderMargin)) / ((float) (this.folderCellWidthPx * this.inv.numFolderColumns)), f / ((float) (i + ((res.getDimensionPixelSize(R.dimen.folder_label_padding_top) + res.getDimensionPixelSize(R.dimen.folder_label_padding_bottom)) + Utilities.calculateTextHeight(res.getDimension(R.dimen.folder_label_text_size))))));
        if (scale < 1.0f) {
            updateFolderCellSize(scale, dm, res);
        }
    }

    private void updateFolderCellSize(float scale, DisplayMetrics dm, Resources res) {
        this.folderChildIconSizePx = (int) (((float) Utilities.pxFromDp(this.inv.iconSize, dm)) * scale);
        this.folderChildTextSizePx = (int) (((float) res.getDimensionPixelSize(R.dimen.folder_child_text_size)) * scale);
        int textHeight = Utilities.calculateTextHeight((float) this.folderChildTextSizePx);
        this.folderCellWidthPx = this.folderChildIconSizePx + (((int) (((float) res.getDimensionPixelSize(R.dimen.folder_cell_x_padding)) * scale)) * 2);
        this.folderCellHeightPx = this.folderChildIconSizePx + (((int) (((float) res.getDimensionPixelSize(R.dimen.folder_cell_y_padding)) * scale)) * 2) + textHeight;
        this.folderChildDrawablePaddingPx = Math.max(0, ((this.folderCellHeightPx - this.folderChildIconSizePx) - textHeight) / 3);
    }

    public void updateInsets(Rect insets) {
        this.mInsets.set(insets);
        updateWorkspacePadding();
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    public Point getCellSize() {
        Point result = new Point();
        Point padding = getTotalWorkspacePadding();
        result.x = calculateCellWidth((this.availableWidthPx - padding.x) - (this.cellLayoutPaddingLeftRightPx * 2), this.inv.numColumns);
        result.y = calculateCellHeight((this.availableHeightPx - padding.y) - this.cellLayoutBottomPaddingPx, this.inv.numRows);
        return result;
    }

    public Point getTotalWorkspacePadding() {
        updateWorkspacePadding();
        return new Point(this.workspacePadding.left + this.workspacePadding.right, this.workspacePadding.top + this.workspacePadding.bottom);
    }

    private void updateWorkspacePadding() {
        Rect padding = this.workspacePadding;
        if (isVerticalBarLayout()) {
            padding.top = 0;
            padding.bottom = this.edgeMarginPx;
            if (isSeascape()) {
                padding.left = this.hotseatBarSizePx;
                padding.right = this.verticalDragHandleSizePx;
                return;
            }
            padding.left = this.verticalDragHandleSizePx;
            padding.right = this.hotseatBarSizePx;
            return;
        }
        int paddingBottom = (this.hotseatBarSizePx + this.verticalDragHandleSizePx) - this.verticalDragHandleOverlapWorkspace;
        if (this.isTablet) {
            int availablePaddingX = (int) Math.min((float) Math.max(0, this.widthPx - ((this.inv.numColumns * this.cellWidthPx) + ((this.inv.numColumns - 1) * this.cellWidthPx))), ((float) this.widthPx) * MAX_HORIZONTAL_PADDING_PERCENT);
            int availablePaddingY = Math.max(0, ((((this.heightPx - this.topWorkspacePadding) - paddingBottom) - ((this.inv.numRows * 2) * this.cellHeightPx)) - this.hotseatBarTopPaddingPx) - this.hotseatBarBottomPaddingPx);
            padding.set(availablePaddingX / 2, this.topWorkspacePadding + (availablePaddingY / 2), availablePaddingX / 2, (availablePaddingY / 2) + paddingBottom);
            return;
        }
        padding.set(this.desiredWorkspaceLeftRightMarginPx, this.topWorkspacePadding, this.desiredWorkspaceLeftRightMarginPx, paddingBottom);
    }

    public Rect getHotseatLayoutPadding() {
        if (!isVerticalBarLayout()) {
            int hotseatAdjustment = Math.round(((((float) this.widthPx) / ((float) this.inv.numColumns)) - (((float) this.widthPx) / ((float) this.inv.numHotseatIcons))) / TALL_DEVICE_ASPECT_RATIO_THRESHOLD);
            this.mHotseatPadding.set(this.workspacePadding.left + hotseatAdjustment + this.cellLayoutPaddingLeftRightPx, this.hotseatBarTopPaddingPx, this.workspacePadding.right + hotseatAdjustment + this.cellLayoutPaddingLeftRightPx, this.hotseatBarBottomPaddingPx + this.mInsets.bottom + this.cellLayoutBottomPaddingPx);
        } else if (isSeascape()) {
            this.mHotseatPadding.set(this.mInsets.left + this.hotseatBarSidePaddingStartPx, this.mInsets.top, this.hotseatBarSidePaddingEndPx, this.mInsets.bottom);
        } else {
            this.mHotseatPadding.set(this.hotseatBarSidePaddingEndPx, this.mInsets.top, this.mInsets.right + this.hotseatBarSidePaddingStartPx, this.mInsets.bottom);
        }
        return this.mHotseatPadding;
    }

    public Rect getAbsoluteOpenFolderBounds() {
        if (isVerticalBarLayout()) {
            return new Rect(this.mInsets.left + this.dropTargetBarSizePx + this.edgeMarginPx, this.mInsets.top, ((this.mInsets.left + this.availableWidthPx) - this.hotseatBarSizePx) - this.edgeMarginPx, this.mInsets.top + this.availableHeightPx);
        }
        return new Rect(this.mInsets.left + this.edgeMarginPx, this.mInsets.top + this.dropTargetBarSizePx + this.edgeMarginPx, (this.mInsets.left + this.availableWidthPx) - this.edgeMarginPx, (((this.mInsets.top + this.availableHeightPx) - this.hotseatBarSizePx) - this.verticalDragHandleSizePx) - this.edgeMarginPx);
    }

    public static int calculateCellWidth(int width, int countX) {
        return width / countX;
    }

    public static int calculateCellHeight(int height, int countY) {
        return height / countY;
    }

    public boolean isVerticalBarLayout() {
        return this.isLandscape && this.transposeLayoutWithOrientation;
    }

    public boolean updateIsSeascape(WindowManager wm) {
        if (isVerticalBarLayout()) {
            boolean isSeascape = wm.getDefaultDisplay().getRotation() == 3;
            if (this.mIsSeascape != isSeascape) {
                this.mIsSeascape = isSeascape;
                return true;
            }
        }
        return false;
    }

    public boolean isSeascape() {
        return isVerticalBarLayout() && this.mIsSeascape;
    }

    public boolean shouldFadeAdjacentWorkspaceScreens() {
        return isVerticalBarLayout() || this.isLargeTablet;
    }

    public int getCellHeight(int containerType) {
        switch (containerType) {
            case 0:
                return this.cellHeightPx;
            case 1:
                return this.hotseatCellHeightPx;
            case 2:
                return this.folderCellHeightPx;
            default:
                return 0;
        }
    }

    private static Context getContext(Context c, int orientation) {
        Configuration context = new Configuration(c.getResources().getConfiguration());
        context.orientation = orientation;
        return c.createConfigurationContext(context);
    }
}
