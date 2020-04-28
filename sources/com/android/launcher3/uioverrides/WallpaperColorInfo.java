package com.android.launcher3.uioverrides;

import android.content.Context;
import android.util.Pair;
import com.android.launcher3.uioverrides.dynamicui.ColorExtractionAlgorithm;
import com.android.launcher3.uioverrides.dynamicui.WallpaperColorsCompat;
import com.android.launcher3.uioverrides.dynamicui.WallpaperManagerCompat;
import java.util.ArrayList;

public class WallpaperColorInfo implements WallpaperManagerCompat.OnColorsChangedListenerCompat {
    private static final int FALLBACK_COLOR = -1;
    private static WallpaperColorInfo sInstance;
    private static final Object sInstanceLock = new Object();
    private final ColorExtractionAlgorithm mExtractionType;
    private boolean mIsDark;
    private final ArrayList<OnChangeListener> mListeners = new ArrayList<>();
    private int mMainColor;
    private int mSecondaryColor;
    private boolean mSupportsDarkText;
    private OnChangeListener[] mTempListeners;
    private final WallpaperManagerCompat mWallpaperManager;

    public interface OnChangeListener {
        void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo);
    }

    public static WallpaperColorInfo getInstance(Context context) {
        WallpaperColorInfo wallpaperColorInfo;
        synchronized (sInstanceLock) {
            if (sInstance == null) {
                sInstance = new WallpaperColorInfo(context.getApplicationContext());
            }
            wallpaperColorInfo = sInstance;
        }
        return wallpaperColorInfo;
    }

    private WallpaperColorInfo(Context context) {
        this.mWallpaperManager = WallpaperManagerCompat.getInstance(context);
        this.mWallpaperManager.addOnColorsChangedListener(this);
        this.mExtractionType = ColorExtractionAlgorithm.newInstance(context);
        update(this.mWallpaperManager.getWallpaperColors(1));
    }

    public int getMainColor() {
        return this.mMainColor;
    }

    public int getSecondaryColor() {
        return this.mSecondaryColor;
    }

    public boolean isDark() {
        return this.mIsDark;
    }

    public boolean supportsDarkText() {
        return this.mSupportsDarkText;
    }

    public void onColorsChanged(WallpaperColorsCompat colors, int which) {
        if ((which & 1) != 0) {
            update(colors);
            notifyChange();
        }
    }

    private void update(WallpaperColorsCompat wallpaperColors) {
        Pair<Integer, Integer> colors = this.mExtractionType.extractInto(wallpaperColors);
        if (colors != null) {
            this.mMainColor = ((Integer) colors.first).intValue();
            this.mSecondaryColor = ((Integer) colors.second).intValue();
        } else {
            this.mMainColor = -1;
            this.mSecondaryColor = -1;
        }
        boolean z = false;
        this.mSupportsDarkText = wallpaperColors != null && (wallpaperColors.getColorHints() & 1) > 0;
        if (wallpaperColors != null && (wallpaperColors.getColorHints() & 2) > 0) {
            z = true;
        }
        this.mIsDark = z;
    }

    public void addOnChangeListener(OnChangeListener listener) {
        this.mListeners.add(listener);
    }

    public void removeOnChangeListener(OnChangeListener listener) {
        this.mListeners.remove(listener);
    }

    private void notifyChange() {
        OnChangeListener[] copy;
        if (this.mTempListeners == null || this.mTempListeners.length != this.mListeners.size()) {
            copy = new OnChangeListener[this.mListeners.size()];
        } else {
            copy = this.mTempListeners;
        }
        this.mTempListeners = (OnChangeListener[]) this.mListeners.toArray(copy);
        for (OnChangeListener listener : this.mTempListeners) {
            listener.onExtractedColorsChanged(this);
        }
    }
}
