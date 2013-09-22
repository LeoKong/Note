package com.xforce.supernotepad.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xforce.supernotepad.util.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteTypeDao {
	SQLiteDatabase sqLiteDatabase;

	public NoteTypeDao(Context context) {
		super();
		sqLiteDatabase = Utils.getDataBase(context);
		// TODO Auto-generated constructor stub
	}

	// ��ȡ���з���
	public List<Map<String, Object>> getAllTyep() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

		Cursor cursor = sqLiteDatabase.query("tb_type",
				new String[] { "t_id","type" }, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("tid", cursor.getInt(0));
			map.put("type", cursor.getString(1));
			list.add(map);
		}

		return list;
	}

	// ͨ��������ȡ��id
	public int getTid(String type) {
		int id=0;
		
		Cursor cursor = sqLiteDatabase.query("tb_type",
				new String[] { "t_id" }, "type=?", new String[] { type }, null,
				null, null);
		
		if (cursor.moveToNext()) {
			id=cursor.getInt(0);
		}

		return id;
	}

	// �жϷ����Ƿ����
	public boolean isExist(String type) {
		boolean exist = false;

		Cursor cursor = sqLiteDatabase.query("tb_type",
				new String[] { "type" }, "type=?", new String[] { type }, null,
				null, null);

		if (cursor.moveToNext()) {
			exist = true;
		}

		return exist;
	}

	// ��� һ������
	public void addNewType(String type) {
		ContentValues values = new ContentValues();
		values.put("type", type);
		System.out.println("_______"
				+ sqLiteDatabase.insert("tb_type", "t_id", values));
	}

	// �޸�һ������
	public void updateType(String oldType, String newType) {
		ContentValues values = new ContentValues();
		values.put("type", newType);
		sqLiteDatabase.update("tb_type", values, "type=?",
				new String[] { oldType });
	}

	/**
	 * ɾ��һ������
	 * @param type ����
	 */
	public void deleteType(String type) {
		sqLiteDatabase.delete("tb_type", "type=?", new String[] { type });
	}
	

}
