package com.besta.app.answerpaper.othersclassinfo;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;

public class util {
	private static final String tag = "util";
	public String save_path = "/sdcard/testpapers/";

	public static void writeDebugInfo(Intent data) {
		if (data != null) {
			Bundle bundle = data.getExtras();
			if (bundle != null) {
				for (String key : bundle.keySet()) {
					Object value = bundle.get(key);
					if (value == null)
						continue;
					if (value instanceof Boolean) {
						Boolean b = (Boolean) value;
						Log.d("tag", "bundle[" + key + "]=" + b.toString());
					} else {
						Log.d("tag", "bundle[" + key + "]=" + value.toString());
					}
				}
			}
		}
	}

	public static Bitmap ChangeBitmap(Bitmap res, int newWidth, int newHeight,
			boolean bRecycleOrg, Context context) {
		Bitmap res2;
		if (res == null) {
			return null;
		}
		int width = res.getWidth();
		int height = res.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		try {
			res2 = Bitmap.createBitmap(res, 0, 0, width, height, matrix, true);
			if (bRecycleOrg) {
				res = null;
			}
			return res2;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap RotationBitmap(Bitmap res, int angle) {
		// int angle = (4 - rotation) * 90;
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		res = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(),
				matrix, true);
		return res;
	}

	public static Bitmap createReflectedImage(Bitmap originalImage) {
		// The gap we want between the reflection and the original image
		final int reflectionGap = 4;

		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		// Create a Bitmap with the flip matrix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
				height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit reflection

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint defaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// Create a shader that is a linear gradient that covers the
		// reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0,
				originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
						+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		Log.d(tag, "bitmap recycle4 " + originalImage);
		originalImage.recycle();
		Log.d(tag, "bitmap recycle4 " + reflectionImage);
		reflectionImage.recycle();
		return bitmapWithReflection;
	}

	// create temporary file name user date and time.
	public static String createTempFilename() {
		SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH.mm.ss.SSS");
		String filename = timeStampFormat.format(new Date());
		filename += "_";
		return filename;
	}

	public static String createRecordShowName() {
		SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				"yyyy/MM/ddHH:mm");
		String filename = timeStampFormat.format(new Date());
		return filename;
	}

	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static void CompareStringToSmall(String[] strs) { // 从大到小排序
		if (strs == null || strs.length == 0) {
			return;
		}
		int num = strs.length;
		boolean change = true;
		String temp = null;
		for (int i = 0; i < num - 1; i++) {
			change = true;
			for (int j = 0; j < num - i - 1; j++) {
				if (strs[j].compareTo(strs[j + 1]) < 0) {
					temp = strs[j];
					strs[j] = strs[j + 1];
					strs[j + 1] = temp;
					change = false;
				}
			}
			if (change) {
				break;
			}
		}
	}

	public static void CompareStringToBig(String[] strs) { // 从大到小排序
		if (strs == null || strs.length == 0) {
			return;
		}
		int num = strs.length;
		boolean change = true;
		String temp = null;
		for (int i = 0; i < num - 1; i++) {
			change = true;
			for (int j = 0; j < num - i - 1; j++) {
				if (strs[j].compareTo(strs[j + 1]) > 0) {
					temp = strs[j];
					strs[j] = strs[j + 1];
					strs[j + 1] = temp;
					change = false;
				}
			}
			if (change) {
				break;
			}
		}
	}

	public static String[] Filter(String path, String contition) {
		File file = new File(path);
		final String temp = contition;
		return file.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fname) {
				return fname.indexOf(temp) != -1;
			}
		});
	}

	public static void BitmapRecycle(Bitmap bmp) {
		if (bmp != null && !bmp.isRecycled()) {
			bmp.recycle();
		}
	}

	public static Bitmap GetScreen(Activity activity) { // get screen show
		FileInputStream fis = null;
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		try {
			final String DISPLAY_DEVICE_NAME = "/dev/graphics/fb0";
			File file2 = new File(DISPLAY_DEVICE_NAME);
			fis = new FileInputStream(file2);

			if (rotation % 2 != 0) {
				int temp = width;
				width = height;
				height = temp;
			}

			byte[] buf = new byte[width * height * 4];
			int[] data = new int[width * height];
			int len = width * height;
			fis.read(buf);
			for (int i = 0, j = 3; i < len; i++, j += 4) {
				data[i] = ((int) (buf[j]) << 24) + ((int) (buf[j - 1]) << 16)
						+ ((int) (buf[j - 2]) << 8) + (int) (buf[j - 3]);
			}
			fis.close();
			Bitmap res = null;

			// File test = new File(Document.PATH_SCREEN + "test" +
			// Configure.PNG);
			// FileOutputStream out = new FileOutputStream(test);
			// out.write(buf);
			// out.flush();
			// out.close();

			res = Bitmap.createBitmap(data, width, height, Config.ARGB_8888);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Bitmap CutStatus(Bitmap screen, Activity activity) {
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int cutHeight = GetStatusHeight(activity);
		switch (rotation) {
		case Surface.ROTATION_0:
			screen = Bitmap.createBitmap(screen, 0, cutHeight,
					screen.getWidth(), screen.getHeight() - cutHeight);
			break;
		case Surface.ROTATION_90:
			screen = Bitmap.createBitmap(screen, 0, 0, screen.getWidth()
					- cutHeight, screen.getHeight());
			break;
		case Surface.ROTATION_180:
			screen = Bitmap.createBitmap(screen, 0, 0, screen.getWidth(),
					screen.getHeight() - cutHeight);
			break;
		case Surface.ROTATION_270:
			screen = Bitmap.createBitmap(screen, cutHeight, 0,
					screen.getWidth() - cutHeight, screen.getHeight());
			break;
		}
		return screen;
	}

	public static int GetStatusHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;

		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = new View(context).getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			Log.e("fail", "get status bar height fail");
			e1.printStackTrace();
		}
		return sbar;
	}

	public static void SaveAppName(String folderName, String appName) {
		try {
			File targetFile = new File(folderName + "/" + appName);
			if (!targetFile.exists()) {
				targetFile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(targetFile);
			fos.write(appName.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String GetAppName(String folderName, String appName) {
		File targetFile = new File(folderName + "/" + appName);
		if (!targetFile.exists()) {
			return folderName;
		} else {
//			String appName = null;
			try {
				FileInputStream fis;
				fis = new FileInputStream(targetFile);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						fis));
				appName = br.readLine();
				br.close();
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return appName;
		}
	}

	public static Bitmap DecodeBitmap(String path, Context context) {
		// Log.d(tag, "Decode Start");
		File file = new File(path);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		int reqWidth = context.getResources().getDisplayMetrics().widthPixels;
		int reqHeight = context.getResources().getDisplayMetrics().heightPixels;
		reqHeight -= GetStatusHeight(context);
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// int size = Math.round((float)options.outWidth *
		// (float)options.outHeight/
		// (float)options.inSampleSize/(float)options.inSampleSize);
		// size = size*4;
		// byte[] buf = new byte[size];

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		try {
			in = null;
			in = new FileInputStream(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
		// Log.d(tag, "bmpWidth = " + bmp.getWidth());
		// Log.d(tag, "bmpHeight = " + bmp.getHeight());
		// Log.d(tag, "Decode End");
		return bmp;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}

		return inSampleSize;
	}
}
