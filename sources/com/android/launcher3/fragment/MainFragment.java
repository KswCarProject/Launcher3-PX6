package com.android.launcher3.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.SysProviderOpt;
import com.android.launcher3.util.EventUtils;
import com.android.launcher3.util.GuideInfoExtraKey;
import com.android.launcher3.views.BlurUtil;
import com.android.launcher3.views.CoverFlowView;
import com.android.launcher3.views.LoopRotarySwitchView;
import com.android.launcher3.views.MyQAnalogClock2;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MainFragment";
    /* access modifiers changed from: private */
    public static int ksw_3d_currentPosition;
    /* access modifiers changed from: private */
    public static int ksw_3d_lastPosition = -1;
    private RelativeLayout KSW_A4L_right_Traffic_information = null;
    private RelativeLayout KSW_A4L_right_show_Medio = null;
    private RelativeLayout KSW_A4L_right_show_Navi = null;
    private RelativeLayout KSW_A4L_right_show_logo = null;
    private boolean bBtConnected = false;
    private boolean bInLeftFocus = false;
    private float fFlowViewLeft;
    private float fFlowViewRight;
    /* access modifiers changed from: private */
    public int iLastMainFocusIndex = 0;
    /* access modifiers changed from: private */
    public int iLastMainPageIndex;
    private int iLeftFocusIndex = 0;
    /* access modifiers changed from: private */
    public int iMainFocusIndex = -1;
    /* access modifiers changed from: private */
    public int iMainPageIndex;
    int[] iMapIconList;
    /* access modifiers changed from: private */
    public int iPagerItemViewItemCount = 0;
    private ImageView[] imageViewFocusList_modeIcon_whats1;
    private ImageView[] imageViewFocusList_modeIcon_whats2;
    private ImageView[] imageViewFocusList_whats1;
    private ImageView[] imageViewFocusList_whats2;
    private ImageView[] imageViewFocusList_whats3;
    private ImageView[] imageViewFocusList_whats4;
    private ImageView[] imageViewLeftFocusList;
    private int[] imgFocusList;
    private int[] imgFocusList_modeIcon;
    private int[] imgItemTypeBgList;
    private int[] imgItemTypeIconList;
    private int[] imgItemTypeKnobList;
    private int[] imgLeftFocusList;
    boolean isAuto = true;
    boolean isJup_Add = false;
    boolean isJup_Sub = false;
    private ImageView ksw_3d_ivGuang;
    /* access modifiers changed from: private */
    public int ksw_3d_selectItem;
    /* access modifiers changed from: private */
    public List<View> ksw_3d_views;
    private ImageView ksw_A4L_audi_che;
    private int ksw_m_i_right_show_index;
    private ImageView mAmapauto_Icon;
    private View mBtnPagerNext;
    private View mBtnPagerPriv;
    private MyQAnalogClock2 mClockRotatingSpeed;
    private MyQAnalogClock2 mClockSpeed;
    /* access modifiers changed from: private */
    public CoverFlowView mCoverFlowView;
    private ImageView mIvAudiRightLogo;
    private View mIvBtStatus;
    private View mIvItemTypeBg;
    private View mIvItemTypeIcon;
    private View mIvItemTypeKnob;
    private ImageView mIvMainBk;
    private ImageView mIvMediaTypeBg;
    /* access modifiers changed from: private */
    public Launcher mLauncher;
    /* access modifiers changed from: private */
    public LoopRotarySwitchView mLoopRotarySwitchView;
    private ImageView mMusicCover;
    private ImageView mMusicCoverBg;
    private Bitmap mMusicCoverBm;
    private View mNaviAmapautoInfo;
    /* access modifiers changed from: private */
    public ImageView mPage0;
    /* access modifiers changed from: private */
    public ImageView mPage1;
    /* access modifiers changed from: private */
    public ImageView mPage2;
    private SeekBar mSkBarProgress;
    private TextView mTvCurDataTime;
    private TextView mTvCurDataTimeWeek;
    private TextView mTvCurDataTimeYMD;
    private TextView mTvCurrTime;
    private TextView mTvDrivingRange;
    private TextView mTvHandbrake;
    private TextView mTvMusicAblumInfor;
    private TextView mTvMusicArtistInfor;
    private TextView mTvMusicTitleInfor;
    private TextView mTvNextRouadName;
    private TextView mTvQil;
    private TextView mTvRouteRemainDis;
    private TextView mTvRouteRemainTime;
    private TextView mTvSeatBelt;
    private TextView mTvSegRemainDisInfor;
    private TextView mTvSpeedUnit;
    private TextView mTvTemp;
    private TextView mTvTotTime;
    private TextView mTvZhuanSu;
    private ViewPager mViewPager;
    /* access modifiers changed from: private */
    public boolean m_b_ksw_init_accOn_focus_index = true;

    public void onAttach(Context context) {
        super.onAttach(context);
        this.mLauncher = (Launcher) context;
        Log.i(TAG, "onAttach: ");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.mLauncher.m_iUITypeVer == 41) {
            if (this.mLauncher.m_iModeSet == 0) {
                this.mLauncher.showSystermTitleBar(false);
                return inflater.inflate(R.layout.kesaiwei_1280x480_nbt_fragment_main, container, false);
            } else if (this.mLauncher.m_iModeSet == 1) {
                return inflater.inflate(R.layout.kesaiwei_1280x480_audi_3d_fragment_main, container, false);
            } else {
                if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                    return inflater.inflate(R.layout.kesaiwei_1280x480_evo_fragment_main, container, false);
                }
                if (this.mLauncher.m_iModeSet == 3) {
                    return inflater.inflate(R.layout.kesaiwei_1024x600_audi_3d_fragment_main, container, false);
                }
                if (this.mLauncher.m_iModeSet == 5) {
                    if ("XinCheng".equalsIgnoreCase(this.mLauncher.xml_client)) {
                        this.mLauncher.showSystermTitleBar(false);
                        return inflater.inflate(R.layout.xincheng_1024x600_xuefulan_fragment_main, container, false);
                    } else if ("QiZhi".equalsIgnoreCase(this.mLauncher.xml_client)) {
                        return inflater.inflate(R.layout.qizhi_1024x600_yingfeinidi_fragment_main, container, false);
                    } else {
                        return inflater.inflate(R.layout.kesaiwei_1024x600_benchi_fragment_main, container, false);
                    }
                } else if (this.mLauncher.m_iModeSet == 6) {
                    return inflater.inflate(R.layout.kesaiwei_1024x600_evo_fragment_main, container, false);
                } else {
                    if (this.mLauncher.m_iModeSet == 7) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2 && "als".equalsIgnoreCase(this.mLauncher.xml_client)) {
                            return inflater.inflate(R.layout.kesaiwei_1280x480_evo_id6_als_fragment_main, container, false);
                        }
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index != 3 || !"GeShi".equalsIgnoreCase(this.mLauncher.xml_client)) {
                            return inflater.inflate(R.layout.kesaiwei_1280x480_evo_id6_fragment_main, container, false);
                        }
                        return inflater.inflate(R.layout.kesaiwei_1280x480_evo_id6_gs3_fragment_main, container, false);
                    } else if (this.mLauncher.m_iModeSet == 10) {
                        return inflater.inflate(R.layout.kesaiwei_1280x480_benchi_fragment_main, container, false);
                    } else {
                        if (this.mLauncher.m_iModeSet == 12) {
                            return inflater.inflate(R.layout.kesaiwei_1280x640_benchi_fragment_main, container, false);
                        }
                        if (this.mLauncher.m_iModeSet == 15) {
                            return inflater.inflate(R.layout.kesaiwei_1280x480_evo_id7_fragment_main, container, false);
                        }
                        if (this.mLauncher.m_iModeSet == 16) {
                            return inflater.inflate(R.layout.kesaiwei_1920x720_evo_id7_fragment_main, container, false);
                        }
                        if (this.mLauncher.m_iModeSet == 17) {
                            return inflater.inflate(R.layout.kesaiwei_1920x720_evo_id6_fragment_main, container, false);
                        }
                        return null;
                    }
                }
            }
        } else if (this.mLauncher.m_iUITypeVer != 101) {
            return null;
        } else {
            this.mLauncher.showSystermTitleBar(false);
            if (this.mLauncher.m_iUiIndex == 2) {
                return inflater.inflate(R.layout.changtong_1920x720_fragment_main_ui1, container, false);
            }
            if (this.mLauncher.m_iUiIndex == 3) {
                return inflater.inflate(R.layout.changtong_1920x720_fragment_main_ui2, container, false);
            }
            return null;
        }
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        int[] btnList = {R.id.btnLeftNavi, R.id.btnLeftMusic, R.id.btnLeftSetting, R.id.btnLeftOriginaCar, R.id.btnLeftApps, R.id.btnPagerPriv, R.id.btnPagerNext, R.id.btnSearch, R.id.btnOffScreen, R.id.btnOriginaCar, R.id.IvRightMusicIcon, R.id.btnMusicPrev, R.id.btnMusicPlayPause, R.id.btnMusicNext, R.id.btnMusic, R.id.btnVideo, R.id.btnApps, R.id.btnNavi, R.id.btnSetting, R.id.btnOriginaCar, R.id.btnPhoneLink, R.id.btnBt};
        for (int findViewById : btnList) {
            View btn = view.findViewById(findViewById);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
        this.mIvBtStatus = view.findViewById(R.id.IvBtStatus);
        this.mTvCurDataTime = (TextView) view.findViewById(R.id.TvCurDataTime);
        this.mTvCurDataTimeYMD = (TextView) view.findViewById(R.id.TvCurDataTimeYMD);
        this.mTvCurDataTimeWeek = (TextView) view.findViewById(R.id.TvCurDataTimeWeek);
        this.mBtnPagerPriv = view.findViewById(R.id.btnPagerPriv);
        this.mBtnPagerNext = view.findViewById(R.id.btnPagerNext);
        this.mTvMusicTitleInfor = (TextView) view.findViewById(R.id.tv_MusicTitleInfor);
        this.mTvDrivingRange = (TextView) view.findViewById(R.id.TvDrivingRange);
        this.mTvZhuanSu = (TextView) view.findViewById(R.id.TvZhuanSu);
        this.mClockRotatingSpeed = (MyQAnalogClock2) view.findViewById(R.id.ClockRotatingSpeed);
        if (this.mClockRotatingSpeed != null) {
            this.mClockRotatingSpeed.setmTvCur(this.mTvZhuanSu);
        }
        this.mTvTemp = (TextView) view.findViewById(R.id.TvTemp);
        this.mNaviAmapautoInfo = view.findViewById(R.id.NaviAmapautoInfo);
        this.mAmapauto_Icon = (ImageView) view.findViewById(R.id.Amapauto_Icon);
        this.mTvSegRemainDisInfor = (TextView) view.findViewById(R.id.TvSegRemainDisInfor);
        this.mTvNextRouadName = (TextView) view.findViewById(R.id.TvNextRouadName);
        this.mTvRouteRemainDis = (TextView) view.findViewById(R.id.TvRouteRemainDis);
        this.mTvRouteRemainTime = (TextView) view.findViewById(R.id.TvRouteRemainTime);
        if (this.mLauncher.m_iUITypeVer != 41) {
            int i = this.mLauncher.m_iUITypeVer;
        } else if (this.mLauncher.m_iModeSet == 0) {
            if (!this.mLauncher.bHaveAux || !this.mLauncher.bHaveTv || this.mLauncher.iHaveDvdType == 0) {
                this.imgLeftFocusList = new int[]{R.id.KSW_Left_Focus_select_0, R.id.KSW_Left_Focus_select_1, R.id.KSW_Left_Focus_select_2, R.id.KSW_Left_Focus_select_3, R.id.KSW_Left_Focus_select_4, R.id.KSW_Left_Focus_select_5, R.id.KSW_Left_Focus_select_6, R.id.KSW_Left_Focus_select_7};
                this.imgItemTypeBgList = new int[]{R.drawable.kesaiwei_1280x480_nbt_nbt_zuo_app, R.drawable.kesaiwei_1280x480_nbt_zuo_nav, R.drawable.kesaiwei_1280x480_nbt_zuo_music, R.drawable.kesaiwei_1280x480_nbt_zuo_video, R.drawable.kesaiwei_1280x480_nbt_zuo_bt, R.drawable.kesaiwei_1280x480_nbt_zuo_shoujihulian, R.drawable.kesaiwei_1280x480_nbt_zuo_dvr, R.drawable.kesaiwei_1280x480_nbt_zuo_set};
                this.imgItemTypeKnobList = new int[]{R.drawable.kesaiwei_1280x480_nbt_anniu_n_00, R.drawable.kesaiwei_1280x480_nbt_anniu_n_01, R.drawable.kesaiwei_1280x480_nbt_anniu_n_02, R.drawable.kesaiwei_1280x480_nbt_anniu_n_03, R.drawable.kesaiwei_1280x480_nbt_anniu_n_04, R.drawable.kesaiwei_1280x480_nbt_anniu_n_05, R.drawable.kesaiwei_1280x480_nbt_anniu_n_06, R.drawable.kesaiwei_1280x480_nbt_anniu_n_07};
                this.imgItemTypeIconList = new int[]{R.drawable.kesaiwei_1280x480_nbt_src_icon_app, R.drawable.kesaiwei_1280x480_nbt_src_icon_nav, R.drawable.kesaiwei_1280x480_nbt_src_icon_music, R.drawable.kesaiwei_1280x480_nbt_src_icon_video, R.drawable.kesaiwei_1280x480_nbt_src_icon_bt, R.drawable.kesaiwei_1280x480_nbt_src_icon_shoujihulian, R.drawable.kesaiwei_1280x480_nbt_src_icon_dvr, R.drawable.kesaiwei_1280x480_nbt_src_icon_set};
            } else {
                this.imgLeftFocusList = new int[]{R.id.KSW_Left_Focus_select_0, R.id.KSW_Left_Focus_select_1, R.id.KSW_Left_Focus_select_2, R.id.KSW_Left_Focus_select_3, R.id.KSW_Left_Focus_select_4, R.id.KSW_Left_Focus_select_5, R.id.KSW_Left_Focus_select_6, R.id.KSW_Left_Focus_select_7, R.id.KSW_Left_Focus_select_8, R.id.KSW_Left_Focus_select_9, R.id.KSW_Left_Focus_select_10};
                this.imgItemTypeBgList = new int[]{R.drawable.kesaiwei_1280x480_nbt_nbt_zuo_app, R.drawable.kesaiwei_1280x480_nbt_zuo_nav, R.drawable.kesaiwei_1280x480_nbt_zuo_music, R.drawable.kesaiwei_1280x480_nbt_zuo_video, R.drawable.kesaiwei_1280x480_nbt_zuo_bt, R.drawable.kesaiwei_1280x480_nbt_zuo_shoujihulian, R.drawable.kesaiwei_1280x480_nbt_zuo_dvr, R.drawable.kesaiwei_1280x480_nbt_zuo_set, R.drawable.kesaiwei_1280x480_nbt_nbt_zuo_app, R.drawable.kesaiwei_1280x480_nbt_zuo_nav, R.drawable.kesaiwei_1280x480_nbt_zuo_music};
                this.imgItemTypeKnobList = new int[]{R.drawable.kesaiwei_1280x480_nbt_anniu_n_00, R.drawable.kesaiwei_1280x480_nbt_anniu_n_01, R.drawable.kesaiwei_1280x480_nbt_anniu_n_02, R.drawable.kesaiwei_1280x480_nbt_anniu_n_03, R.drawable.kesaiwei_1280x480_nbt_anniu_n_04, R.drawable.kesaiwei_1280x480_nbt_anniu_n_05, R.drawable.kesaiwei_1280x480_nbt_anniu_n_06, R.drawable.kesaiwei_1280x480_nbt_anniu_n_07, R.drawable.kesaiwei_1280x480_nbt_anniu_n_00, R.drawable.kesaiwei_1280x480_nbt_anniu_n_01, R.drawable.kesaiwei_1280x480_nbt_anniu_n_02};
                this.imgItemTypeIconList = new int[]{R.drawable.kesaiwei_1280x480_nbt_src_icon_app, R.drawable.kesaiwei_1280x480_nbt_src_icon_nav, R.drawable.kesaiwei_1280x480_nbt_src_icon_music, R.drawable.kesaiwei_1280x480_nbt_src_icon_video, R.drawable.kesaiwei_1280x480_nbt_src_icon_bt, R.drawable.kesaiwei_1280x480_nbt_src_icon_shoujihulian, R.drawable.kesaiwei_1280x480_nbt_src_icon_dvr, R.drawable.kesaiwei_1280x480_nbt_src_icon_set, R.drawable.kesaiwei_1280x480_nbt_src_icon_aux, R.drawable.kesaiwei_1280x480_nbt_src_icon_tv, R.drawable.kesaiwei_1280x480_nbt_src_icon_dvd};
            }
            this.mIvItemTypeBg = view.findViewById(R.id.IvItemTypeBg);
            this.mIvItemTypeKnob = view.findViewById(R.id.IvItemTypeKnob);
            this.mIvItemTypeIcon = view.findViewById(R.id.IvItemTypeIcon);
        } else if (this.mLauncher.m_iModeSet == 1) {
            this.iMapIconList = new int[]{R.drawable.kesaiwei_1280x480_audi_xianshiche, R.drawable.kesaiwei_1280x480_audi_zuozhuan_n, R.drawable.kesaiwei_1280x480_audi_youzhuan_n, R.drawable.kesaiwei_1280x480_audi_zuoqian, R.drawable.kesaiwei_1280x480_audi_youqian, R.drawable.kesaiwei_1280x480_audi_zuohou, R.drawable.kesaiwei_1280x480_audi_youhou, R.drawable.kesaiwei_1280x480_audi_zuodiantou, R.drawable.kesaiwei_1280x480_audi_zhixing, R.drawable.kesaiwei_1280x480_audi_tujingdi, R.drawable.kesaiwei_1280x480_audi_likai_jinruhuanlu, R.drawable.kesaiwei_1280x480_audi_youshichuhuandao, R.drawable.kesaiwei_1280x480_audi_likai_daodafuwuzhan, R.drawable.kesaiwei_1280x480_audi_shoufeizhan, R.drawable.kesaiwei_1280x480_audi_mudidi, R.drawable.kesaiwei_1280x480_audi_likai_suidao, R.drawable.kesaiwei_1280x480_audi_zuojinruhuandao, R.drawable.kesaiwei_1280x480_audi_zuoshichuhuandao, R.drawable.kesaiwei_1280x480_audi_youdiaotou, R.drawable.kesaiwei_1280x480_audi_shunxing};
            this.ksw_A4L_audi_che = (ImageView) view.findViewById(R.id.ksw_A4L_audi_che);
            this.ksw_3d_ivGuang = (ImageView) view.findViewById(R.id.iv_guang);
            this.mIvAudiRightLogo = (ImageView) view.findViewById(R.id.KSW_you_audi);
            this.KSW_A4L_right_show_logo = (RelativeLayout) view.findViewById(R.id.KSW_A4L_right_show_logo);
            this.KSW_A4L_right_show_Navi = (RelativeLayout) view.findViewById(R.id.KSW_A4L_right_show_Navi);
            this.KSW_A4L_right_Traffic_information = (RelativeLayout) view.findViewById(R.id.KSW_A4L_right_Traffic_information);
            this.KSW_A4L_right_show_Medio = (RelativeLayout) view.findViewById(R.id.KSW_A4L_right_show_Medio);
            ksw_refresh_A4L_left_show();
            ksw_refresh_A4L_right_show();
        } else if (this.mLauncher.m_iModeSet == 3) {
            this.ksw_A4L_audi_che = (ImageView) view.findViewById(R.id.ksw_A4L_audi_che);
            this.ksw_3d_ivGuang = (ImageView) view.findViewById(R.id.iv_guang);
            ksw_refresh_A4L_left_show();
        } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
            this.mIvMainBk = (ImageView) view.findViewById(R.id.IvMainBk);
        } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
            this.imgLeftFocusList = new int[]{R.id.KSW_Left_Focus_select_0, R.id.KSW_Left_Focus_select_1, R.id.KSW_Left_Focus_select_2, R.id.KSW_Left_Focus_select_3, R.id.KSW_Left_Focus_select_4};
        }
        if (this.imgLeftFocusList != null) {
            this.imageViewLeftFocusList = new ImageView[this.imgLeftFocusList.length];
            for (int i2 = 0; i2 < this.imgLeftFocusList.length; i2++) {
                this.imageViewLeftFocusList[i2] = (ImageView) view.findViewById(this.imgLeftFocusList[i2]);
            }
        }
        initLoopRotarySwitchView(view);
        initCoverFlowView(view);
        initViewPager(view);
        setPagerPrivNextStutes();
    }

    public void ksw_refresh_A4L_left_show() {
        if (this.ksw_A4L_audi_che != null) {
            int ksw_m_i_left_show_index = this.mLauncher.mApp.getSysProviderOpt().getRecordInteger(SysProviderOpt.KSW_ARL_LEFT_SHOW_INDEX, 1);
            if (this.ksw_3d_ivGuang != null) {
                if (ksw_m_i_left_show_index == 11) {
                    this.ksw_3d_ivGuang.setVisibility(8);
                } else {
                    this.ksw_3d_ivGuang.setVisibility(0);
                }
            }
            switch (ksw_m_i_left_show_index) {
                case 0:
                    this.ksw_A4L_audi_che.setVisibility(8);
                    return;
                case 1:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_01);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_01);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_02);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_02);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_03);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_03);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q5);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q5);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_q5);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_ihavi);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_ihavi);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_a3);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_a3);
                        return;
                    } else {
                        return;
                    }
                case 7:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_a5);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_a5);
                        return;
                    } else {
                        return;
                    }
                case 8:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_a6);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_a6);
                        return;
                    } else {
                        return;
                    }
                case 9:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q3);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_q3);
                        return;
                    } else {
                        return;
                    }
                case 10:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    if (this.mLauncher.m_iModeSet == 1) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q7);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 3) {
                        this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_q7);
                        return;
                    } else {
                        return;
                    }
                case 11:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_normal);
                    return;
                default:
                    return;
            }
        }
    }

    public void ksw_refresh_A4L_right_show() {
        if (this.mLauncher.m_iModeSet == 1 && this.KSW_A4L_right_show_logo != null && this.KSW_A4L_right_show_Navi != null && this.KSW_A4L_right_Traffic_information != null && this.KSW_A4L_right_show_Medio != null) {
            this.ksw_m_i_right_show_index = this.mLauncher.mApp.getSysProviderOpt().getRecordInteger(SysProviderOpt.KSW_ARL_RIGHT_SHOW_INDEX, 1);
            Log.i(TAG, "ksw_refresh_A4L_right_show: ksw_m_i_right_show_index = " + this.ksw_m_i_right_show_index);
            switch (this.ksw_m_i_right_show_index) {
                case 0:
                    this.KSW_A4L_right_show_logo.setVisibility(8);
                    this.KSW_A4L_right_show_Navi.setVisibility(8);
                    this.KSW_A4L_right_Traffic_information.setVisibility(8);
                    this.KSW_A4L_right_show_Medio.setVisibility(8);
                    return;
                case 1:
                    this.KSW_A4L_right_show_logo.setVisibility(0);
                    this.KSW_A4L_right_show_Navi.setVisibility(0);
                    this.KSW_A4L_right_Traffic_information.setVisibility(8);
                    this.KSW_A4L_right_show_Medio.setVisibility(8);
                    return;
                case 2:
                    this.KSW_A4L_right_show_logo.setVisibility(0);
                    this.KSW_A4L_right_show_Navi.setVisibility(8);
                    this.KSW_A4L_right_Traffic_information.setVisibility(8);
                    this.KSW_A4L_right_show_Medio.setVisibility(8);
                    return;
                case 3:
                    this.KSW_A4L_right_show_logo.setVisibility(8);
                    this.KSW_A4L_right_show_Navi.setVisibility(8);
                    this.KSW_A4L_right_Traffic_information.setVisibility(0);
                    this.KSW_A4L_right_show_Medio.setVisibility(8);
                    return;
                case 4:
                    this.KSW_A4L_right_show_logo.setVisibility(8);
                    this.KSW_A4L_right_show_Navi.setVisibility(8);
                    this.KSW_A4L_right_Traffic_information.setVisibility(8);
                    this.KSW_A4L_right_show_Medio.setVisibility(0);
                    return;
                default:
                    return;
            }
        }
    }

    private void initLoopRotarySwitchView(View view) {
        this.mLoopRotarySwitchView = (LoopRotarySwitchView) view.findViewById(R.id.MLoopRotarySwitchView);
        if (this.mLoopRotarySwitchView != null) {
            if (this.mLauncher.m_iModeSet == 1) {
                this.mLoopRotarySwitchView.setMultiple(6.0f).setR(((float) this.mLauncher.getWindowManager().getDefaultDisplay().getWidth()) / 4.0f).setLoopRotationX(-22);
            } else if (this.mLauncher.m_iModeSet == 3) {
                this.mLoopRotarySwitchView.setMultiple(6.0f).setR(((float) this.mLauncher.getWindowManager().getDefaultDisplay().getWidth()) / 2.9f).setLoopRotationX(-28);
            }
            this.mLoopRotarySwitchView.setOnItemClickListener(new LoopRotarySwitchView.OnItemClickListener() {
                public void onItemClick(int item, View view) {
                    Log.i("TAG", "Activity点击的item===" + item);
                    RelativeLayout childView = (RelativeLayout) view;
                    MainFragment.this.setItemClickEvent(childView);
                    MainFragment.this.setBackgroundColor(childView.getChildAt(0), (TextView) childView.getChildAt(1), true);
                    if (!(MainFragment.ksw_3d_lastPosition == -1 || MainFragment.ksw_3d_lastPosition == item)) {
                        RelativeLayout childView2 = (RelativeLayout) MainFragment.this.ksw_3d_views.get(MainFragment.ksw_3d_lastPosition);
                        MainFragment.this.setBackgroundColorNull(childView2.getChildAt(0), (TextView) childView2.getChildAt(1));
                    }
                    int unused = MainFragment.ksw_3d_currentPosition = item;
                    int unused2 = MainFragment.ksw_3d_lastPosition = MainFragment.ksw_3d_currentPosition;
                }
            });
            this.mLoopRotarySwitchView.setOnItemSelectedListener(new LoopRotarySwitchView.OnItemSelectedListener() {
                public void selected(int item, View view) {
                    int unused = MainFragment.this.ksw_3d_selectItem = MainFragment.this.mLoopRotarySwitchView.getSelectItem();
                    Log.i("TAG", "setOnItemSelectedListener---selectItem===" + MainFragment.this.ksw_3d_selectItem);
                    Log.i("TAG", "setOnItemSelectedListener---item===" + item);
                    List unused2 = MainFragment.this.ksw_3d_views = MainFragment.this.mLoopRotarySwitchView.getViews();
                    RelativeLayout childView = (RelativeLayout) view;
                    ImageView ivChildAt = (ImageView) childView.getChildAt(0);
                    TextView tvChildAt = (TextView) childView.getChildAt(1);
                    if (MainFragment.this.m_b_ksw_init_accOn_focus_index) {
                        boolean unused3 = MainFragment.this.m_b_ksw_init_accOn_focus_index = false;
                    } else {
                        MainFragment.this.setBackgroundColor(ivChildAt, tvChildAt, false);
                    }
                    if (!(MainFragment.ksw_3d_lastPosition == -1 || MainFragment.ksw_3d_lastPosition == item)) {
                        RelativeLayout childView2 = (RelativeLayout) MainFragment.this.ksw_3d_views.get(MainFragment.ksw_3d_lastPosition);
                        MainFragment.this.setBackgroundColorNull((ImageView) childView2.getChildAt(0), (TextView) childView2.getChildAt(1));
                    }
                    int unused4 = MainFragment.ksw_3d_currentPosition = item;
                    int unused5 = MainFragment.ksw_3d_lastPosition = item;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void setItemClickEvent(RelativeLayout childView) {
        switch (childView.getId()) {
            case R.id.rl_BtMusic:
                Intent ii = new Intent();
                ii.addCategory("android.intent.category.LAUNCHER");
                Bundle mBundle = new Bundle();
                mBundle.putInt("GotoPageNum", 3);
                ii.putExtras(mBundle);
                ii.setComponent(new ComponentName(EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME));
                ii.setFlags(270532608);
                startActivity(ii);
                return;
            case R.id.rl_browser:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                return;
            case R.id.rl_bt:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case R.id.rl_dvr:
                EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                return;
            case R.id.rl_music:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case R.id.rl_navi:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case R.id.rl_settings:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case R.id.rl_shoujihulian:
                EventUtils.onEnterPhoneLink(this.mLauncher);
                return;
            case R.id.rl_video:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case R.id.rl_yuanche:
                this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void setBackgroundColor(View ivChildAt, View tvChildAt, boolean b) {
        int ivRsId = ivChildAt.getId();
        int tvRsId = tvChildAt.getId();
        if (ivRsId == R.id.iv_BtMusic) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_lanyayinyue_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_aux));
            }
        } else if (ivRsId == R.id.iv_daohang) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_daohang_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_daohang));
            }
        } else if (ivRsId == R.id.iv_lanya) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_lanya_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_lanya));
            }
        } else if (ivRsId == R.id.iv_luluyi) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_luluyi_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_jiluyi));
            }
        } else if (ivRsId == R.id.iv_shezhi) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_shezhi_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_shezhi));
            }
        } else if (ivRsId == R.id.iv_shipin) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_shipin_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_jiluyi));
            }
        } else if (ivRsId == R.id.iv_shoujihulian) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_shoujihulian_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_shoujihulian));
            }
        } else if (ivRsId == R.id.iv_browser) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_browser_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_aux));
            }
        } else if (ivRsId == R.id.iv_yinyue) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_yinyue_d));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_yinyue));
            }
        } else if (ivRsId == R.id.iv_yuanche) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_yuanche_d_2));
            }
            if (this.ksw_3d_ivGuang != null) {
                this.ksw_3d_ivGuang.setBackground((Drawable) null);
                this.ksw_3d_ivGuang.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_guang_lanya));
            }
        }
        if (tvRsId == R.id.tv_daohang || tvRsId == R.id.tv_luluyi || tvRsId == R.id.tv_shipin || tvRsId == R.id.tv_shoujihulian) {
            tvChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_item_selected_p_shape_blue));
        } else if (tvRsId == R.id.tv_lanya || tvRsId == R.id.tv_yuanche) {
            tvChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_item_selected_p_shape_green));
        } else if (tvRsId == R.id.tv_BtMusic || tvRsId == R.id.tv_browser) {
            tvChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_item_selected_p_shape_yellow));
        } else if (tvRsId == R.id.tv_yinyue) {
            tvChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_item_selected_p_shape_red));
        } else if (tvRsId == R.id.tv_shezhi) {
            tvChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_item_selected_p_shape_gray));
        }
    }

    /* access modifiers changed from: private */
    public void setBackgroundColorNull(View ivChildAt, View tvChildAt) {
        int ivRsId = ivChildAt.getId();
        if (ivRsId == R.id.iv_BtMusic) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_lanyayinyue_n));
        } else if (ivRsId == R.id.iv_daohang) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_daohang_n));
        } else if (ivRsId == R.id.iv_lanya) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_lanya_n));
        } else if (ivRsId == R.id.iv_luluyi) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_luluyi_n));
        } else if (ivRsId == R.id.iv_shezhi) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_shezhi_n));
        } else if (ivRsId == R.id.iv_shipin) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_shipin_n));
        } else if (ivRsId == R.id.iv_shoujihulian) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_shoujihulian_n));
        } else if (ivRsId == R.id.iv_browser) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_browser_n));
        } else if (ivRsId == R.id.iv_yinyue) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_yinyue_n));
        } else if (ivRsId == R.id.iv_yuanche) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_yuanche_n_2));
        }
        tvChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_item_selected_n_shape));
    }

    @SuppressLint({"InflateParams"})
    private void initCoverFlowView(View view) {
        this.mCoverFlowView = (CoverFlowView) view.findViewById(R.id.MCoverFlowView);
        if (this.mCoverFlowView != null) {
            this.mCoverFlowView.setLastModeOffset(this.mLauncher.mApp.getSysProviderOpt().getRecordFloat(SysProviderOpt.SYS_LAST_MODE_OFFSET, 0.0f));
            final ArrayList<Bitmap> imgList = new ArrayList<>();
            int[] iLogoRes = null;
            String[] iLogoResname = null;
            if (this.mLauncher.m_iUITypeVer == 41) {
                if (this.mLauncher.m_iModeSet == 5) {
                    iLogoRes = new int[]{R.drawable.kesaiwei_1024x600_benchi_lanya_n, R.drawable.kesaiwei_1024x600_benchi_daohang_n, R.drawable.kesaiwei_1024x600_benchi_lanyayinyue_n, R.drawable.kesaiwei_1024x600_benchi_shezhi_n, R.drawable.kesaiwei_1024x600_benchi_yinyue_n, R.drawable.kesaiwei_1024x600_benchi_gaoqingshipin_n, R.drawable.kesaiwei_1024x600_benchi_yilian_n, R.drawable.kesaiwei_1024x600_benchi_yingyong_n, R.drawable.kesaiwei_1024x600_benchi_yuanche_n, R.drawable.kesaiwei_1024x600_benchi_dvr_n};
                } else if (this.mLauncher.m_iModeSet == 10) {
                    iLogoRes = new int[]{R.drawable.kesaiwei_1280x480_benchi_lanya_n, R.drawable.kesaiwei_1280x480_benchi_daohang_n, R.drawable.kesaiwei_1280x480_benchi_lanyayinyue_n, R.drawable.kesaiwei_1280x480_benchi_shezhi_n, R.drawable.kesaiwei_1280x480_benchi_yinyue_n, R.drawable.kesaiwei_1280x480_benchi_shipin_n, R.drawable.kesaiwei_1280x480_benchi_shoujihulian_n, R.drawable.kesaiwei_1280x480_benchi_yingyong_n, R.drawable.kesaiwei_1280x480_benchi_yuanche_n, R.drawable.kesaiwei_1280x480_benchi_dvr_n};
                } else if (this.mLauncher.m_iModeSet == 12) {
                    iLogoRes = new int[]{R.drawable.kesaiwei_1280x640_benchi_lanya_n, R.drawable.kesaiwei_1280x640_benchi_daohang_n, R.drawable.kesaiwei_1280x640_benchi_lanyayinyue_n, R.drawable.kesaiwei_1280x640_benchi_shezhi_n, R.drawable.kesaiwei_1280x640_benchi_yinyue_n, R.drawable.kesaiwei_1280x640_benchi_shipin_n, R.drawable.kesaiwei_1280x640_benchi_shoujihulian_n, R.drawable.kesaiwei_1280x640_benchi_yingyong_n, R.drawable.kesaiwei_1280x640_benchi_yuanche_n, R.drawable.kesaiwei_1280x640_benchi_jiluyi_n};
                }
                iLogoResname = new String[]{getString(R.string.lb_bt), getString(R.string.lb_navi), getString(R.string.lb_btmusic), getString(R.string.lb_settings), getString(R.string.lb_music), getString(R.string.lb_video), getString(R.string.lb_shoujihulian), getString(R.string.all_apps_button_label), getString(R.string.lb_yuanche_q5_ksw), getString(R.string.lb_dvr)};
            }
            if (iLogoRes != null && iLogoRes.length > 0) {
                View convertView = null;
                for (int i = 0; i < iLogoRes.length; i++) {
                    if (this.mLauncher.m_iUITypeVer == 41) {
                        if (this.mLauncher.m_iModeSet == 5) {
                            convertView = this.mLauncher.getLayoutInflater().inflate(R.layout.kesaiwei_1024x600_benchi_load_image_item, (ViewGroup) null);
                        } else if (this.mLauncher.m_iModeSet == 10) {
                            convertView = this.mLauncher.getLayoutInflater().inflate(R.layout.kesaiwei_1280x480_benchi_load_image_item, (ViewGroup) null);
                        } else if (this.mLauncher.m_iModeSet == 12) {
                            convertView = this.mLauncher.getLayoutInflater().inflate(R.layout.kesaiwei_1280x640_benchi_load_image_item, (ViewGroup) null);
                        }
                    }
                    View findViewById = convertView.findViewById(R.id.item_image_bk);
                    ImageView imageview = (ImageView) convertView.findViewById(R.id.item_image);
                    TextView text = (TextView) convertView.findViewById(R.id.item_text);
                    if (imageview != null) {
                        imageview.setBackgroundResource(iLogoRes[i]);
                    }
                    if (text != null) {
                        text.setText(iLogoResname[i]);
                    }
                    Bitmap bitmap = EventUtils.convertViewToBitmap(convertView);
                    if (bitmap != null) {
                        imgList.add(bitmap);
                    }
                }
            }
            this.mCoverFlowView.setAdapter(new CoverFlowView.CoverFlowAdapter() {
                public int getCount() {
                    if (imgList == null) {
                        return 0;
                    }
                    return imgList.size();
                }

                public Bitmap getImage(int position) {
                    return (Bitmap) imgList.get(position);
                }
            });
            this.mCoverFlowView.setCoverFlowListener(new CoverFlowView.CoverFlowListener() {
                public void imageOnTop(CoverFlowView coverFlowView, int position, float left, float top, float right, float bottom) {
                }

                public void topImageClicked(CoverFlowView coverFlowView, int position) {
                    Log.i(MainFragment.TAG, "topImageClicked: position = " + position);
                    SysProviderOpt sysProviderOpt = MainFragment.this.mLauncher.mApp.getSysProviderOpt();
                    sysProviderOpt.updateRecord(SysProviderOpt.SYS_LAST_MODE_OFFSET, MainFragment.this.mCoverFlowView.getLastModeOffset() + "");
                    MainFragment.this.onClickCoverFlowView(position);
                    MainFragment.this.mCoverFlowView.setSelection(position);
                }

                public void invalidationCompleted() {
                }
            });
        }
    }

    private void add_sub(boolean isAuto2) {
        if (ksw_3d_lastPosition != -1) {
            RelativeLayout oldChildView = (RelativeLayout) this.ksw_3d_views.get(ksw_3d_lastPosition);
            setBackgroundColorNull(oldChildView.getChildAt(0), oldChildView.getChildAt(1));
        }
        RelativeLayout childView = (RelativeLayout) this.ksw_3d_views.get(ksw_3d_currentPosition);
        setBackgroundColor(childView.getChildAt(0), childView.getChildAt(1), false);
        if (isAuto2) {
            this.mLoopRotarySwitchView.setSelectItem(ksw_3d_currentPosition, false);
        }
        Log.i("TAG", "onClick---getSelectItem===" + this.mLoopRotarySwitchView.getSelectItem());
        ksw_3d_lastPosition = ksw_3d_currentPosition;
    }

    public void coverFlowLeftScroll() {
        if (this.fFlowViewLeft <= 410.0f) {
            this.fFlowViewLeft -= 20.0f;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, this.fFlowViewLeft + ((float) this.mCoverFlowView.getLeft()), (float) (this.mCoverFlowView.getTop() + 125), 0));
            this.mLauncher.getMainHandler().removeMessages(2);
            this.mCoverFlowView.setbScroll(false);
            return;
        }
        this.fFlowViewLeft -= 20.0f;
        this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, this.fFlowViewLeft + ((float) this.mCoverFlowView.getLeft()), (float) (this.mCoverFlowView.getTop() + 125), 0));
        this.mLauncher.getMainHandler().removeMessages(2);
        this.mLauncher.getMainHandler().sendEmptyMessageDelayed(2, 10);
    }

    public void coverFlowRightScroll() {
        if (this.fFlowViewRight >= 870.0f) {
            this.fFlowViewRight += 20.0f;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, this.fFlowViewRight + ((float) this.mCoverFlowView.getLeft()), (float) (this.mCoverFlowView.getTop() + 125), 0));
            this.mLauncher.getMainHandler().removeMessages(3);
            this.mCoverFlowView.setbScroll(false);
            return;
        }
        this.fFlowViewRight += 20.0f;
        this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, this.fFlowViewRight + ((float) this.mCoverFlowView.getLeft()), (float) (this.mCoverFlowView.getTop() + 125), 0));
        this.mLauncher.getMainHandler().removeMessages(3);
        this.mLauncher.getMainHandler().sendEmptyMessageDelayed(3, 10);
    }

    /* access modifiers changed from: private */
    public void onClickCoverFlowView(int position) {
        switch (position) {
            case 0:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 2:
                Intent ii = new Intent();
                ii.addCategory("android.intent.category.LAUNCHER");
                Bundle mBundle = new Bundle();
                mBundle.putInt("GotoPageNum", 3);
                ii.putExtras(mBundle);
                ii.setComponent(new ComponentName(EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME));
                ii.setFlags(270532608);
                startActivity(ii);
                return;
            case 3:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 4:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 5:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 6:
                EventUtils.onEnterPhoneLink(this.mLauncher);
                return;
            case 7:
                this.mLauncher.showApps(true);
                return;
            case 8:
                this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            case 9:
                EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                return;
            default:
                return;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:62:0x0210  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0238  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0255  */
    @android.annotation.SuppressLint({"InflateParams"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initViewPager(android.view.View r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            java.lang.String r2 = "MainFragment"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "initViewPager: m_iModeSet = "
            r3.append(r4)
            com.android.launcher3.Launcher r4 = r0.mLauncher
            int r4 = r4.m_iModeSet
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Log.i(r2, r3)
            r2 = 2131427396(0x7f0b0044, float:1.8476407E38)
            android.view.View r2 = r1.findViewById(r2)
            android.support.v4.view.ViewPager r2 = (android.support.v4.view.ViewPager) r2
            r0.mViewPager = r2
            android.support.v4.view.ViewPager r2 = r0.mViewPager
            if (r2 != 0) goto L_0x002e
            return
        L_0x002e:
            r2 = 2131427600(0x7f0b0110, float:1.847682E38)
            android.view.View r2 = r1.findViewById(r2)
            android.widget.ImageView r2 = (android.widget.ImageView) r2
            r0.mPage0 = r2
            r2 = 2131427601(0x7f0b0111, float:1.8476823E38)
            android.view.View r2 = r1.findViewById(r2)
            android.widget.ImageView r2 = (android.widget.ImageView) r2
            r0.mPage1 = r2
            r2 = 2131427602(0x7f0b0112, float:1.8476825E38)
            android.view.View r2 = r1.findViewById(r2)
            android.widget.ImageView r2 = (android.widget.ImageView) r2
            r0.mPage2 = r2
            com.android.launcher3.Launcher r2 = r0.mLauncher
            android.view.LayoutInflater r2 = android.view.LayoutInflater.from(r2)
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iUITypeVer
            r12 = 41
            r14 = 8
            r13 = 3
            r15 = 0
            if (r11 != r12) goto L_0x0556
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            if (r11 != 0) goto L_0x00d5
            r0.iPagerItemViewItemCount = r14
            r11 = 2131624040(0x7f0e0068, float:1.8875248E38)
            android.view.View r3 = r2.inflate(r11, r15)
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveAux
            if (r11 == 0) goto L_0x0091
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x0091
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.iHaveDvdType
            if (r11 == 0) goto L_0x0091
            r11 = 2131624041(0x7f0e0069, float:1.887525E38)
            android.view.View r4 = r2.inflate(r11, r15)
        L_0x0091:
            int[] r11 = new int[r14]
            r11 = {2131427465, 2131427483, 2131427479, 2131427493, 2131427468, 2131427488, 2131427472, 2131427491} // fill-array
            r7 = r11
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveAux
            if (r11 == 0) goto L_0x00af
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x00af
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.iHaveDvdType
            if (r11 == 0) goto L_0x00af
            int[] r11 = new int[r13]
            r11 = {2131427466, 2131427492, 2131427471} // fill-array
            r8 = r11
        L_0x00af:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveAux
            if (r11 == 0) goto L_0x00cb
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x00cb
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.iHaveDvdType
            if (r11 == 0) goto L_0x00cb
            r11 = 11
            int[] r11 = new int[r11]
            r11 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356, 2131427357, 2131427358, 2131427359, 2131427360, 2131427350} // fill-array
            r0.imgFocusList = r11
            goto L_0x00d2
        L_0x00cb:
            int[] r11 = new int[r14]
            r11 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356, 2131427357, 2131427358} // fill-array
            r0.imgFocusList = r11
        L_0x00d2:
            r12 = 0
            goto L_0x0599
        L_0x00d5:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r14 = 1
            r12 = 6
            r13 = 2
            if (r11 == r13) goto L_0x0413
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r13 = 4
            if (r11 != r13) goto L_0x00e7
            goto L_0x0413
        L_0x00e7:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            if (r11 != r12) goto L_0x00ee
            goto L_0x00d2
        L_0x00ee:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r12 = 7
            r15 = 2131427681(0x7f0b0161, float:1.8476985E38)
            if (r11 != r12) goto L_0x0275
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_id6_main_interface_index
            if (r11 != r14) goto L_0x0138
            java.lang.String r11 = "GeShi"
            com.android.launcher3.Launcher r12 = r0.mLauncher
            java.lang.String r12 = r12.xml_client
            boolean r11 = r11.equalsIgnoreCase(r12)
            if (r11 == 0) goto L_0x0138
            r0.iPagerItemViewItemCount = r13
            r11 = 2131624017(0x7f0e0051, float:1.8875202E38)
            r12 = 0
            android.view.View r3 = r2.inflate(r11, r12)
            r11 = 2131624018(0x7f0e0052, float:1.8875204E38)
            android.view.View r4 = r2.inflate(r11, r12)
            r11 = 2131624019(0x7f0e0053, float:1.8875206E38)
            android.view.View r5 = r2.inflate(r11, r12)
            int[] r11 = new int[r13]
            r11 = {2131427483, 2131427479, 2131427468, 2131427493} // fill-array
            r7 = r11
            int[] r11 = new int[r13]
            r11 = {2131427485, 2131427473, 2131427467, 2131427472} // fill-array
            r8 = r11
            int[] r11 = new int[r13]
            r11 = {2131427470, 2131427488, 2131427465, 2131427491} // fill-array
            r9 = r11
        L_0x0134:
            r11 = 3
            r12 = 0
            goto L_0x020a
        L_0x0138:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_id6_main_interface_index
            r12 = 2
            if (r11 != r12) goto L_0x0176
            java.lang.String r11 = "als"
            com.android.launcher3.Launcher r12 = r0.mLauncher
            java.lang.String r12 = r12.xml_client
            boolean r11 = r11.equalsIgnoreCase(r12)
            if (r11 == 0) goto L_0x0176
            r0.iPagerItemViewItemCount = r13
            r11 = 2131624007(0x7f0e0047, float:1.8875182E38)
            r12 = 0
            android.view.View r3 = r2.inflate(r11, r12)
            r11 = 2131624008(0x7f0e0048, float:1.8875184E38)
            android.view.View r4 = r2.inflate(r11, r12)
            r11 = 2131624009(0x7f0e0049, float:1.8875186E38)
            android.view.View r5 = r2.inflate(r11, r12)
            int[] r11 = new int[r13]
            r11 = {2131427483, 2131427485, 2131427479, 2131427493} // fill-array
            r7 = r11
            int[] r11 = new int[r13]
            r11 = {2131427468, 2131427473, 2131427467, 2131427472} // fill-array
            r8 = r11
            int[] r11 = new int[r13]
            r11 = {2131427470, 2131427488, 2131427465, 2131427491} // fill-array
            r9 = r11
            goto L_0x0134
        L_0x0176:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_id6_main_interface_index
            r12 = 3
            if (r11 != r12) goto L_0x01a8
            java.lang.String r11 = "GeShi"
            com.android.launcher3.Launcher r12 = r0.mLauncher
            java.lang.String r12 = r12.xml_client
            boolean r11 = r11.equalsIgnoreCase(r12)
            if (r11 == 0) goto L_0x01a8
            r11 = 5
            r0.iPagerItemViewItemCount = r11
            r12 = 2131624015(0x7f0e004f, float:1.8875198E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r12, r13)
            r12 = 2131624016(0x7f0e0050, float:1.88752E38)
            android.view.View r4 = r2.inflate(r12, r13)
            int[] r12 = new int[r11]
            r12 = {2131427485, 2131427470, 2131427483, 2131427468, 2131427465} // fill-array
            r7 = r12
            int[] r11 = new int[r11]
            r11 = {2131427479, 2131427493, 2131427467, 2131427473, 2131427491} // fill-array
            r8 = r11
            goto L_0x0134
        L_0x01a8:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            r12 = 0
            r11.m_i_ksw_evo_id6_main_interface_index = r12
            com.android.launcher3.Launcher r11 = r0.mLauncher
            com.android.launcher3.SysProviderOpt r11 = r11.mSysProviderOpt
            java.lang.String r13 = "KSW_EVO_ID6_MAIN_INTERFACE_INDEX"
            java.lang.String r14 = "0"
            r11.updateRecord(r13, r14)
            r11 = 3
            r0.iPagerItemViewItemCount = r11
            r11 = 2131624020(0x7f0e0054, float:1.8875208E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624021(0x7f0e0055, float:1.887521E38)
            android.view.View r4 = r2.inflate(r11, r13)
            r11 = 2131624022(0x7f0e0056, float:1.8875212E38)
            android.view.View r5 = r2.inflate(r11, r13)
            r11 = 2131624024(0x7f0e0058, float:1.8875216E38)
            android.view.View r6 = r2.inflate(r11, r13)
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x01e5
            r11 = 2131624023(0x7f0e0057, float:1.8875214E38)
            android.view.View r5 = r2.inflate(r11, r13)
        L_0x01e5:
            r11 = 3
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427483, 2131427468} // fill-array
            r7 = r13
            int[] r13 = new int[r11]
            r13 = {2131427493, 2131427485, 2131427467} // fill-array
            r8 = r13
            int[] r13 = new int[r11]
            r13 = {2131427473, 2131427472, 2131427470} // fill-array
            r9 = r13
            int[] r13 = new int[r11]
            r13 = {2131427488, 2131427465, 2131427491} // fill-array
            r10 = r13
            com.android.launcher3.Launcher r13 = r0.mLauncher
            boolean r13 = r13.bHaveTv
            if (r13 == 0) goto L_0x020a
            int[] r13 = new int[r11]
            r13 = {2131427473, 2131427492, 2131427470} // fill-array
            r9 = r13
        L_0x020a:
            com.android.launcher3.Launcher r13 = r0.mLauncher
            int r13 = r13.m_i_ksw_evo_id6_main_interface_index
            if (r13 != r11) goto L_0x021a
            r11 = 10
            int[] r11 = new int[r11]
            r11 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356, 2131427357, 2131427358, 2131427359, 2131427360} // fill-array
            r0.imgFocusList = r11
            goto L_0x0223
        L_0x021a:
            r11 = 12
            int[] r11 = new int[r11]
            r11 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356, 2131427357, 2131427358, 2131427359, 2131427360, 2131427350, 2131427351} // fill-array
            r0.imgFocusList = r11
        L_0x0223:
            android.view.View r11 = r3.findViewById(r15)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicTitleInfor = r11
            r11 = 2131427680(0x7f0b0160, float:1.8476983E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicArtistInfor = r11
            if (r5 == 0) goto L_0x024e
            r11 = 2131427409(0x7f0b0051, float:1.8476433E38)
            android.view.View r13 = r5.findViewById(r11)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r0.mTvQil = r13
            r11 = 2131427406(0x7f0b004e, float:1.8476427E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvDrivingRange = r11
        L_0x024e:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_id6_main_interface_index
            r13 = 3
            if (r11 != r13) goto L_0x0599
            r11 = 2131427333(0x7f0b0005, float:1.847628E38)
            android.view.View r11 = r3.findViewById(r11)
            com.android.launcher3.views.MyQAnalogClock2 r11 = (com.android.launcher3.views.MyQAnalogClock2) r11
            r0.mClockRotatingSpeed = r11
            r11 = 2131427409(0x7f0b0051, float:1.8476433E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvQil = r11
            android.view.View r11 = r4.findViewById(r15)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicTitleInfor = r11
            goto L_0x0599
        L_0x0275:
            r12 = 0
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r13 = 15
            if (r11 == r13) goto L_0x0308
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r14 = 16
            if (r11 != r14) goto L_0x0288
            goto L_0x0308
        L_0x0288:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r13 = 17
            if (r11 != r13) goto L_0x0599
            r11 = 3
            r0.iPagerItemViewItemCount = r11
            r11 = 2131624045(0x7f0e006d, float:1.8875259E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624046(0x7f0e006e, float:1.887526E38)
            android.view.View r4 = r2.inflate(r11, r13)
            r11 = 2131624047(0x7f0e006f, float:1.8875263E38)
            android.view.View r5 = r2.inflate(r11, r13)
            r11 = 2131624048(0x7f0e0070, float:1.8875265E38)
            android.view.View r6 = r2.inflate(r11, r13)
            r11 = 3
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427483, 2131427468} // fill-array
            r7 = r13
            int[] r13 = new int[r11]
            r13 = {2131427493, 2131427485, 2131427467} // fill-array
            r8 = r13
            int[] r13 = new int[r11]
            r13 = {2131427473, 2131427472, 2131427470} // fill-array
            r9 = r13
            int[] r11 = new int[r11]
            r11 = {2131427488, 2131427465, 2131427491} // fill-array
            r10 = r11
            r11 = 12
            int[] r11 = new int[r11]
            r11 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356, 2131427357, 2131427358, 2131427359, 2131427360, 2131427350, 2131427351} // fill-array
            r0.imgFocusList = r11
            android.view.View r11 = r3.findViewById(r15)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicTitleInfor = r11
            r11 = 2131427680(0x7f0b0160, float:1.8476983E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicArtistInfor = r11
            r11 = 2131427562(0x7f0b00ea, float:1.8476744E38)
            android.view.View r11 = r3.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r0.mMusicCover = r11
            r11 = 2131427409(0x7f0b0051, float:1.8476433E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvQil = r11
            r11 = 2131427406(0x7f0b004e, float:1.8476427E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvDrivingRange = r11
            goto L_0x0599
        L_0x0308:
            r11 = 2
            r0.iPagerItemViewItemCount = r11
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            if (r11 != r13) goto L_0x0328
            r11 = 2131624026(0x7f0e005a, float:1.887522E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624027(0x7f0e005b, float:1.8875222E38)
            android.view.View r4 = r2.inflate(r11, r13)
            r11 = 2131624028(0x7f0e005c, float:1.8875224E38)
            android.view.View r5 = r2.inflate(r11, r13)
            goto L_0x0346
        L_0x0328:
            r13 = 0
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iModeSet
            r14 = 16
            if (r11 != r14) goto L_0x0346
            r11 = 2131624050(0x7f0e0072, float:1.8875269E38)
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624051(0x7f0e0073, float:1.887527E38)
            android.view.View r4 = r2.inflate(r11, r13)
            r11 = 2131624052(0x7f0e0074, float:1.8875273E38)
            android.view.View r5 = r2.inflate(r11, r13)
        L_0x0346:
            r11 = 2
            int[] r13 = new int[r11]
            r13 = {2131427483, 2131427468} // fill-array
            r7 = r13
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427493} // fill-array
            r8 = r13
            int[] r11 = new int[r11]
            r11 = {2131427485, 2131427470} // fill-array
            r9 = r11
            r11 = 6
            int[] r11 = new int[r11]
            r11 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356} // fill-array
            r0.imgFocusList = r11
            r11 = 2131427561(0x7f0b00e9, float:1.8476742E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r0.mIvMediaTypeBg = r11
            r11 = 2131427563(0x7f0b00eb, float:1.8476746E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r0.mMusicCoverBg = r11
            r11 = 2131427562(0x7f0b00ea, float:1.8476744E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.ImageView r11 = (android.widget.ImageView) r11
            r0.mMusicCover = r11
            android.view.View r11 = r4.findViewById(r15)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicTitleInfor = r11
            r11 = 2131427680(0x7f0b0160, float:1.8476983E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicArtistInfor = r11
            r11 = 2131427679(0x7f0b015f, float:1.8476981E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvMusicAblumInfor = r11
            r11 = 2131427401(0x7f0b0049, float:1.8476417E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.SeekBar r11 = (android.widget.SeekBar) r11
            r0.mSkBarProgress = r11
            r11 = 2131427405(0x7f0b004d, float:1.8476425E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvCurrTime = r11
            r11 = 2131427417(0x7f0b0059, float:1.847645E38)
            android.view.View r11 = r4.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvTotTime = r11
            r11 = 2131427409(0x7f0b0051, float:1.8476433E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvQil = r11
            r11 = 2131427416(0x7f0b0058, float:1.8476448E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvTemp = r11
            r11 = 2131427407(0x7f0b004f, float:1.847643E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvHandbrake = r11
            r11 = 2131427412(0x7f0b0054, float:1.847644E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r0.mTvSeatBelt = r11
            r11 = 2131427414(0x7f0b0056, float:1.8476444E38)
            android.view.View r11 = r5.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r13 = 2131427415(0x7f0b0057, float:1.8476446E38)
            android.view.View r13 = r5.findViewById(r13)
            android.widget.TextView r13 = (android.widget.TextView) r13
            r0.mTvSpeedUnit = r13
            r13 = 2131427334(0x7f0b0006, float:1.8476281E38)
            android.view.View r13 = r5.findViewById(r13)
            com.android.launcher3.views.MyQAnalogClock2 r13 = (com.android.launcher3.views.MyQAnalogClock2) r13
            r0.mClockSpeed = r13
            com.android.launcher3.views.MyQAnalogClock2 r13 = r0.mClockSpeed
            r13.setmTvCur(r11)
            goto L_0x0599
        L_0x0413:
            r12 = 0
            r11 = 6
            r0.iPagerItemViewItemCount = r11
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_main_interface_index
            r13 = 2
            if (r11 != r13) goto L_0x0448
            android.widget.ImageView r11 = r0.mIvMainBk
            if (r11 == 0) goto L_0x042a
            android.widget.ImageView r11 = r0.mIvMainBk
            r13 = 2131231472(0x7f0802f0, float:1.8079026E38)
            r11.setBackgroundResource(r13)
        L_0x042a:
            r11 = 2131624029(0x7f0e005d, float:1.8875226E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624030(0x7f0e005e, float:1.8875228E38)
            android.view.View r4 = r2.inflate(r11, r13)
            r11 = 6
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427483, 2131427468, 2131427485, 2131427493, 2131427491} // fill-array
            r7 = r13
            int[] r11 = new int[r11]
            r11 = {2131427488, 2131427472, 2131427473, 2131427467, 2131427465, 2131427470} // fill-array
            r8 = r11
            goto L_0x0545
        L_0x0448:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_main_interface_index
            r13 = 3
            if (r11 != r13) goto L_0x0479
            android.widget.ImageView r11 = r0.mIvMainBk
            if (r11 == 0) goto L_0x045b
            android.widget.ImageView r11 = r0.mIvMainBk
            r13 = 2131231509(0x7f080315, float:1.8079101E38)
            r11.setBackgroundResource(r13)
        L_0x045b:
            r11 = 2131624031(0x7f0e005f, float:1.887523E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624032(0x7f0e0060, float:1.8875232E38)
            android.view.View r4 = r2.inflate(r11, r13)
            r11 = 6
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427483, 2131427468, 2131427485, 2131427493, 2131427491} // fill-array
            r7 = r13
            int[] r11 = new int[r11]
            r11 = {2131427488, 2131427472, 2131427473, 2131427467, 2131427465, 2131427470} // fill-array
            r8 = r11
            goto L_0x0545
        L_0x0479:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.ksw_m_b_Focus_image_zoom_evo
            r13 = 2131231161(0x7f0801b9, float:1.8078395E38)
            if (r11 == 0) goto L_0x04c2
            android.widget.ImageView r11 = r0.mIvMainBk
            if (r11 == 0) goto L_0x048b
            android.widget.ImageView r11 = r0.mIvMainBk
            r11.setBackgroundResource(r13)
        L_0x048b:
            r11 = 2131624036(0x7f0e0064, float:1.887524E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624037(0x7f0e0065, float:1.8875242E38)
            android.view.View r4 = r2.inflate(r11, r13)
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x04a7
            r11 = 2131624038(0x7f0e0066, float:1.8875244E38)
            android.view.View r4 = r2.inflate(r11, r13)
        L_0x04a7:
            r11 = 6
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427468, 2131427483, 2131427473, 2131427485, 2131427467} // fill-array
            r7 = r13
            int[] r13 = new int[r11]
            r13 = {2131427493, 2131427472, 2131427491, 2131427470, 2131427488, 2131427465} // fill-array
            r8 = r13
            com.android.launcher3.Launcher r13 = r0.mLauncher
            boolean r13 = r13.bHaveTv
            if (r13 == 0) goto L_0x0545
            int[] r11 = new int[r11]
            r11 = {2131427493, 2131427492, 2131427491, 2131427470, 2131427488, 2131427465} // fill-array
            r8 = r11
            goto L_0x0545
        L_0x04c2:
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_i_ksw_evo_main_interface_index
            if (r11 != r14) goto L_0x0507
            android.widget.ImageView r11 = r0.mIvMainBk
            if (r11 == 0) goto L_0x04d1
            android.widget.ImageView r11 = r0.mIvMainBk
            r11.setBackgroundResource(r13)
        L_0x04d1:
            r11 = 2131624002(0x7f0e0042, float:1.8875171E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624004(0x7f0e0044, float:1.8875175E38)
            android.view.View r4 = r2.inflate(r11, r13)
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x04ed
            r11 = 2131624003(0x7f0e0043, float:1.8875173E38)
            android.view.View r3 = r2.inflate(r11, r13)
        L_0x04ed:
            r11 = 6
            int[] r13 = new int[r11]
            r13 = {2131427493, 2131427472, 2131427491, 2131427470, 2131427488, 2131427465} // fill-array
            r7 = r13
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427468, 2131427483, 2131427473, 2131427485, 2131427467} // fill-array
            r8 = r13
            com.android.launcher3.Launcher r13 = r0.mLauncher
            boolean r13 = r13.bHaveTv
            if (r13 == 0) goto L_0x0545
            int[] r11 = new int[r11]
            r11 = {2131427493, 2131427492, 2131427491, 2131427470, 2131427488, 2131427465} // fill-array
            r7 = r11
            goto L_0x0545
        L_0x0507:
            android.widget.ImageView r11 = r0.mIvMainBk
            if (r11 == 0) goto L_0x0510
            android.widget.ImageView r11 = r0.mIvMainBk
            r11.setBackgroundResource(r13)
        L_0x0510:
            r11 = 2131624033(0x7f0e0061, float:1.8875234E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131624034(0x7f0e0062, float:1.8875236E38)
            android.view.View r4 = r2.inflate(r11, r13)
            com.android.launcher3.Launcher r11 = r0.mLauncher
            boolean r11 = r11.bHaveTv
            if (r11 == 0) goto L_0x052c
            r11 = 2131624035(0x7f0e0063, float:1.8875238E38)
            android.view.View r4 = r2.inflate(r11, r13)
        L_0x052c:
            r11 = 6
            int[] r13 = new int[r11]
            r13 = {2131427479, 2131427468, 2131427483, 2131427473, 2131427485, 2131427467} // fill-array
            r7 = r13
            int[] r13 = new int[r11]
            r13 = {2131427493, 2131427472, 2131427491, 2131427470, 2131427488, 2131427465} // fill-array
            r8 = r13
            com.android.launcher3.Launcher r13 = r0.mLauncher
            boolean r13 = r13.bHaveTv
            if (r13 == 0) goto L_0x0545
            int[] r11 = new int[r11]
            r11 = {2131427493, 2131427492, 2131427491, 2131427470, 2131427488, 2131427465} // fill-array
            r8 = r11
        L_0x0545:
            r11 = 12
            int[] r13 = new int[r11]
            r13 = {2131427348, 2131427349, 2131427353, 2131427354, 2131427355, 2131427356, 2131427357, 2131427358, 2131427359, 2131427360, 2131427350, 2131427351} // fill-array
            r0.imgFocusList = r13
            int[] r11 = new int[r11]
            r11 = {2131427361, 2131427362, 2131427365, 2131427366, 2131427367, 2131427368, 2131427369, 2131427370, 2131427371, 2131427372, 2131427363, 2131427364} // fill-array
            r0.imgFocusList_modeIcon = r11
            goto L_0x0599
        L_0x0556:
            r12 = 0
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iUITypeVer
            r13 = 101(0x65, float:1.42E-43)
            if (r11 != r13) goto L_0x0599
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iUiIndex
            r13 = 2
            if (r11 != r13) goto L_0x0576
            r11 = 2131623981(0x7f0e002d, float:1.8875129E38)
            r13 = 0
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131623982(0x7f0e002e, float:1.887513E38)
            android.view.View r4 = r2.inflate(r11, r13)
            goto L_0x058c
        L_0x0576:
            r13 = 0
            com.android.launcher3.Launcher r11 = r0.mLauncher
            int r11 = r11.m_iUiIndex
            r15 = 3
            if (r11 != r15) goto L_0x058c
            r11 = 2131623983(0x7f0e002f, float:1.8875133E38)
            android.view.View r3 = r2.inflate(r11, r13)
            r11 = 2131623984(0x7f0e0030, float:1.8875135E38)
            android.view.View r4 = r2.inflate(r11, r13)
        L_0x058c:
            int[] r11 = new int[r14]
            r11 = {2131427483, 2131427468, 2131427479, 2131427493, 2131427491, 2131427489, 2131427466, 2131427467} // fill-array
            r7 = r11
            r11 = 5
            int[] r11 = new int[r11]
            r11 = {2131427469, 2131427473, 2131427488, 2131427465, 2131427472} // fill-array
            r8 = r11
        L_0x0599:
            if (r7 == 0) goto L_0x05b0
            int r11 = r7.length
            if (r11 <= 0) goto L_0x05b0
            r11 = 0
        L_0x059f:
            int r13 = r7.length
            if (r11 >= r13) goto L_0x05b0
            r13 = r7[r11]
            android.view.View r13 = r3.findViewById(r13)
            if (r13 == 0) goto L_0x05ad
            r13.setOnClickListener(r0)
        L_0x05ad:
            int r11 = r11 + 1
            goto L_0x059f
        L_0x05b0:
            if (r8 == 0) goto L_0x05c7
            int r11 = r8.length
            if (r11 <= 0) goto L_0x05c7
            r11 = 0
        L_0x05b6:
            int r13 = r8.length
            if (r11 >= r13) goto L_0x05c7
            r13 = r8[r11]
            android.view.View r13 = r4.findViewById(r13)
            if (r13 == 0) goto L_0x05c4
            r13.setOnClickListener(r0)
        L_0x05c4:
            int r11 = r11 + 1
            goto L_0x05b6
        L_0x05c7:
            if (r9 == 0) goto L_0x05de
            int r11 = r9.length
            if (r11 <= 0) goto L_0x05de
            r11 = 0
        L_0x05cd:
            int r13 = r9.length
            if (r11 >= r13) goto L_0x05de
            r13 = r9[r11]
            android.view.View r13 = r5.findViewById(r13)
            if (r13 == 0) goto L_0x05db
            r13.setOnClickListener(r0)
        L_0x05db:
            int r11 = r11 + 1
            goto L_0x05cd
        L_0x05de:
            if (r10 == 0) goto L_0x05f5
            int r11 = r10.length
            if (r11 <= 0) goto L_0x05f5
            r11 = 0
        L_0x05e4:
            int r13 = r10.length
            if (r11 >= r13) goto L_0x05f5
            r13 = r10[r11]
            android.view.View r13 = r6.findViewById(r13)
            if (r13 == 0) goto L_0x05f2
            r13.setOnClickListener(r0)
        L_0x05f2:
            int r11 = r11 + 1
            goto L_0x05e4
        L_0x05f5:
            int[] r11 = r0.imgFocusList
            if (r11 == 0) goto L_0x065e
            int[] r11 = r0.imgFocusList
            int r11 = r11.length
            android.widget.ImageView[] r11 = new android.widget.ImageView[r11]
            r0.imageViewFocusList_whats1 = r11
            int[] r11 = r0.imgFocusList
            int r11 = r11.length
            android.widget.ImageView[] r11 = new android.widget.ImageView[r11]
            r0.imageViewFocusList_whats2 = r11
            int[] r11 = r0.imgFocusList
            int r11 = r11.length
            android.widget.ImageView[] r11 = new android.widget.ImageView[r11]
            r0.imageViewFocusList_whats3 = r11
            int[] r11 = r0.imgFocusList
            int r11 = r11.length
            android.widget.ImageView[] r11 = new android.widget.ImageView[r11]
            r0.imageViewFocusList_whats4 = r11
            r11 = 0
        L_0x0616:
            int[] r13 = r0.imgFocusList
            int r13 = r13.length
            if (r11 >= r13) goto L_0x065e
            if (r3 == 0) goto L_0x062b
            android.widget.ImageView[] r13 = r0.imageViewFocusList_whats1
            int[] r14 = r0.imgFocusList
            r14 = r14[r11]
            android.view.View r14 = r3.findViewById(r14)
            android.widget.ImageView r14 = (android.widget.ImageView) r14
            r13[r11] = r14
        L_0x062b:
            if (r4 == 0) goto L_0x063b
            android.widget.ImageView[] r13 = r0.imageViewFocusList_whats2
            int[] r14 = r0.imgFocusList
            r14 = r14[r11]
            android.view.View r14 = r4.findViewById(r14)
            android.widget.ImageView r14 = (android.widget.ImageView) r14
            r13[r11] = r14
        L_0x063b:
            if (r5 == 0) goto L_0x064b
            android.widget.ImageView[] r13 = r0.imageViewFocusList_whats3
            int[] r14 = r0.imgFocusList
            r14 = r14[r11]
            android.view.View r14 = r5.findViewById(r14)
            android.widget.ImageView r14 = (android.widget.ImageView) r14
            r13[r11] = r14
        L_0x064b:
            if (r6 == 0) goto L_0x065b
            android.widget.ImageView[] r13 = r0.imageViewFocusList_whats4
            int[] r14 = r0.imgFocusList
            r14 = r14[r11]
            android.view.View r14 = r6.findViewById(r14)
            android.widget.ImageView r14 = (android.widget.ImageView) r14
            r13[r11] = r14
        L_0x065b:
            int r11 = r11 + 1
            goto L_0x0616
        L_0x065e:
            int[] r11 = r0.imgFocusList_modeIcon
            if (r11 == 0) goto L_0x069a
            int[] r11 = r0.imgFocusList_modeIcon
            int r11 = r11.length
            android.widget.ImageView[] r11 = new android.widget.ImageView[r11]
            r0.imageViewFocusList_modeIcon_whats1 = r11
            int[] r11 = r0.imgFocusList_modeIcon
            int r11 = r11.length
            android.widget.ImageView[] r11 = new android.widget.ImageView[r11]
            r0.imageViewFocusList_modeIcon_whats2 = r11
        L_0x0671:
            r11 = r12
            int[] r12 = r0.imgFocusList_modeIcon
            int r12 = r12.length
            if (r11 >= r12) goto L_0x069a
            if (r3 == 0) goto L_0x0687
            android.widget.ImageView[] r12 = r0.imageViewFocusList_modeIcon_whats1
            int[] r13 = r0.imgFocusList_modeIcon
            r13 = r13[r11]
            android.view.View r13 = r3.findViewById(r13)
            android.widget.ImageView r13 = (android.widget.ImageView) r13
            r12[r11] = r13
        L_0x0687:
            if (r4 == 0) goto L_0x0697
            android.widget.ImageView[] r12 = r0.imageViewFocusList_modeIcon_whats2
            int[] r13 = r0.imgFocusList_modeIcon
            r13 = r13[r11]
            android.view.View r13 = r4.findViewById(r13)
            android.widget.ImageView r13 = (android.widget.ImageView) r13
            r12[r11] = r13
        L_0x0697:
            int r12 = r11 + 1
            goto L_0x0671
        L_0x069a:
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            if (r3 == 0) goto L_0x06a4
            r11.add(r3)
        L_0x06a4:
            if (r4 == 0) goto L_0x06a9
            r11.add(r4)
        L_0x06a9:
            if (r5 == 0) goto L_0x06ae
            r11.add(r5)
        L_0x06ae:
            if (r6 == 0) goto L_0x06b3
            r11.add(r6)
        L_0x06b3:
            com.android.launcher3.fragment.MainFragment$5 r12 = new com.android.launcher3.fragment.MainFragment$5
            r12.<init>(r11)
            android.support.v4.view.ViewPager r13 = r0.mViewPager
            if (r13 == 0) goto L_0x06cb
            android.support.v4.view.ViewPager r13 = r0.mViewPager
            r13.setAdapter(r12)
            android.support.v4.view.ViewPager r13 = r0.mViewPager
            com.android.launcher3.fragment.MainFragment$MyOnPageChangeListener r14 = new com.android.launcher3.fragment.MainFragment$MyOnPageChangeListener
            r14.<init>()
            r13.setOnPageChangeListener(r14)
        L_0x06cb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.fragment.MainFragment.initViewPager(android.view.View):void");
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public MyOnPageChangeListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            int i = position;
            switch (i) {
                case 0:
                    if (MainFragment.this.mLauncher.m_iUITypeVer != 41) {
                        if (MainFragment.this.mLauncher.m_iUITypeVer == 101) {
                            if (MainFragment.this.mLauncher.m_iUiIndex != 2) {
                                if (MainFragment.this.mLauncher.m_iUiIndex == 3) {
                                    MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_d));
                                    MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_n));
                                    break;
                                }
                            } else {
                                MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_d));
                                MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_n));
                                break;
                            }
                        }
                    } else if (MainFragment.this.mLauncher.m_iModeSet != 7) {
                        if (MainFragment.this.mLauncher.m_iModeSet != 15) {
                            if (MainFragment.this.mLauncher.m_iModeSet == 16) {
                                MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_d));
                                MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                MainFragment.this.mPage2.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                break;
                            }
                        } else {
                            MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_d));
                            MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                            MainFragment.this.mPage2.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                            break;
                        }
                    }
                    break;
                case 1:
                    if (MainFragment.this.mLauncher.m_iUITypeVer != 41) {
                        if (MainFragment.this.mLauncher.m_iUITypeVer == 101) {
                            if (MainFragment.this.mLauncher.m_iUiIndex != 2) {
                                if (MainFragment.this.mLauncher.m_iUiIndex == 3) {
                                    MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_n));
                                    MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_d));
                                    break;
                                }
                            } else {
                                MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_n));
                                MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_d));
                                break;
                            }
                        }
                    } else if (MainFragment.this.mLauncher.m_iModeSet != 7) {
                        if (MainFragment.this.mLauncher.m_iModeSet != 15) {
                            if (MainFragment.this.mLauncher.m_iModeSet == 16) {
                                MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_d));
                                MainFragment.this.mPage2.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                break;
                            }
                        } else {
                            MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                            MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_d));
                            MainFragment.this.mPage2.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                            break;
                        }
                    }
                    break;
                case 2:
                    if (MainFragment.this.mLauncher.m_iUITypeVer == 41) {
                        if (MainFragment.this.mLauncher.m_iModeSet != 15) {
                            if (MainFragment.this.mLauncher.m_iModeSet == 16) {
                                MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                MainFragment.this.mPage2.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_d));
                                break;
                            }
                        } else {
                            MainFragment.this.mPage0.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                            MainFragment.this.mPage1.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                            MainFragment.this.mPage2.setImageDrawable(MainFragment.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_d));
                            break;
                        }
                    }
                    break;
            }
            Log.i(MainFragment.TAG, "onPageSelected: iLastMainPageIndex = " + MainFragment.this.iLastMainPageIndex + ", position = " + i);
            if (MainFragment.this.iLastMainPageIndex > i) {
                int unused = MainFragment.this.iMainFocusIndex = (MainFragment.this.iPagerItemViewItemCount * MainFragment.this.iLastMainPageIndex) - 1;
            } else {
                int unused2 = MainFragment.this.iMainFocusIndex = MainFragment.this.iPagerItemViewItemCount * i;
            }
            MainFragment.this.refreshMainFocusView();
            int unused3 = MainFragment.this.iLastMainPageIndex = MainFragment.this.iMainPageIndex = i;
            int unused4 = MainFragment.this.iLastMainFocusIndex = MainFragment.this.iLastMainPageIndex * 2;
            MainFragment.this.setPagerPrivNextStutes();
        }

        public void onPageScrollStateChanged(int state) {
        }
    }

    public void setMainFocusMove(int iExtra) {
        int i = iExtra;
        Log.i(TAG, "setMainFocusMove: iExtra = " + i);
        if (this.mLauncher.m_iModeSet == 1 || this.mLauncher.m_iModeSet == 3) {
            this.isAuto = true;
            this.isJup_Add = false;
            this.isJup_Sub = false;
            if (ksw_3d_currentPosition != this.mLoopRotarySwitchView.getSelectItem()) {
                this.isAuto = false;
                float angle = this.mLoopRotarySwitchView.getAngle();
                double lastPositionRadians = ((double) (((180.0f + angle) - ((float) ((ksw_3d_lastPosition * 360) / this.ksw_3d_views.size()))) % 360.0f)) - ((double) (360 / this.ksw_3d_views.size()));
                if (lastPositionRadians < 0.0d) {
                    lastPositionRadians += 360.0d;
                }
                Log.i("TAG", "onClick- lastPositionRadians=" + lastPositionRadians + ",lastPosition = " + ksw_3d_lastPosition + ",angle = " + angle);
                if (lastPositionRadians == 324.0d) {
                    this.isJup_Add = true;
                }
                if (lastPositionRadians == 180.0d) {
                    this.isJup_Sub = true;
                }
            } else {
                this.isAuto = true;
                this.isJup_Add = false;
                this.isJup_Sub = false;
            }
        }
        switch (i) {
            case 1:
                if ((this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) && !this.bInLeftFocus) {
                    this.iLeftFocusIndex = 0;
                    this.bInLeftFocus = true;
                    this.iLastMainFocusIndex = this.iLastMainPageIndex * 2;
                    this.iMainFocusIndex = -1;
                    break;
                }
            case 2:
                if ((this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) && this.bInLeftFocus) {
                    this.bInLeftFocus = false;
                    this.iLeftFocusIndex = -1;
                    this.iMainFocusIndex = this.iLastMainFocusIndex;
                    break;
                }
            case 5:
                if (this.mLauncher.m_iModeSet != 1 && this.mLauncher.m_iModeSet != 3) {
                    if (this.mLauncher.m_iModeSet != 5) {
                        if (this.mLauncher.m_iModeSet != 10 && this.mLauncher.m_iModeSet != 12) {
                            if (!this.bInLeftFocus) {
                                enterMainFocusView();
                                break;
                            } else {
                                enterLeftFocusView();
                                break;
                            }
                        } else if (this.mCoverFlowView != null) {
                            if (!this.mCoverFlowView.isOnTouch()) {
                                if (!this.mCoverFlowView.isbScroll()) {
                                    this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + 667), (float) (this.mCoverFlowView.getTop() - 20), 0));
                                    this.mCoverFlowView.setbScroll(true);
                                    this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (this.mCoverFlowView.getLeft() + 667), (float) (this.mCoverFlowView.getTop() - 20), 0));
                                    this.mCoverFlowView.setbScroll(false);
                                    break;
                                }
                            } else {
                                return;
                            }
                        }
                    } else if (this.mCoverFlowView != null) {
                        if (!this.mCoverFlowView.isOnTouch()) {
                            if (!this.mCoverFlowView.isbScroll()) {
                                this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + 500), (float) (this.mCoverFlowView.getTop() - 200), 0));
                                this.mCoverFlowView.setbScroll(true);
                                this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (this.mCoverFlowView.getLeft() + 500), (float) (this.mCoverFlowView.getTop() - 200), 0));
                                this.mCoverFlowView.setbScroll(false);
                                break;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    this.mLoopRotarySwitchView.getOnItemClickListener().onItemClick(ksw_3d_currentPosition, this.ksw_3d_views.get(ksw_3d_currentPosition));
                    break;
                }
                break;
            case 7:
                if (this.mLauncher.m_iModeSet != 1 && this.mLauncher.m_iModeSet != 3) {
                    if (this.mLauncher.m_iModeSet != 5 && this.mLauncher.m_iModeSet != 10 && this.mLauncher.m_iModeSet != 12) {
                        if (!this.bInLeftFocus) {
                            this.iMainFocusIndex--;
                            if (this.iMainFocusIndex < 0) {
                                this.iMainFocusIndex = 0;
                                break;
                            }
                        } else {
                            this.iLeftFocusIndex--;
                            if (this.iLeftFocusIndex < 0) {
                                this.iLeftFocusIndex = 0;
                                break;
                            }
                        }
                    } else if (this.mCoverFlowView != null) {
                        if (!this.mCoverFlowView.isOnTouch()) {
                            if (!this.mCoverFlowView.isbScroll()) {
                                this.fFlowViewRight = 640.0f;
                                this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, this.fFlowViewRight + ((float) this.mCoverFlowView.getLeft()), (float) (this.mCoverFlowView.getTop() + 125), 0));
                                this.mCoverFlowView.setbScroll(true);
                                this.mLauncher.getMainHandler().removeMessages(3);
                                this.mLauncher.getMainHandler().sendEmptyMessageDelayed(3, 100);
                                break;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    if (this.isJup_Sub) {
                        ksw_3d_currentPosition -= 4;
                    } else {
                        ksw_3d_currentPosition--;
                    }
                    if (ksw_3d_currentPosition < 0) {
                        ksw_3d_currentPosition += this.ksw_3d_views.size();
                    }
                    add_sub(this.isAuto);
                    break;
                }
                break;
            case 8:
                if (this.mLauncher.m_iModeSet != 1 && this.mLauncher.m_iModeSet != 3) {
                    if (this.mLauncher.m_iModeSet != 5 && this.mLauncher.m_iModeSet != 10 && this.mLauncher.m_iModeSet != 12) {
                        if (!this.bInLeftFocus) {
                            this.iMainFocusIndex++;
                            if (this.iMainFocusIndex >= this.imgFocusList.length) {
                                this.iMainFocusIndex = this.imgFocusList.length - 1;
                                break;
                            }
                        } else {
                            this.iLeftFocusIndex++;
                            if (this.iLeftFocusIndex >= this.imageViewLeftFocusList.length) {
                                this.iLeftFocusIndex = this.imageViewLeftFocusList.length - 1;
                                break;
                            }
                        }
                    } else if (this.mCoverFlowView != null) {
                        if (!this.mCoverFlowView.isOnTouch()) {
                            if (!this.mCoverFlowView.isbScroll()) {
                                this.fFlowViewLeft = 640.0f;
                                this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, ((float) this.mCoverFlowView.getLeft()) + this.fFlowViewLeft, (float) (this.mCoverFlowView.getTop() + 125), 0));
                                this.mCoverFlowView.setbScroll(true);
                                this.mLauncher.getMainHandler().removeMessages(2);
                                this.mLauncher.getMainHandler().sendEmptyMessageDelayed(2, 100);
                                break;
                            }
                        } else {
                            return;
                        }
                    }
                } else {
                    if (this.isJup_Add) {
                        ksw_3d_currentPosition += 4;
                    } else {
                        ksw_3d_currentPosition++;
                    }
                    if (ksw_3d_currentPosition >= this.ksw_3d_views.size()) {
                        ksw_3d_currentPosition %= this.ksw_3d_views.size();
                    }
                    add_sub(this.isAuto);
                    break;
                }
                break;
        }
        refreshLeftFocusView();
        refreshMainFocusView();
    }

    private void enterMainFocusView() {
        if (this.mLauncher.m_iModeSet == 0) {
            enterMainFocusView_NBT();
        } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
            enterMainFocusView_EVO();
        } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
            enterMainFocusView_ID6();
        } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
            enterMainFocusView_ID7();
        }
    }

    private void enterMainFocusView_NBT() {
        switch (this.iMainFocusIndex) {
            case 0:
                this.mLauncher.showApps(true);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 3:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 4:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case 5:
                EventUtils.onEnterPhoneLink(this.mLauncher);
                return;
            case 6:
                EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                return;
            case 7:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 8:
                EventUtils.onEnterAux(this.mLauncher, this.mLauncher.bHaveAux);
                return;
            case 9:
                EventUtils.onEnterTv(this.mLauncher, this.mLauncher.bHaveTv);
                return;
            case 10:
                EventUtils.onEnterDvd(this.mLauncher, this.mLauncher.iHaveDvdType);
                return;
            default:
                return;
        }
    }

    private void enterMainFocusView_EVO() {
        switch (this.iMainFocusIndex) {
            case 0:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                }
            case 1:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    if (this.mLauncher.bHaveTv) {
                        EventUtils.onEnterTv(this.mLauncher, this.mLauncher.bHaveTv);
                        return;
                    } else {
                        EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                        return;
                    }
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                }
            case 2:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                }
            case 3:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                }
            case 4:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.onEnterPhoneLink(this.mLauncher);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                } else {
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                }
            case 5:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    this.mLauncher.showApps(true);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    return;
                }
            case 6:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.onEnterPhoneLink(this.mLauncher);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                }
            case 7:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                    return;
                } else if (this.mLauncher.bHaveTv) {
                    EventUtils.onEnterTv(this.mLauncher, this.mLauncher.bHaveTv);
                    return;
                } else {
                    EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                    return;
                }
            case 8:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                }
            case 9:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    return;
                } else {
                    EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                    return;
                }
            case 10:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    this.mLauncher.showApps(true);
                    return;
                } else {
                    EventUtils.onEnterPhoneLink(this.mLauncher);
                    return;
                }
            case 11:
                if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                    EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                    return;
                } else {
                    this.mLauncher.showApps(true);
                    return;
                }
            default:
                return;
        }
    }

    private void enterMainFocusView_ID6() {
        switch (this.iMainFocusIndex) {
            case 0:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                }
            case 1:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                }
            case 2:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                }
            case 3:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                }
            case 4:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    this.mLauncher.showApps(true);
                    return;
                } else {
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                }
            case 5:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    return;
                }
            case 6:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    return;
                } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                }
            case 7:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    return;
                } else if (this.mLauncher.bHaveTv) {
                    EventUtils.onEnterTv(this.mLauncher, this.mLauncher.bHaveTv);
                    return;
                } else {
                    EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                    return;
                }
            case 8:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                } else {
                    EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                    return;
                }
            case 9:
                if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.onEnterPhoneLink(this.mLauncher);
                    return;
                }
            case 10:
                this.mLauncher.showApps(true);
                return;
            case 11:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            default:
                return;
        }
    }

    private void enterMainFocusView_ID7() {
        switch (this.iMainFocusIndex) {
            case 0:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 3:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 4:
                this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            case 5:
                EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void refreshMainFocusView() {
        if (this.mLauncher.m_iUITypeVer != 41) {
            return;
        }
        if (this.mLauncher.m_iModeSet == 0) {
            refreshMainFocusView_NBT();
        } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
            refreshMainFocusView_EV0();
        } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
            refreshMainFocusView_ID6();
        } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
            refreshMainFocusView_ID7();
        }
    }

    private void refreshMainFocusView_NBT() {
        if (this.iMainFocusIndex != -1) {
            if (!(this.mIvItemTypeBg == null || this.imgItemTypeBgList == null || this.imgItemTypeBgList.length <= 0)) {
                this.mIvItemTypeBg.setBackgroundResource(this.imgItemTypeBgList[this.iMainFocusIndex]);
            }
            if (!(this.mIvItemTypeKnob == null || this.imgItemTypeKnobList == null || this.imgItemTypeKnobList.length <= 0)) {
                this.mIvItemTypeKnob.setBackgroundResource(this.imgItemTypeKnobList[this.iMainFocusIndex]);
            }
            if (!(this.mIvItemTypeIcon == null || this.imgItemTypeIconList == null || this.imgItemTypeIconList.length <= 0)) {
                this.mIvItemTypeIcon.setBackgroundResource(this.imgItemTypeIconList[this.iMainFocusIndex]);
            }
        }
        if (this.imgLeftFocusList != null && this.imgLeftFocusList.length > 0) {
            for (int i = 0; i < this.imgLeftFocusList.length; i++) {
                if (this.iMainFocusIndex == i) {
                    if (this.imageViewLeftFocusList[i] != null) {
                        this.imageViewLeftFocusList[i].setVisibility(0);
                    }
                } else if (this.imageViewLeftFocusList[i] != null) {
                    this.imageViewLeftFocusList[i].setVisibility(8);
                }
            }
        }
        if (this.iMainFocusIndex < 8) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(0);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i2 = 0; i2 < this.imgFocusList.length; i2++) {
                    if (this.imageViewFocusList_whats2[i2] != null) {
                        this.imageViewFocusList_whats2[i2].setVisibility(8);
                    }
                    if (this.iMainFocusIndex == i2) {
                        if (this.imageViewFocusList_whats1[i2] != null) {
                            this.imageViewFocusList_whats1[i2].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats1[i2] != null) {
                        this.imageViewFocusList_whats1[i2].setVisibility(8);
                    }
                }
            }
        } else if (this.iMainFocusIndex < 11) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i3 = 0; i3 < this.imgFocusList.length; i3++) {
                    if (this.imageViewFocusList_whats1[i3] != null) {
                        this.imageViewFocusList_whats1[i3].setVisibility(8);
                    }
                    if (this.iMainFocusIndex == i3) {
                        if (this.imageViewFocusList_whats2[i3] != null) {
                            this.imageViewFocusList_whats2[i3].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats2[i3] != null) {
                        this.imageViewFocusList_whats2[i3].setVisibility(8);
                    }
                }
            }
        }
    }

    private void refreshMainFocusView_EV0() {
        if (this.iMainFocusIndex < 6) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(0);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i = 0; i < this.imgFocusList.length; i++) {
                    if (this.imageViewFocusList_whats2[i] != null) {
                        this.imageViewFocusList_whats2[i].setVisibility(8);
                    }
                    if (this.imageViewFocusList_modeIcon_whats2[i] != null) {
                        this.imageViewFocusList_modeIcon_whats2[i].setVisibility(8);
                    }
                    if (this.iMainFocusIndex == i) {
                        if (this.imageViewFocusList_whats1[i] != null) {
                            this.imageViewFocusList_whats1[i].setVisibility(0);
                        }
                        if (this.imageViewFocusList_modeIcon_whats1[i] != null) {
                            this.imageViewFocusList_modeIcon_whats1[i].setVisibility(0);
                        }
                    } else {
                        if (this.imageViewFocusList_whats1[i] != null) {
                            this.imageViewFocusList_whats1[i].setVisibility(8);
                        }
                        if (this.imageViewFocusList_modeIcon_whats1[i] != null) {
                            this.imageViewFocusList_modeIcon_whats1[i].setVisibility(8);
                        }
                    }
                }
            }
        } else if (this.iMainFocusIndex < 12) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i2 = 0; i2 < this.imgFocusList.length; i2++) {
                    if (this.imageViewFocusList_whats1[i2] != null) {
                        this.imageViewFocusList_whats1[i2].setVisibility(8);
                    }
                    if (this.imageViewFocusList_modeIcon_whats1[i2] != null) {
                        this.imageViewFocusList_modeIcon_whats1[i2].setVisibility(8);
                    }
                    if (this.iMainFocusIndex == i2) {
                        if (this.imageViewFocusList_whats2[i2] != null) {
                            this.imageViewFocusList_whats2[i2].setVisibility(0);
                        }
                        if (this.imageViewFocusList_modeIcon_whats2[i2] != null) {
                            this.imageViewFocusList_modeIcon_whats2[i2].setVisibility(0);
                        }
                    } else {
                        if (this.imageViewFocusList_whats2[i2] != null) {
                            this.imageViewFocusList_whats2[i2].setVisibility(8);
                        }
                        if (this.imageViewFocusList_modeIcon_whats2[i2] != null) {
                            this.imageViewFocusList_modeIcon_whats2[i2].setVisibility(8);
                        }
                    }
                }
            }
        }
    }

    private void refreshMainFocusView_ID6() {
        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
            if (this.iMainFocusIndex < 4) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(0);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i = 0; i < this.imgFocusList.length; i++) {
                        if (this.iMainFocusIndex == i) {
                            if (this.imageViewFocusList_whats1[i] != null) {
                                this.imageViewFocusList_whats1[i].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats1[i] != null) {
                            this.imageViewFocusList_whats1[i].setVisibility(8);
                        }
                    }
                }
            } else if (this.iMainFocusIndex < 8) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(1);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i2 = 0; i2 < this.imgFocusList.length; i2++) {
                        if (this.iMainFocusIndex == i2) {
                            if (this.imageViewFocusList_whats2[i2] != null) {
                                this.imageViewFocusList_whats2[i2].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats2[i2] != null) {
                            this.imageViewFocusList_whats2[i2].setVisibility(8);
                        }
                    }
                }
            } else if (this.iMainFocusIndex < 12) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(2);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i3 = 0; i3 < this.imgFocusList.length; i3++) {
                        if (this.iMainFocusIndex == i3) {
                            if (this.imageViewFocusList_whats3[i3] != null) {
                                this.imageViewFocusList_whats3[i3].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats3[i3] != null) {
                            this.imageViewFocusList_whats3[i3].setVisibility(8);
                        }
                    }
                }
            }
        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
            if (this.iMainFocusIndex < 5) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(0);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i4 = 0; i4 < this.imgFocusList.length; i4++) {
                        if (this.imageViewFocusList_whats2[i4] != null) {
                            this.imageViewFocusList_whats2[i4].setVisibility(8);
                        }
                        if (this.iMainFocusIndex == i4) {
                            if (this.imageViewFocusList_whats1[i4] != null) {
                                this.imageViewFocusList_whats1[i4].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats1[i4] != null) {
                            this.imageViewFocusList_whats1[i4].setVisibility(8);
                        }
                    }
                }
            } else if (this.iMainFocusIndex < 10) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(1);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i5 = 0; i5 < this.imgFocusList.length; i5++) {
                        if (this.imageViewFocusList_whats1[i5] != null) {
                            this.imageViewFocusList_whats1[i5].setVisibility(8);
                        }
                        if (this.iMainFocusIndex == i5) {
                            if (this.imageViewFocusList_whats2[i5] != null) {
                                this.imageViewFocusList_whats2[i5].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats2[i5] != null) {
                            this.imageViewFocusList_whats2[i5].setVisibility(8);
                        }
                    }
                }
            }
        } else if (this.iMainFocusIndex < 3) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(0);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i6 = 0; i6 < this.imgFocusList.length; i6++) {
                    if (this.iMainFocusIndex == i6) {
                        if (this.imageViewFocusList_whats1[i6] != null) {
                            this.imageViewFocusList_whats1[i6].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats1[i6] != null) {
                        this.imageViewFocusList_whats1[i6].setVisibility(8);
                    }
                }
            }
        } else if (this.iMainFocusIndex < 6) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i7 = 0; i7 < this.imgFocusList.length; i7++) {
                    if (this.iMainFocusIndex == i7) {
                        if (this.imageViewFocusList_whats2[i7] != null) {
                            this.imageViewFocusList_whats2[i7].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats2[i7] != null) {
                        this.imageViewFocusList_whats2[i7].setVisibility(8);
                    }
                }
            }
        } else if (this.iMainFocusIndex < 9) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(2);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i8 = 0; i8 < this.imgFocusList.length; i8++) {
                    if (this.iMainFocusIndex == i8) {
                        if (this.imageViewFocusList_whats3[i8] != null) {
                            this.imageViewFocusList_whats3[i8].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats3[i8] != null) {
                        this.imageViewFocusList_whats3[i8].setVisibility(8);
                    }
                }
            }
        } else if (this.iMainFocusIndex < 12) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(3);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i9 = 0; i9 < this.imgFocusList.length; i9++) {
                    if (this.iMainFocusIndex == i9) {
                        if (this.imageViewFocusList_whats4[i9] != null) {
                            this.imageViewFocusList_whats4[i9].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats4[i9] != null) {
                        this.imageViewFocusList_whats4[i9].setVisibility(8);
                    }
                }
            }
        }
    }

    private void refreshMainFocusView_ID7() {
        int i = 0;
        if (this.bInLeftFocus) {
            while (true) {
                int i2 = i;
                if (i2 < this.imgFocusList.length) {
                    if (this.imageViewFocusList_whats1[i2] != null) {
                        this.imageViewFocusList_whats1[i2].setVisibility(8);
                    }
                    if (this.imageViewFocusList_whats2[i2] != null) {
                        this.imageViewFocusList_whats2[i2].setVisibility(8);
                    }
                    if (this.imageViewFocusList_whats3[i2] != null) {
                        this.imageViewFocusList_whats3[i2].setVisibility(8);
                    }
                    i = i2 + 1;
                } else {
                    return;
                }
            }
        } else if (this.iMainFocusIndex < 2) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(0);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i3 = 0; i3 < this.imgFocusList.length; i3++) {
                    if (this.iMainFocusIndex == i3) {
                        if (this.imageViewFocusList_whats1[i3] != null) {
                            this.imageViewFocusList_whats1[i3].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats1[i3] != null) {
                        this.imageViewFocusList_whats1[i3].setVisibility(8);
                    }
                }
            }
        } else if (this.iMainFocusIndex < 4) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i4 = 0; i4 < this.imgFocusList.length; i4++) {
                    if (this.iMainFocusIndex == i4) {
                        if (this.imageViewFocusList_whats2[i4] != null) {
                            this.imageViewFocusList_whats2[i4].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats2[i4] != null) {
                        this.imageViewFocusList_whats2[i4].setVisibility(8);
                    }
                }
            }
        } else if (this.iMainFocusIndex < 6) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(2);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i5 = 0; i5 < this.imgFocusList.length; i5++) {
                    if (this.iMainFocusIndex == i5) {
                        if (this.imageViewFocusList_whats3[i5] != null) {
                            this.imageViewFocusList_whats3[i5].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats3[i5] != null) {
                        this.imageViewFocusList_whats3[i5].setVisibility(8);
                    }
                }
            }
        }
    }

    private void enterLeftFocusView() {
        switch (this.iLeftFocusIndex) {
            case 0:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 3:
                this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            case 4:
                this.mLauncher.showApps(true);
                return;
            default:
                return;
        }
    }

    private void refreshLeftFocusView() {
        if (this.mLauncher.m_iUITypeVer != 41) {
            return;
        }
        if (this.mLauncher.m_iModeSet != 15 && this.mLauncher.m_iModeSet != 16) {
            return;
        }
        if ((this.bInLeftFocus || this.iLeftFocusIndex == -1) && this.imgLeftFocusList != null && this.imgLeftFocusList.length > 0) {
            for (int i = 0; i < this.imgLeftFocusList.length; i++) {
                if (this.iLeftFocusIndex == i) {
                    if (this.imageViewLeftFocusList[i] != null) {
                        this.imageViewLeftFocusList[i].setVisibility(0);
                    }
                } else if (this.imageViewLeftFocusList[i] != null) {
                    this.imageViewLeftFocusList[i].setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setPagerPrivNextStutes() {
        if (this.mBtnPagerPriv != null && this.mBtnPagerNext != null) {
            Log.i(TAG, "setPagerPrivNextStutes: iMainPageIndex = " + this.iMainPageIndex);
            if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1) {
                if (this.iMainPageIndex == 0) {
                    this.mBtnPagerPriv.setVisibility(8);
                    this.mBtnPagerNext.setVisibility(0);
                } else if (this.iMainPageIndex == 2) {
                    this.mBtnPagerPriv.setVisibility(0);
                    this.mBtnPagerNext.setVisibility(8);
                } else {
                    this.mBtnPagerPriv.setVisibility(0);
                    this.mBtnPagerNext.setVisibility(0);
                }
            } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                if (this.iMainPageIndex == 1) {
                    this.mBtnPagerPriv.setVisibility(0);
                    this.mBtnPagerNext.setVisibility(8);
                    return;
                }
                this.mBtnPagerPriv.setVisibility(8);
                this.mBtnPagerNext.setVisibility(0);
            } else if (this.iMainPageIndex == 0) {
                this.mBtnPagerPriv.setVisibility(8);
                this.mBtnPagerNext.setVisibility(0);
            } else if (this.iMainPageIndex == 3) {
                this.mBtnPagerPriv.setVisibility(0);
                this.mBtnPagerNext.setVisibility(8);
            } else {
                this.mBtnPagerPriv.setVisibility(0);
                this.mBtnPagerNext.setVisibility(0);
            }
        }
    }

    public void setTvCurDataTime(String time) {
        if (this.mTvCurDataTime != null) {
            this.mTvCurDataTime.setText(time);
        }
    }

    public void setTvCurDataTimeYMD(String time) {
        if (this.mTvCurDataTimeYMD != null) {
            this.mTvCurDataTimeYMD.setText(time);
        }
    }

    public void setTvCurDataTimeWeek(String time) {
        if (this.mTvCurDataTimeWeek != null) {
            this.mTvCurDataTimeWeek.setText(time);
        }
    }

    @SuppressLint({"SetTextI18n"})
    public void setQilValue(String value) {
        if (this.mTvQil == null) {
            return;
        }
        if (this.mLauncher.m_iModeSet != 7) {
            this.mTvQil.setText(value);
        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
            TextView textView = this.mTvQil;
            textView.setText(getString(R.string.lb_Oil_quantity) + ": " + value + "L");
        } else {
            this.mTvQil.setText(value);
        }
    }

    public void setDrivingRangeValue(String value) {
        if (this.mTvDrivingRange != null) {
            this.mTvDrivingRange.setText(value);
        }
    }

    public void setTempValue(String value) {
        if (this.mTvTemp != null) {
            this.mTvTemp.setText(value);
        }
    }

    public void setHandbrakeStatus(boolean status) {
        if (this.mTvHandbrake != null) {
            this.mTvHandbrake.setText(getString(status ? R.string.lb_Hand_brake_release : R.string.lb_Hand_brake_pull));
        }
    }

    public void setSeatBeltStatus(boolean status) {
        if (this.mTvSeatBelt != null) {
            this.mTvSeatBelt.setText(getString(status ? R.string.lb_Seat_belt_Yes : R.string.lb_Seat_belt_No));
        }
    }

    public void setSpeedUnit(boolean unit) {
        if (this.mTvSpeedUnit != null) {
            this.mTvSpeedUnit.setText(unit ? "mph" : "km/h");
        }
    }

    public void setSpeed(int iSpeed) {
        if (this.mClockSpeed != null) {
            this.mClockSpeed.setiSpeedValue(iSpeed);
        }
    }

    public void setRotatingSpeed(int iRotatingSpeed) {
        if (this.mClockRotatingSpeed != null) {
            this.mClockRotatingSpeed.setiSpeedValue(iRotatingSpeed);
        }
    }

    public void setBtStatus(int status) {
        if (status >= 3) {
            if (!this.bBtConnected) {
                this.bBtConnected = true;
                if (this.mIvBtStatus == null) {
                    return;
                }
                if (this.mLauncher.m_iModeSet == 0) {
                    this.mIvBtStatus.setBackgroundResource(R.drawable.kesaiwei_1280x480_nbt_bt_connect_d);
                } else if (this.mLauncher.m_iModeSet == 1) {
                    this.mIvBtStatus.setBackgroundResource(R.drawable.kesaiwei_1280x480_audi_bt_connect_d);
                } else if (this.mLauncher.m_iModeSet == 3) {
                    this.mIvBtStatus.setBackgroundResource(R.drawable.kesaiwei_1024x600_audi_bt_connect_d);
                }
            }
        } else if (this.bBtConnected) {
            this.bBtConnected = false;
            if (this.mIvBtStatus == null) {
                return;
            }
            if (this.mLauncher.m_iModeSet == 0) {
                this.mIvBtStatus.setBackgroundResource(R.drawable.kesaiwei_1280x480_nbt_bt_connect_n);
            } else if (this.mLauncher.m_iModeSet == 1) {
                this.mIvBtStatus.setBackgroundResource(R.drawable.kesaiwei_1280x480_audi_bt_connect_n);
            } else if (this.mLauncher.m_iModeSet == 3) {
                this.mIvBtStatus.setBackgroundResource(R.drawable.kesaiwei_1024x600_audi_bt_connect_n);
            }
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void refreshNaviInfo(int iKey_type, int iExtra_type, Bundle bundle) {
        int i = iKey_type;
        int i2 = iExtra_type;
        Bundle bundle2 = bundle;
        if (i == 10001) {
            if (!(this.mNaviAmapautoInfo == null || this.mNaviAmapautoInfo.getVisibility() == 0)) {
                this.mNaviAmapautoInfo.setVisibility(0);
            }
            if (!(this.ksw_m_i_right_show_index == 2 || this.mIvAudiRightLogo == null || this.mIvAudiRightLogo.getVisibility() != 0)) {
                this.mIvAudiRightLogo.setVisibility(8);
            }
            if (bundle2 != null) {
                int type = bundle2.getInt(GuideInfoExtraKey.TYPE);
                Log.i(TAG, "refreshNaviInfo: type = " + type);
                if (type == 1 || type == 0) {
                    String strNextRouadName = bundle2.getString(GuideInfoExtraKey.NEXT_ROAD_NAME);
                    Log.i(TAG, "refreshNaviInfo: strNextRouadName = " + strNextRouadName);
                    int iMapIcon = bundle2.getInt(GuideInfoExtraKey.ICON);
                    Log.i(TAG, "refreshNaviInfo: iMapIcon = " + iMapIcon);
                    int iRouteRemainDis = bundle2.getInt(GuideInfoExtraKey.ROUTE_REMAIN_DIS);
                    Log.i(TAG, "refreshNaviInfo: iRouteRemainDis = " + iRouteRemainDis);
                    int iRouteRemainTime = bundle2.getInt(GuideInfoExtraKey.ROUTE_REMAIN_TIME);
                    Log.i(TAG, "refreshNaviInfo: iRouteRemainTime = " + iRouteRemainTime);
                    int iSegRemainDis = bundle2.getInt(GuideInfoExtraKey.SEG_REMAIN_DIS);
                    Log.i(TAG, "refreshNaviInfo: iSegRemainDis = " + iSegRemainDis);
                    int iCameraSpeed = bundle2.getInt(GuideInfoExtraKey.CAMERA_SPEED);
                    Log.i(TAG, "refreshNaviInfo: iCameraSpeed = " + iCameraSpeed);
                    int iCameraType = bundle2.getInt(GuideInfoExtraKey.CAMERA_TYPE);
                    Log.i(TAG, "refreshNaviInfo: iCameraType = " + iCameraType);
                    if (this.mAmapauto_Icon != null && this.iMapIconList != null && this.iMapIconList.length > 0 && iMapIcon > 0 && iMapIcon <= this.iMapIconList.length) {
                        this.mAmapauto_Icon.setBackgroundResource(this.iMapIconList[iMapIcon - 1]);
                    }
                    if (this.mTvSegRemainDisInfor == null || iSegRemainDis < 0) {
                    } else {
                        DecimalFormat df = new DecimalFormat("0.0");
                        if (iSegRemainDis >= 1000) {
                            TextView textView = this.mTvSegRemainDisInfor;
                            StringBuilder sb = new StringBuilder();
                            int i3 = type;
                            sb.append(df.format(((double) iSegRemainDis) / 1000.0d));
                            sb.append("KM");
                            textView.setText(sb.toString());
                        } else {
                            TextView textView2 = this.mTvSegRemainDisInfor;
                            textView2.setText(iSegRemainDis + "M");
                        }
                    }
                    if (!(this.mTvNextRouadName == null || strNextRouadName == null)) {
                        this.mTvNextRouadName.setText(strNextRouadName);
                    }
                    if (this.mTvRouteRemainDis != null && iRouteRemainDis >= 0) {
                        DecimalFormat df2 = new DecimalFormat("0.0");
                        if (iRouteRemainDis >= 1000) {
                            TextView textView3 = this.mTvRouteRemainDis;
                            textView3.setText(df2.format(((double) iRouteRemainDis) / 1000.0d) + "KM");
                        } else {
                            TextView textView4 = this.mTvRouteRemainDis;
                            textView4.setText(iRouteRemainDis + "M");
                        }
                    }
                    if (this.mTvRouteRemainTime != null) {
                        int iTotMinTime = EventUtils.getMinFromPosition(iRouteRemainTime * 1000);
                        int iTotHourTime = EventUtils.getHourFromPosition(iRouteRemainTime * 1000);
                        this.mTvRouteRemainTime.setText(String.format("%02d", new Object[]{Integer.valueOf(iTotHourTime)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(iTotMinTime)}));
                    }
                }
            }
        } else if (i == 10019) {
            Log.i(TAG, "iExtra_type = " + i2);
            if (i2 == 2 || i2 == 9 || i2 == 12) {
                if (this.mNaviAmapautoInfo != null && this.mNaviAmapautoInfo.getVisibility() == 0) {
                    this.mNaviAmapautoInfo.setVisibility(8);
                }
                if (this.mIvAudiRightLogo != null && this.mIvAudiRightLogo.getVisibility() != 0) {
                    this.mIvAudiRightLogo.setVisibility(0);
                }
            }
        }
    }

    @SuppressLint({"SetTextI18n"})
    public void refreshPlayState() {
        if (this.mLauncher.mApp.getEvtService() != null) {
            try {
                if (this.mLauncher.mApp.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                    String validModeTitleInfor = this.mLauncher.mApp.getEvtService().getValidModeTitleInfor();
                    String validModeArtistInfor = this.mLauncher.mApp.getEvtService().getValidModeArtistInfor();
                    String validModeAblumInfor = this.mLauncher.mApp.getEvtService().getValidModeAblumInfor();
                    int validModeCurTime = this.mLauncher.mApp.getEvtService().getValidCurTime();
                    int validModeTotTime = this.mLauncher.mApp.getEvtService().getValidTotTime();
                    if (this.mTvMusicTitleInfor != null && ("".equals(validModeTitleInfor) || "".equals(this.mTvMusicTitleInfor.getText().toString()) || !validModeTitleInfor.equals(this.mTvMusicTitleInfor.getText().toString()))) {
                        Log.i(TAG, "refreshPlayState: validModeTitleInfor = " + validModeTitleInfor);
                        this.mTvMusicTitleInfor.setFocusable(true);
                        this.mTvMusicTitleInfor.setText(validModeTitleInfor);
                        if (this.mTvMusicArtistInfor != null) {
                            this.mTvMusicArtistInfor.setText(validModeArtistInfor);
                        }
                        if (this.mTvMusicAblumInfor != null) {
                            this.mTvMusicAblumInfor.setText(validModeAblumInfor);
                        }
                        if (this.mSkBarProgress != null) {
                            this.mSkBarProgress.setMax(validModeTotTime);
                        }
                        if (this.mIvMediaTypeBg != null) {
                            this.mIvMediaTypeBg.setVisibility(this.mMusicCoverBm == null ? 0 : 8);
                        }
                        if (this.mMusicCoverBg != null) {
                            this.mMusicCoverBg.setImageBitmap(BlurUtil.doBlur(this.mMusicCoverBm, 10, false));
                        }
                        if (this.mMusicCover != null) {
                            this.mMusicCover.setImageBitmap(this.mMusicCoverBm);
                        }
                    }
                    if (this.mSkBarProgress != null) {
                        this.mSkBarProgress.setProgress(validModeCurTime);
                    }
                    if (this.mTvCurrTime != null) {
                        this.mTvCurrTime.setText(EventUtils.getProgressFromPosition(validModeCurTime));
                    }
                    if (this.mTvTotTime != null) {
                        this.mTvTotTime.setText(EventUtils.getProgressFromPosition(validModeTotTime));
                    }
                } else if (this.mTvMusicTitleInfor != null && !"".equals(this.mTvMusicTitleInfor.getText().toString())) {
                    if (this.mTvMusicTitleInfor != null) {
                        this.mTvMusicTitleInfor.setText("");
                    }
                    if (this.mTvMusicArtistInfor != null) {
                        this.mTvMusicArtistInfor.setText("");
                    }
                    if (this.mTvMusicAblumInfor != null) {
                        this.mTvMusicAblumInfor.setText("");
                    }
                    if (this.mSkBarProgress != null) {
                        this.mSkBarProgress.setMax(0);
                        this.mSkBarProgress.setProgress(0);
                    }
                    if (this.mTvCurrTime != null) {
                        this.mTvCurrTime.setText("00:00:00");
                    }
                    if (this.mTvTotTime != null) {
                        this.mTvTotTime.setText("00:00:00");
                    }
                    if (this.mIvMediaTypeBg != null) {
                        this.mIvMediaTypeBg.setVisibility(0);
                    }
                    if (this.mMusicCoverBg != null) {
                        this.mMusicCoverBg.setImageBitmap((Bitmap) null);
                    }
                    if (this.mMusicCover != null) {
                        this.mMusicCover.setImageBitmap((Bitmap) null);
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "refreshPlayState: e = " + e.toString());
            }
        }
    }

    public void setMusicCoverBg(Bitmap bitmap) {
        this.mMusicCoverBm = bitmap;
    }

    public void setMediaTypeBackground(int type) {
        if (this.mIvMediaTypeBg != null) {
            switch (type) {
                case 0:
                    if (this.mLauncher.m_iModeSet == 15) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_bendi);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 16) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_bendi);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    if (this.mLauncher.m_iModeSet == 15) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_sd);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 16) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_sd);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    if (this.mLauncher.m_iModeSet == 15) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_usb);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 16) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_usb);
                        return;
                    } else {
                        return;
                    }
                default:
                    if (this.mLauncher.m_iModeSet == 15) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_bendi);
                        return;
                    } else if (this.mLauncher.m_iModeSet == 16) {
                        this.mIvMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_bendi);
                        return;
                    } else {
                        return;
                    }
            }
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

    public void onClick(View view) {
        this.bInLeftFocus = false;
        this.iLeftFocusIndex = -1;
        int id = view.getId();
        if (id != R.id.IvRightMusicIcon) {
            switch (id) {
                case R.id.btnApps:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 0;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 5;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 10;
                        } else {
                            this.iMainFocusIndex = 11;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 10;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 4;
                        } else {
                            this.iMainFocusIndex = 10;
                        }
                    }
                    this.mLauncher.showApps(true);
                    break;
                case R.id.btnAux:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 8;
                    }
                    EventUtils.onEnterAux(this.mLauncher, this.mLauncher.bHaveAux);
                    break;
                case R.id.btnBrowser:
                    if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 11;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 9;
                        } else {
                            this.iMainFocusIndex = 5;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 6;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 7;
                        } else {
                            this.iMainFocusIndex = 5;
                        }
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    break;
                case R.id.btnBt:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 4;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 7;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 2;
                        } else {
                            this.iMainFocusIndex = 1;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1) {
                            this.iMainFocusIndex = 2;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 4;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 3;
                        } else {
                            this.iMainFocusIndex = 2;
                        }
                    } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
                        this.iMainFocusIndex = 1;
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    break;
                case R.id.btnCarInfo:
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.szchoiceway.VScanbus", "com.szchoiceway.VScanbus.CanMainActivity");
                    break;
                case R.id.btnDashBoard:
                    if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 3;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 11;
                        } else {
                            this.iMainFocusIndex = 9;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 8;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 1;
                        } else {
                            this.iMainFocusIndex = 8;
                        }
                    } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
                        this.iMainFocusIndex = 5;
                    }
                    EventUtils.onEnterDashBoard(this.mLauncher, this.mLauncher.bSupportDashBoard);
                    break;
                case R.id.btnDvd:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 10;
                    }
                    EventUtils.onEnterDvd(this.mLauncher, this.mLauncher.iHaveDvdType);
                    break;
                case R.id.btnDvr:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 6;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 1;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 7;
                        } else {
                            this.iMainFocusIndex = 7;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 7;
                        } else {
                            this.iMainFocusIndex = 7;
                        }
                    }
                    EventUtils.onEnterDvr(this.mLauncher, this.mLauncher.iHaveDvrType);
                    break;
                case R.id.btnESFile:
                    if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 9;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 8;
                        } else {
                            this.iMainFocusIndex = 3;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 5;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 8;
                        } else {
                            this.iMainFocusIndex = 6;
                        }
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    break;
                case R.id.btnLeftApps:
                    this.bInLeftFocus = true;
                    this.iLeftFocusIndex = 4;
                    this.mLauncher.showApps(true);
                    break;
                case R.id.btnLeftMusic:
                    this.bInLeftFocus = true;
                    this.iLeftFocusIndex = 0;
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    break;
                case R.id.btnLeftNavi:
                    this.bInLeftFocus = true;
                    this.iLeftFocusIndex = 1;
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    break;
                case R.id.btnLeftOriginaCar:
                    this.bInLeftFocus = true;
                    this.iLeftFocusIndex = 3;
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    break;
                case R.id.btnLeftSetting:
                    this.bInLeftFocus = true;
                    this.iLeftFocusIndex = 2;
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    break;
                case R.id.btnMusic:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 2;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 6;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 0;
                        } else {
                            this.iMainFocusIndex = 0;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1) {
                            this.iMainFocusIndex = 1;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 6;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 5;
                        } else {
                            this.iMainFocusIndex = 0;
                        }
                    } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
                        this.iMainFocusIndex = 2;
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    break;
                case R.id.btnMusicNext:
                    if (this.mLauncher.mApp.getEvtService() != null) {
                        try {
                            if (this.mLauncher.mApp.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                                EventUtils.sendBroadcastCanKeyExtra(this.mLauncher, EventUtils.ZXW_CAN_KEY_EVT, 2);
                                break;
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    break;
                case R.id.btnMusicPlayPause:
                    if (this.mLauncher.mApp.getEvtService() != null) {
                        try {
                            if (this.mLauncher.mApp.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                                EventUtils.sendBroadcastCanKeyExtra(this.mLauncher, EventUtils.ZXW_CAN_KEY_EVT, 6);
                                break;
                            }
                        } catch (RemoteException e2) {
                            e2.printStackTrace();
                            break;
                        }
                    }
                    break;
                case R.id.btnMusicPrev:
                    if (this.mLauncher.mApp.getEvtService() != null) {
                        try {
                            if (this.mLauncher.mApp.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                                EventUtils.sendBroadcastCanKeyExtra(this.mLauncher, EventUtils.ZXW_CAN_KEY_EVT, 3);
                                break;
                            }
                        } catch (RemoteException e3) {
                            e3.printStackTrace();
                            break;
                        }
                    }
                    break;
                case R.id.btnNavi:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 1;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 8;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 1;
                        } else {
                            this.iMainFocusIndex = 2;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 0;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 2;
                        } else {
                            this.iMainFocusIndex = 1;
                        }
                    } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
                        this.iMainFocusIndex = 0;
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.NAV_MODE_PACKAGE_NAME, EventUtils.NAV_MODE_CLASS_NAME);
                    break;
                case R.id.btnOffScreen:
                    EventUtils.sendBroadcastCanKeyExtra(this.mLauncher, EventUtils.ZXW_CAN_KEY_EVT, -9);
                    break;
                case R.id.btnOriginaCar:
                    if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 10;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 3;
                        } else {
                            this.iMainFocusIndex = 4;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1) {
                            this.iMainFocusIndex = 4;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 1;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 0;
                        } else {
                            this.iMainFocusIndex = 4;
                        }
                    } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
                        this.iMainFocusIndex = 4;
                    }
                    this.mLauncher.sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    break;
                case R.id.btnPagerNext:
                    this.iMainPageIndex++;
                    if (this.iMainPageIndex >= this.mViewPager.getAdapter().getCount()) {
                        this.iMainPageIndex = this.mViewPager.getAdapter().getCount() - 1;
                    }
                    if (this.mViewPager != null) {
                        this.mViewPager.setCurrentItem(this.iMainPageIndex);
                        break;
                    }
                    break;
                case R.id.btnPagerPriv:
                    this.iMainPageIndex--;
                    if (this.iMainPageIndex < 0) {
                        this.iMainPageIndex = 0;
                    }
                    if (this.mViewPager != null) {
                        this.mViewPager.setCurrentItem(this.iMainPageIndex);
                        break;
                    }
                    break;
                case R.id.btnPhoneLink:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 5;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 4;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 6;
                        } else {
                            this.iMainFocusIndex = 10;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 9;
                        } else {
                            this.iMainFocusIndex = 9;
                        }
                    }
                    EventUtils.onEnterPhoneLink(this.mLauncher);
                    break;
                case R.id.btnRadio:
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.RADIO_MODE_PACKAGE_NAME, EventUtils.RADIO_MODE_CLASS_NAME);
                    break;
                case R.id.btnSearch:
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
                    break;
                case R.id.btnSetting:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 7;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 2;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 5;
                        } else {
                            this.iMainFocusIndex = 8;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 11;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 9;
                        } else {
                            this.iMainFocusIndex = 11;
                        }
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    break;
                case R.id.btnTv:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 9;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 1;
                        } else {
                            this.iMainFocusIndex = 7;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        this.iMainFocusIndex = 7;
                    }
                    EventUtils.onEnterTv(this.mLauncher, this.mLauncher.bHaveTv);
                    break;
                case R.id.btnVideo:
                    if (this.mLauncher.m_iModeSet == 0) {
                        this.iMainFocusIndex = 3;
                    } else if (this.mLauncher.m_iModeSet == 2 || this.mLauncher.m_iModeSet == 4) {
                        if (this.mLauncher.m_i_ksw_evo_main_interface_index == 1) {
                            this.iMainFocusIndex = 0;
                        } else if (this.mLauncher.m_i_ksw_evo_main_interface_index == 2 || this.mLauncher.m_i_ksw_evo_main_interface_index == 3) {
                            this.iMainFocusIndex = 4;
                        } else {
                            this.iMainFocusIndex = 6;
                        }
                    } else if (this.mLauncher.m_iModeSet == 7 || this.mLauncher.m_iModeSet == 17) {
                        if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 1 || this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 2) {
                            this.iMainFocusIndex = 3;
                        } else if (this.mLauncher.m_i_ksw_evo_id6_main_interface_index == 3) {
                            this.iMainFocusIndex = 6;
                        } else {
                            this.iMainFocusIndex = 3;
                        }
                    } else if (this.mLauncher.m_iModeSet == 15 || this.mLauncher.m_iModeSet == 16) {
                        this.iMainFocusIndex = 3;
                    }
                    EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    break;
            }
        } else {
            EventUtils.startActivityIfNotRuning(this.mLauncher, EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
        }
        refreshLeftFocusView();
        refreshMainFocusView();
    }
}
