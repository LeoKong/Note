package com.xforce.supernotepad.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class Utils {

	// ͼƬ��·��
	public static final String PIC_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SuperNote/Pictures/";

	// ¼����·��
	public static final String VOI_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SuperNote/Voices/";

	// ��Ƶ��·��
	public static final String VID_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SuperNote/Videos/";

	/**
	 * ��ȡsdcard�е����ݿ�
	 * 
	 * @param context
	 *            ������
	 * @return ���ݿ�
	 */
	public static SQLiteDatabase getDataBase(Context context) {
		// ���ݿ�����·��
		String path = "data/data/com.xforce.supernotepad.view/note.db";

		File file = new File(path);
		// �ж�·�����ļ��Ƿ����
		if (file.exists()) {
			// ������ֱ�Ӵ����ݿ�
			System.out.println("��");
			return SQLiteDatabase.openOrCreateDatabase(file, null);
		} else {
			System.out.println("û");
			try {
				// ���������asset�е����ݿ⵼�뵽ָ��·����
				// �õ���Դ
				AssetManager manager = context.getAssets();
				// �õ����ݿ��������
				InputStream inputStream = manager.open("note.db");
				// �������д��SDcard��
				FileOutputStream outputStream = new FileOutputStream(file);
				// ��1kbдһ��
				byte[] buffer = new byte[1024];
				int count = 0;
				while ((count = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);

				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				return null;
			}
		}
		// д����ɺ���ִ��һ�θú���
		return getDataBase(context);
	}

	/**
	 * ���ص�ǰ���ں�ʱ�䣬��ʽΪyyyyMMdd_HHmmss
	 * 
	 * @return
	 */
	public static String getNowDate() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String nowDate = f.format(new Date());
		return nowDate;

	}

	

	/**
	 * ����趨��ʱ���Ƿ����
	 * 
	 * @param setDate
	 * @return true ��ʾ����
	 */
	public static boolean checkOverdue(String setDate) {
		// ��ȡϵͳ��ǰʱ���ж��Ƿ����
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowDateString = format.format(new Date());
		Date nowDate = null;
		Date remindDate = null;
		try {
			nowDate = format.parse(nowDateString);
			remindDate = format.parse(setDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (remindDate.before(nowDate)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * ��������
	 * 
	 * @param sharedPreferences
	 * @param passwd
	 */
	public static void savePasswd(SharedPreferences sharedPreferences,
			String passwd) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("passwd", passwd);
		editor.commit();
	}

	/**
	 * ��ȡ����
	 * 
	 * @param sharedPreferences
	 * @return
	 */
	public static String getPasswd(SharedPreferences sharedPreferences) {
		System.out.println("����--------"
				+ sharedPreferences.getString("passwd", null));
		return sharedPreferences.getString("passwd", null);
	}
	
	/**
	 * ��������compressImg(String pathName, float sizeK, Bitmap bmp)
	 * ��     �ܣ�ѹ��ָ����ͼƬ�ļ�����ָ����Ӧ�Ĵ�С
	 * ��      ����String pathName ͼƬ�ļ��ľ���·��
	 *        float sizeK ָ��ѹ�����ļ��Ĵ�С����λΪK ���� 300.00fK 
	 * �� �� ֵ�� ��
	 */
	public static Bitmap compressImg(String pathName, float sizeK) {
		float sizeB = sizeK * 1000.00f; //ת����λ
		File file = new File(pathName);//������ʱ�ļ�
		Bitmap bmp = BitmapFactory.decodeFile(pathName);//ͼƬ�ļ�ת��Ϊbitmap
		if(file.length() >= sizeB){//�ж��ļ���С�Ƿ񳬹�ָ�����ļ���С
			float scaleWidth = (float)(sizeB*0.88f/(file.length()/1.00f))+0.16f;//����ָ������ʽ����ѹ��ͼƬ��ָ���Ĵ�С��Χ
			float scaleHeight = scaleWidth;//������С����ֵ
			Matrix m = new Matrix();//������С����
			m.postScale(scaleWidth, scaleHeight);
			bmp = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(),bmp.getHeight(), m, true);//ѹ��
		}
		return bmp;
	}
	
	/**
	 * ��������getPicUriFromDir(List<String> allPicUri, File filePath)
	 * ��     �ܣ� ��ȡָ��·��������ͼƬ��Uri
	 * ��      ���� File file ָ�����ļ���·��
	 * �� �� ֵ�� ��
	 */
	public static List<String> getPicUriFromDir(String filePath){
		File file = new File(filePath);
		List<String> allPicUri = new ArrayList<String>();
		allPicUri.removeAll(allPicUri);//��������е�ͼƬuri
		if (file.isDirectory()) {//ѭ������ͼƬ��ŵ��ļ��У�ȡ��ͼƬ��uri
			File[] temp = file.listFiles();
			for (int i = 0; i < temp.length; i++) {
				allPicUri.add(temp[i].getAbsolutePath());
			}
		}
		return allPicUri;
	}
	
	public static int getSizePicUriList(){
		return getPicUriFromDir(PIC_PATH).size();
	}
	
	/**
	 * �����̼߳��ؼ��±�item��ͼƬ
	 * @param imageView
	 * @param picPath
	 */
	public static void loadPic(final ImageView imageView,final String picPath){
		
		final Handler handler = new Handler() {  
            public void handleMessage(Message message) {  
               imageView.setImageBitmap((Bitmap) message.obj);
            }  
        };
        
		new Thread() {  
            public void run() {  
            	BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 3;// ��ͼƬѹ����ʾ
				Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
//            	Bitmap bitmap=compressImg(picPath, 50);
				Message message = handler.obtainMessage(0, bitmap);  
                handler.sendMessage(message);  
				
            }  
        }.start(); 
        
        
	}
	
	

}
