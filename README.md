# RichEditor
rich text editor for android

Simple example:
`layout.xml`
```
 <com.echoleaf.richeditor.RichEditor
      android:id="@+id/editor"
      android:layout_width="0dp"
      android:layout_height="0dp"
      app:layout_constraintBottom_toBottomOf="parent"
      app:layout_constraintEnd_toEndOf="parent"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintTop_toBottomOf="@+id/line" />
```

`Activity.class`
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mEditor = findViewById(R.id.editor);
    mEditor.config().focusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            findViewById(R.id.bold_btn).setBackgroundColor(mEditor.isBold() ? Color.RED : Color.GREEN);
            findViewById(R.id.italic_btn).setBackgroundColor(mEditor.isItalic() ? Color.RED : Color.GREEN);
            findViewById(R.id.underline_btn).setBackgroundColor(mEditor.isUnderline() ? Color.RED : Color.GREEN);
            findViewById(R.id.strike_btn).setBackgroundColor(mEditor.isStrike() ? Color.RED : Color.GREEN);
        }
    }).contentChangeListener(new OnContentChangeListener() {
        @Override
        public void onContentChanged(RichView view, boolean removed) {
            mHtmlText.setText(mEditor.toHtml());
        }
    }).pieceChangeListener(new SimplePieceChangeListener()).defaultSize(12).defaultColor(Color.BLUE).build();
  }

@Override
public void onClick(View v) {
    switch (v.getId()) {
        case R.id.bold_btn:
            mEditor.bold();
            break;
        case R.id.italic_btn:
            mEditor.italic();
            break;
        case R.id.underline_btn:
            mEditor.underline();
            break;
        case R.id.strike_btn:
            mEditor.strike();
            break;
        case R.id.img_btn:
            TextView container = new TextView(this);
            container.setText(" this is image index=" + indexImage++);
            container.setBackgroundColor(Color.GREEN);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80);
            container.setLayoutParams(layoutParams);
            mEditor.insert(container);
            break;
        case R.id.size_btn:
            mEditor.setTextSize(15);
            break;
        case R.id.color_btn:
            mEditor.setTextColor(Color.RED);
            break;
    }
```
