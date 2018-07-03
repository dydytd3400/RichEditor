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
    //RichEditor初始化配置，均为可选。
    //也可全部直接使用默认配置：mEditor.config().build();
    mEditor.config().focusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            //返回选定区域是否有指定样式
            findViewById(R.id.bold_btn).setBackgroundColor(mEditor.isBold() ? Color.RED : Color.GREEN);
            findViewById(R.id.italic_btn).setBackgroundColor(mEditor.isItalic() ? Color.RED : Color.GREEN);
            findViewById(R.id.underline_btn).setBackgroundColor(mEditor.isUnderline() ? Color.RED : Color.GREEN);
            findViewById(R.id.strike_btn).setBackgroundColor(mEditor.isStrike() ? Color.RED : Color.GREEN);
        }
    }).contentChangeListener(new OnContentChangeListener() {
        @Override
        public void onContentChanged(RichView view, boolean removed) {
            mHtmlText.setText(mEditor.toHtml());//展示最终转换的html文本
        }
    }).pieceChangeListener(new SimplePieceChangeListener()).defaultSize(12).defaultColor(Color.BLUE).build();
  }

@Override
public void onClick(View v) {
    switch (v.getId()) {
        case R.id.bold_btn:
            mEditor.bold();//将选定区域设置为粗体，如果选定区域已经是粗体了，则取消粗体效果
            break;
        case R.id.italic_btn:
            mEditor.italic();//同上
            break;
        case R.id.underline_btn:
            mEditor.underline();
            break;
        case R.id.strike_btn:
            mEditor.strike();
            break;
        case R.id.img_btn:
            //此处为示例，可以通过类似方法添加任意View
            ImageView image = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(layoutParams);
            image.setImageResource(R.mipmap.ic_launcher);
            mEditor.insert(image);
            //此处View可通过实现RichView或者RichTextView接口来达到将View转换为特定Html或普通text文本的目的
            break;
        case R.id.size_btn:
            mEditor.setTextSize(15);
            break;
        case R.id.color_btn:
            mEditor.setTextColor(Color.RED);
            break;
    }
```
