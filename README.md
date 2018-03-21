# radiogroupx实现tabhost+小红点
无需大改动让RadioGroup里的view支持小红点数字或纯小红点。还是原来的配方，还是原来的味道

### 如何直接用Android Studio在线接入
第一步在项目根目录build.gralde中添加如下代码 
```groovy


	allprojects {
		repositories {

			maven { url 'https://jitpack.io' }
		}
	}

```
第二步 在主模块中build.gradle添加如下代码
```
	dependencies {
	        compile 'com.github.qssq:radiogroupx:v1.0'
	}


```
### 如何用Eclipse 或者直接下载模块源代码到android studio
https://github.com/qssq/radiogroupx 直接打包下载源码 把radiogroupx模块弄进去
### 具体用法

java代码非常精简所以我说是原来的配方原来的味道:
```
       RadioGroupX groupX = ((RadioGroupX) findViewById(R.id.radiogroup));
        groupX.setOnCheckedChangeListener(this);
    ((RadioButton) groupX.getChildAt(0)).setChecked(true);
        BadgeRadioButton childAt = (BadgeRadioButton) groupX.getChildAt(3);
//        childAt.hide();//隐藏或者显示 支持模式和数字点模式!
        //child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener
 ```
 把原来的```RadioGroup```修改为```cn.qssq666.radiogroupx.RadioGroupX``` 不加小红点直接使用原来的```RadioButton``` 要加小红点的使用``` cn.qssq666.radiogroupx.BadgeRadioButton```
 如果你想自己写一个高级选择器，那么只需要实现``` child instanceof Checkable || child instanceof RadioGroupX.OnCheckedChangeWidgetListener```即可。
 
 xml中配置
 ```
       <cn.qssq666.radiogroupx.BadgeRadioButton
                android:id="@+id/radio_btn_3"
                style="@style/buttom_strip_radiobuttonx"
                app:badgeRadius="8dp"
                app:badgetext=""
                app:badgetextColor="@android:color/white"
                app:badgetextSize="5dp"
                app:buttontextColor="@color/colorThemeBlack"
                app:buttontextSize="@dimen/text_size_12"
                app:drawableTop="@drawable/selector_btn_tab3"
                app:minBadgeSize="2dp"
                app:onlypointer="true"
                app:text="谁看我" />
  ```
  如果不是显示红点而是显示数字小红点 请改为```onlypointer="false"```
xml布局:
```

    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom"

        >

        <cn.qssq666.radiogroupx.RadioGroupX
            android:id="@+id/radiogroup"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            android:background="@android:color/white"
            android:gravity="center"
            android:paddingBottom="2dp"
            android:paddingTop="2dp"
            app:orientation="horizontal">


            <RadioButton
                android:id="@+id/radio_btn_1"
                style="@style/buttom_strip_radiobutton"
                android:layout_weight="1"
                android:checked="false"
                android:drawableTop="@drawable/selector_btn_tab1"
                android:text="头条"
                android:textSize="12sp" />

            <RadioButton

                android:id="@+id/radio_btn_2"
                style="@style/buttom_strip_radiobutton"
                android:layout_weight="1"
                android:drawableTop="@drawable/selector_btn_tab2"
                android:text="营销课" />


            <TextView

                android:layout_width="wrap_content"
                android:layout_height="5dp"

                android:layout_gravity="center_horizontal|bottom"
                android:drawableTop="@drawable/tab_add_selected"
                android:gravity="center"
                android:text="有料"
                android:textColor="@color/colorThemeBlack"
                android:textSize="@dimen/text_size_12"
                android:visibility="invisible" />

            <cn.qssq666.radiogroupx.BadgeRadioButton
                android:id="@+id/radio_btn_3"
                style="@style/buttom_strip_radiobuttonx"
                app:badgeRadius="8dp"
                app:badgetext=""
                app:badgetextColor="@android:color/white"
                app:badgetextSize="5dp"
                app:buttontextColor="@color/colorThemeBlack"
                app:buttontextSize="@dimen/text_size_12"
                app:drawableTop="@drawable/selector_btn_tab3"
                app:minBadgeSize="2dp"
                app:onlypointer="true"
                app:text="谁看我" />

            <RadioButton
                android:id="@+id/radio_btn_4"
                style="@style/buttom_strip_radiobutton"
                android:drawableTop="@drawable/selector_btn_tab4"

                android:text="我的" />

        </cn.qssq666.radiogroupx.RadioGroupX>

        <TextView
            android:id="@+id/btn_call_boommenu"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal|bottom"
            android:drawableTop="@drawable/tab_add_selected"
            android:gravity="center"
            android:paddingBottom="2dp"
            android:text="有料"
            android:textColor="@color/colorThemeBlack"
            android:textSize="@dimen/text_size_12" />

    </FrameLayout>
```

### 什么样的效果？可以实现什么样的需求？能否扩展
可以实现纯小红点，也可以实现带数字小红点,扩展性比原生更强.只需要实现``` Checkable ```或者 ```instanceof RadioGroupX.OnCheckedChangeWidgetListener```就可以实现扩展

### 效果图
简书地址 https://www.jianshu.com/p/dfd06a955a05 有图有真相

### 图片中的效果有用原始的RadioGroup有难度否?
对于一个开发菜鸟来说，如果要中间显示一个突出部分，又要实现小红点，原生的 ```RadioGroup``` 很难实现,就算实现了,要么非常麻烦或者是适配有问题,不同手机可能坐标不一致,因为这里用到了权重.
  