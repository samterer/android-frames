package com.androidfuture.facebook;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AlbumListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<AlbumData> albumList;
	public AlbumListAdapter(Context context,ArrayList<AlbumData> _albumList)
	{
		this.mContext = context;
		this.albumList = _albumList;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return albumList == null ? 0 : this.albumList.size();
	}

	@Override
	public Object getItem(int position) {
		return albumList == null ? null : this.albumList.get(position);
		
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AlbumListItemView itemView;
		AlbumData data = albumList.get(position);
		if(convertView == null){
			itemView = new AlbumListItemView(mContext);
		}else
		{
			itemView = (AlbumListItemView)convertView;
			//itemView.setImage(null);
		}
		itemView.update(data);
		return itemView;
		
		
	}

}
