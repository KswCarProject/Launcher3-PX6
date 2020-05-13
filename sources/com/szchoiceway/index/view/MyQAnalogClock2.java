package com.szchoiceway.index.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.szchoiceway.index.Launcher;
import com.szchoiceway.index.R;
import com.szchoiceway.index.SysProviderOpt;

public class MyQAnalogClock2 extends View {
    private static final String TAG = "MyQAnalogClock2";
    private static final int ZXW_DASH_BOARD_REFRESH_UI = 1004;
    /* access modifiers changed from: private */
    public static boolean bIsRotatingSpeed = false;
    private static int iDeviationScale = 139;
    private static int iMaxRotatingSpeed = 8000;
    private static int iMaxSpeed = 280;
    private static float iMaxValue = ((float) iMaxSpeed);
    private static int iTotalScale = 278;
    private static int mDialDrawableId = R.drawable.shisubiaopan;
    private static int mPointerDrawableId = R.drawable.shisuzhizhen;
    private static SysProviderOpt mSysProviderOpt;
    int availableHeight;
    int availableWidth;
    BitmapDrawable bmdDial;
    BitmapDrawable bmdSecond;
    int centerX;
    int centerY;
    float fadongjizhuansu;
    Bitmap mBmpDial;
    Bitmap mBmpSecond;
    int mHeigh;
    Paint mPaint;
    int mTempHeigh;
    int mTempWidth;
    private TextView mTvCur;
    int mWidth;
    Handler tickHandler;
    /* access modifiers changed from: private */
    public Runnable tickRunnable;

    public MyQAnalogClock2(Context context) {
        this(context, (AttributeSet) null);
    }

    public MyQAnalogClock2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyQAnalogClock2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTempWidth = 0;
        this.availableWidth = 100;
        this.availableHeight = 100;
        this.fadongjizhuansu = 0.0f;
        this.tickRunnable = new Runnable() {
            public void run() {
                if (MyQAnalogClock2.bIsRotatingSpeed) {
                    if (MyQAnalogClock2.this.fadongjizhuansu != ((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu)) {
                        MyQAnalogClock2.this.fadongjizhuansu = (float) Launcher.ksw_m_i_audio_right_ShunShiSuDu;
                        MyQAnalogClock2.this.postInvalidate();
                    }
                } else if (MyQAnalogClock2.this.fadongjizhuansu != ((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu)) {
                    if (Math.abs(MyQAnalogClock2.this.fadongjizhuansu - ((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu)) < 1.0f) {
                        MyQAnalogClock2.this.fadongjizhuansu = (float) Launcher.ksw_m_i_audio_right_ShunShiSuDu;
                    } else if (Math.abs(MyQAnalogClock2.this.fadongjizhuansu - ((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu)) <= 2.0f) {
                        MyQAnalogClock2.this.fadongjizhuansu += (((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu) - MyQAnalogClock2.this.fadongjizhuansu) / 2.0f;
                    } else if (((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu) > MyQAnalogClock2.this.fadongjizhuansu) {
                        MyQAnalogClock2.this.fadongjizhuansu += (float) Math.round(((double) (((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu) - MyQAnalogClock2.this.fadongjizhuansu)) / 5.0d);
                    } else {
                        MyQAnalogClock2.this.fadongjizhuansu += (float) Math.floor(((double) (((float) Launcher.ksw_m_i_audio_right_ShunShiSuDu) - MyQAnalogClock2.this.fadongjizhuansu)) / 5.0d);
                    }
                    MyQAnalogClock2.this.refreshTvCurView();
                    MyQAnalogClock2.this.postInvalidate();
                }
                MyQAnalogClock2.this.tickHandler.postDelayed(MyQAnalogClock2.this.tickRunnable, 50);
            }
        };
        mSysProviderOpt = new SysProviderOpt(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyQAnalogClock);
        bIsRotatingSpeed = typedArray.getBoolean(4, bIsRotatingSpeed);
        Log.i(TAG, "MyQAnalogClock2: bIsRotatingSpeed = " + bIsRotatingSpeed);
        if (bIsRotatingSpeed) {
            mDialDrawableId = R.drawable.shisuzhizhen;
            mPointerDrawableId = R.drawable.shisubiaopan;
            iMaxValue = (float) iMaxRotatingSpeed;
        }
        mDialDrawableId = typedArray.getResourceId(0, mDialDrawableId);
        mPointerDrawableId = typedArray.getResourceId(1, mPointerDrawableId);
        iTotalScale = typedArray.getInt(2, iTotalScale);
        iDeviationScale = typedArray.getInt(3, iDeviationScale);
        typedArray.recycle();
        iTotalScale = mSysProviderOpt.getRecordInteger("iTotalScale", iTotalScale);
        iMaxValue = (float) mSysProviderOpt.getRecordInteger("maxSpeed", (int) iMaxValue);
        setView();
        run();
    }

    public void setView() {
        this.mBmpSecond = BitmapFactory.decodeResource(getResources(), mPointerDrawableId);
        this.bmdSecond = new BitmapDrawable(this.mBmpSecond);
        this.mBmpDial = BitmapFactory.decodeResource(getResources(), mDialDrawableId);
        this.bmdDial = new BitmapDrawable(this.mBmpDial);
        this.mWidth = this.mBmpDial.getWidth();
        this.mHeigh = this.mBmpDial.getHeight();
        int availableWidth2 = this.mWidth;
        int availableHeight2 = this.mHeigh;
        this.centerX = availableWidth2 / 2;
        this.centerY = availableHeight2 / 2;
        this.mPaint = new Paint();
        this.mPaint.setColor(-16776961);
    }

    public void run() {
        this.tickHandler = new Handler();
        this.tickHandler.postDelayed(this.tickRunnable, 50);
    }

    /* access modifiers changed from: package-private */
    public void refreshTvCurView() {
        if (this.mTvCur != null) {
            this.mTvCur.setText(((int) (this.fadongjizhuansu / 20.0f)) + "");
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: fadongjizhuansu = " + (this.fadongjizhuansu / 20.0f));
        Log.i(TAG, "onDraw: iMaxValue = " + iMaxValue);
        Log.i(TAG, "onDraw: iTotalScale = " + iTotalScale);
        if (this.fadongjizhuansu < 0.0f) {
            this.fadongjizhuansu = 0.0f;
        } else if (this.fadongjizhuansu > iMaxValue * 20.0f) {
            this.fadongjizhuansu = (float) (((int) iMaxValue) * 20);
        }
        float second = (this.fadongjizhuansu / (iMaxValue * 20.0f)) * ((float) iTotalScale);
        float secondRotate = 0.0f;
        if (second >= 0.0f && second <= ((float) iDeviationScale)) {
            secondRotate = second + ((float) (360 - iDeviationScale));
        } else if (second > ((float) iDeviationScale) && second <= ((float) iTotalScale)) {
            secondRotate = second - ((float) iDeviationScale);
        }
        this.bmdDial.setBounds(this.centerX - (this.mWidth / 2), this.centerY - (this.mHeigh / 2), this.centerX + (this.mWidth / 2), this.centerY + (this.mHeigh / 2));
        this.bmdDial.draw(canvas);
        this.mTempWidth = this.bmdSecond.getIntrinsicWidth();
        this.mTempHeigh = this.bmdSecond.getIntrinsicHeight();
        Log.i(TAG, "onDraw: centerX = " + this.centerX + ", centerY = " + this.centerY);
        Log.i(TAG, "onDraw: mTempWidth = " + this.mTempWidth + ", centerY = " + this.centerY);
        canvas.save();
        canvas.rotate(secondRotate, (float) this.centerX, (float) this.centerY);
        this.bmdSecond.setBounds(this.centerX - (this.mTempWidth / 2), this.centerY - (this.mTempHeigh / 2), this.centerX + (this.mTempWidth / 2), this.centerY + (this.mTempHeigh / 2));
        this.bmdSecond.draw(canvas);
        canvas.restore();
    }

    public static void setmDialDrawableId(int dialDrawableId) {
        mDialDrawableId = dialDrawableId;
    }

    public static void setmPointerDrawableId(int pointerDrawableId) {
        mPointerDrawableId = pointerDrawableId;
    }

    public static void setTotalScale(int totalScale) {
        iTotalScale = totalScale;
        if (mSysProviderOpt != null) {
            mSysProviderOpt.updateRecord("iTotalScale", iTotalScale + "");
        }
        Log.i(TAG, "setTotalScale: iTotalScale = " + iTotalScale);
    }

    public static void setDeviationScale(int deviationScale) {
        iDeviationScale = deviationScale;
    }

    public static void setMaxSpeed(int maxSpeed) {
        iMaxSpeed = maxSpeed;
        iMaxValue = (float) iMaxSpeed;
        if (mSysProviderOpt != null) {
            mSysProviderOpt.updateRecord("maxSpeed", iMaxValue + "");
        }
    }

    public static void setMaxRotatingSpeed(int maxRotatingSpeed) {
        iMaxRotatingSpeed = maxRotatingSpeed;
        iMaxValue = (float) iMaxRotatingSpeed;
    }

    public static void setbIsRotatingSpeed(boolean isRotatingSpeed) {
        bIsRotatingSpeed = isRotatingSpeed;
    }

    public void removeHandlerMess() {
        if (this.tickHandler != null) {
            if (this.tickRunnable != null) {
                this.tickHandler.removeCallbacks(this.tickRunnable);
                this.tickRunnable = null;
            }
            this.tickHandler = null;
        }
    }

    public void setmTvCur(TextView tv) {
        this.mTvCur = tv;
    }
}
