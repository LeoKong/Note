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
 * �ļ�����ImageAdapter.java �̳и���BaseAdapter �� �ܣ�gallery�������� �� ���� �� �� ֵ��
 */
public class GalleryPicAdapter extends BaseAdapter {

//	private int mGalleryItemBackground; // ��������

	private LayoutInflater inflater;
	private Context mContext; // ������
	private List<PictureModel> allPic; // ͼƬ��Ϣ

	public GalleryPicAdapter(Context context, List<PictureModel> allPic) {
		this.allPic = allPic;
		mContext = context;
		this.inflater = LayoutInflater.from(context);
		// ������������
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
		
		
		
//		imageView.setBackgroundResource(R.drawable.grid_pic_cover);// ��������ͼ����ı���
		
		
//		ImageView i = new ImageView(mContext);
//
//		// ��ʾ����ͼƬ���������ͼƬ�İ�ť
//
//		// ͨ������compressImg��ѹ��ͼƬ��ѹ����30K�����Զ��壩�����Χ�ٽ�����뵽imageview����
//		i.setImageBitmap(Utils.compressImg(Utils.PIC_PATH
//				+ allPic.get(position).getpName(), 1.00f));
//
//		i.setScaleType(ImageView.ScaleType.FIT_CENTER);// ����ͼƬ����������ʽ��Ӧ�������Ϊ����
//		i.setLayoutParams(new Gallery.LayoutParams(300, 400));// ��������ͼ�Ĵ�С��X
//																// Y�����Զ��壩
//		i.setBackgroundResource(R.drawable.grid_pic_cover);// ��������ͼ����ı���
		// i.setBackgroundColor(Color.BLACK);
		return convertView;
	}

}
