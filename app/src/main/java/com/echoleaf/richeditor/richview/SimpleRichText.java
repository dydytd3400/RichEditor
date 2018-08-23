package com.echoleaf.richeditor.richview;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import com.echoleaf.richeditor.Style;
import com.echoleaf.richeditor.listener.OnContentChangeListener;
import com.echoleaf.richeditor.listener.OnSelectionChangeListener;

import java.util.List;

/**
 * @author echoleaf
 */
public class SimpleRichText extends AppCompatEditText implements RichText {

    private boolean readOnly;
    private OnSelectionChangeListener onSelectionChangeListener;
    private OnContentChangeListener onContentChangeListener;
    private int prePos = 0;
    private int curPos = 0;

    public SimpleRichText(Context context) {
        super(context);
        init();
    }

    public SimpleRichText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleRichText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                onContentChanged();
            }
        });
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        curPos = selStart;
        if (onSelectionChangeListener != null)
            onSelectionChangeListener.onSelectionChange(this, prePos, curPos);
        prePos = curPos;
    }

    public void setStyle(@Style.StyleDef int style) {
        int star = getSelectionStart();
        int end = getSelectionEnd();
        if (star < 0 || end > length())
            return;
        if (star == end) {
            if (star > 0) {
                List spans = Style.findStyle(getText(), star - 1, end, style);
                if (spans != null) {
                    removeStyle(spans);
                    return;
                }
            } else if (end < length()) {
                List spans = Style.findStyle(getText(), star, end + 1, style);
                if (spans != null) {
                    removeStyle(spans);
                    return;
                }
            }
            setStyle(star, end, style);
        } else {
            List spans = Style.findStyle(getText(), star, end, style);
            if (spans == null || spans.size() == 0)
                setStyle(star, end, style);
            else
                removeStyle(spans);
        }
    }

    public boolean hasStyle(@Style.StyleDef int style) {
        return Style.hasStyle(this, style);
    }

    public boolean hasStyle(int star, int end, @Style.StyleDef int style) {
        return Style.hasStyle(getText(), star, end, style);
    }

    private void removeStyle(List spans) {
        if (spans != null && spans.size() > 0)
            for (Object span : spans)
                removeStyle(span);
    }

    private void removeStyle(Object span) {
        if (span != null) {
            getText().removeSpan(span);
            onContentChanged();
        }
    }


    @Override
    public void setTextSize(float size, int star, int end) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size, star, end);
    }


    @Override
    public void setTextSize(int unit, float size, int star, int end) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        int s = (int) TypedValue.applyDimension(unit, size, r.getDisplayMetrics());//此处已经完成了各种单位的size向px的转换
        setSpan(star, end, new AbsoluteSizeSpan(s, false));
    }

    @Override
    public void setTextColor(int color, int star, int end) {
        setSpan(star, end, new ForegroundColorSpan(color));
    }


    private void setStyle(int star, int end, @Style.StyleDef int style) {
        setSpan(star, end, Style.getSpan(style));
    }

    private void setSpan(int star, int end, Object span) {
        if (span == null)
            return;
        Style.setSpan(getText(), star, end, span);
        onContentChanged();
    }

    private void onContentChanged() {
        if (onContentChangeListener != null)
            onContentChangeListener.onContentChanged(this, false);
    }

    @Override
    public String toText() {
        return getText().toString();
    }

    @Override
    public String toHtml() {
        return Html.toHtml(new SpannableString(getText()));
    }

    @Override
    public void fromText(String text) {
        setText(text);
    }

    @Override
    public void fromHtml(String html) {
        setText(Html.fromHtml(html));
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        setFocusableInTouchMode(!readOnly);
        setFocusable(!readOnly);
        setClickable(!readOnly);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    @Override
    public void setOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        this.onContentChangeListener = onContentChangeListener;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new DeleteInputConnection(super.onCreateInputConnection(outAttrs),
                true);
    }

    private class DeleteInputConnection extends InputConnectionWrapper {

        public DeleteInputConnection(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_DEL));
            }

            return super.deleteSurroundingText(beforeLength, afterLength);
        }

    }
}