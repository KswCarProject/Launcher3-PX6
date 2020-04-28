package com.android.launcher3.adapter;

import android.annotation.SuppressLint;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.util.EventUtils;
import java.util.List;

public class MRecyclerVierAdapter extends RecyclerView.Adapter<MHolder> implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private static final String TAG = "MRecyclerVierAdapter";
    private int iAppsFocusIndex = -1;
    private List<ResolveInfo> mApps;
    private Launcher mLauncher;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int i);
    }

    public MRecyclerVierAdapter(Launcher mLauncher2, List<ResolveInfo> mApps2) {
        this.mLauncher = mLauncher2;
        this.mApps = mApps2;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener2) {
        this.onItemLongClickListener = onItemLongClickListener2;
    }

    public void setData(List<ResolveInfo> mApps2) {
        this.mApps = mApps2;
        this.iAppsFocusIndex = -1;
        notifyDataSetChanged();
    }

    public void setiAppsFocusIndex(int iAppsFocusIndex2) {
        Log.i(TAG, "setiAppsFocusIndex: iAppsFocusIndex = " + iAppsFocusIndex2);
        this.iAppsFocusIndex = iAppsFocusIndex2;
        notifyDataSetChanged();
    }

    public int getiAppsFocusIndex() {
        return this.iAppsFocusIndex;
    }

    public MHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (this.mLauncher.m_iUITypeVer == 41) {
            if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                view = inflater.inflate(R.layout.recyclerview_item, parent, false);
            } else {
                view = inflater.inflate(R.layout.recyclerview_item_imitation_apple, parent, false);
            }
        } else if (this.mLauncher.m_iUITypeVer == 101) {
            Log.i(TAG, "onCreateViewHolder: UI_NORMAL_1920X720");
            view = inflater.inflate(R.layout.recyclerview_item_720, parent, false);
        }
        return new MHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void onBindViewHolder(MHolder holder, int position) {
        setIconInfo(holder, position);
        if (holder.mRlIcon != null) {
            holder.mRlIcon.setTag(holder);
            holder.mRlIcon.setOnTouchListener(this);
            holder.mRlIcon.setOnClickListener(this);
            holder.mRlIcon.setOnLongClickListener(this);
        }
    }

    public int getItemCount() {
        if (this.mApps == null) {
            return 0;
        }
        return this.mApps.size();
    }

    private void setIconInfo(MHolder holder, int position) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        Drawable drawable4;
        Drawable drawable5;
        Drawable drawable6;
        Drawable drawable7;
        if (this.iAppsFocusIndex == position) {
            holder.mRlIconFocusBk.setBackgroundResource(R.drawable.focused_bg);
        } else {
            holder.mRlIconFocusBk.setBackgroundColor(0);
        }
        Drawable drawable8 = null;
        if (holder.mIvTopIcon != null) {
            holder.mIvTopIcon.setImageDrawable((Drawable) null);
        }
        boolean bSpecial = false;
        if (this.mApps.get(position).activityInfo.packageName.startsWith("com.szchoiceway")) {
            if (EventUtils.MUSIC_MODE_PACKAGE_NAME.equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_music);
                } else {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_music);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_music);
                }
            } else if (EventUtils.MOVIE_MODE_PACKAGE_NAME.equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable7 = this.mLauncher.getDrawable(R.drawable.ksw_icon_video);
                } else {
                    drawable7 = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_video);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_voide);
                }
            } else if (EventUtils.BT_MODE_PACKAGE_NAME.equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable6 = this.mLauncher.getDrawable(R.drawable.ksw_icon_bt);
                } else {
                    drawable6 = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_bt);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    if (EventUtils.BT_MODE_CLASS_NAME.equals(this.mApps.get(position).activityInfo.name)) {
                        drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_bt);
                    } else if ("com.szchoiceway.btsuite.BTMusicActivity".equals(this.mApps.get(position).activityInfo.name)) {
                        drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_bt_music);
                    }
                }
            } else if (EventUtils.SET_MODE_PACKAGE_NAME.equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable5 = this.mLauncher.getDrawable(R.drawable.ksw_icon_settings);
                } else {
                    drawable5 = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_settings);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_setting);
                }
            } else if (EventUtils.NAV_MODE_PACKAGE_NAME.equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable4 = this.mLauncher.getDrawable(R.drawable.ksw_icon_navigation);
                } else {
                    drawable4 = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_navigation);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_navi);
                }
            } else if ("com.szchoiceway.ksw_dashboard".equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable3 = this.mLauncher.getDrawable(R.drawable.ksw_icon_dashboard);
                } else {
                    drawable3 = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_dashboard);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    drawable = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                }
            } else if ("com.szchoiceway.ksw_aux".equals(this.mApps.get(position).activityInfo.packageName) || "com.szchoiceway.dvrplayer".equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable2 = this.mLauncher.getDrawable(R.drawable.ksw_icon_aux);
                } else {
                    drawable2 = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_aux);
                }
                if (this.mLauncher.m_iUITypeVer == 101) {
                    drawable = this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_aux);
                }
            } else if ("com.szchoiceway.ksw_cmmb".equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_tv);
                } else {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_tv);
                }
            } else if ("com.szchoiceway.ksw_dvd".equals(this.mApps.get(position).activityInfo.packageName) || "com.szchoiceway.dvdplayer".equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_dvd);
                } else {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_dvd);
                }
            } else if ("com.szchoiceway.ksw_dvr".equals(this.mApps.get(position).activityInfo.packageName)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_dvr);
                } else {
                    drawable = this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_dvr);
                }
            } else if (EventUtils.RADIO_MODE_PACKAGE_NAME.equals(this.mApps.get(position).activityInfo.packageName)) {
                drawable = this.mLauncher.getDrawable(R.drawable.icon_radio);
            } else if ("com.szchoiceway.auxplayer".equals(this.mApps.get(position).activityInfo.packageName)) {
                drawable = this.mLauncher.getDrawable(R.drawable.icon_aux);
            } else {
                drawable = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
            }
            if (holder.mIvIcon != null) {
                holder.mIvIcon.setImageDrawable(drawable);
            }
        } else {
            if (this.mApps.get(position).activityInfo.packageName.startsWith("com.autonavi.amapauto")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_auto_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith(EventUtils.PHONEAPP_MODE_PACKAGE_NAME)) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_shoujihulian_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.estrongs.android.pop")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_esfile_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.google.android.youtube")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_youtube_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.skysoft.kkbox.android")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_kkbox_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.kingwaytek")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_naviking3d_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("mbinc12.mb32")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_mixerbox_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.android.vending")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_android_vending_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.google.android.googlequicksearchbox")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    if ("com.google.android.googlequicksearchbox.SearchActivity".equals(this.mApps.get(position).activityInfo.name)) {
                        drawable8 = this.mLauncher.getDrawable(R.drawable.icon_google_n);
                    } else if ("com.google.android.googlequicksearchbox.VoiceSearchActivity".equals(this.mApps.get(position).activityInfo.name)) {
                        drawable8 = this.mLauncher.getDrawable(R.drawable.inco_google_voice_n);
                    }
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.google.android.apps.maps")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_google_map);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.qiyi.video.pad")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_qiyi);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("com.papago.s1OBU")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_papago_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("cn.manstep.phonemirrorBox")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_autopaly_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("tw.chaozhuyin.paid")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_chaozhuyin_n);
                    bSpecial = true;
                }
            } else if (this.mApps.get(position).activityInfo.packageName.startsWith("tv.fourgtv.fourgtv")) {
                if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                    drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
                } else {
                    drawable8 = this.mLauncher.getDrawable(R.drawable.icon_fourgtv_n);
                    bSpecial = true;
                }
            } else if (!this.mApps.get(position).activityInfo.packageName.startsWith("com.facebook.katana")) {
                drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
            } else if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                drawable8 = this.mApps.get(position).loadIcon(this.mLauncher.getPackageManager());
            } else {
                drawable8 = this.mLauncher.getDrawable(R.drawable.icon_facebook_n);
                bSpecial = true;
            }
            if (this.mLauncher.m_iUITypeVer == 101) {
                if (holder.mIvTopIcon != null) {
                    holder.mIvTopIcon.setImageDrawable(drawable8);
                }
                holder.mIvIcon.setImageDrawable(this.mLauncher.getDrawable(R.drawable.normal_1920x720_icon_back));
            } else if (!"als".equalsIgnoreCase(this.mLauncher.xml_client) || this.mLauncher.mAppsIconIndex != 1) {
                holder.mIvIcon.setImageDrawable(drawable8);
            } else if (bSpecial) {
                holder.mIvIcon.setImageDrawable(drawable8);
            } else {
                if (holder.mIvTopIcon != null) {
                    holder.mIvTopIcon.setImageDrawable(drawable8);
                }
                holder.mIvIcon.setImageDrawable(this.mLauncher.getDrawable(R.drawable.ksw_icon_imitation_apple_bg));
            }
        }
        if (holder.mTvName != null) {
            holder.mTvName.setText(this.mApps.get(position).loadLabel(this.mLauncher.getPackageManager()));
        }
    }

    public void onClick(View v) {
        if (this.onItemClickListener != null) {
            this.onItemClickListener.onItemClick(v, ((MHolder) v.getTag()).getLayoutPosition());
        }
    }

    public boolean onLongClick(View v) {
        if (this.onItemLongClickListener != null) {
            this.onItemLongClickListener.onItemLongClick(v, ((MHolder) v.getTag()).getLayoutPosition());
        }
        v.setAlpha(1.0f);
        return true;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouch(View v, MotionEvent motionEvent) {
        Log.i(TAG, "onTouch: motionEvent.getAction() = " + motionEvent.getAction());
        switch (motionEvent.getAction()) {
            case 0:
                v.setAlpha(0.5f);
                return false;
            case 1:
            case 2:
            case 3:
                v.setAlpha(1.0f);
                return false;
            default:
                v.setAlpha(1.0f);
                return false;
        }
    }

    class MHolder extends RecyclerView.ViewHolder {
        ImageView mIvIcon;
        ImageView mIvTopIcon;
        RelativeLayout mRlIcon;
        RelativeLayout mRlIconFocusBk;
        TextView mTvName;

        public MHolder(View itemView) {
            super(itemView);
            this.mRlIconFocusBk = (RelativeLayout) itemView.findViewById(R.id.MRlIconFocusBk);
            this.mRlIcon = (RelativeLayout) itemView.findViewById(R.id.MRlIcon);
            this.mIvIcon = (ImageView) itemView.findViewById(R.id.MIvIcon);
            this.mIvTopIcon = (ImageView) itemView.findViewById(R.id.MIvTopIcon);
            this.mTvName = (TextView) itemView.findViewById(R.id.MTvName);
        }
    }
}
