package com.xforce.supernotepad.view;



import java.util.ArrayList;
import java.util.List;

import org.taptwo.android.widget.CircleFlowIndicator;
import org.taptwo.android.widget.ViewFlow;

import com.umeng.analytics.MobclickAgent;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.Utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PictureFlowActivity extends Activity{
	private ViewFlow viewFlow;// �Զ���Ŀ������л�ͼƬ�������Դ��org.taptwo.android.widget�������ļ���

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewflow_layout);
		initViews();// ��ʼ�����
		setups();// ʱʱ����ͼƬ��λ��
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// ��������ͳ��
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// ֹͣ����ͳ��
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {// ��ֹ�˷��ڴ�
			PictureFlowActivity.this.finish();// �ر�����
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ��������initViews() �� �ܣ���ʼ��������Լ���ز��� �� ���� �� �� ֵ�� ��
	 */
	@SuppressWarnings("unchecked")
	private void initViews() {
		ArrayList<PictureModel> picList=(ArrayList<PictureModel>) getIntent().getSerializableExtra("pics");
		MyImageAdpater adp = new MyImageAdpater(this,picList);
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(adp, AddNoteActivity.GALLERY_INDEX);// ����viewFlowGallery����չʾ��ͼƬID
		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);// ����ͼ��չʾʱ�򣬶�����Բ��
		viewFlow.setFlowIndicator(indic);// ���ö���Բ��Ĺ�����ʽ
	}

	/**
	 * ��������setups() �� �ܣ���ʼ��������Լ���ز��� ��
	 * ������������ʱ����ѡ�е�����ı�ShowGallery.INDEX��ֵ����Ҫ��������ͼShowGalleryʱ����ͬ����ѡ�е��� �� �� ֵ��
	 * ��
	 */
	private void setups() {
		viewFlow.setOnViewSwitchListener(new ViewFlow.ViewSwitchListener() {
			public void onSwitched(View view, int position) {
				AddNoteActivity.GALLERY_INDEX = position;
			}
		});
	}

	/**
	 * 
	 * �ڲ�������MyImageAdpater �̳и���BaseAdapter �� �ܣ�viewFlow�������� �� ���� �� �� ֵ��
	 */
	private class MyImageAdpater extends BaseAdapter {
		private ArrayList<PictureModel> allPicUri;
		private LayoutInflater inflater;

		public MyImageAdpater(Context mContext,
				ArrayList<PictureModel> allPicUri) {

			this.allPicUri = allPicUri;
			this.inflater = LayoutInflater.from(mContext);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return allPicUri.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return allPicUri.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			
			if (convertView==null) {
				convertView=inflater.inflate(R.layout.picture_item, null);
			}
			
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.imageView1);// ������ʾÿ��ͼƬ��imageview
			imageView.setImageBitmap(Utils.compressImg(Utils.PIC_PATH
					+ allPicUri.get(position).getpName(), 100.00f));// ��bitmap��������������

			TextView commentView = (TextView) convertView
					.findViewById(R.id.comment_tv);
			
			commentView.setText(allPicUri.get(position).getIllustration());

			return convertView;

		}
	}

}
