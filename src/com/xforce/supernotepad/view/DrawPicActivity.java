package com.xforce.supernotepad.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xforce.supernotepad.util.PaintView;
import com.xforce.supernotepad.util.Utils;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DrawPicActivity extends Activity {

	// ��ɫ�б�
	public static final int[] COLOR_IDS = { Color.BLACK, Color.RED, 0xffFC7E72,
			0xffFA8041, 0xffFAD921, 0xffADFF2F, 0xff38CFEA, Color.MAGENTA,
			0xff0888F5, Color.YELLOW, Color.GREEN, 0xff8B4513 };
	public static final int[] GRAPH_ID = { R.drawable.graph_free,
			R.drawable.graph_point, R.drawable.graph_line,
			R.drawable.graph_rec, R.drawable.graph_circle,
			R.drawable.graph_rec_fill, R.drawable.graph_circle_fill,
			R.drawable.graph_erase };

	// ��ɫѡ�����,ÿ��С��ɫ��ļ���ܶ�
	private float g = 2.0F;

	DisplayMetrics dm = new DisplayMetrics();
	private int screenWidth, screenHeight;// ��Ļ�L��
	public int BITMAP_WIDTH; // �������Ŀ�
	public int BITMAP_HEIGHT; // �������ĸ�
	private ImageButton[] mMenuBtns; // �������ʹ������еĻ��ʰ�ť
	private PaintView mPaintView; // ������
	private View mPopupCenterMenu; // ���ʹ�����
	private View mColorPickerLayout;// ��ɫѡ����������
	private GridView mColorPicker; // ��ɫѡ������ÿ��С��ɫ��

	private View mGraphPickerLayout;// ͼ��ѡ����������
	private GridView mGraphPicker; // ͼ��ѡ������ÿ��С��ɫ��

	private TextView resultPenSize;// ��̬��ʾ���ʴ�ϸֵ
	private SeekBar penSizeSeekBar; // ���ʴ�ϸ������
	private int mSelectedId = -1; // ��ɫѡ���ID
	private int mSelectedId2 = -1; // ��ɫѡ���ID

	private String editPicPath = null;// ��Ҫ�༭��ͼƬ·��

	Drawable da;
	Drawable da1;

	File sdcardTempdir = new File("/mnt/sdcard/SuperNote/tmp"); // ��ʱ�ļ���Ŀ¼
	File sdcardTempFile = new File("/mnt/sdcard/SuperNote/tmp", "tmp_pic.png");// ��ʱ�ļ�
	Uri originalUri = null; // ͼ����ȡ��ͼƬ��URI
	public int IMPORT_PIC = 0;// �Ƿ��ǵ���ͼƬ״̬(0)�����޸�ͼƬ״̬(1)
	public int MODIFY_PIC = 1;
	public int TAKE_PIC = 2;
	public int REQUEST_CODE = 0;
	public Bitmap importBmp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ�����ڶ����ı���
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.drawpic_layout);
		// mPaintView = new PaintView(this);
		mPaintView = (PaintView) findViewById(R.id.palette);

		// ȡ�ô�������
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		g = (float) (screenWidth/360.0);
		// ��ʼ����������
		initViews();
		// ȡ�����Ŀ�ߣ�ͨ����Ӧlayout�ı���ͼƬ
		da = getResources().getDrawable(R.drawable.bottom_bar);
		da1 = getResources().getDrawable(R.drawable.textnote_title_bg_1);
		BITMAP_WIDTH = screenWidth;
		BITMAP_HEIGHT = screenHeight - da.getIntrinsicHeight()
				- da1.getIntrinsicHeight();

		// �ж��½������޸�
		editPicPath = getIntent().getStringExtra("editPic");
		if (editPicPath != null) {
			mPaintView.setHasBgbm(true);
			mPaintView.init(this, BITMAP_WIDTH, BITMAP_HEIGHT, editPicPath);
		} else {
			mPaintView.init(this, BITMAP_WIDTH, BITMAP_HEIGHT);
		}
		// ��ʼ������������
		mPaintView.setScreenHeight(screenHeight);
		mPaintView.setMenuHeight(da1.getIntrinsicHeight());
		mPaintView.setScreenWidth(screenWidth);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "�˳�");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == 1) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// ����bitmap�ռ�
		if (importBmp != null && !importBmp.isRecycled()) {
			importBmp.recycle();
		}
	}

	/**
	 * @see �Զ���ĵ�������������R.id������ѡ��
	 * */

	private OnClickListener mBtnClickListener = new OnClickListener() {

		public void onClick(View paramView) {
			switch (paramView.getId()) {
			// �����
			case R.id.tuya_view:
				break;
			// ���߰�ť������󵯳�����ѡ��*************
			case R.id.toolbtns:

				break;
			// ���˰�ť
			case R.id.tuya_delete_btn:
				if (mGraphPickerLayout == null) {
					hidePopuCenterMenu();
					hideColorPicker();
					showGraphPicker(mPaintView.getCurrentPaintTool());
				} else if (mGraphPickerLayout.getVisibility() == 4
						&& mGraphPickerLayout != null) {
					hidePopuCenterMenu();
					hideColorPicker();
					showGraphPicker(mPaintView.getCurrentPaintTool());
				} else if (mGraphPickerLayout.getVisibility() == 8
						&& mGraphPickerLayout != null) {
					hidePopuCenterMenu();
					hideColorPicker();
					showGraphPicker(mPaintView.getCurrentPaintTool());
				} else if (mGraphPicker.getVisibility() == 0) {
					hideGraphPicker();
				}

				break;
			// �������˰�ť
			case R.id.paint_back_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				mPaintView.setBackPressed(true);
				mPaintView.invalidate();
				break;

			// ����ǰ����ť
			case R.id.paint_forward_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				mPaintView.setForwardPressed(true);
				mPaintView.invalidate();
				break;

			// ���ϽǱ��水ť
			case R.id.tuya_save_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				//��ʾ��
				final ProgressDialog progressDialog=new ProgressDialog(DrawPicActivity.this);
				
				progressDialog.setMessage("���ڱ���...");
				//��ʾ��ʾ��
				progressDialog.show();
				
				//�����߳̽�������Ϣ
				final Handler handler = new Handler() {
					public void handleMessage(Message message) {
						progressDialog.dismiss();
					}
				};
				
				//�����̱߳���ͼƬ
				new Thread(){
					public void run(){
						String picString = savePic();
						// ������½�һ����Ϳѻ��ͼƬ�����ظ�addnoteactivity,����ֱ���˳�
						Intent intent = new Intent();
						if (picString != null) {
							intent.putExtra("pic", picString);
						}
						setResult(Activity.RESULT_OK, intent);

						mPaintView.clearAll();
						DrawPicActivity.this.finish();
						
						Message message = handler.obtainMessage();
						handler.sendMessage(message);
					}
				}.start();
				
				break;

			// ���߰�ť������󵯳�����ѡ��**********
			case R.id.tuya_star_btn:
				if (mPopupCenterMenu.getVisibility() == 8) {
					hideColorPicker();
					hideGraphPicker();
					showPopupCenterMenu();
				} else {
					hidePopuCenterMenu();
				}
				break;
			// ��հ�ť ������������еĶ���
			case R.id.tuya_clear_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				mPaintView.clearAll();
				mPaintView.invalidate();
				checkButtonState();
				break;

			// ��ɫѡ��ť��������ѡ��
			case R.id.tuya_colorbox_btn:
				if (mColorPickerLayout == null) {
					hidePopuCenterMenu();
					hideGraphPicker();
					showColorPicker(mPaintView.getCurrentColor());
				} else if (mColorPickerLayout.getVisibility() == 4
						&& mColorPickerLayout != null) {
					hidePopuCenterMenu();
					hideGraphPicker();
					showColorPicker(mPaintView.getCurrentColor());
				} else if (mColorPickerLayout.getVisibility() == 8
						&& mColorPickerLayout != null) {
					hidePopuCenterMenu();
					hideGraphPicker();
					showColorPicker(mPaintView.getCurrentColor());
				} else if (mColorPicker.getVisibility() == 0) {
					hideColorPicker();
				}

				break;
			// �������� �� 1�� ԭʼ����
			case R.id.tuya_pen1_btn:
				mPaintView.setCurrentPenType(1);
				refreshToolboxItemsState(1);
				break;
			// �������� �� 2�� ˮ�ʻ���
			case R.id.tuya_pen2_btn:
				mPaintView.setCurrentPenType(2);
				refreshToolboxItemsState(2);
				break;

			// �������� ��3�� Ϳ�ϻ���
			case R.id.tuya_pen3_btn:
				mPaintView.setCurrentPenType(3);
				refreshToolboxItemsState(3);
				break;

			case R.id.tuya_bg_import:
				Builder localBuilder = new Builder(DrawPicActivity.this);
				localBuilder.setTitle("���ñ���");
				localBuilder.setIcon(android.R.drawable.stat_sys_warning);
				String[] arrayOfString = new String[3];
				arrayOfString[0] = "��ձ���";
				arrayOfString[1] = "����ᵼ��ͼƬ";
				arrayOfString[2] = "����";
				localBuilder.setItems(arrayOfString,
						new bgImportOnclickListener());
				localBuilder.setNegativeButton("ȡ��", null);
				localBuilder.show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @see ���ʴ�ϸ������seekbar�ļ����� �����Լ���д����
	 * */
	private OnSeekBarChangeListener penSizeListener = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			// ��̬��ʾtextview ���ʴ�ϸ��С
			seekBar.setProgress(progress);
			resultPenSize.setText(progress + "dp");
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			int currentSize = seekBar.getProgress();
			// ���ƻ��ʴ�ϸ��С��Ϊ��
			if (currentSize == 0) {
				currentSize = 1;
			}
			mPaintView.setCurrentSize(currentSize);

		}
	};

	/**
	 * @see ��ʼ�����в����е�����������ö�Ӧ����ļ�����
	 * */
	private void initViews() {
		// ��ʼ�����ø�����ť�ļ�����
		findViewById(R.id.tuya_delete_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.tuya_save_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.paint_back_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.paint_forward_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.tuya_star_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.tuya_clear_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.tuya_colorbox_btn).setOnClickListener(
				this.mBtnClickListener);
		findViewById(R.id.tuya_bg_import).setOnClickListener(
				this.mBtnClickListener);

		// ��ʼ��������ť�������������У��Ա��޸����Ƿ�ѡ��
		this.mPopupCenterMenu = findViewById(R.id.toolbtns);
		this.mMenuBtns = new ImageButton[3];
		ImageButton localImageButton0 = (ImageButton) findViewById(R.id.tuya_pen1_btn);
		localImageButton0.setOnClickListener(this.mBtnClickListener);
		this.mMenuBtns[0] = localImageButton0;
		ImageButton localImageButton1 = (ImageButton) findViewById(R.id.tuya_pen2_btn);
		localImageButton1.setOnClickListener(this.mBtnClickListener);
		this.mMenuBtns[1] = localImageButton1;
		ImageButton localImageButton2 = (ImageButton) findViewById(R.id.tuya_pen3_btn);
		localImageButton2.setOnClickListener(this.mBtnClickListener);
		this.mMenuBtns[2] = localImageButton2;
		// ����seekbar pensize����������̬���û��ʴ�ϸ
		penSizeSeekBar = (SeekBar) findViewById(R.id.seek_bar_pensize);
		penSizeSeekBar.setOnSeekBarChangeListener(penSizeListener);
		resultPenSize = (TextView) findViewById(R.id.result_pensize);
		resultPenSize.setText(penSizeSeekBar.getProgress() + "dp");
	}

	/**
	 * 
	 * @see �������ڱ���ͼƬ
	 * */
	public String savePic() {
		
		String fileUrl = null;
		String picName = null;
		if (editPicPath != null) {
			fileUrl = editPicPath;
		} else {
			picName = "pic_" + Utils.getNowDate() + ".png";
			fileUrl = Utils.PIC_PATH + picName;
		}

		try {
			FileOutputStream fos = new FileOutputStream(new File(fileUrl));
			mPaintView.getmBitmap().compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return picName;
	}

	/**
	 * @see ���ݻ��ʵĴ�ϸ�� ��������ѡ�����ˢ��,��ѡ�еġ��򹴡�
	 * */
	private void refreshToolboxItemsState(int paramInt) {
		paramInt = paramInt - 1;
		if ((paramInt >= 0) && (paramInt < this.mMenuBtns.length)) {
			for (int i = 0; i < this.mMenuBtns.length; i++) {
				if (paramInt == i) {
					this.mMenuBtns[i].setSelected(true);
				} else {
					this.mMenuBtns[i].setSelected(false);
				}

			}
		}
	}

	// ����������
	/**
	 * @see ��ʾ����ѡ�����ʹ�ϸ������
	 * */
	public void showPopupCenterMenu() {
		if (mPopupCenterMenu.getVisibility() != 0) {
			this.refreshToolboxItemsState(mPaintView.getCurrentPenType());
			this.mPopupCenterMenu.setVisibility(0);
			this.mPopupCenterMenu.startAnimation(AnimationUtils.loadAnimation(
					this, R.anim.fade_in_fast));
		}
	}

	/**
	 * @see ���ػ���ѡ�����ʹ�ϸ������
	 * */
	public void hidePopuCenterMenu() {
		if (mPopupCenterMenu.getVisibility() != 8) {
			this.mPopupCenterMenu.setVisibility(8);
			this.mPopupCenterMenu.startAnimation(AnimationUtils.loadAnimation(
					this, R.anim.fade_out_fast));
		}
	}

	/**
	 * @see ��ɫѡ�����Ƿ�����ʾ״̬�� �˷���Ϊ�˱���mColorPickerLayout��ָ������õ��ж�
	 * */
	protected boolean isColorPickerShowing() {
		return (this.mColorPickerLayout != null)
				&& (this.mColorPickerLayout.getVisibility() == 0);
	}

	/**
	 * @see ������ɫѡ����
	 * */
	public void hideColorPicker() {
		if (isColorPickerShowing()) {
			this.mColorPickerLayout.setVisibility(4);
			this.mColorPickerLayout.startAnimation(AnimationUtils
					.loadAnimation(this, R.anim.fade_out_fast));
		}
	}

	/**
	 * @see ��ʾ��ɫѡ������ˢ����ɫѡ����״̬
	 * */
	public void showColorPicker(int paramInt) {
		if (this.mColorPickerLayout == null) {
			// mColorPickerLayout��Ϊ�պ󣬶�mColorPickerLayout�������ӿؼ�mColorPicker��ʼ��
			this.mColorPickerLayout = findViewById(R.id.image_gallery_layout);
			this.mColorPicker = ((GridView) findViewById(R.id.image_gallery));
			this.mColorPicker.setGravity(3);
			// ����adapter
			ImageAdapter localImageAdapter = new ImageAdapter(this);
			this.mColorPicker.setAdapter(localImageAdapter);
			this.mColorPicker.setOnItemClickListener(null);

			// ��ɫ�߿����ز�������
			int j = (int) (60.0F * g);
			this.mColorPicker.getLayoutParams().width = (j
					* localImageAdapter.getCount() + (int) (5.0F * g));

		}
		// �ҳ���ǰ�Ļ�����ɫID
		for (int i = 0; i < COLOR_IDS.length; i++) {
			if (COLOR_IDS[i] == paramInt) {
				this.mSelectedId = i;
				break;
			}
		}
		// �����ʾ
		this.mColorPickerLayout.setVisibility(0);
		this.mColorPickerLayout.startAnimation(AnimationUtils.loadAnimation(
				this, R.anim.fade_in_fast));
	}

	/**
	 * @see ��ɫѡ������adapter�Ķ���
	 * */
	public class ImageAdapter extends BaseAdapter {
		private Context mContext;

		public ImageAdapter(Context arg2) {
			this.mContext = arg2;
		}

		public int getCount() {
			return DrawPicActivity.COLOR_IDS.length;
		}

		public Object getItem(int paramInt) {
			return Integer.valueOf(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			// ��ʼ��layout
			FrameLayout localFrameLayout = new FrameLayout(this.mContext);
			FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
					-1, -1);

			// ����ÿ����ɫ�Ŀ��
			ImageView localImageView1 = new ImageView(this.mContext);
			localFrameLayout
					.setBackgroundResource(R.drawable.selector_hw_color_picker_btn);
			// ���ÿ��������ɫ����λ��
			localImageView1.setImageDrawable(new ColorDrawable(
					DrawPicActivity.COLOR_IDS[paramInt]));
			localImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
			localImageView1.setImageMatrix(new Matrix());
			localLayoutParams1.setMargins(3, 3, 3, 3);
			localLayoutParams1.gravity = 17;
			// �ѿ��ӵ�layout����
			localFrameLayout.addView(localImageView1, localLayoutParams1);
			ImageView localImageView2 = new ImageView(this.mContext);
			// ���ÿ���ID
			localFrameLayout.setId(paramInt);
			localImageView1.setMinimumHeight(10 + localImageView1.getWidth());
			localImageView2.setImageResource(R.drawable.tool_checked);
			// ���ü�����
			localFrameLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					int i = paramAnonymousView.getId();
					if ((i >= 0) && (i < DrawPicActivity.COLOR_IDS.length)) {
						DrawPicActivity.this.mSelectedId = i;
						((BaseAdapter) DrawPicActivity.this.mColorPicker
								.getAdapter()).notifyDataSetChanged();
						mPaintView.setCurrentColor(COLOR_IDS[i]);
						DrawPicActivity.this.hideColorPicker();
					}
				}
			});
			// ����ÿ����ɫ��򶼼��� ��ѡ���е�ͼƬlocalImageView2
			FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
					-2, -2);
			localLayoutParams2.gravity = 85;
			localFrameLayout.addView(localImageView2, localLayoutParams2);
			int i = (int) (10.0F * g);
			localFrameLayout.setLayoutParams(new AbsListView.LayoutParams(
					i * 5, i * 5));
			localImageView2.setVisibility(4);
			// �����mSelectedId��ǰ�Ļ�����ɫһ�¾�����Ϊ��ʾ
			if (DrawPicActivity.this.mSelectedId == paramInt) {
				localImageView2.setVisibility(0);
			}
			return localFrameLayout;
		}
	}

	// graph_drawing ������ ��ʼ

	/**
	 * @see ��ʾͼ��ѡ������ˢ��ͼ��ѡ����״̬
	 * */
	public void showGraphPicker(int paramInt) {
		if (this.mGraphPickerLayout == null) {
			// mGraphPickerLayout��Ϊ�պ󣬶�mGraphPickerLayout�������ӿؼ�mGraphPicker��ʼ��
			this.mGraphPickerLayout = findViewById(R.id.graph_drawing_layout);
			this.mGraphPicker = ((GridView) findViewById(R.id.graph_drawing));
			this.mGraphPicker.setGravity(3);
			// ����adapter
			GraphAdapter localImageAdapter = new GraphAdapter(this);
			this.mGraphPicker.setAdapter(localImageAdapter);
			this.mGraphPicker.setOnItemClickListener(null);

			// �߿����ز�������
			int j = (int) (60.0F * g);
			this.mGraphPicker.getLayoutParams().width = (j
					* localImageAdapter.getCount() + (int) (5.0F * g));

		}
		// �ҳ���ǰ�Ļ���ͼ��ID
		this.mSelectedId2 = paramInt;
		// �����ʾ
		this.mGraphPickerLayout.setVisibility(0);
		this.mGraphPickerLayout.startAnimation(AnimationUtils.loadAnimation(
				this, R.anim.fade_in_fast));
	}

	/**
	 * @see ͼ��ѡ�����Ƿ�����ʾ״̬�� �˷���Ϊ�˱���mGraphPickerLayout��ָ������õ��ж�
	 * */
	protected boolean isGraphPickerShowing() {
		return (this.mGraphPickerLayout != null)
				&& (this.mGraphPickerLayout.getVisibility() == 0);
	}

	/**
	 * @see ����ͼ��ѡ����
	 * */
	public void hideGraphPicker() {
		if (isGraphPickerShowing()) {
			this.mGraphPickerLayout.setVisibility(4);
			this.mGraphPickerLayout.startAnimation(AnimationUtils
					.loadAnimation(this, R.anim.fade_out_fast));
		}
	}

	/**
	 * @see ͼ��ѡ������adapter�Ķ���
	 * */
	public class GraphAdapter extends BaseAdapter {
		private Context mContext;

		public GraphAdapter(Context arg2) {
			this.mContext = arg2;
		}

		public int getCount() {
			return DrawPicActivity.GRAPH_ID.length;
		}

		public Object getItem(int paramInt) {
			return Integer.valueOf(paramInt);
		}

		public long getItemId(int paramInt) {
			return paramInt;
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			// ��ʼ��layout
			FrameLayout localFrameLayout = new FrameLayout(this.mContext);
			FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
					-1, -1);

			// ����ÿ�����
			ImageView localImageView1 = new ImageView(this.mContext);
			localFrameLayout
					.setBackgroundResource(R.drawable.selector_hw_color_picker_btn);
			// ���ÿ������ͼ��
			localImageView1.setImageResource(GRAPH_ID[paramInt]);
			localImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
			localLayoutParams1.setMargins(3, 3, 3, 3);
			localLayoutParams1.gravity = 17;
			// �ѿ��ӵ�layout����
			localFrameLayout.addView(localImageView1, localLayoutParams1);
			ImageView localImageView2 = new ImageView(this.mContext);
			// ���ÿ���ID
			localFrameLayout.setId(paramInt);
			localImageView1.setMinimumHeight(10 + localImageView1.getWidth());
			localImageView2.setImageResource(R.drawable.tool_checked);
			// ���ü�����
			localFrameLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View paramAnonymousView) {
					int i = paramAnonymousView.getId();
					if ((i >= 0) && (i < DrawPicActivity.GRAPH_ID.length)) {
						DrawPicActivity.this.mSelectedId2 = i;
						((BaseAdapter) DrawPicActivity.this.mGraphPicker
								.getAdapter()).notifyDataSetChanged();
						mPaintView.setCurrentPaintTool(i);
						DrawPicActivity.this.hideGraphPicker();
					}
				}
			});
			// ����ÿ����򶼼��� ��ѡ���е�ͼƬlocalImageView2
			FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
					-2, -2);
			localLayoutParams2.gravity = 85;
			localFrameLayout.addView(localImageView2, localLayoutParams2);
			int i = (int) (10.0F * g);
			localFrameLayout.setLayoutParams(new AbsListView.LayoutParams(
					i * 5, i * 5));
			localImageView2.setVisibility(4);
			// �����mSelectedId2��ǰ�Ļ�����״һ�¾�����Ϊ��ʾ
			if (DrawPicActivity.this.mSelectedId2 == paramInt) {
				localImageView2.setVisibility(0);
			}
			return localFrameLayout;
		}
	}

	// graph_drawing ������ ����

	class bgImportOnclickListener implements DialogInterface.OnClickListener {

		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub

			sdcardTempdir.mkdir();
			try {
				sdcardTempFile.createNewFile();
			} catch (IOException e) {
			}

			if (which == 1) {
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent
						.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
				REQUEST_CODE = IMPORT_PIC;
				startActivityForResult(openAlbumIntent, REQUEST_CODE);
			} else if (which == 0) {
				clearBg();
			}else if(which == 2){
				System.out.println("choose take picture!");
				Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				originalUri = Uri.fromFile(sdcardTempFile);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
				REQUEST_CODE = TAKE_PIC;
				startActivityForResult(openCameraIntent, REQUEST_CODE);
				
			}
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == IMPORT_PIC) {
				ContentResolver resolver = getContentResolver();
				originalUri = data.getData();
				System.out.println(originalUri + "..." + sdcardTempFile.getAbsolutePath());
				try {
					importBmp = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					AlertDialog.Builder builder = new AlertDialog.Builder(
							DrawPicActivity.this);
					builder.setTitle("ͼƬ����");
					builder.setIcon(android.R.drawable.stat_sys_warning);
					builder.setMessage("�Ƿ�ü�ͼƬ��");
					android.content.DialogInterface.OnClickListener tmpListener = new android.content.DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							switch (which) {
							case Dialog.BUTTON_POSITIVE:
								System.out.println("BUTTON_POSITIVE");
								Intent intent = new Intent(
										"com.android.camera.action.CROP");
								intent.setDataAndType(originalUri, "image/*");
								intent.putExtra("crop", "true");
								checkPicSize(importBmp, intent);
								intent.putExtra("outputX", BITMAP_WIDTH);
								intent.putExtra("outputY", BITMAP_HEIGHT);
								intent.putExtra("output",
										Uri.fromFile(sdcardTempFile));
								REQUEST_CODE = MODIFY_PIC;
								startActivityForResult(intent, REQUEST_CODE);
								break;
							case Dialog.BUTTON_NEGATIVE:
								System.out.println("BUTTON_NEGATIVE");
								float scaleWidth = (float) ((BITMAP_WIDTH / 1.0) / (importBmp
										.getWidth() / 1.0));
								float scaleHeight = scaleWidth;
								Matrix m = new Matrix();
								m.postScale(scaleWidth, scaleHeight);
								importBmp = Bitmap.createBitmap(importBmp, 0,
										0, importBmp.getWidth(),
										importBmp.getHeight(), m, true);
								FileOutputStream fos;
								try {
									fos = new FileOutputStream(sdcardTempFile);
									importBmp.compress(CompressFormat.PNG, 100,
											fos);
									fos.flush();
									fos.close();
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								reloadPaintView(sdcardTempFile
										.getAbsolutePath());

								break;
							default:
								break;
							}
						}
					};
					builder.setPositiveButton("��", tmpListener);
					builder.setNegativeButton("��", tmpListener);
					builder.show();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (requestCode == MODIFY_PIC) {
				System.out.println("modifying");
				importBmp = BitmapFactory.decodeFile(sdcardTempFile
						.getAbsolutePath());
				float scaleWidth = (float) ((BITMAP_WIDTH / 1.0) / (importBmp
						.getWidth() / 1.0));
				float scaleHeight = scaleWidth;
				Matrix m = new Matrix();
				m.postScale(scaleWidth, scaleHeight);
				importBmp = Bitmap.createBitmap(importBmp, 0, 0,
						importBmp.getWidth(), importBmp.getHeight(), m, true);
				FileOutputStream fos;
				try {
					fos = new FileOutputStream(sdcardTempFile);
					importBmp.compress(CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				reloadPaintView(sdcardTempFile.getAbsolutePath());
				REQUEST_CODE = IMPORT_PIC;
				
			}else if(requestCode == TAKE_PIC){
				System.out.println("ok taking pic");
				importBmp = BitmapFactory.decodeFile(sdcardTempFile.getAbsolutePath());
				AlertDialog.Builder builder = new AlertDialog.Builder(
						DrawPicActivity.this);
				builder.setTitle("��Ƭ����");
				builder.setIcon(android.R.drawable.stat_sys_warning);
				builder.setMessage("�Ƿ�ü���Ƭ��");
				android.content.DialogInterface.OnClickListener tmpListener = new android.content.DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						
						case Dialog.BUTTON_POSITIVE:
							System.out.println("BUTTON_POSITIVE");
							Intent intent = new Intent(
									"com.android.camera.action.CROP");
							String uri = "file:/" + sdcardTempFile.getAbsolutePath();
							System.out.println("uri is " + uri);
							Uri tmpUri = Uri.parse(uri);
							intent.setDataAndType(tmpUri, "image/*");
							intent.putExtra("crop", "true");
							checkPicSize(importBmp, intent);
							intent.putExtra("outputX", BITMAP_WIDTH);
							intent.putExtra("outputY", BITMAP_HEIGHT);
							intent.putExtra("output",
									Uri.fromFile(sdcardTempFile));
							REQUEST_CODE = MODIFY_PIC;
							startActivityForResult(intent, REQUEST_CODE);
							break;
						case Dialog.BUTTON_NEGATIVE:
							System.out.println("BUTTON_NEGATIVE");
							//123123123123123123
							float scaleWidth = (float) ((BITMAP_WIDTH / 1.0) / (importBmp
									.getWidth() / 1.0));
							float scaleHeight = scaleWidth;
							Matrix m = new Matrix();
							m.postScale(scaleWidth, scaleHeight);
							importBmp = Bitmap.createBitmap(importBmp, 0,
									0, importBmp.getWidth(),
									importBmp.getHeight(), m, true);
							FileOutputStream fos;
							try {
								fos = new FileOutputStream(sdcardTempFile);
								importBmp.compress(CompressFormat.PNG, 100,
										fos);
								fos.flush();
								fos.close();
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							reloadPaintView(sdcardTempFile
									.getAbsolutePath());

							break;
						default:
							break;
						}
					}
				};
				builder.setPositiveButton("��", tmpListener);
				builder.setNegativeButton("��", tmpListener);
				builder.show();
				
				
			}
		}
	}

	public void checkPicSize(Bitmap bmp, Intent intent) {
		if (bmp.getWidth() > bmp.getHeight()) {
			intent.putExtra("aspectX", BITMAP_HEIGHT);// �ü������
			intent.putExtra("aspectY", BITMAP_WIDTH);
		} else {
			intent.putExtra("aspectX", BITMAP_WIDTH);// �ü������
			intent.putExtra("aspectY", BITMAP_HEIGHT);
		}
	}

	public void checkPicSize(Bitmap bmp, float scaleWidth, float scaleHeight) {

		if (bmp.getWidth() > bmp.getHeight()) {
			scaleWidth = (float) ((BITMAP_WIDTH / 1.0) / (bmp.getWidth() / 1.0));
			scaleHeight = scaleWidth;
		} else {
			scaleHeight = (float) ((BITMAP_HEIGHT / 1.0) / (bmp.getHeight() / 1.0));
			scaleWidth = scaleHeight;
		}
	}

	public void reloadPaintView(String picPath) {
		System.out.println(picPath);
		this.mPaintView.setBgPicFrmGally(picPath);
	}

	public void clearBg() {
		this.mPaintView.clearBgWithColor();
	}
	
	public void checkButtonState() {
		int size = mPaintView.getActionListCount();
		int index = mPaintView.getCurrentPaintIndex();

		if (index == -1) {
			findViewById(R.id.paint_back_btn).setEnabled(false);
			findViewById(R.id.paint_back_btn).setPressed(false);
			if (index == -1 || size == 0) {
				findViewById(R.id.tuya_clear_btn).setEnabled(false);
				findViewById(R.id.tuya_clear_btn).setPressed(false);
			}
			if(!mPaintView.isHasBgbm()){
				System.out.println("will running  here1");
				findViewById(R.id.tuya_save_btn).setEnabled(false);
				findViewById(R.id.tuya_save_btn).setPressed(false);
			}
		} else {
			System.out.println("will running  here2");
			findViewById(R.id.paint_back_btn).setEnabled(true);
			findViewById(R.id.tuya_clear_btn).setEnabled(true);
			findViewById(R.id.tuya_save_btn).setEnabled(true);
			
		}

		if (index == (size - 1)) {
			findViewById(R.id.paint_forward_btn).setEnabled(false);
			findViewById(R.id.paint_forward_btn).setPressed(false);
		} else {
			findViewById(R.id.paint_forward_btn).setEnabled(true);
		}
		
		if( index == -1 && mPaintView.isHasBgbm()){
			findViewById(R.id.tuya_save_btn).setEnabled(false);
			findViewById(R.id.tuya_save_btn).setPressed(false);
		}
	}
}
