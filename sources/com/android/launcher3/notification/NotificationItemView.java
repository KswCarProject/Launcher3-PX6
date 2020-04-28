package com.android.launcher3.notification;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.launcher3.R;
import com.android.launcher3.graphics.IconPalette;
import com.android.launcher3.notification.NotificationFooterLayout;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.touch.SwipeDetector;
import com.android.launcher3.util.Themes;
import java.util.List;

public class NotificationItemView {
    private static final Rect sTempRect = new Rect();
    private boolean mAnimatingNextIcon;
    private final PopupContainerWithArrow mContainer;
    private final Context mContext;
    private final View mDivider;
    private final NotificationFooterLayout mFooter;
    private View mGutter;
    private final View mHeader;
    private final TextView mHeaderCount;
    private final TextView mHeaderText;
    private final View mIconView;
    private boolean mIgnoreTouch = false;
    private final NotificationMainView mMainView;
    private int mNotificationHeaderTextColor = 0;
    private final SwipeDetector mSwipeDetector;

    public NotificationItemView(PopupContainerWithArrow container) {
        this.mContainer = container;
        this.mContext = container.getContext();
        this.mHeaderText = (TextView) container.findViewById(R.id.notification_text);
        this.mHeaderCount = (TextView) container.findViewById(R.id.notification_count);
        this.mMainView = (NotificationMainView) container.findViewById(R.id.main_view);
        this.mFooter = (NotificationFooterLayout) container.findViewById(R.id.footer);
        this.mIconView = container.findViewById(R.id.popup_item_icon);
        this.mHeader = container.findViewById(R.id.header);
        this.mDivider = container.findViewById(R.id.divider);
        this.mSwipeDetector = new SwipeDetector(this.mContext, (SwipeDetector.Listener) this.mMainView, SwipeDetector.HORIZONTAL);
        this.mSwipeDetector.setDetectableScrollConditions(3, false);
        this.mMainView.setSwipeDetector(this.mSwipeDetector);
        this.mFooter.setContainer(this);
    }

    public void addGutter() {
        if (this.mGutter == null) {
            this.mGutter = this.mContainer.inflateAndAdd(R.layout.notification_gutter, this.mContainer);
        }
    }

    public void removeFooter() {
        if (this.mContainer.indexOfChild(this.mFooter) >= 0) {
            this.mContainer.removeView(this.mFooter);
            this.mContainer.removeView(this.mDivider);
        }
    }

    public void inverseGutterMargin() {
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.mGutter.getLayoutParams();
        int top = lp.topMargin;
        lp.topMargin = lp.bottomMargin;
        lp.bottomMargin = top;
    }

    public void removeAllViews() {
        this.mContainer.removeView(this.mMainView);
        this.mContainer.removeView(this.mHeader);
        if (this.mContainer.indexOfChild(this.mFooter) >= 0) {
            this.mContainer.removeView(this.mFooter);
            this.mContainer.removeView(this.mDivider);
        }
        if (this.mGutter != null) {
            this.mContainer.removeView(this.mGutter);
        }
    }

    public void updateHeader(int notificationCount, int iconColor) {
        this.mHeaderCount.setText(notificationCount <= 1 ? "" : String.valueOf(notificationCount));
        if (Color.alpha(iconColor) > 0) {
            if (this.mNotificationHeaderTextColor == 0) {
                this.mNotificationHeaderTextColor = IconPalette.resolveContrastColor(this.mContext, iconColor, Themes.getAttrColor(this.mContext, R.attr.popupColorPrimary));
            }
            this.mHeaderText.setTextColor(this.mNotificationHeaderTextColor);
            this.mHeaderCount.setTextColor(this.mNotificationHeaderTextColor);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            sTempRect.set(this.mMainView.getLeft(), this.mMainView.getTop(), this.mMainView.getRight(), this.mMainView.getBottom());
            this.mIgnoreTouch = !sTempRect.contains((int) ev.getX(), (int) ev.getY());
            if (!this.mIgnoreTouch) {
                this.mContainer.getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        if (this.mIgnoreTouch || this.mMainView.getNotificationInfo() == null) {
            return false;
        }
        this.mSwipeDetector.onTouchEvent(ev);
        return this.mSwipeDetector.isDraggingOrSettling();
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mIgnoreTouch && this.mMainView.getNotificationInfo() != null) {
            return this.mSwipeDetector.onTouchEvent(ev);
        }
        return false;
    }

    public void applyNotificationInfos(List<NotificationInfo> notificationInfos) {
        if (!notificationInfos.isEmpty()) {
            this.mMainView.applyNotificationInfo(notificationInfos.get(0), false);
            for (int i = 1; i < notificationInfos.size(); i++) {
                this.mFooter.addNotificationInfo(notificationInfos.get(i));
            }
            this.mFooter.commitNotificationInfos();
        }
    }

    public void trimNotifications(List<String> notificationKeys) {
        if (!(!notificationKeys.contains(this.mMainView.getNotificationInfo().notificationKey)) || this.mAnimatingNextIcon) {
            this.mFooter.trimNotifications(notificationKeys);
            return;
        }
        this.mAnimatingNextIcon = true;
        this.mMainView.setContentVisibility(4);
        this.mMainView.setContentTranslation(0.0f);
        this.mIconView.getGlobalVisibleRect(sTempRect);
        this.mFooter.animateFirstNotificationTo(sTempRect, new NotificationFooterLayout.IconAnimationEndListener() {
            public final void onIconAnimationEnd(NotificationInfo notificationInfo) {
                NotificationItemView.lambda$trimNotifications$0(NotificationItemView.this, notificationInfo);
            }
        });
    }

    public static /* synthetic */ void lambda$trimNotifications$0(NotificationItemView notificationItemView, NotificationInfo newMainNotification) {
        if (newMainNotification != null) {
            notificationItemView.mMainView.applyNotificationInfo(newMainNotification, true);
            notificationItemView.mMainView.setContentVisibility(0);
        }
        notificationItemView.mAnimatingNextIcon = false;
    }
}
