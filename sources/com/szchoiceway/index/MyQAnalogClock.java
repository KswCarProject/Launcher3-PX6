package com.szchoiceway.index;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

public class MyQAnalogClock extends View {
    int availableHeight;
    int availableWidth;
    BitmapDrawable bmdDial;
    BitmapDrawable bmdSecond;
    int centerX;
    int centerY;
    int fadongjizhuansu;
    Bitmap mBmpDial;
    Bitmap mBmpSecond;
    int mHeigh;
    Paint mPaint;
    int mTempHeigh;
    int mTempWidth;
    int mWidth;
    Handler tickHandler;
    /* access modifiers changed from: private */
    public Runnable tickRunnable;

    public MyQAnalogClock(Context context, AttributeSet attr) {
        this(context);
    }

    public MyQAnalogClock(Context context) {
        super(context);
        this.mTempWidth = 0;
        this.availableWidth = 100;
        this.availableHeight = 100;
        this.fadongjizhuansu = -1;
        this.tickRunnable = new Runnable() {
            public void run() {
                if (MyQAnalogClock.this.fadongjizhuansu != Launcher.ksw_m_i_audio_right_fadongjizhuansu) {
                    MyQAnalogClock.this.fadongjizhuansu = Launcher.ksw_m_i_audio_right_fadongjizhuansu;
                    MyQAnalogClock.this.postInvalidate();
                }
                MyQAnalogClock.this.tickHandler.postDelayed(MyQAnalogClock.this.tickRunnable, 50);
            }
        };
        this.mBmpSecond = BitmapFactory.decodeResource(getResources(), R.drawable.kesaiwei_1280x480_audi_right_zhizhen_red);
        this.bmdSecond = new BitmapDrawable(this.mBmpSecond);
        this.mBmpDial = BitmapFactory.decodeResource(getResources(), R.drawable.kesaiwei_1280x480_audi_right_biaopandi);
        this.bmdDial = new BitmapDrawable(this.mBmpDial);
        this.mWidth = this.mBmpDial.getWidth();
        this.mHeigh = this.mBmpDial.getHeight();
        int availableWidth2 = this.mWidth;
        int availableHeight2 = this.mHeigh;
        this.centerX = availableWidth2 / 2;
        this.centerY = availableHeight2 / 2;
        this.mPaint = new Paint();
        this.mPaint.setColor(-16776961);
        run();
    }

    public void run() {
        this.tickHandler = new Handler();
        this.tickHandler.post(this.tickRunnable);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (this.fadongjizhuansu < 0) {
            this.fadongjizhuansu = 0;
        } else if (this.fadongjizhuansu > 8000) {
            this.fadongjizhuansu = 8000;
        }
        float second = (float) ((((double) this.fadongjizhuansu) / 8000.0d) * 240.0d);
        float secondRotate = 0.0f;
        if (second >= 0.0f && second <= 120.0f) {
            secondRotate = second + 240.0f;
        } else if (second > 120.0f && second <= 240.0f) {
            secondRotate = second - 120.0f;
        }
        this.bmdDial.setBounds(this.centerX - (this.mWidth / 2), this.centerY - (this.mHeigh / 2), this.centerX + (this.mWidth / 2), this.centerY + (this.mHeigh / 2));
        this.bmdDial.draw(canvas);
        this.mTempWidth = this.bmdSecond.getIntrinsicWidth();
        this.mTempHeigh = this.bmdSecond.getIntrinsicHeight();
        canvas.rotate(secondRotate, (float) this.centerX, (float) this.centerY);
        this.bmdSecond.setBounds(this.centerX - (this.mTempWidth / 2), this.centerY - (this.mTempHeigh / 2), this.centerX + (this.mTempWidth / 2), this.centerY + (this.mTempHeigh / 2));
        this.bmdSecond.draw(canvas);
        canvas.restore();
    }
}
