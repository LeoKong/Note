package com.xforce.supernotepad.dao;

import java.util.ArrayList;
import java.util.List;

import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.FileUtil;
import com.xforce.supernotepad.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class PictureDao {
	SQLiteDatabase sqLiteDatabase;

	public PictureDao(Context context) {
		super();
		sqLiteDatabase = Utils.getDataBase(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * ���ݼ��±�nid��������������ͼƬ
	 * 
	 * @param nid
	 *            �������±���
	 * @return list ����ͼƬ����
	 */
	public List<PictureModel> getPicFromNid(String nid) {
		List<PictureModel> list = new ArrayList<PictureModel>();

		Cursor cursor = sqLiteDatabase.query("tb_pic",
				new String[] { "p_name","illustration" }, "n_id=?", new String[] { nid },
				null, null, null);
		while (cursor.moveToNext()) {
			PictureModel pictureModel=new PictureModel();
			pictureModel.setpName(cursor.getString(0));
			pictureModel.setIllustration(cursor.getString(1));
			list.add(pictureModel);
		}

		return list;

	}
	
	/**
	 * ��һ�����±������һ��ͼƬ
	 * @param nid �������±�id
	 * @param pictureModel ͼƬʵ��
	 */
	public void addNewPicture(String nid,  PictureModel pictureModel) {
		ContentValues values = new ContentValues();
		values.put("n_id", nid);
		values.put("p_name", pictureModel.getpName());
		values.put("illustration", pictureModel.getIllustration());
		System.out.println(sqLiteDatabase.insert("tb_pic", "p_id", values)
				+ "����ͼƬ");

	}

	/**
	 * ɾ��һ��ͼƬ�ļ�¼
	 * 
	 * @param pName
	 *            ͼƬ��
	 */
	public void deleteOnePicture(String pName) {
		System.out.println("�ɹ�ɾ��ͼƬ��"
				+ sqLiteDatabase.delete("tb_pic", "p_name=?",
						new String[] { pName }));
	}
	
	/**
	 * ɾ��һ�����±�������ͼƬ��¼
	 * @param nid
	 */
	public void deleteAllPic(String nid){
		System.out.println("�ɹ�ɾ��ͼƬ��"
				+ sqLiteDatabase.delete("tb_pic", "n_id=?",
						new String[] { nid }));
	}

	/**
	 * ����һ�����±��е�����ͼƬ
	 * 
	 * @param newList
	 * @param nid
	 * @return
	 */
	public List<PictureModel> updateNotePic(List<PictureModel> newList, int nid) {
		// ������Ҫ��sdcard��ɾ����ͼƬ��(��newlist�в����ڵ������ݿ��д���)
		List<PictureModel> noExistList = null;
		//����ӵ�ͼƬ��list
		List<PictureModel> needAddList=null;
		
		// ԭ�����ڸü��±�������ͼƬ
		List<PictureModel> oldList = getPicFromNid(nid + "");
		// ����������ӵ�ͼƬ����ӽ�tb_pic���ݿ���
//		for (int i = 0; i < newList.size(); i++) {
//			if (oldList.size()==0) {
//				//��ʱ��������ӵ�
//				addNewPicture(nid, newList.get(i));
//				System.out.println(newList.get(i) + "------������µ�");
//			}
//			else {
//				for (int j = 0; j < oldList.size(); j++) {
//					if (newList.get(i).equals(oldList.get(j))) {
//						break;
//					} else if (oldList.size() - 1 == j) {
//						// ��ÿһ�α�������ͼƬ���������ݿ���
//						addNewPicture(nid, newList.get(i));
//						System.out.println(newList.get(i) + "------������µ�");
//					}
//				}
//			}
//		}

		// ��������Ҫ�����ݿ���ɾ����ͼƬ
//		for (int i = 0; i < oldList.size(); i++) {
//			if (newList.size()==0) {
//				//��ʱ��Ϊȫ������Ҫɾ��
//				noExistList.add(oldList.get(i));
//				deleteOnePicture(oldList.get(i));
//				System.out.println(oldList.get(i) + "------�����Ҫɾ����");
//			}else {
//				for (int j = 0; j < newList.size(); j++) {
//					if (oldList.get(i).equals(newList.get(j))) {
//						break;
//					} else if (newList.size() - 1 == j) {
//						// ��ÿһ�α���������Ҫɾ����ͼƬ�����ݿ���ɾ��
//						noExistList.add(oldList.get(i));
//						deleteOnePicture(oldList.get(i));
//						System.out.println(oldList.get(i) + "------�����Ҫɾ����");
//					}
//				}
//			}
			
//		}
		//��newlist��oldlist���бȽ�
		needAddList=FileUtil.getNeedAddPictureList(newList, oldList);
		noExistList=FileUtil.getNoExistPictureList(newList, oldList);
//		for (int i = 0; i < needAddList.size(); i++) {
//			System.out.println("���������ӵ�-----"+needAddList.get(i).getpName());
//		}
//		for (int i = 0; i < noExistList.size(); i++) {
//			System.out.println("����Ǿɵ�-----"+noExistList.get(i).getpName());
//		}
//
//		for (int i = 0; i < newList.size(); i++) {
//			System.out.println("�µ�" + newList.get(i).getpName());
//		}
//
//		for (int i = 0; i < oldList.size(); i++) {
//			System.out.println("�ɵ�" + oldList.get(i).getpName());
//		}
		
		//���ü��±���ԭ��������ͼƬ��¼�����ݿ���ɾ��
		deleteAllPic(nid+"");
		//������µ�����ͼƬ��¼
		for (int i = 0; i < newList.size(); i++) {
			addNewPicture(nid+"", newList.get(i));
		}

		return noExistList;

	}

}
