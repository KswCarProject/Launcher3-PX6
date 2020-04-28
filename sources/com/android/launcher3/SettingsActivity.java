package com.android.launcher3;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.launcher3.SettingsActivity;
import com.android.launcher3.graphics.IconShapeOverride;
import com.android.launcher3.notification.NotificationListener;
import com.android.launcher3.states.RotationHelper;
import com.android.launcher3.util.ListViewHighlighter;
import com.android.launcher3.util.SettingsObserver;
import com.android.launcher3.views.ButtonPreference;
import java.util.Objects;

public class SettingsActivity extends Activity {
    private static final int DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    private static final String EXTRA_FRAGMENT_ARG_KEY = ":settings:fragment_args_key";
    private static final String EXTRA_SHOW_FRAGMENT_ARGS = ":settings:show_fragment_args";
    private static final String ICON_BADGING_PREFERENCE_KEY = "pref_icon_badging";
    public static final String NOTIFICATION_BADGING = "notification_badging";
    private static final String NOTIFICATION_ENABLED_LISTENERS = "enabled_notification_listeners";
    private static final String SAVE_HIGHLIGHTED_KEY = "android:preference_highlighted";

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(16908290, getNewFragment()).commit();
        }
    }

    /* access modifiers changed from: protected */
    public PreferenceFragment getNewFragment() {
        return new LauncherSettingsFragment();
    }

    public static class LauncherSettingsFragment extends PreferenceFragment {
        private IconBadgingObserver mIconBadgingObserver;
        private boolean mPreferenceHighlighted = false;
        private String mPreferenceKey;

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
                this.mPreferenceHighlighted = savedInstanceState.getBoolean(SettingsActivity.SAVE_HIGHLIGHTED_KEY);
            }
            getPreferenceManager().setSharedPreferencesName(LauncherFiles.SHARED_PREFERENCES_KEY);
            addPreferencesFromResource(R.xml.launcher_preferences);
            ContentResolver resolver = getActivity().getContentResolver();
            ButtonPreference iconBadgingPref = (ButtonPreference) findPreference(SettingsActivity.ICON_BADGING_PREFERENCE_KEY);
            if (!Utilities.ATLEAST_OREO) {
                getPreferenceScreen().removePreference(findPreference(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY));
                getPreferenceScreen().removePreference(iconBadgingPref);
            } else if (!getResources().getBoolean(R.bool.notification_badging_enabled)) {
                getPreferenceScreen().removePreference(iconBadgingPref);
            } else {
                this.mIconBadgingObserver = new IconBadgingObserver(iconBadgingPref, resolver, getFragmentManager());
                this.mIconBadgingObserver.register(SettingsActivity.NOTIFICATION_BADGING, SettingsActivity.NOTIFICATION_ENABLED_LISTENERS);
            }
            Preference iconShapeOverride = findPreference(IconShapeOverride.KEY_PREFERENCE);
            if (iconShapeOverride != null) {
                if (IconShapeOverride.isSupported(getActivity())) {
                    IconShapeOverride.handlePreferenceUi((ListPreference) iconShapeOverride);
                } else {
                    getPreferenceScreen().removePreference(iconShapeOverride);
                }
            }
            Preference rotationPref = findPreference(RotationHelper.ALLOW_ROTATION_PREFERENCE_KEY);
            if (getResources().getBoolean(R.bool.allow_rotation)) {
                getPreferenceScreen().removePreference(rotationPref);
            } else {
                rotationPref.setDefaultValue(Boolean.valueOf(RotationHelper.getAllowRotationDefaultValue()));
            }
        }

        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putBoolean(SettingsActivity.SAVE_HIGHLIGHTED_KEY, this.mPreferenceHighlighted);
        }

        public void onResume() {
            super.onResume();
            this.mPreferenceKey = getActivity().getIntent().getStringExtra(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY);
            if (isAdded() && !this.mPreferenceHighlighted && !TextUtils.isEmpty(this.mPreferenceKey)) {
                getView().postDelayed(new Runnable() {
                    public final void run() {
                        SettingsActivity.LauncherSettingsFragment.this.highlightPreference();
                    }
                }, 600);
            }
        }

        /* access modifiers changed from: private */
        public void highlightPreference() {
            Preference pref = findPreference(this.mPreferenceKey);
            if (pref != null && getPreferenceScreen() != null) {
                PreferenceScreen screen = getPreferenceScreen();
                if (Utilities.ATLEAST_OREO) {
                    screen = selectPreferenceRecursive(pref, screen);
                }
                if (screen != null) {
                    ListView list = (ListView) (screen.getDialog() != null ? screen.getDialog().getWindow().getDecorView() : getView()).findViewById(16908298);
                    if (list != null && list.getAdapter() != null) {
                        Adapter adapter = list.getAdapter();
                        int position = -1;
                        int i = adapter.getCount() - 1;
                        while (true) {
                            if (i < 0) {
                                break;
                            } else if (pref == adapter.getItem(i)) {
                                position = i;
                                break;
                            } else {
                                i--;
                            }
                        }
                        new ListViewHighlighter(list, position);
                        this.mPreferenceHighlighted = true;
                    }
                }
            }
        }

        public void onDestroy() {
            if (this.mIconBadgingObserver != null) {
                this.mIconBadgingObserver.unregister();
                this.mIconBadgingObserver = null;
            }
            super.onDestroy();
        }

        @TargetApi(26)
        private PreferenceScreen selectPreferenceRecursive(Preference pref, PreferenceScreen topParent) {
            if (!(pref.getParent() instanceof PreferenceScreen)) {
                return null;
            }
            PreferenceScreen parent = (PreferenceScreen) pref.getParent();
            if (Objects.equals(parent.getKey(), topParent.getKey())) {
                return parent;
            }
            if (selectPreferenceRecursive(parent, topParent) == null) {
                return null;
            }
            ((PreferenceScreen) parent.getParent()).onItemClick((AdapterView) null, (View) null, parent.getOrder(), 0);
            return parent;
        }
    }

    private static class IconBadgingObserver extends SettingsObserver.Secure implements Preference.OnPreferenceClickListener {
        private final ButtonPreference mBadgingPref;
        private final FragmentManager mFragmentManager;
        private final ContentResolver mResolver;

        public IconBadgingObserver(ButtonPreference badgingPref, ContentResolver resolver, FragmentManager fragmentManager) {
            super(resolver);
            this.mBadgingPref = badgingPref;
            this.mResolver = resolver;
            this.mFragmentManager = fragmentManager;
        }

        public void onSettingChanged(boolean enabled) {
            int summary = enabled ? R.string.icon_badging_desc_on : R.string.icon_badging_desc_off;
            boolean serviceEnabled = true;
            boolean z = true;
            if (enabled) {
                String enabledListeners = Settings.Secure.getString(this.mResolver, SettingsActivity.NOTIFICATION_ENABLED_LISTENERS);
                ComponentName myListener = new ComponentName(this.mBadgingPref.getContext(), NotificationListener.class);
                serviceEnabled = enabledListeners != null && (enabledListeners.contains(myListener.flattenToString()) || enabledListeners.contains(myListener.flattenToShortString()));
                if (!serviceEnabled) {
                    summary = R.string.title_missing_notification_access;
                }
            }
            ButtonPreference buttonPreference = this.mBadgingPref;
            if (serviceEnabled) {
                z = false;
            }
            buttonPreference.setWidgetFrameVisible(z);
            this.mBadgingPref.setOnPreferenceClickListener(serviceEnabled ? null : this);
            this.mBadgingPref.setSummary(summary);
        }

        public boolean onPreferenceClick(Preference preference) {
            new NotificationAccessConfirmation().show(this.mFragmentManager, "notification_access");
            return true;
        }
    }

    public static class NotificationAccessConfirmation extends DialogFragment implements DialogInterface.OnClickListener {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Context context = getActivity();
            return new AlertDialog.Builder(context).setTitle(R.string.title_missing_notification_access).setMessage(context.getString(R.string.msg_missing_notification_access, new Object[]{context.getString(R.string.derived_app_name)})).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.title_change_settings, this).create();
        }

        public void onClick(DialogInterface dialogInterface, int i) {
            ComponentName cn = new ComponentName(getActivity(), NotificationListener.class);
            Bundle showFragmentArgs = new Bundle();
            showFragmentArgs.putString(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY, cn.flattenToString());
            getActivity().startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(268435456).putExtra(SettingsActivity.EXTRA_FRAGMENT_ARG_KEY, cn.flattenToString()).putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT_ARGS, showFragmentArgs));
        }
    }
}
