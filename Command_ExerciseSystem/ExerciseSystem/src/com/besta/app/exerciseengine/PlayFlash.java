package com.besta.app.exerciseengine;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * <p>
 * Copyright (c) 2010 by Inventec Besta Co., Ltd. All rights reserved.
 * <P>
 * PlayFlash for call flash player play file.
 * 
 * @since Android SDK 2.3, run on Android 2.3.1 and above
 * @version Revision 1.0.1, 2011.11.03
 * @author Jack Dan
 * @see FlashPlayer
 * @Notes Only for call besta flash player
 */
public class PlayFlash {
	/** Standard activity result: operation canceled. */
	static public final String TAG = "PlayFlash"; 

	/** Standard activity result: operation canceled. */
	static public final int quitPlayEnd = 1;
	static public final int quitKeyHome = 2;
	static public final int quitKeyEsc = 3;

    /** Standard activity result: operation canceled. */
	static public final int flashRequestCode = 999;

	private static void CallFlash(Context context, Bundle bundle) {
		Intent intent = new Intent();
		intent.setComponent(new ComponentName("com.besta.flash", "com.besta.flash.FlashCourseActivity"));
		intent.setAction("android.intent.action.VIEW");

		intent.putExtras(bundle);
		if (context != null) {
			if (context instanceof Activity) {
				Log.i(TAG, "Activity call besta flash player");
				Activity activity = (Activity) context;
				//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivityForResult(intent, flashRequestCode);
			} else {
				Log.i(TAG, "Service call besta flash player");
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		} else {
			Log.e(TAG, "Invalid param context.");
		}
		// activity.startActivity(intent);
	}	
	
	/**
	 * as {@link #ShowFlash(Context, String, int, int, boolean)}
	 */
	static public void ShowFlash(Context context, String file) {
		ShowFlash(context, file, 0, 0, true);
	}
	
	/**
	 * as {@link #ShowFlash(Context, String, int, int, boolean)}
	 */
	static public void ShowFlash(Context context, String file, int nBegin, int nEnd) {
		ShowFlash(context, file, nBegin, nEnd, true);	
	}
	
    /**
     * Call when show flash file. flash file support type is swf bfh bfe.
     * swf is Adobe flash build file format.
     * bfh is BXC encode swf file format.
     * bfe is BestaDataDecode encode bfh file format.
     * {@link #ShowFlash(Context, String)} to retrieve
     * 
     * <p> 
     * <p><em>note.</em></p>
     * 
     * @param bCourse If the flash is <b><i>course</i></b> flash file. 
     * 
     * @see #BestaDataDecode
     */
	static public void ShowFlash(Context context, String file, int nBegin, int nEnd, boolean bCourse) {
		Bundle bundle = new Bundle();
		bundle.putString("file", file);
		if (nEnd > nBegin) {
			int arr[] = new int[2];
			arr[0] = nBegin;
			arr[1] = nEnd;
			bundle.putIntArray("playSegment", arr);
		}
		bundle.putBoolean("CoursePlayer", bCourse);
		CallFlash(context, bundle);
	}

	static public void ShowPackFlash(Context context, String packFile, String file) {
		ShowPackFlash(context, packFile, file, 0, 0);
	
	}
	static public void ShowPackFlash(Context context, String packFile, String file, int nBegin, int nEnd) {
		ShowPackFlash(context, packFile, file, 0, 0, true);		
	}
	static public void ShowPackFlash(Context context, String packFile, String file, int nBegin, int nEnd, boolean bCourse) {
		Bundle bundle = new Bundle();
		bundle.putString("file", file);
		bundle.putString("packName", packFile);
		if (nEnd > nBegin) {
			int arr[] = new int[2];
			arr[0] = nBegin;
			arr[1] = nEnd;
			bundle.putIntArray("playSegment", arr);
		}
		bundle.putBoolean("CoursePlayer", bCourse);
		CallFlash(context, bundle);
	}

	private static int FOURCC(byte[] b) {
		return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();	
	}
	
	private static int readIntLittleEndian(RandomAccessFile file) {
		int i = 0;
		byte b[] = new byte[4];
		try {
			file.read(b, 0, 4);
			i = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}

	private static int getOffset(RandomAccessFile file, int foucc, int offset, int nIndex, int data[]) {
		int ret = 0;
		try {
			if (offset == 0) {
				int pos = 0;
				file.seek(file.length() - 4);
				pos = readIntLittleEndian(file);
				file.seek(file.length() - pos);

			} else {
				file.seek(offset);
			}
			int buffer[] = new int[2];
			buffer[0] = readIntLittleEndian(file);
			buffer[1] = readIntLittleEndian(file);
			if ((foucc == 0 || foucc == buffer[0]) && (nIndex > 0 && nIndex < buffer[1])) {
				file.skipBytes((nIndex - 1) * 4);
				buffer[0] = readIntLittleEndian(file);
				buffer[1] = readIntLittleEndian(file);
				ret = buffer[0];
				data[0] = buffer[0];
				data[1] = buffer[1];
				// if(pLength) *pLength = buffer[1]-buffer[0];
			} else if(foucc == buffer[0] && nIndex == 0){
				return buffer[1];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	//private static byte[]  fourccFile        = {'F', 'I', 'L', 0};
	//private static byte[]  fourccStringUtf8  = {'S', 'T', 'R', 0};
	//private static byte[]  fourccStringUtf16 = {'S', 'T', 'R', 1};
	private static byte[]  fourccOffset32    = {'O', 'F', 'S', 0};
	//private static byte[]  fourccOffset64    = {'O', 'F', 'S', 1};
	private static byte[]  fourccBfs         = {'B', 'F', 'S', 0};
	private static byte[]  fourccDWORD       = {'D', 'W', 'D', 0};
	public static String[] getBfsFilenames(String fileName) {
		
		String[] names = null;
		try {
			int data[] = new int[2];
			RandomAccessFile file = new RandomAccessFile(fileName, "r");
			int m_lNameTableOffset = getOffset(file, FOURCC(fourccBfs), 0, 2, data);
			int i;
			int nCount = getOffset(file, FOURCC(fourccOffset32), m_lNameTableOffset, 0, data);
			names = new String[nCount];
			for (i = 1; i < nCount; i++) {
				int t = getOffset(file, FOURCC(fourccOffset32), m_lNameTableOffset, i, data);
				int len = data[1] - data[0];
				byte p[] = new byte[len];
				file.seek(t);
				file.read(p, 0, len);
				names[i] = new String(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return names;
	}
	
	public static int getBfsVersion(String fileName) {
		int ver = 0;
		try {
			int data[] = new int[2];
			RandomAccessFile file = new RandomAccessFile(fileName, "r");
			int m_lNameTableOffset = getOffset(file, FOURCC(fourccBfs), 0, 4, data);
			ver = getOffset(file, FOURCC(fourccDWORD), m_lNameTableOffset, 1, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ver;
	}
}
