package com.besta.app.answerpaper.dragpicview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

public class DragPicView extends View {
	// 定義拖拽屬性種類
	public final static int ATTRIB_KIND_INITIALIZE = 0; // 初始化
	public final static int ATTRIB_KIND_SHOW = 1; // 顯示
	public final static int ATTRIB_KIND_DESSMIS = 2; // 關閉

	// 設置拖拽View是否顯示
	public boolean dragPicViewShowFlag;
	// 設置拖拽View基本屬性種類
	public int setAttribKind;

	/**
	 * DragPicView's constructor
	 * 
	 * @param context
	 */
	public DragPicView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		dragPicViewShowFlag = false;
		setAttribKind = 0;
		this.setBackgroundColor(0xA0C05080);
	}

	public DragPicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 設置DragPicView的基本屬性，如是否顯示、背景色等
	 * 
	 * @param attribKind
	 *            選擇的設置屬性目前分為三種：1. 初始化; 2. 顯示; 3. 關閉
	 */
	public void setDragPicViewAttrib(int attribKind) {
		switch (attribKind) {
		case ATTRIB_KIND_INITIALIZE:
			dragPicViewShowFlag = false;
			this.setBackgroundColor(0xA0C05080);
			this.setVisibility(View.GONE);
			break;
		case ATTRIB_KIND_SHOW:
			dragPicViewShowFlag = true;
			this.setBackgroundColor(Color.TRANSPARENT);
			this.setVisibility(View.VISIBLE);
			break;
		case ATTRIB_KIND_DESSMIS:
			break;
		}
	}

}
