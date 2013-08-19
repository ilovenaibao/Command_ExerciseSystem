package com.besta.app.answerpaper.drawview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.besta.app.answerpaper.othersclassinfo.MyScroll;
import com.besta.app.answerpaper.othersclassinfo.OneLineInfo;
import com.besta.app.answerpaper.othersclassinfo.PaintPos;
import com.besta.app.answerpaper.redrawpng.MyPngEncode;
import com.besta.app.exerciseengine.R;
import com.besta.app.geometry.Geometry;
import com.besta.app.testcallactivity.StartAnswerPaperSetting;
import com.besta.app.toolswindow.MyToolsWindow;

public class DrawView extends View {
	// Debug log
	private String DebugLog = "DrawView->";
	private Context parentContext = null;
	// 当前坐标
	float preX, preY;
	// 是否touch down操作(判定用於在draw時超出邊界返回邊界的問題)
	private boolean touch_down = false;
	// 当前使用的Pen属性
	public DrawViewPen dvPen = null;
	// 当前使用的橡皮属性
	public DrawViewEraser dvEraser = null;
	// 当前View的宽高属性
	public DrawViewSize dvSize = null;
	// 当前View所用到的所有Bitmap
	public DrawViewBitmap dvBitmap = null;
	// 存储的Data
	public DrawViewSaveData dvSaveData = null;
	// Touch动作类
	public DrawViewTouchMove dvTouchMove = null;
	// 几何图形类
	public DrawViewGeometry dvGeometry = null;
	// 反馈调用者的数据集合类
	public DrawViewReturnData dvRetData = null;
	// 从WebView拖拽的图片集合类
	public DrawViewSelectPic dvSelectPic = null;
	// 开始的绝对坐标以及相对坐标的偏移量
	private float start_x, start_y, diff_value_x, diff_value_y;

	// 設定工具欄焦點
	private int tools_focus = 0;
	// 最大劃過的座標
	private Point endPos = null;
	// 刷新的矩形區域
	private Rect reFresh_rect = new Rect();
	// 存儲的絕對路徑
	private String saveAbsolutePath = null;
	// 存儲的絕對名稱
	private String saveAbsoluteName = null;
	// 是否顯示答題水印
	private boolean isDrawWaterMark = false;

	public boolean isFinishFlag = false;

	/**
	* 初始化DrawView
	* 
	* @param context
	*/
	public DrawView(Context context) {

		super(context);
		parentContext = context;
		// 設置橡皮
		dvEraser = new DrawViewEraser();
		// 設置View尺寸
		dvSize = new DrawViewSize();
		// 設置存儲數據
		dvSaveData = new DrawViewSaveData();
		// 設置Touch事件
		dvTouchMove = new DrawViewTouchMove(parentContext);
		// 設置幾何圖形
		dvGeometry = new DrawViewGeometry();
		// 返回數據
		dvRetData = new DrawViewReturnData();
		// 選中的導入圖片
		dvSelectPic = new DrawViewSelectPic();

		isFinishFlag = false;
	}

	/**
	* 附带属性的初始化DrawView
	* 
	* @param context
	* @param set
	*/
	public DrawView(Context context, AttributeSet set) {
		super(context, set);
		parentContext = context;
		// 設置橡皮
		dvEraser = new DrawViewEraser();
		// 設置View尺寸
		dvSize = new DrawViewSize();
		// 設置存儲數據
		dvSaveData = new DrawViewSaveData();
		// 設置Touch事件
		dvTouchMove = new DrawViewTouchMove(parentContext);
		// 設置幾何圖形
		dvGeometry = new DrawViewGeometry();
		// 設置存儲地址信息
		saveAbsolutePath = new String("");
		saveAbsoluteName = new String("");
	}

	/**
	* 设置存储的绝对路径以及ID号
	* 
	* @param path
	*            存儲路徑
	* @param name
	*            存儲文件名
	*/
	public void SetSaveInfo(String path, String name) {
		saveAbsolutePath = path;
		saveAbsoluteName = name;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = dvSize.resetHeight;
		int measuredWidth = dvSize.resetWidth;
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMinimumWidth(measuredWidth);
		setMinimumHeight(measuredHeight);
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	/**
	* 设置当前工具的焦点
	* 
	* @param focus
	*            參考AnswerPaperFun.java定義的工具類型
	*/
	public void SetToolsFocus(int focus) {
		tools_focus = focus;
	}

	/**
	* 设置当前实际纸张的大小
	* 
	* @param width
	*            設置寬高屬性
	* @param height
	* @param padding_x
	*            設置距離DrawView左上角的padding值
	* @param padding_y
	*/
	public void SetRealWidthHeight(int width, int height, int padding_x,
	int padding_y) {

		if (null != dvSize) {
			dvSize.SetRealSize(width, height, padding_x, padding_y);
		}
		// 設置顯示的bitmap
		// dvBitmap = new DrawViewBitmap(parentContext, dvSize.VIEW_WIDTH,
		// dvSize.VIEW_HEIGHT);
		if (dvBitmap != null) {
			dvBitmap.RecyleALlBitmaps();
			dvBitmap = null;
		}
		dvBitmap = new DrawViewBitmap(parentContext, dvSize.VIEW_WIDTH,
		dvSize.VIEW_HEIGHT);
		endPos = new Point(0, 0);
		start_x = start_y = diff_value_x = diff_value_y = 0;
		dvPen = new DrawViewPen();
	}

	/**
	* 切换Pen & Eraser
	* 
	* @param isPenOrEraser
	*            是否是Pen，如果是Pen為true
	*/
	public void setPenOrEraserStyle(boolean isPenOrEraser) {
		if (!isFinishFlag) {
			dvPen.setDrawViewPenOrEraserStyle(this, isPenOrEraser, dvPen,
			dvEraser, dvSaveData);
			invalidate();
		}
	}

	/**
	* 设置切换之后的Pen或者Eraser的宽度
	* 
	* @param width
	*            設置寬度
	* @param pen_kind
	*            設置Pen的類型是畫筆還是橡皮
	*/
	public void SetPenWidth(int width, int pen_kind) {
		switch (pen_kind) {
		case MyToolsWindow.TOOL_BAOPEN:
			dvPen.myPenColor = DrawViewPen.BaoPenColor;
			break;
		case MyToolsWindow.TOOL_PEN:
			dvPen.myPenColor = Color.BLACK;
			break;
		}

		dvPen.paint.setColor(dvPen.myPenColor);
		dvPen.paint.setStrokeWidth(dvPen.myPenWidth);
		dvPen.paint.setStrokeJoin(Paint.Join.ROUND);
		dvPen.paint.setStrokeCap(Paint.Cap.ROUND);
		// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
	}

	/**
	* 设置DrawView的背景图片
	* 
	* @param src_name
	*            目標bitmap
	*/
	public void SetDraViewBackGroundBitmap(String src_name) {

		File file = new File(src_name);
		if (file.exists()) {
			dvBitmap.SetBackGroundBitmap(src_name, dvSize);
			invalidate();
		} else {
			Toast.makeText(parentContext, "cannot open this picture",
			Toast.LENGTH_SHORT).show();
		}
	}

	/**
	* 设置背景的大小
	*/
	public void ResetBackGroundSize() {
		int tmp_x = 0;
		int tmp_y = 0;

		if (diff_value_x < 0) {
			dvSize.now_x = -diff_value_x;
			start_x = 0;
			tmp_x = (int) (dvSize.screen_width - dvSize.now_x);
			if (tmp_x > dvBitmap.question_width) {
				tmp_x = dvBitmap.question_width;
			}
		} else {
			dvSize.now_x = 0;
			start_x = diff_value_x;
			tmp_x = (int) (dvBitmap.question_width - start_x);
			if (tmp_x > dvSize.screen_width) {
				tmp_x = dvSize.screen_width;
			}
		}
		if (diff_value_y < 0) {
			dvSize.now_y = -diff_value_y;
			start_y = 0;
			tmp_y = (int) (dvSize.screen_height - dvSize.now_y);
			if (tmp_y > dvBitmap.question_height) {
				tmp_y = dvBitmap.question_height;
			}
		} else {
			dvSize.now_y = 0;
			start_y = diff_value_y;
			tmp_y = (int) (dvBitmap.question_height - start_y);
			if (tmp_y > dvSize.screen_height) {
				tmp_y = dvSize.screen_height;
			}
		}

		if (tmp_x > 0 && tmp_x <= dvBitmap.question_width && tmp_y > 0
				&& tmp_y <= dvBitmap.question_height) {
			dvBitmap.bgImgBitmap = Bitmap.createBitmap(
			dvBitmap.question_bitmap, (int) start_x, (int) start_y,
			tmp_x, tmp_y);
		} else {
			dvBitmap.bgImgBitmap = null;
		}
	}

	/**
	* 检查存储的绝对路径命名是否符合规范, 如果不规范将特殊字符替换为“_”
	* 
	* @param absolute_path
	*            存儲的絕對路徑
	* @param save_name
	*            存儲的題目ID
	* @return 存儲的絕對路徑
	*/
	public String CheckSavePicName(String absolute_path, String save_name) {
		String sRet = new String(absolute_path + save_name);
		int count = 1;
		for (count = 1;; count++) {
			File tmpFile = new File(sRet);
			if (tmpFile.exists()) {
				sRet = absolute_path + count + "_" + save_name;
			} else {
				break;
			}
		}

		return sRet;
	}

	/**
	* 将从WebView中获得的Pic存储到sdcard
	* 
	* @param path
	*            存儲的路徑
	* @param fileName
	*            存儲的文件名
	* @param srcBmp
	*            存儲的目標bitmap
	* @param width
	*            在反饋給調用者的時候返回的html片段設置的圖片的寬高
	* @param height
	*/
	public void SaveResultPicBuffer(String path, String fileName,
	Bitmap srcBmp, int width, int height, int now_count) {
		String tmp_name = new String(path + fileName);
		File tmp_file = new File(tmp_name);
		if (tmp_file.exists()) {
			tmp_file.delete();
		}
		File dir = new File(path);
		dir.mkdirs();
		try {
			FileOutputStream out = new FileOutputStream(tmp_file);
			srcBmp.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.flush();
			out.close();
			dvRetData.SetOneRetHtmlImg(tmp_name, now_count, height);
			dvRetData.SetRetHtmlBufferHeadEnd(true);
			dvRetData.WriteRetHtmlBufferOnDisk(path + "return_data.html");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	* 存储最终生成的实际大小的图片数据
	* 
	* @param absolute_path
	*            存儲的絕對路徑
	* @param save_name
	*            存儲的題目ID
	* @param exitFlag
	*            是否在存儲完成之後直接退出程序
	* @throws FileNotFoundException
	*/
	public void SaveCurrenPaperMatrixPic(String absolute_path,
	String save_name, boolean exitFlag) throws FileNotFoundException {
		// 清理bitmap---> 初始化
		// 获取屏幕的实际宽高
		WindowManager wm = (WindowManager) parentContext
		.getSystemService(Context.WINDOW_SERVICE);
		int screen_width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		int screen_height = wm.getDefaultDisplay().getHeight();// 屏幕高度
		String name = new String(absolute_path + save_name);
		File file = new File(name);
		if (file.exists()) {
			file.delete();
		}
		name = new String(absolute_path + StartAnswerPaperSetting.resultPngData);
		file = new File(name);
		if (file.exists()) {
			file.delete();
		}

		// 重新计算纸张的实际使用范围(进行以后的截图)
		RefreshMinMaxPos();
		// 获得实际的使用宽高
		int tmpWidth = dvSaveData.max_x - dvSaveData.min_x;
		int tmpHeight = dvSaveData.max_y - dvSaveData.min_y;
		// 屏幕与实际宽高的比例
		float ratio = 0;
		float ratio2 = 0;
		// 缩放之后的宽高
		int matrix_width = 250;
		int matrix_height = 0;
		// 临时生成的bmp
		Bitmap bmp = null;
		// 是否超出一屏
		boolean isOtherFild = false;
		// 是否進行縮放
		boolean matrixFlag = false;
		// 實際的圖片寬高
		int realBmpWith, realBmpHeight;
		// 如果图片宽高 < 1 则放弃操作
		if (tmpWidth < 1 || tmpHeight < 1) {
			dvRetData.return_save_answer_png = "NULL";
			return;
		}

		realBmpWith = tmpWidth;
		realBmpHeight = tmpHeight;

		// add by Taylor

		// 新的寫入圖片方式
		// 寫入png head
		MyPngEncode pngEncode = new MyPngEncode(MyPngEncode.ENCODE_ALPHA, 0, 9,
		realBmpWith, realBmpHeight);
		String savePngPath = absolute_path
		+ StartAnswerPaperSetting.resultPngData;
		File tmpFile = new File(absolute_path);
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		tmpFile = new File(savePngPath);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}
		RandomAccessFile rf_2 = new RandomAccessFile(absolute_path
		+ StartAnswerPaperSetting.resultPngData, "rw");
		// 写入png 图片IHDR(head) 信息
		SaveOneAnswerPic_Start(rf_2, absolute_path,
		StartAnswerPaperSetting.resultPngData, pngEncode);

		Deflater scrunch = null; // 壓縮等級
		ByteArrayOutputStream outBytes = null; // 壓縮OutPutStream流
		DeflaterOutputStream compBytes = null; // 壓縮流
		int compressionLevel = 9;
		scrunch = new Deflater(compressionLevel);
		outBytes = new ByteArrayOutputStream(1024);
		compBytes = new DeflaterOutputStream(outBytes, scrunch);

		// add end
		if (null != dvSize && null != dvSaveData && null != dvBitmap) {
			if ((tmpWidth < dvSize.VIEW_WIDTH)
					&& (tmpHeight < dvSize.VIEW_HEIGHT)) {
				ratio = (float) ((float) tmpHeight / (float) tmpWidth);
				matrix_height = (int) ((float) matrix_width * (ratio));
				// 未超出DrawView的宽高截图
				bmp = Bitmap.createBitmap(tmpWidth, tmpHeight,
				Bitmap.Config.ARGB_8888);
				if (bmp != null) {
					Canvas tmpCanvas = new Canvas(bmp);
					ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
					dvSaveData.min_y);
					// dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
					if (matrixFlag) {
						dvBitmap.firstBitmap = DrawViewBitmap.MakeMtrixPic(bmp,
						matrix_width, matrix_height);
					} else {
						dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
					}
					int bmpWidth = dvBitmap.firstBitmap.getWidth();
					int bmpHeight = dvBitmap.firstBitmap.getHeight();
					int[] tmp_pixles = new int[bmpWidth * bmpHeight];
					dvBitmap.firstBitmap.getPixels(tmp_pixles, 0, bmpWidth, 0,
					0, bmpWidth, bmpHeight);
					// 存儲 png data
					SaveOneAnswerPic_Mid(rf_2, tmp_pixles, bmpWidth, bmpHeight,
					pngEncode, scrunch, outBytes, compBytes, true);
				}
				// 釋放用過的 bitmap
				if (dvBitmap.firstBitmap != null
						&& !dvBitmap.firstBitmap.isRecycled()) {
					dvBitmap.firstBitmap.recycle();
				}
				if (bmp != null && !bmp.isRecycled()) {
					bmp.isRecycled();
				}
			} else if ((tmpWidth < dvSize.VIEW_WIDTH)
					&& (tmpHeight < screen_height)) {
				ratio = (float) ((float) screen_height / (float) tmpWidth);
				matrix_height = (int) ((float) matrix_width * (ratio));
				// 未超出一屏的时候截图
				bmp = Bitmap.createBitmap(tmpWidth, tmpHeight,
				Bitmap.Config.ARGB_8888);
				if (bmp != null) {
					Canvas tmpCanvas = new Canvas(bmp);
					ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
					dvSaveData.min_y);
					// 是否縮放
					if (matrixFlag) {
						dvBitmap.firstBitmap = DrawViewBitmap.MakeMtrixPic(bmp,
						matrix_width, matrix_height);
					} else {
						dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
					}
					int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成png的寬
					int bmpHeight = dvBitmap.firstBitmap.getHeight(); // 生成png的高
					// 創建像素組
					int[] tmp_pixles = new int[bmpWidth * bmpHeight];
					// 獲取像素組
					dvBitmap.firstBitmap.getPixels(tmp_pixles, 0, bmpWidth, 0,
					0, bmpWidth, bmpHeight);
					// 寫入png IDAT 數據塊
					SaveOneAnswerPic_Mid(rf_2, tmp_pixles, bmpWidth, bmpHeight,
					pngEncode, scrunch, outBytes, compBytes, true);
				}
				// 釋放用過的 bitmap
				if (dvBitmap.firstBitmap != null
						&& !dvBitmap.firstBitmap.isRecycled()) {
					dvBitmap.firstBitmap.recycle();
				}
				if (bmp != null && !bmp.isRecycled()) {
					bmp.isRecycled();
				}
			} else {
				// 超出一屏的时候的截图
				isOtherFild = true;
				ratio = (float) ((float) screen_height / (float) tmpWidth);
				matrix_height = (int) ((float) matrix_width * (ratio));
				int tmp_h = 0;
				for (int i = 0, j = 1; i < tmpHeight; j++) {
					tmp_h = tmpHeight - i;
					if (tmp_h > 0) {
						if (tmp_h > screen_height) {
							bmp = Bitmap.createBitmap(tmpWidth, screen_height,
							Bitmap.Config.ARGB_8888);
							if (bmp != null) {
								Canvas tmpCanvas = new Canvas(bmp);
								ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
								dvSaveData.min_y + i);
								// 是否縮放
								if (matrixFlag) {
									dvBitmap.firstBitmap = DrawViewBitmap
									.MakeMtrixPic(bmp, matrix_width,
									matrix_height);
								} else {
									dvBitmap.firstBitmap = Bitmap
									.createBitmap(bmp);
								}
								int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成png寬
								int bmpHeight = dvBitmap.firstBitmap
								.getHeight(); // 生成png高
								// 創建像素組
								int[] tmp_pixles = new int[bmpWidth * bmpHeight];
								// 獲取像素組
								dvBitmap.firstBitmap.getPixels(tmp_pixles, 0,
								bmpWidth, 0, 0, bmpWidth, bmpHeight);
								// 寫入png 部份IDAT 數據塊
								SaveOneAnswerPic_Mid(rf_2, tmp_pixles,
								bmpWidth, bmpHeight, pngEncode,
								scrunch, outBytes, compBytes, false);
								/*
								* Debug png //
								* SaveResultPicBuffer(absolute_path, j +
								* ".png", // dvBitmap.firstBitmap, tmpWidth,
								* tmpHeight, // j);
								*/
							}
							i += screen_height;
						} else {
							ratio2 = (float) ((float) tmp_h / (float) tmpWidth);
							int height2 = (int) (matrix_width * (float) (ratio2));
							bmp = Bitmap.createBitmap(tmpWidth, tmp_h,
							Bitmap.Config.ARGB_8888);
							if (bmp != null) {
								Canvas tmpCanvas = new Canvas(bmp);
								ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
								dvSaveData.min_y + tmpHeight - tmp_h);

								if ((height2 <= 0) || (height2 > tmp_h)) {
									height2 = tmp_h;
								}
								// 是否缩放
								if (matrixFlag) {
									dvBitmap.firstBitmap = DrawViewBitmap
									.MakeMtrixPic(bmp, matrix_width,
									height2);
								} else {
									dvBitmap.firstBitmap = Bitmap
									.createBitmap(bmp);
								}

								int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成png宽
								int bmpHeight = dvBitmap.firstBitmap
								.getHeight(); // 生成png高
								// 创建像素组
								int[] tmp_pixles = new int[bmpWidth * bmpHeight];
								// 获取像素组
								dvBitmap.firstBitmap.getPixels(tmp_pixles, 0,
								bmpWidth, 0, 0, bmpWidth, bmpHeight);
								// 写入png IDAT部分数据块
								SaveOneAnswerPic_Mid(rf_2, tmp_pixles,
								bmpWidth, bmpHeight, pngEncode,
								scrunch, outBytes, compBytes, true);
								/*
								* Debug png //
								* SaveResultPicBuffer(absolute_path, j +
								* ".png", // dvBitmap.firstBitmap, tmpWidth,
								* tmpHeight, // j);
								*/
							}
							break;
						}
						// 釋放用過的 bitmap
						if (dvBitmap.firstBitmap != null
								&& !dvBitmap.firstBitmap.isRecycled()) {
							dvBitmap.firstBitmap.recycle();
						}
						if (bmp != null && !bmp.isRecycled()) {
							bmp.isRecycled();
						}
					}
				}
			}
			dvBitmap.RecyleALlBitmaps();

			// add by Taylor
			SaveOneAnswerPic_End(rf_2, pngEncode);
			dvRetData.return_save_answer_png = absolute_path
			+ StartAnswerPaperSetting.resultPngData;
			// add end
			isFinishFlag = true;
		}
	}

	/**
	* 存储最终生成的实际大小的图片数据
	* 
	* @param absolute_path
	*            存儲的絕對路徑
	* @param save_name
	*            存儲的題目ID
	* @param exitFlag
	*            是否在存儲完成之後直接退出程序
	* @throws FileNotFoundException
	*/
	public void SaveCurrenPaperMatrixPic_Old(String absolute_path,
	String save_name, boolean exitFlag) throws FileNotFoundException {
		// Debug.startMethodTracing("Saving_Pic_Tracing");

		// add by Taylor
		// 創建待寫入的所有像素點文件
		RandomAccessFile rf = new RandomAccessFile(
		StartAnswerPaperSetting.resultPngDataTmpPixlesPath, "rw");
		// add end

		// 清理bitmap---> 初始化
		// dvBitmap.firstBitmap = DrawViewBitmap
		// .ClearBitmapToNull(dvBitmap.firstBitmap);
		// 获取屏幕的实际宽高
		WindowManager wm = (WindowManager) parentContext
		.getSystemService(Context.WINDOW_SERVICE);
		int screen_width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		int screen_height = wm.getDefaultDisplay().getHeight();// 屏幕高度
		String name = new String(absolute_path + save_name);
		File file = new File(name);
		if (file.exists()) {
			file.delete();
		}
		// 重新计算纸张的实际使用范围(进行以后的截图)
		RefreshMinMaxPos();
		// 获得实际的使用宽高
		int tmpWidth = dvSaveData.max_x - dvSaveData.min_x;
		int tmpHeight = dvSaveData.max_y - dvSaveData.min_y;
		// 屏幕与实际宽高的比例
		float ratio = 0;
		float ratio2 = 0;
		// 缩放之后的宽高
		int matrix_width = 250;
		int matrix_height = 0;
		// 临时生成的bmp
		Bitmap bmp = null;
		// 是否超出一屏
		boolean isOtherFild = false;
		// 是否進行縮放
		boolean matrixFlag = false;
		// 實際的圖片寬高
		int realBmpWith, realBmpHeight;
		// 如果图片宽高 < 1 则放弃操作
		if (tmpWidth < 1 || tmpHeight < 1) {
			dvRetData.return_save_answer_png = "NULL";
			return;
		}

		realBmpWith = tmpWidth; // 實際bmp寬
		realBmpHeight = tmpHeight; // 實際bmp高

		if ((tmpWidth < dvSize.VIEW_WIDTH) && (tmpHeight < dvSize.VIEW_HEIGHT)) {
			// 当实际图片未超过DrawView時生成png图片
			// bmp 寬高比例
			ratio = (float) ((float) tmpHeight / (float) tmpWidth);
			// bmp 縮放比例
			matrix_height = (int) ((float) matrix_width * (ratio));
			// 未超出DrawView的宽高截图
			bmp = Bitmap.createBitmap(tmpWidth, tmpHeight,
			Bitmap.Config.ARGB_8888);
			if (bmp != null) {
				// 生成圖片
				Canvas tmpCanvas = new Canvas(bmp);
				ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
				dvSaveData.min_y);
				// 是否需要進行圖片縮放
				if (matrixFlag) {
					dvBitmap.firstBitmap = DrawViewBitmap.MakeMtrixPic(bmp,
					matrix_width, matrix_height);
				} else {
					dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
				}
				int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成圖片寬
				int bmpHeight = dvBitmap.firstBitmap.getHeight(); // 生成圖片高
				// 創建所有像素點集合
				int[] tmp_pixles = new int[bmpWidth * bmpHeight];
				// 獲取當前所有像素點
				dvBitmap.firstBitmap.getPixels(tmp_pixles, 0, bmpWidth, 0, 0,
				bmpWidth, bmpHeight);
				try {
					// 生成圖片
					writeBmpBufferInFile(tmp_pixles, rf);
					rf.close();
				} catch (Exception e) {
				}

				/*
				* Debug 存儲當前生成的png圖片 // SaveResultPicBuffer(absolute_path,
				* "1.png", // dvBitmap.firstBitmap, tmpWidth, tmpHeight, 1);
				*/
			}
			// 回收使用過的bitmap
			if (dvBitmap.firstBitmap != null
					&& !dvBitmap.firstBitmap.isRecycled()) {
				dvBitmap.firstBitmap.recycle();
			}
			if (bmp != null && !bmp.isRecycled()) {
				bmp.isRecycled();
			}
		} else if ((tmpWidth < dvSize.VIEW_WIDTH)
				&& (tmpHeight < screen_height)) {
			// 当实际图片超过DrawView但未超过机器屏幕生成png图片
			// 當前機器的寬高比例
			ratio = (float) ((float) screen_height / (float) tmpWidth);
			// 縮放比例
			matrix_height = (int) ((float) matrix_width * (ratio));
			// 未超出一屏的时候截图
			bmp = Bitmap.createBitmap(tmpWidth, tmpHeight,
			Bitmap.Config.ARGB_8888);
			if (bmp != null) {
				// 生成png 圖片bitmap
				Canvas tmpCanvas = new Canvas(bmp);
				ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
				dvSaveData.min_y);
				// 是否進行縮放
				if (matrixFlag) {
					dvBitmap.firstBitmap = DrawViewBitmap.MakeMtrixPic(bmp,
					matrix_width, matrix_height);
				} else {
					dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
				}
				int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成png寬
				int bmpHeight = dvBitmap.firstBitmap.getHeight(); // 生成png高
				// 創建像素點集合
				int[] tmp_pixles = new int[bmpWidth * bmpHeight];
				// 獲取所有像素
				dvBitmap.firstBitmap.getPixels(tmp_pixles, 0, bmpWidth, 0, 0,
				bmpWidth, bmpHeight);
				try {
					// 生成png 圖片
					writeBmpBufferInFile(tmp_pixles, rf);
					rf.close();
				} catch (Exception e) {
				}

				/*
				* Debug 生成當前的png圖片 // SaveResultPicBuffer(absolute_path,
				* "1.png", // dvBitmap.firstBitmap, tmpWidth, tmpHeight, 1);
				*/
			}
			// 回收用過的bitmap
			if (dvBitmap.firstBitmap != null
					&& !dvBitmap.firstBitmap.isRecycled()) {
				dvBitmap.firstBitmap.recycle();
			}
			if (bmp != null && !bmp.isRecycled()) {
				bmp.isRecycled();
			}
		} else {
			// 超出一屏的时候的png圖片生成方式
			isOtherFild = true; // 是否超出一個屏幕
			// 實際圖片的寬高比例
			ratio = (float) ((float) screen_height / (float) tmpWidth);
			// 縮放比例
			matrix_height = (int) ((float) matrix_width * (ratio));
			int tmp_h = 0; // 當前還未生成png圖片的高度
			for (int i = 0, j = 1; i < tmpHeight; j++) {
				tmp_h = tmpHeight - i;
				if (tmp_h > 0) {
					if (tmp_h > screen_height) {
						// 生成png圖片bitmap
						bmp = Bitmap.createBitmap(tmpWidth, screen_height,
						Bitmap.Config.ARGB_8888);
						if (bmp != null) {
							Canvas tmpCanvas = new Canvas(bmp);
							ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
							dvSaveData.min_y + i);
							// 是否縮放
							if (matrixFlag) {
								dvBitmap.firstBitmap = DrawViewBitmap
								.MakeMtrixPic(bmp, matrix_width,
								matrix_height);
							} else {
								dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
							}
							int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成的png寬
							int bmpHeight = dvBitmap.firstBitmap.getHeight(); // 生成的png高
							// 創建像素點集合
							int[] tmp_pixles = new int[bmpWidth * bmpHeight];
							dvBitmap.firstBitmap.getPixels(tmp_pixles, 0,
							bmpWidth, 0, 0, bmpWidth, bmpHeight);
							// 生成png圖片
							writeBmpBufferInFile(tmp_pixles, rf);

							/*
							* Debug 生成png // SaveResultPicBuffer(absolute_path,
							* j + ".png", // dvBitmap.firstBitmap, tmpWidth,
							* tmpHeight, // j);
							*/
						}
						i += screen_height;
					} else {
						// 剩下的bitmap縮放比例
						ratio2 = (float) ((float) tmp_h / (float) tmpWidth);
						int height2 = (int) (matrix_width * (float) (ratio2));
						// 生成png圖片bitmap
						bmp = Bitmap.createBitmap(tmpWidth, tmp_h,
						Bitmap.Config.ARGB_8888);
						if (bmp != null) {
							Canvas tmpCanvas = new Canvas(bmp);
							ReDrawAllPath(tmpCanvas, bmp, dvSaveData.min_x,
							dvSaveData.min_y + tmpHeight - tmp_h);

							if ((height2 <= 0) || (height2 > tmp_h)) {
								height2 = tmp_h;
							}
							// 是否縮放
							if (matrixFlag) {
								dvBitmap.firstBitmap = DrawViewBitmap
								.MakeMtrixPic(bmp, matrix_width,
								height2);
							} else {
								dvBitmap.firstBitmap = Bitmap.createBitmap(bmp);
							}

							int bmpWidth = dvBitmap.firstBitmap.getWidth(); // 生成的png寬
							int bmpHeight = dvBitmap.firstBitmap.getHeight(); // 生成的png高
							// 創建像素點集合
							int[] tmp_pixles = new int[bmpWidth * bmpHeight];
							// 獲取像素點
							dvBitmap.firstBitmap.getPixels(tmp_pixles, 0,
							bmpWidth, 0, 0, bmpWidth, bmpHeight);
							// 生成png圖片
							writeBmpBufferInFile(tmp_pixles, rf);

							/*
							* Debug png // SaveResultPicBuffer(absolute_path, j
							* + ".png", // dvBitmap.firstBitmap, tmpWidth,
							* tmpHeight, // j);
							*/
						}

						try {
							rf.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					}
					// 回收使用過的bitmap
					if (dvBitmap.firstBitmap != null
							&& !dvBitmap.firstBitmap.isRecycled()) {
						dvBitmap.firstBitmap.recycle();
					}
					if (bmp != null && !bmp.isRecycled()) {
						bmp.isRecycled();
					}
				}
			}
		}
		// 回收所有使用的bitmap釋放內存
		dvBitmap.RecyleALlBitmaps();

		// 獲取存儲結果
		dvRetData.return_save_answer_png = SaveOneAnswerPic(null, realBmpWith,
		realBmpHeight, absolute_path,
		StartAnswerPaperSetting.resultPngData);
		isFinishFlag = true;

		// Debug.stopMethodTracing();
	}

	/**
	* 寫 bitmap 像素組到 sdcard
	* 
	* @note: 此Method測試Debug使用
	* @param bmpBuffer
	*/
	private void writeBmpBufferInFile(int[] bmpBuffer, RandomAccessFile rf) {
		try {
			// 創建byte型數組, 每個像素點是帶 α通道的所以是像素點的4倍
			byte[] tmpBytes = new byte[bmpBuffer.length * 4];
			for (int i = 0, j = 0; i < bmpBuffer.length; i++) {
				// 當bmpBuffer 為0時, 直接寫入4個0
				if (0 == bmpBuffer[i]) {
					for (int k = 0; k < 4; k++) {
						tmpBytes[j] = 0;
						j++;
					}
					continue;
				}
				// 當此時像素點有顏色值的時候獲取當前像素點4bytes
				byte[] oneInt = MyPngEncode.getIntToByte4(bmpBuffer[i]);
				for (int k = 0; k < oneInt.length; k++) {
					tmpBytes[j] = oneInt[k];
					j++;
				}
				oneInt = null;
			}

			// 寫入所有像素點
			rf.write(tmpBytes);
			// rf.close();
			tmpBytes = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	* 早期版本使用的存儲答題信息的png圖片Method
	* 
	* @param all_pixles
	*            所有需要存儲的像素點(如果太多會OutOfMemerry)
	* @param width
	*            圖片的寬
	* @param height
	*            圖片的高
	* @param path
	*            存儲路徑
	* @param name
	*            存儲文件名
	* @return 存儲圖片的絕對路徑
	*/
	private String SaveOneAnswerPic(int[] all_pixles, int width, int height,
	String path, String name) {
		// 保存当前显示的png图像
		String path_png = path;
		String name_png = name;
		byte[] pngbytes;

		// 創建png結構類進行初始化帶 α通道的壓縮等級9, 位深度8, 灰度6(真彩)的png 圖片
		MyPngEncode pngEncode = new MyPngEncode(MyPngEncode.ENCODE_ALPHA, 0, 9,
		width, height);
		// 獲得待存png圖片的 IHDR (head)信息
		pngbytes = pngEncode.getMyPngHeaderBuffer();
		// 判斷當前創建的png圖片文件是否存在，如果存在刪除以前的
		File tmpFile = new File(path_png + name_png);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}
		try {
			RandomAccessFile rf = new RandomAccessFile(tmpFile, "rw");
			if (pngbytes == null) {
				System.out.println("Null image");
			} else {
				// 寫入當前png IHDR 頭文件塊
				rf.write(pngbytes);
				// pngbytes = pngEncode.WriteMySubImageDataTest(all_pixles, rf,
				// 1);
				// 寫入當前png IDAT 數據塊
				pngbytes = pngEncode.WriteMySubImageDataTest_2(all_pixles, rf,
				1);
				// 寫入當前png IEND 數據塊
				pngbytes = pngEncode.myWriteEnd();
				if (pngbytes != null) {
					rf.write(pngbytes);
				}
			}
			rf.close();
		} catch (IOException ee) {

			ee.printStackTrace();
		}
		// Destroy
		// pngEncode.onDestroy();
		return path_png + name_png;
	}

	/**
	* 存儲答題信息的png圖片head頭
	* 
	* @param rf
	*            存入的png文件
	* @param path
	*            存儲路徑
	* @param name
	*            存儲文件名
	* @param pngEncode
	*            PngEncode類
	*/
	private void SaveOneAnswerPic_Start(RandomAccessFile rf, String path,
	String name, MyPngEncode pngEncode) {
		// 保存当前显示的png图像
		String path_png = path;
		String name_png = name;
		byte[] pngbytes;

		pngbytes = pngEncode.getMyPngHeaderBuffer();
		try {
			if (pngbytes == null) {
				System.out.println("Null image");
			} else {
				rf.write(pngbytes);
			}
		} catch (IOException ee) {

			ee.printStackTrace();
		}

		pngbytes = null;
	}

	/**
	* 存儲答題信息png圖片的IDAT部份
	* 
	* @param rf
	*            png圖片文件
	* @param all_pixles
	*            當前存儲的像素點
	* @param width
	*            當前存儲IDAT的寬度(一般如果多次寫入IDAT則寬度必須一樣)
	* @param height
	*            當前存儲的IDAT的高度(同上)
	* @param pngEncode
	*            PngEncode類
	* @param scrunch
	*            壓縮等級設定
	* @param outBytes
	*            壓縮的outputStream流
	* @param compBytes
	*            壓縮數據流
	* @param isEndFlag
	*            是否結束
	*/
	private void SaveOneAnswerPic_Mid(RandomAccessFile rf, int[] all_pixles,
	int width, int height, MyPngEncode pngEncode, Deflater scrunch,
	ByteArrayOutputStream outBytes, DeflaterOutputStream compBytes,
	boolean isEndFlag) {
		try {
			pngEncode.WriteMySubImageDataTest_4(all_pixles, width, height, rf,
			scrunch, outBytes, compBytes, isEndFlag);
		} catch (IOException ee) {

			ee.printStackTrace();
		}
	}

	/**
	* 存儲的答題信息png圖片結束標誌
	* 
	* @param rf
	*            存儲的png文件
	* @param pngEncode
	*            PngEncode類
	*/
	private void SaveOneAnswerPic_End(RandomAccessFile rf, MyPngEncode pngEncode) {
		byte[] pngbytes;

		try {
			pngbytes = pngEncode.myWriteEnd();
			if (pngbytes != null) {
				rf.write(pngbytes);
				rf.close();
			}
		} catch (IOException ee) {

			ee.printStackTrace();
		}
	}

	private int[] copyArray(int[] dst, int[] src, int pos) {
		if (pos > dst.length || pos < 0) {
			return null;
		}
		for (int i = 0; pos < dst.length && i < src.length; i++, pos++) {
			dst[pos] = src[i];
		}
		return dst;
	}

	public void onDestroyView() {
		isFinishFlag = true;
		if (null != dvPen) {
			dvPen.onDestroy();
			dvPen = null;
		}
		if (null != dvEraser) {
			dvEraser.onDestroy();
			dvEraser = null;
		}
		// 当前View的宽高属性
		dvSize = null;
		if (null != dvBitmap) {
			dvBitmap.onDestroy();
			dvBitmap = null;
		}
	}

	/**
	* (未使用的函數)存儲答題信息到一張完整的png圖片中
	* 
	* @param absolute_path
	*            存儲的絕對路徑
	* @param save_name
	*            存儲的名稱
	* @param exitFlag
	*            是否在存儲完成之後直接退出程序
	*/
	public void SaveCurrenQuestionPaperPic(String absolute_path,
	String save_name, boolean exitFlag) {

		String name = new String(absolute_path + save_name);
		Bitmap bmp = Bitmap.createBitmap(dvBitmap.drawLevelBitmap);
		WindowManager wm = (WindowManager) parentContext
		.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		int height = wm.getDefaultDisplay().getHeight();// 屏幕高度
		int tmp_width_ratio, tmp_height_ratio;

		tmp_width_ratio = dvSize.Real_Width / width;
		tmp_height_ratio = dvSize.Real_Height / height;

	}

	/**
	* 加載答題信息的縮略背景圖
	* 
	* @param absolute_path
	*            加載的絕對路徑
	* @param load_name
	*            加載的題目ID
	*/
	public void LoadingMatrixPic(String absolute_path, String load_name) {
		String name = new String(absolute_path + load_name);
		File load_file = new File(name);
		if (load_file.exists()) {
			dvBitmap.firstBitmap = BitmapFactory.decodeFile(absolute_path
			+ load_name);
		} else {
			dvBitmap.firstBitmap = null;
		}
	}

	/**
	* 加載當前題目答題信息
	* 
	* @param absolute_path
	*            加載信息的絕對路徑
	* @param load_name
	*            加載的題目ID
	*/
	public void LoadingCurrenPaper(String absolute_path, String load_name) {
		// dvBitmap.createAllBitmap(parentContext, dvSize.VIEW_WIDTH,
		// dvSize.VIEW_HEIGHT);
		if (dvSaveData.LoadingCurrenPaperData(absolute_path, load_name)) {
			MoveReDraw(true);
		} else {
			// AddWaterMark();
		}
	}

	/**
	* 調用者調用此方法來獲得答題信息的縮略圖
	* 
	* @return 返回此縮略圖
	*/
	public Bitmap GetMatrixBackGroundBitmap() {
		Bitmap tmpBG = null;

		return tmpBG;
	}

	/**
	* 設置新的頁面寬高
	* 
	* @param width
	* @param height
	*/
	public void SetNewPageWidthHeight(int width, int height) {
		dvSize.SetNewPageSize(width, height);
		MyScroll myVerticallScroll = new MyScroll(parentContext,
		dvSize.Real_Height, dvSize.screen_height,
		MyScroll.TYPE_VERTICAL);
		MyScroll myHorizonScroll = new MyScroll(parentContext,
		dvSize.Real_Width, dvSize.screen_width, MyScroll.TYPE_HORIZON);

		dvBitmap.verticall_scroll = myVerticallScroll.GetMyScrollBitmap();
		dvBitmap.horizon_scroll = myHorizonScroll.GetMyScrollBitmap();
	}

	/**
	* 切換移動紙張或者答題的狀態
	* 
	* @param isMove
	*            boolean型是否是移動狀態
	*/
	public void SetMoveOrPaint(boolean isMove) {
		dvTouchMove.isMoveBg = isMove;
		dvTouchMove.distance_x = 0;
		dvTouchMove.distance_y = 0;
		if (dvTouchMove.isMoveBg) {
			dvTouchMove.scroll_show_flag = true;
			dvTouchMove.scroll_Alpha = 255;
			dvTouchMove.myScrollPaint.setAlpha(dvTouchMove.scroll_Alpha);
			new ScrollThread().start();
		}
		invalidate();
	}

	/**
	* 設置Pen的屬性
	* 
	* @param tmpPen
	*            需要設置的目標Paint
	*/
	public void SetPenAttrib(Paint tmpPen) {
		dvPen.paint.set(tmpPen);
	}

	/**
	* 移動紙張的方法
	* 
	* @param x
	*            移動的位置
	* @param y
	*/
	public void ToMovePic(float x, float y) {
		float move_x, move_y;
		move_x = x;
		move_y = y;
		// if (move_x > 0) {
		// // 向左滑動
		// MoveToLeft(move_x);
		// } else {
		// // 向右滑動
		// MoveToRigth(move_x);
		// }

		if (move_y > 0) {
			// 向上滑動
			MoveToTop(move_y);
		} else {
			// 向下滑動
			MoveToBottom(move_y);
		}
		invalidate();
	}

	/**
	* 重繪紙張移動之後的所有線段
	* 
	* @param canvas
	*            重繪的畫布
	* @param targetBmp
	*            目標圖片
	* @param divid_x
	*            目標圖片在畫布的偏移位置
	* @param divid_y
	*/
	public void ReDrawAllPath(Canvas canvas, Bitmap targetBmp, int divid_x,
	int divid_y) {
		int now_line_count = 0;
		int now_paint_count = 0;
		boolean check_result = false;
		OneLineInfo tmpInfo = new OneLineInfo(0, 0, 0, 0, 0, true);
		PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
		boolean isStartPos = false;
		float start_x = 0;
		float start_y = 0;
		float end_x = 0;
		float end_y = 0;

		dvPen.path.reset();
		targetBmp.eraseColor(Color.TRANSPARENT);
		// targetBmp.eraseColor(Color.RED);
		for (; now_line_count < dvSaveData.LineInfo.size(); now_line_count++) {
			tmpInfo = dvSaveData.LineInfo.get(now_line_count);
			if (tmpInfo.isPic) {
				Bitmap tmpDrawBmp = null;
				float sx, sy, ex, ey;
				sx = tmpInfo.SavePaintPos.get(0).pos_x;
				sy = tmpInfo.SavePaintPos.get(0).pos_y;
				ex = tmpInfo.SavePaintPos.get(1).pos_x;
				ey = tmpInfo.SavePaintPos.get(1).pos_y;
				tmpDrawBmp = DrawViewBitmap.LoadOneBitmap(tmpInfo.selectPics,
				sx, sy, ex, ey);
				if (tmpDrawBmp != null) {
					canvas.drawBitmap(tmpDrawBmp, sx - divid_x, sy - divid_y,
					dvPen.tmp);
				}
			} else if (tmpInfo.drawKind != 0) {
				// 重繪幾何圖形部份
				tmpInfo.penWidth = tmpInfo.penWidth * 2;
				dvGeometry.myGeometry.SelectDrawKind(canvas, tmpInfo,
				tmpInfo.drawKind, divid_x, divid_y, false);
			} else {
				// 重繪任意線段部份
				dvPen.myPenColor = tmpInfo.penColor;
				dvPen.isPen = tmpInfo.penFlag;
				if (!dvPen.isPen) {
					dvPen.myEraserWidth = tmpInfo.penWidth;
					dvPen.paint.setStrokeWidth(dvPen.myEraserWidth);
				} else {
					dvPen.myPenWidth = tmpInfo.penWidth * 2;
					dvPen.paint.setStrokeWidth(dvPen.myPenWidth);
				}
				dvPen.paint.setColor(dvPen.myPenColor);
				setPenOrEraserStyle(dvPen.isPen);
				// 重繪一條線段
				for (now_paint_count = 0; now_paint_count < tmpInfo.paint_count; now_paint_count++) {
					tmpPaintPos = tmpInfo.SavePaintPos.get(now_paint_count);
					// check_result = CheckPoitIsOnScreen(tmpPaintPos);
					check_result = true;
					if (check_result) {
						if (!isStartPos) {
							start_x = tmpPaintPos.pos_x - divid_x;
							start_y = tmpPaintPos.pos_y - divid_y;
							dvPen.path.moveTo(start_x, start_y);
							isStartPos = true;
						} else {
							end_x = tmpPaintPos.pos_x - divid_x;
							end_y = tmpPaintPos.pos_y - divid_y;
							// dvPen.path.quadTo(start_x, start_y, end_x,
							// end_y);
							drawBeziercurve(dvPen.path, start_x, start_y,
							end_x, end_y);
							start_x = end_x;
							start_y = end_y;
						}
					} else {
						if (dvPen.path != null && !dvPen.path.isEmpty()) {
							canvas.drawPath(dvPen.path, dvPen.paint);
							dvPen.path.reset();
							isStartPos = false;
						}
					}
				}

				if (dvPen.path != null && !dvPen.path.isEmpty()) {
					canvas.drawPath(dvPen.path, dvPen.paint);
					dvPen.path.reset();
					isStartPos = false;
				}
			}
		}
	}

	// 移動紙張時的重繪
	public void MoveReDraw(boolean check_max_pos_falg) {
		int now_line_count = 0;
		int now_paint_count = 0;
		boolean check_result = false;
		OneLineInfo tmpInfo = new OneLineInfo(0, 0, 0, 0, 0, true);
		PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
		boolean isStartPos = false;
		float start_x = 0;
		float start_y = 0;
		float end_x = 0;
		float end_y = 0;

		dvPen.path.reset();
		dvBitmap.drawLevelBitmap.eraseColor(Color.TRANSPARENT);
		for (; now_line_count < dvSaveData.LineInfo.size(); now_line_count++) {
			tmpInfo = dvSaveData.LineInfo.get(now_line_count);
			if (tmpInfo.isPic) {
				Bitmap tmpDrawBmp = null;
				float sx, sy, ex, ey;
				sx = tmpInfo.SavePaintPos.get(0).pos_x;
				sy = tmpInfo.SavePaintPos.get(0).pos_y;
				ex = tmpInfo.SavePaintPos.get(1).pos_x;
				ey = tmpInfo.SavePaintPos.get(1).pos_y;
				tmpDrawBmp = DrawViewBitmap.LoadOneBitmap(tmpInfo.selectPics,
				sx, sy, ex, ey);
				if (tmpDrawBmp != null) {
					dvBitmap.drawCanvas.drawBitmap(tmpDrawBmp, sx
					- diff_value_x, sy - diff_value_y, dvPen.tmp);
				}
			} else if (tmpInfo.drawKind != 0) {
				dvGeometry.myGeometry.SelectDrawKind(dvBitmap.drawCanvas,
				tmpInfo, tmpInfo.drawKind, diff_value_x, diff_value_y,
				false);
			} else {
				dvPen.myPenColor = tmpInfo.penColor;
				dvPen.isPen = tmpInfo.penFlag;
				if (!dvPen.isPen) {
					dvPen.myEraserWidth = tmpInfo.penWidth;
					dvPen.paint.setStrokeWidth(dvPen.myEraserWidth);
				} else {
					dvPen.myPenWidth = tmpInfo.penWidth;
					dvPen.paint.setStrokeWidth(dvPen.myPenWidth);
				}
				dvPen.paint.setColor(dvPen.myPenColor);
				setPenOrEraserStyle(dvPen.isPen);
				for (now_paint_count = 0; now_paint_count < tmpInfo.paint_count; now_paint_count++) {
					tmpPaintPos = tmpInfo.SavePaintPos.get(now_paint_count);
					// check_result = CheckPoitIsOnScreen(tmpPaintPos);
					check_result = true;
					if (check_result) {
						if (!isStartPos) {
							start_x = tmpPaintPos.pos_x - diff_value_x;
							start_y = tmpPaintPos.pos_y - diff_value_y;
							dvPen.path.moveTo(start_x, start_y);
							isStartPos = true;
						} else {
							end_x = tmpPaintPos.pos_x - diff_value_x;
							end_y = tmpPaintPos.pos_y - diff_value_y;
							// dvPen.path.quadTo(start_x, start_y, end_x,
							// end_y);
							drawBeziercurve(dvPen.path, start_x, start_y,
							end_x, end_y);
							start_x = end_x;
							start_y = end_y;
						}
					} else {
						if (dvPen.path != null && !dvPen.path.isEmpty()) {
							dvBitmap.drawCanvas.drawPath(dvPen.path,
							dvPen.paint);
							// invalidate();
							dvPen.path.reset();
							isStartPos = false;
						}
					}
				}

				if (dvPen.path != null && !dvPen.path.isEmpty()) {
					dvBitmap.drawCanvas.drawPath(dvPen.path, dvPen.paint);
					dvPen.path.reset();
					isStartPos = false;
				}
			}
		}
	}

	/**
	* 檢測幾何圖形繪製的狀態: 1. 改變位置大小 2. 固定繪製位置
	* 
	* @param point
	*            touch down的點座標
	* @return 是否touch 到了重合的点
	*/
	public boolean CheckPoitIsOnScreen(PaintPos point) {
		boolean bRet = false;

		if ((point.pos_x >= diff_value_x && point.pos_x <= diff_value_x
					+ dvSize.VIEW_WIDTH)
				&& (point.pos_y >= diff_value_y && point.pos_y <= diff_value_y
					+ dvSize.VIEW_HEIGHT)) {
			bRet = true;
		}
		return bRet;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_UP:
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			diff_value_x += 10;
			new ReDrawThread().start();
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			diff_value_x -= 10;
			new ReDrawThread().start();
			break;
		}
		// return true;
		return super.onKeyDown(keyCode, event);
	}

	/**
	* 向左移動紙張
	* 
	* @param move_value
	*            移動的偏移量
	*/
	public void MoveToLeft(float move_value) {
		float tmp_x = move_value;
		diff_value_x += move_value;
		if (diff_value_x > dvSize.Real_Width - dvSize.VIEW_WIDTH) {
			tmp_x = move_value
			- (diff_value_x - dvSize.Real_Width + dvSize.VIEW_WIDTH);
			diff_value_x = dvSize.Real_Width - dvSize.VIEW_WIDTH;
		}
		dvTouchMove.distance_x += tmp_x;
	}

	/**
	* 向右移動紙張
	* 
	* @param move_value
	*/
	public void MoveToRigth(float move_value) {
		float tmp_x = move_value;
		diff_value_x += move_value;
		if (diff_value_x < 0) {
			tmp_x = move_value - diff_value_x;
			diff_value_x = 0;
		}
		dvTouchMove.distance_x += tmp_x;
	}

	/**
	* 向上移動紙張
	* 
	* @param move_value
	*/
	public void MoveToTop(float move_value) {
		float tmp_y = move_value;
		diff_value_y += move_value;
		if (diff_value_y > dvSize.Real_Height - dvSize.VIEW_HEIGHT) {
			tmp_y = move_value
			- (diff_value_y - dvSize.Real_Height + dvSize.VIEW_HEIGHT);
			diff_value_y = dvSize.Real_Height - dvSize.VIEW_HEIGHT;
		}
		dvTouchMove.distance_y += tmp_y;
	}

	/**
	* 向下移動紙張
	* 
	* @param move_value
	*/
	public void MoveToBottom(float move_value) {
		float tmp_y = move_value;
		// 获得实际的使用高
		int tmpHeight = dvSaveData.max_y + dvSize.VIEW_HEIGHT;
		diff_value_y += move_value;
		if (diff_value_y < 0) {
			Log.i(DebugLog, "MoveToBottom  < 0" + tmpHeight);
			tmp_y = move_value - diff_value_y;
			diff_value_y = 0;
		} else if (diff_value_y > tmpHeight) {
			// tmp_y =
			Log.i(DebugLog, "MoveToBottom > dvSaveData.max_y"
			+ dvSaveData.max_y);
			Log.i(DebugLog, "MoveToBottom > dvSize.VIEW_HEIGHT"
			+ dvSize.VIEW_HEIGHT);
			Log.i(DebugLog, "MoveToBottom > tmpHeight" + tmpHeight);
		}
		dvTouchMove.distance_y += tmp_y;
	}

	/**
	* 重繪Touch up時的最後一條線段
	* 
	* @param startPath
	*            重繪從第幾條開始的線段
	* @param cleanScreen
	*            重繪之前是否清空屏幕
	*/
	public void ReDrawOthers(int startPath, boolean cleanScreen) {

		int now_line_count = 0;
		int now_paint_count = 0;
		boolean check_result = false;
		OneLineInfo tmpInfo = new OneLineInfo(0, 0, 0, 0, 0, true);
		PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
		boolean isStartPos = false;
		float start_x = 0;
		float start_y = 0;
		float end_x = 0;
		float end_y = 0;

		dvPen.path.reset();
		if (cleanScreen) {
			dvBitmap.drawLevelBitmap.eraseColor(Color.TRANSPARENT);
		}
		for (now_line_count = startPath; now_line_count < dvSaveData.LineInfo
		.size(); now_line_count++) {
			tmpInfo = dvSaveData.LineInfo.get(now_line_count);
			if (tmpInfo.isPic) {
				Bitmap tmpDrawBmp = null;
				float sx, sy, ex, ey;
				sx = tmpInfo.SavePaintPos.get(0).pos_x;
				sy = tmpInfo.SavePaintPos.get(0).pos_y;
				ex = tmpInfo.SavePaintPos.get(1).pos_x;
				ey = tmpInfo.SavePaintPos.get(1).pos_y;
				tmpDrawBmp = DrawViewBitmap.LoadOneBitmap(tmpInfo.selectPics,
				sx, sy, ex, ey);
				if (tmpDrawBmp != null) {
					dvBitmap.drawCanvas.drawBitmap(tmpDrawBmp, sx
					- diff_value_x, sy - diff_value_y, dvPen.tmp);
				}
			} else if (tmpInfo.drawKind != 0) {
				dvGeometry.myGeometry.SelectDrawKind(dvBitmap.drawCanvas,
				tmpInfo, tmpInfo.drawKind, diff_value_x, diff_value_y,
				false);
			} else {
				dvPen.myPenColor = tmpInfo.penColor;
				dvPen.isPen = tmpInfo.penFlag;
				if (!dvPen.isPen) {
					dvPen.myEraserWidth = tmpInfo.penWidth;
					dvPen.paint.setStrokeWidth(dvPen.myEraserWidth);
				} else {
					dvPen.myPenWidth = tmpInfo.penWidth;
					dvPen.paint.setStrokeWidth(dvPen.myPenWidth);
				}
				dvPen.paint.setColor(dvPen.myPenColor);
				setPenOrEraserStyle(dvPen.isPen);
				for (now_paint_count = 0; now_paint_count < tmpInfo.paint_count; now_paint_count++) {
					tmpPaintPos = tmpInfo.SavePaintPos.get(now_paint_count);
					check_result = CheckPoitIsOnScreen(tmpPaintPos);
					check_result = true;
					if (check_result) {
						if (!isStartPos) {
							start_x = tmpPaintPos.pos_x - diff_value_x;
							start_y = tmpPaintPos.pos_y - diff_value_y;
							dvPen.path.moveTo(start_x, start_y);
							isStartPos = true;
						} else {
							end_x = tmpPaintPos.pos_x - diff_value_x;
							end_y = tmpPaintPos.pos_y - diff_value_y;
							// dvPen.path.quadTo(start_x, start_y, end_x,
							// end_y);
							drawBeziercurve(dvPen.path, start_x, start_y,
							end_x, end_y);
							start_x = end_x;
							start_y = end_y;
						}
					} else {
						if (dvPen.path != null && !dvPen.path.isEmpty()) {
							dvBitmap.drawCanvas.drawPath(dvPen.path,
							dvPen.paint);
							dvPen.path.reset();
							isStartPos = false;
						}
					}
				}

				if (dvPen.path != null && !dvPen.path.isEmpty()) {
					dvBitmap.drawCanvas.drawPath(dvPen.path, dvPen.paint);
					dvPen.path.reset();
					isStartPos = false;
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isFinishFlag) {
			return true;
		}
		// 獲取拖動事件發生的位置
		float x = event.getX(0);
		float y = event.getY(0);
		if (null != dvSize && null != dvBitmap && null != dvSaveData
				&& null != dvPen && null != dvTouchMove && null != dvSelectPic
				&& null != dvGeometry && null != dvEraser) {
			if (x >= dvSize.now_x && x < dvSize.screen_width
					&& y >= dvSize.now_y && y < dvSize.screen_height) {
				// 在可畫區域內
				dvEraser.SetEraserPos(x, y);
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_DOWN:
					// 多點觸控移動紙張
					touchActionPointDown(x, y);
					break;
				case MotionEvent.ACTION_POINTER_UP:
					return true;
				case MotionEvent.ACTION_DOWN:
					touchActionDown(x, y);
					break;
				case MotionEvent.ACTION_MOVE:
					touchActionMove(x, y);
					break;
				case MotionEvent.ACTION_UP:
					touchActionUp(x, y);
					break;
				}
				// if (!dvPen.isPen) {
				// invalidate();
				// } else {
				// invalidate();
				// }
			} else {
				// 在可畫區域外
				touchActionOutOfView(x, y);
			}
			invalidate();
		}
		// 返回 true 表示處理方法已經處理該事件
		return true;
	}

	/**
	* touch 事件滑動紙張
	* 
	* @param x
	*            當前touch x
	* @param y
	*            當前touch y
	*/
	private void touchActionPointDown(float x, float y) {
		dvTouchMove.touch_eventFlag = true;
		dvTouchMove.touch_up = false;
		dvTouchMove.isMoveBg = true;
		dvGeometry.drawGeometryEnd = -1;
		dvTouchMove.doubleTouchMoveFlag = true;
		dvSaveData.one_line_info.SavePaintPos.clear();
		dvBitmap.penMoveDraw = false;
		dvBitmap.tmpDrawBitmap.eraseColor(Color.TRANSPARENT);
		// dvSelectPic.drawSelectEnd = -1;
		if (dvPen.path != null || !dvPen.path.isEmpty()) {
			dvPen.path.reset();
		}
		PenDown(x, y);
		if (dvSelectPic.drawSelectEnd == 1) {
			dvBitmap.drawCanvas.drawBitmap(dvSelectPic.showBmp, dvSelectPic.sx,
			dvSelectPic.sy, dvPen.tmp);
			dvBitmap.cacheBitmapShow = false;
			dvSelectPic.drawSelectEnd = 2;
		}
	}

	/**
	* touch 事件在可畫區域內答題 down事件
	* 
	* @param x
	*            touch x
	* @param y
	*            touch y
	*/
	private void touchActionDown(float x, float y) {
		touch_down = true;
		dvTouchMove.touch_eventFlag = true;
		dvTouchMove.touch_up = false;
		PenDown(x, y);
	}

	/**
	* touch 事件在可畫區域內答題 move事件
	* 
	* @param x
	*            touch x
	* @param y
	*            touch y
	*/
	private void touchActionMove(float x, float y) {
		if (dvTouchMove.touch_eventFlag) {
			PenMove(x, y);
		} else if (!touch_down) {
			touchActionDown(x, y);
		}
	}

	/**
	* touch 事件在可畫區域答題的up事件
	* 
	* @param x
	*            touch x
	* @param y
	*            touch y
	*/
	private void touchActionUp(float x, float y) {
		touch_down = false;
		if (dvTouchMove.touch_eventFlag) {
			PenUp(x, y);
			// 重新计算纸张的实际使用范围(进行以后的截图)
			RefreshMinMaxPos();
			dvTouchMove.touch_eventFlag = false;
		}
		if (dvTouchMove.doubleTouchMoveFlag) {
			dvTouchMove.isMoveBg = false;
			dvTouchMove.doubleTouchMoveFlag = false;
		}
	}

	/**
	* touch 事件當touch在答題區域外的時候處理
	* 
	* @param x
	*            touch x
	* @param y
	*            touch y
	*/
	private void touchActionOutOfView(float x, float y) {
		if (dvTouchMove.touch_eventFlag) {
			PenUp(x, y);
			dvTouchMove.touch_eventFlag = false;
			touch_down = false;
		}
		if (dvTouchMove.doubleTouchMoveFlag) {
			dvTouchMove.isMoveBg = false;
			dvTouchMove.doubleTouchMoveFlag = false;
		}
		if (!dvPen.path.isEmpty()) {
			dvTouchMove.touch_eventFlag = false;
			PenUp(x, y);
		}
	}

	/**
	* 改變畫圖的類型，保存Geometry畫出的幾何圖形。此方法是在判 斷畫出的幾何圖形是否可以完成定位，如果可以的話就調用此函數定型。
	*/
	public void ChangeDrawKind() {
		// 判斷是否存在未定型的Geometry圖形
		if (dvGeometry.drawGeometryEnd == 0 && !dvTouchMove.isMoveBg) {
			// 存在，將其進行定型
			dvSaveData.AddNewGeometryPath(dvGeometry.drawGeometryChos,
			dvPen.myPenWidth, dvPen.myPenColor, dvPen.isPen,
			dvGeometry.sx, dvGeometry.sy, dvGeometry.ex, dvGeometry.ey,
			dvGeometry.trianglePoint, diff_value_x, diff_value_y,
			endPos, dvPen.paint);
			dvGeometry.DrawGeometry(dvBitmap.drawCanvas,
			dvGeometry.drawGeometryChos, dvPen, false);
			dvGeometry.drawGeometryEnd = -2;
			invalidate();
		}
	}

	/**
	* Pen down畫筆點下事件
	* 
	* @param x
	* @param y
	*/
	private void PenDown(float x, float y) {
		if (null != dvBitmap && null != dvSaveData && null != dvPen
				&& null != dvTouchMove && null != dvSelectPic
				&& null != dvGeometry && null != dvEraser) {
			if (dvBitmap.cacheBitmapShow && !dvTouchMove.isMoveBg
					&& dvSelectPic.drawSelectEnd == 0) {
				// 是否存在從WebView截取的還沒有固定位置的Pic
				dvSelectPic.checkDownPos = dvSelectPic.CheckPenDownRect(x, y);
				if (dvSelectPic.checkDownPos == -1) {
					// 繼續移動pic
					dvSelectPic.geoMove_x = x;
					dvSelectPic.geoMove_y = y;
					return;
				} else if (dvSelectPic.checkDownPos == 0) {
					// 將pic 信息加入存儲Data中並且使其固定當前位置
					dvSaveData.AddSelectPicPath(dvSelectPic.sx, dvSelectPic.sy,
					diff_value_x, diff_value_y, dvSelectPic.showBmp,
					dvSelectPic.showBmp_path, dvPen.paint);
					// dvBitmap.cacheBitmapShow = false;
					dvSelectPic.isNotMove = true;
					dvSelectPic.drawSelectEnd = 1;
					invalidate();
					return;
				} else if (dvSelectPic.checkDownPos > 0) {
					return;
				}
				return;
			} else if (dvGeometry.drawGeometryEnd == 0 && !dvTouchMove.isMoveBg) {
				// 檢測touch down位置是否在未定型的Geometry圖形上:
				// -1為move圖形; 0為定型圖形 ; >0 為判斷拉伸的點進行拉伸
				dvGeometry.checkDownPos = dvGeometry.CheckPenDownPosIsInMove(x,
				y);
				if (dvGeometry.checkDownPos == -1) {
					// move此幾何圖形
					dvGeometry.geoMove_x = x;
					dvGeometry.geoMove_y = y;
					return;
				} else if (dvGeometry.checkDownPos == 0) {
					// 將此圖形進行定型
					dvSaveData.AddNewGeometryPath(dvGeometry.drawGeometryChos,
					dvPen.myPenWidth, dvPen.myPenColor, dvPen.isPen,
					dvGeometry.sx, dvGeometry.sy, dvGeometry.ex,
					dvGeometry.ey, dvGeometry.trianglePoint,
					diff_value_x, diff_value_y, endPos, dvPen.paint);
					dvGeometry.drawGeometryEnd = 1;
					invalidate();
					return;
				} else if (dvGeometry.checkDownPos > 0) {
					return;
				}
			}
			dvGeometry.sx = x;
			dvGeometry.sy = y;
			dvGeometry.ex = dvGeometry.sx;
			dvGeometry.ey = dvGeometry.sy;
			if (dvGeometry.drawGeometryFlag && !dvTouchMove.isMoveBg) {
				if (dvGeometry.drawGeometryEnd == -2) {
					dvGeometry.drawGeometryEnd = -1;
				}
				invalidate();
				return;
			}
			dvTouchMove.distance_x = 0;
			dvTouchMove.distance_y = 0;
			dvTouchMove.touch_up = false;
			dvTouchMove.scroll_Alpha = 255;
			dvTouchMove.myScrollPaint.setAlpha(dvTouchMove.scroll_Alpha);
			dvTouchMove.scroll_show_flag = false;
			if (!dvTouchMove.isMoveBg) {
				dvPen.path.moveTo(x, y);
				dvSaveData.SaveRealPos(x, y, diff_value_x, diff_value_y,
				endPos, dvPen.paint);
				if (dvPen.path != null) {
					dvBitmap.penMoveDraw = true;
					dvBitmap.cacheCanvas.drawPath(dvPen.path, dvPen.paint);
				}
			}

			preX = x;
			preY = y;
			dvBitmap.move_hand_x = preX;
			dvBitmap.move_hand_y = preY;
		}
	}

	/**
	* pen畫筆移動或者紙張的移動
	* 
	* @param x
	* @param y
	*/
	private void PenMove(float x, float y) {
		if (null != dvBitmap && null != dvSaveData && null != dvPen
				&& null != dvTouchMove && null != dvSelectPic
				&& null != dvGeometry && null != dvEraser) {
			if (dvBitmap.cacheBitmapShow && !dvTouchMove.isMoveBg) {
				if (dvSelectPic.drawSelectEnd == 0) {
					if (dvSelectPic.checkDownPos == -1) {
						float tmp_x = x - dvSelectPic.geoMove_x;
						float tmp_y = y - dvSelectPic.geoMove_y;

						dvSelectPic.sx += tmp_x;
						dvSelectPic.sy += tmp_y;
						dvSelectPic.ex += tmp_x;
						dvSelectPic.ey += tmp_y;

						dvSelectPic.geoMove_x = x;
						dvSelectPic.geoMove_y = y;
					} else if (dvSelectPic.checkDownPos > 0
							&& null != dvBitmap.cacheBitmap) {
						dvSelectPic.ReSizePic(dvBitmap.cacheBitmap, x, y);
					}
					invalidate();
					return;
				}
				// else if (dvSelectPic.drawSelectEnd == 1) {
				// // dvBitmap.drawCanvas.drawBitmap(dvSelectPic.showBmp,
				// start_x,
				// // start_y, dvPen.paint);
				// invalidate();
				// return;
				// }
				return;
			} else if (dvGeometry.drawGeometryEnd == 0 && !dvTouchMove.isMoveBg) {
				if (dvGeometry.checkDownPos == -1) {
					float tmp_x = x - dvGeometry.geoMove_x;
					float tmp_y = y - dvGeometry.geoMove_y;
					if (dvGeometry.drawGeometryChos == Geometry.DRAW_LINE
							|| dvGeometry.drawGeometryChos == Geometry.DRAW_OVAL
							|| dvGeometry.drawGeometryChos == Geometry.DRAW_RECTANGLE) {
						dvGeometry.sx += tmp_x;
						dvGeometry.sy += tmp_y;
						dvGeometry.ex += tmp_x;
						dvGeometry.ey += tmp_y;
					} else if (dvGeometry.drawGeometryChos == Geometry.DRAW_TRIANGLE) {
						dvGeometry.trianglePoint.x1 += tmp_x;
						dvGeometry.trianglePoint.y1 += tmp_y;
						dvGeometry.trianglePoint.x2 += tmp_x;
						dvGeometry.trianglePoint.y2 += tmp_y;
						dvGeometry.trianglePoint.x3 += tmp_x;
						dvGeometry.trianglePoint.y3 += tmp_y;
					}
					dvGeometry.geoMove_x = x;
					dvGeometry.geoMove_y = y;
				} else if (dvGeometry.checkDownPos > 0) {
					dvGeometry.ReSizeGeometryGraphics(x, y);
				}
				// dvBitmap.tmpDrawBitmap.eraseColor(Color.TRANSPARENT);
				// dvGeometry.DrawGeometryOnCanvas(dvBitmap.cacheCanvas,
				// dvBitmap,
				// dvPen);
				invalidate();
				return;
			} else if (dvGeometry.drawGeometryEnd == 2 && !dvTouchMove.isMoveBg) {
				dvGeometry.sx = x;
				dvGeometry.sy = y;
				dvGeometry.ex = x;
				dvGeometry.ey = y;
				return;
			} else if (dvGeometry.drawGeometryEnd == 1 && !dvTouchMove.isMoveBg) {
				return;
			}
			dvGeometry.ex = x;
			dvGeometry.ey = y;
			if (dvGeometry.drawGeometryFlag && !dvTouchMove.isMoveBg) {
				if (dvGeometry.drawGeometryChos == Geometry.DRAW_TRIANGLE) {
					dvGeometry.trianglePoint = dvGeometry.myGeometry
					.GetTrianglePoints(dvGeometry.sx, dvGeometry.sy,
					dvGeometry.ex, dvGeometry.ey);
				}
				// dvBitmap.tmpDrawBitmap.eraseColor(Color.TRANSPARENT);
				// dvGeometry.DrawGeometryOnCanvas(dvBitmap.cacheCanvas,
				// dvBitmap,
				// dvPen);
				invalidate();
				return;
			}
			if (!dvTouchMove.isMoveBg) {
				if ((int) preX != (int) x || (int) preY != (int) y) {
					// dvPen.path.quadTo(preX, preY, x, y);
					drawBeziercurve(dvPen.path, preX, preY, x, y);
					dvSaveData.SaveRealPos(x, y, diff_value_x, diff_value_y,
					endPos, dvPen.paint);
				}
			} else {
				dvTouchMove.scroll_Alpha = 255;
				dvTouchMove.myScrollPaint.setAlpha(dvTouchMove.scroll_Alpha);
				preX = preX - x;
				preY = preY - y;
				ToMovePic(preX, preY);
			}
			if (dvPen.isPen && dvBitmap.penMoveDraw) {
				dvBitmap.cacheCanvas.drawPath(dvPen.path, dvPen.paint); // 1
			} else {
				if (dvPen.path != null && !dvPen.path.isEmpty()) {
					dvBitmap.drawCanvas.drawPath(dvPen.path, dvPen.paint); // 1
				}
			}
			preX = x;
			preY = y;
			dvBitmap.move_hand_x = preX;
			dvBitmap.move_hand_y = preY;
		}
	}

	/**
	* touch up
	* 
	* @param x
	* @param y
	*/
	private void PenUp(float x, float y) {
		if (null != dvBitmap && null != dvSaveData && null != dvPen
				&& null != dvTouchMove && null != dvSelectPic
				&& null != dvGeometry && null != dvEraser) {
			if (dvBitmap.cacheBitmapShow && !dvTouchMove.isMoveBg) {
				if (dvSelectPic.drawSelectEnd == 1) {
					dvBitmap.drawCanvas.drawBitmap(dvSelectPic.showBmp,
					dvSelectPic.sx, dvSelectPic.sy, dvPen.tmp);
					dvBitmap.cacheBitmapShow = false;
					dvSelectPic.drawSelectEnd = 2;
				}
				invalidate();
				return;
			} else if (dvGeometry.drawGeometryEnd == 0 && !dvTouchMove.isMoveBg) {
				invalidate();
				return;
			}
			if (dvGeometry.drawGeometryFlag && !dvTouchMove.isMoveBg) {
				// dvBitmap.tmpDrawBitmap.eraseColor(Color.TRANSPARENT);
				if (dvGeometry.drawGeometryEnd == -1) {
					invalidate();
					dvGeometry.drawGeometryEnd = 0;
				} else if (dvGeometry.drawGeometryEnd == 2) {
					dvGeometry.drawGeometryEnd = -2;
				}
				return;
			}
			if (!dvTouchMove.isMoveBg) {
				// drawCanvas.drawPath(path, paint); // 1
				dvBitmap.penMoveDraw = false;
				dvBitmap.tmpDrawBitmap.eraseColor(Color.TRANSPARENT);
				// 增加一條軌跡
				dvSaveData.AddNewPath(dvPen.myPenWidth, dvPen.myPenColor,
				dvPen.myEraserWidth, dvPen.isPen);
				RedrawPenup(dvSaveData.GetLastPath());
				// ReDrawOthers(dvSaveData.LineInfo.size() - 1, false);
				// ReDrawOthers(0, true);
			} else {
				ReDrawOthers(0, true);
				dvTouchMove.touch_up = true;
				if (!dvTouchMove.scroll_show_flag) {
					new ScrollThread().start();
					dvTouchMove.scroll_show_flag = true;
				}
			}
		}
	}

	/**
	* 貝塞爾曲綫
	* 
	* @param mPath
	* @param sx
	*            起點x
	* @param sy
	*            起點y
	* @param ex
	*            終點x
	* @param ey
	*            終點y
	*/
	private void drawBeziercurve(Path mPath, float sx, float sy, float ex,
	float ey) {
		mPath.quadTo(sx, sy, (sx + ex) / 2, (sy + ey) / 2);
	}

	/**
	* 檢測畫筆使用的紙張範圍
	*/
	private void RefreshMinMaxPos() {
		int now_line_count = 0;
		int now_paint_count = 0;
		OneLineInfo tmpInfo = new OneLineInfo(0, 0, 0, 0, 0, true);
		PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
		for (; now_line_count < dvSaveData.LineInfo.size(); now_line_count++) {
			tmpInfo = dvSaveData.LineInfo.get(now_line_count);
			if (tmpInfo.penFlag) {
				if (now_line_count == 0) {
					tmpPaintPos = tmpInfo.SavePaintPos.get(now_paint_count);
					dvSaveData.min_x = (int) tmpPaintPos.pos_x;
					dvSaveData.min_y = (int) tmpPaintPos.pos_y;
					tmpPaintPos = new PaintPos(0, 0, 0);
				}
				for (now_paint_count = 0; now_paint_count < tmpInfo.paint_count; now_paint_count++) {
					tmpPaintPos = tmpInfo.SavePaintPos.get(now_paint_count);
					dvSaveData.min_x = (int) (tmpPaintPos.pos_x - 9 < dvSaveData.min_x ? (tmpPaintPos.pos_x - 9)
					: dvSaveData.min_x);
					dvSaveData.min_y = (int) (tmpPaintPos.pos_y - 9 < dvSaveData.min_y ? (tmpPaintPos.pos_y - 9)
					: dvSaveData.min_y);
					dvSaveData.max_x = (int) ((tmpPaintPos.pos_x + 10) > dvSaveData.max_x ? (tmpPaintPos.pos_x + 10)
					: dvSaveData.max_x);
					dvSaveData.max_y = (int) ((tmpPaintPos.pos_y + 10) > dvSaveData.max_y ? (tmpPaintPos.pos_y + 10)
					: dvSaveData.max_y);
				}
			}
		}
	}

	/**
	* Touch up之後需要重繪的最後一條線段
	* 
	* @param info
	*/
	private void RedrawPenup(OneLineInfo info) {
		int now_paint_count = 0;
		boolean isStartPos = false;
		float start_x = 0;
		float start_y = 0;
		float end_x = 0;
		float end_y = 0;

		dvPen.path.reset();
		dvPen.myPenColor = info.penColor;
		dvPen.isPen = info.penFlag;
		if (!dvPen.isPen) {
			dvPen.myEraserWidth = info.penWidth;
			dvPen.paint.setStrokeWidth(dvPen.myEraserWidth);
		} else {
			dvPen.myPenWidth = info.penWidth;
			dvPen.paint.setStrokeWidth(dvPen.myPenWidth);
		}
		dvPen.paint.setColor(dvPen.myPenColor);
		setPenOrEraserStyle(dvPen.isPen);
		PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
		for (now_paint_count = 0; now_paint_count < info.paint_count; now_paint_count++) {
			tmpPaintPos = info.SavePaintPos.get(now_paint_count);
			// RefreshMaxPos(tmpPaintPos.pos_x, tmpPaintPos.pos_y);
			if (!isStartPos) {
				start_x = tmpPaintPos.pos_x - diff_value_x;
				start_y = tmpPaintPos.pos_y - diff_value_y;
				dvPen.path.moveTo(start_x, start_y);
				isStartPos = true;
			} else {
				end_x = tmpPaintPos.pos_x - diff_value_x;
				end_y = tmpPaintPos.pos_y - diff_value_y;
				drawBeziercurve(dvPen.path, start_x, start_y, end_x, end_y);
				// dvPen.path.quadTo(start_x, start_y, end_x, end_y);
				start_x = end_x;
				start_y = end_y;
			}
		}
		if (dvPen.path != null && !dvPen.path.isEmpty()) {
			dvBitmap.drawCanvas.drawPath(dvPen.path, dvPen.paint);
			// invalidate();
			dvPen.path.reset();
			isStartPos = false;
		}
	}

	/**
	* DrawView滾動條出現于消失的漸變線程
	*/
	private class ScrollThread extends Thread {
		public ScrollThread() {

		}

		public void run() {
			// Looper.prepare();
			try {
				Thread.sleep(700);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			while (dvTouchMove.scroll_show_flag) {
				try {
					// 睡眠时间为1秒
					Thread.sleep(1);
					dvTouchMove.myScrollPaint
					.setAlpha(dvTouchMove.scroll_Alpha);
					dvTouchMove.scroll_Alpha -= 1;
					if (dvTouchMove.scroll_show_flag) {
						if (null != mHandler) {
							mHandler.sendEmptyMessage(0);
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (dvTouchMove.scroll_Alpha < 0) {
					dvTouchMove.scroll_Alpha = 0;
					break;
				}
			}
			if (dvTouchMove.scroll_show_flag) {
				if (null != mHandler) {
					mHandler.sendEmptyMessage(0);
				}
			}
		}
	}

	private class ReDrawThread extends Thread {
		public ReDrawThread() {

		}

		public void run() {
			if (null != mHandler) {
				mHandler.sendEmptyMessage(1);
			}
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (dvTouchMove.scroll_show_flag) {
					if (!isFinishFlag) {
						invalidate(dvSize.screen_width - 20, 0,
						dvSize.screen_width, dvSize.screen_height);
					}
				}
				break;
			case 1:
				MoveReDraw(false);
				break;
			}

			super.handleMessage(msg);
		}
	};

	/**
	* 設置是否需要答題水印
	* 
	* @param flag
	*/
	public void SetDrawWaterMark(boolean flag) {
		isDrawWaterMark = flag;
	}

	@Override
	public void onDraw(Canvas canvas) {
		if (!isFinishFlag) {
			// super.onDraw(canvas);
			Paint bmpPaint = new Paint();
			bmpPaint.setAntiAlias(true);
			bmpPaint.setDither(true);
			// if (question_bitmap != null && bgImgBitmap != null) {
			// canvas.drawBitmap(bgImgBitmap, now_x, now_y, bmpPaint);
			// }

			// 橡皮擦
			if (!dvPen.isPen && !dvTouchMove.isMoveBg
					&& dvTouchMove.touch_eventFlag) {
				if (null != canvas) {
					dvEraser.DrawEraserOnCanvas(canvas);
				}
			}

			if (null != dvBitmap.drawLevelBitmap) {
				if (dvTouchMove.isMoveBg && !dvTouchMove.touch_up) {
					// 紙張移動時移動的背景圖
					canvas.drawBitmap(dvBitmap.drawLevelBitmap,
					-dvTouchMove.distance_x, -dvTouchMove.distance_y,
					bmpPaint); // 2
				} else {
					// 畫時的軌跡
					canvas.drawBitmap(dvBitmap.drawLevelBitmap, 0, 0, bmpPaint); // 2
				}
			}

			// 顯示紙張ScrollBar
			// if (dvBitmap.move_hand_bitmap != null && dvTouchMove.isMoveBg) {
			// dvTouchMove.DrawScrollOnCanvas(canvas, dvBitmap, dvSize,
			// dvTouchMove, bmpPaint, diff_value_x, diff_value_y);
			// }
			if (dvBitmap.move_hand_bitmap != null && dvTouchMove.isMoveBg) {
				dvTouchMove.DrawScrollOnCanvas(canvas, dvBitmap, dvSize,
				dvTouchMove, bmpPaint, diff_value_x, diff_value_y);
			} else if (dvBitmap.move_hand_bitmap != null
					&& !dvTouchMove.isMoveBg && dvTouchMove.scroll_show_flag) {
				dvTouchMove.DrawScrollOnCanvas(canvas, dvBitmap, dvSize,
				dvTouchMove, bmpPaint, diff_value_x, diff_value_y);
			}

			// 畫抓取的圖片
			// if (dvSelectPic.drawSelectEnd == 2 && dvSelectPic.showBmp !=
			// null) {
			// dvBitmap.drawCanvas.drawBitmap(dvSelectPic.showBmp, start_x,
			// start_y, bmpPaint);
			// }

			if (null != dvSelectPic.showBmp && dvBitmap.cacheBitmapShow) {
				isDrawWaterMark = false;
				float start_x, start_y;
				if (dvSelectPic.sx < dvSelectPic.ex) {
					start_x = dvSelectPic.sx;
				} else {
					start_x = dvSelectPic.ex;
				}
				if (dvSelectPic.sy < dvSelectPic.ey) {
					start_y = dvSelectPic.sy;
				} else {
					start_y = dvSelectPic.ey;
				}
				if (null != dvSelectPic.showBmp) {
					canvas.drawBitmap(dvSelectPic.showBmp, start_x, start_y,
					dvPen.tmp);
				}
				if (!dvSelectPic.isNotMove) {
					// 畫抓取的圖片可移動輪廓
					int divid_value = 5;
					Geometry.DrawDragRectangle(canvas, dvSelectPic.sx
					- divid_value, dvSelectPic.sy - divid_value,
					dvSelectPic.ex + divid_value, dvSelectPic.ey
					+ divid_value, dvPen.tmp, true, true, true,
					true, true, true, true, true, false);
				}
			}

			// 答題紙水印
			if (isDrawWaterMark && !dvBitmap.cacheBitmapShow) {
				String str = parentContext.getString(R.string.writepaper_in);
				Paint tmpWaterMarkPaint = new Paint();
				int textSize = dvSize.screen_width
				/ (int) (str.length() * 1.618);
				tmpWaterMarkPaint.setTextSize(textSize);
				tmpWaterMarkPaint.setColor(Color.GRAY - 10);
				tmpWaterMarkPaint.setAlpha(60);
				int textWidth = (int) tmpWaterMarkPaint.measureText(str);
				canvas.drawText(str, (dvSize.screen_width - textWidth) / 2,
				textSize, tmpWaterMarkPaint);
			}

			if (dvBitmap.penMoveDraw && null != dvBitmap.tmpDrawBitmap) {
				// 臨時劃出的一條線
				canvas.drawBitmap(dvBitmap.tmpDrawBitmap, 0, 0, dvPen.paint);
			}

			// 畫最後一次線
			if (dvPen.isPen) {
				// // 沿著 path 繪製
				// if (dvPen.path != null && !dvPen.path.isEmpty()) {
				// canvas.drawPath(dvPen.path, dvPen.paint);
				// }

				if (dvGeometry.drawGeometryFlag && !dvTouchMove.isMoveBg
						&& !dvTouchMove.touch_up) {
					dvGeometry.DrawGeometryOnCanvas(canvas, dvBitmap, dvPen);
					// canvas.drawBitmap(dvBitmap.tmpDrawBitmap, 0, 0,
					// dvPen.paint);
					// dvGeometry.DrawGeometryOnCanvas(dvBitmap.cacheCanvas,
					// dvBitmap,
					// dvPen);
				}
			}
		}
	}
}
