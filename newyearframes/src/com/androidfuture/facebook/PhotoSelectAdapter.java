package com.androidfuture.facebook;

import java.util.ArrayList;

import com.androidfuture.frames.ui.PhotoSelectItemView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class PhotoSelectAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<PhotoData> dataList;
	private int tab;

	public PhotoSelectAdapter(Context context, ArrayList<PhotoData> list) {
		mContext = context;
		this.dataList = list;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return dataList.get(arg0);
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoSelectItemView itemView;
		PhotoData data;
		data = dataList.get(position);
		if (convertView == null) {
			itemView = new PhotoSelectItemView(mContext);
		} else {
			itemView = (PhotoSelectItemView) convertView;
			// itemView.setImage(null);
		}

		itemView.update(data);
		return itemView;
	}

}
