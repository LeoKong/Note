package com.xforce.supernotepad.view;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

import com.xforce.supernotepad.adapter.CalendarAdapter;
import com.xforce.supernotepad.dao.NoteDetailDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarActivity extends Activity implements OnClickListener {
	private GridView calendarView;
	private Button previousBtn, centerBtn, nextBtn;
	private String[] dayStrings = new String[42];// 日历中的天
	private CalendarAdapter adapter;
	private int year;
	private int month;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_layout);
		calendarView = (GridView) findViewById(R.id.calendar_view);
		previousBtn = (Button) findViewById(R.id.previous_month_btn);
		centerBtn = (Button) findViewById(R.id.center_month_btn);
		nextBtn = (Button) findViewById(R.id.next_month_btn);

		previousBtn.setOnClickListener(this);
		centerBtn.setOnClickListener(this);
		nextBtn.setOnClickListener(this);
		calendarView.setOnItemClickListener(dayItemClickListener);

		// 设置透明度
		calendarView.getBackground().setAlpha(100);
		previousBtn.getBackground().setAlpha(150);
		centerBtn.getBackground().setAlpha(150);
		nextBtn.getBackground().setAlpha(150);

		// 获取当前年月
		Calendar calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;
		centerBtn.setText(formatDate(year, month));
		// 初始化day数组
		adapter = new CalendarAdapter(CalendarActivity.this, year, month,
				dayStrings);
		setCalendar(year, month);
		calendarView.setAdapter(adapter);
	}

	public String adjust(String string) {
		return "0" + string;
	}

	/**
	 * 初始化day数组
	 * 
	 * @param year
	 * @param month
	 */
	public void setCalendar(int year, int month) {
		// 清空数组
		for (int i = 0; i < dayStrings.length; i++) {
			if (dayStrings[i] != null) {
				dayStrings[i] = null;
			}
		}
		// 更新adapter中的年月
		adapter.setYear(year);
		adapter.setMonth(month);

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month - 1, 1);
		// 获取该月的第一天是多少，星期天为1
		int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int day = 0;
		if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8
				|| month == 10 || month == 12) {
			day = 31;

		}
		if (month == 4 || month == 6 || month == 9 || month == 11) {
			day = 30;
		}
		if (month == 2) {
			if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
				day = 29;
			} else {
				day = 28;
			}
		}
		for (int i = week, n = 1; i < week + day; i++) {
			dayStrings[i] = String.valueOf(n);
			if (n < 10) {
				dayStrings[i] = adjust(dayStrings[i]);
			}
			n++;
		}
	}

	public OnItemClickListener dayItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			NoteDetailDao noteDetailDao = new NoteDetailDao(
					CalendarActivity.this);
			List<Map<String, Object>> list = noteDetailDao
					.getAllNoteFromDate(formatDate(year, month,
							dayStrings[position]));
			if (list.size()==0 ) {
				Toast toast =Toast.makeText(CalendarActivity.this, "该日期下暂无记事！", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			Intent intent=new Intent();
			intent.putExtra("list", (Serializable)list);
			setResult(Activity.RESULT_OK, intent);
			CalendarActivity.this.finish();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.previous_month_btn:
			if (month == 1) {
				year = year - 1;
				month = 12;
			} else {
				month--;
			}
			setCalendar(year, month);
			adapter.notifyDataSetChanged();
			break;

		case R.id.next_month_btn:
			if (month == 12) {
				year = year + 1;
				month = 1;
			} else {
				month = month + 1;
			}
			setCalendar(year, month);
			adapter.notifyDataSetChanged();
			break;

		case R.id.center_month_btn:
			showDatePicker();
			break;
		}
		centerBtn.setText(formatDate(year, month));
	}

	/**
	 * 显示年月选择器
	 */
	public void showDatePicker() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CalendarActivity.this);
		LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.datepicker_layout, null);

		final WheelView years = (WheelView) layout.findViewById(R.id.year);
		years.setViewAdapter(new NumericWheelAdapter(this, 1900, 2100, "%d年"));

		final WheelView months = (WheelView) layout.findViewById(R.id.month);
		months.setViewAdapter(new NumericWheelAdapter(this, 1, 12, "%d月"));
		months.setCyclic(true);

		final TextView titleView = (TextView) layout
				.findViewById(R.id.datepick_title);
		titleView.setText(formatDate(year, month));

		// 设置为当前年月
		years.setCurrentItem(year - 1900);
		months.setCurrentItem(month - 1);

		years.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				year = years.getCurrentItem() + 1900;
				titleView.setText(formatDate(year, month));
			}
		});

		months.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				month = months.getCurrentItem() + 1;
				titleView.setText(formatDate(year, month));
			}
		});

		builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				setCalendar(year, month);
				adapter.notifyDataSetChanged();
				centerBtn.setText(formatDate(year, month));
			}
		});

		builder.setNegativeButton("取消", null);

		builder.setView(layout);
		builder.show();
	}

	/**
	 * 调整显示日期
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public String formatDate(int year, int month) {
		return year + "年" + month + "月";
	}

	/**
	 * 调整日期格式
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static String formatDate(int year, int month, String day) {
		if (month < 10) {
			return year + "-0" + month + "-" + day;
		}
		return year + "-" + month + "-" + day;
	}

}
