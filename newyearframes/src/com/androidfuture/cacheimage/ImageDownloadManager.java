package com.androidfuture.cacheimage;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.tools.AFLog;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class ImageDownloadManager{
	Application mApp;
	HashMap<String,SoftReference<Bitmap>> memoryCache; 
	HashMap<String,CacheImageView> viewMap;
	//private boolean hasViewCache;
	ImageDownloadListener listener;
	
	public ImageDownloadManager(Application app) {
		this.mApp = app;
		memoryCache = new HashMap<String,SoftReference<Bitmap>>();
		viewMap = new HashMap<String,CacheImageView>();
		
	}
	public void loadImage(ImageDownloadListener lsten, String picUrl)
	{
		this.listener = lsten;
		
		if(memoryCache.get(picUrl) != null && memoryCache.get(picUrl).get() != null)
		{	
			listener.onFinishDownload(picUrl, memoryCache.get(picUrl).get());
		}else
		{
			//check memory
			String fileName = picUrl.replaceAll("[^a-zA-Z0-9]" , "_");
			File file = new File(FrameApp.imageCacheDir,fileName);
			if(file.exists())
			{
			     Bitmap bitmap = BitmapFactory.decodeFile(FrameApp.imageCacheDir + File.separator + fileName);
			     listener.onFinishDownload(picUrl, bitmap);
			     memoryCache.put(picUrl, new SoftReference(bitmap));
			}else
			{
				AFLog.d("download: " + picUrl);
				new ImageDownloadTask(this).execute(picUrl);
			}
		}
	}
	
	public void loadImage(CacheImageView view, String picUrl) {
		
		if(memoryCache.get(picUrl) != null && memoryCache.get(picUrl).get() != null)
		{	
			view.setImageBitmap(memoryCache.get(picUrl).get());
			
		}else
		{
			//check memory
			String fileName = picUrl.replaceAll("[^a-zA-Z0-9]" , "_");
			File file = new File(FrameApp.imageCacheDir,fileName);
			if(file.exists())
			{
			     Bitmap bitmap = BitmapFactory.decodeFile(FrameApp.imageCacheDir + File.separator + fileName);
			     view.setImageBitmap(bitmap);
			     memoryCache.put(picUrl, new SoftReference(bitmap));
			}else if(viewMap.get(picUrl)== null)
			{
				//Log.d(TAG,"start download:" + picUrl);
				viewMap.put(picUrl, view);
				new ImageDownloadTask(this).execute(picUrl);
			}else
			{
				//do nothing
			}
		}
	}
	
	
	public void cancelLoad(String url)
	{
		if(viewMap.get(url) != null)
		{
			//cancel task
		}
	}

	public void onCanceledDownload(String url) {
		viewMap.remove(url);
	}

	public void onFailDownload(String url) {
		viewMap.remove(url);
	}

	public void onFinishDownload(String url,Bitmap bitmap) {
		
		if(viewMap.get(url) != null)
		{
			if( viewMap.get(url).getImageUrl().equals(url))
			{
				viewMap.get(url).setImageBitmap(bitmap);
				viewMap.remove(url);
			}
		}else
		{
			if(listener != null)
				listener.onFinishDownload(url, bitmap);
		}
		memoryCache.put(url, new SoftReference(bitmap));
	}

}
