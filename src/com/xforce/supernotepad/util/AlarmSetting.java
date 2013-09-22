package com.xforce.supernotepad.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.xforce.supernotepad.service.MyAlarmBroadCast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmSetting {
	
	/**
	 * 设置闹钟
	 * @param context 上下文
	 * @param code 记事本id
	 * @param date 从SetTimeBuilder取回来的date
	 * @return true 设置成功， false 设置未成功
	 */
	public static boolean setAlarm(Context context, int code , int[] date){
		//设置calendar 从SetTimeBuilder取回来的date
    	Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, date[0]);//年
		c.set(Calendar.MONTH,date[1]-1);//月 calendar 0表示 1月
		c.set(Calendar.DAY_OF_MONTH,date[2]);//日
		c.set(Calendar.HOUR_OF_DAY,date[3]);//时
		c.set(Calendar.MINUTE,date[4]);//分
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		System.out.println(format.format(c.getTime()));//打印检测
		Intent intent = new Intent(context, MyAlarmBroadCast.class);
		intent.putExtra("code", code);//传递code
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), code, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);//定义闹钟管理器
		Calendar orig = Calendar.getInstance();//查看设置时候的时间，拿来判断，如果设置是 过去的时间 则返回不成功
		if(orig.getTimeInMillis() < c.getTimeInMillis()){
			System.out.println("成功设置，时间为-->" + c.getTimeInMillis());
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					c.getTimeInMillis(), pendingIntent);
			return  true;
		}else{
			System.out.println("时间为都设置成小于它的了 没意思！-->" + c.getTimeInMillis());
		}
		return false;
	}
	
	
	/**
	 * 取消闹钟
	 * @param context 上下文
	 * @param code 记事本的id
	 */
	public static void cancelAlarm(Context context,int code){
		Intent intent = new Intent(context, MyAlarmBroadCast.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, code, intent, 0);
		AlarmManager aManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		aManager.cancel(pendingIntent);
		System.out.println("提醒" + code + "已取消！");
	}
}
