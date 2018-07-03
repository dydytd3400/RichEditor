package com.echoleaf.richeditor.listener;


import android.widget.EditText;

import com.echoleaf.richeditor.RichEditor;
import com.echoleaf.richeditor.piece.RichPiece;

public class SimplePieceChangeListener extends OnPieceChangeListener {

    @Override
    public void inTransactionChanged(RichEditor richEditor, RichPiece piece) {
        if (richEditor.getPieceCount() == 0 || piece == null || !(piece instanceof EditText)) {
            richEditor.insert(richEditor.createEditPiece());
        }
    }

}
