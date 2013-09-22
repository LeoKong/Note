package com.xforce.supernotepad.dao;

import java.util.ArrayList;
import java.util.List;

import com.xforce.supernotepad.util.FileUtil;
import com.xforce.supernotepad.util.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VideoDao {
	SQLiteDatabase sqLiteDatabase;

	public VideoDao(Context context) {
		super();
		// TODO Auto-generated constructor stub
		sqLiteDatabase = Utils.getDataBase(context);
	}

	/**
	 * ��һ�����±������һ����Ƶ��¼
	 * 
	 * @param nid
	 *            �������±���
	 * @param vidName
	 */
	public void addNewVideo(int nid, String vidName) {
		ContentValues values = new ContentValues();
		values.put("n_id", nid);
		values.put("i_name", vidName);
		System.out.println(sqLiteDatabase.insert("tb_video", "i_id", values)
				+ "������Ƶ��");
	}

	/**
	 * ���ݼ��±�nid����������������Ƶ
	 * 
	 * @param nid
	 *            �������±�id
	 * @return
	 */
	public List<String> getVideoFromNid(String nid) {
		List<String> list = new ArrayList<String>();
		Cursor cursor = sqLiteDatabase.query("tb_video",
				new String[] { "i_name" }, "n_id=?", new String[] { nid },
				null, null, null);

		while (cursor.moveToNext()) {
			list.add(cursor.getString(0));

		}
		return list;
	}

	/**
	 * ����һ�����±���������Ƶ��¼
	 * @param newList
	 * @param nid
	 * @return
	 */
	public List<String> updateNoteVideo(List<String> newList, int nid) {
		// ��Ҫɾ������Ƶlist
		List<String> noExistList = null;
		// ����ӵ���Ƶlist
		List<String> needAddList = null;

		// ԭ�����ڸü��±���������Ƶ
		List<String> oldList = getVideoFromNid(nid + "");

		// ��newlist��oldlist���бȽ�
		needAddList = FileUtil.getNeedAddList(newList, oldList);
		noExistList = FileUtil.getNoExistList(newList, oldList);

		// ��ӽ������ݿ�
		for (int i = 0; i < needAddList.size(); i++) {
			addNewVideo(nid, needAddList.get(i));
			System.out.println("���������ӵ�-----" + needAddList.get(i));
		}
		// �����ݿ���ɾ��
		for (int i = 0; i < noExistList.size(); i++) {
			deleteOneVideo(noExistList.get(i));
			System.out.println("����Ǿɵ�-----" + noExistList.get(i));
		}

		for (int i = 0; i < newList.size(); i++) {
			System.out.println("�µ�" + newList.get(i));
		}

		for (int i = 0; i < oldList.size(); i++) {
			System.out.println("�ɵ�" + oldList.get(i));
		}

		return noExistList;

	}

	/**
	 * ɾ��һ����Ƶ��¼
	 * 
	 * @param vidName
	 */
	public void deleteOneVideo(String vidName) {
		System.out.println("�ɹ�ɾ����Ƶ��¼��"
				+ sqLiteDatabase.delete("tb_video", "i_name=?",
						new String[] { vidName }));

	}

}
