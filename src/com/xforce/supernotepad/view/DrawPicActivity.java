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

	// 颜色列表
	public static final int[] COLOR_IDS = { Color.BLACK, Color.RED, 0xffFC7E72,
			0xffFA8041, 0xffFAD921, 0xffADFF2F, 0xff38CFEA, Color.MAGENTA,
			0xff0888F5, Color.YELLOW, Color.GREEN, 0xff8B4513 };
	public static final int[] GRAPH_ID = { R.drawable.graph_free,
			R.drawable.graph_point, R.drawable.graph_line,
			R.drawable.graph_rec, R.drawable.graph_circle,
			R.drawable.graph_rec_fill, R.drawable.graph_circle_fill,
			R.drawable.graph_erase };

	// 颜色选择框中,每个小颜色框的间隔密度
	private float g = 2.0F;

	DisplayMetrics dm = new DisplayMetrics();
	private int screenWidth, screenHeight;// 屏幕長寬
	public int BITMAP_WIDTH; // 主画布的宽
	public int BITMAP_HEIGHT; // 主画布的高
	private ImageButton[] mMenuBtns; // 弹出画笔工具栏中的画笔按钮
	private PaintView mPaintView; // 主画笔
	private View mPopupCenterMenu; // 画笔工具栏
	private View mColorPickerLayout;// 颜色选择栏的整体
	private GridView mColorPicker; // 颜色选择栏中每个小颜色框

	private View mGraphPickerLayout;// 图形选择栏的整体
	private GridView mGraphPicker; // 图形选择栏中每个小颜色框

	private TextView resultPenSize;// 动态显示画笔粗细值
	private SeekBar penSizeSeekBar; // 画笔粗细滚动条
	private int mSelectedId = -1; // 颜色选择的ID
	private int mSelectedId2 = -1; // 颜色选择的ID

	private String editPicPath = null;// 所要编辑的图片路径

	Drawable da;
	Drawable da1;

	File sdcardTempdir = new File("/mnt/sdcard/SuperNote/tmp"); // 临时文件的目录
	File sdcardTempFile = new File("/mnt/sdcard/SuperNote/tmp", "tmp_pic.png");// 临时文件
	Uri originalUri = null; // 图库中取得图片的URI
	public int IMPORT_PIC = 0;// 是否是导入图片状态(0)还是修改图片状态(1)
	public int MODIFY_PIC = 1;
	public int TAKE_PIC = 2;
	public int REQUEST_CODE = 0;
	public Bitmap importBmp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除窗口顶部的标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.drawpic_layout);
		// mPaintView = new PaintView(this);
		mPaintView = (PaintView) findViewById(R.id.palette);

		// 取得窗口属性
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		g = (float) (screenWidth/360.0);
		// 初始化所有内容
		initViews();
		// 取画布的宽高，通过对应layout的背景图片
		da = getResources().getDrawable(R.drawable.bottom_bar);
		da1 = getResources().getDrawable(R.drawable.textnote_title_bg_1);
		BITMAP_WIDTH = screenWidth;
		BITMAP_HEIGHT = screenHeight - da.getIntrinsicHeight()
				- da1.getIntrinsicHeight();

		// 判断新建还是修改
		editPicPath = getIntent().getStringExtra("editPic");
		if (editPicPath != null) {
			mPaintView.setHasBgbm(true);
			mPaintView.init(this, BITMAP_WIDTH, BITMAP_HEIGHT, editPicPath);
		} else {
			mPaintView.init(this, BITMAP_WIDTH, BITMAP_HEIGHT);
		}
		// 初始化画布等属性
		mPaintView.setScreenHeight(screenHeight);
		mPaintView.setMenuHeight(da1.getIntrinsicHeight());
		mPaintView.setScreenWidth(screenWidth);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "退出");
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
		// 回收bitmap空间
		if (importBmp != null && !importBmp.isRecycled()) {
			importBmp.recycle();
		}
	}

	/**
	 * @see 自定义的单击触发器根据R.id来做出选择
	 * */

	private OnClickListener mBtnClickListener = new OnClickListener() {

		public void onClick(View paramView) {
			switch (paramView.getId()) {
			// 不清楚
			case R.id.tuya_view:
				break;
			// 工具按钮点击，后弹出画笔选择*************
			case R.id.toolbtns:

				break;
			// 后退按钮
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
			// 顶部后退按钮
			case R.id.paint_back_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				mPaintView.setBackPressed(true);
				mPaintView.invalidate();
				break;

			// 顶部前进按钮
			case R.id.paint_forward_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				mPaintView.setForwardPressed(true);
				mPaintView.invalidate();
				break;

			// 右上角保存按钮
			case R.id.tuya_save_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				//提示框
				final ProgressDialog progressDialog=new ProgressDialog(DrawPicActivity.this);
				
				progressDialog.setMessage("正在保存...");
				//显示提示框
				progressDialog.show();
				
				//接收线程结束的消息
				final Handler handler = new Handler() {
					public void handleMessage(Message message) {
						progressDialog.dismiss();
					}
				};
				
				//开启线程保存图片
				new Thread(){
					public void run(){
						String picString = savePic();
						// 如果是新建一个张涂鸦则将图片名返回给addnoteactivity,否则直接退出
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

			// 工具按钮点击，后弹出画笔选择**********
			case R.id.tuya_star_btn:
				if (mPopupCenterMenu.getVisibility() == 8) {
					hideColorPicker();
					hideGraphPicker();
					showPopupCenterMenu();
				} else {
					hidePopuCenterMenu();
				}
				break;
			// 清空按钮 清除画布上所有的东西
			case R.id.tuya_clear_btn:
				hidePopuCenterMenu();
				hideColorPicker();
				hideGraphPicker();
				mPaintView.clearAll();
				mPaintView.invalidate();
				checkButtonState();
				break;

			// 颜色选择按钮，点击后可选择
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
			// 工具栏中 的 1号 原始画笔
			case R.id.tuya_pen1_btn:
				mPaintView.setCurrentPenType(1);
				refreshToolboxItemsState(1);
				break;
			// 工具栏中 的 2号 水彩画笔
			case R.id.tuya_pen2_btn:
				mPaintView.setCurrentPenType(2);
				refreshToolboxItemsState(2);
				break;

			// 工具栏中 的3号 涂料画笔
			case R.id.tuya_pen3_btn:
				mPaintView.setCurrentPenType(3);
				refreshToolboxItemsState(3);
				break;

			case R.id.tuya_bg_import:
				Builder localBuilder = new Builder(DrawPicActivity.this);
				localBuilder.setTitle("设置背景");
				localBuilder.setIcon(android.R.drawable.stat_sys_warning);
				String[] arrayOfString = new String[3];
				arrayOfString[0] = "清空背景";
				arrayOfString[1] = "从相册导入图片";
				arrayOfString[2] = "拍照";
				localBuilder.setItems(arrayOfString,
						new bgImportOnclickListener());
				localBuilder.setNegativeButton("取消", null);
				localBuilder.show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @see 画笔粗细滚动条seekbar的监听器 定义以及重写方法
	 * */
	private OnSeekBarChangeListener penSizeListener = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			// 动态显示textview 画笔粗细大小
			seekBar.setProgress(progress);
			resultPenSize.setText(progress + "dp");
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			int currentSize = seekBar.getProgress();
			// 控制画笔粗细大小不为零
			if (currentSize == 0) {
				currentSize = 1;
			}
			mPaintView.setCurrentSize(currentSize);

		}
	};

	/**
	 * @see 初始化所有布局中的组件，并设置对应组件的监听器
	 * */
	private void initViews() {
		// 初始化设置各个按钮的监听器
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

		// 初始化各个按钮，并放入数组中，以便修改其是否选中
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
		// 设置seekbar pensize滚动条，动态设置画笔粗细
		penSizeSeekBar = (SeekBar) findViewById(R.id.seek_bar_pensize);
		penSizeSeekBar.setOnSeekBarChangeListener(penSizeListener);
		resultPenSize = (TextView) findViewById(R.id.result_pensize);
		resultPenSize.setText(penSizeSeekBar.getProgress() + "dp");
	}

	/**
	 * 
	 * @see 根据日期保存图片
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
	 * @see 根据画笔的粗细对 工具栏中选择情况刷新,被选中的“打勾”
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

	// 弹出工具栏
	/**
	 * @see 显示画笔选择栏和粗细滚动条
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
	 * @see 隐藏画笔选择栏和粗细滚动条
	 * */
	public void hidePopuCenterMenu() {
		if (mPopupCenterMenu.getVisibility() != 8) {
			this.mPopupCenterMenu.setVisibility(8);
			this.mPopupCenterMenu.startAnimation(AnimationUtils.loadAnimation(
					this, R.anim.fade_out_fast));
		}
	}

	/**
	 * @see 颜色选择器是否在显示状态， 此方法为了避免mColorPickerLayout空指针而设置的判断
	 * */
	protected boolean isColorPickerShowing() {
		return (this.mColorPickerLayout != null)
				&& (this.mColorPickerLayout.getVisibility() == 0);
	}

	/**
	 * @see 隐藏颜色选择器
	 * */
	public void hideColorPicker() {
		if (isColorPickerShowing()) {
			this.mColorPickerLayout.setVisibility(4);
			this.mColorPickerLayout.startAnimation(AnimationUtils
					.loadAnimation(this, R.anim.fade_out_fast));
		}
	}

	/**
	 * @see 显示颜色选择器或刷新颜色选择器状态
	 * */
	public void showColorPicker(int paramInt) {
		if (this.mColorPickerLayout == null) {
			// mColorPickerLayout不为空后，对mColorPickerLayout和它的子控件mColorPicker初始化
			this.mColorPickerLayout = findViewById(R.id.image_gallery_layout);
			this.mColorPicker = ((GridView) findViewById(R.id.image_gallery));
			this.mColorPicker.setGravity(3);
			// 设置adapter
			ImageAdapter localImageAdapter = new ImageAdapter(this);
			this.mColorPicker.setAdapter(localImageAdapter);
			this.mColorPicker.setOnItemClickListener(null);

			// 颜色边框等相关参数设置
			int j = (int) (60.0F * g);
			this.mColorPicker.getLayoutParams().width = (j
					* localImageAdapter.getCount() + (int) (5.0F * g));

		}
		// 找出当前的画笔颜色ID
		for (int i = 0; i < COLOR_IDS.length; i++) {
			if (COLOR_IDS[i] == paramInt) {
				this.mSelectedId = i;
				break;
			}
		}
		// 最后显示
		this.mColorPickerLayout.setVisibility(0);
		this.mColorPickerLayout.startAnimation(AnimationUtils.loadAnimation(
				this, R.anim.fade_in_fast));
	}

	/**
	 * @see 颜色选择器中adapter的定义
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
			// 初始化layout
			FrameLayout localFrameLayout = new FrameLayout(this.mContext);
			FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
					-1, -1);

			// 设置每个颜色的框框
			ImageView localImageView1 = new ImageView(this.mContext);
			localFrameLayout
					.setBackgroundResource(R.drawable.selector_hw_color_picker_btn);
			// 设置框框里面颜色，和位置
			localImageView1.setImageDrawable(new ColorDrawable(
					DrawPicActivity.COLOR_IDS[paramInt]));
			localImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
			localImageView1.setImageMatrix(new Matrix());
			localLayoutParams1.setMargins(3, 3, 3, 3);
			localLayoutParams1.gravity = 17;
			// 把框框加到layout里面
			localFrameLayout.addView(localImageView1, localLayoutParams1);
			ImageView localImageView2 = new ImageView(this.mContext);
			// 设置框框的ID
			localFrameLayout.setId(paramInt);
			localImageView1.setMinimumHeight(10 + localImageView1.getWidth());
			localImageView2.setImageResource(R.drawable.tool_checked);
			// 设置监听器
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
			// 设置每个颜色框框都加上 已选择中的图片localImageView2
			FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
					-2, -2);
			localLayoutParams2.gravity = 85;
			localFrameLayout.addView(localImageView2, localLayoutParams2);
			int i = (int) (10.0F * g);
			localFrameLayout.setLayoutParams(new AbsListView.LayoutParams(
					i * 5, i * 5));
			localImageView2.setVisibility(4);
			// 如果与mSelectedId当前的画笔颜色一致就设置为显示
			if (DrawPicActivity.this.mSelectedId == paramInt) {
				localImageView2.setVisibility(0);
			}
			return localFrameLayout;
		}
	}

	// graph_drawing 弹出框 开始

	/**
	 * @see 显示图形选择器或刷新图形选择器状态
	 * */
	public void showGraphPicker(int paramInt) {
		if (this.mGraphPickerLayout == null) {
			// mGraphPickerLayout不为空后，对mGraphPickerLayout和它的子控件mGraphPicker初始化
			this.mGraphPickerLayout = findViewById(R.id.graph_drawing_layout);
			this.mGraphPicker = ((GridView) findViewById(R.id.graph_drawing));
			this.mGraphPicker.setGravity(3);
			// 设置adapter
			GraphAdapter localImageAdapter = new GraphAdapter(this);
			this.mGraphPicker.setAdapter(localImageAdapter);
			this.mGraphPicker.setOnItemClickListener(null);

			// 边框等相关参数设置
			int j = (int) (60.0F * g);
			this.mGraphPicker.getLayoutParams().width = (j
					* localImageAdapter.getCount() + (int) (5.0F * g));

		}
		// 找出当前的画笔图形ID
		this.mSelectedId2 = paramInt;
		// 最后显示
		this.mGraphPickerLayout.setVisibility(0);
		this.mGraphPickerLayout.startAnimation(AnimationUtils.loadAnimation(
				this, R.anim.fade_in_fast));
	}

	/**
	 * @see 图形选择器是否在显示状态， 此方法为了避免mGraphPickerLayout空指针而设置的判断
	 * */
	protected boolean isGraphPickerShowing() {
		return (this.mGraphPickerLayout != null)
				&& (this.mGraphPickerLayout.getVisibility() == 0);
	}

	/**
	 * @see 隐藏图形选择器
	 * */
	public void hideGraphPicker() {
		if (isGraphPickerShowing()) {
			this.mGraphPickerLayout.setVisibility(4);
			this.mGraphPickerLayout.startAnimation(AnimationUtils
					.loadAnimation(this, R.anim.fade_out_fast));
		}
	}

	/**
	 * @see 图形选择器中adapter的定义
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
			// 初始化layout
			FrameLayout localFrameLayout = new FrameLayout(this.mContext);
			FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(
					-1, -1);

			// 设置每个框框
			ImageView localImageView1 = new ImageView(this.mContext);
			localFrameLayout
					.setBackgroundResource(R.drawable.selector_hw_color_picker_btn);
			// 设置框框里面图案
			localImageView1.setImageResource(GRAPH_ID[paramInt]);
			localImageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
			localLayoutParams1.setMargins(3, 3, 3, 3);
			localLayoutParams1.gravity = 17;
			// 把框框加到layout里面
			localFrameLayout.addView(localImageView1, localLayoutParams1);
			ImageView localImageView2 = new ImageView(this.mContext);
			// 设置框框的ID
			localFrameLayout.setId(paramInt);
			localImageView1.setMinimumHeight(10 + localImageView1.getWidth());
			localImageView2.setImageResource(R.drawable.tool_checked);
			// 设置监听器
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
			// 设置每个框框都加上 已选择中的图片localImageView2
			FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(
					-2, -2);
			localLayoutParams2.gravity = 85;
			localFrameLayout.addView(localImageView2, localLayoutParams2);
			int i = (int) (10.0F * g);
			localFrameLayout.setLayoutParams(new AbsListView.LayoutParams(
					i * 5, i * 5));
			localImageView2.setVisibility(4);
			// 如果与mSelectedId2当前的画笔形状一致就设置为显示
			if (DrawPicActivity.this.mSelectedId2 == paramInt) {
				localImageView2.setVisibility(0);
			}
			return localFrameLayout;
		}
	}

	// graph_drawing 弹出框 结束

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
					builder.setTitle("图片操作");
					builder.setIcon(android.R.drawable.stat_sys_warning);
					builder.setMessage("是否裁剪图片？");
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
					builder.setPositiveButton("是", tmpListener);
					builder.setNegativeButton("否", tmpListener);
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
				builder.setTitle("相片操作");
				builder.setIcon(android.R.drawable.stat_sys_warning);
				builder.setMessage("是否裁剪相片？");
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
				builder.setPositiveButton("是", tmpListener);
				builder.setNegativeButton("否", tmpListener);
				builder.show();
				
				
			}
		}
	}

	public void checkPicSize(Bitmap bmp, Intent intent) {
		if (bmp.getWidth() > bmp.getHeight()) {
			intent.putExtra("aspectX", BITMAP_HEIGHT);// 裁剪框比例
			intent.putExtra("aspectY", BITMAP_WIDTH);
		} else {
			intent.putExtra("aspectX", BITMAP_WIDTH);// 裁剪框比例
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
