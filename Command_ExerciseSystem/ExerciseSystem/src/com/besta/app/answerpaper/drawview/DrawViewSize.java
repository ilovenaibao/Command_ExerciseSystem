package com.besta.app.answerpaper.drawview;


public class DrawViewSize {
	public int resetWidth = 0;
	public int resetHeight = 0;
	public int VIEW_WIDTH = 700;
	public int VIEW_HEIGHT = 350;
	// 紙張實際大小
	public int Real_Width = 0;
	public int Real_Height = 0;
	// 屏幕可顯示位置大小
	public int screen_width = 0;
	public int screen_height = 0;
	// 屏幕與實際紙張大小比率
	public int width_ratio = 1;
	public int height_ratio = 1;

	public float now_x, now_y;

	public DrawViewSize() {
		resetWidth = resetHeight = 0;
		now_x = now_y = 0;
	}

	public void ResetScreenSize(int width, int height) {
		resetWidth = width;
		resetHeight = height;
	}

	public void SetRealSize(int width, int height, int padding_x, int padding_y) {
		VIEW_WIDTH = width;
		VIEW_HEIGHT = height;

		screen_width = VIEW_WIDTH;
		screen_height = VIEW_HEIGHT;
		Real_Width = VIEW_WIDTH;
		Real_Height = VIEW_HEIGHT;
		width_ratio = Real_Width / VIEW_WIDTH;
		height_ratio = Real_Height / VIEW_HEIGHT;

		now_x = padding_x;
		now_y = padding_y;
	}

	public void SetNewPageSize(int width, int height) {
		if (width < VIEW_WIDTH) {
			Real_Width = VIEW_WIDTH;
		} else {
			Real_Width = width;
		}
		if (height < VIEW_HEIGHT) {
			Real_Height = VIEW_HEIGHT;
		} else {
			Real_Height = height;
		}
		width_ratio = Real_Width / VIEW_WIDTH;
		height_ratio = Real_Height / VIEW_HEIGHT;
	}
}
