package com.android.launcher3.allapps;

import android.support.v7.widget.RecyclerView;
import com.android.launcher3.allapps.AllAppsGridAdapter;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AllAppsFastScrollHelper implements AllAppsGridAdapter.BindViewCallback {
    private static final int INITIAL_TOUCH_SETTLING_DURATION = 100;
    private static final int REPEAT_TOUCH_SETTLING_DURATION = 200;
    private AlphabeticalAppsList mApps;
    String mCurrentFastScrollSection;
    int mFastScrollFrameIndex;
    final int[] mFastScrollFrames = new int[10];
    Runnable mFastScrollToTargetSectionRunnable = new Runnable() {
        public void run() {
            AllAppsFastScrollHelper.this.mCurrentFastScrollSection = AllAppsFastScrollHelper.this.mTargetFastScrollSection;
            boolean unused = AllAppsFastScrollHelper.this.mHasFastScrollTouchSettled = true;
            boolean unused2 = AllAppsFastScrollHelper.this.mHasFastScrollTouchSettledAtLeastOnce = true;
            AllAppsFastScrollHelper.this.updateTrackedViewsFastScrollFocusState();
        }
    };
    /* access modifiers changed from: private */
    public boolean mHasFastScrollTouchSettled;
    /* access modifiers changed from: private */
    public boolean mHasFastScrollTouchSettledAtLeastOnce;
    /* access modifiers changed from: private */
    public AllAppsRecyclerView mRv;
    Runnable mSmoothSnapNextFrameRunnable = new Runnable() {
        public void run() {
            if (AllAppsFastScrollHelper.this.mFastScrollFrameIndex < AllAppsFastScrollHelper.this.mFastScrollFrames.length) {
                AllAppsFastScrollHelper.this.mRv.scrollBy(0, AllAppsFastScrollHelper.this.mFastScrollFrames[AllAppsFastScrollHelper.this.mFastScrollFrameIndex]);
                AllAppsFastScrollHelper.this.mFastScrollFrameIndex++;
                AllAppsFastScrollHelper.this.mRv.postOnAnimation(AllAppsFastScrollHelper.this.mSmoothSnapNextFrameRunnable);
            }
        }
    };
    int mTargetFastScrollPosition = -1;
    String mTargetFastScrollSection;
    private HashSet<RecyclerView.ViewHolder> mTrackedFastScrollViews = new HashSet<>();

    public AllAppsFastScrollHelper(AllAppsRecyclerView rv, AlphabeticalAppsList apps) {
        this.mRv = rv;
        this.mApps = apps;
    }

    public void onSetAdapter(AllAppsGridAdapter adapter) {
        adapter.setBindViewCallback(this);
    }

    public boolean smoothScrollToSection(int scrollY, int availableScrollHeight, AlphabeticalAppsList.FastScrollSectionInfo info) {
        if (this.mTargetFastScrollPosition == info.fastScrollToItem.position) {
            return false;
        }
        this.mTargetFastScrollPosition = info.fastScrollToItem.position;
        smoothSnapToPosition(scrollY, availableScrollHeight, info);
        return true;
    }

    private void smoothSnapToPosition(int scrollY, int availableScrollHeight, AlphabeticalAppsList.FastScrollSectionInfo info) {
        this.mRv.removeCallbacks(this.mSmoothSnapNextFrameRunnable);
        this.mRv.removeCallbacks(this.mFastScrollToTargetSectionRunnable);
        trackAllChildViews();
        if (this.mHasFastScrollTouchSettled) {
            this.mCurrentFastScrollSection = info.sectionName;
            this.mTargetFastScrollSection = null;
            updateTrackedViewsFastScrollFocusState();
        } else {
            this.mCurrentFastScrollSection = null;
            this.mTargetFastScrollSection = info.sectionName;
            this.mHasFastScrollTouchSettled = false;
            updateTrackedViewsFastScrollFocusState();
            this.mRv.postDelayed(this.mFastScrollToTargetSectionRunnable, this.mHasFastScrollTouchSettledAtLeastOnce ? 200 : 100);
        }
        List<AlphabeticalAppsList.FastScrollSectionInfo> fastScrollSections = this.mApps.getFastScrollerSections();
        int newScrollY = (fastScrollSections.size() <= 0 || fastScrollSections.get(0) != info) ? Math.min(availableScrollHeight, this.mRv.getCurrentScrollY(info.fastScrollToItem.position, 0)) : 0;
        int numFrames = this.mFastScrollFrames.length;
        int deltaY = newScrollY - scrollY;
        float ySign = Math.signum((float) deltaY);
        int step = (int) (((double) ySign) * Math.ceil((double) (((float) Math.abs(deltaY)) / ((float) numFrames))));
        int deltaY2 = deltaY;
        for (int i = 0; i < numFrames; i++) {
            this.mFastScrollFrames[i] = (int) (((float) Math.min(Math.abs(step), Math.abs(deltaY2))) * ySign);
            deltaY2 -= step;
        }
        this.mFastScrollFrameIndex = 0;
        this.mRv.postOnAnimation(this.mSmoothSnapNextFrameRunnable);
    }

    public void onFastScrollCompleted() {
        this.mRv.removeCallbacks(this.mSmoothSnapNextFrameRunnable);
        this.mRv.removeCallbacks(this.mFastScrollToTargetSectionRunnable);
        this.mHasFastScrollTouchSettled = false;
        this.mHasFastScrollTouchSettledAtLeastOnce = false;
        this.mCurrentFastScrollSection = null;
        this.mTargetFastScrollSection = null;
        this.mTargetFastScrollPosition = -1;
        updateTrackedViewsFastScrollFocusState();
        this.mTrackedFastScrollViews.clear();
    }

    public void onBindView(AllAppsGridAdapter.ViewHolder holder) {
        if (this.mCurrentFastScrollSection != null || this.mTargetFastScrollSection != null) {
            this.mTrackedFastScrollViews.add(holder);
        }
    }

    private void trackAllChildViews() {
        int childCount = this.mRv.getChildCount();
        for (int i = 0; i < childCount; i++) {
            RecyclerView.ViewHolder viewHolder = this.mRv.getChildViewHolder(this.mRv.getChildAt(i));
            if (viewHolder != null) {
                this.mTrackedFastScrollViews.add(viewHolder);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTrackedViewsFastScrollFocusState() {
        Iterator<RecyclerView.ViewHolder> it = this.mTrackedFastScrollViews.iterator();
        while (it.hasNext()) {
            RecyclerView.ViewHolder viewHolder = it.next();
            int pos = viewHolder.getAdapterPosition();
            boolean isActive = false;
            if (this.mCurrentFastScrollSection != null && pos > -1 && pos < this.mApps.getAdapterItems().size()) {
                AlphabeticalAppsList.AdapterItem item = this.mApps.getAdapterItems().get(pos);
                isActive = item != null && this.mCurrentFastScrollSection.equals(item.sectionName) && item.position == this.mTargetFastScrollPosition;
            }
            viewHolder.itemView.setActivated(isActive);
        }
    }
}
