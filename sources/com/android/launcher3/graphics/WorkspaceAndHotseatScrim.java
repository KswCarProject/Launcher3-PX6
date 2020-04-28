package com.android.launcher3.graphics;

import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.ColorUtils;
import android.util.DisplayMetrics;
import android.util.Property;
import android.view.View;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.Themes;

public class WorkspaceAndHotseatScrim implements View.OnAttachStateChangeListener, WallpaperColorInfo.OnChangeListener {
    private static final int ALPHA_MASK_BITMAP_DP = 200;
    private static final int ALPHA_MASK_HEIGHT_DP = 500;
    private static final int ALPHA_MASK_WIDTH_DP = 2;
    private static final int DARK_SCRIM_COLOR = 1426063360;
    private static final int MAX_HOTSEAT_SCRIM_ALPHA = 100;
    public static Property<WorkspaceAndHotseatScrim, Float> SCRIM_PROGRESS = new Property<WorkspaceAndHotseatScrim, Float>(Float.TYPE, "scrimProgress") {
        public Float get(WorkspaceAndHotseatScrim scrim) {
            return Float.valueOf(scrim.mScrimProgress);
        }

        public void set(WorkspaceAndHotseatScrim scrim, Float value) {
            scrim.setScrimProgress(value.floatValue());
        }
    };
    private static Property<WorkspaceAndHotseatScrim, Float> SYSUI_ANIM_MULTIPLIER = new Property<WorkspaceAndHotseatScrim, Float>(Float.TYPE, "sysUiAnimMultiplier") {
        public Float get(WorkspaceAndHotseatScrim scrim) {
            return Float.valueOf(scrim.mSysUiAnimMultiplier);
        }

        public void set(WorkspaceAndHotseatScrim scrim, Float value) {
            float unused = scrim.mSysUiAnimMultiplier = value.floatValue();
            scrim.reapplySysUiAlpha();
        }
    };
    public static Property<WorkspaceAndHotseatScrim, Float> SYSUI_PROGRESS = new Property<WorkspaceAndHotseatScrim, Float>(Float.TYPE, "sysUiProgress") {
        public Float get(WorkspaceAndHotseatScrim scrim) {
            return Float.valueOf(scrim.mSysUiProgress);
        }

        public void set(WorkspaceAndHotseatScrim scrim, Float value) {
            scrim.setSysUiProgress(value.floatValue());
        }
    };
    /* access modifiers changed from: private */
    public boolean mAnimateScrimOnNextDraw = false;
    private final Bitmap mBottomMask;
    private final Paint mBottomMaskPaint = new Paint(2);
    private boolean mDrawBottomScrim;
    private boolean mDrawTopScrim;
    private final RectF mFinalMaskRect = new RectF();
    private int mFullScrimColor;
    private final boolean mHasSysUiScrim;
    private boolean mHideSysUiScrim;
    private final Rect mHighlightRect = new Rect();
    private final Launcher mLauncher;
    private final int mMaskHeight;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                boolean unused = WorkspaceAndHotseatScrim.this.mAnimateScrimOnNextDraw = true;
            } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                boolean unused2 = WorkspaceAndHotseatScrim.this.mAnimateScrimOnNextDraw = false;
            }
        }
    };
    private final View mRoot;
    private int mScrimAlpha = 0;
    /* access modifiers changed from: private */
    public float mScrimProgress;
    /* access modifiers changed from: private */
    public float mSysUiAnimMultiplier = 1.0f;
    /* access modifiers changed from: private */
    public float mSysUiProgress = 1.0f;
    private final Drawable mTopScrim;
    private final WallpaperColorInfo mWallpaperColorInfo;
    private Workspace mWorkspace;

    public WorkspaceAndHotseatScrim(View view) {
        this.mRoot = view;
        this.mLauncher = Launcher.getLauncher(view.getContext());
        this.mWallpaperColorInfo = WallpaperColorInfo.getInstance(this.mLauncher);
        this.mMaskHeight = Utilities.pxFromDp(200.0f, view.getResources().getDisplayMetrics());
        this.mHasSysUiScrim = !this.mWallpaperColorInfo.supportsDarkText();
        if (this.mHasSysUiScrim) {
            this.mTopScrim = Themes.getAttrDrawable(view.getContext(), R.attr.workspaceStatusBarScrim);
            this.mBottomMask = createDitheredAlphaMask();
        } else {
            this.mTopScrim = null;
            this.mBottomMask = null;
        }
        view.addOnAttachStateChangeListener(this);
        onExtractedColorsChanged(this.mWallpaperColorInfo);
    }

    public void setWorkspace(Workspace workspace) {
        this.mWorkspace = workspace;
    }

    public void draw(Canvas canvas) {
        if (this.mScrimAlpha > 0) {
            this.mWorkspace.computeScrollWithoutInvalidation();
            CellLayout currCellLayout = this.mWorkspace.getCurrentDragOverlappingLayout();
            canvas.save();
            if (!(currCellLayout == null || currCellLayout == this.mLauncher.getHotseat().getLayout())) {
                this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(currCellLayout, this.mHighlightRect);
                canvas.clipRect(this.mHighlightRect, Region.Op.DIFFERENCE);
            }
            canvas.drawColor(ColorUtils.setAlphaComponent(this.mFullScrimColor, this.mScrimAlpha));
            canvas.restore();
        }
        if (!this.mHideSysUiScrim && this.mHasSysUiScrim) {
            if (this.mSysUiProgress <= 0.0f) {
                this.mAnimateScrimOnNextDraw = false;
                return;
            }
            if (this.mAnimateScrimOnNextDraw) {
                this.mSysUiAnimMultiplier = 0.0f;
                reapplySysUiAlphaNoInvalidate();
                ObjectAnimator anim = ObjectAnimator.ofFloat(this, SYSUI_ANIM_MULTIPLIER, new float[]{1.0f});
                anim.setAutoCancel(true);
                anim.setDuration(600);
                anim.setStartDelay(this.mLauncher.getWindow().getTransitionBackgroundFadeDuration());
                anim.start();
                this.mAnimateScrimOnNextDraw = false;
            }
            if (this.mDrawTopScrim) {
                this.mTopScrim.draw(canvas);
            }
            if (this.mDrawBottomScrim) {
                canvas.drawBitmap(this.mBottomMask, (Rect) null, this.mFinalMaskRect, this.mBottomMaskPaint);
            }
        }
    }

    public void onInsetsChanged(Rect insets) {
        this.mDrawTopScrim = insets.top > 0;
        this.mDrawBottomScrim = !this.mLauncher.getDeviceProfile().isVerticalBarLayout();
    }

    /* access modifiers changed from: private */
    public void setScrimProgress(float progress) {
        if (this.mScrimProgress != progress) {
            this.mScrimProgress = progress;
            this.mScrimAlpha = Math.round(this.mScrimProgress * 255.0f);
            invalidate();
        }
    }

    public void onViewAttachedToWindow(View view) {
        this.mWallpaperColorInfo.addOnChangeListener(this);
        onExtractedColorsChanged(this.mWallpaperColorInfo);
        if (this.mHasSysUiScrim) {
            IntentFilter filter = new IntentFilter("android.intent.action.SCREEN_OFF");
            filter.addAction("android.intent.action.USER_PRESENT");
            this.mRoot.getContext().registerReceiver(this.mReceiver, filter);
        }
    }

    public void onViewDetachedFromWindow(View view) {
        this.mWallpaperColorInfo.removeOnChangeListener(this);
        if (this.mHasSysUiScrim) {
            this.mRoot.getContext().unregisterReceiver(this.mReceiver);
        }
    }

    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        this.mBottomMaskPaint.setColor(ColorUtils.compositeColors((int) DARK_SCRIM_COLOR, wallpaperColorInfo.getMainColor()));
        reapplySysUiAlpha();
        this.mFullScrimColor = wallpaperColorInfo.getMainColor();
        if (this.mScrimAlpha > 0) {
            invalidate();
        }
    }

    public void setSize(int w, int h) {
        if (this.mHasSysUiScrim) {
            this.mTopScrim.setBounds(0, 0, w, h);
            this.mFinalMaskRect.set(0.0f, (float) (h - this.mMaskHeight), (float) w, (float) h);
        }
    }

    public void hideSysUiScrim(boolean hideSysUiScrim) {
        this.mHideSysUiScrim = hideSysUiScrim;
        if (!hideSysUiScrim) {
            this.mAnimateScrimOnNextDraw = true;
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public void setSysUiProgress(float progress) {
        if (progress != this.mSysUiProgress) {
            this.mSysUiProgress = progress;
            reapplySysUiAlpha();
        }
    }

    /* access modifiers changed from: private */
    public void reapplySysUiAlpha() {
        if (this.mHasSysUiScrim) {
            reapplySysUiAlphaNoInvalidate();
            if (!this.mHideSysUiScrim) {
                invalidate();
            }
        }
    }

    private void reapplySysUiAlphaNoInvalidate() {
        float factor = this.mSysUiProgress * this.mSysUiAnimMultiplier;
        this.mBottomMaskPaint.setAlpha(Math.round(100.0f * factor));
        this.mTopScrim.setAlpha(Math.round(255.0f * factor));
    }

    public void invalidate() {
        this.mRoot.invalidate();
    }

    public Bitmap createDitheredAlphaMask() {
        DisplayMetrics dm = this.mLauncher.getResources().getDisplayMetrics();
        int width = Utilities.pxFromDp(2.0f, dm);
        int gradientHeight = Utilities.pxFromDp(500.0f, dm);
        Bitmap dst = Bitmap.createBitmap(width, this.mMaskHeight, Bitmap.Config.ALPHA_8);
        Canvas c = new Canvas(dst);
        Paint paint = new Paint(4);
        paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, (float) gradientHeight, new int[]{16777215, ColorUtils.setAlphaComponent(-1, 242), -1}, new float[]{0.0f, 0.8f, 1.0f}, Shader.TileMode.CLAMP));
        c.drawRect(0.0f, 0.0f, (float) width, (float) gradientHeight, paint);
        return dst;
    }
}
