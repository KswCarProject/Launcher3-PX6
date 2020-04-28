package com.android.launcher3.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.SparseArray;

public class ColorExtractor {
    public static int findDominantColorByHue(Bitmap bitmap) {
        return findDominantColorByHue(bitmap, 20);
    }

    public static int findDominantColorByHue(Bitmap bitmap, int samples) {
        int height;
        int i = samples;
        int height2 = bitmap.getHeight();
        int width = bitmap.getWidth();
        int sampleStride = (int) Math.sqrt((double) ((height2 * width) / i));
        if (sampleStride < 1) {
            sampleStride = 1;
        }
        float[] hsv = new float[3];
        float[] hueScoreHistogram = new float[360];
        int[] pixels = new int[i];
        int pixelCount = 0;
        char c = 0;
        int pixelCount2 = -1;
        float highScore = -1.0f;
        int y = 0;
        while (y < height2) {
            int bestHue = pixelCount2;
            int pixelCount3 = pixelCount;
            float highScore2 = highScore;
            int x = 0;
            while (x < width) {
                int argb = bitmap.getPixel(x, y);
                if (((argb >> 24) & 255) < 128) {
                    height = height2;
                } else {
                    int rgb = argb | -16777216;
                    Color.colorToHSV(rgb, hsv);
                    height = height2;
                    int hue = (int) hsv[c];
                    if (hue >= 0 && hue < hueScoreHistogram.length) {
                        if (pixelCount3 < i) {
                            pixels[pixelCount3] = rgb;
                            pixelCount3++;
                        }
                        hueScoreHistogram[hue] = hueScoreHistogram[hue] + (hsv[1] * hsv[2]);
                        if (hueScoreHistogram[hue] > highScore2) {
                            highScore2 = hueScoreHistogram[hue];
                            bestHue = hue;
                        }
                    }
                }
                x += sampleStride;
                height2 = height;
                c = 0;
            }
            Bitmap bitmap2 = bitmap;
            int i2 = height2;
            y += sampleStride;
            highScore = highScore2;
            pixelCount = pixelCount3;
            pixelCount2 = bestHue;
            c = 0;
        }
        Bitmap bitmap3 = bitmap;
        int i3 = height2;
        SparseArray<Float> rgbScores = new SparseArray<>();
        float highScore3 = -1.0f;
        int bestColor = -16777216;
        int i4 = 0;
        while (i4 < pixelCount) {
            int rgb2 = pixels[i4];
            Color.colorToHSV(rgb2, hsv);
            if (((int) hsv[0]) == pixelCount2) {
                float s = hsv[1];
                float v = hsv[2];
                int bucket = ((int) (s * 100.0f)) + ((int) (v * 10000.0f));
                float score = s * v;
                Float oldTotal = rgbScores.get(bucket);
                float newTotal = oldTotal == null ? score : oldTotal.floatValue() + score;
                float f = score;
                rgbScores.put(bucket, Float.valueOf(newTotal));
                if (newTotal > highScore3) {
                    bestColor = rgb2;
                    highScore3 = newTotal;
                }
            }
            i4++;
            int i5 = samples;
        }
        return bestColor;
    }
}
