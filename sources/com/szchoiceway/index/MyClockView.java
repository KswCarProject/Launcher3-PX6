package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import java.util.Calendar;

public class MyClockView extends View {
    private boolean bInitComplete;
    private int clockCenterX;
    private int clockCenterY;
    private int clockHeith;
    private int clockWidth;
    private int clockX;
    private int clockY;
    private boolean isMoving;
    private boolean isUserTime;
    private Bitmap mClockBitmap;
    private MyTime mCurTime;
    private Bitmap mHourBitmap;
    private int mHourOffsetY;
    private int mHourPosX;
    private int mHourPosY;
    private int mMinuteOffsetY;
    private int mSecondOffsetY;
    private Paint paint;
    WindowManager windowManager;

    public MyClockView(Context context) {
        this(context, (AttributeSet) null);
    }

    public MyClockView(Context context, AttributeSet attrs) {
        this(context, (AttributeSet) null, 0);
    }

    public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.windowManager = (WindowManager) getContext().getSystemService("window");
        this.mHourOffsetY = 18;
        this.mMinuteOffsetY = 18;
        this.mSecondOffsetY = 18;
        this.bInitComplete = false;
        this.isUserTime = false;
        this.isMoving = false;
        this.mCurTime = new MyTime();
        init();
    }

    public void init() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.mClockBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kesaiwei_1280x480_audi_right_biaopandi);
        this.clockWidth = this.mClockBitmap.getWidth();
        this.clockHeith = this.mClockBitmap.getHeight();
        this.clockX = (this.windowManager.getDefaultDisplay().getWidth() / 2) - (this.clockWidth / 2);
        this.clockY = ((this.windowManager.getDefaultDisplay().getHeight() / 2) - this.clockHeith) + 50;
        this.mHourBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kesaiwei_1280x480_audi_right_zhizhen);
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
            this.mHourPosY = (-h) + this.mHourOffsetY;
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.bInitComplete) {
            canvas.save();
            drawClock(canvas);
            drawHour(canvas);
            if (!this.isUserTime) {
                this.mCurTime.initBySystem();
            } else {
                initUserTime();
            }
            postInvalidate();
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
            Log.i("TAG", "mHourDegree = " + this.mHourDegree + ", mMinuteDegree = " + this.mMinuteDegree);
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
