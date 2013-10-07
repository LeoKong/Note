package com.xforce.supernotepad.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xforce.supernotepad.model.NoteDetail;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.Utils;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NoteDetailDao {
	SQLiteDatabase sqLiteDatabase;
	PictureDao pictureDao;

	public NoteDetailDao(Context context) {
		super();
		sqLiteDatabase = Utils.getDataBase(context);
		pictureDao = new PictureDao(context);
		// TODO Auto-generated constructor stub
	}

	// ���ȫ�����±�
	// public List<NoteDetail> getAllNote() {
	// List<NoteDetail> list = new ArrayList<NoteDetail>();
	//
	// Cursor cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
	// "t_id", "Ctime", "Rtime", "content" }, null, null, null, null,
	// null);
	//
	// while (cursor.moveToNext()) {
	//
	// NoteDetail noteDetail = new NoteDetail();
	//
	// noteDetail.setN_id(cursor.getInt(0));
	// noteDetail.setT_id(cursor.getInt(1));
	// noteDetail.setCtime(cursor.getString(2));
	// noteDetail.setRtime(cursor.getString(3));
	// noteDetail.setContent(cursor.getString(4));
	//
	// list.add(noteDetail);
	// }
	//
	// return list;
	//
	// }

	// ��ȡָ������е����м��±�
	// public List<NoteDetail> getAllNote(int tid) {
	// List<NoteDetail> list = new ArrayList<NoteDetail>();
	//
	// Cursor cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
	// "t_id", "Ctime", "Rtime", "content" }, "t_id=?",
	// new String[] { tid + "" }, null, null, null);
	//
	// while (cursor.moveToNext()) {
	// NoteDetail noteDetail = new NoteDetail();
	//
	// noteDetail.setN_id(cursor.getInt(0));
	// noteDetail.setT_id(cursor.getInt(1));
	// noteDetail.setCtime(cursor.getString(2));
	// noteDetail.setRtime(cursor.getString(3));
	// noteDetail.setContent(cursor.getString(4));
	//
	// list.add(noteDetail);
	// }

	// return list;
	// }

	/**
	 * ����:����nid���ص������±�
	 * 
	 * @return NoteDetail
	 */
	public NoteDetail getOneNote(String nid) {
		NoteDetail noteDetail = new NoteDetail();
		Cursor cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
				"t_id", "Ctime", "Rtime", "content", "locked" }, "n_id=?",
				new String[] { nid }, null, null, null);
		if (cursor.moveToNext()) {
			noteDetail.setN_id(cursor.getInt(0));
			noteDetail.setT_id(cursor.getInt(1));
			noteDetail.setCtime(cursor.getString(2));
			noteDetail.setRtime(cursor.getString(3));
			noteDetail.setContent(cursor.getString(4));
			noteDetail.setLocked(cursor.getInt(5));
		}

		cursor.close();
		return noteDetail;
	}

	/**
	 * �����������ݷ����������
	 * 
	 * @param tid
	 * @param searchString
	 * @return ���м��±�����
	 */
	public List<Map<String, Object>> getSearchResult(int tid,
			String searchString) {

		List<Map<String, Object>> searchList = new ArrayList<Map<String, Object>>();
		Cursor cursor = null;
		if (tid == 1) {// �����1��ʾ��ʾ����
			cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
					"t_id", "Ctime", "Rtime", "content", "locked" },
					"content like ?", new String[] { searchString }, null,
					null, null);
		} else {
			cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
					"t_id", "Ctime", "Rtime", "content", "locked" },
					"content like ? and t_id=?", new String[] { searchString,
							tid + "" }, null, null, null);
		}

		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nid", cursor.getInt(0));
			map.put("content", cursor.getString(4));
			map.put("date", cursor.getString(2));
			map.put("locked", cursor.getInt(5));
			map.put("remind", cursor.getString(3));
			map.put("pic", "");

			List<PictureModel> picList = pictureDao.getPicFromNid(cursor
					.getInt(0) + "");
			if (picList.size() > 0) {
				map.put("pic", picList.get(0).getpName());
			}
			searchList.add(map);

		}

		cursor.close();
		return searchList;

	}

	/**
	 * ����:������ŷ�����Ҫ��listview��ʾ��list����
	 * 
	 * @return List
	 */
	public List<Map<String, Object>> getAllNoteForListView(int tid, String order) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		Cursor cursor = null;

		if (tid == 1) {// �����1��ʾ��ʾ����
			cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
					"t_id", "Ctime", "Rtime", "content", "locked" }, null,
					null, null, null, "n_id " + order);
		} else {
			cursor = sqLiteDatabase.query("tb_note", new String[] { "n_id",
					"t_id", "Ctime", "Rtime", "content", "locked" }, "t_id=?",
					new String[] { tid + "" }, null, null, "n_id " + order);
		}

		while (cursor.moveToNext()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("nid", cursor.getInt(0));
			map.put("content", cursor.getString(4));
			map.put("date", cursor.getString(2));
			map.put("locked", cursor.getInt(5));
			map.put("remind", cursor.getString(3));
			map.put("pic", "");

			List<PictureModel> picList = pictureDao.getPicFromNid(cursor
					.getInt(0) + "");
			if (picList.size() > 0) {
				map.put("pic", picList.get(0).getpName());
			}

			list.add(map);
		}
		cursor.close();
		return list;
	}

	/**
	 * ���һ�����±�
	 * 
	 * @param noteDetail
	 * @return ��Ӱ�������
	 */
	public int addNewNote(NoteDetail noteDetail) {
		ContentValues values = new ContentValues();
		values.put("t_id", noteDetail.getT_id());
		values.put("Ctime", noteDetail.getCtime());
		values.put("Rtime", noteDetail.getRtime());
		values.put("content", noteDetail.getContent());
		values.put("locked", noteDetail.getLocked());

		return (int) sqLiteDatabase.insert("tb_note", "n_id", values);
	}

	/**
	 * ɾ��һ�����±�
	 * 
	 * @param nid
	 *            ���±�id
	 */
	public void deleteNote(String nid) {
		sqLiteDatabase.delete("tb_note", "n_id=?", new String[] { nid });
	}

	/**
	 * �޸�һ�����±����ڵķ���
	 * 
	 * @param nid
	 *            ���±���
	 * @param new_tid
	 *            �����
	 * 
	 */
	public void updateNoteType(String nid, String new_tid) {
		ContentValues values = new ContentValues();
		values.put("t_id", new_tid);
		sqLiteDatabase
				.update("tb_note", values, "n_id=?", new String[] { nid });
	}

	/**
	 * ����һ�����±��ļ���״̬
	 * 
	 * @param nid
	 * @param lock
	 */
	public void updateNoteLock(String nid, int lock) {
		ContentValues values = new ContentValues();
		values.put("locked", lock);
		sqLiteDatabase
				.update("tb_note", values, "n_id=?", new String[] { nid });

	}

	/**
	 * �޸�һ�����±����ı����ݺͼ���״̬
	 * 
	 * @param nid
	 *            �������±���
	 * @param new_content
	 *            ������
	 * @param locked
	 *            �¼���״̬
	 * @param new_rtime
	 *            ����ʱ��
	 */
	public void updateNoteInfo(int nid, String new_content, int locked,
			String new_rtime) {
		ContentValues values = new ContentValues();
		values.put("content", new_content);
		values.put("Rtime", new_rtime);
		values.put("locked", locked);
		sqLiteDatabase.update("tb_note", values, "n_id=?", new String[] { nid
				+ "" });

	}

	/**
	 * �����������Ƿ��м���
	 * 
	 * @param date
	 * @return
	 */
	public boolean checkHasNote(String date) {
		Cursor cursor = null;
		try {
			cursor = sqLiteDatabase.rawQuery(
					"select n_id from tb_note where Ctime=? limit 1",
					new String[] { date });
			if (cursor.moveToNext()) {
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return false;
	}

	
	/**
	 * ���ظ����������м���
	 * @param dateString
	 * @return
	 */
	public List<Map<String, Object>> getAllNoteFromDate(String dateString) {
		Cursor cursor = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			cursor = sqLiteDatabase.query("tb_note", null, "Ctime=?",
					new String[] { dateString }, null, null, null);
			
			while (cursor.moveToNext()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("nid", cursor.getInt(0));
				map.put("content", cursor.getString(4));
				map.put("date", cursor.getString(2));
				map.put("locked", cursor.getInt(5));
				map.put("remind", cursor.getString(3));
				map.put("pic", "");

				List<PictureModel> picList = pictureDao.getPicFromNid(cursor
						.getInt(0) + "");
				if (picList.size() > 0) {
					map.put("pic", picList.get(0).getpName());
				}

				list.add(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;

	}

}
