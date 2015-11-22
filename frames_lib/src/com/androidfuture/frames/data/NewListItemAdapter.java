package com.androidfuture.frames.data;



import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.androidfuture.frames.ui.ItemView;

import java.util.ArrayList;


public class NewListItemAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<FrameData> framesList;
	private int tab;
	
	public NewListItemAdapter(Context context,ArrayList<FrameData> newsList,int curSel)
	{
	mContext = context;
	this.framesList = newsList;
	this.tab = curSel;
	}
	public int getCount() {
		// TODO Auto-generated method stub
		return framesList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return framesList.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ItemView itemView;
		FrameData picNews;
		picNews = framesList.get(position);
		if(convertView == null){
			itemView = new ItemView(mContext);
		}else
		{
			itemView = (ItemView)convertView;
			//itemView.setImage(null);
		}
		
		
		itemView.update(picNews, null);
		return itemView;
	}

}
