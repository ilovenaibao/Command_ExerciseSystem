package com.besta.app.answerpaper.othersclassinfo;

import com.besta.app.answerpaper.drawview.DrawView;
import com.besta.app.toolswindow.MyToolsWindow;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AnswerPaperOnLongClickListener implements Button.OnClickListener {
	Context parentContext = null;
	int chosActivityKind = 0;
	AnswerPaperFun.ReturnGetStartBundleData bundleData = null;
	DrawView[] drawView = null;
	ImageView[] imgBt_tool = null;
	ImageView writePaperText = null;
	ImageView testPaperText = null;
	boolean[] isDisable_tool = null;
	MyToolsWindow myTools = null;
	int nowToolChos = 0;

	public AnswerPaperOnLongClickListener(Context context,
			AnswerPaperFun.ReturnGetStartBundleData bundleData,
			DrawView[] drawView, ImageView[] imgBt_tool,
			ImageView writePaperText, ImageView testPaperText,
			boolean[] isDisable_tool, int nowToolChos, MyToolsWindow myTools,
			int chosActivityKind) {
		this.parentContext = context;
		this.chosActivityKind = chosActivityKind;
		this.bundleData = bundleData;
		this.drawView = drawView;
		this.imgBt_tool = imgBt_tool;
		this.writePaperText = writePaperText;
		this.testPaperText = testPaperText;
		this.isDisable_tool = isDisable_tool;
		this.nowToolChos = nowToolChos;
		this.myTools = myTools;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
