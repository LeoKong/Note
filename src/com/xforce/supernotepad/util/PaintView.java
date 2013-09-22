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
	

	private int screenWidth, screenHeight;// 屏幕長寬
	private int menuHeight; //取头顶菜单的长 用于检测触屏时候位置是否在画板处

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

	private Bitmap mBitmap;// 主画布
	private Bitmap tmpBitmap;// 临时画布（保存原始画布）
	private Bitmap bgBitmap; // 背景图案的画布
	private boolean isHasBgbm = false; //是否设置背景图案
	private int TOUCH_FLAG = -1; // 标志


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

	private Canvas mCanvas;// 主画板

	public Canvas getmCanvas() {
		return mCanvas;
	}

	public void setmCanvas(Canvas mCanvas) {
		this.mCanvas = mCanvas;
	}

	private Paint mPaint = new Paint(); // 画笔

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public static int FINGER_WIDTH = 10;//参数

	// 构造函数
	public PaintView(Context context) {
		super(context);
	}
	
	// 构造函数
	public PaintView(Context context, AttributeSet attr) {
		super(context, attr);
	}

	// 当前的画笔动作实例
	private PaintAction curAction = null;
	// 存储所有的动作实例
	private ArrayList<PaintAction> actionList = null;

	// 清除所有动作实例
	public void clearPaintAction() {
		actionList.clear();
		this.currentPaintIndex = -1;
	}

	// 当前的已经选择的画笔参数
	// 画笔工具类型 圆 框等
	private int currentPaintTool = 0; // 0~6

	public int getCurrentPaintTool() {
		return currentPaintTool;
	}

	public void setCurrentPaintTool(int currentPaintTool) {
		this.currentPaintTool = currentPaintTool;
	}

	// 画迹类型

	private int currentPenType = 1;

	public int getCurrentPenType() {
		return currentPenType;
	}

	public void setCurrentPenType(int currentPenType) {
		this.currentPenType = currentPenType;
	}

	// 画笔颜色
	private int currentColor = Color.BLACK;

	public int getCurrentColor() {
		return currentColor;
	}

	public void setCurrentColor(int currentColor) {
		this.currentColor = currentColor;
	}

	// 画笔的粗细
	private int currentSize = 10; // 1,3,5

	public int getCurrentSize() {
		return currentSize;
	}

	public void setCurrentSize(int currentSize) {
		this.currentSize = currentSize;
	}
	
	//起始点和终点
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

	// 动作实例存储list中的头指针
	private int currentPaintIndex = -1;
	
	public int getCurrentPaintIndex() {
		return currentPaintIndex;
	}

	// 回退 标志
	private boolean isBackPressed = false;
	// 前进 标志
	private boolean isForwardPressed = false;
	// 背景颜色
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

	// 初始化paintView
	/**
	 * @see 初始化paintView
	 * @param paramInt1 画布的宽  即屏幕的宽
	 * @param paramInt2 画布的长 屏幕总长-顶端menu长 -低端button长
	 * @param 原activity 用来控制hide各种弹出框。（回调）
	 * 
	 * */
	public void init(DrawPicActivity d,int paramInt1, int paramInt2) {
		try {
			
			this.d = d;
			//初始化主要画布
			this.mBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
					Bitmap.Config.ARGB_8888);
			this.mBitmap.eraseColor(Color.WHITE);
			
			//初始化临时画布，与主画布一样。备份用
			this.tmpBitmap = Bitmap.createBitmap(paramInt1, paramInt2,
					Bitmap.Config.ARGB_8888);
			this.tmpBitmap.eraseColor(Color.WHITE);
			//初始化画板
			this.mCanvas = new Canvas(mBitmap);	
			mCanvas.drawColor(bg_color);
			//初始化画笔
			this.mPaint.setAntiAlias(true);
			this.mPaint.setDither(true);
			this.mPaint.setColor(Color.BLACK);
			this.mPaint.setStyle(Paint.Style.STROKE);
			this.mPaint.setStrokeJoin(Paint.Join.ROUND);
			this.mPaint.setStrokeCap(Paint.Cap.ROUND);
			this.mPaint.setStrokeWidth(FINGER_WIDTH);	
			//初始化 实例List
			this.actionList = new ArrayList<PaintAction>();
			this.d.checkButtonState();
			return;
		} catch (OutOfMemoryError localOutOfMemoryError) {
		}
	}
	// 带背景图片初始化paintView（修改记事本的图片时所用）
	/**
	 * @see 带背景图片初始化paintView（修改记事本的图片时所用）
	 * @param paramInt1 画布的宽  即屏幕的宽
	 * @param paramInt2 画布的长 屏幕总长-顶端menu长 -低端button长
	 * @param 原activity 用来控制hide各种弹出框。（回调）
	 * @param picPath 背景图片的路径
	 * */
	public void init(DrawPicActivity d,int paramInt1, int paramInt2, String picPath) {
		this.init(d, paramInt1, paramInt2);
		
		this.setBgPicture(picPath);
		mCanvas.drawBitmap(bgBitmap, 0, 0, mPaint);
	}

	/**
	 * @see touchevent 根据touch动作 调用对应函数
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



	// 得到当前画笔的类型，并进行实例
	/**
	 * @param x,y touch的坐标
	 * @see 根据类型 新建画笔实例
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

	// 绘图

	/**
	 * @see 画画时候每一笔都调用Validate从而重新调用onDraw来动态更新画布内容
	 * @param 默认的画板，只用于显示在moving时候的画画效果
	 * */
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		this.d.checkButtonState();
		//判断是否按了回退键
		if (isBackPressed) {
			this.drawBack();
			this.setBackPressed(false);
		}

		//判断是否按了前进键
		if (isForwardPressed) {
			this.drawForward();
			this.setForwardPressed(false);
		}
		canvas.drawBitmap(mBitmap, 0, 0, mPaint);
		//根据touch 的动作 执行相应的操作
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
	 * @param x,y touch时候的坐标
	 * */
	public void touch_down(float touchX, float touchY) {
		setCurAction(touchX, touchY);
	}

	/**
	 * @param x,y touch时候的坐标
	 * */
	public void touch_move(float touchX, float touchY) {
		if (curAction != null) {
			curAction.move(touchX, touchY);
		}
	}
	
	/**
	 * @param x,y touch时候的坐标
	 * */
	public void touch_up(float touchX, float touchY) {
		if (curAction != null) {
			curAction.move(touchX, touchY);
		}
	}

	/**
	 * @see 清空画布上的东西
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

	// 后退前进完成后，缓存的动作
	/**
	 * @see  后退前进完成后，清楚多余的动作实例
	 * */
	private void clearSpareAction() {
		for (int i = actionList.size() - 1; i > currentPaintIndex; i--) {
			actionList.remove(i);
		}
	}

	/**
	 * @see  回退按钮实现
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
	 * @see  前进按钮实现
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
	 * @see 取得现当画图动作实例的个数 ，用此设置clear、deletebutton的状态
	 * */
	public int getActionListCount() {
		return this.actionList.size();
	}
	
	/**
	 * @param picPath PIC_PATH 图片文件所在路径
	 * @see 设置背景图片
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

