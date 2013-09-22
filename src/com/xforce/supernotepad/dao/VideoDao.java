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
	 * 往一个记事本中添加一条视频记录
	 * 
	 * @param nid
	 *            所属记事本号
	 * @param vidName
	 */
	public void addNewVideo(int nid, String vidName) {
		ContentValues values = new ContentValues();
		values.put("n_id", nid);
		values.put("i_name", vidName);
		System.out.println(sqLiteDatabase.insert("tb_video", "i_id", values)
				+ "插入视频了");
	}

	/**
	 * 根据记事本nid返回所属的所有视频
	 * 
	 * @param nid
	 *            所属记事本id
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
	 * 更行一个记事本的所有视频记录
	 * @param newList
	 * @param nid
	 * @return
	 */
	public List<String> updateNoteVideo(List<String> newList, int nid) {
		// 需要删除的视频list
		List<String> noExistList = null;
		// 新添加的视频list
		List<String> needAddList = null;

		// 原本属于该记事本的所有视频
		List<String> oldList = getVideoFromNid(nid + "");

		// 将newlist和oldlist进行比较
		needAddList = FileUtil.getNeedAddList(newList, oldList);
		noExistList = FileUtil.getNoExistList(newList, oldList);

		// 添加进入数据库
		for (int i = 0; i < needAddList.size(); i++) {
			addNewVideo(nid, needAddList.get(i));
			System.out.println("这个是新添加的-----" + needAddList.get(i));
		}
		// 从数据库中删除
		for (int i = 0; i < noExistList.size(); i++) {
			deleteOneVideo(noExistList.get(i));
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
	 * 删除一条视频记录
	 * 
	 * @param vidName
	 */
	public void deleteOneVideo(String vidName) {
		System.out.println("成功删除视频记录："
				+ sqLiteDatabase.delete("tb_video", "i_name=?",
						new String[] { vidName }));

	}

}
