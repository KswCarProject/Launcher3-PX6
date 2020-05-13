package android.support.v4.widget;

import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ListPopupWindow;

@RequiresApi(19)
class ListPopupWindowCompatKitKat {
    ListPopupWindowCompatKitKat() {
    }

    public static View.OnTouchListener createDragToOpenListener(Object listPopupWindow, View src) {
        return ((ListPopupWindow) listPopupWindow).createDragToOpenListener(src);
    }
}
