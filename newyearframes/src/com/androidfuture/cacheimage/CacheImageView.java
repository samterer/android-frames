package com.androidfuture.cacheimage;

import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.R;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CacheImageView extends ImageView {
	Context mContext;
	
	private String mUrl;
	public CacheImageView(Context context) {
		super(context);
		mContext = context;
		

	}
	public 	 CacheImageView(Context context, AttributeSet attrs)
	{
		super(context,attrs);
		mContext = context;
	}
	
	public void setImage(String url)
	{
		ImageDownloadManager manager;
		manager = ((FrameApp)((Activity)mContext).getApplication()).getImgDownloadManager();
		if(mUrl != null)
			manager.cancelLoad(mUrl);
		mUrl = url;
		this.setImageResource(R.drawable.fail);
		manager.loadImage(this,url);
	}
	
	public String getImageUrl()
	{
		return this.mUrl;
	}
	
}
