package com.echoleaf.richeditor.listener;


import com.echoleaf.richeditor.richview.RichView;

public interface OnContentChangeListener {

    void onContentChanged(RichView view, boolean removed);
}
