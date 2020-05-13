package com.szchoiceway.index;

import android.accounts.AccountManager;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.SearchManager;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Advanceable;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.choiceway.index.IWeatherService;
import com.szchoiceway.eventcenter.ICallbackfn;
import com.szchoiceway.eventcenter.IEventService;
import com.szchoiceway.index.CellLayout;
import com.szchoiceway.index.CoverFlowView;
import com.szchoiceway.index.DragLayer;
import com.szchoiceway.index.DropTarget;
import com.szchoiceway.index.EventUtils;
import com.szchoiceway.index.LauncherModel;
import com.szchoiceway.index.SmoothPagedView;
import com.szchoiceway.index.Workspace;
import com.szchoiceway.index.listener.OnItemClickListener;
import com.szchoiceway.index.listener.OnItemSelectedListener;
import com.szchoiceway.index.view.LoopRotarySwitchView;
import com.szchoiceway.index.view.MyClockView;
import com.szchoiceway.index.view.MyQAnalogClock2;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpStatus;

@TargetApi(18)
public final class Launcher extends Activity implements View.OnClickListener, View.OnLongClickListener, LauncherModel.Callbacks {
    static final int APPWIDGET_HOST_ID = 1024;
    private static final String BT_SRV_NAME = "com.szchoiceway.btsuite.BTService";
    private static final String CAMERA_CAN_FOCUS_NAME = "com.szchoiceway.FocusCanbus.FocusCanService";
    public static final String CAMERA_CAN_ORIGINACAR_NAME = "com.szchoiceway.originacarcanbus.OriginaCarCanService";
    private static final String CAMERA_CAN_SRV_NAME = "com.szchoiceway.VScanbus.CanBusService";
    private static final int CHEKU_BENCHI_ORIGINAL_ENTER = 14;
    private static final int CHEKU_BENCHI_ORIGINAL_LEFT = 12;
    private static final int CHEKU_BENCHI_ORIGINAL_RIGHT = 13;
    private static final int CHEKU_HANDLE_SHOW_NO_NAVI_DATA_VIEW = 5;
    static final boolean DEBUG_RESUME_TIME = false;
    static final boolean DEBUG_STRICT_MODE = false;
    static final boolean DEBUG_WIDGETS = false;
    static final int DEFAULT_SCREEN = 2;
    private static final int DISMISS_CLING_DURATION = 250;
    static final String DUMP_STATE_PROPERTY = "launcher_dump_state";
    private static final String EVENT_CENTER_SRV_NAME = "com.szchoiceway.eventcenter.EventService";
    private static final String EVENT_STANDBY_SRV_NAME = "com.szchoiceway.standbyunlock.service.NsLockService";
    private static final int EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT = 600;
    private static final int EXIT_SPRINGLOADED_MODE_SHORT_TIMEOUT = 300;
    static final String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
    static final String FORCE_ENABLE_ROTATION_PROPERTY = "launcher_force_rotate";
    static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.szchoiceway.index.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";
    private static final int KSW_HANDLE_DELAYED_BT_CONNECT_MENU = 4;
    private static final int MENU_GROUP_WALLPAPER = 1;
    private static final int MENU_HELP = 5;
    private static final int MENU_MANAGE_APPS = 3;
    private static final int MENU_SYSTEM_SETTINGS = 4;
    private static final int MENU_WALLPAPER_SETTINGS = 2;
    private static final int MSG_CHECK_TOUCH_PANE_CFG = 15;
    private static final int MSG_DELAYED_BTSERVICE = 3;
    private static final int MSG_REFRESH_TIMER = 1;
    private static final int MSG_SEC_FLASH = 2;
    private static int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 10;
    private static final String PREFERENCES = "launcher.preferences";
    static final boolean PROFILE_STARTUP = false;
    private static final int REQUEST_BIND_APPWIDGET = 11;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    private static final int REQUEST_PICK_APPLICATION = 6;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    private static final int REQUEST_PICK_SHORTCUT = 7;
    private static final int REQUEST_PICK_WALLPAPER = 10;
    private static final String RUNTIME_STATE = "launcher.state";
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_X = "launcher.add_cell_x";
    private static final String RUNTIME_STATE_PENDING_ADD_CELL_Y = "launcher.add_cell_y";
    private static final String RUNTIME_STATE_PENDING_ADD_CONTAINER = "launcher.add_container";
    private static final String RUNTIME_STATE_PENDING_ADD_SCREEN = "launcher.add_screen";
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_X = "launcher.add_span_x";
    private static final String RUNTIME_STATE_PENDING_ADD_SPAN_Y = "launcher.add_span_y";
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_ID = "launcher.add_widget_id";
    private static final String RUNTIME_STATE_PENDING_ADD_WIDGET_INFO = "launcher.add_widget_info";
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME = "launcher.rename_folder";
    private static final String RUNTIME_STATE_PENDING_FOLDER_RENAME_ID = "launcher.rename_folder_id";
    static final int SCREEN_COUNT = 5;
    private static final int SHOW_CLING_DURATION = 550;
    private static final String START_YUNZHISHENG_SRV_NAME = "cn.yunzhisheng.vui.assistant.car";
    private static final int Send_Delayed_ShowMusicInfo_MSG = 1004;
    static final String TAG = "Launcher";
    private static final String TOOLBAR_ICON_METADATA_NAME = "com.szchoiceway.index.toolbar_icon";
    private static final String TOOLBAR_SEARCH_ICON_METADATA_NAME = "com.szchoiceway.index.toolbar_search_icon";
    private static final String TOOLBAR_VOICE_SEARCH_ICON_METADATA_NAME = "com.szchoiceway.index.toolbar_voice_search_icon";
    private static final String UPDATE_MCU_SRV_NAME = "com.szchoiceway.updatemcu.UpdateMcuService";
    private static final String UPDATE_SRV_NAME = "com.szchoiceway.appwidget.UpdateService";
    private static final String VOLWND_SRV_NAME = "com.szchoiceway.volwnd.VolWndService";
    private static final String WEATHER_SRV_NAME = "com.szchoiceway.appwidget.WeatherService";
    public static final String ZXW_CAN_KEY_EVT = "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT";
    public static final String ZXW_CAN_KEY_EVT_EXTRA = "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT_EXTRA";
    private static final int ZXW_HANDLER_AUDIO_RIGHT_QICHI_XINXI = 10;
    private static final int ZXW_HANDLER_EVO_ID6_CHELIANGXINXI = 11;
    private static final int ZXW_HANDLER_OLD_X1_ORIGINAL_RETURN_ARM = 9;
    private static final int ZXW_HANDLER_REFRESH_BASE_INFO = 16;
    private static final int ZXW_HANDLER_SD_USB_FACTORY = 6;
    private static final int ZXW_HANDLER_SD_USB_LOGO = 7;
    private static final int ZXW_HANDLER_SD_USB_MAP_APK_LIST = 8;
    public static boolean b_sing_coverFlowView = false;
    public static boolean b_sing_touch = false;
    /* access modifiers changed from: private */
    public static int ksw_3d_currentPosition;
    /* access modifiers changed from: private */
    public static int ksw_3d_lastPosition = -1;
    public static int ksw_m_i_audio_right_ShunShiSuDu = 0;
    public static int ksw_m_i_audio_right_fadongjizhuansu = 0;
    /* access modifiers changed from: private */
    public static int m_iLastPageCurrFocus = 0;
    private static Drawable.ConstantState[] sAppMarketIcon = new Drawable.ConstantState[2];
    static final ArrayList<String> sDumpLogs = new ArrayList<>();
    private static HashMap<Long, FolderInfo> sFolders = new HashMap<>();
    private static boolean sForceEnableRotation = isPropertyEnabled(FORCE_ENABLE_ROTATION_PROPERTY);
    private static Drawable.ConstantState[] sGlobalSearchIcon = new Drawable.ConstantState[2];
    /* access modifiers changed from: private */
    public static LocaleConfiguration sLocaleConfiguration = null;
    private static final Object sLock = new Object();
    private static boolean sPausedFromUserAction = false;
    private static ArrayList<PendingAddArguments> sPendingAddList = new ArrayList<>();
    private static int sScreen = 2;
    private static Drawable.ConstantState[] sVoiceSearchIcon = new Drawable.ConstantState[2];
    private final int ADVANCE_MSG = 1;
    private final int KESAIWEI_HANDLER_SEND_MCU = 1000;
    private RelativeLayout KSW_A4L_right_Traffic_information = null;
    private RelativeLayout KSW_A4L_right_show_Medio = null;
    private RelativeLayout KSW_A4L_right_show_Navi = null;
    private RelativeLayout KSW_A4L_right_show_logo = null;
    private ServiceConnection SerCon = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Launcher.TAG, "*****onServiceConnected*****");
            IEventService unused = Launcher.this.mEvtService = IEventService.Stub.asInterface(service);
            Log.i(Launcher.TAG, "onServiceConnected: mEvtService = " + Launcher.this.mEvtService);
            try {
                Launcher.this.mEvtService.setDashBoardCallback(Launcher.this.mDashBoardCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            synchronized (Launcher.class) {
                Launcher.class.notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i(Launcher.TAG, "****onServiceDisconnected****");
            IEventService unused = Launcher.this.mEvtService = null;
            synchronized (Launcher.class) {
                Launcher.class.notifyAll();
            }
        }
    };
    private RelativeLayout ShowScreen2 = null;
    /* access modifiers changed from: private */
    public LauncherApplication app;
    private boolean bHaveDsp;
    /* access modifiers changed from: private */
    public boolean bInLeftFocus = false;
    /* access modifiers changed from: private */
    public boolean bIsMcuKeyRemove = false;
    private boolean bIsNavigating = false;
    /* access modifiers changed from: private */
    public boolean bIsShowSmallViewEVO_ID6 = false;
    /* access modifiers changed from: private */
    public boolean bKesaiweiBPark;
    /* access modifiers changed from: private */
    public boolean bKesaiweiBelt;
    /* access modifiers changed from: private */
    public boolean bWeatherChange = true;
    private Bitmap bitmap;
    private ImageButton btnPagerNext;
    private ImageButton btnPagerPriv;
    /* access modifiers changed from: private */
    public CheckBox cb_anquandai;
    /* access modifiers changed from: private */
    public CheckBox cb_shousha;
    int cheku_benchi_m_p_left = 0;
    int cheku_benchi_m_p_right = 0;
    int[] iLogoRes = null;
    int[] iLogoResItemBk = null;
    String[] iLogoResname = null;
    private int iMapIcon = 0;
    /* access modifiers changed from: private */
    public int iMediaType;
    private int iRouteRemainDis = 0;
    private int iRouteRemainTime = 0;
    private int iSegRemainDis = 0;
    private int iTest;
    /* access modifiers changed from: private */
    public ImageView[] imageViewFocusList;
    private ImageView[] imageViewFocusList1_chwy;
    private ImageView[] imageViewFocusList2_chwy;
    /* access modifiers changed from: private */
    public ImageView[] imageViewFocusList_whats1;
    private ImageView[] imageViewFocusList_whats1_modeIcon;
    private TextView[] imageViewFocusList_whats1_normal_Text_zoom;
    private ImageView[] imageViewFocusList_whats1_normal_modeIcon_zoom;
    private TextView[] imageViewFocusList_whats1_select_Text_zoom;
    /* access modifiers changed from: private */
    public ImageView[] imageViewFocusList_whats2;
    private ImageView[] imageViewFocusList_whats2_modeIcon;
    private TextView[] imageViewFocusList_whats2_normal_Text_zoom;
    private ImageView[] imageViewFocusList_whats2_normal_modeIcon_zoom;
    private TextView[] imageViewFocusList_whats2_select_Text_zoom;
    private ImageView[] imageViewFocusList_whats3;
    private ImageView[] imageViewFocusList_whats4;
    private ImageView[] imageViewFocusList_zhishi;
    /* access modifiers changed from: private */
    public ImageView[] imageViewLeftFocusList;
    /* access modifiers changed from: private */
    public int[] imgFocusList;
    private int[] imgFocusList_chwy;
    private int[] imgFocusList_modeIcon;
    private int[] imgFocusList_normal_Text_zoom;
    private int[] imgFocusList_normal_modeIcon_zoom;
    private int[] imgFocusList_select_Text_zoom;
    private int[] imgFocusList_src_anniu;
    /* access modifiers changed from: private */
    public int[] imgFocusList_src_icon;
    private int[] imgFocusList_zhishi;
    private int[] imgFocusList_zuo;
    private int[] imgLeftFocusList;
    /* access modifiers changed from: private */
    public ArrayList<Bitmap> imgList;
    private ImageView imgViewFocusList_src_anniu;
    private ImageView imgViewFocusList_src_icon;
    private ImageView imgViewFocusList_zuo;
    /* access modifiers changed from: private */
    public boolean isPlay = false;
    private ImageView ivMediaTypeBg;
    private ImageView ivRightMusicIcon = null;
    private ImageView iv_bt_connect_show_KSW = null;
    private ImageView ksw_3d_ivGuang;
    /* access modifiers changed from: private */
    public LoopRotarySwitchView ksw_3d_mLoopRotarySwitchView;
    /* access modifiers changed from: private */
    public int ksw_3d_selectItem;
    /* access modifiers changed from: private */
    public List<View> ksw_3d_views;
    private int ksw_3d_width;
    private ImageView ksw_A4L_audi_che = null;
    private boolean ksw_m_b_Focus_image_zoom_evo = false;
    /* access modifiers changed from: private */
    public String ksw_m_str_audio_right_wendu = "";
    /* access modifiers changed from: private */
    public String ksw_m_str_audio_right_xushilicheng = "";
    /* access modifiers changed from: private */
    public String ksw_m_str_xushilichengg = "";
    /* access modifiers changed from: private */
    public String ksw_m_str_youliang = "";
    /* access modifiers changed from: private */
    public TextView logoname = null;
    private final int mAdvanceInterval = 20000;
    private final int mAdvanceStagger = 250;
    private View mAllAppsButton;
    private Intent mAppMarketIntent = null;
    /* access modifiers changed from: private */
    public LauncherAppWidgetHost mAppWidgetHost;
    private AppWidgetManager mAppWidgetManager;
    /* access modifiers changed from: private */
    public AppsCustomizePagedView mAppsCustomizeContent;
    /* access modifiers changed from: private */
    public AppsCustomizeTabHost mAppsCustomizeTabHost;
    private boolean mAttached = false;
    private boolean mAutoAdvanceRunning = false;
    private long mAutoAdvanceSentTime;
    private long mAutoAdvanceTimeLeft = -1;
    private Runnable mBindPackagesUpdatedRunnable = new Runnable() {
        public void run() {
            Launcher.this.bindPackagesUpdated(Launcher.this.mWidgetsAndShortcuts);
            ArrayList unused = Launcher.this.mWidgetsAndShortcuts = null;
        }
    };
    /* access modifiers changed from: private */
    public Runnable mBuildLayersRunnable = new Runnable() {
        public void run() {
            if (Launcher.this.mWorkspace != null) {
                Launcher.this.mWorkspace.buildPageHardwareLayers();
            }
        }
    };
    private CheckBox mChkMusicPlayPause;
    private CheckBox mChkVideoPlayPause;
    private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
    CoverFlowView<MyCoverFlowAdapter> mCoverFlowView = null;
    /* access modifiers changed from: private */
    public ICallbackfn.Stub mDashBoardCallback = new ICallbackfn.Stub() {
        public void notifyEvt(int iEvtMsgid, int wParam, int lParam, byte[] byData, String strData) throws RemoteException {
            if (iEvtMsgid == 90) {
                Launcher.this.mMyhandler.sendMessage(Launcher.this.mMyhandler.obtainMessage(90, byData));
            } else if (iEvtMsgid == 91) {
                Launcher.this.mMyhandler.sendMessage(Launcher.this.mMyhandler.obtainMessage(91, byData));
            }
        }
    };
    private SpannableStringBuilder mDefaultKeySsb = null;
    private AnimatorSet mDividerAnimator;
    private View mDockDivider;
    private DragController mDragController;
    /* access modifiers changed from: private */
    public DragLayer mDragLayer;
    /* access modifiers changed from: private */
    public IEventService mEvtService;
    private Bitmap mFolderIconBitmap;
    private Canvas mFolderIconCanvas;
    /* access modifiers changed from: private */
    public ImageView mFolderIconImageView;
    private FolderInfo mFolderInfo;
    private GestureDetector mGestureDetector;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int i = 0;
                for (View key : Launcher.this.mWidgetsToAdvance.keySet()) {
                    final View v = key.findViewById(((AppWidgetProviderInfo) Launcher.this.mWidgetsToAdvance.get(key)).autoAdvanceViewId);
                    int delay = i * 250;
                    if (v instanceof Advanceable) {
                        postDelayed(new Runnable() {
                            public void run() {
                                ((Advanceable) v).advance();
                            }
                        }, (long) delay);
                    }
                    i++;
                }
                Launcher.this.sendAdvanceMessage(20000);
            }
        }
    };
    private HideFromAccessibilityHelper mHideFromAccessibilityHelper = new HideFromAccessibilityHelper();
    private Hotseat mHotseat;
    private IconCache mIconCache;
    private LayoutInflater mInflater;
    /* access modifiers changed from: private */
    public boolean mInitEventState = false;
    private boolean mInitVar = false;
    private View mLauncherView;
    /* access modifiers changed from: private */
    public View[] mMeneItem;
    private LauncherModel mModel;
    /* access modifiers changed from: private */
    public Handler mMyhandler = new Handler() {
        boolean state = false;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Launcher.this.updateTimerInfor();
                    Launcher.this.updateWeatherInfor();
                    Launcher.this.mMyhandler.sendEmptyMessageDelayed(1, 5000);
                    return;
                case 2:
                    if (!Launcher.this.mInitEventState) {
                        boolean unused = Launcher.this.mInitEventState = true;
                        Launcher.this.sendBroadcast(new Intent("com.szchoiceway.initEventState"));
                    }
                    if (Launcher.this.mInitEventState) {
                        Launcher.this.mMyhandler.removeMessages(2);
                        return;
                    } else {
                        Launcher.this.mMyhandler.sendEmptyMessageDelayed(2, 500);
                        return;
                    }
                case 3:
                    Launcher.this.startService(new Intent(Launcher.BT_SRV_NAME).setPackage(EventUtils.BT_MODE_PACKAGE_NAME));
                    return;
                case 4:
                    if (Launcher.this.mSysProviderOpt != null) {
                        Launcher.this.ksw_set_bt_connect_menu(Launcher.this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KESAIWEI_RECORD_BT_CONNECT_MENU, false));
                        return;
                    }
                    return;
                case 5:
                    Launcher.this.onShowNaviInfo(false);
                    return;
                case 6:
                    if (Launcher.this.zxw_context != null) {
                        Toast.makeText(Launcher.this.zxw_context, Launcher.this.getString(R.string.lb_factroy_xml_ok), 1).show();
                        Log.i(Launcher.TAG, "--->>> factory.xml写入参数成功，请拔卡/usb重启机器");
                        return;
                    }
                    return;
                case 7:
                    if (Launcher.this.zxw_context != null) {
                        Toast.makeText(Launcher.this.zxw_context, Launcher.this.getString(R.string.lb_oem_logo_ok), 1).show();
                        Log.i(Launcher.TAG, "--->>> /OEM/logo.bmp写入参数成功，请拔卡/usb重启机器");
                        return;
                    }
                    return;
                case 8:
                    if (Launcher.this.zxw_context != null) {
                        Toast.makeText(Launcher.this.zxw_context, Launcher.this.getString(R.string.lb_mapApkLst_ok), 1).show();
                        Log.i(Launcher.TAG, "--->>> /OEM/mapApkLst.txt写入参数成功");
                        return;
                    }
                    return;
                case 9:
                    if (Launcher.this.zxw_context != null) {
                        Toast.makeText(Launcher.this.zxw_context, Launcher.this.getString(R.string.lb_warnning), 1).show();
                        Log.i(Launcher.TAG, "--->>> 请确保CD在AUX模式");
                        return;
                    }
                    return;
                case 10:
                    Launcher.this.onfresh_qichexinxi_audio_right_KSW();
                    return;
                case 11:
                    Launcher.this.onRefreshCheLiangXinXI();
                    return;
                case 12:
                    if (Launcher.this.cheku_benchi_m_p_left <= 410) {
                        Launcher launcher = Launcher.this;
                        launcher.cheku_benchi_m_p_left -= 20;
                        Launcher.this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (Launcher.this.mCoverFlowView.getLeft() + Launcher.this.cheku_benchi_m_p_left), (float) (Launcher.this.mCoverFlowView.getTop() + 125), 0));
                        Launcher.this.mMyhandler.removeMessages(12);
                        Launcher.b_sing_coverFlowView = false;
                        return;
                    }
                    Launcher launcher2 = Launcher.this;
                    launcher2.cheku_benchi_m_p_left -= 20;
                    Launcher.this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, (float) (Launcher.this.mCoverFlowView.getLeft() + Launcher.this.cheku_benchi_m_p_left), (float) (Launcher.this.mCoverFlowView.getTop() + 125), 0));
                    Launcher.this.mMyhandler.removeMessages(12);
                    Launcher.this.mMyhandler.sendEmptyMessageDelayed(12, 10);
                    return;
                case 13:
                    if (Launcher.this.cheku_benchi_m_p_right >= 870) {
                        Launcher.this.cheku_benchi_m_p_right += 20;
                        Launcher.this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (Launcher.this.mCoverFlowView.getLeft() + Launcher.this.cheku_benchi_m_p_right), (float) (Launcher.this.mCoverFlowView.getTop() + 125), 0));
                        Launcher.this.mMyhandler.removeMessages(13);
                        Launcher.b_sing_coverFlowView = false;
                        return;
                    }
                    Launcher.this.cheku_benchi_m_p_right += 20;
                    Launcher.this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 2, (float) (Launcher.this.mCoverFlowView.getLeft() + Launcher.this.cheku_benchi_m_p_right), (float) (Launcher.this.mCoverFlowView.getTop() + 125), 0));
                    Launcher.this.mMyhandler.removeMessages(13);
                    Launcher.this.mMyhandler.sendEmptyMessageDelayed(13, 10);
                    return;
                case 15:
                    removeMessages(15);
                    new TouchPaneCfg(Launcher.this);
                    return;
                case 16:
                    Launcher.this.onRefreshBaseInfo();
                    return;
                case 90:
                    if (((byte[]) msg.obj).length < 20) {
                    }
                    return;
                case 91:
                    byte[] bydata = (byte[]) msg.obj;
                    boolean unused2 = Launcher.this.bKesaiweiBPark = false;
                    if ((bydata[5] & 8) > 0) {
                        boolean unused3 = Launcher.this.bKesaiweiBPark = true;
                    } else {
                        boolean unused4 = Launcher.this.bKesaiweiBPark = false;
                    }
                    boolean unused5 = Launcher.this.bKesaiweiBelt = false;
                    if ((bydata[6] & 1) > 0) {
                        boolean unused6 = Launcher.this.bKesaiweiBelt = true;
                    } else {
                        boolean unused7 = Launcher.this.bKesaiweiBelt = false;
                    }
                    Log.i(Launcher.TAG, "handleMessage: b_kesaiwei_bPark = " + Launcher.this.bKesaiweiBPark);
                    Log.i(Launcher.TAG, "handleMessage: b_kesaiwei_belt = " + Launcher.this.bKesaiweiBelt);
                    if (Launcher.this.cb_shousha != null) {
                        if (Launcher.this.bKesaiweiBPark) {
                            Launcher.this.cb_shousha.setChecked(true);
                        } else {
                            Launcher.this.cb_shousha.setChecked(false);
                        }
                    }
                    if (Launcher.this.cb_anquandai != null) {
                        if (Launcher.this.bKesaiweiBelt) {
                            Launcher.this.cb_anquandai.setChecked(false);
                        } else {
                            Launcher.this.cb_anquandai.setChecked(true);
                        }
                    }
                    if (Launcher.this.tvShouSha != null) {
                        if (Launcher.this.bKesaiweiBPark) {
                            Launcher.this.tvShouSha.setText(Launcher.this.getResources().getString(R.string.lb_Hand_brake_pull));
                        } else {
                            Launcher.this.tvShouSha.setText(Launcher.this.getResources().getString(R.string.lb_Hand_brake_release));
                        }
                    }
                    if (Launcher.this.tvAnQuanDai == null) {
                        return;
                    }
                    if (Launcher.this.bKesaiweiBelt) {
                        Launcher.this.tvAnQuanDai.setText(Launcher.this.getResources().getString(R.string.lb_Seat_belt_Yes));
                        return;
                    } else {
                        Launcher.this.tvAnQuanDai.setText(Launcher.this.getResources().getString(R.string.lb_Seat_belt_No));
                        return;
                    }
                case 888:
                    Launcher.this.initView_KeSaiWei_evo_id6();
                    if (Launcher.this.bIsShowSmallViewEVO_ID6) {
                        if (Launcher.this.m_iCurrFocus > 5) {
                            if (Launcher.this.mViewPager != null) {
                                Launcher.this.mViewPager.setCurrentItem(1);
                                return;
                            }
                            return;
                        } else if (Launcher.this.mViewPager != null) {
                            Launcher.this.mViewPager.setCurrentItem(0);
                            return;
                        } else {
                            return;
                        }
                    } else if (Launcher.this.m_iCurrFocus < 3) {
                        if (Launcher.this.mViewPager != null) {
                            Launcher.this.mViewPager.setCurrentItem(0);
                            return;
                        }
                        return;
                    } else if (Launcher.this.m_iCurrFocus < 6) {
                        if (Launcher.this.mViewPager != null) {
                            Launcher.this.mViewPager.setCurrentItem(1);
                            return;
                        }
                        return;
                    } else if (Launcher.this.m_iCurrFocus < 9) {
                        if (Launcher.this.mViewPager != null) {
                            Launcher.this.mViewPager.setCurrentItem(2);
                            return;
                        }
                        return;
                    } else if (Launcher.this.mViewPager != null) {
                        Launcher.this.mViewPager.setCurrentItem(3);
                        return;
                    } else {
                        return;
                    }
                case 1000:
                    Log.i(Launcher.TAG, "handleMessage: KESAIWEI_HANDLER_SEND_MCU");
                    try {
                        if (Launcher.this.mEvtService != null) {
                            Launcher.this.mEvtService.sendMcuData_KSW(Launcher.this.m_bydata);
                            return;
                        }
                        return;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return;
                    }
                case 1004:
                    LauncherApplication app = (LauncherApplication) Launcher.this.getApplication();
                    if (app != null && app.getEvtService() != null) {
                        try {
                            if (app.getEvtService().getValidModeTitleInfor() == null || app.getEvtService().getValidCurTime() == 0 || app.getEvtService().getValidTotTime() == 0) {
                                Launcher.this.onShowMusicInfo(false);
                                return;
                            } else {
                                Launcher.this.onShowMusicInfo(true);
                                return;
                            }
                        } catch (RemoteException e2) {
                            e2.printStackTrace();
                            return;
                        }
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    };
    private int mNewShortcutAnimatePage = -1;
    private ArrayList<View> mNewShortcutAnimateViews = new ArrayList<>();
    private ArrayList<Runnable> mOnResumeCallbacks = new ArrayList<>();
    private boolean mOnResumeNeedsLoad;
    /* access modifiers changed from: private */
    public State mOnResumeState = State.NONE;
    /* access modifiers changed from: private */
    public ImageView mPage0;
    /* access modifiers changed from: private */
    public ImageView mPage1;
    /* access modifiers changed from: private */
    public ImageView mPage2;
    private FrameLayout mPageFramelayout = null;
    private boolean mPaused = true;
    /* access modifiers changed from: private */
    public ItemInfo mPendingAddInfo = new ItemInfo();
    private int mPendingAddWidgetId = -1;
    private AppWidgetProviderInfo mPendingAddWidgetInfo;
    protected PlayStatusReceiver mPlayStatusReceiver = new PlayStatusReceiver();
    private View mQsbDivider;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean isAuto;
            boolean isAuto2;
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                boolean unused = Launcher.this.mUserPresent = false;
                Launcher.this.mDragLayer.clearAllResizeFrames();
                Launcher.this.updateRunning();
                if (Launcher.this.mAppsCustomizeTabHost != null && Launcher.this.mPendingAddInfo.container == -1) {
                    Launcher.this.mAppsCustomizeTabHost.reset();
                    Launcher.this.showWorkspace(false);
                }
            } else if ("android.intent.action.USER_PRESENT".equals(action)) {
                boolean unused2 = Launcher.this.mUserPresent = true;
                Launcher.this.updateRunning();
            } else if (action.equals(EventUtils.WEATHER_AREA_CHANGE)) {
                boolean unused3 = Launcher.this.bWeatherChange = true;
                Log.i(Launcher.TAG, "onReceive: WEATHER_AREA_CHANGE");
                if (Launcher.this.mWeatherSrv != null) {
                    try {
                        Launcher.this.mWeatherSrv.updateWeather();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                Launcher.this.mMyhandler.removeMessages(1);
                Launcher.this.mMyhandler.sendEmptyMessage(1);
            } else if (action.equals("android.intent.action.TIME_TICK")) {
                Launcher.this.updateTimerInfor();
            } else if (action.equals("com.android.quicksetting.BROADCAST")) {
            } else {
                if (action.equals(EventUtils.REC_AUTONAVI_STANDARD)) {
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        Launcher.this.cheku_autonavi_standard_1(intent);
                    } else if ((Launcher.this.m_iModeSet != 1 && Launcher.this.m_iModeSet != 3) || Launcher.this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_ARL_RIGHT_SHOW_INDEX, 1) == 1) {
                        if (Launcher.this.m_iModeSet == 8) {
                            Launcher.this.cheku_autonavi_standard_3(intent);
                        } else {
                            Launcher.this.cheku_autonavi_standard_2(intent);
                        }
                    }
                } else if (action.equals(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT)) {
                    if (Launcher.this.m_onResum) {
                        boolean isRuning = EventUtils.isActivityRuning(Launcher.this.getApplicationContext(), Launcher.class);
                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 000");
                        if (isRuning) {
                            int iExtra = intent.getIntExtra(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA, -1);
                            Log.i(Launcher.TAG, "onReceive: FOCUS_MOVE-iExtra = " + iExtra);
                            if (LauncherApplication.m_iUITypeVer == 41) {
                                if (Build.VERSION.SDK_INT >= 26 && Launcher.this.mState == State.APPS_CUSTOMIZE) {
                                    Log.i(Launcher.TAG, "onReceive: ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT-8.1-APPS");
                                    PagedViewCellLayoutChildren page = Launcher.this.mAppsCustomizeContent.getCurrPagedViewCellLayout();
                                    if (page != null) {
                                        View focus = Launcher.this.mAppsCustomizeContent.getCurFocusView();
                                        int index = page.indexOfChild(focus);
                                        if (page.getChildCount() > 0 && !Launcher.this.mAppsCustomizeContent.isPageMoving()) {
                                            if (iExtra == 7) {
                                                if (index < 0) {
                                                    Launcher.this.mAppsCustomizeContent.setCurFocusView(page.getChildAt(0));
                                                    page.getChildAt(0).invalidate();
                                                    return;
                                                } else if (index <= 0) {
                                                    PagedViewCellLayoutChildren prevPage = Launcher.this.mAppsCustomizeContent.getPrevPageViewCellLayout();
                                                    if (prevPage != null) {
                                                        Launcher.this.mAppsCustomizeContent.snapToPage(Launcher.this.mAppsCustomizeContent.getCurrentPage() - 1);
                                                        Launcher.this.mAppsCustomizeContent.setCurFocusView(prevPage.getChildAt(prevPage.getChildCount() - 1));
                                                        focus.invalidate();
                                                        Launcher.this.mAppsCustomizeContent.getCurFocusView().invalidate();
                                                        return;
                                                    }
                                                    return;
                                                } else {
                                                    Launcher.this.mAppsCustomizeContent.setCurFocusView(page.getChildAt(index - 1));
                                                    focus.invalidate();
                                                    Launcher.this.mAppsCustomizeContent.getCurFocusView().invalidate();
                                                    return;
                                                }
                                            } else if (iExtra == 8) {
                                                if (index < 0) {
                                                    Launcher.this.mAppsCustomizeContent.setCurFocusView(page.getChildAt(0));
                                                    page.getChildAt(0).invalidate();
                                                    return;
                                                } else if (index >= page.getChildCount() - 1) {
                                                    PagedViewCellLayoutChildren nextPage = Launcher.this.mAppsCustomizeContent.getNextPageViewCellLayout();
                                                    if (nextPage != null) {
                                                        Launcher.this.mAppsCustomizeContent.snapToPage(Launcher.this.mAppsCustomizeContent.getCurrentPage() + 1);
                                                        Launcher.this.mAppsCustomizeContent.setCurFocusView(nextPage.getChildAt(0));
                                                        focus.invalidate();
                                                        Launcher.this.mAppsCustomizeContent.getCurFocusView().invalidate();
                                                        return;
                                                    }
                                                    return;
                                                } else {
                                                    Launcher.this.mAppsCustomizeContent.setCurFocusView(page.getChildAt(index + 1));
                                                    focus.invalidate();
                                                    Launcher.this.mAppsCustomizeContent.getCurFocusView().invalidate();
                                                    return;
                                                }
                                            } else if (iExtra == 5) {
                                                if (index >= 0) {
                                                    focus.callOnClick();
                                                    Launcher.this.mAppsCustomizeContent.setCurFocusView(focus);
                                                    return;
                                                }
                                                return;
                                            } else if (iExtra == 1) {
                                                EventUtils.sendBroadcastCanKeyExtra(Launcher.this, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 85);
                                            }
                                        } else {
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                }
                                if (Launcher.this.m_bVerticalPager) {
                                    if (Launcher.this.mState == State.WORKSPACE) {
                                        switch (iExtra) {
                                            case 5:
                                                Launcher.this.zxwOriginalMcuKeyEnter();
                                                break;
                                            case 7:
                                                Launcher.access$4110(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus < 0) {
                                                    int unused4 = Launcher.this.m_iCurrFocus = 0;
                                                    break;
                                                }
                                                break;
                                            case 8:
                                                Launcher.access$4108(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus >= Launcher.this.imgFocusList_src_icon.length) {
                                                    int unused5 = Launcher.this.m_iCurrFocus = Launcher.this.imgFocusList_src_icon.length - 1;
                                                    break;
                                                }
                                                break;
                                        }
                                        Launcher.this.refreshFocusView_Vertical();
                                    }
                                } else if (Launcher.this.m_iModeSet == 2 || Launcher.this.m_iModeSet == 4 || Launcher.this.m_iModeSet == 6) {
                                    if (Launcher.this.mState == State.WORKSPACE) {
                                        switch (iExtra) {
                                            case 5:
                                                Log.i(Launcher.TAG, "--->>> ksw kesaiwei_m_btnMusic 333");
                                                Launcher.this.zxwOriginalMcuKeyEnter_evo();
                                                break;
                                            case 7:
                                                Launcher.access$4110(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus < 0) {
                                                    int unused6 = Launcher.this.m_iCurrFocus = 0;
                                                    break;
                                                }
                                                break;
                                            case 8:
                                                Launcher.access$4108(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList_whats1.length + Launcher.this.imageViewFocusList_whats2.length) {
                                                    int unused7 = Launcher.this.m_iCurrFocus = (Launcher.this.imageViewFocusList_whats1.length + Launcher.this.imageViewFocusList_whats2.length) - 1;
                                                    break;
                                                }
                                                break;
                                        }
                                        Launcher.this.refreshFocusView_evo();
                                    }
                                } else if (Launcher.this.m_iModeSet == 1) {
                                    if (Launcher.this.m_b_ksw_audio_main_scroll) {
                                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                            boolean isJup_Add = false;
                                            boolean isJup_Sub = false;
                                            if (Launcher.ksw_3d_currentPosition != Launcher.this.ksw_3d_mLoopRotarySwitchView.getSelectItem()) {
                                                isAuto2 = false;
                                                float angle = Launcher.this.ksw_3d_mLoopRotarySwitchView.getAngle();
                                                double lastPositionRadians = ((double) (((180.0f + angle) - ((float) ((Launcher.ksw_3d_lastPosition * 360) / Launcher.this.ksw_3d_views.size()))) % 360.0f)) - ((double) (360 / Launcher.this.ksw_3d_views.size()));
                                                if (lastPositionRadians < 0.0d) {
                                                    lastPositionRadians += 360.0d;
                                                }
                                                Log.i("TAG", "onClick- lastPositionRadians=" + lastPositionRadians + ",lastPosition = " + Launcher.ksw_3d_lastPosition + ",angle = " + angle);
                                                if (lastPositionRadians == 324.0d) {
                                                    isJup_Add = true;
                                                }
                                                if (lastPositionRadians == 180.0d) {
                                                    isJup_Sub = true;
                                                }
                                            } else {
                                                isAuto2 = true;
                                                isJup_Add = false;
                                                isJup_Sub = false;
                                            }
                                            switch (iExtra) {
                                                case 5:
                                                    Launcher.this.ksw_3d_mLoopRotarySwitchView.getOnItemClickListener().onItemClick(Launcher.ksw_3d_currentPosition, (View) Launcher.this.ksw_3d_views.get(Launcher.ksw_3d_currentPosition));
                                                    return;
                                                case 7:
                                                    if (isJup_Sub) {
                                                        int unused8 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition - 4;
                                                    } else {
                                                        Launcher.access$810();
                                                    }
                                                    if (Launcher.ksw_3d_currentPosition < 0) {
                                                        int unused9 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition + Launcher.this.ksw_3d_views.size();
                                                    }
                                                    Launcher.this.add_sub(isAuto2);
                                                    return;
                                                case 8:
                                                    if (isJup_Add) {
                                                        int unused10 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition + 4;
                                                    } else {
                                                        Launcher.access$808();
                                                    }
                                                    if (Launcher.ksw_3d_currentPosition >= Launcher.this.ksw_3d_views.size()) {
                                                        int unused11 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition % Launcher.this.ksw_3d_views.size();
                                                    }
                                                    Launcher.this.add_sub(isAuto2);
                                                    return;
                                                default:
                                                    return;
                                            }
                                        }
                                    } else {
                                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                            switch (iExtra) {
                                                case 5:
                                                    Launcher.this.zxwOriginalMcuKeyEnter_audi();
                                                    break;
                                                case 7:
                                                    Launcher.access$4110(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus < 0) {
                                                        int unused12 = Launcher.this.m_iCurrFocus = 0;
                                                        break;
                                                    }
                                                    break;
                                                case 8:
                                                    Launcher.access$4108(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList.length) {
                                                        int unused13 = Launcher.this.m_iCurrFocus = Launcher.this.imageViewFocusList.length - 1;
                                                        break;
                                                    }
                                                    break;
                                            }
                                            Launcher.this.refreshFocusView();
                                        }
                                    }
                                } else if (Launcher.this.m_iModeSet == 3) {
                                    if (Launcher.this.m_b_ksw_audio_main_scroll) {
                                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                            boolean isJup_Add2 = false;
                                            boolean isJup_Sub2 = false;
                                            if (Launcher.ksw_3d_currentPosition != Launcher.this.ksw_3d_mLoopRotarySwitchView.getSelectItem()) {
                                                isAuto = false;
                                                float angle2 = Launcher.this.ksw_3d_mLoopRotarySwitchView.getAngle();
                                                double lastPositionRadians2 = ((double) (((180.0f + angle2) - ((float) ((Launcher.ksw_3d_lastPosition * 360) / Launcher.this.ksw_3d_views.size()))) % 360.0f)) - ((double) (360 / Launcher.this.ksw_3d_views.size()));
                                                if (lastPositionRadians2 < 0.0d) {
                                                    lastPositionRadians2 += 360.0d;
                                                }
                                                Log.i("TAG", "onClick- lastPositionRadians=" + lastPositionRadians2 + ",lastPosition = " + Launcher.ksw_3d_lastPosition + ",angle = " + angle2);
                                                if (lastPositionRadians2 == 324.0d) {
                                                    isJup_Add2 = true;
                                                }
                                                if (lastPositionRadians2 == 180.0d) {
                                                    isJup_Sub2 = true;
                                                }
                                            } else {
                                                isAuto = true;
                                                isJup_Add2 = false;
                                                isJup_Sub2 = false;
                                            }
                                            switch (iExtra) {
                                                case 5:
                                                    Launcher.this.ksw_3d_mLoopRotarySwitchView.getOnItemClickListener().onItemClick(Launcher.ksw_3d_currentPosition, (View) Launcher.this.ksw_3d_views.get(Launcher.ksw_3d_currentPosition));
                                                    return;
                                                case 7:
                                                    if (isJup_Sub2) {
                                                        int unused14 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition - 4;
                                                    } else {
                                                        Launcher.access$810();
                                                    }
                                                    if (Launcher.ksw_3d_currentPosition < 0) {
                                                        int unused15 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition + Launcher.this.ksw_3d_views.size();
                                                    }
                                                    Launcher.this.add_sub(isAuto);
                                                    return;
                                                case 8:
                                                    if (isJup_Add2) {
                                                        int unused16 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition + 4;
                                                    } else {
                                                        Launcher.access$808();
                                                    }
                                                    if (Launcher.ksw_3d_currentPosition >= Launcher.this.ksw_3d_views.size()) {
                                                        int unused17 = Launcher.ksw_3d_currentPosition = Launcher.ksw_3d_currentPosition % Launcher.this.ksw_3d_views.size();
                                                    }
                                                    Launcher.this.add_sub(isAuto);
                                                    return;
                                                default:
                                                    return;
                                            }
                                        }
                                    } else {
                                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                            switch (iExtra) {
                                                case 5:
                                                    Launcher.this.zxwOriginalMcuKeyEnter_audi_Q5();
                                                    break;
                                                case 7:
                                                    Launcher.access$4110(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus < 0) {
                                                        int unused18 = Launcher.this.m_iCurrFocus = 0;
                                                        break;
                                                    }
                                                    break;
                                                case 8:
                                                    Launcher.access$4108(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList.length) {
                                                        int unused19 = Launcher.this.m_iCurrFocus = Launcher.this.imageViewFocusList.length - 1;
                                                        break;
                                                    }
                                                    break;
                                            }
                                            Launcher.this.refreshFocusView_Q5();
                                        }
                                    }
                                } else if (Launcher.this.m_iModeSet == 5 || Launcher.this.m_iModeSet == 9 || Launcher.this.m_iModeSet == 10 || Launcher.this.m_iModeSet == 12) {
                                    Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                    if (Launcher.this.mState == State.WORKSPACE && Launcher.this.mCoverFlowView != null) {
                                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                        switch (iExtra) {
                                            case 5:
                                                Launcher.this.cheku_benchi_original_enter();
                                                return;
                                            case 7:
                                                Launcher.this.cheku_benchi_original_right();
                                                return;
                                            case 8:
                                                Launcher.this.cheku_benchi_original_left();
                                                return;
                                            default:
                                                return;
                                        }
                                    }
                                } else if (Launcher.this.m_iModeSet == 7 || Launcher.this.m_iModeSet == 20) {
                                    if (Launcher.this.mState == State.WORKSPACE) {
                                        switch (iExtra) {
                                            case 5:
                                                Launcher.this.zxwOriginalMcuKeyEnter_evo_id6();
                                                break;
                                            case 7:
                                                Launcher.access$4110(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus < 0) {
                                                    int unused20 = Launcher.this.m_iCurrFocus = 0;
                                                    break;
                                                }
                                                break;
                                            case 8:
                                                Launcher.access$4108(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList_whats1.length) {
                                                    int unused21 = Launcher.this.m_iCurrFocus = Launcher.this.imageViewFocusList_whats1.length - 1;
                                                    break;
                                                }
                                                break;
                                        }
                                        Launcher.this.refreshFocusView_evo_id6();
                                    }
                                } else if (Launcher.this.m_iModeSet == 8) {
                                    if (Launcher.this.mState == State.WORKSPACE) {
                                        Launcher.this.rl_apps.setVisibility(8);
                                        Launcher.this.rlApps1.setVisibility(0);
                                        Launcher.this.rlApps2.setVisibility(0);
                                        Launcher.this.rlApps3.setVisibility(0);
                                        Launcher.this.rlApps4.setVisibility(0);
                                        switch (iExtra) {
                                            case 5:
                                                Launcher.this.zxwOriginalMcuKeyEnter_audi_Q5();
                                                break;
                                            case 7:
                                                boolean unused22 = Launcher.this.bIsMcuKeyRemove = true;
                                                int currentItem = Launcher.this.mViewPage.getCurrentItem();
                                                Launcher.access$5810(Launcher.this);
                                                if (Launcher.this.m_iCurrFocus_chwy <= -1) {
                                                    int unused23 = Launcher.this.m_iCurrFocus_chwy = 13;
                                                    currentItem--;
                                                    Launcher.this.mViewPage.setCurrentItem(currentItem);
                                                    Launcher.this.mViewAdapter.notifyDataSetChanged();
                                                }
                                                if (Launcher.this.m_iCurrFocus_chwy == 6) {
                                                    Launcher.this.mViewPage.setCurrentItem(currentItem - 1);
                                                    Launcher.this.mViewAdapter.notifyDataSetChanged();
                                                    break;
                                                }
                                                break;
                                            case 8:
                                                boolean unused24 = Launcher.this.bIsMcuKeyRemove = true;
                                                int currentItem2 = Launcher.this.mViewPage.getCurrentItem();
                                                Log.i("TAG1", "viewpager - currentItem =" + currentItem2);
                                                Launcher.access$5808(Launcher.this);
                                                Log.i("TAG1", "viewpager - m_iCurrFocus_chwy =" + Launcher.this.m_iCurrFocus_chwy);
                                                if (Launcher.this.m_iCurrFocus_chwy == 7) {
                                                    currentItem2++;
                                                    Launcher.this.mViewPage.setCurrentItem(currentItem2);
                                                    Launcher.this.mViewAdapter.notifyDataSetChanged();
                                                }
                                                if (Launcher.this.m_iCurrFocus_chwy == 14) {
                                                    Launcher.this.mViewPage.setCurrentItem(currentItem2 + 1);
                                                    Launcher.this.mViewAdapter.notifyDataSetChanged();
                                                    int unused25 = Launcher.this.m_iCurrFocus_chwy = 0;
                                                    break;
                                                }
                                                break;
                                        }
                                        Launcher.this.refreshFocusView_CHWY();
                                    }
                                } else if (Launcher.this.m_iModeSet != 11 && Launcher.this.m_iModeSet != 14) {
                                    if (Launcher.this.m_iModeSet == 13) {
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            switch (iExtra) {
                                                case 5:
                                                    Launcher.this.zxwOriginalMcuKeyEnter_normal_1920x720();
                                                    break;
                                                case 7:
                                                    Log.i(Launcher.TAG, "onReceive: jelly-ZXW_ORIGINAL_MCU_KEY_LEFT_HANDED");
                                                    Launcher.access$4110(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus < 0) {
                                                        int unused26 = Launcher.this.m_iCurrFocus = 0;
                                                        break;
                                                    }
                                                    break;
                                                case 8:
                                                    Log.i(Launcher.TAG, "onReceive: jelly-ZXW_ORIGINAL_MCU_KEY_RIGHT_HANDED");
                                                    Launcher.access$4108(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus >= Launcher.this.imgFocusList.length * 2) {
                                                        int unused27 = Launcher.this.m_iCurrFocus = (Launcher.this.imgFocusList.length * 2) - 1;
                                                        break;
                                                    }
                                                    break;
                                            }
                                            Launcher.this.refreshFocusView_normal_1920x720();
                                        }
                                    } else if (Launcher.this.m_iModeSet == 15 || Launcher.this.m_iModeSet == 16) {
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            switch (iExtra) {
                                                case 1:
                                                    Log.i(Launcher.TAG, "onReceive: mState =  " + Launcher.this.mState);
                                                    if (!Launcher.this.bInLeftFocus) {
                                                        int unused28 = Launcher.this.m_iCurrLeftFocus = 0;
                                                        boolean unused29 = Launcher.this.bInLeftFocus = true;
                                                        int unused30 = Launcher.this.m_iLastCurrFocus = Launcher.m_iLastPageCurrFocus * 2;
                                                        int unused31 = Launcher.this.m_iCurrFocus = -1;
                                                        break;
                                                    }
                                                    break;
                                                case 2:
                                                    if (Launcher.this.bInLeftFocus) {
                                                        boolean unused32 = Launcher.this.bInLeftFocus = false;
                                                        int unused33 = Launcher.this.m_iCurrLeftFocus = -1;
                                                        int unused34 = Launcher.this.m_iCurrFocus = Launcher.this.m_iLastCurrFocus;
                                                        break;
                                                    }
                                                    break;
                                                case 5:
                                                    if (!Launcher.this.bInLeftFocus) {
                                                        Launcher.this.zxwOriginalMcuKeyEnter_evo_id7();
                                                        break;
                                                    } else {
                                                        Launcher.this.zxwOriginalMcuKeyEnter_evo_id7_leftFocus();
                                                        break;
                                                    }
                                                case 7:
                                                    if (!Launcher.this.bInLeftFocus) {
                                                        Launcher.access$4110(Launcher.this);
                                                        if (Launcher.this.m_iCurrFocus < 0) {
                                                            int unused35 = Launcher.this.m_iCurrFocus = 0;
                                                            break;
                                                        }
                                                    } else {
                                                        Launcher.access$6310(Launcher.this);
                                                        if (Launcher.this.m_iCurrLeftFocus < 0) {
                                                            int unused36 = Launcher.this.m_iCurrLeftFocus = 0;
                                                            break;
                                                        }
                                                    }
                                                    break;
                                                case 8:
                                                    if (!Launcher.this.bInLeftFocus) {
                                                        Launcher.access$4108(Launcher.this);
                                                        if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList_whats1.length) {
                                                            int unused37 = Launcher.this.m_iCurrFocus = Launcher.this.imageViewFocusList_whats1.length - 1;
                                                            break;
                                                        }
                                                    } else {
                                                        Launcher.access$6308(Launcher.this);
                                                        if (Launcher.this.m_iCurrLeftFocus >= Launcher.this.imageViewLeftFocusList.length) {
                                                            int unused38 = Launcher.this.m_iCurrLeftFocus = Launcher.this.imageViewLeftFocusList.length - 1;
                                                            break;
                                                        }
                                                    }
                                                    break;
                                            }
                                            Launcher.this.refreshLeftFocusView();
                                            Launcher.this.refreshFocusView_evo_id7();
                                        }
                                    } else {
                                        Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                        if (Launcher.this.mState == State.WORKSPACE) {
                                            Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                            switch (iExtra) {
                                                case 5:
                                                    Launcher.this.zxwOriginalMcuKeyEnter();
                                                    break;
                                                case 7:
                                                    Launcher.access$4110(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus < 0) {
                                                        int unused39 = Launcher.this.m_iCurrFocus = 0;
                                                        break;
                                                    }
                                                    break;
                                                case 8:
                                                    Launcher.access$4108(Launcher.this);
                                                    if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList.length) {
                                                        int unused40 = Launcher.this.m_iCurrFocus = Launcher.this.imageViewFocusList.length - 1;
                                                        break;
                                                    }
                                                    break;
                                            }
                                            Launcher.this.refreshFocusView();
                                        }
                                    }
                                }
                            } else if (LauncherApplication.m_iUITypeVer == 101) {
                                if (Launcher.this.mState == State.WORKSPACE) {
                                    switch (iExtra) {
                                        case 5:
                                            Launcher.this.zxwOriginalMcuKeyEnter_normal_1920x720();
                                            break;
                                        case 7:
                                            Log.i(Launcher.TAG, "onReceive: jelly-ZXW_ORIGINAL_MCU_KEY_LEFT_HANDED");
                                            Launcher.access$4110(Launcher.this);
                                            if (Launcher.this.m_iCurrFocus < 0) {
                                                int unused41 = Launcher.this.m_iCurrFocus = 0;
                                                break;
                                            }
                                            break;
                                        case 8:
                                            Log.i(Launcher.TAG, "onReceive: jelly-ZXW_ORIGINAL_MCU_KEY_RIGHT_HANDED");
                                            Launcher.access$4108(Launcher.this);
                                            if (Launcher.this.imgFocusList != null && Launcher.this.m_iCurrFocus >= Launcher.this.imgFocusList.length * 2) {
                                                int unused42 = Launcher.this.m_iCurrFocus = (Launcher.this.imgFocusList.length * 2) - 1;
                                                break;
                                            }
                                    }
                                    Launcher.this.refreshFocusView_normal_1920x720();
                                }
                            } else {
                                Log.i(Launcher.TAG, "--->>> KSW FOCUS 111");
                                if (Launcher.this.mState == State.WORKSPACE) {
                                    Log.i(Launcher.TAG, "--->>> KSW FOCUS 222");
                                    switch (iExtra) {
                                        case 5:
                                            Launcher.this.zxwOriginalMcuKeyEnter();
                                            break;
                                        case 7:
                                            Launcher.access$4110(Launcher.this);
                                            if (Launcher.this.m_iCurrFocus < 0) {
                                                int unused43 = Launcher.this.m_iCurrFocus = 0;
                                                break;
                                            }
                                            break;
                                        case 8:
                                            Launcher.access$4108(Launcher.this);
                                            if (Launcher.this.m_iCurrFocus >= Launcher.this.imageViewFocusList.length) {
                                                int unused44 = Launcher.this.m_iCurrFocus = Launcher.this.imageViewFocusList.length - 1;
                                                break;
                                            }
                                            break;
                                    }
                                    Launcher.this.refreshFocusView();
                                }
                            }
                        }
                    }
                } else if (action.equals(EventUtils.ZXW_REFRESH_WALL_LOGO)) {
                    Log.i(Launcher.TAG, "--->>> EventUtils.ZXW_REFRESH_WALL_LOGO");
                    Launcher.this.m_iLogoIndex = Launcher.this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SYS_SETLOGO_INDEX, Launcher.this.m_iLogoIndex);
                } else if (action.equals(EventUtils.ZXW_RESET_WALLPAGER_CHANGE)) {
                } else {
                    if (action.equals(EventUtils.CHEKU_WEATHER_SETCITYCODE)) {
                        try {
                            Launcher.this.startActivity(new Intent(Launcher.this, WeatherSetActivity.class));
                        } catch (Exception e2) {
                        }
                    } else if (action.equals(EventUtils.CHEKU_WEATHER_INIT_REFRESH_WEATHER)) {
                        Launcher.this.updateWeatherInfor();
                    } else if (action.equals(EventUtils.MAISILUO_ZXW_SHOW_ALL_APPS)) {
                        Launcher.this.showAllApps(true);
                    } else if (action.equals(EventUtils.KSW_ZXW_BT_CONNECED_SHOW_VIEW)) {
                        boolean is_Bt_Connneced = intent.getBooleanExtra(EventUtils.KSW_ZXW_BT_CONNECED_SHOW_VIEW_DATA, false);
                        Log.i(Launcher.TAG, "--->>> is_Bt_Connneced = " + is_Bt_Connneced);
                        Launcher.this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_RECORD_BT_CONNECT_MENU, "" + (is_Bt_Connneced ? 1 : 0));
                        Launcher.this.ksw_set_bt_connect_menu(is_Bt_Connneced);
                    } else if (action.equals(EventUtils.CHEKU_BOTTOM_KEY_PARK)) {
                    } else {
                        if (action.equals(EventUtils.ZXW_SENDBROADCAST8902MOD)) {
                            int iExtra2 = intent.getIntExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_EXTRA, 0);
                            Log.i(Launcher.TAG, "onReceive: ZXW_SENDBROADCAST8902MOD-iExtra = " + iExtra2);
                            switch (iExtra2) {
                                case 13:
                                    Launcher.this.ksw_refresh_A4L_left_show();
                                    return;
                                case 14:
                                    Launcher.this.ksw_refresh_A4L_right_show();
                                    return;
                                case 16:
                                    boolean unused45 = Launcher.this.bKesaiweiBPark = intent.getBooleanExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_shousha, false);
                                    boolean unused46 = Launcher.this.bKesaiweiBelt = intent.getBooleanExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_anquandai, false);
                                    Launcher.this.mMyhandler.removeMessages(16);
                                    Launcher.this.mMyhandler.sendEmptyMessage(16);
                                    return;
                                case 17:
                                    Launcher.this.mMyhandler.removeMessages(6);
                                    Launcher.this.mMyhandler.sendEmptyMessage(6);
                                    return;
                                case 19:
                                    Launcher.this.mMyhandler.removeMessages(7);
                                    Launcher.this.mMyhandler.sendEmptyMessage(7);
                                    return;
                                case 20:
                                    Launcher.this.mMyhandler.removeMessages(8);
                                    Launcher.this.mMyhandler.sendEmptyMessage(8);
                                    return;
                                case 21:
                                    Log.i(Launcher.TAG, "--->>> ksw_old_bmw_x1_original 222");
                                    Launcher.this.mMyhandler.removeMessages(9);
                                    Launcher.this.mMyhandler.sendEmptyMessage(9);
                                    return;
                                case 25:
                                    Launcher.ksw_m_i_audio_right_fadongjizhuansu = intent.getIntExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_fadongjizhuansu, 0);
                                    Launcher.ksw_m_i_audio_right_ShunShiSuDu = intent.getIntExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_ShunShiSuDu, 0);
                                    Launcher.ksw_m_i_audio_right_ShunShiSuDu *= 20;
                                    Log.i(Launcher.TAG, "onReceive: ksw_m_i_audio_right_fadongjizhuansu = " + Launcher.ksw_m_i_audio_right_fadongjizhuansu);
                                    Log.i(Launcher.TAG, "onReceive: ksw_m_i_audio_right_ShunShiSuDu = " + Launcher.ksw_m_i_audio_right_ShunShiSuDu);
                                    String unused47 = Launcher.this.ksw_m_str_audio_right_xushilicheng = intent.getStringExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_xushilicheng);
                                    String unused48 = Launcher.this.ksw_m_str_audio_right_wendu = intent.getStringExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_huanjinwendu);
                                    Launcher.this.mMyhandler.removeMessages(10);
                                    Launcher.this.mMyhandler.sendEmptyMessage(10);
                                    String unused49 = Launcher.this.ksw_m_str_xushilichengg = Launcher.this.ksw_m_str_audio_right_xushilicheng;
                                    String unused50 = Launcher.this.ksw_m_str_youliang = intent.getStringExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_youLiang);
                                    Log.i(Launcher.TAG, "onReceive: ksw_m_str_xushilichengg = " + Launcher.this.ksw_m_str_xushilichengg);
                                    Log.i(Launcher.TAG, "onReceive: ksw_m_str_youliang = " + Launcher.this.ksw_m_str_youliang);
                                    Launcher.this.mMyhandler.removeMessages(11);
                                    Launcher.this.mMyhandler.sendEmptyMessage(11);
                                    return;
                                default:
                                    return;
                            }
                        } else if (action.equals(EventUtils.ZXW_CAN_START_UP_APPS)) {
                            Launcher.this.showAllApps(true);
                        }
                    }
                }
            }
        }
    };
    private Rect mRectForFolderAnimation = new Rect();
    private final int mRestoreScreenOrientationDelay = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    private boolean mRestoring;
    private Bundle mSavedInstanceState;
    private Bundle mSavedState;
    /* access modifiers changed from: private */
    public SearchDropTargetBar mSearchDropTargetBar;
    /* access modifiers changed from: private */
    public SharedPreferences mSharedPrefs;
    /* access modifiers changed from: private */
    public State mState = State.WORKSPACE;
    /* access modifiers changed from: private */
    public AnimatorSet mStateAnimation;
    private final ArrayList<Integer> mSynchronouslyBoundPages = new ArrayList<>();
    /* access modifiers changed from: private */
    public SysProviderOpt mSysProviderOpt = null;
    private TextView mTimeYearMD = null;
    private ImageView mTimerHourH = null;
    private ImageView mTimerHourL = null;
    private ImageView mTimerMinH = null;
    private ImageView mTimerMinL = null;
    private TextView mTimerWeek = null;
    private int[] mTmpAddItemCellCoordinates = new int[2];
    private TextView mTvCurTime = null;
    private TextView mTwChineseMonthDay = null;
    private TextView mTwChineseYear = null;
    private TextView mTwCurDataTimeAMPM = null;
    /* access modifiers changed from: private */
    public boolean mUserPresent = true;
    /* access modifiers changed from: private */
    public MyViewPageAdapter mViewAdapter;
    /* access modifiers changed from: private */
    public ViewPager mViewPage;
    /* access modifiers changed from: private */
    public ViewPager mViewPager;
    private boolean mVisible = false;
    private ImageView mVwtmPoint = null;
    private boolean mWaitingForResult;
    private BubbleTextView mWaitingForResume;
    /* access modifiers changed from: private */
    public IWeatherService mWeatherSrv = null;
    private ServiceConnection mWeatherSrvSc = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            IWeatherService unused = Launcher.this.mWeatherSrv = IWeatherService.Stub.asInterface(service);
            Launcher.this.mMyhandler.sendEmptyMessage(1);
            if (Launcher.this.app != null) {
                Launcher.this.app.setmWeatherSrv(Launcher.this.mWeatherSrv);
            }
            Log.i(Launcher.TAG, "mWeatherSrvSc Connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            IWeatherService unused = Launcher.this.mWeatherSrv = null;
            Log.i(Launcher.TAG, "mWeatherSrvSc Disconnected");
        }
    };
    private final ContentObserver mWidgetObserver = new AppWidgetResetObserver();
    /* access modifiers changed from: private */
    public ArrayList<Object> mWidgetsAndShortcuts;
    /* access modifiers changed from: private */
    public HashMap<View, AppWidgetProviderInfo> mWidgetsToAdvance = new HashMap<>();
    /* access modifiers changed from: private */
    public Workspace mWorkspace;
    private Drawable mWorkspaceBackgroundDrawable;
    private boolean mWorkspaceLoading = true;
    /* access modifiers changed from: private */
    public boolean m_bVerticalPager = true;
    private boolean m_b_have_AUX = false;
    private int m_b_have_DVD = 0;
    private boolean m_b_have_TV = false;
    private boolean m_b_ksw_Foreign_browser = false;
    private boolean m_b_ksw_Original_car_video_display = true;
    private boolean m_b_ksw_Support_dashboard = true;
    /* access modifiers changed from: private */
    public boolean m_b_ksw_audio_main_scroll = true;
    /* access modifiers changed from: private */
    public boolean m_b_ksw_init_accOn_focus_index = true;
    byte[] m_bydata = {EventUtils.MCU_KEY_MUSIC, 0, EventUtils.MCU_KEY1_3, 2, 5, 0};
    public int m_iBTTypeVer = 1;
    private int m_iCanbustype = 0;
    private int m_iCarCanbusName_ID = 0;
    private int m_iCarstype_ID = 0;
    protected int m_iCurrDim = 0;
    /* access modifiers changed from: private */
    public int m_iCurrFocus = -1;
    /* access modifiers changed from: private */
    public int m_iCurrFocus_chwy = -1;
    /* access modifiers changed from: private */
    public int m_iCurrLeftFocus = -1;
    /* access modifiers changed from: private */
    public int m_iLastCurrFocus = 0;
    protected int m_iLogoIndex = 0;
    public int m_iModeSet = 0;
    /* access modifiers changed from: private */
    public int m_iPageCurrFocus = 0;
    private int m_iUSBDvr = 0;
    public int m_iUiIndex = 2;
    private int m_i_have_DVR_index = 0;
    /* access modifiers changed from: private */
    public int m_i_ksw_evo_id6_main_interface_index = 0;
    private int m_i_ksw_evo_main_interface_index = 0;
    /* access modifiers changed from: private */
    public int m_i_ksw_jlr_main_interface_index = 0;
    /* access modifiers changed from: private */
    public boolean m_onResum = false;
    private TextView mtvCityName = null;
    private TextView mtvUpdateInfor = null;
    private TextView mtvWeatherDay = null;
    private TextView mtvWeatherInfor = null;
    private TextView mtvWeatherTemp = null;
    private TextView mtvWeatherTitle = null;
    private ImageView mvwWeatherIcon = null;
    private MyQAnalogClock2 myClockSpeed;
    String[] name = new String[0];
    private RelativeLayout reDaoHangDi;
    /* access modifiers changed from: private */
    public RelativeLayout rlApps1;
    /* access modifiers changed from: private */
    public RelativeLayout rlApps2;
    /* access modifiers changed from: private */
    public RelativeLayout rlApps3;
    /* access modifiers changed from: private */
    public RelativeLayout rlApps4;
    /* access modifiers changed from: private */
    public RelativeLayout rl_apps;
    private String strNextRouadName = "";
    TextView[] textViewLine_KSW_AD;
    int[] textView_KSW_AD;
    /* access modifiers changed from: private */
    public TextView tvAnQuanDai;
    /* access modifiers changed from: private */
    public TextView tvShouSha;
    private TextView tvShunShiSuDu;
    private TextView tvWenDu;
    private TextView tvXuHangLiCheng;
    private TextView tvYouLiang;
    private TextView tv_audio_right_fadongjizhuansu = null;
    private TextView tv_audio_right_wendu = null;
    private TextView tv_audio_right_xushilicheng = null;
    private String validModeAblumInfor = "";
    private String validModeArtistInfor = "";
    private int validModeCurTime;
    private String validModeTitleInfor = "";
    private int validModeTotTime;
    public String xml_client;
    /* access modifiers changed from: private */
    public Context zxw_context = null;

    private enum State {
        NONE,
        WORKSPACE,
        APPS_CUSTOMIZE,
        APPS_CUSTOMIZE_SPRING_LOADED
    }

    static /* synthetic */ int access$4108(Launcher x0) {
        int i = x0.m_iCurrFocus;
        x0.m_iCurrFocus = i + 1;
        return i;
    }

    static /* synthetic */ int access$4110(Launcher x0) {
        int i = x0.m_iCurrFocus;
        x0.m_iCurrFocus = i - 1;
        return i;
    }

    static /* synthetic */ int access$5808(Launcher x0) {
        int i = x0.m_iCurrFocus_chwy;
        x0.m_iCurrFocus_chwy = i + 1;
        return i;
    }

    static /* synthetic */ int access$5810(Launcher x0) {
        int i = x0.m_iCurrFocus_chwy;
        x0.m_iCurrFocus_chwy = i - 1;
        return i;
    }

    static /* synthetic */ int access$6308(Launcher x0) {
        int i = x0.m_iCurrLeftFocus;
        x0.m_iCurrLeftFocus = i + 1;
        return i;
    }

    static /* synthetic */ int access$6310(Launcher x0) {
        int i = x0.m_iCurrLeftFocus;
        x0.m_iCurrLeftFocus = i - 1;
        return i;
    }

    static /* synthetic */ int access$808() {
        int i = ksw_3d_currentPosition;
        ksw_3d_currentPosition = i + 1;
        return i;
    }

    static /* synthetic */ int access$810() {
        int i = ksw_3d_currentPosition;
        ksw_3d_currentPosition = i - 1;
        return i;
    }

    private static class PendingAddArguments {
        int cellX;
        int cellY;
        long container;
        Intent intent;
        int requestCode;
        int screen;

        private PendingAddArguments() {
        }
    }

    private static boolean isPropertyEnabled(String propertyName) {
        return Log.isLoggable(propertyName, 2);
    }

    private void initSystemService() {
        this.m_iBTTypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SYS_BT_TYPE_KEY, this.m_iBTTypeVer);
        Log.i(TAG, "m_iBTTypeVer  = " + this.m_iBTTypeVer);
        if (LauncherApplication.m_iUITypeVer == 41) {
            if (this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_RECORD_BT_OFF, 0) != 1) {
                if (this.m_iBTTypeVer == 3) {
                    SystemProperties.set("ctl.start", "gocsdk");
                    this.mMyhandler.sendEmptyMessageDelayed(3, 6000);
                } else if (this.m_iBTTypeVer == 1) {
                    SystemProperties.set("ctl.start", "gocsdk_bc5");
                    this.mMyhandler.sendEmptyMessageDelayed(3, 6000);
                } else if (this.m_iBTTypeVer == 5) {
                    if (Build.VERSION.SDK_INT <= 19) {
                        SystemProperties.set("ctl.start", "blueware");
                    } else {
                        SystemProperties.set("ctl.start", "feasycom");
                    }
                    this.mMyhandler.sendEmptyMessageDelayed(3, 6000);
                } else if (this.m_iBTTypeVer == 6) {
                    SystemProperties.set("ctl.start", "ivt_bc5");
                    this.mMyhandler.sendEmptyMessageDelayed(3, 6000);
                } else {
                    SystemProperties.set("ctl.start", "gocsdk");
                    this.mMyhandler.sendEmptyMessageDelayed(3, 6000);
                }
            }
        } else if (this.m_iBTTypeVer == 2 || this.m_iBTTypeVer == 3 || this.m_iBTTypeVer == 4) {
            SystemProperties.set("ctl.start", "gocsdk");
            this.mMyhandler.sendEmptyMessageDelayed(3, 6000);
        } else {
            startService(new Intent(BT_SRV_NAME).setPackage(EventUtils.BT_MODE_PACKAGE_NAME));
        }
        if (LauncherApplication.m_iUITypeVer == 41) {
            Log.i(TAG, "--->>> ksw sdobdsdk");
            SystemProperties.set("ctl.start", "sdobdsdk");
        }
        startService(new Intent(EVENT_STANDBY_SRV_NAME).setPackage("com.szchoiceway.standbyunlock.service"));
        startService(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"));
        startService(new Intent(UPDATE_SRV_NAME).setPackage("com.szchoiceway.appwidget"));
        startService(new Intent(WEATHER_SRV_NAME).setPackage("com.szchoiceway.appwidget"));
        startService(new Intent(VOLWND_SRV_NAME).setPackage("com.szchoiceway.volwnd"));
        if (LauncherApplication.m_iUITypeVer == 41) {
            startService(new Intent("com.szchoiceway.dvdplayer.DVDService").setPackage("com.szchoiceway.dvdplayer"));
        }
        this.m_iCurrDim = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_DIM_LIGHT_KEY, this.m_iCurrDim);
        this.m_iCanbustype = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_Canbustype_KEY, this.m_iCanbustype);
        this.m_iCarstype_ID = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_Carstype_ID_KEY, this.m_iCarstype_ID);
        this.m_iCarCanbusName_ID = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_CarCanbusName_ID_KEY, this.m_iCarCanbusName_ID);
        this.m_iLogoIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SYS_SETLOGO_INDEX, this.m_iLogoIndex);
        this.m_iUSBDvr = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USB_DVR_MODE, this.m_iUSBDvr);
        Log.i(TAG, "m_iCanbustype  = " + this.m_iCanbustype);
        Log.i(TAG, "m_iCarstype_ID  = " + this.m_iCarstype_ID);
        Log.i(TAG, "m_iCarCanbusName_ID  = " + this.m_iCarCanbusName_ID);
        Log.i(TAG, "--->>> m_iLogoIndex = " + this.m_iLogoIndex);
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(EventUtils.CANBUS_MODE_PACKAGE_NAME, CAMERA_CAN_SRV_NAME));
        startService(intent);
    }

    /* access modifiers changed from: protected */
    public void setTaskDescriptionBar() {
        if (Build.VERSION.SDK_INT >= 21) {
            setTaskDescription(new ActivityManager.TaskDescription(""));
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        this.app = (LauncherApplication) getApplication();
        this.mSysProviderOpt = this.app.getProvider();
        this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
        this.m_b_ksw_audio_main_scroll = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_AUDIO_MAIN_SCROLL, this.m_b_ksw_audio_main_scroll);
        this.m_i_ksw_evo_main_interface_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_EVO_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_main_interface_index);
        this.m_i_ksw_evo_id6_main_interface_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_EVO_ID6_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_id6_main_interface_index);
        this.m_i_ksw_jlr_main_interface_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_JLR_MAIN_INTERFACE_INDEX, this.m_i_ksw_jlr_main_interface_index);
        Log.i(TAG, "onCreate: m_i_ksw_evo_main_interface_index = " + this.m_i_ksw_evo_main_interface_index);
        Log.i(TAG, "onCreate: m_i_ksw_evo_id6_main_interface_index = " + this.m_i_ksw_evo_id6_main_interface_index);
        Log.i(TAG, "onCreate: m_i_ksw_jlr_main_interface_index = " + this.m_i_ksw_jlr_main_interface_index);
        this.m_b_have_AUX = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_HAVE_AUX, this.m_b_have_AUX);
        this.m_b_have_DVD = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_HAVE_DVD, this.m_b_have_DVD);
        this.m_b_have_TV = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_HAVE_TV, this.m_b_have_TV);
        Log.i(TAG, "onCreate: m_b_have_TV = " + this.m_b_have_TV);
        this.m_i_have_DVR_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_RECORD_DVR, this.m_i_have_DVR_index);
        this.ksw_m_b_Focus_image_zoom_evo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM, this.ksw_m_b_Focus_image_zoom_evo);
        this.m_b_ksw_Support_dashboard = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_SUPPORT_DASHBOARD, this.m_b_ksw_Support_dashboard);
        this.m_b_ksw_Foreign_browser = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_FOREIGN_BROWSER, this.m_b_ksw_Foreign_browser);
        if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
            this.ksw_m_b_Focus_image_zoom_evo = true;
        } else if (this.m_i_ksw_evo_main_interface_index == 4 || this.m_i_ksw_evo_main_interface_index == 5) {
            this.ksw_m_b_Focus_image_zoom_evo = false;
        }
        this.mSysProviderOpt.updateRecord(SysProviderOpt.KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM, "" + (this.ksw_m_b_Focus_image_zoom_evo ? 1 : 0));
        Log.e(TAG, "=========app.m_iUITypeVer==============" + LauncherApplication.m_iUITypeVer);
        if (LauncherApplication.m_iUITypeVer == 41) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
            this.xml_client = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_FACTORY_SET_CLIENT);
            Log.i(TAG, "onCreate: metrics.widthPixels = " + metrics.widthPixels + ",metrics.heightPixels = " + metrics.heightPixels);
            if (metrics.widthPixels == 1024 && metrics.heightPixels == EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT) {
                if (this.m_iModeSet == 5) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "5");
                    Utilities.KSW_iModeSet(5, this);
                } else if (this.m_iModeSet == 6) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "6");
                    Utilities.KSW_iModeSet(6, this);
                } else if (this.m_iModeSet == 14) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "14");
                    Utilities.KSW_iModeSet(14, this);
                } else {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "3");
                    Utilities.KSW_iModeSet(3, this);
                }
                if (this.m_b_ksw_audio_main_scroll) {
                    this.ksw_3d_width = getWindowManager().getDefaultDisplay().getWidth();
                }
            } else if (metrics.widthPixels == 1280 && metrics.heightPixels == 480) {
                if (this.m_iModeSet == 3 || this.m_iModeSet == 5 || this.m_iModeSet == 6 || this.m_iModeSet == 9 || this.m_iModeSet == 12 || this.m_iModeSet == 13 || this.m_iModeSet == 14 || this.m_iModeSet == 16) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "0");
                }
            } else if (metrics.widthPixels == 800 && metrics.heightPixels == 480) {
                if (this.m_iModeSet == 9) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "9");
                    Utilities.KSW_iModeSet(9, this);
                } else {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "9");
                    Utilities.KSW_iModeSet(9, this);
                }
            } else if (metrics.widthPixels == 1280 && metrics.heightPixels == 640) {
                if (this.m_iModeSet == 12) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "12");
                    Utilities.KSW_iModeSet(12, this);
                } else {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "12");
                    Utilities.KSW_iModeSet(12, this);
                }
            } else if (metrics.widthPixels == 1920 && metrics.heightPixels == 720) {
                if (this.m_iModeSet == 13) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "13");
                    Utilities.KSW_iModeSet(13, this);
                } else if (this.m_iModeSet == 16) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "16");
                    Utilities.KSW_iModeSet(16, this);
                } else {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "16");
                    Utilities.KSW_iModeSet(16, this);
                }
            }
            this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
            Log.i(TAG, "onCreate: m_iModeSet = " + this.m_iModeSet);
            LauncherApplication.set_m_iModeSet(this.m_iModeSet);
            if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6 || this.m_iModeSet == 7 || this.m_iModeSet == 8 || this.m_iModeSet == 9) {
                SystemProperties.set("persist.sys.systemui.type", "1");
                if (this.m_iModeSet == 8) {
                    setTheme(R.style.Kesaiwei_Theme);
                }
            } else if (this.m_iModeSet == 10 || this.m_iModeSet == 12) {
                SystemProperties.set("persist.sys.systemui.type", "2");
            } else if (this.m_iModeSet == 11) {
                if (this.m_i_ksw_jlr_main_interface_index == 1) {
                    SystemProperties.set("persist.sys.systemui.type", "2");
                } else {
                    setTheme(R.style.Kesaiwei_Theme);
                }
            } else if (this.m_iModeSet == 13) {
                SystemProperties.set("persist.sys.systemui.type", "3");
                setTheme(R.style.Kesaiwei_Theme);
            } else {
                if (this.m_b_ksw_audio_main_scroll) {
                    this.ksw_3d_width = getWindowManager().getDefaultDisplay().getWidth();
                }
                SystemProperties.set("persist.sys.systemui.type", "0");
                if (!(this.m_iModeSet == 1 || this.m_iModeSet == 3 || this.m_iModeSet == 5 || this.m_iModeSet == 14 || this.m_iModeSet == 15 || this.m_iModeSet == 16 || this.m_iModeSet == 20)) {
                    setTheme(R.style.Kesaiwei_Theme);
                }
            }
        } else if (LauncherApplication.m_iUITypeVer == 101) {
            SystemProperties.set("persist.sys.systemui.type", "3");
            setTheme(R.style.Kesaiwei_Theme);
            this.m_iUiIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE_INDEX, this.m_iUiIndex);
        }
        super.onCreate(savedInstanceState);
        if (this.m_iModeSet == 1 || this.m_iModeSet == 3 || this.m_iModeSet == 5) {
            getWindow().addFlags(256);
            getWindow().addFlags(512);
            getWindow().addFlags(67108864);
        }
        this.zxw_context = this;
        this.mSharedPrefs = getSharedPreferences(LauncherApplication.getSharedPreferencesKey(), 0);
        this.mModel = this.app.setLauncher(this);
        this.mIconCache = this.app.getIconCache();
        this.mDragController = new DragController(this);
        this.mInflater = getLayoutInflater();
        this.mAppWidgetManager = AppWidgetManager.getInstance(this);
        this.mAppWidgetHost = new LauncherAppWidgetHost(this, 1024);
        this.mAppWidgetHost.startListening();
        this.mPaused = false;
        checkForLocaleChange();
        getWindow().getDecorView().setSystemUiVisibility(1024);
        if (this.app != null) {
            if (!this.m_b_have_AUX || (!(this.m_b_have_DVD == 1 || this.m_b_have_DVD == 2) || !this.m_b_have_TV)) {
                this.m_bVerticalPager = false;
            } else {
                this.m_bVerticalPager = true;
            }
            if (this.m_iModeSet != 0) {
                this.m_bVerticalPager = false;
            }
            Log.i(TAG, "--->>> app.m_iUITypeVer ==" + LauncherApplication.m_iUITypeVer);
            if (LauncherApplication.m_iUITypeVer == 41) {
                if (this.m_bVerticalPager) {
                    setContentView(R.layout.launcher_kesaiwei_1280x480_verticalviewpaper);
                } else if (this.m_iModeSet == 1) {
                    if (this.m_b_ksw_audio_main_scroll) {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_audi_3d);
                    } else {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_audi);
                    }
                } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4) {
                    if (this.m_i_ksw_evo_main_interface_index == 2) {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo_xhd);
                    } else if (this.m_i_ksw_evo_main_interface_index == 3) {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo_xhd_2);
                    } else {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo);
                    }
                } else if (this.m_iModeSet == 6) {
                    setContentView(R.layout.launcher_kesaiwei_1024x600_baoma);
                } else if (this.m_iModeSet == 3) {
                    if (this.m_b_ksw_audio_main_scroll) {
                        setContentView(R.layout.launcher_kesaiwei_1024x600_audi_q5_3d);
                    } else {
                        setContentView(R.layout.launcher_kesaiwei_1024x600_audi_q5);
                    }
                } else if (this.m_iModeSet == 5) {
                    setContentView(R.layout.launcher_kesaiwei_1024x600_benchi);
                } else if (this.m_iModeSet == 7) {
                    getWindow().addFlags(67108864);
                    if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo_als);
                    } else {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo_id6);
                    }
                } else if (this.m_iModeSet == 8) {
                    setContentView(R.layout.launcher_kesaiwei_1280x480_audi_q5);
                } else if (this.m_iModeSet == 9) {
                    getWindow().addFlags(67108864);
                    if ("XinCheng".equalsIgnoreCase(this.xml_client)) {
                        setContentView(R.layout.launcher_kesaiwei_800x480_xuefulan);
                    } else {
                        setContentView(R.layout.launcher_kesaiwei_800x480_benchi);
                    }
                } else if (this.m_iModeSet == 10) {
                    setContentView(R.layout.launcher_kesaiwei_1280x480_benchi);
                } else if (this.m_iModeSet == 11) {
                    if (this.m_i_ksw_jlr_main_interface_index == 1) {
                        getWindow().addFlags(67108864);
                        setContentView(R.layout.launcher_kesaiwei_1280x480_jlr_ui2);
                    } else {
                        setContentView(R.layout.launcher_kesaiwei_1280x480_jlr);
                    }
                } else if (this.m_iModeSet == 12) {
                    setContentView(R.layout.launcher_kesaiwei_1280x640_benchi);
                } else if (this.m_iModeSet == 13) {
                    setContentView(R.layout.launcher_1920x720);
                } else if (this.m_iModeSet == 14) {
                    setContentView(R.layout.launcher_kesaiwei_1024x600_chuanqi_cusp_ui2);
                } else if (this.m_iModeSet == 15) {
                    setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo_id7);
                } else if (this.m_iModeSet == 16) {
                    setContentView(R.layout.launcher_kesaiwei_1920x720_bmw_evo_id7);
                } else if (this.m_iModeSet == 20) {
                    setContentView(R.layout.launcher_kesaiwei_1280x480_bmw_evo_id6_new);
                } else {
                    setContentView(R.layout.launcher_kesaiwei_1280x480);
                }
            } else if (LauncherApplication.m_iUITypeVer == 101) {
                this.bHaveDsp = EventUtils.getInstallStatus(this, EventUtils.DSP_MODE_PACKAGE_NAME);
                if (this.m_iUiIndex == 1) {
                    setContentView(R.layout.launcher_zhifang_1920x720);
                } else if (this.m_iUiIndex == 2) {
                    setContentView(R.layout.launcher_changtong_1920x720_ui1);
                } else if (this.m_iUiIndex == 3) {
                    setContentView(R.layout.launcher_changtong_1920x720_ui2);
                } else {
                    setContentView(R.layout.launcher_1920x720);
                }
            } else {
                setContentView(R.layout.launcher);
            }
        }
        setupViews();
        if ((this.m_iModeSet == 1 || this.m_iModeSet == 3) && this.m_b_ksw_audio_main_scroll && this.ksw_3d_mLoopRotarySwitchView != null) {
            initLoopRotarySwitchView();
        }
        showFirstRunWorkspaceCling();
        registerContentObservers();
        this.mSavedState = savedInstanceState;
        restoreState(this.mSavedState);
        if (this.mAppsCustomizeContent != null) {
            this.mAppsCustomizeContent.onPackagesUpdated(LauncherModel.getSortedWidgetsAndShortcuts(this));
        }
        if (!this.mRestoring) {
            if (sPausedFromUserAction) {
                this.mModel.startLoader(true, -1);
            } else {
                this.mModel.startLoader(true, this.mWorkspace.getCurrentPage());
            }
        }
        if (!this.mModel.isAllAppsLoaded()) {
            this.mInflater.inflate(R.layout.apps_customize_progressbar, (ViewGroup) this.mAppsCustomizeContent.getParent());
        }
        this.mDefaultKeySsb = new SpannableStringBuilder();
        Selection.setSelection(this.mDefaultKeySsb, 0);
        registerReceiver(this.mCloseSystemDialogsReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        if (this.mPlayStatusReceiver == null) {
            this.mPlayStatusReceiver = new PlayStatusReceiver();
        }
        IntentFilter intent_playstatus = new IntentFilter();
        intent_playstatus.addAction(EventUtils.NOTIFY_WORKSPACE_PLAY_STATUS);
        intent_playstatus.addAction(EventUtils.NOTIFY_WORKSPACE_PLAY_STRACK);
        intent_playstatus.addAction(EventUtils.VALID_MODE_INFOR_CHANGE);
        intent_playstatus.addAction(EventUtils.BROADCAST_VALID_MODE_EVT);
        intent_playstatus.addAction(EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE);
        registerReceiver(this.mPlayStatusReceiver, intent_playstatus);
        updateGlobalIcons();
        unlockScreenOrientation(true);
        initSystemService();
        conectWeatherService();
        startConnectService();
        if (LauncherApplication.m_iUITypeVer == 41 && (this.m_iModeSet == 5 || this.m_iModeSet == 9 || this.m_iModeSet == 10 || this.m_iModeSet == 12)) {
            initLoadImageIcon();
        }
        this.mInitVar = true;
        sendBroadcast(new Intent(START_YUNZHISHENG_SRV_NAME));
        setDefaultWallPaper();
        this.mMyhandler.sendEmptyMessageDelayed(4, 2000);
        SystemProperties.set("ctl.stop", "rm_date");
        this.mGestureDetector = new GestureDetector(this, new MyGestureListener());
        this.mMyhandler.sendEmptyMessageDelayed(15, 9000);
    }

    private void initLoopRotarySwitchView() {
        if (this.m_iModeSet == 1) {
            this.ksw_3d_mLoopRotarySwitchView.setMultiple(6.0f).setR(((float) this.ksw_3d_width) / 4.0f).setLoopRotationX(-22);
        } else if (this.m_iModeSet == 3) {
            Log.i(TAG, "--->>> initLoopRotarySwitchView  ksw_3d_width = " + this.ksw_3d_width);
            this.ksw_3d_mLoopRotarySwitchView.setMultiple(6.0f).setR(((float) this.ksw_3d_width) / 2.9f).setLoopRotationX(-28);
        }
        this.ksw_3d_mLoopRotarySwitchView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(int item, View view) {
                Log.i("TAG", "Activity点击的item===" + item);
                RelativeLayout childView = (RelativeLayout) view;
                Launcher.this.setItemClickEvent(childView);
                Launcher.this.setBackgroundColor(childView.getChildAt(0), (TextView) childView.getChildAt(1), true);
                if (!(Launcher.ksw_3d_lastPosition == -1 || Launcher.ksw_3d_lastPosition == item)) {
                    RelativeLayout childView2 = (RelativeLayout) Launcher.this.ksw_3d_views.get(Launcher.ksw_3d_lastPosition);
                    Launcher.this.setBackgroundColorNull(childView2.getChildAt(0), (TextView) childView2.getChildAt(1));
                }
                int unused = Launcher.ksw_3d_currentPosition = item;
                int unused2 = Launcher.ksw_3d_lastPosition = Launcher.ksw_3d_currentPosition;
            }
        });
        this.ksw_3d_mLoopRotarySwitchView.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void selected(int item, View view) {
                int unused = Launcher.this.ksw_3d_selectItem = Launcher.this.ksw_3d_mLoopRotarySwitchView.getSelectItem();
                Log.i("TAG", "setOnItemSelectedListener---selectItem===" + Launcher.this.ksw_3d_selectItem);
                Log.i("TAG", "setOnItemSelectedListener---item===" + item);
                List unused2 = Launcher.this.ksw_3d_views = Launcher.this.ksw_3d_mLoopRotarySwitchView.getViews();
                RelativeLayout childView = (RelativeLayout) view;
                ImageView ivChildAt = (ImageView) childView.getChildAt(0);
                TextView tvChildAt = (TextView) childView.getChildAt(1);
                if (Launcher.this.m_b_ksw_init_accOn_focus_index) {
                    boolean unused3 = Launcher.this.m_b_ksw_init_accOn_focus_index = false;
                } else {
                    Launcher.this.setBackgroundColor(ivChildAt, tvChildAt, false);
                }
                if (!(Launcher.ksw_3d_lastPosition == -1 || Launcher.ksw_3d_lastPosition == item)) {
                    RelativeLayout childView2 = (RelativeLayout) Launcher.this.ksw_3d_views.get(Launcher.ksw_3d_lastPosition);
                    Launcher.this.setBackgroundColorNull((ImageView) childView2.getChildAt(0), (TextView) childView2.getChildAt(1));
                }
                int unused4 = Launcher.ksw_3d_currentPosition = item;
                int unused5 = Launcher.ksw_3d_lastPosition = item;
            }
        });
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
            case R.id.rl_navi:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case R.id.rl_bt:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case R.id.rl_dvr:
                ksw_enter_dvr();
                return;
            case R.id.rl_settings:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case R.id.rl_video:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case R.id.rl_shoujihulian:
                enter_phoneLink();
                return;
            case R.id.rl_browser:
                ksw_enter_browser();
                return;
            case R.id.rl_music:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case R.id.rl_yuanche:
                this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                if (!this.m_b_ksw_Original_car_video_display) {
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                } else {
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                }
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void setBackgroundColorNull(View ivChildAt, View tvChildAt) {
        int ivRsId = ivChildAt.getId();
        if (ivRsId == R.id.iv_BtMusic) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.lanyayinyue_n));
        } else if (ivRsId == R.id.iv_daohang) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei_1280x480_audio_3d_daohang_n));
        } else if (ivRsId == R.id.iv_lanya) {
            ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei3d_lanya_n));
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

    /* access modifiers changed from: private */
    public void setBackgroundColor(View ivChildAt, View tvChildAt, boolean b) {
        int ivRsId = ivChildAt.getId();
        int tvRsId = tvChildAt.getId();
        if (ivRsId == R.id.iv_BtMusic) {
            if (b) {
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.lanyayinyue_d));
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
                ivChildAt.setBackground(getResources().getDrawable(R.drawable.kesaiwei3d_lanya_d));
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

    public class PlayStatusReceiver extends BroadcastReceiver {
        public PlayStatusReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(EventUtils.VALID_MODE_INFOR_CHANGE)) {
                LauncherApplication app = (LauncherApplication) Launcher.this.getApplication();
                if (app.getEvtService() != null) {
                    try {
                        if (app.getEvtService().getValidPlayStatus() == 1) {
                            boolean unused = Launcher.this.isPlay = true;
                        } else {
                            boolean unused2 = Launcher.this.isPlay = false;
                        }
                        Launcher.this.refreshPlayState();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            } else if (action.equals(EventUtils.BROADCAST_VALID_MODE_EVT)) {
                Launcher.this.refreshPlayState();
            } else if (action.equals(EventUtils.NOTIFY_WORKSPACE_PLAY_STATUS)) {
                boolean unused3 = Launcher.this.isPlay = intent.getBooleanExtra(EventUtils.PLAY_STATUS_DATA, false);
                Launcher.this.refreshPlayState();
            } else if (action.equals(EventUtils.NOTIFY_WORKSPACE_PLAY_STRACK)) {
                Launcher.this.refreshPlayState();
            } else if (action.equals(EventUtils.VALID_MODE_INFOR_CHANGE)) {
                LauncherApplication app2 = (LauncherApplication) Launcher.this.getApplication();
                if (app2.getEvtService() != null) {
                    try {
                        if (app2.getEvtService().getValidPlayStatus() == 1) {
                            boolean unused4 = Launcher.this.isPlay = true;
                        } else {
                            boolean unused5 = Launcher.this.isPlay = false;
                        }
                        Launcher.this.refreshPlayState();
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }
                }
            } else if (action.equals(EventUtils.BROADCAST_VALID_MODE_EVT)) {
                Launcher.this.refreshPlayState();
            } else if (action.equals(EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE)) {
                int unused6 = Launcher.this.iMediaType = intent.getIntExtra(EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE_EXTRA, 0);
                Log.i(Launcher.TAG, "onReceive: ZXW_ACTION_NOTIIFY_MEDIA_TYPE-iMediaType = " + Launcher.this.iMediaType);
                Launcher.this.refreshMediaTypeBackground(Launcher.this.iMediaType);
            }
        }
    }

    /* access modifiers changed from: private */
    public void refreshMediaTypeBackground(int iMediaType2) {
        if (this.ivMediaTypeBg != null) {
            switch (iMediaType2) {
                case 0:
                    if (this.m_iModeSet == 15) {
                        this.ivMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_bendi);
                        return;
                    } else if (this.m_iModeSet == 16) {
                        this.ivMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_bendi);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    if (this.m_iModeSet == 15) {
                        this.ivMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_sd);
                        return;
                    } else if (this.m_iModeSet == 16) {
                        this.ivMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_sd);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    if (this.m_iModeSet == 15) {
                        this.ivMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1280x480_evo_id7_media_usb);
                        return;
                    } else if (this.m_iModeSet == 16) {
                        this.ivMediaTypeBg.setBackgroundResource(R.drawable.kesaiwei_1920x720_evo_id7_media_usb);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        sPausedFromUserAction = true;
    }

    private void updateGlobalIcons() {
        boolean searchVisible = false;
        boolean voiceVisible = false;
        int coi = getCurrentOrientationIndexForGlobalIcons();
        if (sGlobalSearchIcon[coi] == null || sVoiceSearchIcon[coi] == null || sAppMarketIcon[coi] == null) {
            updateAppMarketIcon();
            searchVisible = updateGlobalSearchIcon();
            voiceVisible = updateVoiceSearchIcon(searchVisible);
        }
        if (sGlobalSearchIcon[coi] != null) {
            updateGlobalSearchIcon(sGlobalSearchIcon[coi]);
            searchVisible = true;
        }
        if (sVoiceSearchIcon[coi] != null) {
            updateVoiceSearchIcon(sVoiceSearchIcon[coi]);
            voiceVisible = true;
        }
        if (sAppMarketIcon[coi] != null) {
            updateAppMarketIcon(sAppMarketIcon[coi]);
        }
        if (this.mSearchDropTargetBar != null) {
            this.mSearchDropTargetBar.onSearchPackagesChanged(searchVisible, voiceVisible);
        }
    }

    /* access modifiers changed from: private */
    public void checkForLocaleChange() {
        boolean localeChanged = false;
        if (sLocaleConfiguration == null) {
            new AsyncTask<Void, Void, LocaleConfiguration>() {
                /* access modifiers changed from: protected */
                public LocaleConfiguration doInBackground(Void... unused) {
                    LocaleConfiguration localeConfiguration = new LocaleConfiguration();
                    Launcher.readConfiguration(Launcher.this, localeConfiguration);
                    return localeConfiguration;
                }

                /* access modifiers changed from: protected */
                public void onPostExecute(LocaleConfiguration result) {
                    LocaleConfiguration unused = Launcher.sLocaleConfiguration = result;
                    Launcher.this.checkForLocaleChange();
                }
            }.execute(new Void[0]);
            return;
        }
        Configuration configuration = getResources().getConfiguration();
        String previousLocale = sLocaleConfiguration.locale;
        String locale = configuration.locale.toString();
        int previousMcc = sLocaleConfiguration.mcc;
        int mcc = configuration.mcc;
        int previousMnc = sLocaleConfiguration.mnc;
        int mnc = configuration.mnc;
        if (!(locale.equals(previousLocale) && mcc == previousMcc && mnc == previousMnc)) {
            localeChanged = true;
        }
        if (localeChanged) {
            sLocaleConfiguration.locale = locale;
            sLocaleConfiguration.mcc = mcc;
            sLocaleConfiguration.mnc = mnc;
            this.mIconCache.flush();
            final LocaleConfiguration localeConfiguration = sLocaleConfiguration;
            new Thread("WriteLocaleConfiguration") {
                public void run() {
                    Launcher.writeConfiguration(Launcher.this, localeConfiguration);
                }
            }.start();
        }
    }

    private static class LocaleConfiguration {
        public String locale;
        public int mcc;
        public int mnc;

        private LocaleConfiguration() {
            this.mcc = -1;
            this.mnc = -1;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002b A[SYNTHETIC, Splitter:B:13:0x002b] */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0034 A[SYNTHETIC, Splitter:B:18:0x0034] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x003d A[SYNTHETIC, Splitter:B:23:0x003d] */
    /* JADX WARNING: Removed duplicated region for block: B:36:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void readConfiguration(android.content.Context r4, com.szchoiceway.index.Launcher.LocaleConfiguration r5) {
        /*
            r0 = 0
            java.io.DataInputStream r1 = new java.io.DataInputStream     // Catch:{ FileNotFoundException -> 0x0028, IOException -> 0x0031, all -> 0x003a }
            java.lang.String r2 = "launcher.preferences"
            java.io.FileInputStream r2 = r4.openFileInput(r2)     // Catch:{ FileNotFoundException -> 0x0028, IOException -> 0x0031, all -> 0x003a }
            r1.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0028, IOException -> 0x0031, all -> 0x003a }
            java.lang.String r2 = r1.readUTF()     // Catch:{ FileNotFoundException -> 0x0049, IOException -> 0x0046, all -> 0x0043 }
            r5.locale = r2     // Catch:{ FileNotFoundException -> 0x0049, IOException -> 0x0046, all -> 0x0043 }
            int r2 = r1.readInt()     // Catch:{ FileNotFoundException -> 0x0049, IOException -> 0x0046, all -> 0x0043 }
            r5.mcc = r2     // Catch:{ FileNotFoundException -> 0x0049, IOException -> 0x0046, all -> 0x0043 }
            int r2 = r1.readInt()     // Catch:{ FileNotFoundException -> 0x0049, IOException -> 0x0046, all -> 0x0043 }
            r5.mnc = r2     // Catch:{ FileNotFoundException -> 0x0049, IOException -> 0x0046, all -> 0x0043 }
            if (r1 == 0) goto L_0x004c
            r1.close()     // Catch:{ IOException -> 0x0025 }
            r0 = r1
        L_0x0024:
            return
        L_0x0025:
            r2 = move-exception
            r0 = r1
            goto L_0x0024
        L_0x0028:
            r2 = move-exception
        L_0x0029:
            if (r0 == 0) goto L_0x0024
            r0.close()     // Catch:{ IOException -> 0x002f }
            goto L_0x0024
        L_0x002f:
            r2 = move-exception
            goto L_0x0024
        L_0x0031:
            r2 = move-exception
        L_0x0032:
            if (r0 == 0) goto L_0x0024
            r0.close()     // Catch:{ IOException -> 0x0038 }
            goto L_0x0024
        L_0x0038:
            r2 = move-exception
            goto L_0x0024
        L_0x003a:
            r2 = move-exception
        L_0x003b:
            if (r0 == 0) goto L_0x0040
            r0.close()     // Catch:{ IOException -> 0x0041 }
        L_0x0040:
            throw r2
        L_0x0041:
            r3 = move-exception
            goto L_0x0040
        L_0x0043:
            r2 = move-exception
            r0 = r1
            goto L_0x003b
        L_0x0046:
            r2 = move-exception
            r0 = r1
            goto L_0x0032
        L_0x0049:
            r2 = move-exception
            r0 = r1
            goto L_0x0029
        L_0x004c:
            r0 = r1
            goto L_0x0024
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.Launcher.readConfiguration(android.content.Context, com.szchoiceway.index.Launcher$LocaleConfiguration):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002c A[SYNTHETIC, Splitter:B:13:0x002c] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x003e A[SYNTHETIC, Splitter:B:21:0x003e] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0047 A[SYNTHETIC, Splitter:B:26:0x0047] */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void writeConfiguration(android.content.Context r5, com.szchoiceway.index.Launcher.LocaleConfiguration r6) {
        /*
            r1 = 0
            java.io.DataOutputStream r2 = new java.io.DataOutputStream     // Catch:{ FileNotFoundException -> 0x0029, IOException -> 0x0032 }
            java.lang.String r3 = "launcher.preferences"
            r4 = 0
            java.io.FileOutputStream r3 = r5.openFileOutput(r3, r4)     // Catch:{ FileNotFoundException -> 0x0029, IOException -> 0x0032 }
            r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0029, IOException -> 0x0032 }
            java.lang.String r3 = r6.locale     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            r2.writeUTF(r3)     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            int r3 = r6.mcc     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            r2.writeInt(r3)     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            int r3 = r6.mnc     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            r2.writeInt(r3)     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            r2.flush()     // Catch:{ FileNotFoundException -> 0x0053, IOException -> 0x0050, all -> 0x004d }
            if (r2 == 0) goto L_0x0056
            r2.close()     // Catch:{ IOException -> 0x0026 }
            r1 = r2
        L_0x0025:
            return
        L_0x0026:
            r3 = move-exception
            r1 = r2
            goto L_0x0025
        L_0x0029:
            r3 = move-exception
        L_0x002a:
            if (r1 == 0) goto L_0x0025
            r1.close()     // Catch:{ IOException -> 0x0030 }
            goto L_0x0025
        L_0x0030:
            r3 = move-exception
            goto L_0x0025
        L_0x0032:
            r0 = move-exception
        L_0x0033:
            java.lang.String r3 = "launcher.preferences"
            java.io.File r3 = r5.getFileStreamPath(r3)     // Catch:{ all -> 0x0044 }
            r3.delete()     // Catch:{ all -> 0x0044 }
            if (r1 == 0) goto L_0x0025
            r1.close()     // Catch:{ IOException -> 0x0042 }
            goto L_0x0025
        L_0x0042:
            r3 = move-exception
            goto L_0x0025
        L_0x0044:
            r3 = move-exception
        L_0x0045:
            if (r1 == 0) goto L_0x004a
            r1.close()     // Catch:{ IOException -> 0x004b }
        L_0x004a:
            throw r3
        L_0x004b:
            r4 = move-exception
            goto L_0x004a
        L_0x004d:
            r3 = move-exception
            r1 = r2
            goto L_0x0045
        L_0x0050:
            r0 = move-exception
            r1 = r2
            goto L_0x0033
        L_0x0053:
            r3 = move-exception
            r1 = r2
            goto L_0x002a
        L_0x0056:
            r1 = r2
            goto L_0x0025
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.Launcher.writeConfiguration(android.content.Context, com.szchoiceway.index.Launcher$LocaleConfiguration):void");
    }

    public DragLayer getDragLayer() {
        return this.mDragLayer;
    }

    /* access modifiers changed from: package-private */
    public boolean isDraggingEnabled() {
        return !this.mModel.isLoadingWorkspace();
    }

    static int getScreen() {
        int i;
        synchronized (sLock) {
            i = sScreen;
        }
        return i;
    }

    static void setScreen(int screen) {
        synchronized (sLock) {
            sScreen = screen;
        }
    }

    private boolean completeAdd(PendingAddArguments args) {
        boolean result = false;
        switch (args.requestCode) {
            case 1:
                completeAddShortcut(args.intent, args.container, args.screen, args.cellX, args.cellY);
                result = true;
                break;
            case 5:
                completeAddAppWidget(args.intent.getIntExtra("appWidgetId", -1), args.container, args.screen, (AppWidgetHostView) null, (AppWidgetProviderInfo) null);
                result = true;
                break;
            case 6:
                completeAddApplication(args.intent, args.container, args.screen, args.cellX, args.cellY);
                break;
            case 7:
                processShortcut(args.intent);
                break;
        }
        resetAddInfo();
        return result;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean z;
        int appWidgetId;
        int pendingAddWidgetId = this.mPendingAddWidgetId;
        this.mPendingAddWidgetId = -1;
        if (requestCode == 11) {
            int appWidgetId2 = data != null ? data.getIntExtra("appWidgetId", -1) : -1;
            if (resultCode == 0) {
                completeTwoStageWidgetDrop(0, appWidgetId2);
            } else if (resultCode == -1) {
                addAppWidgetImpl(appWidgetId2, this.mPendingAddInfo, (AppWidgetHostView) null, this.mPendingAddWidgetInfo);
            }
        } else {
            boolean delayExitSpringLoadedMode = false;
            boolean isWidgetDrop = requestCode == 9 || requestCode == 5;
            this.mWaitingForResult = false;
            if (isWidgetDrop) {
                int widgetId = data != null ? data.getIntExtra("appWidgetId", -1) : -1;
                if (widgetId < 0) {
                    appWidgetId = pendingAddWidgetId;
                } else {
                    appWidgetId = widgetId;
                }
                if (appWidgetId < 0) {
                    Log.e(TAG, "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the \\widget configuration activity.");
                    completeTwoStageWidgetDrop(0, appWidgetId);
                    return;
                }
                completeTwoStageWidgetDrop(resultCode, appWidgetId);
                return;
            }
            if (resultCode == -1 && this.mPendingAddInfo.container != -1) {
                PendingAddArguments args = new PendingAddArguments();
                args.requestCode = requestCode;
                args.intent = data;
                args.container = this.mPendingAddInfo.container;
                args.screen = this.mPendingAddInfo.screen;
                args.cellX = this.mPendingAddInfo.cellX;
                args.cellY = this.mPendingAddInfo.cellY;
                if (isWorkspaceLocked()) {
                    sPendingAddList.add(args);
                } else {
                    delayExitSpringLoadedMode = completeAdd(args);
                }
            }
            this.mDragLayer.clearAnimatedView();
            if (resultCode != 0) {
                z = true;
            } else {
                z = false;
            }
            exitSpringLoadedDragModeDelayed(z, delayExitSpringLoadedMode, (Runnable) null);
        }
    }

    private void completeTwoStageWidgetDrop(final int resultCode, final int appWidgetId) {
        CellLayout cellLayout = (CellLayout) this.mWorkspace.getChildAt(this.mPendingAddInfo.screen);
        Runnable onCompleteRunnable = null;
        int animationType = 0;
        AppWidgetHostView boundWidget = null;
        if (resultCode == -1) {
            animationType = 3;
            final AppWidgetHostView layout = this.mAppWidgetHost.createView(this, appWidgetId, this.mPendingAddWidgetInfo);
            boundWidget = layout;
            onCompleteRunnable = new Runnable() {
                public void run() {
                    boolean z;
                    Launcher.this.completeAddAppWidget(appWidgetId, Launcher.this.mPendingAddInfo.container, Launcher.this.mPendingAddInfo.screen, layout, (AppWidgetProviderInfo) null);
                    Launcher launcher = Launcher.this;
                    if (resultCode != 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    launcher.exitSpringLoadedDragModeDelayed(z, false, (Runnable) null);
                }
            };
        } else if (resultCode == 0) {
            this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            animationType = 4;
            onCompleteRunnable = new Runnable() {
                public void run() {
                    boolean z;
                    Launcher launcher = Launcher.this;
                    if (resultCode != 0) {
                        z = true;
                    } else {
                        z = false;
                    }
                    launcher.exitSpringLoadedDragModeDelayed(z, false, (Runnable) null);
                }
            };
        }
        if (this.mDragLayer.getAnimatedView() != null) {
            this.mWorkspace.animateWidgetDrop(this.mPendingAddInfo, cellLayout, (DragView) this.mDragLayer.getAnimatedView(), onCompleteRunnable, animationType, boundWidget, true);
        } else {
            onCompleteRunnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        this.m_onResum = false;
        FirstFrameAnimatorHelper.setIsVisible(false);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        FirstFrameAnimatorHelper.setIsVisible(true);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        boolean z;
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (this.mSysProviderOpt != null) {
            this.mSysProviderOpt.updateRecord("ZXW_LAUNCHER_IS_RESUME", "1");
        }
        this.m_onResum = true;
        if (this.mOnResumeState == State.WORKSPACE) {
            showWorkspace(false);
        } else if (this.mOnResumeState == State.APPS_CUSTOMIZE) {
            showAllApps(false);
        }
        this.mOnResumeState = State.NONE;
        if (this.mState == State.WORKSPACE) {
            z = true;
        } else {
            z = false;
        }
        setWorkspaceBackground(z);
        InstallShortcutReceiver.flushInstallQueue(this);
        this.mPaused = false;
        sPausedFromUserAction = false;
        if (this.mRestoring || this.mOnResumeNeedsLoad) {
            this.mWorkspaceLoading = true;
            this.mModel.startLoader(true, -1);
            this.mRestoring = false;
            this.mOnResumeNeedsLoad = false;
        }
        if (this.mOnResumeCallbacks.size() > 0) {
            if (this.mAppsCustomizeContent != null) {
                this.mAppsCustomizeContent.setBulkBind(true);
            }
            for (int i = 0; i < this.mOnResumeCallbacks.size(); i++) {
                this.mOnResumeCallbacks.get(i).run();
            }
            if (this.mAppsCustomizeContent != null) {
                this.mAppsCustomizeContent.setBulkBind(false);
            }
            this.mOnResumeCallbacks.clear();
        }
        if (this.mWaitingForResume != null) {
            this.mWaitingForResume.setStayPressed(false);
        }
        if (this.mAppsCustomizeContent != null) {
            this.mAppsCustomizeContent.resetDrawableState();
        }
        getWorkspace().reinflateWidgetsIfNecessary();
        updateGlobalIcons();
        LauncherApplication launcherApplication = (LauncherApplication) getApplication();
        if (this.mInitVar) {
            initView();
            this.mMyhandler.sendEmptyMessageDelayed(2, 1600);
            this.mInitVar = false;
        }
        if (LauncherApplication.m_iUITypeVer == 41) {
            if (Build.VERSION.SDK_INT < 26) {
                if (this.mState == State.APPS_CUSTOMIZE) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "1");
                } else {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "0");
                }
            }
            if (LauncherApplication.m_iUITypeVer == 41) {
                this.mMyhandler.removeMessages(1000);
                this.mMyhandler.sendEmptyMessageDelayed(1000, 500);
            }
        } else if (LauncherApplication.m_iUITypeVer == 101) {
            if (this.mState == State.APPS_CUSTOMIZE) {
                this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "1");
            } else {
                this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "0");
            }
        }
        refreshPlayState();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.m_onResum = false;
        Log.i(TAG, "onPause: ");
        if (this.mSysProviderOpt != null) {
            this.mSysProviderOpt.updateRecord("ZXW_LAUNCHER_IS_RESUME", "0");
        }
        this.mPaused = true;
        this.mDragController.cancelDrag();
        this.mDragController.resetLastGestureUpTime();
        if ((LauncherApplication.m_iUITypeVer == 41 || LauncherApplication.m_iUITypeVer == 101) && Build.VERSION.SDK_INT < 26) {
            this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "0");
        }
    }

    public Object onRetainNonConfigurationInstance() {
        this.mModel.stopLoader();
        if (this.mAppsCustomizeContent != null) {
            this.mAppsCustomizeContent.surrender();
        }
        return Boolean.TRUE;
    }

    private boolean acceptFilter() {
        return !((InputMethodManager) getSystemService("input_method")).isFullscreenMode();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: keyCode = " + keyCode);
        int uniChar = event.getUnicodeChar();
        boolean handled = super.onKeyDown(keyCode, event);
        boolean isKeyNotWhitespace = uniChar > 0 && !Character.isWhitespace(uniChar);
        if (!handled && acceptFilter() && isKeyNotWhitespace && TextKeyListener.getInstance().onKeyDown(this.mWorkspace, this.mDefaultKeySsb, keyCode, event) && this.mDefaultKeySsb != null && this.mDefaultKeySsb.length() > 0) {
            return onSearchRequested();
        }
        if (keyCode != 82 || !event.isLongPress()) {
            return handled;
        }
        return true;
    }

    private String getTypedText() {
        return this.mDefaultKeySsb.toString();
    }

    private void clearTypedText() {
        this.mDefaultKeySsb.clear();
        this.mDefaultKeySsb.clearSpans();
        Selection.setSelection(this.mDefaultKeySsb, 0);
    }

    private static State intToState(int stateOrdinal) {
        State state = State.WORKSPACE;
        State[] stateValues = State.values();
        for (int i = 0; i < stateValues.length; i++) {
            if (stateValues[i].ordinal() == stateOrdinal) {
                return stateValues[i];
            }
        }
        return state;
    }

    private void restoreState(Bundle savedState) {
        if (savedState != null) {
            if (intToState(savedState.getInt(RUNTIME_STATE, State.WORKSPACE.ordinal())) == State.APPS_CUSTOMIZE) {
                this.mOnResumeState = State.APPS_CUSTOMIZE;
            }
            int currentScreen = savedState.getInt(RUNTIME_STATE_CURRENT_SCREEN, -1);
            if (currentScreen > -1) {
                this.mWorkspace.setCurrentPage(currentScreen);
            }
            long pendingAddContainer = savedState.getLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, -1);
            int pendingAddScreen = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SCREEN, -1);
            if (pendingAddContainer != -1 && pendingAddScreen > -1) {
                this.mPendingAddInfo.container = pendingAddContainer;
                this.mPendingAddInfo.screen = pendingAddScreen;
                this.mPendingAddInfo.cellX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_X);
                this.mPendingAddInfo.cellY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_CELL_Y);
                this.mPendingAddInfo.spanX = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_X);
                this.mPendingAddInfo.spanY = savedState.getInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y);
                this.mPendingAddWidgetInfo = (AppWidgetProviderInfo) savedState.getParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO);
                this.mPendingAddWidgetId = savedState.getInt(RUNTIME_STATE_PENDING_ADD_WIDGET_ID);
                this.mWaitingForResult = true;
                this.mRestoring = true;
            }
            if (savedState.getBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, false)) {
                this.mFolderInfo = this.mModel.getFolderById(this, sFolders, savedState.getLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID));
                this.mRestoring = true;
            }
            if (this.mAppsCustomizeTabHost != null) {
                String curTab = savedState.getString("apps_customize_currentTab");
                if (curTab != null) {
                    this.mAppsCustomizeTabHost.setContentTypeImmediate(this.mAppsCustomizeTabHost.getContentTypeForTabTag(curTab));
                    this.mAppsCustomizeContent.loadAssociatedPages(this.mAppsCustomizeContent.getCurrentPage());
                }
                this.mAppsCustomizeContent.restorePageForIndex(savedState.getInt("apps_customize_currentIndex"));
            }
        }
    }

    private void initViewListener(View v) {
        int[] btnResLst = {R.id.lanya, R.id.navi, R.id.radio, R.id.Btn_Music, R.id.setting, R.id.Btn_Video, R.id.yilian, R.id.liulanqi, R.id.chelianInfo, R.id.aux, R.id.setting, R.id.dvr, R.id.yuanchecd, R.id.apps, R.id.OriginaCar};
        for (int findViewById : btnResLst) {
            View btn = v.findViewById(findViewById);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
        onShowMusicInfo(false);
        onShowNaviInfo(false);
    }

    public class MyViewPageAdapter extends PagerAdapter {
        public MyViewPageAdapter() {
        }

        public int getCount() {
            return Integer.MAX_VALUE;
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(View container, int position, Object object) {
        }

        public Object instantiateItem(View container, int position) {
            try {
                position %= Launcher.this.mMeneItem.length;
                if (position < 0) {
                    position += Launcher.this.mMeneItem.length;
                }
                ViewParent vp = Launcher.this.mMeneItem[position].getParent();
                if (vp != null) {
                    ((ViewGroup) vp).removeView(Launcher.this.mMeneItem[position]);
                }
                ((ViewPager) container).addView(Launcher.this.mMeneItem[position], 0);
            } catch (Exception e) {
            }
            return Launcher.this.mMeneItem[position];
        }

        public int getItemPosition(Object object) {
            return -2;
        }
    }

    private void setupViews() {
        DragController dragController = this.mDragController;
        LauncherApplication launcherApplication = (LauncherApplication) getApplication();
        if (LauncherApplication.m_iUITypeVer == 41 && ((this.m_iModeSet == 1 || this.m_iModeSet == 3) && this.m_b_ksw_audio_main_scroll)) {
            this.ksw_3d_ivGuang = (ImageView) findViewById(R.id.iv_guang);
            this.ksw_3d_mLoopRotarySwitchView = (LoopRotarySwitchView) findViewById(R.id.mLoopRotarySwitchView);
        }
        this.mLauncherView = findViewById(R.id.launcher);
        this.mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
        this.mWorkspace = (Workspace) this.mDragLayer.findViewById(R.id.workspace);
        this.mQsbDivider = findViewById(R.id.qsb_divider);
        this.mDockDivider = findViewById(R.id.dock_divider);
        this.mLauncherView.setSystemUiVisibility(1024);
        this.mWorkspaceBackgroundDrawable = getResources().getDrawable(R.drawable.workspace_bg);
        this.mDragLayer.setup(this, dragController);
        this.mHotseat = (Hotseat) findViewById(R.id.hotseat);
        if (this.mHotseat != null) {
            this.mHotseat.setup(this);
        }
        this.mWorkspace.setHapticFeedbackEnabled(false);
        this.mWorkspace.setOnLongClickListener(this);
        this.mWorkspace.setup(dragController);
        dragController.addDragListener(this.mWorkspace);
        this.mSearchDropTargetBar = (SearchDropTargetBar) this.mDragLayer.findViewById(R.id.qsb_bar);
        this.mAppsCustomizeTabHost = (AppsCustomizeTabHost) findViewById(R.id.apps_customize_pane);
        this.mAppsCustomizeContent = (AppsCustomizePagedView) this.mAppsCustomizeTabHost.findViewById(R.id.apps_customize_pane_content);
        this.mAppsCustomizeContent.setup(this, dragController);
        dragController.setDragScoller(this.mWorkspace);
        dragController.setScrollView(this.mDragLayer);
        dragController.setMoveTarget(this.mWorkspace);
        dragController.addDropTarget(this.mWorkspace);
        if (this.mSearchDropTargetBar != null) {
            this.mSearchDropTargetBar.setup(this, dragController);
        }
        refreshPlayState();
    }

    /* access modifiers changed from: package-private */
    public View createShortcut(ShortcutInfo info) {
        return createShortcut(R.layout.application, (ViewGroup) this.mWorkspace.getChildAt(this.mWorkspace.getCurrentPage()), info);
    }

    /* access modifiers changed from: package-private */
    public View createShortcut(int layoutResId, ViewGroup parent, ShortcutInfo info) {
        BubbleTextView favorite = (BubbleTextView) this.mInflater.inflate(layoutResId, parent, false);
        favorite.applyFromShortcutInfo(info, this.mIconCache);
        favorite.setOnClickListener(this);
        return favorite;
    }

    /* access modifiers changed from: package-private */
    public void completeAddApplication(Intent data, long container, int screen, int cellX, int cellY) {
        int[] cellXY = this.mTmpAddItemCellCoordinates;
        CellLayout layout = getCellLayout(container, screen);
        if (cellX >= 0 && cellY >= 0) {
            cellXY[0] = cellX;
            cellXY[1] = cellY;
        } else if (!layout.findCellForSpan(cellXY, 1, 1)) {
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }
        ShortcutInfo info = this.mModel.getShortcutInfo(getPackageManager(), data, this);
        if (info != null) {
            info.setActivity(data.getComponent(), 270532608);
            info.container = -1;
            this.mWorkspace.addApplicationShortcut(info, layout, container, screen, cellXY[0], cellXY[1], isWorkspaceLocked(), cellX, cellY);
            return;
        }
        Log.e(TAG, "Couldn't find ActivityInfo for selected application: " + data);
    }

    private void completeAddShortcut(Intent data, long container, int screen, int cellX, int cellY) {
        boolean foundCellSpan;
        int[] cellXY = this.mTmpAddItemCellCoordinates;
        int[] touchXY = this.mPendingAddInfo.dropPos;
        CellLayout layout = getCellLayout(container, screen);
        ShortcutInfo info = this.mModel.infoFromShortcutIntent(this, data, (Bitmap) null);
        if (info != null) {
            View view = createShortcut(info);
            if (cellX >= 0 && cellY >= 0) {
                cellXY[0] = cellX;
                cellXY[1] = cellY;
                foundCellSpan = true;
                if (!this.mWorkspace.createUserFolderIfNecessary(view, container, layout, cellXY, 0.0f, true, (DragView) null, (Runnable) null)) {
                    DropTarget.DragObject dragObject = new DropTarget.DragObject();
                    dragObject.dragInfo = info;
                    if (this.mWorkspace.addToExistingFolderIfNecessary(view, layout, cellXY, 0.0f, dragObject, true)) {
                        return;
                    }
                } else {
                    return;
                }
            } else if (touchXY != null) {
                foundCellSpan = layout.findNearestVacantArea(touchXY[0], touchXY[1], 1, 1, cellXY) != null;
            } else {
                foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
            }
            if (!foundCellSpan) {
                showOutOfSpaceMessage(isHotseatLayout(layout));
                return;
            }
            LauncherModel.addItemToDatabase(this, info, container, screen, cellXY[0], cellXY[1], false);
            if (!this.mRestoring) {
                this.mWorkspace.addInScreen(view, container, screen, cellXY[0], cellXY[1], 1, 1, isWorkspaceLocked());
            }
        }
    }

    static int[] getSpanForWidget(Context context, ComponentName component, int minWidth, int minHeight) {
        Rect padding = AppWidgetHostView.getDefaultPaddingForWidget(context, component, (Rect) null);
        return CellLayout.rectToCell(context.getResources(), padding.left + minWidth + padding.right, padding.top + minHeight + padding.bottom, (int[]) null);
    }

    static int[] getSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minWidth, info.minHeight);
    }

    static int[] getMinSpanForWidget(Context context, AppWidgetProviderInfo info) {
        return getSpanForWidget(context, info.provider, info.minResizeWidth, info.minResizeHeight);
    }

    static int[] getSpanForWidget(Context context, PendingAddWidgetInfo info) {
        return getSpanForWidget(context, info.componentName, info.minWidth, info.minHeight);
    }

    static int[] getMinSpanForWidget(Context context, PendingAddWidgetInfo info) {
        return getSpanForWidget(context, info.componentName, info.minResizeWidth, info.minResizeHeight);
    }

    /* access modifiers changed from: private */
    public void completeAddAppWidget(int appWidgetId, long container, int screen, AppWidgetHostView hostView, AppWidgetProviderInfo appWidgetInfo) {
        boolean foundCellSpan;
        if (appWidgetInfo == null) {
            appWidgetInfo = this.mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        }
        CellLayout layout = getCellLayout(container, screen);
        int[] minSpanXY = getMinSpanForWidget((Context) this, appWidgetInfo);
        int[] spanXY = getSpanForWidget((Context) this, appWidgetInfo);
        int[] cellXY = this.mTmpAddItemCellCoordinates;
        int[] touchXY = this.mPendingAddInfo.dropPos;
        int[] finalSpan = new int[2];
        if (this.mPendingAddInfo.cellX >= 0 && this.mPendingAddInfo.cellY >= 0) {
            cellXY[0] = this.mPendingAddInfo.cellX;
            cellXY[1] = this.mPendingAddInfo.cellY;
            spanXY[0] = this.mPendingAddInfo.spanX;
            spanXY[1] = this.mPendingAddInfo.spanY;
            foundCellSpan = true;
        } else if (touchXY != null) {
            int[] result = layout.findNearestVacantArea(touchXY[0], touchXY[1], minSpanXY[0], minSpanXY[1], spanXY[0], spanXY[1], cellXY, finalSpan);
            spanXY[0] = finalSpan[0];
            spanXY[1] = finalSpan[1];
            foundCellSpan = result != null;
        } else {
            foundCellSpan = layout.findCellForSpan(cellXY, minSpanXY[0], minSpanXY[1]);
        }
        if (!foundCellSpan) {
            if (appWidgetId != -1) {
                final int i = appWidgetId;
                new Thread("deleteAppWidgetId") {
                    public void run() {
                        Launcher.this.mAppWidgetHost.deleteAppWidgetId(i);
                    }
                }.start();
            }
            showOutOfSpaceMessage(isHotseatLayout(layout));
            return;
        }
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId, appWidgetInfo.provider);
        launcherInfo.spanX = spanXY[0];
        launcherInfo.spanY = spanXY[1];
        launcherInfo.minSpanX = this.mPendingAddInfo.minSpanX;
        launcherInfo.minSpanY = this.mPendingAddInfo.minSpanY;
        LauncherModel.addItemToDatabase(this, launcherInfo, container, screen, cellXY[0], cellXY[1], false);
        if (!this.mRestoring) {
            if (hostView == null) {
                launcherInfo.hostView = this.mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
                launcherInfo.hostView.setAppWidget(appWidgetId, appWidgetInfo);
            } else {
                launcherInfo.hostView = hostView;
            }
            launcherInfo.hostView.setTag(launcherInfo);
            launcherInfo.hostView.setVisibility(0);
            launcherInfo.notifyWidgetSizeChanged(this);
            this.mWorkspace.addInScreen(launcherInfo.hostView, container, screen, cellXY[0], cellXY[1], launcherInfo.spanX, launcherInfo.spanY, isWorkspaceLocked());
            addWidgetToAutoAdvanceIfNeeded(launcherInfo.hostView, appWidgetInfo);
        }
        resetAddInfo();
    }

    /* access modifiers changed from: private */
    public void add_sub(boolean isAuto) {
        if (ksw_3d_lastPosition != -1) {
            RelativeLayout oldChildView = (RelativeLayout) this.ksw_3d_views.get(ksw_3d_lastPosition);
            setBackgroundColorNull(oldChildView.getChildAt(0), oldChildView.getChildAt(1));
        }
        RelativeLayout childView = (RelativeLayout) this.ksw_3d_views.get(ksw_3d_currentPosition);
        setBackgroundColor(childView.getChildAt(0), childView.getChildAt(1), false);
        if (isAuto) {
            this.ksw_3d_mLoopRotarySwitchView.setSelectItem(ksw_3d_currentPosition, false);
        }
        Log.i("TAG", "onClick---getSelectItem===" + this.ksw_3d_mLoopRotarySwitchView.getSelectItem());
        ksw_3d_lastPosition = ksw_3d_currentPosition;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.USER_PRESENT");
        filter.addAction(EventUtils.WEATHER_AREA_CHANGE);
        filter.addAction("com.android.quicksetting.BROADCAST");
        filter.addAction(EventUtils.REC_AUTONAVI_STANDARD);
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT);
        filter.addAction(EventUtils.ZXW_REFRESH_WALL_LOGO);
        filter.addAction(EventUtils.ZXW_RESET_WALLPAGER_CHANGE);
        filter.addAction(EventUtils.CHEKU_WEATHER_SETCITYCODE);
        filter.addAction(EventUtils.CHEKU_WEATHER_INIT_REFRESH_WEATHER);
        filter.addAction(EventUtils.MAISILUO_ZXW_SHOW_ALL_APPS);
        filter.addAction(EventUtils.KSW_ZXW_BT_CONNECED_SHOW_VIEW);
        filter.addAction(EventUtils.CHEKU_BOTTOM_KEY_PARK);
        filter.addAction(EventUtils.ZXW_SENDBROADCAST8902MOD);
        filter.addAction(EventUtils.ZXW_CAN_START_UP_APPS);
        registerReceiver(this.mReceiver, filter);
        FirstFrameAnimatorHelper.initializeDrawListener(getWindow().getDecorView());
        this.mAttached = true;
        this.mVisible = true;
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mVisible = false;
        if (this.mAttached) {
            unregisterReceiver(this.mReceiver);
            this.mAttached = false;
        }
        updateRunning();
    }

    public void onWindowVisibilityChanged(int visibility) {
        this.mVisible = visibility == 0;
        updateRunning();
        if (this.mVisible) {
            this.mAppsCustomizeTabHost.onWindowVisible();
            if (!this.mWorkspaceLoading) {
                this.mWorkspace.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                    private boolean mStarted = false;

                    public void onDraw() {
                        if (!this.mStarted) {
                            this.mStarted = true;
                            Launcher.this.mWorkspace.postDelayed(Launcher.this.mBuildLayersRunnable, 500);
                            Launcher.this.mWorkspace.post(new Runnable() {
                                public void run() {
                                    if (Launcher.this.mWorkspace != null && Launcher.this.mWorkspace.getViewTreeObserver() != null) {
                                        Launcher.this.mWorkspace.getViewTreeObserver().removeOnDrawListener(this);
                                    }
                                }
                            });
                        }
                    }
                });
            }
            updateAppMarketIcon();
            clearTypedText();
        }
    }

    /* access modifiers changed from: private */
    public void sendAdvanceMessage(long delay) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), delay);
        this.mAutoAdvanceSentTime = System.currentTimeMillis();
    }

    /* access modifiers changed from: private */
    public void updateRunning() {
        boolean autoAdvanceRunning;
        long delay = 20000;
        if (!this.mVisible || !this.mUserPresent || this.mWidgetsToAdvance.isEmpty()) {
            autoAdvanceRunning = false;
        } else {
            autoAdvanceRunning = true;
        }
        if (autoAdvanceRunning != this.mAutoAdvanceRunning) {
            this.mAutoAdvanceRunning = autoAdvanceRunning;
            if (autoAdvanceRunning) {
                if (this.mAutoAdvanceTimeLeft != -1) {
                    delay = this.mAutoAdvanceTimeLeft;
                }
                sendAdvanceMessage(delay);
                return;
            }
            if (!this.mWidgetsToAdvance.isEmpty()) {
                this.mAutoAdvanceTimeLeft = Math.max(0, 20000 - (System.currentTimeMillis() - this.mAutoAdvanceSentTime));
            }
            this.mHandler.removeMessages(1);
            this.mHandler.removeMessages(0);
        }
    }

    /* access modifiers changed from: package-private */
    public void addWidgetToAutoAdvanceIfNeeded(View hostView, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo != null && appWidgetInfo.autoAdvanceViewId != -1) {
            View v = hostView.findViewById(appWidgetInfo.autoAdvanceViewId);
            if (v instanceof Advanceable) {
                this.mWidgetsToAdvance.put(hostView, appWidgetInfo);
                ((Advanceable) v).fyiWillBeAdvancedByHostKThx();
                updateRunning();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void removeWidgetToAutoAdvance(View hostView) {
        if (this.mWidgetsToAdvance.containsKey(hostView)) {
            this.mWidgetsToAdvance.remove(hostView);
            updateRunning();
        }
    }

    public void removeAppWidget(LauncherAppWidgetInfo launcherInfo) {
        removeWidgetToAutoAdvance(launcherInfo.hostView);
        launcherInfo.hostView = null;
    }

    /* access modifiers changed from: package-private */
    public void showOutOfSpaceMessage(boolean isHotseatLayout) {
        Toast.makeText(this, getString(isHotseatLayout ? R.string.hotseat_out_of_space : R.string.out_of_space), 0).show();
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return this.mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    /* access modifiers changed from: package-private */
    public void closeSystemDialogs() {
        getWindow().closeAllPanels();
        this.mWaitingForResult = false;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("android.intent.action.MAIN".equals(intent.getAction())) {
            closeSystemDialogs();
            final boolean alreadyOnHome = (intent.getFlags() & AccessibilityEventCompat.TYPE_WINDOWS_CHANGED) != 4194304;
            Runnable processIntent = new Runnable() {
                public void run() {
                    if (Launcher.this.mWorkspace != null) {
                        Folder openFolder = Launcher.this.mWorkspace.getOpenFolder();
                        Launcher.this.mWorkspace.exitWidgetResizeMode();
                        if (alreadyOnHome && Launcher.this.mState == State.WORKSPACE && !Launcher.this.mWorkspace.isTouchActive() && openFolder == null) {
                            Launcher.this.mWorkspace.moveToDefaultScreen(true);
                        }
                        Launcher.this.closeFolder();
                        Launcher.this.exitSpringLoadedDragMode();
                        if (alreadyOnHome) {
                            Launcher.this.showWorkspace(true);
                            LauncherApplication app = (LauncherApplication) Launcher.this.getApplication();
                            if (app == null || app.m_iSetUIType == 5) {
                            }
                        } else {
                            State unused = Launcher.this.mOnResumeState = State.WORKSPACE;
                        }
                        View v = Launcher.this.getWindow().peekDecorView();
                        if (!(v == null || v.getWindowToken() == null)) {
                            ((InputMethodManager) Launcher.this.getSystemService("input_method")).hideSoftInputFromWindow(v.getWindowToken(), 0);
                        }
                        if (!alreadyOnHome && Launcher.this.mAppsCustomizeTabHost != null) {
                            Launcher.this.mAppsCustomizeTabHost.reset();
                        }
                    }
                }
            };
            if (!alreadyOnHome || this.mWorkspace.hasWindowFocus()) {
                processIntent.run();
            } else {
                this.mWorkspace.postDelayed(processIntent, 350);
            }
        }
    }

    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Iterator<Integer> it = this.mSynchronouslyBoundPages.iterator();
        while (it.hasNext()) {
            this.mWorkspace.restoreInstanceStateForChild(it.next().intValue());
        }
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, this.mWorkspace.getNextPage());
        super.onSaveInstanceState(outState);
        outState.putInt(RUNTIME_STATE, this.mState.ordinal());
        closeFolder();
        if (this.mPendingAddInfo.container != -1 && this.mPendingAddInfo.screen > -1 && this.mWaitingForResult) {
            outState.putLong(RUNTIME_STATE_PENDING_ADD_CONTAINER, this.mPendingAddInfo.container);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SCREEN, this.mPendingAddInfo.screen);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_X, this.mPendingAddInfo.cellX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_CELL_Y, this.mPendingAddInfo.cellY);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_X, this.mPendingAddInfo.spanX);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_SPAN_Y, this.mPendingAddInfo.spanY);
            outState.putParcelable(RUNTIME_STATE_PENDING_ADD_WIDGET_INFO, this.mPendingAddWidgetInfo);
            outState.putInt(RUNTIME_STATE_PENDING_ADD_WIDGET_ID, this.mPendingAddWidgetId);
        }
        if (this.mFolderInfo != null && this.mWaitingForResult) {
            outState.putBoolean(RUNTIME_STATE_PENDING_FOLDER_RENAME, true);
            outState.putLong(RUNTIME_STATE_PENDING_FOLDER_RENAME_ID, this.mFolderInfo.id);
        }
        if (this.mAppsCustomizeTabHost != null) {
            String currentTabTag = this.mAppsCustomizeTabHost.getCurrentTabTag();
            if (currentTabTag != null) {
                outState.putString("apps_customize_currentTab", currentTabTag);
            }
            outState.putInt("apps_customize_currentIndex", this.mAppsCustomizeContent.getSaveInstanceStateIndex());
        }
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if (this.imgList != null) {
            this.imgList.clear();
            this.imgList = null;
        }
        if (this.m_b_ksw_audio_main_scroll) {
            ksw_3d_lastPosition = -1;
        }
        if (this.mPlayStatusReceiver != null) {
            unregisterReceiver(this.mPlayStatusReceiver);
        }
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(0);
        this.mMyhandler.removeMessages(1);
        this.mWorkspace.removeCallbacks(this.mBuildLayersRunnable);
        this.mModel.stopLoader();
        ((LauncherApplication) getApplication()).setLauncher((Launcher) null);
        try {
            this.mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }
        this.mAppWidgetHost = null;
        this.mWidgetsToAdvance.clear();
        TextKeyListener.getInstance().release();
        if (this.mModel != null) {
            this.mModel.unbindItemInfosAndClearQueuedBindRunnables();
        }
        getContentResolver().unregisterContentObserver(this.mWidgetObserver);
        unregisterReceiver(this.mCloseSystemDialogsReceiver);
        this.mDragLayer.clearAllResizeFrames();
        ((ViewGroup) this.mWorkspace.getParent()).removeAllViews();
        this.mWorkspace.removeAllViews();
        this.mWorkspace = null;
        this.mDragController = null;
        disconectWeatherService();
        stopConntectService();
        LauncherAnimUtils.onDestroyActivity();
    }

    public DragController getDragController() {
        return this.mDragController;
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (requestCode >= 0) {
            this.mWaitingForResult = true;
        }
        super.startActivityForResult(intent, requestCode);
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        showWorkspace(true);
        if (initialQuery == null) {
            initialQuery = getTypedText();
        }
        if (appSearchData == null) {
            appSearchData = new Bundle();
        }
        Rect sourceBounds = new Rect();
        if (this.mSearchDropTargetBar != null) {
            sourceBounds = this.mSearchDropTargetBar.getSearchBarBounds();
        }
        startGlobalSearch(initialQuery, selectInitialQuery, appSearchData, sourceBounds);
    }

    public void startGlobalSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
        Bundle appSearchData2;
        ComponentName globalSearchActivity = ((SearchManager) getSystemService("search")).getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            Log.w(TAG, "No global search activity found.");
            return;
        }
        Intent intent = new Intent("android.search.action.GLOBAL_SEARCH");
        intent.addFlags(268435456);
        intent.setComponent(globalSearchActivity);
        if (appSearchData == null) {
            appSearchData2 = new Bundle();
        } else {
            appSearchData2 = new Bundle(appSearchData);
        }
        if (!appSearchData2.containsKey("source")) {
            appSearchData2.putString("source", getPackageName());
        }
        intent.putExtra("app_data", appSearchData2);
        if (!TextUtils.isEmpty(initialQuery)) {
            intent.putExtra("query", initialQuery);
        }
        if (selectInitialQuery) {
            intent.putExtra("select_query", selectInitialQuery);
        }
        intent.setSourceBounds(sourceBounds);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Global search activity not found: " + globalSearchActivity);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (isWorkspaceLocked()) {
            return false;
        }
        super.onCreateOptionsMenu(menu);
        Intent manageApps = new Intent("android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS");
        manageApps.setFlags(276824064);
        Intent settings = new Intent("android.settings.SETTINGS");
        settings.setFlags(270532608);
        String helpUrl = getString(R.string.help_url);
        Intent help = new Intent("android.intent.action.VIEW", Uri.parse(helpUrl));
        help.setFlags(276824064);
        menu.add(1, 2, 0, R.string.menu_wallpaper).setIcon(17301567).setAlphabeticShortcut('W');
        menu.add(0, 3, 0, R.string.menu_manage_apps).setIcon(17301570).setIntent(manageApps).setAlphabeticShortcut('M');
        menu.add(0, 4, 0, R.string.menu_settings).setIcon(17301577).setIntent(settings).setAlphabeticShortcut('P');
        if (!helpUrl.isEmpty()) {
            menu.add(0, 5, 0, R.string.menu_help).setIcon(17301568).setIntent(help).setAlphabeticShortcut('H');
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean allAppsVisible;
        boolean z = false;
        super.onPrepareOptionsMenu(menu);
        if (this.mAppsCustomizeTabHost.isTransitioning()) {
            return false;
        }
        if (this.mAppsCustomizeTabHost.getVisibility() == 0) {
            allAppsVisible = true;
        } else {
            allAppsVisible = false;
        }
        if (!allAppsVisible) {
            z = true;
        }
        menu.setGroupVisible(1, z);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 2:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onSearchRequested() {
        startSearch((String) null, false, (Bundle) null, true);
        return true;
    }

    public boolean isWorkspaceLocked() {
        return this.mWorkspaceLoading || this.mWaitingForResult;
    }

    private void resetAddInfo() {
        this.mPendingAddInfo.container = -1;
        this.mPendingAddInfo.screen = -1;
        ItemInfo itemInfo = this.mPendingAddInfo;
        this.mPendingAddInfo.cellY = -1;
        itemInfo.cellX = -1;
        ItemInfo itemInfo2 = this.mPendingAddInfo;
        this.mPendingAddInfo.spanY = -1;
        itemInfo2.spanX = -1;
        ItemInfo itemInfo3 = this.mPendingAddInfo;
        this.mPendingAddInfo.minSpanY = -1;
        itemInfo3.minSpanX = -1;
        this.mPendingAddInfo.dropPos = null;
    }

    /* access modifiers changed from: package-private */
    public void addAppWidgetImpl(int appWidgetId, ItemInfo info, AppWidgetHostView boundWidget, AppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo.configure != null) {
            this.mPendingAddWidgetInfo = appWidgetInfo;
            this.mPendingAddWidgetId = appWidgetId;
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra("appWidgetId", appWidgetId);
            startActivityForResultSafely(intent, 5);
            return;
        }
        completeAddAppWidget(appWidgetId, info.container, info.screen, boundWidget, appWidgetInfo);
        exitSpringLoadedDragModeDelayed(true, false, (Runnable) null);
    }

    /* access modifiers changed from: package-private */
    public void processShortcutFromDrop(ComponentName componentName, long container, int screen, int[] cell, int[] loc) {
        resetAddInfo();
        this.mPendingAddInfo.container = container;
        this.mPendingAddInfo.screen = screen;
        this.mPendingAddInfo.dropPos = loc;
        if (cell != null) {
            this.mPendingAddInfo.cellX = cell[0];
            this.mPendingAddInfo.cellY = cell[1];
        }
        Intent createShortcutIntent = new Intent("android.intent.action.CREATE_SHORTCUT");
        createShortcutIntent.setComponent(componentName);
        processShortcut(createShortcutIntent);
    }

    /* access modifiers changed from: package-private */
    public void addAppWidgetFromDrop(PendingAddWidgetInfo info, long container, int screen, int[] cell, int[] span, int[] loc) {
        boolean success;
        resetAddInfo();
        ItemInfo itemInfo = this.mPendingAddInfo;
        info.container = container;
        itemInfo.container = container;
        ItemInfo itemInfo2 = this.mPendingAddInfo;
        info.screen = screen;
        itemInfo2.screen = screen;
        this.mPendingAddInfo.dropPos = loc;
        this.mPendingAddInfo.minSpanX = info.minSpanX;
        this.mPendingAddInfo.minSpanY = info.minSpanY;
        if (cell != null) {
            this.mPendingAddInfo.cellX = cell[0];
            this.mPendingAddInfo.cellY = cell[1];
        }
        if (span != null) {
            this.mPendingAddInfo.spanX = span[0];
            this.mPendingAddInfo.spanY = span[1];
        }
        AppWidgetHostView hostView = info.boundWidget;
        if (hostView != null) {
            addAppWidgetImpl(hostView.getAppWidgetId(), info, hostView, info.info);
            return;
        }
        int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
        Bundle options = info.bindOptions;
        if (options != null) {
            success = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.componentName, options);
        } else {
            success = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.componentName);
        }
        if (success) {
            addAppWidgetImpl(appWidgetId, info, (AppWidgetHostView) null, info.info);
            return;
        }
        this.mPendingAddWidgetInfo = info.info;
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_BIND");
        intent.putExtra("appWidgetId", appWidgetId);
        intent.putExtra("appWidgetProvider", info.componentName);
        startActivityForResult(intent, 11);
    }

    /* access modifiers changed from: package-private */
    public void processShortcut(Intent intent) {
        String applicationName = getResources().getString(R.string.group_applications);
        String shortcutName = intent.getStringExtra("android.intent.extra.shortcut.NAME");
        if (applicationName == null || !applicationName.equals(shortcutName)) {
            startActivityForResultSafely(intent, 1);
            return;
        }
        Intent mainIntent = new Intent("android.intent.action.MAIN", (Uri) null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");
        Intent pickIntent = new Intent("android.intent.action.PICK_ACTIVITY");
        pickIntent.putExtra("android.intent.extra.INTENT", mainIntent);
        pickIntent.putExtra("android.intent.extra.TITLE", getText(R.string.title_select_application));
        startActivityForResultSafely(pickIntent, 6);
    }

    /* access modifiers changed from: package-private */
    public FolderIcon addFolder(CellLayout layout, long container, int screen, int cellX, int cellY) {
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(R.string.folder_name);
        LauncherModel.addItemToDatabase(this, folderInfo, container, screen, cellX, cellY, false);
        sFolders.put(Long.valueOf(folderInfo.id), folderInfo);
        FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this, layout, folderInfo, this.mIconCache);
        this.mWorkspace.addInScreen(newFolder, container, screen, cellX, cellY, 1, 1, isWorkspaceLocked());
        return newFolder;
    }

    /* access modifiers changed from: package-private */
    public void removeFolder(FolderInfo folder) {
        sFolders.remove(Long.valueOf(folder.id));
    }

    private void registerContentObservers() {
        getContentResolver().registerContentObserver(LauncherProvider.CONTENT_APPWIDGET_RESET_URI, true, this.mWidgetObserver);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG, "dispatchKeyEvent  event.getKeyCode() = " + event.getKeyCode());
        if (event.getAction() == 0) {
            switch (event.getKeyCode()) {
                case 3:
                    return true;
                case 25:
                    if (isPropertyEnabled(DUMP_STATE_PROPERTY)) {
                        dumpState();
                        return true;
                    }
                    break;
            }
        } else if (event.getAction() == 1) {
            switch (event.getKeyCode()) {
                case 3:
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void onBackPressed() {
        if (isAllAppsVisible()) {
            showWorkspace(true);
        } else if (this.mWorkspace.getOpenFolder() != null) {
            Folder openFolder = this.mWorkspace.getOpenFolder();
            if (openFolder.isEditingName()) {
                openFolder.dismissEditingName();
            } else {
                closeFolder();
            }
        } else {
            this.mWorkspace.exitWidgetResizeMode();
            this.mWorkspace.showOutlinesTemporarily();
        }
    }

    /* access modifiers changed from: private */
    public void onAppWidgetReset() {
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.startListening();
        }
    }

    public void onClick(View v) {
        if (v.getWindowToken() != null && this.mWorkspace.isFinishedSwitchingState()) {
            Object tag = v.getTag();
            if (tag instanceof ShortcutInfo) {
                Intent intent = ((ShortcutInfo) tag).intent;
                int[] pos = new int[2];
                v.getLocationOnScreen(pos);
                intent.setSourceBounds(new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight()));
                if (startActivitySafely(v, intent, tag) && (v instanceof BubbleTextView)) {
                    this.mWaitingForResume = (BubbleTextView) v;
                    this.mWaitingForResume.setStayPressed(true);
                }
            } else if (tag instanceof FolderInfo) {
                if (v instanceof FolderIcon) {
                    handleFolderClick((FolderIcon) v);
                }
            } else if (v == this.mAllAppsButton) {
                if (isAllAppsVisible()) {
                    showWorkspace(true);
                } else {
                    onClickAllAppsButton(v);
                }
            }
            try {
                this.bInLeftFocus = false;
                this.m_iCurrLeftFocus = -1;
                switch (v.getId()) {
                    case R.id.btnShowWorkspace:
                        showWorkspace(true);
                        break;
                    case R.id.navi:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 7;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 8;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 1;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 2;
                                } else {
                                    this.m_iCurrFocus = 2;
                                }
                            } else if (this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 8;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 1;
                                } else {
                                    this.m_iCurrFocus = 2;
                                }
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 7;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                                    this.m_iCurrFocus = 0;
                                } else {
                                    this.m_iCurrFocus = 1;
                                }
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 4;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 0;
                            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                                this.m_iCurrFocus = 0;
                            } else {
                                this.m_iCurrFocus = 1;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            this.m_iCurrFocus = 0;
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                        break;
                    case R.id.bluetooth:
                    case R.id.lanya:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 2;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 7;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 2;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 1;
                                } else {
                                    this.m_iCurrFocus = 1;
                                }
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 2;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                                    this.m_iCurrFocus = 4;
                                } else {
                                    this.m_iCurrFocus = 2;
                                }
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 2;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 4;
                            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                                this.m_iCurrFocus = 1;
                            } else {
                                this.m_iCurrFocus = 4;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.m_iUiIndex == 1) {
                                this.m_iCurrFocus = 6;
                            } else {
                                this.m_iCurrFocus = 4;
                            }
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                        break;
                    case R.id.music:
                    case R.id.Btn_Music:
                    case R.id.ivRightMusicIcon:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 4;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 6;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 0;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 4;
                                } else {
                                    this.m_iCurrFocus = 0;
                                }
                            } else if (this.m_iModeSet == 6) {
                                this.m_iCurrFocus = 6;
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 4;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_id6_main_interface_index == 1) {
                                    this.m_iCurrFocus = 1;
                                } else if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                                    this.m_iCurrFocus = 2;
                                } else {
                                    this.m_iCurrFocus = 0;
                                }
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 0;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 2;
                            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                                this.m_iCurrFocus = 2;
                            } else {
                                this.m_iCurrFocus = 2;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.m_iUiIndex == 1) {
                                this.m_iCurrFocus = 2;
                            } else {
                                this.m_iCurrFocus = 2;
                            }
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                        break;
                    case R.id.video:
                    case R.id.Btn_Video:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 6;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 0;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 4;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 5;
                                } else {
                                    this.m_iCurrFocus = 6;
                                }
                            } else if (this.m_iModeSet == 6) {
                                this.m_iCurrFocus = 0;
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 6;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                this.m_iCurrFocus = 3;
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 5;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 3;
                            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                                this.m_iCurrFocus = 3;
                            } else {
                                this.m_iCurrFocus = 3;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.m_iUiIndex == 1) {
                                this.m_iCurrFocus = 5;
                            } else {
                                this.m_iCurrFocus = 3;
                            }
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                        break;
                    case R.id.setting:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 0;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 2;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 5;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 10;
                                } else {
                                    this.m_iCurrFocus = 8;
                                }
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 0;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                this.m_iCurrFocus = 11;
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 6;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 10;
                            } else {
                                this.m_iCurrFocus = 7;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.m_iUiIndex == 1) {
                                this.m_iCurrFocus = 7;
                            } else {
                                this.m_iCurrFocus = 10;
                            }
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                        break;
                    case R.id.radio:
                        if (LauncherApplication.m_iUITypeVer != 41) {
                            if (LauncherApplication.m_iUITypeVer != 101) {
                                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.RADIO_MODE_PACKAGE_NAME, EventUtils.RADIO_MODE_CLASS_NAME);
                                break;
                            } else {
                                if (this.m_iUiIndex == 1) {
                                    this.m_iCurrFocus = 1;
                                } else {
                                    this.m_iCurrFocus = 1;
                                }
                                if (!EventUtils.getInstallStatus(this, EventUtils.RADIO_MODE_PACKAGE_NAME)) {
                                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.RADIO_DSP_MODE_PACKAGE_NAME, EventUtils.RADIO_DSP_MODE_CLASS_NAME);
                                    break;
                                } else {
                                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.RADIO_MODE_PACKAGE_NAME, EventUtils.RADIO_MODE_CLASS_NAME);
                                    break;
                                }
                            }
                        } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                            if (this.m_i_ksw_evo_main_interface_index == 1) {
                                this.m_iCurrFocus = 11;
                            } else {
                                this.m_iCurrFocus = 5;
                            }
                            this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                            if (this.m_b_ksw_Original_car_video_display) {
                                Intent intt = new Intent(EventUtils.ACTION_SWITCH_USBDVRMODE);
                                intt.putExtra(EventUtils.ACTION_SWITCH_USBDVRMODE_EXTRA, 5);
                                sendBroadcast(intt);
                                break;
                            } else {
                                Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                                break;
                            }
                        }
                    case R.id.aux:
                        if (LauncherApplication.m_iUITypeVer != 41) {
                            if (LauncherApplication.m_iUITypeVer == 101) {
                                this.m_iCurrFocus = 7;
                            }
                            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.auxplayer", "com.szchoiceway.auxplayer.MainActivity");
                            break;
                        } else {
                            if (!(this.m_iModeSet == 1 || this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6)) {
                                if (this.m_iModeSet == 3) {
                                    this.m_iCurrFocus = 5;
                                } else if (this.m_iModeSet == 8) {
                                    this.m_iCurrFocus_chwy = 13;
                                } else if (this.m_iModeSet == 13) {
                                    this.m_iCurrFocus = 7;
                                } else {
                                    this.m_iCurrFocus = 8;
                                }
                            }
                            ksw_enter_aux();
                            break;
                        }
                    case R.id.btnBrowser:
                    case R.id.liulanqi:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 11;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 9;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 11;
                                } else {
                                    this.m_iCurrFocus = 5;
                                }
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 7;
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                                    this.m_iCurrFocus = 6;
                                } else {
                                    this.m_iCurrFocus = 5;
                                }
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 6;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            this.m_iCurrFocus = 6;
                        }
                        if (LauncherApplication.m_iUITypeVer != 41) {
                            EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.EXPLORER_MODE_PACKAGE_NAME, EventUtils.EXPLORER_MODE_CLASS_NAME);
                            break;
                        } else {
                            ksw_enter_browser();
                            break;
                        }
                        break;
                    case R.id.btnCarInfo:
                        EventUtils.startActivityIfNotRuning(this, EventUtils.CANBUS_MODE_PACKAGE_NAME, "com.szchoiceway.DazongCan.Golf7MainActivity");
                        break;
                    case R.id.btnESFile:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6 || this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 9;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 8;
                                } else if (this.m_i_ksw_evo_main_interface_index == 4) {
                                    this.m_iCurrFocus = 10;
                                } else if (this.m_iModeSet != 7 && this.m_iModeSet != 20) {
                                    this.m_iCurrFocus = 3;
                                } else if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                                    this.m_iCurrFocus = 5;
                                } else {
                                    this.m_iCurrFocus = 6;
                                }
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 5;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.bHaveDsp) {
                                this.m_iCurrFocus = 10;
                            } else {
                                this.m_iCurrFocus = 5;
                            }
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                        break;
                    case R.id.phonelink:
                    case R.id.Carlife:
                    case R.id.yilian:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 3;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 4;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 6;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 6;
                                } else {
                                    this.m_iCurrFocus = 10;
                                }
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 3;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                this.m_iCurrFocus = 9;
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 9;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 8;
                            } else {
                                this.m_iCurrFocus = 5;
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            this.m_iCurrFocus = 8;
                        }
                        enter_phoneLink();
                        break;
                    case R.id.apps:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet != 1) {
                                if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                    if (this.m_i_ksw_evo_main_interface_index == 1) {
                                        this.m_iCurrFocus = 5;
                                    } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                        this.m_iCurrFocus = 10;
                                    } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                        this.m_iCurrFocus = 9;
                                    } else {
                                        this.m_iCurrFocus = 11;
                                    }
                                } else if (this.m_iModeSet != 3) {
                                    if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                        this.m_iCurrFocus = 10;
                                    } else if (this.m_iModeSet == 8) {
                                        this.rl_apps.setVisibility(8);
                                        this.rlApps1.setVisibility(0);
                                        this.rlApps2.setVisibility(0);
                                        this.rlApps3.setVisibility(0);
                                        this.rlApps4.setVisibility(0);
                                        if (this.mViewPage.getCurrentItem() % 2 == 0) {
                                            this.m_iCurrFocus_chwy = 3;
                                        } else {
                                            this.m_iCurrFocus_chwy = 10;
                                        }
                                    } else if (this.m_iModeSet == 13) {
                                        this.m_iCurrFocus = 9;
                                    } else {
                                        this.m_iCurrFocus = 0;
                                    }
                                }
                            }
                        } else if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.m_iUiIndex == 1) {
                                this.m_iCurrFocus = 4;
                            } else {
                                this.m_iCurrFocus = 9;
                            }
                        }
                        Log.i(TAG, "*************apps ************");
                        onClickAllAppsButton(v);
                        break;
                    case R.id.atsl:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.example.administrator.atslcarconsole", "com.example.administrator.atslcarconsole.MainActivity");
                        break;
                    case R.id.btMusic:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BTMUSIC_MODE_CLASS_NAME);
                        break;
                    case R.id.tv:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet != 1) {
                                if (this.m_iModeSet != 2 && this.m_iModeSet != 4 && this.m_iModeSet != 6) {
                                    if (this.m_iModeSet != 3) {
                                        if (this.m_iModeSet != 7 && this.m_iModeSet != 20) {
                                            this.m_iCurrFocus = 9;
                                            ksw_enter_cmmb();
                                            break;
                                        } else {
                                            this.m_iCurrFocus = 7;
                                            ksw_enter_cmmb();
                                            break;
                                        }
                                    }
                                } else if (this.m_i_ksw_evo_main_interface_index != 2 && this.m_i_ksw_evo_main_interface_index != 3) {
                                    if (this.m_iModeSet != 6) {
                                        this.m_iCurrFocus = 7;
                                        ksw_enter_cmmb();
                                        break;
                                    } else {
                                        this.m_iCurrFocus = 3;
                                        ksw_enter_cmmb();
                                        break;
                                    }
                                } else {
                                    this.m_iCurrFocus = 8;
                                    ksw_enter_cmmb();
                                    break;
                                }
                            }
                        } else {
                            EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.TV_MODE_PACKAGE_NAME, EventUtils.TV_MODE_CLASS_NAME);
                            break;
                        }
                        break;
                    case R.id.dvr:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 1;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 1;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 7;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 7;
                                } else {
                                    this.m_iCurrFocus = 7;
                                }
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 1;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                this.m_iCurrFocus = 7;
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 12;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 11;
                            } else {
                                this.m_iCurrFocus = 6;
                            }
                        }
                        if (LauncherApplication.m_iUITypeVer != 41) {
                            if (LauncherApplication.m_iUITypeVer != 101) {
                                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.USB_DVR_MODE_PACKAGE_NAME, EventUtils.USB_DVR_MODE_CLASS_NAME);
                                break;
                            } else {
                                this.m_iCurrFocus = 11;
                                if (!EventUtils.getInstallStatus(this, "com.ankai.cardvr")) {
                                    Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                                    break;
                                } else {
                                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.ankai.cardvr", "com.ankai.cardvr.ui.MainActivity");
                                    break;
                                }
                            }
                        } else {
                            ksw_enter_dvr();
                            break;
                        }
                        break;
                    case R.id.OriginaCar:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 1) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 5;
                                }
                            } else if (this.m_iModeSet == 3) {
                                if (!this.m_b_ksw_audio_main_scroll) {
                                    this.m_iCurrFocus = 5;
                                }
                            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 10;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 3;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 0;
                                } else {
                                    this.m_iCurrFocus = 4;
                                }
                            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                                    this.m_iCurrFocus = 1;
                                } else {
                                    this.m_iCurrFocus = 4;
                                }
                            } else if (this.m_iModeSet == 8) {
                                this.m_iCurrFocus_chwy = 1;
                            } else if (this.m_iModeSet == 13) {
                                this.m_iCurrFocus = 1;
                            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                                this.m_iCurrFocus = 4;
                            }
                            this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                        }
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        break;
                    case R.id.yibiaopan:
                        if (LauncherApplication.m_iUITypeVer == 41) {
                            if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6 || this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 3;
                                } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                                    this.m_iCurrFocus = 11;
                                } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                                    this.m_iCurrFocus = 8;
                                } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                                    this.m_iCurrFocus = 8;
                                } else {
                                    this.m_iCurrFocus = 9;
                                }
                            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                                this.m_iCurrFocus = 5;
                            }
                            ksw_enter_yibiaopan();
                            break;
                        }
                        break;
                    case R.id.btnMusicPrev:
                        if (this.app.getEvtService() != null && this.app.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                            sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 3);
                            break;
                        }
                    case R.id.ChkMusicPlayPause:
                        if (this.app.getEvtService() != null) {
                            if (this.app.getEvtService().getValidMode() != EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                                ((CheckBox) v).setChecked(false);
                                break;
                            } else {
                                sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 6);
                                refreshPlayState();
                                break;
                            }
                        }
                        break;
                    case R.id.btnMusicNext:
                        if (this.app.getEvtService() != null && this.app.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                            sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 2);
                            break;
                        }
                    case R.id.BtnSetCalendar:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.android.calendar", "com.android.calendar.LaunchActivity");
                        break;
                    case R.id.btnVideoPrev:
                        if (this.app.getEvtService() != null && this.app.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MOVIE.getIntValue()) {
                            sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 3);
                            break;
                        }
                    case R.id.ChkVideoPlayPause:
                        if (this.app.getEvtService() != null) {
                            if (this.app.getEvtService().getValidMode() != EventUtils.eSrcMode.SRC_MOVIE.getIntValue()) {
                                ((CheckBox) v).setChecked(false);
                                break;
                            } else {
                                sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 6);
                                refreshPlayState();
                                break;
                            }
                        }
                        break;
                    case R.id.btnVideoNext:
                        if (this.app.getEvtService() != null && this.app.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MOVIE.getIntValue()) {
                            sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 2);
                            break;
                        }
                    case R.id.btnCarPlay:
                        this.m_iCurrFocus = 3;
                        Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                        break;
                    case R.id.btnDvd:
                        if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet != 1) {
                            if (this.m_iModeSet != 2 && this.m_iModeSet != 4 && this.m_iModeSet != 6) {
                                if (this.m_iModeSet != 3) {
                                    this.m_iCurrFocus = 10;
                                    ksw_enter_dvd();
                                    break;
                                }
                            } else {
                                if (this.m_i_ksw_evo_main_interface_index == 1) {
                                    this.m_iCurrFocus = 9;
                                } else {
                                    this.m_iCurrFocus = 3;
                                }
                                ksw_enter_dvd();
                                break;
                            }
                        }
                        break;
                    case R.id.dsp:
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            if (this.m_iUiIndex == 1) {
                                this.m_iCurrFocus = 3;
                            } else {
                                this.m_iCurrFocus = 5;
                            }
                        }
                        if (!this.bHaveDsp) {
                            Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                            break;
                        } else {
                            EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.DSP_MODE_PACKAGE_NAME, EventUtils.DSP_MODE_CLASS_NAME);
                            break;
                        }
                    case R.id.btMusicPlayPause:
                        if (this.app.getEvtService() != null && this.app.getEvtService().getValidMode() == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                            sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", 6);
                            refreshPlayState();
                            break;
                        }
                    case R.id.BtnGuanpin:
                        sendBroadcastCanKeyExtra(this.app, "com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT", -9);
                        break;
                    case R.id.btnPagerPriv:
                        this.m_iPageCurrFocus--;
                        if (this.m_iPageCurrFocus < 0) {
                            this.m_iPageCurrFocus = 0;
                        }
                        if (this.mViewPager != null) {
                            this.mViewPager.setCurrentItem(this.m_iPageCurrFocus);
                            break;
                        }
                        break;
                    case R.id.btnPagerNext:
                        this.m_iPageCurrFocus++;
                        if (this.m_iPageCurrFocus > this.mViewPager.getAdapter().getCount()) {
                            this.m_iPageCurrFocus = this.mViewPager.getAdapter().getCount() - 1;
                        }
                        if (this.mViewPager != null) {
                            this.mViewPager.setCurrentItem(this.m_iPageCurrFocus);
                            break;
                        }
                        break;
                    case R.id.btnLeftMusic:
                        this.bInLeftFocus = true;
                        this.m_iCurrLeftFocus = 0;
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                        break;
                    case R.id.btnLeftNavi:
                        this.bInLeftFocus = true;
                        this.m_iCurrLeftFocus = 1;
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                        break;
                    case R.id.btnLeftSetting:
                        this.bInLeftFocus = true;
                        this.m_iCurrLeftFocus = 2;
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                        break;
                    case R.id.btnLeftOriginaCar:
                        this.bInLeftFocus = true;
                        this.m_iCurrLeftFocus = 3;
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        break;
                    case R.id.btnLeftApps:
                        this.bInLeftFocus = true;
                        this.m_iCurrLeftFocus = 4;
                        onClickAllAppsButton(v);
                        break;
                }
                if (LauncherApplication.m_iUITypeVer == 41) {
                    if (this.m_bVerticalPager) {
                        refreshFocusView_Vertical();
                    } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                        refreshFocusView_evo();
                    } else if (this.m_iModeSet == 3) {
                        refreshFocusView_Q5();
                    } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                        refreshFocusView_evo_id6();
                    } else if (this.m_iModeSet == 8) {
                        refreshFocusView_CHWY();
                    } else if (this.m_iModeSet == 13) {
                        refreshFocusView_normal_1920x720();
                    } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                        refreshLeftFocusView();
                        refreshFocusView_evo_id7();
                    } else {
                        refreshFocusView();
                    }
                } else if (LauncherApplication.m_iUITypeVer == 101) {
                    refreshFocusView_normal_1920x720();
                }
            } catch (Exception e) {
                Log.i(TAG, "onClick: e = " + e.toString());
            }
        }
    }

    private void enter_phoneLink() {
        if (EventUtils.getInstallStatus(this, "com.didi365.miudrive.navi")) {
            startActivity(getPackageManager().getLaunchIntentForPackage("com.didi365.miudrive.navi"));
        } else {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.PHONEAPP_MODE_PACKAGE_NAME, EventUtils.PHONEAPP_MODE_CLASS_NAME);
        }
    }

    public static void sendBroadcastCanKeyExtra(Context context, String action, int iExtraData) {
        if (context != null) {
            Intent intt = new Intent(action);
            intt.putExtra("com.choiceway.eventcenter.EventUtils.ZXW_CAN_KEY_EVT_EXTRA", iExtraData);
            Log.i(TAG, "---iExtraData---" + iExtraData);
            context.sendBroadcast(intt);
        }
    }

    /* access modifiers changed from: protected */
    public void refreshPlayState() {
        if (this.app.getEvtService() != null) {
            try {
                if (!this.bIsNavigating) {
                    int iValidMode = this.app.getEvtService().getValidMode();
                    if (iValidMode == EventUtils.eSrcMode.SRC_MUSIC.getIntValue() || iValidMode == EventUtils.eSrcMode.SRC_MOVIE.getIntValue()) {
                        if (iValidMode == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                            if (this.mChkMusicPlayPause != null) {
                                if (this.isPlay) {
                                    this.mChkMusicPlayPause.setChecked(true);
                                } else {
                                    this.mChkMusicPlayPause.setChecked(false);
                                }
                            }
                            if (this.mChkVideoPlayPause != null) {
                                this.mChkVideoPlayPause.setChecked(false);
                            }
                        } else if (iValidMode == EventUtils.eSrcMode.SRC_MOVIE.getIntValue()) {
                            if (this.mChkVideoPlayPause != null) {
                                if (this.isPlay) {
                                    this.mChkVideoPlayPause.setChecked(true);
                                } else {
                                    this.mChkVideoPlayPause.setChecked(false);
                                }
                            }
                            if (this.mChkMusicPlayPause != null) {
                                this.mChkMusicPlayPause.setChecked(false);
                            }
                        }
                        if (isResumed()) {
                            if (iValidMode == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                                this.mMyhandler.removeMessages(1004);
                                this.mMyhandler.sendEmptyMessageDelayed(1004, 500);
                            }
                        } else if (iValidMode == EventUtils.eSrcMode.SRC_MUSIC.getIntValue()) {
                            this.mMyhandler.removeMessages(1004);
                            this.mMyhandler.sendEmptyMessageDelayed(1004, 0);
                            onShowMusicInfo(false);
                        }
                    } else {
                        onShowMusicInfo(false);
                        if (this.mChkMusicPlayPause != null) {
                            this.mChkMusicPlayPause.setChecked(false);
                        }
                        if (this.mChkVideoPlayPause != null) {
                            this.mChkVideoPlayPause.setChecked(false);
                        }
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onShowMusicInfo(boolean bShow) {
        RelativeLayout musicInfo = (RelativeLayout) findViewById(R.id.rl_yinyuedi);
        TextView tvMusicTitleInfor = (TextView) findViewById(R.id.tv_MusicTitleInfor);
        TextView tvMusicArtistInfor = (TextView) findViewById(R.id.tv_MusicArtistInfor);
        TextView tvMusicAblumInfor = (TextView) findViewById(R.id.tv_MusicAblumInfor);
        TextView tvCurrTime = (TextView) findViewById(R.id.TvCurrTime);
        TextView tvTotTime = (TextView) findViewById(R.id.TvTotTime);
        SeekBar skBarProgress = (SeekBar) findViewById(R.id.SkBarProgress);
        ImageView ivMusicIcon = (ImageView) findViewById(R.id.iv_music_icon);
        Button btMusicPrev = (Button) findViewById(R.id.btnMusicPrev);
        Button btMusicNext = (Button) findViewById(R.id.btnMusicNext);
        Button btMusicPlayPause = (Button) findViewById(R.id.btMusicPlayPause);
        LauncherApplication app2 = (LauncherApplication) getApplication();
        Log.i(TAG, "onShowMusicInfo: bShow = " + bShow);
        if (bShow) {
            try {
                if (app2.getEvtService() != null) {
                    this.validModeTitleInfor = app2.getEvtService().getValidModeTitleInfor();
                    this.validModeArtistInfor = app2.getEvtService().getValidModeArtistInfor();
                    this.validModeAblumInfor = app2.getEvtService().getValidModeAblumInfor();
                    this.validModeCurTime = app2.getEvtService().getValidCurTime();
                    this.validModeTotTime = app2.getEvtService().getValidTotTime();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            if (musicInfo != null) {
                musicInfo.setVisibility(0);
            }
            if (tvMusicTitleInfor != null) {
                tvMusicTitleInfor.setFocusable(true);
                Log.i(TAG, "onShowMusicInfo: validModeTitleInfor2 = " + this.validModeTitleInfor);
                if ("".equals(tvMusicTitleInfor.getText().toString()) || !this.validModeTitleInfor.equals(tvMusicTitleInfor.getText().toString())) {
                    tvMusicTitleInfor.setText(this.validModeTitleInfor);
                    tvMusicTitleInfor.setFocusable(true);
                    Log.i(TAG, "onShowMusicInfo: validModeTitleInfor = " + tvMusicTitleInfor.getText().toString());
                    Log.i(TAG, "onShowMusicInfo: validModeArtistInfor = " + this.validModeArtistInfor);
                    Log.i(TAG, "onShowMusicInfo: validModeAblumInfor = " + this.validModeAblumInfor);
                    Log.i(TAG, "onShowMusicInfo: validModeCurTime = " + this.validModeCurTime);
                    Log.i(TAG, "onShowMusicInfo: validModeTotTime = " + this.validModeTotTime);
                }
            }
            if (tvMusicArtistInfor != null) {
                tvMusicArtistInfor.setText(this.validModeArtistInfor);
            }
            if (tvMusicAblumInfor != null) {
                tvMusicAblumInfor.setText(this.validModeAblumInfor);
            }
            if (btMusicPrev != null) {
                btMusicPrev.setOnClickListener(this);
            }
            if (btMusicNext != null) {
                btMusicNext.setOnClickListener(this);
            }
            if (btMusicPlayPause != null) {
                btMusicPlayPause.setOnClickListener(this);
            }
            if (tvCurrTime != null) {
                tvCurrTime.setText(EventUtils.getProgressFromPosition(this.validModeCurTime));
            }
            if (tvTotTime != null) {
                tvTotTime.setText(EventUtils.getProgressFromPosition(this.validModeTotTime));
            }
            if (skBarProgress != null) {
                skBarProgress.setMax(this.validModeTotTime);
                skBarProgress.setProgress(this.validModeCurTime);
                return;
            }
            return;
        }
        if (musicInfo != null) {
            musicInfo.setVisibility(8);
        }
        if (tvMusicTitleInfor != null) {
            tvMusicTitleInfor.setText("");
        }
        if (tvMusicArtistInfor != null) {
            tvMusicArtistInfor.setText("");
        }
        Log.i(TAG, "onShowMusicInfo: tvMusicAblumInfor = " + tvMusicAblumInfor);
        if (tvMusicAblumInfor != null) {
            tvMusicAblumInfor.setText("");
        }
        if (tvCurrTime != null) {
            tvCurrTime.setText("00:00:00");
        }
        if (tvTotTime != null) {
            tvTotTime.setText("00:00:00");
        }
        if (skBarProgress != null) {
            skBarProgress.setMax(0);
            skBarProgress.setProgress(0);
        }
        if (ivMusicIcon != null) {
            ivMusicIcon.setImageBitmap((Bitmap) null);
            ivMusicIcon.setBackground(getResources().getDrawable(R.drawable.chwy_yinyuexianshi));
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent: ");
        if (this.mGestureDetector.onTouchEvent(event)) {
            return true;
        }
        switch (event.getAction() & 255) {
            case 1:
                Intent intent = new Intent("com.choiceway.action.dsp_vol");
                intent.putExtra("com.choiceway.action.dsp_vol_extra", "dsp_vol_hide");
                sendBroadcast(intent);
                break;
        }
        return super.onTouchEvent(event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        Display disp;
        float iSlipStepping;
        int startX;
        int startY;
        int windowHeight;
        int windowWidth;

        private MyGestureListener() {
            this.iSlipStepping = 50.0f;
            this.startX = 0;
            this.startY = 0;
            this.disp = Launcher.this.getWindowManager().getDefaultDisplay();
            this.windowWidth = this.disp.getWidth();
            this.windowHeight = this.disp.getHeight();
        }

        public boolean onDoubleTap(MotionEvent e) {
            Log.i(Launcher.TAG, "MyGestureListener-onDoubleTap: 双击");
            return true;
        }

        public boolean onDown(MotionEvent event) {
            this.startX = (int) event.getRawX();
            this.startY = (int) event.getRawY();
            Log.i(Launcher.TAG, "MyGestureListener-onDown: startY = " + this.startY);
            return super.onDown(event);
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int currentX = (int) e2.getRawX();
            int currentY = (int) e2.getRawY();
            int i = currentX - this.startX;
            int dy = currentY - this.startY;
            if (this.startY > 86) {
                if (dy < 0) {
                    if (((float) Math.abs(dy)) > this.iSlipStepping) {
                        Intent intent = new Intent("com.choiceway.action.dsp_vol");
                        intent.putExtra("com.choiceway.action.dsp_vol_extra", "dsp_vol_add");
                        Launcher.this.sendBroadcast(intent);
                        this.startX = currentX;
                        this.startY = currentY;
                    }
                } else if (((float) Math.abs(dy)) > this.iSlipStepping) {
                    Intent intent2 = new Intent("com.choiceway.action.dsp_vol");
                    intent2.putExtra("com.choiceway.action.dsp_vol_extra", "dsp_vol_sub");
                    Launcher.this.sendBroadcast(intent2);
                    this.startX = currentX;
                    this.startY = currentY;
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    public void onClickSearchButton(View v) {
    }

    public void onClickVoiceButton(View v) {
    }

    public void onClickAllAppsButton(View v) {
        showAllApps(true);
    }

    public void onTouchDownAllAppsButton(View v) {
        v.performHapticFeedback(1);
    }

    public void onClickAppMarketButton(View v) {
        if (this.mAppMarketIntent != null) {
            startActivitySafely(v, this.mAppMarketIntent, "app market");
        } else {
            Log.e(TAG, "Invalid app market intent.");
        }
    }

    /* access modifiers changed from: package-private */
    public void startApplicationDetailsActivity(ComponentName componentName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", Uri.fromParts("package", componentName.getPackageName(), (String) null));
        intent.setFlags(276824064);
        startActivitySafely((View) null, intent, "startApplicationDetailsActivity");
    }

    /* access modifiers changed from: package-private */
    public void startApplicationUninstallActivity(ApplicationInfo appInfo) {
        if ((appInfo.flags & 1) == 0) {
            Toast.makeText(this, R.string.uninstall_system_app_text, 0).show();
            return;
        }
        Intent intent = new Intent("android.intent.action.DELETE", Uri.fromParts("package", appInfo.componentName.getPackageName(), appInfo.componentName.getClassName()));
        intent.setFlags(276824064);
        startActivity(intent);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x002d A[Catch:{ SecurityException -> 0x0031 }] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0014 A[Catch:{ SecurityException -> 0x0031 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startActivity(android.view.View r10, android.content.Intent r11, java.lang.Object r12) {
        /*
            r9 = this;
            r4 = 1
            r3 = 0
            r5 = 268435456(0x10000000, float:2.5243549E-29)
            r11.addFlags(r5)
            if (r10 == 0) goto L_0x002b
            java.lang.String r5 = "com.szchoiceway.index.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION"
            boolean r5 = r11.hasExtra(r5)     // Catch:{ SecurityException -> 0x0031 }
            if (r5 != 0) goto L_0x002b
            r2 = r4
        L_0x0012:
            if (r2 == 0) goto L_0x002d
            r5 = 0
            r6 = 0
            int r7 = r10.getMeasuredWidth()     // Catch:{ SecurityException -> 0x0031 }
            int r8 = r10.getMeasuredHeight()     // Catch:{ SecurityException -> 0x0031 }
            android.app.ActivityOptions r1 = android.app.ActivityOptions.makeScaleUpAnimation(r10, r5, r6, r7, r8)     // Catch:{ SecurityException -> 0x0031 }
            android.os.Bundle r5 = r1.toBundle()     // Catch:{ SecurityException -> 0x0031 }
            r9.startActivity(r11, r5)     // Catch:{ SecurityException -> 0x0031 }
        L_0x0029:
            r3 = r4
        L_0x002a:
            return r3
        L_0x002b:
            r2 = r3
            goto L_0x0012
        L_0x002d:
            r9.startActivity(r11)     // Catch:{ SecurityException -> 0x0031 }
            goto L_0x0029
        L_0x0031:
            r0 = move-exception
            r4 = 2131099651(0x7f060003, float:1.7811661E38)
            android.widget.Toast r4 = android.widget.Toast.makeText(r9, r4, r3)
            r4.show()
            java.lang.String r4 = "Launcher"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Launcher does not have the permission to launch "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r5 = r5.append(r11)
            java.lang.String r6 = ". Make sure to create a MAIN intent-filter for the corresponding activity "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = "or use the exported attribute for this activity. "
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.String r6 = "tag="
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r5 = r5.append(r12)
            java.lang.String r6 = " intent="
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r5 = r5.append(r11)
            java.lang.String r5 = r5.toString()
            android.util.Log.e(r4, r5, r0)
            goto L_0x002a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.Launcher.startActivity(android.view.View, android.content.Intent, java.lang.Object):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean startActivitySafely(View v, Intent intent, Object tag) {
        try {
            return startActivity(v, intent, tag);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Unable to launch. tag=" + tag + " intent=" + intent, e);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public void startActivityForResultSafely(Intent intent, int requestCode) {
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
        } catch (SecurityException e2) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Launcher does not have the permission to launch " + intent + ". Make sure to create a MAIN intent-filter for the corresponding activity " + "or use the exported attribute for this activity.", e2);
        }
    }

    private void handleFolderClick(FolderIcon folderIcon) {
        FolderInfo info = folderIcon.getFolderInfo();
        Folder openFolder = this.mWorkspace.getFolderForTag(info);
        if (info.opened && openFolder == null) {
            Log.d(TAG, "Folder info marked as open, but associated folder is not open. Screen: " + info.screen + " (" + info.cellX + ", " + info.cellY + ")");
            info.opened = false;
        }
        if (!info.opened && !folderIcon.getFolder().isDestroyed()) {
            closeFolder();
            openFolder(folderIcon);
        } else if (openFolder != null) {
            int folderScreen = this.mWorkspace.getPageForView(openFolder);
            closeFolder(openFolder);
            if (folderScreen != this.mWorkspace.getCurrentPage()) {
                closeFolder();
                openFolder(folderIcon);
            }
        }
    }

    private void copyFolderIconToImage(FolderIcon fi) {
        DragLayer.LayoutParams lp;
        int width = fi.getMeasuredWidth();
        int height = fi.getMeasuredHeight();
        if (this.mFolderIconImageView == null) {
            this.mFolderIconImageView = new ImageView(this);
        }
        if (!(this.mFolderIconBitmap != null && this.mFolderIconBitmap.getWidth() == width && this.mFolderIconBitmap.getHeight() == height)) {
            this.mFolderIconBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.mFolderIconCanvas = new Canvas(this.mFolderIconBitmap);
        }
        if (this.mFolderIconImageView.getLayoutParams() instanceof DragLayer.LayoutParams) {
            lp = (DragLayer.LayoutParams) this.mFolderIconImageView.getLayoutParams();
        } else {
            lp = new DragLayer.LayoutParams(width, height);
        }
        float scale = this.mDragLayer.getDescendantRectRelativeToSelf(fi, this.mRectForFolderAnimation);
        lp.customPosition = true;
        lp.x = this.mRectForFolderAnimation.left;
        lp.y = this.mRectForFolderAnimation.top;
        lp.width = (int) (((float) width) * scale);
        lp.height = (int) (((float) height) * scale);
        this.mFolderIconCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        fi.draw(this.mFolderIconCanvas);
        this.mFolderIconImageView.setImageBitmap(this.mFolderIconBitmap);
        if (fi.getFolder() != null) {
            this.mFolderIconImageView.setPivotX(fi.getFolder().getPivotXForIconAnimation());
            this.mFolderIconImageView.setPivotY(fi.getFolder().getPivotYForIconAnimation());
        }
        if (this.mDragLayer.indexOfChild(this.mFolderIconImageView) != -1) {
            this.mDragLayer.removeView(this.mFolderIconImageView);
        }
        this.mDragLayer.addView(this.mFolderIconImageView, lp);
        if (fi.getFolder() != null) {
            fi.getFolder().bringToFront();
        }
    }

    private void growAndFadeOutFolderIcon(FolderIcon fi) {
        if (fi != null) {
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", new float[]{0.0f});
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", new float[]{1.5f});
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", new float[]{1.5f});
            if (((FolderInfo) fi.getTag()).container == -101) {
                CellLayout.LayoutParams lp = (CellLayout.LayoutParams) fi.getLayoutParams();
                ((CellLayout) fi.getParent().getParent()).setFolderLeaveBehindCell(lp.cellX, lp.cellY);
            }
            copyFolderIconToImage(fi);
            fi.setVisibility(4);
            ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderIconImageView, alpha, scaleX, scaleY);
            oa.setDuration((long) getResources().getInteger(R.integer.config_folderAnimDuration));
            oa.start();
        }
    }

    private void shrinkAndFadeInFolderIcon(final FolderIcon fi) {
        if (fi != null) {
            PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", new float[]{1.0f});
            PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", new float[]{1.0f});
            PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", new float[]{1.0f});
            final CellLayout cl = (CellLayout) fi.getParent().getParent();
            this.mDragLayer.removeView(this.mFolderIconImageView);
            copyFolderIconToImage(fi);
            ObjectAnimator oa = LauncherAnimUtils.ofPropertyValuesHolder(this.mFolderIconImageView, alpha, scaleX, scaleY);
            oa.setDuration((long) getResources().getInteger(R.integer.config_folderAnimDuration));
            oa.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (cl != null) {
                        cl.clearFolderLeaveBehind();
                        Launcher.this.mDragLayer.removeView(Launcher.this.mFolderIconImageView);
                        fi.setVisibility(0);
                    }
                }
            });
            oa.start();
        }
    }

    public void openFolder(FolderIcon folderIcon) {
        Folder folder = folderIcon.getFolder();
        folder.mInfo.opened = true;
        if (folder.getParent() == null) {
            this.mDragLayer.addView(folder);
            this.mDragController.addDropTarget(folder);
        } else {
            Log.w(TAG, "Opening folder (" + folder + ") which already has a parent (" + folder.getParent() + ").");
        }
        folder.animateOpen();
        growAndFadeOutFolderIcon(folderIcon);
        folder.sendAccessibilityEvent(32);
        getDragLayer().sendAccessibilityEvent(2048);
    }

    public void closeFolder() {
        Folder folder = this.mWorkspace.getOpenFolder();
        if (folder != null) {
            if (folder.isEditingName()) {
                folder.dismissEditingName();
            }
            closeFolder(folder);
            dismissFolderCling((View) null);
        }
    }

    /* access modifiers changed from: package-private */
    public void closeFolder(Folder folder) {
        folder.getInfo().opened = false;
        if (((ViewGroup) folder.getParent().getParent()) != null) {
            shrinkAndFadeInFolderIcon((FolderIcon) this.mWorkspace.getViewForTag(folder.mInfo));
        }
        folder.animateClosed();
        getDragLayer().sendAccessibilityEvent(32);
    }

    public boolean onLongClick(View v) {
        boolean allowLongPress;
        if (!isDraggingEnabled() || isWorkspaceLocked() || this.mState != State.WORKSPACE) {
            return false;
        }
        if (!(v instanceof CellLayout)) {
            v = (View) v.getParent().getParent();
        }
        resetAddInfo();
        CellLayout.CellInfo longClickCellInfo = (CellLayout.CellInfo) v.getTag();
        if (longClickCellInfo == null) {
            return true;
        }
        View itemUnderLongClick = longClickCellInfo.cell;
        if (isHotseatLayout(v) || this.mWorkspace.allowLongPress()) {
            allowLongPress = true;
        } else {
            allowLongPress = false;
        }
        if (allowLongPress && !this.mDragController.isDragging()) {
            if (itemUnderLongClick == null) {
                this.mWorkspace.performHapticFeedback(0, 1);
            } else if (!(itemUnderLongClick instanceof Folder)) {
                this.mWorkspace.startDrag(longClickCellInfo);
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isHotseatLayout(View layout) {
        return this.mHotseat != null && layout != null && (layout instanceof CellLayout) && layout == this.mHotseat.getLayout();
    }

    /* access modifiers changed from: package-private */
    public Hotseat getHotseat() {
        return this.mHotseat;
    }

    /* access modifiers changed from: package-private */
    public SearchDropTargetBar getSearchBar() {
        return this.mSearchDropTargetBar;
    }

    /* access modifiers changed from: package-private */
    public CellLayout getCellLayout(long container, int screen) {
        if (container != -101) {
            return (CellLayout) this.mWorkspace.getChildAt(screen);
        }
        if (this.mHotseat != null) {
            return this.mHotseat.getLayout();
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public Workspace getWorkspace() {
        return this.mWorkspace;
    }

    public boolean isAllAppsVisible() {
        return this.mState == State.APPS_CUSTOMIZE || this.mOnResumeState == State.APPS_CUSTOMIZE;
    }

    public boolean isAllAppsButtonRank(int rank) {
        return this.mHotseat.isAllAppsButtonRank(rank);
    }

    /* access modifiers changed from: private */
    public void setPivotsForZoom(View view, float scaleFactor) {
        view.setPivotX(((float) view.getWidth()) / scaleFactor);
        view.setPivotY(((float) view.getHeight()) / scaleFactor);
    }

    private void setWorkspaceBackground(boolean workspace) {
        this.mLauncherView.setBackground((Drawable) null);
    }

    /* access modifiers changed from: package-private */
    public void updateWallpaperVisibility(boolean visible) {
        if (1048576 != (getWindow().getAttributes().flags & 1048576)) {
            getWindow().setFlags(1048576, 1048576);
        }
        setWorkspaceBackground(visible);
        if (!visible) {
            updateWorkspaceVisiblity(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void updateWorkspaceVisiblity(boolean visible) {
        if (this.mWorkspace != null) {
            this.mWorkspace.setVisibility(visible ? 0 : 4);
        }
    }

    private void dispatchOnLauncherTransitionPrepare(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionPrepare(this, animated, toWorkspace);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnLauncherTransitionStart(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStart(this, animated, toWorkspace);
        }
        dispatchOnLauncherTransitionStep(v, 0.0f);
    }

    /* access modifiers changed from: private */
    public void dispatchOnLauncherTransitionStep(View v, float t) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionStep(this, t);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchOnLauncherTransitionEnd(View v, boolean animated, boolean toWorkspace) {
        if (v instanceof LauncherTransitionable) {
            ((LauncherTransitionable) v).onLauncherTransitionEnd(this, animated, toWorkspace);
        }
        dispatchOnLauncherTransitionStep(v, 1.0f);
    }

    private void showAppsCustomizeHelper(boolean animated, boolean springLoaded) {
        if (this.mStateAnimation != null) {
            this.mStateAnimation.setDuration(0);
            this.mStateAnimation.cancel();
            this.mStateAnimation = null;
        }
        Resources res = getResources();
        int duration = res.getInteger(R.integer.config_appsCustomizeZoomInTime);
        int fadeDuration = res.getInteger(R.integer.config_appsCustomizeFadeInTime);
        final float scale = (float) res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = this.mWorkspace;
        final AppsCustomizeTabHost toView = this.mAppsCustomizeTabHost;
        int startDelay = res.getInteger(R.integer.config_workspaceAppsCustomizeAnimationStagger);
        setPivotsForZoom(toView, scale);
        Animator workspaceAnim = this.mWorkspace.getChangeStateAnimation(Workspace.State.SMALL, animated);
        hideHotseat(animated);
        ShowHideScreen2(false);
        Log.i(TAG, "showAppsCustomizeHelper ****** ");
        if (animated) {
            toView.setScaleX(scale);
            toView.setScaleY(scale);
            LauncherViewPropertyAnimator launcherViewPropertyAnimator = new LauncherViewPropertyAnimator(toView);
            launcherViewPropertyAnimator.scaleX(1.0f).scaleY(1.0f).setDuration((long) duration).setInterpolator(new Workspace.ZoomOutInterpolator());
            toView.setVisibility(0);
            toView.setAlpha(0.0f);
            ObjectAnimator alphaAnim = LauncherAnimUtils.ofFloat(toView, "alpha", 0.0f, 1.0f).setDuration((long) fadeDuration);
            alphaAnim.setInterpolator(new DecelerateInterpolator(1.5f));
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation == null) {
                        throw new RuntimeException("animation is null");
                    }
                    float t = ((Float) animation.getAnimatedValue()).floatValue();
                    Launcher.this.dispatchOnLauncherTransitionStep(fromView, t);
                    Launcher.this.dispatchOnLauncherTransitionStep(toView, t);
                }
            });
            this.mStateAnimation = LauncherAnimUtils.createAnimatorSet();
            this.mStateAnimation.play(launcherViewPropertyAnimator).after((long) startDelay);
            this.mStateAnimation.play(alphaAnim).after((long) startDelay);
            this.mHotseat.animate().start();
            final boolean z = animated;
            final boolean z2 = springLoaded;
            this.mStateAnimation.addListener(new AnimatorListenerAdapter() {
                boolean animationCancelled = false;

                public void onAnimationStart(Animator animation) {
                    Launcher.this.updateWallpaperVisibility(true);
                    toView.setTranslationX(0.0f);
                    toView.setTranslationY(0.0f);
                    toView.setVisibility(0);
                    toView.bringToFront();
                }

                public void onAnimationEnd(Animator animation) {
                    Launcher.this.dispatchOnLauncherTransitionEnd(fromView, z, false);
                    Launcher.this.dispatchOnLauncherTransitionEnd(toView, z, false);
                    if (Launcher.this.mWorkspace != null && !z2 && !LauncherApplication.isScreenLarge()) {
                        Launcher.this.mWorkspace.hideScrollingIndicator(true);
                        Launcher.this.hideDockDivider();
                    }
                    if (!this.animationCancelled) {
                        Launcher.this.updateWallpaperVisibility(false);
                    }
                    if (Launcher.this.mSearchDropTargetBar != null) {
                        Launcher.this.mSearchDropTargetBar.hideSearchBar(false);
                    }
                }

                public void onAnimationCancel(Animator animation) {
                    this.animationCancelled = true;
                }
            });
            if (workspaceAnim != null) {
                this.mStateAnimation.play(workspaceAnim);
            }
            boolean delayAnim = false;
            dispatchOnLauncherTransitionPrepare(fromView, animated, false);
            dispatchOnLauncherTransitionPrepare(toView, animated, false);
            if (toView.getContent().getMeasuredWidth() == 0 || this.mWorkspace.getMeasuredWidth() == 0 || toView.getMeasuredWidth() == 0) {
                delayAnim = true;
            }
            final AnimatorSet stateAnimation = this.mStateAnimation;
            final AppsCustomizeTabHost appsCustomizeTabHost = toView;
            final View view = fromView;
            final boolean z3 = animated;
            final Runnable startAnimRunnable = new Runnable() {
                public void run() {
                    if (Launcher.this.mStateAnimation == stateAnimation) {
                        Launcher.this.setPivotsForZoom(appsCustomizeTabHost, scale);
                        Launcher.this.dispatchOnLauncherTransitionStart(view, z3, false);
                        Launcher.this.dispatchOnLauncherTransitionStart(appsCustomizeTabHost, z3, false);
                        LauncherAnimUtils.startAnimationAfterNextDraw(Launcher.this.mStateAnimation, appsCustomizeTabHost);
                    }
                }
            };
            if (delayAnim) {
                toView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        startAnimRunnable.run();
                        toView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
                return;
            }
            startAnimRunnable.run();
            return;
        }
        toView.setTranslationX(0.0f);
        toView.setTranslationY(0.0f);
        toView.setScaleX(1.0f);
        toView.setScaleY(1.0f);
        toView.setVisibility(0);
        toView.bringToFront();
        if (!springLoaded && !LauncherApplication.isScreenLarge()) {
            this.mWorkspace.hideScrollingIndicator(true);
            hideDockDivider();
            if (this.mSearchDropTargetBar != null) {
                this.mSearchDropTargetBar.hideSearchBar(false);
            }
        }
        dispatchOnLauncherTransitionPrepare(fromView, animated, false);
        dispatchOnLauncherTransitionStart(fromView, animated, false);
        dispatchOnLauncherTransitionEnd(fromView, animated, false);
        dispatchOnLauncherTransitionPrepare(toView, animated, false);
        dispatchOnLauncherTransitionStart(toView, animated, false);
        dispatchOnLauncherTransitionEnd(toView, animated, false);
        updateWallpaperVisibility(false);
    }

    private void hideAppsCustomizeHelper(State toState, boolean animated, boolean springLoaded, Runnable onCompleteRunnable) {
        if (this.mStateAnimation != null) {
            this.mStateAnimation.setDuration(0);
            this.mStateAnimation.cancel();
            this.mStateAnimation = null;
        }
        Resources res = getResources();
        int duration = res.getInteger(R.integer.config_appsCustomizeZoomOutTime);
        int fadeOutDuration = res.getInteger(R.integer.config_appsCustomizeFadeOutTime);
        float scaleFactor = (float) res.getInteger(R.integer.config_appsCustomizeZoomScaleFactor);
        final View fromView = this.mAppsCustomizeTabHost;
        final View toView = this.mWorkspace;
        Animator workspaceAnim = null;
        if (toState == State.WORKSPACE) {
            workspaceAnim = this.mWorkspace.getChangeStateAnimation(Workspace.State.NORMAL, animated, res.getInteger(R.integer.config_appsCustomizeWorkspaceAnimationStagger));
        } else if (toState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            workspaceAnim = this.mWorkspace.getChangeStateAnimation(Workspace.State.SPRING_LOADED, animated);
        }
        setPivotsForZoom(fromView, scaleFactor);
        updateWallpaperVisibility(true);
        showHotseat(animated);
        ShowHideScreen2(true);
        Log.i(TAG, "hideAppsCustomizeHelper ****** ");
        if (animated) {
            LauncherViewPropertyAnimator scaleAnim = new LauncherViewPropertyAnimator(fromView);
            scaleAnim.scaleX(scaleFactor).scaleY(scaleFactor).setDuration((long) duration).setInterpolator(new Workspace.ZoomInInterpolator());
            ObjectAnimator alphaAnim = LauncherAnimUtils.ofFloat(fromView, "alpha", 1.0f, 0.0f).setDuration((long) fadeOutDuration);
            alphaAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    float t = 1.0f - ((Float) animation.getAnimatedValue()).floatValue();
                    Launcher.this.dispatchOnLauncherTransitionStep(fromView, t);
                    Launcher.this.dispatchOnLauncherTransitionStep(toView, t);
                }
            });
            this.mStateAnimation = LauncherAnimUtils.createAnimatorSet();
            dispatchOnLauncherTransitionPrepare(fromView, animated, true);
            dispatchOnLauncherTransitionPrepare(toView, animated, true);
            this.mAppsCustomizeContent.pauseScrolling();
            final boolean z = animated;
            final Runnable runnable = onCompleteRunnable;
            this.mStateAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    Launcher.this.updateWallpaperVisibility(true);
                    fromView.setVisibility(8);
                    Launcher.this.dispatchOnLauncherTransitionEnd(fromView, z, true);
                    Launcher.this.dispatchOnLauncherTransitionEnd(toView, z, true);
                    if (Launcher.this.mWorkspace != null) {
                        Launcher.this.mWorkspace.hideScrollingIndicator(false);
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                    Launcher.this.mAppsCustomizeContent.updateCurrentPageScroll();
                    Launcher.this.mAppsCustomizeContent.resumeScrolling();
                }
            });
            this.mStateAnimation.playTogether(new Animator[]{scaleAnim, alphaAnim});
            if (workspaceAnim != null) {
                this.mStateAnimation.play(workspaceAnim);
            }
            this.mHotseat.animate().start();
            dispatchOnLauncherTransitionStart(fromView, animated, true);
            dispatchOnLauncherTransitionStart(toView, animated, true);
            LauncherAnimUtils.startAnimationAfterNextDraw(this.mStateAnimation, toView);
            return;
        }
        fromView.setVisibility(8);
        dispatchOnLauncherTransitionPrepare(fromView, animated, true);
        dispatchOnLauncherTransitionStart(fromView, animated, true);
        dispatchOnLauncherTransitionEnd(fromView, animated, true);
        dispatchOnLauncherTransitionPrepare(toView, animated, true);
        dispatchOnLauncherTransitionStart(toView, animated, true);
        dispatchOnLauncherTransitionEnd(toView, animated, true);
        this.mWorkspace.hideScrollingIndicator(false);
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= 60) {
            this.mAppsCustomizeTabHost.onTrimMemory();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if (!hasFocus) {
            updateWallpaperVisibility(true);
        } else {
            this.mWorkspace.postDelayed(new Runnable() {
                public void run() {
                    LauncherApplication app = (LauncherApplication) Launcher.this.getApplication();
                    if (app == null || app.m_iSetUIType == 5) {
                    }
                }
            }, 500);
        }
    }

    /* access modifiers changed from: package-private */
    public void showWorkspace(boolean animated) {
        showWorkspace(animated, (Runnable) null);
    }

    /* access modifiers changed from: package-private */
    public void showWorkspace(boolean animated, Runnable onCompleteRunnable) {
        boolean z = false;
        if (this.mState != State.WORKSPACE) {
            if (LauncherApplication.m_iUITypeVer == 41) {
            }
            boolean wasInSpringLoadedMode = this.mState == State.APPS_CUSTOMIZE_SPRING_LOADED;
            this.mWorkspace.setVisibility(0);
            hideAppsCustomizeHelper(State.WORKSPACE, animated, false, onCompleteRunnable);
            if (this.mSearchDropTargetBar != null) {
                this.mSearchDropTargetBar.showSearchBar(wasInSpringLoadedMode);
            }
            if (animated && wasInSpringLoadedMode) {
                z = true;
            }
            showDockDivider(z);
            if (this.mAllAppsButton != null) {
                this.mAllAppsButton.requestFocus();
            }
        }
        this.mWorkspace.flashScrollingIndicator(animated);
        this.mState = State.WORKSPACE;
        this.mUserPresent = true;
        updateRunning();
        getWindow().getDecorView().sendAccessibilityEvent(32);
        if (LauncherApplication.m_iUITypeVer == 41 || LauncherApplication.m_iUITypeVer == 101) {
            if (Build.VERSION.SDK_INT < 26) {
                this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "0");
            }
            this.mSysProviderOpt.updateRecord(SysProviderOpt.LAUNCHER_APPS_CUSTOMIZE_RESUM, "0");
        }
    }

    /* access modifiers changed from: package-private */
    public void showAllApps(boolean animated) {
        if (this.mState == State.WORKSPACE) {
            this.mAppsCustomizeContent.setCurFocusView((View) null);
            showAppsCustomizeHelper(animated, false);
            this.mAppsCustomizeTabHost.requestFocus();
            this.mState = State.APPS_CUSTOMIZE;
            this.mUserPresent = false;
            updateRunning();
            closeFolder();
            getWindow().getDecorView().sendAccessibilityEvent(32);
            if (LauncherApplication.m_iUITypeVer == 41 || LauncherApplication.m_iUITypeVer == 101) {
                if (Build.VERSION.SDK_INT < 26) {
                    this.mSysProviderOpt.updateRecord(SysProviderOpt.MAISILUO_LAUNCHER_APPS_CUSTOMIZE_RESUM, "1");
                }
                this.mSysProviderOpt.updateRecord(SysProviderOpt.LAUNCHER_APPS_CUSTOMIZE_RESUM, "1");
                if (LauncherApplication.m_iUITypeVer == 41) {
                    Intent intt = new Intent(EventUtils.ZXW_SENDBROADCAST8902MOD);
                    intt.putExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_EXTRA, 22);
                    sendBroadcast(intt);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void enterSpringLoadedDragMode() {
        if (isAllAppsVisible()) {
            hideAppsCustomizeHelper(State.APPS_CUSTOMIZE_SPRING_LOADED, true, true, (Runnable) null);
            hideDockDivider();
            this.mState = State.APPS_CUSTOMIZE_SPRING_LOADED;
        }
    }

    /* access modifiers changed from: package-private */
    public void exitSpringLoadedDragModeDelayed(final boolean successfulDrop, boolean extendedDelay, final Runnable onCompleteRunnable) {
        if (this.mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    if (successfulDrop) {
                        Launcher.this.mAppsCustomizeTabHost.setVisibility(8);
                        Launcher.this.showWorkspace(true, onCompleteRunnable);
                        return;
                    }
                    Launcher.this.exitSpringLoadedDragMode();
                }
            }, (long) (extendedDelay ? EXIT_SPRINGLOADED_MODE_LONG_TIMEOUT : 300));
        }
    }

    /* access modifiers changed from: package-private */
    public void exitSpringLoadedDragMode() {
        if (this.mState == State.APPS_CUSTOMIZE_SPRING_LOADED) {
            showAppsCustomizeHelper(true, true);
            this.mState = State.APPS_CUSTOMIZE;
        }
    }

    /* access modifiers changed from: package-private */
    public void hideDockDivider() {
        if (this.mQsbDivider != null && this.mDockDivider != null) {
            this.mQsbDivider.setVisibility(4);
            this.mDockDivider.setVisibility(4);
        }
    }

    /* access modifiers changed from: package-private */
    public void showDockDivider(boolean animated) {
        if (this.mQsbDivider != null && this.mDockDivider != null) {
            this.mQsbDivider.setVisibility(0);
            this.mDockDivider.setVisibility(0);
            if (this.mDividerAnimator != null) {
                this.mDividerAnimator.cancel();
                this.mQsbDivider.setAlpha(1.0f);
                this.mDockDivider.setAlpha(1.0f);
                this.mDividerAnimator = null;
            }
            if (animated) {
                this.mDividerAnimator = LauncherAnimUtils.createAnimatorSet();
                this.mDividerAnimator.playTogether(new Animator[]{LauncherAnimUtils.ofFloat(this.mQsbDivider, "alpha", 1.0f), LauncherAnimUtils.ofFloat(this.mDockDivider, "alpha", 1.0f)});
                int duration = 0;
                if (this.mSearchDropTargetBar != null) {
                    duration = this.mSearchDropTargetBar.getTransitionInDuration();
                }
                this.mDividerAnimator.setDuration((long) duration);
                this.mDividerAnimator.start();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void showHotseat(boolean animated) {
        if (!animated) {
            this.mHotseat.setAlpha(1.0f);
        } else if (this.mHotseat.getAlpha() != 1.0f) {
            if (this.mSearchDropTargetBar != null) {
                int duration = this.mSearchDropTargetBar.getTransitionInDuration();
            }
            this.mHotseat.animate().alpha(1.0f).setDuration((long) getResources().getInteger(R.integer.config_workspaceUnshrinkTime));
        }
    }

    /* access modifiers changed from: package-private */
    public void hideHotseat(boolean animated) {
        if (!animated) {
            this.mHotseat.setAlpha(0.0f);
        } else if (this.mHotseat.getAlpha() != 0.0f) {
            if (this.mSearchDropTargetBar != null) {
                int duration = this.mSearchDropTargetBar.getTransitionOutDuration();
            }
            this.mHotseat.animate().alpha(0.0f).setDuration((long) getResources().getInteger(R.integer.config_workspaceUnshrinkTime));
        }
    }

    private int getCurrentOrientationIndexForGlobalIcons() {
        switch (getResources().getConfiguration().orientation) {
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    private Drawable getExternalPackageToolbarIcon(ComponentName activityName, String resourceName) {
        int iconResId;
        try {
            PackageManager packageManager = getPackageManager();
            Bundle metaData = packageManager.getActivityInfo(activityName, 128).metaData;
            if (!(metaData == null || (iconResId = metaData.getInt(resourceName)) == 0)) {
                return packageManager.getResourcesForActivity(activityName).getDrawable(iconResId);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Failed to load toolbar icon; " + activityName.flattenToShortString() + " not found", e);
        } catch (Resources.NotFoundException nfe) {
            Log.w(TAG, "Failed to load toolbar icon from " + activityName.flattenToShortString(), nfe);
        }
        return null;
    }

    private Drawable.ConstantState updateTextButtonWithIconFromExternalActivity(int buttonId, ComponentName activityName, int fallbackDrawableId, String toolbarResourceName) {
        Drawable toolbarIcon = getExternalPackageToolbarIcon(activityName, toolbarResourceName);
        Resources r = getResources();
        int w = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_width);
        int h = r.getDimensionPixelSize(R.dimen.toolbar_external_icon_height);
        TextView button = (TextView) findViewById(buttonId);
        if (toolbarIcon == null) {
            Drawable toolbarIcon2 = r.getDrawable(fallbackDrawableId);
            toolbarIcon2.setBounds(0, 0, w, h);
            if (button == null) {
                return null;
            }
            button.setCompoundDrawables(toolbarIcon2, (Drawable) null, (Drawable) null, (Drawable) null);
            return null;
        }
        toolbarIcon.setBounds(0, 0, w, h);
        if (button != null) {
            button.setCompoundDrawables(toolbarIcon, (Drawable) null, (Drawable) null, (Drawable) null);
        }
        return toolbarIcon.getConstantState();
    }

    private void updateTextButtonWithDrawable(int buttonId, Drawable d) {
        ((TextView) findViewById(buttonId)).setCompoundDrawables(d, (Drawable) null, (Drawable) null, (Drawable) null);
    }

    private boolean updateGlobalSearchIcon() {
        View voiceButton = findViewById(R.id.voice_button);
        View voiceButtonProxy = findViewById(R.id.voice_button_proxy);
        if (voiceButton != null) {
            voiceButton.setVisibility(8);
        }
        if (voiceButtonProxy == null) {
            return false;
        }
        voiceButtonProxy.setVisibility(8);
        return false;
    }

    private void updateGlobalSearchIcon(Drawable.ConstantState d) {
    }

    private boolean updateVoiceSearchIcon(boolean searchVisible) {
        View voiceButton = findViewById(R.id.voice_button);
        View voiceButtonProxy = findViewById(R.id.voice_button_proxy);
        if (voiceButton != null) {
            voiceButton.setVisibility(8);
        }
        if (voiceButtonProxy == null) {
            return false;
        }
        voiceButtonProxy.setVisibility(8);
        return false;
    }

    private void updateVoiceSearchIcon(Drawable.ConstantState d) {
    }

    private void updateAppMarketIcon() {
        View marketButton = findViewById(R.id.market_button);
        Intent intent = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.APP_MARKET");
        ComponentName activityName = intent.resolveActivity(getPackageManager());
        if (activityName != null) {
            int coi = getCurrentOrientationIndexForGlobalIcons();
            this.mAppMarketIntent = intent;
            sAppMarketIcon[coi] = updateTextButtonWithIconFromExternalActivity(R.id.market_button, activityName, R.drawable.ic_launcher_market_holo, TOOLBAR_ICON_METADATA_NAME);
            return;
        }
        marketButton.setVisibility(8);
        marketButton.setEnabled(false);
    }

    private void updateAppMarketIcon(Drawable.ConstantState d) {
        Resources r = getResources();
        Drawable marketIconDrawable = d.newDrawable(r);
        marketIconDrawable.setBounds(0, 0, r.getDimensionPixelSize(R.dimen.toolbar_external_icon_width), r.getDimensionPixelSize(R.dimen.toolbar_external_icon_height));
        updateTextButtonWithDrawable(R.id.market_button, marketIconDrawable);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        boolean result = super.dispatchPopulateAccessibilityEvent(event);
        List<CharSequence> text = event.getText();
        text.clear();
        if (this.mState == State.APPS_CUSTOMIZE) {
            text.add(getString(R.string.all_apps_button_label));
        } else {
            text.add(getString(R.string.all_apps_home_button_label));
        }
        return result;
    }

    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_HOME_KEY;
        final String SYSTEM_DIALOG_REASON_KEY;
        final String SYSTEM_DIALOG_REASON_RECENT_APPS;

        private CloseSystemDialogsIntentReceiver() {
            this.SYSTEM_DIALOG_REASON_KEY = "reason";
            this.SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            this.SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        }

        public void onReceive(Context context, Intent intent) {
            Log.i(Launcher.TAG, "onReceive: CloseSystemDialogsIntentReceiver");
            Launcher.this.closeSystemDialogs();
            String reason = intent.getStringExtra("reason");
            if (reason == null) {
                return;
            }
            if (reason.equals("homekey")) {
                Log.i(Launcher.TAG, "onReceive: Home键被监听");
            } else if (reason.equals("recentapps")) {
                Log.i(Launcher.TAG, "onReceive: 多任务键被监听");
            }
        }
    }

    private class AppWidgetResetObserver extends ContentObserver {
        public AppWidgetResetObserver() {
            super(new Handler());
        }

        public void onChange(boolean selfChange) {
            Launcher.this.onAppWidgetReset();
        }
    }

    private boolean waitUntilResume(Runnable run, boolean deletePreviousRunnables) {
        if (!this.mPaused) {
            return false;
        }
        Log.i(TAG, "Deferring update until onResume");
        if (deletePreviousRunnables) {
            do {
            } while (this.mOnResumeCallbacks.remove(run));
        }
        this.mOnResumeCallbacks.add(run);
        return true;
    }

    private boolean waitUntilResume(Runnable run) {
        return waitUntilResume(run, false);
    }

    public boolean setLoadOnResume() {
        if (!this.mPaused) {
            return false;
        }
        Log.i(TAG, "setLoadOnResume");
        this.mOnResumeNeedsLoad = true;
        return true;
    }

    public int getCurrentWorkspaceScreen() {
        if (this.mWorkspace != null) {
            return this.mWorkspace.getCurrentPage();
        }
        return 2;
    }

    public void startBinding() {
        this.mOnResumeCallbacks.clear();
        Workspace workspace = this.mWorkspace;
        this.mNewShortcutAnimatePage = -1;
        this.mNewShortcutAnimateViews.clear();
        this.mWorkspace.clearDropTargets();
        int count = workspace.getChildCount();
        for (int i = 0; i < count; i++) {
            ((CellLayout) workspace.getChildAt(i)).removeAllViewsInLayout();
        }
        this.mWidgetsToAdvance.clear();
        if (this.mHotseat != null) {
            this.mHotseat.resetLayout();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x004d, code lost:
        continue;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bindItems(java.util.ArrayList<com.szchoiceway.index.ItemInfo> r23, int r24, int r25) {
        /*
            r22 = this;
            com.szchoiceway.index.Launcher$22 r6 = new com.szchoiceway.index.Launcher$22
            r0 = r22
            r1 = r23
            r2 = r24
            r3 = r25
            r6.<init>(r1, r2, r3)
            r0 = r22
            boolean r6 = r0.waitUntilResume(r6)
            if (r6 == 0) goto L_0x0016
        L_0x0015:
            return
        L_0x0016:
            java.util.HashSet r20 = new java.util.HashSet
            r20.<init>()
            r0 = r22
            android.content.SharedPreferences r6 = r0.mSharedPrefs
            java.lang.String r8 = "apps.new.list"
            r0 = r20
            java.util.Set r20 = r6.getStringSet(r8, r0)
            r0 = r22
            com.szchoiceway.index.Workspace r4 = r0.mWorkspace
            r17 = r24
        L_0x002d:
            r0 = r17
            r1 = r25
            if (r0 >= r1) goto L_0x00f4
            r0 = r23
            r1 = r17
            java.lang.Object r19 = r0.get(r1)
            com.szchoiceway.index.ItemInfo r19 = (com.szchoiceway.index.ItemInfo) r19
            r0 = r19
            long r8 = r0.container
            r10 = -101(0xffffffffffffff9b, double:NaN)
            int r6 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r6 != 0) goto L_0x0050
            r0 = r22
            com.szchoiceway.index.Hotseat r6 = r0.mHotseat
            if (r6 != 0) goto L_0x0050
        L_0x004d:
            int r17 = r17 + 1
            goto L_0x002d
        L_0x0050:
            r0 = r19
            int r6 = r0.itemType
            switch(r6) {
                case 0: goto L_0x0058;
                case 1: goto L_0x0058;
                case 2: goto L_0x00c0;
                default: goto L_0x0057;
            }
        L_0x0057:
            goto L_0x004d
        L_0x0058:
            r18 = r19
            com.szchoiceway.index.ShortcutInfo r18 = (com.szchoiceway.index.ShortcutInfo) r18
            r0 = r18
            android.content.Intent r6 = r0.intent
            r8 = 0
            java.lang.String r6 = r6.toUri(r8)
            java.lang.String r21 = r6.toString()
            r0 = r22
            r1 = r18
            android.view.View r5 = r0.createShortcut(r1)
            r0 = r19
            long r6 = r0.container
            r0 = r19
            int r8 = r0.screen
            r0 = r19
            int r9 = r0.cellX
            r0 = r19
            int r10 = r0.cellY
            r11 = 1
            r12 = 1
            r13 = 0
            r4.addInScreen(r5, r6, r8, r9, r10, r11, r12, r13)
            r16 = 0
            monitor-enter(r20)
            boolean r6 = r20.contains(r21)     // Catch:{ all -> 0x00bd }
            if (r6 == 0) goto L_0x0094
            boolean r16 = r20.remove(r21)     // Catch:{ all -> 0x00bd }
        L_0x0094:
            monitor-exit(r20)     // Catch:{ all -> 0x00bd }
            if (r16 == 0) goto L_0x004d
            r6 = 0
            r5.setAlpha(r6)
            r6 = 0
            r5.setScaleX(r6)
            r6 = 0
            r5.setScaleY(r6)
            r0 = r19
            int r6 = r0.screen
            r0 = r22
            r0.mNewShortcutAnimatePage = r6
            r0 = r22
            java.util.ArrayList<android.view.View> r6 = r0.mNewShortcutAnimateViews
            boolean r6 = r6.contains(r5)
            if (r6 != 0) goto L_0x004d
            r0 = r22
            java.util.ArrayList<android.view.View> r6 = r0.mNewShortcutAnimateViews
            r6.add(r5)
            goto L_0x004d
        L_0x00bd:
            r6 = move-exception
            monitor-exit(r20)     // Catch:{ all -> 0x00bd }
            throw r6
        L_0x00c0:
            r9 = 2130968607(0x7f04001f, float:1.7545872E38)
            int r6 = r4.getCurrentPage()
            android.view.View r6 = r4.getChildAt(r6)
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            r8 = r19
            com.szchoiceway.index.FolderInfo r8 = (com.szchoiceway.index.FolderInfo) r8
            r0 = r22
            com.szchoiceway.index.IconCache r10 = r0.mIconCache
            r0 = r22
            com.szchoiceway.index.FolderIcon r7 = com.szchoiceway.index.FolderIcon.fromXml(r9, r0, r6, r8, r10)
            r0 = r19
            long r8 = r0.container
            r0 = r19
            int r10 = r0.screen
            r0 = r19
            int r11 = r0.cellX
            r0 = r19
            int r12 = r0.cellY
            r13 = 1
            r14 = 1
            r15 = 0
            r6 = r4
            r6.addInScreen(r7, r8, r10, r11, r12, r13, r14, r15)
            goto L_0x004d
        L_0x00f4:
            r4.requestLayout()
            goto L_0x0015
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.Launcher.bindItems(java.util.ArrayList, int, int):void");
    }

    public void bindFolders(final HashMap<Long, FolderInfo> folders) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindFolders(folders);
            }
        })) {
            sFolders.clear();
            sFolders.putAll(folders);
        }
    }

    public void bindAppWidget(final LauncherAppWidgetInfo item) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindAppWidget(item);
            }
        })) {
            Workspace workspace = this.mWorkspace;
            int appWidgetId = item.appWidgetId;
            AppWidgetProviderInfo appWidgetInfo = this.mAppWidgetManager.getAppWidgetInfo(appWidgetId);
            item.hostView = this.mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
            item.hostView.setTag(item);
            item.onBindAppWidget(this);
            workspace.addInScreen(item.hostView, item.container, item.screen, item.cellX, item.cellY, item.spanX, item.spanY, false);
            addWidgetToAutoAdvanceIfNeeded(item.hostView, appWidgetInfo);
            workspace.requestLayout();
        }
    }

    public void onPageBoundSynchronously(int page) {
        this.mSynchronouslyBoundPages.add(Integer.valueOf(page));
    }

    public void finishBindingItems() {
        boolean willSnapPage;
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.finishBindingItems();
            }
        })) {
            if (this.mSavedState != null) {
                if (!this.mWorkspace.hasFocus()) {
                    this.mWorkspace.getChildAt(this.mWorkspace.getCurrentPage()).requestFocus();
                }
                this.mSavedState = null;
            }
            this.mWorkspace.restoreInstanceStateForRemainingPages();
            for (int i = 0; i < sPendingAddList.size(); i++) {
                completeAdd(sPendingAddList.get(i));
            }
            sPendingAddList.clear();
            updateAppMarketIcon();
            if (this.mVisible || this.mWorkspaceLoading) {
                Runnable newAppsRunnable = new Runnable() {
                    public void run() {
                        Launcher.this.runNewAppsAnimation(false);
                    }
                };
                if (this.mNewShortcutAnimatePage <= -1 || this.mNewShortcutAnimatePage == this.mWorkspace.getCurrentPage()) {
                    willSnapPage = false;
                } else {
                    willSnapPage = true;
                }
                if (!canRunNewAppsAnimation()) {
                    runNewAppsAnimation(willSnapPage);
                } else if (willSnapPage) {
                    this.mWorkspace.snapToPage(this.mNewShortcutAnimatePage, newAppsRunnable);
                } else {
                    runNewAppsAnimation(false);
                }
            }
            this.mWorkspaceLoading = false;
        }
    }

    private boolean canRunNewAppsAnimation() {
        return System.currentTimeMillis() - this.mDragController.getLastGestureUpTime() > ((long) (NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS * 1000));
    }

    /* access modifiers changed from: private */
    public void runNewAppsAnimation(boolean immediate) {
        AnimatorSet anim = LauncherAnimUtils.createAnimatorSet();
        Collection<Animator> bounceAnims = new ArrayList<>();
        Collections.sort(this.mNewShortcutAnimateViews, new Comparator<View>() {
            public int compare(View a, View b) {
                CellLayout.LayoutParams alp = (CellLayout.LayoutParams) a.getLayoutParams();
                CellLayout.LayoutParams blp = (CellLayout.LayoutParams) b.getLayoutParams();
                int cellCountX = LauncherModel.getCellCountX();
                return ((alp.cellY * cellCountX) + alp.cellX) - ((blp.cellY * cellCountX) + blp.cellX);
            }
        });
        if (immediate) {
            Iterator<View> it = this.mNewShortcutAnimateViews.iterator();
            while (it.hasNext()) {
                View v = it.next();
                v.setAlpha(1.0f);
                v.setScaleX(1.0f);
                v.setScaleY(1.0f);
            }
        } else {
            for (int i = 0; i < this.mNewShortcutAnimateViews.size(); i++) {
                ValueAnimator bounceAnim = LauncherAnimUtils.ofPropertyValuesHolder(this.mNewShortcutAnimateViews.get(i), PropertyValuesHolder.ofFloat("alpha", new float[]{1.0f}), PropertyValuesHolder.ofFloat("scaleX", new float[]{1.0f}), PropertyValuesHolder.ofFloat("scaleY", new float[]{1.0f}));
                bounceAnim.setDuration(450);
                bounceAnim.setStartDelay((long) (i * 75));
                bounceAnim.setInterpolator(new SmoothPagedView.OvershootInterpolator());
                bounceAnims.add(bounceAnim);
            }
            anim.playTogether(bounceAnims);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    if (Launcher.this.mWorkspace != null) {
                        Launcher.this.mWorkspace.postDelayed(Launcher.this.mBuildLayersRunnable, 500);
                    }
                }
            });
            anim.start();
        }
        this.mNewShortcutAnimatePage = -1;
        this.mNewShortcutAnimateViews.clear();
        new Thread("clearNewAppsThread") {
            public void run() {
                Launcher.this.mSharedPrefs.edit().putInt(InstallShortcutReceiver.NEW_APPS_PAGE_KEY, -1).putStringSet(InstallShortcutReceiver.NEW_APPS_LIST_KEY, (Set) null).commit();
            }
        }.start();
    }

    public void bindSearchablesChanged() {
        boolean searchVisible = updateGlobalSearchIcon();
        boolean voiceVisible = updateVoiceSearchIcon(searchVisible);
        if (this.mSearchDropTargetBar != null) {
            this.mSearchDropTargetBar.onSearchPackagesChanged(searchVisible, voiceVisible);
        }
    }

    public void bindAllApplications(final ArrayList<ApplicationInfo> apps) {
        Runnable setAllAppsRunnable = new Runnable() {
            public void run() {
                if (Launcher.this.mAppsCustomizeContent != null) {
                    Launcher.this.mAppsCustomizeContent.setApps(apps);
                }
            }
        };
        View progressBar = this.mAppsCustomizeTabHost.findViewById(R.id.apps_customize_progress_bar);
        if (progressBar != null) {
            ((ViewGroup) progressBar.getParent()).removeView(progressBar);
            this.mAppsCustomizeTabHost.post(setAllAppsRunnable);
            return;
        }
        setAllAppsRunnable.run();
    }

    public void bindAppsAdded(final ArrayList<ApplicationInfo> apps) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindAppsAdded(apps);
            }
        }) && this.mAppsCustomizeContent != null) {
            this.mAppsCustomizeContent.addApps(apps);
        }
    }

    public void bindAppsUpdated(final ArrayList<ApplicationInfo> apps) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindAppsUpdated(apps);
            }
        })) {
            if (this.mWorkspace != null) {
                this.mWorkspace.updateShortcuts(apps);
            }
            if (this.mAppsCustomizeContent != null) {
                this.mAppsCustomizeContent.updateApps(apps);
            }
        }
    }

    public void bindComponentsRemoved(final ArrayList<String> packageNames, final ArrayList<ApplicationInfo> appInfos, final boolean matchPackageNamesOnly) {
        if (!waitUntilResume(new Runnable() {
            public void run() {
                Launcher.this.bindComponentsRemoved(packageNames, appInfos, matchPackageNamesOnly);
            }
        })) {
            if (matchPackageNamesOnly) {
                this.mWorkspace.removeItemsByPackageName(packageNames);
            } else {
                this.mWorkspace.removeItemsByApplicationInfo(appInfos);
            }
            if (this.mAppsCustomizeContent != null) {
                this.mAppsCustomizeContent.removeApps(appInfos);
            }
            this.mDragController.onAppsRemoved(appInfos, this);
        }
    }

    public void bindPackagesUpdated(ArrayList<Object> widgetsAndShortcuts) {
        if (waitUntilResume(this.mBindPackagesUpdatedRunnable, true)) {
            this.mWidgetsAndShortcuts = widgetsAndShortcuts;
        } else if (this.mAppsCustomizeContent != null) {
            this.mAppsCustomizeContent.onPackagesUpdated(widgetsAndShortcuts);
        }
    }

    private int mapConfigurationOriActivityInfoOri(int configOri) {
        Display d = getWindowManager().getDefaultDisplay();
        int naturalOri = 2;
        switch (d.getRotation()) {
            case 0:
            case 2:
                naturalOri = configOri;
                break;
            case 1:
            case 3:
                if (configOri != 2) {
                    naturalOri = 2;
                    break;
                } else {
                    naturalOri = 1;
                    break;
                }
        }
        int[] oriMap = {1, 0, 9, 8};
        int indexOffset = 0;
        if (naturalOri == 2) {
            indexOffset = 1;
        }
        return oriMap[(d.getRotation() + indexOffset) % 4];
    }

    public boolean isRotationEnabled() {
        return sForceEnableRotation || getResources().getBoolean(R.bool.allow_rotation);
    }

    public void lockScreenOrientation() {
        if (isRotationEnabled()) {
            setRequestedOrientation(mapConfigurationOriActivityInfoOri(getResources().getConfiguration().orientation));
        }
    }

    public void unlockScreenOrientation(boolean immediate) {
        if (!isRotationEnabled()) {
            return;
        }
        if (immediate) {
            setRequestedOrientation(-1);
        } else {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    Launcher.this.setRequestedOrientation(-1);
                }
            }, 500);
        }
    }

    private boolean isClingsEnabled() {
        return false;
    }

    private Cling initCling(int clingId, int[] positionData, boolean animate, int delay) {
        boolean z = true;
        final Cling cling = (Cling) findViewById(clingId);
        if (cling != null) {
            cling.init(this, positionData);
            cling.setVisibility(0);
            cling.setLayerType(2, (Paint) null);
            if (animate) {
                cling.buildLayer();
                cling.setAlpha(0.0f);
                cling.animate().alpha(1.0f).setInterpolator(new AccelerateInterpolator()).setDuration(550).setStartDelay((long) delay).start();
            } else {
                cling.setAlpha(1.0f);
            }
            cling.setFocusableInTouchMode(true);
            cling.post(new Runnable() {
                public void run() {
                    cling.setFocusable(true);
                    cling.requestFocus();
                }
            });
            HideFromAccessibilityHelper hideFromAccessibilityHelper = this.mHideFromAccessibilityHelper;
            DragLayer dragLayer = this.mDragLayer;
            if (clingId != R.id.all_apps_cling) {
                z = false;
            }
            hideFromAccessibilityHelper.setImportantForAccessibilityToNo(dragLayer, z);
        }
        return cling;
    }

    private void dismissCling(final Cling cling, final String flag, int duration) {
        if (cling != null && cling.getVisibility() != 8) {
            ObjectAnimator anim = LauncherAnimUtils.ofFloat(cling, "alpha", 0.0f);
            anim.setDuration((long) duration);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    cling.setVisibility(8);
                    cling.cleanup();
                    new Thread("dismissClingThread") {
                        public void run() {
                            SharedPreferences.Editor editor = Launcher.this.mSharedPrefs.edit();
                            editor.putBoolean(flag, true);
                            editor.commit();
                        }
                    }.start();
                }
            });
            anim.start();
            this.mHideFromAccessibilityHelper.restoreImportantForAccessibility(this.mDragLayer);
        }
    }

    private void removeCling(int id) {
        final View cling = findViewById(id);
        if (cling != null) {
            final ViewGroup parent = (ViewGroup) cling.getParent();
            parent.post(new Runnable() {
                public void run() {
                    parent.removeView(cling);
                }
            });
            this.mHideFromAccessibilityHelper.restoreImportantForAccessibility(this.mDragLayer);
        }
    }

    private boolean skipCustomClingIfNoAccounts() {
        if (!((Cling) findViewById(R.id.workspace_cling)).getDrawIdentifier().equals("workspace_custom") || AccountManager.get(this).getAccountsByType("com.google").length != 0) {
            return false;
        }
        return true;
    }

    public void showFirstRunWorkspaceCling() {
        if (!isClingsEnabled() || this.mSharedPrefs.getBoolean("cling.workspace.dismissed", false) || skipCustomClingIfNoAccounts()) {
            removeCling(R.id.workspace_cling);
            return;
        }
        if (this.mSharedPrefs.getInt("DEFAULT_WORKSPACE_RESOURCE_ID", 0) != 0 && getResources().getBoolean(R.bool.config_useCustomClings)) {
            View cling = findViewById(R.id.workspace_cling);
            ViewGroup clingParent = (ViewGroup) cling.getParent();
            int clingIndex = clingParent.indexOfChild(cling);
            clingParent.removeViewAt(clingIndex);
            View customCling = this.mInflater.inflate(R.layout.custom_workspace_cling, clingParent, false);
            clingParent.addView(customCling, clingIndex);
            customCling.setId(R.id.workspace_cling);
        }
        initCling(R.id.workspace_cling, (int[]) null, false, 0);
    }

    public void showFirstRunAllAppsCling(int[] position) {
        if (!isClingsEnabled() || this.mSharedPrefs.getBoolean("cling.allapps.dismissed", false)) {
            removeCling(R.id.all_apps_cling);
        } else {
            initCling(R.id.all_apps_cling, position, true, 0);
        }
    }

    public Cling showFirstRunFoldersCling() {
        if (isClingsEnabled() && !this.mSharedPrefs.getBoolean("cling.folder.dismissed", false)) {
            return initCling(R.id.folder_cling, (int[]) null, true, 0);
        }
        removeCling(R.id.folder_cling);
        return null;
    }

    public boolean isFolderClingVisible() {
        Cling cling = (Cling) findViewById(R.id.folder_cling);
        if (cling == null || cling.getVisibility() != 0) {
            return false;
        }
        return true;
    }

    public void dismissWorkspaceCling(View v) {
        dismissCling((Cling) findViewById(R.id.workspace_cling), "cling.workspace.dismissed", 250);
    }

    public void dismissAllAppsCling(View v) {
        dismissCling((Cling) findViewById(R.id.all_apps_cling), "cling.allapps.dismissed", 250);
    }

    public void dismissFolderCling(View v) {
        dismissCling((Cling) findViewById(R.id.folder_cling), "cling.folder.dismissed", 250);
    }

    public void dumpState() {
        Log.d(TAG, "BEGIN launcher2 dump state for launcher " + this);
        Log.d(TAG, "mSavedState=" + this.mSavedState);
        Log.d(TAG, "mWorkspaceLoading=" + this.mWorkspaceLoading);
        Log.d(TAG, "mRestoring=" + this.mRestoring);
        Log.d(TAG, "mWaitingForResult=" + this.mWaitingForResult);
        Log.d(TAG, "mSavedInstanceState=" + this.mSavedInstanceState);
        Log.d(TAG, "sFolders.size=" + sFolders.size());
        this.mModel.dumpState();
        if (this.mAppsCustomizeContent != null) {
            this.mAppsCustomizeContent.dumpState();
        }
        Log.d(TAG, "END launcher2 dump state");
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.println(" ");
        writer.println("Debug logs: ");
        for (int i = 0; i < sDumpLogs.size(); i++) {
            writer.println("  " + sDumpLogs.get(i));
        }
    }

    public static void dumpDebugLogsToConsole() {
        Log.d(TAG, "");
        Log.d(TAG, "*********************");
        Log.d(TAG, "Launcher debug logs: ");
        for (int i = 0; i < sDumpLogs.size(); i++) {
            Log.d(TAG, "  " + sDumpLogs.get(i));
        }
        Log.d(TAG, "*********************");
        Log.d(TAG, "");
    }

    private void initView() {
        this.ShowScreen2 = (RelativeLayout) findViewById(R.id.exthotseat);
        if (LauncherApplication.m_iUITypeVer == 41) {
            if (this.m_bVerticalPager) {
                initView_KeSaiWei_VerticalPager();
            } else if (this.m_iModeSet == 1) {
                initView_KeSaiWei_aodi();
            } else if (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6) {
                initView_KeSaiWei_evo();
            } else if (this.m_iModeSet == 3) {
                initView_KeSaiWei_aodi_Q5();
            } else if (this.m_iModeSet == 5) {
                initView_KeSaiWei_benchi();
            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                initView_KeSaiWei_evo_id6();
            } else if (this.m_iModeSet == 8) {
                initViewPager_CHWY();
            } else if (this.m_iModeSet == 11) {
                initViewPager_JLR();
            } else if (this.m_iModeSet == 13) {
                initKaiDiLaKePage();
            } else if (this.m_iModeSet == 14) {
                initKaiDiLaKePage();
            } else if (this.m_iModeSet == 15) {
                initViewPager_EVO_ID7();
            } else if (this.m_iModeSet == 16) {
                initViewPager_EVO_ID7();
            } else {
                initView_KeSaiWei();
            }
            this.ksw_A4L_audi_che = (ImageView) findViewById(R.id.ksw_A4L_audi_che);
            this.KSW_A4L_right_show_logo = (RelativeLayout) findViewById(R.id.KSW_A4L_right_show_logo);
            this.KSW_A4L_right_show_Navi = (RelativeLayout) findViewById(R.id.KSW_A4L_right_show_Navi);
            this.KSW_A4L_right_Traffic_information = (RelativeLayout) findViewById(R.id.KSW_A4L_right_Traffic_information);
            this.KSW_A4L_right_show_Medio = (RelativeLayout) findViewById(R.id.KSW_A4L_right_show_Medio);
            this.tv_audio_right_xushilicheng = (TextView) findViewById(R.id.tv_audio_right_xushilicheng);
            this.tv_audio_right_wendu = (TextView) findViewById(R.id.tv_audio_right_wendu);
            this.tv_audio_right_fadongjizhuansu = (TextView) findViewById(R.id.tv_audio_right_fadongjizhuansu);
            this.ivRightMusicIcon = (ImageView) findViewById(R.id.ivRightMusicIcon);
            if (this.ivRightMusicIcon != null) {
                this.ivRightMusicIcon.setOnClickListener(this);
            }
            ksw_refresh_A4L_left_show();
            ksw_refresh_A4L_right_show();
        } else if (LauncherApplication.m_iUITypeVer == 101) {
            initKaiDiLaKePage();
        }
        int[] btnList = {R.id.navi, R.id.radio, R.id.music, R.id.apps, R.id.BtnSetCalendar, R.id.setting, R.id.bluetooth, R.id.video, R.id.dvr, R.id.phonelink, R.id.OriginaCar, R.id.btnBrowser, R.id.BtnGuanpin, R.id.aux, R.id.btnESFile, R.id.btnShowWorkspace, R.id.btnLeftMusic, R.id.btnLeftNavi, R.id.btnLeftSetting, R.id.btnLeftOriginaCar, R.id.btnLeftApps};
        for (int findViewById : btnList) {
            View btn = findViewById(findViewById);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
        this.mTimeYearMD = (TextView) findViewById(R.id.TwCurDataTimeYMD);
        this.mTimerWeek = (TextView) findViewById(R.id.TwCurDataTimeWeek);
        this.mTwCurDataTimeAMPM = (TextView) findViewById(R.id.TwCurDataTimeAMPM);
        this.mTimerHourH = (ImageView) findViewById(R.id.VwHourH);
        this.mTimerHourL = (ImageView) findViewById(R.id.VwHourL);
        this.mTimerMinH = (ImageView) findViewById(R.id.VwMinH);
        this.mTimerMinL = (ImageView) findViewById(R.id.VwMinL);
        this.mVwtmPoint = (ImageView) findViewById(R.id.VwtmPoint);
        this.mTvCurTime = (TextView) findViewById(R.id.tvCurTime);
    }

    private void initKaiDiLaKePage() {
        View view1;
        View view2;
        int[] btnList;
        int[] btnList2;
        View view12;
        View view22;
        int[] btnList22;
        int[] btnList3;
        View view13;
        View view23;
        int[] btnList1;
        int[] btnList23;
        View view14;
        View view24;
        int[] btnList12;
        int[] btnList24;
        int[] btnList13;
        int[] btnList25;
        if (this.ShowScreen2 != null) {
            this.mViewPager = (ViewPager) findViewById(R.id.kaidilake_viewpaper);
            if (this.mViewPager != null) {
                this.mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
            }
            this.mPage0 = (ImageView) findViewById(R.id.page0);
            this.mPage1 = (ImageView) findViewById(R.id.page1);
            this.mPage2 = (ImageView) findViewById(R.id.page2);
            LayoutInflater mLi = LayoutInflater.from(this);
            View view15 = null;
            View view25 = null;
            View view3 = null;
            View view4 = null;
            if (LauncherApplication.m_iUITypeVer == 41) {
                if (this.m_iModeSet == 2 || this.m_iModeSet == 4) {
                    Log.i(TAG, "initKaiDiLaKePage: m_i_ksw_evo_main_interface_index = " + this.m_i_ksw_evo_main_interface_index);
                    Log.i(TAG, "initKaiDiLaKePage: ksw_m_b_Focus_image_zoom_evo = " + this.ksw_m_b_Focus_image_zoom_evo);
                    if (this.m_i_ksw_evo_main_interface_index == 2) {
                        view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1_xhd, (ViewGroup) null);
                        view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_xhd, (ViewGroup) null);
                    } else if (this.m_i_ksw_evo_main_interface_index == 3) {
                        view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1_xhd_2, (ViewGroup) null);
                        view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_xhd_2, (ViewGroup) null);
                    } else if (this.ksw_m_b_Focus_image_zoom_evo) {
                        if (this.m_b_have_TV) {
                            view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1_zoom, (ViewGroup) null);
                            view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_zoom_have_tv, (ViewGroup) null);
                        } else {
                            view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1_zoom, (ViewGroup) null);
                            view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_zoom, (ViewGroup) null);
                        }
                    } else if (this.m_i_ksw_evo_main_interface_index == 4) {
                        view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats_hezheng_1, (ViewGroup) null);
                        view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats_hezheng_2, (ViewGroup) null);
                    } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                        view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1_als_tianmao, (ViewGroup) null);
                        view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_als_tianmao, (ViewGroup) null);
                    } else if (this.m_b_have_TV) {
                        view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1, (ViewGroup) null);
                        view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_have_tv, (ViewGroup) null);
                    } else {
                        view12 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1, (ViewGroup) null);
                        view22 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2, (ViewGroup) null);
                    }
                    int[] btnList32 = null;
                    if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
                        btnList3 = new int[]{R.id.music, R.id.navi, R.id.bluetooth, R.id.OriginaCar, R.id.video, R.id.setting};
                        btnList22 = new int[]{R.id.phonelink, R.id.dvr, R.id.btnESFile, R.id.btnBrowser, R.id.apps, R.id.yibiaopan};
                    } else if (this.m_i_ksw_evo_main_interface_index == 4) {
                        btnList3 = new int[]{R.id.music, R.id.bluetooth, R.id.navi, R.id.miaojia, R.id.OriginaCar, R.id.btnBrowser};
                        btnList22 = new int[]{R.id.video, R.id.dvr, R.id.setting, R.id.yibiaopan, R.id.btnESFile, R.id.apps};
                    } else if (this.m_i_ksw_evo_main_interface_index == 5) {
                        btnList3 = new int[]{R.id.OriginaCar, R.id.bluetooth, R.id.navi, R.id.btnCarPlay, R.id.music, R.id.video};
                        btnList22 = new int[]{R.id.Carlife, R.id.dvr, R.id.yibiaopan, R.id.apps, R.id.setting, R.id.btnBrowser};
                    } else if (this.m_b_have_TV) {
                        btnList3 = new int[]{R.id.music, R.id.bluetooth, R.id.navi, R.id.btnESFile, R.id.OriginaCar, R.id.btnBrowser};
                        btnList22 = new int[]{R.id.video, R.id.tv, R.id.setting, R.id.yibiaopan, R.id.phonelink, R.id.apps};
                    } else {
                        btnList3 = new int[]{R.id.music, R.id.bluetooth, R.id.navi, R.id.btnESFile, R.id.OriginaCar, R.id.btnBrowser};
                        btnList22 = new int[]{R.id.video, R.id.dvr, R.id.setting, R.id.yibiaopan, R.id.phonelink, R.id.apps};
                    }
                    if (btnList3 != null) {
                        for (int findViewById : btnList3) {
                            View btn = view1.findViewById(findViewById);
                            if (btn != null) {
                                btn.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList22 != null) {
                        for (int findViewById2 : btnList22) {
                            View btn2 = view2.findViewById(findViewById2);
                            if (btn2 != null) {
                                btn2.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList32 != null) {
                        for (int findViewById3 : btnList32) {
                            View view = null;
                            View btn3 = view.findViewById(findViewById3);
                            if (btn3 != null) {
                                btn3.setOnClickListener(this);
                            }
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5};
                    this.imgFocusList_modeIcon = new int[]{R.id.KSW_Focus_select_modeIcon_0, R.id.KSW_Focus_select_modeIcon_1, R.id.KSW_Focus_select_modeIcon_2, R.id.KSW_Focus_select_modeIcon_3, R.id.KSW_Focus_select_modeIcon_4, R.id.KSW_Focus_select_modeIcon_5};
                    if (this.ksw_m_b_Focus_image_zoom_evo) {
                        this.imgFocusList_normal_modeIcon_zoom = new int[]{R.id.KSW_Focus_normal_modeIcon_0, R.id.KSW_Focus_normal_modeIcon_1, R.id.KSW_Focus_normal_modeIcon_2, R.id.KSW_Focus_normal_modeIcon_3, R.id.KSW_Focus_normal_modeIcon_4, R.id.KSW_Focus_normal_modeIcon_5};
                        this.imgFocusList_normal_Text_zoom = new int[]{R.id.KSW_Focus_Text_normal_0, R.id.KSW_Focus_Text_normal_1, R.id.KSW_Focus_Text_normal_2, R.id.KSW_Focus_Text_normal_3, R.id.KSW_Focus_Text_normal_4, R.id.KSW_Focus_Text_normal_5};
                        this.imgFocusList_select_Text_zoom = new int[]{R.id.KSW_Focus_Text_select_0, R.id.KSW_Focus_Text_select_1, R.id.KSW_Focus_Text_select_2, R.id.KSW_Focus_Text_select_3, R.id.KSW_Focus_Text_select_4, R.id.KSW_Focus_Text_select_5};
                    }
                    this.imageViewFocusList_whats1 = new ImageView[btnList3.length];
                    this.imageViewFocusList_whats2 = new ImageView[btnList22.length];
                    this.imageViewFocusList_whats1_modeIcon = new ImageView[btnList3.length];
                    this.imageViewFocusList_whats2_modeIcon = new ImageView[btnList22.length];
                    if (this.ksw_m_b_Focus_image_zoom_evo) {
                        this.imageViewFocusList_whats1_normal_modeIcon_zoom = new ImageView[btnList3.length];
                        this.imageViewFocusList_whats2_normal_modeIcon_zoom = new ImageView[btnList22.length];
                        this.imageViewFocusList_whats1_normal_Text_zoom = new TextView[btnList3.length];
                        this.imageViewFocusList_whats2_normal_Text_zoom = new TextView[btnList22.length];
                        this.imageViewFocusList_whats1_select_Text_zoom = new TextView[btnList3.length];
                        this.imageViewFocusList_whats2_select_Text_zoom = new TextView[btnList22.length];
                    }
                    for (int i = 0; i < btnList3.length; i++) {
                        this.imageViewFocusList_whats1[i] = (ImageView) view1.findViewById(this.imgFocusList[i]);
                        this.imageViewFocusList_whats1_modeIcon[i] = (ImageView) view1.findViewById(this.imgFocusList_modeIcon[i]);
                        if (this.ksw_m_b_Focus_image_zoom_evo) {
                            this.imageViewFocusList_whats1_normal_modeIcon_zoom[i] = (ImageView) view1.findViewById(this.imgFocusList_normal_modeIcon_zoom[i]);
                            this.imageViewFocusList_whats1_normal_Text_zoom[i] = (TextView) view1.findViewById(this.imgFocusList_normal_Text_zoom[i]);
                            this.imageViewFocusList_whats1_select_Text_zoom[i] = (TextView) view1.findViewById(this.imgFocusList_select_Text_zoom[i]);
                        }
                    }
                    for (int i2 = 0; i2 < btnList22.length; i2++) {
                        this.imageViewFocusList_whats2[i2] = (ImageView) view2.findViewById(this.imgFocusList[i2]);
                        this.imageViewFocusList_whats2_modeIcon[i2] = (ImageView) view2.findViewById(this.imgFocusList_modeIcon[i2]);
                        if (this.ksw_m_b_Focus_image_zoom_evo) {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i2] = (ImageView) view2.findViewById(this.imgFocusList_normal_modeIcon_zoom[i2]);
                            this.imageViewFocusList_whats2_normal_Text_zoom[i2] = (TextView) view2.findViewById(this.imgFocusList_normal_Text_zoom[i2]);
                            this.imageViewFocusList_whats2_select_Text_zoom[i2] = (TextView) view2.findViewById(this.imgFocusList_select_Text_zoom[i2]);
                        }
                    }
                } else if (this.m_iModeSet == 6) {
                    view1 = mLi.inflate(R.layout.kesaiwei_1024x600_baoma_whats1_xhd, (ViewGroup) null);
                    view2 = mLi.inflate(R.layout.kesaiwei_1024x600_baoma_whats2_xhd, (ViewGroup) null);
                    int[] btnList4 = {R.id.video, R.id.bluetooth, R.id.navi, R.id.tv, R.id.OriginaCar, R.id.btnBrowser};
                    int[] btnList26 = {R.id.music, R.id.dvr, R.id.setting, R.id.yibiaopan, R.id.phonelink, R.id.apps};
                    for (int findViewById4 : btnList4) {
                        View btn4 = view1.findViewById(findViewById4);
                        if (btn4 != null) {
                            btn4.setOnClickListener(this);
                        }
                    }
                    for (int findViewById5 : btnList26) {
                        View btn5 = view2.findViewById(findViewById5);
                        if (btn5 != null) {
                            btn5.setOnClickListener(this);
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5};
                    this.imgFocusList_modeIcon = new int[]{R.id.KSW_Focus_select_modeIcon_0, R.id.KSW_Focus_select_modeIcon_1, R.id.KSW_Focus_select_modeIcon_2, R.id.KSW_Focus_select_modeIcon_3, R.id.KSW_Focus_select_modeIcon_4, R.id.KSW_Focus_select_modeIcon_5};
                    if (this.ksw_m_b_Focus_image_zoom_evo) {
                        this.imgFocusList_normal_modeIcon_zoom = new int[]{R.id.KSW_Focus_normal_modeIcon_0, R.id.KSW_Focus_normal_modeIcon_1, R.id.KSW_Focus_normal_modeIcon_2, R.id.KSW_Focus_normal_modeIcon_3, R.id.KSW_Focus_normal_modeIcon_4, R.id.KSW_Focus_normal_modeIcon_5};
                        this.imgFocusList_normal_Text_zoom = new int[]{R.id.KSW_Focus_Text_normal_0, R.id.KSW_Focus_Text_normal_1, R.id.KSW_Focus_Text_normal_2, R.id.KSW_Focus_Text_normal_3, R.id.KSW_Focus_Text_normal_4, R.id.KSW_Focus_Text_normal_5};
                        this.imgFocusList_select_Text_zoom = new int[]{R.id.KSW_Focus_Text_select_0, R.id.KSW_Focus_Text_select_1, R.id.KSW_Focus_Text_select_2, R.id.KSW_Focus_Text_select_3, R.id.KSW_Focus_Text_select_4, R.id.KSW_Focus_Text_select_5};
                    }
                    this.imageViewFocusList_whats1 = new ImageView[btnList4.length];
                    this.imageViewFocusList_whats2 = new ImageView[btnList26.length];
                    this.imageViewFocusList_whats1_modeIcon = new ImageView[btnList4.length];
                    this.imageViewFocusList_whats2_modeIcon = new ImageView[btnList26.length];
                    if (this.ksw_m_b_Focus_image_zoom_evo) {
                        this.imageViewFocusList_whats1_normal_modeIcon_zoom = new ImageView[btnList4.length];
                        this.imageViewFocusList_whats2_normal_modeIcon_zoom = new ImageView[btnList26.length];
                        this.imageViewFocusList_whats1_normal_Text_zoom = new TextView[btnList4.length];
                        this.imageViewFocusList_whats2_normal_Text_zoom = new TextView[btnList26.length];
                        this.imageViewFocusList_whats1_select_Text_zoom = new TextView[btnList4.length];
                        this.imageViewFocusList_whats2_select_Text_zoom = new TextView[btnList26.length];
                    }
                    for (int i3 = 0; i3 < btnList4.length; i3++) {
                        this.imageViewFocusList_whats1[i3] = (ImageView) view1.findViewById(this.imgFocusList[i3]);
                        this.imageViewFocusList_whats1_modeIcon[i3] = (ImageView) view1.findViewById(this.imgFocusList_modeIcon[i3]);
                        if (this.ksw_m_b_Focus_image_zoom_evo) {
                            this.imageViewFocusList_whats1_normal_modeIcon_zoom[i3] = (ImageView) view1.findViewById(this.imgFocusList_normal_modeIcon_zoom[i3]);
                            this.imageViewFocusList_whats1_normal_Text_zoom[i3] = (TextView) view1.findViewById(this.imgFocusList_normal_Text_zoom[i3]);
                            this.imageViewFocusList_whats1_select_Text_zoom[i3] = (TextView) view1.findViewById(this.imgFocusList_select_Text_zoom[i3]);
                        }
                    }
                    for (int i4 = 0; i4 < btnList26.length; i4++) {
                        this.imageViewFocusList_whats2[i4] = (ImageView) view2.findViewById(this.imgFocusList[i4]);
                        this.imageViewFocusList_whats2_modeIcon[i4] = (ImageView) view2.findViewById(this.imgFocusList_modeIcon[i4]);
                        if (this.ksw_m_b_Focus_image_zoom_evo) {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i4] = (ImageView) view2.findViewById(this.imgFocusList_normal_modeIcon_zoom[i4]);
                            this.imageViewFocusList_whats2_normal_Text_zoom[i4] = (TextView) view2.findViewById(this.imgFocusList_normal_Text_zoom[i4]);
                            this.imageViewFocusList_whats2_select_Text_zoom[i4] = (TextView) view2.findViewById(this.imgFocusList_select_Text_zoom[i4]);
                        }
                    }
                } else if (this.m_iModeSet == 7) {
                    int[] btnList33 = null;
                    int[] btnList42 = null;
                    if (this.bIsShowSmallViewEVO_ID6) {
                        view1 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats1_xiao, (ViewGroup) null);
                        view2 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats2_xiao, (ViewGroup) null);
                        btnList13 = new int[]{R.id.music, R.id.navi, R.id.bluetooth, R.id.video, R.id.OriginaCar, R.id.btnBrowser};
                        btnList25 = new int[]{R.id.btnESFile, R.id.dvr, R.id.yibiaopan, R.id.phonelink, R.id.apps, R.id.setting};
                        this.tvYouLiang = (TextView) view2.findViewById(R.id.tv_youliang);
                        this.tvXuHangLiCheng = (TextView) view2.findViewById(R.id.tv_xushilicheng);
                    } else if (this.m_i_ksw_evo_id6_main_interface_index == 1) {
                        view1 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_gs_whats1, (ViewGroup) null);
                        view2 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_gs_whats2, (ViewGroup) null);
                        view3 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_gs_whats3, (ViewGroup) null);
                        btnList13 = new int[]{R.id.navi, R.id.music, R.id.bluetooth, R.id.video};
                        btnList25 = new int[]{R.id.OriginaCar, R.id.btnESFile, R.id.btnBrowser, R.id.dvr};
                        btnList33 = new int[]{R.id.yibiaopan, R.id.phonelink, R.id.apps, R.id.setting};
                        this.tvYouLiang = (TextView) view3.findViewById(R.id.tv_youliang);
                        this.tvXuHangLiCheng = (TextView) view3.findViewById(R.id.tv_xushilicheng);
                    } else if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                        view1 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats1_als, (ViewGroup) null);
                        view2 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats2_als, (ViewGroup) null);
                        view3 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_whats3_als, (ViewGroup) null);
                        btnList13 = new int[]{R.id.navi, R.id.OriginaCar, R.id.music, R.id.video};
                        btnList25 = new int[]{R.id.bluetooth, R.id.btnESFile, R.id.btnBrowser, R.id.dvr};
                        btnList33 = new int[]{R.id.yibiaopan, R.id.phonelink, R.id.apps, R.id.setting};
                        MyClockView mClockView = (MyClockView) findViewById(R.id.mClockView);
                        if (mClockView != null) {
                            mClockView.setClockXY(357, 354);
                        }
                    } else {
                        if (this.m_b_have_TV) {
                            view1 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats1, (ViewGroup) null);
                            view2 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats2_guowai, (ViewGroup) null);
                            view3 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats3_have_tv, (ViewGroup) null);
                            view4 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats4, (ViewGroup) null);
                        } else {
                            view1 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats1, (ViewGroup) null);
                            view2 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats2_guowai, (ViewGroup) null);
                            view3 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats3, (ViewGroup) null);
                            view4 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats4, (ViewGroup) null);
                        }
                        if (this.m_b_have_TV) {
                            btnList13 = new int[]{R.id.music, R.id.navi, R.id.bluetooth};
                            btnList25 = new int[]{R.id.video, R.id.OriginaCar, R.id.btnBrowser};
                            btnList33 = new int[]{R.id.btnESFile, R.id.tv, R.id.yibiaopan};
                            btnList42 = new int[]{R.id.phonelink, R.id.apps, R.id.setting};
                        } else {
                            btnList13 = new int[]{R.id.music, R.id.navi, R.id.bluetooth};
                            btnList25 = new int[]{R.id.video, R.id.OriginaCar, R.id.btnBrowser};
                            btnList33 = new int[]{R.id.btnESFile, R.id.dvr, R.id.yibiaopan};
                            btnList42 = new int[]{R.id.phonelink, R.id.apps, R.id.setting};
                        }
                        this.tvYouLiang = (TextView) view3.findViewById(R.id.tv_youliang);
                        this.tvXuHangLiCheng = (TextView) view3.findViewById(R.id.tv_xushilicheng);
                    }
                    if (btnList13 != null) {
                        for (int findViewById6 : btnList13) {
                            View btn6 = view1.findViewById(findViewById6);
                            if (btn6 != null) {
                                btn6.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList25 != null) {
                        for (int findViewById7 : btnList25) {
                            View btn7 = view2.findViewById(findViewById7);
                            if (btn7 != null) {
                                btn7.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList33 != null) {
                        for (int findViewById8 : btnList33) {
                            View btn8 = view3.findViewById(findViewById8);
                            if (btn8 != null) {
                                btn8.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList42 != null) {
                        for (int findViewById9 : btnList42) {
                            View btn9 = view4.findViewById(findViewById9);
                            if (btn9 != null) {
                                btn9.setOnClickListener(this);
                            }
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7, R.id.KSW_Focus_select_8, R.id.KSW_Focus_select_9, R.id.KSW_Focus_select_10, R.id.KSW_Focus_select_11};
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats3 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats4 = new ImageView[this.imgFocusList.length];
                    for (int i5 = 0; i5 < this.imgFocusList.length; i5++) {
                        this.imageViewFocusList_whats1[i5] = (ImageView) view1.findViewById(this.imgFocusList[i5]);
                        this.imageViewFocusList_whats2[i5] = (ImageView) view2.findViewById(this.imgFocusList[i5]);
                        if (view3 != null) {
                            this.imageViewFocusList_whats3[i5] = (ImageView) view3.findViewById(this.imgFocusList[i5]);
                        }
                        if (view4 != null) {
                            this.imageViewFocusList_whats4[i5] = (ImageView) view4.findViewById(this.imgFocusList[i5]);
                        }
                    }
                } else if (this.m_iModeSet == 11) {
                    int[] btnList34 = null;
                    if (this.m_i_ksw_jlr_main_interface_index == 1) {
                        view14 = mLi.inflate(R.layout.kesaiwei_1280x480_jlr_ui2_whats1, (ViewGroup) null);
                        view24 = mLi.inflate(R.layout.kesaiwei_1280x480_jlr_ui2_whats2, (ViewGroup) null);
                        view3 = mLi.inflate(R.layout.kesaiwei_1280x480_jlr_ui2_whats3, (ViewGroup) null);
                        btnList12 = new int[]{R.id.navi, R.id.video, R.id.music, R.id.OriginaCar};
                        btnList24 = new int[]{R.id.bluetooth, R.id.phonelink, R.id.yibiaopan, R.id.apps};
                        btnList34 = new int[]{R.id.btnBrowser, R.id.dvr, R.id.btnESFile, R.id.setting};
                    } else {
                        view14 = mLi.inflate(R.layout.kesaiwei_1280x480_jlr_whats1, (ViewGroup) null);
                        view24 = mLi.inflate(R.layout.kesaiwei_1280x480_jlr_whats2, (ViewGroup) null);
                        btnList12 = new int[]{R.id.navi, R.id.video, R.id.music, R.id.OriginaCar, R.id.dvr, R.id.setting};
                        btnList24 = new int[]{R.id.bluetooth, R.id.btnESFile, R.id.yibiaopan, R.id.phonelink, R.id.apps, R.id.btnBrowser};
                    }
                    if (btnList12 != null) {
                        for (int findViewById10 : btnList12) {
                            View btn10 = view1.findViewById(findViewById10);
                            if (btn10 != null) {
                                btn10.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList24 != null) {
                        for (int findViewById11 : btnList24) {
                            View btn11 = view2.findViewById(findViewById11);
                            if (btn11 != null) {
                                btn11.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList34 != null) {
                        for (int findViewById12 : btnList34) {
                            View btn12 = view3.findViewById(findViewById12);
                            if (btn12 != null) {
                                btn12.setOnClickListener(this);
                            }
                        }
                    }
                    TextView tvPhonelink = (TextView) view2.findViewById(R.id.tvPhonelink);
                    if (tvPhonelink != null) {
                        if (EventUtils.getInstallStatus(this, "com.didi365.miudrive.navi")) {
                            tvPhonelink.setText(getResources().getString(R.string.lb_miaojia));
                        } else {
                            tvPhonelink.setText(getResources().getString(R.string.lb_shoujihulian));
                        }
                    }
                } else if (this.m_iModeSet == 13) {
                    view1 = mLi.inflate(R.layout.normal_viewpager_1920x720_whats1_have_yuanche, (ViewGroup) null);
                    view2 = mLi.inflate(R.layout.normal_viewpager_1920x720_whats2, (ViewGroup) null);
                    int[] btnList5 = {R.id.navi, R.id.OriginaCar, R.id.music, R.id.video, R.id.bluetooth, R.id.btnESFile};
                    int[] btnList27 = {R.id.btnBrowser, R.id.aux, R.id.phonelink, R.id.apps, R.id.setting, R.id.dvr};
                    for (int findViewById13 : btnList5) {
                        View btn13 = view1.findViewById(findViewById13);
                        if (btn13 != null) {
                            btn13.setOnClickListener(this);
                        }
                    }
                    for (int findViewById14 : btnList27) {
                        View btn14 = view2.findViewById(findViewById14);
                        if (btn14 != null) {
                            btn14.setOnClickListener(this);
                        }
                    }
                    if (this.m_iUiIndex == 0) {
                        this.imgFocusList = new int[]{R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6};
                    } else if (this.m_iUiIndex == 1) {
                        this.imgFocusList = new int[]{R.id.ZF_Focus_select_1, R.id.ZF_Focus_select_2, R.id.ZF_Focus_select_3, R.id.ZF_Focus_select_4};
                    }
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                    for (int i6 = 0; i6 < this.imgFocusList.length; i6++) {
                        this.imageViewFocusList_whats1[i6] = (ImageView) view1.findViewById(this.imgFocusList[i6]);
                        this.imageViewFocusList_whats2[i6] = (ImageView) view2.findViewById(this.imgFocusList[i6]);
                    }
                } else if (this.m_iModeSet == 14) {
                    view1 = mLi.inflate(R.layout.kesaiwei_1024x600_chuanqi_cusp_ui2_whats1, (ViewGroup) null);
                    view2 = mLi.inflate(R.layout.kesaiwei_1024x600_chuanqi_cusp_ui2_whats2, (ViewGroup) null);
                    int[] btnList6 = {R.id.OriginaCar, R.id.music, R.id.navi, R.id.btnMusicPrev, R.id.btnMusicNext, R.id.ChkMusicPlayPause};
                    int[] btnList28 = {R.id.setting, R.id.video, R.id.BtnSetCalendar, R.id.btnVideoPrev, R.id.btnVideoNext, R.id.ChkVideoPlayPause};
                    for (int findViewById15 : btnList6) {
                        View btn15 = view1.findViewById(findViewById15);
                        if (btn15 != null) {
                            btn15.setOnClickListener(this);
                        }
                    }
                    for (int findViewById16 : btnList28) {
                        View btn16 = view2.findViewById(findViewById16);
                        if (btn16 != null) {
                            btn16.setOnClickListener(this);
                        }
                    }
                    this.mChkMusicPlayPause = (CheckBox) view1.findViewById(R.id.ChkMusicPlayPause);
                    this.mChkVideoPlayPause = (CheckBox) view2.findViewById(R.id.ChkVideoPlayPause);
                } else if (this.m_iModeSet == 15) {
                    view1 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id7_whats1, (ViewGroup) null);
                    view2 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id7_whats2, (ViewGroup) null);
                    view3 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id7_whats3, (ViewGroup) null);
                    int[] btnList14 = {R.id.navi, R.id.bluetooth};
                    int[] btnList29 = {R.id.music, R.id.video};
                    int[] btnList35 = {R.id.OriginaCar, R.id.yibiaopan};
                    if (btnList14 != null) {
                        for (int findViewById17 : btnList14) {
                            View btn17 = view1.findViewById(findViewById17);
                            if (btn17 != null) {
                                btn17.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList29 != null) {
                        for (int findViewById18 : btnList29) {
                            View btn18 = view2.findViewById(findViewById18);
                            if (btn18 != null) {
                                btn18.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList35 != null) {
                        for (int findViewById19 : btnList35) {
                            View btn19 = view3.findViewById(findViewById19);
                            if (btn19 != null) {
                                btn19.setOnClickListener(this);
                            }
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5};
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats3 = new ImageView[this.imgFocusList.length];
                    for (int i7 = 0; i7 < this.imgFocusList.length; i7++) {
                        this.imageViewFocusList_whats1[i7] = (ImageView) view1.findViewById(this.imgFocusList[i7]);
                        this.imageViewFocusList_whats2[i7] = (ImageView) view2.findViewById(this.imgFocusList[i7]);
                        if (view3 != null) {
                            this.imageViewFocusList_whats3[i7] = (ImageView) view3.findViewById(this.imgFocusList[i7]);
                        }
                    }
                    this.ivMediaTypeBg = (ImageView) view2.findViewById(R.id.iv_MediaTypeBg);
                    this.tvYouLiang = (TextView) view3.findViewById(R.id.tv_youliang);
                    this.tvWenDu = (TextView) view3.findViewById(R.id.tv_wendu);
                    this.tvShouSha = (TextView) view3.findViewById(R.id.tv_shousha);
                    this.tvAnQuanDai = (TextView) view3.findViewById(R.id.tv_anquandai);
                    this.tvShunShiSuDu = (TextView) view3.findViewById(R.id.tvShunShiSuDu);
                    this.myClockSpeed = (MyQAnalogClock2) view3.findViewById(R.id.myClockSpeed);
                    this.myClockSpeed.setmTvCur(this.tvShunShiSuDu);
                } else if (this.m_iModeSet == 16) {
                    view1 = mLi.inflate(R.layout.kesaiwei_1920x720_evo_id7_whats1, (ViewGroup) null);
                    view2 = mLi.inflate(R.layout.kesaiwei_1920x720_evo_id7_whats2, (ViewGroup) null);
                    view3 = mLi.inflate(R.layout.kesaiwei_1920x720_evo_id7_whats3, (ViewGroup) null);
                    int[] btnList15 = {R.id.navi, R.id.bluetooth};
                    int[] btnList210 = {R.id.music, R.id.video};
                    int[] btnList36 = {R.id.OriginaCar, R.id.yibiaopan};
                    if (btnList15 != null) {
                        for (int findViewById20 : btnList15) {
                            View btn20 = view1.findViewById(findViewById20);
                            if (btn20 != null) {
                                btn20.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList210 != null) {
                        for (int findViewById21 : btnList210) {
                            View btn21 = view2.findViewById(findViewById21);
                            if (btn21 != null) {
                                btn21.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList36 != null) {
                        for (int findViewById22 : btnList36) {
                            View btn22 = view3.findViewById(findViewById22);
                            if (btn22 != null) {
                                btn22.setOnClickListener(this);
                            }
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5};
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats3 = new ImageView[this.imgFocusList.length];
                    for (int i8 = 0; i8 < this.imgFocusList.length; i8++) {
                        this.imageViewFocusList_whats1[i8] = (ImageView) view1.findViewById(this.imgFocusList[i8]);
                        this.imageViewFocusList_whats2[i8] = (ImageView) view2.findViewById(this.imgFocusList[i8]);
                        if (view3 != null) {
                            this.imageViewFocusList_whats3[i8] = (ImageView) view3.findViewById(this.imgFocusList[i8]);
                        }
                    }
                    this.ivMediaTypeBg = (ImageView) view2.findViewById(R.id.iv_MediaTypeBg);
                    this.tvYouLiang = (TextView) view3.findViewById(R.id.tv_youliang);
                    this.tvWenDu = (TextView) view3.findViewById(R.id.tv_wendu);
                    this.tvShouSha = (TextView) view3.findViewById(R.id.tv_shousha);
                    this.tvAnQuanDai = (TextView) view3.findViewById(R.id.tv_anquandai);
                    this.tvShunShiSuDu = (TextView) view3.findViewById(R.id.tvShunShiSuDu);
                    this.myClockSpeed = (MyQAnalogClock2) view3.findViewById(R.id.myClockSpeed);
                    this.myClockSpeed.setmTvCur(this.tvShunShiSuDu);
                } else if (this.m_iModeSet == 20) {
                    int[] btnList37 = null;
                    int[] btnList43 = null;
                    if (this.bIsShowSmallViewEVO_ID6) {
                        view13 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats1_xiao, (ViewGroup) null);
                        view23 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_whats2_xiao, (ViewGroup) null);
                        btnList1 = new int[]{R.id.music, R.id.navi, R.id.bluetooth, R.id.video, R.id.OriginaCar, R.id.btnBrowser};
                        btnList23 = new int[]{R.id.btnESFile, R.id.dvr, R.id.yibiaopan, R.id.phonelink, R.id.apps, R.id.setting};
                        this.tvYouLiang = (TextView) view23.findViewById(R.id.tv_youliang);
                        this.tvXuHangLiCheng = (TextView) view23.findViewById(R.id.tv_xushilicheng);
                    } else {
                        view13 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_new_whats1, (ViewGroup) null);
                        view23 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_new_whats2, (ViewGroup) null);
                        view3 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_new_whats3, (ViewGroup) null);
                        view4 = mLi.inflate(R.layout.kesaiwei_1280x480_evo_id6_new_whats4, (ViewGroup) null);
                        btnList1 = new int[]{R.id.music, R.id.navi, R.id.bluetooth};
                        btnList23 = new int[]{R.id.video, R.id.OriginaCar, R.id.btnBrowser};
                        btnList37 = new int[]{R.id.btnESFile, R.id.dvr, R.id.yibiaopan};
                        btnList43 = new int[]{R.id.phonelink, R.id.apps, R.id.setting};
                        this.tvYouLiang = (TextView) view3.findViewById(R.id.tv_youliang);
                        this.tvXuHangLiCheng = (TextView) view3.findViewById(R.id.tv_xushilicheng);
                    }
                    if (btnList1 != null) {
                        for (int findViewById23 : btnList1) {
                            View btn23 = view1.findViewById(findViewById23);
                            if (btn23 != null) {
                                btn23.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList23 != null) {
                        for (int findViewById24 : btnList23) {
                            View btn24 = view2.findViewById(findViewById24);
                            if (btn24 != null) {
                                btn24.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList37 != null) {
                        for (int findViewById25 : btnList37) {
                            View btn25 = view3.findViewById(findViewById25);
                            if (btn25 != null) {
                                btn25.setOnClickListener(this);
                            }
                        }
                    }
                    if (btnList43 != null) {
                        for (int findViewById26 : btnList43) {
                            View btn26 = view4.findViewById(findViewById26);
                            if (btn26 != null) {
                                btn26.setOnClickListener(this);
                            }
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7, R.id.KSW_Focus_select_8, R.id.KSW_Focus_select_9, R.id.KSW_Focus_select_10, R.id.KSW_Focus_select_11};
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats3 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats4 = new ImageView[this.imgFocusList.length];
                    for (int i9 = 0; i9 < this.imgFocusList.length; i9++) {
                        this.imageViewFocusList_whats1[i9] = (ImageView) view1.findViewById(this.imgFocusList[i9]);
                        this.imageViewFocusList_whats2[i9] = (ImageView) view2.findViewById(this.imgFocusList[i9]);
                        if (view3 != null) {
                            this.imageViewFocusList_whats3[i9] = (ImageView) view3.findViewById(this.imgFocusList[i9]);
                        }
                        if (view4 != null) {
                            this.imageViewFocusList_whats4[i9] = (ImageView) view4.findViewById(this.imgFocusList[i9]);
                        }
                    }
                } else {
                    view1 = mLi.inflate(R.layout.kesaiwei_1280x480_whats1, (ViewGroup) null);
                    view2 = mLi.inflate(R.layout.kesaiwei_1280x480_whats2, (ViewGroup) null);
                    int[] btnList7 = {R.id.apps, R.id.navi, R.id.music, R.id.video, R.id.bluetooth, R.id.phonelink, R.id.dvr, R.id.setting};
                    int[] btnList211 = {R.id.aux, R.id.tv, R.id.btnDvd};
                    for (int findViewById27 : btnList7) {
                        View btn27 = view1.findViewById(findViewById27);
                        if (btn27 != null) {
                            btn27.setOnClickListener(this);
                        }
                    }
                    for (int findViewById28 : btnList211) {
                        View btn28 = view2.findViewById(findViewById28);
                        if (btn28 != null) {
                            btn28.setOnClickListener(this);
                        }
                    }
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7};
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                    for (int i10 = 0; i10 < this.imgFocusList.length; i10++) {
                        this.imageViewFocusList_whats1[i10] = (ImageView) view1.findViewById(this.imgFocusList[i10]);
                        this.imageViewFocusList_whats2[i10] = (ImageView) view2.findViewById(this.imgFocusList[i10]);
                    }
                }
            } else if (LauncherApplication.m_iUITypeVer == 101) {
                if (this.m_iUiIndex == 0) {
                    if (this.bHaveDsp) {
                        view15 = mLi.inflate(R.layout.normal_viewpager_1920x720_whats1_have_dsp, (ViewGroup) null);
                    } else {
                        view15 = mLi.inflate(R.layout.normal_viewpager_1920x720_whats1, (ViewGroup) null);
                    }
                    view25 = mLi.inflate(R.layout.normal_viewpager_1920x720_whats2, (ViewGroup) null);
                } else if (this.m_iUiIndex == 1) {
                    view15 = mLi.inflate(R.layout.zhifang_1920x720_whats1, (ViewGroup) null);
                    view25 = mLi.inflate(R.layout.zhifang_1920x720_whats2, (ViewGroup) null);
                } else if (this.m_iUiIndex == 2) {
                    view15 = mLi.inflate(R.layout.changtong_1920x720_ui1_whats1, (ViewGroup) null);
                    view25 = mLi.inflate(R.layout.changtong_1920x720_ui1_whats2, (ViewGroup) null);
                } else if (this.m_iUiIndex == 3) {
                    view15 = mLi.inflate(R.layout.changtong_1920x720_ui2_whats1, (ViewGroup) null);
                    view25 = mLi.inflate(R.layout.changtong_1920x720_ui2_whats2, (ViewGroup) null);
                }
                if (this.m_iUiIndex == 1) {
                    btnList = new int[]{R.id.navi, R.id.radio, R.id.music, R.id.dsp};
                    btnList2 = new int[]{R.id.apps, R.id.video, R.id.bluetooth, R.id.setting};
                } else if (this.m_iUiIndex == 2 || this.m_iUiIndex == 3) {
                    btnList = new int[]{R.id.navi, R.id.bluetooth, R.id.music, R.id.video, R.id.setting, R.id.radio, R.id.aux, R.id.btnBrowser};
                    btnList2 = new int[]{R.id.btnCarInfo, R.id.btnESFile, R.id.phonelink, R.id.apps};
                } else {
                    if (this.bHaveDsp) {
                        btnList = new int[]{R.id.navi, R.id.radio, R.id.music, R.id.video, R.id.bluetooth, R.id.dsp};
                    } else {
                        btnList = new int[]{R.id.navi, R.id.radio, R.id.music, R.id.video, R.id.bluetooth, R.id.btnESFile};
                    }
                    btnList2 = new int[]{R.id.btnBrowser, R.id.aux, R.id.phonelink, R.id.apps, R.id.setting, R.id.dvr};
                }
                for (int findViewById29 : btnList) {
                    View btn29 = view1.findViewById(findViewById29);
                    if (btn29 != null) {
                        btn29.setOnClickListener(this);
                    }
                }
                for (int findViewById30 : btnList2) {
                    View btn30 = view2.findViewById(findViewById30);
                    if (btn30 != null) {
                        btn30.setOnClickListener(this);
                    }
                }
                if (this.m_iUiIndex == 0) {
                    this.imgFocusList = new int[]{R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6};
                } else if (this.m_iUiIndex == 1) {
                    this.imgFocusList = new int[]{R.id.ZF_Focus_select_1, R.id.ZF_Focus_select_2, R.id.ZF_Focus_select_3, R.id.ZF_Focus_select_4};
                }
                if (this.imgFocusList != null) {
                    this.imageViewFocusList_whats1 = new ImageView[this.imgFocusList.length];
                    this.imageViewFocusList_whats2 = new ImageView[this.imgFocusList.length];
                }
                if (this.imgFocusList != null) {
                    for (int i11 = 0; i11 < this.imgFocusList.length; i11++) {
                        this.imageViewFocusList_whats1[i11] = (ImageView) view1.findViewById(this.imgFocusList[i11]);
                        this.imageViewFocusList_whats2[i11] = (ImageView) view2.findViewById(this.imgFocusList[i11]);
                    }
                }
            } else {
                view1 = mLi.inflate(R.layout.kaidilake_whats1, (ViewGroup) null);
                view2 = mLi.inflate(R.layout.kaidilake_whats2, (ViewGroup) null);
                int[] btnList8 = {R.id.navi, R.id.atsl, R.id.music, R.id.video, R.id.setting, R.id.bluetooth, R.id.btMusic, R.id.aux, R.id.tv, R.id.dvr};
                for (int loop = 0; loop < btnList8.length / 2; loop++) {
                    View btn31 = view1.findViewById(btnList8[loop]);
                    if (btn31 != null) {
                        btn31.setOnClickListener(this);
                    }
                }
                for (int loop2 = btnList8.length / 2; loop2 < btnList8.length; loop2++) {
                    View btn32 = view2.findViewById(btnList8[loop2]);
                    if (btn32 != null) {
                        btn32.setOnClickListener(this);
                    }
                }
            }
            ArrayList<View> views = new ArrayList<>();
            if (LauncherApplication.m_iUITypeVer == 41 && (this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6)) {
                if (this.m_i_ksw_evo_main_interface_index == 1) {
                    views.add(view2);
                    views.add(view1);
                } else {
                    views.add(view1);
                    views.add(view2);
                }
            } else if (this.m_iModeSet == 7 || this.m_iModeSet == 20) {
                if (this.bIsShowSmallViewEVO_ID6) {
                    views.add(view1);
                    views.add(view2);
                } else if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    views.add(view1);
                    views.add(view2);
                    views.add(view3);
                } else {
                    views.add(view1);
                    views.add(view2);
                    views.add(view3);
                    views.add(view4);
                }
            } else if (this.m_iModeSet == 11) {
                if (this.m_i_ksw_jlr_main_interface_index == 1) {
                    views.add(view1);
                    views.add(view2);
                    views.add(view3);
                } else {
                    views.add(view1);
                    views.add(view2);
                }
            } else if (this.m_iModeSet == 15 || this.m_iModeSet == 16) {
                views.add(view1);
                views.add(view2);
                views.add(view3);
            } else {
                views.add(view1);
                views.add(view2);
            }
            final ArrayList<View> arrayList = views;
            PagerAdapter mPagerAdapter = new PagerAdapter() {
                public int getCount() {
                    return arrayList.size();
                }

                public boolean isViewFromObject(View view, Object object) {
                    return view == object;
                }

                public Object instantiateItem(ViewGroup container, int position) {
                    ((ViewPager) container).addView((View) arrayList.get(position));
                    return arrayList.get(position);
                }

                public void destroyItem(ViewGroup container, int position, Object object) {
                    ((ViewPager) container).removeView((View) arrayList.get(position));
                }
            };
            if (this.mViewPager != null) {
                this.mViewPager.setAdapter(mPagerAdapter);
            }
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        public MyOnPageChangeListener() {
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            Log.i(Launcher.TAG, "onPageSelected: position == " + position);
            switch (position) {
                case 0:
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            if (Launcher.this.m_iUiIndex != 1) {
                                if (Launcher.this.m_iUiIndex != 2) {
                                    if (Launcher.this.m_iUiIndex != 3) {
                                        int unused = Launcher.this.m_iPageCurrFocus = 0;
                                        int unused2 = Launcher.this.m_iCurrFocus = 5;
                                        break;
                                    } else {
                                        Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_d));
                                        Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_n));
                                        break;
                                    }
                                } else {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_d));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_n));
                                    break;
                                }
                            } else {
                                int unused3 = Launcher.this.m_iPageCurrFocus = 0;
                                int unused4 = Launcher.this.m_iCurrFocus = 3;
                                break;
                            }
                        }
                    } else if (Launcher.this.m_iModeSet != 2 && Launcher.this.m_iModeSet != 4 && Launcher.this.m_iModeSet != 6) {
                        if (Launcher.this.m_iModeSet != 7 && Launcher.this.m_iModeSet != 20) {
                            if (Launcher.this.m_iModeSet != 11) {
                                if (Launcher.this.m_iModeSet != 13) {
                                    if (Launcher.this.m_iModeSet != 15) {
                                        if (Launcher.this.m_iModeSet != 16) {
                                            int unused5 = Launcher.this.m_iPageCurrFocus = 0;
                                            int unused6 = Launcher.this.m_iCurrFocus = 7;
                                            break;
                                        } else {
                                            int unused7 = Launcher.this.m_iPageCurrFocus = 0;
                                            int unused8 = Launcher.this.m_iCurrFocus = 1;
                                            Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_d));
                                            Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                            Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                            break;
                                        }
                                    } else {
                                        int unused9 = Launcher.this.m_iPageCurrFocus = 0;
                                        int unused10 = Launcher.this.m_iCurrFocus = 1;
                                        Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_d));
                                        Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                                        Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                                        break;
                                    }
                                } else {
                                    int unused11 = Launcher.this.m_iPageCurrFocus = 0;
                                    int unused12 = Launcher.this.m_iCurrFocus = 5;
                                    break;
                                }
                            } else {
                                int unused13 = Launcher.this.m_iPageCurrFocus = 0;
                                if (Launcher.this.m_i_ksw_jlr_main_interface_index != 1) {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_d));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    break;
                                } else {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_d));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    break;
                                }
                            }
                        } else if (!Launcher.this.bIsShowSmallViewEVO_ID6) {
                            if (Launcher.this.m_i_ksw_evo_id6_main_interface_index != 1 && Launcher.this.m_i_ksw_evo_id6_main_interface_index != 2) {
                                int unused14 = Launcher.this.m_iPageCurrFocus = 0;
                                int unused15 = Launcher.this.m_iCurrFocus = 2;
                                break;
                            } else {
                                int unused16 = Launcher.this.m_iPageCurrFocus = 0;
                                int unused17 = Launcher.this.m_iCurrFocus = 3;
                                break;
                            }
                        } else {
                            int unused18 = Launcher.this.m_iPageCurrFocus = 0;
                            int unused19 = Launcher.this.m_iCurrFocus = 5;
                            break;
                        }
                    } else {
                        int unused20 = Launcher.this.m_iPageCurrFocus = 0;
                        int unused21 = Launcher.this.m_iCurrFocus = 5;
                        break;
                    }
                    break;
                case 1:
                    if (LauncherApplication.m_iUITypeVer != 41) {
                        if (LauncherApplication.m_iUITypeVer == 101) {
                            if (Launcher.this.m_iUiIndex != 1) {
                                if (Launcher.this.m_iUiIndex != 2) {
                                    if (Launcher.this.m_iUiIndex != 3) {
                                        int unused22 = Launcher.this.m_iPageCurrFocus = 1;
                                        int unused23 = Launcher.this.m_iCurrFocus = 6;
                                        break;
                                    } else {
                                        Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_n));
                                        Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_ui2_yuandian_d));
                                        break;
                                    }
                                } else {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_n));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.changtong_1920x720_yuandian_d));
                                    break;
                                }
                            } else {
                                int unused24 = Launcher.this.m_iPageCurrFocus = 1;
                                int unused25 = Launcher.this.m_iCurrFocus = 4;
                                break;
                            }
                        }
                    } else if (Launcher.this.m_iModeSet != 2 && Launcher.this.m_iModeSet != 4 && Launcher.this.m_iModeSet != 6) {
                        if (Launcher.this.m_iModeSet != 7 && Launcher.this.m_iModeSet != 20) {
                            if (Launcher.this.m_iModeSet != 11) {
                                if (Launcher.this.m_iModeSet != 13) {
                                    if (Launcher.this.m_iModeSet != 15) {
                                        if (Launcher.this.m_iModeSet != 16) {
                                            int unused26 = Launcher.this.m_iPageCurrFocus = 1;
                                            int unused27 = Launcher.this.m_iCurrFocus = 8;
                                            break;
                                        } else {
                                            int unused28 = Launcher.this.m_iPageCurrFocus = 1;
                                            if (Launcher.m_iLastPageCurrFocus == 0) {
                                                int unused29 = Launcher.this.m_iCurrFocus = 2;
                                            } else if (Launcher.m_iLastPageCurrFocus == 2) {
                                                int unused30 = Launcher.this.m_iCurrFocus = 3;
                                            } else {
                                                int unused31 = Launcher.this.m_iCurrFocus = 2;
                                            }
                                            Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                            Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_d));
                                            Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                            break;
                                        }
                                    } else {
                                        int unused32 = Launcher.this.m_iPageCurrFocus = 1;
                                        if (Launcher.m_iLastPageCurrFocus == 0) {
                                            int unused33 = Launcher.this.m_iCurrFocus = 2;
                                        } else if (Launcher.m_iLastPageCurrFocus == 2) {
                                            int unused34 = Launcher.this.m_iCurrFocus = 3;
                                        } else {
                                            int unused35 = Launcher.this.m_iCurrFocus = 2;
                                        }
                                        Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                                        Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_d));
                                        Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                                        break;
                                    }
                                } else {
                                    int unused36 = Launcher.this.m_iPageCurrFocus = 1;
                                    int unused37 = Launcher.this.m_iCurrFocus = 6;
                                    break;
                                }
                            } else {
                                int unused38 = Launcher.this.m_iPageCurrFocus = 1;
                                if (Launcher.this.m_i_ksw_jlr_main_interface_index != 1) {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_d));
                                    break;
                                } else {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_d));
                                    Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    break;
                                }
                            }
                        } else if (!Launcher.this.bIsShowSmallViewEVO_ID6) {
                            if (Launcher.this.m_i_ksw_evo_id6_main_interface_index != 1 && Launcher.this.m_i_ksw_evo_id6_main_interface_index != 2) {
                                int unused39 = Launcher.this.m_iPageCurrFocus = 1;
                                if (Launcher.m_iLastPageCurrFocus != 0) {
                                    if (Launcher.m_iLastPageCurrFocus != 2) {
                                        int unused40 = Launcher.this.m_iCurrFocus = 3;
                                        break;
                                    } else {
                                        int unused41 = Launcher.this.m_iCurrFocus = 5;
                                        break;
                                    }
                                } else {
                                    int unused42 = Launcher.this.m_iCurrFocus = 3;
                                    break;
                                }
                            } else {
                                int unused43 = Launcher.this.m_iPageCurrFocus = 1;
                                if (Launcher.m_iLastPageCurrFocus != 0) {
                                    if (Launcher.m_iLastPageCurrFocus != 2) {
                                        int unused44 = Launcher.this.m_iCurrFocus = 4;
                                        break;
                                    } else {
                                        int unused45 = Launcher.this.m_iCurrFocus = 7;
                                        break;
                                    }
                                } else {
                                    int unused46 = Launcher.this.m_iCurrFocus = 4;
                                    break;
                                }
                            }
                        } else {
                            int unused47 = Launcher.this.m_iPageCurrFocus = 1;
                            int unused48 = Launcher.this.m_iCurrFocus = 6;
                            break;
                        }
                    } else {
                        int unused49 = Launcher.this.m_iPageCurrFocus = 1;
                        int unused50 = Launcher.this.m_iCurrFocus = 6;
                        break;
                    }
                    break;
                case 2:
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        if (Launcher.this.m_iModeSet != 7 && Launcher.this.m_iModeSet != 20) {
                            if (Launcher.this.m_iModeSet != 11) {
                                if (Launcher.this.m_iModeSet != 15) {
                                    if (Launcher.this.m_iModeSet == 16) {
                                        int unused51 = Launcher.this.m_iPageCurrFocus = 2;
                                        int unused52 = Launcher.this.m_iCurrFocus = 4;
                                        Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                        Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_n));
                                        Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1920x720_evo_id7_yuan_d));
                                        break;
                                    }
                                } else {
                                    int unused53 = Launcher.this.m_iPageCurrFocus = 2;
                                    int unused54 = Launcher.this.m_iCurrFocus = 4;
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_n));
                                    Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_evo_id7_yuan_d));
                                    break;
                                }
                            } else {
                                int unused55 = Launcher.this.m_iPageCurrFocus = 2;
                                if (Launcher.this.m_i_ksw_jlr_main_interface_index == 1) {
                                    Launcher.this.mPage0.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    Launcher.this.mPage1.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_n));
                                    Launcher.this.mPage2.setImageDrawable(Launcher.this.getResources().getDrawable(R.drawable.kesaiwei_1280x480_jlr_dian_d));
                                    break;
                                }
                            }
                        } else if (Launcher.this.m_i_ksw_evo_id6_main_interface_index != 1 && Launcher.this.m_i_ksw_evo_id6_main_interface_index != 2) {
                            int unused56 = Launcher.this.m_iPageCurrFocus = 2;
                            if (Launcher.m_iLastPageCurrFocus != 1) {
                                if (Launcher.m_iLastPageCurrFocus != 3) {
                                    int unused57 = Launcher.this.m_iCurrFocus = 6;
                                    break;
                                } else {
                                    int unused58 = Launcher.this.m_iCurrFocus = 8;
                                    break;
                                }
                            } else {
                                int unused59 = Launcher.this.m_iCurrFocus = 6;
                                break;
                            }
                        } else {
                            int unused60 = Launcher.this.m_iPageCurrFocus = 2;
                            int unused61 = Launcher.this.m_iCurrFocus = 8;
                            break;
                        }
                    }
                    break;
                case 3:
                    if (LauncherApplication.m_iUITypeVer == 41 && (Launcher.this.m_iModeSet == 7 || Launcher.this.m_iModeSet == 20)) {
                        int unused62 = Launcher.this.m_iPageCurrFocus = 3;
                        int unused63 = Launcher.this.m_iCurrFocus = 9;
                        break;
                    }
            }
            if (LauncherApplication.m_iUITypeVer == 41) {
                if (Launcher.this.m_iModeSet == 2 || Launcher.this.m_iModeSet == 4 || Launcher.this.m_iModeSet == 6) {
                    Launcher.this.refreshFocusView_evo();
                } else if (Launcher.this.m_iModeSet == 7 || Launcher.this.m_iModeSet == 20) {
                    Launcher.this.refreshFocusView_evo_id6();
                } else if (!(Launcher.this.m_iModeSet == 11 || Launcher.this.m_iModeSet == 14)) {
                    if (Launcher.this.m_iModeSet == 13) {
                        Launcher.this.refreshFocusView_normal_1920x720();
                    } else if (Launcher.this.m_iModeSet == 15 || Launcher.this.m_iModeSet == 16) {
                        Launcher.this.refreshFocusView_evo_id7();
                    } else {
                        Launcher.this.refreshFocusView_Vertical();
                    }
                }
            } else if (LauncherApplication.m_iUITypeVer == 101) {
                Launcher.this.refreshFocusView_normal_1920x720();
            }
            Log.i(Launcher.TAG, "onPageSelected: m_iLastPageCurrFocus == " + Launcher.m_iLastPageCurrFocus);
            int unused64 = Launcher.m_iLastPageCurrFocus = position;
            if (Launcher.this.m_iModeSet == 15 || Launcher.this.m_iModeSet == 16) {
                int unused65 = Launcher.this.m_iLastCurrFocus = Launcher.m_iLastPageCurrFocus * 2;
            }
            Log.i(Launcher.TAG, "onPageSelected: m_iCurrFocus = " + Launcher.this.m_iCurrFocus);
        }

        public void onPageScrollStateChanged(int state) {
            Log.i(Launcher.TAG, "onPageScrollStateChanged: state = " + state);
            if (LauncherApplication.m_iUITypeVer != 41) {
                return;
            }
            if (Launcher.this.m_iModeSet == 7 || Launcher.this.m_iModeSet == 20) {
                Launcher.this.setupPrivNextVisibility_id6();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTimerInfor() {
        String str;
        Calendar c = Calendar.getInstance();
        int mYear = c.get(1);
        int mMonth = c.get(2) + 1;
        int mDay = c.get(5);
        int mWay = c.get(7);
        int mHour = c.get(11);
        int mMin = c.get(12);
        String strTimeFormat = Settings.System.getString(getContentResolver(), "time_12_24");
        if (strTimeFormat == null || !strTimeFormat.equals("24")) {
            if (mHour > 12) {
                mHour -= 12;
            } else if (mHour == 0) {
                mHour = 12;
            }
        }
        String str2 = String.format("%d-%02d-%02d", new Object[]{Integer.valueOf(mYear), Integer.valueOf(mMonth), Integer.valueOf(mDay)});
        if (this.mTimeYearMD != null) {
            this.mTimeYearMD.setText(str2);
        }
        int[] weeklst = {0, R.string.lbl_sun, R.string.lbl_mon, R.string.lbl_tue, R.string.lbl_wed, R.string.lbl_thu, R.string.lbl_fri, R.string.lbl_sat};
        if (this.mTimerWeek != null) {
            this.mTimerWeek.setText(getString(weeklst[mWay]));
        }
        int[] numlist = {R.drawable.t0, R.drawable.t1, R.drawable.t2, R.drawable.t3, R.drawable.t4, R.drawable.t5, R.drawable.t6, R.drawable.t7, R.drawable.t8, R.drawable.t9};
        int[] iArr = {R.drawable.h0, R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6, R.drawable.h7, R.drawable.h8, R.drawable.h9};
        int[] iArr2 = {R.drawable.z0, R.drawable.z1, R.drawable.z2, R.drawable.z3, R.drawable.z4, R.drawable.z5, R.drawable.z6, R.drawable.z7, R.drawable.z8, R.drawable.z9};
        int[] numlist_chwy = {R.drawable.chwy_0, R.drawable.chwy_1, R.drawable.chwy_2, R.drawable.chwy_3, R.drawable.chwy_4, R.drawable.chwy_5, R.drawable.chwy_6, R.drawable.chwy_7, R.drawable.chwy_8, R.drawable.chwy_9};
        int[] numlist_zf = {R.drawable.zf_0, R.drawable.zf_1, R.drawable.zf_2, R.drawable.zf_3, R.drawable.zf_4, R.drawable.zf_5, R.drawable.zf_6, R.drawable.zf_7, R.drawable.zf_8, R.drawable.zf_9};
        if (this.app != null) {
            if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 8) {
                if (this.mTimerHourH != null) {
                    this.mTimerHourH.setImageResource(numlist_chwy[mHour / 10]);
                }
                if (this.mTimerHourL != null) {
                    this.mTimerHourL.setImageResource(numlist_chwy[mHour % 10]);
                }
                if (this.mTimerMinH != null) {
                    this.mTimerMinH.setImageResource(numlist_chwy[mMin / 10]);
                }
                if (this.mTimerMinL != null) {
                    this.mTimerMinL.setImageResource(numlist_chwy[mMin % 10]);
                }
            } else if (LauncherApplication.m_iUITypeVer == 101 && this.m_iUiIndex == 1) {
                if (this.mTimerHourH != null) {
                    this.mTimerHourH.setImageResource(numlist_zf[mHour / 10]);
                }
                if (this.mTimerHourL != null) {
                    this.mTimerHourL.setImageResource(numlist_zf[mHour % 10]);
                }
                if (this.mTimerMinH != null) {
                    this.mTimerMinH.setImageResource(numlist_zf[mMin / 10]);
                }
                if (this.mTimerMinL != null) {
                    this.mTimerMinL.setImageResource(numlist_zf[mMin % 10]);
                }
            } else {
                if (this.mTimerHourH != null) {
                    this.mTimerHourH.setImageResource(numlist[mHour / 10]);
                }
                if (this.mTimerHourL != null) {
                    this.mTimerHourL.setImageResource(numlist[mHour % 10]);
                }
                if (this.mTimerMinH != null) {
                    this.mTimerMinH.setImageResource(numlist[mMin / 10]);
                }
                if (this.mTimerMinL != null) {
                    this.mTimerMinL.setImageResource(numlist[mMin % 10]);
                }
            }
        }
        if (c.get(9) == 0) {
            str = getString(R.string.lbl_am);
        } else {
            str = getString(R.string.lbl_pm);
        }
        if (this.mTwCurDataTimeAMPM != null) {
            this.mTwCurDataTimeAMPM.setText(str);
        }
        CalendarUtil myCal = new CalendarUtil();
        myCal.setGregorian(mYear, mMonth, mDay);
        myCal.computeChineseFields();
        myCal.computeSolarTerms();
        if (this.mTwChineseYear != null) {
            this.mTwChineseYear.setText(myCal.getChinesYearStr());
        }
        if (this.mTwChineseMonthDay != null) {
            this.mTwChineseMonthDay.setText(myCal.getChinesMonthStr() + myCal.getChinesDayStr());
        }
        String str3 = String.format("%02d:%02d", new Object[]{Integer.valueOf(mHour), Integer.valueOf(mMin)});
        if (this.mTvCurTime != null) {
            this.mTvCurTime.setText(str3);
        }
    }

    /* access modifiers changed from: package-private */
    public int getWeatherResouceID(String str, boolean dayNight) {
        if (str == null || str.length() == 0) {
            return R.drawable.d99;
        }
        switch (Integer.valueOf(str).intValue()) {
            case 0:
                return dayNight ? R.drawable.d0 : R.drawable.n00;
            case 1:
                return dayNight ? R.drawable.d1 : R.drawable.n01;
            case 2:
                return dayNight ? R.drawable.d2 : R.drawable.n02;
            case 3:
                return dayNight ? R.drawable.d3 : R.drawable.n03;
            case 4:
                return dayNight ? R.drawable.d4 : R.drawable.n04;
            case 5:
                return dayNight ? R.drawable.d5 : R.drawable.n05;
            case 6:
                return dayNight ? R.drawable.d6 : R.drawable.n06;
            case 7:
                return dayNight ? R.drawable.d7 : R.drawable.n07;
            case 8:
                return dayNight ? R.drawable.d8 : R.drawable.n08;
            case 9:
                return dayNight ? R.drawable.d9 : R.drawable.n09;
            case 10:
                return dayNight ? R.drawable.d10 : R.drawable.n10;
            case 11:
                return dayNight ? R.drawable.d11 : R.drawable.n11;
            case 12:
                return dayNight ? R.drawable.d12 : R.drawable.n12;
            case 13:
                return dayNight ? R.drawable.d13 : R.drawable.n13;
            case 14:
                return dayNight ? R.drawable.d14 : R.drawable.n14;
            case 15:
                return dayNight ? R.drawable.d15 : R.drawable.n15;
            case 16:
                return dayNight ? R.drawable.d16 : R.drawable.n16;
            case 17:
                return dayNight ? R.drawable.d17 : R.drawable.n17;
            case 18:
                return dayNight ? R.drawable.d18 : R.drawable.n18;
            case 19:
                return dayNight ? R.drawable.d19 : R.drawable.n19;
            case 20:
                return dayNight ? R.drawable.d20 : R.drawable.n20;
            case 21:
                return dayNight ? R.drawable.d21 : R.drawable.n21;
            case 22:
                return dayNight ? R.drawable.d22 : R.drawable.n22;
            case 23:
                return dayNight ? R.drawable.d23 : R.drawable.n23;
            case 24:
                return dayNight ? R.drawable.d24 : R.drawable.n24;
            case 25:
                return dayNight ? R.drawable.d25 : R.drawable.n25;
            case 26:
                return dayNight ? R.drawable.d26 : R.drawable.n26;
            case 27:
                return dayNight ? R.drawable.d27 : R.drawable.n27;
            case 28:
                return dayNight ? R.drawable.d28 : R.drawable.n28;
            case 29:
                return dayNight ? R.drawable.d29 : R.drawable.n29;
            case 30:
                return dayNight ? R.drawable.d30 : R.drawable.n30;
            case 31:
                return dayNight ? R.drawable.d31 : R.drawable.n31;
            case 53:
                return dayNight ? R.drawable.d53 : R.drawable.n53;
            default:
                return R.drawable.d99;
        }
    }

    /* access modifiers changed from: private */
    public void updateWeatherInfor() {
        String str;
        if (this.mWeatherSrv != null) {
            try {
                if (this.mWeatherSrv.getUpdateTimer().length() > 0) {
                    str = getString(R.string.lbl_update_infor) + this.mWeatherSrv.getUpdateTimer();
                } else {
                    str = getString(R.string.lbl_wait_network);
                }
                if (this.bWeatherChange) {
                    Log.i(TAG, "updateWeatherInfor: mWeatherSrv.getCurWeather() = " + this.mWeatherSrv.getCurWeather());
                    Log.i(TAG, "updateWeatherInfor: mWeatherSrv.getDayWeather() = " + this.mWeatherSrv.getDayWeather());
                    Log.i(TAG, "updateWeatherInfor: mWeatherSrv.getWeatherInfor() = " + this.mWeatherSrv.getWeatherInfor());
                    Log.i(TAG, "updateWeatherInfor: mWeatherSrv.getCityName() = " + this.mWeatherSrv.getCityName());
                    Log.i(TAG, "updateWeatherInfor: mWeatherSrv.getCurWeatherInfor() = " + this.mWeatherSrv.getCurWeatherInfor());
                    Log.i(TAG, "updateWeatherInfor: mWeatherSrv.getWeatherIconStr() = " + this.mWeatherSrv.getWeatherIconStr());
                }
                this.bWeatherChange = false;
                if (this.mtvUpdateInfor != null) {
                    this.mtvUpdateInfor.setText(str);
                }
                if (this.mtvWeatherTemp != null) {
                    this.mtvWeatherTemp.setText(this.mWeatherSrv.getCurWeather());
                }
                if (this.mtvWeatherDay != null) {
                    this.mtvWeatherDay.setText(this.mWeatherSrv.getDayWeather());
                }
                if (this.mtvWeatherTitle != null) {
                    this.mtvWeatherTitle.setText(this.mWeatherSrv.getWeatherInfor());
                }
                if (this.mtvCityName != null) {
                    this.mtvCityName.setText(this.mWeatherSrv.getCityName());
                }
                if (this.mtvWeatherInfor != null) {
                    this.mtvWeatherInfor.setText(this.mWeatherSrv.getCurWeatherInfor());
                }
                if (this.mvwWeatherIcon != null) {
                    this.mvwWeatherIcon.setBackgroundResource(getWeatherResouceID(this.mWeatherSrv.getWeatherIconStr(), true));
                }
                if (LauncherApplication.m_iUITypeVer == 101 || (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 11)) {
                    Intent intt = new Intent(EventUtils.CHEKU_WEATHER_IPC);
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_UpdateInfor, str);
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_WeatherTemp, this.mWeatherSrv.getCurWeather());
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_WeatherDay, this.mWeatherSrv.getDayWeather());
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_WeatherTitle, this.mWeatherSrv.getWeatherInfor());
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_CityName, this.mWeatherSrv.getCityName());
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_WeatherInfor, this.mWeatherSrv.getCurWeatherInfor());
                    intt.putExtra(EventUtils.CHEKU_WEATHER_IPC_WeatherIcon, this.mWeatherSrv.getWeatherIconStr());
                    sendBroadcast(intt);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void conectWeatherService() {
        Log.i(TAG, "conectWeatherService: mWeatherSrvSc");
        try {
            bindService(new Intent(this, WeatherService.class).setPackage(BuildConfig.APPLICATION_ID), this.mWeatherSrvSc, 1);
        } catch (Exception e) {
            Log.e(TAG, "onBindService error " + e.toString());
        }
    }

    private void disconectWeatherService() {
        Log.i(TAG, "disconectWeatherService: mWeatherSrvSc");
        try {
            if (this.mWeatherSrvSc != null) {
                unbindService(this.mWeatherSrvSc);
                this.mWeatherSrvSc = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "onBindService error " + e.toString());
        }
    }

    private void ShowHideScreen2(boolean showhide) {
        if (showhide) {
            if (this.ShowScreen2 != null) {
                this.ShowScreen2.setVisibility(0);
            }
        } else if (this.ShowScreen2 != null) {
            this.ShowScreen2.setVisibility(8);
        }
    }

    /* access modifiers changed from: protected */
    public void refreshNaviInfo() {
        ImageView Amapauto_Icon = (ImageView) findViewById(R.id.Amapauto_Icon);
        TextView TvSegRemainDisInfor = (TextView) findViewById(R.id.TvSegRemainDisInfor);
        TextView TvNextRouadName = (TextView) findViewById(R.id.TvNextRouadName);
        TextView TvRouteRemainDis = (TextView) findViewById(R.id.TvRouteRemainDis);
        TextView TvRouteRemainTime = (TextView) findViewById(R.id.TvRouteRemainTime);
        if (LauncherApplication.m_iUITypeVer != 41) {
            int[] iConLst = {R.drawable.amapauto_yuanche, R.drawable.amapauto_zuozhuan, R.drawable.amapauto_youzhuan, R.drawable.amapauto_zuoqian, R.drawable.amapauto_youqian, R.drawable.amapauto_zuohou, R.drawable.amapauto_youhou, R.drawable.amapauto_zuodiaotou, R.drawable.amapauto_zhixing, R.drawable.amapauto_tujingdi, R.drawable.amapauto_jinruhuanlu, R.drawable.amapauto_likaihuanlu, R.drawable.amapauto_fuwuqu, R.drawable.amapauto_shoufeizhan, R.drawable.amapauto_destination, R.drawable.amapauto_jinrusuidao};
            if (Amapauto_Icon != null && this.iMapIcon > 0 && this.iMapIcon <= iConLst.length) {
                Amapauto_Icon.setBackgroundResource(iConLst[this.iMapIcon - 1]);
            }
        } else if (this.m_iModeSet != 3) {
            if (this.m_iModeSet == 8) {
                int[] iConLst2 = {R.drawable.chwy_yuanche, R.drawable.chwy_zuozhuan, R.drawable.chwy_youzhuan, R.drawable.chwy_zuoqian, R.drawable.chwy_youqian, R.drawable.chwy_zuohou, R.drawable.chwy_youhou, R.drawable.chwy_zuodiaotou, R.drawable.chwy_zhixing, R.drawable.chwy_tujingdi, R.drawable.chwy_jinruhuanlu, R.drawable.chwy_youshichuhuandao, R.drawable.chwy_fuwuqu, R.drawable.chwy_shoufeizhan, R.drawable.chwy_mudidi, R.drawable.chwy_jinrusuidao, R.drawable.chwy_zuojinruhuanlu, R.drawable.chwy_zuoshichuhuandao, R.drawable.chwy_youdiaotou, R.drawable.chwy_shunxing};
                if (Amapauto_Icon != null && this.iMapIcon > 0 && this.iMapIcon <= iConLst2.length) {
                    Amapauto_Icon.setBackgroundResource(iConLst2[this.iMapIcon - 1]);
                }
            } else {
                int[] iConLst3 = {R.drawable.kesaiwei_1280x480_audi_xianshiche, R.drawable.kesaiwei_1280x480_audi_zuozhuan_n, R.drawable.kesaiwei_1280x480_audi_youzhuan_n, R.drawable.kesaiwei_1280x480_audi_zuoqian, R.drawable.kesaiwei_1280x480_audi_youqian, R.drawable.kesaiwei_1280x480_audi_zuohou, R.drawable.kesaiwei_1280x480_audi_youhou, R.drawable.kesaiwei_1280x480_audi_zuodiantou, R.drawable.kesaiwei_1280x480_audi_zhixing, R.drawable.kesaiwei_1280x480_audi_tujingdi, R.drawable.kesaiwei_1280x480_audi_likai_jinruhuanlu, R.drawable.kesaiwei_1280x480_audi_youshichuhuandao, R.drawable.kesaiwei_1280x480_audi_likai_daodafuwuzhan, R.drawable.kesaiwei_1280x480_audi_shoufeizhan, R.drawable.kesaiwei_1280x480_audi_mudidi, R.drawable.kesaiwei_1280x480_audi_likai_suidao, R.drawable.kesaiwei_1280x480_audi_zuojinruhuandao, R.drawable.kesaiwei_1280x480_audi_zuoshichuhuandao, R.drawable.kesaiwei_1280x480_audi_youdiaotou, R.drawable.kesaiwei_1280x480_audi_shunxing};
                if (Amapauto_Icon != null && this.iMapIcon > 0 && this.iMapIcon <= iConLst3.length) {
                    Amapauto_Icon.setBackgroundResource(iConLst3[this.iMapIcon - 1]);
                }
            }
        } else {
            return;
        }
        if (TvSegRemainDisInfor != null && this.iSegRemainDis >= 0) {
            DecimalFormat df = new DecimalFormat("0.0");
            if (this.iSegRemainDis >= 1000) {
                TvSegRemainDisInfor.setText(df.format(((double) this.iSegRemainDis) / 1000.0d) + "KM");
            } else {
                TvSegRemainDisInfor.setText(this.iSegRemainDis + "M");
            }
        }
        if (!(TvNextRouadName == null || this.strNextRouadName == null)) {
            TvNextRouadName.setText(this.strNextRouadName);
        }
        if (TvRouteRemainDis != null && this.iRouteRemainDis >= 0) {
            DecimalFormat df2 = new DecimalFormat("0.0");
            if (this.iRouteRemainDis >= 1000) {
                TvRouteRemainDis.setText(df2.format(((double) this.iRouteRemainDis) / 1000.0d) + "KM");
            } else {
                TvRouteRemainDis.setText(this.iRouteRemainDis + "M");
            }
        }
        if (TvRouteRemainTime != null) {
            int iTotMinTime = EventUtils.getMinFromPosition(this.iRouteRemainTime * 1000);
            int iTotHourTime = EventUtils.getHourFromPosition(this.iRouteRemainTime * 1000);
            TvRouteRemainTime.setText(String.format("%02d", new Object[]{Integer.valueOf(iTotHourTime)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(iTotMinTime)}));
        }
    }

    /* access modifiers changed from: private */
    public void onShowNaviInfo(boolean bShow) {
        RelativeLayout NaviAmapautoInfo = (RelativeLayout) findViewById(R.id.NaviAmapautoInfo);
        ImageView imageView_KSW_ad = (ImageView) findViewById(R.id.KSW_you_audi);
        LauncherApplication launcherApplication = (LauncherApplication) getApplication();
        this.reDaoHangDi = (RelativeLayout) findViewById(R.id.rl_daohangdi);
        if (bShow) {
            if (NaviAmapautoInfo != null) {
                NaviAmapautoInfo.setVisibility(0);
            }
            if (this.reDaoHangDi != null) {
                this.reDaoHangDi.setVisibility(0);
            }
            if (imageView_KSW_ad != null) {
                imageView_KSW_ad.setVisibility(8);
                return;
            }
            return;
        }
        if (imageView_KSW_ad != null) {
            imageView_KSW_ad.setVisibility(0);
        }
        if (this.reDaoHangDi != null) {
            this.reDaoHangDi.setVisibility(8);
        }
        if (NaviAmapautoInfo != null) {
            NaviAmapautoInfo.setVisibility(8);
        }
    }

    private void initLoadImageIcon() {
        this.iLogoResItemBk = new int[]{R.drawable.keshang_1280x480_itembk0, R.drawable.keshang_1280x480_itembk1, R.drawable.keshang_1280x480_itembk2, R.drawable.keshang_1280x480_itembk3, R.drawable.keshang_1280x480_itembk4};
        if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 5) {
            this.iLogoResItemBk = new int[]{R.drawable.kesaiwei_1024x600_benchi_itembk0, R.drawable.kesaiwei_1024x600_benchi_itembk1, R.drawable.kesaiwei_1024x600_benchi_itembk2, R.drawable.kesaiwei_1024x600_benchi_itembk3, R.drawable.kesaiwei_1024x600_benchi_itembk4};
            this.iLogoRes = new int[]{R.drawable.kesaiwei_1024x600_benchi_lanya_n, R.drawable.kesaiwei_1024x600_benchi_daohang_n, R.drawable.kesaiwei_1024x600_benchi_lanyayinyue_n, R.drawable.kesaiwei_1024x600_benchi_shezhi_n, R.drawable.kesaiwei_1024x600_benchi_yinyue_n, R.drawable.kesaiwei_1024x600_benchi_gaoqingshipin_n, R.drawable.kesaiwei_1024x600_benchi_yilian_n, R.drawable.kesaiwei_1024x600_benchi_yingyong_n, R.drawable.kesaiwei_1024x600_benchi_yuanche_n};
        } else if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 9) {
            this.iLogoResItemBk = new int[]{R.drawable.kesaiwei_800x480_benchi_kuang_1, R.drawable.kesaiwei_800x480_benchi_kuang_2, R.drawable.kesaiwei_800x480_benchi_kuang_3, R.drawable.kesaiwei_800x480_benchi_kuang_4, R.drawable.kesaiwei_800x480_benchi_kuang_5};
            this.iLogoRes = new int[]{R.drawable.kesaiwei_800x480_benchi_lanya_n, R.drawable.kesaiwei_800x480_benchi_daohang_n, R.drawable.kesaiwei_800x480_benchi_lanyayinyue_n, R.drawable.kesaiwei_800x480_benchi_shezhi_n, R.drawable.kesaiwei_800x480_benchi_yinyue_n, R.drawable.kesaiwei_800x480_benchi_gaoqingshipin_n, R.drawable.kesaiwei_800x480_benchi_yilian_n, R.drawable.kesaiwei_800x480_benchi_yingyong_n, R.drawable.kesaiwei_800x480_benchi_yuanche_n};
        } else if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 10) {
            this.iLogoResItemBk = new int[]{R.drawable.kesaiwei_1280x480_benchi_kuang_1, R.drawable.kesaiwei_1280x480_benchi_kuang_2, R.drawable.kesaiwei_1280x480_benchi_kuang_3, R.drawable.kesaiwei_1280x480_benchi_kuang_4, R.drawable.kesaiwei_1280x480_benchi_kuang_5};
            this.iLogoRes = new int[]{R.drawable.kesaiwei_1280x480_benchi_lanya_n, R.drawable.kesaiwei_1280x480_benchi_daohang_n, R.drawable.kesaiwei_1280x480_benchi_lanyayinyue_n, R.drawable.kesaiwei_1280x480_benchi_shezhi_n, R.drawable.kesaiwei_1280x480_benchi_yinyue_n, R.drawable.kesaiwei_1280x480_benchi_shipin_n, R.drawable.kesaiwei_1280x480_benchi_shoujihulian_n, R.drawable.kesaiwei_1280x480_benchi_yingyong_n, R.drawable.kesaiwei_1280x480_benchi_yuanche_n};
        } else if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 12) {
            this.iLogoResItemBk = new int[]{R.drawable.kesaiwei_1280x640_benchi_kuang_1, R.drawable.kesaiwei_1280x640_benchi_kuang_2, R.drawable.kesaiwei_1280x640_benchi_kuang_3, R.drawable.kesaiwei_1280x640_benchi_kuang_4, R.drawable.kesaiwei_1280x640_benchi_kuang_5};
            this.iLogoRes = new int[]{R.drawable.kesaiwei_1280x640_benchi_lanya_n, R.drawable.kesaiwei_1280x640_benchi_daohang_n, R.drawable.kesaiwei_1280x640_benchi_lanyayinyue_n, R.drawable.kesaiwei_1280x640_benchi_shezhi_n, R.drawable.kesaiwei_1280x640_benchi_yinyue_n, R.drawable.kesaiwei_1280x640_benchi_shipin_n, R.drawable.kesaiwei_1280x640_benchi_shoujihulian_n, R.drawable.kesaiwei_1280x640_benchi_yingyong_n, R.drawable.kesaiwei_1280x640_benchi_yuanche_n};
        }
        if (LauncherApplication.m_iUITypeVer == 41 && (this.m_iModeSet == 5 || this.m_iModeSet == 9 || this.m_iModeSet == 10 || this.m_iModeSet == 12)) {
            this.iLogoResname = new String[]{getString(R.string.lb_bt), getString(R.string.lb_navi), getString(R.string.lb_btmusic), getString(R.string.lb_settings), getString(R.string.lb_music), getString(R.string.lb_video), getString(R.string.lb_shoujihulian_q5_ksw), getString(R.string.all_apps_button_label), getString(R.string.lb_yuanche)};
            this.name = new String[]{getString(R.string.lb_bt), getString(R.string.lb_navi), getString(R.string.lb_btmusic), getString(R.string.lb_settings), getString(R.string.lb_music), getString(R.string.lb_video), getString(R.string.lb_shoujihulian_q5_ksw), getString(R.string.all_apps_button_label), getString(R.string.lb_yuanche)};
        } else {
            this.iLogoResname = new String[]{getString(R.string.lb_bt), getString(R.string.lb_aux), getString(R.string.lb_Instructions), getString(R.string.lb_Vehicle_Information), getString(R.string.lb_navi), getString(R.string.lb_btmusic), getString(R.string.lb_dvr), getString(R.string.lb_settings), getString(R.string.lb_music), getString(R.string.lb_radio), getString(R.string.lb_video), getString(R.string.lb_EasyConnected)};
            this.name = new String[]{getString(R.string.lb_bt), getString(R.string.lb_aux), getString(R.string.lb_Instructions), getString(R.string.lb_Vehicle_Information), getString(R.string.lb_navi), getString(R.string.lb_btmusic), getString(R.string.lb_dvr), getString(R.string.lb_settings), getString(R.string.lb_music), getString(R.string.lb_radio), getString(R.string.lb_video), getString(R.string.lb_EasyConnected)};
        }
        this.mCoverFlowView = (CoverFlowView) findViewById(R.id.coverflow);
        this.imgList = new ArrayList<>();
        Log.i(TAG, "initLoadImageIcon: iLogoRes.length = " + this.iLogoRes.length);
        for (int i = 0; i < this.iLogoRes.length; i++) {
            View convertView = null;
            if (0 == 0) {
                if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 5) {
                    convertView = getLayoutInflater().inflate(R.layout.kesaiwei_1024x600_benchi_load_image_item, (ViewGroup) null);
                } else if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 9) {
                    convertView = getLayoutInflater().inflate(R.layout.keshang_800x480_load_image_item, (ViewGroup) null);
                } else if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 10) {
                    convertView = getLayoutInflater().inflate(R.layout.kesaiwei_1280x480_benchi_load_image_item, (ViewGroup) null);
                } else if (LauncherApplication.m_iUITypeVer == 41 && this.m_iModeSet == 12) {
                    convertView = getLayoutInflater().inflate(R.layout.kesaiwei_1280x640_benchi_load_image_item, (ViewGroup) null);
                }
            }
            Log.i(TAG, "initLoadImageIcon: convertView = " + convertView);
            ImageView imageViewBK = (ImageView) convertView.findViewById(R.id.item_image_bk);
            ImageView imageview = (ImageView) convertView.findViewById(R.id.item_image);
            TextView text = (TextView) convertView.findViewById(R.id.item_text);
            this.logoname = (TextView) findViewById(R.id.APPname);
            if (imageview != null) {
                imageview.setBackgroundResource(this.iLogoRes[i]);
            }
            if (imageViewBK != null) {
                imageViewBK.setBackgroundResource(this.iLogoResItemBk[0]);
            }
            if (text != null) {
                text.setText(this.name[i]);
            }
            if (convertViewToBitmap(convertView) != null) {
                this.imgList.add(convertViewToBitmap(convertView));
            }
        }
        if (this.mCoverFlowView != null) {
            this.mCoverFlowView.setLastModeOffset(this.mSysProviderOpt.getRecordFloat(SysProviderOpt.SYS_LAST_MODE_OFFSET, 0.0f));
            this.mCoverFlowView.setAdapter(new MyCoverFlowAdapter());
            this.mCoverFlowView.setCoverFlowListener(new CoverFlowView.CoverFlowListener() {
                public void imageOnTop(CoverFlowView coverFlowView, int position, float left, float top, float right, float bottom) {
                    Log.i("setCoverFlowListener", "position ** = " + position);
                    if (Launcher.this.logoname != null) {
                        Launcher.this.logoname.setText(Launcher.this.iLogoResname[position]);
                    }
                }

                public void topImageClicked(CoverFlowView coverFlowView, int position) {
                    Log.i("setCoverFlowListener", "position 11111 = " + position + ", lastmodeoffset = " + Launcher.this.mCoverFlowView.getLastModeOffset());
                    Launcher.this.onSelectCurrImage(position);
                    Launcher.this.mSysProviderOpt.updateRecord(SysProviderOpt.SYS_LAST_MODE_OFFSET, Launcher.this.mCoverFlowView.getLastModeOffset() + "");
                    Launcher.this.mCoverFlowView.setSelection(position);
                }

                public void invalidationCompleted() {
                }
            });
        }
    }

    public class MyCoverFlowAdapter extends CoverFlowAdapter {
        public MyCoverFlowAdapter() {
        }

        public int getCount() {
            if (Launcher.this.iLogoRes.length == 0) {
                return 5;
            }
            return Launcher.this.iLogoRes.length;
        }

        public Bitmap getImage(int position) {
            return (Bitmap) Launcher.this.imgList.get(position);
        }
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap2 = view.getDrawingCache();
        Log.i(TAG, "convertViewToBitmap-initLoadImageIcon-bitmap ==> " + bitmap2);
        return bitmap2;
    }

    public void onSelectCurrImage(int count) {
        try {
            LauncherApplication launcherApplication = (LauncherApplication) getApplication();
            if (LauncherApplication.m_iUITypeVer == 41 && (this.m_iModeSet == 5 || this.m_iModeSet == 9 || this.m_iModeSet == 10 || this.m_iModeSet == 12)) {
                switch (count) {
                    case 0:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                        return;
                    case 1:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                        return;
                    case 2:
                        if (this.m_iModeSet == 10 || this.m_iModeSet == 12) {
                            Intent ii = new Intent();
                            ii.addCategory("android.intent.category.LAUNCHER");
                            Bundle mBundle = new Bundle();
                            mBundle.putInt("GotoPageNum", 3);
                            ii.putExtras(mBundle);
                            ii.setComponent(new ComponentName(EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME));
                            ii.setFlags(270532608);
                            startActivity(ii);
                            return;
                        }
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BTMUSIC_MODE_CLASS_NAME);
                        return;
                    case 3:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                        return;
                    case 4:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                        return;
                    case 5:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                        return;
                    case 6:
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.PHONEAPP_MODE_PACKAGE_NAME, EventUtils.PHONEAPP_MODE_CLASS_NAME);
                        return;
                    case 7:
                        showAllApps(true);
                        return;
                    case 8:
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    default:
                        return;
                }
                Log.e(TAG, e.toString());
            }
            switch (count) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 1:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.auxplayer", "com.szchoiceway.auxplayer.MainActivity");
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.INSTRUCT_MODE_PACKAGE_NAME, EventUtils.INSTRUCT_MODE_CLASS_NAME);
                    return;
                case 3:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.CANBUS_MODE_PACKAGE_NAME, EventUtils.CANBUS_MODE_CLASS_NAME);
                    return;
                case 4:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 5:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BTMUSIC_MODE_CLASS_NAME);
                    return;
                case 6:
                    if (this.m_iUSBDvr == 1) {
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.USB_DVR_MODE_PACKAGE_NAME, EventUtils.USB_DVR_MODE_CLASS_NAME);
                        return;
                    } else {
                        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.DVR_MODE_PACKAGE_NAME, EventUtils.DVR_MODE_CLASS_NAME);
                        return;
                    }
                case 7:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 8:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 9:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.RADIO_MODE_PACKAGE_NAME, EventUtils.RADIO_MODE_CLASS_NAME);
                    return;
                case 10:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 11:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.PHONEAPP_MODE_PACKAGE_NAME, EventUtils.PHONEAPP_MODE_CLASS_NAME);
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public void refreshFocusView() {
        for (int i = 0; i < this.imageViewFocusList.length; i++) {
            if (!(this.imageViewFocusList[i] == null || this.imageViewFocusList_zhishi[i] == null)) {
                if (i != this.m_iCurrFocus) {
                    if (this.textViewLine_KSW_AD != null) {
                        this.textViewLine_KSW_AD[i].setEllipsize(TextUtils.TruncateAt.END);
                    }
                    this.imageViewFocusList[i].setVisibility(8);
                    this.imageViewFocusList_zhishi[i].setVisibility(8);
                } else if (this.m_iModeSet == 1) {
                    this.textViewLine_KSW_AD[i].setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                    this.imageViewFocusList[i].setVisibility(0);
                } else if (this.m_iModeSet == 3) {
                    this.textViewLine_KSW_AD[i].setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                    this.imageViewFocusList[i].setVisibility(0);
                } else {
                    this.imageViewFocusList[i].setVisibility(0);
                    this.imageViewFocusList_zhishi[i].setVisibility(0);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                    this.imgViewFocusList_src_icon.setBackgroundResource(this.imgFocusList_src_icon[i]);
                    this.imgViewFocusList_src_anniu.setBackgroundResource(this.imgFocusList_src_anniu[i]);
                }
            }
        }
    }

    public void refreshFocusView_Q5() {
        for (int i = 0; i < this.imageViewFocusList.length; i++) {
            if (!(this.imageViewFocusList[i] == null || this.imgViewFocusList_zuo == null)) {
                if (i != this.m_iCurrFocus) {
                    if (this.textViewLine_KSW_AD != null) {
                        this.textViewLine_KSW_AD[i].setEllipsize(TextUtils.TruncateAt.END);
                    }
                    this.imageViewFocusList[i].setVisibility(8);
                } else if (this.m_iModeSet == 1) {
                    this.textViewLine_KSW_AD[i].setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                    this.imageViewFocusList[i].setVisibility(0);
                } else if (this.m_iModeSet == 3) {
                    this.textViewLine_KSW_AD[i].setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                    this.imageViewFocusList[i].setVisibility(0);
                } else {
                    this.imageViewFocusList[i].setVisibility(0);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                    this.imgViewFocusList_src_icon.setBackgroundResource(this.imgFocusList_src_icon[i]);
                    this.imgViewFocusList_src_anniu.setBackgroundResource(this.imgFocusList_src_anniu[i]);
                }
            }
        }
    }

    public void zxwOriginalMcuKeyEnter() {
        switch (this.m_iCurrFocus) {
            case 0:
                showAllApps(true);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 3:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 4:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case 5:
                enter_phoneLink();
                return;
            case 6:
                ksw_enter_dvr();
                return;
            case 7:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 8:
                ksw_enter_aux();
                return;
            case 9:
                ksw_enter_cmmb();
                return;
            case 10:
                ksw_enter_dvd();
                return;
            default:
                return;
        }
    }

    public void zxwOriginalMcuKeyEnter_audi() {
        switch (this.m_iCurrFocus) {
            case 0:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 1:
                ksw_enter_dvr();
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case 3:
                enter_phoneLink();
                return;
            case 4:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 5:
                this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            case 6:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 7:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 8:
                ksw_enter_aux();
                return;
            case 9:
                ksw_enter_cmmb();
                return;
            case 10:
                ksw_enter_dvd();
                return;
            default:
                return;
        }
    }

    public void zxwOriginalMcuKeyEnter_audi_Q5() {
        if (this.m_iModeSet == 8) {
            switch (this.m_iCurrFocus_chwy) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 1:
                    this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                    if (LauncherApplication.m_iUITypeVer != 41 || this.m_b_ksw_Original_car_video_display) {
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    } else {
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    }
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 3:
                    showAllApps(true);
                    return;
                case 4:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 5:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 6:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 7:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.EXPLORER_MODE_PACKAGE_NAME, EventUtils.EXPLORER_MODE_CLASS_NAME);
                    return;
                case 9:
                    enter_phoneLink();
                    return;
                case 10:
                    showAllApps(true);
                    return;
                case 12:
                    ksw_enter_dvr();
                    return;
                case 13:
                    ksw_enter_aux();
                    return;
                default:
                    return;
            }
        } else {
            switch (this.m_iCurrFocus) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 1:
                    ksw_enter_dvr();
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 3:
                    enter_phoneLink();
                    return;
                case 4:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 5:
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                case 6:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 7:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 8:
                    ksw_enter_aux();
                    return;
                case 9:
                    ksw_enter_cmmb();
                    return;
                case 10:
                    ksw_enter_dvd();
                    return;
                default:
                    return;
            }
        }
    }

    public void zxwOriginalMcuKeyEnter_normal_1920x720() {
        switch (this.m_iCurrFocus) {
            case 0:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 1:
                if (EventUtils.getInstallStatus(this, EventUtils.RADIO_MODE_PACKAGE_NAME)) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.RADIO_MODE_PACKAGE_NAME, EventUtils.RADIO_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.RADIO_DSP_MODE_PACKAGE_NAME, EventUtils.RADIO_DSP_MODE_CLASS_NAME);
                    return;
                }
            case 2:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 3:
                if (this.m_iUiIndex != 1) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                } else if (this.bHaveDsp) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.DSP_MODE_PACKAGE_NAME, EventUtils.DSP_MODE_CLASS_NAME);
                    return;
                } else {
                    Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                    return;
                }
            case 4:
                if (this.m_iUiIndex == 1) {
                    showAllApps(true);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                }
            case 5:
                if (LauncherApplication.m_iUITypeVer != 101) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                } else if (this.m_iUiIndex == 1) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                } else if (this.bHaveDsp) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.DSP_MODE_PACKAGE_NAME, EventUtils.DSP_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                }
            case 6:
                if (this.m_iUiIndex == 1) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                } else {
                    ksw_enter_browser();
                    return;
                }
            case 7:
                if (this.m_iUiIndex == 1) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                }
                return;
            case 8:
                enter_phoneLink();
                return;
            case 9:
                showAllApps(true);
                return;
            case 10:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 11:
                if (EventUtils.getInstallStatus(this, "com.ankai.cardvr")) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.ankai.cardvr", "com.ankai.cardvr.ui.MainActivity");
                    return;
                } else {
                    Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                    return;
                }
            default:
                return;
        }
    }

    private void setDefaultWallPaper() {
        WallpaperManager mwallpaerManager = WallpaperManager.getInstance(this);
        try {
            if (LauncherApplication.m_iUITypeVer == 41) {
                mwallpaerManager.setResource(R.drawable.wallpaper_14);
            }
        } catch (IOException e) {
            Log.i(TAG, "setDefaultWallPaper: e = " + e.toString());
            e.printStackTrace();
        }
    }

    public void refreshFocusView_Vertical() {
        if (this.m_iPageCurrFocus != 1 || this.m_iCurrFocus > 7) {
            if (this.m_iPageCurrFocus == 0 && this.m_iCurrFocus > 7 && this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
        } else if (this.mViewPager != null) {
            this.mViewPager.setCurrentItem(0);
        }
        if (this.m_iCurrFocus > 7) {
            for (int i = 0; i < this.imageViewFocusList_whats2.length; i++) {
                if (!(this.imageViewFocusList_whats2[i] == null || this.imageViewFocusList_zhishi[i] == null)) {
                    if (i == this.m_iCurrFocus - 8) {
                        this.imageViewFocusList_whats2[i].setVisibility(0);
                        this.imageViewFocusList_zhishi[i].setVisibility(0);
                        this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i]);
                        this.imgViewFocusList_src_icon.setBackgroundResource(this.imgFocusList_src_icon[this.m_iCurrFocus]);
                        this.imgViewFocusList_src_anniu.setBackgroundResource(this.imgFocusList_src_anniu[i]);
                    } else {
                        this.imageViewFocusList_whats2[i].setVisibility(8);
                        this.imageViewFocusList_zhishi[i].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats1[i] != null) {
                    this.imageViewFocusList_whats1[i].setVisibility(8);
                }
            }
            return;
        }
        for (int i2 = 0; i2 < this.imageViewFocusList_whats1.length; i2++) {
            if (!(this.imageViewFocusList_whats1[i2] == null || this.imageViewFocusList_zhishi[i2] == null)) {
                if (i2 == this.m_iCurrFocus) {
                    this.imageViewFocusList_whats1[i2].setVisibility(0);
                    this.imageViewFocusList_zhishi[i2].setVisibility(0);
                    this.imgViewFocusList_zuo.setBackgroundResource(this.imgFocusList_zuo[i2]);
                    this.imgViewFocusList_src_icon.setBackgroundResource(this.imgFocusList_src_icon[i2]);
                    this.imgViewFocusList_src_anniu.setBackgroundResource(this.imgFocusList_src_anniu[i2]);
                } else {
                    this.imageViewFocusList_whats1[i2].setVisibility(8);
                    this.imageViewFocusList_zhishi[i2].setVisibility(8);
                }
            }
            if (this.imageViewFocusList_whats2[i2] != null) {
                this.imageViewFocusList_whats2[i2].setVisibility(8);
            }
        }
    }

    public void zxwOriginalMcuKeyEnter_evo() {
        if (this.m_i_ksw_evo_main_interface_index == 1) {
            switch (this.m_iCurrFocus) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 1:
                    ksw_enter_dvr();
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 3:
                    ksw_enter_yibiaopan();
                    return;
                case 4:
                    enter_phoneLink();
                    return;
                case 5:
                    showAllApps(true);
                    return;
                case 6:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 7:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 8:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 9:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                case 10:
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                case 11:
                    ksw_enter_browser();
                    return;
                default:
                    return;
            }
        } else if (this.m_i_ksw_evo_main_interface_index == 2 || this.m_i_ksw_evo_main_interface_index == 3) {
            switch (this.m_iCurrFocus) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 1:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 3:
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                case 4:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 5:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 6:
                    enter_phoneLink();
                    return;
                case 7:
                    ksw_enter_dvr();
                    return;
                case 8:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                case 9:
                    ksw_enter_browser();
                    return;
                case 10:
                    showAllApps(true);
                    return;
                case 11:
                    ksw_enter_yibiaopan();
                    return;
                default:
                    return;
            }
        } else if (this.m_i_ksw_evo_main_interface_index == 4) {
            switch (this.m_iCurrFocus) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 1:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 3:
                    enter_phoneLink();
                    return;
                case 4:
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                case 5:
                    ksw_enter_browser();
                    return;
                case 6:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 7:
                    ksw_enter_dvr();
                    return;
                case 8:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 9:
                    ksw_enter_yibiaopan();
                    return;
                case 10:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                case 11:
                    showAllApps(true);
                    return;
                default:
                    return;
            }
        } else if (this.m_i_ksw_evo_main_interface_index == 5) {
            switch (this.m_iCurrFocus) {
                case 0:
                    this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                    if (!this.m_b_ksw_Original_car_video_display) {
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    } else {
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    }
                case 1:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 3:
                    Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
                    return;
                case 4:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 5:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 6:
                    enter_phoneLink();
                    return;
                case 7:
                    ksw_enter_dvr();
                    return;
                case 8:
                    ksw_enter_yibiaopan();
                    return;
                case 9:
                    showAllApps(true);
                    return;
                case 10:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 11:
                    ksw_enter_browser();
                    return;
                default:
                    return;
            }
        } else if (this.m_iModeSet == 6) {
            switch (this.m_iCurrFocus) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 1:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 3:
                    ksw_enter_cmmb();
                    return;
                case 4:
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                case 5:
                    ksw_enter_browser();
                    return;
                case 6:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 7:
                    ksw_enter_dvr();
                    return;
                case 8:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 9:
                    ksw_enter_yibiaopan();
                    return;
                case 10:
                    enter_phoneLink();
                    return;
                case 11:
                    showAllApps(true);
                    return;
                default:
                    return;
            }
        } else {
            switch (this.m_iCurrFocus) {
                case 0:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                case 1:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                case 2:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                case 3:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                case 4:
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                case 5:
                    ksw_enter_browser();
                    return;
                case 6:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    return;
                case 7:
                    if (this.m_b_have_TV) {
                        ksw_enter_cmmb();
                        return;
                    } else {
                        ksw_enter_dvr();
                        return;
                    }
                case 8:
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                    return;
                case 9:
                    ksw_enter_yibiaopan();
                    return;
                case 10:
                    enter_phoneLink();
                    return;
                case 11:
                    showAllApps(true);
                    return;
                default:
                    return;
            }
        }
    }

    public void zxwOriginalMcuKeyEnter_evo_id6() {
        switch (this.m_iCurrFocus) {
            case 0:
                if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                }
            case 1:
                if (this.m_i_ksw_evo_id6_main_interface_index == 1) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                } else if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                    if (!this.m_b_ksw_Original_car_video_display) {
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    } else {
                        sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                        return;
                    }
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                    return;
                }
            case 2:
                if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                }
            case 3:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 4:
                if (this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                    return;
                }
                this.m_b_ksw_Original_car_video_display = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_ORIGINAL_CAR_VIDEO_DISPLAY, this.m_b_ksw_Original_car_video_display);
                if (!this.m_b_ksw_Original_car_video_display) {
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                } else {
                    sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                    return;
                }
            case 5:
                if (LauncherApplication.m_iUITypeVer != 41) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.EXPLORER_MODE_PACKAGE_NAME, EventUtils.EXPLORER_MODE_CLASS_NAME);
                    return;
                } else if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                } else {
                    ksw_enter_browser();
                    return;
                }
            case 6:
                if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
                    ksw_enter_browser();
                    return;
                } else {
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.estrongs.android.pop", "com.estrongs.android.pop.view.FileExplorerActivity");
                    return;
                }
            case 7:
                if (this.m_b_have_TV) {
                    ksw_enter_cmmb();
                    return;
                } else {
                    ksw_enter_dvr();
                    return;
                }
            case 8:
                ksw_enter_yibiaopan();
                return;
            case 9:
                enter_phoneLink();
                return;
            case 10:
                showAllApps(true);
                return;
            case 11:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            default:
                return;
        }
    }

    public void zxwOriginalMcuKeyEnter_evo_id7() {
        switch (this.m_iCurrFocus) {
            case 0:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.BT_MODE_PACKAGE_NAME, EventUtils.BT_MODE_CLASS_NAME);
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 3:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            case 4:
                sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            case 5:
                ksw_enter_yibiaopan();
                return;
            default:
                return;
        }
    }

    private void initView_KeSaiWei_VerticalPager() {
        this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7};
        this.imgFocusList_zhishi = new int[]{R.id.KSW_Focus_zhishixian_0, R.id.KSW_Focus_zhishixian_1, R.id.KSW_Focus_zhishixian_2, R.id.KSW_Focus_zhishixian_3, R.id.KSW_Focus_zhishixian_4, R.id.KSW_Focus_zhishixian_5, R.id.KSW_Focus_zhishixian_6, R.id.KSW_Focus_zhishixian_7};
        this.imgFocusList_zuo = new int[]{R.drawable.kesaiwei_1280x480_zuo_app, R.drawable.kesaiwei_1280x480_zuo_nav, R.drawable.kesaiwei_1280x480_zuo_music, R.drawable.kesaiwei_1280x480_zuo_video, R.drawable.kesaiwei_1280x480_zuo_bt, R.drawable.kesaiwei_1280x480_zuo_shoujihulian, R.drawable.kesaiwei_1280x480_zuo_dvr, R.drawable.kesaiwei_1280x480_zuo_set};
        this.imgFocusList_src_icon = new int[]{R.drawable.kesaiwei_1280x480_src_icon_app, R.drawable.kesaiwei_1280x480_src_icon_nav, R.drawable.kesaiwei_1280x480_src_icon_music, R.drawable.kesaiwei_1280x480_src_icon_video, R.drawable.kesaiwei_1280x480_src_icon_bt, R.drawable.kesaiwei_1280x480_src_icon_shoujihulian, R.drawable.kesaiwei_1280x480_src_icon_dvr, R.drawable.kesaiwei_1280x480_src_icon_set, R.drawable.kesaiwei_1280x480_src_icon_aux, R.drawable.kesaiwei_1280x480_src_icon_tv, R.drawable.kesaiwei_1280x480_src_icon_dvd};
        this.imgFocusList_src_anniu = new int[]{R.drawable.kesaiwei_1280x480_anniu_n_00, R.drawable.kesaiwei_1280x480_anniu_n_01, R.drawable.kesaiwei_1280x480_anniu_n_02, R.drawable.kesaiwei_1280x480_anniu_n_03, R.drawable.kesaiwei_1280x480_anniu_n_04, R.drawable.kesaiwei_1280x480_anniu_n_05, R.drawable.kesaiwei_1280x480_anniu_n_06, R.drawable.kesaiwei_1280x480_anniu_n_07};
        this.imgViewFocusList_zuo = (ImageView) findViewById(R.id.KSW_zou);
        this.imgViewFocusList_src_icon = (ImageView) findViewById(R.id.KSW_src_icon);
        this.imgViewFocusList_src_anniu = (ImageView) findViewById(R.id.KSW_src_anniu);
        this.imageViewFocusList = new ImageView[this.imgFocusList.length];
        this.imageViewFocusList_zhishi = new ImageView[this.imgFocusList_zhishi.length];
        for (int i = 0; i < this.imgFocusList.length; i++) {
            this.imageViewFocusList[i] = (ImageView) findViewById(this.imgFocusList[i]);
            this.imageViewFocusList_zhishi[i] = (ImageView) findViewById(this.imgFocusList_zhishi[i]);
        }
        initKaiDiLaKePage();
        refreshFocusView_Vertical();
        this.iv_bt_connect_show_KSW = (ImageView) findViewById(R.id.iv_bt_connect_show_KSW);
    }

    private void initView_KeSaiWei_aodi() {
        this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7};
        this.imgFocusList_zhishi = new int[]{R.id.KSW_Focus_zhishixian_0, R.id.KSW_Focus_zhishixian_1, R.id.KSW_Focus_zhishixian_2, R.id.KSW_Focus_zhishixian_3, R.id.KSW_Focus_zhishixian_4, R.id.KSW_Focus_zhishixian_5, R.id.KSW_Focus_zhishixian_6, R.id.KSW_Focus_zhishixian_7};
        this.textView_KSW_AD = new int[]{R.id.tx00, R.id.tx01, R.id.tx02, R.id.tx03, R.id.tx04, R.id.tx05, R.id.tx06, R.id.tx07};
        this.imgFocusList_zuo = new int[]{R.drawable.kesaiwei_1280x480_audi_guang_set, R.drawable.kesaiwei_1280x480_audi_guang_dvr, R.drawable.kesaiwei_1280x480_audi_guang_bt, R.drawable.kesaiwei_1280x480_audi_guang_shoujihulian, R.drawable.kesaiwei_1280x480_audi_guang_music, R.drawable.kesaiwei_1280x480_audi_guang_aux, R.drawable.kesaiwei_1280x480_audi_guang_dvr, R.drawable.kesaiwei_1280x480_audi_guang_nav};
        this.imgFocusList_src_icon = new int[]{R.drawable.kesaiwei_1280x480_src_icon_app, R.drawable.kesaiwei_1280x480_src_icon_nav, R.drawable.kesaiwei_1280x480_src_icon_music, R.drawable.kesaiwei_1280x480_src_icon_video, R.drawable.kesaiwei_1280x480_src_icon_bt, R.drawable.kesaiwei_1280x480_src_icon_shoujihulian, R.drawable.kesaiwei_1280x480_src_icon_dvr, R.drawable.kesaiwei_1280x480_src_icon_set};
        this.imgFocusList_src_anniu = new int[]{R.drawable.kesaiwei_1280x480_anniu_n_00, R.drawable.kesaiwei_1280x480_anniu_n_01, R.drawable.kesaiwei_1280x480_anniu_n_02, R.drawable.kesaiwei_1280x480_anniu_n_03, R.drawable.kesaiwei_1280x480_anniu_n_04, R.drawable.kesaiwei_1280x480_anniu_n_05, R.drawable.kesaiwei_1280x480_anniu_n_06, R.drawable.kesaiwei_1280x480_anniu_n_07};
        this.imgViewFocusList_zuo = (ImageView) findViewById(R.id.KSW_zou);
        this.imgViewFocusList_src_icon = (ImageView) findViewById(R.id.KSW_src_icon);
        this.imgViewFocusList_src_anniu = (ImageView) findViewById(R.id.KSW_src_anniu);
        this.imageViewFocusList = new ImageView[this.imgFocusList.length];
        this.imageViewFocusList_zhishi = new ImageView[this.imgFocusList_zhishi.length];
        if (this.textView_KSW_AD != null) {
            this.textViewLine_KSW_AD = new TextView[this.textView_KSW_AD.length];
        }
        for (int i = 0; i < this.imgFocusList.length; i++) {
            if (this.textView_KSW_AD != null) {
                this.textViewLine_KSW_AD[i] = (TextView) findViewById(this.textView_KSW_AD[i]);
            }
            this.imageViewFocusList[i] = (ImageView) findViewById(this.imgFocusList[i]);
            this.imageViewFocusList_zhishi[i] = (ImageView) findViewById(this.imgFocusList_zhishi[i]);
        }
        refreshFocusView();
        this.iv_bt_connect_show_KSW = (ImageView) findViewById(R.id.iv_bt_connect_show_KSW);
    }

    private void initView_KeSaiWei_aodi_Q5() {
        this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7};
        this.textView_KSW_AD = new int[]{R.id.tx00, R.id.tx01, R.id.tx02, R.id.tx03, R.id.tx04, R.id.tx05, R.id.tx06, R.id.tx07};
        this.imgFocusList_zuo = new int[]{R.drawable.kesaiwei_1024x600_audi_guang_set, R.drawable.kesaiwei_1024x600_audi_guang_dvr, R.drawable.kesaiwei_1024x600_audi_guang_bt, R.drawable.kesaiwei_1024x600_audi_guang_shoujihulian, R.drawable.kesaiwei_1024x600_audi_guang_music, R.drawable.kesaiwei_1024x600_audi_guang_aux, R.drawable.kesaiwei_1024x600_audi_guang_dvr, R.drawable.kesaiwei_1024x600_audi_guang_nav};
        this.imgFocusList_src_icon = new int[]{R.drawable.kesaiwei_1280x480_src_icon_app, R.drawable.kesaiwei_1280x480_src_icon_nav, R.drawable.kesaiwei_1280x480_src_icon_music, R.drawable.kesaiwei_1280x480_src_icon_video, R.drawable.kesaiwei_1280x480_src_icon_bt, R.drawable.kesaiwei_1280x480_src_icon_shoujihulian, R.drawable.kesaiwei_1280x480_src_icon_dvr, R.drawable.kesaiwei_1280x480_src_icon_set};
        this.imgFocusList_src_anniu = new int[]{R.drawable.kesaiwei_1280x480_anniu_n_00, R.drawable.kesaiwei_1280x480_anniu_n_01, R.drawable.kesaiwei_1280x480_anniu_n_02, R.drawable.kesaiwei_1280x480_anniu_n_03, R.drawable.kesaiwei_1280x480_anniu_n_04, R.drawable.kesaiwei_1280x480_anniu_n_05, R.drawable.kesaiwei_1280x480_anniu_n_06, R.drawable.kesaiwei_1280x480_anniu_n_07};
        this.imgViewFocusList_zuo = (ImageView) findViewById(R.id.KSW_zou);
        Log.i(TAG, "--->>> imgViewFocusList_zuo");
        this.imgViewFocusList_src_icon = (ImageView) findViewById(R.id.KSW_src_icon);
        this.imgViewFocusList_src_anniu = (ImageView) findViewById(R.id.KSW_src_anniu);
        this.imageViewFocusList = new ImageView[this.imgFocusList.length];
        if (this.textView_KSW_AD != null) {
            this.textViewLine_KSW_AD = new TextView[this.textView_KSW_AD.length];
        }
        for (int i = 0; i < this.imgFocusList.length; i++) {
            if (this.textView_KSW_AD != null) {
                this.textViewLine_KSW_AD[i] = (TextView) findViewById(this.textView_KSW_AD[i]);
            }
            this.imageViewFocusList[i] = (ImageView) findViewById(this.imgFocusList[i]);
        }
        refreshFocusView_Q5();
        this.iv_bt_connect_show_KSW = (ImageView) findViewById(R.id.iv_bt_connect_show_KSW);
    }

    private void initView_KeSaiWei_evo() {
        initKaiDiLaKePage();
        refreshFocusView_evo();
        this.iv_bt_connect_show_KSW = (ImageView) findViewById(R.id.iv_bt_connect_show_KSW);
    }

    private void initView_KeSaiWei_benchi() {
        this.iv_bt_connect_show_KSW = (ImageView) findViewById(R.id.iv_bt_connect_show_KSW);
    }

    /* access modifiers changed from: private */
    public void initView_KeSaiWei_evo_id6() {
        Log.i(TAG, "initView_KeSaiWei_evo_id6: jelly-bIsShowSmallViewEVO_ID6 = " + this.bIsShowSmallViewEVO_ID6);
        initKaiDiLaKePage();
        this.btnPagerPriv = (ImageButton) findViewById(R.id.btnPagerPriv);
        this.btnPagerNext = (ImageButton) findViewById(R.id.btnPagerNext);
        if (this.bIsShowSmallViewEVO_ID6) {
            if (this.btnPagerPriv != null) {
                this.btnPagerPriv.setVisibility(8);
            }
            if (this.btnPagerNext != null) {
                this.btnPagerNext.setVisibility(8);
            }
        } else {
            if (this.btnPagerPriv != null) {
                this.btnPagerPriv.setVisibility(0);
            }
            if (this.btnPagerNext != null) {
                this.btnPagerNext.setVisibility(0);
            }
        }
        if (this.btnPagerPriv != null) {
            this.btnPagerPriv.setOnClickListener(this);
        }
        if (this.btnPagerNext != null) {
            this.btnPagerNext.setOnClickListener(this);
        }
        setupPrivNextVisibility_id6();
    }

    private void initViewPager_CHWY() {
        this.rl_apps = (RelativeLayout) findViewById(R.id.rl_apps);
        this.tvYouLiang = (TextView) findViewById(R.id.tv_youliang);
        this.tvXuHangLiCheng = (TextView) findViewById(R.id.tv_xushilicheng);
        this.cb_anquandai = (CheckBox) findViewById(R.id.cb_anquandai);
        this.cb_shousha = (CheckBox) findViewById(R.id.cb_shousha);
        this.mViewPage = (ViewPager) findViewById(R.id.MyViewPager);
        this.mMeneItem = new View[4];
        this.mMeneItem[0] = View.inflate(this, R.layout.workspace_screen_chwy_1280x480_page_memu1_yuanche, (ViewGroup) null);
        this.mMeneItem[1] = View.inflate(this, R.layout.workspace_screen_chwy_1280x480_page_memu0, (ViewGroup) null);
        this.mMeneItem[2] = View.inflate(this, R.layout.workspace_screen_chwy_1280x480_page_memu1_yuanche, (ViewGroup) null);
        this.mMeneItem[3] = View.inflate(this, R.layout.workspace_screen_chwy_1280x480_page_memu0, (ViewGroup) null);
        this.rlApps1 = (RelativeLayout) this.mMeneItem[0].findViewById(R.id.rl_apps_1);
        this.rlApps2 = (RelativeLayout) this.mMeneItem[1].findViewById(R.id.rl_apps_0);
        this.rlApps3 = (RelativeLayout) this.mMeneItem[2].findViewById(R.id.rl_apps_1);
        this.rlApps4 = (RelativeLayout) this.mMeneItem[3].findViewById(R.id.rl_apps_0);
        this.rl_apps.setVisibility(8);
        this.rlApps1.setVisibility(0);
        this.rlApps2.setVisibility(0);
        this.rlApps3.setVisibility(0);
        this.rlApps4.setVisibility(0);
        for (View initViewListener : this.mMeneItem) {
            initViewListener(initViewListener);
        }
        if (this.mViewPage != null) {
            this.mViewAdapter = new MyViewPageAdapter();
            this.mViewPage.setAdapter(this.mViewAdapter);
            for (int loop = 0; loop < this.mMeneItem.length; loop++) {
                ViewParent vp = this.mMeneItem[loop].getParent();
                if (vp != null) {
                    ((ViewGroup) vp).removeView(this.mMeneItem[loop]);
                }
            }
            this.mViewPage.setCurrentItem(this.mMeneItem.length * 100000);
            this.mViewAdapter.notifyDataSetChanged();
            this.mViewPage.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    Log.i(Launcher.TAG, "mViewPage-onPageScrolled-position = " + position);
                    Launcher.this.rl_apps.setVisibility(0);
                    Launcher.this.rlApps1.setVisibility(8);
                    Launcher.this.rlApps2.setVisibility(8);
                    Launcher.this.rlApps3.setVisibility(8);
                    Launcher.this.rlApps4.setVisibility(8);
                }

                public void onPageSelected(int position) {
                    Log.i(Launcher.TAG, "mViewPage-onPageSelected-position = " + position);
                }

                public void onPageScrollStateChanged(int state) {
                    Log.i(Launcher.TAG, "mViewPage-onPageScrollStateChanged-state= " + state);
                    if (state == 1) {
                        Launcher.this.rl_apps.setVisibility(0);
                        Launcher.this.rlApps1.setVisibility(8);
                        Launcher.this.rlApps2.setVisibility(8);
                        Launcher.this.rlApps3.setVisibility(8);
                        Launcher.this.rlApps4.setVisibility(8);
                        boolean unused = Launcher.this.bIsMcuKeyRemove = false;
                    }
                    if (state == 0) {
                        Launcher.this.rl_apps.setVisibility(8);
                        Launcher.this.rlApps1.setVisibility(0);
                        Launcher.this.rlApps2.setVisibility(0);
                        Launcher.this.rlApps3.setVisibility(0);
                        Launcher.this.rlApps4.setVisibility(0);
                        Log.i(Launcher.TAG, "onPageScrollStateChanged: rl_apps = " + Launcher.this.rl_apps);
                        Log.i(Launcher.TAG, "onPageScrollStateChanged: rlApps1 = " + Launcher.this.rlApps1);
                        Log.i(Launcher.TAG, "onPageScrollStateChanged: rlApps2 = " + Launcher.this.rlApps2);
                        Log.i(Launcher.TAG, "onPageScrollStateChanged: rlApps3 = " + Launcher.this.rlApps3);
                        Log.i(Launcher.TAG, "onPageScrollStateChanged: rlApps4 = " + Launcher.this.rlApps4);
                        if (!Launcher.this.bIsMcuKeyRemove) {
                            int currentItem = Launcher.this.mViewPage.getCurrentItem();
                            Log.i(Launcher.TAG, "mViewPage-onPageScrollStateChanged_currentItem = " + currentItem);
                            if (currentItem % 2 == 0) {
                                int unused2 = Launcher.this.m_iCurrFocus_chwy = 0;
                            } else {
                                int unused3 = Launcher.this.m_iCurrFocus_chwy = 7;
                            }
                            Launcher.this.refreshFocusView_CHWY();
                        }
                        boolean unused4 = Launcher.this.bIsMcuKeyRemove = false;
                    }
                }
            });
        }
        this.imgFocusList_chwy = new int[]{R.id.iv_1, R.id.iv_2, R.id.iv_3, R.id.iv_apps, R.id.iv_4, R.id.iv_5, R.id.iv_6, R.id.iv_7, R.id.iv_8, R.id.iv_9, R.id.iv_apps, R.id.iv_10, R.id.iv_11, R.id.iv_12};
        this.imageViewFocusList1_chwy = new ImageView[this.imgFocusList_chwy.length];
        this.imageViewFocusList2_chwy = new ImageView[this.imgFocusList_chwy.length];
        for (int i = 0; i < this.imgFocusList_chwy.length / 2; i++) {
            this.imageViewFocusList1_chwy[i] = (ImageView) this.mMeneItem[0].findViewById(this.imgFocusList_chwy[i]);
        }
        for (int i2 = this.imgFocusList_chwy.length / 2; i2 < this.imgFocusList_chwy.length; i2++) {
            this.imageViewFocusList1_chwy[i2] = (ImageView) this.mMeneItem[1].findViewById(this.imgFocusList_chwy[i2]);
        }
        for (int i3 = 0; i3 < this.imgFocusList_chwy.length / 2; i3++) {
            this.imageViewFocusList2_chwy[i3] = (ImageView) this.mMeneItem[2].findViewById(this.imgFocusList_chwy[i3]);
        }
        for (int i4 = this.imgFocusList_chwy.length / 2; i4 < this.imgFocusList_chwy.length; i4++) {
            this.imageViewFocusList2_chwy[i4] = (ImageView) this.mMeneItem[3].findViewById(this.imgFocusList_chwy[i4]);
        }
        refreshFocusView_CHWY();
    }

    private void initViewPager_JLR() {
        Log.i(TAG, "initViewPager_JLR: ");
        initKaiDiLaKePage();
        this.btnPagerPriv = (ImageButton) findViewById(R.id.btnPagerPriv);
        this.btnPagerNext = (ImageButton) findViewById(R.id.btnPagerNext);
        if (this.btnPagerPriv != null) {
            this.btnPagerPriv.setOnClickListener(this);
        }
        if (this.btnPagerNext != null) {
            this.btnPagerNext.setOnClickListener(this);
        }
    }

    private void initViewPager_EVO_ID7() {
        initKaiDiLaKePage();
        this.imgLeftFocusList = new int[]{R.id.KSW_Left_Focus_select_0, R.id.KSW_Left_Focus_select_1, R.id.KSW_Left_Focus_select_2, R.id.KSW_Left_Focus_select_3, R.id.KSW_Left_Focus_select_4};
        this.imageViewLeftFocusList = new ImageView[this.imgLeftFocusList.length];
        for (int i = 0; i < this.imgLeftFocusList.length; i++) {
            this.imageViewLeftFocusList[i] = (ImageView) findViewById(this.imgLeftFocusList[i]);
        }
    }

    private void initView_KeSaiWei() {
        this.imgFocusList = new int[]{R.id.KSW_Focus_select_0, R.id.KSW_Focus_select_1, R.id.KSW_Focus_select_2, R.id.KSW_Focus_select_3, R.id.KSW_Focus_select_4, R.id.KSW_Focus_select_5, R.id.KSW_Focus_select_6, R.id.KSW_Focus_select_7};
        this.imgFocusList_zhishi = new int[]{R.id.KSW_Focus_zhishixian_0, R.id.KSW_Focus_zhishixian_1, R.id.KSW_Focus_zhishixian_2, R.id.KSW_Focus_zhishixian_3, R.id.KSW_Focus_zhishixian_4, R.id.KSW_Focus_zhishixian_5, R.id.KSW_Focus_zhishixian_6, R.id.KSW_Focus_zhishixian_7};
        this.imgFocusList_zuo = new int[]{R.drawable.kesaiwei_1280x480_zuo_app, R.drawable.kesaiwei_1280x480_zuo_nav, R.drawable.kesaiwei_1280x480_zuo_music, R.drawable.kesaiwei_1280x480_zuo_video, R.drawable.kesaiwei_1280x480_zuo_bt, R.drawable.kesaiwei_1280x480_zuo_shoujihulian, R.drawable.kesaiwei_1280x480_zuo_dvr, R.drawable.kesaiwei_1280x480_zuo_set};
        this.imgFocusList_src_icon = new int[]{R.drawable.kesaiwei_1280x480_src_icon_app, R.drawable.kesaiwei_1280x480_src_icon_nav, R.drawable.kesaiwei_1280x480_src_icon_music, R.drawable.kesaiwei_1280x480_src_icon_video, R.drawable.kesaiwei_1280x480_src_icon_bt, R.drawable.kesaiwei_1280x480_src_icon_shoujihulian, R.drawable.kesaiwei_1280x480_src_icon_dvr, R.drawable.kesaiwei_1280x480_src_icon_set};
        this.imgFocusList_src_anniu = new int[]{R.drawable.kesaiwei_1280x480_anniu_n_00, R.drawable.kesaiwei_1280x480_anniu_n_01, R.drawable.kesaiwei_1280x480_anniu_n_02, R.drawable.kesaiwei_1280x480_anniu_n_03, R.drawable.kesaiwei_1280x480_anniu_n_04, R.drawable.kesaiwei_1280x480_anniu_n_05, R.drawable.kesaiwei_1280x480_anniu_n_06, R.drawable.kesaiwei_1280x480_anniu_n_07};
        this.imgViewFocusList_zuo = (ImageView) findViewById(R.id.KSW_zou);
        this.imgViewFocusList_src_icon = (ImageView) findViewById(R.id.KSW_src_icon);
        this.imgViewFocusList_src_anniu = (ImageView) findViewById(R.id.KSW_src_anniu);
        this.imageViewFocusList = new ImageView[this.imgFocusList.length];
        this.imageViewFocusList_zhishi = new ImageView[this.imgFocusList_zhishi.length];
        for (int i = 0; i < this.imgFocusList.length; i++) {
            this.imageViewFocusList[i] = (ImageView) findViewById(this.imgFocusList[i]);
            this.imageViewFocusList_zhishi[i] = (ImageView) findViewById(this.imgFocusList_zhishi[i]);
        }
        refreshFocusView();
        this.iv_bt_connect_show_KSW = (ImageView) findViewById(R.id.iv_bt_connect_show_KSW);
    }

    public void refreshFocusView_evo() {
        if (this.m_iPageCurrFocus != 1 || this.m_iCurrFocus > 5) {
            if (this.m_iPageCurrFocus == 0 && this.m_iCurrFocus > 5 && this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
        } else if (this.mViewPager != null) {
            this.mViewPager.setCurrentItem(0);
        }
        int len = this.imgFocusList.length;
        if (this.m_i_ksw_evo_main_interface_index == 1) {
            if (this.m_iCurrFocus >= len) {
                for (int i = 0; i < this.imageViewFocusList_whats1.length; i++) {
                    if (this.ksw_m_b_Focus_image_zoom_evo) {
                        if (this.imageViewFocusList_whats1_normal_Text_zoom[i] != null) {
                            if (i == this.m_iCurrFocus - len) {
                                this.imageViewFocusList_whats1_normal_Text_zoom[i].setVisibility(8);
                            } else {
                                this.imageViewFocusList_whats1_normal_Text_zoom[i].setVisibility(0);
                            }
                        }
                        if (this.imageViewFocusList_whats2_normal_Text_zoom[i] != null) {
                            this.imageViewFocusList_whats2_normal_Text_zoom[i].setVisibility(0);
                        }
                        if (this.imageViewFocusList_whats1_select_Text_zoom[i] != null) {
                            if (i == this.m_iCurrFocus - len) {
                                this.imageViewFocusList_whats1_select_Text_zoom[i].setVisibility(0);
                            } else {
                                this.imageViewFocusList_whats1_select_Text_zoom[i].setVisibility(8);
                            }
                        }
                        if (this.imageViewFocusList_whats2_select_Text_zoom[i] != null) {
                            this.imageViewFocusList_whats2_select_Text_zoom[i].setVisibility(8);
                        }
                        if (this.imageViewFocusList_whats1_normal_modeIcon_zoom[i] != null) {
                            if (i == this.m_iCurrFocus - len) {
                                this.imageViewFocusList_whats1_normal_modeIcon_zoom[i].setVisibility(8);
                            } else {
                                this.imageViewFocusList_whats1_normal_modeIcon_zoom[i].setVisibility(0);
                            }
                        }
                        if (this.imageViewFocusList_whats2_normal_modeIcon_zoom[i] != null) {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats1[i] != null) {
                        if (i == this.m_iCurrFocus - len) {
                            this.imageViewFocusList_whats1[i].setVisibility(0);
                        } else {
                            this.imageViewFocusList_whats1[i].setVisibility(8);
                        }
                    }
                    if (this.imageViewFocusList_whats2[i] != null) {
                        this.imageViewFocusList_whats2[i].setVisibility(8);
                    }
                    if (this.imageViewFocusList_whats1_modeIcon[i] != null) {
                        if (i == this.m_iCurrFocus - len) {
                            this.imageViewFocusList_whats1_modeIcon[i].setVisibility(0);
                        } else {
                            this.imageViewFocusList_whats1_modeIcon[i].setVisibility(8);
                        }
                    }
                    if (this.imageViewFocusList_whats2_modeIcon[i] != null) {
                        this.imageViewFocusList_whats2_modeIcon[i].setVisibility(8);
                    }
                }
                return;
            }
            for (int i2 = 0; i2 < this.imageViewFocusList_whats2.length; i2++) {
                if (this.ksw_m_b_Focus_image_zoom_evo) {
                    if (this.imageViewFocusList_whats2_normal_Text_zoom[i2] != null) {
                        if (i2 == this.m_iCurrFocus) {
                            this.imageViewFocusList_whats2_normal_Text_zoom[i2].setVisibility(8);
                        } else {
                            this.imageViewFocusList_whats2_normal_Text_zoom[i2].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats1_normal_Text_zoom[i2] != null) {
                        this.imageViewFocusList_whats1_normal_Text_zoom[i2].setVisibility(0);
                    }
                    if (this.imageViewFocusList_whats2_select_Text_zoom[i2] != null) {
                        if (i2 == this.m_iCurrFocus) {
                            this.imageViewFocusList_whats2_select_Text_zoom[i2].setVisibility(0);
                        } else {
                            this.imageViewFocusList_whats2_select_Text_zoom[i2].setVisibility(8);
                        }
                    }
                    if (this.imageViewFocusList_whats1_select_Text_zoom[i2] != null) {
                        this.imageViewFocusList_whats1_select_Text_zoom[i2].setVisibility(8);
                    }
                    if (this.imageViewFocusList_whats2_normal_modeIcon_zoom[i2] != null) {
                        if (i2 == this.m_iCurrFocus) {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i2].setVisibility(8);
                        } else {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i2].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats1_normal_modeIcon_zoom[i2] != null) {
                        this.imageViewFocusList_whats1_normal_modeIcon_zoom[i2].setVisibility(0);
                    }
                }
                if (this.imageViewFocusList_whats2[i2] != null) {
                    if (i2 == this.m_iCurrFocus) {
                        this.imageViewFocusList_whats2[i2].setVisibility(0);
                    } else {
                        this.imageViewFocusList_whats2[i2].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats1[i2] != null) {
                    this.imageViewFocusList_whats1[i2].setVisibility(8);
                }
                if (this.imageViewFocusList_whats2_modeIcon[i2] != null) {
                    if (i2 == this.m_iCurrFocus) {
                        this.imageViewFocusList_whats2_modeIcon[i2].setVisibility(0);
                    } else {
                        this.imageViewFocusList_whats2_modeIcon[i2].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats1_modeIcon[i2] != null) {
                    this.imageViewFocusList_whats1_modeIcon[i2].setVisibility(8);
                }
            }
        } else if (this.m_iCurrFocus >= len) {
            for (int i3 = 0; i3 < this.imageViewFocusList_whats2.length; i3++) {
                if (this.ksw_m_b_Focus_image_zoom_evo) {
                    if (this.imageViewFocusList_whats2_normal_Text_zoom[i3] != null) {
                        if (i3 == this.m_iCurrFocus - len) {
                            this.imageViewFocusList_whats2_normal_Text_zoom[i3].setVisibility(8);
                        } else {
                            this.imageViewFocusList_whats2_normal_Text_zoom[i3].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats1_normal_Text_zoom[i3] != null) {
                        this.imageViewFocusList_whats1_normal_Text_zoom[i3].setVisibility(0);
                    }
                    if (this.imageViewFocusList_whats2_select_Text_zoom[i3] != null) {
                        if (i3 == this.m_iCurrFocus - len) {
                            this.imageViewFocusList_whats2_select_Text_zoom[i3].setVisibility(0);
                        } else {
                            this.imageViewFocusList_whats2_select_Text_zoom[i3].setVisibility(8);
                        }
                    }
                    if (this.imageViewFocusList_whats1_select_Text_zoom[i3] != null) {
                        this.imageViewFocusList_whats1_select_Text_zoom[i3].setVisibility(8);
                    }
                    if (this.imageViewFocusList_whats2_normal_modeIcon_zoom[i3] != null) {
                        if (i3 == this.m_iCurrFocus - len) {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i3].setVisibility(8);
                        } else {
                            this.imageViewFocusList_whats2_normal_modeIcon_zoom[i3].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats1_normal_modeIcon_zoom[i3] != null) {
                        this.imageViewFocusList_whats1_normal_modeIcon_zoom[i3].setVisibility(0);
                    }
                }
                if (this.imageViewFocusList_whats2[i3] != null) {
                    if (i3 == this.m_iCurrFocus - len) {
                        this.imageViewFocusList_whats2[i3].setVisibility(0);
                    } else {
                        this.imageViewFocusList_whats2[i3].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats1[i3] != null) {
                    this.imageViewFocusList_whats1[i3].setVisibility(8);
                }
                if (this.imageViewFocusList_whats2_modeIcon[i3] != null) {
                    if (i3 == this.m_iCurrFocus - len) {
                        this.imageViewFocusList_whats2_modeIcon[i3].setVisibility(0);
                    } else {
                        this.imageViewFocusList_whats2_modeIcon[i3].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats1_modeIcon[i3] != null) {
                    this.imageViewFocusList_whats1_modeIcon[i3].setVisibility(8);
                }
            }
        } else {
            for (int i4 = 0; i4 < this.imageViewFocusList_whats1.length; i4++) {
                if (this.ksw_m_b_Focus_image_zoom_evo) {
                    if (this.imageViewFocusList_whats1_normal_Text_zoom[i4] != null) {
                        if (i4 == this.m_iCurrFocus) {
                            this.imageViewFocusList_whats1_normal_Text_zoom[i4].setVisibility(8);
                        } else {
                            this.imageViewFocusList_whats1_normal_Text_zoom[i4].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats2_normal_Text_zoom[i4] != null) {
                        this.imageViewFocusList_whats2_normal_Text_zoom[i4].setVisibility(0);
                    }
                    if (this.imageViewFocusList_whats1_select_Text_zoom[i4] != null) {
                        if (i4 == this.m_iCurrFocus) {
                            this.imageViewFocusList_whats1_select_Text_zoom[i4].setVisibility(0);
                        } else {
                            this.imageViewFocusList_whats1_select_Text_zoom[i4].setVisibility(8);
                        }
                    }
                    if (this.imageViewFocusList_whats2_select_Text_zoom[i4] != null) {
                        this.imageViewFocusList_whats2_select_Text_zoom[i4].setVisibility(8);
                    }
                    if (this.imageViewFocusList_whats1_normal_modeIcon_zoom[i4] != null) {
                        if (i4 == this.m_iCurrFocus) {
                            this.imageViewFocusList_whats1_normal_modeIcon_zoom[i4].setVisibility(8);
                        } else {
                            this.imageViewFocusList_whats1_normal_modeIcon_zoom[i4].setVisibility(0);
                        }
                    }
                    if (this.imageViewFocusList_whats2_normal_modeIcon_zoom[i4] != null) {
                        this.imageViewFocusList_whats2_normal_modeIcon_zoom[i4].setVisibility(0);
                    }
                }
                if (this.imageViewFocusList_whats1[i4] != null) {
                    if (i4 == this.m_iCurrFocus) {
                        this.imageViewFocusList_whats1[i4].setVisibility(0);
                    } else {
                        this.imageViewFocusList_whats1[i4].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats2[i4] != null) {
                    this.imageViewFocusList_whats2[i4].setVisibility(8);
                }
                if (this.imageViewFocusList_whats1_modeIcon[i4] != null) {
                    if (i4 == this.m_iCurrFocus) {
                        this.imageViewFocusList_whats1_modeIcon[i4].setVisibility(0);
                    } else {
                        this.imageViewFocusList_whats1_modeIcon[i4].setVisibility(8);
                    }
                }
                if (this.imageViewFocusList_whats2_modeIcon[i4] != null) {
                    this.imageViewFocusList_whats2_modeIcon[i4].setVisibility(8);
                }
            }
        }
    }

    public void refreshFocusView_evo_id6() {
        Log.i(TAG, "refreshFocusView_evo_id6: m_iCurrFocus = " + this.m_iCurrFocus);
        if (this.bIsShowSmallViewEVO_ID6) {
            if (this.m_iCurrFocus < 6) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(0);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i = 0; i < this.imgFocusList.length; i++) {
                        if (this.m_iCurrFocus == i) {
                            if (this.imageViewFocusList_whats1[i] != null) {
                                this.imageViewFocusList_whats1[i].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats1[i] != null) {
                            this.imageViewFocusList_whats1[i].setVisibility(8);
                        }
                    }
                }
            } else if (this.m_iCurrFocus < 12) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(1);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i2 = 0; i2 < this.imgFocusList.length; i2++) {
                        if (this.m_iCurrFocus == i2) {
                            if (this.imageViewFocusList_whats2[i2] != null) {
                                this.imageViewFocusList_whats2[i2].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats2[i2] != null) {
                            this.imageViewFocusList_whats2[i2].setVisibility(8);
                        }
                    }
                }
            }
        } else if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
            if (this.m_iCurrFocus < 4) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(0);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i3 = 0; i3 < this.imgFocusList.length; i3++) {
                        if (this.m_iCurrFocus == i3) {
                            if (this.imageViewFocusList_whats1[i3] != null) {
                                this.imageViewFocusList_whats1[i3].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats1[i3] != null) {
                            this.imageViewFocusList_whats1[i3].setVisibility(8);
                        }
                    }
                }
            } else if (this.m_iCurrFocus < 8) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(1);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i4 = 0; i4 < this.imgFocusList.length; i4++) {
                        if (this.m_iCurrFocus == i4) {
                            if (this.imageViewFocusList_whats2[i4] != null) {
                                this.imageViewFocusList_whats2[i4].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats2[i4] != null) {
                            this.imageViewFocusList_whats2[i4].setVisibility(8);
                        }
                    }
                }
            } else if (this.m_iCurrFocus < 12) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(2);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i5 = 0; i5 < this.imgFocusList.length; i5++) {
                        if (this.m_iCurrFocus == i5) {
                            if (this.imageViewFocusList_whats3[i5] != null) {
                                this.imageViewFocusList_whats3[i5].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats3[i5] != null) {
                            this.imageViewFocusList_whats3[i5].setVisibility(8);
                        }
                    }
                }
            }
        } else if (this.m_iCurrFocus < 3) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(0);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i6 = 0; i6 < this.imgFocusList.length; i6++) {
                    if (this.m_iCurrFocus == i6) {
                        if (this.imageViewFocusList_whats1[i6] != null) {
                            this.imageViewFocusList_whats1[i6].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats1[i6] != null) {
                        this.imageViewFocusList_whats1[i6].setVisibility(8);
                    }
                }
            }
        } else if (this.m_iCurrFocus < 6) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i7 = 0; i7 < this.imgFocusList.length; i7++) {
                    if (this.m_iCurrFocus == i7) {
                        if (this.imageViewFocusList_whats2[i7] != null) {
                            this.imageViewFocusList_whats2[i7].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats2[i7] != null) {
                        this.imageViewFocusList_whats2[i7].setVisibility(8);
                    }
                }
            }
        } else if (this.m_iCurrFocus < 9) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(2);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i8 = 0; i8 < this.imgFocusList.length; i8++) {
                    if (this.m_iCurrFocus == i8) {
                        if (this.imageViewFocusList_whats3[i8] != null) {
                            this.imageViewFocusList_whats3[i8].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats3[i8] != null) {
                        this.imageViewFocusList_whats3[i8].setVisibility(8);
                    }
                }
            }
        } else if (this.m_iCurrFocus < 12) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(3);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i9 = 0; i9 < this.imgFocusList.length; i9++) {
                    if (this.m_iCurrFocus == i9) {
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

    /* access modifiers changed from: private */
    public void refreshFocusView_evo_id7() {
        Log.i(TAG, "refreshFocusView_evo_id7: bInLeftFocus = " + this.bInLeftFocus + ", m_iCurrFocus = " + this.m_iCurrFocus);
        if (this.bInLeftFocus) {
            for (int i = 0; i < this.imgFocusList.length; i++) {
                if (this.imageViewFocusList_whats1[i] != null) {
                    this.imageViewFocusList_whats1[i].setVisibility(8);
                }
                if (this.imageViewFocusList_whats2[i] != null) {
                    this.imageViewFocusList_whats2[i].setVisibility(8);
                }
                if (this.imageViewFocusList_whats3[i] != null) {
                    this.imageViewFocusList_whats3[i].setVisibility(8);
                }
            }
        } else if (this.m_iCurrFocus < 2) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(0);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i2 = 0; i2 < this.imgFocusList.length; i2++) {
                    if (this.m_iCurrFocus == i2) {
                        if (this.imageViewFocusList_whats1[i2] != null) {
                            this.imageViewFocusList_whats1[i2].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats1[i2] != null) {
                        this.imageViewFocusList_whats1[i2].setVisibility(8);
                    }
                }
            }
        } else if (this.m_iCurrFocus < 4) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(1);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i3 = 0; i3 < this.imgFocusList.length; i3++) {
                    if (this.m_iCurrFocus == i3) {
                        if (this.imageViewFocusList_whats2[i3] != null) {
                            this.imageViewFocusList_whats2[i3].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats2[i3] != null) {
                        this.imageViewFocusList_whats2[i3].setVisibility(8);
                    }
                }
            }
        } else if (this.m_iCurrFocus < 6) {
            if (this.mViewPager != null) {
                this.mViewPager.setCurrentItem(2);
            }
            if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                for (int i4 = 0; i4 < this.imgFocusList.length; i4++) {
                    if (this.m_iCurrFocus == i4) {
                        if (this.imageViewFocusList_whats3[i4] != null) {
                            this.imageViewFocusList_whats3[i4].setVisibility(0);
                        }
                    } else if (this.imageViewFocusList_whats3[i4] != null) {
                        this.imageViewFocusList_whats3[i4].setVisibility(8);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void refreshFocusView_CHWY() {
        for (int i = 0; i < this.imageViewFocusList1_chwy.length; i++) {
            if (this.imageViewFocusList1_chwy[i] != null) {
                if (this.m_iCurrFocus_chwy == i) {
                    this.imageViewFocusList1_chwy[i].setVisibility(0);
                } else {
                    this.imageViewFocusList1_chwy[i].setVisibility(8);
                }
            }
        }
        for (int i2 = 0; i2 < this.imageViewFocusList2_chwy.length; i2++) {
            if (this.imageViewFocusList2_chwy[i2] != null) {
                if (this.m_iCurrFocus_chwy == i2) {
                    this.imageViewFocusList2_chwy[i2].setVisibility(0);
                } else {
                    this.imageViewFocusList2_chwy[i2].setVisibility(8);
                }
            }
        }
    }

    public void refreshFocusView_normal_1920x720() {
        if (this.m_iUiIndex == 0) {
            if (this.m_iCurrFocus < 6) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(0);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i = 0; i < this.imgFocusList.length; i++) {
                        if (this.m_iCurrFocus == i) {
                            if (this.imageViewFocusList_whats1[i] != null) {
                                this.imageViewFocusList_whats1[i].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats1[i] != null) {
                            this.imageViewFocusList_whats1[i].setVisibility(8);
                        }
                    }
                }
            } else if (this.m_iCurrFocus < 12) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(1);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i2 = 0; i2 < this.imgFocusList.length; i2++) {
                        if (this.m_iCurrFocus == this.imgFocusList.length + i2) {
                            if (this.imageViewFocusList_whats2[i2] != null) {
                                this.imageViewFocusList_whats2[i2].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats2[i2] != null) {
                            this.imageViewFocusList_whats2[i2].setVisibility(8);
                        }
                    }
                }
            }
        } else if (this.m_iUiIndex != 1) {
        } else {
            if (this.m_iCurrFocus < 4) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(0);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i3 = 0; i3 < this.imgFocusList.length; i3++) {
                        if (this.m_iCurrFocus == i3) {
                            if (this.imageViewFocusList_whats1[i3] != null) {
                                this.imageViewFocusList_whats1[i3].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats1[i3] != null) {
                            this.imageViewFocusList_whats1[i3].setVisibility(8);
                        }
                    }
                }
            } else if (this.m_iCurrFocus < 8) {
                if (this.mViewPager != null) {
                    this.mViewPager.setCurrentItem(1);
                }
                if (this.imgFocusList != null && this.imgFocusList.length > 0) {
                    for (int i4 = 0; i4 < this.imgFocusList.length; i4++) {
                        if (this.m_iCurrFocus == this.imgFocusList.length + i4) {
                            if (this.imageViewFocusList_whats2[i4] != null) {
                                this.imageViewFocusList_whats2[i4].setVisibility(0);
                            }
                        } else if (this.imageViewFocusList_whats2[i4] != null) {
                            this.imageViewFocusList_whats2[i4].setVisibility(8);
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void zxwOriginalMcuKeyEnter_evo_id7_leftFocus() {
        switch (this.m_iCurrLeftFocus) {
            case 0:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.navigation", EventUtils.NAV_MODE_CLASS_NAME);
                return;
            case 2:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.SET_MODE_PACKAGE_NAME, EventUtils.SET_MODE_CLASS_NAME);
                return;
            case 3:
                sendBroadcast(new Intent(EventUtils.ACTION_SWITCH_ORIGINACAR));
                return;
            case 4:
                showAllApps(true);
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: private */
    public void refreshLeftFocusView() {
        if ((this.bInLeftFocus || this.m_iCurrLeftFocus == -1) && this.imgLeftFocusList != null && this.imgLeftFocusList.length > 0) {
            for (int i = 0; i < this.imgLeftFocusList.length; i++) {
                if (this.m_iCurrLeftFocus == i) {
                    if (this.imageViewLeftFocusList[i] != null) {
                        this.imageViewLeftFocusList[i].setVisibility(0);
                    }
                } else if (this.imageViewLeftFocusList[i] != null) {
                    this.imageViewLeftFocusList[i].setVisibility(8);
                }
            }
        }
    }

    public void ksw_enter_dvr() {
        this.m_i_have_DVR_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_RECORD_DVR, this.m_i_have_DVR_index);
        Log.i(TAG, "ksw_enter_dvr: m_i_have_DVR_index = " + this.m_i_have_DVR_index);
        if (this.m_i_have_DVR_index == 1) {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.ksw_dvr", "com.szchoiceway.ksw_dvr.MainActivity");
        } else if (this.m_i_have_DVR_index == 2) {
            String xml_Dvr_apk_packagename = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_DVR_APK_PACKAGENAME);
            if ("".equals(xml_Dvr_apk_packagename) || "com.anwensoft.cardvr".equals(xml_Dvr_apk_packagename)) {
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.anwensoft.cardvr", "com.anwensoft.cardvr.ui.GuideActivity");
            } else if ("".equals(xml_Dvr_apk_packagename) || "com.ankai.cardvr".equals(xml_Dvr_apk_packagename)) {
                EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.ankai.cardvr", "com.ankai.cardvr.ui.GuideActivity");
            } else {
                startAPP_only_packageName(xml_Dvr_apk_packagename);
            }
        } else {
            try {
                Toast.makeText(this, getString(R.string.lb_no_device), 1).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ksw_enter_dvd() {
        Log.i(TAG, "ksw_enter_dvd: m_b_have_DVD = " + this.m_b_have_DVD);
        if (this.m_b_have_DVD == 1) {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.ksw_dvd", "com.szchoiceway.ksw_dvd.MainActivity");
        } else if (this.m_b_have_DVD == 2) {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.dvdplayer", "com.szchoiceway.dvdplayer.MainActivity");
        } else {
            try {
                Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ksw_enter_yibiaopan() {
        if (this.m_b_ksw_Support_dashboard) {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.ksw_dashboard", "com.szchoiceway.ksw_dashboard.MainActivity");
            return;
        }
        try {
            Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ksw_enter_browser() {
        EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.EXPLORER_MODE_PACKAGE_NAME2, EventUtils.EXPLORER_MODE_CLASS_NAME2);
    }

    public void ksw_enter_cmmb() {
        if (this.m_b_have_TV) {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.ksw_cmmb", "com.szchoiceway.ksw_cmmb.MainActivity");
            return;
        }
        try {
            Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ksw_enter_aux() {
        if (this.m_b_have_AUX) {
            EventUtils.startActivityIfNotRuning(getApplicationContext(), "com.szchoiceway.ksw_aux", "com.szchoiceway.ksw_aux.MainActivity");
            return;
        }
        try {
            Toast.makeText(this, getString(R.string.lb_no_device), 0).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void ksw_set_bt_connect_menu(boolean is_Bt_Connneced) {
        if (this.iv_bt_connect_show_KSW == null) {
            return;
        }
        if (is_Bt_Connneced) {
            if (this.m_iModeSet == 1) {
                this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1280x480_audi_bt_connect_d);
            } else if (this.m_iModeSet == 3) {
                this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1024x600_audi_bt_connect_d);
            } else if (this.m_iModeSet == 5) {
                this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1024x600_benchi_bt_connect_d);
            } else {
                this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1280x480_bt_connect_d);
            }
        } else if (this.m_iModeSet == 1) {
            this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1280x480_audi_bt_connect_n);
        } else if (this.m_iModeSet == 3) {
            this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1024x600_audi_bt_connect_n);
        } else if (this.m_iModeSet == 5) {
            this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1024x600_benchi_bt_connect_n);
        } else {
            this.iv_bt_connect_show_KSW.setBackgroundResource(R.drawable.kesaiwei_1280x480_bt_connect_n);
        }
    }

    /* access modifiers changed from: private */
    public void ksw_refresh_A4L_left_show() {
        if (this.m_iModeSet == 1 && this.ksw_A4L_audi_che != null) {
            int ksw_m_i_left_show_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_ARL_LEFT_SHOW_INDEX, 1);
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
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_01);
                    return;
                case 2:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_02);
                    return;
                case 3:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_03);
                    return;
                case 4:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q5);
                    return;
                case 5:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_ihavi);
                    return;
                case 6:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_a3);
                    return;
                case 7:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_a5);
                    return;
                case 8:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_a6);
                    return;
                case 9:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q3);
                    return;
                case 10:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_q7);
                    return;
                case 11:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1280x480_a4l_left_che_normal);
                    return;
                default:
                    return;
            }
        } else if (this.m_iModeSet == 3 && this.ksw_A4L_audi_che != null) {
            switch (this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_ARL_LEFT_SHOW_INDEX, 1)) {
                case 0:
                    this.ksw_A4L_audi_che.setVisibility(8);
                    return;
                case 1:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_01);
                    return;
                case 2:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_02);
                    return;
                case 3:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_03);
                    return;
                case 4:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_q5);
                    return;
                case 5:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_ihavi);
                    return;
                case 6:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_a3);
                    return;
                case 7:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_a5);
                    return;
                case 8:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_a6);
                    return;
                case 9:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_q3);
                    return;
                case 10:
                    this.ksw_A4L_audi_che.setVisibility(0);
                    this.ksw_A4L_audi_che.setBackgroundResource(R.drawable.kesaiwei_1024x600_a4l_left_che_q7);
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: private */
    public void ksw_refresh_A4L_right_show() {
        if (this.m_iModeSet == 1 && this.KSW_A4L_right_show_logo != null && this.KSW_A4L_right_show_Navi != null && this.KSW_A4L_right_Traffic_information != null && this.KSW_A4L_right_show_Medio != null) {
            int ksw_m_i_right_show_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_ARL_RIGHT_SHOW_INDEX, 1);
            Log.i(TAG, "ksw_refresh_A4L_right_show: ksw_m_i_right_show_index = " + ksw_m_i_right_show_index);
            switch (ksw_m_i_right_show_index) {
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

    /* access modifiers changed from: private */
    public void cheku_autonavi_standard_1(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        if (bundle.getInt(GuideInfoExtraKey.TYPE) == 1) {
            this.strNextRouadName = bundle.getString(GuideInfoExtraKey.NEXT_ROAD_NAME);
            this.iMapIcon = bundle.getInt(GuideInfoExtraKey.ICON);
            this.iRouteRemainDis = bundle.getInt(GuideInfoExtraKey.ROUTE_REMAIN_DIS);
            this.iRouteRemainTime = bundle.getInt(GuideInfoExtraKey.ROUTE_REMAIN_TIME);
            this.iSegRemainDis = bundle.getInt(GuideInfoExtraKey.SEG_REMAIN_DIS);
            refreshNaviInfo();
            this.mMyhandler.removeMessages(5);
            onShowNaviInfo(true);
            return;
        }
        this.mMyhandler.removeMessages(5);
        this.mMyhandler.sendEmptyMessageDelayed(5, 3000);
    }

    /* access modifiers changed from: private */
    public void cheku_autonavi_standard_2(Intent intent) {
        int Keytype = intent.getIntExtra("KEY_TYPE", 0);
        Log.i(TAG, "--->>> CHEKU AUTONAVI Keytype = " + Keytype);
        if (Keytype == 10001) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int type = bundle.getInt(GuideInfoExtraKey.TYPE);
                Log.i(TAG, "--->>> CHEKU AUTONAVI type *** = " + type);
                if (type == 1 || type == 0) {
                    this.strNextRouadName = bundle.getString(GuideInfoExtraKey.NEXT_ROAD_NAME);
                    this.iMapIcon = bundle.getInt(GuideInfoExtraKey.ICON);
                    this.iRouteRemainDis = bundle.getInt(GuideInfoExtraKey.ROUTE_REMAIN_DIS);
                    this.iRouteRemainTime = bundle.getInt(GuideInfoExtraKey.ROUTE_REMAIN_TIME);
                    this.iSegRemainDis = bundle.getInt(GuideInfoExtraKey.SEG_REMAIN_DIS);
                    refreshNaviInfo();
                    this.mMyhandler.removeMessages(5);
                    onShowNaviInfo(true);
                }
            }
        } else if (Keytype == 10019) {
            int ExtraState = intent.getIntExtra("EXTRA_STATE", 0);
            if (ExtraState == 2 || ExtraState == 9 || ExtraState == 12) {
                this.mMyhandler.removeMessages(5);
                this.mMyhandler.sendEmptyMessageDelayed(5, 3000);
            }
        }
    }

    /* access modifiers changed from: private */
    public void cheku_autonavi_standard_3(Intent intent) {
        int Keytype = intent.getIntExtra("KEY_TYPE", 0);
        int ExtraState = intent.getIntExtra("EXTRA_STATE", 0);
        if (Keytype == 10001) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int type = bundle.getInt(GuideInfoExtraKey.TYPE);
                if (type == 1 || type == 0) {
                    this.strNextRouadName = bundle.getString(GuideInfoExtraKey.NEXT_ROAD_NAME);
                    this.iMapIcon = bundle.getInt(GuideInfoExtraKey.ICON);
                    this.iRouteRemainDis = bundle.getInt(GuideInfoExtraKey.ROUTE_REMAIN_DIS);
                    this.iRouteRemainTime = bundle.getInt(GuideInfoExtraKey.ROUTE_REMAIN_TIME);
                    this.iSegRemainDis = bundle.getInt(GuideInfoExtraKey.SEG_REMAIN_DIS);
                    refreshNaviInfo();
                    this.bIsNavigating = true;
                    onShowNaviInfo(true);
                    onShowMusicInfo(false);
                }
            }
        } else if (Keytype != 10019) {
        } else {
            if (ExtraState == 2 || ExtraState == 9 || ExtraState == 12) {
                onShowNaviInfo(false);
                this.bIsNavigating = false;
                refreshPlayState();
            }
        }
    }

    public void startAPP_only_packageName(String appPackageName) {
        try {
            startActivity(getPackageManager().getLaunchIntentForPackage(appPackageName));
        } catch (Exception e) {
            Log.e(TAG, "startAPP_only_packageName: --->>> 没有这个包名");
        }
    }

    /* access modifiers changed from: private */
    public void onfresh_qichexinxi_audio_right_KSW() {
        if (this.tv_audio_right_xushilicheng != null) {
            this.tv_audio_right_xushilicheng.setText(this.ksw_m_str_audio_right_xushilicheng);
        }
        if (this.tv_audio_right_wendu != null) {
            this.tv_audio_right_wendu.setText(this.ksw_m_str_audio_right_wendu);
        }
        if (this.tv_audio_right_fadongjizhuansu != null) {
            this.tv_audio_right_fadongjizhuansu.setText("" + ksw_m_i_audio_right_fadongjizhuansu);
        }
    }

    /* access modifiers changed from: private */
    public void onRefreshCheLiangXinXI() {
        Log.i(TAG, "onRefreshCheLiangXinXI: ksw_m_str_xushilichengg = " + this.ksw_m_str_xushilichengg);
        Log.i(TAG, "onRefreshCheLiangXinXI: ksw_m_str_youliang = " + this.ksw_m_str_youliang);
        if (this.tvXuHangLiCheng != null) {
            this.tvXuHangLiCheng.setText(this.ksw_m_str_xushilichengg);
        }
        if (this.tvYouLiang != null) {
            this.tvYouLiang.setText(this.ksw_m_str_youliang + "L");
        }
        if (this.tvWenDu != null) {
            this.tvWenDu.setText(this.ksw_m_str_audio_right_wendu);
        }
    }

    /* access modifiers changed from: private */
    public void onRefreshBaseInfo() {
        if (this.cb_shousha != null) {
            if (this.bKesaiweiBPark) {
                this.cb_shousha.setChecked(true);
            } else {
                this.cb_shousha.setChecked(false);
            }
        }
        if (this.cb_anquandai != null) {
            if (this.bKesaiweiBelt) {
                this.cb_anquandai.setChecked(false);
            } else {
                this.cb_anquandai.setChecked(true);
            }
        }
        if (this.tvShouSha != null) {
            if (this.bKesaiweiBPark) {
                this.tvShouSha.setText(getResources().getString(R.string.lb_Hand_brake_pull));
            } else {
                this.tvShouSha.setText(getResources().getString(R.string.lb_Hand_brake_release));
            }
        }
        if (this.tvAnQuanDai == null) {
            return;
        }
        if (this.bKesaiweiBelt) {
            this.tvAnQuanDai.setText(getResources().getString(R.string.lb_Seat_belt_Yes));
        } else {
            this.tvAnQuanDai.setText(getResources().getString(R.string.lb_Seat_belt_No));
        }
    }

    /* access modifiers changed from: private */
    public void cheku_benchi_original_left() {
        if (b_sing_touch || b_sing_coverFlowView) {
            return;
        }
        if (this.m_iModeSet == 9) {
            this.cheku_benchi_m_p_left = HttpStatus.SC_INTERNAL_SERVER_ERROR;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + this.cheku_benchi_m_p_left), (float) (this.mCoverFlowView.getTop() + 125), 0));
            b_sing_coverFlowView = true;
            this.mMyhandler.removeMessages(12);
            this.mMyhandler.sendEmptyMessageDelayed(12, 20);
            return;
        }
        this.cheku_benchi_m_p_left = 640;
        this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + this.cheku_benchi_m_p_left), (float) (this.mCoverFlowView.getTop() + 125), 0));
        b_sing_coverFlowView = true;
        this.mMyhandler.removeMessages(12);
        this.mMyhandler.sendEmptyMessageDelayed(12, 20);
    }

    /* access modifiers changed from: private */
    public void cheku_benchi_original_right() {
        if (b_sing_touch || b_sing_coverFlowView) {
            return;
        }
        if (this.m_iModeSet == 9) {
            this.cheku_benchi_m_p_right = 780;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + this.cheku_benchi_m_p_right), (float) (this.mCoverFlowView.getTop() + 125), 0));
            b_sing_coverFlowView = true;
            this.mMyhandler.removeMessages(13);
            this.mMyhandler.sendEmptyMessageDelayed(13, 20);
            return;
        }
        this.cheku_benchi_m_p_right = 640;
        this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + this.cheku_benchi_m_p_right), (float) (this.mCoverFlowView.getTop() + 125), 0));
        b_sing_coverFlowView = true;
        this.mMyhandler.removeMessages(13);
        this.mMyhandler.sendEmptyMessageDelayed(13, 20);
    }

    /* access modifiers changed from: private */
    public void cheku_benchi_original_enter() {
        if (b_sing_touch || b_sing_coverFlowView) {
            return;
        }
        if (this.m_iModeSet == 9) {
            Log.i(TAG, "--->>> cheku_benchi_original_enter " + (this.mCoverFlowView.getLeft() + HttpStatus.SC_NOT_ACCEPTABLE) + "，" + (this.mCoverFlowView.getTop() - 40));
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + HttpStatus.SC_NOT_ACCEPTABLE), (float) (this.mCoverFlowView.getTop() - 40), 0));
            b_sing_coverFlowView = true;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (this.mCoverFlowView.getLeft() + HttpStatus.SC_NOT_ACCEPTABLE), (float) (this.mCoverFlowView.getTop() - 40), 0));
            b_sing_coverFlowView = false;
        } else if (this.m_iModeSet == 10 || this.m_iModeSet == 12) {
            Log.i(TAG, "--->>> cheku_benchi_original_enter " + (this.mCoverFlowView.getLeft() + 667) + "，" + (this.mCoverFlowView.getTop() - 20));
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + 667), (float) (this.mCoverFlowView.getTop() - 20), 0));
            b_sing_coverFlowView = true;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (this.mCoverFlowView.getLeft() + 667), (float) (this.mCoverFlowView.getTop() - 20), 0));
            b_sing_coverFlowView = false;
        } else {
            Log.i(TAG, "--->>> cheku_benchi_original_enter " + (this.mCoverFlowView.getLeft() + HttpStatus.SC_INTERNAL_SERVER_ERROR) + "，" + (this.mCoverFlowView.getTop() - 200));
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, (float) (this.mCoverFlowView.getLeft() + HttpStatus.SC_INTERNAL_SERVER_ERROR), (float) (this.mCoverFlowView.getTop() - 200), 0));
            b_sing_coverFlowView = true;
            this.mCoverFlowView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, (float) (this.mCoverFlowView.getLeft() + HttpStatus.SC_INTERNAL_SERVER_ERROR), (float) (this.mCoverFlowView.getTop() - 200), 0));
            b_sing_coverFlowView = false;
        }
    }

    /* access modifiers changed from: private */
    public void setupPrivNextVisibility_id6() {
        if (this.bIsShowSmallViewEVO_ID6 || this.mViewPager == null) {
            return;
        }
        if (this.m_i_ksw_evo_id6_main_interface_index == 1 || this.m_i_ksw_evo_id6_main_interface_index == 2) {
            if (this.mViewPager.getCurrentItem() == 0) {
                if (this.btnPagerPriv != null) {
                    this.btnPagerPriv.setVisibility(8);
                }
            } else if (this.mViewPager.getCurrentItem() != 2) {
                if (this.btnPagerPriv != null) {
                    this.btnPagerPriv.setVisibility(0);
                }
                if (this.btnPagerNext != null) {
                    this.btnPagerNext.setVisibility(0);
                }
            } else if (this.btnPagerNext != null) {
                this.btnPagerNext.setVisibility(8);
            }
        } else if (this.mViewPager.getCurrentItem() == 0) {
            if (this.btnPagerPriv != null) {
                this.btnPagerPriv.setVisibility(8);
            }
        } else if (this.mViewPager.getCurrentItem() != 3) {
            if (this.btnPagerPriv != null) {
                this.btnPagerPriv.setVisibility(0);
            }
            if (this.btnPagerNext != null) {
                this.btnPagerNext.setVisibility(0);
            }
        } else if (this.btnPagerNext != null) {
            this.btnPagerNext.setVisibility(8);
        }
    }

    public class FixedSpeedScroller extends Scroller {
        private int mDuration = 1500;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, this.mDuration);
        }

        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, this.mDuration);
        }

        public void setmDuration(int time) {
            this.mDuration = time;
        }

        public int getmDuration() {
            return this.mDuration;
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public DepthPageTransformer() {
        }

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1.0f) {
                view.setAlpha(0.0f);
            } else if (position <= 0.0f) {
                view.setAlpha(1.0f);
                view.setTranslationX(0.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
            } else if (position <= 1.0f) {
                view.setAlpha(1.0f - position);
                view.setTranslationX(((float) pageWidth) * (-position));
                float scaleFactor = MIN_SCALE + (0.25f * (1.0f - Math.abs(position)));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else {
                view.setAlpha(0.0f);
            }
        }
    }

    private void startConnectService() {
        try {
            bindService(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"), this.SerCon, 1);
            Log.i(TAG, "-------onBind Event Service ok-------");
        } catch (Exception e) {
            Log.e(TAG, "onBindService Event error " + e.toString());
        }
    }

    private void stopConntectService() {
        try {
            unbindService(this.SerCon);
        } catch (Exception e) {
            Log.e(TAG, "unbindService error " + e.toString());
        }
    }

    /* access modifiers changed from: package-private */
    public void showSystermTitleBar(boolean bShow) {
        if (!bShow) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= 1024;
            getWindow().setAttributes(lp);
            getWindow().addFlags(512);
            return;
        }
        WindowManager.LayoutParams attr = getWindow().getAttributes();
        attr.flags &= -1025;
        getWindow().setAttributes(attr);
        getWindow().clearFlags(512);
    }
}
