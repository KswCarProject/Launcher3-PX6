package com.szchoiceway.index;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
                FileInputStream fis2 = new FileInputStream(file);
                try {
                    BufferedReader buffreader = new BufferedReader(new InputStreamReader(fis2));
                    writeTouchPaneCfg(context, buffreader.readLine());
                    buffreader.close();
                    fis2.close();
                } catch (IOException e) {
                    e = e;
                    fis = fis2;
                    e.printStackTrace();
                    try {
                        fis.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (IOException e2) {
                e = e2;
                e.printStackTrace();
                fis.close();
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
                FileOutputStream fos2 = new FileOutputStream(file);
                try {
                    fos2.write(cfgData);
                    Log.i("writeTouchPaneCfg", "write touch param success.");
                    Toast.makeText(context, "write touch param success", 4000).show();
                    fos2.close();
                } catch (IOException e) {
                    e = e;
                    fos = fos2;
                    e.printStackTrace();
                    Log.i("writeTouchPaneCfg", "write touch param fail.");
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (IOException e2) {
                e = e2;
                e.printStackTrace();
                Log.i("writeTouchPaneCfg", "write touch param fail.");
                fos.close();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0056  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x007d A[SYNTHETIC, Splitter:B:28:0x007d] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readTouchPaneCfg(android.content.Context r15) {
        /*
            r14 = this;
            r6 = 0
            java.io.File r6 = new java.io.File
            java.lang.String r11 = "/proc/gt9xx_config"
            r6.<init>(r11)
            r7 = 0
            boolean r11 = r6.exists()
            if (r11 == 0) goto L_0x00ad
            boolean r11 = r6.isFile()
            if (r11 == 0) goto L_0x00ad
            boolean r11 = r6.canRead()
            if (r11 == 0) goto L_0x00ad
            java.lang.String r10 = ""
            r2 = 0
            java.io.FileInputStream r8 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0077 }
            r8.<init>(r6)     // Catch:{ IOException -> 0x0077 }
            java.io.InputStreamReader r9 = new java.io.InputStreamReader     // Catch:{ IOException -> 0x00c3 }
            r9.<init>(r8)     // Catch:{ IOException -> 0x00c3 }
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ IOException -> 0x00c3 }
            r3.<init>(r9)     // Catch:{ IOException -> 0x00c3 }
            java.lang.String r10 = r3.readLine()     // Catch:{ IOException -> 0x00c6 }
            r3.close()     // Catch:{ IOException -> 0x00c6 }
            java.lang.String r11 = "readTouchPaneCfg"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00c6 }
            r12.<init>()     // Catch:{ IOException -> 0x00c6 }
            java.lang.String r13 = "oldCfgData = "
            java.lang.StringBuilder r12 = r12.append(r13)     // Catch:{ IOException -> 0x00c6 }
            java.lang.StringBuilder r12 = r12.append(r10)     // Catch:{ IOException -> 0x00c6 }
            java.lang.String r12 = r12.toString()     // Catch:{ IOException -> 0x00c6 }
            android.util.Log.i(r11, r12)     // Catch:{ IOException -> 0x00c6 }
            r2 = r3
            r7 = r8
        L_0x004e:
            if (r10 == 0) goto L_0x00a5
            int r11 = r10.length()
            if (r11 <= 0) goto L_0x00a5
            java.io.File r6 = new java.io.File
            java.lang.String r11 = TOUCH_PANE_OLD_CFG_FILE_PATH
            r6.<init>(r11)
            r0 = 0
            java.io.BufferedWriter r1 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x008e }
            java.io.FileWriter r11 = new java.io.FileWriter     // Catch:{ IOException -> 0x008e }
            r11.<init>(r6)     // Catch:{ IOException -> 0x008e }
            r1.<init>(r11)     // Catch:{ IOException -> 0x008e }
            r1.write(r10)     // Catch:{ IOException -> 0x00c0 }
            r1.close()     // Catch:{ IOException -> 0x00c0 }
            r0 = 0
            java.lang.String r11 = "readTouchPaneCfg"
            java.lang.String r12 = "sava old cfg success."
            android.util.Log.i(r11, r12)     // Catch:{ IOException -> 0x008e }
        L_0x0076:
            return
        L_0x0077:
            r4 = move-exception
        L_0x0078:
            r4.printStackTrace()
            if (r2 == 0) goto L_0x0080
            r2.close()     // Catch:{ IOException -> 0x0089 }
        L_0x0080:
            r2 = 0
            java.lang.String r11 = "readTouchPaneCfg"
            java.lang.String r12 = "read touch pane cfg error "
            android.util.Log.i(r11, r12)
            goto L_0x004e
        L_0x0089:
            r5 = move-exception
            r5.printStackTrace()
            goto L_0x0080
        L_0x008e:
            r4 = move-exception
        L_0x008f:
            r4.printStackTrace()
            if (r0 == 0) goto L_0x0097
            r0.close()     // Catch:{ IOException -> 0x00a0 }
        L_0x0097:
            r0 = 0
            java.lang.String r11 = "readTouchPaneCfg"
            java.lang.String r12 = "sava old cfg fail."
            android.util.Log.i(r11, r12)
            goto L_0x0076
        L_0x00a0:
            r5 = move-exception
            r5.printStackTrace()
            goto L_0x0097
        L_0x00a5:
            java.lang.String r11 = "readTouchPaneCfg"
            java.lang.String r12 = "oldCfgData == 0"
            android.util.Log.i(r11, r12)
            goto L_0x0076
        L_0x00ad:
            java.lang.String r11 = "readTouchPaneCfg"
            java.lang.String r12 = "path [/proc/gt9xx_config] error."
            android.util.Log.i(r11, r12)
            java.lang.String r11 = "Not find  TouchPaneParam.cfg,Please Update OS 2"
            r12 = 4000(0xfa0, float:5.605E-42)
            android.widget.Toast r11 = android.widget.Toast.makeText(r15, r11, r12)
            r11.show()
            goto L_0x0076
        L_0x00c0:
            r4 = move-exception
            r0 = r1
            goto L_0x008f
        L_0x00c3:
            r4 = move-exception
            r7 = r8
            goto L_0x0078
        L_0x00c6:
            r4 = move-exception
            r2 = r3
            r7 = r8
            goto L_0x0078
        */
        throw new UnsupportedOperationException("Method not decompiled: com.szchoiceway.index.TouchPaneCfg.readTouchPaneCfg(android.content.Context):void");
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
        this.tvTime.setTextColor(-1);
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
                TouchPaneCfg.this.tvTime.setText("检测到触摸屏升级文件，即将在 " + (millisUntilFinished / 1000) + "s 后自动升级！");
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
