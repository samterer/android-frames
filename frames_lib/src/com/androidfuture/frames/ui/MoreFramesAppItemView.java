package com.androidfuture.frames.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFCellView;
import com.androidfuture.frames.R;
import com.androidfuture.frames.data.MoreFramesAppData;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;

public class MoreFramesAppItemView extends AFCellView{

	private final static String TAG = "ItemView";

	public MoreFramesAppItemView(final Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflate.inflate(R.layout.griditem,null);
		int width = (int)(DeviceUtils.GetScreenWidth(context));
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/2,width/2);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
		
		}




@Override
public void update(final AFData data, OnCellInnerViewClickListener listener) {

	CacheImageView picView = (CacheImageView) findViewById(R.id.item_image);
	MoreFramesAppData appData = (MoreFramesAppData)data;
	picView.setImage(appData.getPreviewUrl());
	
}

}
