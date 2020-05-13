package com.szchoiceway.index;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class NoDevice extends Activity implements View.OnClickListener, View.OnTouchListener {
    protected static final String TAG = "NoDevice";
    public static NoDevice m_instance = null;
    public final int KESAIWEI_HANDLE_HIDE_DELAY = 1;
    View btn = null;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    NoDevice.this.handler.removeMessages(1);
                    if (NoDevice.this.lytNoDevice != null) {
                        NoDevice.this.lytNoDevice.setVisibility(8);
                        NoDevice.this.finish();
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    };
    private Intent intent = null;
    RelativeLayout lytNoDevice = null;
    private boolean mInitVar = false;
    private SysProviderOpt mSysProviderOpt = null;
    private int m_iModeSet = 0;
    public int m_iUITypeVer = 0;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        this.mSysProviderOpt = ((LauncherApplication) getApplication()).getProvider();
        this.m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, this.m_iUITypeVer);
        this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
        if (!(this.m_iModeSet == 2 || this.m_iModeSet == 4 || this.m_iModeSet == 6)) {
            setTheme(R.style.Kesaiwei_Theme_Translucent);
        }
        super.onCreate(savedInstanceState);
        m_instance = this;
        Log.i(TAG, "--->>> 3333 xingshuo_m_iClickAppsCodeCount ");
        if (this.m_iModeSet == 2 || this.m_iModeSet == 4) {
            setContentView(R.layout.kesaiwei_1280x480_no_device_evo);
        } else if (this.m_iModeSet == 1) {
            setContentView(R.layout.kesaiwei_1280x480_no_device_aodi);
        } else if (this.m_iModeSet == 3) {
            setContentView(R.layout.kesaiwei_1024x600_no_device_aodi_q5);
        } else if (this.m_iModeSet == 7) {
            setContentView(R.layout.kesaiwei_1280x480_no_device_evo_id6);
        } else if (this.m_iModeSet == 8) {
            setContentView(R.layout.kesaiwei_1280x480_no_device_aodi_q5);
        } else {
            setContentView(R.layout.kesaiwei_1280x480_no_device);
        }
        this.lytNoDevice = (RelativeLayout) findViewById(R.id.LytNoDevice);
        this.handler.sendEmptyMessageDelayed(1, 3000);
        this.intent = getIntent();
        this.mInitVar = true;
        initView();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.d(TAG, "onResume");
        if (getRequestedOrientation() != 0) {
            setRequestedOrientation(0);
        }
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        m_instance = null;
    }

    private void initView() {
        int[] btnList = new int[0];
        for (int findViewById : btnList) {
            View btn2 = findViewById(findViewById);
            if (btn2 != null) {
                btn2.setOnClickListener(this);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void onClick(View v) {
        v.getId();
    }

    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
