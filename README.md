# RichEditor
RichEditor是Android平台下基于Span和NestedScrollView实现的Html富文本编辑器，支持高度自定义View的布局，并且可通过对内部定义的接口和抽象来进行自定义View到html的转换并交由RichEditor进行统一管理。

### 使用：
### gradle
在gradle中增加如下配置
```
dependencies {
 implementation'com.github.dydytd3400:RichEditor:1.0.1'
}
```

### 布局
在layout布局文件中加入如下代码
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

### 初始化
RichEditor初始化配置，均为可选。
```
RichEditor mEditor = findViewById(R.id.editor);

mEditor.config()
        .focusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ...
            }
        })
        .contentChangeListener(new OnContentChangeListener() {
            @Override
            public void onContentChanged(RichView view, boolean removed) {
                ...
            }
        })
        .pieceChangeListener(new SimplePieceChangeListener())
        .selectionActionMenu(new SelectionActionMenuCreator() {
            @Override
            public ActionMode.Callback create(Context context, EditText editText) {
                return new SimpleSelectionActionMenu(context, editText);
            }

            @Override
            protected ActionMode.Callback2 create2(Context context, EditText editText) {
                return new SimpleSelectionActionMenu(context, editText);
            }
        })
        .defaultSize(12)//默认字体大小，此处所定义的值不会被随之转换为html中的样式
        .defaultColor(Color.BLUE)//默认字体颜色，此处所定义的值不会被随之转换为html中的样式
        .build();
```
或者也可全部直接使用默认配置
```
mEditor.config().build();
```

### 简单应用
较为基础的几个样式设置方法
```
mEditor.bold();//将所选文本设置为粗体，若已经是粗体，则取消粗体效果
mEditor.italic();//将所选文本设置为斜体，若已经是斜体，则取消斜体效果
mEditor.underline();//将所选文本添加下划线，若已经有下划线，则取消下划线效果
mEditor.strike();//将所选文本添加删除线，若已经有删除线，则取消删除线效果
mEditor.setTextSize(15);//将所选文本的字体大小设置为15sp
mEditor.setTextColor(Color.RED);//将所选文本的字体颜色设置为红色

mEditor.isBold();//返回光标所选区域是否为粗体样式
...
mEditor.isStrike();//返回光标所选区域是否有删除线
}
```
### 高级应用
插入一段文本
```
mEditor.insert("RichEditor Simple Example");
//或者
SimpleRichText richText = new SimpleRichText(this);
LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
richText.setLayoutParams(layoutParams);
mEditor.insert(richText);
```
插入一个图片View
```
ImageView image = new ImageView(this);
LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
image.setLayoutParams(layoutParams);
image.setImageResource(R.mipmap.simple_image);
mEditor.insert(image);
```
可以通过上述系列方法添加任意View，如果View实现了RichView或者RichTextView接口，那么RichEditor则会将该View转换为特定Html或普通text文本，否则进做内容展示。
