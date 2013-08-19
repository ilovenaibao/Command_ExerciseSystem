package com.besta.app.answerpaper.othersclassinfo;

import com.besta.app.answerpaper.drawview.DrawView;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun.InitializeStartData;
import com.besta.app.toolswindow.MyToolsWindow;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AnswerPaperOnTouchListener implements Button.OnTouchListener {
	Context parentContext = null;
	int chosActivityKind = 0;
	AnswerPaperFun.ReturnGetStartBundleData bundleData = null;
	InitializeStartData initialStartData = null;
	DrawView[] drawView = null;
	boolean[] isMove = null;
	boolean[] isSetPen = null;
	ImageView[] imgBt_tool = null;
	ImageView writePaperText = null;
	ImageView testPaperText = null;
	boolean[] isDisable_tool = null;
	MyToolsWindow myTools = null;
	int nowToolChos = 0;
	// touch position
	int touchPos = 0;
	
	// move Activity pre position
	float preActivityMoveX, preActivityMoveY;

	public AnswerPaperOnTouchListener(Context context,
			AnswerPaperFun.ReturnGetStartBundleData bundleData,
			InitializeStartData initialStartData, DrawView[] drawView,
			ImageView[] imgBt_tool, ImageView writePaperText,
			ImageView testPaperText, boolean[] isDisable_tool,
			boolean[] isMove, boolean[] isSetPen, int nowToolChos,
			MyToolsWindow myTools, int chosActivityKind) {
		this.parentContext = context;
		this.chosActivityKind = chosActivityKind;
		this.bundleData = bundleData;
		this.initialStartData = initialStartData;
		this.drawView = drawView;
		this.imgBt_tool = imgBt_tool;
		this.writePaperText = writePaperText;
		this.testPaperText = testPaperText;
		this.isDisable_tool = isDisable_tool;
		this.isMove = isMove;
		this.isSetPen = isSetPen;
		this.nowToolChos = nowToolChos;
		this.myTools = myTools;
		touchPos = 0;
		preActivityMoveX = preActivityMoveY = 0;
	}

	public void setData(Context context,
			AnswerPaperFun.ReturnGetStartBundleData bundleData,
			InitializeStartData initialStartData, DrawView[] drawView,
			ImageView[] imgBt_tool, ImageView writePaperText,
			ImageView testPaperText, boolean[] isDisable_tool,
			boolean[] isMove, boolean[] isSetPen, int nowToolChos,
			MyToolsWindow myTools, int chosActivityKind) {
		this.parentContext = context;
		this.chosActivityKind = chosActivityKind;
		this.bundleData = bundleData;
		this.initialStartData = initialStartData;
		this.drawView = drawView;
		this.imgBt_tool = imgBt_tool;
		this.writePaperText = writePaperText;
		this.testPaperText = testPaperText;
		this.isDisable_tool = isDisable_tool;
		this.isMove = isMove;
		this.isSetPen = isSetPen;
		this.nowToolChos = nowToolChos;
		this.myTools = myTools;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
