package com.androidfuture.cartoonframes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.androidfuture.frames.AFApp;
import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.data.LocalFrameData;
import com.androidfuture.frames.tools.FileUtils;

import com.androidfuture.statistic.StatisticTool;
import com.androidfuture.tools.DeviceUtils;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

public class FrameApp extends Application implements AFApp {
	public  final static String AppName = "Cartoon Frames";
	public  final static String AppNameNoSpace = "Cartoon_Frames";
	public final static String AppId = "com.androidfuture.cartoonframes";
	public static final String AppHttpUrl = "https://market.android.com/details?id=com.androidfuture.cartoonframes";
	public final static String MY_AD_ID = "a1515b74c3b0d06";
	public static final String AppUrl = "market://details?id=com.androidfuture.cartoonframes";
	public static final String FacebookId = "367495813262713";
	private ArrayList<LocalFrameData> localFrameDatas;
	private ArrayList<LocalFrameData> multiFrameDatas;
	private String tmpDir;
	private String imageCacheDir;
 	public void onCreate() {
		super.onCreate();
		AFAppWrapper.Init(this);
		StatisticTool.postToServer(this, Constants.ACT_LOGIN);
		
		// TODO Auto-generated method stub
		FileUtils fUtils = new FileUtils();
		String rootPath = fUtils.getSDPATH() + "." + AppName;
		if (!FileUtils.isFileExist(rootPath))
			fUtils.createSDDir(rootPath);

		tmpDir = rootPath + "/tmp";
		if (!FileUtils.isFileExist(tmpDir))
			fUtils.createSDDir(tmpDir);
		else
			fUtils.delAllFile(tmpDir);

		imageCacheDir = rootPath + "/image_cache";
		if (!FileUtils.isFileExist(imageCacheDir))
			fUtils.createSDDir(imageCacheDir);
	}

	@Override
	public ArrayList<LocalFrameData> GetLocalFrames() {
		if(localFrameDatas == null)
		{
			localFrameDatas = new ArrayList<LocalFrameData>();
			for (LocalFrameData item: LocalFramesConfig.LocalFrames)
				localFrameDatas.add(item);
		}
		return localFrameDatas;
	}

	@Override
	public ArrayList<LocalFrameData> GetMultFrames() {
		if(multiFrameDatas == null)
		{
			multiFrameDatas = new ArrayList<LocalFrameData>();
			for (LocalFrameData item: LocalFramesConfig.MultiLocalFrames)
				multiFrameDatas.add(item);
		}
		return multiFrameDatas;
	}

	@Override
	public String GetTmpDir() {

		return tmpDir;
	}

	@Override
	public String GetCacheDir() {

		// Log.d(TAG,rootPath);
		return imageCacheDir;
	}

	@Override
	public String GetCat() {
		// TODO Auto-generated method stub
		return "5";
	}

	@Override
	public String GetAppId() {
		// TODO Auto-generated method stub
		return AppId;
	}

	@Override
	public String GetFacebookId() {
		// TODO Auto-generated method stub
		return FacebookId;
	}

	@Override
	public String GetAppName() {
		// TODO Auto-generated method stub
		return AppName;
	}
	
	public String GetAppNameNoSpace()
	{
		return AppNameNoSpace;
	}

	@Override
	public String GetAppHttpUrl() {
		// TODO Auto-generated method stub
		return AppHttpUrl;
	}

	@Override
	public String GetAppUrl() {
		// TODO Auto-generated method stub
		return AppUrl;
	}

	@Override
	public String GetMyAdId() {
		// TODO Auto-generated method stub
		return MY_AD_ID;
	}

	@Override
	public ArrayList<LocalFrameData> GetLocalPins() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<LocalFrameData> GetLocalGrids() {
		// TODO Auto-generated method stub
		return null;
	}

}
