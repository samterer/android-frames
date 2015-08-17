package com.androidfuture.newyear.framesfree;

import java.util.HashMap;

import com.androidfuture.cacheimage.ImageDownloadManager;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.service.BrowseHistoryManager;
import com.androidfuture.frames.service.FavoriteManager;
import com.androidfuture.frames.service.FrameManager;
import com.androidfuture.frames.tools.DatabaseHelper;
import com.androidfuture.frames.tools.FileUtils;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.statistic.StatisticTool;








import android.app.Application;
import android.content.res.Resources;
import android.util.Log;


public class FrameApp extends Application {
	private static final String TAG = "FrameApp";
	private DatabaseHelper dbHelper;
	public static String rootPath;
	public static String tmpDir;
	public static String imageCacheDir;
	private HashMap tabMap;
	private FrameManager frameManager;
	private FavoriteManager favManager;
	private BrowseHistoryManager histManager;
	int[] tabOrder;
	private ImageDownloadManager imgDownloadManager;
	
	private HashMap localFrameMap;
	public void  onCreate  ()
	{
		super.onCreate();
		dbHelper = new DatabaseHelper(this,Constants.dbName);
		frameManager = new FrameManager(this);
		favManager = new FavoriteManager(this);
		favManager.loadFav(this.dbHelper);
		imgDownloadManager = new ImageDownloadManager(this);
		histManager = new BrowseHistoryManager(this);
		//Log.d(TAG,"histManager: " + (histManager==null));
		histManager.loadFav(this.dbHelper);
		//init file system
		FileUtils fUtils = new FileUtils();
		rootPath =fUtils.getSDPATH() + Constants.AppName;
		//Log.d(TAG,rootPath);
		tmpDir = rootPath + "/tmp";
		imageCacheDir = rootPath + "/image_cache";
		if(!fUtils.isFileExist(rootPath));
			fUtils.createSDDir(rootPath);

		if(!fUtils.isFileExist(tmpDir))
			fUtils.createSDDir(tmpDir);
		else
			fUtils.delAllFile(tmpDir);

		
		if(!fUtils.isFileExist(imageCacheDir))
			fUtils.createSDDir(imageCacheDir);
		
		tabMap = new HashMap();
		Resources res = this.getResources();
		tabMap.put(Constants.TAB_MORE, res.getString(R.string.tab_more));
		tabMap.put(Constants.TAB_HOT, res.getString(R.string.tab_hot));
		tabMap.put(Constants.TAB_NEW, res.getString(R.string.tab_new));
		tabMap.put(Constants.TAB_FAVORITE, res.getString(R.string.tab_favorite));
		//tabMap.put(Constants.TAB_HISTORY, res.getString(R.string.tab_history));

		tabOrder = new int[] {  
				Constants.TAB_MORE,
				Constants.TAB_HOT, 
				Constants.TAB_NEW ,
				Constants.TAB_FAVORITE,};
	
		localFrameMap = new HashMap();
		for(FrameData item: LocalFramesConfig.LocalFrames)
		{
			localFrameMap.put(item.getFrameId(), item);
		}
		StatisticTool.postToServer(this, Constants.ACT_LOGIN);
	}
	
	public DatabaseHelper getDBHelper()
	{
		return this.dbHelper;
	}
	public HashMap getTabList()
	{
		return this.tabMap;
	}
	
	public FrameManager getFrameManager()
	{
		return this.frameManager;
	}
	
	public int getTab(int position)
	{
		return this.tabOrder[position];
	}
	
	public ImageDownloadManager getImgDownloadManager()
	{
		return this.imgDownloadManager;
	}

	public FavoriteManager getFavManager() {
		// TODO Auto-generated method stub
		return favManager;
	}
	public BrowseHistoryManager getHistManager()
	{
		return histManager;
	}
}
