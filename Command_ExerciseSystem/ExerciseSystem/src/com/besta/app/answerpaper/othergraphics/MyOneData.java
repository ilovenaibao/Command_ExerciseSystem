package com.besta.app.answerpaper.othergraphics;

import android.graphics.Bitmap;

public class MyOneData {
	public class MyPicInfo {
		Bitmap bmp;
		String index;

		public MyPicInfo() {
			bmp = null;
			index = null;
		}
	}

	public String pic_name;
	public int layer;
	public String layerStr;
	public String absolute_path;
	public boolean isChild;
	public boolean isExtend;
	public boolean isScrollFocuses;
	public String additionName;
	public boolean isAdditonScroll;
	public MyPicInfo picInfo;

	public MyOneData(String name, String layerFlag, int lyr, String flash,
			boolean child, boolean extend) {
		pic_name = name;
		layerStr = layerFlag;
		layer = lyr;
		absolute_path = flash;
	}

	public MyOneData() {
		pic_name = "";
		layer = 0;
		layerStr = "";
		absolute_path = "";
		isChild = false;
		isExtend = false;
		isScrollFocuses = false;
		additionName = "";
		isAdditonScroll = false;
		picInfo = new MyPicInfo();
	}

	public MyPicInfo getMyPicInfo(Bitmap bmp, int width, int height) {
		MyPicInfo ret = new MyPicInfo();

		ret.bmp = bmp;
		ret.index = "size:" + width + "X" + height;

		return ret;
	}
}
