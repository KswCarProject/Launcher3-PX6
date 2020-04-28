package com.android.launcher3.views;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import com.android.launcher3.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class LoopRotarySwitchView extends RelativeLayout {
    private static final int LoopR = 200;
    private static final int horizontal = 1;
    private static final int vertical = 0;
    /* access modifiers changed from: private */
    public float angle;
    /* access modifiers changed from: private */
    public AutoScrollDirection autoRotatinDirection;
    private boolean autoRotation;
    private float distance;
    /* access modifiers changed from: private */
    public boolean isCanClickListener;
    /* access modifiers changed from: private */
    public boolean isSlide;
    private float last_angle;
    private float limitX;
    @SuppressLint({"HandlerLeak"})
    LoopRotarySwitchViewHandler loopHandler;
    /* access modifiers changed from: private */
    public int loopRotationX;
    /* access modifiers changed from: private */
    public int loopRotationZ;
    private Context mContext;
    private GestureDetector mGestureDetector;
    private int mOrientation;
    private float multiple;
    /* access modifiers changed from: private */
    public OnItemClickListener onItemClickListener;
    /* access modifiers changed from: private */
    public OnItemSelectedListener onItemSelectedListener;
    private OnLoopViewTouchListener onLoopViewTouchListener;
    /* access modifiers changed from: private */
    public float r;
    private ValueAnimator rAnimation;
    private ValueAnimator restAnimator;
    /* access modifiers changed from: private */
    public int selectItem;
    /* access modifiers changed from: private */
    public int size;
    /* access modifiers changed from: private */
    public boolean touching;
    /* access modifiers changed from: private */
    public double v;
    /* access modifiers changed from: private */
    public List<View> views;
    private float x;
    private ValueAnimator xAnimation;
    private ValueAnimator zAnimation;

    public enum AutoScrollDirection {
        left,
        right
    }

    public interface OnItemClickListener {
        void onItemClick(int i, View view);
    }

    public interface OnItemSelectedListener {
        void selected(int i, View view);
    }

    public interface OnLoopViewTouchListener {
        void onTouch(MotionEvent motionEvent);
    }

    public LoopRotarySwitchView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LoopRotarySwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoopRotarySwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mOrientation = 1;
        this.restAnimator = null;
        this.rAnimation = null;
        this.zAnimation = null;
        this.xAnimation = null;
        this.loopRotationX = 0;
        this.loopRotationZ = 0;
        this.mGestureDetector = null;
        this.selectItem = 0;
        this.size = 0;
        this.r = 200.0f;
        this.multiple = 2.0f;
        this.distance = this.multiple * this.r;
        this.angle = 0.0f;
        this.last_angle = 0.0f;
        this.autoRotation = false;
        this.touching = false;
        this.autoRotatinDirection = AutoScrollDirection.left;
        this.views = new ArrayList();
        this.onItemSelectedListener = null;
        this.onLoopViewTouchListener = null;
        this.onItemClickListener = null;
        this.isCanClickListener = true;
        this.limitX = 30.0f;
        this.loopHandler = new LoopRotarySwitchViewHandler(Math.abs((int) (10.0d - this.v))) {
            public void doScroll() {
                try {
                    if (LoopRotarySwitchView.this.size != 0) {
                        float perAngle = 0.0f;
                        switch (AnonymousClass10.$SwitchMap$com$android$launcher3$views$LoopRotarySwitchView$AutoScrollDirection[LoopRotarySwitchView.this.autoRotatinDirection.ordinal()]) {
                            case 1:
                                perAngle = (float) (360 / LoopRotarySwitchView.this.size);
                                break;
                            case 2:
                                perAngle = (float) (-360 / LoopRotarySwitchView.this.size);
                                break;
                        }
                        if (LoopRotarySwitchView.this.angle == 360.0f) {
                            float unused = LoopRotarySwitchView.this.angle = 0.0f;
                        }
                        LoopRotarySwitchView.this.AnimRotationTo(LoopRotarySwitchView.this.angle + perAngle, (Runnable) null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.isSlide = true;
        this.mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoopRotarySwitchView);
        this.mOrientation = typedArray.getInt(2, 1);
        this.autoRotation = typedArray.getBoolean(0, false);
        this.r = typedArray.getDimension(3, 200.0f);
        int direction = typedArray.getInt(1, 0);
        typedArray.recycle();
        this.mGestureDetector = new GestureDetector(context, getGeomeryController());
        if (this.mOrientation == 1) {
            this.loopRotationZ = 0;
        } else {
            this.loopRotationZ = 90;
        }
        if (direction == 0) {
            this.autoRotatinDirection = AutoScrollDirection.left;
        } else {
            this.autoRotatinDirection = AutoScrollDirection.right;
        }
        this.loopHandler.setLoop(this.autoRotation);
    }

    /* renamed from: com.android.launcher3.views.LoopRotarySwitchView$10  reason: invalid class name */
    static /* synthetic */ class AnonymousClass10 {
        static final /* synthetic */ int[] $SwitchMap$com$android$launcher3$views$LoopRotarySwitchView$AutoScrollDirection = new int[AutoScrollDirection.values().length];

        static {
            try {
                $SwitchMap$com$android$launcher3$views$LoopRotarySwitchView$AutoScrollDirection[AutoScrollDirection.left.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$launcher3$views$LoopRotarySwitchView$AutoScrollDirection[AutoScrollDirection.right.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    private <T> void sortList(List<View> list) {
        Comparator comparator = new SortComparator();
        T[] array = list.toArray(new Object[list.size()]);
        Arrays.sort(array, comparator);
        int i = 0;
        ListIterator<View> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            listIterator.set(array[i]);
            i++;
        }
        for (int j = 0; j < list.size(); j++) {
            list.get(j).bringToFront();
        }
    }

    private class SortComparator implements Comparator<View> {
        private SortComparator() {
        }

        public int compare(View lhs, View rhs) {
            try {
                return (int) ((lhs.getScaleX() * 1000.0f) - (rhs.getScaleX() * 1000.0f));
            } catch (Exception e) {
                return 0;
            }
        }
    }

    private GestureDetector.SimpleOnGestureListener getGeomeryController() {
        return new GestureDetector.SimpleOnGestureListener() {
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.i("TAG", "LoopRSV-onScroll手勢调用了...");
                Log.i("TAG", "LoopRSV-onScroll-distanceX = " + distanceX + "distanceY = " + distanceY);
                double unused = LoopRotarySwitchView.this.v = Math.abs((Math.cos(Math.toRadians((double) LoopRotarySwitchView.this.loopRotationZ)) * ((double) (distanceX / 4.0f))) + (Math.sin(Math.toRadians((double) LoopRotarySwitchView.this.loopRotationZ)) * ((double) (distanceY / 4.0f))));
                float unused2 = LoopRotarySwitchView.this.angle = (float) (((double) LoopRotarySwitchView.this.angle) + (Math.cos(Math.toRadians((double) LoopRotarySwitchView.this.loopRotationZ)) * ((double) (distanceX / 4.0f))) + (Math.sin(Math.toRadians((double) LoopRotarySwitchView.this.loopRotationZ)) * ((double) (distanceY / 4.0f))));
                Log.i("TAG", "LoopRSV-onScroll-angle =" + LoopRotarySwitchView.this.angle);
                Log.i("TAG", "LoopRSV-onScroll-v =" + LoopRotarySwitchView.this.v);
                LoopRotarySwitchView.this.initView();
                return true;
            }
        };
    }

    public void initView() {
        for (int i = 0; i < this.views.size(); i++) {
            double radians = (double) ((this.angle + 180.0f) - ((float) ((i * 360) / this.size)));
            double radians2 = radians % 360.0d;
            if (radians2 < 0.0d) {
                radians2 += 360.0d;
            }
            float x0 = ((float) Math.sin(Math.toRadians(radians))) * this.r;
            float scale0 = (this.distance - (((float) Math.cos(Math.toRadians(radians))) * this.r)) / (this.distance + this.r);
            this.views.get(i).setScaleX(scale0);
            this.views.get(i).setScaleY(scale0);
            this.views.get(i).setTranslationX(x0 + ((((float) Math.cos(Math.toRadians((double) (-this.loopRotationZ)))) * x0) - x0));
            this.views.get(i).setTranslationY((((float) Math.sin(Math.toRadians(((double) this.loopRotationX) * Math.cos(Math.toRadians(radians))))) * this.r) + ((-((float) Math.sin(Math.toRadians((double) (-this.loopRotationZ))))) * x0));
            if (324.0d >= radians2 || radians2 >= 360.0d) {
                double d = radians;
                if (216.0d < radians2 && radians2 < 252.0d) {
                    this.views.get(i).setAlpha((float) ((252.0d - radians2) / ((double) (360 / this.size))));
                } else if (252.0d > radians2 || radians2 > 324.0d) {
                    this.views.get(i).setAlpha(1.0f);
                    this.views.get(i).setClickable(true);
                } else {
                    this.views.get(i).setAlpha(0.0f);
                    this.views.get(i).setClickable(false);
                }
            } else {
                double d2 = radians;
                this.views.get(i).setAlpha((float) (1.0d - ((360.0d - radians2) / ((double) (360 / this.size)))));
            }
        }
        int i2 = 0;
        List<View> arrayViewList = new ArrayList<>();
        arrayViewList.clear();
        while (true) {
            int i3 = i2;
            if (i3 < this.views.size()) {
                arrayViewList.add(this.views.get(i3));
                i2 = i3 + 1;
            } else {
                sortList(arrayViewList);
                postInvalidate();
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initView();
        if (this.autoRotation) {
            this.loopHandler.sendEmptyMessageDelayed(1000, this.loopHandler.loopTime);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r2, int b) {
        super.onLayout(changed, l, t, r2, b);
        if (changed) {
            checkChildView();
            if (this.onItemSelectedListener != null) {
                this.isCanClickListener = true;
                this.onItemSelectedListener.selected(this.selectItem, this.views.get(this.selectItem));
            }
            RAnimation();
        }
    }

    public void RAnimation() {
        RAnimation(1.0f, this.r);
    }

    public void RAnimation(boolean fromZeroToLoopR) {
        if (fromZeroToLoopR) {
            RAnimation(1.0f, 200.0f);
        } else {
            RAnimation(200.0f, 1.0f);
        }
    }

    public void RAnimation(float from, float to) {
        this.rAnimation = ValueAnimator.ofFloat(new float[]{from, to});
        this.rAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float unused = LoopRotarySwitchView.this.r = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                LoopRotarySwitchView.this.initView();
            }
        });
        Log.i("TAG", "RAnimation-onAnimationUpdate-半径动画...");
        this.rAnimation.setInterpolator(new DecelerateInterpolator());
        this.rAnimation.setDuration(0);
        this.rAnimation.start();
    }

    public void checkChildView() {
        for (int i = 0; i < this.views.size(); i++) {
            this.views.remove(i);
        }
        int count = getChildCount();
        this.size = count;
        for (int i2 = 0; i2 < count; i2++) {
            View view = getChildAt(i2);
            final int position = i2;
            this.views.add(view);
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (LoopRotarySwitchView.this.isCanClickListener && LoopRotarySwitchView.this.onItemClickListener != null) {
                        LoopRotarySwitchView.this.onItemClickListener.onItemClick(position, (View) LoopRotarySwitchView.this.views.get(position));
                    }
                }
            });
        }
    }

    public void restPosition() {
        float finall;
        if (this.size != 0) {
            float part = (float) (360 / this.size);
            if (this.angle < 0.0f) {
                part = -part;
            }
            float minvalue = ((float) ((int) (this.angle / part))) * part;
            float maxvalue = (((float) ((int) (this.angle / part))) * part) + part;
            if (this.angle >= 0.0f) {
                if (this.angle - this.last_angle > 0.0f) {
                    finall = maxvalue;
                } else {
                    finall = minvalue;
                }
            } else if (this.angle - this.last_angle < 0.0f) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
            AnimRotationTo(finall, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    public void AnimRotationTo(float finall, final Runnable complete) {
        if (this.angle != finall) {
            this.restAnimator = ValueAnimator.ofFloat(new float[]{this.angle, finall});
            this.restAnimator.setInterpolator(new DecelerateInterpolator());
            this.restAnimator.setDuration(190);
            this.restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!LoopRotarySwitchView.this.touching) {
                        float unused = LoopRotarySwitchView.this.angle = ((Float) animation.getAnimatedValue()).floatValue();
                        LoopRotarySwitchView.this.initView();
                    }
                }
            });
            this.restAnimator.addListener(new Animator.AnimatorListener() {
                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    if (!LoopRotarySwitchView.this.touching) {
                        int unused = LoopRotarySwitchView.this.selectItem = LoopRotarySwitchView.this.calculateItem();
                        if (LoopRotarySwitchView.this.selectItem < 0) {
                            int unused2 = LoopRotarySwitchView.this.selectItem = LoopRotarySwitchView.this.size + LoopRotarySwitchView.this.selectItem;
                        }
                        if (LoopRotarySwitchView.this.onItemSelectedListener != null && LoopRotarySwitchView.this.isSlide) {
                            LoopRotarySwitchView.this.onItemSelectedListener.selected(LoopRotarySwitchView.this.selectItem, (View) LoopRotarySwitchView.this.views.get(LoopRotarySwitchView.this.selectItem));
                        }
                        boolean unused3 = LoopRotarySwitchView.this.isSlide = true;
                    }
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }
            });
            if (complete != null) {
                this.restAnimator.addListener(new Animator.AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }

                    public void onAnimationEnd(Animator animation) {
                        complete.run();
                    }

                    public void onAnimationCancel(Animator animation) {
                    }

                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }
            this.restAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    public int calculateItem() {
        return ((int) (this.angle / ((float) (360 / this.size)))) % this.size;
    }

    private boolean onTouch(MotionEvent event) {
        boolean sc = this.mGestureDetector.onTouchEvent(event);
        if (event.getAction() == 0) {
            this.last_angle = this.angle;
            this.touching = true;
        }
        if (sc) {
            getParent().requestDisallowInterceptTouchEvent(true);
            Log.i("TAG", "触摸 旋转屏幕了...");
        }
        if (event.getAction() != 1 && event.getAction() != 3) {
            return true;
        }
        this.touching = false;
        restPosition();
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            Log.i("TAG", "LoopRotarySwitchView--onTouchEvent--down");
        }
        if (this.onLoopViewTouchListener != null) {
            this.onLoopViewTouchListener.onTouch(event);
        }
        isCanClickListener(event);
        return true;
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouch(ev);
        if (this.onLoopViewTouchListener != null) {
            this.onLoopViewTouchListener.onTouch(ev);
        }
        isCanClickListener(ev);
        return super.dispatchTouchEvent(ev);
    }

    public void isCanClickListener(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.x = event.getX();
                if (this.autoRotation) {
                    this.loopHandler.removeMessages(1000);
                    return;
                }
                return;
            case 1:
            case 3:
                if (this.autoRotation) {
                    this.loopHandler.sendEmptyMessageDelayed(1000, this.loopHandler.loopTime);
                }
                if (event.getX() - this.x > this.limitX || this.x - event.getX() > this.limitX) {
                    this.isCanClickListener = false;
                    return;
                } else {
                    this.isCanClickListener = true;
                    return;
                }
            default:
                return;
        }
    }

    public List<View> getViews() {
        return this.views;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle2) {
        this.angle = angle2;
    }

    public float getDistance() {
        return this.distance;
    }

    public void setDistance(float distance2) {
        this.distance = distance2;
    }

    public float getR() {
        return this.r;
    }

    public int getSelectItem() {
        return this.selectItem;
    }

    public void setSelectItem(int selectItem2, boolean isSlide2) {
        float jiaodu;
        float finall;
        this.isSlide = isSlide2;
        if (selectItem2 >= 0) {
            if (getSelectItem() == 0) {
                if (selectItem2 == this.views.size() - 1) {
                    jiaodu = this.angle - ((float) (360 / this.size));
                } else {
                    jiaodu = this.angle + ((float) (360 / this.size));
                }
            } else if (getSelectItem() == this.views.size() - 1) {
                if (selectItem2 == 0) {
                    jiaodu = this.angle + ((float) (360 / this.size));
                } else {
                    jiaodu = this.angle - ((float) (360 / this.size));
                }
            } else if (selectItem2 > getSelectItem()) {
                jiaodu = this.angle + ((float) (360 / this.size));
            } else {
                jiaodu = this.angle - ((float) (360 / this.size));
            }
            float part = (float) (360 / this.size);
            if (jiaodu < 0.0f) {
                part = -part;
            }
            float minvalue = ((float) ((int) (jiaodu / part))) * part;
            float maxvalue = ((float) ((int) (jiaodu / part))) * part;
            if (jiaodu >= 0.0f) {
                if (jiaodu - this.last_angle > 0.0f) {
                    finall = maxvalue;
                } else {
                    finall = minvalue;
                }
            } else if (jiaodu - this.last_angle < 0.0f) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
            if (this.size > 0) {
                AnimRotationTo(finall, (Runnable) null);
            }
        }
    }

    public LoopRotarySwitchView setR(float r2) {
        this.r = r2;
        this.distance = this.multiple * r2;
        return this;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener2) {
        this.onItemSelectedListener = onItemSelectedListener2;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener2) {
        this.onItemClickListener = onItemClickListener2;
    }

    public OnItemClickListener getOnItemClickListener() {
        if (this.onItemClickListener != null) {
            return this.onItemClickListener;
        }
        return null;
    }

    public void setOnLoopViewTouchListener(OnLoopViewTouchListener onLoopViewTouchListener2) {
        this.onLoopViewTouchListener = onLoopViewTouchListener2;
    }

    public LoopRotarySwitchView setAutoRotation(boolean autoRotation2) {
        this.autoRotation = autoRotation2;
        this.loopHandler.setLoop(autoRotation2);
        return this;
    }

    public long getAutoRotationTime() {
        return this.loopHandler.loopTime;
    }

    public LoopRotarySwitchView setAutoRotationTime(long autoRotationTime) {
        this.loopHandler.setLoopTime(autoRotationTime);
        return this;
    }

    public boolean isAutoRotation() {
        return this.autoRotation;
    }

    public LoopRotarySwitchView setMultiple(float mMultiple) {
        this.multiple = mMultiple;
        return this;
    }

    public LoopRotarySwitchView setAutoScrollDirection(AutoScrollDirection mAutoScrollDirection) {
        this.autoRotatinDirection = mAutoScrollDirection;
        return this;
    }

    public void createXAnimation(int from, int to, boolean start) {
        if (this.xAnimation != null && this.xAnimation.isRunning()) {
            this.xAnimation.cancel();
        }
        this.xAnimation = ValueAnimator.ofInt(new int[]{from, to});
        this.xAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int unused = LoopRotarySwitchView.this.loopRotationX = ((Integer) animation.getAnimatedValue()).intValue();
                LoopRotarySwitchView.this.initView();
            }
        });
        this.xAnimation.setInterpolator(new DecelerateInterpolator());
        this.xAnimation.setDuration(2000);
        if (start) {
            this.xAnimation.start();
        }
    }

    public ValueAnimator createZAnimation(int from, int to, boolean start) {
        if (this.zAnimation != null && this.zAnimation.isRunning()) {
            this.zAnimation.cancel();
        }
        this.zAnimation = ValueAnimator.ofInt(new int[]{from, to});
        this.zAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int unused = LoopRotarySwitchView.this.loopRotationZ = ((Integer) animation.getAnimatedValue()).intValue();
                LoopRotarySwitchView.this.initView();
            }
        });
        this.zAnimation.setInterpolator(new DecelerateInterpolator());
        this.zAnimation.setDuration(2000);
        if (start) {
            this.zAnimation.start();
        }
        return this.zAnimation;
    }

    public LoopRotarySwitchView setOrientation(int mOrientation2) {
        boolean z = true;
        if (mOrientation2 != 1) {
            z = false;
        }
        setHorizontal(z, false);
        return this;
    }

    public LoopRotarySwitchView setHorizontal(boolean horizontal2, boolean anim) {
        if (!anim) {
            if (horizontal2) {
                setLoopRotationZ(0);
            } else {
                setLoopRotationZ(90);
            }
            initView();
        } else if (horizontal2) {
            createZAnimation(getLoopRotationZ(), 0, true);
        } else {
            createZAnimation(getLoopRotationZ(), 90, true);
        }
        return this;
    }

    public LoopRotarySwitchView setLoopRotationX(int loopRotationX2) {
        this.loopRotationX = loopRotationX2;
        return this;
    }

    public LoopRotarySwitchView setLoopRotationZ(int loopRotationZ2) {
        this.loopRotationZ = loopRotationZ2;
        return this;
    }

    public int getLoopRotationX() {
        return this.loopRotationX;
    }

    public int getLoopRotationZ() {
        return this.loopRotationZ;
    }

    public ValueAnimator getRestAnimator() {
        return this.restAnimator;
    }

    public ValueAnimator getrAnimation() {
        return this.rAnimation;
    }

    public void setzAnimation(ValueAnimator zAnimation2) {
        this.zAnimation = zAnimation2;
    }

    public ValueAnimator getzAnimation() {
        return this.zAnimation;
    }

    public void setxAnimation(ValueAnimator xAnimation2) {
        this.xAnimation = xAnimation2;
    }

    public ValueAnimator getxAnimation() {
        return this.xAnimation;
    }
}
