package com.android.launcher3;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.folder.Folder;
import com.android.launcher3.folder.FolderPagedView;
import com.android.launcher3.util.FocusLogic;

public class FocusHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "FocusHelper";

    public static class PagedFolderKeyEventListener implements View.OnKeyListener {
        private final Folder mFolder;

        public PagedFolderKeyEventListener(Folder folder) {
            this.mFolder = folder;
        }

        public boolean onKey(View v, int keyCode, KeyEvent e) {
            View view = v;
            int i = keyCode;
            boolean consume = FocusLogic.shouldConsume(keyCode);
            if (e.getAction() == 1) {
                return consume;
            }
            int i2 = 0;
            if (!(v.getParent() instanceof ShortcutAndWidgetContainer)) {
                return false;
            }
            ShortcutAndWidgetContainer itemContainer = (ShortcutAndWidgetContainer) v.getParent();
            CellLayout cellLayout = (CellLayout) itemContainer.getParent();
            int iconIndex = itemContainer.indexOfChild(view);
            FolderPagedView pagedView = (FolderPagedView) cellLayout.getParent();
            int pageIndex = pagedView.indexOfChild(cellLayout);
            int pageCount = pagedView.getPageCount();
            boolean isLayoutRtl = Utilities.isRtl(v.getResources());
            int[][] matrix = FocusLogic.createSparseMatrix(cellLayout);
            int[][] matrix2 = matrix;
            int newIconIndex = FocusLogic.handleKeyEvent(keyCode, matrix, iconIndex, pageIndex, pageCount, isLayoutRtl);
            if (newIconIndex == -1) {
                handleNoopKey(i, view);
                return consume;
            }
            View child = null;
            switch (newIconIndex) {
                case FocusLogic.NEXT_PAGE_RIGHT_COLUMN /*-10*/:
                case FocusLogic.NEXT_PAGE_LEFT_COLUMN /*-9*/:
                    ShortcutAndWidgetContainer newParent = FocusHelper.getCellLayoutChildrenForIndex(pagedView, pageIndex + 1);
                    if (newParent != null) {
                        pagedView.snapToPage(pageIndex + 1);
                        child = FocusLogic.getAdjacentChildInNextFolderPage(newParent, view, newIconIndex);
                        break;
                    }
                    break;
                case FocusLogic.NEXT_PAGE_FIRST_ITEM /*-8*/:
                    ShortcutAndWidgetContainer newParent2 = FocusHelper.getCellLayoutChildrenForIndex(pagedView, pageIndex + 1);
                    if (newParent2 != null) {
                        pagedView.snapToPage(pageIndex + 1);
                        child = newParent2.getChildAt(0, 0);
                        break;
                    }
                    break;
                case FocusLogic.CURRENT_PAGE_LAST_ITEM /*-7*/:
                    child = pagedView.getLastItem();
                    break;
                case FocusLogic.CURRENT_PAGE_FIRST_ITEM /*-6*/:
                    child = cellLayout.getChildAt(0, 0);
                    break;
                case FocusLogic.PREVIOUS_PAGE_LEFT_COLUMN /*-5*/:
                case -2:
                    ShortcutAndWidgetContainer newParent3 = FocusHelper.getCellLayoutChildrenForIndex(pagedView, pageIndex - 1);
                    if (newParent3 != null) {
                        int row = ((CellLayout.LayoutParams) v.getLayoutParams()).cellY;
                        pagedView.snapToPage(pageIndex - 1);
                        if (!((newIconIndex == -5) ^ newParent3.invertLayoutHorizontally())) {
                            i2 = matrix2.length - 1;
                        }
                        child = newParent3.getChildAt(i2, row);
                        break;
                    }
                    break;
                case -4:
                    ShortcutAndWidgetContainer newParent4 = FocusHelper.getCellLayoutChildrenForIndex(pagedView, pageIndex - 1);
                    if (newParent4 != null) {
                        pagedView.snapToPage(pageIndex - 1);
                        child = newParent4.getChildAt(matrix2.length - 1, matrix2[0].length - 1);
                        break;
                    }
                    break;
                case -3:
                    ShortcutAndWidgetContainer newParent5 = FocusHelper.getCellLayoutChildrenForIndex(pagedView, pageIndex - 1);
                    if (newParent5 != null) {
                        pagedView.snapToPage(pageIndex - 1);
                        child = newParent5.getChildAt(0, 0);
                        break;
                    }
                    break;
                default:
                    child = itemContainer.getChildAt(newIconIndex);
                    break;
            }
            if (child != null) {
                child.requestFocus();
                FocusHelper.playSoundEffect(i, view);
            } else {
                handleNoopKey(i, view);
            }
            return consume;
        }

        public void handleNoopKey(int keyCode, View v) {
            if (keyCode == 20) {
                this.mFolder.mFolderName.requestFocus();
                FocusHelper.playSoundEffect(keyCode, v);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00e9  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x012d A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean handleHotseatButtonKeyEvent(android.view.View r24, int r25, android.view.KeyEvent r26) {
        /*
            r0 = r24
            r1 = r25
            boolean r2 = com.android.launcher3.util.FocusLogic.shouldConsume(r25)
            int r3 = r26.getAction()
            r4 = 1
            if (r3 == r4) goto L_0x013e
            if (r2 != 0) goto L_0x0013
            goto L_0x013e
        L_0x0013:
            android.content.Context r3 = r24.getContext()
            com.android.launcher3.Launcher r3 = com.android.launcher3.Launcher.getLauncher(r3)
            com.android.launcher3.DeviceProfile r5 = r3.getDeviceProfile()
            android.view.View r6 = r24.getRootView()
            r7 = 2131427714(0x7f0b0182, float:1.8477052E38)
            android.view.View r6 = r6.findViewById(r7)
            com.android.launcher3.Workspace r6 = (com.android.launcher3.Workspace) r6
            android.view.ViewParent r7 = r24.getParent()
            com.android.launcher3.ShortcutAndWidgetContainer r7 = (com.android.launcher3.ShortcutAndWidgetContainer) r7
            android.view.ViewParent r8 = r7.getParent()
            com.android.launcher3.CellLayout r8 = (com.android.launcher3.CellLayout) r8
            java.lang.Object r9 = r24.getTag()
            int r15 = r6.getNextPage()
            int r16 = r6.getChildCount()
            int r10 = r7.indexOfChild(r0)
            com.android.launcher3.ShortcutAndWidgetContainer r11 = r8.getShortcutsAndWidgets()
            android.view.View r11 = r11.getChildAt(r10)
            android.view.ViewGroup$LayoutParams r11 = r11.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r11 = (com.android.launcher3.CellLayout.LayoutParams) r11
            int r14 = r11.cellX
            android.view.View r11 = r6.getChildAt(r15)
            r13 = r11
            com.android.launcher3.CellLayout r13 = (com.android.launcher3.CellLayout) r13
            if (r13 != 0) goto L_0x0062
            return r2
        L_0x0062:
            com.android.launcher3.ShortcutAndWidgetContainer r12 = r13.getShortcutsAndWidgets()
            r11 = 0
            r17 = 0
            int[][] r17 = (int[][]) r17
            r4 = 19
            if (r1 != r4) goto L_0x0085
            boolean r4 = r5.isVerticalBarLayout()
            if (r4 != 0) goto L_0x0085
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r13, r8, r5)
            int r17 = r12.getChildCount()
            int r10 = r10 + r17
            r11 = r12
        L_0x0080:
            r17 = r10
            r18 = r11
            goto L_0x00b4
        L_0x0085:
            r4 = 21
            if (r1 != r4) goto L_0x009b
            boolean r4 = r5.isVerticalBarLayout()
            if (r4 == 0) goto L_0x009b
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r13, r8, r5)
            int r17 = r12.getChildCount()
            int r10 = r10 + r17
            r11 = r12
            goto L_0x0080
        L_0x009b:
            r4 = 22
            if (r1 != r4) goto L_0x00ae
            boolean r4 = r5.isVerticalBarLayout()
            if (r4 == 0) goto L_0x00ae
            r1 = 93
            r18 = r11
            r4 = r17
            r17 = r10
            goto L_0x00b4
        L_0x00ae:
            int[][] r4 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r8)
            r11 = r7
            goto L_0x0080
        L_0x00b4:
            android.content.res.Resources r10 = r24.getResources()
            boolean r19 = com.android.launcher3.Utilities.isRtl(r10)
            r10 = r1
            r11 = r4
            r20 = r3
            r3 = r12
            r12 = r17
            r21 = r13
            r13 = r15
            r22 = r14
            r14 = r16
            r23 = r15
            r15 = r19
            int r10 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r10, r11, r12, r13, r14, r15)
            r11 = 0
            r12 = 0
            switch(r10) {
                case -10: goto L_0x0117;
                case -9: goto L_0x0117;
                case -8: goto L_0x0107;
                case -7: goto L_0x00d8;
                case -6: goto L_0x00d8;
                case -5: goto L_0x0101;
                case -4: goto L_0x00e9;
                case -3: goto L_0x00d9;
                case -2: goto L_0x0101;
                default: goto L_0x00d8;
            }
        L_0x00d8:
            goto L_0x011c
        L_0x00d9:
            int r15 = r23 + -1
            com.android.launcher3.ShortcutAndWidgetContainer r13 = getCellLayoutChildrenForIndex(r6, r15)
            android.view.View r11 = r13.getChildAt(r12)
            int r15 = r23 + -1
            r6.snapToPage(r15)
            goto L_0x011e
        L_0x00e9:
            int r15 = r23 + -1
            com.android.launcher3.ShortcutAndWidgetContainer r12 = getCellLayoutChildrenForIndex(r6, r15)
            int r13 = r12.getChildCount()
            r14 = 1
            int r13 = r13 - r14
            android.view.View r11 = r12.getChildAt(r13)
            int r15 = r23 + -1
            r6.snapToPage(r15)
            r13 = r12
            goto L_0x011e
        L_0x0101:
            int r15 = r23 + -1
            r6.snapToPage(r15)
            goto L_0x011c
        L_0x0107:
            int r15 = r23 + 1
            com.android.launcher3.ShortcutAndWidgetContainer r13 = getCellLayoutChildrenForIndex(r6, r15)
            android.view.View r11 = r13.getChildAt(r12)
            int r15 = r23 + 1
            r6.snapToPage(r15)
            goto L_0x011e
        L_0x0117:
            int r15 = r23 + 1
            r6.snapToPage(r15)
        L_0x011c:
            r13 = r18
        L_0x011e:
            if (r13 != r3) goto L_0x012b
            int r12 = r3.getChildCount()
            if (r10 < r12) goto L_0x012b
            int r12 = r3.getChildCount()
            int r10 = r10 - r12
        L_0x012b:
            if (r13 == 0) goto L_0x013d
            if (r11 != 0) goto L_0x0135
            if (r10 < 0) goto L_0x0135
            android.view.View r11 = r13.getChildAt(r10)
        L_0x0135:
            if (r11 == 0) goto L_0x013d
            r11.requestFocus()
            playSoundEffect(r1, r0)
        L_0x013d:
            return r2
        L_0x013e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleHotseatButtonKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }

    /* JADX WARNING: type inference failed for: r1v39, types: [android.view.View] */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0125, code lost:
        r27 = r4;
        r3 = r5;
        r4 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x023f, code lost:
        r6 = r12;
        r12 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0241, code lost:
        if (r19 == null) goto L_0x0249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0243, code lost:
        r19.requestFocus();
        playSoundEffect(r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x0249, code lost:
        return r8;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean handleIconKeyEvent(android.view.View r34, int r35, android.view.KeyEvent r36) {
        /*
            r0 = r34
            r7 = r35
            boolean r8 = com.android.launcher3.util.FocusLogic.shouldConsume(r35)
            int r1 = r36.getAction()
            r2 = 1
            if (r1 == r2) goto L_0x024a
            if (r8 != 0) goto L_0x0013
            goto L_0x024a
        L_0x0013:
            android.content.Context r1 = r34.getContext()
            com.android.launcher3.Launcher r9 = com.android.launcher3.Launcher.getLauncher(r1)
            com.android.launcher3.DeviceProfile r10 = r9.getDeviceProfile()
            android.view.ViewParent r1 = r34.getParent()
            r11 = r1
            com.android.launcher3.ShortcutAndWidgetContainer r11 = (com.android.launcher3.ShortcutAndWidgetContainer) r11
            android.view.ViewParent r1 = r11.getParent()
            r12 = r1
            com.android.launcher3.CellLayout r12 = (com.android.launcher3.CellLayout) r12
            android.view.ViewParent r1 = r12.getParent()
            r13 = r1
            com.android.launcher3.Workspace r13 = (com.android.launcher3.Workspace) r13
            android.view.ViewParent r1 = r13.getParent()
            r14 = r1
            android.view.ViewGroup r14 = (android.view.ViewGroup) r14
            r1 = 2131427522(0x7f0b00c2, float:1.8476663E38)
            android.view.View r1 = r14.findViewById(r1)
            r15 = r1
            android.view.ViewGroup r15 = (android.view.ViewGroup) r15
            r1 = 2131427547(0x7f0b00db, float:1.8476713E38)
            android.view.View r1 = r14.findViewById(r1)
            r6 = r1
            com.android.launcher3.Hotseat r6 = (com.android.launcher3.Hotseat) r6
            java.lang.Object r1 = r34.getTag()
            r16 = r1
            int r17 = r11.indexOfChild(r0)
            int r5 = r13.indexOfChild(r12)
            int r18 = r13.getChildCount()
            r1 = 0
            android.view.View r1 = r6.getChildAt(r1)
            r4 = r1
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            com.android.launcher3.ShortcutAndWidgetContainer r3 = r4.getShortcutsAndWidgets()
            r1 = 20
            if (r7 != r1) goto L_0x007d
            boolean r1 = r10.isVerticalBarLayout()
            if (r1 != 0) goto L_0x007d
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r12, r4, r10)
        L_0x007b:
            r2 = r1
            goto L_0x0091
        L_0x007d:
            r1 = 22
            if (r7 != r1) goto L_0x008c
            boolean r1 = r10.isVerticalBarLayout()
            if (r1 == 0) goto L_0x008c
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithHotseat(r12, r4, r10)
            goto L_0x007b
        L_0x008c:
            int[][] r1 = com.android.launcher3.util.FocusLogic.createSparseMatrix(r12)
            goto L_0x007b
        L_0x0091:
            android.content.res.Resources r1 = r34.getResources()
            boolean r19 = com.android.launcher3.Utilities.isRtl(r1)
            r1 = r35
            r20 = r3
            r3 = r17
            r21 = r9
            r9 = r4
            r4 = r5
            r22 = r10
            r10 = r5
            r5 = r18
            r23 = r6
            r6 = r19
            int r6 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6)
            android.content.res.Resources r1 = r34.getResources()
            boolean r5 = com.android.launcher3.Utilities.isRtl(r1)
            r19 = 0
            android.view.View r1 = r13.getChildAt(r10)
            r4 = r1
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            switch(r6) {
                case -10: goto L_0x01a7;
                case -9: goto L_0x012c;
                case -8: goto L_0x0120;
                case -7: goto L_0x0115;
                case -6: goto L_0x010a;
                case -5: goto L_0x012c;
                case -4: goto L_0x0105;
                case -3: goto L_0x00ec;
                case -2: goto L_0x01a7;
                case -1: goto L_0x00dc;
                default: goto L_0x00c5;
            }
        L_0x00c5:
            r27 = r4
            r3 = r5
            r1 = r12
            r12 = r6
            if (r12 < 0) goto L_0x021d
            int r4 = r11.getChildCount()
            if (r12 >= r4) goto L_0x021d
            android.view.View r19 = r11.getChildAt(r12)
            r6 = r12
            r4 = r20
        L_0x00d9:
            r12 = r1
            goto L_0x0241
        L_0x00dc:
            r1 = 19
            if (r7 != r1) goto L_0x00e3
            r19 = r15
            goto L_0x0125
        L_0x00e3:
            r27 = r4
            r3 = r5
            r1 = r12
            r4 = r20
            r12 = r6
            goto L_0x023f
        L_0x00ec:
            int r1 = r10 + -1
            android.view.View r1 = r13.getChildAt(r1)
            r4 = r1
            com.android.launcher3.CellLayout r4 = (com.android.launcher3.CellLayout) r4
            android.view.View r19 = getFirstFocusableIconInReadingOrder(r4, r5)
            if (r19 != 0) goto L_0x0125
            android.view.View r19 = getFirstFocusableIconInReadingOrder(r9, r5)
            int r1 = r10 + -1
            r13.snapToPage(r1)
            goto L_0x0125
        L_0x0105:
            android.view.View r19 = handlePreviousPageLastItem(r13, r9, r10, r5)
            goto L_0x0125
        L_0x010a:
            android.view.View r19 = getFirstFocusableIconInReadingOrder(r4, r5)
            if (r19 != 0) goto L_0x0125
            android.view.View r19 = getFirstFocusableIconInReadingOrder(r9, r5)
            goto L_0x0125
        L_0x0115:
            android.view.View r19 = getFirstFocusableIconInReverseReadingOrder(r4, r5)
            if (r19 != 0) goto L_0x0125
            android.view.View r19 = getFirstFocusableIconInReverseReadingOrder(r9, r5)
            goto L_0x0125
        L_0x0120:
            android.view.View r19 = handleNextPageFirstItem(r13, r9, r10, r5)
        L_0x0125:
            r27 = r4
            r3 = r5
            r4 = r20
            goto L_0x0241
        L_0x012c:
            int r24 = r10 + 1
            r1 = -5
            if (r6 != r1) goto L_0x0134
            int r1 = r10 + -1
            goto L_0x0136
        L_0x0134:
            r1 = r24
        L_0x0136:
            android.view.ViewGroup$LayoutParams r24 = r34.getLayoutParams()
            r3 = r24
            com.android.launcher3.CellLayout$LayoutParams r3 = (com.android.launcher3.CellLayout.LayoutParams) r3
            int r3 = r3.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r11 = getCellLayoutChildrenForIndex(r13, r1)
            if (r11 == 0) goto L_0x019d
            android.view.ViewParent r24 = r11.getParent()
            r12 = r24
            com.android.launcher3.CellLayout r12 = (com.android.launcher3.CellLayout) r12
            r27 = r1
            r1 = -1
            int[][] r24 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithPivotColumn(r12, r1, r3)
            r28 = 100
            android.content.res.Resources r1 = r34.getResources()
            boolean r29 = com.android.launcher3.Utilities.isRtl(r1)
            r25 = r27
            r2 = -8
            r1 = r35
            r30 = r12
            r12 = -8
            r2 = r24
            r26 = r3
            r3 = r28
            r27 = r4
            r4 = r25
            r31 = r5
            r5 = r18
            r32 = r6
            r6 = r29
            int r6 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6)
            if (r6 != r12) goto L_0x018e
            r5 = r31
            android.view.View r19 = handleNextPageFirstItem(r13, r9, r10, r5)
        L_0x0185:
            r3 = r5
            r4 = r20
            r2 = r24
            r12 = r30
            goto L_0x0241
        L_0x018e:
            r5 = r31
            r4 = -4
            if (r6 != r4) goto L_0x0198
            android.view.View r19 = handlePreviousPageLastItem(r13, r9, r10, r5)
            goto L_0x0185
        L_0x0198:
            android.view.View r19 = r11.getChildAt(r6)
            goto L_0x0185
        L_0x019d:
            r27 = r4
            r32 = r6
            r1 = r12
            r3 = r5
            r4 = r20
            goto L_0x0241
        L_0x01a7:
            r27 = r4
            r32 = r6
            r1 = r12
            r4 = -4
            r12 = -8
            int r3 = r10 + -1
            r6 = -10
            r12 = r32
            if (r12 != r6) goto L_0x01b8
            int r3 = r10 + 1
        L_0x01b8:
            r6 = r3
            android.view.ViewGroup$LayoutParams r3 = r34.getLayoutParams()
            com.android.launcher3.CellLayout$LayoutParams r3 = (com.android.launcher3.CellLayout.LayoutParams) r3
            int r3 = r3.cellY
            com.android.launcher3.ShortcutAndWidgetContainer r11 = getCellLayoutChildrenForIndex(r13, r6)
            if (r11 == 0) goto L_0x0217
            android.view.ViewParent r24 = r11.getParent()
            r1 = r24
            com.android.launcher3.CellLayout r1 = (com.android.launcher3.CellLayout) r1
            int r4 = r1.getCountX()
            int[][] r24 = com.android.launcher3.util.FocusLogic.createSparseMatrixWithPivotColumn(r1, r4, r3)
            r4 = 100
            android.content.res.Resources r2 = r34.getResources()
            boolean r25 = com.android.launcher3.Utilities.isRtl(r2)
            r26 = r1
            r1 = r35
            r2 = r24
            r28 = r3
            r3 = r4
            r4 = r6
            r33 = r5
            r5 = r18
            r29 = r6
            r6 = r25
            int r6 = com.android.launcher3.util.FocusLogic.handleKeyEvent(r1, r2, r3, r4, r5, r6)
            r1 = -8
            if (r6 != r1) goto L_0x0208
            r3 = r33
            android.view.View r19 = handleNextPageFirstItem(r13, r9, r10, r3)
        L_0x0201:
            r4 = r20
            r2 = r24
            r12 = r26
            goto L_0x0241
        L_0x0208:
            r3 = r33
            r1 = -4
            if (r6 != r1) goto L_0x0212
            android.view.View r19 = handlePreviousPageLastItem(r13, r9, r10, r3)
            goto L_0x0201
        L_0x0212:
            android.view.View r19 = r11.getChildAt(r6)
            goto L_0x0201
        L_0x0217:
            r3 = r5
            r6 = r12
            r4 = r20
            r12 = r1
            goto L_0x0241
        L_0x021d:
            int r4 = r11.getChildCount()
            if (r4 > r12) goto L_0x023d
            int r4 = r11.getChildCount()
            int r5 = r20.getChildCount()
            int r4 = r4 + r5
            if (r12 >= r4) goto L_0x023d
            int r4 = r11.getChildCount()
            int r6 = r12 - r4
            r4 = r20
            android.view.View r19 = r4.getChildAt(r6)
            r6 = r12
            goto L_0x00d9
        L_0x023d:
            r4 = r20
        L_0x023f:
            r6 = r12
            r12 = r1
        L_0x0241:
            if (r19 == 0) goto L_0x0249
            r19.requestFocus()
            playSoundEffect(r7, r0)
        L_0x0249:
            return r8
        L_0x024a:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.FocusHelper.handleIconKeyEvent(android.view.View, int, android.view.KeyEvent):boolean");
    }

    static ShortcutAndWidgetContainer getCellLayoutChildrenForIndex(ViewGroup container, int i) {
        return ((CellLayout) container.getChildAt(i)).getShortcutsAndWidgets();
    }

    static void playSoundEffect(int keyCode, View v) {
        switch (keyCode) {
            case 19:
            case 92:
            case 122:
                v.playSoundEffect(2);
                return;
            case 20:
            case 93:
            case 123:
                v.playSoundEffect(4);
                return;
            case 21:
                v.playSoundEffect(1);
                return;
            case 22:
                v.playSoundEffect(3);
                return;
            default:
                return;
        }
    }

    private static View handlePreviousPageLastItem(Workspace workspace, CellLayout hotseatLayout, int pageIndex, boolean isRtl) {
        if (pageIndex - 1 < 0) {
            return null;
        }
        View newIcon = getFirstFocusableIconInReverseReadingOrder((CellLayout) workspace.getChildAt(pageIndex - 1), isRtl);
        if (newIcon != null) {
            return newIcon;
        }
        View newIcon2 = getFirstFocusableIconInReverseReadingOrder(hotseatLayout, isRtl);
        workspace.snapToPage(pageIndex - 1);
        return newIcon2;
    }

    private static View handleNextPageFirstItem(Workspace workspace, CellLayout hotseatLayout, int pageIndex, boolean isRtl) {
        if (pageIndex + 1 >= workspace.getPageCount()) {
            return null;
        }
        View newIcon = getFirstFocusableIconInReadingOrder((CellLayout) workspace.getChildAt(pageIndex + 1), isRtl);
        if (newIcon != null) {
            return newIcon;
        }
        View newIcon2 = getFirstFocusableIconInReadingOrder(hotseatLayout, isRtl);
        workspace.snapToPage(pageIndex + 1);
        return newIcon2;
    }

    private static View getFirstFocusableIconInReadingOrder(CellLayout cellLayout, boolean isRtl) {
        int countX = cellLayout.getCountX();
        for (int y = 0; y < cellLayout.getCountY(); y++) {
            int increment = isRtl ? -1 : 1;
            int x = isRtl ? countX - 1 : 0;
            while (x >= 0 && x < countX) {
                View childAt = cellLayout.getChildAt(x, y);
                View icon = childAt;
                if (childAt != null && icon.isFocusable()) {
                    return icon;
                }
                x += increment;
            }
        }
        return null;
    }

    private static View getFirstFocusableIconInReverseReadingOrder(CellLayout cellLayout, boolean isRtl) {
        int countX = cellLayout.getCountX();
        for (int y = cellLayout.getCountY() - 1; y >= 0; y--) {
            int increment = isRtl ? 1 : -1;
            int x = isRtl ? 0 : countX - 1;
            while (x >= 0 && x < countX) {
                View childAt = cellLayout.getChildAt(x, y);
                View icon = childAt;
                if (childAt != null && icon.isFocusable()) {
                    return icon;
                }
                x += increment;
            }
        }
        return null;
    }
}
