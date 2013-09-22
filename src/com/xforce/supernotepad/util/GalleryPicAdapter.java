package com.xforce.supernotepad.util;

import java.util.List;

import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.view.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * 
 * 文件名：ImageAdapter.java 继承父类BaseAdapter 功 能：gallery的配置器 参 数： 返 回 值：
 */
public class GalleryPicAdapter extends BaseAdapter {

//	private int mGalleryItemBackground; // 背景参数

	private LayoutInflater inflater;
	private Context mContext; // 上下文
	private List<PictureModel> allPic; // 图片信息

	public GalleryPicAdapter(Context context, List<PictureModel> allPic) {
		this.allPic = allPic;
		mContext = context;
		this.inflater = LayoutInflater.from(context);
		// 基本参数设置
//		TypedArray a = mContext.obtainStyledAttributes(R.styleable.Gallery1);
//		mGalleryItemBackground = a.getResourceId(
//				R.styleable.Gallery1_android_galleryItemBackground, 0);
//		a.recycle();
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return allPic.size();
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
		
		System.out.println(position+"--------------");
		
		
		if (convertView==null) {
			convertView = inflater.inflate(R.layout.gallery_pic_item, null);
		}
		ImageView imageView=(ImageView) convertView.findViewById(R.id.gallery_pic);
		
		Utils.loadPic(imageView, Utils.PIC_PATH+allPic.get(position).getpName());
		
//		new Thread(){
//			public void run(){
//				
//				imageView.setImageBitmap(Utils.compressImg(Utils.PIC_PATH
//						+ allPic.get(p).getpName(), 30.00f));
//			}
//			
//		}.start();
		
		
		
//		imageView.setBackgroundResource(R.drawable.grid_pic_cover);// 设置缩略图后面的背景
		
		
//		ImageView i = new ImageView(mContext);
//
//		// 显示所有图片，包括添加图片的按钮
//
//		// 通过工具compressImg先压缩图片，压缩到30K（可自定义）这个范围再将其加入到imageview当中
//		i.setImageBitmap(Utils.compressImg(Utils.PIC_PATH
//				+ allPic.get(position).getpName(), 1.00f));
//
//		i.setScaleType(ImageView.ScaleType.FIT_CENTER);// 设置图片以怎样的形式适应相框，如下为居中
//		i.setLayoutParams(new Gallery.LayoutParams(300, 400));// 设置缩略图的大小，X
//																// Y（可自定义）
//		i.setBackgroundResource(R.drawable.grid_pic_cover);// 设置缩略图后面的背景
		// i.setBackgroundColor(Color.BLACK);
		return convertView;
	}

}
