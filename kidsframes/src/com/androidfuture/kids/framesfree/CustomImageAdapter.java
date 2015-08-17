package com.androidfuture.kids.framesfree;

import java.util.ArrayList;

import com.androidfuture.frames.data.ThemeData;
import com.androidfuture.frames.ui.GalleryItemView;



import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;


public class CustomImageAdapter extends BaseAdapter {
	private ArrayList<ThemeData> themeList;
	
	private Context mContext;
	
	public CustomImageAdapter(Context c,ArrayList list)
	{
		mContext = c;
		themeList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return themeList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return themeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GalleryItemView view = null;
		if(convertView==null)
		{
			view = new GalleryItemView(mContext);
			 Gallery.LayoutParams params = new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT,Gallery.LayoutParams.WRAP_CONTENT);
		     view.setLayoutParams(params);
		}else
		{
			view = (GalleryItemView) convertView;
		}
		view.update(themeList.get(position));
       
        //imageView.setBackgroundResource(mGalleryItemBackground);
        return view;
	}
	
	
	


}
