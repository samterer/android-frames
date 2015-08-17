package com.androidfuture.frames.ui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFCellView;

import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.data.LocalFrameData;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.DeviceUtils;


public class ItemView extends AFCellView {
	private final static String TAG = "ItemView";



	private FrameData curFrame;

	public ItemView(final Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflate.inflate(R.layout.griditem,null);
		int width = (int)(DeviceUtils.GetScreenWidth((Activity)context));
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/2,width/2);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
		
		}




@Override
public void update(AFData data) {
	this.curFrame = (FrameData) data;
	CacheImageView picView = (CacheImageView) findViewById(R.id.item_image);
	if(data instanceof LocalFrameData)
	{
		LocalFrameData tmpFrame = (LocalFrameData) data;
		picView.setImageResource(tmpFrame.getThumbRes());
	}else
	{
		AFLog.d("start load view");
		picView.setImage(curFrame.getFrameThumbUrl());
	}
	
}
		
}
