package com.echoleaf.richeditor.piece;


import android.view.View;

import com.echoleaf.richeditor.richview.RichView;

/**
 * 富文本内容块
 * 富文本编辑器 @RichEditor 的内容由且仅由该对象填充
 *
 * @param <T> View 将要显示在 @RicheEditor 中的内容View。
 * @author echoleaf
 */
public interface RichPiece<T extends View> extends RichView {

    /**
     * 用于显示在RichEditor之内的View
     * 如果该View对象实现了RichView接口，那它则会将会由RichEditor进行完全管理。
     *
     * @return
     */
    T getView();

    /**
     * 回收对象
     */
    void recyle();
}
