package com.besta.app.answerpaper.xscan;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

import com.besta.app.answerpaper.othergraphics.OtherGraphicSetting;
import com.besta.app.testcallactivity.StartAnswerPaperSetting;
import com.besta.os.BestaEnvironment;

public class ScanFiles {
	public ArrayList<String> searchPath = new ArrayList<String>();

	public void inSystemSearch_Test(Context context, int chosKind) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME };
		Cursor[] mCursor = new Cursor[2];
		mCursor[0] = context.getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
				null);
		mCursor[1] = context.getContentResolver().query(
				MediaStore.Images.Media.INTERNAL_CONTENT_URI, null, null, null,
				null);
		for (int i = 0; i < 2; i++) {
			if (null != mCursor[i]) {
				int count = 0;
				int totalCount = mCursor[i].getCount();
				mCursor[i].moveToFirst();
				for (count = 0; count < totalCount; count++) {
					int columnPathIndex = mCursor[i]
							.getColumnIndex(filePathColumn[0]);
					if (-1 != columnPathIndex) {
						int columnNameIndex = mCursor[i]
								.getColumnIndex(filePathColumn[1]);
						if (-1 != columnNameIndex) {
							String picName = mCursor[i]
									.getString(columnNameIndex);
							String picturePath = mCursor[i]
									.getString(columnPathIndex);
							if (checkSearchPath(picturePath, picName)) {
								searchPath.add(picturePath);
							}
						}
					}
					if (!mCursor[i].moveToNext()) {
						break;
					}
				}
				mCursor[i].close();
			}
		}

	}

	/**
	 * 檢查是否搜索到Answerpaper文件，如果是，則排除
	 * 
	 * @param srcPath
	 *            指定檢測路徑
	 * @param srcName
	 *            指定檢測路徑文件
	 * @return true: 不是Answerpaper文件; false: 是
	 */
	public boolean checkSearchPath(String srcPath, String srcName) {
		boolean bRet = false;
		if (!srcPath.contains(StartAnswerPaperSetting.save_path)) {
			bRet = true;
		}

		return bRet;
	}

	/**
	 * 自定義搜索全盤
	 * 
	 * @param context
	 * @param chosKind
	 *            選擇搜索的類型
	 */
	public void inSystemSearch(Context context, int chosKind) {
		try {
			File[] internalStorages = BestaEnvironment
					.getExternalStorageDirectoryEx();
			if (null != internalStorages && 0 < internalStorages.length) {
				searchAllFiles(internalStorages[0], chosKind);
			}
		} catch (Exception e) {
			// Log.i(TAG, "" + e);
			try {
				File sdcardFile = Environment.getExternalStorageDirectory();
				if (null != sdcardFile) {
					searchAllFiles(sdcardFile, chosKind);
				}
			} catch (Exception e1) {

			} catch (Error e2) {

			}
		} catch (Error e) {
			try {
				File sdcardFile = Environment.getExternalStorageDirectory();
				if (null != sdcardFile) {
					searchAllFiles(sdcardFile, chosKind);
				}
			} catch (Exception e1) {

			} catch (Error e2) {

			}
		}
	}

	public void searchAllFiles(File onePath, int chosKind) {
		if (null != onePath) {
			File[] searchList = onePath.listFiles();
			if (null != searchList) {
				for (int i = 0; i < searchList.length; i++) {
					if (searchList[i].isDirectory()) {
						if (OtherGraphicSetting.IN_SYSTEM == chosKind) {
							if (searchList[i].toString().contains(
									StartAnswerPaperSetting.save_path)) {
								continue;
							}
						}
						searchAllFiles(searchList[i], chosKind);
					} else {
						switch (chosKind) {
						case OtherGraphicSetting.IN_SYSTEM:
							if (compareMySelectFile(searchList[i],
									OtherGraphicSetting.PIC_END_WITH_STR)) {
								searchPath.add(searchList[i].toString());
							}
							break;
						case OtherGraphicSetting.IN_MYDEFINE:
							if (compareMySelectFile(searchList[i],
									OtherGraphicSetting.MY_DEFINE_END_WITH_STR)) {
								searchPath.add(searchList[i].toString());
							}
							break;
						case OtherGraphicSetting.IN_COLLECT:
							break;
						}

					}
				}
			}
		}
	}

	public boolean compareMySelectFile(File oneFile, String[] endWith) {
		boolean bRet = false;
		String tmpStr = oneFile.toString();
		if (null != endWith) {
			for (int i = 0; i < endWith.length; i++) {
				if (tmpStr.endsWith(endWith[i])) {
					bRet = true;
					break;
				}
			}
		}

		return bRet;
	}
}
