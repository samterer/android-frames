package com.androidfuture.frames.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidfuture.cacheimage.CacheImageView;

import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.frames.data.MoreFramesAppData;
import com.androidfuture.network.AFData;

public class MoreFramesAppItemView extends AFCellView{

	public MoreFramesAppItemView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.more_frames_item,null);
		addView(layout,new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
	}

	@Override
	public void update(AFData data) {
		// TODO Auto-generated method stub
		MoreFramesAppData appData = (MoreFramesAppData)data;
		CacheImageView icon = (CacheImageView) findViewById(R.id.app_icon);
		icon.setImageResource(R.drawable.fail);
		icon.setImage(appData.getAppIconUrl());
		((TextView)findViewById(R.id.app_name)).setText(appData.getAppName());
	}

}
