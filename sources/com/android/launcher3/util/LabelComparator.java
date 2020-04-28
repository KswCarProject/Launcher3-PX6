package com.android.launcher3.util;

import java.text.Collator;
import java.util.Comparator;

public class LabelComparator implements Comparator<String> {
    private final Collator mCollator = Collator.getInstance();

    public int compare(String titleA, String titleB) {
        boolean bStartsWithLetter = false;
        boolean aStartsWithLetter = titleA.length() > 0 && Character.isLetterOrDigit(titleA.codePointAt(0));
        if (titleB.length() > 0 && Character.isLetterOrDigit(titleB.codePointAt(0))) {
            bStartsWithLetter = true;
        }
        if (aStartsWithLetter && !bStartsWithLetter) {
            return -1;
        }
        if (aStartsWithLetter || !bStartsWithLetter) {
            return this.mCollator.compare(titleA, titleB);
        }
        return 1;
    }
}
