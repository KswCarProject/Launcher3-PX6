package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.internal.view.SupportMenu;
import com.android.launcher3.LauncherAppState;
import java.nio.ByteBuffer;

public class IconNormalizer {
    private static final float BOUND_RATIO_MARGIN = 0.05f;
    private static final float CIRCLE_AREA_BY_RECT = 0.7853982f;
    private static final boolean DEBUG = false;
    public static final float ICON_VISIBLE_AREA_FACTOR = 0.92f;
    private static final float LINEAR_SCALE_SLOPE = 0.040449437f;
    private static final float MAX_CIRCLE_AREA_FACTOR = 0.6597222f;
    private static final float MAX_SQUARE_AREA_FACTOR = 0.6510417f;
    private static final int MIN_VISIBLE_ALPHA = 40;
    private static final float PIXEL_DIFF_PERCENTAGE_THRESHOLD = 0.005f;
    private static final float SCALE_NOT_INITIALIZED = 0.0f;
    private static final String TAG = "IconNormalizer";
    private final Rect mAdaptiveIconBounds = new Rect();
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap = Bitmap.createBitmap(this.mMaxSize, this.mMaxSize, Bitmap.Config.ALPHA_8);
    private final Rect mBounds = new Rect();
    private final Canvas mCanvas = new Canvas(this.mBitmap);
    private final float[] mLeftBorder = new float[this.mMaxSize];
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape = new Paint();
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels = new byte[(this.mMaxSize * this.mMaxSize)];
    private final float[] mRightBorder = new float[this.mMaxSize];
    private final Path mShapePath;

    IconNormalizer(Context context) {
        this.mMaxSize = LauncherAppState.getIDP(context).iconBitmapSize * 2;
        this.mPaintMaskShape.setColor(SupportMenu.CATEGORY_MASK);
        this.mPaintMaskShape.setStyle(Paint.Style.FILL);
        this.mPaintMaskShape.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        this.mPaintMaskShapeOutline = new Paint();
        this.mPaintMaskShapeOutline.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Paint.Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
    }

    private boolean isShape(Path maskPath) {
        if (Math.abs((((float) this.mBounds.width()) / ((float) this.mBounds.height())) - 1.0f) > BOUND_RATIO_MARGIN) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale((float) this.mBounds.width(), (float) this.mBounds.height());
        this.mMatrix.postTranslate((float) this.mBounds.left, (float) this.mBounds.top);
        maskPath.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        ByteBuffer buffer = ByteBuffer.wrap(this.mPixels);
        buffer.rewind();
        this.mBitmap.copyPixelsToBuffer(buffer);
        int y = this.mBounds.top;
        int rowSizeDiff = this.mMaxSize - this.mBounds.right;
        int index = this.mMaxSize * y;
        int sum = 0;
        for (int y2 = y; y2 < this.mBounds.bottom; y2++) {
            int index2 = index + this.mBounds.left;
            for (int x = this.mBounds.left; x < this.mBounds.right; x++) {
                if ((this.mPixels[index2] & 255) > 40) {
                    sum++;
                }
                index2++;
            }
            index = index2 + rowSizeDiff;
        }
        if (((float) sum) / ((float) (this.mBounds.width() * this.mBounds.height())) < PIXEL_DIFF_PERCENTAGE_THRESHOLD) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01ef, code lost:
        return 1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x01e7, code lost:
        return r2;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0069  */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01e8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized float getScale(@android.support.annotation.NonNull android.graphics.drawable.Drawable r26, @android.support.annotation.Nullable android.graphics.RectF r27, @android.support.annotation.Nullable android.graphics.Path r28, @android.support.annotation.Nullable boolean[] r29) {
        /*
            r25 = this;
            r1 = r25
            r0 = r26
            r2 = r27
            r3 = r29
            monitor-enter(r25)
            boolean r4 = com.android.launcher3.Utilities.ATLEAST_OREO     // Catch:{ all -> 0x01f2 }
            r5 = 0
            if (r4 == 0) goto L_0x0035
            boolean r4 = r0 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x01f2 }
            if (r4 == 0) goto L_0x0035
            float r4 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x01f2 }
            int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r4 == 0) goto L_0x0023
            if (r2 == 0) goto L_0x001f
            android.graphics.Rect r4 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x01f2 }
            r2.set(r4)     // Catch:{ all -> 0x01f2 }
        L_0x001f:
            float r4 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x01f2 }
            monitor-exit(r25)
            return r4
        L_0x0023:
            boolean r4 = r0 instanceof com.android.launcher3.dragndrop.FolderAdaptiveIcon     // Catch:{ all -> 0x01f2 }
            if (r4 == 0) goto L_0x0035
            android.graphics.drawable.AdaptiveIconDrawable r4 = new android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x01f2 }
            android.graphics.drawable.ColorDrawable r6 = new android.graphics.drawable.ColorDrawable     // Catch:{ all -> 0x01f2 }
            r7 = -16777216(0xffffffffff000000, float:-1.7014118E38)
            r6.<init>(r7)     // Catch:{ all -> 0x01f2 }
            r7 = 0
            r4.<init>(r6, r7)     // Catch:{ all -> 0x01f2 }
            r0 = r4
        L_0x0035:
            int r4 = r0.getIntrinsicWidth()     // Catch:{ all -> 0x01f2 }
            int r6 = r0.getIntrinsicHeight()     // Catch:{ all -> 0x01f2 }
            if (r4 <= 0) goto L_0x005b
            if (r6 > 0) goto L_0x0042
            goto L_0x005b
        L_0x0042:
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            if (r4 > r7) goto L_0x004a
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            if (r6 <= r7) goto L_0x0073
        L_0x004a:
            int r7 = java.lang.Math.max(r4, r6)     // Catch:{ all -> 0x01f2 }
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            int r8 = r8 * r4
            int r8 = r8 / r7
            r4 = r8
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            int r8 = r8 * r6
            int r8 = r8 / r7
            r6 = r8
            goto L_0x0073
        L_0x005b:
            if (r4 <= 0) goto L_0x0064
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            if (r4 <= r7) goto L_0x0062
            goto L_0x0064
        L_0x0062:
            r7 = r4
            goto L_0x0066
        L_0x0064:
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
        L_0x0066:
            r4 = r7
            if (r6 <= 0) goto L_0x0070
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            if (r6 <= r7) goto L_0x006e
            goto L_0x0070
        L_0x006e:
            r7 = r6
            goto L_0x0072
        L_0x0070:
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
        L_0x0072:
            r6 = r7
        L_0x0073:
            android.graphics.Bitmap r7 = r1.mBitmap     // Catch:{ all -> 0x01f2 }
            r8 = 0
            r7.eraseColor(r8)     // Catch:{ all -> 0x01f2 }
            r0.setBounds(r8, r8, r4, r6)     // Catch:{ all -> 0x01f2 }
            android.graphics.Canvas r7 = r1.mCanvas     // Catch:{ all -> 0x01f2 }
            r0.draw(r7)     // Catch:{ all -> 0x01f2 }
            byte[] r7 = r1.mPixels     // Catch:{ all -> 0x01f2 }
            java.nio.ByteBuffer r7 = java.nio.ByteBuffer.wrap(r7)     // Catch:{ all -> 0x01f2 }
            r7.rewind()     // Catch:{ all -> 0x01f2 }
            android.graphics.Bitmap r9 = r1.mBitmap     // Catch:{ all -> 0x01f2 }
            r9.copyPixelsToBuffer(r7)     // Catch:{ all -> 0x01f2 }
            r9 = -1
            r10 = -1
            int r11 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            r12 = 1
            int r11 = r11 + r12
            r13 = -1
            r14 = 0
            int r15 = r1.mMaxSize     // Catch:{ all -> 0x01f2 }
            int r15 = r15 - r4
            r16 = r14
            r14 = r11
            r11 = r10
            r10 = r9
            r9 = 0
        L_0x00a0:
            r5 = -1
            if (r9 >= r6) goto L_0x00ff
            r17 = r5
            r18 = r5
            r12 = r17
            r8 = r18
            r17 = r16
            r16 = 0
        L_0x00af:
            r19 = r16
            r5 = r19
            if (r5 >= r4) goto L_0x00d3
            r20 = r7
            byte[] r7 = r1.mPixels     // Catch:{ all -> 0x01f2 }
            byte r7 = r7[r17]     // Catch:{ all -> 0x01f2 }
            r7 = r7 & 255(0xff, float:3.57E-43)
            r21 = r0
            r0 = 40
            if (r7 <= r0) goto L_0x00c9
            r0 = -1
            if (r8 != r0) goto L_0x00c7
            r8 = r5
        L_0x00c7:
            r0 = r5
            r12 = r0
        L_0x00c9:
            int r17 = r17 + 1
            int r16 = r5 + 1
            r7 = r20
            r0 = r21
            r5 = -1
            goto L_0x00af
        L_0x00d3:
            r21 = r0
            r20 = r7
            int r16 = r17 + r15
            float[] r0 = r1.mLeftBorder     // Catch:{ all -> 0x01f2 }
            float r5 = (float) r8     // Catch:{ all -> 0x01f2 }
            r0[r9] = r5     // Catch:{ all -> 0x01f2 }
            float[] r0 = r1.mRightBorder     // Catch:{ all -> 0x01f2 }
            float r5 = (float) r12     // Catch:{ all -> 0x01f2 }
            r0[r9] = r5     // Catch:{ all -> 0x01f2 }
            r0 = -1
            if (r8 == r0) goto L_0x00f5
            r5 = r9
            if (r10 != r0) goto L_0x00ea
            r10 = r9
        L_0x00ea:
            int r0 = java.lang.Math.min(r14, r8)     // Catch:{ all -> 0x01f2 }
            int r7 = java.lang.Math.max(r13, r12)     // Catch:{ all -> 0x01f2 }
            r14 = r0
            r11 = r5
            r13 = r7
        L_0x00f5:
            int r9 = r9 + 1
            r7 = r20
            r0 = r21
            r5 = 0
            r8 = 0
            r12 = 1
            goto L_0x00a0
        L_0x00ff:
            r21 = r0
            r20 = r7
            r0 = 1065353216(0x3f800000, float:1.0)
            r5 = -1
            if (r10 == r5) goto L_0x01e8
            if (r13 != r5) goto L_0x0112
            r0 = r28
            r24 = r10
            r3 = r21
            goto L_0x01ee
        L_0x0112:
            float[] r5 = r1.mLeftBorder     // Catch:{ all -> 0x01f2 }
            r7 = 1
            convertToConvexArray(r5, r7, r10, r11)     // Catch:{ all -> 0x01f2 }
            float[] r5 = r1.mRightBorder     // Catch:{ all -> 0x01f2 }
            r7 = -1
            convertToConvexArray(r5, r7, r10, r11)     // Catch:{ all -> 0x01f2 }
            r5 = 0
            r7 = r5
            r5 = 0
        L_0x0121:
            if (r5 >= r6) goto L_0x013c
            float[] r8 = r1.mLeftBorder     // Catch:{ all -> 0x01f2 }
            r8 = r8[r5]     // Catch:{ all -> 0x01f2 }
            r9 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 > 0) goto L_0x012e
            goto L_0x0139
        L_0x012e:
            float[] r8 = r1.mRightBorder     // Catch:{ all -> 0x01f2 }
            r8 = r8[r5]     // Catch:{ all -> 0x01f2 }
            float[] r9 = r1.mLeftBorder     // Catch:{ all -> 0x01f2 }
            r9 = r9[r5]     // Catch:{ all -> 0x01f2 }
            float r8 = r8 - r9
            float r8 = r8 + r0
            float r7 = r7 + r8
        L_0x0139:
            int r5 = r5 + 1
            goto L_0x0121
        L_0x013c:
            int r5 = r11 + 1
            int r5 = r5 - r10
            int r8 = r13 + 1
            int r8 = r8 - r14
            int r5 = r5 * r8
            float r5 = (float) r5     // Catch:{ all -> 0x01f2 }
            float r8 = r7 / r5
            r9 = 1061752795(0x3f490fdb, float:0.7853982)
            int r9 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r9 >= 0) goto L_0x0152
            r9 = 1059644302(0x3f28e38e, float:0.6597222)
            goto L_0x015e
        L_0x0152:
            r9 = 1059498667(0x3f26aaab, float:0.6510417)
            r12 = 1025879631(0x3d25ae4f, float:0.040449437)
            float r17 = r0 - r8
            float r17 = r17 * r12
            float r9 = r17 + r9
        L_0x015e:
            android.graphics.Rect r12 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            r12.left = r14     // Catch:{ all -> 0x01f2 }
            android.graphics.Rect r12 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            r12.right = r13     // Catch:{ all -> 0x01f2 }
            android.graphics.Rect r12 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            r12.top = r10     // Catch:{ all -> 0x01f2 }
            android.graphics.Rect r12 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            r12.bottom = r11     // Catch:{ all -> 0x01f2 }
            if (r2 == 0) goto L_0x019e
            android.graphics.Rect r12 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            int r12 = r12.left     // Catch:{ all -> 0x01f2 }
            float r12 = (float) r12     // Catch:{ all -> 0x01f2 }
            float r0 = (float) r4     // Catch:{ all -> 0x01f2 }
            float r12 = r12 / r0
            android.graphics.Rect r0 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            int r0 = r0.top     // Catch:{ all -> 0x01f2 }
            float r0 = (float) r0     // Catch:{ all -> 0x01f2 }
            r22 = r5
            float r5 = (float) r6     // Catch:{ all -> 0x01f2 }
            float r0 = r0 / r5
            android.graphics.Rect r5 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            int r5 = r5.right     // Catch:{ all -> 0x01f2 }
            float r5 = (float) r5     // Catch:{ all -> 0x01f2 }
            r23 = r8
            float r8 = (float) r4     // Catch:{ all -> 0x01f2 }
            float r5 = r5 / r8
            r8 = 1065353216(0x3f800000, float:1.0)
            float r5 = r8 - r5
            android.graphics.Rect r8 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            int r8 = r8.bottom     // Catch:{ all -> 0x01f2 }
            float r8 = (float) r8     // Catch:{ all -> 0x01f2 }
            r24 = r10
            float r10 = (float) r6     // Catch:{ all -> 0x01f2 }
            float r8 = r8 / r10
            r10 = 1065353216(0x3f800000, float:1.0)
            float r8 = r10 - r8
            r2.set(r12, r0, r5, r8)     // Catch:{ all -> 0x01f2 }
            goto L_0x01a4
        L_0x019e:
            r22 = r5
            r23 = r8
            r24 = r10
        L_0x01a4:
            if (r3 == 0) goto L_0x01b3
            int r0 = r3.length     // Catch:{ all -> 0x01f2 }
            if (r0 <= 0) goto L_0x01b3
            r0 = r28
            boolean r5 = r1.isShape(r0)     // Catch:{ all -> 0x01f2 }
            r8 = 0
            r3[r8] = r5     // Catch:{ all -> 0x01f2 }
            goto L_0x01b5
        L_0x01b3:
            r0 = r28
        L_0x01b5:
            int r5 = r4 * r6
            float r5 = (float) r5     // Catch:{ all -> 0x01f2 }
            float r5 = r7 / r5
            int r8 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r8 <= 0) goto L_0x01c7
            float r8 = r9 / r5
            double r2 = (double) r8     // Catch:{ all -> 0x01f2 }
            double r2 = java.lang.Math.sqrt(r2)     // Catch:{ all -> 0x01f2 }
            float r2 = (float) r2     // Catch:{ all -> 0x01f2 }
            goto L_0x01c9
        L_0x01c7:
            r2 = 1065353216(0x3f800000, float:1.0)
        L_0x01c9:
            boolean r3 = com.android.launcher3.Utilities.ATLEAST_OREO     // Catch:{ all -> 0x01f2 }
            if (r3 == 0) goto L_0x01e4
            r3 = r21
            boolean r8 = r3 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x01f2 }
            if (r8 == 0) goto L_0x01e6
            float r8 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x01f2 }
            r10 = 0
            int r8 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r8 != 0) goto L_0x01e6
            r1.mAdaptiveIconScale = r2     // Catch:{ all -> 0x01f2 }
            android.graphics.Rect r8 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x01f2 }
            android.graphics.Rect r10 = r1.mBounds     // Catch:{ all -> 0x01f2 }
            r8.set(r10)     // Catch:{ all -> 0x01f2 }
            goto L_0x01e6
        L_0x01e4:
            r3 = r21
        L_0x01e6:
            monitor-exit(r25)
            return r2
        L_0x01e8:
            r0 = r28
            r24 = r10
            r3 = r21
        L_0x01ee:
            monitor-exit(r25)
            r2 = 1065353216(0x3f800000, float:1.0)
            return r2
        L_0x01f2:
            r0 = move-exception
            monitor-exit(r25)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.graphics.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }

    private static void convertToConvexArray(float[] xCoordinates, int direction, int topY, int bottomY) {
        int start;
        float[] angles = new float[(xCoordinates.length - 1)];
        int first = topY;
        int last = -1;
        float lastAngle = Float.MAX_VALUE;
        for (int i = topY + 1; i <= bottomY; i++) {
            if (xCoordinates[i] > -1.0f) {
                if (lastAngle == Float.MAX_VALUE) {
                    start = first;
                } else {
                    float currentAngle = (xCoordinates[i] - xCoordinates[last]) / ((float) (i - last));
                    int start2 = last;
                    if ((currentAngle - lastAngle) * ((float) direction) < 0.0f) {
                        int i2 = start2;
                        float f = currentAngle;
                        start = i2;
                        while (start > first) {
                            start--;
                            if ((((xCoordinates[i] - xCoordinates[start]) / ((float) (i - start))) - angles[start]) * ((float) direction) >= 0.0f) {
                                break;
                            }
                        }
                    } else {
                        start = start2;
                    }
                }
                float lastAngle2 = (xCoordinates[i] - xCoordinates[start]) / ((float) (i - start));
                for (int j = start; j < i; j++) {
                    angles[j] = lastAngle2;
                    xCoordinates[j] = xCoordinates[start] + (((float) (j - start)) * lastAngle2);
                }
                last = i;
                lastAngle = lastAngle2;
            }
        }
    }

    public static int getNormalizedCircleSize(int size) {
        return (int) Math.round(Math.sqrt(((double) (4.0f * (((float) (size * size)) * MAX_CIRCLE_AREA_FACTOR))) / 3.141592653589793d));
    }
}
