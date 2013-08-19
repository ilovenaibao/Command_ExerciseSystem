package com.besta.app.answerpaper.othergraphics;

import com.besta.app.exerciseengine.R;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

public class MyPopupWndTool extends PopupWindow {
	// public static int
	public int nowFocus, preFocus;

	public MyPopupWndTool(Context context, View mainView) {
		nowFocus = preFocus = 0;
		View[] loginView = new View[3];

		loginView[0] = (View) (mainView.findViewById(R.id.in_system_frame));
		loginView[1] = (View) (mainView.findViewById(R.id.in_myDefine_frame));
		loginView[2] = (View) (mainView.findViewById(R.id.in_myCollect_frame));

		if (null != loginView[0]) {
			loginView[0]
					.setOnTouchListener(new OtherGraphicMainWndToolOnTouchListener(
							loginView[0]) {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// Log.i("onTouch", "touch");
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								if (null != myLayoutView) {
									myLayoutView.setBackgroundColor(Color.BLUE);
								}
								break;
							case MotionEvent.ACTION_UP:
								if (null != myLayoutView) {
									myLayoutView
											.setBackgroundColor(OtherGraphicsMain.mainWndColor);
								}
								break;
							}

							return false;
						}
					});

			loginView[0]
					.setOnClickListener(new OtherGraphicMainWndToolOnClickListener(
							loginView[0]) {

						@Override
						public void onClick(View v) {
							// Log.i("onClick", "click");
//							if (preFocus)
						}
					});
		}
	}
}
