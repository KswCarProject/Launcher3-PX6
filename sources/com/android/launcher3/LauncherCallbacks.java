package com.android.launcher3;

import android.content.Intent;
import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public interface LauncherCallbacks {
    void bindAllApplications(ArrayList<AppInfo> arrayList);

    void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    boolean handleBackPressed();

    boolean hasSettings();

    void onActivityResult(int i, int i2, Intent intent);

    void onAttachedToWindow();

    void onCreate(Bundle bundle);

    void onDestroy();

    void onDetachedFromWindow();

    void onHomeIntent(boolean z);

    void onLauncherProviderChange();

    void onPause();

    void onRequestPermissionsResult(int i, String[] strArr, int[] iArr);

    void onResume();

    void onSaveInstanceState(Bundle bundle);

    void onStart();

    void onStop();

    void onTrimMemory(int i);

    boolean startSearch(String str, boolean z, Bundle bundle);
}
