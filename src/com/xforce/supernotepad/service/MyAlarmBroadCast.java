package com.xforce.supernotepad.service;

import com.xforce.supernotepad.view.AlarmDialogActivity;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.RingtonePreference;
import android.util.Log;

public class MyAlarmBroadCast extends BroadcastReceiver{
	
	 String path;
	 RingtonePreference ringpre ;
	
	 @Override  
	    public void onReceive(Context context, Intent intent) {  
	        Log.i("֪ͨ", "�յ��˹㲥");  
	        System.out.println("�����������--�� " + System.currentTimeMillis());
	        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClass(context, AlarmDialogActivity.class);
			context.startActivity(intent);
			
	        
	    }  
}
