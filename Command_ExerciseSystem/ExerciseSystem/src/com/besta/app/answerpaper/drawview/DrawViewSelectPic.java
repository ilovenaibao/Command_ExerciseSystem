package com.besta.app.answerpaper.drawview;

import android.graphics.Bitmap;

public class DrawViewSelectPic {
	public Bitmap showBmp = null;
	public String showBmp_path = null;
	public int width, height;
	public int max_width, max_height;

	public float geoMove_x, geoMove_y, sx, sy, ex, ey;
	public int checkDownPos;
	public boolean isNotMove;
	public int drawSelectEnd = -1;

	public DrawViewSelectPic() {
		width = height = max_width = max_height = 0;
		geoMove_x = geoMove_y = sx = sy = ex = ey = 0;
		checkDownPos = 0;
		isNotMove = true;
		drawSelectEnd = -1;
	}

	public void SetSelectPicIntoView(Bitmap select_src, String src_path,
			DrawViewBitmap dvBitmap, DrawViewSize dvSize) {
		dvBitmap.cacheBitmap = DrawViewBitmap
				.ClearBitmapToNull(dvBitmap.cacheBitmap);
		if (select_src != null) {
			max_width = dvSize.VIEW_WIDTH;
			max_height = dvSize.VIEW_HEIGHT;
			dvBitmap.cacheBitmap = Bitmap.createBitmap(select_src);
			showBmp = Bitmap.createBitmap(dvBitmap.cacheBitmap);
			if (showBmp != null && dvBitmap.cacheBitmap != null) {
				showBmp_path = new String(src_path);
				showBmp = DrawViewBitmap.MakeMtrixPic(dvBitmap.cacheBitmap,
						dvSize.VIEW_WIDTH / 3, dvSize.VIEW_HEIGHT / 3);
				dvBitmap.cacheBitmapShow = true;
				isNotMove = false;
				sx = sy = 60;
				width = showBmp.getWidth();
				height = showBmp.getHeight();
				ex = width + sx;
				ey = height + sy;
				drawSelectEnd = 0;
			}
		}
	}

	public void ReSizePic(Bitmap src, float x, float y) {
		switch (checkDownPos) {
		case 1:
			sx = x;
			sy = y;
			break;
		case 2:
			ex = x;
			sy = y;
			break;
		case 3:
			sx = x;
			ey = y;
			break;
		case 4:
			ex = x;
			ey = y;
			break;
		case 5:
			sy = y;
			break;
		case 6:
			ey = y;
			break;
		case 7:
			sx = x;
			break;
		case 8:
			ex = x;
			break;
		case 9:
			break;
		}
		int width = (int) Math.abs(ex - sx);
		int height = (int) Math.abs(ey - sy);
		if (width > max_width) {
			width = max_width;
		} else if (width == 0) {
			width = 1;
		}
		if (height > max_height) {
			height = max_height;
		} else if (height == 0) {
			height = 1;
		}

		showBmp = DrawViewBitmap.MakeMtrixPic(src, width, height);
	}

	public int CheckPenDownRect(float x, float y) {
		int iRet = 0;
		int tmpCheck = 0;

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

		int tmpRet = 0;
		tmpRet = IsTouchDownPointPos(x, y, sx, sy, ex, ey, true, true, true,
				true, true, true, true, true, false);
		if (tmpRet != 0) {
			iRet = tmpRet;
		}

		return iRet;
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

	public void ClearShowBitmap() {
		if (showBmp != null) {
			showBmp.recycle();
			showBmp = null;
		}
	}
}
