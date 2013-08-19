package com.besta.app.answerpaper.othergraphics;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OtherGraphicMainWndToolOnTouchListener implements OnTouchListener {
	public View myLayoutView;

	public OtherGraphicMainWndToolOnTouchListener(View myLayout) {
		myLayoutView = myLayout;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

}
