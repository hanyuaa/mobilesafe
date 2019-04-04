package com.itheima.mobilesafe.view;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * 能过获取焦点的自定义TextView
 */
public class FocusTextView extends AppCompatTextView {

    //使用通过java代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }

    //由系统调用,带属性+上下文环境的构造方法  xml->java代码
    public FocusTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //带属性+上下文环境+在布局文件中定义样式的构造方法
    public FocusTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写获取焦点的方法, 在调用的时候默认就能获取焦点
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
