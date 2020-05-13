package com.szchoiceway.index;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MultimediaActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MultimediaActivity";
    ImageView[] imageViewFocusList;
    int[] imgFocusList;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT) && EventUtils.isActivityRuning(MultimediaActivity.this.getApplicationContext(), MultimediaActivity.class)) {
                switch (intent.getIntExtra(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_DATA, -1)) {
                    case 3:
                        MultimediaActivity.access$010(MultimediaActivity.this);
                        if (MultimediaActivity.this.m_iCurrFocus2 < 0) {
                            int unused = MultimediaActivity.this.m_iCurrFocus2 = 0;
                            break;
                        }
                        break;
                    case 4:
                        MultimediaActivity.access$008(MultimediaActivity.this);
                        if (MultimediaActivity.this.m_iCurrFocus2 >= MultimediaActivity.this.imageViewFocusList.length) {
                            int unused2 = MultimediaActivity.this.m_iCurrFocus2 = MultimediaActivity.this.imageViewFocusList.length - 1;
                            break;
                        }
                        break;
                    case 5:
                        MultimediaActivity.this.zxwOriginalMcuKeyEnter();
                        break;
                }
                MultimediaActivity.this.refreshFocusView();
            }
        }
    };
    /* access modifiers changed from: private */
    public int m_iCurrFocus2 = 0;

    static /* synthetic */ int access$008(MultimediaActivity x0) {
        int i = x0.m_iCurrFocus2;
        x0.m_iCurrFocus2 = i + 1;
        return i;
    }

    static /* synthetic */ int access$010(MultimediaActivity x0) {
        int i = x0.m_iCurrFocus2;
        x0.m_iCurrFocus2 = i - 1;
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kesaiwei_1280x480_activity_multimedia);
        initView();
        initRegisterReceiver();
    }

    private void initView() {
        LauncherApplication launcherApplication = (LauncherApplication) getApplication();
        if (LauncherApplication.m_iUITypeVer == 41) {
            this.imgFocusList = new int[]{R.id.Focus_music, R.id.Focus_video};
            this.imageViewFocusList = new ImageView[this.imgFocusList.length];
            for (int i = 0; i < this.imgFocusList.length; i++) {
                this.imageViewFocusList[i] = (ImageView) findViewById(this.imgFocusList[i]);
            }
            refreshFocusView();
        }
        int[] btnList = {R.id.music, R.id.video};
        for (int findViewById : btnList) {
            View btn = findViewById(findViewById);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }

    private void initRegisterReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(EventUtils.ZXW_ORIGINAL_MCU_KEY_FOCUS_MOVE_EVT);
        registerReceiver(this.mReceiver, filter);
    }

    public void refreshFocusView() {
        for (int i = 0; i < this.imageViewFocusList.length; i++) {
            if (this.imageViewFocusList[i] != null) {
                if (i == this.m_iCurrFocus2) {
                    this.imageViewFocusList[i].setVisibility(0);
                } else {
                    this.imageViewFocusList[i].setVisibility(8);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.mReceiver != null) {
            unregisterReceiver(this.mReceiver);
        }
    }

    public void onClick(View v) {
        if (v != null) {
            LauncherApplication launcherApplication = (LauncherApplication) getApplication();
            switch (v.getId()) {
                case R.id.music /*2131558430*/:
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        this.m_iCurrFocus2 = 0;
                    }
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                    break;
                case R.id.video /*2131558432*/:
                    if (LauncherApplication.m_iUITypeVer == 41) {
                        this.m_iCurrFocus2 = 1;
                    }
                    EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                    break;
            }
            if (LauncherApplication.m_iUITypeVer == 41) {
                refreshFocusView();
            }
        }
    }

    public void zxwOriginalMcuKeyEnter() {
        switch (this.m_iCurrFocus2) {
            case 0:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MUSIC_MODE_PACKAGE_NAME, EventUtils.MUSIC_MODE_CLASS_NAME);
                return;
            case 1:
                EventUtils.startActivityIfNotRuning(getApplicationContext(), EventUtils.MOVIE_MODE_PACKAGE_NAME, EventUtils.MOVIE_MODE_CLASS_NAME);
                return;
            default:
                return;
        }
    }
}
