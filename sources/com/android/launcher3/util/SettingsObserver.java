package com.android.launcher3.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

public interface SettingsObserver {
    void onSettingChanged(boolean z);

    void register(String str, String... strArr);

    void unregister();

    public static abstract class Secure extends ContentObserver implements SettingsObserver {
        private String mKeySetting;
        private ContentResolver mResolver;

        public Secure(ContentResolver resolver) {
            super(new Handler());
            this.mResolver = resolver;
        }

        public void register(String keySetting, String... dependentSettings) {
            this.mKeySetting = keySetting;
            this.mResolver.registerContentObserver(Settings.Secure.getUriFor(this.mKeySetting), false, this);
            for (String setting : dependentSettings) {
                this.mResolver.registerContentObserver(Settings.Secure.getUriFor(setting), false, this);
            }
            onChange(true);
        }

        public void unregister() {
            this.mResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            boolean z = true;
            if (Settings.Secure.getInt(this.mResolver, this.mKeySetting, 1) != 1) {
                z = false;
            }
            onSettingChanged(z);
        }
    }

    public static abstract class System extends ContentObserver implements SettingsObserver {
        private String mKeySetting;
        private ContentResolver mResolver;

        public System(ContentResolver resolver) {
            super(new Handler());
            this.mResolver = resolver;
        }

        public void register(String keySetting, String... dependentSettings) {
            this.mKeySetting = keySetting;
            this.mResolver.registerContentObserver(Settings.System.getUriFor(this.mKeySetting), false, this);
            for (String setting : dependentSettings) {
                this.mResolver.registerContentObserver(Settings.System.getUriFor(setting), false, this);
            }
            onChange(true);
        }

        public void unregister() {
            this.mResolver.unregisterContentObserver(this);
        }

        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            boolean z = true;
            if (Settings.System.getInt(this.mResolver, this.mKeySetting, 1) != 1) {
                z = false;
            }
            onSettingChanged(z);
        }
    }
}
