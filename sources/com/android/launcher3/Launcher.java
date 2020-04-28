package com.android.launcher3;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.PathInterpolatorCompat;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;
import com.android.launcher3.DropTarget;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Workspace;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.allapps.AllAppsContainerView;
import com.android.launcher3.allapps.AllAppsTransitionController;
import com.android.launcher3.allapps.DiscoveryBounce;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.compat.AppWidgetManagerCompat;
import com.android.launcher3.compat.LauncherAppsCompatVO;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.folder.FolderIconPreviewVerifier;
import com.android.launcher3.fragment.AppsFragment;
import com.android.launcher3.fragment.EmptyFragment;
import com.android.launcher3.fragment.MainFragment;
import com.android.launcher3.keyboard.CustomActionsPopup;
import com.android.launcher3.keyboard.ViewGroupFocusHelper;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.logging.UserEventDispatcher;
import com.android.launcher3.model.ModelWriter;
import com.android.launcher3.model.MyScannerAppsTask;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.popup.PopupDataProvider;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.states.InternalStateHandler;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.touch.TouchPaneCfg;
import com.android.launcher3.uioverrides.UiFactory;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.ActivityResultInfo;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.EventUtils;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.MultiHashMap;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.PackageUserKey;
import com.android.launcher3.util.PendingAnimation;
import com.android.launcher3.util.PendingRequestArgs;
import com.android.launcher3.util.Themes;
import com.android.launcher3.util.TraceHelper;
import com.android.launcher3.util.UiThreadHelper;
import com.android.launcher3.util.ViewOnDrawExecutor;
import com.android.launcher3.views.OptionsPopupView;
import com.android.launcher3.widget.LauncherAppWidgetHostView;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.android.launcher3.widget.PendingAppWidgetHostView;
import com.android.launcher3.widget.WidgetAddFlowHandler;
import com.android.launcher3.widget.WidgetHostViewLoader;
import com.android.launcher3.widget.WidgetListRowEntry;
import com.android.launcher3.widget.WidgetsFullSheet;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Launcher extends BaseDraggingActivity implements LauncherExterns, LauncherModel.Callbacks, LauncherProviderChangeListener, UserEventDispatcher.UserEventDelegate {
    private static final float BOUNCE_ANIMATION_TENSION = 1.3f;
    static final boolean DEBUG_STRICT_MODE = false;
    static final boolean LOGD = false;
    static final int NEW_APPS_ANIMATION_DELAY = 500;
    private static final int NEW_APPS_ANIMATION_INACTIVE_TIMEOUT_SECONDS = 5;
    private static final int NEW_APPS_PAGE_MOVE_DELAY = 500;
    private static final int ON_ACTIVITY_RESULT_ANIMATION_DELAY = 500;
    private static final int REQUEST_BIND_APPWIDGET = 11;
    public static final int REQUEST_BIND_PENDING_APPWIDGET = 12;
    private static final int REQUEST_CREATE_APPWIDGET = 5;
    private static final int REQUEST_CREATE_SHORTCUT = 1;
    protected static final int REQUEST_LAST = 100;
    private static final int REQUEST_PERMISSION_CALL_PHONE = 14;
    private static final int REQUEST_PICK_APPWIDGET = 9;
    public static final int REQUEST_RECONFIGURE_APPWIDGET = 13;
    private static final String RUNTIME_STATE = "launcher.state";
    private static final String RUNTIME_STATE_CURRENT_SCREEN = "launcher.current_screen";
    private static final String RUNTIME_STATE_PENDING_ACTIVITY_RESULT = "launcher.activity_result";
    private static final String RUNTIME_STATE_PENDING_REQUEST_ARGS = "launcher.request_args";
    private static final String RUNTIME_STATE_WIDGET_PANEL = "launcher.widget_panel";
    public static final String TAG = "Launcher";
    public boolean bGoogleApps = false;
    public boolean bHaveAux = false;
    public boolean bHaveTv = false;
    /* access modifiers changed from: private */
    public boolean bIsResume = true;
    /* access modifiers changed from: private */
    public boolean bShowApps = false;
    public boolean bSupportDashBoard = true;
    private FragmentManager fragmentManager = getFragmentManager();
    public int iHaveDvdType;
    public int iHaveDvrType;
    int iTest = -1;
    int iTest2 = -1;
    public boolean ksw_m_b_Focus_image_zoom_evo = false;
    private LauncherAccessibilityDelegate mAccessibilityDelegate;
    AllAppsTransitionController mAllAppsController;
    public LauncherApplication mApp;
    private LauncherAppTransitionManager mAppTransitionManager;
    private LauncherAppWidgetHost mAppWidgetHost;
    private AppWidgetManagerCompat mAppWidgetManager;
    public List<ResolveInfo> mApps;
    /* access modifiers changed from: private */
    public AppsFragment mAppsFragment;
    public int mAppsIconIndex = 0;
    AllAppsContainerView mAppsView;
    private final BroadcastReceiver mBroadcastReceiver = new MBroadcastReceiver();
    private DragController mDragController;
    DragLayer mDragLayer;
    private DropTargetBar mDropTargetBar;
    private EmptyFragment mEmptyFragment;
    public ViewGroupFocusHelper mFocusHandler;
    private final Handler mHandler = new Handler();
    Hotseat mHotseat;
    @Nullable
    private View mHotseatSearchBox;
    private IconCache mIconCache;
    private LauncherCallbacks mLauncherCallbacks;
    private View mLauncherView;
    private final Runnable mLogOnDelayedResume = new Runnable() {
        public final void run() {
            Launcher.this.logOnDelayedResume();
        }
    };
    /* access modifiers changed from: private */
    public MainFragment mMainFragment;
    private MainHandler mMainHandler = new MainHandler(this);
    private LauncherModel mModel;
    private ModelWriter mModelWriter;
    private Configuration mOldConfig;
    private OnResumeCallback mOnResumeCallback;
    private View mOverviewPanel;
    private ActivityResultInfo mPendingActivityResult;
    private ViewOnDrawExecutor mPendingExecutor;
    /* access modifiers changed from: private */
    public PendingRequestArgs mPendingRequestArgs;
    private PopupDataProvider mPopupDataProvider;
    private RotationHelper mRotationHelper;
    private final BroadcastReceiver mScreenOffReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (Launcher.this.mPendingRequestArgs == null) {
                Launcher.this.mStateManager.goToState(LauncherState.NORMAL);
            }
        }
    };
    private SharedPreferences mSharedPrefs;
    /* access modifiers changed from: private */
    public LauncherStateManager mStateManager;
    private int mSynchronouslyBoundPage = -1;
    public SysProviderOpt mSysProviderOpt;
    private final int[] mTmpAddItemCellCoordinates = new int[2];
    Workspace mWorkspace;
    boolean mWorkspaceLoading = true;
    public int m_iModeSet = 0;
    public int m_iUITypeVer = 41;
    public int m_iUiIndex = 2;
    public int m_i_ksw_evo_id6_main_interface_index = 0;
    public int m_i_ksw_evo_main_interface_index = 0;
    private MyScannerAppsTask.ScannerAppsCallback scannerAppsCallback = new MyScannerAppsTask.ScannerAppsCallback() {
        public void ScannedApps(List<ResolveInfo> mAppsList) {
            Launcher.this.mApps = mAppsList;
            for (int i = 0; i < Launcher.this.mApps.size(); i++) {
                String name = Launcher.this.mApps.get(i).loadLabel(Launcher.this.getPackageManager()).toString();
                String packageName = Launcher.this.mApps.get(i).activityInfo.packageName;
                String className = Launcher.this.mApps.get(i).activityInfo.name;
                Log.i(Launcher.TAG, "ScannedApps: i = " + i + ",name = " + name + ", packageName = " + packageName + ", className = " + className);
            }
            if (Launcher.this.mAppsFragment != null && Launcher.this.mAppsFragment.mRecyclerVierAdapter != null) {
                Launcher.this.mAppsFragment.mRecyclerVierAdapter.setData(Launcher.this.mApps);
            }
        }
    };
    private int screenHeight;
    private int screenWidth;
    private Fragment showFragment;
    public String xml_client;

    public interface LauncherOverlay {
        void onScrollChange(float f, boolean z);

        void onScrollInteractionBegin();

        void onScrollInteractionEnd();

        void setOverlayCallbacks(LauncherOverlayCallbacks launcherOverlayCallbacks);
    }

    public interface LauncherOverlayCallbacks {
        void onScrollChanged(float f);
    }

    public interface OnResumeCallback {
        void onLauncherResume();
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        this.mApp = (LauncherApplication) getApplication();
        this.mSysProviderOpt = this.mApp.getSysProviderOpt();
        this.screenWidth = getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = getResources().getDisplayMetrics().heightPixels;
        Log.i(TAG, "onCreate: screenWidth = " + this.screenWidth + " ,screenHeight = " + this.screenHeight);
        getSysProvideParam();
        customerUIProtection();
        TraceHelper.beginSection("Launcher-onCreate");
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: ");
        getWindow().addFlags(512);
        TraceHelper.partitionSection("Launcher-onCreate", "super call");
        LauncherAppState app = LauncherAppState.getInstance(this);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
        this.mModel = app.setLauncher(this);
        initDeviceProfile(app.getInvariantDeviceProfile());
        this.mSharedPrefs = Utilities.getPrefs(this);
        this.mIconCache = app.getIconCache();
        this.mAccessibilityDelegate = new LauncherAccessibilityDelegate(this);
        this.mDragController = new DragController(this);
        this.mAllAppsController = new AllAppsTransitionController(this);
        this.mStateManager = new LauncherStateManager(this);
        UiFactory.onCreate(this);
        this.mAppWidgetManager = AppWidgetManagerCompat.getInstance(this);
        this.mAppWidgetHost = new LauncherAppWidgetHost(this);
        this.mAppWidgetHost.startListening();
        this.mLauncherView = LayoutInflater.from(this).inflate(R.layout.launcher, (ViewGroup) null);
        Log.i(TAG, "onCreate: viewCreated");
        setupViews();
        initView();
        initAppsListView();
        initSystemService();
        this.mPopupDataProvider = new PopupDataProvider(this);
        this.mRotationHelper = new RotationHelper(this);
        this.mAppTransitionManager = LauncherAppTransitionManager.newInstance(this);
        boolean internalStateHandled = InternalStateHandler.handleCreate(this, getIntent());
        if (internalStateHandled && savedInstanceState != null) {
            savedInstanceState.remove(RUNTIME_STATE);
        }
        restoreState(savedInstanceState);
        int currentScreen = PagedView.INVALID_RESTORE_PAGE;
        if (savedInstanceState != null) {
            currentScreen = savedInstanceState.getInt(RUNTIME_STATE_CURRENT_SCREEN, PagedView.INVALID_RESTORE_PAGE);
        }
        if (this.mModel.startLoader(currentScreen)) {
            this.mWorkspace.setCurrentPage(currentScreen);
            setWorkspaceLoading(true);
        } else if (!internalStateHandled) {
            this.mDragLayer.getAlphaProperty(1).setValue(0.0f);
        }
        setDefaultKeyMode(3);
        setContentView(this.mLauncherView);
        getRootView().dispatchInsets();
        registerReceiver(this.mScreenOffReceiver, new IntentFilter("android.intent.action.SCREEN_OFF"));
        getSystemUiController().updateUiState(0, Themes.getAttrBoolean(this, R.attr.isWorkspaceDarkText));
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onCreate(savedInstanceState);
        }
        this.mRotationHelper.initialize();
        TraceHelper.endSection("Launcher-onCreate");
        registerReceiver();
        showApps(false);
        updateTimerInfor();
        new MyScannerAppsTask(this, this.scannerAppsCallback).execute(new Void[0]);
        if (this.mMainHandler != null) {
            this.mMainHandler.sendEmptyMessageDelayed(4, 9000);
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
        Log.i(TAG, "onMultiWindowModeChanged: isInMultiWindowMode = " + isInMultiWindowMode);
        showApps(isInMultiWindowMode);
        if (this.mAppsFragment != null) {
            this.mAppsFragment.setActionBar(isInMultiWindowMode);
        }
    }

    private void getSysProvideParam() {
        this.m_iUITypeVer = this.mApp.getUITypeVer();
        Log.i(TAG, "getSysProvideParam: m_iUITypeVer = " + this.m_iUITypeVer);
        if (this.mSysProviderOpt == null) {
            this.mSysProviderOpt = this.mApp.getSysProviderOpt();
        }
        if (this.mSysProviderOpt != null) {
            this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
            if (this.m_iModeSet == 11) {
                this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "0");
                this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
            } else if (this.m_iModeSet == 6 || this.m_iModeSet == 14) {
                this.mSysProviderOpt.updateRecord(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, "3");
                this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
            }
            Log.i(TAG, "getSysProvideParam: m_iModeSet = " + this.m_iModeSet);
            this.mAppsIconIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_APPS_ICON_SELECT_INDEX, 0);
            Log.i(TAG, "getSysProvideParam: mAppsIconIndex = " + this.mAppsIconIndex);
            this.xml_client = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_FACTORY_SET_CLIENT, "");
            Log.i(TAG, "getSysProvideParam: xml_client = " + this.xml_client);
            this.bGoogleApps = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.MAISILUO_SYS_GOOGLEPLAY, this.bGoogleApps);
            this.bHaveAux = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_HAVE_AUX, this.bHaveAux);
            this.bHaveTv = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_HAVE_TV, this.bHaveTv);
            this.bSupportDashBoard = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_SUPPORT_DASHBOARD, this.bSupportDashBoard);
            this.iHaveDvdType = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_HAVE_DVD, this.iHaveDvdType);
            this.iHaveDvrType = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_RECORD_DVR, this.iHaveDvrType);
            this.ksw_m_b_Focus_image_zoom_evo = this.mSysProviderOpt.getRecordBoolean(SysProviderOpt.KSW_EVO_MAIN_INTERFACE_SELECT_ZOOM, this.ksw_m_b_Focus_image_zoom_evo);
            this.m_i_ksw_evo_main_interface_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_EVO_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_main_interface_index);
            this.m_i_ksw_evo_id6_main_interface_index = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_EVO_ID6_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_id6_main_interface_index);
            Log.i(TAG, "getSysProvideParam: bGoogleApps = " + this.bGoogleApps);
            Log.i(TAG, "getSysProvideParam: bHaveAux = " + this.bHaveAux);
            Log.i(TAG, "getSysProvideParam: bHaveTv = " + this.bHaveTv);
            Log.i(TAG, "getSysProvideParam: iHaveDvdType = " + this.iHaveDvdType);
            Log.i(TAG, "getSysProvideParam: iHaveDvrType = " + this.iHaveDvrType);
            this.m_iUiIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE_INDEX, this.m_iUiIndex);
        }
    }

    private void customerUIProtection() {
        if (this.m_iModeSet == 7 && ((this.m_i_ksw_evo_id6_main_interface_index != 1 || !"GeShi".equalsIgnoreCase(this.xml_client)) && ((this.m_i_ksw_evo_id6_main_interface_index != 2 || !"als".equalsIgnoreCase(this.xml_client)) && (this.m_i_ksw_evo_id6_main_interface_index != 3 || !"GeShi".equalsIgnoreCase(this.xml_client))))) {
            this.m_i_ksw_evo_id6_main_interface_index = 0;
            SysProviderOpt sysProviderOpt = this.mSysProviderOpt;
            sysProviderOpt.updateRecord(SysProviderOpt.KSW_EVO_ID6_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_id6_main_interface_index + "");
        }
        if (this.m_iModeSet == 17) {
            this.m_i_ksw_evo_id6_main_interface_index = 0;
            SysProviderOpt sysProviderOpt2 = this.mSysProviderOpt;
            sysProviderOpt2.updateRecord(SysProviderOpt.KSW_EVO_ID6_MAIN_INTERFACE_INDEX, this.m_i_ksw_evo_id6_main_interface_index + "");
        }
        if (this.mAppsIconIndex != 1 || !"als".equalsIgnoreCase(this.xml_client)) {
            this.mAppsIconIndex = 0;
            SysProviderOpt sysProviderOpt3 = this.mSysProviderOpt;
            sysProviderOpt3.updateRecord(SysProviderOpt.KSW_APPS_ICON_SELECT_INDEX, this.mAppsIconIndex + "");
        }
    }

    private void initView() {
        this.mMainFragment = (MainFragment) getFragmentManager().findFragmentById(R.id.MainFragment);
    }

    private void initAppsListView() {
        this.mAppsFragment = new AppsFragment();
        if ("als".equalsIgnoreCase(this.xml_client) && this.mAppsIconIndex == 1) {
            this.mAppsFragment.setColumns(6);
        } else if (this.screenWidth == 1024 || this.screenHeight == 600) {
            this.mAppsFragment.setColumns(6);
        }
        this.mEmptyFragment = new EmptyFragment();
    }

    public MainHandler getMainHandler() {
        return this.mMainHandler;
    }

    public static class MainHandler extends Handler {
        private final Launcher main = ((Launcher) this.mainActivityWeakReference.get());
        private final WeakReference<Launcher> mainActivityWeakReference;

        public MainHandler(Launcher mainActivity) {
            this.mainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        public void handleMessage(Message msg) {
            Log.i(Launcher.TAG, "handleMessage: what = " + msg.what);
            switch (msg.what) {
                case 1:
                    try {
                        if (this.main.mApp.getEvtService() != null) {
                            this.main.mApp.getEvtService().sendMcuData_KSW(new byte[]{-14, 0, 104, 2, 5, 0});
                            return;
                        }
                        return;
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        return;
                    }
                case 2:
                    if (this.main.mMainFragment != null) {
                        this.main.mMainFragment.coverFlowLeftScroll();
                        return;
                    }
                    return;
                case 3:
                    if (this.main.mMainFragment != null) {
                        this.main.mMainFragment.coverFlowRightScroll();
                        return;
                    }
                    return;
                case 4:
                    removeMessages(4);
                    new TouchPaneCfg(this.main);
                    return;
                default:
                    return;
            }
        }
    }

    private void initSystemService() {
        startService(new Intent("com.szchoiceway.eventcenter.EventService").setPackage("com.szchoiceway.eventcenter"));
        startService(new Intent("com.szchoiceway.volwnd.VolWndService").setPackage("com.szchoiceway.volwnd"));
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.szchoiceway.VScanbus", "com.szchoiceway.VScanbus.CanBusService"));
        startService(intent);
    }

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
    }

    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        UiFactory.onEnterAnimationComplete(this);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if ((newConfig.diff(this.mOldConfig) & 1152) != 0) {
            this.mUserEventDispatcher = null;
            initDeviceProfile(this.mDeviceProfile.inv);
            dispatchDeviceProfileChanged();
            reapplyUi();
            this.mDragLayer.recreateControllers();
            rebindModel();
        }
        this.mOldConfig.setTo(newConfig);
        UiFactory.onLauncherStateOrResumeChanged(this);
        super.onConfigurationChanged(newConfig);
    }

    /* access modifiers changed from: protected */
    public void reapplyUi() {
        getRootView().dispatchInsets();
        getStateManager().reapplyState(true);
    }

    public void rebindModel() {
        int currentPage = this.mWorkspace.getNextPage();
        if (this.mModel.startLoader(currentPage)) {
            this.mWorkspace.setCurrentPage(currentPage);
            setWorkspaceLoading(true);
        }
    }

    private void initDeviceProfile(InvariantDeviceProfile idp) {
        this.mDeviceProfile = idp.getDeviceProfile(this);
        if (isInMultiWindowModeCompat()) {
            Display display = getWindowManager().getDefaultDisplay();
            Point mwSize = new Point();
            display.getSize(mwSize);
            this.mDeviceProfile = this.mDeviceProfile.getMultiWindowProfile(this, mwSize);
        }
        onDeviceProfileInitiated();
        this.mModelWriter = this.mModel.getWriter(this.mDeviceProfile.isVerticalBarLayout(), true);
    }

    public RotationHelper getRotationHelper() {
        return this.mRotationHelper;
    }

    public LauncherStateManager getStateManager() {
        return this.mStateManager;
    }

    public <T extends View> T findViewById(int id) {
        return this.mLauncherView.findViewById(id);
    }

    public void onAppWidgetHostReset() {
        if (this.mAppWidgetHost != null) {
            this.mAppWidgetHost.startListening();
        }
    }

    public void setLauncherOverlay(LauncherOverlay overlay) {
        if (overlay != null) {
            overlay.setOverlayCallbacks(new LauncherOverlayCallbacksImpl());
        }
        this.mWorkspace.setLauncherOverlay(overlay);
    }

    public boolean setLauncherCallbacks(LauncherCallbacks callbacks) {
        this.mLauncherCallbacks = callbacks;
        return true;
    }

    public void onLauncherProviderChanged() {
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onLauncherProviderChange();
        }
    }

    public boolean isDraggingEnabled() {
        return !isWorkspaceLoading();
    }

    public int getViewIdForItem(ItemInfo info) {
        return (int) info.id;
    }

    public PopupDataProvider getPopupDataProvider() {
        return this.mPopupDataProvider;
    }

    public BadgeInfo getBadgeInfoForItem(ItemInfo info) {
        return this.mPopupDataProvider.getBadgeInfoForItem(info);
    }

    public void invalidateParent(ItemInfo info) {
        View folderIcon;
        if (new FolderIconPreviewVerifier(getDeviceProfile().inv).isItemInPreview(info.rank) && info.container >= 0 && (folderIcon = getWorkspace().getHomescreenIconByItemId(info.container)) != null) {
            folderIcon.invalidate();
        }
    }

    private long completeAdd(int requestCode, Intent intent, int appWidgetId, PendingRequestArgs info) {
        LauncherAppWidgetProviderInfo provider;
        long screenId = info.screenId;
        if (info.container == -100) {
            screenId = ensurePendingDropLayoutExists(info.screenId);
        }
        if (requestCode == 1) {
            completeAddShortcut(intent, info.container, screenId, info.cellX, info.cellY, info);
        } else if (requestCode != 5) {
            switch (requestCode) {
                case 12:
                    int widgetId = appWidgetId;
                    LauncherAppWidgetInfo widgetInfo = completeRestoreAppWidget(widgetId, 4);
                    if (!(widgetInfo == null || (provider = this.mAppWidgetManager.getLauncherAppWidgetInfo(widgetId)) == null)) {
                        new WidgetAddFlowHandler((AppWidgetProviderInfo) provider).startConfigActivity(this, widgetInfo, 13);
                        break;
                    }
                case 13:
                    completeRestoreAppWidget(appWidgetId, 0);
                    break;
            }
        } else {
            completeAddAppWidget(appWidgetId, info, (AppWidgetHostView) null, (LauncherAppWidgetProviderInfo) null);
        }
        return screenId;
    }

    private void handleActivityResult(int requestCode, int resultCode, Intent data) {
        int appWidgetId;
        int appWidgetId2;
        int i = requestCode;
        int i2 = resultCode;
        Intent intent = data;
        if (isWorkspaceLoading()) {
            this.mPendingActivityResult = new ActivityResultInfo(i, i2, intent);
            return;
        }
        this.mPendingActivityResult = null;
        PendingRequestArgs requestArgs = this.mPendingRequestArgs;
        setWaitingForResult((PendingRequestArgs) null);
        if (requestArgs != null) {
            int pendingAddWidgetId = requestArgs.getWidgetId();
            Runnable exitSpringLoaded = new Runnable() {
                public void run() {
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL, 500);
                }
            };
            int i3 = -1;
            if (i == 11) {
                int appWidgetId3 = intent != null ? intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, -1) : -1;
                if (i2 == 0) {
                    completeTwoStageWidgetDrop(0, appWidgetId3, requestArgs);
                    this.mWorkspace.removeExtraEmptyScreenDelayed(true, exitSpringLoaded, 500, false);
                    int i4 = appWidgetId3;
                } else if (i2 == -1) {
                    int i5 = appWidgetId3;
                    addAppWidgetImpl(appWidgetId3, requestArgs, (AppWidgetHostView) null, requestArgs.getWidgetHandler(), 500);
                }
            } else {
                if (i == 9 || i == 5) {
                    if (intent != null) {
                        i3 = intent.getIntExtra(LauncherSettings.Favorites.APPWIDGET_ID, -1);
                    }
                    int widgetId = i3;
                    if (widgetId < 0) {
                        appWidgetId = pendingAddWidgetId;
                    } else {
                        appWidgetId = widgetId;
                    }
                    int appWidgetId4 = appWidgetId;
                    if (appWidgetId4 < 0) {
                        appWidgetId2 = appWidgetId4;
                    } else if (i2 == 0) {
                        appWidgetId2 = appWidgetId4;
                    } else {
                        if (requestArgs.container == -100) {
                            requestArgs.screenId = ensurePendingDropLayoutExists(requestArgs.screenId);
                        }
                        CellLayout dropLayout = this.mWorkspace.getScreenWithId(requestArgs.screenId);
                        dropLayout.setDropPending(true);
                        final int i6 = resultCode;
                        final int i7 = appWidgetId4;
                        CellLayout dropLayout2 = dropLayout;
                        final PendingRequestArgs pendingRequestArgs = requestArgs;
                        int appWidgetId5 = appWidgetId4;
                        final CellLayout cellLayout = dropLayout2;
                        this.mWorkspace.removeExtraEmptyScreenDelayed(true, new Runnable() {
                            public void run() {
                                Launcher.this.completeTwoStageWidgetDrop(i6, i7, pendingRequestArgs);
                                cellLayout.setDropPending(false);
                            }
                        }, 500, false);
                        int i8 = appWidgetId5;
                        return;
                    }
                    Log.e(TAG, "Error: appWidgetId (EXTRA_APPWIDGET_ID) was not returned from the widget configuration activity.");
                    completeTwoStageWidgetDrop(0, appWidgetId2, requestArgs);
                    this.mWorkspace.removeExtraEmptyScreenDelayed(true, new Runnable() {
                        public void run() {
                            Launcher.this.getStateManager().goToState(LauncherState.NORMAL);
                        }
                    }, 500, false);
                } else if (i != 13 && i != 12) {
                    if (i == 1) {
                        if (i2 == -1 && requestArgs.container != -1) {
                            completeAdd(i, intent, -1, requestArgs);
                            this.mWorkspace.removeExtraEmptyScreenDelayed(true, exitSpringLoaded, 500, false);
                        } else if (i2 == 0) {
                            this.mWorkspace.removeExtraEmptyScreenDelayed(true, exitSpringLoaded, 500, false);
                        }
                    }
                    this.mDragLayer.clearAnimatedView();
                } else if (i2 == -1) {
                    completeAdd(i, intent, pendingAddWidgetId, requestArgs);
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        handleActivityResult(requestCode, resultCode, data);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PendingRequestArgs pendingArgs = this.mPendingRequestArgs;
        if (requestCode == 14 && pendingArgs != null && pendingArgs.getRequestCode() == 14) {
            setWaitingForResult((PendingRequestArgs) null);
            View v = null;
            CellLayout layout = getCellLayout(pendingArgs.container, pendingArgs.screenId);
            if (layout != null) {
                v = layout.getChildAt(pendingArgs.cellX, pendingArgs.cellY);
            }
            Intent intent = pendingArgs.getPendingIntent();
            if (grantResults.length <= 0 || grantResults[0] != 0) {
                Toast.makeText(this, getString(R.string.msg_no_phone_permission, new Object[]{getString(R.string.derived_app_name)}), 0).show();
            } else {
                startActivitySafely(v, intent, (ItemInfo) null);
            }
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private long ensurePendingDropLayoutExists(long screenId) {
        if (this.mWorkspace.getScreenWithId(screenId) != null) {
            return screenId;
        }
        this.mWorkspace.addExtraEmptyScreen();
        return this.mWorkspace.commitExtraEmptyScreen();
    }

    /* access modifiers changed from: package-private */
    public void completeTwoStageWidgetDrop(int resultCode, final int appWidgetId, final PendingRequestArgs requestArgs) {
        CellLayout cellLayout = this.mWorkspace.getScreenWithId(requestArgs.screenId);
        Runnable onCompleteRunnable = null;
        int animationType = 0;
        AppWidgetHostView boundWidget = null;
        if (resultCode == -1) {
            animationType = 3;
            final AppWidgetHostView layout = this.mAppWidgetHost.createView(this, appWidgetId, requestArgs.getWidgetHandler().getProviderInfo(this));
            boundWidget = layout;
            onCompleteRunnable = new Runnable() {
                public void run() {
                    Launcher.this.completeAddAppWidget(appWidgetId, requestArgs, layout, (LauncherAppWidgetProviderInfo) null);
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL, 500);
                }
            };
        } else if (resultCode == 0) {
            this.mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            animationType = 4;
        }
        AppWidgetHostView boundWidget2 = boundWidget;
        if (this.mDragLayer.getAnimatedView() != null) {
            this.mWorkspace.animateWidgetDrop(requestArgs, cellLayout, (DragView) this.mDragLayer.getAnimatedView(), onCompleteRunnable, animationType, boundWidget2, true);
        } else if (onCompleteRunnable != null) {
            onCompleteRunnable.run();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        FirstFrameAnimatorHelper.setIsVisible(false);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onStop();
        }
        getUserEventDispatcher().logActionCommand(5, this.mStateManager.getState().containerType, -1);
        this.mAppWidgetHost.setListenIfResumed(false);
        NotificationListener.removeNotificationsChangedListener();
        getStateManager().moveToRestState();
        UiFactory.onLauncherStateOrResumeChanged(this);
        onTrimMemory(20);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        FirstFrameAnimatorHelper.setIsVisible(true);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onStart();
        }
        this.mAppWidgetHost.setListenIfResumed(true);
        NotificationListener.setNotificationsChangedListener(this.mPopupDataProvider);
        UiFactory.onStart(this);
    }

    /* access modifiers changed from: private */
    public void logOnDelayedResume() {
        if (hasBeenResumed()) {
            getUserEventDispatcher().logActionCommand(7, this.mStateManager.getState().containerType, -1);
            getUserEventDispatcher().startSession();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        TraceHelper.beginSection("ON_RESUME");
        super.onResume();
        Log.i(TAG, "onResume: ");
        if (this.mSysProviderOpt != null) {
            this.mSysProviderOpt.updateRecord("ZXW_LAUNCHER_IS_RESUME", "1");
        }
        this.bIsResume = true;
        if (this.mMainHandler != null) {
            this.mMainHandler.sendEmptyMessageDelayed(1, 500);
        }
        TraceHelper.partitionSection("ON_RESUME", "superCall");
        this.mHandler.removeCallbacks(this.mLogOnDelayedResume);
        Utilities.postAsyncCallback(this.mHandler, this.mLogOnDelayedResume);
        setOnResumeCallback((OnResumeCallback) null);
        InstallShortcutReceiver.disableAndFlushInstallQueue(1, this);
        this.mModel.refreshShortcutsIfRequired();
        DiscoveryBounce.showForHomeIfNeeded(this);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onResume();
        }
        UiFactory.onLauncherStateOrResumeChanged(this);
        TraceHelper.endSection("ON_RESUME");
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        InstallShortcutReceiver.enableInstallQueue(1);
        super.onPause();
        Log.i(TAG, "onPause: ");
        if (this.mSysProviderOpt != null) {
            this.mSysProviderOpt.updateRecord("ZXW_LAUNCHER_IS_RESUME", "0");
        }
        this.bIsResume = false;
        this.mDragController.cancelDrag();
        this.mDragController.resetLastGestureUpTime();
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        super.onUserLeaveHint();
        UiFactory.onLauncherStateOrResumeChanged(this);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.mStateManager.onWindowFocusChanged();
    }

    @SuppressLint({"ResourceType"})
    private void fragmentManager(int resid, Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
        Log.i(TAG, "fragmentManager: showFragment = " + this.showFragment);
        if (this.showFragment != null) {
            fragmentTransaction.hide(this.showFragment);
        }
        Fragment mFragment = this.fragmentManager.findFragmentByTag(tag);
        if (mFragment != null) {
            fragmentTransaction.show(mFragment);
        } else {
            mFragment = fragment;
            fragmentTransaction.replace(resid, mFragment, tag);
        }
        this.showFragment = mFragment;
        fragmentTransaction.commitAllowingStateLoss();
    }

    class LauncherOverlayCallbacksImpl implements LauncherOverlayCallbacks {
        LauncherOverlayCallbacksImpl() {
        }

        public void onScrollChanged(float progress) {
            if (Launcher.this.mWorkspace != null) {
                Launcher.this.mWorkspace.onOverlayScrollChanged(progress);
            }
        }
    }

    public boolean hasSettings() {
        if (this.mLauncherCallbacks != null) {
            return this.mLauncherCallbacks.hasSettings();
        }
        return Utilities.ATLEAST_OREO || !getResources().getBoolean(R.bool.allow_rotation);
    }

    public boolean isInState(LauncherState state) {
        return this.mStateManager.getState() == state;
    }

    private void restoreState(Bundle savedState) {
        if (savedState != null) {
            LauncherState state = LauncherState.values()[savedState.getInt(RUNTIME_STATE, LauncherState.NORMAL.ordinal)];
            if (!state.disableRestore) {
                this.mStateManager.goToState(state, false);
            }
            PendingRequestArgs requestArgs = (PendingRequestArgs) savedState.getParcelable(RUNTIME_STATE_PENDING_REQUEST_ARGS);
            if (requestArgs != null) {
                setWaitingForResult(requestArgs);
            }
            this.mPendingActivityResult = (ActivityResultInfo) savedState.getParcelable(RUNTIME_STATE_PENDING_ACTIVITY_RESULT);
            SparseArray<Parcelable> widgetsState = savedState.getSparseParcelableArray(RUNTIME_STATE_WIDGET_PANEL);
            if (widgetsState != null) {
                WidgetsFullSheet.show(this, false).restoreHierarchyState(widgetsState);
            }
        }
    }

    private void setupViews() {
        this.mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
        this.mFocusHandler = this.mDragLayer.getFocusIndicatorHelper();
        this.mWorkspace = (Workspace) this.mDragLayer.findViewById(R.id.workspace);
        this.mWorkspace.initParentViews(this.mDragLayer);
        this.mOverviewPanel = findViewById(R.id.overview_panel);
        this.mHotseat = (Hotseat) findViewById(R.id.hotseat);
        this.mHotseatSearchBox = findViewById(R.id.search_container_hotseat);
        this.mLauncherView.setSystemUiVisibility(1792);
        this.mDragLayer.setup(this.mDragController, this.mWorkspace);
        DragLayer dragLayer = this.mDragLayer;
        dragLayer.getClass();
        UiFactory.setOnTouchControllersChangedListener(this, new Runnable() {
            public final void run() {
                DragLayer.this.recreateControllers();
            }
        });
        this.mWorkspace.setup(this.mDragController);
        this.mWorkspace.lockWallpaperToDefaultPage();
        this.mWorkspace.bindAndInitFirstWorkspaceScreen((View) null);
        this.mDragController.addDragListener(this.mWorkspace);
        this.mDropTargetBar = (DropTargetBar) this.mDragLayer.findViewById(R.id.drop_target_bar);
        this.mAppsView = (AllAppsContainerView) findViewById(R.id.apps_view);
        this.mDragController.setMoveTarget(this.mWorkspace);
        this.mDropTargetBar.setup(this.mDragController);
        this.mAllAppsController.setupViews(this.mAppsView);
    }

    /* access modifiers changed from: package-private */
    public View createShortcut(ShortcutInfo info) {
        return createShortcut((ViewGroup) this.mWorkspace.getChildAt(this.mWorkspace.getCurrentPage()), info);
    }

    public View createShortcut(ViewGroup parent, ShortcutInfo info) {
        BubbleTextView favorite = (BubbleTextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.app_icon, parent, false);
        favorite.applyFromShortcutInfo(info);
        favorite.setOnClickListener(ItemClickHandler.INSTANCE);
        favorite.setOnFocusChangeListener(this.mFocusHandler);
        return favorite;
    }

    private void completeAddShortcut(Intent data, long container, long screenId, int cellX, int cellY, PendingRequestArgs args) {
        View view;
        boolean foundCellSpan;
        int[] cellXY;
        CellLayout layout;
        long j = container;
        PendingRequestArgs pendingRequestArgs = args;
        if (args.getRequestCode() == 1 && args.getPendingIntent().getComponent() != null) {
            int[] cellXY2 = this.mTmpAddItemCellCoordinates;
            CellLayout layout2 = getCellLayout(j, screenId);
            ShortcutInfo info = null;
            if (Utilities.ATLEAST_OREO) {
                info = LauncherAppsCompatVO.createShortcutInfoFromPinItemRequest(this, LauncherAppsCompatVO.getPinItemRequest(data), 0);
            }
            if (info == null) {
                info = Process.myUserHandle().equals(pendingRequestArgs.user) ? InstallShortcutReceiver.fromShortcutIntent(this, data) : null;
                if (info == null) {
                    Log.e(TAG, "Unable to parse a valid custom shortcut result");
                    return;
                } else if (!new PackageManagerHelper(this).hasPermissionForActivity(info.intent, args.getPendingIntent().getComponent().getPackageName())) {
                    Log.e(TAG, "Ignoring malicious intent " + info.intent.toUri(0));
                    return;
                }
            }
            ShortcutInfo info2 = info;
            if (j < 0) {
                View view2 = createShortcut(info2);
                if (cellX < 0 || cellY < 0) {
                    view = view2;
                    layout = layout2;
                    cellXY = cellXY2;
                    foundCellSpan = layout.findCellForSpan(cellXY, 1, 1);
                } else {
                    cellXY2[0] = cellX;
                    cellXY2[1] = cellY;
                    foundCellSpan = true;
                    view = view2;
                    CellLayout layout3 = layout2;
                    int[] cellXY3 = cellXY2;
                    if (!this.mWorkspace.createUserFolderIfNecessary(view2, container, layout2, cellXY2, 0.0f, true, (DragView) null)) {
                        DropTarget.DragObject dragObject = new DropTarget.DragObject();
                        dragObject.dragInfo = info2;
                        if (!this.mWorkspace.addToExistingFolderIfNecessary(view, layout3, cellXY3, 0.0f, dragObject, true)) {
                            layout = layout3;
                            cellXY = cellXY3;
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
                if (!foundCellSpan) {
                    this.mWorkspace.onNoCellFound(layout);
                    return;
                }
                CellLayout cellLayout = layout;
                getModelWriter().addItemToDatabase(info2, container, screenId, cellXY[0], cellXY[1]);
                this.mWorkspace.addInScreen(view, info2);
                return;
            }
            FolderIcon folderIcon = findFolderIcon(j);
            if (folderIcon != null) {
                ((FolderInfo) folderIcon.getTag()).add(info2, pendingRequestArgs.rank, false);
                return;
            }
            Log.e(TAG, "Could not find folder with id " + j + " to add shortcut.");
        }
    }

    public FolderIcon findFolderIcon(final long folderIconId) {
        return (FolderIcon) this.mWorkspace.getFirstMatch(new Workspace.ItemOperator() {
            public boolean evaluate(ItemInfo info, View view) {
                return info != null && info.id == folderIconId;
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void completeAddAppWidget(int appWidgetId, ItemInfo itemInfo, AppWidgetHostView hostView, LauncherAppWidgetProviderInfo appWidgetInfo) {
        if (appWidgetInfo == null) {
            appWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(appWidgetId);
        }
        LauncherAppWidgetInfo launcherInfo = new LauncherAppWidgetInfo(appWidgetId, appWidgetInfo.provider);
        launcherInfo.spanX = itemInfo.spanX;
        launcherInfo.spanY = itemInfo.spanY;
        launcherInfo.minSpanX = itemInfo.minSpanX;
        launcherInfo.minSpanY = itemInfo.minSpanY;
        launcherInfo.user = appWidgetInfo.getProfile();
        getModelWriter().addItemToDatabase(launcherInfo, itemInfo.container, itemInfo.screenId, itemInfo.cellX, itemInfo.cellY);
        if (hostView == null) {
            hostView = this.mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        }
        hostView.setVisibility(0);
        prepareAppWidget(hostView, launcherInfo);
        this.mWorkspace.addInScreen(hostView, launcherInfo);
    }

    private void prepareAppWidget(AppWidgetHostView hostView, LauncherAppWidgetInfo item) {
        hostView.setTag(item);
        item.onBindAppWidget(this, hostView);
        hostView.setFocusable(true);
        hostView.setOnFocusChangeListener(this.mFocusHandler);
    }

    public void updateIconBadges(Set<PackageUserKey> updatedBadges) {
        this.mWorkspace.updateIconBadges(updatedBadges);
        this.mAppsView.getAppsStore().updateIconBadges(updatedBadges);
        PopupContainerWithArrow popup = PopupContainerWithArrow.getOpen(this);
        if (popup != null) {
            popup.updateNotificationHeader(updatedBadges);
        }
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FirstFrameAnimatorHelper.initializeDrawListener(getWindow().getDecorView());
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onAttachedToWindow();
        }
    }

    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onDetachedFromWindow();
        }
    }

    public AllAppsTransitionController getAllAppsController() {
        return this.mAllAppsController;
    }

    public LauncherRootView getRootView() {
        return (LauncherRootView) this.mLauncherView;
    }

    public DragLayer getDragLayer() {
        return this.mDragLayer;
    }

    public AllAppsContainerView getAppsView() {
        return this.mAppsView;
    }

    public Workspace getWorkspace() {
        return this.mWorkspace;
    }

    public Hotseat getHotseat() {
        return this.mHotseat;
    }

    public View getHotseatSearchBox() {
        return this.mHotseatSearchBox;
    }

    public <T extends View> T getOverviewPanel() {
        return this.mOverviewPanel;
    }

    public DropTargetBar getDropTargetBar() {
        return this.mDropTargetBar;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return this.mAppWidgetHost;
    }

    public LauncherModel getModel() {
        return this.mModel;
    }

    public ModelWriter getModelWriter() {
        return this.mModelWriter;
    }

    public SharedPreferences getSharedPrefs() {
        return this.mSharedPrefs;
    }

    public int getOrientation() {
        return this.mOldConfig.orientation;
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        TraceHelper.beginSection("NEW_INTENT");
        super.onNewIntent(intent);
        boolean alreadyOnHome = hasWindowFocus() && (intent.getFlags() & 4194304) != 4194304;
        boolean shouldMoveToDefaultScreen = alreadyOnHome && isInState(LauncherState.NORMAL) && AbstractFloatingView.getTopOpenView(this) == null;
        boolean isActionMain = "android.intent.action.MAIN".equals(intent.getAction());
        boolean internalStateHandled = InternalStateHandler.handleNewIntent(this, intent, isStarted());
        if (isActionMain) {
            if (!internalStateHandled) {
                UserEventDispatcher ued = getUserEventDispatcher();
                AbstractFloatingView topOpenView = AbstractFloatingView.getTopOpenView(this);
                if (topOpenView != null) {
                    topOpenView.logActionCommand(0);
                } else if (alreadyOnHome) {
                    LauncherLogProto.Target target = LoggerUtils.newContainerTarget(this.mStateManager.getState().containerType);
                    target.pageIndex = this.mWorkspace.getCurrentPage();
                    ued.logActionCommand(0, target, LoggerUtils.newContainerTarget(1));
                }
                AbstractFloatingView.closeAllOpenViews(this, isStarted());
                if (!isInState(LauncherState.NORMAL)) {
                    this.mStateManager.goToState(LauncherState.NORMAL);
                }
                if (!alreadyOnHome) {
                    this.mAppsView.reset(isStarted());
                }
                if (shouldMoveToDefaultScreen && !this.mWorkspace.isTouchActive()) {
                    Workspace workspace = this.mWorkspace;
                    Workspace workspace2 = this.mWorkspace;
                    workspace2.getClass();
                    workspace.post(new Runnable() {
                        public final void run() {
                            Workspace.this.moveToDefaultScreen();
                        }
                    });
                }
            }
            View v = getWindow().peekDecorView();
            if (!(v == null || v.getWindowToken() == null)) {
                UiThreadHelper.hideKeyboardAsync(this, v.getWindowToken());
            }
            if (this.mLauncherCallbacks != null) {
                this.mLauncherCallbacks.onHomeIntent(internalStateHandled);
            }
        }
        TraceHelper.endSection("NEW_INTENT");
    }

    public void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        this.mWorkspace.restoreInstanceStateForChild(this.mSynchronouslyBoundPage);
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        if (this.mWorkspace.getChildCount() > 0) {
            outState.putInt(RUNTIME_STATE_CURRENT_SCREEN, this.mWorkspace.getNextPage());
        }
        outState.putInt(RUNTIME_STATE, this.mStateManager.getState().ordinal);
        AbstractFloatingView widgets = AbstractFloatingView.getOpenView(this, 16);
        if (widgets != null) {
            SparseArray<Parcelable> widgetsState = new SparseArray<>();
            widgets.saveHierarchyState(widgetsState);
            outState.putSparseParcelableArray(RUNTIME_STATE_WIDGET_PANEL, widgetsState);
        } else {
            outState.remove(RUNTIME_STATE_WIDGET_PANEL);
        }
        AbstractFloatingView.closeAllOpenViews(this, false);
        if (this.mPendingRequestArgs != null) {
            outState.putParcelable(RUNTIME_STATE_PENDING_REQUEST_ARGS, this.mPendingRequestArgs);
        }
        if (this.mPendingActivityResult != null) {
            outState.putParcelable(RUNTIME_STATE_PENDING_ACTIVITY_RESULT, this.mPendingActivityResult);
        }
        super.onSaveInstanceState(outState);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onSaveInstanceState(outState);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mScreenOffReceiver);
        unregisterReceiver();
        this.mWorkspace.removeFolderListeners();
        UiFactory.setOnTouchControllersChangedListener(this, (Runnable) null);
        if (this.mModel.isCurrentCallbacks(this)) {
            this.mModel.stopLoader();
            LauncherAppState.getInstance(this).setLauncher((Launcher) null);
        }
        this.mRotationHelper.destroy();
        try {
            this.mAppWidgetHost.stopListening();
        } catch (NullPointerException ex) {
            Log.w(TAG, "problem while stopping AppWidgetHost during Launcher destruction", ex);
        }
        TextKeyListener.getInstance().release();
        LauncherAnimUtils.onDestroyActivity();
        clearPendingBinds();
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onDestroy();
        }
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public DragController getDragController() {
        return this.mDragController;
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        super.startActivityForResult(intent, requestCode, options);
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) {
        try {
            super.startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } catch (IntentSender.SendIntentException e) {
            throw new ActivityNotFoundException();
        }
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        if (appSearchData == null) {
            appSearchData = new Bundle();
            appSearchData.putString("source", "launcher-search");
        }
        if (this.mLauncherCallbacks == null || !this.mLauncherCallbacks.startSearch(initialQuery, selectInitialQuery, appSearchData)) {
            super.startSearch(initialQuery, selectInitialQuery, appSearchData, true);
        }
        this.mStateManager.goToState(LauncherState.NORMAL);
    }

    public boolean isWorkspaceLocked() {
        return this.mWorkspaceLoading || this.mPendingRequestArgs != null;
    }

    public boolean isWorkspaceLoading() {
        return this.mWorkspaceLoading;
    }

    private void setWorkspaceLoading(boolean value) {
        this.mWorkspaceLoading = value;
    }

    public void setWaitingForResult(PendingRequestArgs args) {
        this.mPendingRequestArgs = args;
    }

    /* access modifiers changed from: package-private */
    public void addAppWidgetFromDropImpl(int appWidgetId, ItemInfo info, AppWidgetHostView boundWidget, WidgetAddFlowHandler addFlowHandler) {
        addAppWidgetImpl(appWidgetId, info, boundWidget, addFlowHandler, 0);
    }

    /* access modifiers changed from: package-private */
    public void addAppWidgetImpl(int appWidgetId, ItemInfo info, AppWidgetHostView boundWidget, WidgetAddFlowHandler addFlowHandler, int delay) {
        if (!addFlowHandler.startConfigActivity(this, appWidgetId, info, 5)) {
            Runnable onComplete = new Runnable() {
                public void run() {
                    Launcher.this.mStateManager.goToState(LauncherState.NORMAL, 500);
                }
            };
            completeAddAppWidget(appWidgetId, info, boundWidget, addFlowHandler.getProviderInfo(this));
            this.mWorkspace.removeExtraEmptyScreenDelayed(true, onComplete, delay, false);
        }
    }

    public void addPendingItem(PendingAddItemInfo info, long container, long screenId, int[] cell, int spanX, int spanY) {
        info.container = container;
        info.screenId = screenId;
        if (cell != null) {
            info.cellX = cell[0];
            info.cellY = cell[1];
        }
        info.spanX = spanX;
        info.spanY = spanY;
        int i = info.itemType;
        if (i != 1) {
            switch (i) {
                case 4:
                case 5:
                    addAppWidgetFromDrop((PendingAddWidgetInfo) info);
                    return;
                default:
                    throw new IllegalStateException("Unknown item type: " + info.itemType);
            }
        } else {
            processShortcutFromDrop((PendingAddShortcutInfo) info);
        }
    }

    private void processShortcutFromDrop(PendingAddShortcutInfo info) {
        setWaitingForResult(PendingRequestArgs.forIntent(1, new Intent("android.intent.action.CREATE_SHORTCUT").setComponent(info.componentName), info));
        if (!info.activityInfo.startConfigActivity(this, 1)) {
            handleActivityResult(1, 0, (Intent) null);
        }
    }

    private void addAppWidgetFromDrop(PendingAddWidgetInfo info) {
        AppWidgetHostView hostView = info.boundWidget;
        WidgetAddFlowHandler addFlowHandler = info.getHandler();
        if (hostView != null) {
            getDragLayer().removeView(hostView);
            addAppWidgetFromDropImpl(hostView.getAppWidgetId(), info, hostView, addFlowHandler);
            info.boundWidget = null;
            return;
        }
        int appWidgetId = getAppWidgetHost().allocateAppWidgetId();
        if (this.mAppWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, info.info, info.bindOptions)) {
            addAppWidgetFromDropImpl(appWidgetId, info, (AppWidgetHostView) null, addFlowHandler);
        } else {
            addFlowHandler.startBindFlow(this, appWidgetId, info, 11);
        }
    }

    /* access modifiers changed from: package-private */
    public FolderIcon addFolder(CellLayout layout, long container, long screenId, int cellX, int cellY) {
        FolderInfo folderInfo = new FolderInfo();
        folderInfo.title = getText(R.string.folder_name);
        getModelWriter().addItemToDatabase(folderInfo, container, screenId, cellX, cellY);
        CellLayout cellLayout = layout;
        FolderIcon newFolder = FolderIcon.fromXml(R.layout.folder_icon, this, layout, folderInfo);
        this.mWorkspace.addInScreen(newFolder, folderInfo);
        this.mWorkspace.getParentCellLayoutForView(newFolder).getShortcutsAndWidgets().measureChild(newFolder);
        return newFolder;
    }

    public boolean removeItem(View v, ItemInfo itemInfo, boolean deleteFromDb) {
        if (itemInfo instanceof ShortcutInfo) {
            View folderIcon = this.mWorkspace.getHomescreenIconByItemId(itemInfo.container);
            if (folderIcon instanceof FolderIcon) {
                ((FolderInfo) folderIcon.getTag()).remove((ShortcutInfo) itemInfo, true);
            } else {
                this.mWorkspace.removeWorkspaceItem(v);
            }
            if (deleteFromDb) {
                getModelWriter().deleteItemFromDatabase(itemInfo);
            }
        } else if (itemInfo instanceof FolderInfo) {
            FolderInfo folderInfo = (FolderInfo) itemInfo;
            if (v instanceof FolderIcon) {
                ((FolderIcon) v).removeListeners();
            }
            this.mWorkspace.removeWorkspaceItem(v);
            if (deleteFromDb) {
                getModelWriter().deleteFolderAndContentsFromDatabase(folderInfo);
            }
        } else if (!(itemInfo instanceof LauncherAppWidgetInfo)) {
            return false;
        } else {
            LauncherAppWidgetInfo widgetInfo = (LauncherAppWidgetInfo) itemInfo;
            this.mWorkspace.removeWorkspaceItem(v);
            if (deleteFromDb) {
                deleteWidgetInfo(widgetInfo);
            }
        }
        return true;
    }

    private void deleteWidgetInfo(final LauncherAppWidgetInfo widgetInfo) {
        final LauncherAppWidgetHost appWidgetHost = getAppWidgetHost();
        if (appWidgetHost != null && !widgetInfo.isCustomWidget() && widgetInfo.isWidgetIdAllocated()) {
            new AsyncTask<Void, Void, Void>() {
                public Void doInBackground(Void... args) {
                    appWidgetHost.deleteAppWidgetId(widgetInfo.appWidgetId);
                    return null;
                }
            }.executeOnExecutor(Utilities.THREAD_POOL_EXECUTOR, new Void[0]);
        }
        getModelWriter().deleteItemFromDatabase(widgetInfo);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        return event.getKeyCode() == 3 || super.dispatchKeyEvent(event);
    }

    public void onBackPressed() {
        if (!finishAutoCancelActionMode()) {
            if (this.mLauncherCallbacks != null && this.mLauncherCallbacks.handleBackPressed()) {
                return;
            }
            if (this.mDragController.isDragging()) {
                this.mDragController.cancelDrag();
                return;
            }
            UserEventDispatcher ued = getUserEventDispatcher();
            AbstractFloatingView topView = AbstractFloatingView.getTopOpenView(this);
            if (topView != null && topView.onBackPressed()) {
                return;
            }
            if (!isInState(LauncherState.NORMAL)) {
                LauncherState lastState = this.mStateManager.getLastState();
                ued.logActionCommand(1, this.mStateManager.getState().containerType, lastState.containerType);
                this.mStateManager.goToState(lastState);
                return;
            }
            this.mWorkspace.showOutlinesTemporarily();
        }
    }

    @TargetApi(23)
    public ActivityOptions getActivityLaunchOptions(View v) {
        return this.mAppTransitionManager.getActivityLaunchOptions(this, v);
    }

    public LauncherAppTransitionManager getAppTransitionManager() {
        return this.mAppTransitionManager;
    }

    /* access modifiers changed from: protected */
    @TargetApi(23)
    public boolean onErrorStartingShortcut(Intent intent, ItemInfo info) {
        if (intent.getComponent() != null || !"android.intent.action.CALL".equals(intent.getAction()) || checkSelfPermission("android.permission.CALL_PHONE") == 0) {
            return false;
        }
        setWaitingForResult(PendingRequestArgs.forIntent(14, intent, info));
        requestPermissions(new String[]{"android.permission.CALL_PHONE"}, 14);
        return true;
    }

    public void modifyUserEvent(LauncherLogProto.LauncherEvent event) {
        if (event.srcTarget != null && event.srcTarget.length > 0 && event.srcTarget[1].containerType == 7) {
            event.srcTarget = new LauncherLogProto.Target[]{event.srcTarget[0], event.srcTarget[1], LoggerUtils.newTarget(3)};
            LauncherState state = this.mStateManager.getState();
            if (state == LauncherState.ALL_APPS) {
                event.srcTarget[2].containerType = 4;
            } else if (state == LauncherState.OVERVIEW) {
                event.srcTarget[2].containerType = 12;
            }
        }
    }

    public boolean startActivitySafely(View v, Intent intent, ItemInfo item) {
        boolean success = super.startActivitySafely(v, intent, item);
        if (success && (v instanceof BubbleTextView)) {
            BubbleTextView btv = (BubbleTextView) v;
            btv.setStayPressed(true);
            setOnResumeCallback(btv);
        }
        return success;
    }

    /* access modifiers changed from: package-private */
    public boolean isHotseatLayout(View layout) {
        return this.mHotseat != null && layout != null && (layout instanceof CellLayout) && layout == this.mHotseat.getLayout();
    }

    public CellLayout getCellLayout(long container, long screenId) {
        if (container != -101) {
            return this.mWorkspace.getScreenWithId(screenId);
        }
        if (this.mHotseat != null) {
            return this.mHotseat.getLayout();
        }
        return null;
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= 20) {
            SQLiteDatabase.releaseMemory();
        }
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.onTrimMemory(level);
        }
        UiFactory.onTrimMemory(this, level);
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        String str;
        boolean result = super.dispatchPopulateAccessibilityEvent(event);
        List<CharSequence> text = event.getText();
        text.clear();
        if (this.mWorkspace == null) {
            str = getString(R.string.all_apps_home_button_label);
        } else {
            str = this.mStateManager.getState().getDescription(this);
        }
        text.add(str);
        return result;
    }

    public void setOnResumeCallback(OnResumeCallback callback) {
        if (this.mOnResumeCallback != null) {
            this.mOnResumeCallback.onLauncherResume();
        }
        this.mOnResumeCallback = callback;
    }

    public int getCurrentWorkspaceScreen() {
        if (this.mWorkspace != null) {
            return this.mWorkspace.getCurrentPage();
        }
        return 0;
    }

    public void clearPendingBinds() {
        if (this.mPendingExecutor != null) {
            this.mPendingExecutor.markCompleted();
            this.mPendingExecutor = null;
        }
    }

    public void startBinding() {
        TraceHelper.beginSection("startBinding");
        AbstractFloatingView.closeOpenViews(this, true, 399);
        setWorkspaceLoading(true);
        this.mWorkspace.clearDropTargets();
        this.mWorkspace.removeAllWorkspaceScreens();
        this.mAppWidgetHost.clearViews();
        if (this.mHotseat != null) {
            this.mHotseat.resetLayout(this.mDeviceProfile.isVerticalBarLayout());
        }
        TraceHelper.endSection("startBinding");
    }

    public void bindScreens(ArrayList<Long> orderedScreenIds) {
        if (orderedScreenIds.indexOf(0L) != 0) {
            orderedScreenIds.remove(0L);
            orderedScreenIds.add(0, 0L);
            LauncherModel.updateWorkspaceScreenOrder(this, orderedScreenIds);
        }
        bindAddScreens(orderedScreenIds);
        this.mWorkspace.unlockWallpaperFromDefaultPageOnNextLayout();
    }

    private void bindAddScreens(ArrayList<Long> orderedScreenIds) {
        int count = orderedScreenIds.size();
        for (int i = 0; i < count; i++) {
            long screenId = orderedScreenIds.get(i).longValue();
            if (screenId != 0) {
                this.mWorkspace.insertNewWorkspaceScreenBeforeEmptyScreen(screenId);
            }
        }
    }

    public void bindAppsAdded(ArrayList<Long> newScreens, ArrayList<ItemInfo> addNotAnimated, ArrayList<ItemInfo> addAnimated) {
        if (newScreens != null) {
            bindAddScreens(newScreens);
        }
        if (addNotAnimated != null && !addNotAnimated.isEmpty()) {
            bindItems(addNotAnimated, false);
        }
        if (addAnimated != null && !addAnimated.isEmpty()) {
            bindItems(addAnimated, true);
        }
        this.mWorkspace.removeExtraEmptyScreen(false, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0051, code lost:
        if (r12 == null) goto L_0x0037;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0078, code lost:
        if (r11.container != -100) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007a, code lost:
        r13 = r0.mWorkspace.getScreenWithId(r11.screenId);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0082, code lost:
        if (r13 == null) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x008c, code lost:
        if (r13.isOccupied(r11.cellX, r11.cellY) == false) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x008e, code lost:
        r15 = r13.getChildAt(r11.cellX, r11.cellY).getTag();
        r4 = new java.lang.StringBuilder();
        r17 = r9;
        r4.append("Collision while binding workspace item: ");
        r4.append(r11);
        r4.append(". Collides with ");
        r4.append(r15);
        android.util.Log.d(TAG, r4.toString());
        getModelWriter().deleteItemFromDatabase(r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00c2, code lost:
        r17 = r9;
        r6.addInScreenFromBind(r12, r11);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00c7, code lost:
        if (r5 == false) goto L_0x00dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00c9, code lost:
        r12.setAlpha(0.0f);
        r12.setScaleX(0.0f);
        r12.setScaleY(0.0f);
        r2.add(createNewAppBounceAnimation(r12, r3));
        r7 = r11.screenId;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bindItems(java.util.List<com.android.launcher3.ItemInfo> r19, boolean r20) {
        /*
            r18 = this;
            r0 = r18
            android.animation.AnimatorSet r1 = com.android.launcher3.LauncherAnimUtils.createAnimatorSet()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r3 = 0
            if (r20 == 0) goto L_0x0016
            boolean r5 = r18.canRunNewAppsAnimation()
            if (r5 == 0) goto L_0x0016
            r5 = 1
            goto L_0x0017
        L_0x0016:
            r5 = 0
        L_0x0017:
            com.android.launcher3.Workspace r6 = r0.mWorkspace
            r7 = -1
            int r9 = r19.size()
        L_0x0020:
            if (r3 >= r9) goto L_0x00e2
            r10 = r19
            java.lang.Object r11 = r10.get(r3)
            com.android.launcher3.ItemInfo r11 = (com.android.launcher3.ItemInfo) r11
            long r12 = r11.container
            r14 = -101(0xffffffffffffff9b, double:NaN)
            int r12 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r12 != 0) goto L_0x003b
            com.android.launcher3.Hotseat r12 = r0.mHotseat
            if (r12 != 0) goto L_0x003b
        L_0x0037:
            r17 = r9
            goto L_0x00dc
        L_0x003b:
            int r12 = r11.itemType
            switch(r12) {
                case 0: goto L_0x0069;
                case 1: goto L_0x0069;
                case 2: goto L_0x0054;
                case 3: goto L_0x0040;
                case 4: goto L_0x004a;
                case 5: goto L_0x004a;
                case 6: goto L_0x0069;
                default: goto L_0x0040;
            }
        L_0x0040:
            r17 = r9
            java.lang.RuntimeException r4 = new java.lang.RuntimeException
            java.lang.String r9 = "Invalid Item Type"
            r4.<init>(r9)
            throw r4
        L_0x004a:
            r12 = r11
            com.android.launcher3.LauncherAppWidgetInfo r12 = (com.android.launcher3.LauncherAppWidgetInfo) r12
            android.view.View r12 = r0.inflateAppWidget(r12)
            if (r12 != 0) goto L_0x0072
            goto L_0x0037
        L_0x0054:
            r12 = 2131623989(0x7f0e0035, float:1.8875145E38)
            int r13 = r6.getCurrentPage()
            android.view.View r13 = r6.getChildAt(r13)
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            r14 = r11
            com.android.launcher3.FolderInfo r14 = (com.android.launcher3.FolderInfo) r14
            com.android.launcher3.folder.FolderIcon r12 = com.android.launcher3.folder.FolderIcon.fromXml(r12, r0, r13, r14)
            goto L_0x0072
        L_0x0069:
            r12 = r11
            com.android.launcher3.ShortcutInfo r12 = (com.android.launcher3.ShortcutInfo) r12
            android.view.View r13 = r0.createShortcut(r12)
            r12 = r13
        L_0x0072:
            long r13 = r11.container
            r15 = -100
            int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r13 != 0) goto L_0x00c2
            com.android.launcher3.Workspace r13 = r0.mWorkspace
            long r14 = r11.screenId
            com.android.launcher3.CellLayout r13 = r13.getScreenWithId(r14)
            if (r13 == 0) goto L_0x00c2
            int r14 = r11.cellX
            int r15 = r11.cellY
            boolean r14 = r13.isOccupied(r14, r15)
            if (r14 == 0) goto L_0x00c2
            int r14 = r11.cellX
            int r15 = r11.cellY
            android.view.View r14 = r13.getChildAt(r14, r15)
            java.lang.Object r15 = r14.getTag()
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r17 = r9
            java.lang.String r9 = "Collision while binding workspace item: "
            r4.append(r9)
            r4.append(r11)
            java.lang.String r9 = ". Collides with "
            r4.append(r9)
            r4.append(r15)
            java.lang.String r4 = r4.toString()
            java.lang.String r9 = "Launcher"
            android.util.Log.d(r9, r4)
            com.android.launcher3.model.ModelWriter r9 = r18.getModelWriter()
            r9.deleteItemFromDatabase(r11)
            goto L_0x00dc
        L_0x00c2:
            r17 = r9
            r6.addInScreenFromBind(r12, r11)
            if (r5 == 0) goto L_0x00dc
            r4 = 0
            r12.setAlpha(r4)
            r12.setScaleX(r4)
            r12.setScaleY(r4)
            android.animation.ValueAnimator r4 = r0.createNewAppBounceAnimation(r12, r3)
            r2.add(r4)
            long r7 = r11.screenId
        L_0x00dc:
            int r3 = r3 + 1
            r9 = r17
            goto L_0x0020
        L_0x00e2:
            r10 = r19
            r17 = r9
            if (r5 == 0) goto L_0x011b
            r3 = -1
            int r3 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            if (r3 <= 0) goto L_0x011b
            com.android.launcher3.Workspace r3 = r0.mWorkspace
            com.android.launcher3.Workspace r4 = r0.mWorkspace
            int r4 = r4.getNextPage()
            long r3 = r3.getScreenIdForPageIndex(r4)
            com.android.launcher3.Workspace r9 = r0.mWorkspace
            int r9 = r9.getPageIndexForScreenId(r7)
            com.android.launcher3.Launcher$10 r11 = new com.android.launcher3.Launcher$10
            r11.<init>(r1, r2)
            int r12 = (r7 > r3 ? 1 : (r7 == r3 ? 0 : -1))
            r13 = 500(0x1f4, double:2.47E-321)
            if (r12 == 0) goto L_0x0116
            com.android.launcher3.Workspace r12 = r0.mWorkspace
            com.android.launcher3.Launcher$11 r15 = new com.android.launcher3.Launcher$11
            r15.<init>(r9, r11)
            r12.postDelayed(r15, r13)
            goto L_0x011b
        L_0x0116:
            com.android.launcher3.Workspace r12 = r0.mWorkspace
            r12.postDelayed(r11, r13)
        L_0x011b:
            r6.requestLayout()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Launcher.bindItems(java.util.List, boolean):void");
    }

    public void bindAppWidget(LauncherAppWidgetInfo item) {
        View view = inflateAppWidget(item);
        if (view != null) {
            this.mWorkspace.addInScreen(view, item);
            this.mWorkspace.requestLayout();
        }
    }

    private View inflateAppWidget(LauncherAppWidgetInfo item) {
        LauncherAppWidgetProviderInfo appWidgetInfo;
        AppWidgetHostView view;
        if (this.mIsSafeModeEnabled) {
            PendingAppWidgetHostView view2 = new PendingAppWidgetHostView(this, item, this.mIconCache, true);
            prepareAppWidget(view2, item);
            return view2;
        }
        TraceHelper.beginSection("BIND_WIDGET");
        if (item.hasRestoreFlag(2)) {
            appWidgetInfo = null;
        } else if (item.hasRestoreFlag(1)) {
            appWidgetInfo = this.mAppWidgetManager.findProvider(item.providerName, item.user);
        } else {
            appWidgetInfo = this.mAppWidgetManager.getLauncherAppWidgetInfo(item.appWidgetId);
        }
        if (!item.hasRestoreFlag(2) && item.restoreStatus != 0) {
            if (appWidgetInfo == null) {
                Log.d(TAG, "Removing restored widget: id=" + item.appWidgetId + " belongs to component " + item.providerName + ", as the provider is null");
                getModelWriter().deleteItemFromDatabase(item);
                return null;
            }
            int i = 4;
            if (item.hasRestoreFlag(1)) {
                if (!item.hasRestoreFlag(16)) {
                    item.appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
                    item.restoreStatus = 16 | item.restoreStatus;
                    PendingAddWidgetInfo pendingInfo = new PendingAddWidgetInfo(appWidgetInfo);
                    pendingInfo.spanX = item.spanX;
                    pendingInfo.spanY = item.spanY;
                    pendingInfo.minSpanX = item.minSpanX;
                    pendingInfo.minSpanY = item.minSpanY;
                    Bundle options = WidgetHostViewLoader.getDefaultOptionsForWidget(this, pendingInfo);
                    boolean isDirectConfig = item.hasRestoreFlag(32);
                    if (isDirectConfig && item.bindOptions != null) {
                        Bundle newOptions = item.bindOptions.getExtras();
                        if (options != null) {
                            newOptions.putAll(options);
                        }
                        options = newOptions;
                    }
                    boolean success = this.mAppWidgetManager.bindAppWidgetIdIfAllowed(item.appWidgetId, appWidgetInfo, options);
                    item.bindOptions = null;
                    item.restoreStatus &= -33;
                    if (success) {
                        if (appWidgetInfo.configure == null || isDirectConfig) {
                            i = 0;
                        }
                        item.restoreStatus = i;
                    }
                    getModelWriter().updateItemInDatabase(item);
                }
            } else if (item.hasRestoreFlag(4) && appWidgetInfo.configure == null) {
                item.restoreStatus = 0;
                getModelWriter().updateItemInDatabase(item);
            }
        }
        if (item.restoreStatus != 0) {
            view = new PendingAppWidgetHostView(this, item, this.mIconCache, false);
        } else if (appWidgetInfo == null) {
            FileLog.e(TAG, "Removing invalid widget: id=" + item.appWidgetId);
            deleteWidgetInfo(item);
            return null;
        } else {
            item.minSpanX = appWidgetInfo.minSpanX;
            item.minSpanY = appWidgetInfo.minSpanY;
            view = this.mAppWidgetHost.createView(this, item.appWidgetId, appWidgetInfo);
        }
        prepareAppWidget(view, item);
        TraceHelper.endSection("BIND_WIDGET", "id=" + item.appWidgetId);
        return view;
    }

    private LauncherAppWidgetInfo completeRestoreAppWidget(int appWidgetId, int finalRestoreFlag) {
        LauncherAppWidgetHostView view = this.mWorkspace.getWidgetForAppWidgetId(appWidgetId);
        if (view == null || !(view instanceof PendingAppWidgetHostView)) {
            Log.e(TAG, "Widget update called, when the widget no longer exists.");
            return null;
        }
        LauncherAppWidgetInfo info = (LauncherAppWidgetInfo) view.getTag();
        info.restoreStatus = finalRestoreFlag;
        if (info.restoreStatus == 0) {
            info.pendingItemInfo = null;
        }
        if (((PendingAppWidgetHostView) view).isReinflateIfNeeded()) {
            view.reInflate();
        }
        getModelWriter().updateItemInDatabase(info);
        return info;
    }

    public void onPageBoundSynchronously(int page) {
        this.mSynchronouslyBoundPage = page;
    }

    public void executeOnNextDraw(ViewOnDrawExecutor executor) {
        if (this.mPendingExecutor != null) {
            this.mPendingExecutor.markCompleted();
        }
        this.mPendingExecutor = executor;
        if (!isInState(LauncherState.ALL_APPS)) {
            this.mAppsView.getAppsStore().setDeferUpdates(true);
            this.mPendingExecutor.execute(new Runnable() {
                public final void run() {
                    Launcher.this.mAppsView.getAppsStore().setDeferUpdates(false);
                }
            });
        }
        executor.attachTo(this);
    }

    public void clearPendingExecutor(ViewOnDrawExecutor executor) {
        if (this.mPendingExecutor == executor) {
            this.mPendingExecutor = null;
        }
    }

    public void finishFirstPageBind(final ViewOnDrawExecutor executor) {
        MultiValueAlpha.AlphaProperty property = this.mDragLayer.getAlphaProperty(1);
        if (property.getValue() < 1.0f) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(property, MultiValueAlpha.VALUE, new float[]{1.0f});
            if (executor != null) {
                anim.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        executor.onLoadAnimationCompleted();
                    }
                });
            }
            anim.start();
        } else if (executor != null) {
            executor.onLoadAnimationCompleted();
        }
    }

    public void finishBindingItems() {
        TraceHelper.beginSection("finishBindingItems");
        this.mWorkspace.restoreInstanceStateForRemainingPages();
        setWorkspaceLoading(false);
        if (this.mPendingActivityResult != null) {
            handleActivityResult(this.mPendingActivityResult.requestCode, this.mPendingActivityResult.resultCode, this.mPendingActivityResult.data);
            this.mPendingActivityResult = null;
        }
        InstallShortcutReceiver.disableAndFlushInstallQueue(2, this);
        TraceHelper.endSection("finishBindingItems");
    }

    private boolean canRunNewAppsAnimation() {
        return System.currentTimeMillis() - this.mDragController.getLastGestureUpTime() > 5000;
    }

    private ValueAnimator createNewAppBounceAnimation(View v, int i) {
        ValueAnimator bounceAnim = LauncherAnimUtils.ofViewAlphaAndScale(v, 1.0f, 1.0f, 1.0f);
        bounceAnim.setDuration(450);
        bounceAnim.setStartDelay((long) (i * 85));
        bounceAnim.setInterpolator(new OvershootInterpolator(BOUNCE_ANIMATION_TENSION));
        return bounceAnim;
    }

    public void bindAllApplications(ArrayList<AppInfo> apps) {
        this.mAppsView.getAppsStore().setApps(apps);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.bindAllApplications(apps);
        }
    }

    public void bindDeepShortcutMap(MultiHashMap<ComponentKey, String> deepShortcutMapCopy) {
        this.mPopupDataProvider.setDeepShortcutMap(deepShortcutMapCopy);
    }

    public void bindAppsAddedOrUpdated(ArrayList<AppInfo> apps) {
        this.mAppsView.getAppsStore().addOrUpdateApps(apps);
        new MyScannerAppsTask(this, this.scannerAppsCallback).execute(new Void[0]);
    }

    public void bindPromiseAppProgressUpdated(PromiseAppInfo app) {
        this.mAppsView.getAppsStore().updatePromiseAppProgress(app);
    }

    public void bindWidgetsRestored(ArrayList<LauncherAppWidgetInfo> widgets) {
        this.mWorkspace.widgetsRestored(widgets);
    }

    public void bindShortcutsChanged(ArrayList<ShortcutInfo> updated, UserHandle user) {
        if (!updated.isEmpty()) {
            this.mWorkspace.updateShortcuts(updated);
        }
    }

    public void bindRestoreItemsChange(HashSet<ItemInfo> updates) {
        this.mWorkspace.updateRestoreItems(updates);
    }

    public void bindWorkspaceComponentsRemoved(ItemInfoMatcher matcher) {
        this.mWorkspace.removeItemsByMatcher(matcher);
        this.mDragController.onAppsRemoved(matcher);
    }

    public void bindAppInfosRemoved(ArrayList<AppInfo> appInfos) {
        this.mAppsView.getAppsStore().removeApps(appInfos);
        new MyScannerAppsTask(this, this.scannerAppsCallback).execute(new Void[0]);
    }

    public void bindAllWidgets(ArrayList<WidgetListRowEntry> allWidgets) {
        this.mPopupDataProvider.setAllWidgets(allWidgets);
        AbstractFloatingView topView = AbstractFloatingView.getTopOpenView(this);
        if (topView != null) {
            topView.onWidgetsBound();
        }
    }

    public void refreshAndBindWidgetsForPackageUser(@Nullable PackageUserKey packageUser) {
        this.mModel.refreshAndBindWidgetsAndShortcuts(packageUser);
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        if (args.length > 0) {
            if (TextUtils.equals(args[0], "--all")) {
                writer.println(prefix + "Workspace Items");
                for (int i = 0; i < this.mWorkspace.getPageCount(); i++) {
                    writer.println(prefix + "  Homescreen " + i);
                    ViewGroup layout = ((CellLayout) this.mWorkspace.getPageAt(i)).getShortcutsAndWidgets();
                    for (int j = 0; j < layout.getChildCount(); j++) {
                        Object tag = layout.getChildAt(j).getTag();
                        if (tag != null) {
                            writer.println(prefix + "    " + tag.toString());
                        }
                    }
                }
                writer.println(prefix + "  Hotseat");
                ViewGroup layout2 = this.mHotseat.getLayout().getShortcutsAndWidgets();
                for (int j2 = 0; j2 < layout2.getChildCount(); j2++) {
                    Object tag2 = layout2.getChildAt(j2).getTag();
                    if (tag2 != null) {
                        writer.println(prefix + "    " + tag2.toString());
                    }
                }
            }
        }
        writer.println(prefix + "Misc:");
        writer.print(prefix + "\tmWorkspaceLoading=" + this.mWorkspaceLoading);
        StringBuilder sb = new StringBuilder();
        sb.append(" mPendingRequestArgs=");
        sb.append(this.mPendingRequestArgs);
        writer.print(sb.toString());
        writer.println(" mPendingActivityResult=" + this.mPendingActivityResult);
        writer.println(" mRotationHelper: " + this.mRotationHelper);
        dumpMisc(writer);
        try {
            FileLog.flushAll(writer);
        } catch (Exception e) {
        }
        this.mModel.dumpState(prefix, fd, writer, args);
        if (this.mLauncherCallbacks != null) {
            this.mLauncherCallbacks.dump(prefix, fd, writer, args);
        }
    }

    @TargetApi(24)
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
        ArrayList<KeyboardShortcutInfo> shortcutInfos = new ArrayList<>();
        if (isInState(LauncherState.NORMAL)) {
            shortcutInfos.add(new KeyboardShortcutInfo(getString(R.string.all_apps_button_label), 29, 4096));
            shortcutInfos.add(new KeyboardShortcutInfo(getString(R.string.widget_button_text), 51, 4096));
        }
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            if (new CustomActionsPopup(this, currentFocus).canShow()) {
                shortcutInfos.add(new KeyboardShortcutInfo(getString(R.string.custom_actions), 43, 4096));
            }
            if ((currentFocus.getTag() instanceof ItemInfo) && DeepShortcutManager.supportsShortcuts((ItemInfo) currentFocus.getTag())) {
                shortcutInfos.add(new KeyboardShortcutInfo(getString(R.string.shortcuts_menu_with_notifications_description), 47, 4096));
            }
        }
        if (!shortcutInfos.isEmpty()) {
            data.add(new KeyboardShortcutGroup(getString(R.string.home_screen), shortcutInfos));
        }
        super.onProvideKeyboardShortcuts(data, menu, deviceId);
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if (event.hasModifiers(4096)) {
            if (keyCode != 29) {
                if (keyCode != 43) {
                    if (keyCode == 47) {
                        View focusedView = getCurrentFocus();
                        if ((focusedView instanceof BubbleTextView) && (focusedView.getTag() instanceof ItemInfo) && this.mAccessibilityDelegate.performAction(focusedView, (ItemInfo) focusedView.getTag(), R.id.action_deep_shortcuts)) {
                            PopupContainerWithArrow.getOpen(this).requestFocus();
                            return true;
                        }
                    } else if (keyCode == 51 && isInState(LauncherState.NORMAL)) {
                        OptionsPopupView.openWidgets(this);
                        return true;
                    }
                } else if (new CustomActionsPopup(this, getCurrentFocus()).show()) {
                    return true;
                }
            } else if (isInState(LauncherState.NORMAL)) {
                getStateManager().goToState(LauncherState.ALL_APPS);
                return true;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyUp: keyCode = " + keyCode);
        if (keyCode != 82) {
            return super.onKeyUp(keyCode, event);
        }
        if (this.mDragController.isDragging() || this.mWorkspace.isSwitchingState() || !isInState(LauncherState.NORMAL)) {
            return true;
        }
        AbstractFloatingView.closeAllOpenViews(this);
        OptionsPopupView.showDefaultOptions(this, -1.0f, -1.0f);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: keyCode = " + keyCode);
        if (keyCode == 4) {
            showApps(false);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showApps(boolean bShow) {
        Log.i(TAG, "showApps: bShow = " + bShow);
        this.bShowApps = bShow;
        if (bShow) {
            fragmentManager(R.id.AppsFrameLayout, this.mAppsFragment, "AppsFragment");
        } else if (this.showFragment == null) {
            fragmentManager(R.id.AppsFrameLayout, this.mEmptyFragment, "EmptyFragment");
        } else if (!this.showFragment.isHidden() && !isInMultiWindowMode()) {
            Log.i(TAG, "showApps: f");
            FragmentTransaction fragmentTransaction = this.fragmentManager.beginTransaction();
            fragmentTransaction.hide(this.showFragment);
            fragmentTransaction.commitAllowingStateLoss();
            if (this.mAppsFragment != null) {
                this.mAppsFragment.setiAppsFocusIndex(-1);
            }
        }
    }

    private class MBroadcastReceiver extends BroadcastReceiver {
        final String SYSTEM_DIALOG_REASON_HOME_KEY;
        final String SYSTEM_DIALOG_REASON_KEY;
        final String SYSTEM_DIALOG_REASON_RECENT_APPS;
        AnimatorSet anim;
        PendingAnimation pendingAnimation;

        private MBroadcastReceiver() {
            this.SYSTEM_DIALOG_REASON_KEY = "reason";
            this.SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            this.SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
            this.anim = new AnimatorSet();
            this.pendingAnimation = new PendingAnimation(this.anim);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(action)) {
                String reason = intent.getStringExtra("reason");
                if (reason == null) {
                    return;
                }
                if (reason.equals("homekey")) {
                    Log.i(Launcher.TAG, "onReceive: Home键被监听");
                    Launcher.this.showApps(false);
                } else if (reason.equals("recentapps")) {
                    Log.i(Launcher.TAG, "onReceive: 多任务键被监听");
                }
            } else if ("android.intent.action.TIME_TICK".equals(action)) {
                Launcher.this.updateTimerInfor();
            } else if (EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT.equals(action)) {
                if (Launcher.this.bIsResume) {
                    int iExtra = intent.getIntExtra(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA, -1);
                    Log.i(Launcher.TAG, "onReceive: bShowApps = " + Launcher.this.bShowApps + ", iExtra = " + iExtra);
                    if (Launcher.this.bShowApps || Launcher.this.isInMultiWindowMode()) {
                        if (Launcher.this.mAppsFragment != null) {
                            Launcher.this.mAppsFragment.setAppsFocusMove(iExtra);
                        }
                    } else if (Launcher.this.mMainFragment != null) {
                        Launcher.this.mMainFragment.setMainFocusMove(iExtra);
                    }
                }
            } else if (EventUtils.ZXW_SENDBROADCAST8902MOD.equals(action)) {
                int iExtra2 = intent.getIntExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_EXTRA, -1);
                if (iExtra2 == 16) {
                    boolean bHandbrake = intent.getBooleanExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_shousha, false);
                    boolean bSeatBelt = intent.getBooleanExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_anquandai, false);
                    if (Launcher.this.mMainFragment != null) {
                        Launcher.this.mMainFragment.setHandbrakeStatus(bHandbrake);
                        Launcher.this.mMainFragment.setSeatBeltStatus(bSeatBelt);
                    }
                } else if (iExtra2 != 25) {
                    switch (iExtra2) {
                        case 13:
                            if (Launcher.this.mMainFragment != null) {
                                Launcher.this.mMainFragment.ksw_refresh_A4L_left_show();
                                return;
                            }
                            return;
                        case 14:
                            if (Launcher.this.mMainFragment != null) {
                                Launcher.this.mMainFragment.ksw_refresh_A4L_right_show();
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                } else {
                    int iEngineSpeed = intent.getIntExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_fadongjizhuansu, 0);
                    int iSpeed = intent.getIntExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_ShunShiSuDu, 0);
                    boolean bSpeedUnit = intent.getBooleanExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_SpeedUnit, false);
                    String strTemp = intent.getStringExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_huanjinwendu);
                    String strDrivingRange = intent.getStringExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_xushilicheng);
                    String strOil = intent.getStringExtra(EventUtils.ZXW_SENDBROADCAST8902MOD_youLiang);
                    if (Launcher.this.mMainFragment != null) {
                        Launcher.this.mMainFragment.setRotatingSpeed(iEngineSpeed);
                        Launcher.this.mMainFragment.setSpeed(iSpeed);
                        Launcher.this.mMainFragment.setSpeedUnit(bSpeedUnit);
                        Launcher.this.mMainFragment.setTempValue(strTemp);
                        Launcher.this.mMainFragment.setQilValue(strOil);
                        Launcher.this.mMainFragment.setDrivingRangeValue(strDrivingRange);
                    }
                }
            } else if (EventUtils.VALID_MODE_INFOR_CHANGE.equals(action)) {
                if (Launcher.this.mMainFragment != null) {
                    Launcher.this.mMainFragment.refreshPlayState();
                }
            } else if (EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE.equals(action)) {
                int type = intent.getIntExtra(EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE_EXTRA, 0);
                if (Launcher.this.mMainFragment != null) {
                    Launcher.this.mMainFragment.setMediaTypeBackground(type);
                }
            } else if (EventUtils.HBCP_EVT_HSHF_STATUS.equals(action)) {
                int btStasus = intent.getIntExtra(EventUtils.INTENT_EXTRA_INT_KEYNAME, 0);
                if (Launcher.this.mMainFragment != null) {
                    Launcher.this.mMainFragment.setBtStatus(btStasus);
                }
            } else if (EventUtils.REC_AUTONAVI_STANDARD.equals(action)) {
                int iKey_type = intent.getIntExtra("KEY_TYPE", 0);
                int iExtra_type = intent.getIntExtra("EXTRA_STATE", 0);
                Bundle bundle = intent.getExtras();
                if (Launcher.this.mMainFragment != null) {
                    Launcher.this.mMainFragment.refreshNaviInfo(iKey_type, iExtra_type, bundle);
                }
            } else if (EventUtils.ZXW_CAN_START_UP_APPS.equals(action)) {
                Launcher.this.showApps(true);
            } else if ("com.choiceway.musicplayer.bitmap".equals(action)) {
                Bitmap bitmap = (Bitmap) intent.getParcelableExtra("bitmap");
                Log.i(Launcher.TAG, "onReceive: com.choiceway.musicplayer.bitmap = " + bitmap);
                if (Launcher.this.mMainFragment != null) {
                    Launcher.this.mMainFragment.setMusicCoverBg(bitmap);
                }
            }
        }
    }

    private int test(int speed) {
        this.iTest++;
        switch (this.iTest) {
            case 0:
                return 0;
            case 1:
                return 1000;
            case 2:
                return 2000;
            case 3:
                return PathInterpolatorCompat.MAX_NUM_POINTS;
            case 4:
                return 4000;
            case 5:
                return 5000;
            case 6:
                return 6000;
            case 7:
                return 7000;
            case 8:
                return 8000;
            case 9:
                return 0;
            case 10:
                this.iTest = -1;
                return 1000;
            default:
                return speed;
        }
    }

    private int test2(int speed) {
        this.iTest2++;
        switch (this.iTest2) {
            case 0:
                return 0;
            case 1:
                return 20;
            case 2:
                return 50;
            case 3:
                return 80;
            case 4:
                return 110;
            case 5:
                return 140;
            case 6:
                return 170;
            case 7:
                return 200;
            case 8:
                return 230;
            case 9:
                return 260;
            case 10:
                this.iTest2 = -1;
                return 280;
            default:
                return speed;
        }
    }

    /* access modifiers changed from: private */
    public void updateTimerInfor() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(1);
        int mMonth = c.get(2) + 1;
        int mDay = c.get(5);
        int mWay = c.get(7);
        int mHour = c.get(11);
        int mMin = c.get(12);
        String strTimeFormat = Settings.System.getString(getContentResolver(), "time_12_24");
        Log.i(TAG, "updateTimerInfor: TimeFormat = " + strTimeFormat);
        if (strTimeFormat == null || strTimeFormat.equals("12")) {
            if (mHour > 12) {
                mHour -= 12;
            } else if (mHour == 0) {
                mHour = 12;
            }
        }
        int[] weeklst = {0, R.string.lbl_sun, R.string.lbl_mon, R.string.lbl_tue, R.string.lbl_wed, R.string.lbl_thu, R.string.lbl_fri, R.string.lbl_sat};
        this.mMainFragment.setTvCurDataTimeYMD(String.format("%d-%02d-%02d", new Object[]{Integer.valueOf(mYear), Integer.valueOf(mMonth), Integer.valueOf(mDay)}));
        this.mMainFragment.setTvCurDataTime(String.format("%02d:%02d", new Object[]{Integer.valueOf(mHour), Integer.valueOf(mMin)}));
        this.mMainFragment.setTvCurDataTimeWeek(getString(weeklst[mWay]));
    }

    public void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        intentFilter.addAction("android.intent.action.TIME_TICK");
        intentFilter.addAction(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT);
        intentFilter.addAction(EventUtils.ZXW_SENDBROADCAST8902MOD);
        intentFilter.addAction(EventUtils.VALID_MODE_INFOR_CHANGE);
        intentFilter.addAction(EventUtils.ZXW_ACTION_NOTIIFY_MEDIA_TYPE);
        intentFilter.addAction(EventUtils.HBCP_EVT_HSHF_STATUS);
        intentFilter.addAction(EventUtils.REC_AUTONAVI_STANDARD);
        intentFilter.addAction(EventUtils.ZXW_CAN_START_UP_APPS);
        intentFilter.addAction("com.choiceway.musicplayer.bitmap");
        registerReceiver(this.mBroadcastReceiver, intentFilter);
    }

    public void unregisterReceiver() {
        unregisterReceiver(this.mBroadcastReceiver);
    }

    public static Launcher getLauncher(Context context) {
        if (context instanceof Launcher) {
            return (Launcher) context;
        }
        return (Launcher) ((ContextWrapper) context).getBaseContext();
    }
}
