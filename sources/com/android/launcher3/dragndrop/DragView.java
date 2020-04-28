package com.android.launcher3.dragndrop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.pm.LauncherActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.view.View;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.graphics.LauncherIcons;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import java.util.Arrays;
import java.util.List;

public class DragView extends View {
    public static final int COLOR_CHANGE_DURATION = 120;
    public static final int VIEW_ZOOM_DURATION = 150;
    static float sDragAlpha = 1.0f;
    private static final ColorMatrix sTempMatrix1 = new ColorMatrix();
    private static final ColorMatrix sTempMatrix2 = new ColorMatrix();
    ValueAnimator mAnim;
    /* access modifiers changed from: private */
    public int mAnimatedShiftX;
    /* access modifiers changed from: private */
    public int mAnimatedShiftY;
    /* access modifiers changed from: private */
    public boolean mAnimationCancelled = false;
    /* access modifiers changed from: private */
    public Drawable mBadge;
    /* access modifiers changed from: private */
    public ColorMatrixColorFilter mBaseFilter;
    /* access modifiers changed from: private */
    public Drawable mBgSpringDrawable;
    /* access modifiers changed from: private */
    public Bitmap mBitmap;
    private final int mBlurSizeOutline;
    private Bitmap mCrossFadeBitmap;
    float mCrossFadeProgress = 0.0f;
    float[] mCurrentFilter;
    final DragController mDragController;
    private final DragLayer mDragLayer;
    private Rect mDragRegion = null;
    private Point mDragVisualizeOffset = null;
    /* access modifiers changed from: private */
    public boolean mDrawBitmap = true;
    /* access modifiers changed from: private */
    public Drawable mFgSpringDrawable;
    private ValueAnimator mFilterAnimator;
    private boolean mHasDrawn = false;
    private final float mInitialScale;
    private float mIntrinsicIconScale = 1.0f;
    private int mLastTouchX;
    private int mLastTouchY;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    Paint mPaint;
    private final int mRegistrationX;
    private final int mRegistrationY;
    private final float mScaleOnDrop;
    /* access modifiers changed from: private */
    public Path mScaledMaskPath;
    private final int[] mTempLoc = new int[2];
    /* access modifiers changed from: private */
    public SpringFloatValue mTranslateX;
    /* access modifiers changed from: private */
    public SpringFloatValue mTranslateY;

    public DragView(Launcher launcher, Bitmap bitmap, int registrationX, int registrationY, final float initialScale, float scaleOnDrop, float finalScaleDps) {
        super(launcher);
        this.mLauncher = launcher;
        this.mDragLayer = launcher.getDragLayer();
        this.mDragController = launcher.getDragController();
        final float scale = (((float) bitmap.getWidth()) + finalScaleDps) / ((float) bitmap.getWidth());
        setScaleX(initialScale);
        setScaleY(initialScale);
        this.mAnim = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        this.mAnim.setDuration(150);
        this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = ((Float) animation.getAnimatedValue()).floatValue();
                DragView.this.setScaleX(initialScale + ((scale - initialScale) * value));
                DragView.this.setScaleY(initialScale + ((scale - initialScale) * value));
                if (DragView.sDragAlpha != 1.0f) {
                    DragView.this.setAlpha((DragView.sDragAlpha * value) + (1.0f - value));
                }
                if (DragView.this.getParent() == null) {
                    animation.cancel();
                }
            }
        });
        this.mAnim.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (!DragView.this.mAnimationCancelled) {
                    DragView.this.mDragController.onDragViewAnimationEnd();
                }
            }
        });
        this.mBitmap = bitmap;
        setDragRegion(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()));
        this.mRegistrationX = registrationX;
        this.mRegistrationY = registrationY;
        this.mInitialScale = initialScale;
        this.mScaleOnDrop = scaleOnDrop;
        int ms = View.MeasureSpec.makeMeasureSpec(0, 0);
        measure(ms, ms);
        this.mPaint = new Paint(2);
        this.mBlurSizeOutline = getResources().getDimensionPixelSize(R.dimen.blur_size_medium_outline);
        setElevation(getResources().getDimension(R.dimen.drag_elevation));
    }

    @TargetApi(26)
    public void setItemInfo(final ItemInfo info) {
        if (Utilities.ATLEAST_OREO) {
            if (info.itemType == 0 || info.itemType == 6 || info.itemType == 2) {
                new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        LauncherAppState appState = LauncherAppState.getInstance(DragView.this.mLauncher);
                        Object[] outObj = new Object[1];
                        final Drawable dr = DragView.this.getFullDrawable(info, appState, outObj);
                        if (dr instanceof AdaptiveIconDrawable) {
                            int w = DragView.this.mBitmap.getWidth();
                            int h = DragView.this.mBitmap.getHeight();
                            int blurMargin = ((int) DragView.this.mLauncher.getResources().getDimension(R.dimen.blur_size_medium_outline)) / 2;
                            Rect bounds = new Rect(0, 0, w, h);
                            bounds.inset(blurMargin, blurMargin);
                            Rect badgeBounds = new Rect(bounds);
                            Drawable unused = DragView.this.mBadge = DragView.this.getBadge(info, appState, outObj[0]);
                            DragView.this.mBadge.setBounds(badgeBounds);
                            LauncherIcons li = LauncherIcons.obtain(DragView.this.mLauncher);
                            Utilities.scaleRectAboutCenter(bounds, li.getNormalizer().getScale(dr, (RectF) null, (Path) null, (boolean[]) null));
                            li.recycle();
                            AdaptiveIconDrawable adaptiveIcon = (AdaptiveIconDrawable) dr;
                            Rect shrunkBounds = new Rect(bounds);
                            Utilities.scaleRectAboutCenter(shrunkBounds, 0.98f);
                            adaptiveIcon.setBounds(shrunkBounds);
                            final Path mask = adaptiveIcon.getIconMask();
                            LauncherAppState launcherAppState = appState;
                            SpringFloatValue unused2 = DragView.this.mTranslateX = new SpringFloatValue(DragView.this, ((float) w) * AdaptiveIconDrawable.getExtraInsetFraction());
                            SpringFloatValue unused3 = DragView.this.mTranslateY = new SpringFloatValue(DragView.this, ((float) h) * AdaptiveIconDrawable.getExtraInsetFraction());
                            bounds.inset((int) (((float) (-bounds.width())) * AdaptiveIconDrawable.getExtraInsetFraction()), (int) (((float) (-bounds.height())) * AdaptiveIconDrawable.getExtraInsetFraction()));
                            Drawable unused4 = DragView.this.mBgSpringDrawable = adaptiveIcon.getBackground();
                            if (DragView.this.mBgSpringDrawable == null) {
                                Drawable unused5 = DragView.this.mBgSpringDrawable = new ColorDrawable(0);
                            }
                            DragView.this.mBgSpringDrawable.setBounds(bounds);
                            Drawable unused6 = DragView.this.mFgSpringDrawable = adaptiveIcon.getForeground();
                            if (DragView.this.mFgSpringDrawable == null) {
                                Drawable unused7 = DragView.this.mFgSpringDrawable = new ColorDrawable(0);
                            }
                            DragView.this.mFgSpringDrawable.setBounds(bounds);
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                public void run() {
                                    Path unused = DragView.this.mScaledMaskPath = mask;
                                    boolean unused2 = DragView.this.mDrawBitmap = !(dr instanceof FolderAdaptiveIcon);
                                    if (info.isDisabled()) {
                                        FastBitmapDrawable d = new FastBitmapDrawable((Bitmap) null);
                                        d.setIsDisabled(true);
                                        ColorMatrixColorFilter unused3 = DragView.this.mBaseFilter = (ColorMatrixColorFilter) d.getColorFilter();
                                    }
                                    DragView.this.updateColorFilter();
                                }
                            });
                            return;
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public void updateColorFilter() {
        if (this.mCurrentFilter == null) {
            this.mPaint.setColorFilter((ColorFilter) null);
            if (this.mScaledMaskPath != null) {
                this.mBgSpringDrawable.setColorFilter(this.mBaseFilter);
                this.mFgSpringDrawable.setColorFilter(this.mBaseFilter);
                this.mBadge.setColorFilter(this.mBaseFilter);
            }
        } else {
            ColorMatrixColorFilter currentFilter = new ColorMatrixColorFilter(this.mCurrentFilter);
            this.mPaint.setColorFilter(currentFilter);
            if (this.mScaledMaskPath != null) {
                if (this.mBaseFilter != null) {
                    this.mBaseFilter.getColorMatrix(sTempMatrix1);
                    sTempMatrix2.set(this.mCurrentFilter);
                    sTempMatrix1.postConcat(sTempMatrix2);
                    currentFilter = new ColorMatrixColorFilter(sTempMatrix1);
                }
                this.mBgSpringDrawable.setColorFilter(currentFilter);
                this.mFgSpringDrawable.setColorFilter(currentFilter);
                this.mBadge.setColorFilter(currentFilter);
            }
        }
        invalidate();
    }

    /* access modifiers changed from: private */
    public Drawable getFullDrawable(ItemInfo info, LauncherAppState appState, Object[] outObj) {
        FolderAdaptiveIcon icon;
        if (info.itemType == 0) {
            LauncherActivityInfo activityInfo = LauncherAppsCompat.getInstance(this.mLauncher).resolveActivity(info.getIntent(), info.user);
            outObj[0] = activityInfo;
            if (activityInfo != null) {
                return appState.getIconCache().getFullResIcon(activityInfo, false);
            }
            return null;
        } else if (info.itemType == 6) {
            if (info instanceof PendingAddShortcutInfo) {
                ShortcutConfigActivityInfo activityInfo2 = ((PendingAddShortcutInfo) info).activityInfo;
                outObj[0] = activityInfo2;
                return activityInfo2.getFullResIcon(appState.getIconCache());
            }
            ShortcutKey key = ShortcutKey.fromItemInfo(info);
            DeepShortcutManager sm = DeepShortcutManager.getInstance(this.mLauncher);
            List<ShortcutInfoCompat> si = sm.queryForFullDetails(key.componentName.getPackageName(), Arrays.asList(new String[]{key.getId()}), key.user);
            if (si.isEmpty()) {
                return null;
            }
            outObj[0] = si.get(0);
            return sm.getShortcutIconDrawable(si.get(0), appState.getInvariantDeviceProfile().fillResIconDpi);
        } else if (info.itemType != 2 || (icon = FolderAdaptiveIcon.createFolderAdaptiveIcon(this.mLauncher, info.id, new Point(this.mBitmap.getWidth(), this.mBitmap.getHeight()))) == null) {
            return null;
        } else {
            outObj[0] = icon;
            return icon;
        }
    }

    /* access modifiers changed from: private */
    @TargetApi(26)
    public Drawable getBadge(ItemInfo info, LauncherAppState appState, Object obj) {
        ItemInfo itemInfo = info;
        Object obj2 = obj;
        int iconSize = appState.getInvariantDeviceProfile().iconBitmapSize;
        if (itemInfo.itemType == 6) {
            boolean iconBadged = (itemInfo instanceof ItemInfoWithIcon) && (((ItemInfoWithIcon) itemInfo).runtimeStatusFlags & 512) > 0;
            if ((itemInfo.id == -1 && !iconBadged) || !(obj2 instanceof ShortcutInfoCompat)) {
                return new FixedSizeEmptyDrawable(iconSize);
            }
            LauncherIcons li = LauncherIcons.obtain(appState.getContext());
            Bitmap badge = li.getShortcutInfoBadge((ShortcutInfoCompat) obj2, appState.getIconCache()).iconBitmap;
            li.recycle();
            float insetFraction = (((float) iconSize) - this.mLauncher.getResources().getDimension(R.dimen.profile_badge_size)) / ((float) iconSize);
            return new InsetDrawable(new FastBitmapDrawable(badge), insetFraction, insetFraction, 0.0f, 0.0f);
        } else if (itemInfo.itemType == 2) {
            return ((FolderAdaptiveIcon) obj2).getBadge();
        } else {
            return this.mLauncher.getPackageManager().getUserBadgedIcon(new FixedSizeEmptyDrawable(iconSize), itemInfo.user);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(this.mBitmap.getWidth(), this.mBitmap.getHeight());
    }

    public void setIntrinsicIconScaleFactor(float scale) {
        this.mIntrinsicIconScale = scale;
    }

    public float getIntrinsicIconScaleFactor() {
        return this.mIntrinsicIconScale;
    }

    public int getDragRegionLeft() {
        return this.mDragRegion.left;
    }

    public int getDragRegionTop() {
        return this.mDragRegion.top;
    }

    public int getDragRegionWidth() {
        return this.mDragRegion.width();
    }

    public int getDragRegionHeight() {
        return this.mDragRegion.height();
    }

    public void setDragVisualizeOffset(Point p) {
        this.mDragVisualizeOffset = p;
    }

    public Point getDragVisualizeOffset() {
        return this.mDragVisualizeOffset;
    }

    public void setDragRegion(Rect r) {
        this.mDragRegion = r;
    }

    public Rect getDragRegion() {
        return this.mDragRegion;
    }

    public Bitmap getPreviewBitmap() {
        return this.mBitmap;
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        boolean crossFade = true;
        this.mHasDrawn = true;
        if (this.mDrawBitmap) {
            if (this.mCrossFadeProgress <= 0.0f || this.mCrossFadeBitmap == null) {
                crossFade = false;
            }
            if (crossFade) {
                this.mPaint.setAlpha(crossFade ? (int) ((1.0f - this.mCrossFadeProgress) * 255.0f) : 255);
            }
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, this.mPaint);
            if (crossFade) {
                this.mPaint.setAlpha((int) (this.mCrossFadeProgress * 255.0f));
                int saveCount = canvas.save();
                canvas.scale((((float) this.mBitmap.getWidth()) * 1.0f) / ((float) this.mCrossFadeBitmap.getWidth()), (((float) this.mBitmap.getHeight()) * 1.0f) / ((float) this.mCrossFadeBitmap.getHeight()));
                canvas.drawBitmap(this.mCrossFadeBitmap, 0.0f, 0.0f, this.mPaint);
                canvas.restoreToCount(saveCount);
            }
        }
        if (this.mScaledMaskPath != null) {
            int cnt = canvas.save();
            canvas.clipPath(this.mScaledMaskPath);
            this.mBgSpringDrawable.draw(canvas);
            canvas.translate(this.mTranslateX.mValue, this.mTranslateY.mValue);
            this.mFgSpringDrawable.draw(canvas);
            canvas.restoreToCount(cnt);
            this.mBadge.draw(canvas);
        }
    }

    public void setCrossFadeBitmap(Bitmap crossFadeBitmap) {
        this.mCrossFadeBitmap = crossFadeBitmap;
    }

    public void crossFade(int duration) {
        ValueAnimator va = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        va.setDuration((long) duration);
        va.setInterpolator(Interpolators.DEACCEL_1_5);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                DragView.this.mCrossFadeProgress = animation.getAnimatedFraction();
                DragView.this.invalidate();
            }
        });
        va.start();
    }

    public void setColor(int color) {
        if (this.mPaint == null) {
            this.mPaint = new Paint(2);
        }
        if (color != 0) {
            ColorMatrix m1 = new ColorMatrix();
            m1.setSaturation(0.0f);
            ColorMatrix m2 = new ColorMatrix();
            Themes.setColorScaleOnMatrix(color, m2);
            m1.postConcat(m2);
            animateFilterTo(m1.getArray());
        } else if (this.mCurrentFilter == null) {
            updateColorFilter();
        } else {
            animateFilterTo(new ColorMatrix().getArray());
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v2, resolved type: java.lang.Object[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void animateFilterTo(float[] r5) {
        /*
            r4 = this;
            float[] r0 = r4.mCurrentFilter
            if (r0 != 0) goto L_0x000e
            android.graphics.ColorMatrix r0 = new android.graphics.ColorMatrix
            r0.<init>()
            float[] r0 = r0.getArray()
            goto L_0x0010
        L_0x000e:
            float[] r0 = r4.mCurrentFilter
        L_0x0010:
            int r1 = r0.length
            float[] r1 = java.util.Arrays.copyOf(r0, r1)
            r4.mCurrentFilter = r1
            android.animation.ValueAnimator r1 = r4.mFilterAnimator
            if (r1 == 0) goto L_0x0020
            android.animation.ValueAnimator r1 = r4.mFilterAnimator
            r1.cancel()
        L_0x0020:
            android.animation.FloatArrayEvaluator r1 = new android.animation.FloatArrayEvaluator
            float[] r2 = r4.mCurrentFilter
            r1.<init>(r2)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r0
            r3 = 1
            r2[r3] = r5
            android.animation.ValueAnimator r1 = android.animation.ValueAnimator.ofObject(r1, r2)
            r4.mFilterAnimator = r1
            android.animation.ValueAnimator r1 = r4.mFilterAnimator
            r2 = 120(0x78, double:5.93E-322)
            r1.setDuration(r2)
            android.animation.ValueAnimator r1 = r4.mFilterAnimator
            com.android.launcher3.dragndrop.DragView$5 r2 = new com.android.launcher3.dragndrop.DragView$5
            r2.<init>()
            r1.addUpdateListener(r2)
            android.animation.ValueAnimator r1 = r4.mFilterAnimator
            r1.start()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.dragndrop.DragView.animateFilterTo(float[]):void");
    }

    public boolean hasDrawn() {
        return this.mHasDrawn;
    }

    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        this.mPaint.setAlpha((int) (255.0f * alpha));
        invalidate();
    }

    public void show(int touchX, int touchY) {
        this.mDragLayer.addView(this);
        BaseDragLayer.LayoutParams lp = new BaseDragLayer.LayoutParams(0, 0);
        lp.width = this.mBitmap.getWidth();
        lp.height = this.mBitmap.getHeight();
        lp.customPosition = true;
        setLayoutParams(lp);
        move(touchX, touchY);
        post(new Runnable() {
            public void run() {
                DragView.this.mAnim.start();
            }
        });
    }

    public void cancelAnimation() {
        this.mAnimationCancelled = true;
        if (this.mAnim != null && this.mAnim.isRunning()) {
            this.mAnim.cancel();
        }
    }

    public void move(int touchX, int touchY) {
        if (touchX > 0 && touchY > 0 && this.mLastTouchX > 0 && this.mLastTouchY > 0 && this.mScaledMaskPath != null) {
            this.mTranslateX.animateToPos((float) (this.mLastTouchX - touchX));
            this.mTranslateY.animateToPos((float) (this.mLastTouchY - touchY));
        }
        this.mLastTouchX = touchX;
        this.mLastTouchY = touchY;
        applyTranslation();
    }

    public void animateTo(int toTouchX, int toTouchY, Runnable onCompleteRunnable, int duration) {
        this.mTempLoc[0] = toTouchX - this.mRegistrationX;
        this.mTempLoc[1] = toTouchY - this.mRegistrationY;
        this.mDragLayer.animateViewIntoPosition(this, this.mTempLoc, 1.0f, this.mScaleOnDrop, this.mScaleOnDrop, 0, onCompleteRunnable, duration);
    }

    public void animateShift(final int shiftX, final int shiftY) {
        if (!this.mAnim.isStarted()) {
            this.mAnimatedShiftX = shiftX;
            this.mAnimatedShiftY = shiftY;
            applyTranslation();
            this.mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float fraction = 1.0f - animation.getAnimatedFraction();
                    int unused = DragView.this.mAnimatedShiftX = (int) (((float) shiftX) * fraction);
                    int unused2 = DragView.this.mAnimatedShiftY = (int) (((float) shiftY) * fraction);
                    DragView.this.applyTranslation();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void applyTranslation() {
        setTranslationX((float) ((this.mLastTouchX - this.mRegistrationX) + this.mAnimatedShiftX));
        setTranslationY((float) ((this.mLastTouchY - this.mRegistrationY) + this.mAnimatedShiftY));
    }

    public void remove() {
        if (getParent() != null) {
            this.mDragLayer.removeView(this);
        }
    }

    public int getBlurSizeOutline() {
        return this.mBlurSizeOutline;
    }

    public float getInitialScale() {
        return this.mInitialScale;
    }

    private static class SpringFloatValue {
        private static final float DAMPENING_RATIO = 1.0f;
        private static final int PARALLAX_MAX_IN_DP = 8;
        private static final int STIFFNESS = 4000;
        private static final FloatPropertyCompat<SpringFloatValue> VALUE = new FloatPropertyCompat<SpringFloatValue>(LauncherSettings.Settings.EXTRA_VALUE) {
            public float getValue(SpringFloatValue object) {
                return object.mValue;
            }

            public void setValue(SpringFloatValue object, float value) {
                float unused = object.mValue = value;
                object.mView.invalidate();
            }
        };
        private final float mDelta;
        private final SpringAnimation mSpring;
        /* access modifiers changed from: private */
        public float mValue;
        /* access modifiers changed from: private */
        public final View mView;

        public SpringFloatValue(View view, float range) {
            this.mView = view;
            this.mSpring = ((SpringAnimation) ((SpringAnimation) new SpringAnimation(this, VALUE, 0.0f).setMinValue(-range)).setMaxValue(range)).setSpring(new SpringForce(0.0f).setDampingRatio(1.0f).setStiffness(4000.0f));
            this.mDelta = view.getResources().getDisplayMetrics().density * 8.0f;
        }

        public void animateToPos(float value) {
            this.mSpring.animateToFinalPosition(Utilities.boundToRange(value, -this.mDelta, this.mDelta));
        }
    }

    private static class FixedSizeEmptyDrawable extends ColorDrawable {
        private final int mSize;

        public FixedSizeEmptyDrawable(int size) {
            super(0);
            this.mSize = size;
        }

        public int getIntrinsicHeight() {
            return this.mSize;
        }

        public int getIntrinsicWidth() {
            return this.mSize;
        }
    }
}
