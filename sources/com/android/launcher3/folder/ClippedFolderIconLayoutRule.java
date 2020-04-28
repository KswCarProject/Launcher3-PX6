package com.android.launcher3.folder;

public class ClippedFolderIconLayoutRule {
    public static final int ENTER_INDEX = -3;
    public static final int EXIT_INDEX = -2;
    private static final float ITEM_RADIUS_SCALE_FACTOR = 1.33f;
    public static final int MAX_NUM_ITEMS_IN_PREVIEW = 4;
    private static final float MAX_RADIUS_DILATION = 0.15f;
    private static final float MAX_SCALE = 0.58f;
    private static final int MIN_NUM_ITEMS_IN_PREVIEW = 2;
    private static final float MIN_SCALE = 0.48f;
    private float mAvailableSpace;
    private float mBaselineIconScale;
    private float mIconSize;
    private boolean mIsRtl;
    private float mRadius;
    private float[] mTmpPoint = new float[2];

    public void init(int availableSpace, float intrinsicIconSize, boolean rtl) {
        this.mAvailableSpace = (float) availableSpace;
        this.mRadius = (((float) availableSpace) * ITEM_RADIUS_SCALE_FACTOR) / 2.0f;
        this.mIconSize = intrinsicIconSize;
        this.mIsRtl = rtl;
        this.mBaselineIconScale = ((float) availableSpace) / (1.0f * intrinsicIconSize);
    }

    public PreviewItemDrawingParams computePreviewItemDrawingParams(int index, int curNumItems, PreviewItemDrawingParams params) {
        float totalScale = scaleForItem(curNumItems);
        if (index == -2) {
            getGridPosition(0, 2, this.mTmpPoint);
        } else if (index == -3) {
            getGridPosition(1, 2, this.mTmpPoint);
        } else if (index >= 4) {
            float[] fArr = this.mTmpPoint;
            float[] fArr2 = this.mTmpPoint;
            float f = (this.mAvailableSpace / 2.0f) - ((this.mIconSize * totalScale) / 2.0f);
            fArr2[1] = f;
            fArr[0] = f;
        } else {
            getPosition(index, curNumItems, this.mTmpPoint);
        }
        float transX = this.mTmpPoint[0];
        float transY = this.mTmpPoint[1];
        if (params == null) {
            return new PreviewItemDrawingParams(transX, transY, totalScale, 0.0f);
        }
        params.update(transX, transY, totalScale);
        params.overlayAlpha = 0.0f;
        return params;
    }

    private void getGridPosition(int row, int col, float[] result) {
        getPosition(0, 4, result);
        float left = result[0];
        float top = result[1];
        getPosition(3, 4, result);
        result[0] = (((float) col) * (result[0] - left)) + left;
        result[1] = (((float) row) * (result[1] - top)) + top;
    }

    private void getPosition(int index, int curNumItems, float[] result) {
        int index2 = index;
        int curNumItems2 = Math.max(curNumItems, 2);
        double theta0 = this.mIsRtl ? 0.0d : 3.141592653589793d;
        int direction = this.mIsRtl ? 1 : -1;
        double thetaShift = 0.0d;
        if (curNumItems2 == 3) {
            thetaShift = 0.5235987755982988d;
        } else if (curNumItems2 == 4) {
            thetaShift = 0.7853981633974483d;
        }
        double theta02 = theta0 + (((double) direction) * thetaShift);
        if (curNumItems2 == 4 && index2 == 3) {
            index2 = 2;
        } else if (curNumItems2 == 4 && index2 == 2) {
            index2 = 3;
        }
        float radius = this.mRadius * (((((float) (curNumItems2 - 2)) * MAX_RADIUS_DILATION) / 2.0f) + 1.0f);
        double d = thetaShift;
        double theta = (((double) index2) * (6.283185307179586d / ((double) curNumItems2)) * ((double) direction)) + theta02;
        float halfIconSize = (this.mIconSize * scaleForItem(curNumItems2)) / 2.0f;
        result[0] = ((this.mAvailableSpace / 2.0f) + ((float) ((((double) radius) * Math.cos(theta)) / 2.0d))) - halfIconSize;
        result[1] = ((this.mAvailableSpace / 2.0f) + ((float) ((((double) (-radius)) * Math.sin(theta)) / 2.0d))) - halfIconSize;
    }

    public float scaleForItem(int numItems) {
        float scale;
        if (numItems <= 2) {
            scale = MAX_SCALE;
        } else if (numItems == 3) {
            scale = 0.53f;
        } else {
            scale = MIN_SCALE;
        }
        return this.mBaselineIconScale * scale;
    }

    public float getIconSize() {
        return this.mIconSize;
    }
}
