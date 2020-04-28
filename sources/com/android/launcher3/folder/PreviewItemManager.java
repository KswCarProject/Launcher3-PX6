package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import java.util.ArrayList;
import java.util.List;

public class PreviewItemManager {
    private static final int FINAL_ITEM_ANIMATION_DURATION = 200;
    static final int INITIAL_ITEM_ANIMATION_DURATION = 350;
    private static final int ITEM_SLIDE_IN_OUT_DISTANCE_PX = 200;
    private static final int SLIDE_IN_FIRST_PAGE_ANIMATION_DURATION = 300;
    private static final int SLIDE_IN_FIRST_PAGE_ANIMATION_DURATION_DELAY = 100;
    /* access modifiers changed from: private */
    public float mCurrentPageItemsTransX = 0.0f;
    /* access modifiers changed from: private */
    public ArrayList<PreviewItemDrawingParams> mCurrentPageParams = new ArrayList<>();
    private ArrayList<PreviewItemDrawingParams> mFirstPageParams = new ArrayList<>();
    private FolderIcon mIcon;
    private float mIntrinsicIconSize = -1.0f;
    private int mPrevTopPadding = -1;
    private Drawable mReferenceDrawable = null;
    private boolean mShouldSlideInFirstPage;
    private int mTotalWidth = -1;

    public PreviewItemManager(FolderIcon icon) {
        this.mIcon = icon;
    }

    public FolderPreviewItemAnim createFirstItemAnimation(boolean reverse, Runnable onCompleteRunnable) {
        if (reverse) {
            return new FolderPreviewItemAnim(this, this.mFirstPageParams.get(0), 0, 2, -1, -1, 200, onCompleteRunnable);
        }
        return new FolderPreviewItemAnim(this, this.mFirstPageParams.get(0), -1, -1, 0, 2, INITIAL_ITEM_ANIMATION_DURATION, onCompleteRunnable);
    }

    /* access modifiers changed from: package-private */
    public Drawable prepareCreateAnimation(View destView) {
        Drawable animateDrawable = ((TextView) destView).getCompoundDrawables()[1];
        computePreviewDrawingParams(animateDrawable.getIntrinsicWidth(), destView.getMeasuredWidth());
        this.mReferenceDrawable = animateDrawable;
        return animateDrawable;
    }

    public void recomputePreviewDrawingParams() {
        if (this.mReferenceDrawable != null) {
            computePreviewDrawingParams(this.mReferenceDrawable.getIntrinsicWidth(), this.mIcon.getMeasuredWidth());
        }
    }

    private void computePreviewDrawingParams(int drawableSize, int totalSize) {
        if (this.mIntrinsicIconSize != ((float) drawableSize) || this.mTotalWidth != totalSize || this.mPrevTopPadding != this.mIcon.getPaddingTop()) {
            this.mIntrinsicIconSize = (float) drawableSize;
            this.mTotalWidth = totalSize;
            this.mPrevTopPadding = this.mIcon.getPaddingTop();
            this.mIcon.mBackground.setup(this.mIcon.mLauncher, this.mIcon, this.mTotalWidth, this.mIcon.getPaddingTop());
            this.mIcon.mPreviewLayoutRule.init(this.mIcon.mBackground.previewSize, this.mIntrinsicIconSize, Utilities.isRtl(this.mIcon.getResources()));
            updatePreviewItems(false);
        }
    }

    /* access modifiers changed from: package-private */
    public PreviewItemDrawingParams computePreviewItemDrawingParams(int index, int curNumItems, PreviewItemDrawingParams params) {
        if (index == -1) {
            return getFinalIconParams(params);
        }
        return this.mIcon.mPreviewLayoutRule.computePreviewItemDrawingParams(index, curNumItems, params);
    }

    private PreviewItemDrawingParams getFinalIconParams(PreviewItemDrawingParams params) {
        float iconSize = (float) this.mIcon.mLauncher.getDeviceProfile().iconSizePx;
        float trans = (((float) this.mIcon.mBackground.previewSize) - iconSize) / 2.0f;
        params.update(trans, trans, iconSize / ((float) this.mReferenceDrawable.getIntrinsicWidth()));
        return params;
    }

    public void drawParams(Canvas canvas, ArrayList<PreviewItemDrawingParams> params, float transX) {
        canvas.translate(transX, 0.0f);
        for (int i = params.size() - 1; i >= 0; i--) {
            PreviewItemDrawingParams p = params.get(i);
            if (!p.hidden) {
                drawPreviewItem(canvas, p);
            }
        }
        canvas.translate(-transX, 0.0f);
    }

    public void draw(Canvas canvas) {
        PreviewBackground bg = this.mIcon.getFolderBackground();
        canvas.translate((float) bg.basePreviewOffsetX, (float) bg.basePreviewOffsetY);
        float firstPageItemsTransX = 0.0f;
        if (this.mShouldSlideInFirstPage) {
            drawParams(canvas, this.mCurrentPageParams, this.mCurrentPageItemsTransX);
            firstPageItemsTransX = this.mCurrentPageItemsTransX - 0.022460938f;
        }
        drawParams(canvas, this.mFirstPageParams, firstPageItemsTransX);
        canvas.translate((float) (-bg.basePreviewOffsetX), (float) (-bg.basePreviewOffsetY));
    }

    public void onParamsChanged() {
        this.mIcon.invalidate();
    }

    private void drawPreviewItem(Canvas canvas, PreviewItemDrawingParams params) {
        canvas.save();
        canvas.translate(params.transX, params.transY);
        canvas.scale(params.scale, params.scale);
        Drawable d = params.drawable;
        if (d != null) {
            Rect bounds = d.getBounds();
            canvas.save();
            canvas.translate((float) (-bounds.left), (float) (-bounds.top));
            canvas.scale(this.mIntrinsicIconSize / ((float) bounds.width()), this.mIntrinsicIconSize / ((float) bounds.height()));
            d.draw(canvas);
            canvas.restore();
        }
        canvas.restore();
    }

    public void hidePreviewItem(int index, boolean hidden) {
        int index2 = index + Math.max(this.mFirstPageParams.size() - 4, 0);
        PreviewItemDrawingParams params = index2 < this.mFirstPageParams.size() ? this.mFirstPageParams.get(index2) : null;
        if (params != null) {
            params.hidden = hidden;
        }
    }

    /* access modifiers changed from: package-private */
    public void buildParamsForPage(int page, ArrayList<PreviewItemDrawingParams> params, boolean animate) {
        char c;
        int i = page;
        ArrayList<PreviewItemDrawingParams> arrayList = params;
        List<BubbleTextView> items = this.mIcon.getPreviewItemsOnPage(i);
        int prevNumItems = params.size();
        while (true) {
            c = 1;
            if (items.size() >= params.size()) {
                break;
            }
            arrayList.remove(params.size() - 1);
        }
        while (items.size() > params.size()) {
            arrayList.add(new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f));
        }
        int numItemsInFirstPagePreview = i == 0 ? items.size() : 4;
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 < params.size()) {
                PreviewItemDrawingParams p = arrayList.get(i3);
                p.drawable = items.get(i3).getCompoundDrawables()[c];
                if (p.drawable != null && !this.mIcon.mFolder.isOpen()) {
                    p.drawable.setCallback(this.mIcon);
                }
                if (!animate) {
                    computePreviewItemDrawingParams(i3, numItemsInFirstPagePreview, p);
                    if (this.mReferenceDrawable == null) {
                        this.mReferenceDrawable = p.drawable;
                    }
                } else {
                    PreviewItemDrawingParams p2 = p;
                    FolderPreviewItemAnim anim = new FolderPreviewItemAnim(this, p, i3, prevNumItems, i3, numItemsInFirstPagePreview, 400, (Runnable) null);
                    if (p2.anim != null) {
                        if (!p2.anim.hasEqualFinalState(anim)) {
                            p2.anim.cancel();
                        }
                    }
                    p2.anim = anim;
                    p2.anim.start();
                }
                i2 = i3 + 1;
                c = 1;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onFolderClose(int currentPage) {
        this.mShouldSlideInFirstPage = currentPage != 0;
        if (this.mShouldSlideInFirstPage) {
            this.mCurrentPageItemsTransX = 0.0f;
            buildParamsForPage(currentPage, this.mCurrentPageParams, false);
            onParamsChanged();
            ValueAnimator slideAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 200.0f});
            slideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float unused = PreviewItemManager.this.mCurrentPageItemsTransX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    PreviewItemManager.this.onParamsChanged();
                }
            });
            slideAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    PreviewItemManager.this.mCurrentPageParams.clear();
                }
            });
            slideAnimator.setStartDelay(100);
            slideAnimator.setDuration(300);
            slideAnimator.start();
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePreviewItems(boolean animate) {
        buildParamsForPage(0, this.mFirstPageParams, animate);
    }

    /* access modifiers changed from: package-private */
    public boolean verifyDrawable(@NonNull Drawable who) {
        for (int i = 0; i < this.mFirstPageParams.size(); i++) {
            if (this.mFirstPageParams.get(i).drawable == who) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public float getIntrinsicIconSize() {
        return this.mIntrinsicIconSize;
    }

    public void onDrop(List<BubbleTextView> oldParams, List<BubbleTextView> newParams, ShortcutInfo dropped) {
        List<BubbleTextView> list = oldParams;
        List<BubbleTextView> list2 = newParams;
        int numItems = newParams.size();
        ArrayList<PreviewItemDrawingParams> params = this.mFirstPageParams;
        buildParamsForPage(0, params, false);
        List<BubbleTextView> moveIn = new ArrayList<>();
        for (BubbleTextView btv : newParams) {
            if (list.contains(btv)) {
                ShortcutInfo shortcutInfo = dropped;
            } else if (!btv.getTag().equals(dropped)) {
                moveIn.add(btv);
            }
        }
        ShortcutInfo shortcutInfo2 = dropped;
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 >= moveIn.size()) {
                break;
            }
            int prevIndex = list2.indexOf(moveIn.get(i2));
            PreviewItemDrawingParams p = params.get(prevIndex);
            computePreviewItemDrawingParams(prevIndex, numItems, p);
            PreviewItemDrawingParams previewItemDrawingParams = p;
            PreviewItemDrawingParams previewItemDrawingParams2 = p;
            updateTransitionParam(previewItemDrawingParams, moveIn.get(i2), -3, list2.indexOf(moveIn.get(i2)), numItems);
            i = i2 + 1;
        }
        int newIndex = 0;
        while (true) {
            int newIndex2 = newIndex;
            if (newIndex2 >= newParams.size()) {
                break;
            }
            int oldIndex = list.indexOf(list2.get(newIndex2));
            if (oldIndex >= 0 && newIndex2 != oldIndex) {
                updateTransitionParam(params.get(newIndex2), list2.get(newIndex2), oldIndex, newIndex2, numItems);
            }
            newIndex = newIndex2 + 1;
        }
        List<BubbleTextView> moveOut = new ArrayList<>(list);
        moveOut.removeAll(list2);
        int i3 = 0;
        while (true) {
            int i4 = i3;
            if (i4 >= moveOut.size()) {
                break;
            }
            BubbleTextView item = moveOut.get(i4);
            int oldIndex2 = list.indexOf(item);
            PreviewItemDrawingParams p2 = computePreviewItemDrawingParams(oldIndex2, numItems, (PreviewItemDrawingParams) null);
            int i5 = oldIndex2;
            BubbleTextView bubbleTextView = item;
            updateTransitionParam(p2, item, oldIndex2, -2, numItems);
            params.add(0, p2);
            i3 = i4 + 1;
        }
        for (int i6 = 0; i6 < params.size(); i6++) {
            if (params.get(i6).anim != null) {
                params.get(i6).anim.start();
            }
        }
    }

    private void updateTransitionParam(PreviewItemDrawingParams p, BubbleTextView btv, int prevIndex, int newIndex, int numItems) {
        PreviewItemDrawingParams previewItemDrawingParams = p;
        previewItemDrawingParams.drawable = btv.getCompoundDrawables()[1];
        if (!this.mIcon.mFolder.isOpen()) {
            previewItemDrawingParams.drawable.setCallback(this.mIcon);
        }
        FolderPreviewItemAnim anim = new FolderPreviewItemAnim(this, p, prevIndex, numItems, newIndex, numItems, 400, (Runnable) null);
        if (previewItemDrawingParams.anim != null && !previewItemDrawingParams.anim.hasEqualFinalState(anim)) {
            previewItemDrawingParams.anim.cancel();
        }
        previewItemDrawingParams.anim = anim;
    }
}
