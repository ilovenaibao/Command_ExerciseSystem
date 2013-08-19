package com.besta.app.answerpaper.othergraphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 系統\自定義圖形類, 注意：系統定義的是矢量圖，用戶定義的不是
 * 
 * @author BXC2011007
 * 
 */
public abstract class MyDefineGraphicSetting {
	// 保存文件擴展名定義
	public final static String SYSTEM_DEFINE_EXTERN_NAME = ".sysdf";
	public final static String USER_DEFINE_EXTERN_NAME = ".usrdf";
	// 文件check head定義
	public final static String SYSTEM_HEAD = "SYSTEM_HEAD" + 0xFFFF01;
	public final static String USER_HEAD = "USER_HEAD" + 0xFFFF02;

	public class MyDefineData {
		private String MARK_BEGIN_START = "<";
		private String MARK_BEGIN_END = ">";
		private String MARK_FINISH_START = MARK_BEGIN_START;
		private String MARK_FINISH_END = "/>";
		private String MARK_HEAD = "HEAD";
		private String MARK_NODES = "NODES";
		private String MARK_GRAPHIC = "GRAPHIC";
		private String MARK_REM = "REM";

		// head
		public String head;
		public String nodes;
		public String graphic;
		public String rem;

		public MyDefineData() {
			head = nodes = graphic = rem = null;
		}

		public MyDefineData(String buffer) {
			head = nodes = graphic = rem = null;
			head = checkMarks(buffer, MARK_HEAD, MARK_REM);
			nodes = checkMarks(buffer, MARK_NODES, MARK_REM);
			graphic = checkMarks(buffer, MARK_GRAPHIC, MARK_REM);
		}

		public String checkMarks(String buffer, String targetMark,
				String delMark) {
			String sRet = new String("");
			String[] targetMarks = new String[2];
			// mark
			targetMarks[0] = MARK_BEGIN_START + targetMark + MARK_BEGIN_END;
			targetMarks[1] = MARK_FINISH_START + targetMark + MARK_FINISH_END;

			char[] bufferBytes = new char[buffer.length()];
			buffer.getChars(0, buffer.length(), bufferBytes, 0);
			int startPos, endPos;
			startPos = buffer.indexOf(targetMarks[0], 0)
					+ targetMarks[0].length();
			if (0 <= startPos) {
				endPos = buffer.indexOf(targetMarks[1], startPos);
				if (startPos <= endPos) {
					if (0x0D == bufferBytes[startPos]) {
						startPos += 2;
					}
					if (0x0D == bufferBytes[endPos - 2]) {
						endPos -= 2;
					}
					sRet = buffer.substring(startPos, endPos);
					sRet = removeMark(sRet, delMark);
				}
			}

			return sRet;
		}

		public String removeMark(String buffer, String rmMark) {
			String sRet = new String("");
			// mark
			String[] delMarks = new String[2];
			delMarks[0] = MARK_BEGIN_START + rmMark + MARK_BEGIN_END;
			delMarks[1] = MARK_FINISH_START + rmMark + MARK_FINISH_END;

			byte[] bufferBytes = buffer.getBytes();
			int nowCount = 0;
			int totalCount = bufferBytes.length;
			boolean checkFlag = false;
			for (nowCount = 0; nowCount < totalCount; nowCount++) {
				if (MARK_BEGIN_START.getBytes()[0] == bufferBytes[nowCount]
						&& !checkFlag) {
					if (nowCount + delMarks[0].length() - 1 < totalCount) {
						if (MARK_BEGIN_END.getBytes()[0] == bufferBytes[nowCount
								+ delMarks[0].length() - 1]) {
							checkFlag = true;
							nowCount += delMarks[0].length() - 1;
						}
					} else {
						break;
					}
				}
				if (checkFlag) {
					if (MARK_FINISH_START.getBytes()[0] == bufferBytes[nowCount]) {
						if (nowCount + delMarks[1].length() - 1 < totalCount) {
							if (MARK_FINISH_END.getBytes()[1] == bufferBytes[nowCount
									+ delMarks[1].length() - 1]) {
								checkFlag = false;
								nowCount += delMarks[1].length() - 1;
								if (0x0D == bufferBytes[nowCount + 1]
										&& 0x0A == bufferBytes[nowCount + 2]) {
									nowCount += 2;
								}
								// sRet += (char) bufferBytes[nowCount];
							}
						} else {
							break;
						}
					}
					continue;
				}
				sRet += (char) bufferBytes[nowCount];
			}

			return sRet;
		}
	}

	/**
	 * 讀取指定圖形文件
	 * 
	 * @param srcPath
	 *            文件路徑（無論是否包含文件名都允許）
	 * @param srcName
	 *            文件名
	 * @return
	 */
	public static MyDefineData readMyDefineGraphicFile(String srcPath,
			String srcName) {
		MyDefineData ret = null;

		File srcFile = new File(srcPath);
		if (srcFile.exists()) {
			long nowFilePos = 0;
			long totalFileLen = srcFile.length();
			try {
				RandomAccessFile rf = new RandomAccessFile(srcFile, "r");
				int maxByteLen = 1024;
				int realByteLen = 0;
				String readFileStr = new String("");
				for (nowFilePos = 0; nowFilePos < totalFileLen; nowFilePos++) {
					byte[] buffer = null;
					if (nowFilePos + maxByteLen < totalFileLen) {
						realByteLen = maxByteLen;
					} else {
						realByteLen = (int) (totalFileLen - nowFilePos);
					}
					buffer = new byte[realByteLen];
					try {
						nowFilePos = rf.read(buffer, (int) nowFilePos,
								realByteLen);
						String tmpStr = convertByte2Char(buffer);
						readFileStr += tmpStr;
						if (-1 == nowFilePos) {
							break;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				try {
					rf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// check read file str
				ret = new MyDefineGraphicSetting() {
				}.new MyDefineData(readFileStr);

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	/**
	 * 讀取的buffer 轉化為 String
	 * 
	 * @param buffer
	 * @return
	 */
	public static String convertByte2Char(byte[] buffer) {
		String sRet = new String("");

		int totalCount = buffer.length;
		for (int nowCount = 0; nowCount < totalCount; nowCount += 2) {
			if ((byte) 0xFF == buffer[nowCount]
					&& (byte) 0xFE == buffer[nowCount + 1]) {
				continue;
			}
			char tmpChar = (char) ((char) ((buffer[nowCount + 1] > 0 ? buffer[nowCount + 1]
					: buffer[nowCount + 1] & 0x00FF) << 8) | (char) ((buffer[nowCount] > 0 ? buffer[nowCount]
					: buffer[nowCount] & 0x00FF)));
			sRet += tmpChar;
		}

		return sRet;
	}

	/**
	 * 創建圖形問題件
	 * 
	 * @param targetPath
	 *            文件路徑（無論是否包含文件名都允許）
	 * @param targetName
	 *            文件名
	 */
	public void writeMyDefineGraphicFile(String targetPath, String targetName) {

	}

	/**
	 * 加密數據buffer
	 * 
	 * @param buffer
	 *            數據
	 * @return 是否成功
	 */
	public boolean encodeMyDefineGraphicFile_encodeBuffer(String buffer) {
		boolean bRet = false;

		return bRet;
	}

	/**
	 * 加密數據文件
	 * 
	 * @param targetPath
	 *            指定文件路徑
	 * @param targetName
	 *            指定文件名
	 * @return
	 */
	public boolean encodeMyDefineGraphicFile_encodeFile(String targetPath,
			String targetName) {
		boolean bRet = false;

		return bRet;
	}

	/**
	 * 解密數據buffer
	 * 
	 * @param buffer
	 * @return
	 */
	public boolean decodeMyDefineGraphicFile_decodeBuffer(String buffer) {
		boolean bRet = false;

		return bRet;
	}

	/**
	 * 解密數據文件
	 * 
	 * @param targetPath
	 *            解密文件路徑
	 * @param targetName
	 *            文件名
	 * @return
	 */
	public boolean decodeMyDefineGraphicFile_decodeFile(String targetPath,
			String targetName) {
		boolean bRet = false;

		return bRet;
	}
}
