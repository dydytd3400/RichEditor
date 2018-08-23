package com.echoleaf.richeditor.htmltext.render;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.echoleaf.richeditor.htmltext.Constants;
import com.echoleaf.richeditor.htmltext.Util;
import com.echoleaf.richeditor.htmltext.events.AREMovementMethod;
import com.echoleaf.richeditor.htmltext.inner.Html;

import java.util.HashMap;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/5/20
 * @discription null
 * @usage null
 */
public class AreTextView extends AppCompatTextView {

    private static HashMap<String, Spanned> spannedHashMap = new HashMap<>();


    Context mContext;

    public AreTextView(Context context) {
        this(context, null);
    }

    public AreTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AreTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.DEFAULT_FONT_SIZE);
        initGlobalValues();
        initMovementMethod();
    }

    private void initGlobalValues() {
        int[] wh = Util.getScreenWidthAndHeight(mContext);
        Constants.SCREEN_WIDTH = wh[0];
        Constants.SCREEN_HEIGHT = wh[1];
    }

    private void initMovementMethod() {
        this.setMovementMethod(new AREMovementMethod(null));
    }

    public void fromHtml(String html) {
        Spanned spanned = getSpanned(html);
        setText(spanned);
    }

    private Spanned getSpanned(String html) {
        Html.sContext = mContext;
        Html.TagHandler tagHandler = new AreTagHandler();
        return Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, null, tagHandler);
    }

    /**
     * Use cache will take more RAM, you need to call clear cache when you think it is safe to do that.
     * You may need cache when working with {@link android.widget.ListView} or RecyclerView
     *
     * @param html
     */
    public void fromHtmlWithCache(String html) {
        Spanned spanned = null;
        if (spannedHashMap.containsKey(html)) {
            spanned = spannedHashMap.get(html);
        }
        if (spanned == null) {
            spanned = getSpanned(html);
            spannedHashMap.put(html, spanned);
        }
        if (spanned != null) {
            setText(spanned);
        }
    }

    public static void clearCache() {
        spannedHashMap.clear();
    }

}
