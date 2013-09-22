package com.xforce.supernotepad.util;


import com.xforce.supernotepad.view.R;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.RadioButton;

public class TypeRadioButton extends RadioButton {

	public TypeRadioButton(Context context,String name) {
		super(context);
		// TODO Auto-generated constructor stub
		setText(name);
        setTextSize(20);
        setBackgroundResource(R.drawable.radiobtn_selector);//���Ҫ��setpaddingǰ�棬��Ȼsetpadding��ʧЧ
        setTextColor(Color.WHITE);
        setButtonDrawable(android.R.color.transparent);
        setPadding(10,10,10,10);
        setGravity(Gravity.CENTER);
        
	}

}
