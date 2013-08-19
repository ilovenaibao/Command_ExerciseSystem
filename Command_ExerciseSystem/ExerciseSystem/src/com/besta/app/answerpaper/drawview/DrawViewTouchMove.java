package com.besta.app.answerpaper.drawview;

import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class DrawViewTouchMove {
	private Context parentContext;
	// Scroll move divide value
	private int divid_value = 4;

	public boolean touch_eventFlag = false;
	public boolean isMoveBg = false;
	public boolean touch_up = false;
	public float distance_x = 0;
	public float distance_y = 0;

	Paint myScrollPaint = null;
	public boolean scroll_show_flag = false;
	public int scroll_Alpha = 255;

	public boolean doubleTouchMoveFlag = false;

	public DrawViewTouchMove(Context context) {
		parentContext = context;
		touch_eventFlag = false;
		isMoveBg = false;
		touch_up = false;
		distance_x = distance_y = 0;
		myScrollPaint = new Paint();
		scroll_show_flag = false;
		scroll_Alpha = 255;

		doubleTouchMoveFlag = false;
	}

	public void DrawScrollOnCanvas(Canvas canvas, DrawViewBitmap dvBitmap,
			DrawViewSize dvSize, DrawViewTouchMove dvTouchMove, Paint bmpPaint,
			float diff_value_x, float diff_value_y) {
		// Log.i("dvBitmap.move_hand_bitmap =============", ""
		// + dvBitmap.move_hand_bitmap);
		// canvas.drawBitmap(dvBitmap.move_hand_bitmap, dvBitmap.move_hand_x,
		// dvBitmap.move_hand_y, bmpPaint); // 3
		float tmp_pos = 0;
		float tmpLen = 0;
		// 顯示垂直scrollbar
		if (null != dvBitmap.verticall_scroll) {
			tmp_pos = diff_value_y / dvSize.height_ratio;
			tmpLen = dvBitmap.verticall_scroll.getHeight();
			if (diff_value_y < divid_value) {
				tmp_pos = divid_value;
			} else if (tmp_pos + tmpLen > dvSize.screen_height - divid_value) {
				tmp_pos = dvSize.screen_height - divid_value - tmpLen;
			}
			bmpPaint.setAlpha(255);
			// canvas.drawBitmap(dvBitmap.verticall_scroll,
			// dvSize.screen_width - 11, tmp_pos,
			// dvTouchMove.myScrollPaint); // 4

			if (null != dvBitmap.verticall_scroll) {
				canvas.drawBitmap(
						dvBitmap.verticall_scroll,
						dvSize.screen_width
								- dvBitmap.verticall_scroll.getWidth(),
						tmp_pos, dvTouchMove.myScrollPaint); // 4
			}
		}
		// 顯示水平ScrollBar
		// if (horizon_scroll != null) {
		// tmp_pos = diff_value_x / width_ratio;
		// tmpLen = horizon_scroll.getWidth();
		// if (diff_value_x < divid_value) {
		// tmp_pos = divid_value;
		// } else if (tmp_pos + tmpLen > screen_width - divid_value) {
		// tmp_pos = screen_width - divid_value - tmpLen;
		// }
		// canvas.drawBitmap(horizon_scroll, tmp_pos, screen_height - 11,
		// myScrollPaint); // 4
		// }
	}
}
