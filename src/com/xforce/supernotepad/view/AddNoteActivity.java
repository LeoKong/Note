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
	protected static final int IMPORT_PIC = 7;// ����ͼƬrequestcode
	protected static final int MODIFY_PIC = 8;// �޸�ͼƬ
	protected static final int TAKE_PIC = 9;// ����
	DisplayMetrics dm = new DisplayMetrics();
	Uri originalUri = null;// �������ȡ����Ƭ��URI��ַ
	Bitmap importBmp = null; // ���������ʱ�����ʱbitmap
	File sdcardTempFile = null;// ��ʱ�ļ�
	String importPicName = null;// ��ǰ����ͼƬ������
	String fileUrl = null;// ͼƬ�ĵ�ַ
	// all in here end 3

	public TextView backButton, saveButton;// ���ذ�ť�����水ť
	private ImageButton faceButton, writingButton, drawpicButton,
			importpicButton, recorederButton, videoButton, lockButton,
			remindButton;
	public EditText contentEdit;// ��������������
	public LinearLayout voiceListLayout, videoListLayout;

	public TextView remindTimeTextView;// ��ʾ�û����õ�����ʱ��

	public GestureOverlayView gestureOverlayView;// ��д�����layout
	GestureLibrary gestureLibrary;// ʵ����ʶ���
	public TextView completeWriteButton;// �����д���밴ť
	RelativeLayout writingTileLayout;// ��д����ʱ������
	boolean isWriting = false;// ��ǰ�Ƿ�������д����

	private MediaRecorder voiceRecorder = null;// ¼����
	private MediaPlayer voicePlayer = null;// ����¼����
	boolean isVoicePlaying = false;// ��¼��ǰ�Ƿ����ڲ���¼��

	private FacePopView facePopView;// ����ѡ���

	public List<PictureModel> picList = new ArrayList<PictureModel>();// ��ʱ��������ӵ�ͼƬ�б���
	public List<String> voiList = new ArrayList<String>();// ��ʱ��������ӵ�¼���б���
	public List<String> videoList = new ArrayList<String>();// ��ʱ��������ӵ���Ƶ�б�����
	public String nowRecored = null;// ��ǰ¼�����ļ���

	public int selectedMedia = 0;// ��ǰ����ѡ�еĶ�ý��
	public int locked = 0;// �Ƿ���Ҫ����
	public SharedPreferences sharedPreferences = null;// ϵͳ����

	// ���л����е���Ƭ��URI����������
	private LinearLayout galleryLayout;// ���Ż��ȵ�layout
	private Gallery gallery;// ����ʵ��
	public static int GALLERY_INDEX = 0;// ��������ʾ�ı�־
	private File tmpFile;// ��ʱ�ļ� ���� �������յ�ͼƬ

	private SetTimeBuilder setTimeBuilder;
	public int[] Rdate;// ����ʱ��

	// ��������
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

	// ����ʱ������
	public String remindString = null;
	// �Ƿ���������
	boolean needRemind = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_layout);

		// ��ʼ���ؼ�
		initWidget();

		// ��ȡϵͳ����
		sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (gallery != null) {
			// ��������activity����ʱ������gallery��ʾ��Ӧ��ͼƬ���ﵽͬ�������߶���ʾͬһ�š�ͬһλ�õ�ͼƬ
			gallery.setSelection(GALLERY_INDEX);
			initGallery();
		}

		// ��������ͳ��
		MobclickAgent.onResume(this);

		// ����bitmap�ռ�
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
					// ���ͼƬresult
					PictureModel pictureModel = new PictureModel();
					pictureModel.setpName(picName);
					picList.add(pictureModel);
					// addPicOnNote(picName, true);
					GALLERY_INDEX = picList.size() - 1;
					// ˢ�»���
					initGallery();

				} else {
					// �༭ͼƬresult
					// ˢ����ʾ�޸ĺ��ͼƬ�б�
					// refreshImageList();

					// ˢ�»���
					initGallery();

				}

			}
			// ��ѡ����ͼƬ
			else if (requestCode == IMPORT_PIC) {
				ContentResolver resolver = getContentResolver();
				originalUri = data.getData();// ȡ�����ͼƬ��URI
				// ͨ��URI��ͼƬת����bitmap
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
				// �������˶�ͼƬ���������ͼƬ�����޸ģ��������²���
			} else if (requestCode == MODIFY_PIC) {
				// �Ӳü�����ͼƬ�ļ�����bitmap
				importBmp = BitmapFactory.decodeFile(sdcardTempFile
						.getAbsolutePath());
				// // ��bitmap���б���ѹ��������ѹ������������Ļ�Ĵ�С����ѹ��
				// float scaleWidth = 0.5f;
				// float scaleHeight = scaleWidth;
				// Matrix m = new Matrix();
				// m.postScale(scaleWidth, scaleHeight);
				// // ����ѹ��
				// importBmp = Bitmap.createBitmap(importBmp, 0, 0,
				// importBmp.getWidth(), importBmp.getHeight(), m, true);
				// FileOutputStream fos;
				// try {
				// // �ļ�����bitmapд��ԭ�����ļ���ȥ
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
				// // ˢ�»���
				// initGallery();
				// // ѡ������ӵ�ͼƬ
				// GALLERY_INDEX = picList.size() - 1;
				showDialog(PROGRESSING);

				new PicProgressThread().start();

				// ��ѡ��������ʱ��
			} else if (requestCode == TAKE_PIC) {
				// �����պ󱣴��ͼƬ�ļ�ת��bitmap

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 3;// ��ͼƬѹ����ʾ

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
					// ϵͳĬ��ָ�������·��
					String videoPath = cursor.getString(cursor
							.getColumnIndex("_data"));
					// ����һ���ļ���
					String newVideoName = "vid_" + Utils.getNowDate() + ".mp4";
					File oldFile = new File(videoPath);
					File newFile = new File(Utils.VID_PATH + newVideoName);

					try {
						// ���ļ����Ƶ�ָ��·��
						FileUtils.copyFile(oldFile, newFile);
						// ��ԭ����ɾ��
						FileUtil.deleteMediaInSdcard(oldFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// �����ʾview
					addVidOnNote(newVideoName, AddNoteActivity.this);
					videoList.add(newVideoName);
				}
			}
		}

	}

	/**
	 * ˢ��ͼƬ�б�
	 */
	// public void refreshImageList() {
	// imageListLayout.removeAllViews();
	// for (int i = 0; i < picList.size(); i++) {
	// addPicOnNote(picList.get(i).getpName(), false);
	// }
	//
	// }

	/**
	 * ��̬��ʾ�û���ӵ�ͼƬview
	 * 
	 * @param picName
	 *            ͼƬ�ļ���
	 * @param hasAnima
	 *            �Ƿ���Ҫ��Ӷ���
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
	// options.inSampleSize = 2;// ������ѹ����ʾ
	// Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
	// imageView.setImageBitmap(bitmap);
	// imageView.setClickable(true);
	// // ����¼�
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
	// // �����¼�
	// imageView.setOnLongClickListener(new OnLongClickListener() {
	//
	// @SuppressWarnings("deprecation")
	// public boolean onLongClick(View v) {
	// // TODO Auto-generated method stub
	// // ��������ͼƬ��list�е�λ��
	// getLongSelectedPicture(picList, picName);
	//
	// pictureOprDialog("��ѡ�������");
	// return false;
	// }
	// });
	//
	// // ��Ӷ���Ч��
	// if (hasAnima) {
	// imageView.startAnimation(AnimationUtils.loadAnimation(this,
	// R.anim.alpha_scale_in));
	// }
	// imageListLayout.addView(imageView);
	//
	// }

	/**
	 * ��̬�����ʾ¼��view
	 * 
	 * @param vName
	 *            ¼����
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
						// ��������¼����list�е�λ��
						getLongSelectedMedia(voiList, vName);

						showDialog(SURE_DEL_VOI);
						return false;
					}
				});

				voiceView.addView(button);
				voiceView.addView(textView);

				// ��Ӷ���Ч��
				voiceView.startAnimation(AnimationUtils.loadAnimation(
						AddNoteActivity.this, R.anim.alpha_scale_in));

				Message message = handler.obtainMessage(0, voiceView);
				handler.sendMessage(message);
			}
		}.start();

	}

	/**
	 * ��̬�����ʾ��Ƶview
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

				// ��ȡͼƬ������ͼ
				BitmapDrawable drawable = new BitmapDrawable(getVideoThumbnail(
						Utils.VID_PATH + vidName, 500, 500,
						MediaStore.Images.Thumbnails.MICRO_KIND));
				imageButton.setBackgroundDrawable(drawable);
				imageButton.setImageResource(R.drawable.voice_play);

				imageButton.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						// TODO Auto-generated method stub
						// ����ϵͳ������
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
						// ��������¼����list�е�λ��
						getLongSelectedMedia(videoList, vidName);

						showDialog(SURE_DEL_VID);
						return false;
					}
				});

				// ��Ӷ���Ч��
				imageButton.startAnimation(AnimationUtils.loadAnimation(
						AddNoteActivity.this, R.anim.alpha_scale_in));

				Message message = handler.obtainMessage(0, imageButton);
				handler.sendMessage(message);

			}
		}.start();
	}

	/**
	 * ��ȡ��Ƶ������ͼ����ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ��
	 * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档
	 * 
	 * @param videoPath
	 *            ��Ƶ·��
	 * @param width
	 *            ָ�������Ƶ����ͼ�Ŀ��
	 * @param height
	 *            ָ�������Ƶ����ͼ�ĸ߶�
	 * @param kind
	 *            ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND��
	 *            ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96
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
	 * �ҳ�����ѡ��Ķ�ý���ļ�������������list�������Ÿ�ֵ��selectedMedia
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
	 * �ҳ�����ѡ���ͼƬ������������list�������Ÿ�ֵ��selectedMedia
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
	 * ��ʼ���ؼ�
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

		// Ϊʶ�����Ӽ�����
		gestureOverlayView.addOnGesturePerformedListener(this);
		// ����ʶ������Ե��ļ�λ��
		gestureLibrary = GestureLibraries.fromRawResource(AddNoteActivity.this,
				R.raw.gestures);

		if (!gestureLibrary.load()) {
			Toast.makeText(AddNoteActivity.this, "��д�⵼��ʧ�ܣ�", Toast.LENGTH_LONG)
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
			builder.setTitle("ɾ����ͼƬ��");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int which) {
							// TODO Auto-generated method stub
							// ����ʱ�б���ɾ��
							picList.remove(selectedMedia);

							GALLERY_INDEX--;
							// ����Ļ��ɾ��
							// imageListLayout.removeViewAt(selectedMedia);

							//
							initGallery();

							for (int i = 0; i < picList.size(); i++) {
								System.out.println("ʣ�µ�" + picList.get(i));
							}
						}
					});
			builder.setNegativeButton("��", null);
			return builder.create();

		case SURE_DEL_VOI:
			builder.setTitle("ɾ����¼����");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// ����ʱ�б���ɾ��
							voiList.remove(selectedMedia);
							// ����Ļ��ɾ��
							voiceListLayout.removeViewAt(selectedMedia);
							for (int i = 0; i < voiList.size(); i++) {
								System.out.println("ʣ�µ�" + voiList.get(i));
							}
						}
					});

			builder.setNegativeButton("��", null);
			return builder.create();

		case SURE_DEL_VID:
			builder.setTitle("ɾ������Ƶ��");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int whilch) {
							// TODO Auto-generated method stub
							// ����ʱ�б���ɾ��
							videoList.remove(selectedMedia);
							// ����Ļ��ɾ��
							videoListLayout.removeViewAt(selectedMedia);

						}
					});
			builder.setNegativeButton("��", null);
			return builder.create();

		case SURE_EXIT:
			builder.setTitle("���±����������棬ȷ���˳���");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method
							// ������Ӷ�ý���ļ��Ѿ���sdcard�У���������Ҫ����ɾ��
							FileUtil.deleteMediaInSdcard(picList);
							FileUtil.deleteMediaInSdcard(voiList,
									Utils.VOI_PATH);

							AddNoteActivity.this.finish();
						}
					});
			builder.setNegativeButton("��", null);
			return builder.create();

		case CREATE_PASSWD:
			builder.setTitle("��������");
			builder.setCancelable(false);
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.createpasswd_dialog_layout, null);
			final EditText passwdEditText = (EditText) linearLayout
					.findViewById(R.id.addpasswd_edit);
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String passwdString = passwdEditText.getText()
									.toString();
							if (passwdString.length() < 5) {
								passwdEditText.setError("����̫�̣�");
								keepDialog(dialog);
							} else {
								SharedPreferences sp = getSharedPreferences(
										"setting", MODE_PRIVATE);
								Utils.savePasswd(sp, passwdString);
								Toast.makeText(AddNoteActivity.this, "���봴���ɹ�",
										Toast.LENGTH_SHORT).show();
								destoryDialog(dialog);
							}
						}
					});
			builder.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							destoryDialog(dialog);
						}
					});
			builder.setView(linearLayout);

			return builder.create();

		case SURE_EDIT_PIC:

			builder.setTitle("�Ƿ�ü�ͼƬ��");
			builder.setCancelable(false);
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// �������޸�ͼƬǰ��׼������
							Intent intent = new Intent(
									"com.android.camera.action.CROP");
							intent.setDataAndType(originalUri, "image/*");
							intent.putExtra("crop", "true");

							// ȷ������ͼƬ�����ƺ͵�ַ��������ͼƬ�ļ�
							/* �Ӹ�ȷ�� tempfile �ķ��� */
							importPicName = "ipic_" + Utils.getNowDate()
									+ ".png";
							fileUrl = Utils.PIC_PATH + importPicName;
							sdcardTempFile = new File(fileUrl);
							try {
								// �����ָ�� �Ƚ�����ͼƬ
								sdcardTempFile.createNewFile();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							// ȷ���ü���ͼƬ��ͼƬ�����λ��
							intent.putExtra("output",
									Uri.fromFile(sdcardTempFile));
							// ��ȥ�޸�
							startActivityForResult(intent, MODIFY_PIC);
						}
					});
			builder.setNegativeButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// ȷ������ͼƬ�ļ������Լ���ַ
							importPicName = "ipic_" + Utils.getNowDate()
									+ ".png";
							fileUrl = Utils.PIC_PATH + importPicName;
							sdcardTempFile = new File(fileUrl);
							// ��ʾ��ʾ��
							showDialog(PROGRESSING);
							// �����������ͼƬ
							new PicProgressThread().start();
						}
					});

			return builder.create();

		case PROGRESSING:

			return ProgressDialog.show(this, null, "���ڴ���...", true);

		case RECORDING:
			final ProgressDialog recordingDialog = new ProgressDialog(
					AddNoteActivity.this);
			recordingDialog.setCancelable(false);
			recordingDialog.setMessage("����¼��...");
			recordingDialog.setButton("���",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// ֹͣ¼��
							stopRecording();
							recordingDialog.dismiss();
							// ��������ʱ�б���
							voiList.add(nowRecored);
							// ������ʾ
							addVoiOnNote(nowRecored, AddNoteActivity.this);
						}
					});

			recordingDialog.setButton2("ȡ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							recordingDialog.dismiss();
							// ֹͣ¼���������sdcard��ɾ��
							stopRecording();
							FileUtil.deleteMediaInSdcard(new File(
									Utils.VOI_PATH + nowRecored));
						}
					});
			recordingDialog.show();
			return recordingDialog;

			// case REMIND_SETTING:
			// builder.setTitle("ѡ������ʱ�䣺");
			// linearLayout = (LinearLayout) getLayoutInflater().inflate(
			// R.layout.datetime_setting_laytou, null);
			// DatePicker datePicker = (DatePicker) linearLayout
			// .findViewById(R.id.date_picker);
			// TimePicker timePicker = (TimePicker) linearLayout
			// .findViewById(R.id.time_picker);
			// final TextView showdateTextView = (TextView) linearLayout
			// .findViewById(R.id.showdate_tv);
			// // ��ȡ��ǰʱ����Ϣ
			// final Calendar calendar = Calendar.getInstance();
			// mYear = calendar.get(Calendar.YEAR);
			// mMonth = calendar.get(Calendar.MONTH);
			// mDay = calendar.get(Calendar.DAY_OF_MONTH);
			// mHour = calendar.get(Calendar.HOUR_OF_DAY);
			// mMinute = calendar.get(Calendar.MINUTE);
			//
			// remindString = mYear + "��" + formatTime((mMonth + 1)) + "��"
			// + formatTime(mDay) + "�� " + formatTime(mHour) + ":"
			// + formatTime(mMinute);
			//
			// showdateTextView.setText(remindString);
			//
			// // ��ʼ������ѡ�����������ü���
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
			// remindString = mYear + "��" + formatTime((mMonth + 1)) + "��"
			// + formatTime(mDay) + "�� " + formatTime(mHour) + ":"
			// + formatTime(mMinute);
			// showdateTextView.setText(remindString);
			// }
			// });
			//
			// // ����ʱ�����
			// timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			//
			// public void onTimeChanged(TimePicker view, int hourOfDay,
			// int minute) {
			// // TODO Auto-generated method stub
			// calendar.set(mYear, mMonth, mDay, hourOfDay, minute);
			// mHour = hourOfDay;
			// mMinute = minute;
			//
			// remindString = mYear + "��" + formatTime((mMonth + 1)) + "��"
			// + formatTime(mDay) + "�� " + formatTime(mHour) + ":"
			// + formatTime(mMinute);
			// showdateTextView.setText(remindString);
			// }
			// });
			//
			// builder.setPositiveButton("����",
			// new DialogInterface.OnClickListener() {
			//
			// public void onClick(DialogInterface dialog, int which) {
			// // TODO Auto-generated method stub
			// if (Utils.checkOverdue(remindString)) {
			// Toast.makeText(AddNoteActivity.this,
			// "���õ�ʱ���ѹ��ڣ�", Toast.LENGTH_LONG).show();
			// } else {
			// needRemind = true;
			// remindTimeTextView
			// .setText("����:" + remindString);
			// }
			//
			// }
			// });
			//
			// builder.setNegativeButton("ȡ������",
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
	 * ����ͼƬ���߳�
	 * 
	 * @author �����
	 * 
	 */
	private class PicProgressThread extends Thread {
		FileOutputStream fos;

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// �Ե���ͼƬbitmap����ѹ����������ѹ���������ã�����Ļ�ĳ���Ϊ����
			float scaleWidth = 0.5f;
			float scaleHeight = scaleWidth;
			Matrix m = new Matrix();
			m.postScale(scaleWidth, scaleHeight);
			// �Դ�ͼ���е����ͼƬbitmapѹ��
			importBmp = Bitmap.createBitmap(importBmp, 0, 0,
					importBmp.getWidth(), importBmp.getHeight(), m, true);

			// �ļ���������� ��bitmapд���ļ�����
			try {
				fos = new FileOutputStream(sdcardTempFile);
				importBmp.compress(CompressFormat.PNG, 50, fos);

				// ��handler���ͳɹ���Ϣ
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt("state", 0);
				message.setData(bundle);
				handler.sendMessage(message);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				// handler���ʹ�����Ϣ
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt("state", -1);
				message.setData(bundle);
				handler.sendMessage(message);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// handler���ʹ�����Ϣ
				Message message = handler.obtainMessage();
				Bundle bundle = new Bundle();
				bundle.putInt("state", -1);
				message.setData(bundle);
				handler.sendMessage(message);
			} finally {
				// ����bitmap
				if (importBmp != null && !importBmp.isRecycled()) {
					importBmp.recycle();
				}
				try {
					// �ر������
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
				int state = message.getData().getInt("state");// ��ȡ��Ϣ
				switch (state) {
				case 0:
					// ��ӵ�list
					PictureModel pictureModel = new PictureModel();
					pictureModel.setpName(importPicName);
					picList.add(pictureModel);

					// ѡ������ӵ�ͼƬ
					GALLERY_INDEX = picList.size() - 1;
					// ˢ�»���
					initGallery();

					Toast.makeText(AddNoteActivity.this, "ͼƬ����ɹ���",
							Toast.LENGTH_LONG).show();
					break;

				case -1:
					Toast.makeText(AddNoteActivity.this, "ͼƬ����ʧ�ܣ�",
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
			// �����κ�����ʱֱ���˳�����ʾ
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
			// ���������ʧ
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
			localBuilder.setTitle("ѡ���뷽ʽ");
			String[] arrayOfString = new String[2];
			arrayOfString[0] = "����ᵼ��ͼƬ";
			arrayOfString[1] = "����";
			localBuilder.setItems(arrayOfString, importPicListener);
			localBuilder.setNegativeButton("ȡ��", null);
			localBuilder.show();

			break;

		case R.id.savebtn:
			// ��ȡ���±�����ʱ��
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String Ctime = dateFormat.format(new Date());

			// ��ȡ���±����ı�����
			String content = contentEdit.getText().toString();

			// if (contentEdit.getText().toString().trim().equals("")) {
			// content = "������";
			// }

			// �����κ�����ʱ���������û�����
			if (content.trim().equals("") && picList.size() == 0
					&& voiList.size() == 0 && videoList.size() == 0) {
				Toast.makeText(AddNoteActivity.this, "�޿ɱ������ݣ�",
						Toast.LENGTH_SHORT).show();
				break;
			}

			// ��ȡ���±����ڵķ����
			int Tid = getIntent().getIntExtra("index", 1);

			// ��װ��ʵ�����в������ݿ�
			NoteDetail noteDetail = new NoteDetail();
			noteDetail.setT_id(Tid);
			noteDetail.setCtime(Ctime);
			noteDetail.setContent(content);
			noteDetail.setLocked(locked);
			// ������������������
			if (needRemind) {
				noteDetail.setRtime(remindString);
			}

			NoteDetailDao noteDetailDao = new NoteDetailDao(
					AddNoteActivity.this);
			// ���浽���ݲ��������ڵ�nid
			int nid = noteDetailDao.addNewNote(noteDetail);

			// ��������
			if (needRemind) {
				AlarmSetting.setAlarm(AddNoteActivity.this, nid, Rdate);
			}

			// �ж�����ͼƬ�����򱣴�nid��Ӧ��tb_pic����
			if (picList.size() != 0) {
				PictureDao pictureDao = new PictureDao(AddNoteActivity.this);
				for (int i = 0; i < picList.size(); i++) {
					pictureDao.addNewPicture(nid + "", picList.get(i));
				}
			}

			// �ж�����¼�������򱣴�nid��Ӧ��tb_voi����
			if (voiList.size() != 0) {
				VoiceDao voiceDao = new VoiceDao(AddNoteActivity.this);
				for (int i = 0; i < voiList.size(); i++) {
					voiceDao.addNewVoice(nid, voiList.get(i));
				}

			}

			// �ж�������Ƶ�����򱣴�nid��Ӧ��tb_vidro����
			if (videoList.size() != 0) {
				VideoDao videoDao = new VideoDao(AddNoteActivity.this);
				for (int i = 0; i < videoList.size(); i++) {
					videoDao.addNewVideo(nid, videoList.get(i));
				}
			}

			Toast.makeText(AddNoteActivity.this, "���±���ӳɹ���", Toast.LENGTH_LONG)
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
			// ָ����Ƶ¼������
			recordintent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
			startActivityForResult(recordintent, VID);
			break;

		case R.id.lockbtn:
			// ���ж��Ƿ񴴽�������
			if (Utils.getPasswd(sharedPreferences) == null) {
				showDialog(CREATE_PASSWD);
			} else {
				// ����Ѵ���������ֱ�ӽ��в���
				if (locked == 0) {
					Toast.makeText(AddNoteActivity.this, "���±��Ѽ�����",
							Toast.LENGTH_LONG).show();
					locked = 1;
				} else {
					Toast.makeText(AddNoteActivity.this, "���±��ѽ�����",
							Toast.LENGTH_LONG).show();
					locked = 0;
				}

				// �ı������ťͼ��
				changeLockState(locked);
			}

			break;

		case R.id.remindbtn:
			// showDialog(REMIND_SETTING);

			setTimeBuilder = new SetTimeBuilder(AddNoteActivity.this);

			setTimeBuilder.setCancelable(true);

			setTimeBuilder.setPositiveButton("����",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub

							// if (AlarmSetting.setAlarm(AddNoteActivity.this,
							// 30, setTimeBuilder.getResultDate())) {
							if (!Utils.checkOverdue(setTimeBuilder
									.set_YMD_wkD_Tm())) {
								needRemind = true;
								remindString = setTimeBuilder.set_YMD_wkD_Tm();
								remindTimeTextView.setText("���ѣ�"
										+ setTimeBuilder.set_YMD_wkD_Tm());

								Rdate = setTimeBuilder.getResultDate();

								Toast.makeText(AddNoteActivity.this, "�������óɹ���",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(AddNoteActivity.this,
										"ʱ���ѹ��ڣ�����ʧ�ܣ�", Toast.LENGTH_LONG)
										.show();
							}
						}
					});

			setTimeBuilder.setNegativeButton("ȡ������",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							needRemind = false;
							remindTimeTextView.setText("");
							Toast.makeText(AddNoteActivity.this, "ȡ�����ӳɹ���",
									Toast.LENGTH_LONG).show();
						}
					});

			setTimeBuilder.show();

			break;

		}

	}

	/**
	 * �жϵ�ǰ�Ƿ�������ı������ť��ͼ��
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
	 * ����item�Ĵ����¼�
	 */
	public OnItemClickListener itemClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// facePopView.dismiss();
			// TODO Auto-generated method stub
			// ��ȡѡ��ı�������
			String faceName = arg1.getResources()
					.getResourceName(imageId[position]).split("/")[1];
			// ��ȡEditText������ڵ�λ��
			int cPosition = contentEdit.getSelectionStart();

			if (faceName.equals("backspace")) {// ����ǻ�ɾ��

				String string = contentEdit.getText().toString();
				// �����ж�edit����û��������飬����С��8��ֱ��ɾȥһ���ַ�
				if (string.length() < 8 && string.length() != 0) {
					contentEdit.getText().delete(cPosition - 1, cPosition);

				} else if (string.length() >= 8) {
					// ��ȡ���ǰ���8���ַ�
					String target = string.substring(cPosition - 8, cPosition);
					if (target.matches("\\[face[0-9][0-9]\\]")
							|| target.equals("[circle]")
							|| target.equals("[square]")
							|| target.equals("[rectangle]")
							|| target.equals("[love]")) {// ���ƥ����һ������
						deleteFace(contentEdit, cPosition);
						System.out.println(target);
					} else {
						// ����ɾ��һ���ַ�
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
	 * ����ͼƬѡ���������
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
	 * ��ӱ���ķ���
	 * 
	 * @param faceName
	 *            ������
	 * @return
	 * @throws Exception
	 */
	public SpannableString addFace(String faceName) throws Exception {
		// ͨ���ļ�����ȡid
		Field field = R.drawable.class.getDeclaredField(faceName);
		// int id = Integer.parseInt(field.get(null).toString());
		int id = field.getInt(faceName);

		// ����λͼ
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
		ImageSpan imageSpan = new ImageSpan(AddNoteActivity.this, bitmap);

		// ������string�е�����,����edittext��ʾ������
		SpannableString spannableString = new SpannableString("[" + faceName
				+ "]");
		// ��editview��ʾ�ķ�Χ
		spannableString.setSpan(imageSpan, 0, faceName.length() + 2,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return spannableString;
	}

	/**
	 * ��EditTextָ�����λ�ò����ַ���
	 * 
	 * @param mEditText
	 * @param fileName
	 *            ���������
	 * @param position
	 *            ����λ��
	 * @throws Exception
	 */
	private void insertText(EditText mEditText, String fileName, int position)
			throws Exception {

		mEditText.getText().insert(position, addFace(fileName));
	}

	/**
	 * ��EditTextָ�����λ��ɾ��һ���ַ�
	 * 
	 * @param editText
	 * @param position
	 *            ����λ��
	 */
	private void deleteFace(EditText editText, int position) {
		// ��ȡEditText������ڵ�λ��
		editText.getText().delete(position - 8, position);
	}

	/**
	 * ��ʽ��ʱ�䣬����9:3ʱ���09:03
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
	 * ��ʼ¼��
	 */
	private void startRecording() {

		// ��ʼ¼��ʱ��ֵ
		nowRecored = "voi_" + Utils.getNowDate() + ".3gp";

		voiceRecorder = new MediaRecorder();
		voiceRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		voiceRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		voiceRecorder.setOutputFile(Utils.VOI_PATH + nowRecored);// ����·��������
		voiceRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			voiceRecorder.prepare();// Ԥ����
		} catch (IOException e) {

		}
		voiceRecorder.start();// ��ʼ¼��
	}

	/**
	 * ֹͣ¼��
	 */
	private void stopRecording() {
		voiceRecorder.stop();
		voiceRecorder.release();
		voiceRecorder = null;
	}

	/**
	 * ����һ��¼��
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
			System.out.println("����¼��ʧ��");
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
	 * ֹͣ����¼��
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
	 * ����dialog��ʾ
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
	 * dialog��ʧ�ķ���
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
	 * ����һ��ͼƬʱ���ֵĲ���ѡ���
	 * 
	 * @param title
	 *            ����
	 */
	public void pictureOprDialog(String title) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(
				AddNoteActivity.this);// ����������
		localBuilder.setTitle(title);// ���öԻ���ı���
		String[] arrayOfString = new String[3];// ����Ի������������
		arrayOfString[0] = "�༭ͼƬ";
		arrayOfString[1] = "�༭����";
		arrayOfString[2] = "ɾ��ͼƬ";
		// ���ü�����
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

		localBuilder.setNegativeButton("ȡ��", null);
		localBuilder.show();// ��ʾ����
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

		// ֹͣ����ͳ��
		MobclickAgent.onPause(this);

		// ����bitmap�ռ�
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
				// �������д����״̬���˳���״̬
				toNormal();
				return true;
			}
			showDialog(SURE_EXIT);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �˳���д����ģʽ
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
	 * ������д����ģʽ
	 */
	public void toWriting() {
		isWriting = true;
		writingTileLayout.setVisibility(View.VISIBLE);
		gestureOverlayView.setVisibility(View.VISIBLE);

		writingTileLayout.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_top_in));

		// ���������
		hiddenKeyborad();

		backButton.setEnabled(false);
		saveButton.setEnabled(false);
	}

	/**
	 * ���ؼ���
	 */
	public void hiddenKeyborad() {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(AddNoteActivity.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * ��д����¼�����
	 */
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		// TODO Auto-generated method stub
		// ����һ���������ڴ��ʶ����
		ArrayList<Prediction> list = gestureLibrary.recognize(gesture);
		if (list.size() > 0) {
			Prediction prediction = list.get(0);
			if (prediction.score > 1.0) {
				// ��ȡEditText������ڵ�λ��
				int cPosition = contentEdit.getSelectionStart();

				try {
					// ����ͼ��
					insertText(contentEdit, prediction.name, cPosition);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(AddNoteActivity.this, "�޷�ʶ���ͼ��",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/**
	 * 
	 * @name ��������initGallery()
	 * @function ���� ��ʼ��gallery���������еĲ����Լ��󶨵��¼�
	 * @param ��
	 *            ������
	 * @return �� �� ֵ�� ��
	 */
	public void initGallery() {

		// �Ƿ���Ҫ����
		if (picList.size() > 0) {
			galleryLayout.setVisibility(View.VISIBLE);
		} else {
			galleryLayout.setVisibility(View.GONE);
			return;
		}

		System.out.println("��ʼ�� ��gallery");
		// ��������ͼƬ֮���϶
		gallery.setSpacing(1);
		// allPicUri = Utils.getPicUriFromDir(Utils.PIC_PATH);// ����ͼ��uri�б�����
		// ����gallery��ר��������
		GalleryPicAdapter imageAdapter = new GalleryPicAdapter(
				AddNoteActivity.this, picList);

		// Utils.getPicUriFromDir(Utils.PIC_PATH);
		gallery.setAdapter(imageAdapter);// gallery��������
		// ����������ͼ��������ת��viewFlowGallery
		gallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// ���õ�ǰ��ѡ�е�����ݵ�viewFlowGallery��
				GALLERY_INDEX = arg2;// ����gallery�ı�ǣ���ס��ǰ����ʾ�ڼ���ͼƬ,�����ʶ��viewFlowGallery������ʾ��Ӧ��ͼƬ
				Intent intent = new Intent(AddNoteActivity.this,
						PictureFlowActivity.class);// ��תactivity
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
				pictureOprDialog("��ѡ�������");
				return false;
			}
		});

		// ��adapter�е�position�й�
		gallery.setSelection(GALLERY_INDEX);
	}

	/**
	 * ��ʾ�༭ͼƬ������ʾ��
	 * 
	 * @param position
	 *            ѡ�е�ͼƬ��list��λ��
	 */
	public void AddIllustrationDialog(int position) {
		final PictureModel selectedPic = picList.get(position);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddNoteActivity.this);
		builder.setTitle("������");
		// ����������������
		final EditText editText = new EditText(AddNoteActivity.this);
		editText.setTextColor(Color.WHITE);
		editText.setHint("������25�����ڵ�ͼƬ����");
		editText.setText(selectedPic.getIllustration());
		builder.setView(editText);
		builder.setPositiveButton("����", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				String string = editText.getText().toString();
				if (string.length() < 25) {
					// ���û�г������������۱��浽lis��
					selectedPic.setIllustration(editText.getText().toString());
					destoryDialog(dialog);
					Toast.makeText(AddNoteActivity.this, "ͼƬ�����༭�ɹ���",
							Toast.LENGTH_SHORT).show();
				} else {
					// ������������ʾ
					editText.setError("��������25������");
					keepDialog(dialog);
				}

			}
		});
		builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				destoryDialog(dialog);
			}
		});
		builder.show();
	}

}
