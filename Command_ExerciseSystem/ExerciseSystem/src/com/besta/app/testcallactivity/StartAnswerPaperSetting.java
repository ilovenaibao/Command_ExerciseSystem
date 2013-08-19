package com.besta.app.testcallactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.besta.app.answerpaper.AnswerPaperActivity;
import com.besta.app.answerpaper.PrtScr;
import com.besta.app.answerpaper.othersclassinfo.GetScreenWidthHeight;
import com.besta.app.exerciseengine.R;

public class StartAnswerPaperSetting {
	// Debug log
	private String DebugLog = "StartAnswerPaperSetting->";
	// 答題紙結束,調用其他Ap的時候返回選擇data的入口對應碼，default不修改
	public final static int result_code = 0xA0001; // 調用截圖
	public final static int result_code2 = 0xA0002; // 調用答題紙

	// 調用答題紙的Action
	public final static String ACTION_NORMAL = "com.besta.app.answerpaper.ANSWERNORMAL";
	public final static String ACTION_DLG = "com.besta.app.answerpaper.ANSWERDLG";
	public final static String ACTION_TEST = "com.besta.app.answerpaper.TESTDLG";
	public final static String ACTION_OTHER_GRAPHIC = "com.besta.app.answerpaper.othergraphics.OtherGraphicActivity";
	public final static String ACTION_PRTSCR = "com.besta.app.answerpaper.prtscr";

	// 調用類型：解答題、選擇題、填空題
	public final static int KIND_NORMAL = 1;
	public final static int KIND_DLG = 2;
	public final static int KIND_TEST = 3;

	// 調用答題紙傳遞的有效數據Key值
	public final static String ANSWER_RESULT_CODE_KEY = "ANSWER_RESULT_CODE_KEY";
	public final static String PACK_NAME_KEY = "PACK_NAME_KEY";
	public final static String ANSWER_ID_KEY = "ANSWER_ID_KEY";
	public final static String PAGE_WIDTH_KEY = "PAGE_WIDTH_KEY";
	public final static String PAGE_HEIGHT_KEY = "PAGE_HEIGHT_KEY";
	public final static String NORMAL_WEBVIEW_DATA_KEY = "NORMAL_WEBVIEW_DATA_KEY";
	public final static String DLG_POS_X_KEY = "DLG_POS_X_KEY";
	public final static String DLG_POS_Y_KEY = "DLG_POS_Y_KEY";
	public final static String DLG_ANSWER_FLAG_KEY = "DLG_ANSWER_FLAG_KEY";
	public final static String pic_extern_name = ".png";
	public final static String RESULT_BACKGROUND_PIC_KEY = "RESULT_BACKGROUND_PIC_KEY";
	public final static String ANSWER_RESULT_DATA_KEY = "ANSWER_RESULT_DATA_KEY";

	// 調用答題紙傳遞的默認信息default不要修改
	// 數據存儲路徑根目錄
	public final static String save_path = "/sdcard/AnswrPapers/";
	// 存儲的背景圖擴展名
	public final static String save_bgImgExternName = ".pdat";
	// 存儲的縮略圖擴展名
	public final static String SavePaperMatrixPicExname = "_sln.pdat";
	// 存儲的答題紙筆記信息
	public final static String SavePaperInfoExname = "_sln.dat";
	// 演算紙筆記信息
	public final static String TestPaperInfoExname = "_slntmp.dat";
	// 返回給真題介面的png 數據
	public final static String resultPngData = "result_show"
			+ save_bgImgExternName;
	// 臨時存儲的答題信息png圖片的像素組信息存儲路徑
	public final static String resultPngDataTmpPixlesPath = save_path
			+ "tmpResultPngDataPixles.dat_";

	public boolean isActivityStartFlag = false;

	// 調用窗口模式時，窗口模式顯示的位置，default不修改
	public int dlgPos_x;
	public int dlgPos_y;

	// 所要传递的一些信息
	public String bgImg_path;
	public String pack_name;
	public String answer_id;
	public int VIEW_WIDTH;
	public int VIEW_HEIGHT;
	public int set_width;
	public int set_height;
	public String result_path;
	public String answer_result_data;
	public IdConvert retAbsolutePathConvert;

	public StartAnswerPaperSetting(Context context) {
		isActivityStartFlag = false;
		dlgPos_x = 0;
		dlgPos_y = 0;

		bgImg_path = null;
		// pack_name = "com.besta.app.testcallactivity";
		pack_name = context.getPackageName();
		answer_id = "1";

		GetScreenWidthHeight getScreen = new GetScreenWidthHeight(context);
		VIEW_WIDTH = getScreen.getWidth();// 屏幕宽度 (px)
		VIEW_HEIGHT = getScreen.getHeight(); // 屏幕高度 (px)
		set_width = VIEW_WIDTH;
		set_height = 2 * VIEW_HEIGHT;
		result_path = "";
		answer_result_data = new String("");
	}

	// 設置答題紙的實際寬高
	public void ShowAnswerPaperSize(int width, int height) {
		set_width = VIEW_WIDTH;
		// if (height > 8 * VIEW_HEIGHT) {
		// set_height = 8 * VIEW_HEIGHT;
		// } else {
		set_height = height;
		// }
	}

	// 设置窗口模式答题纸的显示位置
	public void ShowAnswerDlgActivityPos(int xoffset, int yoffset) {
		dlgPos_x = xoffset;
		dlgPos_y = yoffset;
	}

	// 截取屏幕背景图
	public void PrtScrBackground(Context context, String question_id) {
		PrtScr cutPic = new PrtScr();
		answer_id = question_id;
		bgImg_path = cutPic.CallBoard((Activity) context, pack_name,
				context.getString(R.string.app_name), answer_id
						+ pic_extern_name, false);
	}

	// 選擇調用答題紙的方式有：解答題、填空題、選擇題
	public void ChosStartAnswerPaper(Context context, String question_id,
			String data, int chos) {
		Intent intent = null;

		if (!isActivityStartFlag) {

			if (data == null) {
				data = new String("");
			}
			answer_id = question_id;
			Bundle bundle = new Bundle();
			bundle.putInt(ANSWER_RESULT_CODE_KEY, result_code2);
			bundle.putString(PACK_NAME_KEY, pack_name);
			bundle.putString(ANSWER_ID_KEY, answer_id);
			bundle.putInt(PAGE_WIDTH_KEY, set_width);
			bundle.putInt(PAGE_HEIGHT_KEY, set_height);
			if (chos == KIND_NORMAL) {
				bundle.putString(NORMAL_WEBVIEW_DATA_KEY, data);
				intent = new Intent(ACTION_NORMAL);
			} else if (chos == KIND_DLG) {
				bundle.putInt(DLG_POS_X_KEY, dlgPos_x);
				bundle.putInt(DLG_POS_Y_KEY, dlgPos_y);
				bundle.putBoolean(DLG_ANSWER_FLAG_KEY, true);
				intent = new Intent(ACTION_DLG);
			} else if (chos == KIND_TEST) {
				bundle.putInt(DLG_POS_X_KEY, dlgPos_x);
				bundle.putInt(DLG_POS_Y_KEY, dlgPos_y);
				bundle.putBoolean(DLG_ANSWER_FLAG_KEY, false);
				intent = new Intent(ACTION_TEST);
			}

			// 獲取存放的路徑信息
			retAbsolutePathConvert = new IdConvert(context, answer_id);
			intent.putExtras(bundle);
			((Activity) context).startActivityForResult(intent, 0);
			isActivityStartFlag = true;
		} else {
			Toast.makeText(
					context,
					context.getResources().getString(
							R.string.answer_paper_started), Toast.LENGTH_SHORT)
					.show();
		}
	}

	// 答题纸结束时返回的data
	public void AnswerActivityResult(Context context, int requestCode,
			int resultCode, Intent data) {
		isActivityStartFlag = false;
		if (resultCode == result_code) {
			// 早期設計先截背景圖之後再調用答題紙, 不適用
			Intent intent = new Intent(context, AnswerPaperActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString(PACK_NAME_KEY, pack_name);
			bundle.putString(ANSWER_ID_KEY, answer_id);
			bundle.putInt(PAGE_WIDTH_KEY, set_width);
			bundle.putInt(PAGE_HEIGHT_KEY, set_height);
			intent.putExtras(bundle);
			((Activity) context).startActivityForResult(intent, 0);

		} else if (resultCode == result_code2) {
			// 答題紙結束時返回的縮略圖路徑
			result_path = data.getExtras().getString(RESULT_BACKGROUND_PIC_KEY);
			answer_result_data = data.getExtras().getString(
					ANSWER_RESULT_DATA_KEY);
			if (null == answer_result_data) {
				answer_result_data = new String("NULL");
			}
			Log.i(DebugLog, "" + answer_result_data);
		}
	}

	// 獲取答題結束的答案html buffer
	public String GetAnswerResult() {
		if (null == answer_result_data) {
			answer_result_data = new String("NULL");
		}
		return answer_result_data;
	}

	// 答题纸结束后可以获取缩略图的路径
	public String GetResultBackgroundPic() {
		return result_path;
	}

	// 初始化獲取已存在的大體信息路徑
	public String getAlreadyExistAnswerPath() {
		String bRet = null;
		if (null != retAbsolutePathConvert) {
			bRet = retAbsolutePathConvert.getAbsolutePath();
		}
		return bRet;
	}
}
