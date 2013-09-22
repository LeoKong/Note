package com.xforce.supernotepad.util;


import com.xforce.supernotepad.view.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

public class FaceAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
	private int[] imageId;

	public FaceAdapter(Context context,int[] imageID) {
		super();
		// TODO Auto-generated constructor stub
		this.mInflater=LayoutInflater.from(context);
		this.imageId=imageID;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return imageId.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
//		if (convertView!=null) {
			convertView = mInflater.inflate(R.layout.face_item, null);
			
			ImageView imageView=(ImageView) convertView.findViewById(R.id.everyface);
			imageView.setBackgroundResource(imageId[position]);
			
//		}
		return convertView;
	}

}
