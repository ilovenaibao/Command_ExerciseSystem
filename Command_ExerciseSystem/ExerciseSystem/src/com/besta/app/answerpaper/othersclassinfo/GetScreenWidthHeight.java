package com.besta.app.answerpaper.othersclassinfo;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class GetScreenWidthHeight {
	private int width, height;
	public int Title_Bar_Height;
	public int SDK_VER;

	public GetScreenWidthHeight(Context context) {
		width = height = Title_Bar_Height = SDK_VER = 0;
		DisplayMetrics DM = new DisplayMetrics();
		// 获取窗口管理器,获取当前的窗口,调用getDefaultDisplay()后，其将关于屏幕的一些信息写进DM对象中,最后通过getMetrics(DM)获取
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(DM);
		// 打印获取的宽和高
		width = DM.widthPixels;// 屏幕宽度 (px)
		height = DM.heightPixels;// 屏幕高度 (px)
		// 要获取屏幕的宽和高等参数，首先需要声明一个DisplayMetrics对象，屏幕的宽高等属性存放在这个对象中

		height = getDisplayScreenHeight(context);

		// width = MyPxDipSpConvert.px2dip(context, width);
		// height = MyPxDipSpConvert.px2dip(context, height);
	}

	public int getDisplayScreenHeight(Context context) {
		int screenHeight = 0;

		DisplayMetrics metrics = new DisplayMetrics();
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		display.getMetrics(metrics);

		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int tmpWidth = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		int tmpHeight = wm.getDefaultDisplay().getHeight();// 屏幕高度

		int ver = getAndroidSDKVersion();
		SDK_VER = ver;
		if (ver < 13) {
			screenHeight = metrics.heightPixels;
		} else if (ver == 13) {
			try {
				Method method = display.getClass().getMethod("getRealHeight");
				screenHeight = (Integer) method.invoke(display);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (ver > 13) {
			try {
				Method method = display.getClass().getMethod("getRawHeight");
				screenHeight = (Integer) method.invoke(display);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Title_Bar_Height = Math.abs(screenHeight - tmpHeight);

		return screenHeight;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getTitleBarHeight() {
		return Title_Bar_Height;
	}

	public static int getAndroidSDKVersion() {
		int version = 0;
		try {
			version = Integer.valueOf(android.os.Build.VERSION.SDK_INT);
		} catch (NumberFormatException e) {
			Log.e("sdk_version", "cannot get");
		}
		return version;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
