package cn.qssq666.radiogroupx;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by qssq on 2018/1/19 qssq666@foxmail.com
 * 分离drawableTop可以使用补间动画进行旋转图片，解决变态需求 让image分离可以实现单独image旋转
 * 警告自己别犯低级错误，drawableTop要用app,以及 app:text 郁闷死了，傻逼死了。搞了1上午才知道是自己给自己挖了一个坑，我说咋一直是白色呢。图片咋一直没有呢。
 */

public class DrawableTopRadioButton extends RelativeLayout implements Checkable, RadioGroupX.OnCheckedChangeWidgetListener {

    private static final String TAG = "BadgeRadioButton";

    public RadioButton getRadioButton() {
        return radioButton;
    }

    private RadioButton radioButton;
    private boolean isShown;
    private TextView badgePointView;
    private int minBadgeSize;
    private int badgeRadius;

    public ProxyWrapRadioButtonInner getRadioButtonWrap() {
        return radioButtonWrap;
    }




    private ProxyWrapRadioButtonInner radioButtonWrap;

    public RadioCheckableImageView getTopImageView() {
        return mTopImageView;
    }

    private RadioCheckableImageView mTopImageView;

    public DrawableTopRadioButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public DrawableTopRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }


    ColorStateList textColor = null;
    ColorStateList textColorHint = null;
    int textSize = 10;
    Drawable drawableLeft = null, drawableTop = null, drawableRight = null,
            drawableBottom = null, drawableStart = null, drawableEnd = null;
    int drawablePadding = 0;
    int ellipsize = -1;
    CharSequence text = "";
    CharSequence badgetext = "";
    int badgetextSize = 12;

    public boolean isNotNeedDight() {
        return notNeedDight;
    }




    public void setNotNeedDight(boolean notNeedDight) {
        this.notNeedDight = notNeedDight;
//        int i = dp2px(getContext(), 5);
        if (notNeedDight) {
//        this.badgePointView.setPadding(0, 0, 0, 0);

            setBadgePointPadding(0);
        } else {
            int padding = dp2px(getContext(), 1.5);
            setBadgePointPadding(padding);
        }
//        postInvalidate();
    }

    /**
     * 不需要数字表示只需要一个红点.这个时候padding需要修改一下
     */
    boolean notNeedDight;
    ColorStateList badgetextColor = null;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return radioButton.onTouchEvent(event);
//        return super.onTouchEvent(event);
    }

    private class MyRadioButtonProxyClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (radioButton != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    radioButton.callOnClick();//使用callOnClick是没什么效果的，setChecked才有用，否则不会触发。
                    radioButton.setChecked(true);
                } else {
                    radioButton.setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        final Resources.Theme theme = context.getTheme();

        /*
         * Look the appearance up without checking first if it exists because
         * almost every TextView has one and it greatly simplifies the logic
         * to be able to parse the appearance first and then let specific tags
         * for this View override it.
         */
        radioButton = new RadioButton(context, attrs, defStyleAttr);
        radioButton.setChecked(true);

        radioButton.setId(getId());//为了保证点击事件成功寻找 id,这里也必须这样。否则会出事
//        radioButton.setId(R.id.radio_button);

        //配置多少就有多少.的tyearray
        TypedArray typedArrayTextView = theme.obtainStyledAttributes(
                attrs, R.styleable.BadgeRadioButton, defStyleAttr, 0);

        int count = typedArrayTextView.getIndexCount();
        /*
                app:badgeRadius="8dp"
                    app:badgetextSize="5dp"
                    app:minBadgeSize="2dp"
         */
        notNeedDight = typedArrayTextView.getBoolean(R.styleable.BadgeRadioButton_onlypointer, false);  //设置描边颜色
        badgetextSize = typedArrayTextView.getDimensionPixelSize(R.styleable.BadgeRadioButton_badgetextSize, isNotNeedDight() ?
                dp2px(getContext(), 5) : dp2px(getContext(), 10));
        minBadgeSize = typedArrayTextView.getDimensionPixelSize(R.styleable.BadgeRadioButton_minBadgeSize, isNotNeedDight() ?
                dp2px(getContext(), 2) : dp2px(getContext(), 16));
        badgeRadius = typedArrayTextView.getDimensionPixelSize(R.styleable.BadgeRadioButton_badgeRadius, isNotNeedDight() ?
                dp2px(getContext(), 8) : dp2px(getContext(), 8));
        int badgePadding = typedArrayTextView.getDimensionPixelSize(R.styleable.BadgeRadioButton_badgePadding, isNotNeedDight() ? 0 : dp2px(getContext(), 2));

        for (int i = 0; i < count; i++) {
            int attr = typedArrayTextView.getIndex(i);

            if (attr == R.styleable.BadgeRadioButton_drawableLeft) {
                drawableLeft = typedArrayTextView.getDrawable(attr);
            } else if (attr == R.styleable.BadgeRadioButton_drawableTop) {
                drawableTop = typedArrayTextView.getDrawable(attr);

            } else if (attr == R.styleable.BadgeRadioButton_drawableRight) {
                drawableRight = typedArrayTextView.getDrawable(attr);

            } else if (attr == R.styleable.BadgeRadioButton_drawableBottom) {
                drawableBottom = typedArrayTextView.getDrawable(attr);

            } else if (attr == R.styleable.BadgeRadioButton_drawableStart) {
                drawableStart = typedArrayTextView.getDrawable(attr);
          /*      }else if (attr == R.styleable.BadgeRadioButton_onlypointer){
                    notNeedDight = typedArrayTextView.getBoolean(attr, false);//default need dight 不需要数字和仅仅需要点一个意思 由于循环优先级出现问题，导致默认只也有问题
                    break;*/

            } else if (attr == R.styleable.BadgeRadioButton_drawableEnd) {
                drawableEnd = typedArrayTextView.getDrawable(attr);


            } else if (attr == R.styleable.BadgeRadioButton_drawablePadding) {
                drawablePadding = typedArrayTextView.getDimensionPixelSize(attr, drawablePadding);
            } else if (attr == R.styleable.BadgeRadioButton_buttongravity) {
                radioButton.setGravity(typedArrayTextView.getInt(attr, -1));
            } else if (attr == R.styleable.BadgeRadioButton_text) {
                text = typedArrayTextView.getText(attr);
            } else if (attr == R.styleable.BadgeRadioButton_badgetext) {
                badgetext = typedArrayTextView.getText(attr);
                if (!isNotNeedDight() && TextUtils.isEmpty(badgetext)) {
//                        badgetext = " ";
                }
            } else if (attr == R.styleable.BadgeRadioButton_ellipsize) {
                ellipsize = typedArrayTextView.getInt(attr, ellipsize);


            } else if (attr == R.styleable.BadgeRadioButton_enabled) {
                setEnabled(typedArrayTextView.getBoolean(attr, isEnabled()));


            } else if (attr == R.styleable.BadgeRadioButton_buttontextColor) {
                textColor = typedArrayTextView.getColorStateList(attr);
            } else if (attr == R.styleable.BadgeRadioButton_badgetextColor) {
                badgetextColor = typedArrayTextView.getColorStateList(attr);
            } else if (attr == R.styleable.BadgeRadioButton_buttontextSize) {
                textSize = typedArrayTextView.getDimensionPixelSize(attr, textSize);
            }

        }

        typedArrayTextView.recycle();
        radioButton.setTextColor(textColor != null ? textColor : ColorStateList.valueOf(0xFF000000));

        radioButton.setButtonDrawable(0);

        radioButton.setBackgroundResource(0);
        setClickable(true);
        radioButton.setClickable(true);
        radioButton.setHintTextColor(textColorHint != null ? textColorHint : ColorStateList.valueOf(0xFF000000));
        radioButton.setText(text);
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(200);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(200);
        //初始化button wrap布局
        radioButtonWrap = new ProxyWrapRadioButtonInner(context);
//        radioButtonWrap.setId(R.id.radio_button_wrap);
        setRawTextSize(radioButton, textSize, false);
        radioButton.setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft, null, drawableRight, drawableBottom);

//          radioButton.setRelativeDrawablesIfNeeded(drawableStart, drawableEnd);
        radioButton.setCompoundDrawablePadding(drawablePadding);

//        radioButton.setId();

        mTopImageView = new RadioCheckableImageView(context);

//        mTopImageView.setPadding(0, 0, 0, drawablePadding);
        mTopImageView.setId(R.id.radio_button_drawTop);
        if (drawableTop != null) {
            mTopImageView.setImageDrawable(drawableTop);

        }

        //添加imageview
        LayoutParams layoutParamsWrap = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsWrap.addRule(CENTER_HORIZONTAL);
        radioButtonWrap.addView(mTopImageView, layoutParamsWrap);

        //添加button
        layoutParamsWrap = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsWrap.topMargin = drawablePadding;
        layoutParamsWrap.addRule(CENTER_HORIZONTAL);
        layoutParamsWrap.addRule(BELOW, mTopImageView.getId());
        radioButtonWrap.addView(radioButton, layoutParamsWrap);
        //添加内部维护
        radioButtonWrap.setInnerCheckAble(radioButton);
        radioButtonWrap.setInnerImageView(mTopImageView);
        //添加badgeview
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(radioButtonWrap, layoutParams);

        badgePointView = new TextView(context);
        badgePointView.setVisibility(INVISIBLE);

        setBadgePointDrawable(getContext(), badgeRadius);

        badgePointView.setText(badgetext);
/*        if (TextUtils.isEmpty(badgetext) && !isNotNeedDight()) {
            hide();
        } else {
            show();
        }*/
        if (badgetextColor != null) {
            badgePointView.setTextColor(badgetextColor);

        } else {
            badgePointView.setTextColor(Color.WHITE);
        }
        badgePointView.setEllipsize(TextUtils.TruncateAt.END);
        badgePointView.setGravity(Gravity.CENTER);
        setRawTextSize(badgePointView, badgetextSize, false);
        setMinBadgeSize(context, minBadgeSize);
//        badgePointView.maxl(dp2px(context,20));//maxLength
        if (notNeedDight) {
            setBadgePointPadding(0);
        } else {
            setBadgePointPadding(badgePadding);

        }
        badgePointView.setLines(1);
        badgePointView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_TOP, radioButtonWrap.getId());
        layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, radioButtonWrap.getId());

        addView(badgePointView, layoutParams);


        //修复焦点 和点击事件代理
        MyRadioButtonProxyClickListener l = new MyRadioButtonProxyClickListener();
        mTopImageView.setOnClickListener(l);
        badgePointView.setOnClickListener(l);
        onInitFinish(context,attrs);

    }

    protected void onInitFinish(Context context, AttributeSet attrs) {

    }

    private void setMinBadgeSize(Context context, int px) {
        badgePointView.setMinWidth(px);
        badgePointView.setMinHeight(px);
/*        badgePointView.setMinWidth(dp2px(context, px));
        badgePointView.setMinHeight(dp2px(context, px));*/
    }

    public void setBadgePointPadding(int padding) {
        badgePointView.setPadding(padding, padding, padding, padding);
    }

    public void setBadgePointDrawable(Context context, int badgeRadius) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setSize(badgeRadius, badgeRadius);
        gradientDrawable.setCornerRadius(badgeRadius);
        gradientDrawable.setColor(Color.parseColor("#ef132c"));
        setBadgePointDrawable(context, gradientDrawable);
    }


    public void setBadgePointDrawable(Context context, Drawable drawable) {
        badgePointView.setBackgroundDrawable(drawable);
    }


    protected int dp2px(Context context, double dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;//density=dpi/160
        return (int) (dpValue * scale + 0.5f);

    }


    private void setRawTextSize(TextView textView, float size, boolean shouldRequestLayout) {
        if (size != textView.getPaint().getTextSize()) {
            textView.getPaint().setTextSize(size);

            if (shouldRequestLayout && textView.getLayout() != null) {
                requestLayout();
                invalidate();
            }
        }
    }

    public DrawableTopRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public final void setText(CharSequence text) {
        radioButton.setText(text);
    }

    @Override
    public void setChecked(boolean checked) {
        radioButton.setChecked(checked);
        mTopImageView.setChecked(checked);

       /* if (mOnCheckedChangeWidgetListener != null) {
            mOnCheckedChangeWidgetListener.onCheckedChanged(this, mChecked);
        }*/

    }

    @Override
    public boolean isChecked() {
        return radioButton.isChecked();
    }

    @Override
    public void toggle() {

        if (!radioButton.isChecked()) {
            radioButton.toggle();
        }
    }


    @Override
    public boolean performClick() {
        toggle();

        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }

        return handled;
    }

    @Override
    public void setOnCheckedChangeWidgetListener(CompoundButton.OnCheckedChangeListener onCheckedChangeWidgetListener) {
        RadioGroupX.setRadioButtonOnCheckedChangeWidgetListener(radioButtonWrap, onCheckedChangeWidgetListener);
    }


    /**
     * Make the badge visible in the UI.
     */
    public void show() {
        show(false, null);
    }

    /**
     * Make the badge visible in the UI.
     *
     * @param animate flag to apply the default fade-in animation.
     */
    public void show(boolean animate) {
        show(animate, fadeIn);
    }

    /**
     * Make the badge visible in the UI.
     *
     * @param anim Animation to apply to the view when made visible.
     */
    public void show(Animation anim) {
        show(true, anim);
    }

    /**
     * Make the badge non-visible in the UI.
     */
    public void hide() {
        hide(false, null);
    }

    /**
     * Make the badge non-visible in the UI.
     *
     * @param animate flag to apply the default fade-out animation.
     */
    public void hide(boolean animate) {
        hide(animate, fadeOut);
    }

    /**
     * Make the badge non-visible in the UI.
     *
     * @param anim Animation to apply to the view when made non-visible.
     */
    public void hide(Animation anim) {
        hide(true, anim);
    }

    /**
     * Toggle the badge visibility in the UI.
     *
     * @param animate flag to apply the default fade-in/out animation.
     */
    public void toggle(boolean animate) {
        toggle(animate, fadeIn, fadeOut);
    }

    /**
     * Toggle the badge visibility in the UI.
     *
     * @param animIn  Animation to apply to the view when made visible.
     * @param animOut Animation to apply to the view when made non-visible.
     */
    public void toggle(Animation animIn, Animation animOut) {
        toggle(true, animIn, animOut);
    }

    private void show(boolean animate, Animation anim) {

        if (animate) {
            badgePointView.startAnimation(anim);
        }
        badgePointView.setVisibility(View.VISIBLE);
        isShown = true;
    }

    private void hide(boolean animate, Animation anim) {
        badgePointView.setVisibility(View.GONE);
        if (animate) {
            badgePointView.startAnimation(anim);
        }
        isShown = false;
    }

    private void toggle(boolean animate, Animation animIn, Animation animOut) {
        if (isShown) {
            hide(animate && (animOut != null), animOut);
        } else {
            show(animate && (animIn != null), animIn);
        }
    }

    private static Animation fadeIn;
    private static Animation fadeOut;

}
