package com.androidfuture.frames.ui;

import com.androidfuture.cacheimage.CacheImageView;

import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FilterInfo;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.WWScreenUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FilterThumbItemView extends RelativeLayout {
	private ImageView picView;
	private TextView textView;
	
	public FilterThumbItemView(Context context) {
		super(context);
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.filter_item,null);
		float scale = WWScreenUtils.getInstance(context).getScale();
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams((int)(80 * scale),(int)(80 * scale));
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout,param);
		
	}
	
	public void update(FilterInfo filterInfo)
	{
		((ImageView)findViewById(R.id.filter_icon)).setImageResource(filterInfo.filterID);
		((TextView)findViewById(R.id.filter_name)).setText(filterInfo.filterName);
	}
	
	

}
