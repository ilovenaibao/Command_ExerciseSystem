package com.besta.app.answerpaper.redrawpng;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.besta.app.testcallactivity.StartAnswerPaperSetting;

/**
 * PngEncoder以java图像对象,并创建一数据组存储数据信息并以此生成PNG图像文件
 */

public class MyPngEncode extends Object {

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
	// protected Image image;
	// 圖像所有像素
	private int[] imgPixls = null;
	// 圖像航像素count
	private int imgPixlsOneLineCount;
	// width 域
	protected int width, height;
	// 标记比特位置bytePos为当前数据位置而maxPos为PNG数组长度以及IDAT起始位置和長度，也就是数据的最大位置
	protected int bytePos, maxPos, iDatlen;
	long iDatOffset;
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
	// 臨時Buffer
	private byte[] tempBytes;

	/**
	 * 构造函数类
	 */
	public MyPngEncode() {

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
	public MyPngEncode(int[] pixs, boolean encodeAlpha, int whichFilter,
			int compLevel) {
		imgPixls = pixs;
		this.encodeAlpha = encodeAlpha;
		setFilter(whichFilter);
		if (compLevel >= 0 && compLevel <= 9) {
			this.compressionLevel = compLevel;
		}
	}

	// add by Taylor Gu

	/**
	 * 初始化Png圖像屬性信息
	 * 
	 * @param pixles
	 *            像素組
	 * @param encodeAlpha
	 *            a通道
	 * @param whichFilter
	 *            濾波器
	 * @param compLevel
	 *            壓縮等級
	 * @param imgWidth
	 *            圖像寬
	 * @param imgHeight
	 *            圖像高
	 */
	public MyPngEncode(int[] pixles, boolean encodeAlpha, int whichFilter,
			int compLevel, int imgWidth, int imgHeight) {
		tempBytes = null;
		maxPos = bytePos = iDatlen = 0;
		iDatOffset = 0;
		imgPixls = pixles;
		this.encodeAlpha = encodeAlpha;
		setFilter(whichFilter);
		if (compLevel >= 0 && compLevel <= 9) {
			this.compressionLevel = compLevel;
		}
		this.width = imgWidth;
		this.height = imgHeight;
	}

	/**
	 * 初始化Png圖像屬性信息
	 * 
	 * @param pixles
	 *            像素組
	 * @param encodeAlpha
	 *            a通道
	 * @param whichFilter
	 *            濾波器
	 * @param compLevel
	 *            壓縮等級
	 * @param imgWidth
	 *            圖像寬
	 * @param imgHeight
	 *            圖像高
	 */
	public MyPngEncode(boolean encodeAlpha, int whichFilter, int compLevel,
			int imgWidth, int imgHeight) {
		tempBytes = null;
		maxPos = bytePos = iDatlen = 0;
		iDatOffset = 0;
		imgPixls = null;
		this.encodeAlpha = encodeAlpha;
		setFilter(whichFilter);
		if (compLevel >= 0 && compLevel <= 9) {
			this.compressionLevel = compLevel;
		}
		this.width = imgWidth;
		this.height = imgHeight;
	}

	/**
	 * 清空TempBytes數組
	 */
	public void ClearTempBytes() {
		tempBytes = null;
		bytePos = 0;
	}

	/**
	 * 將信息寫入TempBytes buffer中
	 * 
	 * @param data
	 *            要寫入的數據
	 * @param offset
	 *            寫入TempBytes的位置
	 * @return 下一次寫入數組的位置
	 */
	private int myPngWriteBytes(byte[] data, int offset) {
		maxPos = Math.max(maxPos, offset + data.length);
		// 如果數組長度不夠，則擴充
		if (tempBytes == null) {
			tempBytes = new byte[1];
			tempBytes = resizeByteArray(tempBytes, data.length);
		} else {
			if (data.length + offset > tempBytes.length) {// 扩充数组长度
				tempBytes = resizeByteArray(tempBytes, tempBytes.length
						+ data.length);
			}
		}
		// 寫data 到TempBytes
		System.arraycopy(data, 0, tempBytes, offset, data.length);
		return offset + data.length;
	}

	/**
	 * 寫入int型信息到TempBytes Buffer中
	 * 
	 * @param n
	 *            寫入的int數據
	 * @param offset
	 *            寫入到TempBytes的位置
	 * @return 下一次寫入數組的位置
	 */
	private int myWriteInt4(int n, int offset) {
		// 将int值转换为byte型
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff),
				(byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return myPngWriteBytes(temp, offset);
	}

	/**
	 * 轉化 int ---> byte 4
	 * 
	 * @param n
	 * @return byte[4]
	 */
	public static byte[] getIntToByte4(int n) {
		// 将int值转换为byte型
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff),
				(byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return temp;
	}

	/**
	 * 4byte型轉int型
	 * 
	 * @param buffer
	 *            待轉換byte[4]
	 * @return int型數
	 */
	public static int getByte4ToInt(byte[] buffer) {
		int tmpValue = 256;
		int ret = ((int) (0 > buffer[0] ? (buffer[0] + tmpValue) : buffer[0])) << 24
				| (int) (0 > buffer[1] ? (buffer[1] + tmpValue) : buffer[1]) << 16
				| (int) (0 > buffer[2] ? (buffer[2] + tmpValue) : buffer[2]) << 8
				| (int) (0 > buffer[3] ? (buffer[3] + tmpValue) : buffer[3]);

		return ret;
	}

	/**
	 * Int to byte 字節轉化
	 * 
	 * @param n
	 * @return byte[4]
	 */
	private byte[] myIntToByte(int n) {
		// 将int值转换为byte型
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff),
				(byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return temp;
	}

	/**
	 * 寫入一個byte數據
	 * 
	 * @param b
	 *            要寫入的byte數據
	 * @param offset
	 *            寫入TempBytes的位置
	 * @return 写入数据在PNG数组中的位置的下一个位置即下次写入信息的位置
	 */
	private int myWriteByte(int b, int offset) {
		byte[] temp = { (byte) b };
		return myPngWriteBytes(temp, offset);
	}

	/**
	 * 寫此png圖像的頭信息
	 */
	private void myWriteHeaderBuffer() {
		int startPos;

		// 文件头数据块由13字节组成，先写入长度13
		startPos = bytePos = myWriteInt4(13, bytePos);
		// 写入数据块类型码 IHDR
		bytePos = myPngWriteBytes(IHDR, bytePos);
		// 写入Width和Heigth数据信息
		bytePos = myWriteInt4(width, bytePos);
		bytePos = myWriteInt4(height, bytePos);
		// bit depth图像深度 这里设置为 真彩色图像：8
		bytePos = myWriteByte(8, bytePos);
		// ColorType 颜色类型 2：真彩色图像 6：带α通道数据的真彩色图像direct model
		bytePos = myWriteByte((encodeAlpha) ? 6 : 2, bytePos);
		bytePos = myWriteByte(0, bytePos); // compression method
											// 压缩方法(LZ77派生算法)
		bytePos = myWriteByte(0, bytePos); // filter method 滤波器方法
		bytePos = myWriteByte(0, bytePos); // no interlace 隔行扫描方法: 0：非隔行扫描

		// CRC(循环冗余检测)
		crc.reset();
		crc.update(tempBytes, startPos, bytePos - startPos);
		crcValue = crc.getValue();
		bytePos = myWriteInt4((int) crcValue, bytePos);
	}

	/**
	 * 獲取Png圖像頭buffer
	 * 
	 * @return byte[] Png IHDR buffer
	 */
	public byte[] getMyPngHeaderBuffer() {
		ClearTempBytes();
		// {-119, 80, 78, 71, 13, 10, 26, 10}
		// 8字节的PNG文件署名域用来识别该文件是不是PNG文件
		// -119是指137的byte值
		byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
		// maxPos，bytePos标记pngBytes中的数据位置
		maxPos = 0;
		// 寫入png圖片固定標識
		bytePos = myPngWriteBytes(pngIdBytes, 0);
		// 寫入當前png圖片頭信息
		myWriteHeaderBuffer();
		iDatOffset = bytePos;

		return tempBytes;
	}

	/**
	 * 寫入數據到tempBytes中
	 * 
	 * @param data
	 *            待寫入數據
	 * @param nBytes
	 *            寫入的count
	 * @param offset
	 *            待寫入的數據起始位置
	 * @return 當前數組位置
	 */
	private int myWriteBytes(byte[] data, int nBytes, int offset) {
		maxPos = Math.max(maxPos, offset + nBytes);
		// 如果數組長度不夠，則擴充
		if (tempBytes == null) {
			tempBytes = new byte[1];
			tempBytes = resizeByteArray(tempBytes, data.length);
		} else {
			if (nBytes + offset > tempBytes.length) {
				tempBytes = resizeByteArray(tempBytes, tempBytes.length
						+ nBytes);
			}
		}
		// 寫data 到TempBytes
		System.arraycopy(data, 0, tempBytes, offset, nBytes);
		return offset + nBytes;
	}

	/**
	 * 把子像素數組信息写入png数组中 将写入一个或多个 PNG图像的 IDAT chunks（图像数据块）
	 * 无论是不是比较少，为了保持信息的完整，尽可能多的写入图像信息
	 * 
	 * @return 如果获取像素信息失败则返回false，如果无错误则返回true
	 * @throws FileNotFoundException
	 */
	public byte[] WriteMySubImageDataTest(int[] srcPixles,
			RandomAccessFile targetFile, int isStartOrEndPos)
			throws FileNotFoundException {

		// isStartOrEndPos: -1 :start ; 1: end;
		ClearTempBytes();
		int rowsLeft = height; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行

		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置

		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = null; // 壓縮等級
		ByteArrayOutputStream outBytes = null; // 待壓縮壓縮流
		DeflaterOutputStream compBytes = null; // 壓縮OutPutStream流
		scrunch = new Deflater(compressionLevel);
		outBytes = new ByteArrayOutputStream(1024);
		compBytes = new DeflaterOutputStream(outBytes, scrunch);
		imgPixlsOneLineCount = 0; // bitmap 的pixles行數
		try {
			while (rowsLeft > 0) {
				if (imgPixlsOneLineCount >= height) {
					break;
				}
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				int startCount = imgPixlsOneLineCount * width;
				if (startCount >= srcPixles.length) {
					break;
				}
				int[] pixels = new int[width * nRows];
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = srcPixles[startCount];
					startCount++;
				}
				imgPixlsOneLineCount++;
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
				// 濾波處理
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

			if (isStartOrEndPos == 1) {
				// End
				compBytes.close();
				compressedLines = outBytes.toByteArray();
				nCompressed = compressedLines.length;
				ClearTempBytes();

				// add by Taylor
				int maxCompressLen = 1024;
				int nCount = 0;
				boolean endFlag = false;
				while (true) {
					if (endFlag) {
						break;
					}
					int totalCompressLen = 0;
					if (nCompressed < maxCompressLen) {
						totalCompressLen = nCompressed;
						endFlag = true;
					} else {
						totalCompressLen = maxCompressLen;
						nCompressed -= totalCompressLen;
					}
					int startCount = nCount * maxCompressLen;
					byte[] cmprs = new byte[totalCompressLen];
					for (int i = 0; i < totalCompressLen; i++) {
						cmprs[i] = compressedLines[i + startCount];
					}
					iDatlen = totalCompressLen;
					bytePos = myPngWriteBytes(IDAT, bytePos);
					crc.reset();
					crc.update(IDAT);
					bytePos = myWriteBytes(cmprs, totalCompressLen, bytePos);
					crc.update(cmprs, 0, totalCompressLen);
					crcValue = crc.getValue();
					bytePos = myWriteInt4((int) crcValue, bytePos);
					targetFile.seek(iDatOffset);
					targetFile.write(myIntToByte(iDatlen));
					targetFile.seek(targetFile.length());
					targetFile.write(tempBytes);
					iDatOffset = targetFile.length();
					ClearTempBytes();
					nCount++;
				}

				// add end

				// 以下是將壓縮信息以一個IDAT方式寫入圖片
				// iDatlen += nCompressed;
				// bytePos = myPngWriteBytes(IDAT, bytePos);
				// crc.reset();
				// crc.update(IDAT);
				// bytePos = myWriteBytes(compressedLines, nCompressed,
				// bytePos);
				// crc.update(compressedLines, 0, nCompressed);
				// crcValue = crc.getValue();
				// bytePos = myWriteInt4((int) crcValue, bytePos);
				scrunch.finish();
			}

			// // add by Taylor
			// compBytes_1.close();
			// compressedLines = outBytes_1.toByteArray();
			// nCompressed = compressedLines.length;
			// iDatlen = nCompressed;
			// iDatOffset = targetFile.length();
			// ClearTempBytes();
			// bytePos = myPngWriteBytes(IDAT, bytePos);
			// crc.reset();
			// crc.update(IDAT);
			// bytePos = myWriteBytes(compressedLines, nCompressed, bytePos);
			// crc.update(compressedLines, 0, nCompressed);
			// crcValue = crc.getValue();
			// bytePos = myWriteInt4((int) crcValue, bytePos);
			// targetFile.seek(iDatOffset);
			// targetFile.write(myIntToByte(iDatlen));
			// targetFile.seek(targetFile.length());
			// targetFile.write(tempBytes);
			// scrunch_1.finish();
			//
			// // add end

			// if (tempBytes != null) {
			// if (isStartOrEndPos == 1) {
			// targetFile.seek(iDatOffset);
			// targetFile.write(myIntToByte(iDatlen));
			// targetFile.seek(targetFile.length());
			// }
			// targetFile.write(tempBytes);
			// }
			setIDATLen(0, targetFile.length());
			setImgPixlesLineCount(0);
			return tempBytes;
		} catch (IOException e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/**
	 * 與WriteMySubImageDataTest不同之處: 從 rf 中讀出所有像素點的集合.
	 * 缺點是從開始創建像素點集合到讀取並且生成png圖需要2次讀寫文件耗費時間很長
	 * 
	 * @return 如果获取像素信息失败则返回false，如果无错误则返回true
	 * @throws FileNotFoundException
	 */
	public byte[] WriteMySubImageDataTest_2(int[] srcPixles,
			RandomAccessFile targetFile, int isStartOrEndPos)
			throws FileNotFoundException {

		// add by Taylor

		// 從StartAnswerPaperSetting.resultPngDataTmpPixlesPath文件當中讀pixles信息
		RandomAccessFile rf = new RandomAccessFile(
				StartAnswerPaperSetting.resultPngDataTmpPixlesPath, "r");
		// add end

		// isStartOrEndPos: -1 :start ; 1: end;
		ClearTempBytes();
		int rowsLeft = height; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行

		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置

		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = null;
		ByteArrayOutputStream outBytes = null;
		DeflaterOutputStream compBytes = null;
		scrunch = new Deflater(compressionLevel);
		outBytes = new ByteArrayOutputStream(1024);
		compBytes = new DeflaterOutputStream(outBytes, scrunch);
		imgPixlsOneLineCount = 0;
		int imgSize = this.width * this.height;
		try {
			while (rowsLeft > 0) {
				if (imgPixlsOneLineCount >= height) {
					break;
				}
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				int startCount = imgPixlsOneLineCount * width;
				if (startCount >= imgSize) {
					break;
				}
				int[] pixels = new int[width * nRows];
				byte[] tmpLineBytes = new byte[pixels.length * 4];
				rf.read(tmpLineBytes);
				for (int i = 0; i < pixels.length; i++) {
					// pixels[i] = srcPixles[startCount];
					byte[] tmp4Bytes = new byte[4];
					for (int j = 0; j < 4; j++) {
						tmp4Bytes[j] = tmpLineBytes[i * 4 + j];
					}
					pixels[i] = getByte4ToInt(tmp4Bytes);
					startCount++;
				}
				imgPixlsOneLineCount++;
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
				// 濾波處理
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

			if (isStartOrEndPos == 1) {
				// End
				compBytes.close();
				compressedLines = outBytes.toByteArray();
				nCompressed = compressedLines.length;
				ClearTempBytes();

				// add by Taylor
				int maxCompressLen = 1024;
				int nCount = 0;
				boolean endFlag = false;
				while (true) {
					if (endFlag) {
						break;
					}
					int totalCompressLen = 0;
					if (nCompressed < maxCompressLen) {
						totalCompressLen = nCompressed;
						endFlag = true;
					} else {
						totalCompressLen = maxCompressLen;
						nCompressed -= totalCompressLen;
					}
					int startCount = nCount * maxCompressLen;
					byte[] cmprs = new byte[totalCompressLen];
					for (int i = 0; i < totalCompressLen; i++) {
						cmprs[i] = compressedLines[i + startCount];
					}
					iDatlen = totalCompressLen;
					bytePos = myPngWriteBytes(IDAT, bytePos);
					crc.reset();
					crc.update(IDAT);
					bytePos = myWriteBytes(cmprs, totalCompressLen, bytePos);
					crc.update(cmprs, 0, totalCompressLen);
					crcValue = crc.getValue();
					bytePos = myWriteInt4((int) crcValue, bytePos);
					targetFile.seek(iDatOffset);
					targetFile.write(myIntToByte(iDatlen));
					targetFile.seek(targetFile.length());
					targetFile.write(tempBytes);
					iDatOffset = targetFile.length();
					ClearTempBytes();
					nCount++;
				}
				scrunch.finish();
			}
			rf.close();
			setIDATLen(0, targetFile.length());
			setImgPixlesLineCount(0);
			return tempBytes;
		} catch (IOException e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/**
	 * 與WriteMySubImageDataTest不同之處: 傳入參數多了圖片寬高
	 * 
	 * @return 如果获取像素信息失败则返回false，如果无错误则返回true
	 * @throws FileNotFoundException
	 */
	public void WriteMySubImageDataTest_3(int[] srcPixles, int srcWidth,
			int srcHeight, RandomAccessFile targetFile, boolean isEndFlag)
			throws FileNotFoundException {
		// isStartOrEndPos: -1 :start ; 1: end;
		ClearTempBytes();
		int rowsLeft = srcHeight; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行

		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置

		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = null;
		ByteArrayOutputStream outBytes = null;
		DeflaterOutputStream compBytes = null;
		scrunch = new Deflater(compressionLevel);
		outBytes = new ByteArrayOutputStream(1024);
		compBytes = new DeflaterOutputStream(outBytes, scrunch);
		int imgPixlsOneLineCount = 0;
		int imgSize = srcWidth * srcHeight;
		try {
			while (rowsLeft > 0) {
				if (imgPixlsOneLineCount >= srcHeight) {
					break;
				}
				nRows = Math.min(32767 / (srcWidth * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				int startCount = imgPixlsOneLineCount * srcWidth;
				if (startCount >= imgSize) {
					break;
				}
				int[] pixels = new int[srcWidth * nRows];
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = srcPixles[startCount];
					startCount++;
				}
				imgPixlsOneLineCount++;
				// 创建一个数据块，扫描行增加nRows作为滤波字节
				scanLines = new byte[srcWidth * nRows * bytesPerPixel + nRows];
				if (filter == FILTER_SUB) {
					leftBytes = new byte[16];
				}
				if (filter == FILTER_UP) {
					priorRow = new byte[srcWidth * bytesPerPixel];
				}

				scanPos = 0;
				startPos = 1;
				for (int i = 0; i < srcWidth * nRows; i++) {
					if (i % srcWidth == 0) {
						scanLines[scanPos++] = (byte) filter;
						startPos = scanPos;
					}
					scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i] >> 8) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
					if (encodeAlpha) {
						scanLines[scanPos++] = (byte) ((pixels[i] >> 24) & 0xff);
					}
					if ((i % srcWidth == srcWidth - 1)
							&& (filter != FILTER_NONE)) {
						if (filter == FILTER_SUB) {
							filterSub(scanLines, startPos, srcWidth);
						}
						if (filter == FILTER_UP) {
							filterUp(scanLines, startPos, srcWidth);
						}
					}
				}
				// 写入行到输出域
				compBytes.write(scanLines, 0, scanPos);

				startRow += nRows;
				rowsLeft -= nRows;
			}

			if (!isEndFlag) {
				// End
				compBytes.close();
				compressedLines = outBytes.toByteArray();
				nCompressed = compressedLines.length;
				ClearTempBytes();

				// add by Taylor
				int maxCompressLen = 1024;
				int nCount = 0;
				boolean endFlag = false;
				while (true) {
					if (endFlag) {
						break;
					}
					int totalCompressLen = 0;
					if (nCompressed < maxCompressLen) {
						totalCompressLen = nCompressed;
						endFlag = true;
					} else {
						totalCompressLen = maxCompressLen;
						nCompressed -= totalCompressLen;
					}
					int startCount = nCount * maxCompressLen;
					byte[] cmprs = new byte[totalCompressLen];
					for (int i = 0; i < totalCompressLen; i++) {
						cmprs[i] = compressedLines[i + startCount];
					}
					iDatlen = totalCompressLen;
					bytePos = myPngWriteBytes(IDAT, bytePos);
					crc.reset();
					crc.update(IDAT);
					bytePos = myWriteBytes(cmprs, totalCompressLen, bytePos);
					crc.update(cmprs, 0, totalCompressLen);
					crcValue = crc.getValue();
					bytePos = myWriteInt4((int) crcValue, bytePos);
					targetFile.seek(iDatOffset);
					targetFile.write(myIntToByte(iDatlen));
					targetFile.seek(targetFile.length());
					targetFile.write(tempBytes);
					iDatOffset = targetFile.length();
					ClearTempBytes();
					nCount++;
				}
				scrunch.finish();
			}

			// setIDATLen(0, targetFile.length());
			setImgPixlesLineCount(0);
			// return tempBytes;
		} catch (IOException e) {
			System.err.println(e.toString());
			// return null;
		}
	}

	/**
	 * 使用IDAT断续create的方法, 将一幅大图分成(n * bitmap)分别按顺序传进来. 此函数仅仅写入png
	 * IDAT数据块的一部分，需要配合写入头尾(IHDR, IEND)一起使用才可以生成大型png图片
	 * 
	 * @param srcPixles
	 *            pixles[]
	 * @param srcWidth
	 *            當前傳入部份圖片的寬
	 * @param srcHeight
	 *            當前傳入部份圖片的高
	 * @param targetFile
	 *            create's png 圖片
	 * @param scrunch
	 *            壓縮等級
	 * @param outBytes
	 *            OutPutStream流
	 * @param compBytes
	 *            壓縮數據流
	 * @param isEndFlag
	 *            壓縮是否結束
	 * @throws FileNotFoundException
	 *             File的讀寫異常
	 */
	public void WriteMySubImageDataTest_4(int[] srcPixles, int srcWidth,
			int srcHeight, RandomAccessFile targetFile, Deflater scrunch,
			ByteArrayOutputStream outBytes, DeflaterOutputStream compBytes,
			boolean isEndFlag) throws FileNotFoundException {
		// isStartOrEndPos: -1 :start ; 1: end;
		// 初始化當前pixles集合
		ClearTempBytes();
		int rowsLeft = srcHeight; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行
		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置
		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		// 是否帶α通道
		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		int imgPixlsOneLineCount = 0;
		int imgSize = srcWidth * srcHeight;
		try {
			while (rowsLeft > 0) {
				if (imgPixlsOneLineCount >= srcHeight) {
					break;
				}
				nRows = Math.min(32767 / (srcWidth * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				// 判斷pixles數據是否溢出
				int startCount = imgPixlsOneLineCount * srcWidth;
				if (startCount >= imgSize) {
					break;
				}
				// 獲取bitmap一行的像素數組信息
				int[] pixels = new int[srcWidth * nRows];
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = srcPixles[startCount];
					startCount++;
				}
				imgPixlsOneLineCount++;
				// 创建一个数据块，扫描行增加nRows作为滤波字节
				scanLines = new byte[srcWidth * nRows * bytesPerPixel + nRows];
				if (filter == FILTER_SUB) {
					leftBytes = new byte[16];
				}
				if (filter == FILTER_UP) {
					priorRow = new byte[srcWidth * bytesPerPixel];
				}

				scanPos = 0;
				startPos = 1;
				// 濾波處理
				for (int i = 0; i < srcWidth * nRows; i++) {
					if (i % srcWidth == 0) {
						scanLines[scanPos++] = (byte) filter;
						startPos = scanPos;
					}
					scanLines[scanPos++] = (byte) ((pixels[i] >> 16) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i] >> 8) & 0xff);
					scanLines[scanPos++] = (byte) ((pixels[i]) & 0xff);
					if (encodeAlpha) {
						scanLines[scanPos++] = (byte) ((pixels[i] >> 24) & 0xff);
					}
					if ((i % srcWidth == srcWidth - 1)
							&& (filter != FILTER_NONE)) {
						if (filter == FILTER_SUB) {
							filterSub(scanLines, startPos, srcWidth);
						}
						if (filter == FILTER_UP) {
							filterUp(scanLines, startPos, srcWidth);
						}
					}
				}
				// 写入行到输出域
				compBytes.write(scanLines, 0, scanPos);

				startRow += nRows;
				rowsLeft -= nRows;
			}

			if (isEndFlag) {
				// End
				compBytes.close();
				compressedLines = outBytes.toByteArray();
				nCompressed = compressedLines.length;
				ClearTempBytes();

				// add by Taylor
				// int maxCompressLen = 1024;
				int maxCompressLen = nCompressed;
				int nCount = 0;
				boolean endFlag = false;
				while (true) {
					if (endFlag) {
						break;
					}
					int totalCompressLen = 0;
					if (nCompressed < maxCompressLen) {
						totalCompressLen = nCompressed;
						endFlag = true;
					} else {
						totalCompressLen = maxCompressLen;
						nCompressed -= totalCompressLen;
					}
					int startCount = nCount * maxCompressLen;
					byte[] cmprs = new byte[totalCompressLen];
					for (int i = 0; i < totalCompressLen; i++) {
						cmprs[i] = compressedLines[i + startCount];
					}
					// 當前寫入的pixles
					iDatlen = totalCompressLen;
					// 寫入的標記 IDAT
					bytePos = myPngWriteBytes(IDAT, bytePos);
					crc.reset();
					crc.update(IDAT);
					bytePos = myWriteBytes(cmprs, totalCompressLen, bytePos);
					crc.update(cmprs, 0, totalCompressLen);
					crcValue = crc.getValue();
					bytePos = myWriteInt4((int) crcValue, bytePos);
					targetFile.seek(iDatOffset);
					targetFile.write(myIntToByte(iDatlen));
					targetFile.seek(targetFile.length());
					targetFile.write(tempBytes);
					iDatOffset = targetFile.length();
					ClearTempBytes();
					nCount++;
				}
				scrunch.finish();
			}

			// setIDATLen(0, targetFile.length());
			setImgPixlesLineCount(0);
			// return tempBytes;
		} catch (IOException e) {
			System.err.println(e.toString());
			// return null;
		}
	}

	/**
	 * 把子像素數組信息写入png数组中 将写入一个或多个 PNG图像的 IDAT chunks（图像数据块）
	 * 无论是不是比较少，为了保持信息的完整，尽可能多的写入图像信息
	 * 
	 * @return 如果获取像素信息失败则返回false，如果无错误则返回true
	 */
	public byte[] WriteMySubImageData(int[] srcPixles,
			RandomAccessFile targetFile, int isStartOrEndPos) {
		// isStartOrEndPos: -1 :start ; 1: end;
		ClearTempBytes();
		int rowsLeft = height; // 剩余的要写的行数
		int startRow = 0; // 开始行
		int nRows; // 一次多少行

		byte[] scanLines; // 要压缩的扫描的行的信息
		int scanPos; // 扫描的行的位置
		int startPos; // 开始位置

		byte[] compressedLines; // 合成压缩行
		int nCompressed; // 压缩域大小

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = null;
		ByteArrayOutputStream outBytes = null;
		DeflaterOutputStream compBytes = null;
		scrunch = new Deflater(compressionLevel);
		outBytes = new ByteArrayOutputStream(1024);
		compBytes = new DeflaterOutputStream(outBytes, scrunch);
		imgPixlsOneLineCount = 0;
		try {
			while (rowsLeft > 0) {
				if (imgPixlsOneLineCount >= height) {
					break;
				}
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				int startCount = imgPixlsOneLineCount * width;
				if (startCount >= srcPixles.length) {
					break;
				}
				int[] pixels = new int[width * nRows];
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = srcPixles[startCount];
					startCount++;
				}
				imgPixlsOneLineCount++;
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

			if (isStartOrEndPos == 1) {
				// // End
				compBytes.close();
				compressedLines = outBytes.toByteArray();
				nCompressed = compressedLines.length;

				bytePos = myPngWriteBytes(IDAT, bytePos);
				crc.reset();
				crc.update(IDAT);
				bytePos = myWriteBytes(compressedLines, nCompressed, bytePos);
				crc.update(compressedLines, 0, nCompressed);
				crcValue = crc.getValue();
				bytePos = myWriteInt4((int) crcValue, bytePos);
				scrunch.finish();
			}

			if (tempBytes != null) {
				if (isStartOrEndPos == 1) {
					targetFile.seek(iDatOffset);
					targetFile.write(myIntToByte(iDatlen));
					targetFile.seek(targetFile.length());
				}
				targetFile.write(tempBytes);
			}
			setIDATLen(0, targetFile.length());
			setImgPixlesLineCount(0);
			return tempBytes;
		} catch (IOException e) {
			System.err.println(e.toString());
			return null;
		}
	}

	/**
	 * 设置当前bitmap所读行的信息
	 * 
	 * @param lineCount
	 *            設置當前所在行
	 */
	public void setImgPixlesLineCount(int lineCount) {
		// 设置当前bitmap所读行的信息
		imgPixlsOneLineCount = lineCount;
	}

	/**
	 * 设置IDAT的长度以及起始位置
	 * 
	 * @param len
	 * @param offset
	 */
	public void setIDATLen(int len, long offset) {
		// 设置IDAT的长度以及起始位置
		iDatlen = len;
		iDatOffset = offset;
	}

	/**
	 * 写入IEND数据域的信息
	 */
	public byte[] myWriteEnd() {
		ClearTempBytes();
		bytePos = myWriteInt4(0, bytePos);
		bytePos = myPngWriteBytes(IEND, bytePos);
		crc.reset();
		crc.update(IEND);
		crcValue = crc.getValue();
		bytePos = myWriteInt4((int) crcValue, bytePos);

		return tempBytes;
	}

	// add End

	/**
	 * 为当前image创建一个PNG字节数组并指定是否进行alpha编码
	 * 
	 * @param encodeAlpha
	 *            表示是否alpha通道编码
	 * @return 无问题存在的情况下返回一个字节数组
	 */
	public byte[] pngEncode(boolean encodeAlpha, int width, int height) {

		// {-119, 80, 78, 71, 13, 10, 26, 10}
		// 8字节的PNG文件署名域用来识别该文件是不是PNG文件
		// -119是指137的byte值
		byte[] pngIdBytes = { -119, 80, 78, 71, 13, 10, 26, 10 };
		this.width = width;
		this.height = height;
		// pngBytes字节数组设置成有足够大能够储存所有像素（包括滤波器）的信息
		// 并且额外设置200比特作为文件头数据块的信息储存空间
		pngBytes = new byte[((width + 1) * height * 3) + 200];
		// maxPos，bytePos标记pngBytes中的数据位置
		maxPos = 0;
		bytePos = writeBytes(pngIdBytes, 0);
		writeHeader(this.width, this.height);

		if (writeImageData()) {
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
	public byte[] pngEncode(int width, int height) {
		return pngEncode(encodeAlpha, width, height);
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

	private byte[] Int2Byte4(int n) {
		byte[] temp = { (byte) ((n >> 24) & 0xff), (byte) ((n >> 16) & 0xff),
				(byte) ((n >> 8) & 0xff), (byte) (n & 0xff) };
		return temp;
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

	private byte[] Int2Byte1(int b) {
		byte[] temp = { (byte) b };
		return temp;
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
	protected void writeHeader(int width, int height) {
		int startPos;

		// 文件头数据块由13字节组成，先写入长度13
		startPos = bytePos = writeInt4(13, bytePos);
		// 写入数据块类型码 IHDR
		bytePos = writeBytes(IHDR, bytePos);
		this.width = width;
		this.height = height;
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

		bytesPerPixel = (encodeAlpha) ? 4 : 3;

		Deflater scrunch = new Deflater(compressionLevel);
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream(1);
		DeflaterOutputStream compBytes = new DeflaterOutputStream(outBytes,
				scrunch);
		imgPixlsOneLineCount = 0;
		try {
			while (rowsLeft > 0) {
				if (imgPixlsOneLineCount >= height) {
					break;
				}
				nRows = Math.min(32767 / (width * (bytesPerPixel + 1)),
						rowsLeft);
				nRows = Math.max(nRows, 1);

				nRows = 1;
				int startCount = imgPixlsOneLineCount * width;
				if (startCount >= imgPixls.length) {
					break;
				}
				int[] pixels = new int[width * nRows];
				for (int i = 0; i < pixels.length; i++) {
					pixels[i] = imgPixls[startCount];
					startCount++;
				}
				imgPixlsOneLineCount++;
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
