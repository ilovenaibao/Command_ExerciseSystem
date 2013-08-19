package com.besta.app.toolswindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class MyCanvas extends View{

	public Paint paint = null;
	Path path = null;
	public MyCanvas(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(path != null){
			canvas.drawPath(path, paint);
		}
	}
	
	public void Init(int width, int height){
		LayoutParams params = getLayoutParams();
		params.width = width;
		params.height = height;
		setLayoutParams(params);
		computePath(width, height);
	}
	
	public void computePath(int width, int height){
		path = new Path();
		int len = width - 40*2;
		int endX = width - 40;
		int lineHeight = height>>1;
		float x = 40;
		float y = lineHeight;
		float singleLen = (float)((len)/360.0);
		path.moveTo(x, y);
		while(x < endX){
			x += singleLen;
			y = -(float)Math.sin((x-40)*2*Math.PI/len)*(lineHeight-33) + lineHeight;
			path.lineTo(x, y);
		}
	}
}
