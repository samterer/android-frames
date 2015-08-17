package com.androidfuture.photo.picker;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.cacheimage.CacheThumbImageView;
import com.androidfuture.data.AFCellView;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;

import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;
import com.androidfuture.tools.WWScreenUtils;

public class AFPhotoChoseGridView extends AFCellView {
	public AFPhotoChoseGridView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.photo_chose_grid_cell,null);
		float scale = WWScreenUtils.getInstance(context).getScale();
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams((int)(100 * scale),(int)(100 * scale));
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
	}

	@Override
	public void update(final AFData data) {
		// TODO Auto-generated method stub
		AFPhotoData appData = (AFPhotoData) data;
		CacheThumbImageView view = (CacheThumbImageView) findViewById(R.id.photo_grid_cell_image);
		findViewById(R.id.photo_grid_cell_delete).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				PhotoManager.GetInstance().delPhoto((AFPhotoData)data);
				
			}
			
		});
		view.setBackgroundResource(R.drawable.image_default);
		view.setScaleType(ScaleType.CENTER_CROP);
		if (appData.getThumbUrl() != null)
		{
			view.setImage(appData.getThumbUrl());
			
		}else
		{
			view.setImagePath(appData.getPath(), 1 << 17, false);
		}
		
	}

	
	
	public void recycle()
	{
		CacheImageView view = (CacheImageView) findViewById(R.id.photo_grid_cell_image);
		view.recycle();
	}


}
