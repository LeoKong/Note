package com.xforce.supernotepad.dao;

import java.util.ArrayList;
import java.util.List;

import com.xforce.supernotepad.util.FileUtil;
import com.xforce.supernotepad.util.Utils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VoiceDao {
	SQLiteDatabase sqLiteDatabase;

	public VoiceDao(Context context) {
		super();
		// TODO Auto-generated constructor stub
		sqLiteDatabase = Utils.getDataBase(context);
	}

	/**
	 * �����±������һ��¼����¼
	 * 
	 * @param nid
	 *            �������±���
	 * @param vName
	 *            ¼����
	 */
	public void addNewVoice(int nid, String vName) {
		ContentValues values = new ContentValues();
		values.put("n_id", nid);
		values.put("v_name", vName);
		System.out.println(sqLiteDatabase.insert("tb_voi", "v_id", values)
				+ "����¼����");
	}

	/**
	 * ���ݼ��±�nid��������������¼��
	 * 
	 * @param nid
	 *            �������±�id
	 * @return
	 */
	public List<String> getVoiFromNid(String nid) {
		List<String> list = new ArrayList<String>();

		Cursor cursor = sqLiteDatabase.query("tb_voi",
				new String[] { "v_name" }, "n_id=?", new String[] { nid },
				null, null, null);

		while (cursor.moveToNext()) {
			list.add(cursor.getString(0));

		}

		return list;

	}

	/**
	 * ����һ�����±�������¼����¼
	 * @param newList
	 * @param nid
	 * @return ��Ҫɾ����¼��list
	 */
	public List<String> updateNoteVoi(List<String> newList, int nid) {
		// ��Ҫɾ����¼��list
		List<String> noExistList = null;
		// ����ӵ�¼��list
		List<String> needAddList = null;

		// ԭ�����ڸü��±�������¼��
		List<String> oldList = getVoiFromNid(nid + "");

		// ��newlist��oldlist���бȽ�
		needAddList = FileUtil.getNeedAddList(newList, oldList);
		noExistList = FileUtil.getNoExistList(newList, oldList);

		// ��ӽ������ݿ�
		for (int i = 0; i < needAddList.size(); i++) {
			addNewVoice(nid, needAddList.get(i));
			System.out.println("���������ӵ�-----" + needAddList.get(i));
		}
		// �����ݿ���ɾ��
		for (int i = 0; i < noExistList.size(); i++) {
			deleteOneVoice(noExistList.get(i));
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
	 * ɾ��һ��¼���ļ�¼
	 * 
	 * @param vName
	 */
	public void deleteOneVoice(String vName) {
		System.out.println("�ɹ�ɾ��¼����"
				+ sqLiteDatabase.delete("tb_voi", "v_name=?",
						new String[] { vName }));
	}

}
