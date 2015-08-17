package com.androidfuture.frames.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFCellView;

import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.data.LocalFrameData;
import com.androidfuture.frames.data.MoreFramesAppData;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;

public class MoreFramesAppItemView extends AFCellView{

	private final static String TAG = "ItemView";

	public MoreFramesAppItemView(final Context context) {
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

	CacheImageView picView = (CacheImageView) findViewById(R.id.item_image);
	MoreFramesAppData appData = (MoreFramesAppData)data;
	picView.setImage(appData.getPreviewUrl());
	
}

}
