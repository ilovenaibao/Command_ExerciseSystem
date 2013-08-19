package com.besta.app.answerpaper.othersclassinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

import com.besta.app.exerciseengine.R;

public class MyScroll {
	private int Real_Len = 0;
	private int screen_len = 0;
	private int scroll_len = 0;
	private int divid_value = 3;
	// 1:vertical & 2:horizon -> scroll
	final public static int TYPE_VERTICAL = 1;
	final public static int TYPE_HORIZON = 2;
	private int scroll_type = 0;
	private Context parentContext = null;
	public Bitmap scroll_bg = null;

	public MyScroll(Context context, int scrollStyle) {
		parentContext = context;
		Real_Len = 0;
		screen_len = 0;
		scroll_len = 0;
		scroll_type = scrollStyle;

	}

	public MyScroll(Context context, int RealLen, int screenLen, int scrollStyle) {
		parentContext = context;
		Real_Len = RealLen / 2;
		screen_len = screenLen;
		scroll_type = scrollStyle;
		SetScrollLen();
	}

	public Bitmap GetMyScrollBitmap() {
		return scroll_bg;
	}

	public int GetScrollLen() {
		return scroll_len;
	}

	private void SetScrollLen() {
		// 計算實際scroll的長度即為: 此答題紙範圍的顯示長度占總答題紙長度的幾分之幾
		scroll_len = screen_len * screen_len / Real_Len;
		// 3張Bitmap載入scroll的上中下
		Bitmap head_bitmap = null;
		Bitmap end_bitmap = null;
		Bitmap middle_bitmap = null;
		// if (scroll_len <= 20) {
		// return;
		// }
		if (scroll_type == TYPE_VERTICAL) {
			head_bitmap = ((BitmapDrawable) parentContext.getResources()
					.getDrawable(R.drawable.ver_scrollbar_head)).getBitmap();
			end_bitmap = ((BitmapDrawable) parentContext.getResources()
					.getDrawable(R.drawable.ver_scrollbar_end)).getBitmap();
			middle_bitmap = ((BitmapDrawable) parentContext.getResources()
					.getDrawable(R.drawable.ver_scrollbar_middle)).getBitmap();

		} else if (scroll_type == TYPE_HORIZON) {
			head_bitmap = ((BitmapDrawable) parentContext.getResources()
					.getDrawable(R.drawable.hori_scrollbar_head)).getBitmap();
			end_bitmap = ((BitmapDrawable) parentContext.getResources()
					.getDrawable(R.drawable.hori_scrollbar_end)).getBitmap();
			middle_bitmap = ((BitmapDrawable) parentContext.getResources()
					.getDrawable(R.drawable.hori_scrollbar_middle)).getBitmap();
		}

		// 生成Scroll的完整Bitmap
		scroll_bg = MakeNewScrollBitmap(head_bitmap, end_bitmap, middle_bitmap,
				scroll_type);
	}

	private Bitmap MakeNewScrollBitmap(Bitmap bitmap_head, Bitmap bitmap_end,
			Bitmap bitmap_middle, int scroll_type) {
		Bitmap drawBitmap = null;
		int real_scroll_len = 0;
		int count = 0;
		int total_count = 0;
		float left = 0;
		float top = 0;
		if (scroll_type == TYPE_VERTICAL) {
			total_count = scroll_len / bitmap_middle.getHeight();
			if (total_count < divid_value) {
				total_count = divid_value;
			}
			real_scroll_len = (total_count - divid_value)
					* bitmap_middle.getHeight() + bitmap_head.getHeight()
					+ bitmap_end.getHeight();
			drawBitmap = Bitmap.createBitmap(bitmap_middle.getWidth(),
					real_scroll_len, Config.ARGB_8888);
		} else if (scroll_type == TYPE_HORIZON) {
			total_count = scroll_len / bitmap_middle.getWidth();
			if (total_count < divid_value) {
				total_count = divid_value;
			}
			real_scroll_len = (total_count - divid_value)
					* bitmap_middle.getWidth() + bitmap_head.getWidth()
					+ bitmap_end.getWidth();
			drawBitmap = Bitmap.createBitmap(real_scroll_len,
					bitmap_middle.getHeight(), Config.ARGB_8888);
		}

		Canvas myScrollCanvas = new Canvas(drawBitmap);
		myScrollCanvas.drawBitmap(bitmap_head, left, top, null);
		if (scroll_type == TYPE_VERTICAL) {
			top += bitmap_head.getHeight();
			for (count = 0; count < total_count - divid_value; count++) {
				myScrollCanvas.drawBitmap(bitmap_middle, left, top, null);
				top += bitmap_middle.getHeight() / 2;
			}
			if (total_count > divid_value) {
				top += bitmap_middle.getHeight() / 2;
			}
		} else if (scroll_type == TYPE_HORIZON) {
			left += bitmap_head.getWidth();
			for (count = 0; count < total_count - divid_value; count++) {
				myScrollCanvas.drawBitmap(bitmap_middle, left, top, null);
				left += bitmap_middle.getWidth() / 2;
			}
			if (total_count > divid_value) {
				left += bitmap_middle.getWidth() / 2;
			}
		}
		myScrollCanvas.drawBitmap(bitmap_end, left, top, null);
		top += bitmap_end.getHeight();
		left += bitmap_end.getWidth();
		drawBitmap = Bitmap.createBitmap(drawBitmap, 0, 0, (int) left,
				(int) top);

		return drawBitmap;
	}

	private Bitmap MakeMtrixPic(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newBmp;
	}
}
