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
	 * ��������
	 * @param context ������
	 * @param code ���±�id
	 * @param date ��SetTimeBuilderȡ������date
	 * @return true ���óɹ��� false ����δ�ɹ�
	 */
	public static boolean setAlarm(Context context, int code , int[] date){
		//����calendar ��SetTimeBuilderȡ������date
    	Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, date[0]);//��
		c.set(Calendar.MONTH,date[1]-1);//�� calendar 0��ʾ 1��
		c.set(Calendar.DAY_OF_MONTH,date[2]);//��
		c.set(Calendar.HOUR_OF_DAY,date[3]);//ʱ
		c.set(Calendar.MINUTE,date[4]);//��
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
		System.out.println(format.format(c.getTime()));//��ӡ���
		Intent intent = new Intent(context, MyAlarmBroadCast.class);
		intent.putExtra("code", code);//����code
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), code, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);//�������ӹ�����
		Calendar orig = Calendar.getInstance();//�鿴����ʱ���ʱ�䣬�����жϣ���������� ��ȥ��ʱ�� �򷵻ز��ɹ�
		if(orig.getTimeInMillis() < c.getTimeInMillis()){
			System.out.println("�ɹ����ã�ʱ��Ϊ-->" + c.getTimeInMillis());
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					c.getTimeInMillis(), pendingIntent);
			return  true;
		}else{
			System.out.println("ʱ��Ϊ�����ó�С�������� û��˼��-->" + c.getTimeInMillis());
		}
		return false;
	}
	
	
	/**
	 * ȡ������
	 * @param context ������
	 * @param code ���±���id
	 */
	public static void cancelAlarm(Context context,int code){
		Intent intent = new Intent(context, MyAlarmBroadCast.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context, code, intent, 0);
		AlarmManager aManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
		aManager.cancel(pendingIntent);
		System.out.println("����" + code + "��ȡ����");
	}
}
