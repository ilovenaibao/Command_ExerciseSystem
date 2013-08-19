package com.besta.app.answerpaper.othergraphics;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.besta.app.answerpaper.othersclassinfo.GetScreenWidthHeight;
import com.besta.app.exerciseengine.R;

public class OtherGraphicsMain {
	Context parentContext;
	// 其他圖形主窗口
	public PopupWindow otherGraphicsMainWnd;
	// 主窗口顏色
	public static int mainWndColor = 0xF0000000;
	// graphic show?
	public static boolean otherGraphicShowFlag = false;
	// graphic info
	public String graphicPath = "";
	public String graphicName = "";
	public int chosListKind = 0;
	public boolean showGraphic = false;

	/**
	 * 初始化主窗口
	 */
	public OtherGraphicsMain(Context context, int chosActivityKind) {
		parentContext = context;
		otherGraphicShowFlag = false;
		showGraphic = false;
		View wndMainFram = ((Activity) context).getLayoutInflater().inflate(
				R.layout.other_graphic_window2, null);
		LinearLayout layout = (LinearLayout) (wndMainFram
				.findViewById(R.id.dlg_size));
		if (null != layout) {
			layout.setBackgroundColor(mainWndColor);
			GetScreenWidthHeight screen_set = new GetScreenWidthHeight(context);
			// if (AnswerPaperFun.ACTIVITY_SOLUTION == chosActivityKind) {
			//
			// }
			// else if (AnswerPaperFun.ACTIVITY_FILL == chosActivityKind
			// || AnswerPaperFun.ACTIVITY_CHOICE == chosActivityKind) {
			//
			// }
			otherGraphicsMainWnd = new PopupWindow(wndMainFram,
					screen_set.getWidth(), screen_set.getHeight());
			// otherGraphicsMainWnd.setContentView(wndMainFram);
			BitmapDrawable tmpBmpda = new BitmapDrawable();
			otherGraphicsMainWnd.setFocusable(true);
			otherGraphicsMainWnd.setBackgroundDrawable(tmpBmpda);
			setViewAttribs(context, wndMainFram);
		}
	}

	/**
	 * 設置選擇系統、自定義、收藏的listener
	 * 
	 * @param context
	 * @param mainView
	 */
	private void setViewAttribs(Context context, View mainView) {
		// MyPopupWndTool myPopupWndTool = new MyPopupWndTool(context,
		// mainView);
	}
}
