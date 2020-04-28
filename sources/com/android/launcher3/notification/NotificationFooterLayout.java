package com.android.launcher3.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.android.launcher3.LauncherAnimUtils;
import com.android.launcher3.R;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.PropertyListBuilder;
import com.android.launcher3.anim.PropertyResetListener;
import com.android.launcher3.util.Themes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NotificationFooterLayout extends FrameLayout {
    private static final int MAX_FOOTER_NOTIFICATIONS = 5;
    private static final Rect sTempRect = new Rect();
    private final int mBackgroundColor;
    private NotificationItemView mContainer;
    FrameLayout.LayoutParams mIconLayoutParams;
    private LinearLayout mIconRow;
    private final List<NotificationInfo> mNotifications;
    private View mOverflowEllipsis;
    private final List<NotificationInfo> mOverflowNotifications;
    private final boolean mRtl;

    public interface IconAnimationEndListener {
        void onIconAnimationEnd(NotificationInfo notificationInfo);
    }

    public NotificationFooterLayout(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public NotificationFooterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationFooterLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mNotifications = new ArrayList();
        this.mOverflowNotifications = new ArrayList();
        Resources res = getResources();
        this.mRtl = Utilities.isRtl(res);
        int iconSize = res.getDimensionPixelSize(R.dimen.notification_footer_icon_size);
        this.mIconLayoutParams = new FrameLayout.LayoutParams(iconSize, iconSize);
        this.mIconLayoutParams.gravity = 16;
        int paddingEnd = res.getDimensionPixelSize(R.dimen.notification_footer_icon_row_padding);
        this.mIconLayoutParams.setMarginStart((((res.getDimensionPixelSize(R.dimen.bg_popup_item_width) - paddingEnd) - (res.getDimensionPixelSize(R.dimen.horizontal_ellipsis_offset) + res.getDimensionPixelSize(R.dimen.horizontal_ellipsis_size))) - (iconSize * 5)) / 5);
        this.mBackgroundColor = Themes.getAttrColor(context, R.attr.popupColorPrimary);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mOverflowEllipsis = findViewById(R.id.overflow);
        this.mIconRow = (LinearLayout) findViewById(R.id.icon_row);
    }

    /* access modifiers changed from: package-private */
    public void setContainer(NotificationItemView container) {
        this.mContainer = container;
    }

    public void addNotificationInfo(NotificationInfo notificationInfo) {
        if (this.mNotifications.size() < 5) {
            this.mNotifications.add(notificationInfo);
        } else {
            this.mOverflowNotifications.add(notificationInfo);
        }
    }

    public void commitNotificationInfos() {
        this.mIconRow.removeAllViews();
        for (int i = 0; i < this.mNotifications.size(); i++) {
            addNotificationIconForInfo(this.mNotifications.get(i));
        }
        updateOverflowEllipsisVisibility();
    }

    private void updateOverflowEllipsisVisibility() {
        this.mOverflowEllipsis.setVisibility(this.mOverflowNotifications.isEmpty() ? 8 : 0);
    }

    private View addNotificationIconForInfo(NotificationInfo info) {
        View icon = new View(getContext());
        icon.setBackground(info.getIconForBackground(getContext(), this.mBackgroundColor));
        icon.setOnClickListener(info);
        icon.setTag(info);
        icon.setImportantForAccessibility(2);
        this.mIconRow.addView(icon, 0, this.mIconLayoutParams);
        return icon;
    }

    public void animateFirstNotificationTo(Rect toBounds, IconAnimationEndListener callback) {
        NotificationFooterLayout notificationFooterLayout = this;
        AnimatorSet animation = LauncherAnimUtils.createAnimatorSet();
        int i = 1;
        final View firstNotification = notificationFooterLayout.mIconRow.getChildAt(notificationFooterLayout.mIconRow.getChildCount() - 1);
        Rect fromBounds = sTempRect;
        firstNotification.getGlobalVisibleRect(fromBounds);
        float scale = ((float) toBounds.height()) / ((float) fromBounds.height());
        Animator moveAndScaleIcon = LauncherAnimUtils.ofPropertyValuesHolder(firstNotification, new PropertyListBuilder().scale(scale).translationY(((float) (toBounds.top - fromBounds.top)) + (((((float) fromBounds.height()) * scale) - ((float) fromBounds.height())) / 2.0f)).build());
        final IconAnimationEndListener iconAnimationEndListener = callback;
        moveAndScaleIcon.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                iconAnimationEndListener.onIconAnimationEnd((NotificationInfo) firstNotification.getTag());
                NotificationFooterLayout.this.removeViewFromIconRow(firstNotification);
            }
        });
        animation.play(moveAndScaleIcon);
        int gapWidth = notificationFooterLayout.mIconLayoutParams.width + notificationFooterLayout.mIconLayoutParams.getMarginStart();
        if (notificationFooterLayout.mRtl) {
            gapWidth = -gapWidth;
        }
        if (!notificationFooterLayout.mOverflowNotifications.isEmpty()) {
            NotificationInfo notification = notificationFooterLayout.mOverflowNotifications.remove(0);
            notificationFooterLayout.mNotifications.add(notification);
            animation.play(ObjectAnimator.ofFloat(notificationFooterLayout.addNotificationIconForInfo(notification), ALPHA, new float[]{0.0f, 1.0f}));
        }
        int numIcons = notificationFooterLayout.mIconRow.getChildCount() - 1;
        PropertyResetListener<View, Float> propertyResetListener = new PropertyResetListener<>(TRANSLATION_X, Float.valueOf(0.0f));
        int i2 = 0;
        while (i2 < numIcons) {
            View child = notificationFooterLayout.mIconRow.getChildAt(i2);
            Property property = TRANSLATION_X;
            float[] fArr = new float[i];
            fArr[0] = (float) gapWidth;
            Animator shiftChild = ObjectAnimator.ofFloat(child, property, fArr);
            shiftChild.addListener(propertyResetListener);
            animation.play(shiftChild);
            i2++;
            notificationFooterLayout = this;
            i = 1;
        }
        animation.start();
    }

    /* access modifiers changed from: private */
    public void removeViewFromIconRow(View child) {
        this.mIconRow.removeView(child);
        this.mNotifications.remove(child.getTag());
        updateOverflowEllipsisVisibility();
        if (this.mIconRow.getChildCount() == 0 && this.mContainer != null) {
            this.mContainer.removeFooter();
        }
    }

    public void trimNotifications(List<String> notifications) {
        if (isAttachedToWindow() && this.mIconRow.getChildCount() != 0) {
            Iterator<NotificationInfo> overflowIterator = this.mOverflowNotifications.iterator();
            while (overflowIterator.hasNext()) {
                if (!notifications.contains(overflowIterator.next().notificationKey)) {
                    overflowIterator.remove();
                }
            }
            for (int i = this.mIconRow.getChildCount() - 1; i >= 0; i--) {
                View child = this.mIconRow.getChildAt(i);
                if (!notifications.contains(((NotificationInfo) child.getTag()).notificationKey)) {
                    removeViewFromIconRow(child);
                }
            }
        }
    }
}
