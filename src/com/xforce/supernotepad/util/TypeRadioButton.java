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
        setBackgroundResource(R.drawable.radiobtn_selector);//这个要在setpadding前面，不然setpadding会失效
        setTextColor(Color.WHITE);
        setButtonDrawable(android.R.color.transparent);
        setPadding(10,10,10,10);
        setGravity(Gravity.CENTER);
        
	}

}
