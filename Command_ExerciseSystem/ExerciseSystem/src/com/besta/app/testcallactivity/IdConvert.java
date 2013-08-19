package com.besta.app.testcallactivity;

import java.io.File;

import android.content.Context;

public class IdConvert {
	public String retAbsolutePath;

	public IdConvert(Context context, String answerId) {
		retAbsolutePath = null;
		if (null != context && null != answerId) {
			if (answerId.equals("")) {
				return;
			}
			retAbsolutePath = setAbsolutePath(context.getPackageName(),
					answerId);
		}
	}

	public String getAbsolutePath() {
		return retAbsolutePath;
	}

	// 獲取結果路徑
	public static String startGetAbsolutePath(Context context, String answerId) {
		return (StartAnswerPaperSetting.save_path + context.getPackageName()
				+ File.separator + ResetAbsolutPathStr(answerId)
				+ File.separator + StartAnswerPaperSetting.resultPngData);
	}

	private String setAbsolutePath(String packageName, String answerId) {
		return (StartAnswerPaperSetting.save_path + packageName
				+ File.separator + ResetAbsolutPathStr(answerId)
				+ File.separator + StartAnswerPaperSetting.resultPngData);
	}

	public static String ResetAbsolutPathStr(String src_path) {
		String strRet = null;
		byte[] byteStr = src_path.getBytes();
		for (int i = 0; i < byteStr.length; i++) {
			if ((byteStr[i] >= 48 && byteStr[i] <= 57)
					|| (byteStr[i] >= 65 && byteStr[i] <= 90)) {

			} else {
				byteStr[i] = '_';
			}
		}

		strRet = new String(byteStr);

		return strRet;
	}
}
