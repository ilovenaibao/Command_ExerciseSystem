package com.besta.app.answerpaper.othergraphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;

public class OtherGraphicPicMoveView extends View {
	private Bitmap movePic = null;
	private Bitmap moveBGBmp = null;
	private Canvas moveCanvas = null;

	public OtherGraphicPicMoveView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		ViewGroup.LayoutParams viewParam = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		this.setLayoutParams(viewParam);
	}

	public OtherGraphicPicMoveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public boolean setBGAttribs() {
		boolean bRet = false;

		return bRet;
	}

	public boolean setMovePic(String srcPath) {
		boolean bRet = false;
		if (null != srcPath && !srcPath.equals("")) {
			movePic = BitmapFactory.decodeFile(srcPath);
		}
		if (null != movePic) {
			bRet = true;
		}

		return bRet;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		canvas.drawColor(Color.BLUE);
		super.onDraw(canvas);
	}

}
