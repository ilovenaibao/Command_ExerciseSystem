package com.besta.app.answerpaper.drawview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.besta.app.geometry.Geometry;
import com.besta.app.geometry.GeometryDataStruct;
import com.besta.app.toolswindow.MyToolsWindow;

public class DrawViewGeometry {
	public Geometry myGeometry = null;
	public float geoMove_x = 0;
	public float geoMove_y = 0;

	public float sx, sy, ex, ey;

	public boolean drawGeometryFlag = false;
	public int drawGeometryEnd = -1;
	public int drawGeometryChos = 0;
	public int preDrawGeometryChos = 0;
	public int checkDownPos = 0;

	// Triangle struct
	GeometryDataStruct.TrianglePoint trianglePoint = null;

	/**
	 * 初始化幾何圖形基本設置
	 */
	public DrawViewGeometry() {
		myGeometry = new Geometry();
		geoMove_x = geoMove_y = 0;
		sx = sy = ex = ey = 0;

		drawGeometryFlag = false;
		drawGeometryEnd = -1;
		drawGeometryChos = 0;
		preDrawGeometryChos = 0;
		checkDownPos = 0;

		trianglePoint = new GeometryDataStruct().new TrianglePoint();
	}

	/**
	 * 切換是否是畫幾何圖形還是任意圖像
	 * 
	 * @param chos
	 *            具體參照Geometry.java定義
	 */
	public void SetGeometryOrPen(int chos) {
		drawGeometryChos = chos;
		if (preDrawGeometryChos != drawGeometryChos
				&& preDrawGeometryChos == Geometry.DRAW_PEN) {
			sx = sy = ex = ey = -10;
			trianglePoint.ResetTrianglePoint();
		}

		if (drawGeometryChos != Geometry.DRAW_PEN) {
			if (drawGeometryChos != MyToolsWindow.TOOL_DRAW_CLEAN) {
				drawGeometryFlag = true;
			}
		} else {
			drawGeometryFlag = false;
			drawGeometryEnd = -1;
			sx = sy = ex = ey = -10;
			trianglePoint.ResetTrianglePoint();
		}
		preDrawGeometryChos = drawGeometryChos;
	}

	/**
	 * 檢測Touch Down的位置是在幾何圖形中還是幾何圖形外
	 * 
	 * @param x
	 *            Touch Down的點座標
	 * @param y
	 * @return 0為外，其他 > 0 的值為內部
	 */
	public int CheckPenDownPosIsInMove(float x, float y) {
		int iRet = 0;
		int tmpCheck = 0;

		if (drawGeometryChos == Geometry.DRAW_LINE
				|| drawGeometryChos == Geometry.DRAW_OVAL
				|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
			if (ex > sx) {
				if (x >= sx && x <= ex) {
					tmpCheck -= 1;
				}
			} else {
				if (x >= ex && x <= sx) {
					tmpCheck -= 1;
				}
			}

			if (ey > sy) {
				if (y >= sy && y <= ey) {
					tmpCheck -= 1;
				}
			} else {
				if (y >= ey && y <= sy) {
					tmpCheck -= 1;
				}
			}

			if (tmpCheck == -2) {
				iRet = -1;
			}
		} else if (drawGeometryChos == Geometry.DRAW_TRIANGLE) {
			iRet = myGeometry.CheckTouchTriangleScale(x, y, trianglePoint);
		}

		int tmpRet = 0;
		switch (drawGeometryChos) {
		case Geometry.DRAW_LINE:
			tmpRet = myGeometry.IsTouchDownPointPos(x, y, sx, sy, ex, ey, true,
					false, false, true, false, false, false, false, false);
			break;
		case Geometry.DRAW_RECTANGLE:
			tmpRet = myGeometry.IsTouchDownPointPos(x, y, sx, sy, ex, ey, true,
					true, true, true, true, true, true, true, false);
			break;
		case Geometry.DRAW_OVAL:
			tmpRet = myGeometry.IsTouchDownPointPos(x, y, sx, sy, ex, ey, true,
					true, true, true, true, true, true, true, false);
			break;
		case Geometry.DRAW_TRIANGLE:
			tmpRet = myGeometry.IsTouchTrianglePoint(x, y, trianglePoint);
			break;
		}
		if (tmpRet != 0) {
			iRet = tmpRet;
		}

		return iRet;
	}

	public void ReSizeGeometryGraphics(float x, float y) {
		switch (checkDownPos) {
		case 1:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				sx = x;
				sy = y;
			} else if (drawGeometryChos == Geometry.DRAW_TRIANGLE) {
				trianglePoint.x1 = x;
				trianglePoint.y1 = y;
			}
			break;
		case 2:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				ex = x;
				sy = y;
			} else if (drawGeometryChos == Geometry.DRAW_TRIANGLE) {
				trianglePoint.x2 = x;
				trianglePoint.y2 = y;
			}
			break;
		case 3:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				sx = x;
				ey = y;
			} else if (drawGeometryChos == Geometry.DRAW_TRIANGLE) {
				trianglePoint.x3 = x;
				trianglePoint.y3 = y;
			}
			break;
		case 4:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				ex = x;
				ey = y;
			}
			break;
		case 5:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				sy = y;
			}
			break;
		case 6:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				ey = y;
			}
			break;
		case 7:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				sx = x;
			}
			break;
		case 8:
			if (drawGeometryChos == Geometry.DRAW_LINE
					|| drawGeometryChos == Geometry.DRAW_OVAL
					|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
				ex = x;
			}
			break;
		case 9:
			break;
		}
	}

	public void DrawGeometry(Canvas canvas, int kind, DrawViewPen dvPen,
			boolean drawRectFlag) {
		Paint tmpBmpPaint = new Paint(dvPen.paint);
		tmpBmpPaint.setStrokeWidth(dvPen.myPenWidth);
		tmpBmpPaint.setStyle(Paint.Style.STROKE);
		tmpBmpPaint.setStrokeJoin(Paint.Join.ROUND);
		tmpBmpPaint.setStrokeCap(Paint.Cap.ROUND);
		tmpBmpPaint.setAntiAlias(true);
		tmpBmpPaint.setDither(true);
		switch (kind) {
		case Geometry.DRAW_LINE:
			myGeometry.DrawLine(canvas, sx, sy, ex, ey, tmpBmpPaint,
					drawRectFlag);
			break;
		case Geometry.DRAW_OVAL:
			myGeometry.DrawOVAL(canvas, sx, sy, ex, ey, tmpBmpPaint,
					drawRectFlag);
			break;
		case Geometry.DRAW_RECTANGLE:
			myGeometry.DrawRectangle(canvas, sx, sy, ex, ey, tmpBmpPaint,
					drawRectFlag);
			;
			break;
		case Geometry.DRAW_TRIANGLE:
			myGeometry.DrawTriangle(canvas, trianglePoint, tmpBmpPaint,
					drawRectFlag);
			break;
		case Geometry.DRAW_OTHER_GRAPHIC:
			break;
		}

	}

	public void DrawGeometryOnCanvas(Canvas canvas, DrawViewBitmap dvBitmap,
			DrawViewPen dvPen) {
		if (drawGeometryEnd == 1) {
			DrawGeometry(dvBitmap.drawCanvas, drawGeometryChos, dvPen, false);
			drawGeometryEnd = 2;
		} else if (drawGeometryEnd != 2) {
			if (drawGeometryEnd != -2) {
				DrawGeometry(canvas, drawGeometryChos, dvPen, true);
			}
		}
	}
}
