package com.besta.app.answerpaper.othergraphics;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OtherGraphicListOnTouchListener implements OnTouchListener {
	int chosKind;

	public OtherGraphicListOnTouchListener(int chosKind) {
		this.chosKind = chosKind;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
