package com.androidfuture.frames.ui;



import android.content.Context;
import android.graphics.Color;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.androidfuture.frames.data.ThemeData;

public class GalleryItemView extends RelativeLayout {
	private final static String TAG = "ItemView";

	private ImageView picView;
	private TextView text;
	private ThemeData curTheme;
	private  Context context;
	
	
	//TextView dingNum;
	public GalleryItemView(final Context context) {
		super(context);
		this.context = context;

		
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		picView = new ImageView(context);
		text = new TextView(context);
		
		
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(90,120);
		layout.addView(picView,param);
		
		addView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

		
		
		}


public void update(ThemeData theme)
{
	this.curTheme = theme;
	picView.setImageResource(theme.getPreViewRes());
	text.setText(theme.getName());
		
}
	
private void clearBackColor()
{
	
	for(int index  = 0;index < ((ListView)this.getParent()).getChildCount();index ++)
	{
		if(((ListView) this.getParent()).getChildAt(index) != this)
			((ListView)this.getParent()).getChildAt(index).setBackgroundColor(Color.TRANSPARENT);
	}
}
	

	
	
	
}
