package com.xforce.supernotepad.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.xforce.supernotepad.view.R;

public class BeginningActivity extends Activity {
	private ImageView imageView;
	private AnimationDrawable walk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.beginning_layout);
		// overridePendingTransition(R.anim.push_top_in, R.anim.push_top_out);
		imageView = (ImageView) findViewById(R.id.walk);
		//…Ë÷√∂Øª≠Õº∆¨
		imageView.setBackgroundResource(R.drawable.begining);
		walk = (AnimationDrawable) imageView
				.getBackground();
//		walk.start();
		int duration = 0;
		for (int i = 0; i < walk.getNumberOfFrames(); i++) {
			duration += walk.getDuration(i);
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				Intent intent = new Intent(BeginningActivity.this,
						MainActivity.class);
				startActivity(intent);
				BeginningActivity.this.finish();
				overridePendingTransition(0, R.anim.fade_out);
			}
		}, duration);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		walk.start();
	}

}
