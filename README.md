# RichEditor
rich text editor for android

### Use
##### gradle
> dependencies {
>     implementation'com.github.dydytd3400:RichEditor:1.0'
> }


Simple example:

`main_layout.xml`
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <android.support.constraint.ConstraintLayout
        android:id="@+id/relativeLayout"
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintBottom_toTopOf="@+id/guideline"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_weight="1">

        <LinearLayout
            android:id="@+id/button_bar"
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:gravity="center_vertical"
            android:orientation="horizontal"
            app:layout_constraintBottom_toTopOf="@+id/editor"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintHorizontal_weight="1"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintVertical_bias="1.0">

            <TextView
                android:id="@+id/bold_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="B" />

            <TextView
                android:id="@+id/italic_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="i" />

            <TextView
                android:id="@+id/underline_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="U" />

            <TextView
                android:id="@+id/strike_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="S" />

            <TextView
                android:id="@+id/img_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="img" />

            <TextView
                android:id="@+id/video_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="video" />

            <TextView
                android:id="@+id/audio_btn"
                android:layout_width="35dp"
                android:layout_height="35dp"
                android:layout_margin="4dp"
                android:background="#a6f6a6"
                android:gravity="center"
                android:text="audio" />
        </LinearLayout>

        <android.support.constraint.Guideline
            android:id="@+id/line"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            app:layout_constraintGuide_percent="0.2" />

        <com.echoleaf.richeditor.RichEditor
            android:id="@+id/editor"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toBottomOf="@+id/line" />
    </android.support.constraint.ConstraintLayout>


    <TextView
        android:id="@+id/html_text"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:background="#eeeeee"
        android:text="dfsdfsdfsdfsdf"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@+id/guideline"
        app:layout_constraintVertical_bias="1.0"
        app:layout_constraintVertical_weight="1" />

    <android.support.constraint.Guideline
        android:id="@+id/guideline"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_percent="0.5" />
</android.support.constraint.ConstraintLayout>
```

`MainActivity.class`
```
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    RichEditor mEditor;
    TextView mHtmlText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup buttonGroup = findViewById(R.id.button_bar);
        mHtmlText = findViewById(R.id.html_text);
        mEditor = findViewById(R.id.editor);
        int childCount = buttonGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            buttonGroup.getChildAt(i).setOnClickListener(this);
        }
        //RichEditor初始化配置，均为可选。
        //也可全部直接使用默认配置：mEditor.config().build();
        mEditor.config()
                .focusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        //返回选定区域是否有指定样式
                        findViewById(R.id.bold_btn).setBackgroundColor(mEditor.isBold() ? Color.RED : Color.GREEN);
                        findViewById(R.id.italic_btn).setBackgroundColor(mEditor.isItalic() ? Color.RED : Color.GREEN);
                        findViewById(R.id.underline_btn).setBackgroundColor(mEditor.isUnderline() ? Color.RED : Color.GREEN);
                        findViewById(R.id.strike_btn).setBackgroundColor(mEditor.isStrike() ? Color.RED : Color.GREEN);
                    }
                })
                .contentChangeListener(new OnContentChangeListener() {
                    @Override
                    public void onContentChanged(RichView view, boolean removed) {
                        mHtmlText.setText(mEditor.toHtml());//展示最终转换的html文本
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
                .defaultSize(12)
                .defaultColor(Color.BLUE)
                .build();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bold_btn:
                mEditor.bold();//将所选文本设置为粗体，若已经是粗体，则取消粗体效果
                break;
            case R.id.italic_btn:
                mEditor.italic();//将所选文本设置为斜体，若已经是斜体，则取消斜体效果
                break;
            case R.id.underline_btn:
                mEditor.underline();//将所选文本添加下划线，若已经有下划线，则取消下划线效果
                break;
            case R.id.strike_btn:
                mEditor.strike();//将所选文本添加删除线，若已经有删除线，则取消删除线效果
                break;
            case R.id.img_btn:
                //可以通过类似方法添加任意View，无论是否实现RichView或RichText接口均可添加。
                //如果该View实现了RichView或者RichText接口，那么RichEditor则会通过调用对应接口实现来组装为text文本或html文本
                ImageView image = new ImageView(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                image.setLayoutParams(layoutParams);
                image.setImageResource(R.mipmap.simple_image);
                mEditor.insert(image);
                break;
            case R.id.video_btn:
                mEditor.setTextSize(15);
                break;
            case R.id.audio_btn:
                mEditor.setTextColor(Color.RED);
                break;
        }
    }

}
```
