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

	private static final int ADDTYPE = 1;// 添加分组框
	private static final int SELECT_OPR = 2;// 长按一个分组时选择操作框
	protected static final int SURE_DEL_TYPE = 3;// 确认删除一个分组框
	protected static final int EDITTYPE = 4;// 编辑分组框
	private static final int SURE_DEL_NOTE = 5;// 确认删除所选记事本框
	private static final int SELECT_GROUP = 6;// 选择需要移动到的分组框
	private static final int CREATE_PASSWD = 7;// 创建密码输入框
	private static final int INPUT_PASSWD = 8;// 输入密码框
	private static final int UPDATE = 12;// 更新框

	private static final int CALENDAR = 9;// calendar的activity的标识符
	private static final int ADDNOTE = 10;// AddActivity的标识符
	private static final int EDITNOTE = 11;// EditActivity的标识符

	private RelativeLayout editTopLayout, searchTitle;
	private LinearLayout bottomLayout;// 底部选择操作布局
	private ListView noteListView;
	private MyListAdapter listAdapter = null;
	private LinearLayout showEmptyView;

	private SlideHolder mSlideHolder;// 侧栏控件

	private ImageButton seeAllButton, addNoteButton, addTypeButton,
			startSearchButton, voiceSearchButton;// 显示侧栏按钮，添加记事本按钮，添加新组按钮，开始搜索按钮，语音输入按钮
	private ImageButton deleteButton, shareButton, moveButton, lockedButton,
			unlockedButton;// 删除按钮，分享按钮，移动按钮,加锁按钮
	private Button showSearchButton, calendarButton, albumButton, passwdButton,
			feedbackButton, shareAppButton, aboutButton;// 侧栏中搜索按钮，日历浏览按钮，图片相册按钮，密码管理按钮，反馈按钮，分享应用按钮，关于按钮
	private ImageButton changeOrderButton;// 改变记事本排序方式按钮

	private USCRecognizerDialog voiceRecognizerDialog;// 语音识别框

	private RadioGroup typeGroup;// 记事本分组列
	private TextView showTypeView, showSelectView;// 显示主标题的分组名view；多选时显示选中了多少个
	private TextView showNowDateView;// 显示当前日期

	private NoteTypeDao noteTypeDao = null;
	private NoteDetailDao noteDetailDao = null;

	private String selectedTypeNameString;// 记录长按选中的分组名
	private int selectedTypeId;// 记录长按选中的radiobutton的id

	private EditText typeEditText;// dialog中的edittext
	private EditText searchEditText;

	private int currentGroupIndex = 1;// 当前选中的组号

	List<Map<String, Object>> typeList;// 分组信息集合
	List<Map<String, Object>> noteList;// 记事本基本信息集合
	int selectItemNum;// 保存已选择的item数

	boolean editmodel = false;// 判断当前是否为编辑状态

	SharedPreferences sharedPreferences = null;// 读取系统设置
	FeedbackAgent agent;// 用户反馈
	UMSocialService umSocialService;// app分享

	ProgressDialog updateProgressDialog;// 更新进度框

	int nowPosition = 0;// 当前用户点击选择的item的position
	// 每个note的颜色
	private int[] noteBgs = { R.drawable.note_item_bg_green,
			R.drawable.note_item_bg_blue, R.drawable.note_item_bg_organ,
			R.drawable.note_item_bg_pink, R.drawable.note_item_bg_yellow };

	private long exitTime = 0;// 退出时间

	private String orderString = "desc";// 记事本排列顺序，默认为逆序

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		overridePendingTransition(R.anim.fade_in, 0);

		// 初始化控件
		initWidget();
		// adapter绑定list
		noteList = noteDetailDao.getAllNoteForListView(currentGroupIndex,
				orderString);

		// 初始化用户反馈
		agent = new FeedbackAgent(this);
		// 刷新回复
		agent.sync();

		// 设置新浪SSO handler
		umSocialService = UMServiceFactory.getUMSocialService(
				MainActivity.class.getName(), RequestType.SOCIAL);
		umSocialService.getConfig().setSinaSsoHandler(new SinaSsoHandler());

		// 检查更新
		UmengUpdateAgent.update(this);

		// 读取系统设置
		sharedPreferences = getSharedPreferences("setting", MODE_PRIVATE);

		// 当typeGroup中改变按钮时
		typeGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				// 改变radiobutton字体颜色
				changeRadioButoonColor();

				RadioButton radioButton = (RadioButton) findViewById(checkedId);
				String typeName = radioButton.getText().toString();
				showTypeView.setText(typeName);

				// 获取分组的全部记事本
				if (typeName.equals("全部")) {
					// 如果是“全部”则显示全部记事本
					currentGroupIndex = 1;
				} else {
					// 否则根据分组名获取tid找出全部记事本
					currentGroupIndex = noteTypeDao.getTid(typeName);
				}
				// 刷新listview
				showListView();

			}
		});

		// 默认选中"全部"分组
		SelectOneType(0);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 初始化gallery的索引值
		AddNoteActivity.GALLERY_INDEX = 0;
		// 开启友盟统计
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止友盟统计
		MobclickAgent.onPause(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("为什么没有");
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
			 * 使用SSO必须添加，指定获取授权信息的回调页面，并传给SDK进行处理
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
	 * 检测Radiobutton当前选中状态，做出字体颜色改变
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
	 * 初始化控件
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

		// 初始化语音识别器
		initVoiceRecognizer();

		// 获取所有分类
		typeList = noteTypeDao.getAllTyep();
		// 动态加载radiobutton
		for (int i = 0; i < typeList.size(); i++) {
			AddRadioButton(typeList.get(i).get("type").toString());
		}

		// 初始化所有的记事本并显示
		noteDetailDao = new NoteDetailDao(MainActivity.this);

		// 显示当前时间
		showNowDate();

		// 创建需要用到的文件夹
		createDir();
	}

	/**
	 * 初始化语音识别器
	 */
	public void initVoiceRecognizer() {
		// 创建识别对话框
		voiceRecognizerDialog = new USCRecognizerDialog(this,
				"443xlyuwfbzli6cc4p7cfo2ihrnf7wcsblwkurqo");
		// 设置识别领域
		voiceRecognizerDialog.setEngine("general");
		// 识别结果回调监听器
		voiceRecognizerDialog.setListener(voiceSearchListener);
	}

	/**
	 * 在textview上显示当前时间
	 */
	public void showNowDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		showNowDateView.setText(format.format(new Date()));
	}

	/**
	 * 创建应用专门的文件夹
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
	 * 显示listview的内容
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
	 * 根据给定list显示listview内容
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
			int which = 0;// 判断选中哪个一个记事本
			for (int i = 0; i < listAdapter.map.size(); i++) {
				if (listAdapter.map.get(i)) {
					which = i;
					break;
				}
			}

			// 通过which获取记事本的nid,再获取该记事本的基本信息
			NoteDetail noteDetail = noteDetailDao.getOneNote(noteList
					.get(which).get("nid").toString());

			intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/*");
			// 设置邮件默认标题
			intent.putExtra(Intent.EXTRA_SUBJECT, "我正在使用XNote超级记事本！");

			// 分享图片
			if (noteList.get(which).get("pic") != null) {
				File file = new File(Utils.PIC_PATH
						+ noteList.get(which).get("pic"));
				intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			}

			// 分享的内容
			intent.putExtra(Intent.EXTRA_TEXT,
					"我用XNote写了一篇记事：\n" + noteDetail.getContent());
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// gettitle获取app的名字，显示在最上面
			startActivity(Intent.createChooser(intent, getTitle()));
			break;

		case R.id.move_btn_main:
			showDialog(SELECT_GROUP);
			break;

		case R.id.searchbtn_sidebar:

			if (listAdapter == null) {
				Toast.makeText(MainActivity.this, "暂无记事本，不能进行搜索服务",
						Toast.LENGTH_SHORT).show();
			} else {
				// 收取侧栏
				mSlideHolder.toggle();
				toSearch();
			}

			break;

		case R.id.startsearch_btn:
			if (searchEditText.getText().toString().trim().equals("")) {
				searchEditText.setError("请输入内容");
			} else {
				String searchString = prepareSearch(searchEditText.getText()
						.toString());

				System.out.println("组号----------" + currentGroupIndex);
				List<Map<String, Object>> searchList = noteDetailDao
						.getSearchResult(currentGroupIndex, searchString);
				if (searchList.size() > 0) {
					toNormal();
					// 让软键盘消失
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(MainActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					showListView(searchList);

				} else {
					searchEditText.setError("对不起，无搜索结果");
				}

			}
			break;

		case R.id.calendarbtn_sidebar:
			// 收起侧栏
			mSlideHolder.toggle();
			// 选中“全部”
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
				Toast toast = Toast.makeText(this, "您的记事本中还没有添加相片哦！",
						Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}

			break;

		case R.id.passwdbtn_sidebar:

			// 查看用户是否设置过密码，若无则创建密码
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
			// "我使用了快速分享接口（UMServiceFactory.share）", null);

			// UMSocialService controller = UMServiceFactory.getUMSocialService(
			// MainActivity.class.getName(), RequestType.SOCIAL);
			umSocialService
					.setShareContent("我正在使用一款功能强大的记事本XNote，个性化的工具和多媒体的记录让我爱不释手，你们也赶快去下载吧，各大安卓应用市场搜索“XNote”!");// 设置分享文字内容

			// 设置分享图片
			UMImage shareImage = new UMImage(MainActivity.this,
					getString(R.string.poster_url));

			umSocialService.setShareMedia(shareImage);

			UMWXHandler.WX_APPID = "wxab4a81c9399bba08";// 设置微信的Appid

			// 添加微信平台
			umSocialService.getConfig().supportWXPlatform(MainActivity.this);

			// 添加微信朋友圈
			umSocialService.getConfig().supportWXPlatform(
					MainActivity.this,
					UMServiceFactory.getUMWXHandler(MainActivity.this)
							.setToCircle(true));
			// 微信图文分享必须设置一个url,默认"http://www.umeng.com"
			UMWXHandler.CONTENT_URL = getString(R.string.poster_url);

			umSocialService.openShare(MainActivity.this, false);
			break;

		case R.id.aboutbtn_sidebar:

			intent = new Intent(MainActivity.this, AboutActivity.class);
			startActivity(intent);

			break;

		case R.id.voice_search_btn:
			// 显示语音输入提示框
			voiceRecognizerDialog.show();
			break;

		case R.id.changebtn:

			if (orderString.contains("asc")) {
				// 如果为顺序则改为逆序
				orderString = orderString.replace("asc", "desc");
			} else {
				// 如果为逆序则改为顺序
				orderString = orderString.replace("desc", "asc");
			}

			showListView();

			break;

		case R.id.locked_btn_main:

			// 查看用户是否设置过密码，若无则创建密码
			if (Utils.getPasswd(sharedPreferences) == null) {
				showDialog(CREATE_PASSWD);
			} else {
				for (int i = 0; i < listAdapter.map.size(); i++) {
					// 找出选中的记事本并加锁
					if (listAdapter.map.get(i)) {
						noteDetailDao.updateNoteLock(noteList.get(i).get("nid")
								.toString(), 1);
					}
				}

				Toast.makeText(MainActivity.this, "加锁成功！", Toast.LENGTH_SHORT)
						.show();
				// 回到正常模式
				toNormal();
				// 刷新列表
				showListView();
			}

			break;

		case R.id.unlocked_btn_main:
			boolean hasLocked=false;
			//判断是否选中的记事本均未加锁
			for (int i = 0; i < listAdapter.map.size(); i++) {
				// 找出选中的记事本
				if (listAdapter.map.get(i)) {
					if (noteList.get(i).get("locked")
							.toString().equals("1")) {
						hasLocked=true;
					}
				}
			}
			//如果选中的都未加锁则直接返回
			if (!hasLocked) {
				Toast.makeText(MainActivity.this, "亲，没有加锁怎么解锁呢?!", Toast.LENGTH_SHORT).show();
				return;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("请输入密码进行解锁！");
			final EditText editText = new EditText(MainActivity.this);
			editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
			editText.setHint("请输入密码");
			builder.setNegativeButton("取消", null);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String passwdString = editText.getText().toString();
							// 密码正确
							if (passwdString.equals(Utils
									.getPasswd(sharedPreferences))) {
								for (int i = 0; i < listAdapter.map.size(); i++) {
									// 找出选中的记事本并解锁
									if (listAdapter.map.get(i)) {
										noteDetailDao.updateNoteLock(noteList
												.get(i).get("nid").toString(),
												0);
									}
								}

								Toast.makeText(MainActivity.this, "密码正确，解锁成功！",
										Toast.LENGTH_SHORT).show();

								toNormal();
								// 刷新列表
								showListView();
							} else {
								Toast.makeText(MainActivity.this, "密码错误,解锁失败！",
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

	// 仅仅运行一次
	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		LinearLayout linearLayout = null;
		switch (id) {
		case ADDTYPE:
			builder.setTitle("添加分组");
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.addtype_dialog_layout, null);
			final EditText editText = (EditText) linearLayout
					.findViewById(R.id.addtype_edit);
			builder.setCancelable(false);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String string = editText.getText().toString()
									.trim();

							if (!string.equals("")) {
								// 判断长度
								if (string.length() > 10) {
									editText.setError("长度不能超过10个字符！");
									keepDialog(dialog);
								} else {
									// 判断组名是否已存在
									if (!noteTypeDao.isExist(string)) {
										noteTypeDao.addNewType(string);
										AddRadioButton(string);

										Toast.makeText(MainActivity.this,
												"已添加新分组'" + string + "'",
												Toast.LENGTH_SHORT).show();

										// 让新添加的分组为选中状态
										SelectOneType(typeGroup.getChildCount() - 1);

										destoryDialog(dialog);
									} else {
										editText.setError("该组名已存在!");
										keepDialog(dialog);
									}
								}

							} else {
								editText.setError("不能为空!");
								keepDialog(dialog);
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

		case SELECT_OPR:
			builder.setTitle("请选择操作:");
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
			builder.setTitle("确定删除吗?");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							// 从数据库中删除
							noteTypeDao.deleteType(selectedTypeNameString);
							typeGroup.removeView(findViewById(selectedTypeId));// 通过id删除radiobutton

							Toast.makeText(MainActivity.this, "删除成功",
									Toast.LENGTH_SHORT).show();

							// 如果删除的组号为当前选中的组，则删除后让“全部”为选中状态
							if (typeGroup.getCheckedRadioButtonId() == selectedTypeId) {
								SelectOneType(0);
							}

						}
					});
			builder.setNegativeButton("否", null);
			return builder.create();

		case EDITTYPE:
			builder.setTitle("修改分组名称");
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.addtype_dialog_layout, null);
			typeEditText = (EditText) linearLayout
					.findViewById(R.id.addtype_edit);
			typeEditText.setText(selectedTypeNameString);

			builder.setCancelable(false);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String string = typeEditText.getText().toString()
									.trim();

							if (!string.equals("")) {
								// 判断长度
								if (string.length() > 10) {
									typeEditText.setError("长度不能超过10个字符！");
									keepDialog(dialog);
								} else {
									// 判断组名是否存在
									if (!noteTypeDao.isExist(string)) {
										// 修改数据库中的信息
										noteTypeDao.updateType(
												selectedTypeNameString, string);
										// 修改显示
										RadioButton radioButton = (RadioButton) findViewById(selectedTypeId);
										radioButton.setText(string);

										showTypeView.setText(string);

										Toast.makeText(MainActivity.this,
												"修改成功", Toast.LENGTH_SHORT)
												.show();
										destoryDialog(dialog);
									} else {
										typeEditText.setError("该组名已存在!");
										keepDialog(dialog);
									}
								}

							} else {
								typeEditText.setError("不能为空!");
								keepDialog(dialog);
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

		case SURE_DEL_NOTE:
			builder.setTitle("确定删除所选记事本吗？");
			builder.setPositiveButton("是",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							List<String> selectedList = new ArrayList<String>();

							for (int i = 0; i < listAdapter.map.size(); i++) {
								// 找出选中的记事本
								if (listAdapter.map.get(i)) {
									selectedList.add(noteList.get(i).get("nid")
											.toString());
									// deleteSelectedNote(noteList.get(i)
									// .get("nid").toString());
									// 如果有加锁记事本则不能进行删除
									if (noteList.get(i).get("locked")
											.toString().equals("1")) {
										Toast.makeText(MainActivity.this,
												"选中的记事本中包含锁定记事，不能进行删除！",
												Toast.LENGTH_SHORT).show();
										return;
									}
								}
							}

							// 删除所选记事本
							for (int j = 0; j < selectedList.size(); j++) {
								deleteSelectedNote(selectedList.get(j));
							}

							// 回到正常模式
							toNormal();
							// 刷新listview
							showListView();
						}
					});
			builder.setNegativeButton("否", null);

			return builder.create();

		case SELECT_GROUP:
			builder.setTitle("移动到该分组:");
			// 获取所有分类
			typeList = noteTypeDao.getAllTyep();
			CharSequence[] items = new CharSequence[typeList.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = typeList.get(i).get("type").toString();
			}
			builder.setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int which) {
					// TODO Auto-generated method stub
					// 修改所有选中的记事本所在分组
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
								// 保存密码
								Utils.savePasswd(sharedPreferences,
										passwdString);
								Toast.makeText(MainActivity.this, "密码创建成功",
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

		case INPUT_PASSWD:
			builder.setTitle("该记事本已加密");
			builder.setCancelable(false);
			linearLayout = (LinearLayout) getLayoutInflater().inflate(
					R.layout.createpasswd_dialog_layout, null);
			final EditText inputEditText = (EditText) linearLayout
					.findViewById(R.id.addpasswd_edit);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String passwdString = inputEditText.getText()
									.toString();
							// 验证通过则开启记事本
							if (passwdString.equals(Utils
									.getPasswd(sharedPreferences))) {
								destoryDialog(dialog);

								Intent intent = new Intent(MainActivity.this,
										EditNoteActivity.class);
								intent.putExtra("nid", noteList
										.get(nowPosition).get("nid").toString());
								startActivityForResult(intent, EDITNOTE);

								Toast.makeText(MainActivity.this, "密码正确!",
										Toast.LENGTH_SHORT).show();
								
								// 清空输入框
								inputEditText.setText("");

							} else {
								keepDialog(dialog);
								inputEditText.setError("密码错误！");

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

		case UPDATE:
			updateProgressDialog = new ProgressDialog(MainActivity.this);
			updateProgressDialog.setMessage("正在下载更新包...");
			updateProgressDialog
					.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			updateProgressDialog.setCancelable(false);
			updateProgressDialog.show();
			return updateProgressDialog;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * 动态添加Radiobutton
	 * 
	 * @param name
	 *            分组名
	 */
	public void AddRadioButton(String name) {
		TypeRadioButton radioButton = new TypeRadioButton(this, name);
		typeGroup.addView(radioButton, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		radioButton.setOnLongClickListener(this);

	}

	/**
	 * 每个分组按钮的长按时间
	 */
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		RadioButton radioButton = (RadioButton) arg0;
		String typeName = radioButton.getText().toString();
		if (!typeName.equals("全部")) {
			selectedTypeNameString = typeName;
			selectedTypeId = radioButton.getId();
			showDialog(SELECT_OPR);
		} else {
			Toast.makeText(MainActivity.this, "该分组不允许修改！", Toast.LENGTH_SHORT)
					.show();
		}
		return false;
	}

	/**
	 * 让某一个分组为当前选中状态，然后会调用check的监听
	 * 
	 * @param id
	 */
	public void SelectOneType(int id) {
		RadioButton radioButton = (RadioButton) typeGroup.getChildAt(id);
		radioButton.setChecked(true);
	}

	/**
	 * 删除选中的记事本，并且把所属的多媒体文件从Sdcard中删除
	 * 
	 * @param nid
	 */
	public void deleteSelectedNote(String nid) {
		PictureDao pictureDao = new PictureDao(MainActivity.this);
		VoiceDao voiceDao = new VoiceDao(MainActivity.this);
		VideoDao videoDao = new VideoDao(MainActivity.this);

		List<PictureModel> picList = pictureDao.getPicFromNid(nid);
		// 若有图片则先删除SDcard中的图片
		if (picList.size() > 0) {
			FileUtil.deleteMediaInSdcard(picList);
		}

		List<String> voiList = voiceDao.getVoiFromNid(nid);
		// 若有录音则先删除SDcard中的录音
		if (voiList.size() > 0) {
			FileUtil.deleteMediaInSdcard(voiList, Utils.VOI_PATH);
		}
		// 若有视频则先删除SDcard中的视频
		List<String> videoList = videoDao.getVideoFromNid(nid);
		if (videoList.size() > 0) {
			FileUtil.deleteMediaInSdcard(videoList, Utils.VID_PATH);
		}

		// 删除数据库中数据,仅需删除note表，其他表将级联删除
		noteDetailDao.deleteNote(nid);

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
	 * 内部类实现自定义listview
	 * 
	 * @author 孔令琛
	 * 
	 */
	public class MyListAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		private List<Map<String, Object>> list;
		private Context context;

		private boolean edit = false;

		Map<Integer, Boolean> map;// 保存item的选中的状态

		public MyListAdapter(Context context, List<Map<String, Object>> list) {
			super();
			// TODO Auto-generated constructor stub
			this.context = context;
			this.list = list;
			this.inflater = LayoutInflater.from(context);

			initMap();
		}

		/**
		 * 初始化checkbox状态为未选中
		 */
		public void initMap() {
			map = new HashMap<Integer, Boolean>();
			for (int i = 0; i < list.size(); i++) {
				map.put(i, false);
			}
		}

		/**
		 * 设置当前list是否为编辑状态
		 * 
		 * @param model
		 *            是否编辑状态
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
				System.out.println("初始化adapter");
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
				System.out.println("不用初始化adapter");
				holder = (ViewHolder) convertView.getTag();

			}

			if (list != null) {

				// holder.noteItemLayout.setBackground(getResources().getDrawable(
				// noteBgs[position % 5]));
				holder.noteItemLayout
						.setBackgroundResource(noteBgs[position % 5]);

				String nContent = list.get(position).get("content").toString();// 获取文字内容
				String nDate = list.get(position).get("date").toString();// 获取日期信息
				int nLocked = Integer.parseInt(list.get(position).get("locked")
						.toString());// 获取是否加锁
				Object nRemind = list.get(position).get("remind");// 是否设置闹钟
				final String nPic = list.get(position).get("pic").toString();// 获取图片信息

				// 显示文字内容
				if (nContent.trim().equals("")) {
					nContent = "无内容";
					holder.contentView.setText(nContent);
				} else {
					FaceParser faceParser = new FaceParser(context);
					holder.contentView.setText((faceParser.replace(nContent)));
				}

				// 显示日期
				holder.dateView.setText(nDate);

				// 是否加锁
				holder.lockedView.setVisibility(View.GONE);
				if (nLocked == 1) {
					holder.lockedView.setVisibility(View.VISIBLE);
				}

				// 是否已设置提醒
				holder.remindView.setVisibility(View.GONE);
				if (nRemind != null) {
					// 提醒是否过期
					if (!Utils.checkOverdue(nRemind.toString())) {
						holder.remindView.setVisibility(View.VISIBLE);
					}
				}

				// 避免图片混乱
				holder.picView.setVisibility(View.GONE);
				holder.picView.setImageDrawable(getResources().getDrawable(
						R.drawable.empty_icon));
				// 显示图片
				if (!nPic.equals("")) {

					// holder.picView.setTag(nPic);

					holder.picView.setVisibility(View.VISIBLE);
					holder.picView.setOnClickListener(new OnClickListener() {

						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							// 如果当前侧栏为显示状态，先让侧栏收起
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
					// 让checkbox可见
					holder.checkBox.setVisibility(View.VISIBLE);
					// 首次长按让该item为选中状态
					holder.checkBox.setChecked(map.get(position));
				}

			}

			return convertView;
		}
	}

	/**
	 * 语音搜索的回调监听
	 */
	public USCRecognizerDialogListener voiceSearchListener = new USCRecognizerDialogListener() {

		// 识别结果回调接口
		public void onResult(String result, boolean arg1) {
			// TODO Auto-generated method stub
			// 将最后的句号去掉
			String string = result.replace("。", "");
			searchEditText.append(string);
		}

		// 识别结束回调接口
		public void onEnd(USCError arg0) {
			// TODO Auto-generated method stub

		}
	};

	// list中item的监听事件
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		if (editmodel) {
			CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.multiple_checkbox);

			checkBox.toggle();

			listAdapter.map.put(position, checkBox.isChecked());

			// 记录选中item的数目
			if (checkBox.isChecked()) {
				selectItemNum++;
			} else {
				selectItemNum--;
			}

			showSelectView.setText("已选择" + selectItemNum + "项");

			// 根据选中item数目显示和隐藏相应的btn
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
			// 如果该记事本已加密则提示
			if (noteList.get(position).get("locked").toString().equals("1")) {
				// 记录当前position
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
			// 显示checkbox和初始化参数
			CheckBox checkBox = (CheckBox) view
					.findViewById(R.id.multiple_checkbox);
			checkBox.toggle();
			listAdapter.map.put(position, checkBox.isChecked());

			selectItemNum = 0;
			selectItemNum++;
			showSelectView.setText("已选择" + selectItemNum + "项");
			// 切换到编辑模式
			toEdit();

		}

		return true;
	}

	/**
	 * 回到正常选择模式
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

		// 恢复侧栏可用
		mSlideHolder.setEnabled(true);
		seeAllButton.setEnabled(true);// 恢复侧栏按钮可用
		addNoteButton.setEnabled(true);// 恢复添加按钮可用
	}

	/**
	 * 进入编辑模式
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

		// 侧栏不可用
		mSlideHolder.setEnabled(false);
		seeAllButton.setEnabled(false);// 侧栏按钮不可用
		addNoteButton.setEnabled(false);// 添加按钮不可用
		// 必定选择了一项，所以全部按钮都为可见
		deleteButton.setVisibility(View.VISIBLE);
		shareButton.setVisibility(View.VISIBLE);
		moveButton.setVisibility(View.VISIBLE);
		lockedButton.setVisibility(View.VISIBLE);
		unlockedButton.setVisibility(View.VISIBLE);
	}

	/**
	 * 进入搜索模式
	 */
	public void toSearch() {
		editmodel = true;
		searchTitle.setVisibility(View.VISIBLE);// 让搜索栏可见
		searchEditText.setText("");// 清空搜索输入框

		seeAllButton.setEnabled(false);// 侧栏按钮不可用
		addNoteButton.setEnabled(false);// 添加按钮不可用

		searchTitle.startAnimation(AnimationUtils.loadAnimation(this,
				R.anim.push_top_in));
	}

	/**
	 * 内容搜索前准备，将字符串插入%
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

			// 如果此时侧栏展开，则让其收起
			if (mSlideHolder.isOpened()) {
				mSlideHolder.toggle();
				// 返回true让程序不退出
				return true;
			}

			// 如果是编辑模式则退出编辑模式
			if (editmodel) {
				toNormal();
				return true;
			}

			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(MainActivity.this, "再按一次退出程序",
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
