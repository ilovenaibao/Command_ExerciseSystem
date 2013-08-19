package com.besta.app.answerpaper.othergraphics;

import android.graphics.Color;

public abstract class OtherGraphicSetting {
	// kind
	public final static int IN_SYSTEM = 0;
	public final static int IN_MYDEFINE = 1;
	public final static int IN_COLLECT = 2;
	public final static int TOTAL_KIND_COUNT = 3;

	// Key value
	public final static String GRAPHIC_RESULT_PATH = "GRAPHIC_RESULT_PATH";
	public final static String GRAPHIC_RESULT_PICNAME = "GRAPHIC_RESULT_PICNAME";
	public final static String GRAPHIC_RESULT_LIST_KIND = "GRAPHIC_RESULT_LIST_KIND";

	// 主窗口顏色
	public static int mainWndColor = 0xF0000000;
	// TitleBar focus & unFocus color
	public static int titleBarFocusColor = 0x40B0C0F0;
	public static int titleBarUnFocusColor = Color.TRANSPARENT;

	public static String[] PIC_END_WITH_STR = { ".png", ".jpg" };
	public static String[] MY_DEFINE_END_WITH_STR = {
			MyDefineGraphicSetting.SYSTEM_DEFINE_EXTERN_NAME,
			MyDefineGraphicSetting.USER_DEFINE_EXTERN_NAME };

	public static int list_pic_width = 100;
	public static int list_pic_height = 100;

	public static void addNewPicType(String addType) {

		int length = PIC_END_WITH_STR.length + 1;
		String[] newArray = new String[length];
		System.arraycopy(PIC_END_WITH_STR, 0, newArray, 0, length - 1);
		newArray[length - 1] = addType;
		PIC_END_WITH_STR = new String[length];
		System.arraycopy(newArray, 0, PIC_END_WITH_STR, 0, length);

	}
}
