package com.besta.app.answerpaper.drawview;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.PorterDuff.Mode;
import android.view.View;

public class DrawViewPen {
	public final static int BaoPenColor = 0xFF4F94CD;

	public Paint tmp;
	public Path path;
	public Paint paint = null;
	public Xfermode myXfermode = null;
	public int myPenColor = 0;
	public int myPenWidth = 0;
	public static int prePenWidth = 2;
	public int myEraserWidth = 10;
	public boolean isPen = true;

	public DrawViewPen() {
		tmp = new Paint(Paint.DITHER_FLAG);
		tmp.setColor(DrawViewPen.BaoPenColor);
		tmp.setStyle(Paint.Style.STROKE);
		tmp.setStrokeWidth(DrawViewPen.prePenWidth * 2);
		tmp.setStrokeJoin(Paint.Join.ROUND);
		tmp.setStrokeCap(Paint.Cap.ROUND);

		path = new Path();
		// 設置畫筆的顏色
		paint = new Paint(Paint.DITHER_FLAG);
		myPenColor = BaoPenColor;
		paint.setColor(myPenColor);
		// 設置畫筆的風格
		paint.setStyle(Paint.Style.STROKE);
		myPenWidth = prePenWidth;
		paint.setStrokeWidth(myPenWidth);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setFilterBitmap(true);
		// 反鋸齒
		paint.setAntiAlias(true);
		paint.setDither(true);
		myXfermode = paint.getXfermode();
		myEraserWidth = 10;

		isPen = true;
	}

	public void setDrawViewPenOrEraserStyle(View view, boolean isPenOrEraser,
	DrawViewPen dvPen, DrawViewEraser dvEraser,
	DrawViewSaveData dvSaveData) {
		dvPen.isPen = isPenOrEraser;
		if (dvPen.isPen) {
			dvPen.paint.setColor(dvPen.myPenColor);
			dvPen.paint.setStrokeWidth(dvPen.myPenWidth);
			dvPen.paint.setXfermode(dvPen.myXfermode);
		} else {
			dvEraser.currentSize = dvPen.myEraserWidth / 2;
			dvPen.paint.setStrokeWidth(dvPen.myEraserWidth);
			dvPen.paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		}
		dvSaveData.one_line_info.penFlag = dvPen.isPen;
		dvPen.paint.setStrokeJoin(Paint.Join.ROUND);
		dvPen.paint.setStrokeCap(Paint.Cap.ROUND);
		try {
			view.invalidate();
		} catch (Exception e) {

		}
	}

	public boolean GetPenSet() {
		return isPen;
	}

	// @Override
	// protected void finalize() throws Throwable {
	// tmp = null;
	// path = null;
	// paint = null;
	// myXfermode = null;
	// super.finalize();
	// }

	public void onDestroy() {
		tmp = null;
		path = null;
		paint = null;
		myXfermode = null;
		// try {
		// finalize();
		// } catch (Throwable e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.gc();
	}
}
