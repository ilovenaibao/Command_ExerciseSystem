package com.besta.app.answerpaper.mywebview;

import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun.InitializeStartData;
import com.besta.app.answerpaper.othersclassinfo.AnswerPaperFun.ReturnGetStartBundleData;

import android.view.View;
import android.webkit.WebView;

public class MyWebViewOnLongClickListener implements WebView.OnLongClickListener {
	public ReturnGetStartBundleData myBundleData;
	public InitializeStartData myInitializeStartData;
	public WebView myWebView;

	public MyWebViewOnLongClickListener(ReturnGetStartBundleData bundleData,
			InitializeStartData initializeStartData, WebView myWebView) {
		this.myBundleData = bundleData;
		this.myInitializeStartData = initializeStartData;
		this.myWebView = myWebView;
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		return false;
	}

}
