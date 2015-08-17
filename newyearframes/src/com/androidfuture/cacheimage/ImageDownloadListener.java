package com.androidfuture.cacheimage;

import android.graphics.Bitmap;

public interface ImageDownloadListener {
	public void onCanceledDownload(String url);

	public void onFailDownload(String url) ;
	
	public void onFinishDownload(String url,Bitmap bitmap);
	
}
