package com.besta.app.answerpaper.drawview;

import java.io.File;
import java.io.RandomAccessFile;

import android.graphics.BitmapFactory;

public class DrawViewReturnData {
	public final static String ret_html_buffer_head = "<html><head><title>AnswerPaper</title></head><body>";
	public final static String ret_html_buffer_end = "</body></html>";
	public final static String ret_html_img_head = "<div id=\"img";
	public final static String ret_html_img_end = "</img></div>";

	public String ret_html_buffer;
	private String save_temp_ret_buffer;
	public String return_save_answer_png;

	public DrawViewReturnData() {
		ret_html_buffer = new String("");
		save_temp_ret_buffer = new String("");
		return_save_answer_png = new String("");
	}

	public void CleanRetHtmlBuffer() {
		ret_html_buffer = "";
		save_temp_ret_buffer = "";
	}

	public void SetOneRetHtmlImg(String src_path, int now_count,
			int top_distance) {
		File newFile = new File(src_path);
		if (newFile.exists()) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(src_path, opt);

			ret_html_buffer += ret_html_img_head + now_count
					+ " style=\"position:absolute; top:" + (now_count - 1)
					* top_distance + "px;>" + "<img src=\"" + src_path
					+ "\" width=" + opt.outWidth + "px height=\""
					+ opt.outHeight + "px\">" + ret_html_img_end;
		}
	}

	public void SetAllRetHtmlImg(String[] src_path, int count) {
		for (int i = 0; i < count; i++) {
			File newFile = new File(src_path[i]);
			if (newFile.exists()) {
				BitmapFactory.Options opt = new BitmapFactory.Options();
				opt.inJustDecodeBounds = true;
				// 此时返回的bitmap为null
				BitmapFactory.decodeFile(src_path[i], opt);

				ret_html_buffer += ret_html_img_head + i
						+ " style=\"position:absolute; width:" + opt.outWidth
						+ "px; height:" + opt.outHeight + "px;>"
						+ "<img src=\"" + src_path[i] + "\">"
						+ ret_html_img_end;
			}
		}
	}

	public void SetRetHtmlBufferHeadEnd(boolean hasAddHeadOrEnd) {
		if (hasAddHeadOrEnd) {
			AddRetHtmlBufferTerminal();
		}
	}

	public void AddRetHtmlBufferTerminal() {
		save_temp_ret_buffer = ret_html_buffer_head + ret_html_buffer
				+ ret_html_buffer_end;
	}

	public String GetResultPngPath() {
		return return_save_answer_png;
	}

	public String GetRetHtmlBuffer() {
		return ret_html_buffer;
	}

	public void WriteRetHtmlBufferOnDisk(String path) {
		File tmpFile = new File(path);
		if (tmpFile.exists()) {
			tmpFile.delete();
		}
		try {
			RandomAccessFile writeFile = new RandomAccessFile(tmpFile, "rw");
			writeFile.writeBytes(save_temp_ret_buffer);
			writeFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
