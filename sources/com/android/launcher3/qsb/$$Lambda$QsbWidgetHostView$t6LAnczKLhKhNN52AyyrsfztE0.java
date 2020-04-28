package com.android.launcher3.qsb;

import android.os.Bundle;
import android.view.View;
import com.android.launcher3.Launcher;

/* renamed from: com.android.launcher3.qsb.-$$Lambda$QsbWidgetHostView$t6LAn-czKLhKhNN52AyyrsfztE0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$QsbWidgetHostView$t6LAnczKLhKhNN52AyyrsfztE0 implements View.OnClickListener {
    public static final /* synthetic */ $$Lambda$QsbWidgetHostView$t6LAnczKLhKhNN52AyyrsfztE0 INSTANCE = new $$Lambda$QsbWidgetHostView$t6LAnczKLhKhNN52AyyrsfztE0();

    private /* synthetic */ $$Lambda$QsbWidgetHostView$t6LAnczKLhKhNN52AyyrsfztE0() {
    }

    public final void onClick(View view) {
        Launcher.getLauncher(view.getContext()).startSearch("", false, (Bundle) null, true);
    }
}
