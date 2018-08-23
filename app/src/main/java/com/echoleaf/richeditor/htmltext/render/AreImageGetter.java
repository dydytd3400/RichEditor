package com.echoleaf.richeditor.htmltext.render;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.echoleaf.richeditor.htmltext.Constants;
import com.echoleaf.richeditor.htmltext.inner.Html;


public class AreImageGetter implements Html.ImageGetter {

    private Context mContext;


    public AreImageGetter(Context context) {
        mContext = context;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (source.startsWith(Constants.EMOJI)) {
            String resIdStr = source.substring(6);
            int resId = Integer.parseInt(resIdStr);
            Drawable d = mContext.getResources().getDrawable(resId);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            return d;
        } else if (source.startsWith("http")) {
            AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);

            return areUrlDrawable;
        } else if (source.startsWith("content")) {
            AreUrlDrawable areUrlDrawable = new AreUrlDrawable(mContext);
            try {
                return areUrlDrawable;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        return null;
    }

}

