package cn.qssq666.radiogroupdemo;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.test.ProviderTestCase2;
import android.util.Log;
import android.widget.Checkable;
import android.widget.RadioButton;

import cn.qssq666.radiogroupx.BadgeRadioButton;
import cn.qssq666.radiogroupx.DrawableTopRadioButton;
import cn.qssq666.radiogroupx.RadioGroupX;

public class MainActivity extends AppCompatActivity implements RadioGroupX.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadioGroupX groupX = ((RadioGroupX) findViewById(R.id.radiogroup));
        groupX.setOnCheckedChangeListener(this);
        ((Checkable) groupX.getChildAt(0)).setChecked(true);
        RadioButton radioButton = ((DrawableTopRadioButton) groupX.getChildAt(0)).getRadioButton();
        Log.w(TAG,radioButton.isChecked()+","+radioButton.isFocused()+","+radioButton.isEnabled());
        BadgeRadioButton childAt = (BadgeRadioButton) groupX.getChildAt(3);

        //        childAt.hide();//隐藏或者显示 支持模式和数字点模式!
        //child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener
    }

    @Override
    public void onCheckedChanged(RadioGroupX group, int checkedId) {
        int index = group.indexOfChild(group.findViewById(checkedId));
        Log.w(TAG,"INDEX:"+index);
    }
}
