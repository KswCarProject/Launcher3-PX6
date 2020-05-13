package com.szchoiceway.index;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.Scroller;
import com.szchoiceway.index.CoverFlowAdapter;
import java.util.ArrayList;
import org.apache.http.HttpStatus;

public class CoverFlowView<T extends CoverFlowAdapter> extends View {
    private static final float CARD_SCALE = 0.15f;
    private static final int DURATION = 0;
    private static final float FRICTION = 10.0f;
    private static final int LONG_CLICK_DELAY = ViewConfiguration.getLongPressTimeout();
    private static final float MAX_SPEED = 6.0f;
    private static float MOVE_POS_MULTIPLE = 8.0f;
    private static final float MOVE_SPEED_MULTIPLE = 1.0f;
    static final int NO_POSITION = -1;
    protected static final String TAG = "CoverFlowView";
    private static final int TOUCH_MINIMUM_MOVE = 5;
    private final int ALPHA_DATUM = 76;
    protected final int CHILD_SPACING = -200;
    protected final int INVALID_POSITION = -1;
    private int STANDARD_ALPHA;
    protected int VISIBLE_VIEWS = 3;
    private T mAdapter;
    private Runnable mAnimationRunnable;
    private int mChildHeight;
    private Matrix mChildTransfromer;
    private int mChildTranslateY;
    protected int mCoverFlowCenter;
    private CoverFlowListener<T> mCoverFlowListener;
    private Rect mCoverFlowPadding;
    private int mCurrSelectPostion = 0;
    boolean mDataChanged;
    private Paint mDrawChildPaint;
    private PaintFlagsDrawFilter mDrawFilter;
    private boolean mDrawing;
    private float mDuration;
    protected CoverFlowGravity mGravity;
    private SparseArray<int[]> mImageRecorder;
    private int mItemCount;
    public float mLastModeOffset = 0.0f;
    private int mLastOffset;
    protected CoverFlowLayoutMode mLayoutMode;
    /* access modifiers changed from: private */
    public TopImageLongClickListener mLongClickListener;
    private boolean mLongClickPosted;
    private CoverFlowView<T>.LongClickRunnable mLongClickRunnable;
    /* access modifiers changed from: private */
    public boolean mLongClickTriggled;
    private float mOffset;
    private CoverFlowView<T>.RecycleBin mRecycler;
    private Matrix mReflectionTransfromer;
    private int mReflectionTranslateY;
    private ArrayList<Integer> mRemoveReflectionPendingArray;
    private Scroller mScroller;
    private float mStartOffset;
    private float mStartSpeed;
    private long mStartTime;
    private SysProviderOpt mSysProviderOpt = null;
    private int mTopImageIndex;
    private boolean mTouchDown = false;
    private boolean mTouchMoved;
    private RectF mTouchRect;
    private float mTouchStartPos;
    private float mTouchStartX;
    private float mTouchStartY;
    private VelocityTracker mVelocity;
    private int mVisibleChildCount;
    private int mWidth;
    private int m_iModeSet = 0;
    /* access modifiers changed from: private */
    public int m_iUITypeVer = 0;
    private int reflectGap;
    private float reflectHeightFraction;
    private boolean topImageClickEnable = true;
    private float translateX;

    public enum CoverFlowGravity {
        TOP,
        BOTTOM,
        CENTER_VERTICAL
    }

    public enum CoverFlowLayoutMode {
        MATCH_PARENT,
        WRAP_CONTENT
    }

    public interface CoverFlowListener<V extends CoverFlowAdapter> {
        void imageOnTop(CoverFlowView<V> coverFlowView, int i, float f, float f2, float f3, float f4);

        void invalidationCompleted();

        void topImageClicked(CoverFlowView<V> coverFlowView, int i);
    }

    public interface TopImageLongClickListener {
        void onLongClick(int i);
    }

    public CoverFlowView(Context context) {
        super(context);
        init();
    }

    public CoverFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
        init();
    }

    public CoverFlowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttributes(context, attrs);
        init();
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Carousel);
        this.VISIBLE_VIEWS = a.getInt(0, 2) >> 1;
        this.reflectHeightFraction = a.getFraction(1, 100, 0, 0.0f);
        if (this.reflectHeightFraction > 100.0f) {
            this.reflectHeightFraction = 100.0f;
        }
        this.reflectHeightFraction /= 100.0f;
        this.reflectGap = a.getDimensionPixelSize(2, 0);
        this.mGravity = CoverFlowGravity.values()[a.getInt(4, CoverFlowGravity.CENTER_VERTICAL.ordinal())];
        this.mLayoutMode = CoverFlowLayoutMode.values()[a.getInt(5, CoverFlowLayoutMode.WRAP_CONTENT.ordinal())];
        a.recycle();
    }

    private void init() {
        this.mSysProviderOpt = ((LauncherApplication) this.mContext.getApplicationContext()).getProvider();
        this.m_iUITypeVer = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.SET_USER_UI_TYPE, this.m_iUITypeVer);
        this.m_iModeSet = this.mSysProviderOpt.getRecordInteger(SysProviderOpt.KESAIWEI_SYS_MODE_SELECTION, this.m_iModeSet);
        setWillNotDraw(false);
        setClickable(true);
        this.mChildTransfromer = new Matrix();
        this.mReflectionTransfromer = new Matrix();
        this.mTouchRect = new RectF();
        this.mImageRecorder = new SparseArray<>();
        this.mDrawChildPaint = new Paint();
        this.mDrawChildPaint.setAntiAlias(true);
        this.mDrawChildPaint.setFlags(1);
        this.mCoverFlowPadding = new Rect();
        this.mDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.mScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
        this.mRemoveReflectionPendingArray = new ArrayList<>();
    }

    public void setAdapter(T adapter) {
        this.mAdapter = adapter;
        if (this.mAdapter != null) {
            this.mItemCount = this.mAdapter.getCount();
            this.mRecycler = new RecycleBin();
        }
        resetList();
        requestLayout();
    }

    public T getAdapter() {
        return this.mAdapter;
    }

    public void setCoverFlowListener(CoverFlowListener<T> l) {
        this.mCoverFlowListener = l;
    }

    private void resetList() {
        if (this.mRecycler != null) {
            this.mRecycler.clear();
        }
        this.mChildHeight = 0;
        this.mOffset = this.mLastModeOffset;
        this.mLastOffset = -1;
        this.STANDARD_ALPHA = 179 / this.VISIBLE_VIEWS;
        if (this.mGravity == null) {
            this.mGravity = CoverFlowGravity.CENTER_VERTICAL;
        }
        if (this.mLayoutMode == null) {
            this.mLayoutMode = CoverFlowLayoutMode.WRAP_CONTENT;
        }
        this.mImageRecorder.clear();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mAdapter != null) {
            this.mCoverFlowPadding.left = getPaddingLeft();
            this.mCoverFlowPadding.right = getPaddingRight();
            this.mCoverFlowPadding.top = getPaddingTop();
            this.mCoverFlowPadding.bottom = getPaddingBottom();
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
            int visibleCount = (this.VISIBLE_VIEWS << 1) + 1;
            int avaiblableHeight = (heightSize - this.mCoverFlowPadding.top) - this.mCoverFlowPadding.bottom;
            int maxChildTotalHeight = 0;
            for (int i = 0; i < visibleCount; i++) {
                int childHeight = this.mAdapter.getImage(i).getHeight();
                int childTotalHeight = (int) (((float) childHeight) + (((float) childHeight) * this.reflectHeightFraction) + ((float) this.reflectGap));
                if (maxChildTotalHeight < childTotalHeight) {
                    maxChildTotalHeight = childTotalHeight;
                }
            }
            if (heightMode != 1073741824 && heightMode != Integer.MIN_VALUE) {
                Log.i(TAG, "heightMode ***************");
                if (this.mLayoutMode == CoverFlowLayoutMode.MATCH_PARENT) {
                    this.mChildHeight = avaiblableHeight;
                } else if (this.mLayoutMode == CoverFlowLayoutMode.WRAP_CONTENT) {
                    this.mChildHeight = maxChildTotalHeight;
                    heightSize = this.mChildHeight + this.mCoverFlowPadding.top + this.mCoverFlowPadding.bottom;
                }
            } else if (avaiblableHeight < maxChildTotalHeight) {
                this.mChildHeight = avaiblableHeight;
            } else if (this.mLayoutMode == CoverFlowLayoutMode.MATCH_PARENT) {
                this.mChildHeight = avaiblableHeight;
            } else if (this.mLayoutMode == CoverFlowLayoutMode.WRAP_CONTENT) {
                this.mChildHeight = maxChildTotalHeight;
                Log.i(TAG, "mChildHeight ***** = " + this.mChildHeight);
                if (heightMode == Integer.MIN_VALUE) {
                    heightSize = this.mChildHeight + this.mCoverFlowPadding.top + this.mCoverFlowPadding.bottom;
                    Log.i(TAG, "heightSize ***** = " + heightSize);
                }
            }
            if (this.mGravity == CoverFlowGravity.CENTER_VERTICAL) {
                this.mChildTranslateY = (heightSize >> 1) - (this.mChildHeight >> 1);
            } else if (this.mGravity == CoverFlowGravity.TOP) {
                this.mChildTranslateY = this.mCoverFlowPadding.top;
            } else if (this.mGravity == CoverFlowGravity.BOTTOM) {
                this.mChildTranslateY = (heightSize - this.mCoverFlowPadding.bottom) - this.mChildHeight;
            }
            Log.i(TAG, "mChildTranslateY == >" + this.mChildTranslateY);
            this.mReflectionTranslateY = (int) (((float) (this.mChildTranslateY + this.mChildHeight)) - (((float) this.mChildHeight) * this.reflectHeightFraction));
            Log.i(TAG, "mReflectionTranslateY == >" + this.mReflectionTranslateY);
            setMeasuredDimension(widthSize, heightSize);
            this.mVisibleChildCount = visibleCount;
            this.mWidth = widthSize;
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int rightChild;
        if (this.mAdapter == null) {
            super.onDraw(canvas);
            return;
        }
        this.mDrawing = true;
        canvas.setDrawFilter(this.mDrawFilter);
        float offset = this.mOffset;
        int mid = (int) Math.floor(((double) offset) + 0.5d);
        if (this.mVisibleChildCount % 2 == 0) {
            rightChild = (this.mVisibleChildCount >> 1) - 1;
        } else {
            rightChild = this.mVisibleChildCount >> 1;
        }
        for (int i = mid - (this.mVisibleChildCount >> 1); i < mid; i++) {
            drawChild(canvas, i, ((float) i) - offset);
        }
        for (int i2 = mid + rightChild; i2 >= mid; i2--) {
            drawChild(canvas, i2, ((float) i2) - offset);
        }
        if (this.mLastOffset != ((int) offset)) {
            imageOnTop(getActuallyPosition((int) offset));
            this.mLastOffset = (int) offset;
        }
        this.mDrawing = false;
        int removeCount = this.mRemoveReflectionPendingArray.size();
        for (int i3 = 0; i3 < removeCount; i3++) {
            this.mRecycler.removeCachedBitmap(this.mRemoveReflectionPendingArray.get(i3).intValue());
        }
        this.mRemoveReflectionPendingArray.clear();
        super.onDraw(canvas);
        if (this.mCoverFlowListener != null) {
            this.mCoverFlowListener.invalidationCompleted();
        }
    }

    /* access modifiers changed from: protected */
    public final void drawChild(Canvas canvas, int position, float offset) {
        int actuallyPosition = getActuallyPosition(position);
        Bitmap child = this.mAdapter.getImage(actuallyPosition);
        int[] wAndh = this.mImageRecorder.get(actuallyPosition);
        if (wAndh == null) {
            this.mImageRecorder.put(actuallyPosition, new int[]{child.getWidth(), child.getHeight()});
        } else {
            wAndh[0] = child.getWidth();
            wAndh[1] = child.getHeight();
        }
        if (child != null && !child.isRecycled() && canvas != null) {
            makeChildTransfromer(child, position, offset);
            Log.i(TAG, "actuallyPosition ==> " + actuallyPosition);
            if (!this.mTouchDown || this.mCurrSelectPostion != actuallyPosition) {
                this.mDrawChildPaint.setAlpha(255);
            } else {
                this.mDrawChildPaint.setAlpha(128);
            }
            canvas.drawBitmap(child, this.mChildTransfromer, this.mDrawChildPaint);
        }
    }

    private void makeChildTransfromer(Bitmap child, int position, float offset) {
        float scale;
        this.mChildTransfromer.reset();
        this.mReflectionTransfromer.reset();
        if (position == 0) {
            scale = MOVE_SPEED_MULTIPLE - (Math.abs(offset) * CARD_SCALE);
        } else if (this.m_iUITypeVer != 40 && this.m_iUITypeVer != 38 && this.m_iUITypeVer != 44) {
            scale = MOVE_SPEED_MULTIPLE - (Math.abs(offset) * CARD_SCALE);
        } else if (offset < -1.0f || offset > MOVE_SPEED_MULTIPLE) {
            scale = MOVE_SPEED_MULTIPLE - (((float) Math.abs(1)) * 0.25f);
        } else {
            scale = MOVE_SPEED_MULTIPLE - (Math.abs(offset) * 0.25f);
        }
        if (!(this.m_iUITypeVer == 40 || this.m_iUITypeVer == 38 || this.m_iUITypeVer == 44 || scale >= 0.85f)) {
            scale = 0.85f;
        }
        this.translateX = 0.0f;
        int originalChildHeight = (int) ((((float) this.mChildHeight) - (((float) this.mChildHeight) * this.reflectHeightFraction)) - ((float) this.reflectGap));
        int childTotalHeight = (int) (((float) child.getHeight()) + (((float) child.getHeight()) * this.reflectHeightFraction) + ((float) this.reflectGap));
        float originalChildHeightScale = ((float) originalChildHeight) / ((float) child.getHeight());
        float childHeightScale = originalChildHeightScale * scale;
        int childWidth = (int) (((float) child.getWidth()) * childHeightScale);
        int centerChildWidth = (int) (((float) child.getWidth()) * originalChildHeightScale);
        int leftSpace = ((this.mWidth >> 1) - this.mCoverFlowPadding.left) - (centerChildWidth >> 1);
        int rightSpace = ((this.mWidth >> 1) - this.mCoverFlowPadding.right) - (centerChildWidth >> 1);
        if (this.m_iUITypeVer == 40 || this.m_iUITypeVer == 38 || this.m_iUITypeVer == 44) {
            if (offset < -2.0f) {
                this.translateX = ((((float) leftSpace) / ((float) this.VISIBLE_VIEWS)) * (((float) this.VISIBLE_VIEWS) + offset)) + ((float) this.mCoverFlowPadding.left) + (150.0f * (2.0f + offset));
            } else if (offset >= -2.0f && offset <= 0.0f) {
                this.translateX = ((((float) leftSpace) / ((float) this.VISIBLE_VIEWS)) * (((float) this.VISIBLE_VIEWS) + offset)) + ((float) this.mCoverFlowPadding.left);
            } else if (offset > 2.0f) {
                this.translateX = (((((float) this.mWidth) - ((((float) rightSpace) / ((float) this.VISIBLE_VIEWS)) * (((float) this.VISIBLE_VIEWS) - offset))) - ((float) childWidth)) - ((float) this.mCoverFlowPadding.right)) + (150.0f * (offset - 2.0f));
            } else {
                this.translateX = ((((float) this.mWidth) - ((((float) rightSpace) / ((float) this.VISIBLE_VIEWS)) * (((float) this.VISIBLE_VIEWS) - offset))) - ((float) childWidth)) - ((float) this.mCoverFlowPadding.right);
            }
        } else if (offset <= 0.0f) {
            this.translateX = ((((float) leftSpace) / ((float) this.VISIBLE_VIEWS)) * (((float) this.VISIBLE_VIEWS) + offset)) + ((float) this.mCoverFlowPadding.left);
        } else {
            this.translateX = ((((float) this.mWidth) - ((((float) rightSpace) / ((float) this.VISIBLE_VIEWS)) * (((float) this.VISIBLE_VIEWS) - offset))) - ((float) childWidth)) - ((float) this.mCoverFlowPadding.right);
        }
        float alpha = 254.0f - (Math.abs(offset) * ((float) this.STANDARD_ALPHA));
        if (alpha >= 0.0f) {
            if (alpha > 254.0f) {
            }
        }
        this.mChildTransfromer.preTranslate(0.0f, (float) (-(childTotalHeight >> 1)));
        this.mChildTransfromer.postScale(childHeightScale, childHeightScale);
        float adjustedChildTranslateY = 0.0f;
        if (childHeightScale != MOVE_SPEED_MULTIPLE) {
            adjustedChildTranslateY = (float) ((this.mChildHeight - childTotalHeight) >> 1);
        }
        this.mChildTransfromer.postTranslate(this.translateX, ((float) this.mChildTranslateY) + adjustedChildTranslateY);
        getCustomTransformMatrix(this.mChildTransfromer, this.mDrawChildPaint, child, position, offset);
        this.mChildTransfromer.postTranslate(0.0f, (float) (childTotalHeight >> 1));
        this.mReflectionTransfromer.preTranslate(0.0f, (float) (-(childTotalHeight >> 1)));
        this.mReflectionTransfromer.postScale(childHeightScale, childHeightScale);
        this.mReflectionTransfromer.postTranslate(this.translateX, (((float) this.mReflectionTranslateY) * scale) + adjustedChildTranslateY);
        getCustomTransformMatrix(this.mReflectionTransfromer, this.mDrawChildPaint, child, position, offset);
        this.mReflectionTransfromer.postTranslate(0.0f, (float) (childTotalHeight >> 1));
    }

    /* access modifiers changed from: protected */
    public void getCustomTransformMatrix(Matrix transfromer, Paint mDrawChildPaint2, Bitmap child, int position, float offset) {
    }

    private void imageOnTop(int position) {
        int widthInView;
        int heightInView;
        this.mTopImageIndex = position;
        int[] wAndh = this.mImageRecorder.get(position);
        int i = (int) (((float) wAndh[0]) * (((float) ((int) ((((float) this.mChildHeight) - (((float) this.mChildHeight) * this.reflectHeightFraction)) - ((float) this.reflectGap)))) / ((float) wAndh[1])));
        if (this.m_iUITypeVer == 40 || this.m_iUITypeVer == 38 || this.m_iUITypeVer == 44) {
            heightInView = 480;
            widthInView = 640;
        } else {
            heightInView = 600;
            widthInView = 512;
        }
        this.mTouchRect.left = (float) ((this.mWidth >> 1) - (widthInView >> 1));
        this.mTouchRect.top = (float) this.mChildTranslateY;
        this.mTouchRect.right = this.mTouchRect.left + ((float) widthInView);
        this.mTouchRect.bottom = this.mTouchRect.top + ((float) heightInView);
        if (this.mCoverFlowListener != null) {
            this.mCoverFlowListener.imageOnTop(this, position, this.mTouchRect.left, this.mTouchRect.top, this.mTouchRect.right, this.mTouchRect.bottom);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getAction()) {
            case 0:
                if (Launcher.b_sing_coverFlowView) {
                    return false;
                }
                Launcher.b_sing_touch = true;
                Log.i(TAG, "--->>>ACTION: ACTION_DOWN; px = " + event.getX() + ", py = " + event.getY());
                if (this.mScroller.computeScrollOffset()) {
                    this.mScroller.abortAnimation();
                    invalidate();
                }
                stopLongClick();
                triggleLongClick(event.getX(), event.getY());
                touchBegan(event);
                invalidate();
                return true;
            case 1:
                Log.i(TAG, "--->>>ACTION: ACTION_UP; px = " + event.getX() + ", py = " + event.getY());
                this.mTouchDown = false;
                touchEnded(event);
                stopLongClick();
                this.mCurrSelectPostion = -16777215;
                this.mDrawChildPaint.setAlpha(255);
                invalidate();
                Launcher.b_sing_touch = false;
                return true;
            case 2:
                Log.i(TAG, "--->>>ACTION: ACTION_MOVE; px = " + event.getX() + ", py = " + event.getY());
                this.mTouchDown = false;
                this.mDrawChildPaint.setAlpha(255);
                touchMoved(event);
                return true;
            default:
                return false;
        }
    }

    private void triggleLongClick(float x, float y) {
        if (this.mTouchRect.contains(x, y) && this.mLongClickListener != null && this.topImageClickEnable && !this.mLongClickPosted) {
            int actuallyPosition = this.mTopImageIndex;
            Log.i(TAG, "actuallyPosition ==> " + actuallyPosition);
            this.mLongClickRunnable.setPosition(actuallyPosition);
            Log.i(TAG, "LONG_CLICK_DELAY ==> " + LONG_CLICK_DELAY);
            postDelayed(this.mLongClickRunnable, (long) LONG_CLICK_DELAY);
        }
    }

    private void stopLongClick() {
        if (this.mLongClickRunnable != null) {
            removeCallbacks(this.mLongClickRunnable);
            this.mLongClickPosted = false;
            this.mLongClickTriggled = false;
        }
    }

    private void touchBegan(MotionEvent event) {
        Rect rt1;
        Rect rt2;
        Rect rt3;
        Rect rt4;
        Rect rt5;
        endAnimation();
        float x = event.getX();
        this.mTouchStartX = x;
        this.mTouchStartY = event.getY();
        this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
        this.mStartOffset = this.mOffset;
        this.mTouchMoved = false;
        this.mTouchDown = true;
        this.mTouchStartPos = ((x / ((float) this.mWidth)) * MOVE_POS_MULTIPLE) - 5.0f;
        this.mTouchStartPos /= 2.0f;
        this.mVelocity = VelocityTracker.obtain();
        this.mVelocity.addMovement(event);
        Log.i(TAG, "mStartOffset ==> " + this.mStartOffset);
        Log.i(TAG, "mTouchStartX ==> " + this.mTouchStartX);
        Log.i(TAG, "mTouchStartY ==> " + this.mTouchStartY);
        Log.d("CoverFlowViewgetTop()", getTop() + "");
        Log.i(TAG, "--->>> touchBean x = " + event.getX() + ", y = " + event.getY());
        if (this.m_iUITypeVer == 41) {
            Log.i(TAG, "touchBegan: m_iModeSet = " + this.m_iModeSet);
            if (this.m_iModeSet == 9) {
                rt1 = new Rect(46, 41, 174, 158);
                rt2 = new Rect(208, 45, 313, 159);
                rt3 = new Rect(331, 38, 472, 168);
                rt4 = new Rect(484, 41, 597, 164);
                rt5 = new Rect(626, 41, 734, 168);
            } else if (this.m_iModeSet == 10) {
                rt1 = new Rect(50, 28, 241, 190);
                rt2 = new Rect(320, 37, 475, 194);
                rt3 = new Rect(553, 13, 742, 194);
                rt4 = new Rect(821, 28, 985, 188);
                rt5 = new Rect(1077, 30, 1227, 193);
            } else if (this.m_iModeSet == 12) {
                rt1 = new Rect(50, 28, 241, 520);
                rt2 = new Rect(320, 37, 475, 524);
                rt3 = new Rect(553, 13, 742, 524);
                rt4 = new Rect(821, 28, 985, 518);
                rt5 = new Rect(1077, 30, 1227, 523);
            } else {
                rt1 = new Rect(26, 20, 203, 186);
                rt2 = new Rect(238, 31, 380, 185);
                rt3 = new Rect(421, 7, 604, HttpStatus.SC_MULTI_STATUS);
                rt4 = new Rect(625, 41, 793, 181);
                rt5 = new Rect(799, 41, 981, 208);
            }
        } else {
            rt1 = new Rect(200, 10, 335, 175);
            rt2 = new Rect(365, 10, HttpStatus.SC_INTERNAL_SERVER_ERROR, 175);
            rt3 = new Rect(560, 0, 760, 210);
            rt4 = new Rect(800, 10, 935, 175);
            rt5 = new Rect(980, 10, 1115, 175);
        }
        if (rt1.contains((int) event.getX(), (int) event.getY())) {
            this.mCurrSelectPostion = getActuallyPosition(((int) this.mStartOffset) - 2);
            this.mLastModeOffset = this.mStartOffset - 2.0f;
        } else if (rt2.contains((int) event.getX(), (int) event.getY())) {
            this.mCurrSelectPostion = getActuallyPosition(((int) this.mStartOffset) - 1);
            Log.i(TAG, "mCurrSelectPostion ==> " + this.mCurrSelectPostion);
            this.mLastModeOffset = this.mStartOffset - MOVE_SPEED_MULTIPLE;
        } else if (rt3.contains((int) event.getX(), (int) event.getY())) {
            this.mCurrSelectPostion = getActuallyPosition((int) this.mStartOffset);
            Log.i(TAG, "touchBegan mCurrSelectPostion ==> " + this.mCurrSelectPostion);
            this.mLastModeOffset = this.mStartOffset;
        } else if (rt4.contains((int) event.getX(), (int) event.getY())) {
            this.mCurrSelectPostion = getActuallyPosition(((int) this.mStartOffset) + 1);
            Log.i(TAG, "touchBegan mCurrSelectPostion ==> " + this.mCurrSelectPostion);
            this.mLastModeOffset = this.mStartOffset + MOVE_SPEED_MULTIPLE;
        } else if (rt5.contains((int) event.getX(), (int) event.getY())) {
            this.mCurrSelectPostion = getActuallyPosition(((int) this.mStartOffset) + 2);
            Log.i(TAG, "touchBegan mCurrSelectPostion ==> " + this.mCurrSelectPostion);
            this.mLastModeOffset = this.mStartOffset + 2.0f;
        }
    }

    private void touchMoved(MotionEvent event) {
        float pos = (((event.getX() / ((float) this.mWidth)) * MOVE_POS_MULTIPLE) - 5.0f) / 2.0f;
        if (!this.mTouchMoved) {
            float dx = Math.abs(event.getX() - this.mTouchStartX);
            float dy = Math.abs(event.getY() - this.mTouchStartY);
            if (dx >= 5.0f || dy >= 5.0f) {
                this.mTouchMoved = true;
                stopLongClick();
            } else {
                return;
            }
        }
        this.mOffset = (this.mStartOffset + this.mTouchStartPos) - pos;
        invalidate();
        this.mVelocity.addMovement(event);
    }

    private void touchEnded(MotionEvent event) {
        float pos = (((event.getX() / ((float) this.mWidth)) * MOVE_POS_MULTIPLE) - 5.0f) / 2.0f;
        if (this.mTouchMoved || ((double) this.mOffset) - Math.floor((double) this.mOffset) != 0.0d) {
            this.mStartOffset += this.mTouchStartPos - pos;
            this.mOffset = this.mStartOffset;
            this.mVelocity.addMovement(event);
            this.mVelocity.computeCurrentVelocity(1000);
            double speed = (((double) this.mVelocity.getXVelocity()) / ((double) this.mWidth)) * 1.0d;
            if (speed > 6.0d) {
                speed = 6.0d;
            } else if (speed < -6.0d) {
                speed = -6.0d;
            }
            startAnimation(-speed);
        } else if (!(this.mTouchRect == null || this.mCoverFlowListener == null || !this.topImageClickEnable)) {
            int i = this.mTopImageIndex;
            this.mCoverFlowListener.topImageClicked(this, this.mCurrSelectPostion);
        }
        this.mVelocity.clear();
        this.mVelocity.recycle();
    }

    private void startAnimation(double speed) {
        if (this.mAnimationRunnable == null) {
            double delta = (speed * speed) / 20.0d;
            if (speed < 0.0d) {
                delta = -delta;
            }
            double nearest = Math.floor(0.5d + ((double) this.mStartOffset) + delta);
            this.mStartSpeed = (float) Math.sqrt(Math.abs(nearest - ((double) this.mStartOffset)) * 10.0d * 2.0d);
            if (nearest < ((double) this.mStartOffset)) {
                this.mStartSpeed = -this.mStartSpeed;
            }
            this.mDuration = Math.abs(this.mStartSpeed / FRICTION);
            this.mStartTime = AnimationUtils.currentAnimationTimeMillis();
            this.mAnimationRunnable = new Runnable() {
                public void run() {
                    CoverFlowView.this.driveAnimation();
                }
            };
            post(this.mAnimationRunnable);
        }
    }

    /* access modifiers changed from: private */
    public void driveAnimation() {
        float elapsed = ((float) (AnimationUtils.currentAnimationTimeMillis() - this.mStartTime)) / 1000.0f;
        if (elapsed >= this.mDuration) {
            endAnimation();
            return;
        }
        updateAnimationAtElapsed(elapsed);
        post(this.mAnimationRunnable);
    }

    private void endAnimation() {
        if (this.mAnimationRunnable != null) {
            this.mOffset = (float) Math.floor(((double) this.mOffset) + 0.5d);
            invalidate();
            removeCallbacks(this.mAnimationRunnable);
            this.mAnimationRunnable = null;
        }
    }

    private void updateAnimationAtElapsed(float elapsed) {
        if (elapsed > this.mDuration) {
            elapsed = this.mDuration;
        }
        float delta = (Math.abs(this.mStartSpeed) * elapsed) - (((FRICTION * elapsed) * elapsed) / 2.0f);
        if (this.mStartSpeed < 0.0f) {
            delta = -delta;
        }
        this.mOffset = this.mStartOffset + delta;
        invalidate();
    }

    public void invalidatePosition(int position) {
        if (this.mAdapter != null && position >= 0 && position < this.mAdapter.getCount()) {
            if (!this.mDrawing) {
                this.mRecycler.removeCachedBitmap(position);
            } else if (!this.mRemoveReflectionPendingArray.contains(Integer.valueOf(position))) {
                this.mRemoveReflectionPendingArray.add(Integer.valueOf(position));
            }
            if (position >= this.mTopImageIndex - this.VISIBLE_VIEWS && position <= this.mTopImageIndex + this.VISIBLE_VIEWS) {
                invalidate();
            }
        }
    }

    private int getActuallyPosition(int position) {
        if (this.mAdapter == null) {
            return -1;
        }
        int max = this.mAdapter.getCount();
        int position2 = position + this.VISIBLE_VIEWS;
        while (true) {
            if (position2 >= 0 && position2 < max) {
                return position2;
            }
            if (position2 < 0) {
                position2 += max;
            } else if (position2 >= max) {
                position2 -= max;
            }
        }
    }

    public void setVisibleImage(int count) {
        if (count % 2 == 0) {
            throw new IllegalArgumentException("visible image must be an odd number");
        }
        this.VISIBLE_VIEWS = count / 2;
        this.STANDARD_ALPHA = 179 / this.VISIBLE_VIEWS;
    }

    public void setCoverFlowGravity(CoverFlowGravity gravity) {
        this.mGravity = gravity;
    }

    public void setCoverFlowLayoutMode(CoverFlowLayoutMode mode) {
        this.mLayoutMode = mode;
    }

    public void setReflectionHeight(int fraction) {
        if (fraction < 0) {
            fraction = 0;
        } else if (fraction > 100) {
            fraction = 100;
        }
        this.reflectHeightFraction = (float) fraction;
    }

    public void setReflectionGap(int gap) {
        if (gap < 0) {
            gap = 0;
        }
        this.reflectGap = gap;
    }

    public void disableTopImageClick() {
        this.topImageClickEnable = false;
    }

    public void enableTopImageClick() {
        this.topImageClickEnable = true;
    }

    public void setSelection(int position) {
        int max = this.mAdapter.getCount();
        if (position >= 0 && position < max && this.mTopImageIndex != position) {
            if (this.mScroller.computeScrollOffset()) {
                this.mScroller.abortAnimation();
            }
            int from = (int) (this.mOffset * 100.0f);
            this.mScroller.startScroll(from, 0, ((position - this.VISIBLE_VIEWS) * 100) - from, 0, Math.min(Math.abs((position + max) - this.mTopImageIndex), Math.abs(position - this.mTopImageIndex)) * 0);
            invalidate();
        }
    }

    public void computeScroll() {
        super.computeScroll();
        if (this.mScroller.computeScrollOffset()) {
            this.mOffset = ((float) this.mScroller.getCurrX()) / 100.0f;
            invalidate();
        }
    }

    public void setTopImageLongClickListener(TopImageLongClickListener listener) {
        this.mLongClickListener = listener;
        if (listener == null) {
            this.mLongClickRunnable = null;
        } else if (this.mLongClickRunnable == null) {
            this.mLongClickRunnable = new LongClickRunnable();
        }
    }

    private class LongClickRunnable implements Runnable {
        private int position;

        private LongClickRunnable() {
        }

        public void setPosition(int position2) {
            this.position = position2;
        }

        public void run() {
            if (CoverFlowView.this.mLongClickListener != null) {
                Log.e("****", "position *** = " + this.position);
                CoverFlowView.this.mLongClickListener.onLongClick(this.position);
                boolean unused = CoverFlowView.this.mLongClickTriggled = true;
            }
        }
    }

    class RecycleBin {
        @SuppressLint({"NewApi"})
        final LruCache<Integer, Bitmap> bitmapCache = new LruCache<Integer, Bitmap>(getCacheSize(CoverFlowView.this.getContext())) {
            /* access modifiers changed from: protected */
            public int sizeOf(Integer key, Bitmap bitmap) {
                if (Build.VERSION.SDK_INT < 12) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }
                return bitmap.getByteCount();
            }

            /* access modifiers changed from: protected */
            public void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
                if (evicted && oldValue != null && !oldValue.isRecycled()) {
                    oldValue.recycle();
                }
            }
        };

        RecycleBin() {
        }

        public Bitmap getCachedBitmap(int position) {
            return this.bitmapCache.get(Integer.valueOf(position));
        }

        public void addBitmap2Cache(int position, Bitmap b) {
            this.bitmapCache.put(Integer.valueOf(position), b);
            Runtime.getRuntime().gc();
        }

        public Bitmap removeCachedBitmap(int position) {
            if (position < 0 || position >= this.bitmapCache.size()) {
                return null;
            }
            return this.bitmapCache.remove(Integer.valueOf(position));
        }

        public void clear() {
            this.bitmapCache.evictAll();
        }

        private int getCacheSize(Context context) {
            int cacheSize;
            int memClass = ((ActivityManager) context.getSystemService("activity")).getMemoryClass();
            if (CoverFlowView.this.m_iUITypeVer == 40 || CoverFlowView.this.m_iUITypeVer == 38 || CoverFlowView.this.m_iUITypeVer == 44) {
                cacheSize = (1638400 * memClass) / 21;
            } else {
                cacheSize = (1048576 * memClass) / 21;
            }
            Log.e("View", "cacheSize == " + cacheSize);
            return cacheSize;
        }
    }

    public void setCurrImage(int count) {
        this.mDrawChildPaint.setAlpha(255);
        invalidate();
    }

    public float getLastModeOffset() {
        return this.mLastModeOffset;
    }

    public void setLastModeOffset(float LastModeOffset) {
        this.mLastModeOffset = LastModeOffset;
    }
}
