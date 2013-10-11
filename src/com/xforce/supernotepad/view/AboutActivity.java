package com.xforce.supernotepad.view;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

public class AboutActivity extends Activity {
	TextView backButton;
	TextView versionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);

		backButton = (TextView) findViewById(R.id.backbtn);
		versionView = (TextView) findViewById(R.id.version_tv);

		versionView.setText("XNote���±� " + getVersion());

		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutActivity.this.finish();
			}
		});
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

	/**
	 * ��ȡ�汾��
	 * 
	 * @return
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return "V" + version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
