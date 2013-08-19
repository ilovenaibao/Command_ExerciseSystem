package com.besta.app.answerpaper.floatmyview;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.besta.app.exerciseengine.R;

public class MyFloatView extends View {
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;

	private WindowManager wm;
	private WindowManager.LayoutParams wmParams;

	private LinearLayout main_layout = null;

	public MyFloatView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Initialize(context);
	}

	public void Initialize(Context context) {
		wm = (WindowManager) getContext().getApplicationContext()
				.getSystemService("window");
		// 此wmParams变量为获取的全局变量，用以保存悬浮窗口的属性
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

		main_layout = new LinearLayout(context);
		ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		main_layout.setLayoutParams(param);
		main_layout.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.layout_xml));
		main_layout.setOrientation(LinearLayout.VERTICAL);
		main_layout.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout main_tools = new LinearLayout(context);
		main_tools.setLayoutParams(lp);
		main_tools.setOrientation(LinearLayout.VERTICAL);
		main_layout.addView(main_tools);
		ImageView imgView_back = new ImageView(context);
		imgView_back.setLayoutParams(param);
		imgView_back.setBackgroundResource(R.drawable.btn_close_default);
		main_tools.addView(imgView_back, 1);
		main_tools.addView(imgView_back, 2);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 获取相对屏幕的坐标，即以屏幕左上角为原点
		x = event.getRawX();
		y = event.getRawY() - 25; // 25是系统状态栏的高度
		Log.i("currP", "currX" + x + "====currY" + y);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
			// 获取相对View的坐标，即以此View左上角为原点
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			Log.i("startP", "startX" + mTouchStartX + "====startY"
					+ mTouchStartY);
			break;
		case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
			updateViewPosition();
			break;
		case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
			updateViewPosition();
			mTouchStartX = mTouchStartY = 0;
			break;
		}
		return true;
	}

	private void updateViewPosition() {
		// 更新浮动窗口位置参数
		wmParams.x = (int) (x - mTouchStartX);
		wmParams.y = (int) (y - mTouchStartY);
		wm.updateViewLayout(this, wmParams); // 刷新显示
	}
}
