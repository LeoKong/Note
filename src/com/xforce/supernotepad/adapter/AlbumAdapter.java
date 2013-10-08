package com.xforce.supernotepad.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.aphidmobile.utils.IO;
import com.aphidmobile.utils.UI;
import com.xforce.supernotepad.dao.PictureDao;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.Utils;
import com.xforce.supernotepad.view.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumAdapter extends BaseAdapter {
	private List<PictureModel> picList = null;
	private LayoutInflater inflater;
	private int repeatCount = 1;

	public AlbumAdapter(Context context) {
		super();
		// TODO Auto-generated constructor stub
		inflater = LayoutInflater.from(context);
		PictureDao pictureDao = new PictureDao(context);
		picList = pictureDao.getAllPictures();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return picList.size() * repeatCount;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.album_item, null);
		}
		String picName = picList.get(position).getpName();
		//显示日期
		UI.<TextView> findViewById(convertView, R.id.album_date)
		.setText(getDate(picName));
		
		// 读取图片
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(Utils.PIC_PATH + picName);
			UI.<ImageView> findViewById(convertView, R.id.album_photo)
					.setImageBitmap(IO.readBitmap(inputStream));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		//显示描述
		UI.<TextView> findViewById(convertView, R.id.album_description)
				.setText(picList.get(position).getIllustration());

		return convertView;
	}
	
	public String getDate(String string){
		String dateString="";
		if (string.contains("ipic")) {
			dateString=string.substring(5,13);
		}else {
			dateString=string.substring(4,12);
		}
		//解析日期
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMdd");
		Date date;
		try {
			date = dateFormat.parse(dateString);
			//整理新日期
			SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd E"); 
			return dateFormat2.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

}
