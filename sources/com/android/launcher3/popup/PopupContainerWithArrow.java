package com.android.launcher3.popup;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.DragSource;
import com.android.launcher3.DropTarget;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.ItemInfoWithIcon;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.R;
import com.android.launcher3.accessibility.LauncherAccessibilityDelegate;
import com.android.launcher3.accessibility.ShortcutMenuAccessibilityDelegate;
import com.android.launcher3.badge.BadgeInfo;
import com.android.launcher3.dragndrop.DragController;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragOptions;
import com.android.launcher3.logging.LoggerUtils;
import com.android.launcher3.notification.NotificationInfo;
import com.android.launcher3.notification.NotificationItemView;
import com.android.launcher3.notification.NotificationKeyData;
import com.android.launcher3.notification.NotificationMainView;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutDragPreviewProvider;
import com.android.launcher3.touch.ItemLongClickListener;
import com.android.launcher3.userevent.nano.LauncherLogProto;
import com.android.launcher3.util.PackageUserKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TargetApi(24)
public class PopupContainerWithArrow extends ArrowPopup implements DragSource, DragController.DragListener, View.OnLongClickListener, View.OnTouchListener {
    private final LauncherAccessibilityDelegate mAccessibilityDelegate;
    private final Point mIconLastTouchPos;
    private final PointF mInterceptTouchDown;
    private NotificationItemView mNotificationItemView;
    private int mNumNotifications;
    /* access modifiers changed from: private */
    public BubbleTextView mOriginalIcon;
    private final List<DeepShortcutView> mShortcuts;
    /* access modifiers changed from: private */
    public final int mStartDragThreshold;
    private ViewGroup mSystemShortcutContainer;

    public PopupContainerWithArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mShortcuts = new ArrayList();
        this.mInterceptTouchDown = new PointF();
        this.mIconLastTouchPos = new Point();
        this.mStartDragThreshold = getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_start_drag_threshold);
        this.mAccessibilityDelegate = new ShortcutMenuAccessibilityDelegate(this.mLauncher);
    }

    public PopupContainerWithArrow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupContainerWithArrow(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public LauncherAccessibilityDelegate getAccessibilityDelegate() {
        return this.mAccessibilityDelegate;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            this.mInterceptTouchDown.set(ev.getX(), ev.getY());
        }
        if ((this.mNotificationItemView == null || !this.mNotificationItemView.onInterceptTouchEvent(ev)) && Math.hypot((double) (this.mInterceptTouchDown.x - ev.getX()), (double) (this.mInterceptTouchDown.y - ev.getY())) <= ((double) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
            return false;
        }
        return true;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.mNotificationItemView != null) {
            return this.mNotificationItemView.onTouchEvent(ev) || super.onTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    /* access modifiers changed from: protected */
    public boolean isOfType(int type) {
        return (type & 2) != 0;
    }

    public void logActionCommand(int command) {
        this.mLauncher.getUserEventDispatcher().logActionCommand(command, (View) this.mOriginalIcon, 9);
    }

    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            DragLayer dl = this.mLauncher.getDragLayer();
            if (!dl.isEventOverView(this, ev)) {
                this.mLauncher.getUserEventDispatcher().logActionTapOutside(LoggerUtils.newContainerTarget(9));
                close(true);
                if (this.mOriginalIcon == null || !dl.isEventOverView(this.mOriginalIcon, ev)) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public static PopupContainerWithArrow showForIcon(BubbleTextView icon) {
        Launcher launcher = Launcher.getLauncher(icon.getContext());
        if (getOpen(launcher) != null) {
            icon.clearFocus();
            return null;
        }
        ItemInfo itemInfo = (ItemInfo) icon.getTag();
        if (!DeepShortcutManager.supportsShortcuts(itemInfo)) {
            return null;
        }
        PopupDataProvider popupDataProvider = launcher.getPopupDataProvider();
        List<String> shortcutIds = popupDataProvider.getShortcutIdsForItem(itemInfo);
        List<NotificationKeyData> notificationKeys = popupDataProvider.getNotificationKeysForItem(itemInfo);
        List<SystemShortcut> systemShortcuts = popupDataProvider.getEnabledSystemShortcutsForItem(itemInfo);
        PopupContainerWithArrow container = (PopupContainerWithArrow) launcher.getLayoutInflater().inflate(R.layout.popup_container, launcher.getDragLayer(), false);
        container.populateAndShow(icon, shortcutIds, notificationKeys, systemShortcuts);
        return container;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: android.view.View} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: com.android.launcher3.shortcuts.DeepShortcutView} */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onInflationComplete(boolean r7) {
        /*
            r6 = this;
            if (r7 == 0) goto L_0x000b
            com.android.launcher3.notification.NotificationItemView r0 = r6.mNotificationItemView
            if (r0 == 0) goto L_0x000b
            com.android.launcher3.notification.NotificationItemView r0 = r6.mNotificationItemView
            r0.inverseGutterMargin()
        L_0x000b:
            int r0 = r6.getChildCount()
            r1 = 0
            r2 = 0
            r3 = r1
            r1 = 0
        L_0x0013:
            if (r1 >= r0) goto L_0x0032
            android.view.View r4 = r6.getChildAt(r1)
            int r5 = r4.getVisibility()
            if (r5 != 0) goto L_0x002f
            boolean r5 = r4 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r5 == 0) goto L_0x002f
            if (r3 == 0) goto L_0x0028
            r3.setDividerVisibility(r2)
        L_0x0028:
            r3 = r4
            com.android.launcher3.shortcuts.DeepShortcutView r3 = (com.android.launcher3.shortcuts.DeepShortcutView) r3
            r5 = 4
            r3.setDividerVisibility(r5)
        L_0x002f:
            int r1 = r1 + 1
            goto L_0x0013
        L_0x0032:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.popup.PopupContainerWithArrow.onInflationComplete(boolean):void");
    }

    @TargetApi(28)
    private void populateAndShow(BubbleTextView originalIcon, List<String> shortcutIds, List<NotificationKeyData> notificationKeys, List<SystemShortcut> systemShortcuts) {
        this.mNumNotifications = notificationKeys.size();
        this.mOriginalIcon = originalIcon;
        if (this.mNumNotifications > 0) {
            View.inflate(getContext(), R.layout.notification_content, this);
            this.mNotificationItemView = new NotificationItemView(this);
            if (this.mNumNotifications == 1) {
                this.mNotificationItemView.removeFooter();
            }
            updateNotificationHeader();
        }
        int viewsToFlip = getChildCount();
        this.mSystemShortcutContainer = this;
        if (!shortcutIds.isEmpty()) {
            if (this.mNotificationItemView != null) {
                this.mNotificationItemView.addGutter();
            }
            for (int i = shortcutIds.size(); i > 0; i--) {
                this.mShortcuts.add(inflateAndAdd(R.layout.deep_shortcut, this));
            }
            updateHiddenShortcuts();
            if (!systemShortcuts.isEmpty()) {
                this.mSystemShortcutContainer = (ViewGroup) inflateAndAdd(R.layout.system_shortcut_icons, this);
                for (SystemShortcut shortcut : systemShortcuts) {
                    initializeSystemShortcut(R.layout.system_shortcut_icon_only, this.mSystemShortcutContainer, shortcut);
                }
            }
        } else if (!systemShortcuts.isEmpty()) {
            if (this.mNotificationItemView != null) {
                this.mNotificationItemView.addGutter();
            }
            for (SystemShortcut shortcut2 : systemShortcuts) {
                initializeSystemShortcut(R.layout.system_shortcut, this, shortcut2);
            }
        }
        reorderAndShow(viewsToFlip);
        ItemInfo originalItemInfo = (ItemInfo) originalIcon.getTag();
        if (Build.VERSION.SDK_INT >= 28) {
            setAccessibilityPaneTitle(getTitleForAccessibility());
        }
        this.mLauncher.getDragController().addDragListener(this);
        this.mOriginalIcon.forceHideBadge(true);
        setLayoutTransition(new LayoutTransition());
        new Handler(LauncherModel.getWorkerLooper()).postAtFrontOfQueue(PopupPopulator.createUpdateRunnable(this.mLauncher, originalItemInfo, new Handler(Looper.getMainLooper()), this, shortcutIds, this.mShortcuts, notificationKeys));
    }

    private String getTitleForAccessibility() {
        return getContext().getString(this.mNumNotifications == 0 ? R.string.action_deep_shortcut : R.string.shortcuts_menu_with_notifications_description);
    }

    /* access modifiers changed from: protected */
    public Pair<View, String> getAccessibilityTarget() {
        return Pair.create(this, "");
    }

    /* access modifiers changed from: protected */
    public void getTargetObjectLocation(Rect outPos) {
        int i;
        this.mLauncher.getDragLayer().getDescendantRectRelativeToSelf(this.mOriginalIcon, outPos);
        outPos.top += this.mOriginalIcon.getPaddingTop();
        outPos.left += this.mOriginalIcon.getPaddingLeft();
        outPos.right -= this.mOriginalIcon.getPaddingRight();
        int i2 = outPos.top;
        if (this.mOriginalIcon.getIcon() != null) {
            i = this.mOriginalIcon.getIcon().getBounds().height();
        } else {
            i = this.mOriginalIcon.getHeight();
        }
        outPos.bottom = i2 + i;
    }

    public void applyNotificationInfos(List<NotificationInfo> notificationInfos) {
        this.mNotificationItemView.applyNotificationInfos(notificationInfos);
    }

    private void updateHiddenShortcuts() {
        int allowedCount = this.mNotificationItemView != null ? 2 : 4;
        int originalHeight = getResources().getDimensionPixelSize(R.dimen.bg_popup_item_height);
        int itemHeight = this.mNotificationItemView != null ? getResources().getDimensionPixelSize(R.dimen.bg_popup_item_condensed_height) : originalHeight;
        float iconScale = ((float) itemHeight) / ((float) originalHeight);
        int total = this.mShortcuts.size();
        int i = 0;
        while (i < total) {
            DeepShortcutView view = this.mShortcuts.get(i);
            view.setVisibility(i >= allowedCount ? 8 : 0);
            view.getLayoutParams().height = itemHeight;
            view.getIconView().setScaleX(iconScale);
            view.getIconView().setScaleY(iconScale);
            i++;
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v0, resolved type: android.view.View} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v3, resolved type: com.android.launcher3.shortcuts.DeepShortcutView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateDividers() {
        /*
            r6 = this;
            int r0 = r6.getChildCount()
            r1 = 0
            r2 = 0
            r3 = r1
            r1 = 0
        L_0x0008:
            if (r1 >= r0) goto L_0x0027
            android.view.View r4 = r6.getChildAt(r1)
            int r5 = r4.getVisibility()
            if (r5 != 0) goto L_0x0024
            boolean r5 = r4 instanceof com.android.launcher3.shortcuts.DeepShortcutView
            if (r5 == 0) goto L_0x0024
            if (r3 == 0) goto L_0x001d
            r3.setDividerVisibility(r2)
        L_0x001d:
            r3 = r4
            com.android.launcher3.shortcuts.DeepShortcutView r3 = (com.android.launcher3.shortcuts.DeepShortcutView) r3
            r5 = 4
            r3.setDividerVisibility(r5)
        L_0x0024:
            int r1 = r1 + 1
            goto L_0x0008
        L_0x0027:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.popup.PopupContainerWithArrow.updateDividers():void");
    }

    /* access modifiers changed from: protected */
    public void onWidgetsBound() {
        SystemShortcut widgetInfo = new SystemShortcut.Widgets();
        View.OnClickListener onClickListener = widgetInfo.getOnClickListener(this.mLauncher, (ItemInfo) this.mOriginalIcon.getTag());
        View widgetsView = null;
        int count = this.mSystemShortcutContainer.getChildCount();
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            View systemShortcutView = this.mSystemShortcutContainer.getChildAt(i);
            if (systemShortcutView.getTag() instanceof SystemShortcut.Widgets) {
                widgetsView = systemShortcutView;
                break;
            }
            i++;
        }
        if (onClickListener == null || widgetsView != null) {
            if (onClickListener == null && widgetsView != null) {
                if (this.mSystemShortcutContainer != this) {
                    this.mSystemShortcutContainer.removeView(widgetsView);
                    return;
                }
                close(false);
                showForIcon(this.mOriginalIcon);
            }
        } else if (this.mSystemShortcutContainer != this) {
            initializeSystemShortcut(R.layout.system_shortcut_icon_only, this.mSystemShortcutContainer, widgetInfo);
        } else {
            close(false);
            showForIcon(this.mOriginalIcon);
        }
    }

    private void initializeSystemShortcut(int resId, ViewGroup container, SystemShortcut info) {
        View view = inflateAndAdd(resId, container);
        if (view instanceof DeepShortcutView) {
            DeepShortcutView shortcutView = (DeepShortcutView) view;
            shortcutView.getIconView().setBackgroundResource(info.iconResId);
            shortcutView.getBubbleText().setText(info.labelResId);
        } else if (view instanceof ImageView) {
            ImageView shortcutIcon = (ImageView) view;
            shortcutIcon.setImageResource(info.iconResId);
            shortcutIcon.setContentDescription(getContext().getText(info.labelResId));
        }
        view.setTag(info);
        view.setOnClickListener(info.getOnClickListener(this.mLauncher, (ItemInfo) this.mOriginalIcon.getTag()));
    }

    public DragOptions.PreDragCondition createPreDragCondition() {
        return new DragOptions.PreDragCondition() {
            public boolean shouldStartDrag(double distanceDragged) {
                return distanceDragged > ((double) PopupContainerWithArrow.this.mStartDragThreshold);
            }

            public void onPreDragStart(DropTarget.DragObject dragObject) {
                if (PopupContainerWithArrow.this.mIsAboveIcon) {
                    PopupContainerWithArrow.this.mOriginalIcon.setIconVisible(false);
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                    return;
                }
                PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
            }

            public void onPreDragEnd(DropTarget.DragObject dragObject, boolean dragStarted) {
                PopupContainerWithArrow.this.mOriginalIcon.setIconVisible(true);
                if (dragStarted) {
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(4);
                    return;
                }
                PopupContainerWithArrow.this.mLauncher.getUserEventDispatcher().logDeepShortcutsOpen(PopupContainerWithArrow.this.mOriginalIcon);
                if (!PopupContainerWithArrow.this.mIsAboveIcon) {
                    PopupContainerWithArrow.this.mOriginalIcon.setVisibility(0);
                    PopupContainerWithArrow.this.mOriginalIcon.setTextVisibility(false);
                }
            }
        };
    }

    public void updateNotificationHeader(Set<PackageUserKey> updatedBadges) {
        if (updatedBadges.contains(PackageUserKey.fromItemInfo((ItemInfo) this.mOriginalIcon.getTag()))) {
            updateNotificationHeader();
        }
    }

    private void updateNotificationHeader() {
        ItemInfoWithIcon itemInfo = (ItemInfoWithIcon) this.mOriginalIcon.getTag();
        BadgeInfo badgeInfo = this.mLauncher.getBadgeInfoForItem(itemInfo);
        if (this.mNotificationItemView != null && badgeInfo != null) {
            this.mNotificationItemView.updateHeader(badgeInfo.getNotificationCount(), itemInfo.iconColor);
        }
    }

    public void trimNotifications(Map<PackageUserKey, BadgeInfo> updatedBadges) {
        if (this.mNotificationItemView != null) {
            BadgeInfo badgeInfo = updatedBadges.get(PackageUserKey.fromItemInfo((ItemInfo) this.mOriginalIcon.getTag()));
            if (badgeInfo == null || badgeInfo.getNotificationKeys().size() == 0) {
                this.mNotificationItemView.removeAllViews();
                this.mNotificationItemView = null;
                updateHiddenShortcuts();
                updateDividers();
                return;
            }
            this.mNotificationItemView.trimNotifications(NotificationKeyData.extractKeysOnly(badgeInfo.getNotificationKeys()));
        }
    }

    public void onDropCompleted(View target, DropTarget.DragObject d, boolean success) {
    }

    public void onDragStart(DropTarget.DragObject dragObject, DragOptions options) {
        this.mDeferContainerRemoval = true;
        animateClose();
    }

    public void onDragEnd() {
        if (this.mIsOpen) {
            return;
        }
        if (this.mOpenCloseAnimator != null) {
            this.mDeferContainerRemoval = false;
        } else if (this.mDeferContainerRemoval) {
            closeComplete();
        }
    }

    public void fillInLogContainerData(View v, ItemInfo info, LauncherLogProto.Target target, LauncherLogProto.Target targetParent) {
        if (info == NotificationMainView.NOTIFICATION_ITEM_INFO) {
            target.itemType = 8;
        } else {
            target.itemType = 5;
            target.rank = info.rank;
        }
        targetParent.containerType = 9;
    }

    /* access modifiers changed from: protected */
    public void onCreateCloseAnimation(AnimatorSet anim) {
        anim.play(this.mOriginalIcon.createTextAlphaAnimator(true));
        this.mOriginalIcon.forceHideBadge(false);
    }

    /* access modifiers changed from: protected */
    public void closeComplete() {
        super.closeComplete();
        this.mOriginalIcon.setTextVisibility(this.mOriginalIcon.shouldTextBeVisible());
        this.mOriginalIcon.forceHideBadge(false);
    }

    public boolean onTouch(View v, MotionEvent ev) {
        int action = ev.getAction();
        if (action != 0 && action != 2) {
            return false;
        }
        this.mIconLastTouchPos.set((int) ev.getX(), (int) ev.getY());
        return false;
    }

    public boolean onLongClick(View v) {
        if (!ItemLongClickListener.canStartDrag(this.mLauncher) || !(v.getParent() instanceof DeepShortcutView)) {
            return false;
        }
        DeepShortcutView sv = (DeepShortcutView) v.getParent();
        sv.setWillDrawIcon(false);
        Point iconShift = new Point();
        iconShift.x = this.mIconLastTouchPos.x - sv.getIconCenter().x;
        iconShift.y = this.mIconLastTouchPos.y - this.mLauncher.getDeviceProfile().iconSizePx;
        this.mLauncher.getWorkspace().beginDragShared(sv.getIconView(), this, sv.getFinalInfo(), new ShortcutDragPreviewProvider(sv.getIconView(), iconShift), new DragOptions()).animateShift(-iconShift.x, -iconShift.y);
        AbstractFloatingView.closeOpenContainer(this.mLauncher, 1);
        return false;
    }

    public static PopupContainerWithArrow getOpen(Launcher launcher) {
        return (PopupContainerWithArrow) getOpenView(launcher, 2);
    }
}
