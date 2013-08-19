package com.besta.app.answerpaper.redrawpng;

import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/**
 * PngEncoder以java图像对象,并创建一数据组存储数据信息并以此生成PNG图像文件
 */

public class Encode extends Object {
	// bool常数 标记 alpha 通道 是否编码
	public static final boolean ENCODE_ALPHA = true;
	public static final boolean NO_ALPHA = false;
	// 滤波器方法(NONE,SUB,UP)
	public static final int FILTER_NONE = 0;
	public static final int FILTER_SUB = 1;
	public static final int FILTER_UP = 2;
	public static final int FILTER_LAST = 2;
	// IHDR（文件头数据块）,IDAT（图像数据块), IEND（图像结束数据）标记
	protected static final byte IHDR[] = { 73, 72, 68, 82 };
	protected static final byte IDAT[] = { 73, 68, 65, 84 };
	protected static final byte IEND[] = { 73, 69, 78, 68 };
	// png数据信息
	protected byte[] pngBytes;
	protected byte[] priorRow;
	protected byte[] leftBytes;
	// 图像对象
	protected Image image;
	// width 域
	protected int width, height;
	// 标记比特位置bytePos为当前数据位置而maxPos为PNG数组长度，也就是数据的最大位置
	protected int bytePos, maxPos;
	// CRC(循环冗余检测)
	protected CRC32 crc = new CRC32();
	// CRC值
	protected long crcValue;
	// 是否已经完成 alpha 编码
	protected boolean encodeAlpha;
	// 滤波器方法
	protected int filter;
	// 每像素信息的比特大小
	protected int bytesPerPixel;
	// 压缩方法(LZ77派生算法)等级
	protected int compressionLevel;

	/**
	 * 构造函数类
	 */
	public Encode() {
		this(null, false, FILTER_NONE, 0);
	}

	/**
	 * 构造函数指定编码的图像，并不做alpha通道编码
	 * 
	 * @param image表示使用直接颜色模式的Java
	 *            Image对象
	 */

	public Encode(Image image) {
		this(image, false, FILTER_NONE, 0);
	}

	/**
	 * 构造函数指定编码的图像，并根据参数决定是否alpha通道编码
	 * 
	 * @param image表示使用直接颜色模式的Java
	 *            Image对象
	 * @param encodeAlpha
	 *            表示是否alpha通道编码
	 */
	public Encode(Image image, boolean encodeAlpha) {
		this(image, encodeAlpha, FILTER_NONE, 0);
	}

	/**
	 * 构造函数指定编码的图像，并根据参数决定是否alpha通道编码,以及使用何种滤波器(默认压缩级为0)
	 * 
	 * @param image表示使用直接颜色模式的Java
	 *            Image对象
	 * @param encodeAlpha
	 *            表示是否alpha通道编码
	 * @param whichFilter
	 *            0表示none 1表示sub 2表示up
	 */
	public Encode(Image image, boolean encodeAlpha, int whichFilter) {
		this(image, encodeAlpha, whichFilter, 0);
	}

	/**
	 * 构造函数指定编码的图像，并根据参数决定是否alpha通道编码,以及使用何种滤波器和压缩方法
	 * 
	 * @param image表示使用直接颜色模式的Java
	 *            Image对象
	 * @param encodeAlpha
	 *            表示是否alpha通道编码
	 * @param whichFilter
	 *            0表示none 1表示sub 2表示up
	 * @param compLevel
	 *            压缩级 0~9
	 */
	public Encode(Image image, boolean encodeAlpha, int whichFilter,
			int compLevel) {
		this.image = image;
		this.encodeAlpha = encodeAlpha;
		setFilter(whichFilter);
		if (compLevel >= 0 && compLevel <= 9) {
			this.compressionLevel = compLevel;
		}
	}

	/**
	 * 设置要编码的image
	 * 
	 * @param image
	 *            表示一个使用直接颜色模式的Java Image对象
	 */
	public void setImage(Image image) {
		this.image = image;
		pngBytes = null;
	}

	/**
	 * 为当前image创建一个PNG字节数组并指定是否进行alpha编码
	 * 
	 * @param encodeAlpha
	 *            表示是否alpha通道编码
	 * @return 无问题存在的情况下返回一个字节数组
	 */
	public byte[] pngEncode(boolean encodeAlpha) {

		// {-119, 80, 78, 71, 13, 10, 26, 10}
		// 8字节的PNG文件署名域用来识别该文件是不是PNG文件
		// -119是指137的byte值
		byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

		if (image == null) {
			return null;
		}
		width = image.getWidth(null);
		height = image.getHeight(null);

		// pngBytes字节数组设置成有足够大能够储存所有像素（包括滤波器）的信息
		// 并且额外设置200比特作为文件头数据块的信息储存空间

		pngBytes = new byte[((width + 1) * height * 3) + 200];

		// maxPos，bytePos标记pngBytes中的数据位置

		maxPos = 0;

		bytePos = writeBytes(pngIdBytes, 0);

		writeHeader();

		if (writeImageData()) {
			writeEnd();
			pngBytes = resizeByteArray(pngBytes, maxPos);
		} else {
			pngBytes = null;
		}
		return pngBytes;
	}

	/**
	 * 为当前image创建一个PNG字节数组并指定是否进行alpha编码
	 * 
	 * @param encodeAlpha
	 *            表示是否alpha通道编码
	 * @return 无问题存在的情况下返回一个字节数组
	 */
	public byte[] MyPngEncode(int[] pixels, int width, int height,
			boolean encodeAlpha) {

		// {-119, 80, 78, 71, 13, 10, 26, 10}
		// 8字节的PNG文件署名域用来识别该文件是不是PNG文件
		// -119是指137的byte值
		byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };

		// pngBytes字节数组设置成有足够大能够储存所有像素（包括滤波器）的信息
		// 并且额外设置200比特作为文件头数据块的信息储存空间
		pngBytes = new byte[((width + 1) * height * 3) + 200];

		// maxPos，bytePos标记pngBytes中的数据位置
		maxPos = 0;
		bytePos = writeBytes(pngIdBytes, 0);
		MyWritePngHeader(width, height);

		if (MyWriteImageData(pixels, width, height)) {
			writeEnd();
			pngBytes = resizeByteArray(pngBytes, maxPos);
		} else {
			pngBytes = null;
		}
		return pngBytes;
	}

	/**
	 * 为当前image创建一个PNG字节数组并根据当前encodeAlpha的值决定是否进行alpha编码
	 * 
	 * @return 无问题存在的情况下返回一个字节数组
	 */
	public byte[] pngEncode() {
		return pngEncode(encodeAlpha);
	}

	/**
	 * 设置encodeAlpha的值
	 */
	public void setEncodeAlpha(boolean encodeAlpha) {
		this.encodeAlpha = encodeAlpha;
	}

	/**
	 * 返回encodeAlpha的值
	 */
	public boolean getEncodeAlpha() {
		return encodeAlpha;
	}

	/**
	 * 设置滤波器类型
	 */
	public void setFilter(int whichFilter) {
		this.filter = FILTER_NONE;
		if (whichFilter <= FILTER_LAST) {
			this.filter = whichFilter;
		}
	}

	/**
	 * 返回滤波器类型
	 */
	public int getFilter() {
		return filter;
	}

	/**
	 * 设置压缩方法参数
	 */
	public void setCompressionLevel(int level) {
		if (level >= 0 && level <= 9) {
			this.compressionLevel = level;
		}
	}

	/**
	 * 返回压缩方法参数
	 */
	public int getCompressionLevel() {
		return compressionLevel;
	}

	/**
	 * 增加或者减少比特数组的长度
	 * 
	 * @param array
	 *            要改变长度的数组.
	 * @param newLength
	 *            数组的新的长度.
	 * @return 返回新的数组，如果新数组长度比原来数组短，那么被削短的部分会被截去
	 */
	protected byte[] resizeByteArray(byte[] array, int newLength) {
		byte[] newArray = new byte[newLength];
		int oldLength = array.length;

		System.arraycopy(array, 0, newArray, 0, Math.min(oldLength, newLength));
		return newArray;
	}

	/**
	 * 写入一组比特数据到PNG信息比特数组中 注意：这个程序对于更新maxPos，在数组中的最大元素有副作用
	 * 如果数据更大的情况下，数组将会被1000比特或者要写入的数据的长度所缩放
	 * 
	 * @param data
	 *            要写入PNG比特数组中的数据
	 * @param offset
	 *            要写入的开始位置
	 * @return 写入数据在PNG数组中的位置的下一个位置即下次写入信息的位置
	 */
	protected int writeBytes(byte[] data, int offset) {
		maxPos = Math.max(maxPos, offset + data.length);
		if (data.length + offset > pngBytes.length) {// 扩充数组长度
			pngBytes = resizeByteArray(pngBytes,
					pngBytes.length + Math.max(1000, data.length));
		}
		System.arraycopy(data, 0, pngBytes, offset, data.length);
		return offset + data.length;
	}

	/**
	 * 写入一组比特数据到PNG信息比特数组中 注意：这个程序对于更新maxPos，在数组中的最大元素有副作用
	 * 如果数据更大的情况下，数组将会被1000比特或者要写入的数据的长度所缩放
	 * 
	 * @param data
	 *            要写入PNG比特数组中的数据
	 * @param nBytes
	 *            要写入的比特数
	 * @param offset
	 *            要写入的开始位置
	 * @return 写入数据在PNG数组中的位置的下一个位置即下次写入信息的位置
	 */
	protected int writeBytes(byte[] data, int nBytes, int offset) {
		maxPos = Math.max(maxPos, offset + nBytes);
		if (nBytes + offset > pngBytes.length) {
			pngBytes = resizeByteArray(pngBytes,
					pngBytes.length + Math.max(1000, nBytes));
		}
		System.arraycopy(data, 0, pngBytes, offset, nBytes);
		return offset + nBytes;
	}

	/**
	 * 从所给位置开始写入一个2字节的int数据到PNG数据数组中
	 * 
	 * @param n
	 *            要写入的Int值
	 * @param offset
	 *            要写入的位置
	 * @return 写入数据在PNG数组中的位置的下一个位置即下次写入信息的位置
	 */
	protected int writeInt2(int n, int offset) {
		byte[] temp = { (byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return writeBytes(temp, offset);
	}

	/**
	 * 从所给位置开始写入一个4字节的int数据到PNG数据数组中
	 * 
	 * @param n
	 *            要写入的Int值
	 * @param offset
	 *            要写入的位置
	 * @return 写入数据在PNG数组中的位置的下一个位置即下次写入信息的位置
	 */
	protected int writeInt4(int n, int offset) {
		// 将int值转换为byte型
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff),
				(byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return writeBytes(temp, offset);
	}

	/**
	 * 从所给位置开始写入一个字节的int数据到PNG数据数组中
	 * 
	 * @param b
	 *            要写入的Int值
	 * @param offset
	 *            要写入的位置
	 * @return 写入数据在PNG数组中的位置的下一个位置即下次写入信息的位置
	 */
	protected int writeByte(int b, int offset) {
		byte[] temp = { (byte) b };
		return writeBytes(temp, offset);
	}

	/**
	 * 写入IHDR chunk（文件头数据块）到PNG数组中
	 */
	protected void writeHeader() {
		int startPos;

		// 文件头数据块由13字节组成，先写入长度13
		startPos = bytePos = writeInt4(13, bytePos);
		// 写入数据块类型码 IHDR
		bytePos = writeBytes(IHDR, bytePos);
		width = image.getWidth(null);
		height = image.getHeight(null);
		// 写入Width和Heigth数据信息
		bytePos = writeInt4(width, bytePos);
		bytePos = writeInt4(height, bytePos);
		// bit depth图像深度 这里设置为 真彩色图像：8
		bytePos = writeByte(8, bytePos);
		// ColorType 颜色类型 2：真彩色图像 6：带α通道数据的真彩色图像direct model
		bytePos = writeByte((encodeAlpha) ? 6 : 2, bytePos);
		bytePos = writeByte(0, bytePos); // compression method 压缩方法(LZ77派生算法)
		bytePos = writeByte(0, bytePos); // filter method 滤波器方法
		bytePos = writeByte(0, bytePos); // no interlace 隔行扫描方法: 0：非隔行扫描

		// CRC(循环冗余检测)
		crc.reset();
		crc.update(pngBytes, startPos, bytePos - startPos);
		crcValue = crc.getValue();
		bytePos = writeInt4((int) crcValue, bytePos);
	}

	/**
	 * 写入IHDR chunk（文件头数据块）到PNG数组中
	 */
	protected void MyWritePngHeader(int width, int height) {
		int startPos;

		// 文件头数据块由13字节组成，先写入长度13
		startPos = bytePos = writeInt4(13, bytePos);
		// 写入数据块类型码 IHDR
		bytePos = writeBytes(IHDR, bytePos);
		// 写入Width和Heigth数据信息
		bytePos = writeInt4(width, bytePos);
		bytePos = writeInt4(height, bytePos);
		// bit depth图像深度 这里设置为 真彩色图像：8
		bytePos = writeByte(8, bytePos);
		// ColorType 颜色类型 2：真彩色图像 6：带α通道数据的真彩色图像direct model
		bytePos = writeByte((encodeAlpha) ? 6 : 2, bytePos);
		bytePos = writeByte(0, bytePos); // compression method 压缩方法(LZ77派生算法)
		bytePos = writeByte(0, bytePos); // filter method 滤波器方法
		bytePos = writeByte(0, bytePos); // no interlace 隔行扫描方法: 0：非隔行扫描

		// CRC(循环冗余检测)
		crc.reset();
		crc.update(pngBytes, startPos, bytePos - startPos);
		crcValue = crc.getValue();
		bytePos = writeInt4((int) crcValue, bytePos);
	}

	/**
	 * 执行 sub 滤波器 使用临时比特数组储存像素值，这个16比特的数组能更好地附加alpha
	 * 
	 * @param pixels
	 *            像素信息
	 * @param startPos
	 *            开始位置
	 * @param width
	 *            pixels的组宽.
	 */
	protected void filterSub(byte[] pixels, int startPos, int width) {
		int i;
		int offset = bytesPerPixel;
		int actualStart = startPos + offset;
		int nBytes = width * bytesPerPixel;
		int leftInsert = offset;
		int leftExtract = 0;

		for (i = actualStart; i < startPos + nBytes; i++) {
			leftBytes[leftInsert] = pixels[i];
			pixels[i] = (byte) ((pixels[i] - leftBytes[leftExtract]) % 256);
			leftInsert = (leftInsert + 1) % 0x0f;
			leftExtract = (leftExtract + 1) % 0x0f;
		}
	}

	/**
	 * 执行 up 滤波器 使用临时比特数组储存像素值，这个16比特的数组能更好地附加alpha
	 * 
	 * @param pixels
	 *            像素信息
	 * @param startPos
	 *            开始位置
	 * @param width
	 *            pixels的组宽.
	 */
	protected void filterUp(byte[] pixels, int startPos, int width) {
		int i, nBytes;
		byte currentByte;

		nBytes = width * bytesPerPixel;

		for (i = 0; i < nBytes; i++) {
			currentByte = pixels[startPos + i];
			pixels[startPos + i] = (byte) ((pixels[startPos + i] - priorRow[i]) % 256);
			priorRow[i] = currentByte;
		}
	}

	/**
	 * 把image信息写入png数组中 将写入一个或多个 PNG图像的 IDAT chunks（图像数据块）
	 * 无论是不是比较少，为了保持信息的完整，尽可能多的写入图像信息
	 * 
	 * @return 如果获取像素信息失败则返回false，如果无错误则返回true
	 */
	protected boolean writeImageData() {
		int rowsLeft = height; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行

		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置

		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		PixelGrabber pg;

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = new Deflater(compressionLevel);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);

		DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes,
				scrunch);
		try {
			while (rowsLeft > 0) {
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				int[] pixels = new int[width * nRows];
				pg = new PixelGrabber(image, 0, startRow, width, nRows, pixels,
						0, width);
				try {
					pg.grabPixels();
				} catch (Exception e) {
					System.err.println("interrupted waiting for pixels!");
					return false;
				}
				if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
					System.err.println("image fetch aborted or errored");
					return false;
				}
				// 创建一个数据块，扫描行增加nRows作为滤波字节
				scanLines = new byte[width * nRows * bytesPerPixel + nRows];
				if (filter == FILTER_SUB) {
					leftBytes = new byte[16];
				}
				if (filter == FILTER_UP) {
					priorRow = new byte[width * bytesPerPixel];
				}

				scanPos = 0;
				startPos = 1;
				for (int i = 0; i < width * nRows; i++) {
					if (i % width == 0) {
						scanLines[scanPos++] = (byte) filter;
						startPos = scanPos;
					}
					scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i] >> 8) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
					if (encodeAlpha) {
						scanLines[scanPos++] = (byte) ((pixels[i] >> 24) & 0xff);
					}
					if ((i % width == width - 1) && (filter != FILTER_NONE)) {
						if (filter == FILTER_SUB) {
							filterSub(scanLines, startPos, width);
						}
						if (filter == FILTER_UP) {
							filterUp(scanLines, startPos, width);
						}
					}
				}
				// 写入行到输出域
				compBytes.write(scanLines, 0, scanPos);

				startRow += nRows;
				rowsLeft -= nRows;
			}
			compBytes.close();
			// 写入压缩信息
			compressedLines = outBytes.toByteArray();
			nCompressed = compressedLines.length;

			crc.reset();
			bytePos = writeInt4(nCompressed, bytePos);
			bytePos = writeBytes(IDAT, bytePos);
			crc.update(IDAT);
			bytePos = writeBytes(compressedLines, nCompressed, bytePos);
			crc.update(compressedLines, 0, nCompressed);

			crcValue = crc.getValue();
			bytePos = writeInt4((int) crcValue, bytePos);
			scrunch.finish();
			return true;
		} catch (IOException e) {
			System.err.println(e.toString());
			return false;
		}
	}

	/**
	 * 把image信息写入png数组中 将写入一个或多个 PNG图像的 IDAT chunks（图像数据块）
	 * 无论是不是比较少，为了保持信息的完整，尽可能多的写入图像信息
	 * 
	 * @return 如果获取像素信息失败则返回false，如果无错误则返回true
	 */
	protected boolean MyWriteImageData(int[] pixels, int width, int height) {
		int rowsLeft = height; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行

		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置

		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		// PixelGrabber pg;

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = new Deflater(compressionLevel);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1024);

		DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes,
				scrunch);
		try {
			while (rowsLeft > 0) {
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				// int[] pixels = new int[width * nRows];
				// pg = new PixelGrabber(image, 0, startRow, width, nRows,
				// pixels,
				// 0, width);
				// try {
				// pg.grabPixels();
				// } catch (Exception e) {
				// System.err.println("interrupted waiting for pixels!");
				// return false;
				// }
				// if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
				// System.err.println("image fetch aborted or errored");
				// return false;
				// }
				// 创建一个数据块，扫描行增加nRows作为滤波字节
				scanLines = new byte[width * nRows * bytesPerPixel + nRows];
				if (filter == FILTER_SUB) {
					leftBytes = new byte[16];
				}
				if (filter == FILTER_UP) {
					priorRow = new byte[width * bytesPerPixel];
				}

				scanPos = 0;
				startPos = 1;
				for (int i = 0; i < width * nRows; i++) {
					if (i % width == 0) {
						scanLines[scanPos++] = (byte) filter;
						startPos = scanPos;
					}
					scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i] >> 8) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
					if (encodeAlpha) {
						scanLines[scanPos++] = (byte) ((pixels[i] >> 24) & 0xff);
					}
					if ((i % width == width - 1) && (filter != FILTER_NONE)) {
						if (filter == FILTER_SUB) {
							filterSub(scanLines, startPos, width);
						}
						if (filter == FILTER_UP) {
							filterUp(scanLines, startPos, width);
						}
					}
				}
				// 写入行到输出域
				compBytes.write(scanLines, 0, scanPos);

				startRow += nRows;
				rowsLeft -= nRows;
			}
			compBytes.close();
			// 写入压缩信息
			compressedLines = outBytes.toByteArray();
			nCompressed = compressedLines.length;

			crc.reset();
			bytePos = writeInt4(nCompressed, bytePos);
			bytePos = writeBytes(IDAT, bytePos);
			crc.update(IDAT);
			bytePos = writeBytes(compressedLines, nCompressed, bytePos);
			crc.update(compressedLines, 0, nCompressed);

			crcValue = crc.getValue();
			bytePos = writeInt4((int) crcValue, bytePos);
			scrunch.finish();
			return true;
		} catch (IOException e) {
			System.err.println(e.toString());
			return false;
		}
	}

	/**
	 * 写入IEND数据域的信息
	 */
	protected void writeEnd() {
		bytePos = writeInt4(0, bytePos);
		bytePos = writeBytes(IEND, bytePos);
		crc.reset();
		crc.update(IEND);
		crcValue = crc.getValue();
		bytePos = writeInt4((int) crcValue, bytePos);
	}

}
