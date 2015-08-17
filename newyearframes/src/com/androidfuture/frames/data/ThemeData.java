package com.androidfuture.frames.data;

public class ThemeData {
	private int preViewRes;
	
	private String resFile;
	
	private String themeName;
	public ThemeData(int _preViewRes,String _themeName, String _resFile)
	{
		this.preViewRes = _preViewRes;
		this.themeName = _themeName;
		this.resFile = _resFile;
	}
	public int getPreViewRes() {
		// TODO Auto-generated method stub
		return preViewRes;
	}
	public String getName()
	{
		return themeName;
	}
	
}
