package com.xforce.supernotepad.view;


import android.app.Activity;
import android.os.Bundle;

import com.aphidmobile.flip.FlipViewController;
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

	}

	@Override
	public void onResume() {
		super.onResume();
		flipView.onResume();

	}

}
