package com.szchoiceway.index;

public class SpringLoadedDragController implements OnAlarmListener {
    final long ENTER_SPRING_LOAD_CANCEL_HOVER_TIME = 950;
    final long ENTER_SPRING_LOAD_HOVER_TIME = 500;
    final long EXIT_SPRING_LOAD_HOVER_TIME = 200;
    Alarm mAlarm;
    private Launcher mLauncher;
    private CellLayout mScreen;

    public SpringLoadedDragController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mAlarm = new Alarm();
        this.mAlarm.setOnAlarmListener(this);
    }

    public void cancel() {
        this.mAlarm.cancelAlarm();
    }

    public void setAlarm(CellLayout cl) {
        this.mAlarm.cancelAlarm();
        this.mAlarm.setAlarm(cl == null ? 950 : 500);
        this.mScreen = cl;
    }

    public void onAlarm(Alarm alarm) {
        if (this.mScreen != null) {
            Workspace w = this.mLauncher.getWorkspace();
            int page = w.indexOfChild(this.mScreen);
            if (page != w.getCurrentPage()) {
                w.snapToPage(page);
                return;
            }
            return;
        }
        this.mLauncher.getDragController().cancelDrag();
    }
}
