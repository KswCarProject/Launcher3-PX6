package com.szchoiceway.index.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.szchoiceway.index.R;
import java.util.Calendar;

public class MyClockView extends View {
    private static final int SETTING_MIMUTE = 1;
    private static final int SETTING_SECOND = 0;
    private static final String TAG = "MyClockView";
    private boolean bInitComplete;
    private int clockCenterX;
    private int clockCenterY;
    private int clockDrawableId;
    private int clockHeith;
    private int clockWidth;
    private int clockX;
    private int clockY;
    private int hourDrawableId;
    private boolean isMoving;
    private boolean isUserTime;
    private Bitmap mClockBitmap;
    private Context mContext;
    private MyTime mCurTime;
    private Bitmap mHourBitmap;
    private int mHourOffsetY;
    private int mHourPosX;
    private int mHourPosY;
    private Bitmap mMinuteBitmap;
    private int mMinuteOffsetY;
    private int mMinutePosX;
    private int mMinutePosY;
    private Bitmap mSecondBitmap;
    private int mSecondOffsetY;
    private int mSecondPosX;
    private int mSecondPosY;
    private int minuteDrawableId;
    private Paint paint;
    private int secondDrawableId;
    Handler tickHandler;
    /* access modifiers changed from: private */
    public Runnable tickRunnable;
    WindowManager windowManager;

    public MyClockView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MyClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mHourOffsetY = 18;
        this.mMinuteOffsetY = 18;
        this.mSecondOffsetY = 18;
        this.bInitComplete = false;
        this.isUserTime = false;
        this.isMoving = false;
        this.clockDrawableId = R.drawable.dial_bk_ksw;
        this.hourDrawableId = R.drawable.hand_hour_ksw_2;
        this.minuteDrawableId = R.drawable.hand_minute_ksw_2;
        this.secondDrawableId = R.drawable.hand_second_ksw_2;
        this.tickRunnable = new Runnable() {
            public void run() {
                MyClockView.this.postInvalidate();
                MyClockView.this.tickHandler.postDelayed(MyClockView.this.tickRunnable, 50);
            }
        };
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyClockView);
        this.clockDrawableId = typedArray.getResourceId(0, this.clockDrawableId);
        this.hourDrawableId = typedArray.getResourceId(1, this.hourDrawableId);
        this.minuteDrawableId = typedArray.getResourceId(2, this.minuteDrawableId);
        this.secondDrawableId = typedArray.getResourceId(3, this.secondDrawableId);
        this.mContext = context;
        this.windowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mCurTime = new MyTime();
        init();
        run();
    }

    public void run() {
        this.tickHandler = new Handler();
        this.tickHandler.post(this.tickRunnable);
    }

    public void init() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.mClockBitmap = BitmapFactory.decodeResource(getResources(), this.clockDrawableId);
        this.clockWidth = this.mClockBitmap.getWidth();
        this.clockHeith = this.mClockBitmap.getHeight();
        Log.i(TAG, "init: clockWidth = " + this.clockWidth + ", clockHeith = " + this.clockHeith);
        this.clockX = (this.windowManager.getDefaultDisplay().getWidth() / 2) - (this.clockWidth / 2);
        this.clockY = ((this.windowManager.getDefaultDisplay().getHeight() / 2) - this.clockHeith) + 80;
        Log.i(TAG, "init: clockX = " + this.clockX + ", clockY = " + this.clockY);
        this.mHourBitmap = BitmapFactory.decodeResource(getResources(), this.hourDrawableId);
        this.mMinuteBitmap = BitmapFactory.decodeResource(getResources(), this.minuteDrawableId);
        this.mSecondBitmap = BitmapFactory.decodeResource(getResources(), this.secondDrawableId);
        calcPointPosition();
        calcCenter();
        this.bInitComplete = true;
        this.mCurTime.initBySystem();
    }

    public void setClockXY(int clockX2, int clockY2) {
        this.clockX = clockX2 - (this.clockWidth / 2);
        this.clockY = clockY2 - this.clockHeith;
        calcCenter();
    }

    public void setPointOffset(int hourOffset, int minuteOffset, int secondOffset) {
        this.mHourOffsetY = hourOffset;
        this.mMinuteOffsetY = minuteOffset;
        this.mSecondOffsetY = secondOffset;
        calcPointPosition();
    }

    public void calcCenter() {
        if (this.mClockBitmap != null) {
            this.clockCenterX = this.clockX + (this.mClockBitmap.getWidth() / 2);
            this.clockCenterY = this.clockY + (this.mClockBitmap.getHeight() / 2);
        }
    }

    public void calcPointPosition() {
        if (this.mHourBitmap != null) {
            int w = this.mHourBitmap.getWidth();
            int h = this.mHourBitmap.getHeight();
            this.mHourPosX = (-w) / 2;
            this.mHourPosY = (-h) / 2;
        }
        if (this.mMinuteBitmap != null) {
            int w2 = this.mMinuteBitmap.getWidth();
            int h2 = this.mMinuteBitmap.getHeight();
            this.mMinutePosX = (-w2) / 2;
            this.mMinutePosY = (-h2) / 2;
        }
        if (this.mSecondBitmap != null) {
            int w3 = this.mSecondBitmap.getWidth();
            int h3 = this.mSecondBitmap.getHeight();
            this.mSecondPosX = (-w3) / 2;
            this.mSecondPosY = (-h3) / 2;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.bInitComplete) {
            canvas.save();
            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, 3));
            drawClock(canvas);
            drawHour(canvas);
            drawMinute(canvas);
            drawSecond(canvas);
            if (!this.isUserTime) {
                this.mCurTime.initBySystem();
            } else {
                initUserTime();
            }
            canvas.restore();
        }
    }

    private void initUserTime() {
        Calendar.getInstance().setTimeInMillis(System.currentTimeMillis());
        int currentHour = this.mCurTime.mHour;
        int currentMinute = this.mCurTime.mMinute;
        int currentScond = this.mCurTime.mSecond;
        if (!this.isMoving) {
            SystemClock.sleep(1000);
            currentScond++;
        }
        if (currentScond == 60) {
            currentScond = 0;
            currentMinute++;
            if (currentMinute == 60) {
                currentMinute = 0;
                currentHour++;
                if (currentHour == 12) {
                    currentHour = 0;
                }
            }
        }
        this.mCurTime.mHour = currentHour;
        this.mCurTime.mMinute = currentMinute;
        this.mCurTime.mSecond = currentScond;
        this.mCurTime.calcDegreeByTime();
    }

    public void drawClock(Canvas canvas) {
        if (this.mClockBitmap != null) {
            canvas.drawBitmap(this.mClockBitmap, (float) this.clockX, (float) this.clockY, (Paint) null);
        }
    }

    private void drawHour(Canvas canvas) {
        if (this.mHourBitmap != null) {
            canvas.save();
            canvas.translate((float) this.clockCenterX, (float) this.clockCenterY);
            canvas.rotate((float) this.mCurTime.mHourDegree);
            canvas.drawBitmap(this.mHourBitmap, (float) this.mHourPosX, (float) this.mHourPosY, this.paint);
            canvas.restore();
        }
    }

    public void drawMinute(Canvas canvas) {
        if (this.mMinuteBitmap != null) {
            canvas.save();
            canvas.translate((float) this.clockCenterX, (float) this.clockCenterY);
            canvas.rotate((float) this.mCurTime.mMinuteDegree);
            canvas.drawBitmap(this.mMinuteBitmap, (float) this.mMinutePosX, (float) this.mMinutePosY, this.paint);
            canvas.restore();
        }
    }

    public void drawSecond(Canvas canvas) {
        if (this.mSecondBitmap != null) {
            canvas.save();
            canvas.translate((float) this.clockCenterX, (float) this.clockCenterY);
            canvas.rotate((float) this.mCurTime.mSecondDegree);
            canvas.drawBitmap(this.mSecondBitmap, (float) this.mSecondPosX, (float) this.mSecondPosY, this.paint);
            canvas.restore();
        }
    }

    class MyTime {
        private Calendar mCalendar;
        int mHour = 0;
        int mHourDegree = 0;
        int mMinute = 0;
        int mMinuteDegree = 0;
        int mPreDegree = 0;
        int mSecond = 0;
        int mSecondDegree = 0;

        MyTime() {
        }

        public void initBySystem() {
            long time = System.currentTimeMillis();
            this.mCalendar = Calendar.getInstance();
            this.mCalendar.setTimeInMillis(time);
            this.mHour = this.mCalendar.get(11);
            this.mMinute = this.mCalendar.get(12);
            this.mSecond = this.mCalendar.get(13);
            calcDegreeByTime();
        }

        public void calcDegreeByTime() {
            this.mSecondDegree = this.mSecond * 6;
            this.mPreDegree = this.mSecondDegree;
            this.mMinuteDegree = (this.mMinute * 6) + (this.mSecond / 10);
            this.mHourDegree = ((this.mHour % 12) * 30) + (this.mMinuteDegree / 12);
        }

        public void calcTime(boolean bFlag) {
            if (this.mSecondDegree >= 360) {
                this.mSecondDegree -= 360;
            }
            if (this.mSecondDegree < 0) {
                this.mSecondDegree += 360;
            }
            this.mSecond = (int) ((((double) this.mSecondDegree) / 360.0d) * 60.0d);
            if (deasil()) {
                if (this.mSecondDegree < this.mPreDegree) {
                    this.mMinute++;
                    if (this.mMinute == 60) {
                        this.mMinute = 0;
                        this.mHour++;
                    }
                }
            } else if (this.mSecondDegree > this.mPreDegree) {
                this.mMinute--;
                if (this.mMinute < 0) {
                    this.mMinute += 60;
                    this.mHour--;
                }
            }
            this.mMinuteDegree = (this.mMinute * 6) + (this.mSecond / 10);
            this.mPreDegree = this.mSecondDegree;
            Log.i(MyClockView.TAG, "mHourDegree = " + this.mHourDegree + ", mMinuteDegree = " + this.mMinuteDegree);
            if (bFlag) {
                calcDegreeByTime();
            }
        }

        public boolean deasil() {
            if (this.mSecondDegree >= this.mPreDegree) {
                if (this.mSecondDegree - this.mPreDegree < 180) {
                    return true;
                }
                return false;
            } else if (this.mPreDegree - this.mSecondDegree <= 180) {
                return false;
            } else {
                return true;
            }
        }
    }

    public void calcDegree(int x, int y, boolean flag) {
        Point point = new Point(x - this.clockCenterX, -(y - this.clockCenterY));
        this.mCurTime.mSecondDegree = MyDegreeAdapter.GetRadianByPos(point);
        this.mCurTime.calcTime(flag);
    }
}
