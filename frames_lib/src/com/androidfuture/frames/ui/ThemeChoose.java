package com.androidfuture.frames.ui;

import java.util.ArrayList;

import com.androidfuture.frames.CustomImageAdapter;
import com.androidfuture.frames.R;
import com.androidfuture.frames.data.ThemeData;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.ImageView;

public class ThemeChoose extends Activity implements OnClickListener, OnItemClickListener{
	private ImageView imageView;
	private Gallery imageGallery;
	private ArrayList themeList;
	private ThemeData curTheme;
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.theme_choose);
        imageView = (ImageView)findViewById(R.id.theme_flipper);
        imageGallery = (Gallery)findViewById(R.id.theme_gallery);
        themeList = new ArrayList();
        //themeList.add(new ThemeData(R.drawable.wed_theme,"wed&Romantic",null));
        //themeList.add(new ThemeData(R.drawable.kids_theme,"Kids",null));
        CustomImageAdapter adapter = new CustomImageAdapter(this,themeList);
        imageGallery.setAdapter(adapter);
        imageGallery.setSpacing(40);
        imageGallery.setOnItemClickListener(this);
        findViewById(R.id.theme_ok).setOnClickListener(this);
        findViewById(R.id.theme_cancel).setOnClickListener(this);
	}


	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.theme_ok)
		{
			//apply the theme to app
			
			this.finish();
		}else if( arg0.getId() == R.id.theme_cancel)
		{
			this.finish();
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		curTheme  = (ThemeData) themeList.get(position);
		if(curTheme != null)
		{
			imageView.setImageResource(curTheme.getPreViewRes());
		}
	}
}
