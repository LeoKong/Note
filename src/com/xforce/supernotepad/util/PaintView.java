package com.xforce.supernotepad.util;

import java.util.ArrayList;

import com.xforce.supernotepad.view.DrawPicActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View {
	
	DrawPicActivity d = null;
	

	private int screenWidth, screenHeight;// ��Ļ�L��
	private int menuHeight; //ȡͷ���˵��ĳ� ���ڼ�ⴥ��ʱ��λ���Ƿ��ڻ��崦

	public int getMenuHeight() {
		return menuHeight;
	}

	public void setMenuHeight(int menuHeight) {
		this.menuHeight = menuHeight;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	private Bitmap mBitmap;// ������
	private Bitmap tmpBitmap;// ��ʱ����������ԭʼ������
	private Bitmap bgBitmap; // ����ͼ���Ļ���
	private boolean isHasBgbm = false; //�Ƿ����ñ���ͼ��
	private int TOUCH_FLAG = -1; // ��־


	public int getTOUCH_FLAG() {
		return TOUCH_FLAG;
	}

	public void setTOUCH_FLAG(int tOUCH_FLAG) {
		TOUCH_FLAG = tOUCH_FLAG;
	}

	public Bitmap getmBitmap() {
		return mBitmap;
	}

	public void setmBitmap(Bitmap mBitmap) {
		this.mBitmap = mBitmap;
	}
	
	

	public Bitmap getTmpBitmap() {
		return tmpBitmap;
	}

	public void setTmpBitmap(Bitmap tmpBitmap) {
		this.tmpBitmap = tmpBitmap;
	}
	

	public Bitmap getBgBitmap() {
		return bgBitmap;
	}

	public void setBgBitmap(Bitmap bgBitmap) {
		this.bgBitmap = bgBitmap;
	}

	public boolean isHasBgbm() {
		return isHasBgbm;
	}

	public void setHasBgbm(boolean isHasBgbm) {
		this.isHasBgbm = isHasBgbm;
	}

	private Canvas mCanvas;// ������

	public Canvas getmCanvas() {
		return mCanvas;
	}

	public void setmCanvas(Canvas mCanvas) {
		this.mCanvas = mCanvas;
	}

	private Paint mPaint = new Paint(); // ����

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public static int FINGER_WIDTH = 10;//����

	// ���캯��
	public PaintView(Context context) {
		super(context);
	}
	
	// ���캯��
	public PaintView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	// ��ǰ�Ļ��ʶ���ʵ��
	private PaintAction curAction = null;
	// �洢���еĶ���ʵ��
	private ArrayList<PaintAction> actionList = null;

	// ������ж���ʵ��
	public void clearPaintAction() {
		actionList.clear();
		this.currentPaintIndex = -1;
	}

	// ��ǰ���Ѿ�ѡ��Ļ��ʲ���
	// ���ʹ������� Բ ���
	private int currentPaintTool = 0; // 0~6

	public int getCurrentPaintTool() {
		return currentPaintTool;
	}

	public void setCurrentPaintTool(int currentPaintTool) {
		this.currentPaintTool = currentPaintTool;
	}

	// ��������

	private int currentPenType = 1;

	public int getCurrentPenType() {
		return currentPenType;
	}

	public void setCurrentPenType(int currentPenType) {
		this.currentPenType = currentPenType;
	}

	// ������ɫ
	private int currentColor = Color.BLACK;

	public int getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(int currentColor) {
		this.currentColor = currentColor;
	}

	// ���ʵĴ�ϸ
	private int currentSize = 10; // 1,3,5

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
	
	//��ʼ����յ�
	PointF startP = new PointF(-1,-1);
	PointF endP = new PointF(-1,-1);

	public PointF getStartP() {
		return startP;
	}

	public void setStartP(PointF startP) {
		this.startP = startP;
	}

	public PointF getEndP() {
		return endP;
	}

	public void setEndP(PointF endP) {
		this.endP = endP;
	}
	
	public boolean startP_endP_equals(PointF startP,PointF endP){
		if(startP.x == endP.x && startP.y == endP.y){
			return true;
		}else{
			return false;
		}
	}

	// ����ʵ���洢list�е�ͷָ��
	private int currentPaintIndex = -1;
	
	public int getCurrentPaintIndex() {
		return currentPaintIndex;
	}

	// ���� ��־
	private boolean isBackPressed = false;
	// ǰ�� ��־
	private boolean isForwardPressed = false;
	// ������ɫ
	private int bg_color = Color.WHITE;

	public boolean isBackPressed() {
		return isBackPressed;
	}

	public void setBackPressed(boolean isBackPressed) {
		this.isBackPressed = isBackPressed;
	}

	public boolean isForwardPressed() {
		return isForwardPressed;
	}

	public void setForwardPressed(boolean isForwardPressed) {
		this.isForwardPressed = isForwardPressed;
	}

	// ��ʼ��paintView
	/**
	 * @see ��ʼ��paintView
	 * @param paramInt1 �����Ŀ�  ����Ļ�Ŀ�
	 * @param paramInt2 �����ĳ� ��Ļ�ܳ�-����menu�� -�Ͷ�button��
	 * @param ԭactivity ��������hide���ֵ����򡣣��ص���
	 * 
	 * */
	public void init(DrawPicActivity d,int paramInt1, int paramInt2) {
		try {
			
			this.d = d;
			//��ʼ����Ҫ����
			this.mBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
					Bitmap.Config.ARGB_8888);
			this.mBitmap.eraseColor(Color.WHITE);
			
			//��ʼ����ʱ��������������һ����������
			this.tmpBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
					Bitmap.Config.ARGB_8888);
			this.tmpBitmap.eraseColor(Color.WHITE);
			//��ʼ������
			this.mCanvas = new Canvas(mBitmap);	
			mCanvas.drawColor(bg_color);
			//��ʼ������
			this.mPaint.setAntiAlias(true);
			this.mPaint.setDither(true);
			this.mPaint.setColor(Color.BLACK);
			this.mPaint.setStyle(Paint.Style.STROKE);
			this.mPaint.setStrokeJoin(Paint.Join.ROUND);
			this.mPaint.setStrokeCap(Paint.Cap.ROUND);
			this.mPaint.setStrokeWidth(FINGER_WIDTH);	
			//��ʼ�� ʵ��List
			this.actionList = new ArrayList<PaintAction>();
			this.d.checkButtonState();
			return;
		} catch (OutOfMemoryError localOutOfMemoryError) {
		}
	}
	// ������ͼƬ��ʼ��paintView���޸ļ��±���ͼƬʱ���ã�
	/**
	 * @see ������ͼƬ��ʼ��paintView���޸ļ��±���ͼƬʱ���ã�
	 * @param paramInt1 �����Ŀ�  ����Ļ�Ŀ�
	 * @param paramInt2 �����ĳ� ��Ļ�ܳ�-����menu�� -�Ͷ�button��
	 * @param ԭactivity ��������hide���ֵ����򡣣��ص���
	 * @param picPath ����ͼƬ��·��
	 * */
	public void init(DrawPicActivity d,int paramInt1, int paramInt2, String picPath) {
		this.init(d, paramInt1, paramInt2);
		
		this.setBgPicture(picPath);
		mCanvas.drawBitmap(bgBitmap, 0, 0, mPaint);
	}

	/**
	 * @see touchevent ����touch���� ���ö�Ӧ����
	 * */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		float touchX = event.getX();
		float touchY = event.getY();
		int action = event.getAction();
		this.setTOUCH_FLAG(action);
		if (action == MotionEvent.ACTION_DOWN) {
			d.hideColorPicker();
			d.hidePopuCenterMenu();
			d.hideGraphPicker();
			this.setStartP(new PointF(touchX,touchY));
			this.setEndP(new PointF(touchX,touchY));
			this.touch_down(touchX, touchY);	
		}
		if (action == MotionEvent.ACTION_MOVE) {
			touch_move(touchX, touchY);
			this.invalidate();
		}
		if (action == MotionEvent.ACTION_UP) {
			this.setEndP(new PointF(touchX,touchY));
			touch_up(touchX, touchY);
			this.invalidate();
		}
		return true;
	}



	// �õ���ǰ���ʵ����ͣ�������ʵ��
	/**
	 * @param x,y touch������
	 * @see �������� �½�����ʵ��
	 * */
	public void setCurAction(float x, float y) {
		 switch (currentPaintTool) {
			 case 0:
			 curAction = new MyPath(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
			 break;
			 case 1:
			 curAction = new MyPoint(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
		     break;
			 case 2:
			 curAction = new MyLine(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
			 break;
			 case 3:
			 curAction = new MyRect(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
			 break;
			 case 4:
			 curAction = new MyCircle(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
			 break;
			 case 5:
			 curAction = new MyFillRect(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
			 break;
			 case 6:
			 curAction = new MyFillCircle(x, y, currentSize, currentColor);
			 curAction.setPenType(currentPenType);
			 break;
			 case 7:
			 curAction = new MyEraser(x, y, currentSize, bg_color);
			 break;
			 default:
			 break;
		 }
	}

	// ��ͼ

	/**
	 * @see ����ʱ��ÿһ�ʶ�����Validate�Ӷ����µ���onDraw����̬���»�������
	 * @param Ĭ�ϵĻ��壬ֻ������ʾ��movingʱ��Ļ���Ч��
	 * */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		this.d.checkButtonState();
		//�ж��Ƿ��˻��˼�
		if (isBackPressed) {
			this.drawBack();
			this.setBackPressed(false);
		}

		//�ж��Ƿ���ǰ����
		if (isForwardPressed) {
			this.drawForward();
			this.setForwardPressed(false);
		}
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		//����touch �Ķ��� ִ����Ӧ�Ĳ���
		switch (this.getTOUCH_FLAG()) {
		case MotionEvent.ACTION_DOWN: {
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (curAction != null && currentPaintTool == 7) {
				curAction.draw(mCanvas);
			}else if(curAction != null && currentPaintTool == 0){
				curAction.draw(mCanvas);
			}else if(curAction != null && currentPaintTool != 7 && currentPaintTool != 0){
				curAction.draw(canvas);
			}
				break;
		}
		case MotionEvent.ACTION_UP: {
			if (curAction != null) {
				if(startP_endP_equals(startP, endP) && currentPaintTool != 7){
					curAction = new MyPoint(endP.x, endP.y, currentSize, currentColor);
				    curAction.setPenType(currentPenType);
				}
				//curAction.draw(canvas);
				if(startP_endP_equals(startP, endP) && currentPaintTool == 7){
					curAction.drawEraPoint(mCanvas);
				}else{
					curAction.draw(mCanvas);
				}
				clearSpareAction();
				actionList.add(curAction);
				currentPaintIndex++;
				curAction = null;
			}
			this.setTOUCH_FLAG(-1);
			this.d.checkButtonState();
			this.invalidate();
			break;
		}
		default:
			break;
		}
		super.onDraw(canvas);
	}

	/**
	 * @param x,y touchʱ�������
	 * */
	public void touch_down(float touchX, float touchY) {
		setCurAction(touchX, touchY);
	}

	/**
	 * @param x,y touchʱ�������
	 * */
	public void touch_move(float touchX, float touchY) {
		if (curAction != null) {
			curAction.move(touchX, touchY);
		}
	}
	
	/**
	 * @param x,y touchʱ�������
	 * */
	public void touch_up(float touchX, float touchY) {
		if (curAction != null) {
			curAction.move(touchX, touchY);
		}
	}

	/**
	 * @see ��ջ����ϵĶ���
	 * */
	public void clearAll() {
		this.setmBitmap(tmpBitmap);
		this.mCanvas.setBitmap(mBitmap);

		if(isHasBgbm){
			mCanvas.drawBitmap(bgBitmap, 0, 0, mPaint);
		}else{
			mCanvas.drawColor(bg_color);
		}

		if (!isBackPressed && !isForwardPressed) {
			currentPaintIndex = -1;
		}
	}

	// ����ǰ����ɺ󣬻���Ķ���
	/**
	 * @see  ����ǰ����ɺ��������Ķ���ʵ��
	 * */
	private void clearSpareAction() {
		for (int i = actionList.size() - 1; i > currentPaintIndex; i--) {
			actionList.remove(i);
		}
	}

	/**
	 * @see  ���˰�ťʵ��
	 * */
	public void drawBack() {
		if (isBackPressed && currentPaintIndex >= 0) {
			clearAll();
			currentPaintIndex--;
			if (currentPaintIndex >= 0) {
				for (int i = 0; i <= currentPaintIndex; i++) {
					actionList.get(i).draw(mCanvas);
				}
			} else if (currentPaintIndex != -1) {
				currentPaintIndex++;
			}
		}
		this.d.checkButtonState();
	}
	
	/**
	 * @see  ǰ����ťʵ��
	 * */

	public void drawForward() {
		if (isForwardPressed && currentPaintIndex >= 0) {
			currentPaintIndex++;
			if (currentPaintIndex < actionList.size()) {
				actionList.get(currentPaintIndex).draw(mCanvas);
			} else {
				currentPaintIndex--;
			}
		} else if (isForwardPressed && currentPaintIndex == -1
				&& actionList.size() >= 1) {
			currentPaintIndex++;
			actionList.get(currentPaintIndex).draw(mCanvas);
		}
		this.d.checkButtonState();

	}

	/**
	 * @see ȡ���ֵ���ͼ����ʵ���ĸ��� ���ô�����clear��deletebutton��״̬
	 * */
	public int getActionListCount() {
		return this.actionList.size();
	}
	
	/**
	 * @param picPath PIC_PATH ͼƬ�ļ�����·��
	 * @see ���ñ���ͼƬ
	 * */
	public void setBgPicture(String picPath){
		this.bgBitmap = BitmapFactory.decodeFile(picPath);
	}
	
	public void setBgPicFrmGally(String picPath){
		Bitmap tmp = BitmapFactory.decodeFile(picPath);
		int drawPointX = (d.BITMAP_WIDTH - tmp.getWidth())/2;
		int drawPointY = (d.BITMAP_HEIGHT - tmp.getHeight())/2;
		this.bgBitmap = Bitmap.createBitmap(d.BITMAP_WIDTH, d.BITMAP_HEIGHT,
				Bitmap.Config.ARGB_8888);
		Canvas tmpCanvas = new Canvas(bgBitmap);
		tmpCanvas.drawBitmap(tmpBitmap, 0, 0, mPaint);
		tmpCanvas.drawColor(bg_color);
		tmpCanvas.drawBitmap(tmp, drawPointX,drawPointY, mPaint);
		if(isHasBgbm){
			clearBgWithColor();
		}
		mCanvas.drawBitmap(bgBitmap, 0,0, mPaint);
		this.setHasBgbm(true);
		this.currentPaintIndex = -1;
		clearSpareAction();
		this.invalidate();
	}
	
	public void clearBgWithColor(){
		if(isHasBgbm){
			this.setHasBgbm(false);
			clearAll();	
		}else{
			clearAll();
		}
		this.currentPaintIndex = -1;
		clearSpareAction();
		this.invalidate();
	}

}

