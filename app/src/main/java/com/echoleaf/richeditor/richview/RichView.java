package com.echoleaf.richeditor.richview;


import android.view.View;

import com.echoleaf.richeditor.listener.OnContentChangeListener;

/**
 * 富文本编辑器显示模块
 *
 * @author echoleaf
 */
public interface RichView {

    String toText();

    String toHtml();

    void fromText(String text);

    void fromHtml(String html);

    void setReadOnly(boolean readOnly);

    boolean isReadOnly();

    void setOnContentChangeListener(OnContentChangeListener onContentChangeListener);

    void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener);
}
