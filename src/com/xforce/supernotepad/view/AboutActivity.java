package com.xforce.supernotepad.view;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {
	TextView backButton;
	TextView versionView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);

		backButton = (TextView) findViewById(R.id.backbtn);
		versionView=(TextView) findViewById(R.id.version_tv);
		
		versionView.setText("XNote记事本 "+getVersion());

		backButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutActivity.this.finish();
			}
		});
	}

	/**
	 * 获取版本号
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
