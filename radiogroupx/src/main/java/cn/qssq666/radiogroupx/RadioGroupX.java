package cn.qssq666.radiogroupx;/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * 本项目大部分代码基于官方源代码改写，部分else逻辑基于博客Jaylong的代码改写,另外还有一部分代码 instanceOf 是本人自己改写，可以说是一个超级增强版本
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class RadioGroupX extends LinearLayout {
    private static final String TAG = "RadioGroupX";
    // holds the checked id; the selection is empty by default
    private int mCheckedId = -1;
    // tracks children radio buttons checked state
    private CompoundButton.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    public RadioGroupX(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init(context, null);
    }

    /**
     * 查找radioButton控件
     */
    public RadioButton findRadioButton(ViewGroup group) {
        RadioButton resBtn = null;
        int len = group.getChildCount();
        for (int i = 0; i < len; i++) {
            if (group.getChildAt(i) instanceof RadioButton) {
                resBtn = (RadioButton) group.getChildAt(i);
            } else if (group.getChildAt(i) instanceof ViewGroup) {
                findRadioButton((ViewGroup) group.getChildAt(i));
            }
        }
        return resBtn;
    }

    public RadioGroupX(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public RadioGroupX(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.RadioGroupX, 0, 0);


        int value = attributes.getResourceId(R.styleable.RadioGroupX_checkedButton, View.NO_ID);
        if (value != View.NO_ID) {
            mCheckedId = value;
        }

//        int orientation = getOrientation();

        final int index = attributes.getInt(R.styleable.RadioGroupX_orientation, VERTICAL);
        setOrientation(index);
        attributes.recycle();
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        if (mCheckedId != -1) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
            setCheckedId(mCheckedId);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof Checkable && child instanceof View) {//修改实例判断
            final Checkable button = (Checkable) child;
            if (button.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;

                setCheckedId(((View) button).getId());
            }
        } else if (child instanceof ViewGroup) {  //这里是我添加的代码
            final RadioButton button = findRadioButton((ViewGroup) child);
            if (button.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != -1) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(button.getId());
            }
        }//这里我添加的代码结束


        super.addView(child, index, params);
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #getCheckedRadioButtonId()
     * @see #clearCheck()
     */
    public void check(int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

    private void setCheckedId(int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof Checkable) {
            ((Checkable) checkedView).setChecked(checked);
        } else {
            Log.e(TAG, "setCheckedStateForView fail ,is not imp Checkable");
        }
    }

    /**
     * <p>Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is -1.</p>
     *
     * @return the unique id of the selected radio button in this group
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     * @see #check(int)
     * @see #clearCheck()
     */
    public int getCheckedRadioButtonId() {
        return mCheckedId;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedRadioButtonId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedRadioButtonId()
     */
    public void clearCheck() {
        check(-1);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new RadioGroupX.LayoutParams(getContext(), attrs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof RadioGroupX.LayoutParams;
    }

    @Override
    protected LinearLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(RadioGroupX.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(RadioGroupX.class.getName());
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h) {
            super(w, h);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int w, int h, float initWeight) {
            super(w, h, initWeight);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * <p>Fixes the child's width to
         * {@link ViewGroup.LayoutParams#WRAP_CONTENT} and the child's
         * height to  {@link ViewGroup.LayoutParams#WRAP_CONTENT}
         * when not specified in the XML file.</p>
         *
         * @param a          the styled attributes set
         * @param widthAttr  the width attribute to fetch
         * @param heightAttr the height attribute to fetch
         */
        @Override
        protected void setBaseAttributes(TypedArray a,
                                         int widthAttr, int heightAttr) {

            if (a.hasValue(widthAttr)) {
                width = a.getLayoutDimension(widthAttr, "layout_width");
            } else {
                width = WRAP_CONTENT;
            }

            if (a.hasValue(heightAttr)) {
                height = a.getLayoutDimension(heightAttr, "layout_height");
            } else {
                height = WRAP_CONTENT;
            }
        }
    }

    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is -1.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        public void onCheckedChanged(RadioGroupX group, int checkedId);
    }

    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

            mProtectFromCheckedChange = true;
            if (mCheckedId != -1) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            int id = buttonView.getId();//这里比较曹丹.由于必须继承CompoundButton导致
            setCheckedId(id);
        }
    }

    /**
     * <p>A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.</p>
     */
    private class PassThroughHierarchyChangeListener implements
            OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        public void onChildViewAdded(View parent, View child) {
            //TODO 这里需要改动，由于多了一层包裹

            if ((child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener)) {//这里我修改的代码
//            if (parent == RadioGroupX.this && (child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener)) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        id = View.generateViewId();
                    } else {
                        id = new Random().nextInt(10000);
                    }
                    child.setId(id);
                }
                setRadioButtonOnCheckedChangeWidgetListener(child,
                        mChildOnCheckedChangeListener);
            } else if (parent == RadioGroupX.this && child instanceof ViewGroup) {  //这里是我添加的代码else if
                RadioButton btn = findRadioButton((ViewGroup) child);
                if (btn == null) {
                    Log.e(TAG, "setRadioButtonOnCheckedChangeWidgetListener fail ,from child is not imp Checkable");
                    return;
                }
                int id = btn.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = btn.hashCode();
                    btn.setId(id);
                }
                btn.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            } else {
                Log.e(TAG, "setRadioButtonOnCheckedChangeWidgetListener fail ,is not imp Checkable");
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }


        }

        /**
         * {@inheritDoc}
         */
        public void onChildViewRemoved(View parent, View child) {
            if ((child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener)) {
//            if (parent == RadioGroupX.this && (child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener)) {
                setRadioButtonOnCheckedChangeWidgetListener(child, null);
            } else if (parent == RadioGroupX.this && child instanceof ViewGroup) {//我添加的代码else
                RadioButton radioButton = findRadioButton((ViewGroup) child);
                if (radioButton != null) {
                    radioButton.setOnCheckedChangeListener(null);
                }
            } else {
                Log.e(TAG, "setRadioButtonOnCheckedChangeWidgetListener to null fail ,is not imp Checkable");
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }


    /**
     * 通过反射调用{@link CompoundButton.OnCheckedChangeListener}
     *
     * @param view
     * @param listener
     * @return
     */
    public static boolean setRadioButtonOnCheckedChangeWidgetListener(View view, CompoundButton.OnCheckedChangeListener listener) {
        Class aClass = view.getClass();
        if (view instanceof OnCheckedChangeWidgetListener) {
            ((OnCheckedChangeWidgetListener) view).setOnCheckedChangeWidgetListener(listener);
        } else {
            while (aClass != Object.class) {
           /*
             void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeWidgetListener = listener;
    }

            */
                try {
                    Method setOnCheckedChangeWidgetListener = aClass.getDeclaredMethod("setOnCheckedChangeWidgetListener", CompoundButton.OnCheckedChangeListener.class);
                    setOnCheckedChangeWidgetListener.setAccessible(true);
                    setOnCheckedChangeWidgetListener.invoke(view, listener);
                    return true;
                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
                    aClass = aClass.getSuperclass();
                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
                    aClass = aClass.getSuperclass();
                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
                    aClass = aClass.getSuperclass();
                }
            }

        }
        return false;

    }

    public interface OnCheckedChangeWidgetListener {
        void setOnCheckedChangeWidgetListener(CompoundButton.OnCheckedChangeListener onCheckedChangeWidgetListener);
    }
}
