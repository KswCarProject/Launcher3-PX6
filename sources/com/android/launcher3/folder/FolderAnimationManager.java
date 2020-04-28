package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.graphics.ColorUtils;
import android.util.Property;
import android.view.View;
import android.view.animation.AnimationUtils;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.BaseDragLayer;
import java.util.Iterator;
import java.util.List;

public class FolderAnimationManager {
    private FolderPagedView mContent;
    private Context mContext;
    private final int mDelay;
    private final int mDuration;
    /* access modifiers changed from: private */
    public Folder mFolder;
    private GradientDrawable mFolderBackground;
    private FolderIcon mFolderIcon;
    private final TimeInterpolator mFolderInterpolator;
    /* access modifiers changed from: private */
    public final boolean mIsOpening;
    private final TimeInterpolator mLargeFolderPreviewItemCloseInterpolator;
    private final TimeInterpolator mLargeFolderPreviewItemOpenInterpolator;
    private Launcher mLauncher;
    private PreviewBackground mPreviewBackground;
    private final PreviewItemDrawingParams mTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f);

    public FolderAnimationManager(Folder folder, boolean isOpening) {
        this.mFolder = folder;
        this.mContent = folder.mContent;
        this.mFolderBackground = (GradientDrawable) this.mFolder.getBackground();
        this.mFolderIcon = folder.mFolderIcon;
        this.mPreviewBackground = this.mFolderIcon.mBackground;
        this.mContext = folder.getContext();
        this.mLauncher = folder.mLauncher;
        this.mIsOpening = isOpening;
        Resources res = this.mContent.getResources();
        this.mDuration = res.getInteger(R.integer.config_materialFolderExpandDuration);
        this.mDelay = res.getInteger(R.integer.config_folderDelay);
        this.mFolderInterpolator = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.folder_interpolator);
        this.mLargeFolderPreviewItemOpenInterpolator = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.large_folder_preview_item_open_interpolator);
        this.mLargeFolderPreviewItemCloseInterpolator = AnimationUtils.loadInterpolator(this.mContext, R.interpolator.large_folder_preview_item_close_interpolator);
    }

    public AnimatorSet getAnimator() {
        BaseDragLayer.LayoutParams lp = (BaseDragLayer.LayoutParams) this.mFolder.getLayoutParams();
        ClippedFolderIconLayoutRule rule = this.mFolderIcon.getLayoutRule();
        List<BubbleTextView> itemsInPreview = this.mFolderIcon.getPreviewItems();
        Rect folderIconPos = new Rect();
        float scaleRelativeToDragLayer = this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this.mFolderIcon, folderIconPos);
        int scaledRadius = this.mPreviewBackground.getScaledRadius();
        float initialSize = ((float) (scaledRadius * 2)) * scaleRelativeToDragLayer;
        float previewSize = rule.getIconSize() * rule.scaleForItem(itemsInPreview.size());
        float initialScale = (previewSize / ((float) itemsInPreview.get(0).getIconSize())) * scaleRelativeToDragLayer;
        float scale = this.mIsOpening ? initialScale : 1.0f;
        this.mFolder.setScaleX(scale);
        this.mFolder.setScaleY(scale);
        this.mFolder.setPivotX(0.0f);
        this.mFolder.setPivotY(0.0f);
        int previewItemOffsetX = (int) (previewSize / 2.0f);
        if (Utilities.isRtl(this.mContext.getResources())) {
            previewItemOffsetX = (int) (((((float) lp.width) * initialScale) - initialSize) - ((float) previewItemOffsetX));
        }
        int previewItemOffsetX2 = previewItemOffsetX;
        int paddingOffsetX = (int) (((float) (this.mFolder.getPaddingLeft() + this.mContent.getPaddingLeft())) * initialScale);
        int paddingOffsetY = (int) (((float) (this.mFolder.getPaddingTop() + this.mContent.getPaddingTop())) * initialScale);
        int initialX = ((folderIconPos.left + this.mPreviewBackground.getOffsetX()) - paddingOffsetX) - previewItemOffsetX2;
        int initialY = (folderIconPos.top + this.mPreviewBackground.getOffsetY()) - paddingOffsetY;
        float xDistance = (float) (initialX - lp.x);
        float yDistance = (float) (initialY - lp.y);
        int finalColor = Themes.getAttrColor(this.mContext, 16843827);
        int initialColor = ColorUtils.setAlphaComponent(finalColor, this.mPreviewBackground.getBackgroundAlpha());
        this.mFolderBackground.setColor(this.mIsOpening ? initialColor : finalColor);
        int totalOffsetX = paddingOffsetX + previewItemOffsetX2;
        float yDistance2 = yDistance;
        int totalOffsetX2 = totalOffsetX;
        Rect rect = new Rect(Math.round(((float) totalOffsetX) / initialScale), Math.round(((float) paddingOffsetY) / initialScale), Math.round((((float) totalOffsetX) + initialSize) / initialScale), Math.round((((float) paddingOffsetY) + initialSize) / initialScale));
        int initialColor2 = initialColor;
        Rect startRect = rect;
        int paddingOffsetY2 = paddingOffsetY;
        Rect endRect = new Rect(0, 0, lp.width, lp.height);
        float yDistance3 = yDistance2;
        float initialRadius = (initialSize / initialScale) / 2.0f;
        float finalRadius = (float) Utilities.pxFromDp(2.0f, this.mContext.getResources().getDisplayMetrics());
        AnimatorSet a = LauncherAnimUtils.createAnimatorSet();
        float finalRadius2 = finalRadius;
        int paddingOffsetX2 = paddingOffsetX;
        float f = scale;
        PropertyResetListener colorResetListener = new PropertyResetListener(BubbleTextView.TEXT_ALPHA_PROPERTY, Float.valueOf(1.0f));
        Iterator<BubbleTextView> it = this.mFolder.getItemsOnPage(this.mFolder.mContent.getCurrentPage()).iterator();
        while (it.hasNext()) {
            BubbleTextView icon = it.next();
            Iterator<BubbleTextView> it2 = it;
            if (this.mIsOpening) {
                icon.setTextVisibility(false);
            }
            ObjectAnimator anim = icon.createTextAlphaAnimator(this.mIsOpening);
            anim.addListener(colorResetListener);
            play(a, anim);
            it = it2;
        }
        PropertyResetListener colorResetListener2 = colorResetListener;
        play(a, getAnimator((View) this.mFolder, View.TRANSLATION_X, xDistance, 0.0f));
        play(a, getAnimator((View) this.mFolder, View.TRANSLATION_Y, yDistance3, 0.0f));
        play(a, getAnimator((View) this.mFolder, (Property) LauncherAnimUtils.SCALE_PROPERTY, initialScale, 1.0f));
        play(a, getAnimator(this.mFolderBackground, "color", initialColor2, finalColor));
        play(a, this.mFolderIcon.mFolderName.createTextAlphaAnimator(!this.mIsOpening));
        AnimatorSet a2 = a;
        int i = initialColor2;
        int i2 = totalOffsetX2;
        float f2 = xDistance;
        AnonymousClass1 r0 = new RoundedRectRevealOutlineProvider(initialRadius, finalRadius2, startRect, endRect) {
            public boolean shouldRemoveElevationDuringAnimation() {
                return true;
            }
        };
        play(a2, r0.createRevealAnimator(this.mFolder, !this.mIsOpening));
        int midDuration = this.mDuration / 2;
        AnonymousClass1 r38 = r0;
        Animator z = getAnimator((View) this.mFolder, View.TRANSLATION_Z, -this.mFolder.getElevation(), 0.0f);
        int i3 = finalColor;
        int i4 = paddingOffsetY2;
        float f3 = yDistance3;
        int i5 = paddingOffsetX2;
        Animator animator = z;
        Animator animator2 = z;
        float initialScale2 = initialScale;
        AnimatorSet a3 = a2;
        PropertyResetListener propertyResetListener = colorResetListener2;
        Rect rect2 = folderIconPos;
        play(a2, animator, this.mIsOpening ? (long) midDuration : 0, midDuration);
        a3.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                FolderAnimationManager.this.mFolder.setTranslationX(0.0f);
                FolderAnimationManager.this.mFolder.setTranslationY(0.0f);
                FolderAnimationManager.this.mFolder.setTranslationZ(0.0f);
                FolderAnimationManager.this.mFolder.setScaleX(1.0f);
                FolderAnimationManager.this.mFolder.setScaleY(1.0f);
            }
        });
        Iterator<Animator> it3 = a3.getChildAnimations().iterator();
        while (it3.hasNext()) {
            it3.next().setInterpolator(this.mFolderInterpolator);
        }
        int radiusDiff = scaledRadius - this.mPreviewBackground.getRadius();
        addPreviewItemAnimators(a3, initialScale2 / scaleRelativeToDragLayer, previewItemOffsetX2 + radiusDiff, radiusDiff);
        return a3;
    }

    private void addPreviewItemAnimators(AnimatorSet animatorSet, float folderScale, int previewItemOffsetX, int previewItemOffsetY) {
        List<BubbleTextView> list;
        boolean isOnFirstPage;
        TimeInterpolator previewItemInterpolator;
        FolderAnimationManager folderAnimationManager = this;
        AnimatorSet animatorSet2 = animatorSet;
        ClippedFolderIconLayoutRule rule = folderAnimationManager.mFolderIcon.getLayoutRule();
        int i = 0;
        boolean z = true;
        boolean isOnFirstPage2 = folderAnimationManager.mFolder.mContent.getCurrentPage() == 0;
        if (isOnFirstPage2) {
            list = folderAnimationManager.mFolderIcon.getPreviewItems();
        } else {
            list = folderAnimationManager.mFolderIcon.getPreviewItemsOnPage(folderAnimationManager.mFolder.mContent.getCurrentPage());
        }
        List<BubbleTextView> itemsInPreview = list;
        int numItemsInPreview = itemsInPreview.size();
        int numItemsInFirstPagePreview = isOnFirstPage2 ? numItemsInPreview : 4;
        TimeInterpolator previewItemInterpolator2 = getPreviewItemInterpolator();
        ShortcutAndWidgetContainer cwc = folderAnimationManager.mContent.getPageAt(0).getShortcutsAndWidgets();
        while (true) {
            int i2 = i;
            if (i2 < numItemsInPreview) {
                final BubbleTextView btv = itemsInPreview.get(i2);
                CellLayout.LayoutParams btvLp = (CellLayout.LayoutParams) btv.getLayoutParams();
                btvLp.isLockedToGrid = z;
                cwc.setupLp(btv);
                float iconScale = (rule.getIconSize() * rule.scaleForItem(numItemsInFirstPagePreview)) / ((float) itemsInPreview.get(i2).getIconSize());
                float initialScale = iconScale / folderScale;
                float scale = folderAnimationManager.mIsOpening ? initialScale : 1.0f;
                btv.setScaleX(scale);
                btv.setScaleY(scale);
                rule.computePreviewItemDrawingParams(i2, numItemsInFirstPagePreview, folderAnimationManager.mTmpParams);
                int i3 = i2;
                ShortcutAndWidgetContainer cwc2 = cwc;
                ClippedFolderIconLayoutRule rule2 = rule;
                int previewPosX = (int) (((folderAnimationManager.mTmpParams.transX - ((float) (((int) (((float) (btvLp.width - btv.getIconSize())) * iconScale)) / 2))) + ((float) previewItemOffsetX)) / folderScale);
                float f = scale;
                int previewPosY = (int) ((folderAnimationManager.mTmpParams.transY + ((float) previewItemOffsetY)) / folderScale);
                float xDistance = (float) (previewPosX - btvLp.x);
                float yDistance = (float) (previewPosY - btvLp.y);
                CellLayout.LayoutParams btvLp2 = btvLp;
                int previewPosY2 = previewPosY;
                Animator translationX = folderAnimationManager.getAnimator((View) btv, View.TRANSLATION_X, xDistance, 0.0f);
                translationX.setInterpolator(previewItemInterpolator2);
                folderAnimationManager.play(animatorSet2, translationX);
                float xDistance2 = xDistance;
                Animator translationY = folderAnimationManager.getAnimator((View) btv, View.TRANSLATION_Y, yDistance, 0.0f);
                translationY.setInterpolator(previewItemInterpolator2);
                folderAnimationManager.play(animatorSet2, translationY);
                int i4 = previewPosX;
                Animator scaleAnimator = folderAnimationManager.getAnimator((View) btv, (Property) LauncherAnimUtils.SCALE_PROPERTY, initialScale, 1.0f);
                scaleAnimator.setInterpolator(previewItemInterpolator2);
                folderAnimationManager.play(animatorSet2, scaleAnimator);
                if (folderAnimationManager.mFolder.getItemCount() > 4) {
                    int delay = folderAnimationManager.mIsOpening ? folderAnimationManager.mDelay : folderAnimationManager.mDelay * 2;
                    if (folderAnimationManager.mIsOpening) {
                        previewItemInterpolator = previewItemInterpolator2;
                        translationX.setStartDelay((long) delay);
                        translationY.setStartDelay((long) delay);
                        scaleAnimator.setStartDelay((long) delay);
                    } else {
                        previewItemInterpolator = previewItemInterpolator2;
                    }
                    isOnFirstPage = isOnFirstPage2;
                    translationX.setDuration(translationX.getDuration() - ((long) delay));
                    translationY.setDuration(translationY.getDuration() - ((long) delay));
                    scaleAnimator.setDuration(scaleAnimator.getDuration() - ((long) delay));
                } else {
                    previewItemInterpolator = previewItemInterpolator2;
                    isOnFirstPage = isOnFirstPage2;
                }
                Animator animator = translationX;
                CellLayout.LayoutParams layoutParams = btvLp2;
                BubbleTextView bubbleTextView = btv;
                final float f2 = xDistance2;
                int i5 = previewPosY2;
                Animator animator2 = translationY;
                final float f3 = yDistance;
                final float f4 = initialScale;
                animatorSet2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (FolderAnimationManager.this.mIsOpening) {
                            btv.setTranslationX(f2);
                            btv.setTranslationY(f3);
                            btv.setScaleX(f4);
                            btv.setScaleY(f4);
                        }
                    }

                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        btv.setTranslationX(0.0f);
                        btv.setTranslationY(0.0f);
                        btv.setScaleX(1.0f);
                        btv.setScaleY(1.0f);
                    }
                });
                i = i3 + 1;
                cwc = cwc2;
                rule = rule2;
                previewItemInterpolator2 = previewItemInterpolator;
                isOnFirstPage2 = isOnFirstPage;
                folderAnimationManager = this;
                z = true;
            } else {
                TimeInterpolator timeInterpolator = previewItemInterpolator2;
                ClippedFolderIconLayoutRule clippedFolderIconLayoutRule = rule;
                boolean z2 = isOnFirstPage2;
                return;
            }
        }
    }

    private void play(AnimatorSet as, Animator a) {
        play(as, a, a.getStartDelay(), this.mDuration);
    }

    private void play(AnimatorSet as, Animator a, long startDelay, int duration) {
        a.setStartDelay(startDelay);
        a.setDuration((long) duration);
        as.play(a);
    }

    private TimeInterpolator getPreviewItemInterpolator() {
        if (this.mFolder.getItemCount() > 4) {
            return this.mIsOpening ? this.mLargeFolderPreviewItemOpenInterpolator : this.mLargeFolderPreviewItemCloseInterpolator;
        }
        return this.mFolderInterpolator;
    }

    private Animator getAnimator(View view, Property property, float v1, float v2) {
        if (this.mIsOpening) {
            return ObjectAnimator.ofFloat(view, property, new float[]{v1, v2});
        }
        return ObjectAnimator.ofFloat(view, property, new float[]{v2, v1});
    }

    private Animator getAnimator(GradientDrawable drawable, String property, int v1, int v2) {
        if (this.mIsOpening) {
            return ObjectAnimator.ofArgb(drawable, property, new int[]{v1, v2});
        }
        return ObjectAnimator.ofArgb(drawable, property, new int[]{v2, v1});
    }
}
