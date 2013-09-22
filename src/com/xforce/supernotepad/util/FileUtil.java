package com.xforce.supernotepad.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.xforce.supernotepad.model.PictureModel;

public class FileUtil {

	/**
	 * 获取newList不存在但在oldList中存在的元素，即需要移除的元素
	 * 
	 * @param newList
	 * @param oldList
	 * @return 需要删除的list
	 */
	public static List<String> getNoExistList(List<String> newList,
			List<String> oldList) {
		List<String> noExistList = new ArrayList<String>();
		for (int i = 0; i < oldList.size(); i++) {
			if (newList.size() == 0) {
				// 此时均为全部都需要删除
				// noExistList.add(oldList.get(i));
				return oldList;
			} else {
				for (int j = 0; j < newList.size(); j++) {
					if (oldList.get(i).equals(newList.get(j))) {
						break;
					} else if (newList.size() - 1 == j) {
						// 将每一次遍历出的需要删除的保存在list中
						noExistList.add(oldList.get(i));
					}
				}
			}
		}
		return noExistList;

	}

	/**
	 * 获取new图片List不存在但在old图片List中存在的元素，即需要移除的元素
	 * @param newList
	 * @param oldList
	 * @return 需要删除的图片list
	 */
	public static List<PictureModel> getNoExistPictureList(
			List<PictureModel> newList, List<PictureModel> oldList) {
		List<PictureModel> noExistList = new ArrayList<PictureModel>();
		for (int i = 0; i < oldList.size(); i++) {
			if (newList.size() == 0) {
				// 此时均为全部都需要删除
				// noExistList.add(oldList.get(i));
				return oldList;
			} else {
				for (int j = 0; j < newList.size(); j++) {
					if (oldList.get(i).getpName()
							.equals(newList.get(j).getpName())) {
						break;
					} else if (newList.size() - 1 == j) {
						// 将每一次遍历出的需要删除的保存在list中
						noExistList.add(oldList.get(i));
					}
				}
			}
		}
		return noExistList;
	}

	/**
	 * 获取新添加的元素list，即newlist中有，oldlist中没有的元素
	 * 
	 * @param newList
	 * @param oldList
	 * @return 需要添加的list
	 */
	public static List<String> getNeedAddList(List<String> newList,
			List<String> oldList) {
		List<String> needAddList = new ArrayList<String>();
		for (int i = 0; i < newList.size(); i++) {
			if (oldList.size() == 0) {
				// 此时都是新添加的
				// needAddList.add(newList.get(i));
				return newList;
			} else {
				for (int j = 0; j < oldList.size(); j++) {
					if (newList.get(i).equals(oldList.get(j))) {
						break;
					} else if (oldList.size() - 1 == j) {
						// 将每一次遍历出新记录保存在list中
						needAddList.add(newList.get(i));
					}
				}
			}
		}
		return needAddList;
	}

	/**
	 * 获取新添加的图片list，即newlist中有，oldlist中没有的元素
	 * @param newList
	 * @param oldList
	 * @return 需要添加图片list
	 */
	public static List<PictureModel> getNeedAddPictureList(
			List<PictureModel> newList, List<PictureModel> oldList) {
		List<PictureModel> needAddList = new ArrayList<PictureModel>();
		for (int i = 0; i < newList.size(); i++) {
			if (oldList.size() == 0) {
				// 此时都是新添加的
				// needAddList.add(newList.get(i));
				return newList;
			} else {
				for (int j = 0; j < oldList.size(); j++) {
					if (newList.get(i).getpName()
							.equals(oldList.get(j).getpName())) {
						break;
					} else if (oldList.size() - 1 == j) {
						// 将每一次遍历出新记录保存在list中
						needAddList.add(newList.get(i));
					}
				}
			}
		}
		return needAddList;
	}

	/**
	 * 删除SDcard中的一个多媒体文件
	 * 
	 * @param file
	 */
	public static void deleteMediaInSdcard(File file) {

		if (file.exists()) {
			System.out.println("从sdcard中删除多媒体------" + file.delete());
		}
	}

	/**
	 * 删除Sdcard中的一组多媒体文件
	 * 
	 * @param mediaList
	 *            多媒体列表
	 * @param mediaPath
	 *            所在文件夹路径
	 */
	public static void deleteMediaInSdcard(List<String> mediaList,
			String mediaPath) {
		for (int i = 0; i < mediaList.size(); i++) {
			File file = new File(mediaPath + mediaList.get(i));
			if (file.exists()) {
				System.out.println("从sdcard中删除多媒体------" + file.delete());
			}
		}
	}

	/**
	 * 删除SDcard中一组图片文件
	 * @param mediaList
	 */
	public static void deleteMediaInSdcard(List<PictureModel> mediaList) {
		for (int i = 0; i < mediaList.size(); i++) {
			File file = new File(Utils.PIC_PATH + mediaList.get(i).getpName());
			if (file.exists()) {
				System.out.println("从sdcard中删除多媒体------" + file.delete());
			}
		}
	}

}
