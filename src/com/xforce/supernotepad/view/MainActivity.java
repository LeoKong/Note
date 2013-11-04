package com.xforce.supernotepad.view;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.yunzhisheng.basic.USCRecognizerDialog;
import cn.yunzhisheng.basic.USCRecognizerDialogListener;
import cn.yunzhisheng.common.USCError;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.UMSsoHandler;
import com.umeng.socialize.controller.UMWXHandler;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.xforce.supernotepad.dao.NoteDetailDao;
import com.xforce.supernotepad.dao.NoteTypeDao;
import com.xforce.supernotepad.dao.PictureDao;
import com.xforce.supernotepad.dao.VideoDao;
import com.xforce.supernotepad.dao.VoiceDao;
import com.xforce.supernotepad.model.NoteDetail;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.FaceParser;
import com.xforce.supernotepad.util.FileUtil;
import com.xforce.supernotepad.util.SlideHolder;
import com.xforce.supernotepad.util.TypeRadioButton;
import com.xforce.supernotepad.util.Utils;

public class MainActivity extends Activity implements OnClickListener,
		OnLongClickListener, OnItemClickListener, OnItemLongClickListener {

	private static final int ADDTYPE = 1;// ��ӷ����
	private static final int SELECT_OPR = 2;// ����һ������ʱѡ�������
	protected static final int SURE_DEL_TYPE = 3;// ȷ��ɾ��һ�������
	protected static final int EDITTYPE = 4;// �༭�����
	private static final int SURE_DEL_NOTE = 5;// ȷ��ɾ����ѡ���±���
	private static final int SELECT_GROUP = 6;// ѡ����Ҫ�ƶ����ķ����
	private static final int CREATE_PASSWD = 7;// �������������
	private static final int INPUT_PASSWD = 8;// ���������
	private static final int UPDATE = 12;// ���¿�

	private static final int CALENDAR = 9;// calendar��activity�ı�ʶ��
	private static final int ADDNOTE = 10;// AddActivity�ı�ʶ��
	private static final int EDITNOTE = 11;// EditActivity�ı�ʶ��

	private RelativeLayout editTopLayout, searchTitle;
	private LinearLayout bottomLayout;// �ײ�ѡ���������
	private ListView noteListView;
	private MyListAdapter listAdapter = null;
	private LinearLayout showEmptyView;

	private SlideHolder mSlideHolder;// �����ؼ�

	private ImageButton seeAllButton, addNoteButton, addTypeButton,
			startSearchButton, voiceSearchButton;// ��ʾ������ť����Ӽ��±���ť��������鰴ť����ʼ������ť���������밴ť
	private ImageButton deleteButton, shareButton, moveButton, lockedButton,
			unlockedButton;// ɾ����ť������ť���ƶ���ť,������ť
	private Button showSearchButton, calendarButton, albumButton, passwdButton,
			feedbackButton, shareAppButton, aboutButton;// ������������ť�����������ť��ͼƬ��ᰴť���������ť��������ť������Ӧ�ð�ť�����ڰ�ť
	private ImageButton changeOrderButton;// �ı���±�����ʽ��ť

	private USCRecognizerDialog voiceRecognizerDialog;// ����ʶ���

	private RadioGroup typeGroup;// ���±�������
	private TextView showTypeView, showSelectView;// ��ʾ������ķ�����view����ѡʱ��ʾѡ���˶��ٸ�
	private TextView showNowDateView;// ��ʾ��ǰ����

	private NoteTypeDao noteTypeDao = null;
	private NoteDetailDao noteDetailDao = null;

	private String selectedTypeNameString;// ��¼����ѡ�еķ�����
	private int selectedTypeId;// ��¼����ѡ�е�radiobutton��id

	private EditText typeEditText;// dialog�е�edittext
	private EditText searchEditText;

	private int currentGroupIndex = 1;// ��ǰѡ�е����

	List<Map<String, Object>> typeList;// ������Ϣ����
	List<Map<String, Object>> noteList;// ���±�������Ϣ����
	int selectItemNum;// ������ѡ���item��

	boolean editmodel = false;// �жϵ�ǰ�Ƿ�Ϊ�༭״̬

	SharedPreferences sharedPreferences = null;// ��ȡϵͳ����
	FeedbackAgent agent;// �û�����
	UMSocialService umSocialService;// app����

	ProgressDialog updateProgressDialog;// ���½��ȿ�

	int nowPosition = 0;// ��ǰ�û����ѡ���item��position
	// ÿ��note����ɫ
	private int[] noteBgs = { R.drawable.note_item_bg_green,
			R.drawable.note_item_bg_blue, R.drawable.note_item_bg_organ,
			R.drawable.note_item_bg_pink, R.drawable.note_item_bg_yellow };

	private long exitTime = 0;// �˳�ʱ��

	private String orderString = "desc";// ���±�����˳��Ĭ��Ϊ����

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		overridePendingTransition(R.anim.fade_in, 0);

		// ��ʼ���ؼ�
		initWidget();
		// adapter��list
		noteList = noteDetailDao.getAllNoteForListView(currentGroupIndex,
				orderString);

		// ��ʼ���û�����
		agent = new FeedbackAgent(this);
		// ˢ�»ظ�
		agent.sync();

		// ��������SSO handler
		umSocialService = UMServiceFactory.getUMSocialService(
				MainActivity.class.getName(), RequestType.SOCIAL);
		umSocialService.getConfig().setSinaSsoHandler(new SinaSsoHandler());

		// ������
		UmengUpdateAgent.update(this);

		// ��ȡϵͳ����
		sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

		// ��typeGroup�иı䰴ťʱ
		typeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				// �ı�radiobutton������ɫ
				changeRadioButoonColor();

				RadioButton radioButton = (RadioButton) findViewById(checkedId);
				String typeName = radioButton.getText().toString();
				showTypeView.setText(typeName);

				// ��ȡ�����ȫ�����±�
				if (typeName.equals("ȫ��")) {
					// ����ǡ�ȫ��������ʾȫ�����±�
					currentGroupIndex = 1;
				} else {
					// ������ݷ�������ȡtid�ҳ�ȫ�����±�
					currentGroupIndex = noteTypeDao.getTid(typeName);
				}
				// ˢ��listview
				showListView();

			}
		});

		// Ĭ��ѡ��"ȫ��"����
		SelectOneType(0);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// ��ʼ��gallery������ֵ
		AddNoteActivity.GALLERY_INDEX = 0;
		// ��������ͳ��
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// ֹͣ����ͳ��
		MobclickAgent.onPause(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("Ϊʲôû��");
		switch (requestCode) {
		case CALENDAR:
			if (resultCode == RESULT_OK) {
				List<Map<String, Object>> list = (List<Map<String, Object>>) data
						.getSerializableExtra("list");
				showListView(list);
			}
			break;

		case ADDNOTE:
			if (resultCode == RESULT_OK) {
				showListView();
			}
			break;

		case EDITNOTE:
			if (resultCode == RESULT_OK) {
				showListView();
			}
			break;

		default:
			/**
			 * ʹ��SSO������ӣ�ָ����ȡ��Ȩ��Ϣ�Ļص�ҳ�棬������SDK���д���
			 */
			UMSsoHandler sinaSsoHandler = umSocialService.getConfig()
					.getSinaSsoHandler();
			if (sinaSsoHandler != null
					&& requestCode == UMSsoHandler.DEFAULT_AUTH_ACTIVITY_CODE) {
				sinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
			break;
		}

	}

	/**
	 * ���Radiobutton��ǰѡ��״̬������������ɫ�ı�
	 */
	public void changeRadioButoonColor() {
		for (int i = 0; i < typeGroup.getChildCount(); i++) {
			RadioButton radioButton = (RadioButton) typeGroup.getChildAt(i);
			if (radioButton.isChecked()) {
				radioButton.setTextColor(Color.BLACK);
			} else {
				radioButton.setTextColor(Color.WHITE);
			}
		}
	}

	/**
	 * ��ʼ���ؼ�
	 */
	public void initWidget() {
		mSlideHolder = (SlideHolder) findViewById(R.id.slideHolder);
		editTopLayout = (RelativeLayout) findViewById(R.id.edit_top_title);
		searchTitle = (RelativeLayout) findViewById(R.id.main_top_search);
		bottomLayout = (LinearLayout) findViewById(R.id.main_bottom);
		seeAllButton = (ImageButton) findViewById(R.id.seeallbtn);
		addNoteButton = (ImageButton) findViewById(R.id.addnotebtn);
		addTypeButton = (ImageButton) findViewById(R.id.addtype_btn);
		deleteButton = (ImageButton) findViewById(R.id.delete_btn_main);
		shareButton = (ImageButton) findViewById(R.id.share_btn_main);
		moveButton = (ImageButton) findViewById(R.id.move_btn_main);
		lockedButton = (ImageButton) findViewById(R.id.locked_btn_main);
		unlockedButton = (ImageButton) findViewById(R.id.unlocked_btn_main);
		showTypeView = (TextView) findViewById(R.id.showtype);
		showSelectView = (TextView) findViewById(R.id.showselect);
		showNowDateView = (TextView) findViewById(R.id.nowdate_tv);
		typeGroup = (RadioGroup) findViewById(R.id.typegroup);
		noteListView = (ListView) findViewById(R.id.notelist);
		showEmptyView = (LinearLayout) findViewById(R.id.showempty_view);
		showSearchButton = (Button) findViewById(R.id.searchbtn_sidebar);
		calendarButton = (Button) findViewById(R.id.calendarbtn_sidebar);
		albumButton = (Button) findViewById(R.id.albumbtn_sidebar);
		passwdButton = (Button) findViewById(R.id.passwdbtn_sidebar);
		feedbackButton = (Button) findViewById(R.id.feedbackbtn_sidebar);
		shareAppButton = (Button) findViewById(R.id.shareappbtn_sidebar);
		aboutButton = (Button) findViewById(R.id.aboutbtn_sidebar);
		startSearchButton = (ImageButton) findViewById(R.id.startsearch_btn);
		voiceSearchButton = (ImageButton) findViewById(R.id.voice_search_btn);
		searchEditText = (EditText) findViewById(R.id.edittext_search);
		changeOrderButton = (ImageButton) findViewById(R.id.changebtn);

		seeAllButton.setOnClickListener(this);
		addNoteButton.setOnClickListener(this);
		addTypeButton.setOnClickListener(this);
		deleteButton.setOnClickListener(this);
		shareButton.setOnClickListener(this);
		moveButton.setOnClickListener(this);
		lockedButton.setOnClickListener(this);
		unlockedButton.setOnClickListener(this);
		showSearchButton.setOnClickListener(this);
		startSearchButton.setOnClickListener(this);
		calendarButton.setOnClickListener(this);
		albumButton.setOnClickListener(this);
		passwdButton.setOnClickListener(this);
		feedbackButton.setOnClickListener(this);
		shareAppButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
		voiceSearchButton.setOnClickListener(this);
		changeOrderButton.setOnClickListener(this);
		noteListView.setOnItemClickListener(this);
		noteListView.setOnItemLongClickListener(this);

		noteTypeDao = new NoteTypeDao(MainActivity.this);

		// ��ʼ������ʶ����
		initVoiceRecognizer();

		// ��ȡ���з���
		typeList = noteTypeDao.getAllTyep();
		// ��̬����radiobutton
		for (int i = 0; i < typeList.size(); i++) {
			AddRadioButton(typeList.get(i).get("type").toString());
		}

		// ��ʼ�����еļ��±�����ʾ
		noteDetailDao = new NoteDetailDao(MainActivity.this);

		// ��ʾ��ǰʱ��
		showNowDate();

		// ������Ҫ�õ����ļ���
		createDir();
	}

	/**
	 * ��ʼ������ʶ����
	 */
	public void initVoiceRecognizer() {
		// ����ʶ��Ի���
		voiceRecognizerDialog = new USCRecognizerDialog(this,
				"443xlyuwfbzli6cc4p7cfo2ihrnf7wcsblwkurqo");
		// ����ʶ������
		voiceRecognizerDialog.setEngine("general");
		// ʶ�����ص�������
		voiceRecognizerDialog.setListener(voiceSearchListener);
	}

	/**
	 * ��textview����ʾ��ǰʱ��
	 */
	public void showNowDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy��MM��dd��");
		showNowDateView.setText(format.format(new Date()));
	}

	/**
	 * ����Ӧ��ר�ŵ��ļ���
	 */
	public void createDir() {
		String path = Utils.PIC_PATH;

		File file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}

		path = Utils.VOI_PATH;

		file = new File(path);

		if (!file.exists()) {
			file.mkdirs();
		}

		path = Utils.VID_PATH;
		file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

	}

	/**
	 * ��ʾlistview������
	 */
	public void showListView() {
		noteList = noteDetailDao.getAllNoteForListView(currentGroupIndex,
				orderString);
		if (noteList.size() > 0) {
			listAdapter = new MyListAdapter(this, noteList);
			noteListView.setVisibility(View.VISIBLE);
			showEmptyView.setVisibility(View.GONE);
			noteListView.setAdapter(listAdapter);
		} else {
			noteListView.setVisibility(View.GONE);
			showEmptyView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * ���ݸ���list��ʾlistview����
	 * 
	 * @param list
	 */
	public void showListView(List<Map<String, Object>> list) {
		noteList = list;
		listAdapter = new MyListAdapter(this, noteList);
		noteListView.setVisibility(View.VISIBLE);
		showEmptyView.setVisibility(View.GONE);
		noteListView.setAdapter(listAdapter);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.seeallbtn:
			mSlideHolder.toggle();
			break;

		case R.id.addnotebtn:
			intent = new Intent(MainActivity.this, AddNoteActivity.class);
			intent.putExtra("index", currentGroupIndex);
			startActivityForResult(intent, ADDNOTE);
			break;

		case R.id.addtype_btn:
			showDialog(ADDTYPE);
			break;

		case R.id.delete_btn_main:

			showDialog(SURE_DEL_NOTE);

			break;

		case R.id.share_btn_main:
			int which = 0;// �ж�ѡ���ĸ�һ�����±�
			for (int i = 0; i < listAdapter.map.size(); i++) {
				if (listAdapter.map.get(i)) {
					which = i;
					break;
				}
			}

			// ͨ��which��ȡ���±���nid,�ٻ�ȡ�ü��±��Ļ�����Ϣ
			NoteDetail noteDetail = noteDetailDao.getOneNote(noteList
					.get(which).get("nid").toString());

			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/*");
			// �����ʼ�Ĭ�ϱ���
			intent.putExtra(Intent.EXTRA_SUBJECT, "������ʹ��XNote�������±���");

			// ����ͼƬ
			if (noteList.get(which).get("pic") != null) {
				File file = new File(Utils.PIC_PATH
						+ noteList.get(which).get("pic"));
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			}

			// ���������
			intent.putExtra(Intent.EXTRA_TEXT,
					"����XNoteд��һƪ���£�\n" + noteDetail.getContent());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// gettitle��ȡapp�����֣���ʾ��������
			startActivity(Intent.createChooser(intent, getTitle()));
			break;

		case R.id.move_btn_main:
			showDialog(SELECT_GROUP);
			break;

		case R.id.searchbtn_sidebar:

			if (listAdapter == null) {
				Toast.makeText(MainActivity.this, "���޼��±������ܽ�����������",
						Toast.LENGTH_SHORT).show();
			} else {
				// ��ȡ����
				mSlideHolder.toggle();
				toSearch();
			}

			break;

		case R.id.startsearch_btn:
			if (searchEditText.getText().toString().trim().equals("")) {
				searchEditText.setError("����������");
			} else {
				String searchString = prepareSearch(searchEditText.getText()
						.toString());

				System.out.println("���----------" + currentGroupIndex);
				List<Map<String, Object>> searchList = noteDetailDao
						.getSearchResult(currentGroupIndex, searchString);
				if (searchList.size() > 0) {
					toNormal();
					// ���������ʧ
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(MainActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					showListView(searchList);

				} else {
					searchEditText.setError("�Բ������������");
				}

			}
			break;

		case R.id.calendarbtn_sidebar:
			// �������
			mSlideHolder.toggle();
			// ѡ�С�ȫ����
			SelectOneType(0);
			intent = new Intent(MainActivity.this, CalendarActivity.class);
			startActivityForResult(intent, CALENDAR);

			break;

		case R.id.albumbtn_sidebar:
			PictureDao pictureDao = new PictureDao(this);
			if (pictureDao.checkHasPic()) {
				intent = new Intent(MainActivity.this, AlbumActivity.class);
				startActivity(intent);
			} else {
				Toast toast = Toast.makeText(this, "���ļ��±��л�û�������ƬŶ��",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

			break;

		case R.id.passwdbtn_sidebar:

			// �鿴�û��Ƿ����ù����룬�����򴴽�����
			if (Utils.getPasswd(sharedPreferences) == null) {
				showDialog(CREATE_PASSWD);
			} else {
				intent = new Intent(MainActivity.this,
						EncryptSettingActivity.class);
				startActivity(intent);
			}

			break;

		case R.id.feedbackbtn_sidebar:
			agent.startFeedbackActivity();
			break;

		case R.id.shareappbtn_sidebar:
			// UMServiceFactory.shareTo(MainActivity.this,
			// "��ʹ���˿��ٷ���ӿڣ�UMServiceFactory.share��", null);

			// UMSocialService controller = UMServiceFactory.getUMSocialService(
			// MainActivity.class.getName(), RequestType.SOCIAL);
			umSocialService
					.setShareContent("������ʹ��һ���ǿ��ļ��±�XNote�����Ի��Ĺ��ߺͶ�ý��ļ�¼���Ұ������֣�����Ҳ�Ͽ�ȥ���ذɣ�����׿Ӧ���г�������XNote��!");// ���÷�����������

			// ���÷���ͼƬ
			UMImage shareImage = new UMImage(MainActivity.this,
					getString(R.string.poster_url));

			umSocialService.setShareMedia(shareImage);

			UMWXHandler.WX_APPID = "wxab4a81c9399bba08";// ����΢�ŵ�Appid

			// ���΢��ƽ̨
			umSocialService.getConfig().supportWXPlatform(MainActivity.this);

			// ���΢������Ȧ
			umSocialService.getConfig().supportWXPlatform(
					MainActivity.this,
					UMServiceFactory.getUMWXHandler(MainActivity.this)
							.setToCircle(true));
			// ΢��ͼ�ķ����������һ��url,Ĭ��"http://www.umeng.com"
			UMWXHandler.CONTENT_URL = getString(R.string.poster_url);

			umSocialService.openShare(MainActivity.this, false);
			break;

		case R.id.aboutbtn_sidebar:

			intent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(intent);

			break;

		case R.id.voice_search_btn:
			// ��ʾ����������ʾ��
			voiceRecognizerDialog.show();
			break;

		case R.id.changebtn:

			if (orderString.contains("asc")) {
				// ���Ϊ˳�����Ϊ����
				orderString = orderString.replace("asc", "desc");
			} else {
				// ���Ϊ�������Ϊ˳��
				orderString = orderString.replace("desc", "asc");
			}

			showListView();

			break;

		case R.id.locked_btn_main:

			// �鿴�û��Ƿ����ù����룬�����򴴽�����
			if (Utils.getPasswd(sharedPreferences) == null) {
				showDialog(CREATE_PASSWD);
			} else {
				for (int i = 0; i < listAdapter.map.size(); i++) {
					// �ҳ�ѡ�еļ��±�������
					if (listAdapter.map.get(i)) {
						noteDetailDao.updateNoteLock(noteList.get(i).get("nid")
								.toString(), 1);
					}
				}

				Toast.makeText(MainActivity.this, "�����ɹ���", Toast.LENGTH_SHORT)
						.show();
				// �ص�����ģʽ
				toNormal();
				// ˢ���б�
				showListView();
			}

			break;

		case R.id.unlocked_btn_main:
			boolean hasLocked=false;
			//�ж��Ƿ�ѡ�еļ��±���δ����
			for (int i = 0; i < listAdapter.map.size(); i++) {
				// �ҳ�ѡ�еļ��±�
				if (listAdapter.map.get(i)) {
					if (noteList.get(i).get("locked")
							.toString().equals("1")) {
						hasLocked=true;
					}
				}
			}
			//���ѡ�еĶ�δ������ֱ�ӷ���
			if (!hasLocked) {
				Toast.makeText(MainActivity.this, "�ף�û�м�����ô������?!", Toast.LENGTH_SHORT).show();
				return;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("������������н�����");
			final EditText editText = new EditText(MainActivity.this);
			editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			editText.setHint("����������");
			builder.setNegativeButton("ȡ��", null);
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String passwdString = editText.getText().toString();
							// ������ȷ
							if (passwdString.equals(Utils
									.getPasswd(sharedPreferences))) {
								for (int i = 0; i < listAdapter.map.size(); i++) {
									// �ҳ�ѡ�еļ��±�������
									if (listAdapter.map.get(i)) {
										noteDetailDao.updateNoteLock(noteList
												.get(i).get("nid").toString(),
												0);
									}
								}

								Toast.makeText(MainActivity.this, "������ȷ�������ɹ���",
										Toast.LENGTH_SHORT).show();

								toNormal();
								// ˢ���б�
								showListView();
							} else {
								Toast.makeText(MainActivity.this, "�������,����ʧ�ܣ�",
										Toast.LENGTH_SHORT).show();
							}
						}
					});

			builder.setView(editText);
			builder.show();

			break;

		}

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub
		switch (id) {
		case EDITTYPE:
			typeEditText.setText(selectedTypeNameString);
			break;

		default:
			break;
		}

		super.onPrepareDialog(id, dialog);
	}

	// ��������һ��
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		LinearLayout linearLayout = null;
		switch (id) {
		case ADDTYPE:
			builder.setTitle("��ӷ���");
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.addtype_dialog_layout, null);
			final EditText editText = (EditText) linearLayout
					.findViewById(R.id.addtype_edit);
			builder.setCancelable(false);
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String string = editText.getText().toString()
									.trim();

							if (!string.equals("")) {
								// �жϳ���
								if (string.length() > 10) {
									editText.setError("���Ȳ��ܳ���10���ַ���");
									keepDialog(dialog);
								} else {
									// �ж������Ƿ��Ѵ���
									if (!noteTypeDao.isExist(string)) {
										noteTypeDao.addNewType(string);
										AddRadioButton(string);

										Toast.makeText(MainActivity.this,
												"������·���'" + string + "'",
												Toast.LENGTH_SHORT).show();

										// ������ӵķ���Ϊѡ��״̬
										SelectOneType(typeGroup.getChildCount() - 1);

										destoryDialog(dialog);
									} else {
										editText.setError("�������Ѵ���!");
										keepDialog(dialog);
									}
								}

							} else {
								editText.setError("����Ϊ��!");
								keepDialog(dialog);
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

		case SELECT_OPR:
			builder.setTitle("��ѡ�����:");
			builder.setItems(R.array.selecthandle,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

							if (which == 0) {
								showDialog(EDITTYPE);
							} else {
								showDialog(SURE_DEL_TYPE);
							}
						}
					});
			return builder.create();

		case SURE_DEL_TYPE:
			builder.setTitle("ȷ��ɾ����?");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// �����ݿ���ɾ��
							noteTypeDao.deleteType(selectedTypeNameString);
							typeGroup.removeView(findViewById(selectedTypeId));// ͨ��idɾ��radiobutton

							Toast.makeText(MainActivity.this, "ɾ���ɹ�",
									Toast.LENGTH_SHORT).show();

							// ���ɾ�������Ϊ��ǰѡ�е��飬��ɾ�����á�ȫ����Ϊѡ��״̬
							if (typeGroup.getCheckedRadioButtonId() == selectedTypeId) {
								SelectOneType(0);
							}

						}
					});
			builder.setNegativeButton("��", null);
			return builder.create();

		case EDITTYPE:
			builder.setTitle("�޸ķ�������");
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.addtype_dialog_layout, null);
			typeEditText = (EditText) linearLayout
					.findViewById(R.id.addtype_edit);
			typeEditText.setText(selectedTypeNameString);

			builder.setCancelable(false);
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String string = typeEditText.getText().toString()
									.trim();

							if (!string.equals("")) {
								// �жϳ���
								if (string.length() > 10) {
									typeEditText.setError("���Ȳ��ܳ���10���ַ���");
									keepDialog(dialog);
								} else {
									// �ж������Ƿ����
									if (!noteTypeDao.isExist(string)) {
										// �޸����ݿ��е���Ϣ
										noteTypeDao.updateType(
												selectedTypeNameString, string);
										// �޸���ʾ
										RadioButton radioButton = (RadioButton) findViewById(selectedTypeId);
										radioButton.setText(string);

										showTypeView.setText(string);

										Toast.makeText(MainActivity.this,
												"�޸ĳɹ�", Toast.LENGTH_SHORT)
												.show();
										destoryDialog(dialog);
									} else {
										typeEditText.setError("�������Ѵ���!");
										keepDialog(dialog);
									}
								}

							} else {
								typeEditText.setError("����Ϊ��!");
								keepDialog(dialog);
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

		case SURE_DEL_NOTE:
			builder.setTitle("ȷ��ɾ����ѡ���±���");
			builder.setPositiveButton("��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							List<String> selectedList = new ArrayList<String>();

							for (int i = 0; i < listAdapter.map.size(); i++) {
								// �ҳ�ѡ�еļ��±�
								if (listAdapter.map.get(i)) {
									selectedList.add(noteList.get(i).get("nid")
											.toString());
									// deleteSelectedNote(noteList.get(i)
									// .get("nid").toString());
									// ����м������±����ܽ���ɾ��
									if (noteList.get(i).get("locked")
											.toString().equals("1")) {
										Toast.makeText(MainActivity.this,
												"ѡ�еļ��±��а����������£����ܽ���ɾ����",
												Toast.LENGTH_SHORT).show();
										return;
									}
								}
							}

							// ɾ����ѡ���±�
							for (int j = 0; j < selectedList.size(); j++) {
								deleteSelectedNote(selectedList.get(j));
							}

							// �ص�����ģʽ
							toNormal();
							// ˢ��listview
							showListView();
						}
					});
			builder.setNegativeButton("��", null);

			return builder.create();

		case SELECT_GROUP:
			builder.setTitle("�ƶ����÷���:");
			// ��ȡ���з���
			typeList = noteTypeDao.getAllTyep();
			CharSequence[] items = new CharSequence[typeList.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = typeList.get(i).get("type").toString();
			}
			builder.setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int which) {
					// TODO Auto-generated method stub
					// �޸�����ѡ�еļ��±����ڷ���
					for (int i = 0; i < listAdapter.map.size(); i++) {
						if (listAdapter.map.get(i)) {
							noteDetailDao.updateNoteType(
									noteList.get(i).get("nid").toString(),
									typeList.get(which).get("tid").toString());
						}
					}
					toNormal();
					showListView();
				}
			});

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
								// ��������
								Utils.savePasswd(sharedPreferences,
										passwdString);
								Toast.makeText(MainActivity.this, "���봴���ɹ�",
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

		case INPUT_PASSWD:
			builder.setTitle("�ü��±��Ѽ���");
			builder.setCancelable(false);
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.createpasswd_dialog_layout, null);
			final EditText inputEditText = (EditText) linearLayout
					.findViewById(R.id.addpasswd_edit);
			builder.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String passwdString = inputEditText.getText()
									.toString();
							// ��֤ͨ���������±�
							if (passwdString.equals(Utils
									.getPasswd(sharedPreferences))) {
								destoryDialog(dialog);

								Intent intent = new Intent(MainActivity.this,
										EditNoteActivity.class);
								intent.putExtra("nid", noteList
										.get(nowPosition).get("nid").toString());
								startActivityForResult(intent, EDITNOTE);

								Toast.makeText(MainActivity.this, "������ȷ!",
										Toast.LENGTH_SHORT).show();
								
								// ��������
								inputEditText.setText("");

							} else {
								keepDialog(dialog);
								inputEditText.setError("�������");

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

		case UPDATE:
			updateProgressDialog = new ProgressDialog(MainActivity.this);
			updateProgressDialog.setMessage("�������ظ��°�...");
			updateProgressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			updateProgressDialog.setCancelable(false);
			updateProgressDialog.show();
			return updateProgressDialog;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * ��̬���Radiobutton
	 * 
	 * @param name
	 *            ������
	 */
	public void AddRadioButton(String name) {
		TypeRadioButton radioButton = new TypeRadioButton(this, name);
		typeGroup.addView(radioButton, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		radioButton.setOnLongClickListener(this);

	}

	/**
	 * ÿ�����鰴ť�ĳ���ʱ��
	 */
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		RadioButton radioButton = (RadioButton) arg0;
		String typeName = radioButton.getText().toString();
		if (!typeName.equals("ȫ��")) {
			selectedTypeNameString = typeName;
			selectedTypeId = radioButton.getId();
			showDialog(SELECT_OPR);
		} else {
			Toast.makeText(MainActivity.this, "�÷��鲻�����޸ģ�", Toast.LENGTH_SHORT)
					.show();
		}
		return false;
	}

	/**
	 * ��ĳһ������Ϊ��ǰѡ��״̬��Ȼ������check�ļ���
	 * 
	 * @param id
	 */
	public void SelectOneType(int id) {
		RadioButton radioButton = (RadioButton) typeGroup.getChildAt(id);
		radioButton.setChecked(true);
	}

	/**
	 * ɾ��ѡ�еļ��±������Ұ������Ķ�ý���ļ���Sdcard��ɾ��
	 * 
	 * @param nid
	 */
	public void deleteSelectedNote(String nid) {
		PictureDao pictureDao = new PictureDao(MainActivity.this);
		VoiceDao voiceDao = new VoiceDao(MainActivity.this);
		VideoDao videoDao = new VideoDao(MainActivity.this);

		List<PictureModel> picList = pictureDao.getPicFromNid(nid);
		// ����ͼƬ����ɾ��SDcard�е�ͼƬ
		if (picList.size() > 0) {
			FileUtil.deleteMediaInSdcard(picList);
		}

		List<String> voiList = voiceDao.getVoiFromNid(nid);
		// ����¼������ɾ��SDcard�е�¼��
		if (voiList.size() > 0) {
			FileUtil.deleteMediaInSdcard(voiList, Utils.VOI_PATH);
		}
		// ������Ƶ����ɾ��SDcard�е���Ƶ
		List<String> videoList = videoDao.getVideoFromNid(nid);
		if (videoList.size() > 0) {
			FileUtil.deleteMediaInSdcard(videoList, Utils.VID_PATH);
		}

		// ɾ�����ݿ�������,����ɾ��note������������ɾ��
		noteDetailDao.deleteNote(nid);

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
	 * �ڲ���ʵ���Զ���listview
	 * 
	 * @author �����
	 * 
	 */
	public class MyListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<Map<String, Object>> list;
		private Context context;

		private boolean edit = false;

		Map<Integer, Boolean> map;// ����item��ѡ�е�״̬

		public MyListAdapter(Context context, List<Map<String, Object>> list) {
			super();
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			this.inflater = LayoutInflater.from(context);

			initMap();
		}

		/**
		 * ��ʼ��checkbox״̬Ϊδѡ��
		 */
		public void initMap() {
			map = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size(); i++) {
				map.put(i, false);
			}
		}

		/**
		 * ���õ�ǰlist�Ƿ�Ϊ�༭״̬
		 * 
		 * @param model
		 *            �Ƿ�༭״̬
		 */
		public void setEdit(boolean model) {
			edit = model;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder = null;
			if (convertView == null) {
				System.out.println("��ʼ��adapter");
				convertView = inflater.inflate(R.layout.note_item, null);
				holder = new ViewHolder();

				holder.noteItemLayout = (LinearLayout) convertView
						.findViewById(R.id.noteitem);

				holder.contentView = (TextView) convertView
						.findViewById(R.id.content_tv);
				holder.dateView = (TextView) convertView
						.findViewById(R.id.showdate_tv);
				holder.lockedView = (ImageView) convertView
						.findViewById(R.id.locked_signal);
				holder.remindView = (ImageView) convertView
						.findViewById(R.id.reminded_signal);

				holder.picView = (ImageView) convertView
						.findViewById(R.id.showpic_iv);

				convertView.setTag(holder);
			} else {
				System.out.println("���ó�ʼ��adapter");
				holder = (ViewHolder) convertView.getTag();

			}

			if (list != null) {

				// holder.noteItemLayout.setBackground(getResources().getDrawable(
				// noteBgs[position % 5]));
				holder.noteItemLayout
						.setBackgroundResource(noteBgs[position % 5]);

				String nContent = list.get(position).get("content").toString();// ��ȡ��������
				String nDate = list.get(position).get("date").toString();// ��ȡ������Ϣ
				int nLocked = Integer.parseInt(list.get(position).get("locked")
						.toString());// ��ȡ�Ƿ����
				Object nRemind = list.get(position).get("remind");// �Ƿ���������
				final String nPic = list.get(position).get("pic").toString();// ��ȡͼƬ��Ϣ

				// ��ʾ��������
				if (nContent.trim().equals("")) {
					nContent = "������";
					holder.contentView.setText(nContent);
				} else {
					FaceParser faceParser = new FaceParser(context);
					holder.contentView.setText((faceParser.replace(nContent)));
				}

				// ��ʾ����
				holder.dateView.setText(nDate);

				// �Ƿ����
				holder.lockedView.setVisibility(View.GONE);
				if (nLocked == 1) {
					holder.lockedView.setVisibility(View.VISIBLE);
				}

				// �Ƿ�����������
				holder.remindView.setVisibility(View.GONE);
				if (nRemind != null) {
					// �����Ƿ����
					if (!Utils.checkOverdue(nRemind.toString())) {
						holder.remindView.setVisibility(View.VISIBLE);
					}
				}

				// ����ͼƬ����
				holder.picView.setVisibility(View.GONE);
				holder.picView.setImageDrawable(getResources().getDrawable(
						R.drawable.empty_icon));
				// ��ʾͼƬ
				if (!nPic.equals("")) {

					// holder.picView.setTag(nPic);

					holder.picView.setVisibility(View.VISIBLE);
					holder.picView.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							// �����ǰ����Ϊ��ʾ״̬�����ò�������
							if (mSlideHolder.isOpened()) {
								mSlideHolder.toggle();
							} else {
								Intent intent = new Intent(MainActivity.this,
										ShowOnePicActivity.class);
								intent.putExtra("picPath", Utils.PIC_PATH
										+ nPic);
								startActivity(intent);
								overridePendingTransition(
										R.anim.alpha_scale_in, 0);

							}

						}
					});

					Utils.loadPic(holder.picView, Utils.PIC_PATH + nPic);

				}

				if (edit) {
					holder.checkBox = (CheckBox) convertView
							.findViewById(R.id.multiple_checkbox);
					// ��checkbox�ɼ�
					holder.checkBox.setVisibility(View.VISIBLE);
					// �״γ����ø�itemΪѡ��״̬
					holder.checkBox.setChecked(map.get(position));
				}

			}

			return convertView;
		}
	}

	/**
	 * ���������Ļص�����
	 */
	public USCRecognizerDialogListener voiceSearchListener = new USCRecognizerDialogListener() {

		// ʶ�����ص��ӿ�
		public void onResult(String result, boolean arg1) {
			// TODO Auto-generated method stub
			// �����ľ��ȥ��
			String string = result.replace("��", "");
			searchEditText.append(string);
		}

		// ʶ������ص��ӿ�
		public void onEnd(USCError arg0) {
			// TODO Auto-generated method stub

		}
	};

	// list��item�ļ����¼�
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (editmodel) {
			CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.multiple_checkbox);

			checkBox.toggle();

			listAdapter.map.put(position, checkBox.isChecked());

			// ��¼ѡ��item����Ŀ
			if (checkBox.isChecked()) {
				selectItemNum++;
			} else {
				selectItemNum--;
			}

			showSelectView.setText("��ѡ��" + selectItemNum + "��");

			// ����ѡ��item��Ŀ��ʾ��������Ӧ��btn
			switch (selectItemNum) {
			case 0:
				deleteButton.setVisibility(View.GONE);
				shareButton.setVisibility(View.GONE);
				moveButton.setVisibility(View.GONE);
				lockedButton.setVisibility(View.GONE);
				unlockedButton.setVisibility(View.GONE);
				break;

			case 1:
				deleteButton.setVisibility(View.VISIBLE);
				shareButton.setVisibility(View.VISIBLE);
				moveButton.setVisibility(View.VISIBLE);
				lockedButton.setVisibility(View.VISIBLE);
				unlockedButton.setVisibility(View.VISIBLE);
				break;
			default:
				deleteButton.setVisibility(View.VISIBLE);
				moveButton.setVisibility(View.VISIBLE);
				shareButton.setVisibility(View.GONE);
				lockedButton.setVisibility(View.VISIBLE);
				unlockedButton.setVisibility(View.VISIBLE);

				break;
			}

		} else {
			// ����ü��±��Ѽ�������ʾ
			if (noteList.get(position).get("locked").toString().equals("1")) {
				// ��¼��ǰposition
				nowPosition = position;
				showDialog(INPUT_PASSWD);
			} else {
				Intent intent = new Intent(MainActivity.this,
						EditNoteActivity.class);
				intent.putExtra("nid", noteList.get(position).get("nid")
						.toString());
				startActivityForResult(intent, EDITNOTE);
			}

		}

	}

	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			int position, long id) {
		// TODO Auto-generated method stub

		if (!editmodel) {
			// ��ʾcheckbox�ͳ�ʼ������
			CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.multiple_checkbox);
			checkBox.toggle();
			listAdapter.map.put(position, checkBox.isChecked());

			selectItemNum = 0;
			selectItemNum++;
			showSelectView.setText("��ѡ��" + selectItemNum + "��");
			// �л����༭ģʽ
			toEdit();

		}

		return true;
	}

	/**
	 * �ص�����ѡ��ģʽ
	 */
	public void toNormal() {
		editmodel = false;
		listAdapter.setEdit(false);
		listAdapter.initMap();
		noteListView.setAdapter(listAdapter);

		if (editTopLayout.getVisibility() == View.VISIBLE) {
			editTopLayout.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
			bottomLayout.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_bottom_out));
			editTopLayout.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_top_out));
		}

		if (searchTitle.getVisibility() == View.VISIBLE) {
			searchTitle.setVisibility(View.GONE);
			searchTitle.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.push_top_out));
		}

		// �ָ���������
		mSlideHolder.setEnabled(true);
		seeAllButton.setEnabled(true);// �ָ�������ť����
		addNoteButton.setEnabled(true);// �ָ���Ӱ�ť����
	}

	/**
	 * ����༭ģʽ
	 */
	public void toEdit() {
		editmodel = true;
		listAdapter.setEdit(true);
		noteListView.setAdapter(listAdapter);

		editTopLayout.setVisibility(View.VISIBLE);
		bottomLayout.setVisibility(View.VISIBLE);

		bottomLayout.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_bottom_in));
		editTopLayout.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_top_in));

		// ����������
		mSlideHolder.setEnabled(false);
		seeAllButton.setEnabled(false);// ������ť������
		addNoteButton.setEnabled(false);// ��Ӱ�ť������
		// �ض�ѡ����һ�����ȫ����ť��Ϊ�ɼ�
		deleteButton.setVisibility(View.VISIBLE);
		shareButton.setVisibility(View.VISIBLE);
		moveButton.setVisibility(View.VISIBLE);
		lockedButton.setVisibility(View.VISIBLE);
		unlockedButton.setVisibility(View.VISIBLE);
	}

	/**
	 * ��������ģʽ
	 */
	public void toSearch() {
		editmodel = true;
		searchTitle.setVisibility(View.VISIBLE);// ���������ɼ�
		searchEditText.setText("");// ������������

		seeAllButton.setEnabled(false);// ������ť������
		addNoteButton.setEnabled(false);// ��Ӱ�ť������

		searchTitle.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_top_in));
	}

	/**
	 * ��������ǰ׼�������ַ�������%
	 * 
	 * @param input
	 * @return
	 */
	public String prepareSearch(String input) {
		StringBuffer output = new StringBuffer("%");
		for (int i = 0; i < input.length(); i++) {
			output.append(input.substring(i, i + 1));
			output.append("%");
		}
		return output.toString();
	}

	public class ViewHolder {
		public TextView contentView = null;
		public TextView dateView = null;
		public ImageView picView = null;
		public CheckBox checkBox = null;
		public ImageView lockedView = null;
		public ImageView remindView = null;
		public LinearLayout noteItemLayout = null;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {

			// �����ʱ����չ��������������
			if (mSlideHolder.isOpened()) {
				mSlideHolder.toggle();
				// ����true�ó����˳�
				return true;
			}

			// ����Ǳ༭ģʽ���˳��༭ģʽ
			if (editmodel) {
				toNormal();
				return true;
			}

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(MainActivity.this, "�ٰ�һ���˳�����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				MainActivity.this.finish();
				System.exit(0);
			}
			return true;

		}
		return super.onKeyDown(keyCode, event);
	}
}
