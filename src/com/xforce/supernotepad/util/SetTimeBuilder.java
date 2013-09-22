package com.xforce.supernotepad.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.xforce.supernotepad.view.R;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


public class SetTimeBuilder extends Builder {

	
	private Context context;
	private Button lbtn;//头顶左边按钮--时间
	private Button rbtn;//头顶右边按钮--日期
	private TextView tmDisplay; //显示时间的地方
	private LinearLayout localLinearLayout; //总体的layout
	private FrameLayout f_ddt_picker; //设置date day time 的滚动选择器
	private FrameLayout f_ymd_picker; //设置year month day 的滚动选择器
	
	private Calendar calendar; //控制真实时间的日历对象
	private Calendar ddt_cld ;//设置date wkd time 的 时间日历对象
	private Calendar ymd_cld ;//设置year month day 的时间日历对象
	private int tMon;//控制 月 的临时变量
	private int tDay;//控制 日 的临时变量
	private int year ; //时间的--年
	private int month ;//时间的--月
	private int day ;//时间的--日
	private int hour;//时间的--小时
	private int min;//时间的--分钟
	private String wkDayS="";//显示周几
	
	private WheelView wv_md = null; //ddt中日期的滚动器
	private WheelView wv_hour = null;//ddt中小时的滚动器
	private WheelView wv_min = null;//ddt中分钟的滚动器
	
	private WheelView wv_day = null; //ymd中的日
	private WheelView wv_mon = null;//ymd中的月
	private WheelView wv_year = null;//ymd中的年
	private  int START_YEAR = 1990, END_YEAR = 2100; //年 计数的开始年份以及 结束年份
	
	
	// 添加大小月月份并将其转换为list,方便之后的判断
	private String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private String[] months_little = { "4", "6", "9", "11" };
	List<String> list_big = Arrays.asList(months_big);
	List<String> list_little = Arrays.asList(months_little);
	
	public SetTimeBuilder(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub

	}

	public SetTimeBuilder(Activity context) {
		super(context);
		this.context = context;
		localLinearLayout = (LinearLayout) context
				.getLayoutInflater().inflate(R.layout.dateswitcher_layout, null);
		init();
		setPositiveButton("设置", null);
		setNegativeButton("取消", null);
		setView(localLinearLayout);
		setCancelable(false);
	}
	
	
	public void init(){
		
		this.calendar = Calendar.getInstance(Locale.US);
		day = calendar.get(Calendar.DATE);
		tDay = day;
		month = calendar.get(Calendar.MONTH) + 1;
		tMon = month;
		year = calendar.get(Calendar.YEAR);
		DateFormat format = new SimpleDateFormat("EEEE");
		wkDayS = format.format(calendar.getTime());
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		min = calendar.get(Calendar.MINUTE);
		tmDisplay = (TextView) localLinearLayout.findViewById(R.id.tvDisplay);
		this.set_YMD_wkD_Tm();
		

		f_ddt_picker = (FrameLayout) localLinearLayout.findViewById(R.id.ddt_picker);
		f_ddt_picker.setVisibility(0);
		f_ymd_picker = (FrameLayout) localLinearLayout.findViewById(R.id.ymd_picker);
		f_ymd_picker.setVisibility(8);
		
		wv_hour = (WheelView) localLinearLayout.findViewById(R.id.id_hour);
        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23,"%d时");
        hourAdapter.setItemResource(R.layout.wheel_text_item);
        hourAdapter.setItemTextResource(R.id.text);
        wv_hour.setViewAdapter(hourAdapter);
        wv_hour.setCyclic(true);
        
		wv_min = (WheelView) localLinearLayout.findViewById(R.id.id_minute);
		NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59, "%d分");
        minAdapter.setItemResource(R.layout.wheel_text_item);
        minAdapter.setItemTextResource(R.id.text);
        wv_min.setViewAdapter(minAdapter);
        wv_min.setCyclic(true);
        
		
		wv_hour.setCurrentItem(hour);
        wv_min.setCurrentItem(min);
		
		
        ddt_cld = Calendar.getInstance(Locale.US);
        ddt_cld.set(year,tMon - 1, 1);
        this.wv_md = (WheelView) localLinearLayout.findViewById(R.id.id_date);
		wv_md.setViewAdapter(new DayArrayAdapter(context, ddt_cld));
		wv_md.setCurrentItem(tDay-1);
		wv_md.setCyclic(true);
        
        //////////////////////////////
		this.wv_year = (WheelView) localLinearLayout.findViewById(R.id.id_year);
        NumericWheelAdapter yearAdapter = new NumericWheelAdapter(context, START_YEAR, END_YEAR,"%d年");
        yearAdapter.setItemResource(R.layout.wheel_text_item);
        yearAdapter.setItemTextResource(R.id.text);
        wv_year.setViewAdapter(yearAdapter);
        wv_year.setCyclic(true);
        
		this.wv_mon = (WheelView) localLinearLayout.findViewById(R.id.id_month);
		NumericWheelAdapter monAdapter = new NumericWheelAdapter(context, 1, 12, "%d月");
        monAdapter.setItemResource(R.layout.wheel_text_item);
        monAdapter.setItemTextResource(R.id.text);
        wv_mon.setViewAdapter(monAdapter);
        wv_mon.setCyclic(true);
    
		this.calendar = Calendar.getInstance(Locale.US);
		wv_year.setCurrentItem(calendar.get(Calendar.YEAR)- START_YEAR);
        wv_mon.setCurrentItem(calendar.get(Calendar.MONTH));
        
        ymd_cld = Calendar.getInstance(Locale.US);
        ymd_cld.set(year,month-1,1);
        this.wv_day = (WheelView) localLinearLayout.findViewById(R.id.id_day);
		wv_day.setViewAdapter(new DayArrayAdapter2(context, ymd_cld,30));
		wv_day.setCurrentItem(tDay-1);
		wv_day.setCyclic(true);

		
		
		
		lbtn = (Button) localLinearLayout.findViewById(R.id.lBtn);
		rbtn = (Button) localLinearLayout.findViewById(R.id.rBtn);
		lbtn.setEnabled(false);
		rbtn.setEnabled(true);
		lbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				lbtn.setEnabled(false);
				rbtn.setEnabled(true);
				f_ddt_picker.setVisibility(0);
				f_ymd_picker.setVisibility(8);
				tMon = month;
				tDay = day;
				ddt_cld.set(year,tMon - 1, 1);
				wv_md.setViewAdapter(new DayArrayAdapter(context, ddt_cld));
				wv_md.setCurrentItem(tDay - 1 );
				Calendar tmp = (Calendar) ddt_cld.clone();
				tmp.roll(Calendar.DAY_OF_YEAR, tDay - 1);
				DateFormat format = new SimpleDateFormat("EEEE");
				wkDayS = format.format(tmp.getTime());
				set_YMD_wkD_Tm();
			}
		});
		rbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				rbtn.setEnabled(false);
				lbtn.setEnabled(true);
				f_ddt_picker.setVisibility(8);
				f_ymd_picker.setVisibility(0);
				ymd_cld.set(year,month-1,1);
				wv_day.setViewAdapter(new DayArrayAdapter2(context, ymd_cld,30));
				wv_mon.setCurrentItem(month - 1);
				wv_day.setCurrentItem(day-1);
				
				Calendar tmp = (Calendar) ymd_cld.clone();
				tmp.roll(Calendar.DAY_OF_YEAR, day - 1);
				DateFormat format = new SimpleDateFormat("EEEE");
				wkDayS = format.format(tmp.getTime());
				set_YMD_wkD_Tm();
				
			}
		});
		
		
		OnWheelChangedListener wheelListener_ddt = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				wheel.addScrollingListener(new OnWheelScrollListener() {
					
					public void onScrollingStarted(WheelView wheel) {
						// TODO Auto-generated method stub
					}
					
					public void onScrollingFinished(WheelView wheel) {
						// TODO Auto-generated method stub
						Calendar tmp = (Calendar) ddt_cld.clone();
						tmp.set(year,tMon - 1, 1);
						tmp.roll(Calendar.DAY_OF_YEAR, wv_md.getCurrentItem());
						day = tmp.get(Calendar.DATE);
						month = tmp.get(Calendar.MONTH) + 1;
						year = tmp.get(Calendar.YEAR);
						DateFormat format = new SimpleDateFormat("EEEE");
						wkDayS = format.format(tmp.getTime());
						hour = wv_hour.getCurrentItem();
						min = wv_min.getCurrentItem();
						set_YMD_wkD_Tm();
					}
				});

			}
		};
		
		wv_hour.addChangingListener(wheelListener_ddt);
		wv_min.addChangingListener(wheelListener_ddt);
		wv_md.addChangingListener(wheelListener_ddt);
		
		OnWheelChangedListener wheelListener_ymd_day = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				wheel.addScrollingListener(new OnWheelScrollListener() {
					
					public void onScrollingStarted(WheelView wheel) {
						// TODO Auto-generated method stub
						
					}
					
					public void onScrollingFinished(WheelView wheel) {
						// TODO Auto-generated method stub
						day = wv_day.getCurrentItem() + 1;
						month = wv_mon.getCurrentItem() + 1;
						year = wv_year.getCurrentItem() + START_YEAR;
						
						Calendar tmp = (Calendar) ymd_cld.clone();
						tmp.roll(Calendar.DAY_OF_YEAR, day - 1);
						DateFormat format = new SimpleDateFormat("EEEE");
						wkDayS = format.format(tmp.getTime());
						set_YMD_wkD_Tm();
					}
				});
			}
			
		};
		
		OnWheelChangedListener wheelListener_ym = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				
				wheel.addScrollingListener(new OnWheelScrollListener() {
					
					public void onScrollingStarted(WheelView wheel) {
						// TODO Auto-generated method stub
						
					}
					
					public void onScrollingFinished(WheelView wheel) {
						// TODO Auto-generated method stub
						day = wv_day.getCurrentItem() ;
						month = wv_mon.getCurrentItem()+1;
						year = wv_year.getCurrentItem() + START_YEAR;
						
						if (list_big.contains(String.valueOf(month))) {
							ymd_cld.set(year,month -1,1);
							wv_day.setViewAdapter(new DayArrayAdapter2(context, ymd_cld, 31-1));
							
						} else if (list_little.contains(String.valueOf(month))) {
							if(day >= 31-1){
								day = 30-1;
							}
							ymd_cld.set(year,month -1,1);
							wv_day.setViewAdapter(new DayArrayAdapter2(context, ymd_cld, 30-1));
							wv_day.setCurrentItem(day);
							
						} else {
							if ((year % 4 == 0 && year % 100 != 0)|| year % 400 == 0){
								if(day >= 30-1){
									day = 29-1;
								}
								ymd_cld.set(year,month -1,1);
								wv_day.setViewAdapter(new DayArrayAdapter2(context, ymd_cld, 29-1));
								wv_day.setCurrentItem(day);
								
							}else{
								if(day >= 29-1){
									day = 28-1;
								}
								ymd_cld.set(year,month -1,1);
								wv_day.setViewAdapter(new DayArrayAdapter2(context, ymd_cld, 28-1));
								wv_day.setCurrentItem(day);
							}
						}
						day = day + 1;
						
						Calendar tmp = (Calendar) ymd_cld.clone();
						tmp.roll(Calendar.DAY_OF_YEAR, day - 1);
						DateFormat format = new SimpleDateFormat("EEEE");
						wkDayS = format.format(tmp.getTime());
						set_YMD_wkD_Tm();
					}
				});
			}
		};
		wv_day.addChangingListener(wheelListener_ymd_day);
		wv_year.addChangingListener(wheelListener_ym);
		wv_mon.addChangingListener(wheelListener_ym);
		
	}
	
	
	
    public boolean isEapYear(int year){
    	if(year%4 == 0){
    		return true;
    	}else{
    		return false;
    		
    	}
    }
	
    
    public String set_YMD_wkD_Tm(){
    	String date = year + "年" + (month < 10 ? "0" + month : month) + "月" + (day < 10 ? "0" + day : day) + "日" + "   " + wkDayS + "   " +(hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);
    	tmDisplay.setText(date);
    	date = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day) + " " +(hour < 10 ? "0" + hour : hour) + ":" + (min < 10 ? "0" + min : min);
    	return date;
    }
    
    public int[] getResultDate(){
    	int date[] = new int[]{this.year,this.month,this.day,this.hour,this.min};
    	return date;
    }
    
//    public boolean sendBrocast(Context context, int code){
//    	int date[] = this.getResultDate();
//    	Calendar c = Calendar.getInstance();
//		c.set(Calendar.YEAR, date[0]);
//		c.set(Calendar.MONTH,date[1]-1);
//		c.set(Calendar.DAY_OF_MONTH,date[2]);
//		c.set(Calendar.HOUR_OF_DAY,date[3]);
//		c.set(Calendar.MINUTE,date[4]);
//		DateFormat format = new SimpleDateFormat("yyyy-MMM-dd-hh:mm");
//		System.out.println(format.format(c.getTime()));
//		
//		Intent intent = new Intent(context, MyAlarmBroadCast.class);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(
//				context.getApplicationContext(), code, intent, 0);
//		
//		AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
//		Calendar orig = Calendar.getInstance();
//		if(orig.getTimeInMillis() < c.getTimeInMillis()){
//			System.out.println("成功设置，时间为-->" + c.getTimeInMillis());
//			alarmManager.set(AlarmManager.RTC_WAKEUP,
//					c.getTimeInMillis(), pendingIntent);
//		}else{
//			System.out.println("时间为都设置成小于它的了 没意思！-->" + c.getTimeInMillis());
//		}
//    	return false;
//    }
    

	/**
     * Day-wkDay adapter
     *
     */
    private class DayArrayAdapter extends AbstractWheelTextAdapter {
        // Count of days to be shown
        private  int daysCount = isEapYear(year)? (366-1) : (365-1);
        
        // Calendar
        Calendar calendar;
        
        /**
         * Constructor
         */
        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.time2_day, NO_RESOURCE);
            this.calendar = calendar;
            setItemTextResource(R.id.time2_monthday);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            int day = 0 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);
            View view = super.getItem(index, cachedView, parent);
            TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
            DateFormat format = new SimpleDateFormat("EEE");
            weekday.setText(format.format(newCalendar.getTime()));
            
            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            format = new SimpleDateFormat("MM－dd");
            monthday.setText(format.format(newCalendar.getTime()));
                monthday.setTextColor(0xFF111111);
            return view;
        }
        
        public int getItemsCount() {
            return daysCount + 1;
        }
        
        @Override
        protected CharSequence getItemText(int index) {
            return "";
        }
    }
    
    
    /**
     * Day adapter
     *
     */
    private class DayArrayAdapter2 extends AbstractWheelTextAdapter {
        // Count of days to be shown
        private  int daysCount = 30;
        Calendar calendar;
        
        /**
         * Constructor
         */
        protected DayArrayAdapter2(Context context, Calendar calendar, int daysCount) {
            super(context, R.layout.time2_day, NO_RESOURCE);
            this.calendar = calendar;
            this.daysCount = daysCount;
            setItemTextResource(R.id.time2_monthday);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            int day = index;
            Calendar newCalendar = (Calendar) calendar.clone();
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);
            View view = super.getItem(index, cachedView, parent);
            TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
            DateFormat format = new SimpleDateFormat("EEE");
            weekday.setText(format.format(newCalendar.getTime()));
            
            TextView monthday = (TextView) view.findViewById(R.id.time2_monthday);
            format = new SimpleDateFormat("dd日");
            monthday.setText(format.format(newCalendar.getTime()));
            monthday.setTextColor(0xFF111111);
            return view;
        }
        
        public int getItemsCount() {
            return daysCount + 1;
        }
        
        @Override
        protected CharSequence getItemText(int index) {
            return "";
        }
    }

}
