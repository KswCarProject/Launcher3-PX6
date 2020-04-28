package com.android.launcher3.touch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class TouchPaneCfg {
    private static final String TAG = "TouchPaneCfg";
    private static String TOUCH_PANE_CFG_FILE_PATH = "/mnt/usb_storage/TouchPaneParam.cfg";
    private static String TOUCH_PANE_OLD_CFG_FILE_PATH = "/mnt/usb_storage/TouchPaneParam.cfg.bak";
    /* access modifiers changed from: private */
    public DownTimer downTimer;
    private AlertDialog.Builder mAlertDialog;
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public AlertDialog showAlertDialog;
    /* access modifiers changed from: private */
    public TextView tvTime;

    public TouchPaneCfg() {
    }

    public TouchPaneCfg(Context context) {
        if (Build.VERSION.SDK_INT > 19) {
            TOUCH_PANE_CFG_FILE_PATH = "/storage/usb_storage/TouchPaneParam.cfg";
            TOUCH_PANE_OLD_CFG_FILE_PATH = "/storage/usb_storage/TouchPaneParam.cfg.bak";
        }
        this.mContext = context;
        File file = new File(TOUCH_PANE_CFG_FILE_PATH);
        Log.i("loadTouchCfgFile", "loadTouchCfgFile: " + file.exists() + "|" + file.isFile() + "|" + file.canRead());
        if (file.exists() && file.isFile() && file.canRead()) {
            showAlertDialog(context);
        }
    }

    public void loadTouchCfgFile(Context context) {
        Log.i("loadTouchCfgFile", "start");
        File file = new File(TOUCH_PANE_CFG_FILE_PATH);
        Log.i("loadTouchCfgFile", "loadTouchCfgFile: " + file.exists() + "|" + file.isFile() + "|" + file.canRead());
        if (file.exists() && file.isFile() && file.canRead()) {
            readTouchPaneCfg(context);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                BufferedReader buffreader = new BufferedReader(new InputStreamReader(fis));
                writeTouchPaneCfg(context, buffreader.readLine());
                buffreader.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void writeTouchPaneCfg(Context context, String strCfgData) {
        String strCfgData2 = strCfgData.replace("0x", "").replace(",", "");
        Log.i("writeTouchPaneCfg", "strCfgData len = " + (strCfgData2.length() / 2));
        if (strCfgData2.length() / 2 != 186) {
            Toast.makeText(context, "invalid TouchPaneParam.cfg", 4000).show();
            return;
        }
        byte[] cfgData = hexStringToBytes(strCfgData2);
        if (cfgData != null) {
            cfgData[0] = 0;
            byte bySum = 0;
            for (int loop = 0; loop < cfgData.length - 2; loop++) {
                bySum = (byte) (cfgData[loop] + bySum);
            }
            cfgData[cfgData.length - 2] = (byte) (((bySum ^ 255) + 1) & 255);
            cfgData[cfgData.length - 1] = 1;
            Log.i("writeScreenTouchCfg", bytesToHexStringEx(cfgData, ","));
            File file = new File("/proc/gt9xx_config");
            FileOutputStream fos = null;
            if (!file.exists() || !file.canWrite()) {
                Log.i("writeTouchPaneCfg", "path [/proc/gt9xx_config] error.");
                Toast.makeText(context, "Not find  TouchPaneParam.cfg,Please Update OS", 4000).show();
                return;
            }
            try {
                fos = new FileOutputStream(file);
                fos.write(cfgData);
                Log.i("writeTouchPaneCfg", "write touch param success.");
                Toast.makeText(context, "write touch param success", 4000).show();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("writeTouchPaneCfg", "write touch param fail.");
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void readTouchPaneCfg(Context context) {
        File file = new File("/proc/gt9xx_config");
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            Log.i("readTouchPaneCfg", "path [/proc/gt9xx_config] error.");
            Toast.makeText(context, "Not find  TouchPaneParam.cfg,Please Update OS 2", 4000).show();
            return;
        }
        String oldCfgData = "";
        BufferedWriter bufferedWriter = null;
        BufferedReader buffreader = null;
        try {
            buffreader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            oldCfgData = buffreader.readLine();
            buffreader.close();
            Log.i("readTouchPaneCfg", "oldCfgData = " + oldCfgData);
        } catch (IOException e) {
            e.printStackTrace();
            if (buffreader != null) {
                try {
                    buffreader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            Log.i("readTouchPaneCfg", "read touch pane cfg error ");
        }
        if (oldCfgData == null || oldCfgData.length() <= 0) {
            Log.i("readTouchPaneCfg", "oldCfgData == 0");
            return;
        }
        try {
            BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(new File(TOUCH_PANE_OLD_CFG_FILE_PATH)));
            bufferedWriter2.write(oldCfgData);
            bufferedWriter2.close();
            bufferedWriter = null;
            Log.i("readTouchPaneCfg", "sava old cfg success.");
        } catch (IOException e2) {
            e2.printStackTrace();
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e12) {
                    e12.printStackTrace();
                }
            }
            Log.i("readTouchPaneCfg", "sava old cfg fail.");
        }
    }

    private byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        String hexString2 = hexString.toUpperCase();
        int length = hexString2.length() / 2;
        char[] hexChars = hexString2.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) ((charToByte(hexChars[pos]) << 4) | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private String bytesToHexStringEx(byte[] src, String splite) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & 255);
            stringBuilder.append("0x");
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv + splite);
        }
        return stringBuilder.toString();
    }

    private void showAlertDialog(Context context) {
        this.mAlertDialog = new AlertDialog.Builder(context);
        this.tvTime = new TextView(context);
        this.tvTime.setTextSize(24.0f);
        this.tvTime.setTextColor(-16776961);
        this.tvTime.setPadding(20, 20, 20, 20);
        this.mAlertDialog.setView(this.tvTime);
        this.mAlertDialog.setTitle("触摸屏升级");
        this.mAlertDialog.setPositiveButton("升级", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TouchPaneCfg.this.loadTouchCfgFile(TouchPaneCfg.this.mContext);
            }
        });
        this.mAlertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        this.mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                if (TouchPaneCfg.this.downTimer != null) {
                    TouchPaneCfg.this.downTimer.cancel();
                }
            }
        });
        this.showAlertDialog = this.mAlertDialog.show();
        this.downTimer = new DownTimer();
        this.downTimer.start();
    }

    private class DownTimer extends CountDownTimer {
        private DownTimer() {
            super(30000, 500);
        }

        public void onTick(long millisUntilFinished) {
            Log.i(TouchPaneCfg.TAG, "onTick: millisUntilFinished = " + millisUntilFinished);
            if (TouchPaneCfg.this.tvTime != null) {
                TextView access$300 = TouchPaneCfg.this.tvTime;
                access$300.setText("检测到触摸屏升级文件，即将在 " + (millisUntilFinished / 1000) + "s 后自动升级！");
            }
        }

        public void onFinish() {
            Log.i(TouchPaneCfg.TAG, "onFinish: ");
            if (TouchPaneCfg.this.showAlertDialog != null) {
                TouchPaneCfg.this.showAlertDialog.dismiss();
            }
            TouchPaneCfg.this.loadTouchCfgFile(TouchPaneCfg.this.mContext);
        }
    }
}
