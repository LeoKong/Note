package com.xforce.supernotepad.adapter;


import com.xforce.supernotepad.dao.NoteDetailDao;
import com.xforce.supernotepad.view.CalendarActivity;
import com.xforce.supernotepad.view.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {
	private String[] dayStrings;
	private Context context;
	private int year;
	private int month;
	NoteDetailDao noteDetailDao ;

	public CalendarAdapter(Context context, int year, int month,
			String[] dayStrings) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.year = year;
		this.month = month;
		this.dayStrings = dayStrings;
		noteDetailDao= new NoteDetailDao(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dayStrings.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	

	public void setYear(int year) {
		this.year = year;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.day,
					null);
		}
		TextView dayView = (TextView) convertView.findViewById(R.id.daytext);
		dayView.setText(dayStrings[position]);
		dayView.setBackgroundDrawable(null);
//		dayView.setBackground(null);

		if (dayStrings[position] != null) {
			String dateString=CalendarActivity.formatDate(year,
					month, dayStrings[position]);
			//如果该天有记事，则标记
			if (noteDetailDao.checkHasNote(dateString)) {
				dayView.setBackgroundResource(R.drawable.circle4day);
			}
		}
		return convertView;
	}

}
