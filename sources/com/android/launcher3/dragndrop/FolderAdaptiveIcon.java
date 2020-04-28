package com.android.launcher3.dragndrop;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import com.android.launcher3.Launcher;
import com.android.launcher3.MainThreadExecutor;
import com.android.launcher3.R;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.PreviewBackground;
import com.android.launcher3.graphics.BitmapRenderer;
import com.android.launcher3.util.Preconditions;
import java.util.concurrent.Callable;

@TargetApi(26)
public class FolderAdaptiveIcon extends AdaptiveIconDrawable {
    private static final String TAG = "FolderAdaptiveIcon";
    private final Drawable mBadge;
    private final Path mMask;

    private FolderAdaptiveIcon(Drawable bg, Drawable fg, Drawable badge, Path mask) {
        super(bg, fg);
        this.mBadge = badge;
        this.mMask = mask;
    }

    public Path getIconMask() {
        return this.mMask;
    }

    public Drawable getBadge() {
        return this.mBadge;
    }

    public static FolderAdaptiveIcon createFolderAdaptiveIcon(Launcher launcher, long folderId, Point dragViewSize) {
        Preconditions.assertNonUiThread();
        int margin = launcher.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        try {
            return (FolderAdaptiveIcon) new MainThreadExecutor().submit(new Callable(folderId, Bitmap.createBitmap(dragViewSize.x - margin, dragViewSize.y - margin, Bitmap.Config.ARGB_8888), dragViewSize) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ Bitmap f$2;
                private final /* synthetic */ Point f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final Object call() {
                    return FolderAdaptiveIcon.lambda$createFolderAdaptiveIcon$0(Launcher.this, this.f$1, this.f$2, this.f$3);
                }
            }).get();
        } catch (Exception e) {
            Log.e(TAG, "Unable to create folder icon", e);
            return null;
        }
    }

    static /* synthetic */ FolderAdaptiveIcon lambda$createFolderAdaptiveIcon$0(Launcher launcher, long folderId, Bitmap badge, Point dragViewSize) throws Exception {
        FolderIcon icon = launcher.findFolderIcon(folderId);
        if (icon == null) {
            return null;
        }
        return createDrawableOnUiThread(icon, badge, dragViewSize);
    }

    private static FolderAdaptiveIcon createDrawableOnUiThread(FolderIcon icon, Bitmap badgeBitmap, Point dragViewSize) {
        FolderIcon folderIcon = icon;
        Bitmap bitmap = badgeBitmap;
        Point point = dragViewSize;
        Preconditions.assertUIThread();
        float margin = icon.getResources().getDimension(R.dimen.blur_size_medium_outline) / 2.0f;
        Canvas c = new Canvas();
        PreviewBackground bg = icon.getFolderBackground();
        c.setBitmap(bitmap);
        bg.drawShadow(c);
        bg.drawBackgroundStroke(c);
        folderIcon.drawBadge(c);
        float sizeScaleFactor = (AdaptiveIconDrawable.getExtraInsetFraction() * 2.0f) + 1.0f;
        int previewWidth = (int) (((float) point.x) * sizeScaleFactor);
        int previewHeight = (int) (((float) point.y) * sizeScaleFactor);
        float shiftFactor = AdaptiveIconDrawable.getExtraInsetFraction() / sizeScaleFactor;
        float previewShiftX = ((float) previewWidth) * shiftFactor;
        float previewShiftY = ((float) previewHeight) * shiftFactor;
        Bitmap previewBitmap = BitmapRenderer.createHardwareBitmap(previewWidth, previewHeight, new BitmapRenderer.Renderer(previewShiftX, previewShiftY, folderIcon) {
            private final /* synthetic */ float f$0;
            private final /* synthetic */ float f$1;
            private final /* synthetic */ FolderIcon f$2;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void draw(Canvas canvas) {
                FolderAdaptiveIcon.lambda$createDrawableOnUiThread$1(this.f$0, this.f$1, this.f$2, canvas);
            }
        });
        Path mask = new Path();
        Matrix m = new Matrix();
        m.setTranslate(margin, margin);
        bg.getClipPath().transform(m, mask);
        ShiftedBitmapDrawable badge = new ShiftedBitmapDrawable(bitmap, margin, margin);
        float f = margin;
        return new FolderAdaptiveIcon(new ColorDrawable(bg.getBgColor()), new ShiftedBitmapDrawable(previewBitmap, margin - previewShiftX, margin - previewShiftY), badge, mask);
    }

    static /* synthetic */ void lambda$createDrawableOnUiThread$1(float previewShiftX, float previewShiftY, FolderIcon icon, Canvas canvas) {
        int count = canvas.save();
        canvas.translate(previewShiftX, previewShiftY);
        icon.getPreviewItemManager().draw(canvas);
        canvas.restoreToCount(count);
    }

    private static class ShiftedBitmapDrawable extends Drawable {
        private final Bitmap mBitmap;
        private final Paint mPaint = new Paint(2);
        private final float mShiftX;
        private final float mShiftY;

        ShiftedBitmapDrawable(Bitmap bitmap, float shiftX, float shiftY) {
            this.mBitmap = bitmap;
            this.mShiftX = shiftX;
            this.mShiftY = shiftY;
        }

        public void draw(Canvas canvas) {
            canvas.drawBitmap(this.mBitmap, this.mShiftX, this.mShiftY, this.mPaint);
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
            this.mPaint.setColorFilter(colorFilter);
        }

        public int getOpacity() {
            return -3;
        }
    }
}
