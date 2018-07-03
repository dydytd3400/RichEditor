package com.echoleaf.richeditor.piece;

import android.text.Html;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.echoleaf.richeditor.listener.OnContentChangeListener;
import com.echoleaf.richeditor.richview.RichView;

/**
 * 富文本内容块
 * 富文本编辑器 @RichEditor 的内容由且仅由该对象填充
 *
 * @param <T> View 将要显示在@RicheEditor中的内容View。
 * @author echoleaf
 */
public class RichPieceAdapter<T extends View> implements RichPiece<T> {

    private T view;

    public RichPieceAdapter(T view) {
        this.view = view;
        view.setTag(this);
    }

    @Override
    public T getView() {
        return view;
    }

    @Override
    public void recyle() {
        if (view != null) {
            view.setTag(0);
            view = null;
        }
    }

    @Override
    public String toText() {
        if (view != null && view instanceof RichView)
            return ((RichView) view).toText();
        else if (view instanceof TextView)
            return ((TextView) view).getText().toString();
        return "";
    }

    @Override
    public String toHtml() {
        if (view != null && view instanceof RichView)
            return ((RichView) view).toHtml();
        else if (view instanceof TextView) {
            CharSequence content = ((TextView) view).getText();
            if (content != null) {
                SpannableString spanString = new SpannableString(content);
                return Html.toHtml(spanString);
            }
        }
        return "";
    }

    @Override
    public void fromText(String text) {
        if (view != null && view instanceof RichView)
            ((RichView) view).fromText(text);
    }

    @Override
    public void fromHtml(String html) {
        if (view != null && view instanceof RichView)
            ((RichView) view).fromHtml(html);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        if (view != null && view instanceof RichView)
            ((RichView) view).setReadOnly(readOnly);
    }

    @Override
    public boolean isReadOnly() {
        if (view != null && view instanceof RichView)
            ((RichView) view).isReadOnly();
        return false;
    }

    @Override
    public void setOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        if (view != null && view instanceof RichView)
            ((RichView) view).setOnContentChangeListener(onContentChangeListener);
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener onFocusChangeListener) {
        if (view != null)
            view.setOnFocusChangeListener(onFocusChangeListener);
    }
}
