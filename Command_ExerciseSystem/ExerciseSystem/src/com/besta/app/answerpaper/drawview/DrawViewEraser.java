package com.besta.app.answerpaper.drawview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class DrawViewEraser {
	// 初始化eraser的大小
	public static int eraseWidth = 20;
	// eraser的最小值
	public static int MinEraseSize = 10;

	// eraser paint
	public Paint pEraser = new Paint();

	// initialize eraser size
	public int myEraserWidth = 10;

	// curren eraser size
	public float currentSize = 10;

	// eraser position
	public float currentX = 0;
	public float currentY = 0;

	// destroy eraser
	public void onDestroy() {
		pEraser = null;
	}

	/**
	 * 初始化橡皮的屬性
	 */
	public DrawViewEraser() {
		myEraserWidth = 10; // initialize eraser size
		currentSize = 10; // curren size
		currentX = currentY = 0;
		// eraser style
		pEraser.setStyle(Paint.Style.STROKE);
		pEraser.setStrokeJoin(Paint.Join.ROUND);
		pEraser.setStrokeCap(Paint.Cap.ROUND);
		pEraser.setStrokeWidth(2);
		pEraser.setColor(Color.BLACK);
		pEraser.setAntiAlias(true);
	}

	/**
	 * 設置橡皮的大小
	 * 
	 * @param width
	 *            大小值
	 * @param paint
	 *            顏色屬性
	 * @return 當前修改的大小
	 */
	public int setEraseWidth(int width, Paint paint) {
		paint.setStrokeWidth(width);
		currentSize = width / 2;
		myEraserWidth = width;
		return width;
	}

	/**
	 * 設置橡皮位置
	 * 
	 * @param x
	 * @param y
	 */
	public void SetEraserPos(float x, float y) {
		currentX = x;
		currentY = y;
	}

	/**
	 * 獲取當前橡皮大小
	 * 
	 * @return 當前大小
	 */
	public int GetErazerWidth() {
		return myEraserWidth;
	}

	/**
	 * 將橡皮擦除動作顯示到DrawView上
	 * 
	 * @param canvas
	 */
	public void DrawEraserOnCanvas(Canvas canvas) {
		canvas.drawCircle(currentX, currentY, currentSize, pEraser);
	}
}
