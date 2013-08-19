package com.besta.app.answerpaper.othergraphics;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class OtherGraphicListOnItemSelectListener implements
		OnItemSelectedListener {
	int chosKind;

	public OtherGraphicListOnItemSelectListener(int chosKind) {
		this.chosKind = chosKind;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
