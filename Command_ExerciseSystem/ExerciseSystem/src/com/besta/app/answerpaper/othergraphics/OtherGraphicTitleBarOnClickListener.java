package com.besta.app.answerpaper.othergraphics;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class OtherGraphicTitleBarOnClickListener implements OnClickListener {
	Context parentContext;
	// 所有 View
	View[] imgViw;
	// Views count
	int viwCount;
	// 當前選中View
	View focusViw;
	// focus view count
	int focusCount;

	public OtherGraphicTitleBarOnClickListener(Context context, View[] imgViw,
			int viwCount, View focusViw, int focusView) {
		parentContext = context;
		this.imgViw = new View[viwCount];
		this.viwCount = viwCount;
		for (int vCount = 0; vCount < this.viwCount; vCount++) {
			this.imgViw[vCount] = imgViw[vCount];
		}

		this.focusViw = focusViw;
		this.focusCount = focusView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}
