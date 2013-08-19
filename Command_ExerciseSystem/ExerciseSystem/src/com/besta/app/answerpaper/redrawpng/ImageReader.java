package com.besta.app.answerpaper.redrawpng;

import java.io.*;
import java.awt.*;
import java.awt.image.*;

/**
 * 定义一系列数据和方法能够设置和读取image信息
 */
public abstract class ImageReader {

	// 图像宽，高
	int width = 0;

	int height = 0;

	// 图像像素信息数组
	int pixels[] = null;

	// 每个像素的数据大小
	int bitsPerPixel = 0;

	boolean indexedColor = false;

	// 调色板数据块PLTE(palette chunk)
	int colorPalette[] = null;

	/**
	 * @return 返回image对象
	 */
	public Image getImage() {

		if (pixels != null) {// 生成图像并返回

			return Toolkit.getDefaultToolkit().createImage(
					new MemoryImageSource(width, height, pixels, 0, width));
		}

		else
			return null;
	}

	/**
	 * @return 返回颜色深度信息
	 */
	public int getColorDepth() {

		return bitsPerPixel;

	}

	/**
	 * @return 返回调色板信息
	 */
	public int[] getColorPalette() {

		return colorPalette;

	}

	/**
	 * @return 返回indexedColor
	 */
	public boolean isIndexedColor() {

		return indexedColor;

	}

	/**
	 * @return 返回图像尺寸
	 */
	public int[] getImageSize() {
		int[] iRet = new int[2];

		iRet[0] = this.width;
		iRet[1] = this.height;

		return iRet;
		// return new Dimension(width, height);

	}

	/**
	 * @return 返回图像数据
	 */
	public int[] getImageData() {

		return pixels;

	}

	/**
	 * 对图像进行解码
	 * 
	 * @param in
	 *            文件信息流
	 * @throws Exception
	 */
	public abstract void unpackPNG(InputStream in) throws Exception;
}
