package com.besta.app.answerpaper.drawview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.besta.app.answerpaper.othersclassinfo.OneLineInfo;
import com.besta.app.answerpaper.othersclassinfo.PaintPos;
import com.besta.app.geometry.Geometry;
import com.besta.app.geometry.GeometryDataStruct.TrianglePoint;

public class DrawViewSaveData {
	public int total_line_count = 0;
	public int min_x, min_y, max_x, max_y;

	public ArrayList<OneLineInfo> LineInfo = null;
	public OneLineInfo one_line_info = null;

	public DrawViewSaveData() {
		min_x = min_y = max_x = max_y = 0;
		LineInfo = new ArrayList<OneLineInfo>();
		one_line_info = new OneLineInfo(0, 0, 0, 0, 0, true);
	}

	public void AddWaterMark(DrawViewSize dvSize, DrawViewBitmap dvBitmap) {
		String str = "請在此處答題";
		Paint tmpWaterMarkPaint = new Paint();
		int textSize = dvSize.screen_width / (int) (str.length() * 1.618);
		tmpWaterMarkPaint.setTextSize(textSize);
		tmpWaterMarkPaint.setColor(Color.GRAY - 10);
		tmpWaterMarkPaint.setAlpha(60);
		int textWidth = (int) tmpWaterMarkPaint.measureText(str);
		// int textHeight = (int) tmpWaterMarkPaint.

		dvBitmap.drawCanvas.drawText(str,
				(dvSize.screen_width - textWidth) / 2, textSize,
				tmpWaterMarkPaint);
	}

	public void AddNewPath(int myPenWidth, int myPenColor, int myEraserWidth,
			boolean isPen) {
		total_line_count += 1;
		one_line_info.line_count = total_line_count;
		one_line_info.GetOneLinePaintCount();
		one_line_info.penColor = myPenColor;
		if (isPen) {
			one_line_info.penWidth = myPenWidth;
		} else {
			one_line_info.penWidth = myEraserWidth;
		}
		one_line_info.penFlag = isPen;
		LineInfo.add(one_line_info);
		one_line_info = new OneLineInfo(0, 0, 0, myPenWidth, myPenColor, isPen);
	}

	public void AddNewGeometryPath(int drawGeometryChos, int myPenWidth,
			int myPenColor, boolean isPen, float sx, float sy, float ex,
			float ey, TrianglePoint trianglePoint, float diff_value_x,
			float diff_value_y, Point endPos, Paint paint) {
		one_line_info = new OneLineInfo(0, 0, drawGeometryChos, myPenWidth,
				myPenColor, isPen);
		if (drawGeometryChos == Geometry.DRAW_LINE
				|| drawGeometryChos == Geometry.DRAW_OVAL
				|| drawGeometryChos == Geometry.DRAW_RECTANGLE) {
			SaveRealPos(sx, sy, diff_value_x, diff_value_y, endPos, paint);
			SaveRealPos(ex, ey, diff_value_x, diff_value_y, endPos, paint);
		} else if (drawGeometryChos == Geometry.DRAW_TRIANGLE) {
			SaveRealPos(trianglePoint.x1, trianglePoint.y1, diff_value_x,
					diff_value_y, endPos, paint);
			SaveRealPos(trianglePoint.x2, trianglePoint.y2, diff_value_x,
					diff_value_y, endPos, paint);
			SaveRealPos(trianglePoint.x3, trianglePoint.y3, diff_value_x,
					diff_value_y, endPos, paint);
		}
		total_line_count += 1;
		one_line_info.line_count = total_line_count;
		one_line_info.GetOneLinePaintCount();
		one_line_info.penColor = myPenColor;
		one_line_info.penWidth = myPenWidth;
		one_line_info.penFlag = isPen;
		LineInfo.add(one_line_info);
		one_line_info = new OneLineInfo(0, 0, drawGeometryChos, myPenWidth,
				myPenColor, isPen);
	}

	public void AddSelectPicPath(float x, float y, float diff_value_x,
			float diff_value_y, Bitmap src_bitmap, String src_path, Paint paint) {
		float realx, realy, realEndx, realEndy;
		realx = x + diff_value_x;
		realy = y + diff_value_y;
		realEndx = realx + src_bitmap.getWidth();
		realEndy = realy + src_bitmap.getHeight();

		total_line_count += 1;
		one_line_info.line_count = total_line_count;
		one_line_info.penColor = paint.getColor();
		one_line_info.penWidth = (int) paint.getStrokeWidth();
		one_line_info.penFlag = true;
		one_line_info.isPic = true;
		PaintPos now_paint_pos = new PaintPos(realx, realy, -1);
		one_line_info.SavePaintPos.add(now_paint_pos);
		now_paint_pos = new PaintPos(realEndx, realEndy, -1);
		one_line_info.SavePaintPos.add(now_paint_pos);
		one_line_info.GetOneLinePaintCount();
		one_line_info.selectPics = src_path;
		LineInfo.add(one_line_info);
		one_line_info = new OneLineInfo(0, 0, 0, 0, 0, true);
	}

	public void SaveRealPos(float x, float y, float diff_value_x,
			float diff_value_y, Point endPos, Paint paint) {
		float realx, realy;
		realx = x + diff_value_x;
		realy = y + diff_value_y;
		if (realx > endPos.x) {
			endPos.x = (int) realx;
		}
		if (realy > endPos.y) {
			endPos.y = (int) realy;
		}
		PaintPos now_paint_pos = new PaintPos(realx, realy, paint.getColor());
		one_line_info.SavePaintPos.add(now_paint_pos);
	}

	public void SaveCurrenPaperAllInfo(String absolute_path, String save_name) {
		File dir = new File(absolute_path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		int total_count = LineInfo.size();
		String filename = absolute_path + save_name;
		File saveFile = new File(filename);
		if (saveFile.exists()) {
			saveFile.delete();
		}
		if (total_count == 0) {
			if (saveFile.exists()) {
				saveFile.delete();
			}
		} else {
			RandomAccessFile save_file;
			try {
				save_file = new RandomAccessFile(filename, "rw");
				int count = 0;
				OneLineInfo tmpOneLine = null;
				for (count = 0; count < total_count; count++) {
					tmpOneLine = LineInfo.get(count);
					try {
						save_file.writeInt(tmpOneLine.line_count);
						save_file.writeInt(tmpOneLine.drawKind);
						save_file.writeInt(tmpOneLine.paint_count);
						save_file.writeBoolean(tmpOneLine.penFlag);
						save_file.writeInt(tmpOneLine.penWidth);
						save_file.writeInt(tmpOneLine.penColor);
						save_file.writeBoolean(tmpOneLine.isPic);
						int count2 = 0;
						int total_count2 = 0;
						if (tmpOneLine.isPic) {
							String tmpStr = tmpOneLine.selectPics;
							save_file.writeInt(tmpStr.length());
							save_file.writeBytes(tmpStr);
						}
						count2 = 0;
						total_count2 = tmpOneLine.SavePaintPos.size();
						for (count2 = 0; count2 < total_count2; count2++) {
							save_file.writeFloat(tmpOneLine.SavePaintPos
									.get(count2).pos_x);
							save_file.writeFloat(tmpOneLine.SavePaintPos
									.get(count2).pos_y);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				save_file.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean LoadingCurrenPaperData(String absolute_path, String load_name) {
		boolean bRet = false;
		int tmp_count = 0;
		File dir = new File(absolute_path + load_name);
		if (!dir.exists()) {
			return bRet;
		} else {
			String filename = absolute_path + load_name;
			RandomAccessFile load_file;
			try {
				load_file = new RandomAccessFile(filename, "r");
				long file_count = 0;
				try {
					long total_file_count = load_file.length();
					for (file_count = 0; file_count < total_file_count; file_count++) {
						OneLineInfo tmpOneLineInfo = new OneLineInfo(0, 0, 0,
								0, 0, true);
						tmpOneLineInfo.line_count = load_file.readInt();
						tmpOneLineInfo.drawKind = load_file.readInt();
						tmpOneLineInfo.paint_count = load_file.readInt();
						tmpOneLineInfo.penFlag = load_file.readBoolean();
						tmpOneLineInfo.penWidth = load_file.readInt();
						tmpOneLineInfo.penColor = load_file.readInt();
						tmpOneLineInfo.isPic = load_file.readBoolean();
						total_line_count = tmpOneLineInfo.line_count;

						if (tmpOneLineInfo.isPic) {
							int tmpSize = load_file.readInt();
							byte[] tmpStrBytes = new byte[tmpSize];
							load_file.read(tmpStrBytes);
							String tmpStr = new String(tmpStrBytes);
							tmpOneLineInfo.selectPics = tmpStr;
						}

						int file_count2 = 0;
						for (file_count2 = 0; file_count2 < tmpOneLineInfo.paint_count; file_count2++) {
							PaintPos tmpPaintPos = new PaintPos(0, 0, 0);
							tmpPaintPos.pos_x = load_file.readFloat();
							tmpPaintPos.pos_y = load_file.readFloat();
							tmpOneLineInfo.SavePaintPos.add(tmpPaintPos);
						}
						tmp_count += tmpOneLineInfo.paint_count;
						LineInfo.add(tmpOneLineInfo);
						file_count = load_file.getFilePointer();
					}
					load_file.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (LineInfo.size() != 0) {
				bRet = true;
			}
		}

		return bRet;
	}

	public int GetTotalPathCount() {
		total_line_count = LineInfo.size();
		return LineInfo.size();
	}

	public OneLineInfo GetLastPath() {
		return LineInfo.get(LineInfo.size() - 1);
	}

	public void ClearAllData() {
		LineInfo.clear();
	}

	public void ResetMaxPos(float x, float y) {
		max_x = (int) x;
		max_y = (int) y;
	}

	public void ClearLastPath() {
		int tmpLastPath = LineInfo.size() - 1;
		if (tmpLastPath >= 0) {
			LineInfo.remove(LineInfo.size() - 1);
		}
	}
}
