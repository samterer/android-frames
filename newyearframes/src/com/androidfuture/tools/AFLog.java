package com.androidfuture.tools;

import android.util.Log;

public class AFLog {
	
	private static final boolean DEBUG = true;
	
	public static void v(String str) {
		if(DEBUG)
			Log.v(getFileName(), "Line " + getLineNumber() + ": " + str);
	}
	
	public static void d(String str) {
		if(DEBUG)
			Log.d(getFileName(), "Line " + getLineNumber() + ": " + str);
	}
	
	public static void i(String str) {
		if(DEBUG)
			Log.i(getFileName(), "Line " + getLineNumber() + ": " + str);
	}
	
	public static void e(String str) {
		if(DEBUG)
			Log.e(getFileName(), "Line " + getLineNumber() + ": " + str);
	}
	/*
	public static void wtf(String str) {
		if(DEBUG)
			Log.wtf(getFileName(), "Line " + getLineNumber() + ": " + str);
	}
	*/

	private static int getLineNumber() {
		return Thread.currentThread().getStackTrace()[4].getLineNumber();
	}

	private static String getFileName() {
		return Thread.currentThread().getStackTrace()[4].getFileName();
	}
}
