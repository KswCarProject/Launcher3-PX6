package com.android.launcher3;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.os.ParcelFileDescriptor;
import com.android.launcher3.logging.FileLog;
import com.android.launcher3.provider.RestoreDbTask;

public class LauncherBackupAgent extends BackupAgent {
    public void onCreate() {
        super.onCreate();
        FileLog.setDir(getFilesDir());
    }

    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) {
    }

    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) {
    }

    public void onRestoreFinished() {
        RestoreDbTask.setPending(this, true);
    }
}
