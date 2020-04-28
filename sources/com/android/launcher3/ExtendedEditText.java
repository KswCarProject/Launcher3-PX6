package com.android.launcher3;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.android.launcher3.util.UiThreadHelper;

public class ExtendedEditText extends EditText {
    private OnBackKeyListener mBackKeyListener;
    private boolean mForceDisableSuggestions = false;
    /* access modifiers changed from: private */
    public boolean mShowImeAfterFirstLayout;

    public interface OnBackKeyListener {
        boolean onBackKey();
    }

    public ExtendedEditText(Context context) {
        super(context);
    }

    public ExtendedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnBackKeyListener(OnBackKeyListener listener) {
        this.mBackKeyListener = listener;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode != 4 || event.getAction() != 1) {
            return super.onKeyPreIme(keyCode, event);
        }
        if (this.mBackKeyListener != null) {
            return this.mBackKeyListener.onBackKey();
        }
        return false;
    }

    public boolean onDragEvent(DragEvent event) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.mShowImeAfterFirstLayout) {
            post(new Runnable() {
                public void run() {
                    boolean unused = ExtendedEditText.this.showSoftInput();
                    boolean unused2 = ExtendedEditText.this.mShowImeAfterFirstLayout = false;
                }
            });
        }
    }

    public void showKeyboard() {
        this.mShowImeAfterFirstLayout = !showSoftInput();
    }

    public void hideKeyboard() {
        UiThreadHelper.hideKeyboardAsync(getContext(), getWindowToken());
    }

    /* access modifiers changed from: private */
    public boolean showSoftInput() {
        if (!requestFocus() || !((InputMethodManager) getContext().getSystemService("input_method")).showSoftInput(this, 1)) {
            return false;
        }
        return true;
    }

    public void dispatchBackKey() {
        hideKeyboard();
        if (this.mBackKeyListener != null) {
            this.mBackKeyListener.onBackKey();
        }
    }

    public void forceDisableSuggestions(boolean forceDisableSuggestions) {
        this.mForceDisableSuggestions = forceDisableSuggestions;
    }

    public boolean isSuggestionsEnabled() {
        return !this.mForceDisableSuggestions && super.isSuggestionsEnabled();
    }

    public void reset() {
        View nextFocus;
        if (!TextUtils.isEmpty(getText())) {
            setText("");
        }
        if (isFocused() && (nextFocus = focusSearch(130)) != null) {
            nextFocus.requestFocus();
        }
        hideKeyboard();
    }
}
