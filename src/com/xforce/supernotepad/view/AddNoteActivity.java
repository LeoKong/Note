package com.xforce.supernotepad.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.xforce.supernotepad.dao.NoteDetailDao;
import com.xforce.supernotepad.dao.PictureDao;
import com.xforce.supernotepad.dao.VideoDao;
import com.xforce.supernotepad.dao.VoiceDao;
import com.xforce.supernotepad.model.NoteDetail;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.AlarmSetting;
import com.xforce.supernotepad.util.FacePopView;
import com.xforce.supernotepad.util.FileUtil;
import com.xforce.supernotepad.util.GalleryPicAdapter;
import com.xforce.supernotepad.util.SetTimeBuilder;
import com.xforce.supernotepad.util.Utils;

public class AddNoteActivity extends Activity implements OnClickListener,
		OnGesturePerformedListener {
	private static final int PIC = 0;
	private static final int VID = 5;
	protected static final int SURE_DELE_PIC = 1;
	protected static final int SURE_EXIT = 2;
	protected static final int RECORDING = 3;
	protected static final int SURE_DEL_VOI = 4;
	protected static final int SURE_DEL_VID = 6;
	protected static final int SURE_EDIT_PIC = 10;
	protected static final int CREATE_PASSWD = 11;
	// protected static final int REMIND_SETTING = 12;
	protected static final int PROGRESSING = 13;

	// all in here 3
	protected static final int IMPORT_PIC = 7;// 导入图片requestcode
	protected static final int MODIFY_PIC = 8;// 修改图片
	protected static final int TAKE_PIC = 9;// 拍照
	DisplayMetrics dm = new DisplayMetrics();
	Uri originalUri = null;// 从相册中取出相片的URI地址
	Bitmap importBmp = null; // 导入或拍照时候的临时bitmap
	File sdcardTempFile = null;// 临时文件
	String importPicName = null;// 当前插入图片的名称
	String fileUrl = null;// 图片的地址
	// all in here end 3

	public TextView backButton, saveButton;// 返回按钮，保存按钮
	private ImageButton faceButton, writingButton, drawpicButton,
			importpicButton, recorederButton, videoButton, lockButton,
			remindButton;
	public EditText contentEdit;// 文字内容输入栏
	public LinearLayout voiceListLayout, videoListLayout;

	public TextView remindTimeTextView;// 显示用户设置的提醒时间

	public GestureOverlayView gestureOverlayView;// 手写输入的layout
	GestureLibrary gestureLibrary;// 实例化识别库
	public TextView completeWriteButton;// 完成手写输入按钮
	RelativeLayout writingTileLayout;// 手写输入时标题栏
	boolean isWriting = false;// 当前是否正在手写输入

	private MediaRecorder voiceRecorder = null;// 录音器
	private MediaPlayer voicePlayer = null;// 播放录音器
	boolean isVoicePlaying = false;// 记录当前是否正在播放录音

	private FacePopView facePopView;// 表情选择框

	public List<PictureModel> picList = new ArrayList<PictureModel>();// 临时保存已添加的图片列表名
	public List<String> voiList = new ArrayList<String>();// 临时保存已添加的录音列表名
	public List<String> videoList = new ArrayList<String>();// 临时保存已添加的视频列表明名
	public String nowRecored = null;// 当前录音的文件名

	public int selectedMedia = 0;// 当前长按选中的多媒体
	public int locked = 0;// 是否需要加锁
	public SharedPreferences sharedPreferences = null;// 系统设置

	// 所有画廊中的相片的URI保存在这里
	private LinearLayout galleryLayout;// 套着画廊的layout
	private Gallery gallery;// 画廊实体
	public static int GALLERY_INDEX = 0;// 画廊所显示的标志
	private File tmpFile;// 临时文件 保存 相册或拍照的图片

	private SetTimeBuilder setTimeBuilder;
	public int[] Rdate;// 提醒时间

	// 表情数组
	int[] imageId = new int[] { R.drawable.face01, R.drawable.face02,
			R.drawable.face03, R.drawable.face04, R.drawable.face05,
			R.drawable.face06, R.drawable.face07, R.drawable.face08,
			R.drawable.face09, R.drawable.face10, R.drawable.face11,
			R.drawable.face12, R.drawable.face13, R.drawable.face14,
			R.drawable.face15, R.drawable.face16, R.drawable.face17,
			R.drawable.face18, R.drawable.face19, R.drawable.face20,
			R.drawable.face21, R.drawable.face22, R.drawable.face23,
			R.drawable.face24, R.drawable.face25, R.drawable.face26,
			R.drawable.face27, R.drawable.face28, R.drawable.face29,
			R.drawable.face30, R.drawable.face31, R.drawable.face32,
			R.drawable.face33, R.drawable.face34, R.drawable.backspace };

	// 闹钟时间设置
	public String remindString = null;
	// 是否设置提醒
	boolean needRemind = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_layout);

		// 初始化控件
		initWidget();

		// 读取系统设置
		sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (gallery != null) {
			// 当从其它activity返回时，设置gallery显示对应的图片，达到同步，两边都显示同一张、同一位置的图片
			gallery.setSelection(GALLERY_INDEX);
			initGallery();
		}

		// 开启友盟统计
		MobclickAgent.onResume(this);

		// 回收bitmap空间
		// if (importBmp != null && !importBmp.isRecycled()) {
		// importBmp.recycle();
		// }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == PIC) {

				String picName = data.getStringExtra("pic");
				if (picName != null) {
					// 添加图片result
					PictureModel pictureModel = new PictureModel();
					pictureModel.setpName(picName);
					picList.add(pictureModel);
					// addPicOnNote(picName, true);
					GALLERY_INDEX = picList.size() - 1;
					// 刷新画廊
					initGallery();

				} else {
					// 编辑图片result
					// 刷新显示修改后的图片列表
					// refreshImageList();

					// 刷新画廊
					initGallery();

				}

			}
			// 当选择导入图片
			else if (requestCode == IMPORT_PIC) {
				ContentResolver resolver = getContentResolver();
				originalUri = data.getData();// 取得相册图片的URI
				// 通过URI将图片转换成bitmap
				try {
					importBmp = MediaStore.Images.Media.getBitmap(resolver,
							originalUri);
					showDialog(SURE_EDIT_PIC);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 当设置了对图片或者拍摄的图片进行修改，进行如下操作
			} else if (requestCode == MODIFY_PIC) {
				// 从裁剪完后的图片文件导回bitmap
				importBmp = BitmapFactory.decodeFile(sdcardTempFile
						.getAbsolutePath());
				// // 对bitmap进行比例压缩，设置压缩比例，按屏幕的大小进行压缩
				// float scaleWidth = 0.5f;
				// float scaleHeight = scaleWidth;
				// Matrix m = new Matrix();
				// m.postScale(scaleWidth, scaleHeight);
				// // 这里压缩
				// importBmp = Bitmap.createBitmap(importBmp, 0, 0,
				// importBmp.getWidth(), importBmp.getHeight(), m, true);
				// FileOutputStream fos;
				// try {
				// // 文件流将bitmap写回原来的文件中去
				// fos = new FileOutputStream(sdcardTempFile);
				// importBmp.compress(CompressFormat.PNG, 100, fos);
				// fos.flush();
				// fos.close();
				// } catch (FileNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// } catch (IOException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// PictureModel pictureModel = new PictureModel();
				// pictureModel.setpName(importPicName);
				// picList.add(pictureModel);
				// // addPicOnNote(importPicName, true);
				// // 刷新画廊
				// initGallery();
				// // 选中新添加的图片
				// GALLERY_INDEX = picList.size() - 1;
				showDialog(PROGRESSING);

				new PicProgressThread().start();

				// 当选择了拍照时候
			} else if (requestCode == TAKE_PIC) {
				// 将拍照后保存的图片文件转回bitmap

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 3;// 将图片压缩显示

				importBmp = BitmapFactory.decodeFile(
						sdcardTempFile.getAbsolutePath(), options);
				showDialog(SURE_EDIT_PIC);
			}
		}

		if (requestCode == VID) {
			if (resultCode == RESULT_OK) {
				Uri uri = data.getData();
				Cursor cursor = this.getContentResolver().query(uri, null,
						null, null, null);
				if (cursor.moveToNext()) {
					// 系统默认指定保存的路径
					String videoPath = cursor.getString(cursor
							.getColumnIndex("_data"));
					// 给予一个文件名
					String newVideoName = "vid_" + Utils.getNowDate() + ".mp4";
					File oldFile = new File(videoPath);
					File newFile = new File(Utils.VID_PATH + newVideoName);

					try {
						// 将文件复制到指定路径
						FileUtils.copyFile(oldFile, newFile);
						// 将原来的删除
						FileUtil.deleteMediaInSdcard(oldFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 添加显示view
					addVidOnNote(newVideoName, AddNoteActivity.this);
					videoList.add(newVideoName);
				}
			}
		}

	}

	/**
	 * 刷新图片列表
	 */
	// public void refreshImageList() {
	// imageListLayout.removeAllViews();
	// for (int i = 0; i < picList.size(); i++) {
	// addPicOnNote(picList.get(i).getpName(), false);
	// }
	//
	// }

	/**
	 * 动态显示用户添加的图片view
	 * 
	 * @param picName
	 *            图片文件名
	 * @param hasAnima
	 *            是否需要添加动画
	 */
	// public void addPicOnNote(final String picName, boolean hasAnima) {
	//
	// final String filePath = Utils.PIC_PATH + picName;
	//
	// ImageView imageView = new ImageView(AddNoteActivity.this);
	// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	// params.setMargins(10, 10, 10, 10);
	// imageView.setLayoutParams(params);
	//
	// BitmapFactory.Options options = new BitmapFactory.Options();
	// options.inSampleSize = 2;// 将比例压缩显示
	// Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
	// imageView.setImageBitmap(bitmap);
	// imageView.setClickable(true);
	// // 点击事件
	// imageView.setOnClickListener(new OnClickListener() {
	//
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	// // if (picName.contains("ipic")) {
	// Intent intent = new Intent(AddNoteActivity.this,
	// ShowOnePicActivity.class);
	// intent.putExtra("picPath", Utils.PIC_PATH + picName);
	// startActivity(intent);
	// overridePendingTransition(R.anim.alpha_scale_in, 0);
	// // } else {
	// // Intent intent = new Intent(AddNoteActivity.this,
	// // DrawPicActivity.class);
	// // intent.putExtra("editPic", filePath);
	// // startActivityForResult(intent, PIC);
	// //
	// // }
	//
	// }
	// });
	// imageView.setLongClickable(true);
	// // 长按事件
	// imageView.setOnLongClickListener(new OnLongClickListener() {
	//
	// @SuppressWarnings("deprecation")
	// public boolean onLongClick(View v) {
	// // TODO Auto-generated method stub
	// // 遍历出该图片在list中的位置
	// getLongSelectedPicture(picList, picName);
	//
	// pictureOprDialog("请选择操作：");
	// return false;
	// }
	// });
	//
	// // 添加动画效果
	// if (hasAnima) {
	// imageView.startAnimation(AnimationUtils.loadAnimation(this,
	// R.anim.alpha_scale_in));
	// }
	// imageListLayout.addView(imageView);
	//
	// }

	/**
	 * 动态添加显示录音view
	 * 
	 * @param vName
	 *            录音名
	 * @param context
	 */
	public void addVoiOnNote(final String vName, final Context context) {

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {

				voiceListLayout.addView((View) message.obj);
			}
		};

		new Thread() {
			public void run() {
				LinearLayout voiceView = new LinearLayout(context);
				voiceView.setPadding(10, 10, 10, 10);
				voiceView.setGravity(Gravity.CENTER);
				final ImageButton button = new ImageButton(context);
				TextView textView = new TextView(context);

				textView.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT));
				textView.setText(vName);
				textView.setTextSize(18);
				textView.setTextColor(Color.BLACK);
				textView.setBackgroundColor(Color.GRAY);
				textView.setPadding(10, 10, 10, 10);
				textView.setSingleLine(true);
				textView.setGravity(Gravity.CENTER);

				button.setBackgroundResource(R.color.sky);
				button.setImageResource(R.drawable.voice_play);
				button.setOnClickListener(new OnClickListener() {

					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (isVoicePlaying) {
							stopPlaying(button);
						} else {
							startPlaying(vName, button);
						}
					}
				});

				voiceView.setClickable(true);
				voiceView.setOnLongClickListener(new OnLongClickListener() {

					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						// 遍历出该录音在list中的位置
						getLongSelectedMedia(voiList, vName);

						showDialog(SURE_DEL_VOI);
						return false;
					}
				});

				voiceView.addView(button);
				voiceView.addView(textView);

				// 添加动画效果
				voiceView.startAnimation(AnimationUtils.loadAnimation(
						AddNoteActivity.this, R.anim.alpha_scale_in));

				Message message = handler.obtainMessage(0, voiceView);
				handler.sendMessage(message);
			}
		}.start();

	}

	/**
	 * 动态添加显示视频view
	 * 
	 * @param vidName
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public void addVidOnNote(final String vidName, final Context context) {

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {

				videoListLayout.addView((View) message.obj);
			}
		};

		new Thread() {
			public void run() {

				ImageButton imageButton = new ImageButton(context);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(10, 10, 10, 10);

				imageButton.setLayoutParams(params);

				// 获取图片的缩略图
				BitmapDrawable drawable = new BitmapDrawable(getVideoThumbnail(
						Utils.VID_PATH + vidName, 500, 500,
						MediaStore.Images.Thumbnails.MICRO_KIND));
				imageButton.setBackgroundDrawable(drawable);
				imageButton.setImageResource(R.drawable.voice_play);

				imageButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						// 调用系统播放器
						Intent intent = new Intent(Intent.ACTION_VIEW);
						Uri uri = Uri.parse("file:///sdcard/SuperNote/Videos/"
								+ vidName);
						intent.setDataAndType(uri, "video/mp4");
						startActivity(intent);
					}
				});

				imageButton.setOnLongClickListener(new OnLongClickListener() {

					public boolean onLongClick(View v) {
						// TODO Auto-generated method stub
						// 遍历出该录音在list中的位置
						getLongSelectedMedia(videoList, vidName);

						showDialog(SURE_DEL_VID);
						return false;
					}
				});

				// 添加动画效果
				imageButton.startAnimation(AnimationUtils.loadAnimation(
						AddNoteActivity.this, R.anim.alpha_scale_in));

				Message message = handler.obtainMessage(0, imageButton);
				handler.sendMessage(message);

			}
		}.start();
	}

	/**
	 * 获取视频的缩略图。先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return
	 */
	public Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {

		Bitmap bitmap = null;
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 找出长按选择的多媒体文件，并将其所在list的索引号赋值给selectedMedia
	 * 
	 * @param list
	 * @param selectName
	 */
	public void getLongSelectedMedia(List<String> list, String selectName) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(selectName)) {
				selectedMedia = i;
				break;
			}
		}
	}

	/**
	 * 找出长按选择的图片，并将其所在list的索引号赋值给selectedMedia
	 * 
	 * @param list
	 * @param selectName
	 */
	public void getLongSelectedPicture(List<PictureModel> picList,
			String selectName) {
		for (int i = 0; i < picList.size(); i++) {
			if (picList.get(i).getpName().equals(selectName)) {
				selectedMedia = i;
				break;
			}
		}
	}

	/**
	 * 初始化控件
	 */
	public void initWidget() {
		backButton = (TextView) findViewById(R.id.backbtn);
		saveButton = (TextView) findViewById(R.id.savebtn);
		faceButton = (ImageButton) findViewById(R.id.facebtn);
		drawpicButton = (ImageButton) findViewById(R.id.drawpicbtn);
		writingButton = (ImageButton) findViewById(R.id.writingbtn);
		importpicButton = (ImageButton) findViewById(R.id.importpicbtn);
		recorederButton = (ImageButton) findViewById(R.id.recorderbtn);
		videoButton = (ImageButton) findViewById(R.id.videobtn);
		lockButton = (ImageButton) findViewById(R.id.lockbtn);
		remindButton = (ImageButton) findViewById(R.id.remindbtn);
		completeWriteButton = (TextView) findViewById(R.id.compelete_writing_btn);

		contentEdit = (EditText) findViewById(R.id.contentedit);
		remindTimeTextView = (TextView) findViewById(R.id.remindtime_tv);
		galleryLayout = (LinearLayout) findViewById(R.id.gallery_layout);
		gallery = (Gallery) findViewById(R.id.gallery);
		voiceListLayout = (LinearLayout) findViewById(R.id.voice_list_add);
		videoListLayout = (LinearLayout) findViewById(R.id.video_list_add);
		gestureOverlayView = (GestureOverlayView) findViewById(R.id.gesture);
		writingTileLayout = (RelativeLayout) findViewById(R.id.title_write_input);

		backButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		completeWriteButton.setOnClickListener(this);

		faceButton.setOnClickListener(this);
		writingButton.setOnClickListener(this);
		drawpicButton.setOnClickListener(this);
		importpicButton.setOnClickListener(this);
		recorederButton.setOnClickListener(this);
		videoButton.setOnClickListener(this);
		lockButton.setOnClickListener(this);
		remindButton.setOnClickListener(this);

		// 为识别库添加监听器
		gestureOverlayView.addOnGesturePerformedListener(this);
		// 设置识别库来自的文件位置
		gestureLibrary = GestureLibraries.fromRawResource(AddNoteActivity.this,
				R.raw.gestures);

		if (!gestureLibrary.load()) {
			Toast.makeText(AddNoteActivity.this, "手写库导入失败！", Toast.LENGTH_LONG)
					.show();
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddNoteActivity.this);
		LinearLayout linearLayout = null;
		switch (id) {
		case SURE_DELE_PIC:
			builder.setTitle("删除该图片？");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int which) {
							// TODO Auto-generated method stub
							// 从临时列表中删除
							picList.remove(selectedMedia);

							GALLERY_INDEX--;
							// 从屏幕中删除
							// imageListLayout.removeViewAt(selectedMedia);

							//
							initGallery();

							for (int i = 0; i < picList.size(); i++) {
								System.out.println("剩下的" + picList.get(i));
							}
						}
					});
			builder.setNegativeButton("否", null);
			return builder.create();

		case SURE_DEL_VOI:
			builder.setTitle("删除该录音？");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 从临时列表中删除
							voiList.remove(selectedMedia);
							// 从屏幕中删除
							voiceListLayout.removeViewAt(selectedMedia);
							for (int i = 0; i < voiList.size(); i++) {
								System.out.println("剩下的" + voiList.get(i));
							}
						}
					});

			builder.setNegativeButton("否", null);
			return builder.create();

		case SURE_DEL_VID:
			builder.setTitle("删除该视频？");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whilch) {
							// TODO Auto-generated method stub
							// 从临时列表中删除
							videoList.remove(selectedMedia);
							// 从屏幕中删除
							videoListLayout.removeViewAt(selectedMedia);

						}
					});
			builder.setNegativeButton("否", null);
			return builder.create();

		case SURE_EXIT:
			builder.setTitle("记事本将不被保存，确定退出？");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method
							// 由于添加多媒体文件已经在sdcard中，所以现在要将其删除
							FileUtil.deleteMediaInSdcard(picList);
							FileUtil.deleteMediaInSdcard(voiList,
									Utils.VOI_PATH);

							AddNoteActivity.this.finish();
						}
					});
			builder.setNegativeButton("否", null);
			return builder.create();

		case CREATE_PASSWD:
			builder.setTitle("创建密码");
			builder.setCancelable(false);
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.createpasswd_dialog_layout, null);
			final EditText passwdEditText = (EditText) linearLayout
					.findViewById(R.id.addpasswd_edit);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String passwdString = passwdEditText.getText()
									.toString();
							if (passwdString.length() < 5) {
								passwdEditText.setError("密码太短！");
								keepDialog(dialog);
							} else {
								SharedPreferences sp = getSharedPreferences(
										"setting", MODE_PRIVATE);
								Utils.savePasswd(sp, passwdString);
								Toast.makeText(AddNoteActivity.this, "密码创建成功",
										Toast.LENGTH_SHORT).show();
								destoryDialog(dialog);
							}
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							destoryDialog(dialog);
						}
					});
			builder.setView(linearLayout);

			return builder.create();

		case SURE_EDIT_PIC:

			builder.setTitle("是否裁剪图片？");
			builder.setCancelable(false);
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 以下是修改图片前的准备工作
							Intent intent = new Intent(
									"com.android.camera.action.CROP");
							intent.setDataAndType(originalUri, "image/*");
							intent.putExtra("crop", "true");

							// 确定最终图片的名称和地址并创建该图片文件
							/* 加个确定 tempfile 的方法 */
							importPicName = "ipic_" + Utils.getNowDate()
									+ ".png";
							fileUrl = Utils.PIC_PATH + importPicName;
							sdcardTempFile = new File(fileUrl);
							try {
								// 避免空指针 先建立该图片
								sdcardTempFile.createNewFile();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							// 确定裁剪完图片后图片保存的位置
							intent.putExtra("output",
									Uri.fromFile(sdcardTempFile));
							// 跳去修改
							startActivityForResult(intent, MODIFY_PIC);
						}
					});
			builder.setNegativeButton("否",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 确定最终图片文件名称以及地址
							importPicName = "ipic_" + Utils.getNowDate()
									+ ".png";
							fileUrl = Utils.PIC_PATH + importPicName;
							sdcardTempFile = new File(fileUrl);
							// 显示提示框
							showDialog(PROGRESSING);
							// 开启处理导入的图片
							new PicProgressThread().start();
						}
					});

			return builder.create();

		case PROGRESSING:

			return ProgressDialog.show(this, null, "正在处理...", true);

		case RECORDING:
			final ProgressDialog recordingDialog = new ProgressDialog(
					AddNoteActivity.this);
			recordingDialog.setCancelable(false);
			recordingDialog.setMessage("正在录音...");
			recordingDialog.setButton("完成",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 停止录音
							stopRecording();
							recordingDialog.dismiss();
							// 保存在临时列表中
							voiList.add(nowRecored);
							// 进行显示
							addVoiOnNote(nowRecored, AddNoteActivity.this);
						}
					});

			recordingDialog.setButton2("取消",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							recordingDialog.dismiss();
							// 停止录音并将其从sdcard中删除
							stopRecording();
							FileUtil.deleteMediaInSdcard(new File(
									Utils.VOI_PATH + nowRecored));
						}
					});
			recordingDialog.show();
			return recordingDialog;

			// case REMIND_SETTING:
			// builder.setTitle("选择提醒时间：");
			// linearLayout = (LinearLayout) getLayoutInflater().inflate(
			// R.layout.datetime_setting_laytou, null);
			// DatePicker datePicker = (DatePicker) linearLayout
			// .findViewById(R.id.date_picker);
			// TimePicker timePicker = (TimePicker) linearLayout
			// .findViewById(R.id.time_picker);
			// final TextView showdateTextView = (TextView) linearLayout
			// .findViewById(R.id.showdate_tv);
			// // 获取当前时间信息
			// final Calendar calendar = Calendar.getInstance();
			// mYear = calendar.get(Calendar.YEAR);
			// mMonth = calendar.get(Calendar.MONTH);
			// mDay = calendar.get(Calendar.DAY_OF_MONTH);
			// mHour = calendar.get(Calendar.HOUR_OF_DAY);
			// mMinute = calendar.get(Calendar.MINUTE);
			//
			// remindString = mYear + "年" + formatTime((mMonth + 1)) + "月"
			// + formatTime(mDay) + "日 " + formatTime(mHour) + ":"
			// + formatTime(mMinute);
			//
			// showdateTextView.setText(remindString);
			//
			// // 初始化日期选择器，并设置监听
			// datePicker.init(mYear, mMonth, mDay, new OnDateChangedListener()
			// {
			//
			// public void onDateChanged(DatePicker arg0, int year, int month,
			// int day) {
			// // TODO Auto-generated method stub
			// calendar.set(year, month, day);
			// mYear = year;
			// mMonth = month;
			// mDay = day;
			//
			// remindString = mYear + "年" + formatTime((mMonth + 1)) + "月"
			// + formatTime(mDay) + "日 " + formatTime(mHour) + ":"
			// + formatTime(mMinute);
			// showdateTextView.setText(remindString);
			// }
			// });
			//
			// // 设置时间监听
			// timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			//
			// public void onTimeChanged(TimePicker view, int hourOfDay,
			// int minute) {
			// // TODO Auto-generated method stub
			// calendar.set(mYear, mMonth, mDay, hourOfDay, minute);
			// mHour = hourOfDay;
			// mMinute = minute;
			//
			// remindString = mYear + "年" + formatTime((mMonth + 1)) + "月"
			// + formatTime(mDay) + "日 " + formatTime(mHour) + ":"
			// + formatTime(mMinute);
			// showdateTextView.setText(remindString);
			// }
			// });
			//
			// builder.setPositiveButton("设置",
			// new DialogInterface.OnClickListener() {
			//
			// public void onClick(DialogInterface dialog, int which) {
			// // TODO Auto-generated method stub
			// if (Utils.checkOverdue(remindString)) {
			// Toast.makeText(AddNoteActivity.this,
			// "设置的时间已过期！", Toast.LENGTH_LONG).show();
			// } else {
			// needRemind = true;
			// remindTimeTextView
			// .setText("提醒:" + remindString);
			// }
			//
			// }
			// });
			//
			// builder.setNegativeButton("取消闹钟",
			// new DialogInterface.OnClickListener() {
			//
			// public void onClick(DialogInterface dialog, int which) {
			// // TODO Auto-generated method stub
			// needRemind = false;
			// remindTimeTextView.setText("");
			// }
			// });
			//
			// builder.setView(linearLayout);
			// return builder.create();
		}
		return null;
	}

	/**
	 * 处理图片的线程
	 * 
	 * @author 孔令琛
	 * 
	 */
	private class PicProgressThread extends Thread {
		FileOutputStream fos;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 对导入图片bitmap进行压缩，以下是压缩比例设置，以屏幕的长宽为比例
			float scaleWidth = 0.5f;
			float scaleHeight = scaleWidth;
			Matrix m = new Matrix();
			m.postScale(scaleWidth, scaleHeight);
			// 对从图库中导入的图片bitmap压缩
			importBmp = Bitmap.createBitmap(importBmp, 0, 0,
					importBmp.getWidth(), importBmp.getHeight(), m, true);

			// 文件输入输出流 将bitmap写入文件当中
			try {
				fos = new FileOutputStream(sdcardTempFile);
				importBmp.compress(CompressFormat.PNG, 50, fos);

				// 用handler发送成功消息
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt("state", 0);
				message.setData(bundle);
				handler.sendMessage(message);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				// handler发送错误消息
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt("state", -1);
				message.setData(bundle);
				handler.sendMessage(message);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// handler发送错误消息
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt("state", -1);
				message.setData(bundle);
				handler.sendMessage(message);
			} finally {
				// 回收bitmap
				if (importBmp != null && !importBmp.isRecycled()) {
					importBmp.recycle();
				}
				try {
					// 关闭输出流
					fos.flush();
					fos.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				int state = message.getData().getInt("state");// 获取消息
				switch (state) {
				case 0:
					// 添加到list
					PictureModel pictureModel = new PictureModel();
					pictureModel.setpName(importPicName);
					picList.add(pictureModel);

					// 选中新添加的图片
					GALLERY_INDEX = picList.size() - 1;
					// 刷新画廊
					initGallery();

					Toast.makeText(AddNoteActivity.this, "图片导入成功！",
							Toast.LENGTH_LONG).show();
					break;

				case -1:
					Toast.makeText(AddNoteActivity.this, "图片导入失败！",
							Toast.LENGTH_LONG).show();
					break;
				}

				dismissDialog(PROGRESSING);
			}

		};
	}

	@SuppressWarnings("deprecation")
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backbtn:
			String contentString = contentEdit.getText().toString();
			// 当无任何内容时直接退出不提示
			if (contentString.trim().equals("") && picList.size() == 0
					&& voiList.size() == 0 && videoList.size() == 0) {
				AddNoteActivity.this.finish();
			} else {
				showDialog(SURE_EXIT);
			}
			break;

		case R.id.facebtn:
			facePopView = new FacePopView(AddNoteActivity.this, imageId,
					itemClickListener);
			// 让软键盘消失
			((InputMethodManager) getApplicationContext().getSystemService(
					Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
					InputMethodManager.HIDE_NOT_ALWAYS);

			facePopView.setFocusable(true);
			facePopView.setTouchable(true);
			facePopView.setOutsideTouchable(true);
			facePopView.update();
			facePopView.showAtLocation(
					AddNoteActivity.this.findViewById(R.id.addlayout),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		case R.id.writingbtn:
			toWriting();
			break;

		case R.id.compelete_writing_btn:
			toNormal();
			break;

		case R.id.drawpicbtn:
			Intent intent = new Intent(AddNoteActivity.this,
					DrawPicActivity.class);
			startActivityForResult(intent, PIC);

			break;

		case R.id.importpicbtn:
			Builder localBuilder = new Builder(AddNoteActivity.this);
			localBuilder.setTitle("选择导入方式");
			String[] arrayOfString = new String[2];
			arrayOfString[0] = "从相册导入图片";
			arrayOfString[1] = "拍照";
			localBuilder.setItems(arrayOfString, importPicListener);
			localBuilder.setNegativeButton("取消", null);
			localBuilder.show();

			break;

		case R.id.savebtn:
			// 获取记事本创建时间
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String Ctime = dateFormat.format(new Date());

			// 获取记事本的文本内容
			String content = contentEdit.getText().toString();

			// if (contentEdit.getText().toString().trim().equals("")) {
			// content = "无内容";
			// }

			// 当无任何内容时，不允许用户保存
			if (content.trim().equals("") && picList.size() == 0
					&& voiList.size() == 0 && videoList.size() == 0) {
				Toast.makeText(AddNoteActivity.this, "无可保存内容！",
						Toast.LENGTH_SHORT).show();
				break;
			}

			// 获取记事本所在的分组号
			int Tid = getIntent().getIntExtra("index", 1);

			// 封装到实体类中插入数据库
			NoteDetail noteDetail = new NoteDetail();
			noteDetail.setT_id(Tid);
			noteDetail.setCtime(Ctime);
			noteDetail.setContent(content);
			noteDetail.setLocked(locked);
			// 如果设置了提醒则添加
			if (needRemind) {
				noteDetail.setRtime(remindString);
			}

			NoteDetailDao noteDetailDao = new NoteDetailDao(
					AddNoteActivity.this);
			// 保存到数据并返回所在的nid
			int nid = noteDetailDao.addNewNote(noteDetail);

			// 设置闹钟
			if (needRemind) {
				AlarmSetting.setAlarm(AddNoteActivity.this, nid, Rdate);
			}

			// 判断有无图片，有则保存nid对应的tb_pic表中
			if (picList.size() != 0) {
				PictureDao pictureDao = new PictureDao(AddNoteActivity.this);
				for (int i = 0; i < picList.size(); i++) {
					pictureDao.addNewPicture(nid + "", picList.get(i));
				}
			}

			// 判断有无录音，有则保存nid对应的tb_voi表中
			if (voiList.size() != 0) {
				VoiceDao voiceDao = new VoiceDao(AddNoteActivity.this);
				for (int i = 0; i < voiList.size(); i++) {
					voiceDao.addNewVoice(nid, voiList.get(i));
				}

			}

			// 判断有无视频，有则保存nid对应的tb_vidro表中
			if (videoList.size() != 0) {
				VideoDao videoDao = new VideoDao(AddNoteActivity.this);
				for (int i = 0; i < videoList.size(); i++) {
					videoDao.addNewVideo(nid, videoList.get(i));
				}
			}

			Toast.makeText(AddNoteActivity.this, "记事本添加成功！", Toast.LENGTH_LONG)
					.show();
			setResult(RESULT_OK);
			AddNoteActivity.this.finish();

			break;

		case R.id.recorderbtn:
			startRecording();
			showDialog(RECORDING);
			break;

		case R.id.videobtn:
			Intent recordintent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			// 指定视频录制质量
			recordintent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(recordintent, VID);
			break;

		case R.id.lockbtn:
			// 先判断是否创建了密码
			if (Utils.getPasswd(sharedPreferences) == null) {
				showDialog(CREATE_PASSWD);
			} else {
				// 如果已创建密码则直接进行操作
				if (locked == 0) {
					Toast.makeText(AddNoteActivity.this, "记事本已加锁！",
							Toast.LENGTH_LONG).show();
					locked = 1;
				} else {
					Toast.makeText(AddNoteActivity.this, "记事本已解锁！",
							Toast.LENGTH_LONG).show();
					locked = 0;
				}

				// 改变加锁按钮图标
				changeLockState(locked);
			}

			break;

		case R.id.remindbtn:
			// showDialog(REMIND_SETTING);

			setTimeBuilder = new SetTimeBuilder(AddNoteActivity.this);

			setTimeBuilder.setCancelable(true);

			setTimeBuilder.setPositiveButton("设置",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

							// if (AlarmSetting.setAlarm(AddNoteActivity.this,
							// 30, setTimeBuilder.getResultDate())) {
							if (!Utils.checkOverdue(setTimeBuilder
									.set_YMD_wkD_Tm())) {
								needRemind = true;
								remindString = setTimeBuilder.set_YMD_wkD_Tm();
								remindTimeTextView.setText("提醒："
										+ setTimeBuilder.set_YMD_wkD_Tm());

								Rdate = setTimeBuilder.getResultDate();

								Toast.makeText(AddNoteActivity.this, "提醒设置成功！",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(AddNoteActivity.this,
										"时间已过期，设置失败！", Toast.LENGTH_LONG)
										.show();
							}
						}
					});

			setTimeBuilder.setNegativeButton("取消闹钟",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							needRemind = false;
							remindTimeTextView.setText("");
							Toast.makeText(AddNoteActivity.this, "取消闹钟成功！",
									Toast.LENGTH_LONG).show();
						}
					});

			setTimeBuilder.show();

			break;

		}

	}

	/**
	 * 判断当前是否加锁，改变加锁按钮的图案
	 * 
	 * @param locked
	 */
	public void changeLockState(int locked) {
		if (locked == 1) {
			lockButton.setImageDrawable(getResources().getDrawable(
					R.drawable.btn_lock));
		} else {
			lockButton.setImageDrawable(getResources().getDrawable(
					R.drawable.btn_unlock));
		}
	}

	/**
	 * 表情item的触发事件
	 */
	public OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// facePopView.dismiss();
			// TODO Auto-generated method stub
			// 获取选择的表情名字
			String faceName = arg1.getResources()
					.getResourceName(imageId[position]).split("/")[1];
			// 获取EditText光标所在的位置
			int cPosition = contentEdit.getSelectionStart();

			if (faceName.equals("backspace")) {// 如果是回删键

				String string = contentEdit.getText().toString();
				// 首先判断edit中有没有输入表情，长度小于8则直接删去一个字符
				if (string.length() < 8 && string.length() != 0) {
					contentEdit.getText().delete(cPosition - 1, cPosition);

				} else if (string.length() >= 8) {
					// 获取光标前面的8个字符
					String target = string.substring(cPosition - 8, cPosition);
					if (target.matches("\\[face[0-9][0-9]\\]")
							|| target.equals("[circle]")
							|| target.equals("[square]")
							|| target.equals("[rectangle]")
							|| target.equals("[love]")) {// 如果匹配是一个表情
						deleteFace(contentEdit, cPosition);
						System.out.println(target);
					} else {
						// 否则删除一个字符
						contentEdit.getText().delete(cPosition - 1, cPosition);
					}

				}

			} else {
				try {
					insertText(contentEdit, faceName, cPosition);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * 导入图片选择操作监听
	 */
	public DialogInterface.OnClickListener importPicListener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch (which) {
			case 0:
				Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
				openAlbumIntent
						.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
				startActivityForResult(openAlbumIntent, IMPORT_PIC);
				break;

			case 1:
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);

				importPicName = "ipic_" + Utils.getNowDate() + ".png";
				fileUrl = Utils.PIC_PATH + importPicName;
				sdcardTempFile = new File(fileUrl);
				try {
					sdcardTempFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				originalUri = Uri.fromFile(sdcardTempFile);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri);
				startActivityForResult(openCameraIntent, TAKE_PIC);

				break;
			}
		}
	};

	/**
	 * 添加表情的方法
	 * 
	 * @param faceName
	 *            表情名
	 * @return
	 * @throws Exception
	 */
	public SpannableString addFace(String faceName) throws Exception {
		// 通过文件名获取id
		Field field = R.drawable.class.getDeclaredField(faceName);
		// int id = Integer.parseInt(field.get(null).toString());
		int id = field.getInt(faceName);

		// 建立位图
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
		ImageSpan imageSpan = new ImageSpan(AddNoteActivity.this, bitmap);

		// 保存在string中的名称,即在edittext显示的名称
		SpannableString spannableString = new SpannableString("[" + faceName
				+ "]");
		// 在editview显示的范围
		spannableString.setSpan(imageSpan, 0, faceName.length() + 2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannableString;
	}

	/**
	 * 向EditText指定光标位置插入字符串
	 * 
	 * @param mEditText
	 * @param fileName
	 *            插入的文字
	 * @param position
	 *            插入位置
	 * @throws Exception
	 */
	private void insertText(EditText mEditText, String fileName, int position)
			throws Exception {

		mEditText.getText().insert(position, addFace(fileName));
	}

	/**
	 * 向EditText指定光标位置删除一个字符
	 * 
	 * @param editText
	 * @param position
	 *            插入位置
	 */
	private void deleteFace(EditText editText, int position) {
		// 获取EditText光标所在的位置
		editText.getText().delete(position - 8, position);
	}

	/**
	 * 格式化时间，即当9:3时变成09:03
	 * 
	 * @param i
	 * @return
	 */
	public String formatTime(int i) {
		if (i < 10) {
			return "0" + i;
		} else {
			return i + "";
		}
	}

	/**
	 * 开始录音
	 */
	private void startRecording() {

		// 开始录音时赋值
		nowRecored = "voi_" + Utils.getNowDate() + ".3gp";

		voiceRecorder = new MediaRecorder();
		voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		voiceRecorder.setOutputFile(Utils.VOI_PATH + nowRecored);// 保存路径和名称
		voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			voiceRecorder.prepare();// 预加载
		} catch (IOException e) {

		}
		voiceRecorder.start();// 开始录音
	}

	/**
	 * 停止录音
	 */
	private void stopRecording() {
		voiceRecorder.stop();
		voiceRecorder.release();
		voiceRecorder = null;
	}

	/**
	 * 播放一个录音
	 * 
	 * @param vName
	 */
	private void startPlaying(String vName, final ImageButton button) {
		isVoicePlaying = true;
		voicePlayer = new MediaPlayer();

		button.setImageResource(R.drawable.voice_stop);

		try {
			voicePlayer.setDataSource(Utils.VOI_PATH + vName);
			voicePlayer.prepare();
			voicePlayer.start();
		} catch (IOException e) {
			System.out.println("播放录音失败");
		}

		voicePlayer.setOnCompletionListener(new OnCompletionListener() {

			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				isVoicePlaying = false;
				button.setImageResource(R.drawable.voice_play);
			}
		});

	}

	/**
	 * 停止播放录音
	 */
	private void stopPlaying(ImageButton button) {
		button.setImageResource(R.drawable.voice_play);
		isVoicePlaying = false;
		if (voicePlayer != null) {
			voicePlayer.release();
			voicePlayer = null;
		}

	}

	/**
	 * 保持dialog显示
	 * 
	 * @param dialog
	 */
	private void keepDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * dialog消失的方法
	 * 
	 * @param dialog
	 */
	private void destoryDialog(DialogInterface dialog) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 长按一张图片时出现的操作选择框
	 * 
	 * @param title
	 *            标题
	 */
	public void pictureOprDialog(String title) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(
				AddNoteActivity.this);// 设置上下文
		localBuilder.setTitle(title);// 设置对话框的标题
		String[] arrayOfString = new String[3];// 定义对话框的两项内容
		arrayOfString[0] = "编辑图片";
		arrayOfString[1] = "编辑文字";
		arrayOfString[2] = "删除图片";
		// 设置监听类
		localBuilder.setItems(arrayOfString,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent intent = new Intent(AddNoteActivity.this,
									DrawPicActivity.class);
							intent.putExtra("editPic", Utils.PIC_PATH
									+ picList.get(selectedMedia).getpName());
							startActivityForResult(intent, PIC);
							break;

						case 1:
							AddIllustrationDialog(selectedMedia);
							break;
						case 2:
							showDialog(SURE_DELE_PIC);
							break;
						}
					}
				});

		localBuilder.setNegativeButton("取消", null);
		localBuilder.show();// 显示出来
	}

	@Override
	public void onPause() {
		super.onPause();
		if (voiceRecorder != null) {
			voiceRecorder.release();
			voiceRecorder = null;
		}
		if (voicePlayer != null) {
			voicePlayer.release();
			voicePlayer = null;
		}

		// 停止友盟统计
		MobclickAgent.onPause(this);

		// 回收bitmap空间
		if (importBmp != null && !importBmp.isRecycled()) {
			importBmp.recycle();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			if (isWriting) {
				// 如果是手写输入状态则退出该状态
				toNormal();
				return true;
			}
			showDialog(SURE_EXIT);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 退出手写输入模式
	 */
	public void toNormal() {
		isWriting = false;
		writingTileLayout.setVisibility(View.GONE);
		gestureOverlayView.setVisibility(View.GONE);

		writingTileLayout.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_top_out));
		backButton.setEnabled(true);
		saveButton.setEnabled(true);
	}

	/**
	 * 进入手写输入模式
	 */
	public void toWriting() {
		isWriting = true;
		writingTileLayout.setVisibility(View.VISIBLE);
		gestureOverlayView.setVisibility(View.VISIBLE);

		writingTileLayout.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_top_in));

		// 隐藏软键盘
		hiddenKeyborad();

		backButton.setEnabled(false);
		saveButton.setEnabled(false);
	}

	/**
	 * 隐藏键盘
	 */
	public void hiddenKeyborad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(AddNoteActivity.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 手写完成事件监听
	 */
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		// 定义一个数组用于存放识别结果
		ArrayList<Prediction> list = gestureLibrary.recognize(gesture);
		if (list.size() > 0) {
			Prediction prediction = list.get(0);
			if (prediction.score > 1.0) {
				// 获取EditText光标所在的位置
				int cPosition = contentEdit.getSelectionStart();

				try {
					// 插入图形
					insertText(contentEdit, prediction.name, cPosition);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(AddNoteActivity.this, "无法识别该图形",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * 
	 * @name 方法名：initGallery()
	 * @function 功能 初始化gallery，设置其中的参数以及绑定的事件
	 * @param 参
	 *            数：无
	 * @return 返 回 值： 无
	 */
	public void initGallery() {

		// 是否需要画廊
		if (picList.size() > 0) {
			galleryLayout.setVisibility(View.VISIBLE);
		} else {
			galleryLayout.setVisibility(View.GONE);
			return;
		}

		System.out.println("初始化 了gallery");
		// 设置两张图片之间空隙
		gallery.setSpacing(1);
		// allPicUri = Utils.getPicUriFromDir(Utils.PIC_PATH);// 更新图像uri列表内容
		// 定义gallery的专属适配器
		GalleryPicAdapter imageAdapter = new GalleryPicAdapter(
				AddNoteActivity.this, picList);

		// Utils.getPicUriFromDir(Utils.PIC_PATH);
		gallery.setAdapter(imageAdapter);// gallery绑定适配器
		// 当点中缩略图，并且跳转至viewFlowGallery
		gallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// 设置当前所选中的项并传递到viewFlowGallery中
				GALLERY_INDEX = arg2;// 设置gallery的标记，记住当前是显示第几张图片,这可以识得viewFlowGallery可以显示对应的图片
				Intent intent = new Intent(AddNoteActivity.this,
						PictureFlowActivity.class);// 跳转activity
				Bundle bundle = new Bundle();
				bundle.putSerializable("pics",
						(ArrayList<PictureModel>) picList);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		gallery.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int which, long arg3) {
				// TODO Auto-generated method stub
				selectedMedia = which;
				GALLERY_INDEX = selectedMedia;
				pictureOprDialog("请选择操作：");
				return false;
			}
		});

		// 和adapter中的position有关
		gallery.setSelection(GALLERY_INDEX);
	}

	/**
	 * 显示编辑图片描述提示框
	 * 
	 * @param position
	 *            选中的图片在list的位置
	 */
	public void AddIllustrationDialog(int position) {
		final PictureModel selectedPic = picList.get(position);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddNoteActivity.this);
		builder.setTitle("描述：");
		// 设置输入框相关属性
		final EditText editText = new EditText(AddNoteActivity.this);
		editText.setTextColor(Color.WHITE);
		editText.setHint("请输入25字以内的图片描述");
		editText.setText(selectedPic.getIllustration());
		builder.setView(editText);
		builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String string = editText.getText().toString();
				if (string.length() < 25) {
					// 如果没有超过字数将评论保存到lis中
					selectedPic.setIllustration(editText.getText().toString());
					destoryDialog(dialog);
					Toast.makeText(AddNoteActivity.this, "图片描述编辑成功！",
							Toast.LENGTH_SHORT).show();
				} else {
					// 超过字数则提示
					editText.setError("字数请在25个以内");
					keepDialog(dialog);
				}

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				destoryDialog(dialog);
			}
		});
		builder.show();
	}

}
