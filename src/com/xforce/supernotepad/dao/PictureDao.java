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
	 * 根据记事本nid返回所属的所有图片
	 * 
	 * @param nid
	 *            所属记事本号
	 * @return list 所有图片名称
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
	 * 往一个记事本中添加一个图片
	 * @param nid 所属记事本id
	 * @param pictureModel 图片实体
	 */
	public void addNewPicture(String nid,  PictureModel pictureModel) {
		ContentValues values = new ContentValues();
		values.put("n_id", nid);
		values.put("p_name", pictureModel.getpName());
		values.put("illustration", pictureModel.getIllustration());
		System.out.println(sqLiteDatabase.insert("tb_pic", "p_id", values)
				+ "插入图片");

	}

	/**
	 * 删除一张图片的记录
	 * 
	 * @param pName
	 *            图片名
	 */
	public void deleteOnePicture(String pName) {
		System.out.println("成功删除图片："
				+ sqLiteDatabase.delete("tb_pic", "p_name=?",
						new String[] { pName }));
	}
	
	/**
	 * 删除一个记事本中所有图片记录
	 * @param nid
	 */
	public void deleteAllPic(String nid){
		System.out.println("成功删除图片："
				+ sqLiteDatabase.delete("tb_pic", "n_id=?",
						new String[] { nid }));
	}

	/**
	 * 更新一个记事本中的所有图片
	 * 
	 * @param newList
	 * @param nid
	 * @return
	 */
	public List<PictureModel> updateNotePic(List<PictureModel> newList, int nid) {
		// 保存需要从sdcard中删除的图片名(在newlist中不存在但在数据库中存在)
		List<PictureModel> noExistList = null;
		//新添加的图片名list
		List<PictureModel> needAddList=null;
		
		// 原本属于该记事本的所有图片
		List<PictureModel> oldList = getPicFromNid(nid + "");
		// 遍历出新添加的图片并添加进tb_pic数据库中
//		for (int i = 0; i < newList.size(); i++) {
//			if (oldList.size()==0) {
//				//此时都是新添加的
//				addNewPicture(nid, newList.get(i));
//				System.out.println(newList.get(i) + "------这个是新的");
//			}
//			else {
//				for (int j = 0; j < oldList.size(); j++) {
//					if (newList.get(i).equals(oldList.get(j))) {
//						break;
//					} else if (oldList.size() - 1 == j) {
//						// 将每一次遍历出新图片保存入数据库中
//						addNewPicture(nid, newList.get(i));
//						System.out.println(newList.get(i) + "------这个是新的");
//					}
//				}
//			}
//		}

		// 遍历出需要从数据库中删除的图片
//		for (int i = 0; i < oldList.size(); i++) {
//			if (newList.size()==0) {
//				//此时均为全部都需要删除
//				noExistList.add(oldList.get(i));
//				deleteOnePicture(oldList.get(i));
//				System.out.println(oldList.get(i) + "------这个是要删除的");
//			}else {
//				for (int j = 0; j < newList.size(); j++) {
//					if (oldList.get(i).equals(newList.get(j))) {
//						break;
//					} else if (newList.size() - 1 == j) {
//						// 将每一次遍历出的需要删除的图片从数据库中删除
//						noExistList.add(oldList.get(i));
//						deleteOnePicture(oldList.get(i));
//						System.out.println(oldList.get(i) + "------这个是要删除的");
//					}
//				}
//			}
			
//		}
		//将newlist和oldlist进行比较
		needAddList=FileUtil.getNeedAddPictureList(newList, oldList);
		noExistList=FileUtil.getNoExistPictureList(newList, oldList);
//		for (int i = 0; i < needAddList.size(); i++) {
//			System.out.println("这个是新添加的-----"+needAddList.get(i).getpName());
//		}
//		for (int i = 0; i < noExistList.size(); i++) {
//			System.out.println("这个是旧的-----"+noExistList.get(i).getpName());
//		}
//
//		for (int i = 0; i < newList.size(); i++) {
//			System.out.println("新的" + newList.get(i).getpName());
//		}
//
//		for (int i = 0; i < oldList.size(); i++) {
//			System.out.println("旧的" + oldList.get(i).getpName());
//		}
		
		//将该记事本中原来的所有图片记录从数据库中删除
		deleteAllPic(nid+"");
		//添加最新的所有图片记录
		for (int i = 0; i < newList.size(); i++) {
			addNewPicture(nid+"", newList.get(i));
		}

		return noExistList;

	}

}
