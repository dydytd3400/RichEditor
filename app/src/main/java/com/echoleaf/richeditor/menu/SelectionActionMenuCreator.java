package com.echoleaf.richeditor.menu;

import android.content.Context;
import android.os.Build;
import android.view.ActionMode;
import android.widget.EditText;

public abstract class SelectionActionMenuCreator {

    abstract protected ActionMode.Callback create(Context context, EditText editText);

    abstract protected ActionMode.Callback2 create2(Context context, EditText editText);

    public ActionMode.Callback getSelectionActionMenu(Context context, EditText editText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return create2(context, editText);
        } else {
            return create(context, editText);
        }
    }

}
