package cn.qssq666.radiogroupx;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by qssq on 2018/3/22 qssq666@foxmail.com
 */

public class MyDrawableTopRadioButton extends DrawableTopRadioButton {
    public MyDrawableTopRadioButton(Context context) {
        super(context);
    }

    public MyDrawableTopRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyDrawableTopRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onInitFinish(Context context, AttributeSet attrs) {
        setFocusable(true);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
