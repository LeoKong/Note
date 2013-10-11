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

	public MediaPlayer alarmMusic; // ý�岥����
	boolean flag = true;// �Ƿ�ִֻ��һ��
	public Vibrator vibrator;// ��
	private Button back; // ���ؼ��±��İ�ť
	private Button cancel; // ȡ����ť

	private TextView remindContentView, remindTimeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_dialog_layout);

		remindContentView = (TextView) findViewById(R.id.remind_content_tv);
		remindTimeView = (TextView) findViewById(R.id.remind_time_tv);

		Intent intent = this.getIntent(); // ȡIntent ��������code�����±�id��
		int code = intent.getIntExtra("code", -1);

		NoteDetailDao noteDetailDao = new NoteDetailDao(
				AlarmDialogActivity.this);
		NoteDetail noteDetail = noteDetailDao.getOneNote(code + "");

		FaceParser faceParser = new FaceParser(AlarmDialogActivity.this);
		remindContentView.setText(faceParser.replace(noteDetail.getContent()));
		remindTimeView.setText(noteDetail.getRtime());

		System.out.println("this is dialog code -- > " + code);

		// ȡ����Ļ��С������activity �ĳ���
		DisplayMetrics dm = new DisplayMetrics();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // ȡ����Ļ��С
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;// ��Ļ�Ŀ�
		int screenHeight = dm.heightPixels;// ��Ļ�ĳ�
		Window window = getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.width = screenWidth;
		layoutParams.height = (int) (screenHeight * 0.5);
		window.setAttributes(layoutParams);// ����activity�ĳ���

		try {
			vibrator = (Vibrator) AlarmDialogActivity.this
					.getSystemService(Context.VIBRATOR_SERVICE);// ��ʼ����
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // ��������Ϊ������ʾ����
			alarmMusic = new MediaPlayer();// ��ʼ�����ֲ�����
			alarmMusic.setDataSource(AlarmDialogActivity.this, alert);
			alarmMusic.setAudioStreamType(AudioManager.STREAM_RING);
			alarmMusic.setLooping(false);// ���ظ�
			alarmMusic.prepare();// ׼��һ��
			alarmMusic.start();// ��ʼ����
			long[] pattern = { 800, 150, 400, 130 }; // OFF/ON/OFF/ON...��Ƶ��
			vibrator.vibrate(pattern, 2);// �����𶯵�Ƶ�ʣ�������
			// ����������ʾ���ֵĲ������
			alarmMusic.setOnCompletionListener(new OnCompletionListener() {
				// ������Ϻ�������ֹͣ
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					vibrator.cancel();
					System.out.println("��������ˣ�~~");
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		back = (Button) findViewById(R.id.dialog_button_back);// ��ʼ�����ؼ��±��İ�ť
		// ���ü����¼�
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				vibrator.cancel();// ֹͣ��
				alarmMusic.stop();// ֹͣ����
				Intent intent = new Intent(AlarmDialogActivity.this,
						MainActivity.class);// ��ת�����±���avtivity
				startActivity(intent);
				AlarmDialogActivity.this.finish();// ����
			}
		});

		cancel = (Button) findViewById(R.id.dialog_button_cancel);// ��ʼ��ȡ����ť
		// ���ü����¼�
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// �������еĶ���
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
		// ��������ͳ��
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// ֹͣ����ͳ��
		MobclickAgent.onPause(this);
	}

}
