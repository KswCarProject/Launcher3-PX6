package com.android.launcher3.notification;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.touch.OverScroll;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.util.Themes;

@TargetApi(24)
public class NotificationMainView extends FrameLayout implements SwipeDetector.Listener {
    private static FloatProperty<NotificationMainView> CONTENT_TRANSLATION = new FloatProperty<NotificationMainView>("contentTranslation") {
        public void setValue(NotificationMainView view, float v) {
            view.setContentTranslation(v);
        }

        public Float get(NotificationMainView view) {
            return Float.valueOf(view.mTextAndBackground.getTranslationX());
        }
    };
    public static final ItemInfo NOTIFICATION_ITEM_INFO = new ItemInfo();
    private int mBackgroundColor;
    private final ObjectAnimator mContentTranslateAnimator;
    private View mIconView;
    private NotificationInfo mNotificationInfo;
    /* access modifiers changed from: private */
    public SwipeDetector mSwipeDetector;
    /* access modifiers changed from: private */
    public ViewGroup mTextAndBackground;
    private TextView mTextView;
    private TextView mTitleView;

    public NotificationMainView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public NotificationMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContentTranslateAnimator = ObjectAnimator.ofFloat(this, CONTENT_TRANSLATION, new float[]{0.0f});
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTextAndBackground = (ViewGroup) findViewById(R.id.text_and_background);
        ColorDrawable colorBackground = (ColorDrawable) this.mTextAndBackground.getBackground();
        this.mBackgroundColor = colorBackground.getColor();
        this.mTextAndBackground.setBackground(new RippleDrawable(ColorStateList.valueOf(Themes.getAttrColor(getContext(), 16843820)), colorBackground, (Drawable) null));
        this.mTitleView = (TextView) this.mTextAndBackground.findViewById(R.id.title);
        this.mTextView = (TextView) this.mTextAndBackground.findViewById(R.id.text);
        this.mIconView = findViewById(R.id.popup_item_icon);
    }

    public void setSwipeDetector(SwipeDetector swipeDetector) {
        this.mSwipeDetector = swipeDetector;
    }

    public void applyNotificationInfo(NotificationInfo mainNotification, boolean animate) {
        this.mNotificationInfo = mainNotification;
        NotificationListener listener = NotificationListener.getInstanceIfConnected();
        if (listener != null) {
            listener.setNotificationsShown(new String[]{this.mNotificationInfo.notificationKey});
        }
        CharSequence title = this.mNotificationInfo.title;
        CharSequence text = this.mNotificationInfo.text;
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(text)) {
            this.mTitleView.setMaxLines(2);
            this.mTitleView.setText(TextUtils.isEmpty(title) ? text.toString() : title.toString());
            this.mTextView.setVisibility(8);
        } else {
            this.mTitleView.setText(title.toString());
            this.mTextView.setText(text.toString());
        }
        this.mIconView.setBackground(this.mNotificationInfo.getIconForBackground(getContext(), this.mBackgroundColor));
        if (this.mNotificationInfo.intent != null) {
            setOnClickListener(this.mNotificationInfo);
        }
        setContentTranslation(0.0f);
        setTag(NOTIFICATION_ITEM_INFO);
        if (animate) {
            ObjectAnimator.ofFloat(this.mTextAndBackground, ALPHA, new float[]{0.0f, 1.0f}).setDuration(150).start();
        }
    }

    public void setContentTranslation(float translation) {
        this.mTextAndBackground.setTranslationX(translation);
        this.mIconView.setTranslationX(translation);
    }

    public void setContentVisibility(int visibility) {
        this.mTextAndBackground.setVisibility(visibility);
        this.mIconView.setVisibility(visibility);
    }

    public NotificationInfo getNotificationInfo() {
        return this.mNotificationInfo;
    }

    public boolean canChildBeDismissed() {
        return this.mNotificationInfo != null && this.mNotificationInfo.dismissable;
    }

    public void onChildDismissed() {
        Launcher launcher = Launcher.getLauncher(getContext());
        launcher.getPopupDataProvider().cancelNotification(this.mNotificationInfo.notificationKey);
        launcher.getUserEventDispatcher().logActionOnItem(3, 4, 8);
    }

    public void onDragStart(boolean start) {
    }

    public boolean onDrag(float displacement, float velocity) {
        float f;
        if (canChildBeDismissed()) {
            f = displacement;
        } else {
            f = (float) OverScroll.dampedScroll(displacement, getWidth());
        }
        setContentTranslation(f);
        this.mContentTranslateAnimator.cancel();
        return true;
    }

    public void onDragEnd(float velocity, boolean fling) {
        final boolean willExit;
        float startTranslation = this.mTextAndBackground.getTranslationX();
        float endTranslation = 0.0f;
        if (!canChildBeDismissed()) {
            willExit = false;
            endTranslation = 0.0f;
        } else if (fling) {
            willExit = true;
            endTranslation = (float) (velocity < 0.0f ? -getWidth() : getWidth());
        } else if (Math.abs(startTranslation) > ((float) (getWidth() / 2))) {
            willExit = true;
            endTranslation = (float) (startTranslation < 0.0f ? -getWidth() : getWidth());
        } else {
            willExit = false;
        }
        long duration = SwipeDetector.calculateDuration(velocity, (endTranslation - startTranslation) / ((float) getWidth()));
        this.mContentTranslateAnimator.removeAllListeners();
        this.mContentTranslateAnimator.setDuration(duration).setInterpolator(Interpolators.scrollInterpolatorForVelocity(velocity));
        this.mContentTranslateAnimator.setFloatValues(new float[]{startTranslation, endTranslation});
        this.mContentTranslateAnimator.addListener(new AnimationSuccessListener() {
            public void onAnimationSuccess(Animator animator) {
                NotificationMainView.this.mSwipeDetector.finishedScrolling();
                if (willExit) {
                    NotificationMainView.this.onChildDismissed();
                }
            }
        });
        this.mContentTranslateAnimator.start();
    }
}
