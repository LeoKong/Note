package com.xforce.supernotepad.view;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.WindowManager;

import com.harism.curl.CurlPage;
import com.harism.curl.CurlView;
import com.xforce.supernotepad.dao.PictureDao;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.Utils;

public class AlbumActivity extends Activity {
	private CurlView curlView;
	private List<PictureModel> picList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.album_layout);

		PictureDao pictureDao = new PictureDao(this);
		picList = pictureDao.getAllPictures();

		// int index = 0;
		// if (getLastNonConfigurationInstance() != null) {
		// index = (Integer) getLastNonConfigurationInstance();
		// }

		curlView = (CurlView) findViewById(R.id.album_view);
		curlView.setPageProvider(new PageProvider());
		curlView.setSizeChangedObserver(new SizeChangedObserver());
		curlView.setCurrentIndex(0);
		curlView.setBackgroundColor(Color.BLACK);

	}

	@Override
	public void onPause() {
		super.onPause();
		curlView.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		curlView.onResume();
	}

	// @Override
	// public Object onRetainNonConfigurationInstance() {
	// return curlView.getCurrentIndex();
	// }

	private class PageProvider implements CurlView.PageProvider {

		@Override
		public int getPageCount() {
			return picList.size();
		}

		private Bitmap loadBitmap(int width, int height, String picName) {
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			bitmap.eraseColor(0xFFFFFFFF);
			Canvas canvas = new Canvas(bitmap);
			Drawable drawable = Drawable.createFromPath(Utils.PIC_PATH
					+ picName);

			int margin = 7;
			int border = 3;
			Rect rect = new Rect(margin, margin, width - margin, height
					- margin);

			int imageWidth = rect.width() - (border * 2);
			int imageHeight = imageWidth * drawable.getIntrinsicHeight()
					/ drawable.getIntrinsicWidth();
			if (imageHeight > rect.height() - (border * 2)) {
				imageHeight = rect.height() - (border * 2);
				imageWidth = imageHeight * drawable.getIntrinsicWidth()
						/ drawable.getIntrinsicHeight();
			}

			rect.left += ((rect.width() - imageWidth) / 2) - border;
			rect.right = rect.left + imageWidth + border + border;
			rect.top += ((rect.height() - imageHeight) / 2) - border;
			rect.bottom = rect.top + imageHeight + border + border;

			Paint paint = new Paint();
			paint.setColor(0xFFC0C0C0);
			canvas.drawRect(rect, paint);
			rect.left += border;
			rect.right -= border;
			rect.top += border;
			rect.bottom -= border;

			drawable.setBounds(rect);
			drawable.draw(canvas);

			return bitmap;
		}

		@Override
		public void updatePage(CurlPage page, int width, int height, int index) {

			if (index == picList.size() - 1) {
				Bitmap front = loadBitmap(width, height, picList.get(index)
						.getpName());
				page.setTexture(front, CurlPage.SIDE_BOTH);
				page.setColor(Color.argb(127, 255, 255, 255),
						CurlPage.SIDE_BACK);
			} else {
				Bitmap front = loadBitmap(width, height, picList.get(index)
						.getpName());
				Bitmap back = loadBitmap(width, height, picList.get(index + 1)
						.getpName());
				page.setTexture(front, CurlPage.SIDE_FRONT);
				page.setTexture(back, CurlPage.SIDE_FRONT);
			}

			// switch (index) {
			// // First case is image on front side, solid colored back.
			// case 0: {
			// Bitmap front = loadBitmap(width, height,
			// picList.get(0).getpName());
			// page.setTexture(front, CurlPage.SIDE_FRONT);
			// page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);
			// break;
			// }
			//
			// // Second case is image on back side, solid colored front.
			// case 1: {
			// Bitmap back = loadBitmap(width, height,
			// "pic_20130621_114444.png");
			// page.setTexture(back, CurlPage.SIDE_BACK);
			// page.setColor(Color.rgb(127, 140, 180), CurlPage.SIDE_FRONT);
			// break;
			// }
			// // Third case is images on both sides.
			// case 2: {
			// Bitmap front = loadBitmap(width, height,
			// "pic_20130621_114444.png");
			// Bitmap back = loadBitmap(width, height,
			// "pic_20130621_114444.png");
			// page.setTexture(front, CurlPage.SIDE_FRONT);
			// page.setTexture(back, CurlPage.SIDE_BACK);
			// break;
			// }
			// // Fourth case is images on both sides - plus they are blend
			// against
			// // separate colors.
			// case 3: {
			// Bitmap front = loadBitmap(width, height,
			// "pic_20130621_114444.png");
			// Bitmap back = loadBitmap(width, height,
			// "pic_20130621_114444.png");
			// page.setTexture(front, CurlPage.SIDE_FRONT);
			// page.setTexture(back, CurlPage.SIDE_BACK);
			// page.setColor(Color.argb(127, 170, 130, 255),
			// CurlPage.SIDE_FRONT);
			// page.setColor(Color.rgb(255, 190, 150), CurlPage.SIDE_BACK);
			// break;
			// }
			// // Fifth case is same image is assigned to front and back. In
			// this
			// // scenario only one texture is used and shared for both sides.
			// case 4:
			// Bitmap front = loadBitmap(width, height,
			// "pic_20130621_114444.png");
			// page.setTexture(front, CurlPage.SIDE_BOTH);
			// page.setColor(Color.argb(127, 255, 255, 255),
			// CurlPage.SIDE_BACK);
			// break;
			// }
		}

	}

	/**
	 * CurlView size changed observer.
	 */
	private class SizeChangedObserver implements CurlView.SizeChangedObserver {
		@Override
		public void onSizeChanged(int w, int h) {
			if (w > h) {
				curlView.setViewMode(CurlView.SHOW_TWO_PAGES);
				curlView.setMargins(.1f, .05f, .1f, .05f);
			} else {
				curlView.setViewMode(CurlView.SHOW_ONE_PAGE);
				curlView.setMargins(.1f, .1f, .1f, .1f);
			}
		}
	}

}
