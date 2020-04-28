package com.android.launcher3.allapps;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import com.android.launcher3.AppInfo;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.allapps.AlphabeticalAppsList;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.List;

public class AllAppsGridAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final String TAG = "AppsGridAdapter";
    public static final int VIEW_TYPE_ALL_APPS_DIVIDER = 16;
    public static final int VIEW_TYPE_EMPTY_SEARCH = 4;
    public static final int VIEW_TYPE_ICON = 2;
    public static final int VIEW_TYPE_MASK_DIVIDER = 16;
    public static final int VIEW_TYPE_MASK_ICON = 2;
    public static final int VIEW_TYPE_SEARCH_MARKET = 8;
    public static final int VIEW_TYPE_WORK_TAB_FOOTER = 32;
    /* access modifiers changed from: private */
    public final AlphabeticalAppsList mApps;
    /* access modifiers changed from: private */
    public final int mAppsPerRow;
    private BindViewCallback mBindViewCallback;
    private String mEmptySearchMessage;
    private final GridLayoutManager mGridLayoutMgr;
    private final GridSpanSizer mGridSizer = new GridSpanSizer();
    private View.OnFocusChangeListener mIconFocusListener;
    /* access modifiers changed from: private */
    public final Launcher mLauncher;
    private final LayoutInflater mLayoutInflater;
    /* access modifiers changed from: private */
    public Intent mMarketSearchIntent;

    public interface BindViewCallback {
        void onBindView(ViewHolder viewHolder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class AppsGridLayoutManager extends GridLayoutManager {
        public AppsGridLayoutManager(Context context) {
            super(context, 1, 1, false);
        }

        public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
            super.onInitializeAccessibilityEvent(event);
            AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
            record.setItemCount(AllAppsGridAdapter.this.mApps.getNumFilteredApps());
            record.setFromIndex(Math.max(0, record.getFromIndex() - getRowsNotForAccessibility(record.getFromIndex())));
            record.setToIndex(Math.max(0, record.getToIndex() - getRowsNotForAccessibility(record.getToIndex())));
        }

        public int getRowCountForAccessibility(RecyclerView.Recycler recycler, RecyclerView.State state) {
            return super.getRowCountForAccessibility(recycler, state) - getRowsNotForAccessibility(AllAppsGridAdapter.this.mApps.getAdapterItems().size() - 1);
        }

        public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler recycler, RecyclerView.State state, View host, AccessibilityNodeInfoCompat info) {
            super.onInitializeAccessibilityNodeInfoForItem(recycler, state, host, info);
            ViewGroup.LayoutParams lp = host.getLayoutParams();
            AccessibilityNodeInfoCompat.CollectionItemInfoCompat cic = info.getCollectionItemInfo();
            if ((lp instanceof GridLayoutManager.LayoutParams) && cic != null) {
                info.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(cic.getRowIndex() - getRowsNotForAccessibility(((GridLayoutManager.LayoutParams) lp).getViewAdapterPosition()), cic.getRowSpan(), cic.getColumnIndex(), cic.getColumnSpan(), cic.isHeading(), cic.isSelected()));
            }
        }

        private int getRowsNotForAccessibility(int adapterPosition) {
            List<AlphabeticalAppsList.AdapterItem> items = AllAppsGridAdapter.this.mApps.getAdapterItems();
            int adapterPosition2 = Math.max(adapterPosition, AllAppsGridAdapter.this.mApps.getAdapterItems().size() - 1);
            int extraRows = 0;
            for (int i = 0; i <= adapterPosition2; i++) {
                if (!AllAppsGridAdapter.isViewType(items.get(i).viewType, 2)) {
                    extraRows++;
                }
            }
            return extraRows;
        }
    }

    public class GridSpanSizer extends GridLayoutManager.SpanSizeLookup {
        public GridSpanSizer() {
            setSpanIndexCacheEnabled(true);
        }

        public int getSpanSize(int position) {
            if (AllAppsGridAdapter.isIconViewType(AllAppsGridAdapter.this.mApps.getAdapterItems().get(position).viewType)) {
                return 1;
            }
            return AllAppsGridAdapter.this.mAppsPerRow;
        }
    }

    public AllAppsGridAdapter(Launcher launcher, AlphabeticalAppsList apps) {
        Resources res = launcher.getResources();
        this.mLauncher = launcher;
        this.mApps = apps;
        this.mEmptySearchMessage = res.getString(R.string.all_apps_loading_message);
        this.mGridLayoutMgr = new AppsGridLayoutManager(launcher);
        this.mGridLayoutMgr.setSpanSizeLookup(this.mGridSizer);
        this.mLayoutInflater = LayoutInflater.from(launcher);
        this.mAppsPerRow = this.mLauncher.getDeviceProfile().inv.numColumns;
        this.mGridLayoutMgr.setSpanCount(this.mAppsPerRow);
    }

    public static boolean isDividerViewType(int viewType) {
        return isViewType(viewType, 16);
    }

    public static boolean isIconViewType(int viewType) {
        return isViewType(viewType, 2);
    }

    public static boolean isViewType(int viewType, int viewTypeMask) {
        return (viewType & viewTypeMask) != 0;
    }

    public void setIconFocusListener(View.OnFocusChangeListener focusListener) {
        this.mIconFocusListener = focusListener;
    }

    public void setLastSearchQuery(String query) {
        this.mEmptySearchMessage = this.mLauncher.getResources().getString(R.string.all_apps_no_search_results, new Object[]{query});
        this.mMarketSearchIntent = PackageManagerHelper.getMarketSearchIntent(this.mLauncher, query);
    }

    public void setBindViewCallback(BindViewCallback cb) {
        this.mBindViewCallback = cb;
    }

    public GridLayoutManager getLayoutManager() {
        return this.mGridLayoutMgr;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 2) {
            BubbleTextView icon = (BubbleTextView) this.mLayoutInflater.inflate(R.layout.all_apps_icon, parent, false);
            icon.setOnClickListener(ItemClickHandler.INSTANCE);
            icon.setOnLongClickListener(ItemLongClickListener.INSTANCE_ALL_APPS);
            icon.setLongPressTimeout(ViewConfiguration.getLongPressTimeout());
            icon.setOnFocusChangeListener(this.mIconFocusListener);
            icon.getLayoutParams().height = this.mLauncher.getDeviceProfile().allAppsCellHeightPx;
            return new ViewHolder(icon);
        } else if (viewType == 4) {
            return new ViewHolder(this.mLayoutInflater.inflate(R.layout.all_apps_empty_search, parent, false));
        } else {
            if (viewType == 8) {
                View searchMarketView = this.mLayoutInflater.inflate(R.layout.all_apps_search_market, parent, false);
                searchMarketView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        AllAppsGridAdapter.this.mLauncher.startActivitySafely(v, AllAppsGridAdapter.this.mMarketSearchIntent, (ItemInfo) null);
                    }
                });
                return new ViewHolder(searchMarketView);
            } else if (viewType == 16) {
                return new ViewHolder(this.mLayoutInflater.inflate(R.layout.all_apps_divider, parent, false));
            } else {
                if (viewType == 32) {
                    return new ViewHolder(this.mLayoutInflater.inflate(R.layout.work_tab_footer, parent, false));
                }
                throw new RuntimeException("Unexpected view type");
            }
        }
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();
        if (itemViewType == 2) {
            AppInfo info = this.mApps.getAdapterItems().get(position).appInfo;
            BubbleTextView icon = (BubbleTextView) holder.itemView;
            icon.reset();
            icon.applyFromApplicationInfo(info);
        } else if (itemViewType == 4) {
            TextView emptyViewText = (TextView) holder.itemView;
            emptyViewText.setText(this.mEmptySearchMessage);
            emptyViewText.setGravity(this.mApps.hasNoFilteredResults() ? 17 : 8388627);
        } else if (itemViewType == 8) {
            TextView searchView = (TextView) holder.itemView;
            if (this.mMarketSearchIntent != null) {
                searchView.setVisibility(0);
            } else {
                searchView.setVisibility(8);
            }
        } else if (itemViewType != 16 && itemViewType == 32) {
            ((WorkModeSwitch) holder.itemView.findViewById(R.id.work_mode_toggle)).refresh();
            TextView managedByLabel = (TextView) holder.itemView.findViewById(R.id.managed_by_label);
            managedByLabel.setText(UserManagerCompat.getInstance(managedByLabel.getContext()).isAnyProfileQuietModeEnabled() ? R.string.work_mode_off_label : R.string.work_mode_on_label);
        }
        if (this.mBindViewCallback != null) {
            this.mBindViewCallback.onBindView(holder);
        }
    }

    public boolean onFailedToRecycleView(ViewHolder holder) {
        return true;
    }

    public int getItemCount() {
        return this.mApps.getAdapterItems().size();
    }

    public int getItemViewType(int position) {
        return this.mApps.getAdapterItems().get(position).viewType;
    }
}
