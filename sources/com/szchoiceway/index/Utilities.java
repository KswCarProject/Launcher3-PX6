package com.szchoiceway.index;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.support.v4.internal.view.SupportMenu;
import android.util.Log;
import java.util.Random;

final class Utilities {
    private static final String TAG = "Launcher.Utilities";
    private static int mAppsIconIndex = 0;
    private static int mResCount = 0;
    private static int m_iModeSet = 0;
    private static int m_iUIType = 0;
    private static int m_iUITypeVer = 0;
    private static final Paint sBlurPaint = new Paint();
    private static final Canvas sCanvas = new Canvas();
    static int sColorIndex = 0;
    static int[] sColors = {SupportMenu.CATEGORY_MASK, -16711936, -16776961};
    private static final Paint sDisabledPaint = new Paint();
    private static final Paint sGlowColorFocusedPaint = new Paint();
    private static final Paint sGlowColorPressedPaint = new Paint();
    private static int sIconHeight = -1;
    private static int sIconTextureHeight = -1;
    private static int sIconTextureWidth = -1;
    private static int sIconWidth = -1;
    private static final Rect sOldBounds = new Rect();
    private static String xml_client = "";

    Utilities() {
    }

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
    }

    static Bitmap createIconBitmap(Bitmap icon, Context context) {
        int textureWidth = sIconTextureWidth;
        int textureHeight = sIconTextureHeight;
        int sourceWidth = icon.getWidth();
        int sourceHeight = icon.getHeight();
        if (sourceWidth <= textureWidth || sourceHeight <= textureHeight) {
            return (sourceWidth == textureWidth && sourceHeight == textureHeight) ? icon : createIconBitmap((Drawable) new BitmapDrawable(context.getResources(), icon), context);
        }
        return Bitmap.createBitmap(icon, (sourceWidth - textureWidth) / 2, (sourceHeight - textureHeight) / 2, textureWidth, textureHeight);
    }

    static Bitmap createIconBitmap(Drawable icon, Context context) {
        return createIconBitmap(icon, context, false, false);
    }

    static Bitmap createIconBitmap(Drawable icon, Context context, boolean normalMode, boolean bNeedZoom) {
        Bitmap bitmap;
        int tmp_top;
        synchronized (sCanvas) {
            if (sIconWidth == -1) {
                initStatics(context);
            }
            int width = sIconWidth;
            int height = sIconHeight;
            Log.i(TAG, "--->>>控件的宽和高:" + width + ", " + height);
            if (icon instanceof PaintDrawable) {
                PaintDrawable painter = (PaintDrawable) icon;
                painter.setIntrinsicWidth(width);
                painter.setIntrinsicHeight(height);
            } else if (icon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                if (bitmapDrawable.getBitmap().getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int sourceWidth = icon.getIntrinsicWidth();
            int sourceHeight = icon.getIntrinsicHeight();
            if (sourceWidth > 0 && sourceHeight > 0) {
                if (width < sourceWidth || height < sourceHeight) {
                    float ratio = ((float) sourceWidth) / ((float) sourceHeight);
                    if (m_iUITypeVer == 41 && ((m_iModeSet == 3 || m_iModeSet == 5 || m_iModeSet == 6) && IconCache.ksw_m_b_easyconn)) {
                        width = (int) (((double) width) * 0.8d);
                        height = (int) (((double) height) * 0.8d);
                    } else if (bNeedZoom) {
                        if (m_iUITypeVer == 41 && (m_iModeSet == 3 || m_iModeSet == 5 || m_iModeSet == 6)) {
                            width = (int) (((double) width) * 0.9d);
                            height = (int) (((double) height) * 0.9d);
                        } else if (m_iUITypeVer == 41 && m_iModeSet == 14) {
                            width = (int) (((double) width) * 0.4d);
                            height = (int) (((double) height) * 0.4d);
                        } else if (m_iUITypeVer == 101) {
                            width = (int) (((double) width) * 0.6d);
                            height = (int) (((double) height) * 0.6d);
                        } else {
                            width = (int) (((double) width) * 0.7d);
                            height = (int) (((double) height) * 0.7d);
                        }
                    } else if (sourceWidth > sourceHeight) {
                        height = (int) (((float) width) / ratio);
                    } else if (sourceHeight > sourceWidth) {
                        width = (int) (((float) height) * ratio);
                    } else if (m_iUITypeVer == 41 && (m_iModeSet == 3 || m_iModeSet == 5 || m_iModeSet == 6)) {
                        width = (int) (((double) width) * 1.3d);
                        height = (int) (((double) height) * 1.3d);
                    }
                } else if (sourceWidth < width && sourceHeight < height) {
                    Log.i(TAG, "createIconBitmap: normalMode = " + normalMode);
                    if (m_iUITypeVer == 41 && (m_iModeSet == 3 || m_iModeSet == 5 || m_iModeSet == 6)) {
                        width = (int) (((double) width) * 0.9d);
                        height = (int) (((double) height) * 0.9d);
                    } else if (m_iUITypeVer == 41 && m_iModeSet == 14) {
                        width = (int) (((double) width) * 0.45d);
                        height = (int) (((double) height) * 0.45d);
                    } else if (m_iUITypeVer != 41 || !normalMode) {
                        if (m_iUITypeVer == 101) {
                            width = (int) (((double) width) * 0.6d);
                            height = (int) (((double) height) * 0.6d);
                        } else {
                            width = (int) (((double) width) * 0.7d);
                            height = (int) (((double) height) * 0.7d);
                        }
                    }
                }
            }
            int textureWidth = sIconTextureWidth;
            int textureHeight = sIconTextureHeight;
            bitmap = Bitmap.createBitmap(textureWidth, textureHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = sCanvas;
            canvas.setBitmap(bitmap);
            int left = (textureWidth - width) / 2;
            if (m_iUITypeVer != 41) {
                tmp_top = (textureHeight - height) / 2;
            } else if (m_iModeSet != 14 || normalMode) {
                tmp_top = (textureHeight - height) / 2;
            } else {
                tmp_top = ((textureHeight - height) / 2) - 10;
            }
            int top = tmp_top;
            if (!normalMode) {
                Bitmap backBitmap = null;
                if (m_iUITypeVer == 41) {
                    int[] iResList = {R.drawable.kesaiwei_1280x480_als_apps_di_0};
                    if ("ALS".equalsIgnoreCase(xml_client) && mAppsIconIndex == 1) {
                        iResList = new int[]{R.drawable.kesaiwei_1280x480_als_apps_di_0};
                    } else if (m_iModeSet == 14) {
                        iResList = new int[]{R.drawable.kesaiwei_1024x600_chuanqi_cusp_apps_kuang};
                    }
                    backBitmap = BitmapFactory.decodeResource(context.getResources(), iResList[mResCount]);
                    mResCount++;
                    if (mResCount >= iResList.length) {
                        mResCount = 0;
                    }
                } else if (m_iUITypeVer == 101) {
                    int[] iResList2 = {R.drawable.normal_1920x720_icon_back};
                    backBitmap = BitmapFactory.decodeResource(context.getResources(), iResList2[mResCount]);
                    mResCount++;
                    if (mResCount >= iResList2.length) {
                        mResCount = 0;
                    }
                }
                if (m_iUITypeVer == 41) {
                    if (m_iModeSet == 14 || ("ALS".equalsIgnoreCase(xml_client) && mAppsIconIndex == 1)) {
                        setIconBackBitmap(canvas, backBitmap);
                    }
                } else if (m_iUITypeVer == 101) {
                    setIconBackBitmap(canvas, backBitmap);
                }
            }
            sOldBounds.set(icon.getBounds());
            icon.setBounds(left, top, left + width, top + height);
            icon.draw(canvas);
            icon.setBounds(sOldBounds);
            canvas.setBitmap((Bitmap) null);
        }
        return bitmap;
    }

    private static void setIconBackBitmap(Canvas canvas, Bitmap backBitmap) {
        if (backBitmap != null) {
            int backWidth = backBitmap.getWidth();
            int backHeight = backBitmap.getHeight();
            if (backWidth == sIconWidth && backHeight == sIconHeight) {
                canvas.drawBitmap(backBitmap, 0.0f, 0.0f, (Paint) null);
                return;
            }
            Matrix matrix = new Matrix();
            matrix.postScale(((float) sIconWidth) / ((float) backWidth), ((float) sIconHeight) / ((float) backHeight));
            canvas.drawBitmap(Bitmap.createBitmap(backBitmap, 0, 0, backWidth, backHeight, matrix, true), 0.0f, 0.0f, (Paint) null);
        }
    }

    static void drawSelectedAllAppsBitmap(Canvas dest, int destWidth, int destHeight, boolean pressed, Bitmap src) {
        synchronized (sCanvas) {
            if (sIconWidth == -1) {
                throw new RuntimeException("Assertion failed: Utilities not initialized");
            }
            dest.drawColor(0, PorterDuff.Mode.CLEAR);
            int[] xy = new int[2];
            Bitmap mask = src.extractAlpha(sBlurPaint, xy);
            dest.drawBitmap(mask, ((float) ((destWidth - src.getWidth()) / 2)) + ((float) xy[0]), ((float) ((destHeight - src.getHeight()) / 2)) + ((float) xy[1]), pressed ? sGlowColorPressedPaint : sGlowColorFocusedPaint);
            mask.recycle();
        }
    }

    static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
        synchronized (sCanvas) {
            if (sIconWidth == -1) {
                initStatics(context);
            }
            if (bitmap.getWidth() != sIconWidth || bitmap.getHeight() != sIconHeight) {
                bitmap = createIconBitmap((Drawable) new BitmapDrawable(context.getResources(), bitmap), context);
            }
        }
        return bitmap;
    }

    static Bitmap drawDisabledBitmap(Bitmap bitmap, Context context) {
        Bitmap disabled;
        synchronized (sCanvas) {
            if (sIconWidth == -1) {
                initStatics(context);
            }
            disabled = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = sCanvas;
            canvas.setBitmap(disabled);
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, sDisabledPaint);
            canvas.setBitmap((Bitmap) null);
        }
        return disabled;
    }

    private static void initStatics(Context context) {
        Resources resources = context.getResources();
        float density = resources.getDisplayMetrics().density;
        if ("ALS".equalsIgnoreCase(xml_client) && mAppsIconIndex == 1) {
            int dimension = (int) resources.getDimension(R.dimen.app_icon_size_apple);
            sIconHeight = dimension;
            sIconWidth = dimension;
        } else if (m_iUITypeVer == 41 && m_iModeSet == 14) {
            int dimension2 = (int) resources.getDimension(R.dimen.app_icon_size_chuanqi_cusp);
            sIconHeight = dimension2;
            sIconWidth = dimension2;
        } else {
            int dimension3 = (int) resources.getDimension(R.dimen.app_icon_size);
            sIconHeight = dimension3;
            sIconWidth = dimension3;
        }
        Log.i(TAG, "initStatics: m_iUITypeVer = " + m_iUITypeVer);
        Log.i(TAG, "initStatics: m_iModeSet = " + m_iModeSet);
        Log.i(TAG, "initStatics: sIconWidth = " + sIconWidth);
        int i = sIconWidth;
        sIconTextureHeight = i;
        sIconTextureWidth = i;
        sBlurPaint.setMaskFilter(new BlurMaskFilter(5.0f * density, BlurMaskFilter.Blur.NORMAL));
        sGlowColorPressedPaint.setColor(-15616);
        sGlowColorFocusedPaint.setColor(-29184);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.2f);
        sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
        sDisabledPaint.setAlpha(136);
    }

    static int roundToPow2(int n) {
        int orig = n;
        int n2 = n >> 1;
        int mask = 134217728;
        while (mask != 0 && (n2 & mask) == 0) {
            mask >>= 1;
        }
        while (mask != 0) {
            n2 |= mask;
            mask >>= 1;
        }
        int n3 = n2 + 1;
        if (n3 != orig) {
            return n3 << 1;
        }
        return n3;
    }

    static int generateRandomId() {
        return new Random(System.currentTimeMillis()).nextInt(16777216);
    }

    static int SetUIType(int mUIType, Context context) {
        m_iUIType = mUIType;
        return m_iUIType;
    }

    static int SetUITypeVer(int mUITypeVer, Context context) {
        m_iUITypeVer = mUITypeVer;
        return m_iUITypeVer;
    }

    static int KSW_iModeSet(int iModeSet, Context context) {
        m_iModeSet = iModeSet;
        return m_iModeSet;
    }

    static String setXmlClient(String client) {
        xml_client = client;
        return xml_client;
    }

    static int setAppsIconIndex(int index) {
        mAppsIconIndex = index;
        return mAppsIconIndex;
    }
}
