package com.android.launcher3.folder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import com.android.launcher3.LauncherAnimUtils;

class FolderPreviewItemAnim {
    private static PreviewItemDrawingParams sTmpParams = new PreviewItemDrawingParams(0.0f, 0.0f, 0.0f, 0.0f);
    float finalScale = sTmpParams.scale;
    float finalTransX = sTmpParams.transX;
    float finalTransY = sTmpParams.transY;
    private ValueAnimator mValueAnimator;

    FolderPreviewItemAnim(PreviewItemManager previewItemManager, PreviewItemDrawingParams params, int index0, int items0, int index1, int items1, int duration, Runnable onCompleteRunnable) {
        PreviewItemManager previewItemManager2 = previewItemManager;
        previewItemManager2.computePreviewItemDrawingParams(index1, items1, sTmpParams);
        previewItemManager2.computePreviewItemDrawingParams(index0, items0, sTmpParams);
        float scale0 = sTmpParams.scale;
        float transX0 = sTmpParams.transX;
        float transY0 = sTmpParams.transY;
        this.mValueAnimator = LauncherAnimUtils.ofFloat(0.0f, 1.0f);
        final PreviewItemDrawingParams previewItemDrawingParams = params;
        final float f = transX0;
        final float f2 = transY0;
        AnonymousClass1 r8 = r0;
        final float f3 = scale0;
        ValueAnimator valueAnimator = this.mValueAnimator;
        final PreviewItemManager previewItemManager3 = previewItemManager;
        AnonymousClass1 r0 = new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                float progress = animation.getAnimatedFraction();
                previewItemDrawingParams.transX = f + ((FolderPreviewItemAnim.this.finalTransX - f) * progress);
                previewItemDrawingParams.transY = f2 + ((FolderPreviewItemAnim.this.finalTransY - f2) * progress);
                previewItemDrawingParams.scale = f3 + ((FolderPreviewItemAnim.this.finalScale - f3) * progress);
                previewItemManager3.onParamsChanged();
            }
        };
        valueAnimator.addUpdateListener(r8);
        final Runnable runnable = onCompleteRunnable;
        this.mValueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (runnable != null) {
                    runnable.run();
                }
                previewItemDrawingParams.anim = null;
            }
        });
        this.mValueAnimator.setDuration((long) duration);
    }

    public void start() {
        this.mValueAnimator.start();
    }

    public void cancel() {
        this.mValueAnimator.cancel();
    }

    public boolean hasEqualFinalState(FolderPreviewItemAnim anim) {
        return this.finalTransY == anim.finalTransY && this.finalTransX == anim.finalTransX && this.finalScale == anim.finalScale;
    }
}
