package com.androidfuture.frames.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.frames.data.FrameData;

public class ItemView extends RelativeLayout {
	private final static String TAG = "ItemView";

	private CacheImageView picView;

	private FrameData curFrame;
	private  Context context;
	private int tab;
	
	
	//TextView dingNum;
	public ItemView(final Context context) {
		super(context);
		this.context = context;

		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflate.inflate(R.layout.griditem,null);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(160,160);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
		
		
		picView =(CacheImageView) findViewById(R.id.item_image);
		
		
		}


public void update(FrameData picNews,final int tab)
{
	this.tab = tab;
	this.curFrame = picNews;
	/*
	if(curTopic.getPicture() != null)
	{
		picView.setImageBitmap(curTopic.getPicture().getBitMap());
	}
	*/

	   /*
	   if(curFrame.getPubDate() != null)
	   {
		   try {
		    Date date2 = df2.parse(curFrame.getPubDate());
		    dateStr =  DateFormat.getDateInstance().format(date2);
		   } catch (ParseException e) {
		    
		   }
	   }
	   */
	   if(curFrame.isLocal())
	   {
		  // Log.d(TAG,"local");
		   picView.setImageResource(curFrame.getFrameThumbRes());
	   }else
	   {
		   picView.setImageResource(R.drawable.fail);
		   picView.setImage(curFrame.getFrameThumbUrl());
	   }
	
}
		
}
