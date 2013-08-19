package com.besta.app.answerpaper.othergraphics;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileTypeFilter implements FilenameFilter {
	List<String> types;

	/**
	 * 构造一个PicturesFileFilter对象，其指定文件类型为空。
	 */
	protected FileTypeFilter() {
		types = new ArrayList<String>();
	}

	/**
	 * 构造一个PicturesFileFilter对象，具有指定的文件类型。
	 * 
	 * @param types
	 */
	protected FileTypeFilter(List<String> types) {
		super();
		this.types = types;
	}

	@Override
	public boolean accept(File dir, String filename) {
		// TODO Auto-generated method stub
		for (Iterator<String> iterator = types.iterator(); iterator.hasNext();) {
			String type = (String) iterator.next();
			if (filename.endsWith(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加指定类型的文件。
	 * 
	 * @param type
	 *            将添加的文件类型，如".jpg"。
	 */
	public void addType(String type) {
		types.add(type);
	}

	/**
	 * 清空所有type信息
	 */
	public void clearType() {
		types.clear();
	}

}
