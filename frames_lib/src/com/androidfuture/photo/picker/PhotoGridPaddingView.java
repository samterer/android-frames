package com.androidfuture.photo.picker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.androidfuture.data.AFCellView;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.DeviceUtils;

public class PhotoGridPaddingView extends AFCellView {
	AFPhotoData appData;
	public PhotoGridPaddingView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.photo_grid_with_padding,null);
		int width = (int)(DeviceUtils.GetScreenWidth((Activity)context));
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(width/3,width/3);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
	}

	@Override
	public void update(final AFData data, OnCellInnerViewClickListener listener) {
	
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		((ImageView)findViewById(R.id.photo_grid_cell_image)).setImageBitmap(bitmap);
	}
	public AFPhotoData getData()
	{
		return appData;
	}
}
