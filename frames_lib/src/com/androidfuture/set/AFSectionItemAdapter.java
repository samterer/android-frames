package com.androidfuture.set;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.androidfuture.cacheimage.CacheImageView;

import com.androidfuture.network.AFData;
import com.androidfuture.tools.AFLog;



import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class AFSectionItemAdapter extends BaseAdapter {
	ArrayList<AFSetItem> datas;
	private Context mContext;
	public AFSectionItemAdapter(Context context, ArrayList<AFSetItem> data) {
		datas = data;
		mContext = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return datas == null ? 0 : datas.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return datas == null ? null : datas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return datas == null ? null : datas.get(arg0).getId();
	}

	public int getPosition(AFSetItem item) {
		return datas.indexOf(item);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		AFSetItem item = datas.get(position);
		View view = null;
		switch (item.getType())
		{
		case SIMPLE_ITEM:
			view = new AFSimpleSetItemView(mContext, item);
			break;
		case TOGGLE_ITEM:
			view = new AFToggleSetItemView(mContext, item);
			break;
		default:
		}
		return view;
	}

}
