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

	// 图片的路径
	public static final String PIC_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SuperNote/Pictures/";

	// 录音的路径
	public static final String VOI_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SuperNote/Voices/";

	// 视频的路劲
	public static final String VID_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/SuperNote/Videos/";

	/**
	 * 读取sdcard中的数据库
	 * 
	 * @param context
	 *            上下文
	 * @return 数据库
	 */
	public static SQLiteDatabase getDataBase(Context context) {
		// 数据库所在路径
		String path = "data/data/com.xforce.supernotepad.view/note.db";

		File file = new File(path);
		// 判断路径下文件是否存在
		if (file.exists()) {
			// 存在则直接带数据库
			System.out.println("有");
			return SQLiteDatabase.openOrCreateDatabase(file, null);
		} else {
			System.out.println("没");
			try {
				// 不存在则把asset中的数据库导入到指定路径中
				// 得到资源
				AssetManager manager = context.getAssets();
				// 得到数据库的输入流
				InputStream inputStream = manager.open("note.db");
				// 用输出流写到SDcard上
				FileOutputStream outputStream = new FileOutputStream(file);
				// 用1kb写一次
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
		// 写入完成后再执行一次该函数
		return getDataBase(context);
	}

	/**
	 * 返回当前日期和时间，格式为yyyyMMdd_HHmmss
	 * 
	 * @return
	 */
	public static String getNowDate() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String nowDate = f.format(new Date());
		return nowDate;

	}

	

	/**
	 * 检查设定的时间是否过期
	 * 
	 * @param setDate
	 * @return true 表示过期
	 */
	public static boolean checkOverdue(String setDate) {
		// 获取系统当前时间判断是否过期
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
	 * 保存密码
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
	 * 读取密码
	 * 
	 * @param sharedPreferences
	 * @return
	 */
	public static String getPasswd(SharedPreferences sharedPreferences) {
		System.out.println("密码--------"
				+ sharedPreferences.getString("passwd", null));
		return sharedPreferences.getString("passwd", null);
	}
	
	/**
	 * 方法名：compressImg(String pathName, float sizeK, Bitmap bmp)
	 * 功     能：压缩指定的图片文件，并指定对应的大小
	 * 参      数：String pathName 图片文件的绝对路径
	 *        float sizeK 指定压缩后文件的大小，单位为K 例如 300.00fK 
	 * 返 回 值： 无
	 */
	public static Bitmap compressImg(String pathName, float sizeK) {
		float sizeB = sizeK * 1000.00f; //转换单位
		File file = new File(pathName);//创建临时文件
		Bitmap bmp = BitmapFactory.decodeFile(pathName);//图片文件转换为bitmap
		if(file.length() >= sizeB){//判断文件大小是否超过指定的文件大小
			float scaleWidth = (float)(sizeB*0.88f/(file.length()/1.00f))+0.16f;//根据指定的算式进行压缩图片到指定的大小范围
			float scaleHeight = scaleWidth;//设置缩小比例值
			Matrix m = new Matrix();//创建大小矩阵
			m.postScale(scaleWidth, scaleHeight);
			bmp = Bitmap.createBitmap(bmp, 0,0, bmp.getWidth(),bmp.getHeight(), m, true);//压缩
		}
		return bmp;
	}
	
	/**
	 * 方法名：getPicUriFromDir(List<String> allPicUri, File filePath)
	 * 功     能： 获取指定路径的所有图片的Uri
	 * 参      数： File file 指定的文件夹路径
	 * 返 回 值： 无
	 */
	public static List<String> getPicUriFromDir(String filePath){
		File file = new File(filePath);
		List<String> allPicUri = new ArrayList<String>();
		allPicUri.removeAll(allPicUri);//先清除所有的图片uri
		if (file.isDirectory()) {//循环遍历图片存放的文件夹，取出图片的uri
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
	 * 开启线程加载记事本item的图片
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
				options.inSampleSize = 3;// 将图片压缩显示
				Bitmap bitmap = BitmapFactory.decodeFile(picPath, options);
//            	Bitmap bitmap=compressImg(picPath, 50);
				Message message = handler.obtainMessage(0, bitmap);  
                handler.sendMessage(message);  
				
            }  
        }.start(); 
        
        
	}
	
	

}
