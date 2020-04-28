package com.android.launcher3.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.adapter.HorizontalPageLayoutManager;
import com.android.launcher3.adapter.MRecyclerVierAdapter;
import com.android.launcher3.adapter.PagingScrollHelper;
import com.android.launcher3.util.EventUtils;

public class AppsFragment extends Fragment implements MRecyclerVierAdapter.OnItemClickListener, MRecyclerVierAdapter.OnItemLongClickListener, PagingScrollHelper.onPageChangeListener {
    private static final String TAG = "AppsFragment";
    private boolean bActionBar = false;
    private boolean bAppsPageChanged = true;
    private int iAppsFocusIndex = -1;
    private int iColumns = 8;
    private int iLastAppsPageIndex = 0;
    private int iRows = 2;
    private Launcher mLauncher;
    public MRecyclerVierAdapter mRecyclerVierAdapter;
    private RecyclerView mRecyclerView;
    private PagingScrollHelper mScrollHelper;
    private View mVActionBar;

    public AppsFragment() {
        Log.i(TAG, "AppsFragment: ");
    }

    public void setRows(int iRows2) {
        this.iRows = iRows2;
    }

    public void setColumns(int iColumns2) {
        this.iColumns = iColumns2;
    }

    public void setiAppsFocusIndex(int iAppsFocusIndex2) {
        this.iAppsFocusIndex = iAppsFocusIndex2;
        if (this.mRecyclerVierAdapter != null) {
            this.mRecyclerVierAdapter.setiAppsFocusIndex(iAppsFocusIndex2);
        }
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mLauncher = (Launcher) context;
        Log.i(TAG, "onAttach: ");
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: mAppsIconIndex = " + this.mLauncher.mAppsIconIndex);
        return inflater.inflate(R.layout.fragment_apps_list, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        this.mVActionBar = view.findViewById(R.id.VActionBar);
        if (this.mVActionBar != null) {
            this.mVActionBar.setVisibility(this.bActionBar ? 8 : 0);
        }
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.MRecyclerView);
        this.mRecyclerVierAdapter = new MRecyclerVierAdapter(this.mLauncher, this.mLauncher.mApps);
        this.mRecyclerView.setAdapter(this.mRecyclerVierAdapter);
        this.mRecyclerView.setLayoutManager(new HorizontalPageLayoutManager(this.iRows, this.iColumns));
        this.mScrollHelper = new PagingScrollHelper();
        this.mScrollHelper.setUpRecycleView(this.mRecyclerView);
        this.mScrollHelper.setOnPageChangeListener(this);
        this.mRecyclerView.setHorizontalScrollBarEnabled(true);
        this.mRecyclerVierAdapter.setOnItemClickListener(this);
        this.mRecyclerVierAdapter.setOnItemLongClickListener(this);
    }

    public void setActionBar(boolean status) {
        this.bActionBar = status;
        Log.i(TAG, "setActionBar: status = " + status);
        Log.i(TAG, "setActionBar: mVActionBar = " + this.mVActionBar);
        if (this.mVActionBar != null) {
            this.mVActionBar.setVisibility(status ? 8 : 0);
        }
    }

    public void onDestroyView() {
        Log.i(TAG, "onDestroyView: ");
        super.onDestroyView();
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy: ");
        super.onDestroy();
    }

    public void onItemClick(View v, int position) {
        EventUtils.startActivityIfNotRuning(this.mLauncher, this.mLauncher.mApps.get(position).activityInfo.packageName, this.mLauncher.mApps.get(position).activityInfo.name);
        this.iAppsFocusIndex = position;
        if (this.mRecyclerVierAdapter != null) {
            this.mRecyclerVierAdapter.setiAppsFocusIndex(-1);
        }
    }

    public void onItemLongClick(View v, int position) {
        Log.i(TAG, "onItemLongClick: package = " + this.mLauncher.mApps.get(position).activityInfo.packageName);
        this.mLauncher.startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + this.mLauncher.mApps.get(position).activityInfo.packageName)));
    }

    public void onPageChange(int index) {
        if (this.iLastAppsPageIndex != index) {
            Log.i(TAG, "onPageChange: index  = " + index);
            if (!(this.mRecyclerVierAdapter == null || this.mRecyclerVierAdapter.getiAppsFocusIndex() == -1)) {
                if (this.iLastAppsPageIndex > index) {
                    this.iAppsFocusIndex = ((this.iRows * this.iColumns) * this.iLastAppsPageIndex) - 1;
                } else {
                    this.iAppsFocusIndex = this.iRows * this.iColumns * index;
                }
                this.mRecyclerVierAdapter.setiAppsFocusIndex(this.iAppsFocusIndex);
            }
            this.iLastAppsPageIndex = index;
            this.bAppsPageChanged = true;
        }
    }

    public void setAppsFocusMove(int iExtra) {
        if (this.bAppsPageChanged) {
            if (iExtra == 1) {
                this.mLauncher.showApps(false);
            } else if (iExtra != 5) {
                switch (iExtra) {
                    case 7:
                        if (this.mRecyclerVierAdapter != null && this.mRecyclerVierAdapter.getiAppsFocusIndex() == -1) {
                            this.iAppsFocusIndex = (this.iRows * this.iColumns * this.iLastAppsPageIndex) + 1;
                        }
                        this.iAppsFocusIndex--;
                        if (this.iAppsFocusIndex < 0) {
                            this.iAppsFocusIndex = 0;
                        }
                        if (this.iAppsFocusIndex < this.iRows * this.iColumns * this.iLastAppsPageIndex && this.mScrollHelper != null) {
                            this.bAppsPageChanged = false;
                            this.mScrollHelper.scrollToPosition(this.iLastAppsPageIndex - 1);
                        }
                        this.mRecyclerVierAdapter.setiAppsFocusIndex(this.iAppsFocusIndex);
                        return;
                    case 8:
                        if (this.mRecyclerVierAdapter != null && this.mRecyclerVierAdapter.getiAppsFocusIndex() == -1) {
                            this.iAppsFocusIndex = ((this.iRows * this.iColumns) * this.iLastAppsPageIndex) - 1;
                        }
                        this.iAppsFocusIndex++;
                        if (this.iAppsFocusIndex >= this.mLauncher.mApps.size()) {
                            this.iAppsFocusIndex = this.mLauncher.mApps.size() - 1;
                        }
                        if (this.iAppsFocusIndex >= this.iRows * this.iColumns * (this.iLastAppsPageIndex + 1) && this.mScrollHelper != null) {
                            this.bAppsPageChanged = false;
                            this.mScrollHelper.scrollToPosition(this.iLastAppsPageIndex + 1);
                        }
                        this.mRecyclerVierAdapter.setiAppsFocusIndex(this.iAppsFocusIndex);
                        return;
                    default:
                        return;
                }
            } else if (this.mRecyclerVierAdapter.getiAppsFocusIndex() != -1) {
                EventUtils.startActivityIfNotRuning(this.mLauncher, this.mLauncher.mApps.get(this.iAppsFocusIndex).activityInfo.packageName, this.mLauncher.mApps.get(this.iAppsFocusIndex).activityInfo.name);
            }
        }
    }
}
