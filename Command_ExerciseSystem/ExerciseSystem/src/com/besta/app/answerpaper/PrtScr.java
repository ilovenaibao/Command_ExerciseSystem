package com.besta.app.answerpaper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import com.besta.app.testcallactivity.StartAnswerPaperSetting;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author BXC2011007 Taylor
 * 
 * @describe
 * 
 */
public class PrtScr {
	public static String save_path = StartAnswerPaperSetting.save_path;

	public static String CallBoard(Activity activity, String packageName,
			String appName, String pic_name, Boolean FullScreen) {// true is
																	// FullScreen,
																	// false
		// isn't FullScreen
		String sRet = null;

		Intent intent = new Intent();
		intent.setAction("com.besta.app.draw");
		// intent.setAction(StartAnswerPaperSetting.ACTION_PRTSCR);
		Bundle bundle = new Bundle();
		bundle.putString("FOLDERNAME", packageName);
		bundle.putString("ABSOLUTE_PATH", save_path);
		bundle.putString("APPNAME", appName);
		bundle.putString("PIC_NAME", pic_name);
		bundle.putBoolean("FullScreen", FullScreen);
		sRet = save_path + packageName + "/" + pic_name;
		// 1.构建Bitmap
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int w = display.getWidth();
		int h = display.getHeight();
		Bitmap bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		// 2.获取屏幕
		View decorview = activity.getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		bmp = decorview.getDrawingCache();
		decorview.setDrawingCacheEnabled(false);
		File dir = new File(save_path + packageName);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File bmpOutputFile = new File(sRet);
		try {
			FileOutputStream out = new FileOutputStream(bmpOutputFile);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			// bmp.recycle();
			// bmp = null;
			// out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (null != bmp) {
			bmp.recycle();
			bmp = null;
		}

		// ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		// bundle.putByteArray("Back", baos.toByteArray());
		// intent.putExtras(bundle);
		// if (activity.getPackageManager().queryIntentActivities(intent, 0)
		// .size() > 0) {
		// activity.startActivityForResult(intent, 0);
		//
		// sRet = save_path + packageName + "/" + pic_name;
		// } else {
		// Toast.makeText(activity, "Can not find the function!",
		// Toast.LENGTH_SHORT).show();
		// sRet = null;
		// }

		return sRet;
	}
}
