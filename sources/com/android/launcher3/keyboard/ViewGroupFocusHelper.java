package com.android.launcher3.keyboard;

import android.graphics.Rect;
import android.view.View;
import com.android.launcher3.PagedView;

public class ViewGroupFocusHelper extends FocusIndicatorHelper {
    private final View mContainer;

    public ViewGroupFocusHelper(View container) {
        super(container);
        this.mContainer = container;
    }

    public void viewToRect(View v, Rect outRect) {
        outRect.left = 0;
        outRect.top = 0;
        computeLocationRelativeToContainer(v, outRect);
        outRect.left = (int) (((float) outRect.left) + (((1.0f - v.getScaleX()) * ((float) v.getWidth())) / 2.0f));
        outRect.top = (int) (((float) outRect.top) + (((1.0f - v.getScaleY()) * ((float) v.getHeight())) / 2.0f));
        outRect.right = outRect.left + ((int) (v.getScaleX() * ((float) v.getWidth())));
        outRect.bottom = outRect.top + ((int) (v.getScaleY() * ((float) v.getHeight())));
    }

    private void computeLocationRelativeToContainer(View child, Rect outRect) {
        View parent = (View) child.getParent();
        outRect.left = (int) (((float) outRect.left) + child.getX());
        outRect.top = (int) (((float) outRect.top) + child.getY());
        if (parent != this.mContainer) {
            if (parent instanceof PagedView) {
                PagedView page = (PagedView) parent;
                outRect.left -= page.getScrollForPage(page.indexOfChild(child));
            }
            computeLocationRelativeToContainer(parent, outRect);
        }
    }
}
