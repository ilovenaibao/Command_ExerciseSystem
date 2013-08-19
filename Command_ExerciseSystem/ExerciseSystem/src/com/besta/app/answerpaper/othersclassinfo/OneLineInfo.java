package com.besta.app.answerpaper.othersclassinfo;

import java.util.ArrayList;

public class OneLineInfo {
	// count total line count 
	public int line_count = 0;
	// Geometry or other line
	public int drawKind = 0;
	// all points count
	public int paint_count = 0;
	// Eraser or pen
	public boolean penFlag = true;
	public int penWidth = 0;
	public int penColor = 0;
	public ArrayList<PaintPos> SavePaintPos = new ArrayList<PaintPos>();
	public boolean isPic = false;
	public int selectPics_count = 0;
	public String selectPics = null;

	public OneLineInfo(int line, int paint_t, int drawKind_t, int pen_width,
			int pen_color, boolean penOrEraser) {
		line_count = line;
		drawKind = drawKind_t;
		paint_count = paint_t;
		penFlag = penOrEraser;
		penWidth = pen_width;
		penColor = pen_color;
		isPic = false;
		selectPics = new String("");
	}

	public int GetOneLinePaintCount() {
		return paint_count = SavePaintPos.size();
	}
}
