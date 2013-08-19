package com.besta.app.answerpaper.redrawpng;

import java.io.*;
import java.util.zip.InflaterInputStream;

/**
 * 解码并绘制PNG图像 仅对8比特的真彩图像和8比特的带α通道数据的真彩色图像有效
 */

public class Recode extends ImageReader {

	// PNG文件署名域以16进制表示，以下类似情况都以16进制表示和运算
	// 137 80 78 71 13 10 26 10 ====== 0x89504e470d0a1a0aL
	public static final long PNG_SIGNATURE = 0x89504E470D0A1A0AL;

	// IHDR（文件头数据块）,IDAT（图像数据块), IEND（图像结束数据）标记
	private static final int IHDR = 0x49484452;

	private static final int IDAT = 0x49444154;

	private static final int IEND = 0x49454E44;

	private static final int PLTE = 0x504C5445;

	// 文件头数据块IHDR(header chunk)
	// 图像深度
	byte bitDepth;

	// 颜色类型
	byte colorType;

	// 压缩方法(LZ77派生算法)
	byte compression;

	// 滤波器方法
	byte filterMethod;

	// 隔行扫描方法： 0：非隔行扫描 1： Adam7
	byte interlaceMethod;

	// 滤波器方法(NONE,SUB,UP,AVERAGE,PAETH)
	private static final byte NONE = 0;
	private static final byte SUB = 1;
	private static final byte UP = 2;
	private static final byte AVERAGE = 3;
	private static final byte PAETH = 4;

	// α通道数据
	byte[] alpha;

	/**
	 * 对图像进行解码
	 * 
	 * @param 文件输入流
	 */
	public void unpackPNG(InputStream in) throws Exception {

		// 数据长度和类型
		int dataLength = 0;
		int chunkType = 0;

		// 存放读出的压缩图像的数据
		ByteArrayOutputStream pngBytes = new ByteArrayOutputStream(65536);

		// 读取8个字节的文件署名域
		long signature = readLong(in);

		// 判断是否为PNG文件
		if (signature != PNG_SIGNATURE) {

			System.out.println("--- NOT A PNG IMAGE ---");

			return;
		} else
			System.out.println("--- PNG IS NOT GIF ---");

		// 读入文件头数据块IHDR(header chunk)

		if (!read_IHDR(in))
			throw new IOException("NOT A VALID PNG IMAGE");
		// 图像宽度，图像高度
		System.out.println("image width: " + width);
		System.out.println("image height: " + height);
		// 图像深度，颜色类型
		System.out.println("image bit depth: " + bitDepth);
		System.out.println("image color type: " + colorType);
		// 压缩方法(LZ77派生算法),滤波器方法,隔行扫描方法
		System.out.println("image compression: " + compression);
		System.out.println("image filter method: " + filterMethod);
		System.out.println("image interlace method: " + interlaceMethod);
		// 其他关键数据块——调色板数据块PLTE(palette chunk), 图像数据块IDAT(image data
		// chunk)，图像结束数据IEND(image trailer chunk)

		while (true) {
			dataLength = readInt(in);
			chunkType = readInt(in);
			if (chunkType == IEND)// 图像结束数据IEND(image trailer chunk)，跳出循环
				break;
			switch (chunkType) {
			case IDAT: {// 图像数据块IDAT(image data chunk)
				read_IDAT(in, dataLength, pngBytes);
				break;
			}
			case PLTE: {// 调色板数据块PLTE(palette chunk)
				colorPalette = new int[dataLength / 3];
				read_PLTE(in, dataLength);
				break;
			}
			default: {
				in.skip(dataLength);
				// 跳过CRC循环冗余检测数据
				readUnsignedInt(in);
				break;
			}
			}
		}

		process_IDAT(pngBytes.toByteArray());
	}

	/**
	 * 读取文件头数据块IHDR(header chunk)
	 * 
	 * @param in
	 *            文件读入流
	 * @return 如果成功读取数据返回true，反之,返回false
	 * @throws Exception
	 */
	private boolean read_IHDR(InputStream in) throws Exception {

		// 判断数据的合法性
		if ((readInt(in) != 13) || (readInt(in) != IHDR))

			return false;

		// 文件头数据块IHDR(header chunk)的13字节长的数据信息
		byte[] hdr = new byte[13];

		in.read(hdr, 0, 13);

		width = readInt(hdr, 0);

		height = readInt(hdr, 4);

		// 设置像素信息数组大小
		pixels = new int[width * height];

		bitDepth = hdr[8];

		colorType = hdr[9];

		compression = hdr[10];

		filterMethod = hdr[11];

		interlaceMethod = hdr[12];

		// 跳过CRC循环冗余检测数据
		readUnsignedInt(in);

		return true;
	}

	/**
	 * 读取调色板数据块PLTE(palette chunk)
	 * 
	 * @param in
	 *            文件读入流
	 * @param dataLength
	 *            数据长度
	 * @throws Exception
	 */
	private void read_PLTE(InputStream in, int dataLength) throws Exception {
		int index = 0;
		byte[] rgb_table = new byte[dataLength];
		int len = dataLength / 3;
		in.read(rgb_table, 0, dataLength);
		for (int i = 0; i < len; i++) {
			colorPalette[i] = ((0xff << 24)
					| ((rgb_table[index++] & 0xff) << 16)
					| ((rgb_table[index++] & 0xff) << 8) | (rgb_table[index++] & 0xff));
		}
		// 跳过CRC循环冗余检测数据
		readUnsignedInt(in);
	}

	/**
	 * 读取图像数据块IDAT(image data chunk)
	 * 
	 * @param in
	 *            文件读入流
	 * @param dataLength
	 *            数据长度
	 * @param pngBytes
	 *            数据信息数组
	 * @throws Exception
	 */
	private void read_IDAT(InputStream in, int dataLength,
			ByteArrayOutputStream pngBytes) throws Exception {
		byte[] buf = new byte[dataLength];
		int len = in.read(buf, 0, dataLength);
		pngBytes.write(buf, 0, len);
		// 跳过CRC循环冗余检测数据
		readUnsignedInt(in);
	}

	/**
	 * 根据colorType(颜色类型)处理数据
	 * 
	 * @param pngBytes
	 *            图像信息数组
	 * @throws Exception
	 */
	private void process_IDAT(byte[] pngBytes) throws Exception {
		switch (colorType) {
		case 0: // 灰度图像
			break;
		case 2: // 真彩色图像
			process_RGB_Image(pngBytes, false);
			break;
		case 3: // 索引彩色图像
			process_Indexed_Image(pngBytes);
			break;
		case 4: // 带α通道数据的灰度图像
			break;
		case 6: // 带α通道数据的真彩色图像
			process_RGB_Image(pngBytes, true);
			break;
		default:
			System.out.println("..Invalid color type...");
		}

	}

	/**
	 * 索引彩色图像数据处理
	 * 
	 * @param pngBytes
	 *            图像信息数组
	 * @throws Exception
	 */
	private void process_Indexed_Image(byte[] pngBytes) throws Exception {
		int bytesPerScanLine = 0;
		int bytesPerPixel = 0;
		byte[] block;
		int b_indx = 0;
		switch (bitDepth) {// 图像深度
		case 8: // 真彩色图像
			bytesPerPixel = 1;
			bytesPerScanLine = width * bytesPerPixel;
			break;
		case 1: // 索引彩色图像
		case 2: // 索引彩色图像
		case 4: // 索引彩色图像
			// 无法解析——本程序的局限所在
			System.out.println("... " + bitDepth
					+ " bit indexed image not supported by this decoder...");

			break;
		default:
			System.out.println("... " + bitDepth
					+ " bit color depth is not valid for indexed image...");
		}
		block = new byte[bytesPerScanLine * height];
		BufferedInputStream bis = new BufferedInputStream(
				new InflaterInputStream(new ByteArrayInputStream(pngBytes)));
		byte[] filterType = new byte[height];
		for (int j = 0; j < height; j++, b_indx += width) {
			filterType[j] = (byte) bis.read();
			bis.read(block, b_indx, width);
		}
		de_filter_IndexedImage(filterType, block);
	}

	/**
	 * 滤波处理
	 * 
	 * @param filterType
	 *            滤波器信息
	 * @param img_data
	 *            被处理信息
	 */
	private void de_filter_IndexedImage(byte[] filterType, byte[] img_data) {
		int index = 0;
		for (int j = 0; j < height; j++) {
			switch (filterType[j]) {
			case NONE:
				break; // 无滤波器
			case SUB: // Sub 滤波器
				sub_Filter(j, img_data);
				break;
			case UP: // Up 滤波器
				if (j == 0)// 第一行无需滤波
					break;
				up_Filter(j, img_data);
				break;
			case AVERAGE: // Average 滤波器
				average_Filter(j, img_data);
				break;
			case PAETH: // Paeth 滤波器
				paeth_Filter(j, img_data);
				break;
			default: // Invalid 滤波器
				System.out.println("Invalid filter type: " + filterType[j]);
			}
			for (int i = 0; i < width; i++, index++) {
				pixels[index] = colorPalette[img_data[index] & 0xff];
			}
		}
	}

	/**
	 * 根据bitDepth(图像深度)处理数据
	 * 
	 * @param pngBytes
	 *            图像信息数组
	 * @param alpha
	 *            是否带α通道数据
	 * @throws Exception
	 */
	private void process_RGB_Image(byte[] pngByte, boolean alpha)
			throws Exception {
		int bytesPerScanLine = 0;
		int bytesPerPixel = 0;
		byte[] block;
		int pIndex = 0;
		int bIndex = 0;
		byte[] pixelRed;
		byte[] pixelGreen;
		byte[] pixelBlue;
		byte[] pixelAlpha = null;
		switch (bitDepth) {// 图像深度
		case 8:
			if (alpha)// 是否带α通道数据
				bytesPerPixel = 4;
			else
				bytesPerPixel = 3;
			bytesPerScanLine = width * bytesPerPixel;
			break;
		case 16:// 无法解析——本程序的局限所在
			System.out
					.println("...16 bit RGB image not supported by this decoder...");
			break;
		default:
			System.out.println("... " + bitDepth
					+ " bit color depth is not valid for RGB image...");
		}
		block = new byte[bytesPerScanLine];
		int image_size = width * height;
		pixelRed = new byte[image_size];
		pixelGreen = new byte[image_size];
		pixelBlue = new byte[image_size];
		if (alpha)
			pixelAlpha = new byte[image_size];
		BufferedInputStream bis = new BufferedInputStream(
				new InflaterInputStream(new ByteArrayInputStream(pngByte)));
		byte[] filter_type = new byte[height];

		for (int j = 0; j < height; j++) {
			bIndex = 0;
			filter_type[j] = (byte) bis.read();
			bis.read(block);
			for (int i = 0; i < width; i++, pIndex++) {
				pixelRed[pIndex] = block[bIndex++];
				pixelGreen[pIndex] = block[bIndex++];
				pixelBlue[pIndex] = block[bIndex++];
				if (alpha)
					pixelAlpha[pIndex] = block[bIndex++];
			}
		}
		de_filter_RGBImage(filter_type, pixelRed, pixelGreen, pixelBlue,
				pixelAlpha);
	}

	/**
	 * 滤波处理
	 * 
	 * @param filterType
	 *            滤波器信息
	 * @param pixelRed
	 *            Red数据信息
	 * @param pixelGreen
	 *            Green数据信息
	 * @param pixelBlue
	 *            Blue数据信息
	 * @param pixelAlpha
	 *            Alpha数据信息
	 */
	private void de_filter_RGBImage(byte[] filterType, byte[] pixelRed,
			byte[] pixelGreen, byte[] pixelBlue, byte[] pixelAlpha) {

		int pIndex = 0;

		for (int j = 0; j < height; j++) {

			switch (filterType[j]) {

			case NONE:
				break;// 无滤波器

			case SUB: // Sub 滤波器

				sub_Filter(j, pixelRed);

				sub_Filter(j, pixelGreen);

				sub_Filter(j, pixelBlue);

				if (pixelAlpha != null)

					sub_Filter(j, pixelAlpha);

				break;

			case UP: // Up 滤波器

				if (j == 0)

					break;

				up_Filter(j, pixelRed);

				up_Filter(j, pixelGreen);

				up_Filter(j, pixelBlue);

				if (pixelAlpha != null)

					up_Filter(j, pixelAlpha);

				break;

			case AVERAGE: // Average 滤波器

				average_Filter(j, pixelRed);

				average_Filter(j, pixelGreen);

				average_Filter(j, pixelBlue);

				if (pixelAlpha != null)

					average_Filter(j, pixelAlpha);

				break;

			case PAETH: // Paeth 滤波器

				paeth_Filter(j, pixelRed);

				paeth_Filter(j, pixelGreen);

				paeth_Filter(j, pixelBlue);

				if (pixelAlpha != null)

					paeth_Filter(j, pixelAlpha);

				break;

			default:

				System.out.println("Invalid filter type: " + filterType[j]);
			}

			if (pixelAlpha != null)

				for (int i = 0; i < width; i++, pIndex++) {

					pixels[pIndex] = (((pixelRed[pIndex] & 0xff) << 16)
							| ((pixelGreen[pIndex] & 0xff) << 8)
							| (pixelGreen[pIndex] & 0xff) | ((pixelAlpha[pIndex] & 0xff) << 24));
				}
			else

				for (int i = 0; i < width; i++, pIndex++) {

					pixels[pIndex] = ((0xff << 24)
							| ((pixelRed[pIndex] & 0xff) << 16)
							| ((pixelGreen[pIndex] & 0xff) << 8) | (pixelBlue[pIndex] & 0xff));
				}
		}
	}

	/**
	 * sub滤波处理数据
	 * 
	 * @param row
	 *            处理行数
	 * @param sample
	 *            被处理信息
	 */
	private void sub_Filter(int row, byte[] sample) {

		int p_indx = row * width;

		for (int i = 1; i < width; i++) {

			sample[p_indx + i] = (byte) (((sample[p_indx + i] & 0xff) + (sample[p_indx
					+ i - 1] & 0xff)) % 256);
		}
	}

	/**
	 * up滤波处理数据
	 * 
	 * @param row
	 *            处理行数
	 * @param sample
	 *            被处理信息
	 */
	private void up_Filter(int row, byte[] sample) {

		int p_indx = row * width;

		for (int i = 0; i < width; i++) {

			sample[p_indx + i] = (byte) (((sample[p_indx + i] & 0xff) + (sample[p_indx
					+ i - width] & 0xff)) % 256);
		}
	}

	/**
	 * average滤波处理数据
	 * 
	 * @param row
	 *            处理行数
	 * @param sample
	 *            被处理信息
	 */
	private void average_Filter(int row, byte[] sample) {

		int p_indx = row * width;

		int previous = 0;

		int upper = 0;

		for (int i = 0; i < width; i++) {

			if (row != 0)

				upper = (sample[p_indx + i - width] & 0xff);

			if (i >= 1)

				previous = (sample[p_indx + i - 1] & 0xff);

			sample[p_indx + i] = (byte) (((sample[p_indx + i] & 0xff) + ((upper + previous) >> 1)) % 256);
		}
	}

	/**
	 * paeth滤波处理数据
	 * 
	 * @param row
	 *            处理行数
	 * @param sample
	 *            被处理信息
	 */
	private void paeth_Filter(int row, byte[] sample) {

		int p_indx = row * width;

		int previous = 0;

		int upper = 0;

		int upper_previous = 0;

		for (int i = 0; i < width; i++) {

			if (row != 0)

				upper = (sample[p_indx + i - width] & 0xff);

			if (i >= 1)

				previous = (sample[p_indx + i - 1] & 0xff);

			if ((i >= 1) && (row != 0))

				upper_previous = (sample[p_indx + i - width - 1] & 0xff);

			sample[p_indx + i] = (byte) (((sample[p_indx + i] & 0xff) + paeth_Predictor(
					previous, upper, upper_previous)) % 256);
		}
	}

	private int paeth_Predictor(int left, int above, int upper_left) {

		int p = left + above - upper_left;

		int p_left = (p > left) ? (p - left) : (left - p);

		int p_above = (p > above) ? (p - above) : (above - p);

		int p_upper_left = (p > upper_left) ? (p - upper_left)
				: (upper_left - p);

		if ((p_left <= p_above) && (p_left <= p_upper_left))

			return left;

		else if (p_above <= p_upper_left)

			return above;

		else
			return upper_left;
	}

	/**
	 * 从byte数据组中读取一个Int大小的数据
	 * 
	 * @param buf
	 *            读取数据的数组
	 * @param start_idx
	 *            开始读取的位置
	 * @return 返回读取出的信息
	 * @throws Exception
	 */
	private int readInt(byte[] buf, int start_idx) throws Exception {

		return (((buf[start_idx++] & 0xff) << 24)
				| ((buf[start_idx++] & 0xff) << 16)
				| ((buf[start_idx++] & 0xff) << 8) | (buf[start_idx++] & 0xff));
	}

	/**
	 * 直接从文件流中取一个Int大小的数据
	 * 
	 * @param in
	 *            文件读取流
	 * @return 返回读取出的信息
	 * @throws Exception
	 */
	private int readInt(InputStream in) throws Exception {

		byte[] buf = new byte[4];

		in.read(buf, 0, 4);

		return (((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
				| ((buf[2] & 0xff) << 8) | (buf[3] & 0xff));
	}

	/**
	 * 从byte数据组中读取一个UnsignedInt大小的数据
	 * 
	 * @param buf
	 *            读取数据的数组
	 * @param start_idx
	 *            开始读取的位置
	 * @return 返回读取出的信息
	 * @throws Exception
	 */
	private long readUnsignedInt(byte[] buf, int start_idx) throws Exception {
		return (((buf[start_idx++] & 0xff) << 24)
				| ((buf[start_idx++] & 0xff) << 16)
				| ((buf[start_idx++] & 0xff) << 8) | (buf[start_idx++] & 0xff)) & 0xffffffffL;
	}

	/**
	 * 直接从文件流中取一个UnsignedInt大小的数据
	 * 
	 * @param in
	 *            文件读取流
	 * @return 返回读取出的信息
	 * @throws Exception
	 */
	private long readUnsignedInt(InputStream in) throws Exception {

		byte[] buf = new byte[4];

		in.read(buf, 0, 4);

		return (((buf[0] & 0xff) << 24) | ((buf[1] & 0xff) << 16)
				| ((buf[2] & 0xff) << 8) | (buf[3] & 0xff)) & 0xffffffffL;
	}

	/**
	 * 直接从文件流中取一个Long大小的数据
	 * 
	 * @param in
	 *            文件读取流
	 * @return 返回读取出的信息
	 * @throws Exception
	 */
	private long readLong(InputStream in) throws Exception {

		byte[] buf = new byte[8];

		in.read(buf, 0, 8);

		return (((buf[0] & 0xffL) << 56) | ((buf[1] & 0xffL) << 48)
				| ((buf[2] & 0xffL) << 40) | ((buf[3] & 0xffL) << 32)
				| ((buf[4] & 0xffL) << 24) | ((buf[5] & 0xffL) << 16)
				| ((buf[6] & 0xffL) << 8) | (buf[7] & 0xffL));
	}

}
