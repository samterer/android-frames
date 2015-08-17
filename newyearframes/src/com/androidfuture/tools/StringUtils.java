package com.androidfuture.tools;

public class StringUtils {
	private final static String TAG = "StringUtils";
	public static final int offset = 2;
	public static String getAFFileName(String url)
	{
		if(url == null)
			return "";
		String rtn = url.replaceAll("[^a-zA-Z0-9]" , "_");
		
		rtn = rtn.replaceAll("[o-z]", "x");
		//AFLog.d(TAG,rtn);
		return rtn;
	}
}
