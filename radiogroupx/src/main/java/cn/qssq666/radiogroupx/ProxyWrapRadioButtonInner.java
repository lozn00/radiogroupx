package cn.qssq666.radiogroupx;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by qssq on 2018/3/22 qssq666@foxmail.com
 */

public class ProxyWrapRadioButtonInner extends RelativeLayout implements Checkable, RadioGroupX.OnCheckedChangeWidgetListener, CompoundButton.OnCheckedChangeListener {
    public ProxyWrapRadioButtonInner(Context context) {
        super(context);
    }

    public ProxyWrapRadioButtonInner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProxyWrapRadioButtonInner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Checkable getInnerCheckAble() {
        return innerCheckAble;
    }

    /**
     * 必须调用
     *
     * @param innerCheckAble
     */
    public void setInnerCheckAble(Checkable innerCheckAble) {
        this.innerCheckAble = innerCheckAble;
    }

    Checkable innerCheckAble;

    public ImageView getInnerImageView() {
        return innerImageView;
    }

    public void setInnerImageView(RadioCheckableImageView innerImageView) {
        this.innerImageView = innerImageView;
    }

    RadioCheckableImageView innerImageView;

    @Override
    public void setChecked(boolean checked) {
        if (innerCheckAble != null) {
            innerCheckAble.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        if (innerCheckAble != null) {
            return innerCheckAble.isChecked();
        }
        return false;
    }

    @Override
    public void toggle() {
        if (innerCheckAble != null) {
            innerCheckAble.toggle();
        } else {

        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    @Override
    public void setFocusable(int focusable) {
        super.setFocusable(focusable);
    }

    @Override
    public void setOnCheckedChangeWidgetListener(final CompoundButton.OnCheckedChangeListener onCheckedChangeWidgetListener) {
        if (innerCheckAble != null && innerCheckAble instanceof View) {
            RadioGroupX.setRadioButtonOnCheckedChangeWidgetListener((View) innerCheckAble, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //这里如何实现CompoundButton
                    onCheckedChangeWidgetListener.onCheckedChanged(buttonView, isChecked);
                    innerImageView.setChecked(isChecked);
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (innerCheckAble instanceof CompoundButton.OnCheckedChangeListener) {
            ((CompoundButton.OnCheckedChangeListener) innerCheckAble).onCheckedChanged(buttonView, isChecked);
            innerImageView.setChecked(isChecked);

        }
    }
}
