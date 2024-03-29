package com.androidfuture.chrismas.framesfree;

import android.content.SharedPreferences;
import com.androidfuture.frames.AFApp;
import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.AFConf;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.data.LocalFrameData;
import com.androidfuture.frames.tools.FileUtils;
import com.androidfuture.tools.ConfigManager;
import com.androidfuture.tools.DeviceUtils;

import java.io.File;
import java.util.ArrayList;

public class FrameApp extends AFApp {
	public  final static String AppName = "Christmas Frames";
	public  final static String AppNameNoSpace = "Christmas_Frames_for_Free";
	public final static String AppId = "com.androidfuture.chrismas.framesfree";
	public static final String AppHttpUrl = "https://market.android.com/details?id=com.androidfuture.chrismas.framesfree";
	public final static String MY_AD_ID = "a151e802edab6a1";
	public static final String AppUrl = "market://details?id=com.androidfuture.chrismas.framesfree";
	public static final String FacebookId = "491988760831727";//"414784071897829";
	private ArrayList<LocalFrameData> localFrameDatas;
	private ArrayList<LocalFrameData> multiFrameDatas;
	private String tmpDir;
	private String imageCacheDir;
    private AFConf conf;
	public void onCreate() {
		super.onCreate();
		AFAppWrapper.Init(this);

		init();
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
	public void init()
	{
		if(DeviceUtils.isSDCardOk())
		{
			// TODO Auto-generated method stub
			FileUtils fUtils = new FileUtils();
			String rootPath = fUtils.getSDPATH() + "." + AppNameNoSpace;
			if (!FileUtils.isFileExist(rootPath))
				fUtils.createSDDir(rootPath);

			tmpDir = rootPath + "/tmp";
			if (!FileUtils.isFileExist(tmpDir))
				fUtils.createSDDir(tmpDir);
			else
				fUtils.delAllFile(tmpDir);

			imageCacheDir = rootPath + "/cache";
			if (!FileUtils.isFileExist(imageCacheDir))
				fUtils.createSDDir(imageCacheDir);

			ConfigManager.GetInstance().Init(
					this,
					AppId,
					AppNameNoSpace,
					rootPath,
					Constants.URLRoot,
					Constants.ConfigRoot,
					Constants.Market,
					Constants.SETTING_INFOS);

			String wallpaperDir = FileUtils.getSDPATH() + File.separator + AppNameNoSpace;

			if (!FileUtils.isFileExist(rootPath))
				fUtils.createSDDir(rootPath);

			if (!FileUtils.isFileExist(tmpDir))
				fUtils.createSDDir(tmpDir);
			else
				fUtils.delAllFile(tmpDir);

			if (!FileUtils.isFileExist(imageCacheDir))
				fUtils.createSDDir(imageCacheDir);


			if (!FileUtils.isFileExist(wallpaperDir))
				fUtils.createSDDir(wallpaperDir);


			conf = new AFConf();
			conf.TmpDir = rootPath + File.separator + "tmp";
			conf.CacheDir = rootPath + File.separator + "cache";
			conf.Cat = 0;
			conf.AppId = AppId;
			conf.FacebookId = FacebookId;
			conf.AppName = R.string.app_name;
			conf.AppIcon = R.drawable.icon_launch;
			conf.AppHttpUrl = AppHttpUrl;
			conf.AppUrl = AppUrl;
			conf.MyAdId = MY_AD_ID;
			conf.AppNameNoSpace = AppNameNoSpace;
			//conf.RootUrl = URLRoot;
			SharedPreferences settings = this.getSharedPreferences(
					ConfigManager.GetInstance().getSetting(), 0);
			conf.colNum = settings.getInt(Constants.SET_COLUMN_MODE, 2);
			conf.ContentDir = wallpaperDir;

			AFAppWrapper.Init(this);
		}
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

	@Override
	public AFConf GetConf() {
		// TODO Auto-generated method stub
		return conf;
	}

}
