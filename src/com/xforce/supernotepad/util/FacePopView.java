package com.xforce.supernotepad.util;


import com.xforce.supernotepad.view.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;

public class FacePopView extends PopupWindow {
	private View faceView;
	private GridView gridView;

	public FacePopView(Activity context, int[] imageId,OnItemClickListener itemClickListener) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		faceView = inflater.inflate(R.layout.face_popview, null);

		gridView = (GridView) faceView.findViewById(R.id.face_gridview);
		
		gridView.setOnItemClickListener(itemClickListener);

		this.setContentView(gridView);

		// 设置弹出窗体的动画
		this.setAnimationStyle(R.style.AnimPopUp);

		// 设置弹出窗体的宽和高
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);

		gridView.setAdapter(new FaceAdapter(context, imageId));
		

	}

}
