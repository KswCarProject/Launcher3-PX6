package com.android.launcher3.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.ShortcutAndWidgetContainer;
import java.lang.reflect.Array;
import java.util.Arrays;

public class FocusLogic {
    public static final int ALL_APPS_COLUMN = -11;
    public static final int CURRENT_PAGE_FIRST_ITEM = -6;
    public static final int CURRENT_PAGE_LAST_ITEM = -7;
    private static final boolean DEBUG = false;
    public static final int EMPTY = -1;
    public static final int NEXT_PAGE_FIRST_ITEM = -8;
    public static final int NEXT_PAGE_LEFT_COLUMN = -9;
    public static final int NEXT_PAGE_RIGHT_COLUMN = -10;
    public static final int NOOP = -1;
    public static final int PIVOT = 100;
    public static final int PREVIOUS_PAGE_FIRST_ITEM = -3;
    public static final int PREVIOUS_PAGE_LAST_ITEM = -4;
    public static final int PREVIOUS_PAGE_LEFT_COLUMN = -5;
    public static final int PREVIOUS_PAGE_RIGHT_COLUMN = -2;
    private static final String TAG = "FocusLogic";

    public static boolean shouldConsume(int keyCode) {
        return keyCode == 21 || keyCode == 22 || keyCode == 19 || keyCode == 20 || keyCode == 122 || keyCode == 123 || keyCode == 92 || keyCode == 93;
    }

    public static int handleKeyEvent(int keyCode, int[][] map, int iconIdx, int pageIndex, int pageCount, boolean isRtl) {
        int cntX = map == null ? -1 : map.length;
        int cntY = map == null ? -1 : map[0].length;
        switch (keyCode) {
            case 19:
                return handleDpadVertical(iconIdx, cntX, cntY, map, -1);
            case 20:
                return handleDpadVertical(iconIdx, cntX, cntY, map, 1);
            case 21:
                int newIndex = handleDpadHorizontal(iconIdx, cntX, cntY, map, -1, isRtl);
                if (!isRtl && newIndex == -1 && pageIndex > 0) {
                    return -2;
                }
                if (!isRtl || newIndex != -1 || pageIndex >= pageCount - 1) {
                    return newIndex;
                }
                return -10;
            case 22:
                int newIndex2 = handleDpadHorizontal(iconIdx, cntX, cntY, map, 1, isRtl);
                if (!isRtl && newIndex2 == -1 && pageIndex < pageCount - 1) {
                    return -9;
                }
                if (!isRtl || newIndex2 != -1 || pageIndex <= 0) {
                    return newIndex2;
                }
                return -5;
            case 92:
                return handlePageUp(pageIndex);
            case 93:
                return handlePageDown(pageIndex, pageCount);
            case 122:
                return handleMoveHome();
            case 123:
                return handleMoveEnd();
            default:
                return -1;
        }
    }

    private static int[][] createFullMatrix(int m, int n) {
        int[][] matrix = (int[][]) Array.newInstance(int.class, new int[]{m, n});
        for (int i = 0; i < m; i++) {
            Arrays.fill(matrix[i], -1);
        }
        return matrix;
    }

    public static int[][] createSparseMatrix(CellLayout layout) {
        ShortcutAndWidgetContainer parent = layout.getShortcutsAndWidgets();
        int m = layout.getCountX();
        int n = layout.getCountY();
        boolean invert = parent.invertLayoutHorizontally();
        int[][] matrix = createFullMatrix(m, n);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View cell = parent.getChildAt(i);
            if (cell.isFocusable()) {
                int cx = ((CellLayout.LayoutParams) cell.getLayoutParams()).cellX;
                int cy = ((CellLayout.LayoutParams) cell.getLayoutParams()).cellY;
                int x = invert ? (m - cx) - 1 : cx;
                if (x < m && cy < n) {
                    matrix[x][cy] = i;
                }
            }
        }
        return matrix;
    }

    public static int[][] createSparseMatrixWithHotseat(CellLayout iconLayout, CellLayout hotseatLayout, DeviceProfile dp) {
        int n;
        int m;
        ViewGroup iconParent = iconLayout.getShortcutsAndWidgets();
        ViewGroup hotseatParent = hotseatLayout.getShortcutsAndWidgets();
        boolean isHotseatHorizontal = !dp.isVerticalBarLayout();
        if (isHotseatHorizontal) {
            m = hotseatLayout.getCountX();
            n = iconLayout.getCountY() + hotseatLayout.getCountY();
        } else {
            m = iconLayout.getCountX() + hotseatLayout.getCountX();
            n = hotseatLayout.getCountY();
        }
        int[][] matrix = createFullMatrix(m, n);
        if (0 != 0) {
            int allappsiconRank = dp.inv.getAllAppsButtonRank();
            if (isHotseatHorizontal) {
                for (int j = 0; j < n; j++) {
                    matrix[allappsiconRank][j] = -11;
                }
            } else {
                for (int j2 = 0; j2 < m; j2++) {
                    matrix[j2][allappsiconRank] = -11;
                }
            }
        }
        for (int i = 0; i < iconParent.getChildCount(); i++) {
            View cell = iconParent.getChildAt(i);
            if (cell.isFocusable()) {
                int cx = ((CellLayout.LayoutParams) cell.getLayoutParams()).cellX;
                int cy = ((CellLayout.LayoutParams) cell.getLayoutParams()).cellY;
                if (0 != 0) {
                    int allappsiconRank2 = dp.inv.getAllAppsButtonRank();
                    if (isHotseatHorizontal && cx >= allappsiconRank2) {
                        cx++;
                    }
                    if (!isHotseatHorizontal && cy >= allappsiconRank2) {
                        cy++;
                    }
                }
                matrix[cx][cy] = i;
            }
        }
        for (int i2 = hotseatParent.getChildCount() - 1; i2 >= 0; i2--) {
            if (isHotseatHorizontal) {
                matrix[((CellLayout.LayoutParams) hotseatParent.getChildAt(i2).getLayoutParams()).cellX][iconLayout.getCountY()] = iconParent.getChildCount() + i2;
            } else {
                matrix[iconLayout.getCountX()][((CellLayout.LayoutParams) hotseatParent.getChildAt(i2).getLayoutParams()).cellY] = iconParent.getChildCount() + i2;
            }
        }
        return matrix;
    }

    public static int[][] createSparseMatrixWithPivotColumn(CellLayout iconLayout, int pivotX, int pivotY) {
        ViewGroup iconParent = iconLayout.getShortcutsAndWidgets();
        int[][] matrix = createFullMatrix(iconLayout.getCountX() + 1, iconLayout.getCountY());
        for (int i = 0; i < iconParent.getChildCount(); i++) {
            View cell = iconParent.getChildAt(i);
            if (cell.isFocusable()) {
                int cx = ((CellLayout.LayoutParams) cell.getLayoutParams()).cellX;
                int cy = ((CellLayout.LayoutParams) cell.getLayoutParams()).cellY;
                if (pivotX < 0) {
                    matrix[cx - pivotX][cy] = i;
                } else {
                    matrix[cx][cy] = i;
                }
            }
        }
        if (pivotX < 0) {
            matrix[0][pivotY] = 100;
        } else {
            matrix[pivotX][pivotY] = 100;
        }
        return matrix;
    }

    private static int handleDpadHorizontal(int iconIdx, int cntX, int cntY, int[][] matrix, int increment, boolean isRtl) {
        int i;
        int i2 = iconIdx;
        int i3 = cntX;
        int i4 = cntY;
        int[][] iArr = matrix;
        int i5 = increment;
        if (iArr != null) {
            int newIconIndex = -1;
            int xPos = -1;
            int yPos = -1;
            int i6 = 0;
            while (i6 < i3) {
                int yPos2 = xPos;
                int xPos2 = yPos;
                for (int j = 0; j < i4; j++) {
                    if (iArr[i6][j] == i2) {
                        xPos2 = i6;
                        yPos2 = j;
                    }
                }
                i6++;
                yPos = xPos2;
                xPos = yPos2;
            }
            int x = yPos + i5;
            while (true) {
                i = -11;
                if (x < 0 || x >= i3) {
                    boolean haveCrossedAllAppsColumn1 = false;
                    boolean haveCrossedAllAppsColumn2 = false;
                    int coeff = 1;
                } else {
                    int inspectMatrix = inspectMatrix(x, xPos, i3, i4, iArr);
                    newIconIndex = inspectMatrix;
                    if (inspectMatrix != -1 && newIconIndex != -11) {
                        return newIconIndex;
                    }
                    x += i5;
                }
            }
            boolean haveCrossedAllAppsColumn12 = false;
            boolean haveCrossedAllAppsColumn22 = false;
            int coeff2 = 1;
            while (coeff2 < i4) {
                int nextYPos1 = (coeff2 * i5) + xPos;
                int nextYPos2 = xPos - (coeff2 * i5);
                int x2 = yPos + (i5 * coeff2);
                if (inspectMatrix(x2, nextYPos1, i3, i4, iArr) == i) {
                    haveCrossedAllAppsColumn12 = true;
                }
                if (inspectMatrix(x2, nextYPos2, i3, i4, iArr) == i) {
                    haveCrossedAllAppsColumn22 = true;
                }
                while (x2 >= 0 && x2 < i3) {
                    int newIconIndex2 = inspectMatrix(x2, nextYPos1 + ((!haveCrossedAllAppsColumn12 || x2 >= i3 + -1) ? 0 : i5), i3, i4, iArr);
                    if (newIconIndex2 != -1) {
                        return newIconIndex2;
                    }
                    int i7 = newIconIndex2;
                    newIconIndex = inspectMatrix(x2, nextYPos2 + ((!haveCrossedAllAppsColumn22 || x2 >= i3 + -1) ? 0 : -i5), i3, i4, iArr);
                    if (newIconIndex != -1) {
                        return newIconIndex;
                    }
                    x2 += i5;
                    i3 = cntX;
                }
                coeff2++;
                i3 = cntX;
                i = -11;
            }
            if (i2 != 100) {
                return newIconIndex;
            }
            if (isRtl) {
                if (i5 < 0) {
                    return -8;
                }
                return -4;
            } else if (i5 < 0) {
                return -4;
            } else {
                return -8;
            }
        } else {
            throw new IllegalStateException("Dpad navigation requires a matrix.");
        }
    }

    private static int handleDpadVertical(int iconIndex, int cntX, int cntY, int[][] matrix, int increment) {
        int i;
        int i2 = cntX;
        int i3 = cntY;
        int[][] iArr = matrix;
        int i4 = increment;
        int newIconIndex = -1;
        if (iArr != null) {
            int yPos = -1;
            int yPos2 = -1;
            int i5 = 0;
            while (i5 < i2) {
                int xPos = yPos2;
                int yPos3 = yPos;
                for (int j = 0; j < i3; j++) {
                    if (iArr[i5][j] == iconIndex) {
                        xPos = i5;
                        yPos3 = j;
                    }
                }
                int i6 = iconIndex;
                i5++;
                yPos = yPos3;
                yPos2 = xPos;
            }
            int i7 = iconIndex;
            int y = yPos + i4;
            while (true) {
                i = -11;
                if (y < 0 || y >= i3 || y < 0) {
                    boolean haveCrossedAllAppsColumn1 = false;
                    boolean haveCrossedAllAppsColumn2 = false;
                    int coeff = 1;
                } else {
                    int inspectMatrix = inspectMatrix(yPos2, y, i2, i3, iArr);
                    newIconIndex = inspectMatrix;
                    if (inspectMatrix != -1 && newIconIndex != -11) {
                        return newIconIndex;
                    }
                    y += i4;
                }
            }
            boolean haveCrossedAllAppsColumn12 = false;
            boolean haveCrossedAllAppsColumn22 = false;
            int coeff2 = 1;
            while (coeff2 < i2) {
                int nextXPos1 = (coeff2 * i4) + yPos2;
                int nextXPos2 = yPos2 - (coeff2 * i4);
                int y2 = yPos + (i4 * coeff2);
                if (inspectMatrix(nextXPos1, y2, i2, i3, iArr) == i) {
                    haveCrossedAllAppsColumn12 = true;
                }
                if (inspectMatrix(nextXPos2, y2, i2, i3, iArr) == i) {
                    haveCrossedAllAppsColumn22 = true;
                }
                while (y2 >= 0 && y2 < i3) {
                    int newIconIndex2 = inspectMatrix(nextXPos1 + ((!haveCrossedAllAppsColumn12 || y2 >= i3 + -1) ? 0 : i4), y2, i2, i3, iArr);
                    if (newIconIndex2 != -1) {
                        return newIconIndex2;
                    }
                    int i8 = newIconIndex2;
                    newIconIndex = inspectMatrix(nextXPos2 + ((!haveCrossedAllAppsColumn22 || y2 >= i3 + -1) ? 0 : -i4), y2, i2, i3, iArr);
                    if (newIconIndex != -1) {
                        return newIconIndex;
                    }
                    y2 += i4;
                    i2 = cntX;
                }
                coeff2++;
                i2 = cntX;
                i = -11;
            }
            return newIconIndex;
        }
        int i9 = iconIndex;
        throw new IllegalStateException("Dpad navigation requires a matrix.");
    }

    private static int handleMoveHome() {
        return -6;
    }

    private static int handleMoveEnd() {
        return -7;
    }

    private static int handlePageDown(int pageIndex, int pageCount) {
        if (pageIndex < pageCount - 1) {
            return -8;
        }
        return -7;
    }

    private static int handlePageUp(int pageIndex) {
        if (pageIndex > 0) {
            return -3;
        }
        return -6;
    }

    private static boolean isValid(int xPos, int yPos, int countX, int countY) {
        return xPos >= 0 && xPos < countX && yPos >= 0 && yPos < countY;
    }

    private static int inspectMatrix(int x, int y, int cntX, int cntY, int[][] matrix) {
        if (!isValid(x, y, cntX, cntY) || matrix[x][y] == -1) {
            return -1;
        }
        return matrix[x][y];
    }

    private static String getStringIndex(int index) {
        switch (index) {
            case ALL_APPS_COLUMN /*-11*/:
                return "ALL_APPS_COLUMN";
            case NEXT_PAGE_LEFT_COLUMN /*-9*/:
                return "NEXT_PAGE_LEFT_COLUMN";
            case NEXT_PAGE_FIRST_ITEM /*-8*/:
                return "NEXT_PAGE_FIRST";
            case CURRENT_PAGE_LAST_ITEM /*-7*/:
                return "CURRENT_PAGE_LAST";
            case CURRENT_PAGE_FIRST_ITEM /*-6*/:
                return "CURRENT_PAGE_FIRST";
            case -4:
                return "PREVIOUS_PAGE_LAST";
            case -3:
                return "PREVIOUS_PAGE_FIRST";
            case -2:
                return "PREVIOUS_PAGE_RIGHT_COLUMN";
            case -1:
                return "NOOP";
            default:
                return Integer.toString(index);
        }
    }

    private static void printMatrix(int[][] matrix) {
        Log.v(TAG, "\tprintMap:");
        int m = matrix.length;
        int n = matrix[0].length;
        for (int j = 0; j < n; j++) {
            String colY = "\t\t";
            for (int i = 0; i < m; i++) {
                colY = colY + String.format("%3d", new Object[]{Integer.valueOf(matrix[i][j])});
            }
            Log.v(TAG, colY);
        }
    }

    public static View getAdjacentChildInNextFolderPage(ShortcutAndWidgetContainer nextPage, View oldView, int edgeColumn) {
        int newRow = ((CellLayout.LayoutParams) oldView.getLayoutParams()).cellY;
        int column = 0;
        if (!((edgeColumn == -9) ^ nextPage.invertLayoutHorizontally())) {
            column = ((CellLayout) nextPage.getParent()).getCountX() - 1;
        }
        while (column >= 0) {
            for (int row = newRow; row >= 0; row--) {
                View newView = nextPage.getChildAt(column, row);
                if (newView != null) {
                    return newView;
                }
            }
            column--;
        }
        return null;
    }
}
