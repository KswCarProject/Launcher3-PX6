package com.android.launcher3.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.BitmapRenderer;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import java.nio.ByteBuffer;

public class DragPreviewProvider {
    protected final int blurSizeOutline;
    public Bitmap generatedDragOutline;
    private OutlineGeneratorCallback mOutlineGeneratorCallback;
    private final Rect mTempRect;
    protected final View mView;
    public final int previewPadding;

    public DragPreviewProvider(View view) {
        this(view, view.getContext());
    }

    public DragPreviewProvider(View view, Context context) {
        this.mTempRect = new Rect();
        this.mView = view;
        this.blurSizeOutline = context.getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        if (this.mView instanceof BubbleTextView) {
            Rect bounds = getDrawableBounds(((BubbleTextView) this.mView).getIcon());
            this.previewPadding = (this.blurSizeOutline - bounds.left) - bounds.top;
            return;
        }
        this.previewPadding = this.blurSizeOutline;
    }

    /* access modifiers changed from: protected */
    public void drawDragView(Canvas destCanvas, float scale) {
        destCanvas.save();
        destCanvas.scale(scale, scale);
        if (this.mView instanceof BubbleTextView) {
            Drawable d = ((BubbleTextView) this.mView).getIcon();
            Rect bounds = getDrawableBounds(d);
            destCanvas.translate((float) ((this.blurSizeOutline / 2) - bounds.left), (float) ((this.blurSizeOutline / 2) - bounds.top));
            d.draw(destCanvas);
        } else {
            Rect clipRect = this.mTempRect;
            this.mView.getDrawingRect(clipRect);
            boolean textVisible = false;
            if ((this.mView instanceof FolderIcon) && ((FolderIcon) this.mView).getTextVisible()) {
                ((FolderIcon) this.mView).setTextVisible(false);
                textVisible = true;
            }
            destCanvas.translate((float) ((-this.mView.getScrollX()) + (this.blurSizeOutline / 2)), (float) ((-this.mView.getScrollY()) + (this.blurSizeOutline / 2)));
            destCanvas.clipRect(clipRect);
            this.mView.draw(destCanvas);
            if (textVisible) {
                ((FolderIcon) this.mView).setTextVisible(true);
            }
        }
        destCanvas.restore();
    }

    public Bitmap createDragBitmap() {
        int width = this.mView.getWidth();
        int height = this.mView.getHeight();
        if (this.mView instanceof BubbleTextView) {
            Rect bounds = getDrawableBounds(((BubbleTextView) this.mView).getIcon());
            width = bounds.width();
            height = bounds.height();
        } else if (this.mView instanceof LauncherAppWidgetHostView) {
            float scale = ((LauncherAppWidgetHostView) this.mView).getScaleToFit();
            return BitmapRenderer.createSoftwareBitmap(this.blurSizeOutline + ((int) (((float) this.mView.getWidth()) * scale)), this.blurSizeOutline + ((int) (((float) this.mView.getHeight()) * scale)), new BitmapRenderer.Renderer(scale) {
                private final /* synthetic */ float f$1;

                {
                    this.f$1 = r2;
                }

                public final void draw(Canvas canvas) {
                    DragPreviewProvider.this.drawDragView(canvas, this.f$1);
                }
            });
        }
        return BitmapRenderer.createHardwareBitmap(this.blurSizeOutline + width, this.blurSizeOutline + height, new BitmapRenderer.Renderer() {
            public final void draw(Canvas canvas) {
                DragPreviewProvider.this.drawDragView(canvas, 1.0f);
            }
        });
    }

    public final void generateDragOutline(Bitmap preview) {
        this.mOutlineGeneratorCallback = new OutlineGeneratorCallback(preview);
        new Handler(UiThreadHelper.getBackgroundLooper()).post(this.mOutlineGeneratorCallback);
    }

    protected static Rect getDrawableBounds(Drawable d) {
        Rect bounds = new Rect();
        d.copyBounds(bounds);
        if (bounds.width() == 0 || bounds.height() == 0) {
            bounds.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        } else {
            bounds.offsetTo(0, 0);
        }
        return bounds;
    }

    public float getScaleAndPosition(Bitmap preview, int[] outPos) {
        float scale = Launcher.getLauncher(this.mView.getContext()).getDragLayer().getLocationInDragLayer(this.mView, outPos);
        if (this.mView instanceof LauncherAppWidgetHostView) {
            scale /= ((LauncherAppWidgetHostView) this.mView).getScaleToFit();
        }
        outPos[0] = Math.round(((float) outPos[0]) - ((((float) preview.getWidth()) - ((((float) this.mView.getWidth()) * scale) * this.mView.getScaleX())) / 2.0f));
        outPos[1] = Math.round((((float) outPos[1]) - (((1.0f - scale) * ((float) preview.getHeight())) / 2.0f)) - ((float) (this.previewPadding / 2)));
        return scale;
    }

    /* access modifiers changed from: protected */
    public Bitmap convertPreviewToAlphaBitmap(Bitmap preview) {
        return preview.copy(Bitmap.Config.ALPHA_8, true);
    }

    private class OutlineGeneratorCallback implements Runnable {
        private final Context mContext;
        private final Bitmap mPreviewSnapshot;

        OutlineGeneratorCallback(Bitmap preview) {
            this.mPreviewSnapshot = preview;
            this.mContext = DragPreviewProvider.this.mView.getContext();
        }

        public void run() {
            Bitmap preview = DragPreviewProvider.this.convertPreviewToAlphaBitmap(this.mPreviewSnapshot);
            byte[] pixels = new byte[(preview.getWidth() * preview.getHeight())];
            ByteBuffer buffer = ByteBuffer.wrap(pixels);
            buffer.rewind();
            preview.copyPixelsToBuffer(buffer);
            for (int i = 0; i < pixels.length; i++) {
                if ((pixels[i] & 255) < 188) {
                    pixels[i] = 0;
                }
            }
            buffer.rewind();
            preview.copyPixelsFromBuffer(buffer);
            Paint paint = new Paint(3);
            Canvas canvas = new Canvas();
            paint.setMaskFilter(new BlurMaskFilter((float) DragPreviewProvider.this.blurSizeOutline, BlurMaskFilter.Blur.OUTER));
            int[] outerBlurOffset = new int[2];
            Bitmap thickOuterBlur = preview.extractAlpha(paint, outerBlurOffset);
            paint.setMaskFilter(new BlurMaskFilter(this.mContext.getResources().getDimension(R.dimen.blur_size_thin_outline), BlurMaskFilter.Blur.OUTER));
            int[] brightOutlineOffset = new int[2];
            Bitmap brightOutline = preview.extractAlpha(paint, brightOutlineOffset);
            canvas.setBitmap(preview);
            canvas.drawColor(-16777216, PorterDuff.Mode.SRC_OUT);
            paint.setMaskFilter(new BlurMaskFilter((float) DragPreviewProvider.this.blurSizeOutline, BlurMaskFilter.Blur.NORMAL));
            int[] thickInnerBlurOffset = new int[2];
            Bitmap thickInnerBlur = preview.extractAlpha(paint, thickInnerBlurOffset);
            paint.setMaskFilter((MaskFilter) null);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            canvas.setBitmap(thickInnerBlur);
            canvas.drawBitmap(preview, (float) (-thickInnerBlurOffset[0]), (float) (-thickInnerBlurOffset[1]), paint);
            Bitmap thickInnerBlur2 = thickInnerBlur;
            int[] thickInnerBlurOffset2 = thickInnerBlurOffset;
            Bitmap brightOutline2 = brightOutline;
            Paint paint2 = paint;
            canvas.drawRect(0.0f, 0.0f, (float) (-thickInnerBlurOffset[0]), (float) thickInnerBlur.getHeight(), paint2);
            canvas.drawRect(0.0f, 0.0f, (float) thickInnerBlur2.getWidth(), (float) (-thickInnerBlurOffset2[1]), paint2);
            paint.setXfermode((Xfermode) null);
            canvas.setBitmap(preview);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            Bitmap thickInnerBlur3 = thickInnerBlur2;
            canvas.drawBitmap(thickInnerBlur3, (float) thickInnerBlurOffset2[0], (float) thickInnerBlurOffset2[1], paint);
            canvas.drawBitmap(thickOuterBlur, (float) outerBlurOffset[0], (float) outerBlurOffset[1], paint);
            Bitmap brightOutline3 = brightOutline2;
            canvas.drawBitmap(brightOutline3, (float) brightOutlineOffset[0], (float) brightOutlineOffset[1], paint);
            canvas.setBitmap((Bitmap) null);
            brightOutline3.recycle();
            thickOuterBlur.recycle();
            thickInnerBlur3.recycle();
            DragPreviewProvider.this.generatedDragOutline = preview;
        }
    }
}
