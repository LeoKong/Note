package com.xforce.supernotepad.view;

import android.app.Activity;
import android.os.Bundle;

import com.aphidmobile.flip.FlipViewController;
import com.umeng.analytics.MobclickAgent;
import com.xforce.supernotepad.adapter.AlbumAdapter;

public class AlbumActivity extends Activity {

	private FlipViewController flipView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		flipView = new FlipViewController(this, FlipViewController.HORIZONTAL);
		flipView.setAdapter(new AlbumAdapter(this));
		setContentView(flipView);

	}

	@Override
	public void onPause() {
		super.onPause();
		flipView.onPause();
		// 停止友盟统计
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		flipView.onResume();
		// 开启友盟统计
		MobclickAgent.onResume(this);

	}

}
