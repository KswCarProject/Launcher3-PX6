package com.android.launcher3.allapps;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Process;
import android.os.UserHandle;
import android.util.AttributeSet;
import android.widget.Switch;
import com.android.launcher3.compat.UserManagerCompat;

public class WorkModeSwitch extends Switch {
    public WorkModeSwitch(Context context) {
        super(context);
    }

    public WorkModeSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WorkModeSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChecked(boolean checked) {
    }

    public void toggle() {
        trySetQuietModeEnabledToAllProfilesAsync(isChecked());
    }

    private void setCheckedInternal(boolean checked) {
        super.setChecked(checked);
    }

    public void refresh() {
        setCheckedInternal(!UserManagerCompat.getInstance(getContext()).isAnyProfileQuietModeEnabled());
        setEnabled(true);
    }

    private void trySetQuietModeEnabledToAllProfilesAsync(final boolean enabled) {
        new AsyncTask<Void, Void, Boolean>() {
            /* access modifiers changed from: protected */
            public void onPreExecute() {
                super.onPreExecute();
                WorkModeSwitch.this.setEnabled(false);
            }

            /* access modifiers changed from: protected */
            public Boolean doInBackground(Void... voids) {
                UserManagerCompat userManager = UserManagerCompat.getInstance(WorkModeSwitch.this.getContext());
                boolean showConfirm = false;
                for (UserHandle userProfile : userManager.getUserProfiles()) {
                    if (!Process.myUserHandle().equals(userProfile)) {
                        showConfirm |= !userManager.requestQuietModeEnabled(enabled, userProfile);
                    }
                }
                return Boolean.valueOf(showConfirm);
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Boolean showConfirm) {
                if (showConfirm.booleanValue()) {
                    WorkModeSwitch.this.setEnabled(true);
                }
            }
        }.execute(new Void[0]);
    }
}
