package com.androidfuture.frames.service;


import java.util.ArrayList;

import java.util.HashMap;


import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.LocalFramesConfig;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.tools.DatabaseHelper;
import com.androidfuture.frames.ui.ListChangeListener;


import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.BaseAdapter;


public class FavoriteManager {
	private final static String TAG = "FavoriteManager";
	
	private int maxListLen;
	private Application app;
	
	private ListChangeListener mListener;
	private final String FAVIRATE_TABLE = "favorite";
	private final String ID = "frame_id";
	private final String CAT = "frame_cat";
	private final String IS_LOCAL = "is_local";
	private final String FRAME_URL = "frame_url";
	private final String FRAME_THUMB_URL = "frame_thumb_url";
	private final String FRAME_IS_VIP = "frame_is_vip";
	private final ArrayList<FrameData> favFrameList;
	
	public FavoriteManager(Application context){
		this.maxListLen = 100;
		this.app = context;//app
		this.favFrameList = new ArrayList();
	}
	//load frame from db
	public void loadFav(DatabaseHelper dbHelp)
	{
		String col[]={ID,CAT,FRAME_URL,FRAME_THUMB_URL,IS_LOCAL,FRAME_IS_VIP};
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		Cursor cur = db.query(FAVIRATE_TABLE, col, null,null, null, null, null);
		ArrayList<FrameData> list = new ArrayList<FrameData>();
		cur.moveToFirst();
		
		HashMap<Integer,FrameData> map  = new HashMap<Integer,FrameData>();
		for(FrameData frame: LocalFramesConfig.LocalFrames)
		{
			map.put(frame.getFrameId(), frame);
		}
		while(!cur.isAfterLast())
		{
			FrameData frame = new FrameData();
			boolean isLocal = cur.getInt(cur.getColumnIndex(IS_LOCAL)) == 1;
			frame.setFrameId(cur.getInt(cur.getColumnIndex(ID)));
			frame.setLocal(isLocal);
			if(isLocal)
			{
				//local frames
				FrameData local = map.get(frame.getFrameId());
				if(local != null)
				{
					frame.setFrameRes(local.getFrameRes());
					frame.setFrameThumbRes(local.getFrameThumbRes());
				}
				
			}else
			{
				frame.setFrameUrl(cur.getString(cur.getColumnIndex(FRAME_URL)));
				frame.setFrameThumbUrl(cur.getString(cur.getColumnIndex(FRAME_THUMB_URL)));
				
			}

			frame.setVIP(cur.getInt(cur.getColumnIndex(FRAME_IS_VIP))==1);
			
			this.favFrameList.add(frame);
			cur.moveToNext();
			
			
		}
		cur.close();
		db.close();
	}
	
	public boolean isFav(int topicId)
	{
		for(FrameData item:this.favFrameList)
			if(item.getFrameId() == topicId)
				return true;
	    return false;
		
	}

	
	//add fav
	public void addFav(FrameData newItem)
	{
		
		if(isFav(newItem.getFrameId()))
		{
			return;
		}
		if(getSize()>=maxListLen)
		{
			
		}
		this.favFrameList.add(newItem);
		//newItem.setLocalIconUri(genIconUri(newItem));
		//saveIcon(newItem);
		insert(newItem);
		notifyAdapter();
	}
	
	private int getSize() {
		// TODO Auto-generated method stub

		String col[]={ID};
		SQLiteDatabase db = ((FrameApp)app).getDBHelper().getReadableDatabase();
		Cursor cur = db.query(FAVIRATE_TABLE, col, null,null, null, null, null);
		int num = cur.getCount();
		cur.close();
		db.close();
		return num;
	}
	
	public void setMaxFavItem(int maxListLen)
	{
		this.maxListLen = maxListLen;
	}
	
	//find
	private int find(FrameData newsItem)
	{
		//id
		String col[]={ID};
		SQLiteDatabase db = ((FrameApp)app).getDBHelper().getReadableDatabase();
		//Cursor cur = db.query(HOSITORY_TABLE, col, null,null, null, null, null);
		int count = 0;
		try
		{
			Cursor cur = db.query(FAVIRATE_TABLE, col, ID+"=?",new String[]{"" + newsItem.getFrameId()}, null, null, null);
			count = cur.getCount();
			cur.close();
		}catch(Exception e)
		{
			Log.e("Error", "Fail to query: "+e.getMessage());
		}
		
		return count;
	}
	
	public ArrayList<FrameData> getFavFrameList()
	{
		return this.favFrameList;
	}

	private void insert(FrameData frame)
	{
		SQLiteDatabase db = ((FrameApp)app).getDBHelper().getWritableDatabase();
		//String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(item.getBrowseDate());
		String sql = null;
		if(frame.isLocal())
		{
			sql = "insert into favorite(frame_id,frame_cat,is_local,frame_is_vip)values("
			+frame.getFrameId()+","
			+frame.getFrameCat()+","
			+(frame.isLocal()?1:0)+","
			+(frame.isVip()?1:0)+")";
		}else
		{
			sql = "insert into favorite(frame_id,frame_cat,is_local,frame_url,frame_thumb_url,frame_is_vip)values("
				+frame.getFrameId()+","
				+frame.getFrameCat()+","
				+(frame.isLocal()?1:0)+",'"
				+frame.getFrameUrl()+"','"
				+frame.getFrameThumbUrl()+"',"
				+(frame.isVip()?1:0)+")";
		}
		
		try{
			//Log.i("insert", sql);
			db.execSQL(sql);
		}catch(SQLException e)
		{
			Log.e("E", "Failed to insert:"+sql);
		}
		db.close();
	
	}
	private void removeTheOldest()
	{
		
	}
	
	/**
	 * delete frameData from database
	 * @param delItem
	 */
	public void delete(FrameData delItem)
	{
		
		
		SQLiteDatabase db = ((FrameApp)app).getDBHelper().getWritableDatabase();
		try{
		db.delete(FAVIRATE_TABLE, ID+"=?", new String[]{""+delItem.getFrameId()});
		//Log.i("delete:", "frame id: " + delItem.getFrameId());
			
		}catch(SQLException e)
		{
			
		}
		db.close();

		for(FrameData item:this.favFrameList)
			if(item.getFrameId() == delItem.getFrameId())
			{
				this.favFrameList.remove(item);
				break;
			}
		notifyAdapter();
	}
	
	public void setListChangeListener(ListChangeListener l)
	{
		mListener = l;
	}

	private void notifyAdapter()
	{
		mListener.onListChange();

	}
}
