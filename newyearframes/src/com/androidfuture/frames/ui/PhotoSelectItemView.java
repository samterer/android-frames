package com.androidfuture.frames.ui;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.facebook.PhotoData;
import com.androidfuture.frames.data.ThemeData;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class PhotoSelectItemView extends RelativeLayout {
	private Context context;
	
	private CacheImageView image;
	public PhotoSelectItemView(Context context) {
		super(context);
		this.context = context;

		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		image = new CacheImageView(context);
		image.setScaleType(ScaleType.CENTER_CROP);
		
		float scale = context .getResources().getDisplayMetrics().density;
		
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams((int)(80*scale + 0.5f),(int)(80*scale + 0.5f));
		param.addRule(RelativeLayout.CENTER_IN_PARENT);

		layout.addView(image,param);
		
		addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

		
		
		}

public void update(PhotoData photo)
{
	image.setImage(photo.getThumbUrl());
		
}
}
