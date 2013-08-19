package com.besta.app.answerpaper.othergraphics;

import android.content.Context;

import com.besta.app.answerpaper.xscan.ScanFiles;

public class ScanGraphicFiles {
	private static String TAG = "ScanGraphicFiles-->";
	public ScanFiles scanFile = new ScanFiles();

	public ScanGraphicFiles(Context context, int chosKind) {
		switch (chosKind) {
		case OtherGraphicSetting.IN_SYSTEM:
			scanFile.inSystemSearch_Test(context, OtherGraphicSetting.IN_SYSTEM);
			break;
		case OtherGraphicSetting.IN_MYDEFINE:
			scanFile.inSystemSearch(context,
					OtherGraphicSetting.IN_MYDEFINE);
			break;
		case OtherGraphicSetting.IN_COLLECT:
			break;
		}
	}
	
	public class ScanGraphicFilesThread extends Thread {
		
	}
}
