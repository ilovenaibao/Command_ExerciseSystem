package com.besta.app.answerpaper.mywebview;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;

public class MyWebView extends WebView {
	private Rect capPicRect;

	public MyWebView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		capPicRect = new Rect(0, 0, 0, 0);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Picture capturePicture() {
		// TODO Auto-generated method stub
		return super.capturePicture();
	}

}
