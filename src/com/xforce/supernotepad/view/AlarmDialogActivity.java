package com.xforce.supernotepad.view;

import com.umeng.analytics.MobclickAgent;
import com.xforce.supernotepad.dao.NoteDetailDao;
import com.xforce.supernotepad.model.NoteDetail;
import com.xforce.supernotepad.util.FaceParser;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class AlarmDialogActivity extends Activity {

	public MediaPlayer alarmMusic; // 媒体播放器
	boolean flag = true;// 是否只执行一次
	public Vibrator vibrator;// 震动
	private Button back; // 返回记事本的按钮
	private Button cancel; // 取消按钮

	private TextView remindContentView, remindTimeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_dialog_layout);

		remindContentView = (TextView) findViewById(R.id.remind_content_tv);
		remindTimeView = (TextView) findViewById(R.id.remind_time_tv);

		Intent intent = this.getIntent(); // 取Intent 传过来的code（记事本id）
		int code = intent.getIntExtra("code", -1);

		NoteDetailDao noteDetailDao = new NoteDetailDao(
				AlarmDialogActivity.this);
		NoteDetail noteDetail = noteDetailDao.getOneNote(code + "");

		FaceParser faceParser = new FaceParser(AlarmDialogActivity.this);
		remindContentView.setText(faceParser.replace(noteDetail.getContent()));
		remindTimeView.setText(noteDetail.getRtime());

		System.out.println("this is dialog code -- > " + code);

		// 取得屏幕大小，设置activity 的长宽
		DisplayMetrics dm = new DisplayMetrics();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 取得屏幕大小
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;// 屏幕的宽
		int screenHeight = dm.heightPixels;// 屏幕的长
		Window window = getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = screenWidth;
		layoutParams.height = (int) (screenHeight * 0.5);
		window.setAttributes(layoutParams);// 设置activity的长宽

		try {
			vibrator = (Vibrator) AlarmDialogActivity.this
					.getSystemService(Context.VIBRATOR_SERVICE);// 初始化震动
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // 设置音乐为短信提示音乐
			alarmMusic = new MediaPlayer();// 初始化音乐播放器
			alarmMusic.setDataSource(AlarmDialogActivity.this, alert);
			alarmMusic.setAudioStreamType(AudioManager.STREAM_RING);
			alarmMusic.setLooping(false);// 不重复
			alarmMusic.prepare();// 准备一下
			alarmMusic.start();// 开始播放
			long[] pattern = { 800, 150, 400, 130 }; // OFF/ON/OFF/ON...震动频率
			vibrator.vibrate(pattern, 2);// 按照震动的频率，震动两次
			// 监听短信提示音乐的播放情况
			alarmMusic.setOnCompletionListener(new OnCompletionListener() {
				// 播放完毕后，则让震动停止
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					vibrator.cancel();
					System.out.println("播放完毕了！~~");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		back = (Button) findViewById(R.id.dialog_button_back);// 初始化返回记事本的按钮
		// 设置监听事件
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vibrator.cancel();// 停止震动
				alarmMusic.stop();// 停止音乐
				Intent intent = new Intent(AlarmDialogActivity.this,
						MainActivity.class);// 跳转到记事本的avtivity
				startActivity(intent);
				AlarmDialogActivity.this.finish();// 结束
			}
		});

		cancel = (Button) findViewById(R.id.dialog_button_cancel);// 初始化取消按钮
		// 设置监听事件
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 结束所有的东西
				vibrator.cancel();
				alarmMusic.stop();
				AlarmDialogActivity.this.finish();
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 开启友盟统计
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止友盟统计
		MobclickAgent.onPause(this);
	}

}
