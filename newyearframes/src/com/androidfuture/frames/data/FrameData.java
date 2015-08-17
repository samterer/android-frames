package com.androidfuture.frames.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.androidfuture.newyear.framesfree.FrameApp;


public class FrameData implements Parcelable {
	int mFrameResId;
	
	int mFrameThumbResId;
	
	int mFrameId;
	
	String mFilePathName;
	
	boolean mIsLocal;
	
	String mFrameThumbUrl;
	
	String mFrameUrl;
	
	boolean mIsVip;
	
	int mUseNum;
	
	int mFavNum;
	
	int mFrameCat;
	
	public FrameData()
	{
		
	}
	
	public FrameData(int frameId,int cat,int frameResId,int mFrameThumbResId)
	{
		this.mFrameId = frameId;
		
		this.mFrameCat = cat;
		
		this.mFrameResId = frameResId;
		
		this.mFrameThumbResId = mFrameThumbResId;
		
		mIsLocal = true;
	}
	
	public FrameData(int frameId,String frameThumbUrl,String frameUrl)
	{
		mIsLocal = false;
		this.mFrameThumbUrl = frameThumbUrl;
		this.mFrameUrl = frameUrl;
		this.mFilePathName = FrameApp.imageCacheDir + File.separator + "frame_" + frameId + ".png";
	}
	
	public boolean isLocal()
	{
		return mIsLocal;
	}
	
	
	public void saveToImageCache(final Bitmap bitmap)
	{

		    new Thread(){
		    	public void run(){
		    		File file = new File(mFilePathName);
		    		try {
		    		FileOutputStream os = new FileOutputStream(file);

		    		bitmap.compress( Bitmap.CompressFormat.PNG, 100, os);  
		    		  
		    		os.close();  

		    		} catch (IOException e1) {
		    			// TODO Auto-generated catch block
		    			e1.printStackTrace();
		    		}
		    	}
		    	}.start();
	}
	
	public Bitmap loadFromImageCache(float  frameWidth,float frameHeight)
	{		
			Bitmap origFrameBitmap = BitmapFactory.decodeFile(mFilePathName);
			Bitmap rtnBitmap = Bitmap.createScaledBitmap(origFrameBitmap, (int)frameWidth, (int)frameHeight, false);
		    origFrameBitmap.recycle();
		    return rtnBitmap;
	}
	
	public Bitmap loadFromImageCache()
	{
		return BitmapFactory.decodeFile(mFilePathName);
	}

	public String getFrameFileName() {
		// TODO Auto-generated method stub
		return mFilePathName;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(mFrameId);
		parcel.writeInt(mFrameResId);
		parcel.writeInt(mFrameThumbResId);
		parcel.writeInt(mIsLocal?1:0);
		parcel.writeInt(mIsVip?1:0);
		parcel.writeString(mFrameUrl);
		parcel.writeString(mFrameThumbUrl);
		parcel.writeString(mFilePathName);
		
	}
	 public static final Parcelable.Creator<FrameData> CREATOR
     = new Parcelable.Creator<FrameData>() {
         public FrameData createFromParcel(Parcel p) {
             return new FrameData(p);
         }

		@Override
		public FrameData[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	 };

	public FrameData(Parcel parcel)
	{
		mFrameId = parcel.readInt();
		mFrameResId = parcel.readInt();
		mFrameThumbResId = parcel.readInt();
		mIsLocal = parcel.readInt() == 1;
		mIsVip = parcel.readInt() == 1;
		mFrameUrl = parcel.readString();
		mFrameThumbUrl = parcel.readString();
		mFilePathName = parcel.readString();

	}
	public void setFrameId(int id)
	{
		mFrameId = id;
	}
	public void setLocal(boolean isLocal)
	{
		mIsLocal = isLocal;
	}
	public void setFrameUrl(String url)
	{
		mFrameUrl = url;
	}
	public void setVIP(boolean isVip)
	{
		mIsVip = isVip;
	}
	public void setFrameThumbUrl(String thumbUrl)
	{
		mFrameThumbUrl = thumbUrl;
	}
	
	public void setUseNum(int num)
	{
		mUseNum = num;
	}
	
	public void setFavNum(int num)
	{
		mFavNum = num;
	}
	
	public int getFrameId()
	{
		return mFrameId;
	}
	public String getFrameUrl()
	{
		return mFrameUrl;
	}
	
	public String getFrameThumbUrl()
	{
		return mFrameThumbUrl;
	}

	public boolean isVip()
	{
		return mIsVip;
	}
	
	public void setFrameCat(int cat)
	{
		mFrameCat = cat;
	}
	
	public int getFrameCat()
	{
		return mFrameCat;
	}
	
	public void setFrameRes(int res)
	{
		mFrameResId = res;
	}
	
	public void setFrameThumbRes(int thumbRes)
	{
		mFrameThumbResId = thumbRes;
	}
	
	public int getFrameRes()
	{
		return mFrameResId;
	}
	
	public int getFrameThumbRes()
	{
		return mFrameThumbResId;
	}
	
}
