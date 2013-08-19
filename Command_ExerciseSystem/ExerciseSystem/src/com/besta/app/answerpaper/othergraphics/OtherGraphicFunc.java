package com.besta.app.answerpaper.othergraphics;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.besta.app.exerciseengine.R;

public abstract class OtherGraphicFunc {

	public static void setOtherGraphicUI(Context context, int kind) {
		View[] uiView = new View[3];

		uiView[OtherGraphicSetting.IN_SYSTEM] = (View) ((Activity) context)
				.findViewById(R.id.tab_system);

		uiView[OtherGraphicSetting.IN_MYDEFINE] = (View) ((Activity) context)
				.findViewById(R.id.tab_mydefine);

		uiView[OtherGraphicSetting.IN_COLLECT] = (View) ((Activity) context)
				.findViewById(R.id.tab_collect);

		for (int count = 0; count < OtherGraphicSetting.TOTAL_KIND_COUNT; count++) {
			if (kind == count) {
				uiView[count].setVisibility(View.VISIBLE);
			} else {
				uiView[count].setVisibility(View.GONE);
			}
		}
	}
}
