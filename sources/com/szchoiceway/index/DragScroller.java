package com.szchoiceway.index;

public interface DragScroller {
    boolean onEnterScrollArea(int i, int i2, int i3);

    boolean onExitScrollArea();

    void scrollLeft();

    void scrollRight();
}
