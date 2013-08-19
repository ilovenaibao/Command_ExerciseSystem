package com.besta.app.toolswindow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun;
import com.besta.app.exerciseengine.R;

public class MySaveWindow {
	public PopupWindow myExitWindow;

	public MySaveWindow(Context context, int chosActivityKind) {
		View exitView = ((Activity) context).getLayoutInflater().inflate(
				R.layout.mysavewindow, null);
		LinearLayout layout = (LinearLayout) (exitView
				.findViewById(R.id.layout_save_loading_main));
		if (null != layout) {
			if (chosActivityKind == AnswerPaperFun.ACTIVITY_SOLUTION) {
				layout.setBackgroundColor(0xA0000000);
				myExitWindow = new PopupWindow(exitView,
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
			} else if (chosActivityKind == AnswerPaperFun.ACTIVITY_FILL
					|| chosActivityKind == AnswerPaperFun.ACTIVITY_CHOICE) {
				layout.setBackgroundColor(0xA0828282);
				layout.setBackgroundDrawable(context.getResources()
						.getDrawable(R.drawable.save_data_loading_dlg));
				myExitWindow = new PopupWindow(exitView,
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
			}
		}

		myExitWindow.setContentView(exitView);
		myExitWindow.setFocusable(true);

	}
}
