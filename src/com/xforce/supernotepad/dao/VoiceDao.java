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
	 * 往记事本中添加一条录音记录
	 * 
	 * @param nid
	 *            所属记事本号
	 * @param vName
	 *            录音名
	 */
	public void addNewVoice(int nid, String vName) {
		ContentValues values = new ContentValues();
		values.put("n_id", nid);
		values.put("v_name", vName);
		System.out.println(sqLiteDatabase.insert("tb_voi", "v_id", values)
				+ "插入录音了");
	}

	/**
	 * 根据记事本nid返回所属的所有录音
	 * 
	 * @param nid
	 *            所属记事本id
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
	 * 更新一个记事本中所有录音记录
	 * @param newList
	 * @param nid
	 * @return 需要删除的录音list
	 */
	public List<String> updateNoteVoi(List<String> newList, int nid) {
		// 需要删除的录音list
		List<String> noExistList = null;
		// 新添加的录音list
		List<String> needAddList = null;

		// 原本属于该记事本的所有录音
		List<String> oldList = getVoiFromNid(nid + "");

		// 将newlist和oldlist进行比较
		needAddList = FileUtil.getNeedAddList(newList, oldList);
		noExistList = FileUtil.getNoExistList(newList, oldList);

		// 添加进入数据库
		for (int i = 0; i < needAddList.size(); i++) {
			addNewVoice(nid, needAddList.get(i));
			System.out.println("这个是新添加的-----" + needAddList.get(i));
		}
		// 从数据库中删除
		for (int i = 0; i < noExistList.size(); i++) {
			deleteOneVoice(noExistList.get(i));
			System.out.println("这个是旧的-----" + noExistList.get(i));
		}

		for (int i = 0; i < newList.size(); i++) {
			System.out.println("新的" + newList.get(i));
		}

		for (int i = 0; i < oldList.size(); i++) {
			System.out.println("旧的" + oldList.get(i));
		}

		return noExistList;

	}

	/**
	 * 删除一个录音的记录
	 * 
	 * @param vName
	 */
	public void deleteOneVoice(String vName) {
		System.out.println("成功删除录音："
				+ sqLiteDatabase.delete("tb_voi", "v_name=?",
						new String[] { vName }));
	}

}
