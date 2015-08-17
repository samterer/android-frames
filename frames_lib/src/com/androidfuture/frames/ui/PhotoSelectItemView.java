package com.androidfuture.frames.ui;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFCellView;
import com.androidfuture.data.AFPhotoData;

import com.androidfuture.frames.R;
import com.androidfuture.frames.data.ThemeData;

import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class PhotoSelectItemView extends AFCellView {
	
	
	public PhotoSelectItemView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.photo_grid_view,null);
		int width = (int)(DeviceUtils.GetScreenWidth((Activity)context));
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/3,width/3);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
	}

	public void update(AFData data) {
		AFPhotoData imageData = (AFPhotoData)data;
		CacheImageView image = (CacheImageView)(findViewById(R.id.item_image));
		image.setImage(imageData.getThumbUrl());
		image.setScaleType(ScaleType.CENTER_CROP);
	}
}
