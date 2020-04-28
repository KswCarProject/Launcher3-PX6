package com.android.launcher3.util;

import android.graphics.Rect;
import com.android.launcher3.ItemInfo;
import java.lang.reflect.Array;

public class GridOccupancy {
    public final boolean[][] cells;
    private final int mCountX;
    private final int mCountY;

    public GridOccupancy(int countX, int countY) {
        this.mCountX = countX;
        this.mCountY = countY;
        this.cells = (boolean[][]) Array.newInstance(boolean.class, new int[]{countX, countY});
    }

    public boolean findVacantCell(int[] vacantOut, int spanX, int spanY) {
        for (int y = 0; y + spanY <= this.mCountY; y++) {
            for (int x = 0; x + spanX <= this.mCountX; x++) {
                int j = !this.cells[x][y];
                int i = x;
                while (true) {
                    if (i >= x + spanX) {
                        break;
                    }
                    int available = j;
                    for (int j2 = y; j2 < y + spanY; j2++) {
                        available = (available == 0 || this.cells[i][j2]) ? 0 : 1;
                        if (available == 0) {
                            j = available;
                            break;
                        }
                    }
                    i++;
                    j = available;
                }
                if (j != 0) {
                    vacantOut[0] = x;
                    vacantOut[1] = y;
                    return true;
                }
            }
        }
        return false;
    }

    public void copyTo(GridOccupancy dest) {
        for (int i = 0; i < this.mCountX; i++) {
            for (int j = 0; j < this.mCountY; j++) {
                dest.cells[i][j] = this.cells[i][j];
            }
        }
    }

    public boolean isRegionVacant(int x, int y, int spanX, int spanY) {
        int x2 = (x + spanX) - 1;
        int y2 = (y + spanY) - 1;
        if (x < 0 || y < 0 || x2 >= this.mCountX || y2 >= this.mCountY) {
            return false;
        }
        for (int i = x; i <= x2; i++) {
            for (int j = y; j <= y2; j++) {
                if (this.cells[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void markCells(int cellX, int cellY, int spanX, int spanY, boolean value) {
        if (cellX >= 0 && cellY >= 0) {
            int x = cellX;
            while (x < cellX + spanX && x < this.mCountX) {
                int y = cellY;
                while (y < cellY + spanY && y < this.mCountY) {
                    this.cells[x][y] = value;
                    y++;
                }
                x++;
            }
        }
    }

    public void markCells(Rect r, boolean value) {
        markCells(r.left, r.top, r.width(), r.height(), value);
    }

    public void markCells(CellAndSpan cell, boolean value) {
        markCells(cell.cellX, cell.cellY, cell.spanX, cell.spanY, value);
    }

    public void markCells(ItemInfo item, boolean value) {
        markCells(item.cellX, item.cellY, item.spanX, item.spanY, value);
    }

    public void clear() {
        markCells(0, 0, this.mCountX, this.mCountY, false);
    }
}
