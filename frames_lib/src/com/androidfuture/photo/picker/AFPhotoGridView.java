package com.androidfuture.photo.picker;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.cacheimage.CacheThumbImageView;
import com.androidfuture.data.AFCellView;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;

import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;
import com.androidfuture.tools.MediaUtils;

public class AFPhotoGridView extends AFCellView {
	public AFPhotoGridView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.photo_grid_cell,null);
		int width = (int)(DeviceUtils.GetScreenWidth((Activity)context));
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/3,width/3);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
	}

	@Override
	public void update(AFData data) {
		// TODO Auto-generated method stub
		AFPhotoData appData = (AFPhotoData) data;
		CacheThumbImageView view = (CacheThumbImageView) findViewById(R.id.photo_grid_cell_image);
		view.setBackgroundResource(R.drawable.image_default);
		view.setScaleType(ScaleType.CENTER_CROP);
		if (appData.getThumbUrl() != null)
		{
			view.setImage(appData.getThumbUrl());
		}else 
		{
			view.setImagePath(appData.getPath(), 1<<17);
		}
		if (PhotoManager.GetInstance().isSelected(appData)) {
			findViewById(R.id.photo_grid_checked).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.photo_grid_checked).setVisibility(View.GONE);
		}

	}

	public void updateCheck(boolean val) {
		if (val) {
			findViewById(R.id.photo_grid_checked).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.photo_grid_checked).setVisibility(View.GONE);
		}

	}
	
	public void recycle()
	{
		CacheImageView view = (CacheImageView) findViewById(R.id.photo_grid_cell_image);
		view.recycle();
	}

}
