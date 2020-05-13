package com.szchoiceway.index;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class HandleView extends ImageView {
    private static final int ORIENTATION_HORIZONTAL = 1;
    private Launcher mLauncher;
    private int mOrientation;

    public HandleView(Context context) {
        super(context);
        this.mOrientation = 1;
    }

    public HandleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mOrientation = 1;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HandleView, defStyle, 0);
        this.mOrientation = a.getInt(0, 1);
        a.recycle();
        setContentDescription(context.getString(R.string.all_apps_button_label));
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public View focusSearch(int direction) {
        View newFocus = super.focusSearch(direction);
        if (newFocus != null || this.mLauncher.isAllAppsVisible()) {
            return newFocus;
        }
        Workspace workspace = this.mLauncher.getWorkspace();
        workspace.dispatchUnhandledMove((View) null, direction);
        return (this.mOrientation == 1 && direction == 130) ? this : workspace;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || !this.mLauncher.isAllAppsVisible()) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setLauncher(Launcher launcher) {
        this.mLauncher = launcher;
    }
}
