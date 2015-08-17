package com.androidfuture.frames.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.androidfuture.network.AFData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.androidfuture.tools.StringUtils;

public class FrameData extends AFData {
	int mFrameId;

	String mFrameThumbUrl;

	String mFrameUrl;

	int mFrameWidth;
	
	int mFrameHeight;
	
	
	boolean mIsVip;

	int mFrameNum;

	FrameCell[] mFrameCells;

	int mUseNum;

	int mFavNum;

	int mFrameCat;
	
	boolean mIsLocal;
	
	protected int mThumbRes;

	protected int mRes;
	
	private Bitmap frameBitmap;

	public FrameData() {

	}

	public FrameData(int frameId, int cat, String url, String thumbUrl,
			int frameNum, FrameCell[] frameCells) {
		this.mFrameId = frameId;

		this.mFrameCat = cat;

		this.mFrameUrl = url;

		this.mFrameThumbUrl = thumbUrl;

		this.mFrameNum = frameNum;

		this.mFrameCells = frameCells;
		
		this.mIsLocal = false;

	}

	public void saveToImageCache(final Bitmap bitmap) {

		new Thread() {
			public void run() {
				File file = new File(StringUtils.getAFFilePath(mFrameUrl));
				try {
					FileOutputStream os = new FileOutputStream(file);

					bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);

					os.close();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}.start();
	}


	public Bitmap loadFromImageCache(float frameWidth, float frameHeight) {
		Bitmap origFrameBitmap = BitmapFactory.decodeFile(StringUtils
				.getAFFilePath(mFrameUrl));
		Bitmap rtnBitmap = Bitmap.createScaledBitmap(origFrameBitmap,
				(int) frameWidth, (int) frameHeight, false);
		if(origFrameBitmap != rtnBitmap && !origFrameBitmap.isRecycled())
		{
			origFrameBitmap.recycle();
			origFrameBitmap = null;
			System.gc();
			
		}
		return rtnBitmap;
	}

	public Bitmap loadFromImageCache() {
		return BitmapFactory.decodeFile(StringUtils.getAFFilePath(mFrameUrl));
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static final Parcelable.Creator<FrameData> CREATOR = new Parcelable.Creator<FrameData>() {
		public FrameData createFromParcel(Parcel p) {
			return new FrameData(p);
		}

		@Override
		public FrameData[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}
	};

	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(mFrameId);
		parcel.writeInt(mIsVip ? 1 : 0);
		parcel.writeInt(mIsLocal ? 1 : 0);
		parcel.writeInt(mFrameWidth);
		parcel.writeInt(mFrameHeight);
		parcel.writeInt(mFrameCat);
		parcel.writeInt(mFrameNum);
		parcel.writeInt(mRes);
		parcel.writeInt(mThumbRes);
		parcel.writeString(mFrameUrl);
		parcel.writeString(mFrameThumbUrl);
		parcel.writeParcelableArray(mFrameCells, 0);

	}

	public FrameData(Parcel parcel) {
		mFrameId = parcel.readInt();
		mIsVip = parcel.readInt() == 1;
		mIsLocal = parcel.readInt() == 1;
		mFrameWidth = parcel.readInt();
		mFrameHeight = parcel.readInt();
		mFrameCat = parcel.readInt();
		mFrameNum = parcel.readInt();
		mRes = parcel.readInt();
		mThumbRes = parcel.readInt();
		mFrameUrl = parcel.readString();
		mFrameThumbUrl = parcel.readString();
		Parcelable [] parcels = (Parcelable[]) parcel.readParcelableArray(FrameCell.class
				.getClassLoader());
	if(parcels != null)
		{
			mFrameCells = new FrameCell[parcels.length];
			for (int i = 0; i < parcels.length; i++)
				mFrameCells[i] = (FrameCell)parcels[i];
		}else
		{
			mFrameCells = new FrameCell[1];
			mFrameCells[0] = new FrameCell(0.0f, 0.0f,1.0f,1.0f, 1);
		}
	}

	public void setFrameId(int id) {
		mFrameId = id;
	}

	public void setFrameUrl(String url) {
		mFrameUrl = url;
	}

	public void setVIP(boolean isVip) {
		mIsVip = isVip;
	}

	public void setFrameThumbUrl(String thumbUrl) {
		mFrameThumbUrl = thumbUrl;
	}

	public void setUseNum(int num) {
		mUseNum = num;
	}

	public void setFavNum(int num) {
		mFavNum = num;
	}

	public int getFrameId() {
		return mFrameId;
	}

	public String getFrameUrl() {
		return mFrameUrl;
	}

	public String getFrameThumbUrl() {
		return mFrameThumbUrl;
	}

	public boolean isVip() {
		return mIsVip;
	}

	public boolean isLocal(){
		return mIsLocal;
	}



	public int getThumbRes() {
		return mThumbRes;
	}

	public void setThumbRes(int mThumbRes) {
		this.mThumbRes = mThumbRes;
	}

	public int getRes() {
		return mRes;
	}

	public void setRes(int mRes) {
		this.mRes = mRes;
	}
	public void setFrameCat(int cat) {
		mFrameCat = cat;
	}

	public int getFrameCat() {
		return mFrameCat;
	}

	public int getmFrameNum() {
		return mFrameNum;
	}

	public void setmFrameNum(int mFrameNum) {
		this.mFrameNum = mFrameNum;
	}

	public FrameCell[] getmFrameCells() {
		return mFrameCells;
	}

	public void setmFrameCells(FrameCell[] mFrameCells) {
		this.mFrameCells = mFrameCells;
	}

	public void setFrameWidth(int width)
	{
		this.mFrameWidth = width;
	}
	
	public void setFrameHeight(int height)
	{
		this.mFrameHeight = height;
	}
	public int getFrameWidth()
	{
		return this.mFrameWidth;
	}
	
	public int getFrameHeight()
	{
		return this.mFrameHeight;
	}
	
	public Bitmap getFrameBitmap()
	{
		return frameBitmap;
	}
	
	public void setFrameBitmap(Bitmap bitmap)
	{
		if(frameBitmap !=null && frameBitmap != bitmap && !frameBitmap.isRecycled())
		{
			frameBitmap.recycle();
		}
		frameBitmap = bitmap;
	}
}
