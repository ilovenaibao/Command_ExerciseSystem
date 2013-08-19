package com.besta.app.answerpaper.othergraphics;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextViewSet extends TextView {
	boolean scrollFlag = false;
	boolean additionScrollFlag = false;

	public MyTextViewSet(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public MyTextViewSet(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public MyTextViewSet(Context context) {
		super(context);
		
	}
	
//	public void setHorizenScroll(boolean flg) {
//		scrollFlag = flg;
//	}

	@Override
	public boolean isFocused() {
		if (scrollFlag || additionScrollFlag) {
			return true;
		} else {
			return false;
		}
//		return true;
//		return super.isFocused();
	}
	
	

}
