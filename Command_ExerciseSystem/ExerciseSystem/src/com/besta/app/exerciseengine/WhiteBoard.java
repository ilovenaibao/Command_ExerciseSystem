package com.besta.app.exerciseengine;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class WhiteBoard {

	public static void CallBoard(Activity activity, String packageName,
			String appName, Boolean FullScreen) {// true is FullScreen, false
													// isn't FullScreen
		Intent intent = new Intent();
		intent.setAction("com.besta.app.memo.DrawActivity");
		Bundle bundle = new Bundle();
		bundle.putString("FOLDERNAME", packageName); 
		bundle.putString("APPNAME", appName);
		bundle.putBoolean("FullScreen", FullScreen);
		View v = activity.getWindow().getDecorView();
		v.setDrawingCacheEnabled(true);
		Bitmap bmp = v.getDrawingCache();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
		bundle.putByteArray("Back", baos.toByteArray());
		intent.putExtras(bundle);
		if (activity.getPackageManager().queryIntentActivities(intent, 0)
				.size() > 0) {
			activity.startActivity(intent);

		} else {
			Toast.makeText(activity, "Can not find the function!", Toast.LENGTH_SHORT).show();
		}
	}
}
