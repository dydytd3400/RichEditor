package com.echoleaf.richeditor;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.echoleaf.richeditor.listener.OnContentChangeListener;
import com.echoleaf.richeditor.listener.OnPieceChangeListener;
import com.echoleaf.richeditor.listener.OnSelectionChangeListener;
import com.echoleaf.richeditor.menu.SelectionActionMenuCreator;
import com.echoleaf.richeditor.piece.RichPiece;
import com.echoleaf.richeditor.piece.RichPieceAdapter;
import com.echoleaf.richeditor.richview.RichText;
import com.echoleaf.richeditor.richview.RichView;
import com.echoleaf.richeditor.richview.SimpleRichText;


/**
 * @author echoleaf
 */
public class RichEditor extends NestedScrollView implements RichView {

    private LinearLayout container;
    private View.OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private View.OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    private EditText lastFocusEdit; // 最近被聚焦的EditText
    private LayoutTransition transition; // 只在View添加或remove时，触发transition动画
    private View.OnFocusChangeListener onFocusChangeListener;
    private OnContentChangeListener onContentChangeListener;
    private OnPieceChangeListener onPieceChangeListener;
    private SelectionActionMenuCreator selectionActionMenuCreator;
    private OnScrollListener onScrollListener;

    private BaseEditTextCreator baseEditTextCreator;
    private float defaultSize = 12;
    private int defaultColor = Color.BLACK;

    public RichEditor(@NonNull Context context) {
        this(context, null);
    }

    public RichEditor(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichEditor(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        addView(container, layoutParams);

        keyListener = new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText editTxt = (EditText) v;
                    if (editTxt.getSelectionStart() == 0) {
                        int curIndex = indexOfPiece(editTxt);
                        if (editTxt.length() == 0)
                            remove((RichPiece) editTxt.getTag());
                        else
                            remove(curIndex - 1);
                    }
                }
                return false;
            }
        };

        focusListener = new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && v instanceof EditText) {
                    lastFocusEdit = (EditText) v;
                }
                if (onFocusChangeListener != null)
                    onFocusChangeListener.onFocusChange(v, hasFocus);
            }
        };
    }

    private void init() {
        if (getPieceCount() == 0 || !(getPieceAt(getPieceCount() - 1) instanceof EditText)) {
            insert("");
        } else {
            findLastEdit(getPieceCount());
        }
    }

    public RichPiece<EditText> createEditPiece() {
        return createEditPiece(null);
    }

    public RichPiece<EditText> createEditPiece(CharSequence text) {
        EditText editText = baseEditTextCreator.create(getContext());
        editText.setOnKeyListener(keyListener);
        if (selectionActionMenuCreator != null) {
            editText.setCustomSelectionActionModeCallback(selectionActionMenuCreator.getSelectionActionMenu(getContext(), editText));
        }
        if (text != null)
            editText.setText(text);
        editText.setTextSize(defaultSize);
        editText.setTextColor(defaultColor);
        baseEditTextCreator.someStyle(editText);
        return new RichPieceAdapter<>(editText);
    }

    public void insert(CharSequence text) {
        EditText insertView;
        if (lastFocusEdit != null) {
            insertView = lastFocusEdit;
        } else if (getPieceCount() > 0 && getPieceAt(getPieceCount() - 1) instanceof EditText) {
            insertView = (EditText) getPieceAt(getPieceCount() - 1);
        } else {
            RichPiece<EditText> editTextPiece = createEditPiece(text);
            append(editTextPiece);
            changeFocus(editTextPiece.getView());
            return;
        }
        insertView.getText().insert(insertView.getSelectionEnd(), text);
        insertView.setSelection(insertView.length(), insertView.length());
    }

    public void insert(@NonNull View view) {
        insert(new RichPieceAdapter(view));
    }

    public void insert(@NonNull RichPiece richPiece) {
        View view = richPiece.getView();
        if (view instanceof EditText) {
            if (getPieceCount() > 0 && getPieceAt(getPieceCount() - 1) instanceof EditText)
                insert(((EditText) view).getText());
            else {
                append(richPiece);
                changeFocus((EditText) view);
            }
            return;
        }
        EditText focusEdit = null;
        if (lastFocusEdit != null) {
            focusEdit = lastFocusEdit;
        } else if (getPieceCount() > 0 && getPieceAt(getPieceCount() - 1) instanceof EditText) {
            focusEdit = (EditText) getPieceAt(getPieceCount() - 1);
        }
        if (focusEdit != null) {
            int selectionEnd = focusEdit.getSelectionEnd();
            int focusEditIndex = indexOfPiece(focusEdit);
            boolean hasNext = selectionEnd < focusEdit.length();
            insert(richPiece, focusEditIndex + 1, !hasNext);
            if (hasNext) {
                CharSequence charSequence = focusEdit.getText().subSequence(selectionEnd, focusEdit.length());
                focusEdit.getText().replace(selectionEnd, focusEdit.length(), "");
                insert(createEditPiece(charSequence), focusEditIndex + 2);
            }
        } else append(richPiece);
    }


    private void append(@NonNull RichPiece richPiece) {
        insert(richPiece, getPieceCount());
    }

    private void insert(@NonNull RichPiece richPiece, int index) {
        insert(richPiece, index, true);
    }

    private void insert(@NonNull RichPiece richPiece, int index, boolean finish) {
        int size = getPieceCount();
        if (index > size)
            return;
        View view = richPiece.getView();
        view.setOnFocusChangeListener(focusListener);
        if (index < container.getChildCount())
            container.addView(view, index);
        else
            container.addView(view);
        if (onPieceChangeListener != null) {
            onPieceChangeListener.onAdd(this, richPiece);
            if (finish)
                onPieceChangeListener.inTransactionChanged(this, richPiece);
        }
        if (view instanceof RichView) {
            ((RichView) view).setOnContentChangeListener(onContentChangeListener);
            if (view instanceof RichText)
                ((RichText) view).setOnSelectionChangeListener(new OnSelectionChangeListener() {
                    @Override
                    public void onSelectionChange(View v, int prePos, int curPos) {
                        if (onFocusChangeListener != null)
                            onFocusChangeListener.onFocusChange(v, true);
                    }
                });
            if (onContentChangeListener != null)
                onContentChangeListener.onContentChanged((RichView) view, false);
        }

    }

    public void remove(int index) {
        if (index >= 0 && index < getPieceCount()) {
            remove(getPieceAt(index));
        }
    }

    public void remove(@NonNull View view) {
        if (view != null)
            remove((RichPiece) view.getTag());
    }


    public void remove(@NonNull RichPiece curPiece) {
        if (curPiece == null)
            return;
        int curIndex = indexOfPiece(curPiece);
        View curView = curPiece.getView();
        if (!(curView instanceof EditText)) {
            if (curIndex > 0) {
                View preView = getPieceAt(curIndex - 1);
                if (preView instanceof EditText) {
                    EditText preEdit = (EditText) preView;
                    String preText = preEdit.getText().toString();
                    int nexIndex = curIndex + 1;
                    if (nexIndex < getPieceCount()) {
                        View nexView = getPieceAt(nexIndex);
                        if (nexView instanceof EditText) {
                            String nexText = ((EditText) nexView).getText().toString();
                            preEdit.setText(preText + nexText);
                            remove((RichPiece) nexView.getTag(), false, false);
                        }
                    }
                    remove(curPiece, true, true);
                    changeFocus(preEdit, preText.length());
                    return;
                }
            }
        } else if (lastFocusEdit == curView || lastFocusEdit == null) {
            findLastEdit(curIndex);
        }
        remove(curPiece, true, true);

    }

    private void remove(@NonNull RichPiece richPiece, boolean play, boolean finish) {
        View view = richPiece.getView();
        if (!play) {
            container.setLayoutTransition(null);
        } else if (transition != null && !transition.isRunning()) {
            container.setLayoutTransition(transition);
        }
        container.removeView(view);
        if (onPieceChangeListener != null) {
            onPieceChangeListener.onRemove(this, richPiece);
            if (finish)
                onPieceChangeListener.inTransactionChanged(this, getPieceCount() > 0 ? (RichPiece) getPieceAt(getPieceCount() - 1).getTag() : null);
        }
        if (onContentChangeListener != null && view instanceof RichView)
            onContentChangeListener.onContentChanged((RichView) view, true);
        richPiece.recyle();
    }

    public void clear() {
        container.removeAllViews();
        onContentChangeListener.onContentChanged(this, true);
        lastFocusEdit = null;
    }

    public int indexOfPiece(@NonNull View piece) {
        if (piece.getTag() != null && piece.getTag() instanceof RichPiece)
            return indexOfPiece((RichPiece) piece.getTag());
        return -1;
    }

    public int indexOfPiece(@NonNull RichPiece piece) {
        return container.indexOfChild(piece.getView());
    }

    public int getPieceCount() {
        return container.getChildCount();
    }

    public View getPieceAt(int index) {
        return index < 0 || index >= container.getChildCount() ? null : container.getChildAt(index);
    }


    private void findLastEdit(int curIndex) {
        int i = curIndex - 1;
        for (; i >= 0; i--) {
            if (setLastFocusEdit(i))
                break;
        }
        if (i < 0) {
            i = curIndex + 1;
            for (; i < getPieceCount(); i++) {
                if (setLastFocusEdit(i))
                    break;
            }
        }
        if (i >= getPieceCount())
            lastFocusEdit = null;
    }

    private boolean setLastFocusEdit(int i) {
        View view = getPieceAt(i);
        if (view != null && view instanceof EditText) {
            changeFocus((EditText) view);
            return true;
        }
        return false;
    }

    private void changeFocus(@NonNull EditText view) {
        changeFocus(view, view.length());
    }

    private void changeFocus(@NonNull EditText view, int selection) {
        lastFocusEdit = view;
        if (lastFocusEdit != null) {
            lastFocusEdit.requestFocus();
            lastFocusEdit.setSelection(selection, selection);
        }
    }

    public void bold() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            ((RichText) lastFocusEdit).setStyle(Style.BOLD);
    }

    public void italic() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            ((RichText) lastFocusEdit).setStyle(Style.ITALIC);
    }

    public void underline() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            ((RichText) lastFocusEdit).setStyle(Style.UNDERLINE);
    }

    public void strike() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            ((RichText) lastFocusEdit).setStyle(Style.STRIKE);
    }

    public boolean isBold() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            return ((RichText) lastFocusEdit).hasStyle(Style.BOLD);
        return false;
    }

    public boolean isItalic() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            return ((RichText) lastFocusEdit).hasStyle(Style.ITALIC);
        return false;
    }

    public boolean isUnderline() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            return ((RichText) lastFocusEdit).hasStyle(Style.UNDERLINE);
        return false;
    }

    public boolean isStrike() {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            return ((RichText) lastFocusEdit).hasStyle(Style.STRIKE);
        return false;
    }

    @Override
    public String toText() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getPieceCount(); i++) {
            View piece = getPieceAt(i);
            if (piece != null && piece instanceof RichView) {
                sb.append(((RichView) piece).toText());
            }
        }
        return sb.toString();
    }

    @Override
    public String toHtml() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < getPieceCount(); i++) {
            View piece = getPieceAt(i);
            if (piece != null && piece instanceof RichView) {
                sb.append(((RichView) piece).toHtml());
            }
        }
        return sb.toString();
    }


    @Override
    public void fromText(String text) {
        clear();
        insert(text);
    }

    //TODO
    @Override
    public void fromHtml(String html) {
        for (int i = 0; i < getPieceCount(); i++) {
            View piece = getPieceAt(i);
            if (piece != null && piece instanceof RichView) {
                ((RichView) piece).fromHtml(html);
            }
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (int i = 0; i < getPieceCount(); i++) {
            View piece = getPieceAt(i);
            if (piece != null && piece instanceof RichView) {
                ((RichView) piece).setReadOnly(readOnly);
            }
        }
    }

    @Override
    public boolean isReadOnly() {
        for (int i = 0; i < getPieceCount(); i++) {
            View piece = getPieceAt(i);
            if (piece != null && piece instanceof RichView) {
                if (((RichView) piece).isReadOnly())
                    return true;
            }
        }
        return false;
    }

    @Override
    public void setOnFocusChangeListener(View.OnFocusChangeListener l) {
        onFocusChangeListener = l;
    }


    @Override
    public void setOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        this.onContentChangeListener = onContentChangeListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }


    public void setTextSize(float size) {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            ((RichText) lastFocusEdit).setTextSize(size, lastFocusEdit.getSelectionStart(), lastFocusEdit.getSelectionEnd());
    }

    public void setTextColor(int color) {
        if (lastFocusEdit != null && lastFocusEdit instanceof RichText)
            ((RichText) lastFocusEdit).setTextColor(color, lastFocusEdit.getSelectionStart(), lastFocusEdit.getSelectionEnd());
    }

    public void setBaseEditTextCreator(BaseEditTextCreator baseEditTextCreator) {
        this.baseEditTextCreator = baseEditTextCreator;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScrollListener != null) {
            onScrollListener.onScroll(this, x, y, oldx, oldy);
        }
    }

    private Config config;

    public static class Config {
        RichEditor mRichEditor;
        private String text;
        private String html;
        private LayoutTransition transition; // 只在View添加或remove时，触发transition动画
        private View.OnFocusChangeListener onFocusChangeListener;
        private OnContentChangeListener onContentChangeListener;
        private BaseEditTextCreator baseEditTextCreator;
        private OnPieceChangeListener pieceChangeListener;
        private SelectionActionMenuCreator selectionActionMenuCreator;
        private OnScrollListener scrollListener;

        private Config(RichEditor richEditor) {
            this.mRichEditor = richEditor;
            this.transition = new LayoutTransition();
            this.transition.setDuration(300);
        }

        public Config baseEditCreator(BaseEditTextCreator baseEditTextCreator) {
            this.baseEditTextCreator = baseEditTextCreator;
            return this;
        }

        public Config focusChangeListener(View.OnFocusChangeListener focusChangeListener) {
            this.onFocusChangeListener = focusChangeListener;
            return this;
        }

        public Config contentChangeListener(OnContentChangeListener contentChangeListener) {
            this.onContentChangeListener = contentChangeListener;
            return this;
        }

        public Config pieceChangeListener(OnPieceChangeListener pieceChangeListener) {
            this.pieceChangeListener = pieceChangeListener;
            return this;
        }

        public Config selectionActionMenu(SelectionActionMenuCreator selectionActionMenuCreator) {
            this.selectionActionMenuCreator = selectionActionMenuCreator;
            return this;
        }


        public Config scrollListener(OnScrollListener scrollListener) {
            this.scrollListener = scrollListener;
            return this;
        }

        public Config transition(LayoutTransition transition) {
            this.transition = transition;
            return this;
        }

        public Config fromText(String text) {
            this.text = text;
            this.html = null;
            return this;
        }

        public Config fromHtml(String html) {
            this.html = html;
            this.text = null;
            return this;
        }

        /**
         * 该textsize仅用于默认显示效果，不会转换到html中
         *
         * @param textSize
         * @return
         */
        public Config defaultSize(float textSize) {
            mRichEditor.defaultSize = textSize;
            return this;
        }

        /**
         * 该textColor仅用于默认显示效果，不会转换到html中
         *
         * @param textColor
         * @return
         */
        public Config defaultColor(int textColor) {
            mRichEditor.defaultColor = textColor;
            return this;
        }

        public void build() {
            if (baseEditTextCreator == null) {
                final int lr = dip2px(mRichEditor.getContext(), 10);
                final int tb = dip2px(mRichEditor.getContext(), 8);
                baseEditTextCreator = new BaseEditTextCreator() {
                    @Override
                    public EditText create(Context context) {
                        EditText editText = new SimpleRichText(context);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                        editText.setLayoutParams(params);
                        editText.setPadding(lr, tb, lr, tb);
                        editText.setBackgroundResource(0);
                        return editText;
                    }
                };
            }
            if (!TextUtils.isEmpty(text)) {
                mRichEditor.fromText(text);
            }
            if (!TextUtils.isEmpty(html)) {
                mRichEditor.fromHtml(html);
            }
            mRichEditor.transition = transition;
            mRichEditor.container.setLayoutTransition(transition);
            mRichEditor.setBaseEditTextCreator(baseEditTextCreator);
            mRichEditor.setOnFocusChangeListener(onFocusChangeListener);
            mRichEditor.setOnContentChangeListener(onContentChangeListener);
            mRichEditor.onPieceChangeListener = pieceChangeListener;
            mRichEditor.selectionActionMenuCreator = selectionActionMenuCreator;
            mRichEditor.setOnScrollListener(scrollListener);
            mRichEditor.init();
        }

        private static int dip2px(Context c, int i) {
            Resources r;
            if (c == null) {
                r = Resources.getSystem();
            } else {
                r = c.getResources();
            }
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics());
        }
    }


    /**
     * 获取RichEditor的配置
     * 推荐通过此方法对RichEditor进行初始配置而不建议手动设置。
     *
     * @return
     */
    public Config config() {
        if (config == null)
            config = new Config(this);
        return config;
    }

    /**
     * 清空RichEditor并获取RichEditor重置后的配置
     *
     * @return
     */
    public Config reset() {
        this.clear();
        config = new Config(this);
        return config;
    }

    public abstract static class BaseEditTextCreator {
        /**
         * 必须实现的抽象方法
         * 用于创建默认的EditTextView
         * 该抽象方法下所设置的EditText部分样式值可能会被覆盖，如果不想被覆盖，请重载下方 someStyle 方法来设置
         *
         * @param context
         * @return
         */
        abstract public EditText create(Context context);

        /**
         * 用于设置不被覆盖的EditText部分样式属性
         *
         * @param editText
         */
        public void someStyle(EditText editText) {
        }
    }

    public interface OnScrollListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param v          The view whose scroll position has changed.
         * @param scrollX    Current horizontal scroll origin.
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollX Previous horizontal scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScroll(RichEditor v, int scrollX, int scrollY, int oldScrollX, int oldScrollY);
    }
}
