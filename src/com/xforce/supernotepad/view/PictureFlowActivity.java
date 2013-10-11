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
	private ViewFlow viewFlow;// 自定义的可左右切换图片的组件（源于org.taptwo.android.widget的三个文件）

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewflow_layout);
		initViews();// 初始化组件
		setups();// 时时监听图片的位置
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 开启友盟统计
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止友盟统计
		MobclickAgent.onPause(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {// 防止浪费内存
			PictureFlowActivity.this.finish();// 关闭自身
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 方法名：initViews() 功 能：初始化组件，以及相关参数 参 数： 返 回 值： 无
	 */
	@SuppressWarnings("unchecked")
	private void initViews() {
		ArrayList<PictureModel> picList=(ArrayList<PictureModel>) getIntent().getSerializableExtra("pics");
		MyImageAdpater adp = new MyImageAdpater(this,picList);
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(adp, AddNoteActivity.GALLERY_INDEX);// 设置viewFlowGallery首先展示的图片ID
		CircleFlowIndicator indic = (CircleFlowIndicator) findViewById(R.id.viewflowindic);// 定义图像展示时候，顶部的圆点
		viewFlow.setFlowIndicator(indic);// 设置顶部圆点的工作方式
	}

	/**
	 * 方法名：setups() 功 能：初始化组件，以及相关参数 参
	 * 数：监听滑动时候所选中的项，并改变ShowGallery.INDEX的值，当要返回缩略图ShowGallery时候，则同步所选中的项 返 回 值：
	 * 无
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
	 * 内部类名：MyImageAdpater 继承父类BaseAdapter 功 能：viewFlow的配置器 参 数： 返 回 值：
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
					.findViewById(R.id.imageView1);// 定义显示每张图片的imageview
			imageView.setImageBitmap(Utils.compressImg(Utils.PIC_PATH
					+ allPicUri.get(position).getpName(), 100.00f));// 把bitmap挂载上配置器上

			TextView commentView = (TextView) convertView
					.findViewById(R.id.comment_tv);
			
			commentView.setText(allPicUri.get(position).getIllustration());

			return convertView;

		}
	}

}
