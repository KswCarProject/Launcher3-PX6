package com.android.launcher3.folder;

import com.android.launcher3.FolderInfo;
import com.android.launcher3.InvariantDeviceProfile;

public class FolderIconPreviewVerifier {
    private boolean mDisplayingUpperLeftQuadrant = false;
    private int mGridCountX;
    private final int[] mGridSize = new int[2];
    private final int mMaxGridCountX;
    private final int mMaxGridCountY;
    private final int mMaxItemsPerPage;

    public FolderIconPreviewVerifier(InvariantDeviceProfile profile) {
        this.mMaxGridCountX = profile.numFolderColumns;
        this.mMaxGridCountY = profile.numFolderRows;
        this.mMaxItemsPerPage = this.mMaxGridCountX * this.mMaxGridCountY;
    }

    public void setFolderInfo(FolderInfo info) {
        int numItemsInFolder = info.contents.size();
        boolean z = false;
        FolderPagedView.calculateGridSize(numItemsInFolder, 0, 0, this.mMaxGridCountX, this.mMaxGridCountY, this.mMaxItemsPerPage, this.mGridSize);
        this.mGridCountX = this.mGridSize[0];
        if (numItemsInFolder > 4) {
            z = true;
        }
        this.mDisplayingUpperLeftQuadrant = z;
    }

    public boolean isItemInPreview(int rank) {
        return isItemInPreview(0, rank);
    }

    public boolean isItemInPreview(int page, int rank) {
        if (page > 0 || this.mDisplayingUpperLeftQuadrant) {
            int col = rank % this.mGridCountX;
            int row = rank / this.mGridCountX;
            if (col >= 2 || row >= 2) {
                return false;
            }
            return true;
        } else if (rank < 4) {
            return true;
        } else {
            return false;
        }
    }
}
