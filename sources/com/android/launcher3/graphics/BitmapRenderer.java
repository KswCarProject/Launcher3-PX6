package com.android.launcher3.graphics;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import com.android.launcher3.Utilities;

public class BitmapRenderer {
    public static final boolean USE_HARDWARE_BITMAP = Utilities.ATLEAST_P;

    public interface Renderer {
        void draw(Canvas canvas);
    }

    public static Bitmap createSoftwareBitmap(int width, int height, Renderer renderer) {
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        renderer.draw(new Canvas(result));
        return result;
    }

    @TargetApi(28)
    public static Bitmap createHardwareBitmap(int width, int height, Renderer renderer) {
        if (!USE_HARDWARE_BITMAP) {
            return createSoftwareBitmap(width, height, renderer);
        }
        Picture picture = new Picture();
        renderer.draw(picture.beginRecording(width, height));
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }
}
