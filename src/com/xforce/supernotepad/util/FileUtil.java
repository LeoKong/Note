package com.xforce.supernotepad.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xforce.supernotepad.model.PictureModel;

public class FileUtil {

	/**
	 * ��ȡnewList�����ڵ���oldList�д��ڵ�Ԫ�أ�����Ҫ�Ƴ���Ԫ��
	 * 
	 * @param newList
	 * @param oldList
	 * @return ��Ҫɾ����list
	 */
	public static List<String> getNoExistList(List<String> newList,
			List<String> oldList) {
		List<String> noExistList = new ArrayList<String>();
		for (int i = 0; i < oldList.size(); i++) {
			if (newList.size() == 0) {
				// ��ʱ��Ϊȫ������Ҫɾ��
				// noExistList.add(oldList.get(i));
				return oldList;
			} else {
				for (int j = 0; j < newList.size(); j++) {
					if (oldList.get(i).equals(newList.get(j))) {
						break;
					} else if (newList.size() - 1 == j) {
						// ��ÿһ�α���������Ҫɾ���ı�����list��
						noExistList.add(oldList.get(i));
					}
				}
			}
		}
		return noExistList;

	}

	/**
	 * ��ȡnewͼƬList�����ڵ���oldͼƬList�д��ڵ�Ԫ�أ�����Ҫ�Ƴ���Ԫ��
	 * @param newList
	 * @param oldList
	 * @return ��Ҫɾ����ͼƬlist
	 */
	public static List<PictureModel> getNoExistPictureList(
			List<PictureModel> newList, List<PictureModel> oldList) {
		List<PictureModel> noExistList = new ArrayList<PictureModel>();
		for (int i = 0; i < oldList.size(); i++) {
			if (newList.size() == 0) {
				// ��ʱ��Ϊȫ������Ҫɾ��
				// noExistList.add(oldList.get(i));
				return oldList;
			} else {
				for (int j = 0; j < newList.size(); j++) {
					if (oldList.get(i).getpName()
							.equals(newList.get(j).getpName())) {
						break;
					} else if (newList.size() - 1 == j) {
						// ��ÿһ�α���������Ҫɾ���ı�����list��
						noExistList.add(oldList.get(i));
					}
				}
			}
		}
		return noExistList;
	}

	/**
	 * ��ȡ����ӵ�Ԫ��list����newlist���У�oldlist��û�е�Ԫ��
	 * 
	 * @param newList
	 * @param oldList
	 * @return ��Ҫ��ӵ�list
	 */
	public static List<String> getNeedAddList(List<String> newList,
			List<String> oldList) {
		List<String> needAddList = new ArrayList<String>();
		for (int i = 0; i < newList.size(); i++) {
			if (oldList.size() == 0) {
				// ��ʱ��������ӵ�
				// needAddList.add(newList.get(i));
				return newList;
			} else {
				for (int j = 0; j < oldList.size(); j++) {
					if (newList.get(i).equals(oldList.get(j))) {
						break;
					} else if (oldList.size() - 1 == j) {
						// ��ÿһ�α������¼�¼������list��
						needAddList.add(newList.get(i));
					}
				}
			}
		}
		return needAddList;
	}

	/**
	 * ��ȡ����ӵ�ͼƬlist����newlist���У�oldlist��û�е�Ԫ��
	 * @param newList
	 * @param oldList
	 * @return ��Ҫ���ͼƬlist
	 */
	public static List<PictureModel> getNeedAddPictureList(
			List<PictureModel> newList, List<PictureModel> oldList) {
		List<PictureModel> needAddList = new ArrayList<PictureModel>();
		for (int i = 0; i < newList.size(); i++) {
			if (oldList.size() == 0) {
				// ��ʱ��������ӵ�
				// needAddList.add(newList.get(i));
				return newList;
			} else {
				for (int j = 0; j < oldList.size(); j++) {
					if (newList.get(i).getpName()
							.equals(oldList.get(j).getpName())) {
						break;
					} else if (oldList.size() - 1 == j) {
						// ��ÿһ�α������¼�¼������list��
						needAddList.add(newList.get(i));
					}
				}
			}
		}
		return needAddList;
	}

	/**
	 * ɾ��SDcard�е�һ����ý���ļ�
	 * 
	 * @param file
	 */
	public static void deleteMediaInSdcard(File file) {

		if (file.exists()) {
			System.out.println("��sdcard��ɾ����ý��------" + file.delete());
		}
	}

	/**
	 * ɾ��Sdcard�е�һ���ý���ļ�
	 * 
	 * @param mediaList
	 *            ��ý���б�
	 * @param mediaPath
	 *            �����ļ���·��
	 */
	public static void deleteMediaInSdcard(List<String> mediaList,
			String mediaPath) {
		for (int i = 0; i < mediaList.size(); i++) {
			File file = new File(mediaPath + mediaList.get(i));
			if (file.exists()) {
				System.out.println("��sdcard��ɾ����ý��------" + file.delete());
			}
		}
	}

	/**
	 * ɾ��SDcard��һ��ͼƬ�ļ�
	 * @param mediaList
	 */
	public static void deleteMediaInSdcard(List<PictureModel> mediaList) {
		for (int i = 0; i < mediaList.size(); i++) {
			File file = new File(Utils.PIC_PATH + mediaList.get(i).getpName());
			if (file.exists()) {
				System.out.println("��sdcard��ɾ����ý��------" + file.delete());
			}
		}
	}

}
