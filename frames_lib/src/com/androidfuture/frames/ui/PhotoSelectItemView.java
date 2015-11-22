package com.androidfuture.frames.ui;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFCellView;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;



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

	public void update(AFData data, OnCellInnerViewClickListener listener) {
		AFPhotoData imageData = (AFPhotoData)data;
		CacheImageView image = (CacheImageView)(findViewById(R.id.item_image));
		image.setImage(imageData.getThumbUrl());
		image.setScaleType(ScaleType.CENTER_CROP);
	}
}
