package com.besta.app.answerpaper.othersclassinfo;

public class MyZipLib {

	private int[] intIndex;
	public int myCount;
	public int value;

	public MyZipLib() {
		intIndex = null;
		myCount = value = 0;
	}

	public int[] changeArrayLen(int[] buf, int offset, int count, int[] addValue) {
		int[] ret = null;
		int maxCount = buf.length;
		boolean isAdd = true;
		if (count > 0) {
			isAdd = true;
		} else if (count < 0) {
			isAdd = false;
		}

		ret = new int[maxCount + count];

		for (int i = 0, j = 0; i < ret.length; i++, j++) {
			if (i >= offset && i < count) {
				if (!isAdd) {
					i--;
				} else {
					int k = 0;
					if (null != addValue) {
						for (; k < addValue.length; k++) {
							ret[i + k] = addValue[k];
						}
					} else {
						for (; k < count; k++) {
							ret[i + k] = -1;
						}
					}
					i = i + k - 1;
				}
				continue;
			}
			ret[i] = buf[j];
		}

		return ret;
	}

	public int[] myZip(int[] buffer) {
		int[] ret = new int[buffer.length];
		int retLen = 0;

		for (int i = 0; i < buffer.length; i++) {
			// if (intIndex.)
		}

		return ret;
	}
}
