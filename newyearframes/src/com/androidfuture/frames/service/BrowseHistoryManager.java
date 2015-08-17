package com.androidfuture.frames.service;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;


import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.LocalFramesConfig;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.tools.DatabaseHelper;
import com.androidfuture.frames.ui.ListChangeListener;
import com.androidfuture.tools.AFLog;

public class BrowseHistoryManager {
	private final static String TAG = "BrowseHistoryManager";
	private int maxListLen;
	private Context context;
	private ArrayList<FrameData> historyList;
	private ListChangeListener mListener;
	
	private final String HOSITORY_TABLE = "history";
	private final String ID = "frame_id";
	private final String BROWSE_DATE = "browse_date";
	private final String CAT = "frame_cat";
	private final String IS_LOCAL = "is_local";
	private final String FRAME_URL = "frame_url";
	private final String FRAME_THUMB_URL = "frame_thumb_url";
	private final String FRAME_IS_VIP = "frame_is_vip";

	public BrowseHistoryManager(Context context){
		this.maxListLen = Constants.HistorySize;
		this.context = context;
		this.historyList = new ArrayList();
	}
	
	//load frame from db
	public void loadFav(DatabaseHelper dbHelp)
	{

		String col[]={ID,CAT,BROWSE_DATE,FRAME_URL,FRAME_THUMB_URL,IS_LOCAL,FRAME_IS_VIP};
		SQLiteDatabase db = dbHelp.getReadableDatabase();
		Cursor cur = db.query(HOSITORY_TABLE, col, null,null, null, null, null);
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
			
			this.historyList.add(frame);
			cur.moveToNext();
			
			
		}
		cur.close();
		db.close();
	}
	
	//
	public void addBrowseHistory(FrameData newItem)
	{
		
		if(find(newItem)>0)
		{
			delete(newItem);
		}
		if(getSize()>=maxListLen)
		{
			removeTheOldest();
		}
		insert(newItem);
	}
	/*
	 * get size
	 */
	private int getSize() {
		// TODO Auto-generated method stub
		//
		String col[]={ID};
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getReadableDatabase();
		Cursor cur = db.query(HOSITORY_TABLE, col, null,null, null, null, null);
		int num = cur.getCount();
		cur.close();
		db.close();
		AFLog.d("num: " + num);
		return num;

	}
	/**
	 * get 
	 * @param topicId
	 * @return
	 */
	public FrameData getBrowseHistory(String frame_Id)
	{
		return null;
	}
	
	public void setMaxBrowseHistory(int maxListLen)
	{
		this.maxListLen = maxListLen;
	}
	
	//find
	private int find(FrameData bHis)
	{
		String col[]={ID};
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getReadableDatabase();
		//Cursor cur = db.query(HOSITORY_TABLE, col, null,null, null, null, null);
		Cursor cur = db.query(HOSITORY_TABLE, col, ID+"=?",new String[]{""+(bHis.getFrameId())}, null, null, null);
		int count = cur.getCount();
		cur.close();
		return count;
	}
	
	/**
	 * get frame list
	 * @return
	 */
	public ArrayList<FrameData> getBrowseHistoryList()
	{
		return this.historyList;		
	}

	// insert
	private void insert(FrameData frame)
	{
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getWritableDatabase();
		String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String sql = null;
		if(frame.isLocal())
		{
			sql = "insert into history(frame_id,frame_cat,is_local,frame_is_vip,browse_date)values("
			+frame.getFrameId()+","
			+frame.getFrameCat()+","
			+(frame.isLocal()?1:0)+","
			+(frame.isVip()?1:0)+",'"
			+dateStr+"')";
		}else
		{
			sql = "insert into history(frame_id,frame_cat,is_local,frame_url,frame_thumb_url,frame_is_vip,browse_date)values("
				+frame.getFrameId()+","
				+frame.getFrameCat()+","
				+(frame.isLocal()?1:0)+",'"
				+frame.getFrameUrl()+"','"
				+frame.getFrameThumbUrl()+"',"
				+(frame.isVip()?1:0)+",'"
				+dateStr+"')";
		}
		
		try{
			//Log.d("insert", sql);
			db.execSQL(sql);
		}catch(SQLException e)
		{
			Log.e("E", "Failed to insert:"+sql);
		}
		db.close();
		
	}
	private void removeTheOldest()
	{
		String col[]={ID};
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getReadableDatabase();
		Cursor cur = db.query(HOSITORY_TABLE, col, null,null, null, null, BROWSE_DATE);
		cur.moveToFirst();
		if(!cur.isAfterLast())
		{
			int id = cur.getInt(cur.getColumnIndex(ID));
			deleteById(id);
			for(FrameData item:this.historyList)
				if(item.getFrameId() == id)
				{
					this.historyList.remove(item);
					break;
				}
		}
		cur.close();
		db.close();

		notifyAdapter();
	}
	public void delete(FrameData item)
	{
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getWritableDatabase();
		try{
		db.delete(HOSITORY_TABLE, ID+"=?", new String[]{""+item.getFrameId()});
		//Log.i(TAG,"delete: " +  item.getFrameId());
			
		}catch(SQLException e)
		{
			
		}
		db.close();
		for(FrameData hist:this.historyList)
			if(item.getFrameId() == hist.getFrameId())
			{
				this.historyList.remove(item);
				break;
			}
		notifyAdapter();
	}
	public void deleteAll()
	{
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getWritableDatabase();
		try{
		db.delete(HOSITORY_TABLE,null,null);
		//Log.i("delete:", "all rows");
			
		}catch(SQLException e)
		{
			
		}
		db.close();
		notifyAdapter();
		
	}
	/**
	 * delete a frame from history
	 * @param id
	 */
	public void deleteById(int id)
	{
		SQLiteDatabase db = ((FrameApp)context).getDBHelper().getWritableDatabase();
		try{
		db.delete(HOSITORY_TABLE, ID+"=?", new String[]{""+id});
		//Log.i("delete:", id);
			
		}catch(SQLException e)
		{
			
		}
		db.close();
	} 
	
	public void setListChangeListener(ListChangeListener l)
	{
		mListener = l;
	}

	private void notifyAdapter()
	{
		if(mListener != null)
		mListener.onListChange();
	}

	
}
