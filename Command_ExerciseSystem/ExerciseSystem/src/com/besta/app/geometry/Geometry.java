package com.besta.app.geometry;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.besta.app.answerpaper.othergraphics.MyDefineGraphicSetting.MyDefineData;
import com.besta.app.answerpaper.othersclassinfo.OneLineInfo;
import com.besta.app.answerpaper.othersclassinfo.PaintPos;
import com.besta.app.geometry.GeometryDataStruct.SpecificPoint;

public class Geometry {
	public final static int DRAW_PEN = 0;
	public final static int DRAW_LINE = 3;
	public final static int DRAW_OVAL = 4;
	public final static int DRAW_RECTANGLE = 5;
	public final static int DRAW_TRIANGLE = 6;
	public final static int DRAW_OTHER_GRAPHIC = 7;

	static// 設定拖拽框的矩形半徑以及移動框的增值
	int tmp = 7;
	static int tmp2 = 2;

	public Geometry() {
		tmp = 7;
		tmp2 = 2;
	}

	public Point[] GetDrawAllPoints(OneLineInfo info, int drawKind) {
		Point[] ret = null;

		return ret;
	}

	// diff_value 是Redraw時與屏幕移動的位置, 僅當重畫的時候設定
	public void SelectDrawKind(Canvas drawCanvas, OneLineInfo info,
			int drawKind, float diff_value_x, float diff_value_y,
			boolean drawRectFlag) {
		Redraw(drawCanvas, info, drawKind, diff_value_x, diff_value_y,
				drawRectFlag);
	}

	// 選擇重畫對象
	private void Redraw(Canvas drawCanvas, OneLineInfo info, int drawKind,
			float diff_value_x, float diff_value_y, boolean drawRectFlag) {
		int total_count = info.SavePaintPos.size() - 1;
		float start_x, start_y, end_x, end_y;
		start_x = start_y = end_x = end_y = 0;
		GeometryDataStruct.TrianglePoint trianglePoint = new GeometryDataStruct().new TrianglePoint();
		if (total_count == 1) {
			PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
			tmpPaintPos = info.SavePaintPos.get(0);
			start_x = tmpPaintPos.pos_x - diff_value_x;
			start_y = tmpPaintPos.pos_y - diff_value_y;
			tmpPaintPos = new PaintPos(0, 0, 0);
			tmpPaintPos = info.SavePaintPos.get(1);
			end_x = tmpPaintPos.pos_x - diff_value_x;
			end_y = tmpPaintPos.pos_y - diff_value_y;
		} else if (total_count == 2) {
			if (drawKind == DRAW_TRIANGLE) {
				PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
				tmpPaintPos = info.SavePaintPos.get(0);
				trianglePoint.x1 = tmpPaintPos.pos_x - diff_value_x;
				trianglePoint.y1 = tmpPaintPos.pos_y - diff_value_y;
				tmpPaintPos = new PaintPos(0, 0, 0);
				tmpPaintPos = info.SavePaintPos.get(1);
				trianglePoint.x2 = tmpPaintPos.pos_x - diff_value_x;
				trianglePoint.y2 = tmpPaintPos.pos_y - diff_value_y;
				tmpPaintPos = new PaintPos(0, 0, 0);
				tmpPaintPos = info.SavePaintPos.get(2);
				trianglePoint.x3 = tmpPaintPos.pos_x - diff_value_x;
				trianglePoint.y3 = tmpPaintPos.pos_y - diff_value_y;
			}
		}

		Paint tmpPaint = new Paint();
		tmpPaint.setStyle(Paint.Style.STROKE);
		tmpPaint.setColor(info.penColor);
		tmpPaint.setStrokeWidth(info.penWidth);
		tmpPaint.setStrokeJoin(Paint.Join.ROUND);
		tmpPaint.setStrokeCap(Paint.Cap.ROUND);
		// 反鋸齒
		tmpPaint.setAntiAlias(true);
		tmpPaint.setDither(true);
		switch (drawKind) {
		case DRAW_LINE:
			DrawLine(drawCanvas, start_x, start_y, end_x, end_y, tmpPaint,
					drawRectFlag);
			break;
		case DRAW_RECTANGLE:
			DrawRectangle(drawCanvas, start_x, start_y, end_x, end_y, tmpPaint,
					drawRectFlag);
			break;
		case DRAW_OVAL:
			DrawOVAL(drawCanvas, start_x, start_y, end_x, end_y, tmpPaint,
					drawRectFlag);
			break;
		case DRAW_TRIANGLE:
			DrawTriangle(drawCanvas, trianglePoint, tmpPaint, drawRectFlag);
			break;
		// case DRAW_CIRCLE:
		// DrawCirle(drawCanvas, start_x, start_y, end_x, end_y, tmpPaint);
		// break;
		}
	}

	public int IsTouchDownPointPos(float x, float y, float preSX, float preSY,
			float preEX, float preEY, boolean leftTop, boolean rightTop,
			boolean leftBottom, boolean rightBottom, boolean topMid,
			boolean bottomMid, boolean leftMid, boolean rightMid, boolean center) {
		int iRet = 0;
		int radius = 20;
		float leftTopX, leftTopY, rightTopX, rightTopY, leftBottomX, leftBottomY, rightBottomX, rightBottomY, topMidX, topMidY, bottomMidX, bottomMidY, leftMidX, leftMidY, rightMidX, rightMidY, centerX, centerY;

		leftTopX = preSX;
		leftTopY = preSY;
		rightTopX = preEX;
		rightTopY = preSY;
		leftBottomX = preSX;
		leftBottomY = preEY;
		rightBottomX = preEX;
		rightBottomY = preEY;
		if (preEX > preSX) {
			topMidX = preSX + (preEX - preSX) / 2;
		} else {
			topMidX = preEX + (preSX - preEX) / 2;
		}
		topMidY = preSY;
		bottomMidX = topMidX;
		bottomMidY = preEY;

		if (preEY > preSY) {
			leftMidY = preSY + (preEY - preSY) / 2;
		} else {
			leftMidY = preEY + (preSY - preEY) / 2;
		}
		leftMidX = preSX;
		rightMidY = leftMidY;
		rightMidX = preEX;

		centerX = topMidX;
		centerY = topMidY;

		if ((leftTop) && (x <= leftTopX + radius && x >= leftTopX - radius)
				&& (y <= leftTopY + radius && y >= leftTopY - radius)) {
			iRet = 1;
		} else if ((rightTop)
				&& (x <= rightTopX + radius && x >= rightTopX - radius)
				&& (y <= rightTopY + radius && y >= rightTopY - radius)) {
			iRet = 2;
		} else if ((leftBottom)
				&& (x <= leftBottomX + radius && x >= leftBottomX - radius)
				&& (y <= leftBottomY + radius && y >= leftBottomY - radius)) {
			iRet = 3;
		} else if ((rightBottom)
				&& (x <= rightBottomX + radius && x >= rightBottomX - radius)
				&& (y <= rightBottomY + radius && y >= rightBottomY - radius)) {
			iRet = 4;
		} else if ((topMid) && (x <= topMidX + radius && x >= topMidX - radius)
				&& (y <= topMidY + radius && y >= topMidY - radius)) {
			iRet = 5;
		} else if ((bottomMid)
				&& (x <= bottomMidX + radius && x >= bottomMidX - radius)
				&& (y <= bottomMidY + radius && y >= bottomMidY - radius)) {
			iRet = 6;
		} else if ((leftMid)
				&& (x <= leftMidX + radius && x >= leftMidX - radius)
				&& (y <= leftMidY + radius && y >= leftMidY - radius)) {
			iRet = 7;
		} else if ((rightMid)
				&& (x <= rightMidX + radius && x >= rightMidX - radius)
				&& (y <= rightMidY + radius && y >= rightMidY - radius)) {
			iRet = 8;
		} else if ((center) && (x <= centerX + radius && x >= centerX - radius)
				&& (y <= centerY + radius && y >= centerY - radius)) {
			iRet = 9;
		}

		return iRet;
	}

	private float[] GetTriangleDragRectangleScale(
			GeometryDataStruct.TrianglePoint trianglePoint) {
		int count = 0;
		int count2 = 0;
		int total_count = 3;
		float tmpValue = 0;
		float[] ret_x = new float[3];
		float[] ret_y = new float[3];
		float[] ret = new float[6];

		ret_x[0] = trianglePoint.x1;
		ret_x[1] = trianglePoint.x2;
		ret_x[2] = trianglePoint.x3;
		ret_y[0] = trianglePoint.y1;
		ret_y[1] = trianglePoint.y2;
		ret_y[2] = trianglePoint.y3;
		for (count = 0; count < total_count - 1; count++) {
			for (count2 = 0; count2 < total_count - 1; count2++) {
				if (ret_x[count2] > ret_x[count2 + 1]) {
					tmpValue = ret_x[count2];
					ret_x[count2] = ret_x[count2 + 1];
					ret_x[count2 + 1] = tmpValue;
				}
				if (ret_y[count2] > ret_y[count2 + 1]) {
					tmpValue = ret_y[count2];
					ret_y[count2] = ret_y[count2 + 1];
					ret_y[count2 + 1] = tmpValue;
				}
			}
		}

		for (count = 0; count < 6; count++) {
			if (count < 3) {
				ret[count] = ret_x[count];
			} else {
				ret[count] = ret_y[count - 3];
			}
		}

		return ret;
	}

	public int CheckTouchTriangleScale(float x, float y,
			GeometryDataStruct.TrianglePoint trianglePoint) {
		int iRet = 0;
		float[] tmpPoint = new float[6];

		tmpPoint = GetTriangleDragRectangleScale(trianglePoint);
		if ((x >= tmpPoint[0] && x <= tmpPoint[2])
				&& (y >= tmpPoint[3] && y <= tmpPoint[5])) {
			iRet = -1;
		}

		return iRet;
	}

	public int IsTouchTrianglePoint(float x, float y,
			GeometryDataStruct.TrianglePoint trianglePoint) {
		int iRet = 0;
		int radius = 20;

		if ((x <= trianglePoint.x1 + radius && x >= trianglePoint.x1 - radius)
				&& (y <= trianglePoint.y1 + radius && y >= trianglePoint.y1
						- radius)) {
			iRet = 1;
		} else if ((x <= trianglePoint.x2 + radius && x >= trianglePoint.x2
				- radius)
				&& (y <= trianglePoint.y2 + radius && y >= trianglePoint.y2
						- radius)) {
			iRet = 2;
		} else if ((x <= trianglePoint.x3 + radius && x >= trianglePoint.x3
				- radius)
				&& (y <= trianglePoint.y3 + radius && y >= trianglePoint.y3
						- radius)) {
			iRet = 3;
		}

		return iRet;
	}

	/***************************************************************/
	// describe:
	// 1.作圖時的拉伸邊框以及移動的時候圖形邊框
	// ----> ..........5
	// ----> .1-----------------2
	// ----> .|.................|
	// ----> .|.................|
	// ----> 7|........9........|8
	// ----> .|.................|
	// ----> .|.................|
	// ----> .3-----------------4
	// ----> ..........6
	// 以此矩形為順序來選擇需要顯示的個數
	/***************************************************************/
	public static void DrawDragRectangle(Canvas drawCanvas, float start_x,
			float start_y, float end_x, float end_y, Paint paint,
			boolean left_top, boolean right_top, boolean left_bottom,
			boolean right_bottom, boolean top_mid, boolean bottom_mid,
			boolean left_mid, boolean right_mid, boolean center_mid) {
		float tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y;

		Paint moveMarkPaint = new Paint(paint);
		moveMarkPaint.setColor(Color.GRAY - 50);
		moveMarkPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
		moveMarkPaint.setAlpha(100);

		Paint roundLinePaint = new Paint(moveMarkPaint);
		roundLinePaint.setStyle(Style.STROKE);
		roundLinePaint.setStrokeWidth(1);
		roundLinePaint
				.setPathEffect(new DashPathEffect(new float[] { 4, 8 }, 1));

		// 左上角
		if (left_top) {
			tmpStart_x = start_x - tmp;
			tmpStart_y = start_y - tmp;
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}
		// 右上角
		if (right_top) {
			tmpStart_x = end_x - tmp;
			tmpStart_y = start_y - tmp;
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}
		// 左下角
		if (left_bottom) {
			tmpStart_x = start_x - tmp;
			tmpStart_y = end_y - tmp;
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}
		// 右下角
		if (right_bottom) {
			tmpStart_x = end_x - tmp;
			tmpStart_y = end_y - tmp;
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}

		// 四條邊中點位置拉伸框
		// 上邊
		if (top_mid) {
			if (end_x > start_x) {
				tmpStart_x = start_x + Math.abs(end_x - start_x) / 2 - tmp;
			} else {
				tmpStart_x = start_x - Math.abs(end_x - start_x) / 2 - tmp;
			}
			if (end_y > start_y) {
				tmpStart_y = start_y - tmp;
			} else {
				tmpStart_y = end_y - tmp;
			}
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}
		// 下邊
		if (bottom_mid) {
			if (end_x > start_x) {
				tmpStart_x = start_x + Math.abs(end_x - start_x) / 2 - tmp;
			} else {
				tmpStart_x = start_x - Math.abs(end_x - start_x) / 2 - tmp;
			}
			if (end_y > start_y) {
				tmpStart_y = end_y - tmp;
			} else {
				tmpStart_y = start_y - tmp;
			}
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}
		// 左邊
		if (left_mid) {
			if (end_x > start_x) {
				tmpStart_x = start_x - tmp;
			} else {
				tmpStart_x = end_x - tmp;
			}
			if (end_y > start_y) {
				tmpStart_y = start_y + Math.abs(end_y - start_y) / 2 - tmp;
			} else {
				tmpStart_y = start_y - Math.abs(end_y - start_y) / 2 - tmp;
			}
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}
		// 右邊
		if (right_mid) {
			if (end_x > start_x) {
				tmpStart_x = end_x - tmp;
			} else {
				tmpStart_x = start_x - tmp;
			}
			if (end_y > start_y) {
				tmpStart_y = start_y + Math.abs(end_y - start_y) / 2 - tmp;
			} else {
				tmpStart_y = start_y - Math.abs(end_y - start_y) / 2 - tmp;
			}
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}

		// 中心位置拉伸框
		if (center_mid) {
			if (end_x > start_x) {
				tmpStart_x = start_x + Math.abs(end_x - start_x) / 2 - tmp;
			} else {
				tmpStart_x = start_x - Math.abs(end_x - start_x) / 2 - tmp;
			}
			if (end_y > start_y) {
				tmpStart_y = start_y + Math.abs(end_y - start_y) / 2 - tmp;
			} else {
				tmpStart_y = start_y - Math.abs(end_y - start_y) / 2 - tmp;
			}
			tmpEnd_x = tmpStart_x + tmp + tmp;
			tmpEnd_y = tmpStart_y + tmp + tmp;
			drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
					moveMarkPaint);
		}

		// 整個移動邊框
		if (end_x > start_x) {
			tmpStart_x = start_x - tmp - tmp2;
			tmpEnd_x = end_x + tmp + tmp2;
		} else {
			tmpStart_x = start_x + tmp + tmp2;
			tmpEnd_x = end_x - tmp - tmp2;
		}
		if (end_y > start_y) {
			tmpStart_y = start_y - tmp - tmp2;
			tmpEnd_y = end_y + tmp + tmp2;
		} else {
			tmpStart_y = start_y + tmp + tmp2;
			tmpEnd_y = end_y - tmp - tmp2;
		}
		drawCanvas.drawRect(tmpStart_x, tmpStart_y, tmpEnd_x, tmpEnd_y,
				roundLinePaint);
	}

	private void DrawSpecificDragPoints(Canvas drawCanvas,
			GeometryDataStruct.SpecificPoint[] point, Paint paint) {
		int point_count = 0;
		int point_total_count = point.length;

		Paint moveMarkPaint = new Paint(paint);
		moveMarkPaint.setColor(Color.GRAY - 50);
		moveMarkPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
		moveMarkPaint.setAlpha(100);

		Paint roundLinePaint = new Paint(moveMarkPaint);
		roundLinePaint.setStyle(Style.STROKE);
		roundLinePaint.setStrokeWidth(1);
		roundLinePaint
				.setPathEffect(new DashPathEffect(new float[] { 4, 8 }, 1));

		for (point_count = 0; point_count < point_total_count; point_count++) {
			drawCanvas.drawRect(point[point_count].x - tmp,
					point[point_count].y - tmp, point[point_count].x + tmp,
					point[point_count].y + tmp, moveMarkPaint);
		}
	}

	// 畫線-----> kind = 1
	public void DrawLine(Canvas drawCanvas, float start_x, float start_y,
			float end_x, float end_y, Paint paint, boolean drawRectFlag) {
		Path drawPath = new Path();

		drawPath.moveTo(start_x, start_y);
		drawPath.lineTo(end_x, end_y);

		if (drawRectFlag) {
			DrawDragRectangle(drawCanvas, start_x, start_y, end_x, end_y,
					paint, true, false, false, true, false, false, false,
					false, false);
		}
		drawPath.close();
		drawCanvas.drawPath(drawPath, paint);
	}

	// 畫矩形-----> kind = 2
	public void DrawRectangle(Canvas drawCanvas, float start_x, float start_y,
			float end_x, float end_y, Paint paint, boolean drawRectFlag) {
		// paint.setAntiAlias(true);
		// paint.setDither(true);
		if (drawRectFlag) {
			DrawDragRectangle(drawCanvas, start_x, start_y, end_x, end_y,
					paint, true, true, true, true, true, true, true, true,
					false);
		}
		drawCanvas.drawRect(start_x, start_y, end_x, end_y, paint);
	}

	// 畫橢圓-----> kind = 3
	public void DrawOVAL(Canvas drawCanvas, float start_x, float start_y,
			float end_x, float end_y, Paint paint, boolean drawRectFlag) {
		// paint.setAntiAlias(true);
		// paint.setDither(true);
		if (drawRectFlag) {
			DrawDragRectangle(drawCanvas, start_x, start_y, end_x, end_y,
					paint, true, true, true, true, true, true, true, true,
					false);
		}
		RectF oval = new RectF(start_x, start_y, end_x, end_y);
		drawCanvas.drawOval(oval, paint);
	}

	// 畫圓-----> kind = 4
	public void DrawCirle(Canvas drawCanvas, float start_x, float start_y,
			float end_x, float end_y, Paint paint) {
		// paint.setAntiAlias(true);
		// paint.setDither(true);
		float a = end_x - start_x;
		float b = end_y - start_y;
		float sx = a / 2 + start_x;
		float sy = b / 2 + start_y;
		a *= a;
		b *= b;
		DrawDragRectangle(drawCanvas, start_x, start_y, end_x, end_y, paint,
				true, true, true, true, true, true, true, true, false);
		float radius = (float) Math.sqrt(a + b) / 2;
		drawCanvas.drawCircle(sx, sy, radius, paint);
	}

	// 畫矩形-----> kind = 2
	public void DrawMyDefine(Canvas drawCanvas, float start_x, float start_y,
			float end_x, float end_y, Paint paint, MyDefineData myDefData) {
		// paint.setAntiAlias(true);
		// paint.setDither(true);

		DrawDragRectangle(drawCanvas, start_x, start_y, end_x, end_y, paint,
				true, true, true, true, true, true, true, true, false);
		drawCanvas.drawRect(start_x, start_y, end_x, end_y, paint);
	}

	public GeometryDataStruct.TrianglePoint GetTrianglePoints(float start_x,
			float start_y, float end_x, float end_y) {
		GeometryDataStruct.TrianglePoint ret = new GeometryDataStruct().new TrianglePoint();

		if (end_x > start_x) {
			ret.x1 = start_x + (end_x - start_x) / 2;
			ret.x2 = start_x;
			ret.x3 = end_x;
		} else {
			ret.x1 = end_x + (start_x - end_x) / 2;
			ret.x2 = end_x;
			ret.x3 = start_x;
		}
		if (end_y > start_y) {
			ret.y1 = start_y;
			ret.y2 = end_y;
			ret.y3 = end_y;
		} else {
			ret.y1 = end_y;
			ret.y2 = start_y;
			ret.y3 = start_y;
		}

		return ret;
	}

	public void DrawTriangle(Canvas drawCanvas,
			GeometryDataStruct.TrianglePoint trianglePoint, Paint paint,
			boolean drawRectFlag) {
		Path drawPath = new Path();
		GeometryDataStruct.SpecificPoint[] point = new GeometryDataStruct.SpecificPoint[3];
		float[] tmpTriangle = new float[6];

		drawPath.moveTo(trianglePoint.x1, trianglePoint.y1);
		drawPath.lineTo(trianglePoint.x2, trianglePoint.y2);
		drawPath.moveTo(trianglePoint.x2, trianglePoint.y2);
		drawPath.lineTo(trianglePoint.x3, trianglePoint.y3);
		drawPath.moveTo(trianglePoint.x3, trianglePoint.y3);
		drawPath.lineTo(trianglePoint.x1, trianglePoint.y1);

		point[0] = new GeometryDataStruct().new SpecificPoint();
		point[0].x = trianglePoint.x1;
		point[0].y = trianglePoint.y1;
		point[1] = new GeometryDataStruct().new SpecificPoint();
		point[1].x = trianglePoint.x2;
		point[1].y = trianglePoint.y2;
		point[2] = new GeometryDataStruct().new SpecificPoint();
		point[2].x = trianglePoint.x3;
		point[2].y = trianglePoint.y3;

		tmpTriangle = GetTriangleDragRectangleScale(trianglePoint);
		if (drawRectFlag) {
			DrawDragRectangle(drawCanvas, tmpTriangle[0], tmpTriangle[3],
					tmpTriangle[2], tmpTriangle[5], paint, false, false, false,
					false, false, false, false, false, false);
			DrawSpecificDragPoints(drawCanvas, point, paint);
		}

		drawPath.close();
		drawCanvas.drawPath(drawPath, paint);
	}
}
