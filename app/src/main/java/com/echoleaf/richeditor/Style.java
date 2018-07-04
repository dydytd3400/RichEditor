package com.echoleaf.richeditor;

import android.support.annotation.IntDef;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class Style {
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int BOLD_ITALIC = 3;
    public static final int UNDERLINE = 4;
    public static final int STRIKE = 5;

    @IntDef({NORMAL, BOLD, ITALIC, BOLD_ITALIC, UNDERLINE, STRIKE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StyleDef {
    }


    public static List<Object> findStyle(CharSequence charSequence, int star, int end, @StyleDef int style) {
        if (charSequence == null || !(charSequence instanceof Spanned))
            return null;
        Spanned spanned = (Spanned) charSequence;
        if (star < 0 || end > spanned.length())
            return null;
        Class<?> spanType = null;
        switch (style) {
            case BOLD:
            case ITALIC:
            case BOLD_ITALIC:
            case NORMAL:
                spanType = StyleSpan.class;
                break;
            case UNDERLINE:
                spanType = UnderlineSpan.class;
                break;
            case STRIKE:
                spanType = StrikethroughSpan.class;
                break;
        }
        Object[] spans = spanned.getSpans(star, end, spanType);
        if (spans != null && spans.length > 0) {
            List<Object> list = new ArrayList<>();
            for (Object span : spans)
                if (span != null) {
                    boolean isStyleSpan = span instanceof StyleSpan && ((StyleSpan) span).getStyle() == style;
                    boolean isUnderlineSpan = span instanceof UnderlineSpan && UNDERLINE == style;
                    boolean isStrikethroughSpan = span instanceof StrikethroughSpan && STRIKE == style;
                    if (isStyleSpan || isUnderlineSpan || isStrikethroughSpan) {
                        list.add(span);
                    }
                }
            return list;
        }
        return null;
    }


    public static boolean hasStyle(TextView textView, @StyleDef int style) {
        int star = textView.getSelectionStart();
        int end = textView.getSelectionEnd();
        if (star < 0 || end > textView.length())
            return false;
        CharSequence text = textView.getText();
        if (star == end) {
            if (star > 0) {
                if (hasStyle(text, star - 1, end, style))
                    return true;
            } else if (end < textView.length()) {
                if (hasStyle(text, star, end + 1, style))
                    return true;
            }
        } else return hasStyle(text, star, end, style);
        return false;
    }

    public static boolean hasStyle(CharSequence charSequence, int star, int end, @StyleDef int style) {
        List spans = findStyle(charSequence, star, end, style);
        return spans != null && spans.size() > 0;
    }

    public static Object getSpan(@StyleDef int style) {
        Object span = null;
        switch (style) {
            case Style.BOLD:
            case Style.ITALIC:
            case Style.BOLD_ITALIC:
            case Style.NORMAL:
                span = new StyleSpan(style);
                break;
            case Style.UNDERLINE:
                span = new UnderlineSpan();
                break;
            case Style.STRIKE:
                span = new StrikethroughSpan();
                break;
        }
        return span;
    }

    public static void setStyle(CharSequence charSequence, @StyleDef int style) {
        setSpan(charSequence, getSpan(style));
    }

    public static void setStyle(CharSequence charSequence, int star, int end, @StyleDef int style) {
        setSpan(charSequence, star, end, getSpan(style));
    }

    public static void setSpan(CharSequence charSequence, Object span) {
        setSpan(charSequence, 0, charSequence.length(), span);
    }

    public static void setSpan(CharSequence charSequence, int star, int end, Object span) {
        if (span == null)
            return;
        Spannable spannable;
        if (charSequence instanceof Spannable)
            spannable = (Spannable) charSequence;
        else {
            spannable = new SpannableStringBuilder(charSequence);
        }
        spannable.setSpan(span, star, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

}
