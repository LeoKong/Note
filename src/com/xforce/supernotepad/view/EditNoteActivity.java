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

	private int nid = 0;// ��ǰ���±���id��

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

		// ��edittexe��ʾԭ������
		FaceParser faceParser = new FaceParser(this);
		contentEdit.setText(faceParser.replace(noteDetail.getContent()));
		
		//�����ݿ�õ��ü��±��ļ���״̬
		locked=noteDetail.getLocked();
		//�ı������ťͼ��
		changeLockState(locked);
		
		//��ʾ����ʱ��
		String Rtime=noteDetail.getRtime();
		if (Rtime!=null) {
			//��ʼ��ֵ
			needRemind=true;
			remindString=Rtime;
			//����ѹ���
			if (Utils.checkOverdue(Rtime)) {
				remindTimeTextView.setText("�����ѹ��ڣ�"+Rtime);
			}else {
				remindTimeTextView.setText("���ѣ�"+Rtime);
			}
			
		}

		// ��ʾ�ü��±�������ȫ��ͼƬ
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
		// ��������ͳ��
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// ֹͣ����ͳ��
		MobclickAgent.onPause(this);
	}

	/**
	 * �������btn�������¼�
	 */
	public OnClickListener saveButtOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			String newContent = contentEdit.getText().toString();
			//�жϵ�ǰ�Ƿ�������ʱ�������ʱ���Ƿ��и���
			if (!needRemind) {
				remindString=null;
			}else {
				if (Rdate!=null) {
					AlarmSetting.setAlarm(EditNoteActivity.this, nid, Rdate);
				}
			}
			// �������ݿ����ı�����,����״̬,����ʱ��
			noteDetailDao.updateNoteInfo(nid, newContent,locked,remindString);
			// ����ͼƬ��������Ҫ�����ݿ���ɾ����ͼƬ
			List<PictureModel> deletePicList = pictureDao.updateNotePic(picList, nid);
			// ����Ҫɾ����ͼƬ��sdcard��ɾ��
			if (deletePicList.size() > 0) {
				FileUtil.deleteMediaInSdcard(deletePicList);
			}

			// ����¼����������Ҫ�����ݿ���ɾ����¼��
			List<String> deleteVoiList = voiceDao.updateNoteVoi(voiList, nid);
			if (deleteVoiList.size() > 0) {
				FileUtil.deleteMediaInSdcard(deleteVoiList, Utils.VOI_PATH);
			}

			// ������Ƶ��������Ҫ�����ݿ���ɾ������Ƶ
			List<String> deleteVideoList = videoDao.updateNoteVideo(videoList,
					nid);
			if (deleteVideoList.size() > 0) {
				FileUtil.deleteMediaInSdcard(deleteVideoList, Utils.VID_PATH);
			}

			Toast.makeText(EditNoteActivity.this, "���±��޸ĳɹ���", Toast.LENGTH_LONG)
			.show();
			setResult(RESULT_OK);
			EditNoteActivity.this.finish();
		}
	};

	/**
	 * �������btn�������¼�
	 */
	public OnClickListener backButtonClickListener = new OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			showSureExit();
		}
	};
	
	/**
	 * ��ʾ�Ƿ��˳��ĶԻ���
	 */
	public void showSureExit(){
		Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
		builder.setTitle("�޸Ĳ��ֽ���������,ȷ���˳���");
		builder.setPositiveButton("��",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						List<PictureModel> delePicList=getNoNeedPictureList(pictureDao.getPicFromNid(nid+""), picList);
						List<String> deleVoiList=getNoNeedList(voiceDao.getVoiFromNid(nid+""), voiList);
						List<String> deleVidList=getNoNeedList(videoDao.getVideoFromNid(nid+""), videoList);
						//ɾ��SD���в���Ҫ�Ķ�ý��
						FileUtil.deleteMediaInSdcard(delePicList);
						FileUtil.deleteMediaInSdcard(deleVoiList, Utils.VOI_PATH);
						FileUtil.deleteMediaInSdcard(deleVidList, Utils.VID_PATH);
						
						EditNoteActivity.this.finish();
					}
				});
		builder.setNegativeButton("��", null);
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
	 * ��ȡ��ǰlist������ӵ�Ԫ��
	 * 
	 * @param origList
	 * @param curList
	 * @return ��Ҫɾ����list
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
						// ���Ƚϵ����һ��Ԫ�ض������
						noNeedList.add(curList.get(i));
					}
				}
			}

		}
		return noNeedList;

	}
	
	/**
	 * ��ȡ��ǰͼƬlist������ӵ�Ԫ��
	 * @param origList
	 * @param curList
	 * @return ��Ҫɾ����ͼƬlist
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
						// ���Ƚϵ����һ��Ԫ�ض������
						noNeedList.add(curList.get(i));
					}
				}
			}

		}
		return noNeedList;

	}

	/**
	 * ��ʾ�ü��±�������ȫ��ͼƬ
	 * 
	 * @param nid
	 *            �������±���
	 */
	public void showAllPicInOneNote(int nid) {
		// ��ֵ��ȫ�ֱ���������һ���Ĳ���
		picList = pictureDao.getPicFromNid(nid + "");
//		if (picList != null) {
//			for (int i = 0; i < picList.size(); i++) {
//				addPicOnNote(picList.get(i).getpName(),true);
//			}
//		}
		initGallery();
	}

	/**
	 * ��ʾ�ü��±�������ȫ��¼��
	 * 
	 * @param nid
	 */
	public void showAllVoiInOneNote(int nid) {
		List<String> allVoi = voiceDao.getVoiFromNid(nid + "");
		// ��ֵ��ȫ�ֱ���voiList
		voiList = allVoi;
		if (allVoi != null) {
			int size=allVoi.size();
			for (int i = 0; i < size; i++) {
				addVoiOnNote(allVoi.get(i), EditNoteActivity.this);
			}
		}
	}

	/**
	 * ��ʾ�ü��±�������������Ƶ
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
