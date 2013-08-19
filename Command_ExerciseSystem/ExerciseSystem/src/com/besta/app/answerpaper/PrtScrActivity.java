package com.besta.app.answerpaper;

import java.io.File;
import java.io.FileOutputStream;

import com.besta.app.answerpaper.othersclassinfo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class PrtScrActivity extends Activity {

	public static String save_path = "/sdcard/testpapers/";
	private Bitmap bmp = null;
	private static final String tag = "PrtScrActivity";
	// private String save_path = null;
	private int result_code = 0xEDDE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getIntent().getExtras();
		String folderName = null;
		String appName = null;
		String pic_name = null;
		Bitmap bmp = null;
		boolean fullscreen = false;
		if (bundle != null) {
			folderName = bundle.getString("FOLDERNAME");
			appName = bundle.getString("APPNAME");
			save_path = bundle.getString("ABSOLUTE_PATH");
			pic_name = bundle.getString("PIC_NAME");
			fullscreen = bundle.getBoolean("FullScreen");
			byte[] backData = bundle.getByteArray("Back");
			bmp = BitmapFactory.decodeByteArray(backData, 0, backData.length);
			if (!fullscreen) {
				bmp = util.CutStatus(bmp, this);
			}
		}

		String absolut_path = save_path + folderName + "/";
		File dir = new File(absolut_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filename = absolut_path + pic_name;
		File file = new File(filename);
		try {
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			bmp = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		setResult(result_code, null);
		finish();
	}
}