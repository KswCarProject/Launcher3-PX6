package com.android.launcher3.shortcuts;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.android.launcher3.BubbleTextView;
import com.android.launcher3.Launcher;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.android.launcher3.popup.PopupContainerWithArrow;
import com.android.launcher3.touch.ItemClickHandler;

public class DeepShortcutView extends FrameLayout {
    private static final Point sTempPoint = new Point();
    private BubbleTextView mBubbleText;
    private ShortcutInfoCompat mDetail;
    private View mDivider;
    private View mIconView;
    private ShortcutInfo mInfo;
    private final Rect mPillRect;

    public DeepShortcutView(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public DeepShortcutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeepShortcutView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPillRect = new Rect();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mBubbleText = (BubbleTextView) findViewById(R.id.bubble_text);
        this.mIconView = findViewById(R.id.icon);
        this.mDivider = findViewById(R.id.divider);
    }

    public void setDividerVisibility(int visibility) {
        this.mDivider.setVisibility(visibility);
    }

    public BubbleTextView getBubbleText() {
        return this.mBubbleText;
    }

    public void setWillDrawIcon(boolean willDraw) {
        this.mIconView.setVisibility(willDraw ? 0 : 4);
    }

    public boolean willDrawIcon() {
        return this.mIconView.getVisibility() == 0;
    }

    public Point getIconCenter() {
        Point point = sTempPoint;
        Point point2 = sTempPoint;
        int measuredHeight = getMeasuredHeight() / 2;
        point2.x = measuredHeight;
        point.y = measuredHeight;
        if (Utilities.isRtl(getResources())) {
            sTempPoint.x = getMeasuredWidth() - sTempPoint.x;
        }
        return sTempPoint;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mPillRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    public void applyShortcutInfo(ShortcutInfo info, ShortcutInfoCompat detail, PopupContainerWithArrow container) {
        this.mInfo = info;
        this.mDetail = detail;
        this.mBubbleText.applyFromShortcutInfo(info);
        this.mIconView.setBackground(this.mBubbleText.getIcon());
        CharSequence longLabel = this.mDetail.getLongLabel();
        this.mBubbleText.setText(!TextUtils.isEmpty(longLabel) && (this.mBubbleText.getPaint().measureText(longLabel.toString()) > ((float) ((this.mBubbleText.getWidth() - this.mBubbleText.getTotalPaddingLeft()) - this.mBubbleText.getTotalPaddingRight())) ? 1 : (this.mBubbleText.getPaint().measureText(longLabel.toString()) == ((float) ((this.mBubbleText.getWidth() - this.mBubbleText.getTotalPaddingLeft()) - this.mBubbleText.getTotalPaddingRight())) ? 0 : -1)) <= 0 ? longLabel : this.mDetail.getShortLabel());
        this.mBubbleText.setOnClickListener(ItemClickHandler.INSTANCE);
        this.mBubbleText.setOnLongClickListener(container);
        this.mBubbleText.setOnTouchListener(container);
    }

    public ShortcutInfo getFinalInfo() {
        ShortcutInfo badged = new ShortcutInfo(this.mInfo);
        Launcher.getLauncher(getContext()).getModel().updateAndBindShortcutInfo(badged, this.mDetail);
        return badged;
    }

    public View getIconView() {
        return this.mIconView;
    }
}
