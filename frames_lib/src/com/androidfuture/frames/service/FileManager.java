package com.androidfuture.frames.service;

public class FileManager {
	static FileManager instance;
	public static FileManager GetInstance()
	{
		if(instance==null)
		{
			instance = new FileManager();
			
		}
		
		return instance;
	}
	
	public String GetImageCacheDir()
	{
		return "";
	}
}
