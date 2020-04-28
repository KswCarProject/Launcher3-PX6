package com.android.launcher3.views;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.v4.view.MotionEventCompat;
import java.lang.reflect.Array;

public class BlurUtil {
    private static final String TAG = BlurUtil.class.getSimpleName();

    public static Bitmap doBlur(Bitmap sentBitmap, int radius, boolean canReuseInBitmap) {
        Bitmap bitmap;
        int i;
        int divsum;
        int y;
        int i2;
        int h;
        int i3;
        Bitmap bitmap2 = sentBitmap;
        int p = radius;
        if (bitmap2 == null) {
            return null;
        }
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = bitmap2.copy(sentBitmap.getConfig(), true);
        }
        if (p < 1) {
            return null;
        }
        int w = bitmap.getWidth();
        int rbs = bitmap.getHeight();
        int[] pix = new int[(w * rbs)];
        bitmap.getPixels(pix, 0, w, 0, 0, w, rbs);
        int wm = w - 1;
        int p2 = rbs - 1;
        int wh = w * rbs;
        int div = p + p + 1;
        int[] r = new int[wh];
        int[] g = new int[wh];
        int[] b = new int[wh];
        int[] vmin = new int[Math.max(w, rbs)];
        int divsum2 = (div + 1) >> 1;
        int wh2 = wh;
        int y2 = divsum2 * divsum2;
        int[] dv = new int[(y2 * 256)];
        int i4 = 0;
        while (true) {
            i = i4;
            if (i >= y2 * 256) {
                break;
            }
            dv[i] = i / y2;
            i4 = i + 1;
        }
        int i5 = i;
        int[][] stack = (int[][]) Array.newInstance(int.class, new int[]{div, 3});
        int r1 = p + 1;
        int yw = 0;
        int yi = 0;
        int yi2 = 0;
        while (true) {
            divsum = y2;
            y = yi2;
            if (y >= rbs) {
                break;
            }
            int gsum = 0;
            int rsum = 0;
            int boutsum = 0;
            int goutsum = 0;
            int routsum = 0;
            int binsum = 0;
            int ginsum = 0;
            int rinsum = 0;
            Bitmap bitmap3 = bitmap;
            int i6 = -p;
            int bsum = 0;
            while (i6 <= p) {
                int hm = p2;
                int h2 = rbs;
                int p3 = pix[yi + Math.min(wm, Math.max(i6, 0))];
                int[] sir = stack[i6 + p];
                sir[0] = (p3 & 16711680) >> 16;
                sir[1] = (p3 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                sir[2] = p3 & 255;
                int rbs2 = r1 - Math.abs(i6);
                rsum += sir[0] * rbs2;
                gsum += sir[1] * rbs2;
                bsum += sir[2] * rbs2;
                if (i6 > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                i6++;
                rbs = h2;
                p2 = hm;
            }
            int hm2 = p2;
            int h3 = rbs;
            int stackpointer = radius;
            int x = 0;
            while (x < w) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                int rsum2 = rsum - routsum;
                int gsum2 = gsum - goutsum;
                int bsum2 = bsum - boutsum;
                int[] sir2 = stack[((stackpointer - p) + div) % div];
                int routsum2 = routsum - sir2[0];
                int goutsum2 = goutsum - sir2[1];
                int boutsum2 = boutsum - sir2[2];
                if (y == 0) {
                    i3 = i6;
                    vmin[x] = Math.min(x + p + 1, wm);
                } else {
                    i3 = i6;
                }
                int p4 = pix[yw + vmin[x]];
                sir2[0] = (p4 & 16711680) >> 16;
                sir2[1] = (p4 & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8;
                int wm2 = wm;
                sir2[2] = p4 & 255;
                int rinsum2 = rinsum + sir2[0];
                int ginsum2 = ginsum + sir2[1];
                int binsum2 = binsum + sir2[2];
                rsum = rsum2 + rinsum2;
                gsum = gsum2 + ginsum2;
                bsum = bsum2 + binsum2;
                stackpointer = (stackpointer + 1) % div;
                int[] sir3 = stack[stackpointer % div];
                routsum = routsum2 + sir3[0];
                goutsum = goutsum2 + sir3[1];
                boutsum = boutsum2 + sir3[2];
                rinsum = rinsum2 - sir3[0];
                ginsum = ginsum2 - sir3[1];
                binsum = binsum2 - sir3[2];
                yi++;
                x++;
                i6 = i3;
                wm = wm2;
            }
            int i7 = i6;
            int i8 = wm;
            yw += w;
            yi2 = y + 1;
            y2 = divsum;
            bitmap = bitmap3;
            rbs = h3;
            p2 = hm2;
            int bsum3 = i7;
        }
        Bitmap bitmap4 = bitmap;
        int hm3 = p2;
        int wm3 = wm;
        int h4 = rbs;
        int rbs3 = y;
        int x2 = 0;
        while (x2 < w) {
            int goutsum3 = 0;
            int routsum3 = 0;
            int binsum3 = 0;
            int ginsum3 = 0;
            int rinsum3 = 0;
            int i9 = -p;
            int gsum3 = 0;
            int hm4 = 0;
            int boutsum3 = 0;
            int bsum4 = 0;
            int yp = (-p) * w;
            while (i9 <= p) {
                int y3 = rbs3;
                int yi3 = Math.max(0, yp) + x2;
                int[] sir4 = stack[i9 + p];
                sir4[0] = r[yi3];
                sir4[1] = g[yi3];
                sir4[2] = b[yi3];
                int rbs4 = r1 - Math.abs(i9);
                int rsum3 = hm4 + (r[yi3] * rbs4);
                gsum3 += g[yi3] * rbs4;
                bsum4 += b[yi3] * rbs4;
                if (i9 > 0) {
                    rinsum3 += sir4[0];
                    ginsum3 += sir4[1];
                    binsum3 += sir4[2];
                } else {
                    routsum3 += sir4[0];
                    goutsum3 += sir4[1];
                    boutsum3 += sir4[2];
                }
                int rsum4 = rsum3;
                int rsum5 = hm3;
                if (i9 < rsum5) {
                    yp += w;
                }
                i9++;
                hm3 = rsum5;
                rbs3 = y3;
                hm4 = rsum4;
            }
            int rsum6 = hm4;
            int i10 = rbs3;
            int rsum7 = hm3;
            int yi4 = x2;
            rbs3 = 0;
            int binsum4 = binsum3;
            int stackpointer2 = radius;
            while (true) {
                i2 = i9;
                h = h4;
                if (rbs3 >= h) {
                    break;
                }
                pix[yi4] = (pix[yi4] & -16777216) | (dv[rsum6] << 16) | (dv[gsum3] << 8) | dv[bsum4];
                int rsum8 = rsum6 - routsum3;
                int gsum4 = gsum3 - goutsum3;
                int bsum5 = bsum4 - boutsum3;
                int[] sir5 = stack[((stackpointer2 - p) + div) % div];
                int routsum4 = routsum3 - sir5[0];
                int goutsum4 = goutsum3 - sir5[1];
                int boutsum4 = boutsum3 - sir5[2];
                if (x2 == 0) {
                    vmin[rbs3] = Math.min(rbs3 + r1, rsum7) * w;
                }
                int p5 = vmin[rbs3] + x2;
                sir5[0] = r[p5];
                sir5[1] = g[p5];
                sir5[2] = b[p5];
                int rinsum4 = rinsum3 + sir5[0];
                int ginsum4 = ginsum3 + sir5[1];
                int binsum5 = binsum4 + sir5[2];
                rsum6 = rsum8 + rinsum4;
                gsum3 = gsum4 + ginsum4;
                bsum4 = bsum5 + binsum5;
                stackpointer2 = (stackpointer2 + 1) % div;
                int[] sir6 = stack[stackpointer2];
                routsum3 = routsum4 + sir6[0];
                goutsum3 = goutsum4 + sir6[1];
                boutsum3 = boutsum4 + sir6[2];
                rinsum3 = rinsum4 - sir6[0];
                ginsum3 = ginsum4 - sir6[1];
                binsum4 = binsum5 - sir6[2];
                yi4 += w;
                rbs3++;
                h4 = h;
                i9 = i2;
                p = radius;
            }
            x2++;
            h4 = h;
            hm3 = rsum7;
            int gsum5 = i2;
            p = radius;
        }
        int i11 = rbs3;
        int[] iArr = vmin;
        int[] iArr2 = b;
        int[] iArr3 = g;
        int[] iArr4 = r;
        int i12 = div;
        int i13 = divsum;
        int divsum3 = wh2;
        int i14 = hm3;
        int i15 = wm3;
        bitmap4.setPixels(pix, 0, w, 0, 0, w, h4);
        return bitmap4;
    }

    public static Bitmap doBlur(Bitmap originBitmap, int scaleRatio, int blurRadius) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originBitmap, originBitmap.getWidth() / scaleRatio, originBitmap.getHeight() / scaleRatio, false);
        Bitmap blurBitmap = doBlur(scaledBitmap, blurRadius, false);
        scaledBitmap.recycle();
        return blurBitmap;
    }

    public static Bitmap doBlur(Bitmap originBitmap, int width, int height, int blurRadius) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(originBitmap, width, height);
        Bitmap blurBitmap = doBlur(thumbnail, blurRadius, true);
        thumbnail.recycle();
        return blurBitmap;
    }
}
