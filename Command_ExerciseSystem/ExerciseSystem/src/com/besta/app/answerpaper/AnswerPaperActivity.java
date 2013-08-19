package com.besta.app.answerpaper;

import java.awt.Image;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.besta.app.answerpaper.drawview.DrawView;
import com.besta.app.answerpaper.mywebview.MyWebViewCaller;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun;
import com.besta.app.exerciseengine.R;
import com.besta.app.toolswindow.MyToolsWindow;
import com.besta.media.audio.play.CallBack;

/**
 * @author BXC2011007 Taylor
 * 
 * @describe 答題紙(應用題)Activity, 主要用來答一些大型題目，例如幾 何, 代數等應用題. 主要模塊有兩個,一個答题域,
 *           另一个是显示题目信息域. 主要是为了方便答题的同时可以与题目信息进行交互操作例如:将题目信息当中用到的
 *           图片可以直接引入答题域供答题使用。
 * 
 * @call_method 可參見工程路徑： com.besta.app.testcallactiv ity.testFirstActivity.java
 * 
 */
public class AnswerPaperActivity extends Activity {
	// 捆綁的所有被調用的設置信息類
	private AnswerPaperFun.ReturnGetStartBundleData bundleData = null;
	// 所有初始化需要的屬性類
	private AnswerPaperFun.InitializeStartData initialStartData = null;
	// 答題紙 & 演算紙 View
	public DrawView[] drawView = null;
	// 是否移動紙張的標記
	boolean[] isMove = null;
	// 是否切換Pen || Eraser的標記
	boolean[] isSetPen = null;
	// 答題紙介面工具箱
	ImageView[] imgBt_tool = null;
	// 水印
	ImageView writePaperText = null;
	ImageView testPaperText = null;
	// 工具箱是否可用
	boolean[] isDisable_tool = null;
	// 工具焦點
	private int nowToolChos = 0;
	// 現實的完整的題目
	private WebView myShowWebView = null;
	// 答題工具箱
	private MyToolsWindow myTools = null;
	// 答題工具箱是否彈出
	boolean reShowDlg = false;
	// Activity View
	private static WindowManager wm = null;
	private static WindowManager.LayoutParams wmParams = null;
	// 載入圖片時的回調函數
	public CallBack reciver = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 初始化獲得出入信息
		bundleData = new AnswerPaperFun.ReturnGetStartBundleData();
		// 初始化所有屬性值
		bundleData = AnswerPaperFun.GetStartBundle(this, bundleData,
				AnswerPaperFun.ACTIVITY_SOLUTION);
		if (bundleData.bRet) {
			nowToolChos = 0;
			// create new Activity Views
			InitializeNewActivityView();
			// new attrib data
			initialStartData = new AnswerPaperFun.InitializeStartData();
			AnswerPaperFun.InitializeStart(initialStartData, bundleData,
					AnswerPaperFun.ACTIVITY_SOLUTION);
			// initial attrib data
			AnswerPaperFun.InitializeAllViewAttrib(AnswerPaperActivity.this,
					bundleData, initialStartData, drawView, imgBt_tool,
					writePaperText, testPaperText, isDisable_tool, nowToolChos,
					myTools, AnswerPaperFun.ACTIVITY_SOLUTION);
			nowToolChos = AnswerPaperFun.TOOL_WRITEPAPER;
			// set tools focus
			AnswerPaperFun.SetOthersToolFocus(this, imgBt_tool, writePaperText,
					testPaperText, nowToolChos,
					AnswerPaperFun.ACTIVITY_SOLUTION);

			// 加載題目信息以及答題信息
			if (!AnswerPaperFun.LoadMyDrawView(this, myShowWebView, bundleData,
					initialStartData, drawView, imgBt_tool, isMove, isSetPen,
					myTools, AnswerPaperFun.ACTIVITY_SOLUTION)) {
				finish();
			}
			// 設置當前工具箱的焦點位置
			AnswerPaperFun.SetToolsListener(this, imgBt_tool, drawView, isMove,
					isSetPen, myTools, initialStartData, bundleData,
					AnswerPaperFun.ACTIVITY_SOLUTION);
			bundleData.caller = new MyWebViewCaller().new Caller();
		} else {
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case AnswerPaperFun.RESULT_CODE_OTHERGRAPHIC: // call
			// otherGraphicActivity
			AnswerPaperFun.otherGraphicActivityResult(this, requestCode,
					resultCode, data, myTools, imgBt_tool);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 當屏幕大小改變的時候刷新屏幕信息——自適應屏幕
		// reSize titlebar height
		AnswerPaperFun.OnChangeTitleBarHeight(AnswerPaperActivity.this);
		// change new Screen size
		AnswerPaperFun.OnChangeScreenSize(AnswerPaperActivity.this,
				myShowWebView, bundleData, initialStartData, drawView,
				imgBt_tool, myTools, reShowDlg,
				AnswerPaperFun.ACTIVITY_SOLUTION);
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// WindowManager wm = AnswerPaperFun.GetActivityWindows();
		if (wm != null) {
			// 在程序退出(Activity销毁）时销毁悬浮窗口
			wm.removeView(bundleData.myMainView);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		// 暫停時保留信息
		if (myTools.toolsWindow.isShowing()) {
			// close all tools
			myTools.toolsWindow.dismiss();
		}
		if (wm != null && !bundleData.onReturnEvent_flag) {
			bundleData.myMainView.setVisibility(View.INVISIBLE);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		int status = 0;
		status = AnswerPaperFun.onResumeSetting(drawView, 1, 0, myTools, 0);
		AnswerPaperFun.OnChangeTitleBarHeight(AnswerPaperActivity.this);
		AnswerPaperFun.OnChangeScreenSize(AnswerPaperActivity.this,
				myShowWebView, bundleData, initialStartData, drawView,
				imgBt_tool, myTools, reShowDlg,
				AnswerPaperFun.ACTIVITY_SOLUTION);
		if (bundleData.myMainView.getVisibility() == View.INVISIBLE) {
			bundleData.myMainView.setVisibility(View.VISIBLE);
		}

		if (0 != status) {
			if ((null != AnswerPaperFun.otherGraphic.graphicPath && !AnswerPaperFun.otherGraphic.graphicPath
					.equals(""))
					&& (null != AnswerPaperFun.otherGraphic.graphicName && !AnswerPaperFun.otherGraphic.graphicName
							.equals(""))) {
				AnswerPaperFun.SetSelectBmp(this,
						AnswerPaperFun.otherGraphic.graphicPath, bundleData,
						drawView, AnswerPaperFun.otherGraphic.graphicName,
						AnswerPaperFun.otherGraphic.chosListKind);
				AnswerPaperFun.onResumeSetting(drawView, 2, status, myTools, 0);
			}
		}

		// 判斷Tools工具欄是否在onPause時是打開狀態，如果是則重新打開
		if (null != myTools && null != myTools.toolsWindow
				&& myTools.toolsWindowIsShowing
				&& !myTools.toolsWindow.isShowing()) {
			if (null != imgBt_tool[AnswerPaperFun.TOOL_TOOLS]) {
				myTools.toolsWindow.showAtLocation(
						imgBt_tool[AnswerPaperFun.TOOL_TOOLS], Gravity.BOTTOM,
						0, 0);
			} else {
				myTools.toolsWindowIsShowing = false;
			}
		}

		super.onResume();
	}

	/**
	 * 初始化新的Activity所需的View
	 */
	private void InitializeNewActivityView() {
		drawView = new DrawView[2]; // write/test paper
		isMove = new boolean[2]; // move flag
		isSetPen = new boolean[2]; // pen/eraser flag
		drawView[AnswerPaperFun.TOOL_WRITEPAPER] = new DrawView(this);
		drawView[AnswerPaperFun.TOOL_TESTPAPER] = new DrawView(this);
		writePaperText = new ImageView(this); // write paper water mark
		testPaperText = new ImageView(this); // test paper water mark

		imgBt_tool = new ImageView[AnswerPaperFun.tools_count];
		isDisable_tool = new boolean[AnswerPaperFun.tools_count];
		// initial can excute tools
		int disable_count = 0;
		for (disable_count = 0; disable_count < AnswerPaperFun.tools_count; disable_count++) {
			imgBt_tool[disable_count] = new ImageView(this);
			isDisable_tool[disable_count] = false;
		}
		myTools = new MyToolsWindow(this, bundleData.realWidth,
				bundleData.realHeight, AnswerPaperFun.ACTIVITY_SOLUTION,
				AnswerPaperFun.IsClearAllData(
						drawView[AnswerPaperFun.TOOL_WRITEPAPER].dvSaveData,
						myTools, AnswerPaperFun.IS_CLEAR_START));

		myShowWebView = new WebView(this);
		myShowWebView.getSettings().setJavaScriptEnabled(true);
		// AnswerPaperFun.CreateAllView(this, myShowWebView, bundleData,
		// imgBt_tool, writePaperText, testPaperText, drawView,
		// AnswerPaperFun.ACTIVITY_SOLUTION);
		// create Answerpaper layouts
		CreateAllView(AnswerPaperActivity.this);
	}

	/**
	 * 創建所有View
	 * 
	 * @param context
	 */
	public void CreateAllView(Context context) {
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		bundleData.myMainView = new LinearLayout(context);
		bundleData.myMainView.setLayoutParams(param);
		bundleData.myMainView.setOrientation(LinearLayout.VERTICAL);
		bundleData.myMainView.setGravity(Gravity.CENTER_HORIZONTAL);
		myShowWebView.setLayoutParams(param);
		bundleData.myMainView.addView(myShowWebView);

		bundleData.main_frame_pre = new LinearLayout(context);
		bundleData.main_frame_pre.setLayoutParams(param);
		bundleData.main_frame_pre.setOrientation(LinearLayout.HORIZONTAL);
		bundleData.myMainView.addView(bundleData.main_frame_pre);

		bundleData.main_tool_bar = new LinearLayout(context);
		bundleData.main_tool_bar.setLayoutParams(param);
		bundleData.main_tool_bar.setOrientation(LinearLayout.VERTICAL);
		bundleData.main_tool_bar.setGravity(Gravity.CENTER_VERTICAL);
		bundleData.main_tool_bar
				.setBackgroundResource(R.drawable.bg_h_titlebar);

		bundleData.main_frame = new LinearLayout(context);
		// bundleData.main_frame.setLayoutParams(param);
		// bundleData.main_frame.setOrientation(LinearLayout.VERTICAL);
		// bundleData.main_frame.setGravity(Gravity.CENTER_VERTICAL);
		// bundleData.main_frame.setBackgroundResource(R.drawable.bg_h_titlebar);
		bundleData.main_frame.addView(bundleData.main_tool_bar);
		bundleData.main_frame_pre.addView(bundleData.main_frame);

		// ImgView -> back
		imgBt_tool[AnswerPaperFun.TOOL_BACK].setLayoutParams(param);
		imgBt_tool[AnswerPaperFun.TOOL_BACK].setImageDrawable(context
				.getResources().getDrawable(R.drawable.btn_close_default));
		bundleData.main_tool_bar.addView(imgBt_tool[AnswerPaperFun.TOOL_BACK]);

		// tools_writepaper & tools_testpaper
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		// rp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		rp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		rp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		// ImgView -> writepaper
		if (imgBt_tool[AnswerPaperFun.TOOL_WRITEPAPER] != null) {
			RelativeLayout writePaper_frame = new RelativeLayout(context);
			writePaper_frame.setLayoutParams(param);
			bundleData.main_tool_bar.addView(writePaper_frame);
			imgBt_tool[AnswerPaperFun.TOOL_WRITEPAPER]
					.setBackgroundDrawable(context.getResources().getDrawable(
							R.drawable.tab_h_press));
			writePaper_frame
					.addView(imgBt_tool[AnswerPaperFun.TOOL_WRITEPAPER]);
			writePaperText.setImageDrawable(context.getResources().getDrawable(
					R.drawable.tabfont_dtz_h_press));
			writePaper_frame.addView(writePaperText, rp);
		}
		// ImgView -> testpaper
		RelativeLayout testpaper_frame = new RelativeLayout(context);
		testpaper_frame.setLayoutParams(param);
		bundleData.main_tool_bar.addView(testpaper_frame);
		imgBt_tool[AnswerPaperFun.TOOL_TESTPAPER].setBackgroundDrawable(context
				.getResources().getDrawable(R.drawable.tab_h_press));
		testpaper_frame.addView(imgBt_tool[AnswerPaperFun.TOOL_TESTPAPER]);
		testPaperText.setImageDrawable(context.getResources().getDrawable(
				R.drawable.tabfont_ysz_h_press));

		testpaper_frame.addView(testPaperText, rp);

		// ImgView -> tools
		imgBt_tool[AnswerPaperFun.TOOL_TOOLS].setLayoutParams(param);
		imgBt_tool[AnswerPaperFun.TOOL_TOOLS].setImageDrawable(context
				.getResources().getDrawable(R.drawable.btn_tool_default));
		bundleData.main_tool_bar.addView(imgBt_tool[AnswerPaperFun.TOOL_TOOLS]);

		// DrawView
		bundleData.drawView_frame = new LinearLayout(context);
		bundleData.drawView_frame.setLayoutParams(param);
		bundleData.drawView_frame.setOrientation(LinearLayout.VERTICAL);
		bundleData.drawView_frame
				.setBackgroundResource(R.drawable.bg_h_drawarea);
		bundleData.main_frame_pre.addView(bundleData.drawView_frame);

		if (drawView[AnswerPaperFun.TOOL_WRITEPAPER] != null) {
			drawView[AnswerPaperFun.TOOL_WRITEPAPER]
					.setVisibility(View.VISIBLE);
			drawView[AnswerPaperFun.TOOL_TESTPAPER].setVisibility(View.GONE);
			bundleData.drawView_frame
					.addView(drawView[AnswerPaperFun.TOOL_WRITEPAPER]);
			bundleData.drawView_frame
					.addView(drawView[AnswerPaperFun.TOOL_TESTPAPER]);
		} else {
			drawView[AnswerPaperFun.TOOL_TESTPAPER].setVisibility(View.VISIBLE);
			bundleData.drawView_frame
					.addView(drawView[AnswerPaperFun.TOOL_TESTPAPER]);
		}

		// 获取WindowManager
		wm = (WindowManager) context.getApplicationContext().getSystemService(
				"window");
		// 设置LayoutParams(全局变量）相关参数
		// Application app = ((Activity) context).getApplication();
		// wmParams = ((MyApplication) app).getMywmParams();
		// wmParams = ((MyApplication) ((Activity) context).getApplication())
		// .getMywmParams();
		wmParams = new WindowManager.LayoutParams();

		// wmParams.type = LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
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
		wmParams.height = param.height - AnswerPaperFun.Title_Bar_Height;
		// 显示myFloatView图像
		wm.addView(bundleData.myMainView, wmParams);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			bundleData.onReturnEvent_flag = true;
			if (myTools.toolsWindow.isShowing()) {
				myTools.toolsWindow.dismiss();
				myTools.toolsWindowIsShowing = false;
			}
			AnswerPaperFun.onMyFinish(AnswerPaperActivity.this, drawView,
					bundleData, AnswerPaperFun.ACTIVITY_SOLUTION, myTools,
					imgBt_tool);
			break;
		}

		// return false;
		return super.onKeyDown(keyCode, event);
	}
}