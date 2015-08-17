package com.androidfuture.photo.picker;



import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFAlbumData;
import com.androidfuture.data.AFCellView;
import com.androidfuture.frames.R;

import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import android.widget.TextView;

public class AlbumListItemView extends AFCellView {




	public AlbumListItemView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.album_grid_cell,null);
		int width = (int)(DeviceUtils.GetScreenWidth((Activity)context));
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/3,width/3);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
	}

		

	@Override
	public void update(AFData data) {
		AFAlbumData album = (AFAlbumData)data;	
		TextView albumName = (TextView) findViewById(R.id.album_title);
		albumName.setText(album.getTitle());
		
		CacheImageView cover = (CacheImageView)findViewById(R.id.album_cover);
		if (album.getCoverPhoto()!=null)
		{
			if(album.getCoverPhoto().getPath()!=null)
				cover.setImagePath(album.getCoverPhoto().getPath(), 1<<16);
			else
				cover.setImage(album.getCoverPhoto().getThumbUrl());
			cover.setScaleType(ScaleType.CENTER_CROP);
		}
	}
}
