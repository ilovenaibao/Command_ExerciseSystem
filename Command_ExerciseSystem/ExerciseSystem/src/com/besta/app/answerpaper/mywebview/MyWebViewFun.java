package com.besta.app.answerpaper.mywebview;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.besta.app.answerpaper.drawview.DrawView;
import com.besta.app.answerpaper.drawview.DrawViewBitmap;
import com.besta.app.answerpaper.mywebview.MyWebViewCaller.Callee;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun.InitializeStartData;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun.ReturnGetStartBundleData;
import com.besta.app.testcallactivity.StartAnswerPaperSetting;
import com.besta.media.audio.play.CallBack;

public abstract class MyWebViewFun {
	private final static String url_mark = "image:";
	private final static String img_head = " src=\"data:image/png;base64,";
	private final static String img_end = "\"></a>";
	private static String buffer;

	public static Context parentContext = null;
	public static Bitmap Select_Bmp = null;

	public static void SetMyWebView(Context context, Bundle bundle,
	final ReturnGetStartBundleData bundleData,
	InitializeStartData initialStartData, WebView myShowWebView,
	final DrawView[] drawView) {
		parentContext = context;
		buffer = new String(
		bundle.getString(StartAnswerPaperSetting.NORMAL_WEBVIEW_DATA_KEY));
		// myShowWebView.loadDataWithBaseURL(null, buffer, "text/html", "utf-8",
		// null);
		// Debug by Taylor
		String cacheDir = context.getCacheDir().toString();
		buffer = cacheDir + File.separator + buffer;
		myShowWebView.loadUrl("file:///" + buffer);
		// Debug End
		// myShowWebView.loadData(buffer, "text/html", "utf-8");
		myShowWebView.getSettings().setJavaScriptEnabled(true);
		// myShowWebView.loadUrl("file:///android_asset/test_2.html");

		myShowWebView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}
		});

		myShowWebView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i("Click--->", "Click");
			}
		});

		myShowWebView.setOnLongClickListener(new MyWebViewOnLongClickListener(
		bundleData, initialStartData, myShowWebView) {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				// Picture myWVPic = myWebView.capturePicture();
				// Bitmap tmpWVbmp = Bitmap.createBitmap(
				// myInitializeStartData.writePaper_width,
				// myBundleData.screen_height, Bitmap.Config.ARGB_8888);
				// tmpWVbmp.eraseColor(Color.WHITE);
				// Canvas tmpCanvas = new Canvas(tmpWVbmp);
				// myWVPic.draw(tmpCanvas);
				Log.i("Long Click--->", "Long Click");
				return super.onLongClick(v);
			}

		});

		myShowWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains(url_mark)) {
					Log.i("touch_png--->", "touch_png");
					String imgBase64 = FindImageBuffer(url, img_head, 0);
					SetSelectBmp(imgBase64, bundleData, drawView,url.substring(url_mark.length()));
					//SetSelectBmpFromPath(parentContext, imgBase64, bundleData,drawView, url.substring(url_mark.length()));
				}
				return true;
			}

		});
	}

	/**
	* 選擇WebView介面的一張圖片
	* 
	* @param imgBase64
	*            base64圖片String
	* @param bundleData
	*            調用者信息
	* @param drawView
	*            答題介面
	* @param bitmap_id
	*            圖片id
	*/
	private static void SetSelectBmp(String imgBase64,
	final ReturnGetStartBundleData bundleData, DrawView[] drawView,
	String bitmap_id) {
		if (parentContext != null) {
			WindowManager wm = (WindowManager) parentContext
			.getSystemService(Context.WINDOW_SERVICE);
			int screen_width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			int screen_height = wm.getDefaultDisplay().getHeight();// 屏幕高度

			try {
				byte[] tmpImg = Base64.decode(imgBase64, Base64.DEFAULT);
				if (null == tmpImg) {
					Toast.makeText(parentContext, "cannot find picture",
					Toast.LENGTH_SHORT).show();
					return;
				}
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(tmpImg, 0, tmpImg.length, opt);

				final int minSideLength = Math.min(screen_width, screen_height);
				opt.inSampleSize = DrawViewBitmap.computeSampleSize(opt,
				minSideLength, screen_width * screen_height);
				opt.inJustDecodeBounds = false;
				opt.inInputShareable = true;
				opt.inPurgeable = true;
				try {
					Select_Bmp = BitmapFactory.decodeByteArray(tmpImg, 0,
					tmpImg.length, opt);
					String save_path = bundleData.absolute_path
					+ bundleData.answer_id + File.separator + bitmap_id
					+ ".png";
					SavePrtBitmap(Select_Bmp, save_path, false);

					bundleData.c1 = new MyWebViewCaller().new Callee();
					bundleData.c1.SetSelectPic(Select_Bmp, save_path,
					drawView[AnswerPaperFun.TOOL_WRITEPAPER]);
					bundleData.caller.SetI(bundleData.c1);
					if (drawView[AnswerPaperFun.TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSelectPic
						.SetSelectPicIntoView(
						bundleData.caller.GetSelectPic(),
						bundleData.caller.GetSelectPicPath(),
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvBitmap,
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSize);
						bundleData.caller.RefreshMyView();
					} else if (drawView[AnswerPaperFun.TOOL_TESTPAPER]
							.getVisibility() == View.VISIBLE) {
						drawView[AnswerPaperFun.TOOL_TESTPAPER].dvSelectPic
						.SetSelectPicIntoView(
						bundleData.caller.GetSelectPic(),
						bundleData.caller.GetSelectPicPath(),
						drawView[AnswerPaperFun.TOOL_TESTPAPER].dvBitmap,
						drawView[AnswerPaperFun.TOOL_TESTPAPER].dvSize);
					}
					
				} catch (Exception e1) {
					Toast.makeText(parentContext, "cannot find picture",
					Toast.LENGTH_SHORT).show();
					return;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	* 選擇WebView中的圖片, 同上但是傳入的是圖片路徑
	* 
	* @param imgSrcPath
	*            圖片路徑
	* @param bundleData
	*            調用者信息
	* @param drawView
	*            答題介面
	* @param bitmap_id
	*            圖片 id
	*/
	private static void SetSelectBmpFromPath(Context context, String imgSrcPath,
	final ReturnGetStartBundleData bundleData, DrawView[] drawView,
	String bitmap_id) {
		if (parentContext != null) {
			WindowManager wm = (WindowManager) parentContext
			.getSystemService(Context.WINDOW_SERVICE);
			int screen_width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			int screen_height = wm.getDefaultDisplay().getHeight();// 屏幕高度

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			imgSrcPath = imgSrcPath.substring(imgSrcPath.indexOf("src=\"") + 5);
			BitmapFactory.decodeFile(imgSrcPath, opt);

			final int minSideLength = Math.min(screen_width, screen_height);
			opt.inSampleSize = DrawViewBitmap.computeSampleSize(opt,
			minSideLength, screen_width * screen_height);
			opt.inJustDecodeBounds = false;
			opt.inInputShareable = true;
			opt.inPurgeable = true;
			try {
				Select_Bmp = BitmapFactory.decodeFile(imgSrcPath, opt);
				if (null == Select_Bmp) {
					Toast.makeText(context, "cannot find picture",
					Toast.LENGTH_SHORT).show();
					return;
				}
				String save_path = bundleData.absolute_path
				+ bundleData.answer_id + File.separator + bitmap_id
				+ ".png";
				SavePrtBitmap(Select_Bmp, save_path, false);

				bundleData.c1 = new MyWebViewCaller().new Callee();
				bundleData.c1.SetSelectPic(Select_Bmp, save_path,
				drawView[AnswerPaperFun.TOOL_WRITEPAPER]);
				bundleData.caller.SetI(bundleData.c1);
				if (drawView[AnswerPaperFun.TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
					drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSelectPic
					.SetSelectPicIntoView(
					bundleData.caller.GetSelectPic(),
					bundleData.caller.GetSelectPicPath(),
					drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvBitmap,
					drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSize);
					bundleData.caller.RefreshMyView();
				} else if (drawView[AnswerPaperFun.TOOL_TESTPAPER]
						.getVisibility() == View.VISIBLE) {
					drawView[AnswerPaperFun.TOOL_TESTPAPER].dvSelectPic
					.SetSelectPicIntoView(
					bundleData.caller.GetSelectPic(),
					bundleData.caller.GetSelectPicPath(),
					drawView[AnswerPaperFun.TOOL_TESTPAPER].dvBitmap,
					drawView[AnswerPaperFun.TOOL_TESTPAPER].dvSize);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap GetSelectBmp() {
		return Select_Bmp;
	}

	private static String FindImageBuffer(String id, String subString,
	int offset) {
		String strRet = new String("");
		int[] ioffset = new int[2];

		ioffset[0] = buffer.indexOf(subString, buffer.indexOf(id, offset))
		+ img_head.length();
		ioffset[1] = buffer.indexOf(img_end, ioffset[0]);

		if (ioffset[0] < ioffset[1]) {
			strRet = buffer.substring(ioffset[0], ioffset[1]);
		}

		return strRet;
	}

	public static boolean SavePrtBitmap(Bitmap src, String save_path,
	boolean isRecycle) {
		boolean bRet = false;
		File dirFile = new File(save_path.substring(0,
		save_path.lastIndexOf(File.separator) + 1));
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File tmpPicFile = new File(save_path);
		try {
			FileOutputStream fos = new FileOutputStream(tmpPicFile);
			src.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			if (isRecycle) {
				src.recycle();
				src = null;
			}
			bRet = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bRet;
	}
}
