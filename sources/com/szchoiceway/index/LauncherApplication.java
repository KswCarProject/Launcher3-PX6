package com.szchoiceway.index;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import com.choiceway.index.IWeatherService;
import com.szchoiceway.eventcenter.IEventService;
import com.szchoiceway.index.LauncherSettings;
import com.szchoiceway.index.WidgetPreviewLoader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpStatus;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LauncherApplication extends Application {
    protected static final String TAG = "LauncherApplication";
    public static int m_iModeSet = 0;
    public static int m_iSetKeshangUIType = 1;
    public static int m_iUIType = 4;
    public static int m_iUITypeVer = 0;
    private static boolean sIsScreenLarge = false;
    private static int sLongPressTimeout = HttpStatus.SC_MULTIPLE_CHOICES;
    private static float sScreenDensity = 0.0f;
    private static final String sSharedPreferencesKey = "com.szchoiceway.index.prefs";
    public int mAppsIconIndex = 0;
    private SharedPreferences.Editor mEditor = null;
    private ServiceConnection mEvtSc = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(LauncherApplication.TAG, "ServiceConnection");
            IEventService unused = LauncherApplication.this.mEvtService = IEventService.Stub.asInterface(service);
            if (LauncherApplication.this.mEvtService != null) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.i(LauncherApplication.TAG, "onServiceDisconnected");
            IEventService unused = LauncherApplication.this.mEvtService = null;
        }
    };
    /* access modifiers changed from: private */
    public IEventService mEvtService = null;
    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            LauncherApplication.this.mModel.resetLoadedState(false, true);
            LauncherApplication.this.mModel.startLoaderFromBackground();
        }
    };
    private IconCache mIconCache;
    WeakReference<LauncherProvider> mLauncherProvider;
    /* access modifiers changed from: private */
    public LauncherModel mModel;
    private SharedPreferences mSettings = null;
    private SysProviderOpt mSysProviderOpt = null;
    private IWeatherService mWeatherSrv = null;
    private WidgetPreviewLoader.CacheDb mWidgetPreviewCacheDb;
    public SysVarConfig m_SysVarConfig = new SysVarConfig();
    public int m_iSetUIType = -1;
    public String xml_client;

    public void onCreate() {
        super.onCreate();
        this.mSysProviderOpt = new SysProviderOpt(this);
        Map<String, String> mpCustomerAtt = loadXmlFile();
        String iUITypeVer = mpCustomerAtt.get("m_iUITypeVer");
        if (iUITypeVer != null && iUITypeVer.length() > 0) {
            m_iUITypeVer = Integer.parseInt(iUITypeVer);
        }
        String appVersion = mpCustomerAtt.get("AppVersion");
        Log.i(TAG, "initSystemParam: appVersion = " + appVersion);
        if (appVersion != null && appVersion.length() > 0) {
            this.mSysProviderOpt.updateRecord(SysProviderOpt.SYS_APP_VERSION, appVersion);
        }
        this.mSysProviderOpt.updateRecord(SysProviderOpt.SET_USER_UI_TYPE, "" + m_iUITypeVer);
        startService(new Intent(EventUtils.EVENT_SERVICE_NAME).setPackage("com.szchoiceway.eventcenter"));
        this.mSettings = getSharedPreferences("WeatherService.setting", 0);
        this.mEditor = this.mSettings.edit();
        sIsScreenLarge = getResources().getBoolean(R.bool.is_large_screen);
        sScreenDensity = getResources().getDisplayMetrics().density;
        this.mSysProviderOpt.updateRecord(SysProviderOpt.SYS_SET_UI_TYPE, "" + m_iUITypeVer);
        this.m_iSetUIType = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SYS_SET_UI_TYPE, this.m_iSetUIType);
        Log.i(TAG, "--->>> m_iSetUIType  = " + this.m_iSetUIType);
        m_iSetKeshangUIType = this.m_iSetUIType;
        m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, m_iUITypeVer);
        m_iUIType = m_iUITypeVer;
        m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, m_iModeSet);
        Utilities.SetUITypeVer(m_iUITypeVer, this);
        Utilities.KSW_iModeSet(m_iModeSet, this);
        this.xml_client = this.mSysProviderOpt.getRecordValue(SysProviderOpt.KSW_FACTORY_SET_CLIENT);
        Log.i(TAG, "--->>> m_iUITypeVer  = " + m_iUITypeVer);
        Utilities.setXmlClient(this.xml_client);
        this.mAppsIconIndex = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KSW_APPS_ICON_SELECT_INDEX, 0);
        Utilities.setAppsIconIndex(this.mAppsIconIndex);
        this.mWidgetPreviewCacheDb = new WidgetPreviewLoader.CacheDb(this);
        this.mIconCache = new IconCache(this);
        this.mModel = new LauncherModel(this, this.mIconCache);
        bindService(new Intent(EventUtils.EVENT_SERVICE_NAME).setPackage("com.szchoiceway.eventcenter"), this.mEvtSc, 1);
        IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addDataScheme("package");
        registerReceiver(this.mModel, filter);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(IntentCompat.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter2.addAction(IntentCompat.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter2.addAction("android.intent.action.LOCALE_CHANGED");
        filter2.addAction("android.intent.action.CONFIGURATION_CHANGED");
        registerReceiver(this.mModel, filter2);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED");
        registerReceiver(this.mModel, filter3);
        IntentFilter filter4 = new IntentFilter();
        filter4.addAction("android.search.action.SEARCHABLES_CHANGED");
        registerReceiver(this.mModel, filter4);
        getContentResolver().registerContentObserver(LauncherSettings.Favorites.CONTENT_URI, true, this.mFavoritesObserver);
    }

    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(this.mModel);
        unbindService(this.mEvtSc);
        getContentResolver().unregisterContentObserver(this.mFavoritesObserver);
    }

    /* access modifiers changed from: package-private */
    public LauncherModel setLauncher(Launcher launcher) {
        this.mModel.initialize(launcher);
        return this.mModel;
    }

    /* access modifiers changed from: package-private */
    public IconCache getIconCache() {
        return this.mIconCache;
    }

    /* access modifiers changed from: package-private */
    public LauncherModel getModel() {
        return this.mModel;
    }

    /* access modifiers changed from: package-private */
    public WidgetPreviewLoader.CacheDb getWidgetPreviewCacheDb() {
        return this.mWidgetPreviewCacheDb;
    }

    /* access modifiers changed from: package-private */
    public void setLauncherProvider(LauncherProvider provider) {
        this.mLauncherProvider = new WeakReference<>(provider);
    }

    /* access modifiers changed from: package-private */
    public LauncherProvider getLauncherProvider() {
        return (LauncherProvider) this.mLauncherProvider.get();
    }

    public static String getSharedPreferencesKey() {
        return sSharedPreferencesKey;
    }

    public static boolean isScreenLarge() {
        return sIsScreenLarge;
    }

    public static boolean isScreenLandscape(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    public static float getScreenDensity() {
        return sScreenDensity;
    }

    public static int getLongPressTimeout() {
        return sLongPressTimeout;
    }

    /* access modifiers changed from: package-private */
    public SharedPreferences getSetting() {
        return this.mSettings;
    }

    /* access modifiers changed from: package-private */
    public SharedPreferences.Editor getEditor() {
        return this.mEditor;
    }

    public SysProviderOpt getProvider() {
        return this.mSysProviderOpt;
    }

    public static int SetUIType() {
        return m_iSetKeshangUIType;
    }

    public static int SetUITypeVer() {
        return m_iUIType;
    }

    /* access modifiers changed from: package-private */
    public void setmWeatherSrv(IWeatherService WeatherSrv) {
        this.mWeatherSrv = WeatherSrv;
    }

    /* access modifiers changed from: package-private */
    public IWeatherService getmWeatherSrv() {
        return this.mWeatherSrv;
    }

    public IEventService getEvtService() {
        return this.mEvtService;
    }

    public SysFatPara GetSysVarConfig() {
        return this.m_SysVarConfig.GetFatPara();
    }

    public void SaveSysVarConfig(SysFatPara sysFatPara) {
        Log.i(TAG, "****SaveSysVarConfig****" + SysFatPara.iCarCanbusName_ID);
        Log.i(TAG, "mSysProvider = " + this.mSysProviderOpt);
        if (this.mSysProviderOpt != null) {
            this.mSysProviderOpt.updateRecord(SysProviderOpt.SET_Canbustype_KEY, "" + SysFatPara.iCanbustype);
            this.mSysProviderOpt.updateRecord(SysProviderOpt.SET_Carstype_ID_KEY, "" + SysFatPara.iCarstype_ID);
            this.mSysProviderOpt.updateRecord(SysProviderOpt.SET_CarCanbusName_ID_KEY, "" + SysFatPara.iCarCanbusName_ID);
            Log.i(TAG, "sysFatPara.iCanbustype = " + SysFatPara.iCanbustype);
            Log.i(TAG, "sysFatPara.iCarstype_ID = " + SysFatPara.iCarstype_ID);
            Log.i(TAG, "sysFatPara.iCarCanbusName_ID = " + SysFatPara.iCarCanbusName_ID);
        }
        this.m_SysVarConfig.SaveFatPara(sysFatPara);
    }

    public static int get_m_iUITypeVer() {
        return m_iUITypeVer;
    }

    public static int get_m_iModeSet() {
        return m_iModeSet;
    }

    public static void set_m_iModeSet(int iModeSet) {
        m_iModeSet = iModeSet;
    }

    private Map<String, String> loadXmlFile() {
        String strCustomerFileName;
        DocumentBuilder builder;
        InputStream myInput;
        String strResolution = this.mSysProviderOpt.getRecordValue(SysProviderOpt.RESOLUTION);
        if ("1280x480".equalsIgnoreCase(strResolution)) {
            strCustomerFileName = "customer.xml";
        } else if ("1920x720".equalsIgnoreCase(strResolution)) {
            strCustomerFileName = "customer_1920x720.xml";
        } else if ("1024x600".equalsIgnoreCase(strResolution)) {
            strCustomerFileName = "customer_1024x600.xml";
        } else {
            strCustomerFileName = "customer.xml";
        }
        Map<String, String> map = new HashMap<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Log.i("test", "start");
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            builder = null;
            e.printStackTrace();
        }
        try {
            myInput = new FileInputStream("/oem/app/" + strCustomerFileName);
        } catch (FileNotFoundException e2) {
            try {
                myInput = new FileInputStream("/system/app/" + strCustomerFileName);
            } catch (FileNotFoundException e3) {
                try {
                    myInput = new FileInputStream("/oem/app/customer.xml");
                } catch (FileNotFoundException e4) {
                    try {
                        myInput = new FileInputStream("/system/app/customer.xml");
                    } catch (FileNotFoundException e5) {
                        myInput = null;
                        Log.i(TAG, "loadXmlFile: e = " + e2.toString());
                    }
                }
            }
        }
        if (!(myInput == null || builder == null)) {
            try {
                NodeList list = builder.parse(myInput).getElementsByTagName("customer");
                Log.i("test", "customer list.getLength() = " + list.getLength());
                if (list.getLength() > 0) {
                    NodeList list2 = ((Element) list.item(0)).getElementsByTagName("item");
                    Log.i("test", "item list.getLength() = " + list2.getLength());
                    for (int loop = 0; loop < list2.getLength(); loop++) {
                        Element node = (Element) list2.item(loop);
                        map.put(node.getAttribute(InstallShortcutReceiver.NAME_KEY), node.getAttribute("value"));
                        Log.i("test", "name =" + node.getAttribute(InstallShortcutReceiver.NAME_KEY) + " value=" + node.getAttribute("value"));
                    }
                }
            } catch (Exception e6) {
                e6.printStackTrace();
            }
        }
        if (myInput != null) {
            try {
                myInput.close();
            } catch (IOException e7) {
                e7.printStackTrace();
            }
        }
        return map;
    }
}
