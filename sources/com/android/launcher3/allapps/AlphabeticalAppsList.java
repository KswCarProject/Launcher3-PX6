package com.android.launcher3.allapps;

import android.content.ComponentName;
import android.content.Context;
import com.android.launcher3.AppInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.allapps.AllAppsStore;
import com.android.launcher3.compat.AlphabeticIndexCompat;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LabelComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class AlphabeticalAppsList implements AllAppsStore.OnUpdateListener {
    private static final int FAST_SCROLL_FRACTION_DISTRIBUTE_BY_NUM_SECTIONS = 1;
    private static final int FAST_SCROLL_FRACTION_DISTRIBUTE_BY_ROWS_FRACTION = 0;
    public static final String TAG = "AlphabeticalAppsList";
    private AllAppsGridAdapter mAdapter;
    private final ArrayList<AdapterItem> mAdapterItems = new ArrayList<>();
    private final AllAppsStore mAllAppsStore;
    private AppInfoComparator mAppNameComparator;
    private final List<AppInfo> mApps = new ArrayList();
    private HashMap<CharSequence, String> mCachedSectionNames = new HashMap<>();
    private final int mFastScrollDistributionMode = 1;
    private final List<FastScrollSectionInfo> mFastScrollerSections = new ArrayList();
    private final List<AppInfo> mFilteredApps = new ArrayList();
    private AlphabeticIndexCompat mIndexer;
    private final boolean mIsWork;
    private ItemInfoMatcher mItemFilter;
    private final Launcher mLauncher;
    private int mNumAppRowsInAdapter;
    private final int mNumAppsPerRow;
    private ArrayList<ComponentKey> mSearchResults;

    public static class FastScrollSectionInfo {
        public AdapterItem fastScrollToItem;
        public String sectionName;
        public float touchFraction;

        public FastScrollSectionInfo(String sectionName2) {
            this.sectionName = sectionName2;
        }
    }

    public static class AdapterItem {
        public int appIndex = -1;
        public AppInfo appInfo = null;
        public int position;
        public int rowAppIndex;
        public int rowIndex;
        public String sectionName = null;
        public int viewType;

        public static AdapterItem asApp(int pos, String sectionName2, AppInfo appInfo2, int appIndex2) {
            AdapterItem item = new AdapterItem();
            item.viewType = 2;
            item.position = pos;
            item.sectionName = sectionName2;
            item.appInfo = appInfo2;
            item.appIndex = appIndex2;
            return item;
        }

        public static AdapterItem asEmptySearch(int pos) {
            AdapterItem item = new AdapterItem();
            item.viewType = 4;
            item.position = pos;
            return item;
        }

        public static AdapterItem asAllAppsDivider(int pos) {
            AdapterItem item = new AdapterItem();
            item.viewType = 16;
            item.position = pos;
            return item;
        }

        public static AdapterItem asMarketSearch(int pos) {
            AdapterItem item = new AdapterItem();
            item.viewType = 8;
            item.position = pos;
            return item;
        }

        public static AdapterItem asWorkTabFooter(int pos) {
            AdapterItem item = new AdapterItem();
            item.viewType = 32;
            item.position = pos;
            return item;
        }
    }

    public AlphabeticalAppsList(Context context, AllAppsStore appsStore, boolean isWork) {
        this.mAllAppsStore = appsStore;
        this.mLauncher = Launcher.getLauncher(context);
        this.mIndexer = new AlphabeticIndexCompat(context);
        this.mAppNameComparator = new AppInfoComparator(context);
        this.mIsWork = isWork;
        this.mNumAppsPerRow = this.mLauncher.getDeviceProfile().inv.numColumns;
        this.mAllAppsStore.addUpdateListener(this);
    }

    public void updateItemFilter(ItemInfoMatcher itemFilter) {
        this.mItemFilter = itemFilter;
        onAppsUpdated();
    }

    public void setAdapter(AllAppsGridAdapter adapter) {
        this.mAdapter = adapter;
    }

    public List<AppInfo> getApps() {
        return this.mApps;
    }

    public List<FastScrollSectionInfo> getFastScrollerSections() {
        return this.mFastScrollerSections;
    }

    public List<AdapterItem> getAdapterItems() {
        return this.mAdapterItems;
    }

    public int getNumAppRows() {
        return this.mNumAppRowsInAdapter;
    }

    public int getNumFilteredApps() {
        return this.mFilteredApps.size();
    }

    public boolean hasFilter() {
        return this.mSearchResults != null;
    }

    public boolean hasNoFilteredResults() {
        return this.mSearchResults != null && this.mFilteredApps.isEmpty();
    }

    public boolean setOrderedFilter(ArrayList<ComponentKey> f) {
        if (this.mSearchResults == f) {
            return false;
        }
        boolean same = this.mSearchResults != null && this.mSearchResults.equals(f);
        this.mSearchResults = f;
        onAppsUpdated();
        if (!same) {
            return true;
        }
        return false;
    }

    public void onAppsUpdated() {
        this.mApps.clear();
        for (AppInfo app : this.mAllAppsStore.getApps()) {
            if (this.mItemFilter == null || this.mItemFilter.matches(app, (ComponentName) null) || hasFilter()) {
                this.mApps.add(app);
            }
        }
        Collections.sort(this.mApps, this.mAppNameComparator);
        if (this.mLauncher.getResources().getConfiguration().locale.equals(Locale.SIMPLIFIED_CHINESE)) {
            TreeMap<String, ArrayList<AppInfo>> sectionMap = new TreeMap<>(new LabelComparator());
            for (AppInfo info : this.mApps) {
                String sectionName = getAndUpdateCachedSectionName(info.title);
                ArrayList<AppInfo> sectionApps = sectionMap.get(sectionName);
                if (sectionApps == null) {
                    sectionApps = new ArrayList<>();
                    sectionMap.put(sectionName, sectionApps);
                }
                sectionApps.add(info);
            }
            this.mApps.clear();
            for (Map.Entry<String, ArrayList<AppInfo>> entry : sectionMap.entrySet()) {
                this.mApps.addAll(entry.getValue());
            }
        } else {
            for (AppInfo info2 : this.mApps) {
                getAndUpdateCachedSectionName(info2.title);
            }
        }
        updateAdapterItems();
    }

    private void updateAdapterItems() {
        refillAdapterItems();
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    private void refillAdapterItems() {
        int position;
        String lastSectionName = null;
        FastScrollSectionInfo lastFastScrollerSectionInfo = null;
        int position2 = 0;
        int appIndex = 0;
        this.mFilteredApps.clear();
        this.mFastScrollerSections.clear();
        this.mAdapterItems.clear();
        for (AppInfo info : getFiltersAppInfos()) {
            String sectionName = getAndUpdateCachedSectionName(info.title);
            if (!sectionName.equals(lastSectionName)) {
                lastSectionName = sectionName;
                lastFastScrollerSectionInfo = new FastScrollSectionInfo(sectionName);
                this.mFastScrollerSections.add(lastFastScrollerSectionInfo);
            }
            int position3 = position2 + 1;
            int appIndex2 = appIndex + 1;
            AdapterItem appItem = AdapterItem.asApp(position2, sectionName, info, appIndex);
            if (lastFastScrollerSectionInfo.fastScrollToItem == null) {
                lastFastScrollerSectionInfo.fastScrollToItem = appItem;
            }
            this.mAdapterItems.add(appItem);
            this.mFilteredApps.add(info);
            position2 = position3;
            appIndex = appIndex2;
        }
        if (hasFilter()) {
            if (hasNoFilteredResults()) {
                position = position2 + 1;
                this.mAdapterItems.add(AdapterItem.asEmptySearch(position2));
            } else {
                position = position2 + 1;
                this.mAdapterItems.add(AdapterItem.asAllAppsDivider(position2));
            }
            this.mAdapterItems.add(AdapterItem.asMarketSearch(position));
            position2 = position + 1;
        }
        if (this.mNumAppsPerRow != 0) {
            int numAppsInSection = 0;
            int numAppsInRow = 0;
            int rowIndex = -1;
            Iterator<AdapterItem> it = this.mAdapterItems.iterator();
            while (it.hasNext()) {
                AdapterItem item = it.next();
                item.rowIndex = 0;
                if (AllAppsGridAdapter.isDividerViewType(item.viewType)) {
                    numAppsInSection = 0;
                } else if (AllAppsGridAdapter.isIconViewType(item.viewType)) {
                    if (numAppsInSection % this.mNumAppsPerRow == 0) {
                        numAppsInRow = 0;
                        rowIndex++;
                    }
                    item.rowIndex = rowIndex;
                    item.rowAppIndex = numAppsInRow;
                    numAppsInSection++;
                    numAppsInRow++;
                }
            }
            this.mNumAppRowsInAdapter = rowIndex + 1;
            switch (1) {
                case 0:
                    float rowFraction = 1.0f / ((float) this.mNumAppRowsInAdapter);
                    for (FastScrollSectionInfo info2 : this.mFastScrollerSections) {
                        AdapterItem item2 = info2.fastScrollToItem;
                        if (!AllAppsGridAdapter.isIconViewType(item2.viewType)) {
                            info2.touchFraction = 0.0f;
                        } else {
                            info2.touchFraction = (((float) item2.rowIndex) * rowFraction) + (((float) item2.rowAppIndex) * (rowFraction / ((float) this.mNumAppsPerRow)));
                        }
                    }
                    break;
                case 1:
                    float perSectionTouchFraction = 1.0f / ((float) this.mFastScrollerSections.size());
                    float cumulativeTouchFraction = 0.0f;
                    for (FastScrollSectionInfo info3 : this.mFastScrollerSections) {
                        if (!AllAppsGridAdapter.isIconViewType(info3.fastScrollToItem.viewType)) {
                            info3.touchFraction = 0.0f;
                        } else {
                            info3.touchFraction = cumulativeTouchFraction;
                            cumulativeTouchFraction += perSectionTouchFraction;
                        }
                    }
                    break;
            }
        }
        if (shouldShowWorkFooter() != 0) {
            this.mAdapterItems.add(AdapterItem.asWorkTabFooter(position2));
            int i = position2 + 1;
        }
    }

    private boolean shouldShowWorkFooter() {
        return this.mIsWork && Utilities.ATLEAST_P && (DeepShortcutManager.getInstance(this.mLauncher).hasHostPermission() || this.mLauncher.checkSelfPermission("android.permission.MODIFY_QUIET_MODE") == 0);
    }

    private List<AppInfo> getFiltersAppInfos() {
        if (this.mSearchResults == null) {
            return this.mApps;
        }
        ArrayList<AppInfo> result = new ArrayList<>();
        Iterator<ComponentKey> it = this.mSearchResults.iterator();
        while (it.hasNext()) {
            AppInfo match = this.mAllAppsStore.getApp(it.next());
            if (match != null) {
                result.add(match);
            }
        }
        return result;
    }

    private String getAndUpdateCachedSectionName(CharSequence title) {
        String sectionName = this.mCachedSectionNames.get(title);
        if (sectionName != null) {
            return sectionName;
        }
        String sectionName2 = this.mIndexer.computeSectionName(title);
        this.mCachedSectionNames.put(title, sectionName2);
        return sectionName2;
    }
}
