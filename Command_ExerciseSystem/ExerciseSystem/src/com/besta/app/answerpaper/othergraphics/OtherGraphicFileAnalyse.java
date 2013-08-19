package com.besta.app.answerpaper.othergraphics;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class OtherGraphicFileAnalyse {

	public OtherGraphicFileAnalyse(Context context, int chosKind) {
		// switch (chosKind) {
		// case OtherGraphicSetting.IN_SYSTEM:
		// break;
		// case OtherGraphicSetting.IN_MYDEFINE:
		// break;
		// case OtherGraphicSetting.IN_COLLECT:
		// break;
		// }
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize_width = 1;
		int inSampleSize_height = 1;
		int retSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			inSampleSize_width = Math.round((float) width / (float) reqWidth);
			inSampleSize_height = Math
					.round((float) height / (float) reqHeight);
			retSampleSize = inSampleSize_width > inSampleSize_height ? inSampleSize_width
					: inSampleSize_height;
			// if (width > height) {
			// inSampleSize = Math.round((float) height / (float) reqHeight);
			// } else {
			// inSampleSize = Math.round((float) width / (float) reqWidth);
			// }
		}
		return retSampleSize;
	}

	/**
	 * 解析System pics 到MyOneData 類
	 * 
	 * @param path
	 *            要解析的數據路徑
	 * @return MyOneData 類數據結構
	 */
	public MyOneData analyseInSystem(String path) {
		MyOneData ret = new MyOneData();
		// Loading name
		File f = new File(path);
		ret.pic_name = f.getName();
		ret.additionName = path.substring(0,
				path.length() - ret.pic_name.length());
		ret.absolute_path = ret.additionName + ret.pic_name;

		// Loading Pic
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options,
				OtherGraphicSetting.list_pic_width,
				OtherGraphicSetting.list_pic_height);

		// Decode bitmap with inSampleSize set
		options.outWidth = OtherGraphicSetting.list_pic_width;
		options.outHeight = OtherGraphicSetting.list_pic_height;
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		ret.picInfo = ret.getMyPicInfo(bmp, imageWidth, imageHeight);

		return ret;
	}

	/**
	 * 解析MyDefine 格式到 MyOneData 類
	 * 
	 * @param path
	 *            要解析的data 路徑
	 * @return MyOneData類型數據結構
	 */
	public MyOneData analyseInMyDefine(String path) {
		MyOneData ret = new MyOneData();
		// Loading name
		File f = new File(path);
		ret.pic_name = f.getName();
		ret.additionName = path.substring(0,
				path.length() - ret.pic_name.length());
		ret.absolute_path = ret.additionName + ret.pic_name;

		// Loading Pic
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options,
				OtherGraphicSetting.list_pic_width,
				OtherGraphicSetting.list_pic_height);

		// Decode bitmap with inSampleSize set
		options.outWidth = OtherGraphicSetting.list_pic_width;
		options.outHeight = OtherGraphicSetting.list_pic_height;
		options.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		ret.picInfo = ret.getMyPicInfo(bmp, imageWidth, imageHeight);

		return ret;
	}
}
