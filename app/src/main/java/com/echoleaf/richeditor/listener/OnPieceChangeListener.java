package com.echoleaf.richeditor.listener;

import com.echoleaf.richeditor.RichEditor;
import com.echoleaf.richeditor.piece.RichPiece;

public abstract class OnPieceChangeListener {

    /**
     * 每次addRichPiece时均会调用
     *
     * @param richEditor
     * @param piece
     */
    public void onAdd(RichEditor richEditor, RichPiece piece) {
    }

    /**
     * 每次将RichPiece从richEditor移除后均会调用该方法
     *
     * @param richEditor
     * @param piece
     */
    public void onRemove(RichEditor richEditor, RichPiece piece) {
    }

    /**
     * 当外部单次调用RichEditor.insert/RichEditor.remove时，RichEditor内部可能会触发一次或多次onAdd/onRemove行为
     * 所以仅当单次调用全部完成以后才会触发一次该方法
     *
     * @param richEditor
     * @param lastPiece 最后一个受到更改的RichPiece
     */
    public void inTransactionChanged(RichEditor richEditor, RichPiece lastPiece) {
    }
}
