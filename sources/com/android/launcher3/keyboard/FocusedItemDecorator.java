package com.android.launcher3.keyboard;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.android.launcher3.keyboard.FocusIndicatorHelper;

public class FocusedItemDecorator extends RecyclerView.ItemDecoration {
    private FocusIndicatorHelper mHelper;

    public FocusedItemDecorator(View container) {
        this.mHelper = new FocusIndicatorHelper.SimpleFocusIndicatorHelper(container);
    }

    public View.OnFocusChangeListener getFocusListener() {
        return this.mHelper;
    }

    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        this.mHelper.draw(c);
    }
}
