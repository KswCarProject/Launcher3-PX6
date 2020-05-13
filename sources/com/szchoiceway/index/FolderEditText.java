package com.szchoiceway.index;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class FolderEditText extends EditText {
    private Folder mFolder;

    public FolderEditText(Context context) {
        super(context);
    }

    public FolderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FolderEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
    }

    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == 4) {
            this.mFolder.doneEditingFolderName(true);
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
