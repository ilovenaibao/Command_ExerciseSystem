package com.besta.app.answerpaper.othersclassinfo;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;

import com.besta.app.answerpaper.drawview.DrawView;
import com.besta.app.answerpaper.drawview.DrawViewBitmap;
import com.besta.app.answerpaper.drawview.DrawViewEraser;
import com.besta.app.answerpaper.drawview.DrawViewSaveData;
import com.besta.app.answerpaper.mywebview.MyWebViewCaller;
import com.besta.app.answerpaper.mywebview.MyWebViewCaller.Callee;
import com.besta.app.answerpaper.mywebview.MyWebViewCaller.Caller;
import com.besta.app.answerpaper.mywebview.MyWebViewFun;
import com.besta.app.answerpaper.othergraphics.MyDefineGraphicSetting;
import com.besta.app.answerpaper.othergraphics.MyDefineGraphicSetting.MyDefineData;
import com.besta.app.answerpaper.othergraphics.OtherGraphicResultData;
import com.besta.app.answerpaper.othergraphics.OtherGraphicSetting;
import com.besta.app.answerpaper.othergraphics.OtherGraphicsMain;
import com.besta.app.exerciseengine.R;
import com.besta.app.geometry.Geometry;
import com.besta.app.testcallactivity.IdConvert;
import com.besta.app.testcallactivity.StartAnswerPaperSetting;
import com.besta.app.toolswindow.MySaveWindow;
import com.besta.app.toolswindow.MyToolsWindow;

public abstract class AnswerPaperFun {
	// Debug log
	final static String DebugLog = "AnswerPaperFun->";
	// Activity for resultCode
	public final static int RESULT_CODE_OTHERGRAPHIC = 0xA0003; // 調用OtherGraphic

	// parent Context
	private static Context parentContext;

	// Activity Kind
	public final static int ACTIVITY_SOLUTION = 1;
	public final static int ACTIVITY_FILL = 2;
	public final static int ACTIVITY_CHOICE = 3;

	// Data check status
	public final static int IS_CLEAR_START = 1;
	public final static int IS_CLEAR_CHECK = 2;
	public final static int IS_CHECK_DATA = 3;

	// all buttons
	public final static int TOOL_WRITEPAPER = 0;
	public final static int TOOL_TESTPAPER = 1;
	public final static int TOOL_TOOLS = 2;
	public final static int TOOL_BACK = 3;
	public final static int tools_count = 4;

	// screen titlebar height
	public static int Title_Bar_Height_Normal = 36;
	public static int Title_Bar_Height = 36;

	// Dialog activity move on screen
	private static float mTouchStartX;
	private static float mTouchStartY;
	private static float g_x;
	private static float g_y;
	private static boolean touchMultiPointerFlag = false;
	// Touch activity's rectangle to change activity size's scope;
	private static int touchActivityChangeSizeDistance = 5;

	// Answerpaper main wind
	private static WindowManager wm = null;
	private static WindowManager.LayoutParams wmParams = null;

	// layout divid value
	private final static int layoutMargin = 2;

	// tmp now chos Tools focus
	private static int tmpNowToolsChos = 0;
	private static boolean toolsIsFocus = false;
	private static boolean isAddWaterMark = false;

	// use to fill layout, it is not use by this Activity
	public static TextView tv = null;
	// 其他圖形
	public static OtherGraphicsMain otherGraphic = null;

	// start Answerpaper activity get bundle data
	public static class ReturnGetStartBundleData {
		public int result_code2; // return activity for result code
		public String pack_name; // package name
		public String answer_id; // question id
		public int set_width;
		public int set_height;
		public String absolute_path; // save data path
		public String bgImg_path; // save background bitmap path
		public int realWidth; // Answerpaper real width
		public int realHeight; // Answerpaper real height
		public boolean bRet;
		public int screen_width;
		public int screen_height;

		public Point dlgPos; // Dialog activity positon

		public LinearLayout myMainView = null; // Answerpaper Main frame
		public LinearLayout main_tool_bar = null; // tools bar of main frame
		public LinearLayout main_frame_pre = null; // main_tool_bar of layout
		public LinearLayout main_frame = null; // all views layout
		public LinearLayout drawView_frame = null; // draw view frame

		public boolean onReturnEvent_flag;

		// CallBack of Select picture
		public Callee c1 = null;
		public Caller caller = null;

		// constructor
		public ReturnGetStartBundleData() {
			result_code2 = set_width = set_height = realWidth = realHeight = 0;
			pack_name = answer_id = absolute_path = bgImg_path = "";
			bRet = false;
			dlgPos = new Point(0, 0);
			screen_width = screen_height = 0;
			onReturnEvent_flag = false;
		}
	}

	/**
	* 獲取當前調用答題紙Activity時傳入的參數並且初始化設置答題紙介面
	* 
	* @param context
	* @param bundlData
	*            傳入調用者信息
	* @param chosActivityKind
	*            以哪一種答題紙來啟動
	* @return 返回的是一個調用者傳入的所有信息類
	*/
	public static ReturnGetStartBundleData GetStartBundle(Context context,
	ReturnGetStartBundleData bundlData, int chosActivityKind) {
		Bundle bundle = ((Activity) context).getIntent().getExtras();
		if (bundle != null) {
			parentContext = context;
			otherGraphic = new OtherGraphicsMain(context, 0);
			exitWindow = new MySaveWindow(context, chosActivityKind);
			bundlData.bRet = true;
			bundlData.result_code2 = bundle
			.getInt(StartAnswerPaperSetting.ANSWER_RESULT_CODE_KEY);
			bundlData.pack_name = bundle
			.getString(StartAnswerPaperSetting.PACK_NAME_KEY);
			bundlData.answer_id = bundle
			.getString(StartAnswerPaperSetting.ANSWER_ID_KEY);
			bundlData.answer_id = IdConvert
			.ResetAbsolutPathStr(bundlData.answer_id);
			bundlData.set_width = bundle
			.getInt(StartAnswerPaperSetting.PAGE_WIDTH_KEY);
			bundlData.set_height = bundle
			.getInt(StartAnswerPaperSetting.PAGE_HEIGHT_KEY);
			bundlData.absolute_path = StartAnswerPaperSetting.save_path
			+ bundlData.pack_name + "/";
			bundlData.bgImg_path = bundlData.absolute_path
			+ bundlData.answer_id;

			int width, height;
			GetScreenWidthHeight getScreen = new GetScreenWidthHeight(context);
			width = getScreen.getWidth();
			Title_Bar_Height = GetScreenWidthHeight.px2dip(context,
			Title_Bar_Height_Normal);
			height = getScreen.getHeight() - Title_Bar_Height;
			bundlData.screen_width = width;
			bundlData.screen_height = height;

			bundlData.realWidth = width;
			bundlData.realHeight = height;

			if (0 != getScreen.Title_Bar_Height) {
				Title_Bar_Height = GetScreenWidthHeight.px2dip(context, 0);
			} else {
				Title_Bar_Height = GetScreenWidthHeight.px2dip(context, 65);
			}

			if (chosActivityKind == ACTIVITY_FILL
					|| chosActivityKind == ACTIVITY_CHOICE) {
				int dlgPos_x = 0;
				int dlgPos_y = 0;
				dlgPos_x = bundle.getInt(StartAnswerPaperSetting.DLG_POS_X_KEY);
				dlgPos_y = bundle.getInt(StartAnswerPaperSetting.DLG_POS_Y_KEY);
				// 設置屏幕顯示寬高
				bundlData.realWidth = width * 2 / 3;
				bundlData.realHeight = height * 1 / 3;

				dlgPos_x = width / 2 - bundlData.realWidth / 2;
				// 設置Activity顯示的位置
				bundlData.dlgPos = GetMyActivityRealPos(dlgPos_x, dlgPos_y,
				width, height, bundlData.realWidth,
				bundlData.realHeight);
				// SetActivityPos(context, tmpPos.x, tmpPos.y);
			}
		}

		return bundlData;
	}

	// private static String ResetAbsolutPathStr(String src_path) {
	// String strRet = null;
	// byte[] byteStr = src_path.getBytes();
	// for (int i = 0; i < byteStr.length; i++) {
	// if ((byteStr[i] >= 48 && byteStr[i] <= 57)
	// || (byteStr[i] >= 65 && byteStr[i] <= 90)) {
	//
	// } else {
	// byteStr[i] = '_';
	// }
	// }
	//
	// strRet = new String(byteStr);
	//
	// return strRet;
	// }

	/**
	* 获取调用Dialog形式的答题纸时传入的当前显示位置
	* 
	* @param x
	*            相对屏幕x
	* @param y
	*            相对屏幕y
	* @param screen_x
	*            屏幕宽
	* @param screen_y
	*            屏幕高
	* @param realWidth
	*            答题纸实际宽
	* @param realHeight
	*            答题纸实际高
	* @return 返回的是Activity合理的顯示位置
	*/
	public static Point GetMyActivityRealPos(int x, int y, int screen_x,
	int screen_y, int realWidth, int realHeight) {
		Point pRet = new Point();
		int distance_y = 150;
		if (y + realHeight + distance_y >= screen_y) {
			y -= (realHeight / 2 + distance_y);
		} else {
			y += distance_y;
		}

		pRet.x = x;
		pRet.y = y;

		return pRet;
	}

	/**
	* 設置Dialog 答題紙顯示的位置
	* 
	* @param context
	* @param bundleData
	*            調用者信息
	*/
	public static void SetActivityPos(Context context,
	ReturnGetStartBundleData bundleData) {
		WindowManager wm = null;
		// WindowManager.LayoutParams wmParams = null;
		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService(
		"window");
		// 设置LayoutParams(全局变量）相关参数
		// wmParams = ((MyApplication1) ((Activity) context).getApplication())
		// .getMywmParams();
		if (wmParams == null) {
			wmParams = new WindowManager.LayoutParams();
		}
		wmParams.x = bundleData.dlgPos.x;
		wmParams.y = bundleData.dlgPos.y;
		wm.updateViewLayout(bundleData.myMainView, wmParams); // 刷新显示
	}

	/**
	* 設置Dialog 答題紙顯示的位置
	* 
	* @param context
	* @param myMainView
	* @param x
	* @param y
	*/
	public static void SetActivityPos2(Context context,
	LinearLayout myMainView, int x, int y) {
		WindowManager wm = null;
		// WindowManager.LayoutParams wmParams = null;
		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService(
		"window");
		// 设置LayoutParams(全局变量）相关参数
		// wmParams = ((MyApplication1) ((Activity) context).getApplication())
		// .getMywmParams();
		if (wmParams == null) {
			wmParams = new WindowManager.LayoutParams();
		}
		wmParams.x = x;
		wmParams.y = y;
		wm.updateViewLayout(myMainView, wmParams); // 刷新显示
	}

	/**
	* 創建Answerpaper View
	* 
	* @param context
	* @param myShowWebView
	*            如果全屏調用答題紙會出現WebView來顯示當前題目
	* @param bundleData
	*            調用者信息
	* @param initializeStartData
	*            初始化數據
	* @param imgBt_tool
	*            所有button
	* @param writePaperText
	*            答題紙水印
	* @param testPaperText
	*            草稿紙水印
	* @param drawView
	*            答題區域
	* @param chosActivityKind
	*            選擇調用的答題紙類型
	*/
	public static void CreateAllView(Context context, WebView myShowWebView,
	ReturnGetStartBundleData bundleData,
	InitializeStartData initializeStartData, ImageView[] imgBt_tool,
	ImageView writePaperText, ImageView testPaperText,
	DrawView[] drawView, boolean[] isDisable_tool, int nowToolChos,
	MyToolsWindow myTools, int chosActivityKind) {
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
		ViewGroup.LayoutParams.WRAP_CONTENT,
		ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.FILL_PARENT,
		LinearLayout.LayoutParams.WRAP_CONTENT);

		// 總View框架
		bundleData.myMainView = new LinearLayout(context);
		bundleData.myMainView.setLayoutParams(param);
		bundleData.myMainView.setOrientation(LinearLayout.VERTICAL);
		bundleData.myMainView.setGravity(Gravity.CENTER_HORIZONTAL);
		bundleData.myMainView.setPadding(touchActivityChangeSizeDistance,
		touchActivityChangeSizeDistance,
		touchActivityChangeSizeDistance + 5,
		touchActivityChangeSizeDistance);
		
		// 工具欄總框架
		bundleData.main_frame_pre = new LinearLayout(context);
		bundleData.main_frame_pre.setLayoutParams(param);
		bundleData.main_frame_pre.setOrientation(LinearLayout.VERTICAL);
		// bundleData.main_frame_pre
		// .setBackgroundResource(R.drawable.bg_v_titlebar);
		bundleData.myMainView.addView(bundleData.main_frame_pre);

		// 工具欄位layout
		bundleData.main_frame = new LinearLayout(context);
		bundleData.main_frame.setLayoutParams(param);
		bundleData.main_frame.setOrientation(LinearLayout.HORIZONTAL);
		bundleData.main_frame.setGravity(Gravity.CENTER_HORIZONTAL);
		bundleData.main_frame.setBackgroundResource(R.drawable.bg_v_titlebar);
		bundleData.main_frame_pre.addView(bundleData.main_frame);

		// ImgView -> back
		LinearLayout.LayoutParams tmpLayout = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT,
		LinearLayout.LayoutParams.WRAP_CONTENT);
		tmpLayout.width = param.width;
		tmpLayout.height = param.height;
		tmpLayout.gravity = Gravity.CENTER;
		imgBt_tool[TOOL_BACK].setLayoutParams(tmpLayout);
		imgBt_tool[TOOL_BACK].setImageDrawable(context.getResources()
		.getDrawable(R.drawable.btn_close_default));
		bundleData.main_frame.addView(imgBt_tool[TOOL_BACK]);

		// tools_writepaper & tools_testpaper
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
		ViewGroup.LayoutParams.WRAP_CONTENT,
		ViewGroup.LayoutParams.WRAP_CONTENT);
		rp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		rp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		// rp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		// ImgView -> writepaper
		if (imgBt_tool[TOOL_WRITEPAPER] != null) {
			RelativeLayout writePaper_frame = new RelativeLayout(context);
			writePaper_frame.setLayoutParams(param);
			bundleData.main_frame.addView(writePaper_frame);
			imgBt_tool[TOOL_WRITEPAPER].setBackgroundDrawable(context
			.getResources().getDrawable(R.drawable.tab_v_press));
			writePaper_frame.addView(imgBt_tool[TOOL_WRITEPAPER]);
			writePaperText.setImageDrawable(context.getResources().getDrawable(
			R.drawable.tabfont_dtz_v_press));
			writePaper_frame.addView(writePaperText, rp);

			// ImgView -> testpaper
			RelativeLayout testpaper_frame = new RelativeLayout(context);
			testpaper_frame.setLayoutParams(param);
			bundleData.main_frame.addView(testpaper_frame);
			imgBt_tool[TOOL_TESTPAPER].setBackgroundDrawable(context
			.getResources().getDrawable(R.drawable.tab_v_press));
			testpaper_frame.addView(imgBt_tool[TOOL_TESTPAPER]);
			testPaperText.setImageDrawable(context.getResources().getDrawable(
			R.drawable.tabfont_ysz_v_press));

			testpaper_frame.addView(testPaperText, rp);
		} else {
			RelativeLayout writePaper_frame = new RelativeLayout(context);
			writePaper_frame.setLayoutParams(param);
			bundleData.main_frame.addView(writePaper_frame);
			tv = new TextView(context);
			writePaper_frame.addView(tv);
			// writePaper_frame.addView(writePaperText, rp);

			// ImgView -> testpaper
			RelativeLayout testpaper_frame = new RelativeLayout(context);
			// LinearLayout testpaper_frame = new LinearLayout(context);
			testpaper_frame.setLayoutParams(param);
			bundleData.main_frame.addView(testpaper_frame);
			imgBt_tool[TOOL_TESTPAPER].setBackgroundDrawable(context
			.getResources().getDrawable(R.drawable.tab_v_press));
			testpaper_frame.addView(imgBt_tool[TOOL_TESTPAPER]);
			testPaperText.setImageDrawable(context.getResources().getDrawable(
			R.drawable.tabfont_ysz_v_press));

			testpaper_frame.addView(testPaperText, rp);
		}

		// ImgView -> tools
		imgBt_tool[TOOL_TOOLS].setLayoutParams(tmpLayout);
		imgBt_tool[TOOL_TOOLS].setImageDrawable(context.getResources()
		.getDrawable(R.drawable.btn_tool_default));
		bundleData.main_frame.addView(imgBt_tool[TOOL_TOOLS]);

		// DrawView
		bundleData.drawView_frame = new LinearLayout(context);
		bundleData.drawView_frame.setLayoutParams(param);
		bundleData.drawView_frame.setOrientation(LinearLayout.VERTICAL);
		bundleData.drawView_frame
		.setBackgroundResource(R.drawable.bg_v_drawarea);
		bundleData.myMainView.addView(bundleData.drawView_frame);

		if (drawView[TOOL_WRITEPAPER] != null) {
			drawView[TOOL_WRITEPAPER].setVisibility(View.VISIBLE);
			drawView[TOOL_TESTPAPER].setVisibility(View.GONE);
			bundleData.drawView_frame.addView(drawView[TOOL_WRITEPAPER]);
			bundleData.drawView_frame.addView(drawView[TOOL_TESTPAPER]);
		} else {
			drawView[TOOL_TESTPAPER].setVisibility(View.VISIBLE);
			bundleData.drawView_frame.addView(drawView[TOOL_TESTPAPER]);
		}

		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService(
		"window");
		// 设置LayoutParams(全局变量）相关参数
		// wmParams = ((MyApplication) ((Activity) context).getApplication())
		// .getMywmParams();
		if (wmParams == null) {
			wmParams = new WindowManager.LayoutParams();
		}

		wmParams.type = LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// // 设置Window flag
		// wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
		// | LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗口至左上角，便于调整坐标
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = bundleData.dlgPos.x;
		wmParams.y = bundleData.dlgPos.y;
		wmParams.width = param.width;
		wmParams.height = param.height;
		// 显示myFloatView图像
		wm.addView(bundleData.myMainView, wmParams);

	}
	
	public static void setActivityTouchListener(Context context,
	WebView myShowWebView, ReturnGetStartBundleData bundleData,
	InitializeStartData initializeStartData, ImageView[] imgBt_tool,
	ImageView writePaperText, ImageView testPaperText,
	DrawView[] drawView, boolean[] isDisable_tool, int nowToolChos,
	MyToolsWindow myTools, int chosActivityKind) {

		// bundleData.myMainView.setBackgroundColor(Color.RED - 100);
		if (chosActivityKind == AnswerPaperFun.ACTIVITY_FILL
				|| chosActivityKind == AnswerPaperFun.ACTIVITY_CHOICE) {
			bundleData.myMainView
			.setOnTouchListener(new AnswerPaperOnTouchListener(context,
			bundleData, initializeStartData, drawView,
			imgBt_tool, writePaperText, testPaperText,
			isDisable_tool, null, null, nowToolChos, myTools,
			chosActivityKind) {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
						// touchTime = event.getEventTime();
						// 获取相对View的坐标，即以此View左上角为原点

						g_x = event.getRawX();
						g_y = event.getRawY() - Title_Bar_Height;
						this.preActivityMoveX = event.getRawX();
						this.preActivityMoveY = event.getRawY()
						- Title_Bar_Height;
						Log.i(DebugLog, "touch down > g_x, g_y ("
						+ this.preActivityMoveX + ", "
						+ this.preActivityMoveY + ")");
						// g_y = event.getRawY();
						// 獲取點擊的子View 當前相對座標, 即以此子View為座標原點
						mTouchStartX = event.getX(0);
						mTouchStartY = event.getY(0);
						if (null != bundleData) {
							touchPos = getDlgActivityTouchChangeSize(
							mTouchStartX, mTouchStartY,
							bundleData.realWidth,
							bundleData.realHeight);
						}
						touchMultiPointerFlag = false;
						break;
					case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
						// int tmp_x = (int) event.getX(0);
						// int tmp_y = (int) event.getY(0);
						if (touchMultiPointerFlag) {
							// 判斷是否多點觸控，如果是則放棄移動操作
							return true;
						}
						if (event.getPointerCount() > 1) {
							// 判斷是否是多點觸控(第一次)，如果是則將Flag置true
							touchMultiPointerFlag = true;
							return true;
						}
						if (0 != touchPos) {
							OnChangeTitleBarHeight(parentContext);
							OnChangeDlgActivitySize(parentContext,
							null, bundleData, initialStartData,
							drawView, imgBt_tool, myTools,
							false, chosActivityKind, event,
							event.getRawX(), event.getRawY()
							- Title_Bar_Height,
							touchPos);

							return true;

						}
						if (g_x != event.getRawX()
								|| g_y != event.getRawY()
								- Title_Bar_Height) {
							g_x = event.getRawX();
							g_y = event.getRawY() - Title_Bar_Height;
							// g_y = event.getRawY();
							updateViewPosition(bundleData, 0, 0);

						}
						break;
					case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作

						touchPos = 0;
						break;
					}
					return true;
				}
			});
		}
	}

	public static void reSizeDlgActivitySize(float touchMove_x,
	float touchMove_y, Context context, WebView myShowWebView,
	ReturnGetStartBundleData bundleData,
	InitializeStartData initializeStartData, ImageView[] imgBt_tool,
	ImageView writePaperText, ImageView testPaperText,
	DrawView[] drawView, boolean[] isDisable_tool, int nowToolChos,
	MyToolsWindow myTools, int chosActivityKind) {
		if (null != wmParams) {
			int tmpX = (int) (mTouchStartX - touchMove_x);
			int tmpY = (int) (mTouchStartY - touchMove_y);
			OnChangeTitleBarHeight(parentContext);
			// OnChangeDlgActivitySize(parentContext, null, bundleData,
			// initializeStartData, drawView, imgBt_tool, myTools, false,
			// chosActivityKind);
			wmParams.width = bundleData.realWidth;
			wmParams.height = bundleData.realHeight;
			wm.updateViewLayout(bundleData.myMainView, wmParams); // 刷新显示
		}
		mTouchStartX = touchMove_x;
		mTouchStartY = touchMove_y;
	}

	/**
	* 獲取Dlg形式的Activity Touch事件當中Touch down所在Pos，如下圖:
	* 
	* @describe: 圖中所標位<BR/>
	*            置即為touch down的時候的返回值 <BR/>
	* <BR/>
	*            1-----------------2<BR/>
	*            .|..........................|<BR/>
	*            .|.............0...........|<BR/>
	*            .|..........................|<BR/>
	*            3-----------------4<BR/>
	* <BR/>
	* 
	* @param touch_x
	*            touch down x
	* @param touch_y
	*            touch down y
	* @param width
	*            Dialog activity width
	* @param height
	*            Dialog activity height
	* @return 以此圖數字為返回值
	*/

	public static int getDlgActivityTouchChangeSize(float touch_x,
	float touch_y, int width, int height) {
		int iRet = 0;
		if (touch_x < touchActivityChangeSizeDistance * 6
				&& touch_y < touchActivityChangeSizeDistance * 6) {
			iRet = 1;
		} else if (touch_x > width - touchActivityChangeSizeDistance * 3
				&& touch_y < touchActivityChangeSizeDistance * 6) {
			iRet = 2;
		} else if (touch_x < touchActivityChangeSizeDistance * 6
				&& touch_y > height - touchActivityChangeSizeDistance * 3) {
			iRet = 3;
		} else if (touch_x > width - touchActivityChangeSizeDistance * 3
				&& touch_y > height - touchActivityChangeSizeDistance * 3) {
			iRet = 4;
		}
		Log.i(DebugLog, "SubView:" + touch_x + ", " + touch_y + "--> (" + width
		+ ", " + height + ")" + " iRet = " + iRet);

		return iRet;
	}

	/**
	* 獲取當前Answerpaper window
	* 
	* @return 如果為null 則表示答題紙已經finish
	*/
	public static WindowManager GetActivityWindows() {
		return wm;
	}

	/**
	* 刷新Dialog Answerpaper顯示的位置
	* 
	* @param bundleData
	*            調用者傳入位置信息
	*/
	public static void updateViewPosition(ReturnGetStartBundleData bundleData,
	float divid_value_x, float divid_value_y) {
		// 更新浮动窗口位置参数
		bundleData.dlgPos.x = (int) (g_x - mTouchStartX - divid_value_x);
		bundleData.dlgPos.y = (int) (g_y - mTouchStartY - divid_value_y);
		wmParams.x = bundleData.dlgPos.x;
		wmParams.y = bundleData.dlgPos.y;
		wm.updateViewLayout(bundleData.myMainView, wmParams); // 刷新显示
	}

	/**
	* 初始化所有View 的基本屬性
	* 
	* @param context
	* @param bundleData
	*            調用者傳入信息
	* @param initialStartData
	*            初始化需要的data
	* @param drawView
	*            答題域
	* @param imgBt_tool
	*            所有tools
	* @param writePaperText
	*            答題水印
	* @param testPaperText
	*            草稿水印
	* @param isDisable_tool
	*            工具是否可用
	* @param nowToolChos
	*            當前工具focus
	* @param myTools
	*            工具類
	* @param chosActivityKind
	*            調用Answerpaper類型
	*/
	public static void InitializeAllViewAttrib(Context context,
	ReturnGetStartBundleData bundleData,
	InitializeStartData initialStartData, DrawView[] drawView,
	ImageView[] imgBt_tool, ImageView writePaperText,
	ImageView testPaperText, boolean[] isDisable_tool, int nowToolChos,
	MyToolsWindow myTools, int chosActivityKind) {
		ViewGroup.LayoutParams param = null;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT,
		LinearLayout.LayoutParams.WRAP_CONTENT);
		int[] tmpDrawable = { -1, -1, -1, -1, -1, -1 };

		if (chosActivityKind == ACTIVITY_SOLUTION) {
			tmpDrawable[0] = R.drawable.tabfont_dtz_h_press;
			tmpDrawable[1] = R.drawable.tab_h_press;
			tmpDrawable[2] = R.drawable.tabfont_ysz_h_default;
			tmpDrawable[3] = R.drawable.tab_h_default;
			tmpDrawable[4] = R.drawable.btn_tool_default;
			tmpDrawable[5] = R.drawable.btn_close_default;
		} else if (chosActivityKind == ACTIVITY_FILL
				|| chosActivityKind == ACTIVITY_CHOICE) {
			lp.topMargin = lp.leftMargin = lp.rightMargin = layoutMargin;
			tmpDrawable[0] = R.drawable.tabfont_dtz_v_press;
			tmpDrawable[1] = R.drawable.tab_v_press;
			tmpDrawable[2] = R.drawable.tabfont_ysz_v_default;
			tmpDrawable[3] = R.drawable.tab_v_default;
			tmpDrawable[4] = R.drawable.btn_tool_default;
			tmpDrawable[5] = R.drawable.btn_close_default;
		}
		if (chosActivityKind == ACTIVITY_SOLUTION) {
			bundleData.main_frame_pre.setLayoutParams(lp);
		}

		if (imgBt_tool[TOOL_WRITEPAPER] != null) {
			if (tmpDrawable[0] != -1 && writePaperText != null) {
				writePaperText.setImageDrawable(context.getResources()
				.getDrawable(tmpDrawable[0]));
			}

			if (tmpDrawable[1] != -1) {
				imgBt_tool[TOOL_WRITEPAPER]
				.setBackgroundResource((tmpDrawable[1]));
			}
			imgBt_tool[TOOL_WRITEPAPER]
			.setOnClickListener(new AnswerPaperOnLongClickListener(
			context, bundleData, drawView, imgBt_tool,
			writePaperText, testPaperText, isDisable_tool,
			nowToolChos, myTools, chosActivityKind) {

				@Override
				public void onClick(View v) {
					if (!isDisable_tool[TOOL_WRITEPAPER]) {
						int[] tmpDrawable2 = { -1, -1, -1, -1 };
						nowToolChos = TOOL_WRITEPAPER;
						tmpNowToolsChos = TOOL_WRITEPAPER;
						if (chosActivityKind == ACTIVITY_SOLUTION) {
							tmpDrawable2[0] = R.drawable.tabfont_dtz_h_press;
							tmpDrawable2[1] = R.drawable.tab_h_press;
							tmpDrawable2[2] = R.drawable.tabfont_ysz_h_default;
							tmpDrawable2[3] = R.drawable.tab_h_default;
						} else if (chosActivityKind == ACTIVITY_FILL) {
							tmpDrawable2[0] = R.drawable.tabfont_dtz_v_press;
							tmpDrawable2[1] = R.drawable.tab_v_press;
							tmpDrawable2[2] = R.drawable.tabfont_ysz_v_default;
							tmpDrawable2[3] = R.drawable.tab_v_default;
						}
						if (tmpDrawable2[0] != -1) {
							writePaperText
							.setImageDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[0]));
						}
						if (tmpDrawable2[1] != -1) {
							imgBt_tool[TOOL_WRITEPAPER]
							.setBackgroundDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[1]));
						}
						if (tmpDrawable2[2] != -1) {
							testPaperText
							.setImageDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[2]));
						}
						if (tmpDrawable2[3] != -1) {
							imgBt_tool[TOOL_TESTPAPER]
							.setBackgroundDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[3]));
						}
						nowToolChos = TOOL_WRITEPAPER;
						LoadTestOrWritePaper(drawView, myTools,
						nowToolChos);
					}
					super.onClick(v);
				}

			});
			imgBt_tool[TOOL_WRITEPAPER]
			.setOnTouchListener(new AnswerPaperOnTouchListener(context,
			bundleData, initialStartData, drawView, imgBt_tool,
			writePaperText, testPaperText, isDisable_tool,
			null, null, nowToolChos, myTools, chosActivityKind) {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!isDisable_tool[TOOL_WRITEPAPER]) {
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
							// touchTime = event.getEventTime();
							if (tmpNowToolsChos != TOOL_WRITEPAPER) {
								int[] tmpDrawable = { -1, -1 };
								if (chosActivityKind == ACTIVITY_SOLUTION) {
									tmpDrawable[0] = R.drawable.tabfont_dtz_h_press;
									tmpDrawable[1] = R.drawable.tab_h_press;
								} else if (chosActivityKind == ACTIVITY_FILL) {
									tmpDrawable[0] = R.drawable.tabfont_dtz_v_press;
									tmpDrawable[1] = R.drawable.tab_v_press;
								}
								if (tmpDrawable[0] != -1) {
									writePaperText
									.setImageDrawable(parentContext
									.getResources()
									.getDrawable(
									tmpDrawable[0]));
								}
								if (tmpDrawable[1] != -1) {
									imgBt_tool[TOOL_WRITEPAPER]
									.setBackgroundDrawable(parentContext
									.getResources()
									.getDrawable(
									tmpDrawable[1]));
								}
							} else {
								if (chosActivityKind == ACTIVITY_FILL
										|| chosActivityKind == ACTIVITY_CHOICE) {
									g_x = event.getRawX();
									g_y = event.getRawY()
									- Title_Bar_Height;
									mTouchStartX = event.getX(0);
									mTouchStartY = event.getY(0);
									touchMultiPointerFlag = false;
								}
							}
							break;
						case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
							if (tmpNowToolsChos == TOOL_WRITEPAPER
									&& (chosActivityKind == ACTIVITY_FILL || chosActivityKind == ACTIVITY_CHOICE)) {
								if (touchMultiPointerFlag) {
									// 判斷是否多點觸控，如果是則放棄移動操作
									return true;
								}
								if (event.getPointerCount() > 1) {
									// 判斷是否是多點觸控(第一次)，如果是則將Flag置true
									touchMultiPointerFlag = true;
									return true;
								}
								if (g_x != event.getRawX()
										|| g_y != event.getRawY()
										- Title_Bar_Height) {
									g_x = event.getRawX();
									g_y = event.getRawY()
									- Title_Bar_Height;
									// g_y = event.getRawY();
									updateViewPosition(
									bundleData,
									initialStartData.tools_size,
									0);
								}
							}
							break;
						case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
							if (tmpNowToolsChos != TOOL_WRITEPAPER) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									int[] tmpDrawable2 = { -1, -1 };
									if (chosActivityKind == ACTIVITY_SOLUTION) {
										tmpDrawable2[0] = R.drawable.tabfont_dtz_h_default;
										tmpDrawable2[1] = R.drawable.tab_h_default;
									} else if (chosActivityKind == ACTIVITY_FILL) {
										tmpDrawable2[0] = R.drawable.tabfont_dtz_v_default;
										tmpDrawable2[1] = R.drawable.tab_v_default;
									}
									if (tmpDrawable2[0] != -1) {
										writePaperText
										.setImageDrawable(parentContext
										.getResources()
										.getDrawable(
										tmpDrawable2[0]));
									}
									if (tmpDrawable2[1] != -1) {
										imgBt_tool[TOOL_WRITEPAPER]
										.setBackgroundDrawable(parentContext
										.getResources()
										.getDrawable(
										tmpDrawable2[1]));
									}
								}
							} else {

							}
							break;
						case MotionEvent.ACTION_POINTER_DOWN:
						case MotionEvent.ACTION_POINTER_UP:
							return true;
						}
					}
					return super.onTouch(v, event);
				}

			});
		}

		if (imgBt_tool[TOOL_TESTPAPER] != null) {
			if (tmpDrawable[2] != -1) {
				testPaperText.setImageDrawable(context.getResources()
				.getDrawable(tmpDrawable[2]));
			}
			if (tmpDrawable[3] != -1) {
				imgBt_tool[TOOL_TESTPAPER]
				.setBackgroundResource((tmpDrawable[3]));
			}
			if (chosActivityKind != ACTIVITY_CHOICE) {
				imgBt_tool[TOOL_TESTPAPER]
				.setOnClickListener(new AnswerPaperOnLongClickListener(
				context, bundleData, drawView, imgBt_tool,
				writePaperText, testPaperText, isDisable_tool,
				nowToolChos, myTools, chosActivityKind) {

					@Override
					public void onClick(View v) {
						if (!isDisable_tool[TOOL_TESTPAPER]) {
							int[] tmpDrawable2 = { -1, -1, -1, -1 };
							if (chosActivityKind == ACTIVITY_SOLUTION) {
								tmpDrawable2[0] = R.drawable.tabfont_dtz_h_default;
								tmpDrawable2[1] = R.drawable.tab_h_default;
								tmpDrawable2[2] = R.drawable.tabfont_ysz_h_press;
								tmpDrawable2[3] = R.drawable.tab_h_press;
							} else if (chosActivityKind == ACTIVITY_FILL) {
								tmpDrawable2[0] = R.drawable.tabfont_dtz_v_default;
								tmpDrawable2[1] = R.drawable.tab_v_default;
								tmpDrawable2[2] = R.drawable.tabfont_ysz_v_press;
								tmpDrawable2[3] = R.drawable.tab_v_press;
							}
							nowToolChos = TOOL_TESTPAPER;
							tmpNowToolsChos = TOOL_TESTPAPER;
							if (tmpDrawable2[0] != -1) {
								writePaperText
								.setImageDrawable(parentContext
								.getResources()
								.getDrawable(
								tmpDrawable2[0]));
							}
							imgBt_tool[TOOL_WRITEPAPER]
							.setBackgroundDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[1]));
							testPaperText
							.setImageDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[2]));
							imgBt_tool[TOOL_TESTPAPER]
							.setBackgroundDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[3]));
							LoadTestOrWritePaper(drawView, myTools,
							nowToolChos);
						}
						super.onClick(v);
					}

				});
				imgBt_tool[TOOL_TESTPAPER]
				.setOnTouchListener(new AnswerPaperOnTouchListener(
				context, bundleData, initialStartData,
				drawView, imgBt_tool, writePaperText,
				testPaperText, isDisable_tool, null, null,
				nowToolChos, myTools, chosActivityKind) {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (!isDisable_tool[TOOL_TESTPAPER]) {
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
								// touchTime = event.getEventTime();
								if (tmpNowToolsChos != TOOL_TESTPAPER) {
									int[] tmpDrawable2 = { -1, -1 };
									if (chosActivityKind == ACTIVITY_SOLUTION) {
										tmpDrawable2[0] = R.drawable.tabfont_ysz_h_press;
										tmpDrawable2[1] = R.drawable.tab_h_press;
									} else if (chosActivityKind == ACTIVITY_FILL) {
										tmpDrawable2[0] = R.drawable.tabfont_ysz_v_press;
										tmpDrawable2[1] = R.drawable.tab_v_press;
									}
									testPaperText
									.setImageDrawable(parentContext
									.getResources()
									.getDrawable(
									tmpDrawable2[0]));
									imgBt_tool[TOOL_TESTPAPER]
									.setBackgroundDrawable(parentContext
									.getResources()
									.getDrawable(
									tmpDrawable2[1]));
								} else {
									if (chosActivityKind == ACTIVITY_FILL
											|| chosActivityKind == ACTIVITY_CHOICE) {
										g_x = event.getRawX();
										g_y = event.getRawY()
										- Title_Bar_Height;
										// g_y = event.getRawY();
										mTouchStartX = event.getX(0);
										mTouchStartY = event.getY(0);
										touchMultiPointerFlag = false;
									}
								}
								break;
							case MotionEvent.ACTION_MOVE:
								if (tmpNowToolsChos == TOOL_TESTPAPER
										&& (chosActivityKind == ACTIVITY_FILL || chosActivityKind == ACTIVITY_CHOICE)) {
									if (touchMultiPointerFlag) {
										// 判斷是否多點觸控，如果是則放棄移動操作
										return true;
									}
									if (event.getPointerCount() > 1) {
										// 判斷是否是多點觸控(第一次)，如果是則將Flag置true
										touchMultiPointerFlag = true;
										return true;
									}
									if (g_x != event.getRawX()
											|| g_y != event.getRawY()
											- Title_Bar_Height) {
										g_x = event.getRawX();
										g_y = event.getRawY()
										- Title_Bar_Height;
										// g_y = event.getRawY();
										updateViewPosition(
										bundleData,
										initialStartData.tools_size
										+ imgBt_tool[TOOL_WRITEPAPER]
										.getWidth(),
										0);
									}
								}
								break;
							case MotionEvent.ACTION_UP:
								if (tmpNowToolsChos != TOOL_TESTPAPER) {
									float x, y;
									x = event.getX();
									y = event.getY();
									if ((x >= v.getWidth() || x < 0)
											|| (y >= v.getHeight() || y < 0)) {
										int[] tmpDrawable2 = { -1, -1 };
										if (chosActivityKind == ACTIVITY_SOLUTION) {
											tmpDrawable2[0] = R.drawable.tabfont_ysz_h_default;
											tmpDrawable2[1] = R.drawable.tab_h_default;
										} else if (chosActivityKind == ACTIVITY_FILL) {
											tmpDrawable2[0] = R.drawable.tabfont_ysz_v_default;
											tmpDrawable2[1] = R.drawable.tab_v_default;
										}
										testPaperText
										.setImageDrawable(parentContext
										.getResources()
										.getDrawable(
										tmpDrawable2[0]));
										imgBt_tool[TOOL_TESTPAPER]
										.setBackgroundDrawable(parentContext
										.getResources()
										.getDrawable(
										tmpDrawable2[1]));
									}
								} else {
									if (chosActivityKind == ACTIVITY_FILL
											|| chosActivityKind == ACTIVITY_CHOICE) {
									}
								}
								break;
							case MotionEvent.ACTION_POINTER_DOWN:
							case MotionEvent.ACTION_POINTER_UP:
								return true;
							}
						}
						return super.onTouch(v, event);
					}

				});
			}
		}

		if (imgBt_tool[TOOL_TOOLS] != null) {
			if (tmpDrawable[4] != -1) {
				imgBt_tool[TOOL_TOOLS].setImageDrawable(context.getResources()
				.getDrawable(tmpDrawable[4]));
			}
			imgBt_tool[TOOL_TOOLS]
			.setOnClickListener(new AnswerPaperOnLongClickListener(
			context, bundleData, drawView, imgBt_tool,
			writePaperText, testPaperText, isDisable_tool,
			nowToolChos, myTools, chosActivityKind) {

				@Override
				public void onClick(View v) {
					if (!isDisable_tool[TOOL_TOOLS]) {
						int[] tmpDrawable2 = { -1, -1 };
						if (chosActivityKind == ACTIVITY_SOLUTION) {
							tmpDrawable2[0] = R.drawable.btn_tool_press;
							tmpDrawable2[1] = R.drawable.btn_tool_default;
						} else if (chosActivityKind == ACTIVITY_FILL
								|| chosActivityKind == ACTIVITY_CHOICE) {
							tmpDrawable2[0] = R.drawable.btn_tool_press;
							tmpDrawable2[1] = R.drawable.btn_tool_default;
						}
						if (!myTools.toolsWindow.isShowing()) {
							myTools.toolsWindow.showAtLocation(
							imgBt_tool[TOOL_TOOLS],
							Gravity.BOTTOM, 0, 0);
							myTools.toolsWindowIsShowing = true;
							imgBt_tool[TOOL_TOOLS]
							.setImageDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[0]));
						} else {
							if (myTools.toolsWindow.isShowing()) {
								myTools.toolsWindow.dismiss();
								myTools.toolsWindowIsShowing = false;
								myTools.eraser_click_count = 1;
							}
							myTools.toolsWindow.dismiss();
							myTools.toolsWindowIsShowing = false;
							imgBt_tool[TOOL_TOOLS]
							.setImageDrawable(parentContext
							.getResources()
							.getDrawable(
							tmpDrawable2[1]));
						}

					}
					super.onClick(v);
				}

			});
			imgBt_tool[TOOL_TOOLS]
			.setOnTouchListener(new AnswerPaperOnTouchListener(context,
			bundleData, initialStartData, drawView, imgBt_tool,
			writePaperText, testPaperText, isDisable_tool,
			null, null, nowToolChos, myTools, chosActivityKind) {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!isDisable_tool[TOOL_TOOLS]) {
						if (!myTools.toolsWindow.isShowing()) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								imgBt_tool[TOOL_TOOLS]
								.setImageDrawable(parentContext
								.getResources()
								.getDrawable(
								R.drawable.btn_tool_press));
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									imgBt_tool[TOOL_TOOLS]
									.setImageDrawable(parentContext
									.getResources()
									.getDrawable(
									R.drawable.btn_tool_default));
								}
							}
						}
					}
					return super.onTouch(v, event);
				}

			});
		}

		if (imgBt_tool[TOOL_BACK] != null) {
			imgBt_tool[TOOL_BACK].setImageDrawable(context.getResources()
			.getDrawable(tmpDrawable[5]));
			imgBt_tool[TOOL_BACK]
			.setOnClickListener(new AnswerPaperOnLongClickListener(
			context, bundleData, drawView, imgBt_tool,
			writePaperText, testPaperText, isDisable_tool,
			nowToolChos, myTools, chosActivityKind) {
				@Override
				public void onClick(View v) {
					if (!isDisable_tool[TOOL_BACK]) {
						bundleData.onReturnEvent_flag = true;
						((ImageView) imgBt_tool[TOOL_BACK])
						.setImageDrawable(parentContext
						.getResources()
						.getDrawable(
						R.drawable.btn_close_press));
						if (myTools.toolsWindow.isShowing()) {
							myTools.toolsWindow.dismiss();
							myTools.toolsWindowIsShowing = false;
						}
						// MyFinish(parentContext, drawView, bundleData,
						// chosActivityKind);
						AnswerPaperFun.onMyFinish(parentContext,
						drawView, bundleData, 0, myTools,
						imgBt_tool);
					}
					super.onClick(v);
				}

			});
			imgBt_tool[TOOL_BACK]
			.setOnTouchListener(new AnswerPaperOnTouchListener(context,
			bundleData, initialStartData, drawView, imgBt_tool,
			writePaperText, testPaperText, isDisable_tool,
			null, null, nowToolChos, myTools, chosActivityKind) {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!isDisable_tool[TOOL_BACK]) {
						if (nowToolChos != TOOL_BACK) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								imgBt_tool[TOOL_BACK]
								.setImageDrawable(parentContext
								.getResources()
								.getDrawable(
								R.drawable.btn_close_press));

							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									imgBt_tool[TOOL_BACK]
									.setImageDrawable(parentContext
									.getResources()
									.getDrawable(
									R.drawable.btn_close_default));
								}
							}
						}
					}

					return super.onTouch(v, event);
				}

			});
		}

		ResetToolsWidthHeight(imgBt_tool, initialStartData, chosActivityKind);
	}

	/**
	* 重新打開工具欄
	* 
	* @param myTools
	*            所有工具類
	* @param imgBt_tool
	*            點擊的工具欄圖標
	*/
	public static void restartToolsWindow(MyToolsWindow myTools,
	ImageView[] imgBt_tool) {
		if (!myTools.toolsWindow.isShowing()) {
			myTools.toolsWindow.showAtLocation(imgBt_tool[TOOL_TOOLS],
			Gravity.BOTTOM, 0, 0);
			myTools.toolsWindowIsShowing = true;
			imgBt_tool[TOOL_TOOLS].setImageDrawable(parentContext
			.getResources().getDrawable(R.drawable.btn_tool_press));
		}
	}

	/**
	* 重新set 工具欄寬高以及屬性(當屏幕尺寸改變時調用)
	* 
	* @param imgBt_tool
	*            所有工具
	* @param initialStartData
	*            初始化設置的參數
	* @param chosActivityKind
	*            答題紙類型
	*/
	public static void ResetToolsWidthHeight(ImageView[] imgBt_tool,
	InitializeStartData initialStartData, int chosActivityKind) {
		ViewGroup.LayoutParams param = null;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		LinearLayout.LayoutParams.WRAP_CONTENT,
		LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
		RelativeLayout.LayoutParams.WRAP_CONTENT,
		RelativeLayout.LayoutParams.WRAP_CONTENT);

		if (chosActivityKind == ACTIVITY_SOLUTION) {
			int tmpHeight = 5;
			int imgBtHeight = (initialStartData.writePaper_height - initialStartData.tools_size
			* (tools_count - 2))
			/ 2 - tmpHeight * 2;
			param = imgBt_tool[TOOL_WRITEPAPER].getLayoutParams();
			param.width = initialStartData.tools_size;
			param.height = imgBtHeight;
			imgBt_tool[TOOL_WRITEPAPER].setLayoutParams(param);

			rp.width = initialStartData.tools_size - 2;
			rp.height = imgBtHeight;
			rp.leftMargin = 10;
			rp.rightMargin = -4;
			imgBt_tool[TOOL_WRITEPAPER].setLayoutParams(rp);

			param = imgBt_tool[TOOL_TESTPAPER].getLayoutParams();
			param.width = initialStartData.tools_size;
			param.height = imgBtHeight;
			imgBt_tool[TOOL_TESTPAPER].setLayoutParams(param);

			rp = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
			rp.width = initialStartData.tools_size - 2;
			rp.height = param.height;
			rp.leftMargin = 10;
			rp.rightMargin = -4;
			imgBt_tool[TOOL_TESTPAPER].setLayoutParams(rp);

			int param_count = 0;
			for (param_count = 2; param_count < tools_count; param_count++) {
				param = imgBt_tool[param_count].getLayoutParams();
				param.width = initialStartData.tools_size;
				param.height = initialStartData.tools_size - 10;
				imgBt_tool[param_count].setLayoutParams(param);
			}

			lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = tmpHeight;
			lp.width = initialStartData.tools_size;
			lp.height = initialStartData.tools_size - 10;
			imgBt_tool[TOOL_BACK].setLayoutParams(lp);

			lp = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.topMargin = tmpHeight;
			lp.width = initialStartData.tools_size;
			lp.height = initialStartData.tools_size - 10;
			imgBt_tool[TOOL_TOOLS].setLayoutParams(lp);
		} else if (chosActivityKind == ACTIVITY_FILL) {
			int imgBtWidth = (initialStartData.writePaper_width - initialStartData.tools_size
			* (tools_count - 2))
			/ 2 - layoutMargin;

			if (imgBt_tool[TOOL_WRITEPAPER] != null) {
				param = imgBt_tool[TOOL_WRITEPAPER].getLayoutParams();
				param.width = imgBtWidth;
				param.height = initialStartData.tools_size;
				imgBt_tool[TOOL_WRITEPAPER].setLayoutParams(param);

				rp.width = param.width;
				rp.height = param.height - 2;
				rp.topMargin = 0;
				rp.bottomMargin = -4;
				imgBt_tool[TOOL_WRITEPAPER].setLayoutParams(rp);
			}

			param = imgBt_tool[TOOL_TESTPAPER].getLayoutParams();
			param.width = imgBtWidth;
			param.height = initialStartData.tools_size;
			imgBt_tool[TOOL_TESTPAPER].setLayoutParams(param);

			rp = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
			rp.width = param.width;
			rp.height = param.height - 2;
			rp.topMargin = 0;
			rp.bottomMargin = -4;
			imgBt_tool[TOOL_TESTPAPER].setLayoutParams(rp);

			LinearLayout.LayoutParams tmpLayout = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
			tmpLayout.width = initialStartData.tools_size - 17;
			tmpLayout.height = initialStartData.tools_size - 17;
			tmpLayout.topMargin = 2;
			tmpLayout.bottomMargin = 2;
			tmpLayout.leftMargin = 2;
			tmpLayout.rightMargin = 2;
			tmpLayout.gravity = Gravity.CENTER_VERTICAL;

			int param_count = 0;
			for (param_count = 2; param_count < tools_count; param_count++) {
				imgBt_tool[param_count].setLayoutParams(tmpLayout);
			}

		} else if (chosActivityKind == ACTIVITY_CHOICE) {
			int imgBtWidth = (initialStartData.writePaper_width - initialStartData.tools_size
			* (tools_count - 2))
			/ 2 - layoutMargin * 5 - 14;

			int imgBtWidth2 = (initialStartData.writePaper_width - initialStartData.tools_size
			* (tools_count - 2)) / 2;

			if (imgBt_tool[TOOL_WRITEPAPER] != null) {
				param = imgBt_tool[TOOL_WRITEPAPER].getLayoutParams();
				param.width = imgBtWidth;
				param.height = initialStartData.tools_size;
				imgBt_tool[TOOL_WRITEPAPER].setLayoutParams(param);

				rp.width = param.width;
				rp.height = param.height - 2;
				rp.topMargin = 0;
				rp.bottomMargin = -4;
				imgBt_tool[TOOL_WRITEPAPER].setLayoutParams(rp);
			} else if (tv != null) {
				// param = tv.getLayoutParams();
				// param.width = imgBtWidth;
				// param.height = initialStartData.tools_size;
				// tv.setLayoutParams(param);
				// tv.setBackgroundColor(Color.RED);
				//
				// rp.width = imgBtWidth;
				// rp.height = param.height;
				// rp.topMargin = 0;
				// rp.bottomMargin = -4;
				// tv.setLayoutParams(rp);
			}

			param = imgBt_tool[TOOL_TESTPAPER].getLayoutParams();
			param.width = imgBtWidth;
			param.height = initialStartData.tools_size;
			imgBt_tool[TOOL_TESTPAPER].setLayoutParams(param);

			rp = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
			rp.width = param.width;
			rp.height = param.height - 2;
			rp.topMargin = 0;
			rp.bottomMargin = -4;
			// rp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			imgBt_tool[TOOL_TESTPAPER].setLayoutParams(rp);

			LinearLayout.LayoutParams tmpLayout = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
			tmpLayout.width = initialStartData.tools_size - 17;
			tmpLayout.height = initialStartData.tools_size - 17;
			tmpLayout.topMargin = 2;
			tmpLayout.bottomMargin = 2;
			tmpLayout.leftMargin = 2;
			tmpLayout.rightMargin = 2;
			tmpLayout.gravity = Gravity.CENTER_VERTICAL;

			int param_count = 0;
			for (param_count = 2; param_count < tools_count; param_count++) {
				param = imgBt_tool[param_count].getLayoutParams();
				imgBt_tool[param_count].setLayoutParams(tmpLayout);
				imgBt_tool[param_count].setPadding(2, 2, 2, 2);
			}
		}
	}

	/**
	* 調用加載其他圖形Activity的返回值
	* 
	* @param context
	*            調用者Acitivity
	* @param requestCode
	*            defualt return value
	* @param resultCode
	*            defualt return value
	* @param data
	*            返回加載的其他圖形數據
	* @param myTools
	*            工具欄類
	* @param imgBt_tool
	*            工具欄類icon按鈕
	*/
	public static void otherGraphicActivityResult(Context context,
	int requestCode, int resultCode, Intent data,
	MyToolsWindow myTools, ImageView[] imgBt_tool) {
		AnswerPaperFun.restartToolsWindow(myTools, imgBt_tool);
		Bundle bundle = data.getExtras();
		AnswerPaperFun.otherGraphic.graphicPath = bundle
		.getString(OtherGraphicSetting.GRAPHIC_RESULT_PATH);
		AnswerPaperFun.otherGraphic.graphicName = bundle
		.getString(OtherGraphicSetting.GRAPHIC_RESULT_PICNAME);
		AnswerPaperFun.otherGraphic.chosListKind = bundle
		.getInt(OtherGraphicSetting.GRAPHIC_RESULT_LIST_KIND);
		AnswerPaperFun.otherGraphic.showGraphic = true;

	}

	/**
	* 回調函數來刷新加載選擇的其他圖形
	* 
	* @param context
	*            調用者Activity
	* @param srcPath
	*            加載的圖形路徑
	* @param bundleData
	*            父Activity(調用者)View 信息
	* @param drawView
	*            加載的DrawView
	* @param bitmap_id
	*            加載的圖形名稱(包括擴展名)
	* @param chosListKind
	*            選擇讀取數據的類型
	* 
	*/
	public static void SetSelectBmp(Context context, String srcPath,
	final ReturnGetStartBundleData bundleData, DrawView[] drawView,
	String bitmap_id, int chosListKind) {
		if (context != null) {
			WindowManager wm = (WindowManager) context
			.getSystemService(Context.WINDOW_SERVICE);
			int screen_width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
			int screen_height = wm.getDefaultDisplay().getHeight();// 屏幕高度

			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(srcPath, opt);

			final int minSideLength = Math.min(screen_width, screen_height);
			opt.inSampleSize = DrawViewBitmap.computeSampleSize(opt,
			minSideLength, screen_width * screen_height);
			opt.inJustDecodeBounds = false;
			opt.inInputShareable = true;
			opt.inPurgeable = true;
			try {
				Bitmap select_bmp = BitmapFactory.decodeFile(srcPath, opt);
				String save_path = bundleData.absolute_path
				+ bundleData.answer_id + File.separator + bitmap_id;
				switch (chosListKind) {
				case OtherGraphicSetting.IN_SYSTEM:
					if (null != select_bmp) {
						MyWebViewFun
						.SavePrtBitmap(select_bmp, save_path, false);
					}
					break;
				case OtherGraphicSetting.IN_MYDEFINE:
					// 讀自定義數據
					MyDefineData mydefineData = MyDefineGraphicSetting
					.readMyDefineGraphicFile(srcPath, bitmap_id);
					break;
				case OtherGraphicSetting.IN_COLLECT:
					break;
				}

				// 回調響應刷新介面
				bundleData.c1 = new MyWebViewCaller().new Callee();
				bundleData.c1.SetSelectPic(select_bmp, save_path,
				drawView[AnswerPaperFun.TOOL_WRITEPAPER]);
				bundleData.caller.SetI(bundleData.c1);
				if (drawView[AnswerPaperFun.TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
					switch (chosListKind) {
					case OtherGraphicSetting.IN_SYSTEM:
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSelectPic
						.SetSelectPicIntoView(
						bundleData.caller.GetSelectPic(),
						bundleData.caller.GetSelectPicPath(),
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvBitmap,
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSize);
						break;
					case OtherGraphicSetting.IN_MYDEFINE:
						break;
					}
				} else if (drawView[AnswerPaperFun.TOOL_TESTPAPER]
						.getVisibility() == View.VISIBLE) {
					switch (chosListKind) {
					case OtherGraphicSetting.IN_SYSTEM:
						drawView[AnswerPaperFun.TOOL_TESTPAPER].dvSelectPic
						.SetSelectPicIntoView(
						bundleData.caller.GetSelectPic(),
						bundleData.caller.GetSelectPicPath(),
						drawView[AnswerPaperFun.TOOL_TESTPAPER].dvBitmap,
						drawView[AnswerPaperFun.TOOL_TESTPAPER].dvSize);
						break;
					}
				}

				bundleData.caller.RefreshMyView();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	* 判斷在父Activity(調用者)OnResume重新加載Activity時是否是在調用加載其他
	* 圖形Activity返回時的OnResume，如果是，那麼對此加載的圖形進行操作。
	* 
	* @param drawView
	*            答題紙的答題介面
	* @param startOrEnd
	*            因為此方法需要調用2次，第一次是判斷是否調用過OtherGraphicActivity; 第二次是進行圖片加載的操作
	* @param endChos
	*            第二次調用時才有效，判斷答題紙還是演算紙上加載圖形
	* @return 第一次調用有效，判斷是否調用OtherGraphicActivity來加載其他圖形
	*/
	public static int onResumeSetting(DrawView[] drawView, int startOrEnd,
	int endChos, MyToolsWindow myTools, int nowToolChos) {
		int status = 0;
		switch (startOrEnd) {
		case 1:
			if (null != drawView[AnswerPaperFun.TOOL_WRITEPAPER]
					&& View.VISIBLE == drawView[AnswerPaperFun.TOOL_WRITEPAPER]
					.getVisibility()) {
				if (otherGraphic.showGraphic) {
					status = 1;
				}
			} else if (null != drawView[AnswerPaperFun.TOOL_TESTPAPER]
					&& View.VISIBLE == drawView[AnswerPaperFun.TOOL_TESTPAPER]
					.getVisibility()) {
				if (otherGraphic.showGraphic) {
					status = 2;
				}
			}
			break;
		case 2:
			switch (endChos) {
			case 1:
				drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvBitmap.cacheBitmapShow = true;
				break;
			case 2:
				drawView[AnswerPaperFun.TOOL_TESTPAPER].dvBitmap.cacheBitmapShow = true;
				break;
			}
			break;
		}

		return status;
	}

	/**
	* 加載答題紙或者草稿紙(切換答題紙、草稿紙時使用)
	* 
	* @param drawView
	*            切換的答題域
	* @param myTools
	*            工具類
	* @param chos
	*            選擇要切換的狀態
	*/
	public static void LoadTestOrWritePaper(DrawView[] drawView,
	MyToolsWindow myTools, int chos) {
		if (drawView[TOOL_WRITEPAPER] != null
				&& drawView[TOOL_TESTPAPER] != null) {
			if (chos == TOOL_WRITEPAPER) {
				drawView[TOOL_TESTPAPER].setVisibility(View.GONE);
				drawView[TOOL_WRITEPAPER].setVisibility(View.VISIBLE);

				if (IsClearAllData(drawView[TOOL_WRITEPAPER].dvSaveData,
							myTools, IS_CLEAR_START)) {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
					// 答題水印
					// drawView[TOOL_WRITEPAPER].dvSaveData.AddWaterMark(
					// drawView[TOOL_WRITEPAPER].dvSize,
					// drawView[TOOL_WRITEPAPER].dvBitmap);
					isAddWaterMark = true;
					drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
				} else {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = false;
				}
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos,
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]);
			} else if (chos == TOOL_TESTPAPER) {
				drawView[TOOL_WRITEPAPER].setVisibility(View.GONE);
				drawView[TOOL_TESTPAPER].setVisibility(View.VISIBLE);

				if (IsClearAllData(drawView[TOOL_TESTPAPER].dvSaveData,
							myTools, IS_CLEAR_START)) {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
				} else {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
				}
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos,
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]);
			}
		}
	}

	/**
	* 設置答題時使用的工具Focus
	* 
	* @param context
	* @param imgBt_tool
	*            工具
	* @param writePaperText
	*            水印
	* @param testPaperText
	* @param tool_chos
	*            Focus指定位置
	* @param chosActivityKind
	*            Answerpaper類型
	*/
	public static void SetOthersToolFocus(Context context,
	ImageView[] imgBt_tool, ImageView writePaperText,
	ImageView testPaperText, int tool_chos, int chosActivityKind) {
		int count = 0;
		for (count = 0; count < tools_count; count++) {
			int tmpDrawable = -1;
			int tmpDrawable2 = -1;
			if (imgBt_tool[count] == null) {
				continue;
			}
			switch (count) {
			case TOOL_WRITEPAPER:
				if (count == tool_chos) {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.tab_h_press;
						tmpDrawable2 = R.drawable.tabfont_dtz_h_press;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.tab_v_press;
						tmpDrawable2 = R.drawable.tabfont_dtz_v_press;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.tab_v_press;
					}
				} else {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.tab_h_default;
						tmpDrawable2 = R.drawable.tabfont_dtz_h_default;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.tab_v_default;
						tmpDrawable2 = R.drawable.tabfont_dtz_v_default;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.tab_v_default;
					}
				}
				if (writePaperText != null && tmpDrawable2 != -1) {
					writePaperText.setImageDrawable(context.getResources()
					.getDrawable(tmpDrawable2));
				}

				if (tmpDrawable != -1) {
					imgBt_tool[count].setBackgroundDrawable(context
					.getResources().getDrawable(tmpDrawable));
				}
				break;
			case TOOL_TESTPAPER:
				if (count == tool_chos) {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.tab_h_press;
						tmpDrawable2 = R.drawable.tabfont_ysz_h_press;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.tab_v_press;
						tmpDrawable2 = R.drawable.tabfont_ysz_v_press;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.tab_v_press;
					}
				} else {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.tab_h_default;
						tmpDrawable2 = R.drawable.tabfont_ysz_h_default;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.tab_v_default;
						tmpDrawable2 = R.drawable.tabfont_ysz_v_default;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.tab_v_press;
					}
				}
				if (testPaperText != null && tmpDrawable2 != -1) {
					testPaperText.setImageDrawable(context.getResources()
					.getDrawable(tmpDrawable2));
				}

				if (tmpDrawable != -1) {
					imgBt_tool[count].setBackgroundDrawable(context
					.getResources().getDrawable(tmpDrawable));
				}
				break;
			case TOOL_TOOLS:
				if (count == tool_chos) {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.btn_tool_press;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.btn_tool_press;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.btn_tool_press;
					}
				} else {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.btn_tool_default;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.btn_tool_default;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.btn_tool_default;
					}
				}

				if (tmpDrawable != -1) {
					imgBt_tool[count].setImageDrawable(context.getResources()
					.getDrawable(tmpDrawable));
				}
				break;
			case TOOL_BACK:
				if (count == tool_chos) {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.btn_close_press;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.btn_close_press;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.btn_close_press;
					}
				} else {
					if (chosActivityKind == ACTIVITY_SOLUTION) {
						tmpDrawable = R.drawable.btn_close_default;
					} else if (chosActivityKind == ACTIVITY_FILL) {
						tmpDrawable = R.drawable.btn_close_default;
					} else if (chosActivityKind == ACTIVITY_CHOICE) {
						tmpDrawable = R.drawable.btn_close_default;
					}
				}

				if (tmpDrawable != -1)
				imgBt_tool[count].setImageDrawable(context.getResources()
				.getDrawable(tmpDrawable));
				break;
			}
		}
	}

	/**
	* 判斷答題域是否被清空
	* 
	* @param dvData
	*            答題信息
	* @param myTools
	*            答題工具類
	* @param chos
	*            當前工具Focus
	* @return true答題信息清空; false沒有清空
	*/
	public static boolean IsClearAllData(DrawViewSaveData dvData,
	MyToolsWindow myTools, int chos) {
		boolean bRet = false;
		if (chos == IS_CLEAR_START) {
			if (dvData.LineInfo == null) {
				bRet = true;
			} else if (dvData.LineInfo.size() == 0) {
				bRet = true;
			}
		} else if (chos == IS_CLEAR_CHECK) {
			if (dvData.one_line_info.SavePaintPos.size() != 0
					&& myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]) {
				// myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
				// myTools.SetOthersToolFocus(myTools.nowToolChos,
				// myTools.tools_size, myTools.preToolChos, false);
				bRet = false;
			} else if (dvData.LineInfo.size() != 0
					&& myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]) {
				// myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
				// myTools.SetOthersToolFocus(myTools.nowToolChos,
				// myTools.tools_size, myTools.preToolChos, false);
				bRet = false;
			}
		} else if (chos == IS_CHECK_DATA) {
			if (dvData.LineInfo.size() != 0) {

				bRet = false;
			} else if (dvData.one_line_info.SavePaintPos.size() != 0) {
				bRet = false;
			} else {
				bRet = true;
			}
		}

		if (bRet) {
			if (myTools != null
					&& myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]) {
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos, true);
			}
		} else {
			if (myTools != null
					&& myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]) {
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos, false);
			}
		}
		// myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = bRet;

		return bRet;
	}

	// 初始化創建Answerpaper時使用的配置信息類
	public static class InitializeStartData {
		public int tools_size; // 顯示的工具icon大小
		public int myWV_height; // WebView顯示高度
		public int writePaper_width; // 答題紙寬度
		public int writePaper_height; // 答題紙高度
		public int screen_divid_value_x; // divid value
		public int screen_divid_value_y;

		public InitializeStartData() {
			tools_size = myWV_height = writePaper_width = writePaper_height = screen_divid_value_x = screen_divid_value_y = 0;
		}
	}

	/**
	* 初始化Answerpaper View 屬性
	* 
	* @param initialStartData
	* @param bundleData
	* @param chosActivityKind
	*/
	public static void InitializeStart(InitializeStartData initialStartData,
	ReturnGetStartBundleData bundleData, int chosActivityKind) {
		if (chosActivityKind == ACTIVITY_SOLUTION) {
			initialStartData.tools_size = bundleData.realHeight / 10;
			if (initialStartData.tools_size < 48) {
				initialStartData.tools_size = 48;
			} else if (initialStartData.tools_size > 60) {
				initialStartData.tools_size = 60;
			}
			initialStartData.myWV_height = bundleData.realHeight / 2;
			initialStartData.writePaper_width = bundleData.realWidth
			- initialStartData.tools_size;
			initialStartData.writePaper_height = bundleData.realHeight
			- initialStartData.myWV_height;
		} else if (chosActivityKind == ACTIVITY_FILL) {
			initialStartData.screen_divid_value_x = 7;
			initialStartData.screen_divid_value_y = 10;
			initialStartData.tools_size = bundleData.realHeight / 10;
			if (initialStartData.tools_size < 60) {
				initialStartData.tools_size = 60;
			} else if (initialStartData.tools_size > 70) {
				initialStartData.tools_size = 70;
			}
			initialStartData.myWV_height = 0;
			initialStartData.writePaper_width = bundleData.realWidth;
			initialStartData.writePaper_height = bundleData.realHeight
			- initialStartData.tools_size;
		} else if (chosActivityKind == ACTIVITY_CHOICE) {
			initialStartData.screen_divid_value_x = 7;
			initialStartData.screen_divid_value_y = 10;
			initialStartData.tools_size = bundleData.realHeight / 10;
			if (initialStartData.tools_size < 60) {
				initialStartData.tools_size = 60;
			} else if (initialStartData.tools_size > 70) {
				initialStartData.tools_size = 65;
			}
			initialStartData.myWV_height = 0;
			initialStartData.writePaper_width = bundleData.realWidth;
			initialStartData.writePaper_height = bundleData.realHeight
			- initialStartData.tools_size;
		}
	}

	/**
	* 工具切換時動作
	* 
	* @param reFresh
	*            是否刷新所有工具
	* @param drawView
	*            答題域
	* @param isMove
	*            是否是移動紙張
	* @param isSetPen
	*            筆還是橡皮
	* @param myTools
	*            工具類
	* @return 當前被操作的工具
	*/
	public static int OnChangeToolsFocus(boolean reFresh, DrawView[] drawView,
	boolean[] isMove, boolean[] isSetPen, MyToolsWindow myTools) {
		int bRet = -1;
		int focus = myTools.GetChosItem();
		if (drawView[TOOL_WRITEPAPER] != null
				&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
			drawView[TOOL_WRITEPAPER].SetToolsFocus(focus);
			drawView[TOOL_WRITEPAPER].dvPen.myEraserWidth = drawView[TOOL_WRITEPAPER].dvEraser
			.setEraseWidth(DrawViewEraser.eraseWidth,
			drawView[TOOL_WRITEPAPER].dvPen.paint);
			if (drawView[TOOL_WRITEPAPER].isShown() && reFresh) {
				drawView[TOOL_WRITEPAPER].ChangeDrawKind();
			}

			boolean check_data = false;
			check_data = IsClearAllData(drawView[TOOL_WRITEPAPER].dvSaveData,
			myTools, IS_CHECK_DATA);
			if (check_data) {
				isAddWaterMark = true;
				drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
			} else {
				isAddWaterMark = false;
				drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
			}

			check_data = IsClearAllData(drawView[TOOL_WRITEPAPER].dvSaveData,
			myTools, IS_CHECK_DATA);
		}

		if (drawView[TOOL_TESTPAPER] != null
				&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
			drawView[TOOL_TESTPAPER].SetToolsFocus(focus);
			drawView[TOOL_TESTPAPER].dvPen.myEraserWidth = drawView[TOOL_TESTPAPER].dvEraser
			.setEraseWidth(DrawViewEraser.eraseWidth,
			drawView[TOOL_TESTPAPER].dvPen.paint);
			if (drawView[TOOL_TESTPAPER].isShown() && reFresh) {
				drawView[TOOL_TESTPAPER].ChangeDrawKind();
			}

			IsClearAllData(drawView[TOOL_TESTPAPER].dvSaveData, myTools,
			IS_CLEAR_CHECK);
		}

		switch (focus) {
		case MyToolsWindow.TOOL_ERASER:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = false;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_PEN);
			}
			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = false;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_PEN);
			}

			break;
		case MyToolsWindow.TOOL_BAOPEN:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0,
				MyToolsWindow.TOOL_BAOPEN);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_PEN);
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0,
				MyToolsWindow.TOOL_BAOPEN);
				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_PEN);
			}

			break;
		case MyToolsWindow.TOOL_PEN:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER]
				.SetPenWidth(0, MyToolsWindow.TOOL_PEN);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_PEN);
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, MyToolsWindow.TOOL_PEN);
				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_PEN);
			}

			break;
		case MyToolsWindow.TOOL_DRAW_LINE:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_LINE);
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_LINE);
			}
			break;
		case MyToolsWindow.TOOL_DRAW_OVAL:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_OVAL);
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_OVAL);
			}
			break;
		case MyToolsWindow.TOOL_DRAW_RECTANGLE:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_RECTANGLE);
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_RECTANGLE);
			}
			break;
		case MyToolsWindow.TOOL_DRAW_TRIANGLE:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_WRITEPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_TRIANGLE);
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, myTools.preToolChos);

				drawView[TOOL_TESTPAPER].dvGeometry
				.SetGeometryOrPen(Geometry.DRAW_TRIANGLE);
			}
			break;
		case MyToolsWindow.TOOL_DRAW_UNDO:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag
					&& drawView[TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_WRITEPAPER].dvSaveData.ClearLastPath();
				drawView[TOOL_WRITEPAPER].dvSaveData.ResetMaxPos(0, 0);
				drawView[TOOL_WRITEPAPER].MoveReDraw(true);
				if (drawView[TOOL_WRITEPAPER].dvSaveData.GetTotalPathCount() == 0) {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
					myTools.SetOthersToolFocus(myTools.nowToolChos,
					myTools.tools_size, myTools.preToolChos, true);
					// 答題水印
					isAddWaterMark = true;
					drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
					bRet = 1;
				} else {
					myTools.SetOthersToolFocus(myTools.nowToolChos,
					myTools.tools_size, myTools.preToolChos, false);
					isAddWaterMark = false;
					bRet = 0;
				}
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag
					&& drawView[TOOL_TESTPAPER].getVisibility() == View.VISIBLE) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, myTools.preToolChos);

				drawView[TOOL_TESTPAPER].dvSaveData.ClearLastPath();
				drawView[TOOL_TESTPAPER].MoveReDraw(true);
				if (drawView[TOOL_TESTPAPER].dvSaveData.GetTotalPathCount() == 0) {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
					myTools.SetOthersToolFocus(myTools.nowToolChos,
					myTools.tools_size, myTools.preToolChos, true);
					bRet = 1;
				} else {
					myTools.SetOthersToolFocus(myTools.nowToolChos,
					myTools.tools_size, myTools.preToolChos, false);
					bRet = 0;
				}
			}
			break;
		case MyToolsWindow.TOOL_DRAW_CLEAN:
			if (drawView[TOOL_WRITEPAPER] != null
					&& !drawView[TOOL_WRITEPAPER].isFinishFlag
					&& drawView[TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_WRITEPAPER].dvSaveData.ClearAllData();
				if (drawView[TOOL_WRITEPAPER].dvSelectPic.showBmp != null) {
					drawView[TOOL_WRITEPAPER].dvSelectPic.ClearShowBitmap();
					drawView[TOOL_WRITEPAPER].dvBitmap.cacheBitmapShow = false;
				}
				if (drawView[TOOL_WRITEPAPER].dvSaveData.GetTotalPathCount() == 0) {
					drawView[TOOL_WRITEPAPER].dvSaveData.ResetMaxPos(0, 0);
					drawView[TOOL_WRITEPAPER].MoveReDraw(true);
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
					// 答題水印
					isAddWaterMark = true;
					drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
				}
			}

			if (drawView[TOOL_TESTPAPER] != null
					&& !drawView[TOOL_TESTPAPER].isFinishFlag
					&& drawView[TOOL_TESTPAPER].getVisibility() == View.VISIBLE) {
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER].SetPenWidth(0, myTools.preToolChos);
				drawView[TOOL_TESTPAPER].dvSaveData.ClearAllData();
				if (drawView[TOOL_TESTPAPER].dvSelectPic.showBmp != null) {
					drawView[TOOL_TESTPAPER].dvSelectPic.ClearShowBitmap();
					drawView[TOOL_TESTPAPER].dvBitmap.cacheBitmapShow = false;
				}
				if (drawView[TOOL_TESTPAPER].dvSaveData.GetTotalPathCount() == 0) {
					drawView[TOOL_TESTPAPER].MoveReDraw(true);
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
				}
			}
			break;
		}

		if (drawView[TOOL_WRITEPAPER] != null
				&& !drawView[TOOL_WRITEPAPER].isFinishFlag
				&& drawView[TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
			if (drawView[TOOL_WRITEPAPER].dvSaveData.GetTotalPathCount() == 0) {
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos, true);
				// 答題水印
				isAddWaterMark = true;
			} else {
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos, false);
				isAddWaterMark = false;
			}
			drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
		}

		if (drawView[TOOL_TESTPAPER] != null
				&& !drawView[TOOL_TESTPAPER].isFinishFlag
				&& drawView[TOOL_TESTPAPER].getVisibility() == View.VISIBLE) {
			if (drawView[TOOL_TESTPAPER].dvSaveData.GetTotalPathCount() == 0) {
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos, true);
				bRet = 1;
			} else {
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos, false);
				bRet = 0;
			}
		}

		return bRet;
	}

	/**
	* 改變titlebar 高度(當屏幕寬高改變時調用)
	* 
	* @param context
	*/
	public static void OnChangeTitleBarHeight(Context context) {
		Rect rect = new Rect();
		((Activity) context).getWindow().getDecorView()
		.getWindowVisibleDisplayFrame(rect);// /取得整个视图部分,注意，如果你要
		int top = rect.top;// //状态栏的高度，所以rect.height,rect.width分别是系统的高度的宽度
		View v1 = ((Activity) context).getWindow().findViewById(
		Window.ID_ANDROID_CONTENT);// /获得根视图
		int top2 = v1.getTop();// /状态栏标题栏的总高度,所以标题栏的高度为top2-top
		int tmpHeight = Math.abs(top2 - top);
		if (tmpHeight > 0) {
			Title_Bar_Height = tmpHeight;
		}
	}

	/**
	* 設置所有工具的Touch, Click Listener
	* 
	* @param context
	* @param imgBt_tool
	* @param drawView
	* @param isMove
	* @param isSetPen
	* @param myTools
	* @param initialStartData
	* @param bundleData
	* @param chosActivityKind
	*/
	public static void SetToolsListener(Context context,
	final ImageView[] imgBt_tool, final DrawView[] drawView,
	final boolean[] isMove, final boolean[] isSetPen,
	final MyToolsWindow myTools,
	final InitializeStartData initialStartData,
	ReturnGetStartBundleData bundleData, final int chosActivityKind) {

		if (drawView[TOOL_WRITEPAPER] != null) {
			drawView[TOOL_WRITEPAPER].SetToolsFocus(myTools.GetChosItem());
		}

		if (drawView[TOOL_TESTPAPER] != null) {
			drawView[TOOL_TESTPAPER].SetToolsFocus(myTools.GetChosItem());
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_BAOPEN
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_BAOPEN]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_BAOPEN]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_BAOPEN]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_BAOPEN) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_BAOPEN]
								.setBackgroundResource(R.drawable.baopen_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_BAOPEN]
									.setBackgroundResource(R.drawable.baopen_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_BAOPEN]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_BAOPEN]) {
						myTools.preToolChos = myTools.nowToolChos = MyToolsWindow.TOOL_BAOPEN;
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size, -1, false);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_PEN
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_PEN]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_PEN]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_PEN]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_PEN) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_PEN]
								.setBackgroundResource(R.drawable.pen_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_PEN]
									.setBackgroundResource(R.drawable.pen_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_PEN]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_PEN]) {
						myTools.preToolChos = myTools.nowToolChos = MyToolsWindow.TOOL_PEN;
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size, -1, false);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
					}
				}
			});

		}

		if (myTools.tools_size > MyToolsWindow.TOOL_ERASER
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_ERASER]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_ERASER]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_ERASER]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_ERASER) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_ERASER]
								.setBackgroundResource(R.drawable.eraser_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_ERASER]
									.setBackgroundResource(R.drawable.eraser_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_ERASER]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_ERASER]) {
						myTools.nowToolChos = MyToolsWindow.TOOL_ERASER;
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size, -1, false);
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);

						if (myTools.eraser_click_count == 0) {
							myTools.eraser_click_count = 1;
						} else if (myTools.eraser_click_count == 1) {
							if (chosActivityKind == ACTIVITY_SOLUTION) {
								myTools.eraseWindow
								.showAsDropDown(
								imgBt_tool[TOOL_TOOLS],
								initialStartData.writePaper_width / 2 - 120,
								initialStartData.writePaper_height / 2 - 120);
							} else if (chosActivityKind == ACTIVITY_FILL
									|| chosActivityKind == ACTIVITY_CHOICE) {
								myTools.eraseWindow
								.showAsDropDown(
								imgBt_tool[TOOL_BACK],
								initialStartData.writePaper_width / 2 - 172,
								initialStartData.writePaper_height / 2 - 132);
							}
							myTools.eraser_click_count = 2;
						} else if (myTools.eraser_click_count == 2) {
							if (!myTools.eraseWindow.isShowing()) {
								if (chosActivityKind == ACTIVITY_SOLUTION) {
									myTools.eraseWindow
									.showAsDropDown(
									imgBt_tool[TOOL_TOOLS],
									initialStartData.writePaper_width / 2 - 120,
									initialStartData.writePaper_height / 2 - 120);
								} else if (chosActivityKind == ACTIVITY_FILL
										|| chosActivityKind == ACTIVITY_CHOICE) {
									myTools.eraseWindow
									.showAsDropDown(
									imgBt_tool[TOOL_BACK],
									initialStartData.writePaper_width / 2 - 172,
									initialStartData.writePaper_height / 2 - 132);
								}
							} else {
								myTools.eraseWindow.dismiss();
								myTools.eraser_click_count = 1;
							}
						}
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_LINE
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_LINE]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_LINE]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_LINE]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_LINE) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_LINE]
								.setBackgroundResource(R.drawable.line_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_LINE]
									.setBackgroundResource(R.drawable.line_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_LINE]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_LINE]) {
						if (!toolsIsFocus) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_LINE;
							toolsIsFocus = true;
						} else if (toolsIsFocus
								&& myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_LINE) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_LINE;
							toolsIsFocus = true;
						} else {
							myTools.nowToolChos = myTools.preToolChos;
							toolsIsFocus = false;
						}
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size,
						myTools.preToolChos, false);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_OVAL
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OVAL]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OVAL]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_OVAL]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_OVAL) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OVAL]
								.setBackgroundResource(R.drawable.oval_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OVAL]
									.setBackgroundResource(R.drawable.oval_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OVAL]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_OVAL]) {
						if (!toolsIsFocus) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_OVAL;
							toolsIsFocus = true;
						} else if (toolsIsFocus
								&& myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_OVAL) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_OVAL;
							toolsIsFocus = true;
						} else {
							myTools.nowToolChos = myTools.preToolChos;
							toolsIsFocus = false;
						}
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size,
						myTools.preToolChos, false);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_RECTANGLE
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_RECTANGLE]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_RECTANGLE]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_RECTANGLE]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_RECTANGLE) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_RECTANGLE]
								.setBackgroundResource(R.drawable.rectangle_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_RECTANGLE]
									.setBackgroundResource(R.drawable.rectangle_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_RECTANGLE]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_RECTANGLE]) {
						if (!toolsIsFocus) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_RECTANGLE;
							toolsIsFocus = true;
						} else if (toolsIsFocus
								&& myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_RECTANGLE) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_RECTANGLE;
							toolsIsFocus = true;
						} else {
							myTools.nowToolChos = myTools.preToolChos;
							toolsIsFocus = false;
						}
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size,
						myTools.preToolChos, false);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_TRIANGLE
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_TRIANGLE]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_TRIANGLE]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_TRIANGLE]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_TRIANGLE) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_TRIANGLE]
								.setBackgroundResource(R.drawable.triangle_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_TRIANGLE]
									.setBackgroundResource(R.drawable.triangle_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_TRIANGLE]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_TRIANGLE]) {
						if (!toolsIsFocus) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_TRIANGLE;
							toolsIsFocus = true;
						} else if (toolsIsFocus
								&& myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_TRIANGLE) {
							myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_TRIANGLE;
							toolsIsFocus = true;
						} else {
							myTools.nowToolChos = myTools.preToolChos;
							toolsIsFocus = false;
						}
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size,
						myTools.preToolChos, false);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_UNDO
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_UNDO]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_UNDO]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_UNDO) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_UNDO]
								.setBackgroundResource(R.drawable.undo_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_UNDO]
									.setBackgroundResource(R.drawable.undo_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_UNDO]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO]) {
						int tmpNowChos = myTools.nowToolChos;
						int tmpChosRefresh = -1;
						myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_UNDO;
						myTools.eraser_click_count = 0;
						tmpChosRefresh = OnChangeToolsFocus(true,
						drawView, isMove, isSetPen, myTools);
						myTools.nowToolChos = tmpNowChos;
						if (tmpChosRefresh == 0) {
							myTools.SetOthersToolFocus(
							myTools.nowToolChos,
							myTools.tools_size,
							myTools.preToolChos, false);
						} else if (tmpChosRefresh == 1) {
							myTools.SetOthersToolFocus(
							myTools.nowToolChos,
							myTools.tools_size,
							myTools.preToolChos, true);
						}
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_CLEAN
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_CLEAN]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_CLEAN]
			.setOnTouchListener(new Button.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_CLEAN) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_CLEAN]
								.setBackgroundResource(R.drawable.delete_press);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_CLEAN]
									.setBackgroundResource(R.drawable.delete_default);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_CLEAN]
			.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]) {
						int tmpNowChos = myTools.nowToolChos;
						myTools.nowToolChos = MyToolsWindow.TOOL_DRAW_CLEAN;
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size, -1, true);
						myTools.eraser_click_count = 0;
						OnChangeToolsFocus(true, drawView, isMove,
						isSetPen, myTools);
						myTools.nowToolChos = tmpNowChos;
						myTools.SetOthersToolFocus(myTools.nowToolChos,
						myTools.tools_size,
						myTools.preToolChos, true);
					}
				}
			});
		}

		if (myTools.tools_size > MyToolsWindow.TOOL_DRAW_OTHERS
				&& null != myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OTHERS]) {
			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OTHERS]
			.setOnTouchListener(new Button.OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_OTHERS]) {
						if (myTools.nowToolChos != MyToolsWindow.TOOL_DRAW_OTHERS) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OTHERS]
								.setBackgroundResource(R.drawable.tools_default);
							} else if (event.getAction() == MotionEvent.ACTION_UP) {
								float x, y;
								x = event.getX();
								y = event.getY();
								if ((x >= v.getWidth() || x < 0)
										|| (y >= v.getHeight() || y < 0)) {
									myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OTHERS]
									.setBackgroundResource(R.drawable.tools_disabled);
								}
							}
						}
					}
					return false;
				}
			});

			myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OTHERS]
			.setOnClickListener(new Button.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!myTools.isDisable[MyToolsWindow.TOOL_DRAW_OTHERS]) {
						// open other graphics main window
						if (null != otherGraphic) {
							myTools.imgBt_tool[MyToolsWindow.TOOL_DRAW_OTHERS]
							.setBackgroundResource(R.drawable.tools_disabled);
							Intent intent = new Intent(
							StartAnswerPaperSetting.ACTION_OTHER_GRAPHIC);
							if (0 < parentContext.getPackageManager()
									.queryIntentActivities(intent, 0)
									.size()) {
								((Activity) parentContext)
								.startActivityForResult(intent,
								0);
								((Activity) parentContext)
								.overridePendingTransition(
								Animation.INFINITE,
								Animation.INFINITE);
							}
						}
					} else {
						if (null != otherGraphic) {
							if (otherGraphic.otherGraphicsMainWnd
									.isShowing()) {
								otherGraphic.otherGraphicsMainWnd
								.dismiss();
							}
						}
					}
				}
			});
		}

	}

	/**
	* 調用OtherGraphic Activity返回的結果
	* 
	* @param resultCode
	* @param intent
	* @return 返回結果類
	*/
	public OtherGraphicResultData otherGraphicActivityForResult(int resultCode,
	Intent intent) {
		OtherGraphicResultData ret = new OtherGraphicResultData();

		return ret;
	}

	/**
	* @describe 保存答題信息線程
	* 
	*/
	public class SaveDataThread extends Thread {
		Context context;
		DrawView[] drawView;
		ReturnGetStartBundleData bundleData;
		int chosActivityKind;

		public SaveDataThread(Context context, DrawView[] drawView,
		ReturnGetStartBundleData bundleData, int chosActivityKind) {
			this.context = context;
			this.drawView = drawView;
			this.bundleData = bundleData;
			this.chosActivityKind = chosActivityKind;
		}

		public void run() {
			// Looper.prepare();
			MyFinish(context, drawView, bundleData, chosActivityKind);
			mHandler.sendEmptyMessage(0);
			// Looper.loop();
		}
	}

	// 退出Answerpaper時退出的window
	static MySaveWindow exitWindow;

	// 所有線程返回的message
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// Log.d(">>>>>Mhandler", "Save answer data");
				if (null != otherGraphic) {
					if (otherGraphic.otherGraphicsMainWnd.isShowing()) {
						otherGraphic.otherGraphicsMainWnd.dismiss();
					}
					otherGraphic = null;
				}
				if (null != exitWindow) {
					if (exitWindow.myExitWindow.isShowing()) {
						exitWindow.myExitWindow.dismiss();
					}
					exitWindow = null;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	* Answerpaper finish
	* 
	* @param context
	* @param drawView
	* @param bundleData
	* @param chosActivityKind
	* @param myTools
	* @param imgBt_tool
	*/
	public static void onMyFinish(Context context, DrawView[] drawView,
	ReturnGetStartBundleData bundleData, int chosActivityKind,
	MyToolsWindow myTools, ImageView[] imgBt_tool) {
		if (null != exitWindow && !exitWindow.myExitWindow.isShowing()) {
			if (null != drawView[TOOL_WRITEPAPER]) {
				drawView[TOOL_WRITEPAPER].isFinishFlag = false;
			}
			if (null != drawView[TOOL_TESTPAPER]) {
				drawView[TOOL_TESTPAPER].isFinishFlag = false;
			}
			exitWindow.myExitWindow
			.showAtLocation(imgBt_tool[AnswerPaperFun.TOOL_TOOLS],
			Gravity.CENTER, 0, 0);
		}

		SaveDataThread saveDataThread = new AnswerPaperFun() {
		}.new SaveDataThread(context, drawView, bundleData, chosActivityKind);
		saveDataThread.start();
	}

	/**
	* Answerpaper finish時 Destroy所有window並且傳遞返回信息給調用者
	* 
	* @param context
	* @param drawView
	* @param bundleData
	* @param chosActivityKind
	*/
	public static void MyFinish(Context context, DrawView[] drawView,
	ReturnGetStartBundleData bundleData, int chosActivityKind) {
		if (drawView[TOOL_WRITEPAPER] != null) {
			try {
				drawView[TOOL_WRITEPAPER]
				.SaveCurrenPaperMatrixPic(
				bundleData.absolute_path + bundleData.answer_id
				+ File.separator,
				bundleData.answer_id
				+ StartAnswerPaperSetting.SavePaperMatrixPicExname,
				true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			drawView[TOOL_WRITEPAPER].dvSaveData.SaveCurrenPaperAllInfo(
			bundleData.absolute_path + bundleData.answer_id
			+ File.separator, bundleData.answer_id
			+ StartAnswerPaperSetting.SavePaperInfoExname);
			drawView[TOOL_WRITEPAPER].onDestroyView();
		}

		if (drawView[TOOL_TESTPAPER] != null) {
			drawView[TOOL_TESTPAPER].dvSaveData.SaveCurrenPaperAllInfo(
			bundleData.absolute_path + bundleData.answer_id
			+ File.separator, bundleData.answer_id
			+ StartAnswerPaperSetting.TestPaperInfoExname);
			// drawView[TOOL_TESTPAPER].dvBitmap.RecyleALlBitmaps();
			drawView[TOOL_TESTPAPER].onDestroyView();
		}

		if (wm != null) {
			if (null != otherGraphic) {
				if (otherGraphic.otherGraphicsMainWnd.isShowing()) {
					otherGraphic.otherGraphicsMainWnd.dismiss();
				}
				otherGraphic = null;
			}
			if (null != exitWindow) {
				if (exitWindow.myExitWindow.isShowing()) {
					exitWindow.myExitWindow.dismiss();
				}
				exitWindow = null;
			}
			wm.removeView(bundleData.myMainView);
			wm = null;
		}

		// 返回給調用者的信息
		Intent intent = new Intent();
		intent.putExtra(StartAnswerPaperSetting.RESULT_BACKGROUND_PIC_KEY,
		bundleData.absolute_path + bundleData.answer_id
		+ StartAnswerPaperSetting.SavePaperMatrixPicExname);
		if (drawView[TOOL_WRITEPAPER] != null) {
			intent.putExtra(StartAnswerPaperSetting.ANSWER_RESULT_DATA_KEY,
			// drawView[TOOL_WRITEPAPER].dvRetData.GetRetHtmlBuffer());
			drawView[TOOL_WRITEPAPER].dvRetData.GetResultPngPath());
		}

		((Activity) context).setResult(bundleData.result_code2, intent);
		((Activity) context).finish();
	}

	/**
	* 屏幕尺寸改變時重新計算答題紙顯示信息並且刷新重新Loading
	* 
	* @param context
	* @param myShowWebView
	* @param bundleData
	* @param initialStartData
	* @param drawView
	* @param imgBt_tool
	* @param myTools
	* @param reShowDlg
	* @param chosActivityKind
	*/
	public static void OnChangeScreenSize(Context context,
	WebView myShowWebView, ReturnGetStartBundleData bundleData,
	InitializeStartData initialStartData, DrawView[] drawView,
	ImageView[] imgBt_tool, MyToolsWindow myTools, boolean reShowDlg,
	int chosActivityKind) {
		if (myTools.eraseWindow.isShowing()) {
			myTools.eraseWindow.dismiss();
			reShowDlg = true;
		}
		// WindowManager wm = (WindowManager) context
		// .getSystemService(Context.WINDOW_SERVICE);
		// // getWindowManager().removeView(view)
		// int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		// int height = wm.getDefaultDisplay().getHeight() - Title_Bar_Height;//
		// 屏幕高度
		GetScreenWidthHeight getScreen = new GetScreenWidthHeight(context);
		Title_Bar_Height = GetScreenWidthHeight.px2dip(context,
		Title_Bar_Height_Normal);
		int width = getScreen.getWidth();// 屏幕宽度
		int height = getScreen.getHeight() - Title_Bar_Height;
		if (0 != getScreen.Title_Bar_Height) {
			Title_Bar_Height = GetScreenWidthHeight.px2dip(context, 0);
		} else {
			Title_Bar_Height = GetScreenWidthHeight.px2dip(context, 65);
		}
		if (chosActivityKind == ACTIVITY_SOLUTION) {
			// 設置屏幕顯示寬高
			bundleData.realWidth = width;
			bundleData.realHeight = height;
		} else if (chosActivityKind == ACTIVITY_FILL
				|| chosActivityKind == ACTIVITY_CHOICE) {
			// 設置屏幕顯示寬高
			bundleData.realWidth = width * 2 / 3;
			bundleData.realHeight = height * 1 / 3;
		}

		InitializeStart(initialStartData, bundleData, chosActivityKind);
		ResetToolsWidthHeight(imgBt_tool, initialStartData, chosActivityKind);
		SetMyLayout(context, myShowWebView, bundleData, initialStartData,
		drawView, imgBt_tool, chosActivityKind);
		if (drawView[TOOL_WRITEPAPER] != null) {
			drawView[TOOL_WRITEPAPER].SetNewPageWidthHeight(
			bundleData.set_width, bundleData.set_height);
			drawView[TOOL_WRITEPAPER].MoveReDraw(true);
		}
		if (drawView[TOOL_TESTPAPER] != null) {
			drawView[TOOL_TESTPAPER].SetNewPageWidthHeight(
			bundleData.set_width, bundleData.set_height);
			drawView[TOOL_TESTPAPER].MoveReDraw(true);
		}

		if (reShowDlg) {
			if (chosActivityKind == ACTIVITY_SOLUTION) {
				myTools.eraseWindow.showAsDropDown(imgBt_tool[TOOL_TOOLS],
				initialStartData.writePaper_width / 2 - 120,
				initialStartData.writePaper_height / 2 - 120);
			} else if (chosActivityKind == ACTIVITY_FILL
					|| chosActivityKind == ACTIVITY_CHOICE) {
				myTools.eraseWindow.showAsDropDown(imgBt_tool[TOOL_BACK],
				initialStartData.writePaper_width / 2 - 172,
				initialStartData.writePaper_height / 2 - 132);
			}
			myTools.eraser_click_count = 2;
		}
	}
	
	public static void OnChangeDlgActivitySize(Context context,
	WebView myShowWebView, ReturnGetStartBundleData bundleData,
	InitializeStartData initialStartData, DrawView[] drawView,
	ImageView[] imgBt_tool, MyToolsWindow myTools, boolean reShowDlg,
	int chosActivityKind, MotionEvent event, float move_x,
	float move_y, int touchKind) {
		if (myTools.eraseWindow.isShowing()) {
			myTools.eraseWindow.dismiss();
			reShowDlg = true;
		}
		// 屏幕高度
		GetScreenWidthHeight getScreen = new GetScreenWidthHeight(context);
		Title_Bar_Height = GetScreenWidthHeight.px2dip(context,
		Title_Bar_Height_Normal);
		int width = getScreen.getWidth();// 屏幕宽度
		int height = getScreen.getHeight() - Title_Bar_Height;
		if (0 != getScreen.Title_Bar_Height) {
			Title_Bar_Height = GetScreenWidthHeight.px2dip(context, 0);
		} else {
			Title_Bar_Height = GetScreenWidthHeight.px2dip(context, 65);
		}
		if (chosActivityKind == ACTIVITY_SOLUTION) {
			// 設置屏幕顯示寬高
			bundleData.realWidth = width;
			bundleData.realHeight = height;
		} else if (chosActivityKind == ACTIVITY_FILL
				|| chosActivityKind == ACTIVITY_CHOICE) {
			// 設置屏幕顯示寬高

			g_x = (int) g_x;
			g_y = (int) g_y;
			move_x = (int) move_x;
			move_y = (int) move_y;

			int tmp_x = (int) (g_x - move_x);
			int tmp_y = (int) (g_y - move_y);

			switch (touchKind) {
			case 1:
				tmp_x = -tmp_x;
				tmp_y = -tmp_y;
				bundleData.dlgPos.x = bundleData.dlgPos.x + tmp_x;
				bundleData.dlgPos.y = bundleData.dlgPos.y + tmp_y;
				break;
			case 2:
				tmp_y = -tmp_y;
				bundleData.dlgPos.y = bundleData.dlgPos.y + tmp_y;
				break;
			case 3:
				tmp_x = -tmp_x;
				bundleData.dlgPos.x = bundleData.dlgPos.x + tmp_x;
				break;
			case 4:
				// tmp_x = tmp_x;
				// tmp_y = tmp_y;
				break;
			}
			bundleData.realWidth = (int) (bundleData.realWidth - tmp_x);
			bundleData.realHeight = (int) (bundleData.realHeight - tmp_y);
			Bitmap bmp = ((BitmapDrawable) context.getResources().getDrawable(
			R.drawable.tabfont_dtz_v_default)).getBitmap();
			int tmpSize = bmp.getWidth() * 2 + 30 + initialStartData.tools_size * 2;
			if (bundleData.realWidth < tmpSize) {
				bundleData.realWidth = tmpSize;
			}
			if (bundleData.realHeight < initialStartData.tools_size * 3) {
				bundleData.realHeight = initialStartData.tools_size * 3;
			}
			if (4 != touchKind) {
				wmParams.x = bundleData.dlgPos.x;
				wmParams.y = bundleData.dlgPos.y;
				wm.updateViewLayout(bundleData.myMainView, wmParams); // 刷新显示
			}

			g_x = move_x;
			g_y = move_y;
		}

		InitializeStart(initialStartData, bundleData, chosActivityKind);
		ResetToolsWidthHeight(imgBt_tool, initialStartData, chosActivityKind);
		SetMyLayout(context, myShowWebView, bundleData, initialStartData,
		drawView, imgBt_tool, chosActivityKind);
		if (drawView[TOOL_WRITEPAPER] != null) {
			drawView[TOOL_WRITEPAPER].SetNewPageWidthHeight(
			bundleData.set_width, bundleData.set_height);
			drawView[TOOL_WRITEPAPER].MoveReDraw(true);
		}
		if (drawView[TOOL_TESTPAPER] != null) {
			drawView[TOOL_TESTPAPER].SetNewPageWidthHeight(
			bundleData.set_width, bundleData.set_height);
			drawView[TOOL_TESTPAPER].MoveReDraw(true);
		}

		if (reShowDlg) {
			if (chosActivityKind == ACTIVITY_SOLUTION) {
				myTools.eraseWindow.showAsDropDown(imgBt_tool[TOOL_TOOLS],
				initialStartData.writePaper_width / 2 - 120,
				initialStartData.writePaper_height / 2 - 120);
			} else if (chosActivityKind == ACTIVITY_FILL
					|| chosActivityKind == ACTIVITY_CHOICE) {
				myTools.eraseWindow.showAsDropDown(imgBt_tool[TOOL_BACK],
				initialStartData.writePaper_width / 2 - 172,
				initialStartData.writePaper_height / 2 - 132);
			}
			myTools.eraser_click_count = 2;
		}
	}

	/**
	* 设置Answerpaper 所有layout 宽高等属性
	* 
	* @param context
	* @param myShowWebView
	*            需要显示的题目信息
	* @param bundleData
	*            调用者信息
	* @param initialStartData
	*            初始化View 信息
	* @param drawView
	*            答题域
	* @param imgBt_tool
	*            所有工具
	* @param chosActivityKind
	*            答题纸类型
	*/
	public static void SetMyLayout(Context context, WebView myShowWebView,
	ReturnGetStartBundleData bundleData,
	InitializeStartData initialStartData, DrawView[] drawView,
	ImageView[] imgBt_tool, int chosActivityKind) {
		if (chosActivityKind == ACTIVITY_SOLUTION) {
			ViewGroup.LayoutParams param = myShowWebView.getLayoutParams();
			param.height = initialStartData.myWV_height;
			myShowWebView.setLayoutParams(param);
			param = drawView[TOOL_WRITEPAPER].getLayoutParams();
			param.height = initialStartData.writePaper_height;
			param.width = initialStartData.writePaper_width;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			// draw_view_frame can set in file
			lp1.topMargin = 0;
			lp1.bottomMargin = 12;
			lp1.leftMargin = 0;
			lp1.rightMargin = 0;
			lp1.width = initialStartData.writePaper_width - lp1.leftMargin
					- lp1.rightMargin;
			lp1.height = initialStartData.writePaper_height - lp1.topMargin
					- lp1.bottomMargin;
			bundleData.drawView_frame.setLayoutParams(lp1);
			LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// main_tool_bar can set in file
			lp2.topMargin = 0;
			lp2.bottomMargin = 0; // 12
			lp2.leftMargin = 0;
			lp2.rightMargin = 0;
			lp2.width = initialStartData.tools_size;
			lp2.height = lp1.height;
			// bundleData.main_frame_pre.setLayoutParams(lp2);
			// bundleData.main_frame.setPadding(0, 0, 0, 50);
			bundleData.main_tool_bar.setLayoutParams(lp2);
			if (drawView[TOOL_WRITEPAPER] != null) {
				lp1 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				float scale = context.getResources().getDisplayMetrics().density;
				// draw_view_size can set in file
				int tmp_width_divid_value = 0;
				int tmp_height_divid_value = 0;
				if (scale > 0.7 && scale <= 1.0) {
					lp1.topMargin = 5;
					lp1.bottomMargin = 0;
					lp1.leftMargin = 0;
					lp1.rightMargin = 0;
					tmp_width_divid_value = 7;
					tmp_height_divid_value = 25;
				} else if (scale > 1.0 && scale <= 1.7) {
					lp1.topMargin = 8;
					lp1.bottomMargin = 0;
					lp1.leftMargin = 0;
					lp1.rightMargin = 0;
					tmp_width_divid_value = 10;
					tmp_height_divid_value = 32;
				} else if (scale > 0 && scale < 1.0) {
					lp1.topMargin = 4;
					lp1.bottomMargin = 0;
					lp1.leftMargin = 0;
					lp1.rightMargin = 0;
					tmp_width_divid_value = 5;
					tmp_height_divid_value = 22;
				}
				lp1.width = initialStartData.writePaper_width
						- tmp_width_divid_value;
				lp1.height = initialStartData.writePaper_height
						- tmp_height_divid_value;
				// lp1.width = initialStartData.writePaper_width -
				// lp1.leftMargin
				// - lp1.rightMargin;
				// lp1.height = initialStartData.writePaper_height -
				// lp1.topMargin
				// - lp1.bottomMargin;
				// lp1.gravity = Gravity.CENTER;
				drawView[TOOL_WRITEPAPER].setLayoutParams(lp1);
				drawView[TOOL_WRITEPAPER].dvSize.ResetScreenSize(lp1.width,
						lp1.height);
				drawView[TOOL_WRITEPAPER].SetRealWidthHeight(lp1.width,
						lp1.height, 0, 0);
				// drawView[TOOL_WRITEPAPER].setBackgroundColor(Color.WHITE -
				// 200);
			}

			if (drawView[TOOL_TESTPAPER] != null) {
				drawView[TOOL_TESTPAPER].setLayoutParams(lp1);
				drawView[TOOL_TESTPAPER].dvSize.ResetScreenSize(lp1.width,
						lp1.height);
				drawView[TOOL_TESTPAPER].SetRealWidthHeight(lp1.width,
						lp1.height, 0, 0);
				// drawView[TOOL_TESTPAPER].setBackgroundColor(Color.WHITE -
				// 10);
			}

			lp.width = initialStartData.tools_size;
			lp.height = initialStartData.writePaper_height;
			bundleData.main_frame.setLayoutParams(lp);
		} else if (chosActivityKind == ACTIVITY_FILL) {
			ViewGroup.LayoutParams param = null;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			// LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
			// LinearLayout.LayoutParams.WRAP_CONTENT,
			// LinearLayout.LayoutParams.WRAP_CONTENT);
			// // main_tool_bar can set in file
			// lp2.topMargin = 0;
			// lp2.bottomMargin = 0; // 12
			// lp2.leftMargin = 0;
			// lp2.rightMargin = 0;
			// lp2.width = initialStartData.tools_size;
			// lp2.height = initialStartData.tools_size;
			// // bundleData.main_frame_pre.setLayoutParams(lp2);
			// // bundleData.main_frame.setPadding(0, 0, 0, 50);
			// bundleData.main_tool_bar.setLayoutParams(lp2);

			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp1.topMargin = 0;
			lp1.bottomMargin = 0;
			lp1.leftMargin = 0;
			lp1.rightMargin = 0;
			lp1.width = initialStartData.writePaper_width - lp1.leftMargin
					- lp1.rightMargin;
			lp1.height = initialStartData.writePaper_height - lp1.topMargin
					- lp1.bottomMargin;
			bundleData.drawView_frame.setLayoutParams(lp1);
			if (drawView[TOOL_WRITEPAPER] != null) {
				param = drawView[TOOL_WRITEPAPER].getLayoutParams();
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				float scale = context.getResources().getDisplayMetrics().density;
				// draw_view_size can set in file
				int tmp_width_divid_value = 0;
				int tmp_height_divid_value = 0;
				if (scale > 0.7 && scale <= 1.2) {
					lp2.topMargin = 5;
					lp2.bottomMargin = 0;
					lp2.leftMargin = 0;
					lp2.rightMargin = 0;
					tmp_width_divid_value = 23;
					tmp_height_divid_value = 10;
				} else if (scale > 1.2 && scale <= 1.7) {
					lp2.topMargin = 8;
					lp2.bottomMargin = 0;
					lp2.leftMargin = 0;
					lp2.rightMargin = 0;
					tmp_width_divid_value = 33;
					tmp_height_divid_value = 12;
				} else if (scale > 0 && scale < 0.7) {
					lp2.topMargin = 4;
					lp2.bottomMargin = 0;
					lp2.leftMargin = 0;
					lp2.rightMargin = 0;
					tmp_width_divid_value = 5;
					tmp_height_divid_value = 22;
				}
				lp2.width = initialStartData.writePaper_width
						- tmp_width_divid_value;
				lp2.height = initialStartData.writePaper_height
						- tmp_height_divid_value;

				param.width = initialStartData.writePaper_width
						- tmp_width_divid_value;
				param.height = initialStartData.writePaper_height
						- tmp_height_divid_value;
				// param.height = initialStartData.writePaper_height;
				// param.width = initialStartData.writePaper_width;
				drawView[TOOL_WRITEPAPER].setLayoutParams(lp1);
				drawView[TOOL_WRITEPAPER].dvSize.ResetScreenSize(lp1.width,
						lp1.height);
				drawView[TOOL_WRITEPAPER].SetRealWidthHeight(lp2.width,
						lp2.height, 0, 0);
				// drawView[TOOL_WRITEPAPER].setBackgroundColor(Color.WHITE -
				// 200);
			}

			if (drawView[TOOL_TESTPAPER] != null) {
				param = drawView[TOOL_TESTPAPER].getLayoutParams();
				param.height = initialStartData.writePaper_height;
				param.width = initialStartData.writePaper_width;
				drawView[TOOL_TESTPAPER].setLayoutParams(lp1);
				drawView[TOOL_TESTPAPER].dvSize.ResetScreenSize(lp1.width,
						lp1.height);
				drawView[TOOL_TESTPAPER].SetRealWidthHeight(lp1.width - 33,
						lp1.height - 18, 0, 0);
				// drawView[TOOL_TESTPAPER].setBackgroundColor(Color.WHITE -
				// 10);
			}
			lp.leftMargin = 0;
			lp.width = initialStartData.writePaper_width;
			lp.height = initialStartData.tools_size;
			bundleData.main_frame.setLayoutParams(lp);
		} else if (chosActivityKind == ACTIVITY_CHOICE) {
			ViewGroup.LayoutParams param = null;
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);

			lp1.topMargin = 0;
			lp1.bottomMargin = 0;
			lp1.leftMargin = 0;
			lp1.rightMargin = 0;
			lp1.width = initialStartData.writePaper_width - lp1.leftMargin
					- lp1.rightMargin;
			lp1.height = initialStartData.writePaper_height - lp1.topMargin
					- lp1.bottomMargin;
			// LinearLayout myViewLayout = (LinearLayout) ((Activity) context)
			// .findViewById(R.id.myViewLayout);
			bundleData.drawView_frame.setLayoutParams(lp1);

			if (drawView[TOOL_TESTPAPER] != null) {
				param = drawView[TOOL_TESTPAPER].getLayoutParams();
				LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				float scale = context.getResources().getDisplayMetrics().density;
				// draw_view_size can set in file
				int tmp_width_divid_value = 0;
				int tmp_height_divid_value = 0;
				if (scale > 0.7 && scale <= 1.2) {
					lp2.topMargin = 5;
					lp2.bottomMargin = 0;
					lp2.leftMargin = 0;
					lp2.rightMargin = 0;
					tmp_width_divid_value = 23;
					tmp_height_divid_value = 10;
				} else if (scale > 1.2 && scale <= 1.7) {
					lp2.topMargin = 8;
					lp2.bottomMargin = 0;
					lp2.leftMargin = 0;
					lp2.rightMargin = 0;
					tmp_width_divid_value = 33;
					tmp_height_divid_value = 12;
				} else if (scale > 0 && scale < 0.7) {
					lp2.topMargin = 4;
					lp2.bottomMargin = 0;
					lp2.leftMargin = 0;
					lp2.rightMargin = 0;
					tmp_width_divid_value = 5;
					tmp_height_divid_value = 22;
				}
				lp2.width = initialStartData.writePaper_width
						- tmp_width_divid_value;
				lp2.height = initialStartData.writePaper_height
						- tmp_height_divid_value;

				param.height = initialStartData.writePaper_height;
				param.width = initialStartData.writePaper_width;
				drawView[TOOL_TESTPAPER].setLayoutParams(lp1);
				drawView[TOOL_TESTPAPER].dvSize.ResetScreenSize(lp1.width,
						lp1.height);
				drawView[TOOL_TESTPAPER].SetRealWidthHeight(lp2.width,
						lp2.height, 0, 0);
				// drawView[TOOL_TESTPAPER].setBackgroundColor(Color.WHITE -
				// 100);
			}

			lp.leftMargin = 0;
			lp.width = initialStartData.writePaper_width;
			lp.height = initialStartData.tools_size;
			bundleData.main_frame.setLayoutParams(lp);

			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			int tmpMargin = (bundleData.realWidth - ((initialStartData.writePaper_width - initialStartData.tools_size
					* (tools_count - 2))
					/ 2 - layoutMargin * 5 - 14))
					/ 2 - initialStartData.tools_size - 20;

			lp.rightMargin = tmpMargin;
			lp.width = initialStartData.tools_size;
			lp.height = initialStartData.tools_size - 10;
			imgBt_tool[TOOL_BACK].setLayoutParams(lp);

			lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.leftMargin = tmpMargin;
			lp.width = initialStartData.tools_size;
			lp.height = initialStartData.tools_size - 10;
			imgBt_tool[TOOL_TOOLS].setLayoutParams(lp);
		}
	}

	/**
	* dip conver px
	* 
	* @param context
	* @param dipValue
	*            需要轉換的dip值
	* @return 像素值
	*/
	public static int dipTopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	* px conver dip
	* 
	* @param context
	* @param pxValue
	*            待轉換px
	* @return dip 值
	*/
	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale);
	}

	/**
	* 加載答題紙答題信息
	* 
	* @param context
	* @param myShowWebView
	*            需要顯示的題目信息
	* @param bundleData
	*            調用者傳入信息
	* @param initialStartData
	*            Answerpaper 所有View
	* @param drawView
	*            答題域
	* @param imgBt_tool
	*            工具
	* @param isMove
	*            是否是移動紙張狀態
	* @param isSetPen
	*            筆還是橡皮
	* @param myTools
	*            工具類
	* @param chosActivityKind
	*            Answerpaper類型
	* @return 信息是否加載成功
	*/
	public static boolean LoadMyDrawView(Context context,
	WebView myShowWebView, ReturnGetStartBundleData bundleData,
	InitializeStartData initialStartData, DrawView[] drawView,
	ImageView[] imgBt_tool, boolean[] isMove, boolean[] isSetPen,
	MyToolsWindow myTools, int chosActivityKind) {
		boolean bRet = false;
		Bundle bundle = ((Activity) context).getIntent().getExtras();
		if (bundle != null) {
			if (chosActivityKind == ACTIVITY_SOLUTION) {
				MyWebViewFun.SetMyWebView(context, bundle, bundleData,
				initialStartData, myShowWebView, drawView);
			}
			SetMyLayout(context, myShowWebView, bundleData, initialStartData,
			drawView, imgBt_tool, chosActivityKind);
			if (drawView[TOOL_WRITEPAPER] != null) {
				drawView[TOOL_WRITEPAPER].SetSaveInfo(bundleData.absolute_path,
				bundleData.answer_id);
				drawView[TOOL_WRITEPAPER].SetNewPageWidthHeight(
				bundleData.set_width, bundleData.set_height);
				drawView[TOOL_WRITEPAPER].LoadingCurrenPaper(
				bundleData.absolute_path + bundleData.answer_id
				+ File.separator, bundleData.answer_id
				+ StartAnswerPaperSetting.SavePaperInfoExname);
				drawView[TOOL_WRITEPAPER]
				.LoadingMatrixPic(
				bundleData.absolute_path + bundleData.answer_id
				+ File.separator,
				bundleData.answer_id
				+ StartAnswerPaperSetting.SavePaperMatrixPicExname);
				isSetPen[TOOL_WRITEPAPER] = drawView[TOOL_WRITEPAPER].dvPen
				.GetPenSet();
				isSetPen[TOOL_WRITEPAPER] = true;
				drawView[TOOL_WRITEPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_WRITEPAPER]);
				isMove[TOOL_WRITEPAPER] = false;
				drawView[TOOL_WRITEPAPER]
				.SetMoveOrPaint(isMove[TOOL_WRITEPAPER]);
				drawView[TOOL_WRITEPAPER]
				.setOnTouchListener(new AnswerPaperOnTouchListener(
				context, bundleData, initialStartData,
				drawView, imgBt_tool, null, null, null, isMove,
				isSetPen, -1, myTools, chosActivityKind) {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						boolean clearAllDataFlag = false;
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							AnswerPaperFun
							.OnChangeToolsFocus(false,
							drawView, isMove, isSetPen,
							myTools);
							if (drawView[TOOL_WRITEPAPER].dvGeometry.drawGeometryFlag) {
								clearAllDataFlag = false;
							} else {
								clearAllDataFlag = IsClearAllData(
								drawView[TOOL_WRITEPAPER].dvSaveData,
								myTools, IS_CHECK_DATA);
							}

							if (!clearAllDataFlag) {
								isAddWaterMark = false;
								drawView[TOOL_WRITEPAPER]
								.SetDrawWaterMark(isAddWaterMark);
							} else {
								isAddWaterMark = true;
								drawView[TOOL_WRITEPAPER]
								.SetDrawWaterMark(isAddWaterMark);
							}
							break;
						case MotionEvent.ACTION_UP:
							if (drawView[TOOL_WRITEPAPER].dvGeometry.drawGeometryFlag) {
								clearAllDataFlag = false;
								if (drawView[TOOL_WRITEPAPER].dvGeometry.checkDownPos == 0) {
									AnswerPaperFun.OnChangeToolsFocus(
									true, drawView, isMove,
									isSetPen, myTools);
								}
							} else {
								clearAllDataFlag = IsClearAllData(
								drawView[TOOL_WRITEPAPER].dvSaveData,
								myTools, IS_CHECK_DATA);
							}
							myTools.SetOthersToolFocus(
							myTools.nowToolChos,
							myTools.tools_size,
							myTools.preToolChos,
							clearAllDataFlag);
							if (!clearAllDataFlag) {
								isAddWaterMark = false;
								drawView[TOOL_WRITEPAPER]
								.SetDrawWaterMark(isAddWaterMark);
							} else {
								isAddWaterMark = true;
								drawView[TOOL_WRITEPAPER]
								.SetDrawWaterMark(isAddWaterMark);
							}

							break;
						case MotionEvent.ACTION_MOVE:
							if (drawView[TOOL_WRITEPAPER].dvGeometry.drawGeometryFlag) {
								clearAllDataFlag = false;
							} else {
								clearAllDataFlag = IsClearAllData(
								drawView[TOOL_WRITEPAPER].dvSaveData,
								myTools, IS_CHECK_DATA);
							}
							if (!clearAllDataFlag) {
								isAddWaterMark = false;
							} else {
								isAddWaterMark = true;
							}
							drawView[TOOL_WRITEPAPER]
							.SetDrawWaterMark(isAddWaterMark);
							break;
						}
						return super.onTouch(v, event);
					}

				});
				drawView[TOOL_WRITEPAPER].setFocusable(true);
			}

			if (drawView[TOOL_TESTPAPER] != null) {
				drawView[TOOL_TESTPAPER].SetSaveInfo(bundleData.absolute_path,
				bundleData.answer_id);
				drawView[TOOL_TESTPAPER].SetNewPageWidthHeight(
				bundleData.set_width, bundleData.set_height);
				drawView[TOOL_TESTPAPER].LoadingCurrenPaper(
				bundleData.absolute_path + bundleData.answer_id
				+ File.separator, bundleData.answer_id
				+ StartAnswerPaperSetting.TestPaperInfoExname);
				myTools.SetOthersToolFocus(
				myTools.nowToolChos,
				myTools.tools_size,
				-1,
				IsClearAllData(drawView[TOOL_TESTPAPER].dvSaveData,
				myTools, IS_CLEAR_CHECK));
				drawView[TOOL_TESTPAPER]
				.LoadingMatrixPic(
				bundleData.absolute_path + bundleData.answer_id
				+ File.separator,
				bundleData.answer_id
				+ StartAnswerPaperSetting.SavePaperMatrixPicExname);
				isSetPen[TOOL_TESTPAPER] = drawView[TOOL_TESTPAPER].dvPen
				.GetPenSet();
				isSetPen[TOOL_TESTPAPER] = true;
				drawView[TOOL_TESTPAPER]
				.setPenOrEraserStyle(isSetPen[TOOL_TESTPAPER]);
				isMove[TOOL_TESTPAPER] = false;
				drawView[TOOL_TESTPAPER].SetMoveOrPaint(isMove[TOOL_TESTPAPER]);
				drawView[TOOL_TESTPAPER]
				.setOnTouchListener(new AnswerPaperOnTouchListener(
				context, bundleData, initialStartData,
				drawView, imgBt_tool, null, null, null, isMove,
				isSetPen, -1, myTools, chosActivityKind) {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						boolean clearAllDataFlag = false;
						switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							AnswerPaperFun
							.OnChangeToolsFocus(false,
							drawView, isMove, isSetPen,
							myTools);
							break;
						case MotionEvent.ACTION_UP:
							myTools.SetOthersToolFocus(
							myTools.nowToolChos,
							myTools.tools_size,
							myTools.preToolChos,
							AnswerPaperFun
							.IsClearAllData(
							drawView[TOOL_TESTPAPER].dvSaveData,
							myTools,
							IS_CHECK_DATA));
							break;
						case MotionEvent.ACTION_MOVE:
							if (drawView[TOOL_TESTPAPER].dvGeometry.drawGeometryFlag) {
								clearAllDataFlag = false;
								myTools.SetOthersToolFocus(
								myTools.nowToolChos,
								myTools.tools_size,
								myTools.preToolChos,
								clearAllDataFlag);
							} else {
								clearAllDataFlag = IsClearAllData(
								drawView[TOOL_TESTPAPER].dvSaveData,
								myTools, IS_CHECK_DATA);
							}
							break;
						}
						return super.onTouch(v, event);
					}

				});
				drawView[TOOL_TESTPAPER].setFocusable(true);
			}

			if (drawView[TOOL_WRITEPAPER] != null
					&& drawView[TOOL_WRITEPAPER].getVisibility() == View.VISIBLE) {
				if (IsClearAllData(drawView[TOOL_WRITEPAPER].dvSaveData,
							myTools, IS_CLEAR_START)) {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
					// 答題水印
					// drawView[TOOL_WRITEPAPER].dvSaveData.AddWaterMark(
					// drawView[TOOL_WRITEPAPER].dvSize,
					// drawView[TOOL_WRITEPAPER].dvBitmap);
					isAddWaterMark = true;
					drawView[TOOL_WRITEPAPER].SetDrawWaterMark(isAddWaterMark);
				} else {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = false;
				}
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos,
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]);
			} else if (drawView[TOOL_TESTPAPER] != null
					&& drawView[TOOL_TESTPAPER].getVisibility() == View.VISIBLE) {
				if (IsClearAllData(drawView[TOOL_TESTPAPER].dvSaveData,
							myTools, IS_CLEAR_START)) {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = true;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = true;
				} else {
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN] = false;
					myTools.isDisable[MyToolsWindow.TOOL_DRAW_UNDO] = false;
				}
				myTools.SetOthersToolFocus(myTools.nowToolChos,
				myTools.tools_size, myTools.preToolChos,
				myTools.isDisable[MyToolsWindow.TOOL_DRAW_CLEAN]);
			}

			bRet = true;
		}

		return bRet;
	}
}
