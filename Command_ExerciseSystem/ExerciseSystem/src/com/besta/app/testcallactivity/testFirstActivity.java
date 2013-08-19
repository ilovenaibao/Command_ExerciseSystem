package com.besta.app.testcallactivity;

import com.besta.app.exerciseengine.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;

/**
 * @author BXC2011007 Taylor
 * 
 * @describe 調用答題紙的例子
 * 
 */
public class testFirstActivity extends Activity {
	public static int MENU_ANSWER_1 = 1;
	public static int MENU_ANSWER_2 = 2;
	public static int MENU_ANSWER_3 = 3;

	// setting info class
	StartAnswerPaperSetting setting;
	public Button bt_login = null;
	ImageView imgV = null;
	WebView myWebView = null;

	// WebView show info
	String sendData = "<a href=\"image:0001-0002\"><img align=\"absmiddle\" src=\"file:///android_asset/3.png\"></a>";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first_activity);
		setting = new StartAnswerPaperSetting(this);
		setting.ShowAnswerPaperSize(setting.VIEW_WIDTH, 4 * setting.VIEW_HEIGHT);

		myWebView = (WebView) findViewById(R.id.wv_1);
		myWebView.getSettings().setJavaScriptEnabled(true);
		webviewOverride();
		webviewOnTouch();
		myWebView.loadUrl("file:///android_asset/test.html");
	}

	public void webviewOnTouch() {
		myWebView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					setting.ShowAnswerDlgActivityPos((int) event.getX(),
							(int) event.getY());
					// setting.ChosStartAnswerPaper(testFirstActivity.this, 2);
					break;
				case MotionEvent.ACTION_MOVE:

					break;
				case MotionEvent.ACTION_UP:
					break;
				}
				return false;
			}
		});
	}

	public void webviewOverride() {
		myWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.contains("wd")) {
					setting.ChosStartAnswerPaper(testFirstActivity.this,
							"001-003", sendData,
							StartAnswerPaperSetting.KIND_NORMAL);
				} else if (url.contains("tk")) {
					setting.ChosStartAnswerPaper(testFirstActivity.this,
							"028-006", "", StartAnswerPaperSetting.KIND_DLG);
				} else if (url.contains("xz")) {
					setting.ChosStartAnswerPaper(testFirstActivity.this,
							"004-090", "", StartAnswerPaperSetting.KIND_TEST);
				}

				return true;
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		String menuStr1 = "簡答題";
		String menuStr2 = "填空題";
		String menuStr3 = "選擇題";

		menu.add(1, MENU_ANSWER_1, 1, menuStr1).setIcon(R.drawable.menuopen);
		menu.add(1, MENU_ANSWER_2, 2, menuStr2).setIcon(R.drawable.menuopen);
		menu.add(1, MENU_ANSWER_3, 3, menuStr3).setIcon(R.drawable.menuopen);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == MENU_ANSWER_1) {
			setting.ChosStartAnswerPaper(testFirstActivity.this, "001-003",
					sendData, StartAnswerPaperSetting.KIND_NORMAL);
		} else if (item.getItemId() == MENU_ANSWER_2) {
			setting.ChosStartAnswerPaper(testFirstActivity.this, "028-006", "",
					StartAnswerPaperSetting.KIND_DLG);
		} else if (item.getItemId() == MENU_ANSWER_3) {
			setting.ChosStartAnswerPaper(testFirstActivity.this, "004-090", "",
					StartAnswerPaperSetting.KIND_TEST);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		setting.AnswerActivityResult(this, requestCode, resultCode, data);
		String result_pic_path = setting.GetAnswerResult();
		super.onActivityResult(requestCode, resultCode, data);
	}
}
