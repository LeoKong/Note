package com.xforce.supernotepad.util;


import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class PaintAction {
	public int color;

	public int penType = 1;

	public int getPenType() {
		return penType;
	}

	public void setPenType(int penType) {
		this.penType = penType;
	}

	PaintAction() {
		color = Color.BLACK;
	}

	PaintAction(int color) {
		this.color = color;
	}

	public void draw(Canvas canvas) {
	}
	
	public void drawEraPoint(Canvas canvas){
		
	}

	public void move(float mx, float my) {

	}

	public void setPenType(int penType, Paint paint) {
		switch (penType) {
		case 1:
			break;
		case 2:
			paint.setMaskFilter(new BlurMaskFilter(10,
					BlurMaskFilter.Blur.INNER));
			break;
		case 3:
			paint.setMaskFilter(new EmbossMaskFilter(new float[] { 1.0F, 1.0F,
					1.0F }, 0.4F, 6.0F, 3.5F));
			break;
		default:
			break;
		}
	}

}

// 点
class MyPoint extends PaintAction {
	public float x;
	public float y;
	int size;

	MyPoint(float px, float py,int size, int color) {
		super(color);
		this.size = size;
		this.x = px;
		this.y = py;
	}

	public void draw(Canvas canvas) {		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		this.setPenType(penType, paint);
		canvas.drawPoint(x, y, paint);
	}
}

// 自由曲线
class MyPath extends PaintAction {
	Path path;
	int size;

	MyPath() {
		path = new Path();
		size = 1;
	}

	MyPath(float x, float y, int size, int color) {
		super(color);
		path = new Path();
		this.size = size;
		path.moveTo(x, y);
		path.lineTo(x, y);
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		this.setPenType(penType, paint);
		canvas.drawPath(path, paint);
	}

	public void move(float mx, float my) {
		path.lineTo(mx, my);
	}
}

// 直线
class MyLine extends PaintAction {
	float startX;
	float startY;
	float stopX;
	float stopY;
	int size;

	MyLine() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
	}

	MyLine(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		this.setPenType(penType, paint);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
	}
}

// 方框
class MyRect extends PaintAction {
	float startX;
	float startY;
	float stopX;
	float stopY;
	int size;

	MyRect() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
	}

	MyRect(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		this.setPenType(penType, paint);
		if(  stopX > startX &&  stopY > startY ){
			canvas.drawRect(startX, startY, stopX, stopY, paint);
		}else if( stopX > startX &&  stopY < startY){
			canvas.drawRect(startX, stopY, stopX, startY, paint);
		}else if( stopX < startX &&  stopY < startY){
			canvas.drawRect(stopX, stopY, startX, startY, paint);
		}else if( stopX < startX &&  stopY > startY){
			canvas.drawRect(stopX, startY, startX, stopY, paint);
		}
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
	}
}

// 圆框
class MyCircle extends PaintAction {
	float startX;
	float startY;
	float stopX;
	float stopY;
	float radius;
	int size;

	MyCircle() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
		radius = 0;
	}

	MyCircle(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		radius = 0;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		this.setPenType(penType, paint);
		canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius,
				paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
		radius = (float) ((Math.sqrt((mx - startX) * (mx - startX)
				+ (my - startY) * (my - startY))) / 2);
	}
}

// 方块
class MyFillRect extends PaintAction {
	float startX;
	float startY;
	float stopX;
	float stopY;
	int size;

	MyFillRect() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
	}

	MyFillRect(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		this.setPenType(penType, paint);
		canvas.drawRect(startX, startY, stopX, stopY, paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
	}
}

// 圆饼
class MyFillCircle extends PaintAction {
	float startX;
	float startY;
	float stopX;
	float stopY;
	float radius;
	int size;

	MyFillCircle() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
		radius = 0;
	}

	MyFillCircle(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		radius = 0;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		this.setPenType(penType, paint);
		canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius,
				paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
		radius = (float) ((Math.sqrt((mx - startX) * (mx - startX)
				+ (my - startY) * (my - startY))) / 2);
	}
}

// 橡皮
class MyEraser extends PaintAction {
	Path path;
	int size;
	public float x;
	public float y;

	MyEraser() {
		path = new Path();
		size = 1;
		x = 0;
		y = 0;
	}

	MyEraser(float x, float y, int size, int color) {
		super(color);
		path = new Path();
		this.size = size;
		this.x = x;
		this.y = y;
		path.moveTo(x, y);
		path.lineTo(x, y);
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawPath(path, paint);
	}
	public void drawEraPoint(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawPoint(x, y, paint);
	}

	public void move(float mx, float my) {
		path.lineTo(mx, my);
		this.x = mx;
		this.y = my;
	}

}