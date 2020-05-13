package com.szchoiceway.eventcenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.storage.StorageManager;
import android.util.Log;
import com.szchoiceway.index.LauncherApplication;
import com.szchoiceway.index.SysProviderOpt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static CrashHandler mCrashHandler = new CrashHandler();
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
    private Map<String, String> infos = new HashMap();
    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private SysProviderOpt mSysProviderOpt;
    private String strAppver = "";
    private String usb_ = "/storage/usb_storage";

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return mCrashHandler;
    }

    public void init(Context context) {
        this.mContext = context;
        this.mSysProviderOpt = ((LauncherApplication) context).getProvider();
        this.strAppver = this.mSysProviderOpt.getRecordValue(SysProviderOpt.SYS_APP_VERSION);
        Log.i(TAG, "init: strAppver = " + this.strAppver);
        this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void uncaughtException(Thread thread, Throwable ex) {
        if (handleException(ex) || this.mDefaultHandler == null) {
            Log.i(TAG, "uncaughtException: else");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            Process.killProcess(Process.myPid());
            System.exit(1);
            return;
        }
        Log.i(TAG, "uncaughtException: if");
        this.mDefaultHandler.uncaughtException(thread, ex);
    }

    private boolean handleException(Throwable ex) {
        Log.i(TAG, "handleException: ");
        if (ex == null) {
            return false;
        }
        new Thread() {
            public void run() {
                Looper.prepare();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo(this.mContext);
        saveCrashInfo2File(ex);
        return true;
    }

    public void collectDeviceInfo(Context ctx) {
        try {
            PackageInfo pi = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 1);
            if (pi != null) {
                this.infos.put("versionName", pi.versionName == null ? "null" : pi.versionName);
                this.infos.put("versionCode", pi.versionCode + "");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        for (Field field : Build.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                this.infos.put(field.getName(), field.get((Object) null).toString());
                Log.d(TAG, field.getName() + " : " + field.get((Object) null));
            } catch (Exception e2) {
                Log.e(TAG, "an error occured when collect crash info", e2);
            }
        }
    }

    private String saveCrashInfo2File(Throwable ex) {
        boolean mkdirs;
        StringBuffer sb = new StringBuffer();
        sb.append(this.strAppver + "\n");
        for (Map.Entry<String, String> entry : this.infos.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        for (Throwable cause = ex.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        printWriter.close();
        sb.append(writer.toString());
        try {
            String fileName = "crash-" + this.formatter.format(new Date()) + "-" + System.currentTimeMillis() + ".log";
            if (!Environment.getExternalStorageState().equals("mounted")) {
                return fileName;
            }
            if (Build.VERSION.SDK_INT > 19) {
                String path = "/storage/emulated/0/log/";
                Log.i(TAG, "saveCrashInfo2File: isMountUSB() = " + isMountUSB());
                if (isMountUSB()) {
                    Log.i(TAG, "saveCrashInfo2File: usb_ = " + this.usb_);
                    path = this.usb_ + "/log/";
                }
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
                return fileName;
            }
            String path2 = "/mnt/usb_storage/log/";
            File dir2 = new File(path2);
            if (!dir2.exists()) {
                mkdirs = dir2.mkdirs();
                if (!mkdirs) {
                    path2 = "/mnt/usb_storage1/log/";
                    File dir3 = new File(path2);
                    if (!dir3.exists()) {
                        mkdirs = dir3.mkdirs();
                    } else {
                        mkdirs = true;
                    }
                }
            } else {
                mkdirs = true;
            }
            if (mkdirs) {
                FileOutputStream fos2 = new FileOutputStream(path2 + fileName);
                fos2.write(sb.toString().getBytes());
                fos2.close();
                return fileName;
            }
            Log.i(TAG, "saveCrashInfo2File: mkdirs = false");
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
            return null;
        }
    }

    private boolean isMountUSB() {
        StorageManager mStorageManager = null;
        if (0 == 0) {
            mStorageManager = (StorageManager) this.mContext.getSystemService("storage");
        }
        String str = mStorageManager.getVolumeState(this.usb_);
        Log.i(TAG, "isMountUSB: str = " + str);
        if ("mounted".equals(str)) {
            return true;
        }
        this.usb_ = "/storage/usb_storage1";
        if ("mounted".equals(mStorageManager.getVolumeState(this.usb_))) {
            return true;
        }
        this.usb_ = "/storage/usb_storage2";
        if ("mounted".equals(mStorageManager.getVolumeState(this.usb_))) {
            return true;
        }
        this.usb_ = "/storage/usb_storage3";
        if ("mounted".equals(mStorageManager.getVolumeState(this.usb_))) {
            return true;
        }
        this.usb_ = "/storage/usb_storage4";
        if ("mounted".equals(mStorageManager.getVolumeState(this.usb_))) {
            return true;
        }
        this.usb_ = "/storage/usb_storage5";
        if (!"mounted".equals(mStorageManager.getVolumeState(this.usb_))) {
            return false;
        }
        return true;
    }
}
