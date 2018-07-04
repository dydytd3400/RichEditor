package com.echoleaf.richeditor.menu;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.echoleaf.richeditor.R;
import com.echoleaf.richeditor.Style;
import com.echoleaf.richeditor.richview.RichTextView;

import static android.content.Context.CLIPBOARD_SERVICE;

@SuppressLint("NewApi")
public class SimpleSelectionActionMenu extends ActionMode.Callback2 {

    private final int SELECT_COPY = 0x1;
    private final int SELECT_CUT = 0x2;

    private Menu mMenu;
    private EditText mEditText;
    private Context mContext;

    public SimpleSelectionActionMenu(Context context, EditText editText) {
        this.mEditText = editText;
        this.mContext = context;
    }


    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        MenuInflater menuInflater = actionMode.getMenuInflater();
        menu.clear();
        menuInflater.inflate(R.menu.simple_text_select_menu, menu);
        int start = mEditText.getSelectionStart();
        int end = mEditText.getSelectionEnd();
        if (start == 0 && mEditText.length() == end)
            menu.removeItem(R.id.all);
        menu.setGroupVisible(R.id.style_group, mEditText instanceof RichTextView);
        this.mMenu = menu;
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.all) {
            mEditText.selectAll();
        } else if (itemId == R.id.copy) {
            //setText(selectText)是为了后面的this.mMenu.close()起作用
            mEditText.setText(copyOrCut(SELECT_COPY));
            Toast.makeText(mContext, "已复制到剪切板", Toast.LENGTH_SHORT).show();
            this.mMenu.close();
        } else if (itemId == R.id.cut) {
            mEditText.setText(copyOrCut(SELECT_CUT));
            Toast.makeText(mContext, "已剪切到剪切板", Toast.LENGTH_SHORT).show();
            this.mMenu.close();
        } else if (itemId == R.id.paste) {
            ClipboardManager cbs = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
            if (cbs.hasPrimaryClip()) {
                ClipData.Item itemAt = cbs.getPrimaryClip().getItemAt(0);
                mEditText.getText().replace(mEditText.getSelectionStart(), mEditText.getSelectionEnd(), itemAt.getText());
            }
            this.mMenu.close();
        } else {
            styleActionItem(itemId);
        }
        return true;
    }

    private void styleActionItem(int itemId) {
        if (!(mEditText instanceof RichTextView)) {
            this.mMenu.close();
            return;
        }
        RichTextView richEditText = (RichTextView) mEditText;
        int style = 0;
        if (itemId == R.id.bold) {
            style = Style.BOLD;
        } else if (itemId == R.id.strike) {
            style = Style.STRIKE;
        } else if (itemId == R.id.underline) {
            style = Style.UNDERLINE;
        } else if (itemId == R.id.italic) {
            style = Style.ITALIC;
        } else if (itemId == R.id.clear) {
            richEditText.fromText(richEditText.toText());
            this.mMenu.close();
            return;
        }
        richEditText.setStyle(style);
        Editable text = mEditText.getText();
        mEditText.setText(text);
        mEditText.setSelection(text.length(), text.length());
        this.mMenu.close();
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
    }

    private Editable copyOrCut(int mode) {
        ClipboardManager cbs = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
        int selectionStart = mEditText.getSelectionStart();
        int selectionEnd = mEditText.getSelectionEnd();
        Editable txt = mEditText.getText();
        CharSequence substring = txt.subSequence(selectionStart, selectionEnd);
        cbs.setPrimaryClip(ClipData.newPlainText(null, substring));
        if (mode == SELECT_COPY)
            return txt;
        txt = txt.replace(selectionStart, selectionEnd, "");
        return txt;
    }

}
