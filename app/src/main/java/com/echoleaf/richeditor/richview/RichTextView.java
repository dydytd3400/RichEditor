package com.echoleaf.richeditor.richview;

import com.echoleaf.richeditor.Style;
import com.echoleaf.richeditor.listener.OnSelectionChangeListener;

/**
 * 富文本编辑器文本内容块
 *
 * @author echoleaf
 */
public interface RichTextView extends RichView {

    void setStyle(@Style.StyleVal int style);

    boolean hasStyle(@Style.StyleVal int style);

    void setTextSize(float textSize);

    void setTextSize(float textSize, int star, int end);

    void setTextSize(int unit, float size);

    void setTextSize(int unit, float size, int star, int end);

    void setTextColor(int color);

    void setTextColor(int color, int star, int end);

    void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener);

    int getSelectionStart();

    int getSelectionEnd();

}
