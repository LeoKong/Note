package com.xforce.supernotepad.view;

import java.util.ArrayList;
import java.util.List;

import com.umeng.analytics.MobclickAgent;
import com.xforce.supernotepad.dao.NoteDetailDao;
import com.xforce.supernotepad.dao.PictureDao;
import com.xforce.supernotepad.dao.VideoDao;
import com.xforce.supernotepad.dao.VoiceDao;
import com.xforce.supernotepad.model.NoteDetail;
import com.xforce.supernotepad.model.PictureModel;
import com.xforce.supernotepad.util.AlarmSetting;
import com.xforce.supernotepad.util.FaceParser;
import com.xforce.supernotepad.util.FileUtil;
import com.xforce.supernotepad.util.Utils;



import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class EditNoteActivity extends AddNoteActivity {
	NoteDetailDao noteDetailDao;
	PictureDao pictureDao;
	VoiceDao voiceDao;
	VideoDao videoDao;

	private int nid = 0;// 当前记事本的id号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		noteDetailDao = new NoteDetailDao(EditNoteActivity.this);
		pictureDao = new PictureDao(EditNoteActivity.this);
		voiceDao = new VoiceDao(EditNoteActivity.this);
		videoDao = new VideoDao(EditNoteActivity.this);

		NoteDetail noteDetail = noteDetailDao.getOneNote(getIntent()
				.getStringExtra("nid"));

		// 在edittexe显示原有内容
		FaceParser faceParser = new FaceParser(this);
		contentEdit.setText(faceParser.replace(noteDetail.getContent()));
		
		//从数据库得到该记事本的加锁状态
		locked=noteDetail.getLocked();
		//改变加锁按钮图标
		changeLockState(locked);
		
		//显示提醒时间
		String Rtime=noteDetail.getRtime();
		if (Rtime!=null) {
			//初始化值
			needRemind=true;
			remindString=Rtime;
			//如果已过期
			if (Utils.checkOverdue(Rtime)) {
				remindTimeTextView.setText("提醒已过期："+Rtime);
			}else {
				remindTimeTextView.setText("提醒："+Rtime);
			}
			
		}

		// 显示该记事本所属的全部图片
		nid = noteDetail.getN_id();
		showAllPicInOneNote(nid);
		showAllVoiInOneNote(nid);
		showAllVideoInOneNote(nid);

		saveButton.setOnClickListener(saveButtOnClickListener);
		backButton.setOnClickListener(backButtonClickListener);

	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 开启友盟统计
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// 停止友盟统计
		MobclickAgent.onPause(this);
	}

	/**
	 * 点击保存btn触发的事件
	 */
	public OnClickListener saveButtOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			String newContent = contentEdit.getText().toString();
			//判断当前是否有提醒时间或提醒时间是否有更新
			if (!needRemind) {
				remindString=null;
			}else {
				if (Rdate!=null) {
					AlarmSetting.setAlarm(EditNoteActivity.this, nid, Rdate);
				}
			}
			// 更新数据库中文本内容,加锁状态,提醒时间
			noteDetailDao.updateNoteInfo(nid, newContent,locked,remindString);
			// 更新图片，返回需要从数据库中删除的图片
			List<PictureModel> deletePicList = pictureDao.updateNotePic(picList, nid);
			// 将需要删除的图片从sdcard中删除
			if (deletePicList.size() > 0) {
				FileUtil.deleteMediaInSdcard(deletePicList);
			}

			// 更新录音，返回需要从数据库中删除的录音
			List<String> deleteVoiList = voiceDao.updateNoteVoi(voiList, nid);
			if (deleteVoiList.size() > 0) {
				FileUtil.deleteMediaInSdcard(deleteVoiList, Utils.VOI_PATH);
			}

			// 更新视频，返回需要从数据库中删除的视频
			List<String> deleteVideoList = videoDao.updateNoteVideo(videoList,
					nid);
			if (deleteVideoList.size() > 0) {
				FileUtil.deleteMediaInSdcard(deleteVideoList, Utils.VID_PATH);
			}

			Toast.makeText(EditNoteActivity.this, "记事本修改成功！", Toast.LENGTH_LONG)
			.show();
			setResult(RESULT_OK);
			EditNoteActivity.this.finish();
		}
	};

	/**
	 * 点击返回btn触发的事件
	 */
	public OnClickListener backButtonClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			showSureExit();
		}
	};
	
	/**
	 * 提示是否退出的对话框
	 */
	public void showSureExit(){
		Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
		builder.setTitle("修改部分将不被保存,确定退出？");
		builder.setPositiveButton("是",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						List<PictureModel> delePicList=getNoNeedPictureList(pictureDao.getPicFromNid(nid+""), picList);
						List<String> deleVoiList=getNoNeedList(voiceDao.getVoiFromNid(nid+""), voiList);
						List<String> deleVidList=getNoNeedList(videoDao.getVideoFromNid(nid+""), videoList);
						//删除SD卡中不需要的多媒体
						FileUtil.deleteMediaInSdcard(delePicList);
						FileUtil.deleteMediaInSdcard(deleVoiList, Utils.VOI_PATH);
						FileUtil.deleteMediaInSdcard(deleVidList, Utils.VID_PATH);
						
						EditNoteActivity.this.finish();
					}
				});
		builder.setNegativeButton("否", null);
		builder.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_HOME) {
			if (isWriting) {
				toNormal();
				return true;
			}
			showSureExit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 获取当前list中新添加的元素
	 * 
	 * @param origList
	 * @param curList
	 * @return 需要删除的list
	 */
	public List<String> getNoNeedList(List<String> origList,
			List<String> curList) {
		List<String> noNeedList = new ArrayList<String>();
		for (int i = 0; i < curList.size(); i++) {
			if (origList.size() == 0) {
				return curList;
			} else {
				for (int j = 0; j < origList.size(); j++) {
					if (curList.get(i).equals(origList.get(j))) {
						break;
					} else if (origList.size() - 1 == j) {
						// 当比较到最后一个元素都不相等
						noNeedList.add(curList.get(i));
					}
				}
			}

		}
		return noNeedList;

	}
	
	/**
	 * 获取当前图片list中新添加的元素
	 * @param origList
	 * @param curList
	 * @return 需要删除的图片list
	 */
	public List<PictureModel> getNoNeedPictureList(List<PictureModel> origList,
			List<PictureModel> curList) {
		List<PictureModel> noNeedList = new ArrayList<PictureModel>();
		for (int i = 0; i < curList.size(); i++) {
			if (origList.size() == 0) {
				return curList;
			} else {
				for (int j = 0; j < origList.size(); j++) {
					if (curList.get(i).getpName().equals(origList.get(j).getpName())) {
						break;
					} else if (origList.size() - 1 == j) {
						// 当比较到最后一个元素都不相等
						noNeedList.add(curList.get(i));
					}
				}
			}

		}
		return noNeedList;

	}

	/**
	 * 显示该记事本所属的全部图片
	 * 
	 * @param nid
	 *            所属记事本号
	 */
	public void showAllPicInOneNote(int nid) {
		// 赋值给全局变量方便下一步的操作
		picList = pictureDao.getPicFromNid(nid + "");
//		if (picList != null) {
//			for (int i = 0; i < picList.size(); i++) {
//				addPicOnNote(picList.get(i).getpName(),true);
//			}
//		}
		initGallery();
	}

	/**
	 * 显示该记事本所属的全部录音
	 * 
	 * @param nid
	 */
	public void showAllVoiInOneNote(int nid) {
		List<String> allVoi = voiceDao.getVoiFromNid(nid + "");
		// 赋值给全局变量voiList
		voiList = allVoi;
		if (allVoi != null) {
			int size=allVoi.size();
			for (int i = 0; i < size; i++) {
				addVoiOnNote(allVoi.get(i), EditNoteActivity.this);
			}
		}
	}

	/**
	 * 显示该记事本所属的所有视频
	 * 
	 * @param nid
	 */
	public void showAllVideoInOneNote(int nid) {
		List<String> allVideo = videoDao.getVideoFromNid(nid + "");
		videoList = allVideo;
		if (allVideo != null) {
			for (int i = 0; i < allVideo.size(); i++) {
				addVidOnNote(allVideo.get(i), EditNoteActivity.this);
			}
		}
	}

}
