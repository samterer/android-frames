package com.androidfuture.network;



import android.os.Parcel;
import android.os.Parcelable;



public abstract class AFData  implements Parcelable{
	public final static int TYPE_SONG = 1;
	
	public final static int TYPE_STORY = 2;
	
	public final static int TYPE_ENGLISH = 3;
	
	protected int mId;
	
	
	
	

	public void setId(int id)
	{
		mId = id;
	}

	
	
	public int getId()
	{
		return mId;
	}


	
	//parcal method
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(mId);
	
	}
	

	public AFData(Parcel parcel)
	{
		mId = parcel.readInt();
	
	}
	
	public AFData()
	{
		
	}
}