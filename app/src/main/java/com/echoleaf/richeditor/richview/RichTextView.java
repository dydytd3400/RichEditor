package com.echoleaf.richeditor.richview;

import android.support.annotation.IntDef;

import com.echoleaf.richeditor.Style;
import com.echoleaf.richeditor.listener.OnSelectionChangeListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 富文本编辑器文本内容块
 *
 * @author echoleaf
 */
public interface RichTextView extends RichView {
    @IntDef({Style.NORMAL, Style.BOLD, Style.ITALIC, Style.BOLD_ITALIC, Style.UNDERLINE, Style.STRIKE})
    @Retention(RetentionPolicy.SOURCE)
    @interface StyleVal {
    }

    void setStyle(@StyleVal int style);

    boolean hasStyle(@StyleVal int style);

    void setTextSize(float textSize);

    void setTextSize(float textSize, int star, int end);

    void setTextSize(int unit, float size);

    void setTextSize(int unit, float size, int star, int end);

    void setTextColor(int color);

    void setTextColor(int color, int star, int end);

    void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener);

}
