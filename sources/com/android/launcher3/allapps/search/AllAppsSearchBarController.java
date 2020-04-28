package com.android.launcher3.allapps.search;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.ExtendedEditText;
import com.android.launcher3.ItemInfo;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.ComponentKey;
import com.android.launcher3.util.PackageManagerHelper;
import java.util.ArrayList;

public class AllAppsSearchBarController implements TextWatcher, TextView.OnEditorActionListener, ExtendedEditText.OnBackKeyListener, View.OnFocusChangeListener {
    protected Callbacks mCb;
    protected ExtendedEditText mInput;
    protected Launcher mLauncher;
    protected String mQuery;
    protected SearchAlgorithm mSearchAlgorithm;

    public interface Callbacks {
        void clearSearchResult();

        void onSearchResult(String str, ArrayList<ComponentKey> arrayList);
    }

    public void setVisibility(int visibility) {
        this.mInput.setVisibility(visibility);
    }

    public final void initialize(SearchAlgorithm searchAlgorithm, ExtendedEditText input, Launcher launcher, Callbacks cb) {
        this.mCb = cb;
        this.mLauncher = launcher;
        this.mInput = input;
        this.mInput.addTextChangedListener(this);
        this.mInput.setOnEditorActionListener(this);
        this.mInput.setOnBackKeyListener(this);
        this.mInput.setOnFocusChangeListener(this);
        this.mSearchAlgorithm = searchAlgorithm;
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void afterTextChanged(Editable s) {
        this.mQuery = s.toString();
        if (this.mQuery.isEmpty()) {
            this.mSearchAlgorithm.cancel(true);
            this.mCb.clearSearchResult();
            return;
        }
        this.mSearchAlgorithm.cancel(false);
        this.mSearchAlgorithm.doSearch(this.mQuery, this.mCb);
    }

    public void refreshSearchResult() {
        if (!TextUtils.isEmpty(this.mQuery)) {
            this.mSearchAlgorithm.cancel(false);
            this.mSearchAlgorithm.doSearch(this.mQuery, this.mCb);
        }
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId != 3) {
            return false;
        }
        String query = v.getText().toString();
        if (query.isEmpty()) {
            return false;
        }
        return this.mLauncher.startActivitySafely(v, PackageManagerHelper.getMarketSearchIntent(this.mLauncher, query), (ItemInfo) null);
    }

    public boolean onBackKey() {
        if (!Utilities.trim(this.mInput.getEditableText().toString()).isEmpty()) {
            return false;
        }
        reset();
        return true;
    }

    public void onFocusChange(View view, boolean hasFocus) {
        if (!hasFocus) {
            this.mInput.hideKeyboard();
        }
    }

    public void reset() {
        this.mCb.clearSearchResult();
        this.mInput.reset();
        this.mQuery = null;
    }

    public void focusSearchField() {
        this.mInput.showKeyboard();
    }

    public boolean isSearchFieldFocused() {
        return this.mInput.isFocused();
    }
}
