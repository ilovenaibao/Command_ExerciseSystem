package com.besta.app.answerpaper.drawview;

import java.io.ByteArrayOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;

import com.besta.app.exerciseengine.R;

public class DrawViewBitmap {
	// answer question bmp
	public Bitmap question_bitmap = null;
	public int question_width = 0; // answer question bmp width
	public int question_height = 0; // answer question bmp height
	// 定義一個內存中的圖片，該圖片將作為緩衝區
	public Bitmap tmpDrawBitmap = null;
	public Bitmap cacheBitmap = null;
	// 實際紙張的bitmap
	public Bitmap drawLevelBitmap = null;
	// DrawView的背景圖片
	public Bitmap bgImgBitmap = null;
	// 移動的時候手勢的信息
	public Bitmap move_hand_bitmap = null;
	// 存儲的所有圖片信息
	public Bitmap firstBitmap = null;
	// 橫豎Scroll
	public Bitmap verticall_scroll = null;
	public Bitmap horizon_scroll = null;
	// 第一次繪製的時候效果圖
	public boolean cacheBitmapShow = false;
	public boolean penMoveDraw = false;

	// 定義 cacheBitmap 上的 Canvas 對象
	public Canvas cacheCanvas = null;
	public Canvas drawCanvas = null;

	// move answerpaper show move icon
	public float move_hand_x, move_hand_y;

	public void onDestroy() {
		RecyleALlBitmaps();
		// try {
		// finalize();
		// } catch (Throwable e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.gc();
	}

	// @Override
	// protected void finalize() throws Throwable {
	// if (question_bitmap != null && !question_bitmap.isRecycled()) {
	// question_bitmap.recycle();
	// question_bitmap = null;
	// }
	// if (tmpDrawBitmap != null && !tmpDrawBitmap.isRecycled()) {
	// tmpDrawBitmap.recycle();
	// tmpDrawBitmap = null;
	// }
	// if (cacheBitmap != null && !cacheBitmap.isRecycled()) {
	// cacheBitmap.recycle();
	// cacheBitmap = null;
	// }
	// if (drawLevelBitmap != null && !drawLevelBitmap.isRecycled()) {
	// drawLevelBitmap.recycle();
	// drawLevelBitmap = null;
	// }
	// if (bgImgBitmap != null && !bgImgBitmap.isRecycled()) {
	// bgImgBitmap.recycle();
	// bgImgBitmap = null;
	// }
	// if (move_hand_bitmap != null && !move_hand_bitmap.isRecycled()) {
	// move_hand_bitmap.recycle();
	// move_hand_bitmap = null;
	// }
	// if (firstBitmap != null && !firstBitmap.isRecycled()) {
	// firstBitmap.recycle();
	// firstBitmap = null;
	// }
	// if (verticall_scroll != null && !verticall_scroll.isRecycled()) {
	// verticall_scroll.recycle();
	// verticall_scroll = null;
	// }
	// if (horizon_scroll != null && !horizon_scroll.isRecycled()) {
	// horizon_scroll.recycle();
	// horizon_scroll = null;
	// }
	//
	// question_bitmap = null;
	// tmpDrawBitmap = null;
	// cacheBitmap = null;
	// drawLevelBitmap = null;
	// bgImgBitmap = null;
	// move_hand_bitmap = null;
	// firstBitmap = null;
	// verticall_scroll = null;
	// horizon_scroll = null;
	// super.finalize();
	// }

	public DrawViewBitmap(Context context, int width, int height,
			boolean createMemeryFlag) {
		move_hand_x = move_hand_y = 0;
		cacheBitmapShow = false;
		penMoveDraw = false;
	}

	public void createMoveHandBmp(Context context) {
		move_hand_bitmap = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.move_hand)).getBitmap();
	}

	public void createDrawLevelBmp(Context context, int width, int height) {
		drawLevelBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(drawLevelBitmap);
	}

	public void createTmpDrawBmp(Context context, int width, int height) {
		tmpDrawBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		cacheCanvas = new Canvas(tmpDrawBitmap);
	}

	public void createAllBitmap(Context context, int width, int height) {
		createMoveHandBmp(context);
		createDrawLevelBmp(context, width, height);
		createTmpDrawBmp(context, width, height);
	}

	/**
	 * 初始化DrawView所有Bitmap信息
	 * 
	 * @param context
	 * @param width
	 *            根據DrawView寬高屬性來初始化bitmap信息
	 * @param height
	 */
	public DrawViewBitmap(Context context, int width, int height) {
		move_hand_x = move_hand_y = 0;
		move_hand_bitmap = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.move_hand)).getBitmap();

		cacheBitmapShow = false;
		penMoveDraw = false;
		drawLevelBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(drawLevelBitmap);
		tmpDrawBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		cacheCanvas = new Canvas(tmpDrawBitmap);
	}

	public void RecyleALlBitmaps() {
		if (tmpDrawBitmap != null) {
			tmpDrawBitmap.recycle();
			tmpDrawBitmap = null;
		}
		if (cacheBitmap != null) {
			cacheBitmap.recycle();
			cacheBitmap = null;
		}
		if (drawLevelBitmap != null) {
			// 實際紙張的bitmap
			drawLevelBitmap.recycle();
			drawLevelBitmap = null;
		}
		if (bgImgBitmap != null) {
			// DrawView的背景圖片
			bgImgBitmap.recycle();
			bgImgBitmap = null;
		}
		if (move_hand_bitmap != null) {
			// 移動的時候手勢的信息
			move_hand_bitmap.recycle();
			move_hand_bitmap = null;
		}
		if (firstBitmap != null) {
			// 存儲的所有圖片信息
			firstBitmap.recycle();
			firstBitmap = null;
		}
		if (verticall_scroll != null) {
			// 橫豎Scroll
			verticall_scroll.recycle();
			verticall_scroll = null;
		}
		if (horizon_scroll != null) {
			horizon_scroll.recycle();
			horizon_scroll = null;
		}
	}

	/**
	 * 清除Bitmap的信息
	 * 
	 * @param src
	 *            返回一張null的bitmap
	 * @return
	 */
	public static Bitmap ClearBitmapToNull(Bitmap src) {
		if (src != null) {
			src.recycle();
			src = null;
		}

		return src;
	}

	/**
	 * 獲取Bitmap的所有像素
	 * 
	 * @param bitmap
	 *            目標bitmap
	 * @return 所有像素集合
	 */
	public static int[] GetBitmapPixels(Bitmap bitmap) {
		int[] pixels = new int[bitmap.getByteCount()];
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

		return pixels;
	}

	/**
	 * 將Bitmap轉換成Base64編碼的字串
	 * 
	 * @param bitmap
	 *            目標bitmap
	 * @return 0表示成功
	 */
	public static int bitmaptoInt(Bitmap bitmap) {
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return 0;
	}

	/**
	 * 將Base64編碼的字串轉換成bitmap類
	 * 
	 * @param string
	 *            Base64編碼的buffer
	 * @return 轉換成的Bitmap
	 */
	public Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 將Bitmap轉換成Base64編碼的字串
	 * 
	 * @param bitmap
	 *            目標bitmap
	 * @return 轉換成的String
	 */
	public static String bitmaptoString(Bitmap bitmap) {
		// 将Bitmap转换成字符串
		String string = null;
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, bStream);
		byte[] bytes = bStream.toByteArray();
		string = Base64.encodeToString(bytes, Base64.DEFAULT);
		return string;
	}

	/**
	 * 加載一張bitmap
	 * 
	 * @param src_path
	 *            路徑
	 * @param sx
	 *            View起始位置
	 * @param sy
	 * @param ex
	 *            寬度、高度
	 * @param ey
	 * @return 返回一個解析過的Bitmap類
	 */
	public static Bitmap LoadOneBitmap(String src_path, float sx, float sy,
			float ex, float ey) {
		Bitmap tmpLoadBmp = null;
		int width = (int) Math.abs(ex - sx);
		int height = (int) Math.abs(ey - sy);

		tmpLoadBmp = BitmapFactory.decodeFile(src_path);
		if (tmpLoadBmp != null) {
			tmpLoadBmp = MakeMtrixPic(tmpLoadBmp, width, height);
		}

		return tmpLoadBmp;
	}

	/**
	 * 將bitmap按指定width\height比例縮小
	 * 
	 * @param bitmap
	 *            目標bitmap
	 * @param w
	 *            指定寬高
	 * @param h
	 * @return 返回一個新的bitmap
	 */
	public static Bitmap MakeMtrixPic(Bitmap bitmap, int w, int h) {
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

	/**
	 * 計算按固定牡蠣縮小的屬性值
	 * 
	 * @param options
	 *            BitmapFactory類
	 * @param minSideLength
	 *            最小寬度
	 * @param maxNumOfPixels
	 *            最大像素
	 * @return
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * 初始化按比例縮小圖片的範圍
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 設置bitmap背景顏色
	 * 
	 * @param context
	 * @param width
	 * @param height
	 */
	public void SetBackGroundColor(Context context, int width, int height) {
		bgImgBitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		bgImgBitmap.eraseColor(0x90A0A0A0);
	}

	/**
	 * 設置背景圖片
	 * 
	 * @param src_name
	 *            圖片的路徑
	 * @param dvSize
	 *            圖片的大小
	 */
	public void SetBackGroundBitmap(String src_name, DrawViewSize dvSize) {
		int tmp_x = 0;
		int tmp_y = 0;
		question_bitmap = BitmapFactory.decodeFile(src_name);
		question_width = question_bitmap.getWidth();
		question_height = question_bitmap.getHeight();
		if (question_width < dvSize.screen_width) {
			tmp_x = question_width;
		} else {
			tmp_x = dvSize.screen_width;
		}
		if (question_height < dvSize.screen_height) {
			tmp_y = question_height;
		} else {
			tmp_y = dvSize.screen_height;
		}
		bgImgBitmap = Bitmap.createBitmap(question_bitmap, (int) dvSize.now_x,
				(int) dvSize.now_y, tmp_x, tmp_y);
	}
}
