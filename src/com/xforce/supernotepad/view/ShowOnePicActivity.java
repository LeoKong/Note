package com.xforce.supernotepad.view;



import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ShowOnePicActivity extends Activity implements OnClickListener {
	private Bitmap bitmap;
	ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showpic_layout);

		String picPath = this.getIntent().getStringExtra("picPath");
		imageView = (ImageView) findViewById(R.id.onepic_iv);

		bitmap = BitmapFactory.decodeFile(picPath);
		imageView.setImageBitmap(bitmap);
		imageView.setOnClickListener(this);

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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// bitmap.recycle();
		// bitmap = null;
		// 回收bitmap空间
				if (bitmap != null && !bitmap.isRecycled())
					bitmap.recycle();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		ShowOnePicActivity.this.finish();
		overridePendingTransition(0, R.anim.alpha_scale_out);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			ShowOnePicActivity.this.finish();
			overridePendingTransition(0, R.anim.alpha_scale_out);
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
