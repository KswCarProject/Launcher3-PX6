package com.android.launcher3.views;

import android.annotation.SuppressLint;
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
import com.android.launcher3.R;

public class MyQAnalogClock2 extends View {
    private static final String TAG = "MyQAnalogClock2";
    private static final int ZXW_DASH_BOARD_REFRESH_UI = 1004;
    private static boolean bIsRotatingSpeed = false;
    private static int iDeviationScale = 120;
    private static int iMaxRotatingSpeed = 8000;
    private static int iMaxSpeed = 280;
    private static float iMaxValue = ((float) iMaxSpeed);
    private static int iTotalScale = 240;
    private static int mDialDrawableId = R.drawable.shisubiaopan;
    private static int mPointerDrawableId = R.drawable.shisuzhizhen;
    int availableHeight;
    int availableWidth;
    BitmapDrawable bmdDial;
    BitmapDrawable bmdSecond;
    int centerX;
    int centerY;
    /* access modifiers changed from: private */
    public float iCurSpeedValue;
    /* access modifiers changed from: private */
    public int iSpeedValue;
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
        this.iCurSpeedValue = 0.0f;
        this.tickRunnable = new Runnable() {
            public void run() {
                if (MyQAnalogClock2.this.iCurSpeedValue != ((float) MyQAnalogClock2.this.iSpeedValue)) {
                    if (Math.abs(MyQAnalogClock2.this.iCurSpeedValue - ((float) MyQAnalogClock2.this.iSpeedValue)) < 1.0f) {
                        float unused = MyQAnalogClock2.this.iCurSpeedValue = (float) MyQAnalogClock2.this.iSpeedValue;
                    } else if (Math.abs(MyQAnalogClock2.this.iCurSpeedValue - ((float) MyQAnalogClock2.this.iSpeedValue)) <= 2.0f) {
                        float unused2 = MyQAnalogClock2.this.iCurSpeedValue = MyQAnalogClock2.this.iCurSpeedValue + ((((float) MyQAnalogClock2.this.iSpeedValue) - MyQAnalogClock2.this.iCurSpeedValue) / 2.0f);
                    } else if (((float) MyQAnalogClock2.this.iSpeedValue) > MyQAnalogClock2.this.iCurSpeedValue) {
                        float unused3 = MyQAnalogClock2.this.iCurSpeedValue = MyQAnalogClock2.this.iCurSpeedValue + ((float) Math.round(((double) (((float) MyQAnalogClock2.this.iSpeedValue) - MyQAnalogClock2.this.iCurSpeedValue)) / 5.0d));
                    } else {
                        float unused4 = MyQAnalogClock2.this.iCurSpeedValue = MyQAnalogClock2.this.iCurSpeedValue + ((float) Math.floor(((double) (((float) MyQAnalogClock2.this.iSpeedValue) - MyQAnalogClock2.this.iCurSpeedValue)) / 5.0d));
                    }
                    MyQAnalogClock2.this.refreshTvCurView();
                    MyQAnalogClock2.this.postInvalidate();
                }
                MyQAnalogClock2.this.tickHandler.postDelayed(MyQAnalogClock2.this.tickRunnable, 50);
            }
        };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyQAnalogClock);
        bIsRotatingSpeed = typedArray.getBoolean(2, bIsRotatingSpeed);
        Log.i(TAG, "MyQAnalogClock2: bIsRotatingSpeed = " + bIsRotatingSpeed);
        if (bIsRotatingSpeed) {
            mDialDrawableId = R.drawable.shisuzhizhen;
            mPointerDrawableId = R.drawable.shisubiaopan;
            iMaxValue = (float) iMaxRotatingSpeed;
        }
        mDialDrawableId = typedArray.getResourceId(1, mDialDrawableId);
        mPointerDrawableId = typedArray.getResourceId(3, mPointerDrawableId);
        iTotalScale = typedArray.getInt(4, iTotalScale);
        iDeviationScale = typedArray.getInt(0, iDeviationScale);
        typedArray.recycle();
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
    @SuppressLint({"SetTextI18n"})
    public void refreshTvCurView() {
        if (this.mTvCur != null) {
            TextView textView = this.mTvCur;
            textView.setText(((int) this.iCurSpeedValue) + "");
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: iCurSpeedValue = " + (this.iCurSpeedValue / 20.0f));
        Log.i(TAG, "onDraw: iMaxValue = " + iMaxValue);
        Log.i(TAG, "onDraw: iTotalScale = " + iTotalScale);
        if (this.iCurSpeedValue < 0.0f) {
            this.iCurSpeedValue = 0.0f;
        } else if (this.iCurSpeedValue > iMaxValue) {
            this.iCurSpeedValue = (float) ((int) iMaxValue);
        }
        float second = (this.iCurSpeedValue / iMaxValue) * ((float) iTotalScale);
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
    }

    public static void setDeviationScale(int deviationScale) {
        iDeviationScale = deviationScale;
    }

    public static void setMaxSpeed(int maxSpeed) {
        iMaxSpeed = maxSpeed;
        iMaxValue = (float) iMaxSpeed;
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

    public void setiSpeedValue(int iSpeedValue2) {
        this.iSpeedValue = iSpeedValue2;
    }
}
