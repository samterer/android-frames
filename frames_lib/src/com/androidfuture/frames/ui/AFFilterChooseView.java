package com.androidfuture.frames.ui;

import java.util.ArrayList;
import java.util.List;


import com.androidfuture.frames.data.FilterInfo;
import com.androidfuture.frames.service.FilterManager;
import com.androidfuture.tools.AFLog;
import android.view.View.OnClickListener;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;


public class AFFilterChooseView extends LinearLayout implements OnClickListener {
	 private ImageFilterAdapter adapter ;
	 
	private OnItemClickListener l;
	public AFFilterChooseView(Context context) {
		super(context);
		adapter = new ImageFilterAdapter(context);

		AFLog.d("On Layout");
		this.removeAllViews();
		int space = 8;
		for (int i = 0;i < adapter.getCount(); i++)
		{
			View item = adapter.getView(i, null, this);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
										LinearLayout.LayoutParams.WRAP_CONTENT);
			param.setMargins(space, space, space, space);   
			AFLog.d("Add " + item);
			item.setOnClickListener(this);
			this.addView(item,param);
		}
		
		//this.setBackgroundColor(0xffff0000);
		// TODO Auto-generated constructor stub
	}
	
	public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener)
	{
		l = onItemClickListener;
	}
	
	public void onClick(View v) 
	{
		if(l != null)
		{
			l.onItemClick(null, v, v.getId(), 0);
		}
	}
	
	public Object getItem(int position)
	{
		return adapter.getItem(position);
	}
	
	
public class ImageFilterAdapter extends BaseAdapter {
		

		private Context mContext;
		private List<FilterInfo> filterArray = new ArrayList<FilterInfo>();

		public ImageFilterAdapter(Context c) {
			mContext = c;
			filterArray.clear();
			filterArray.addAll(FilterManager.getInstance().getFilterList());
		}
		public int getCount() {
			
			return filterArray.size();
		}

		public Object getItem(int position) {
			return position < filterArray.size() ? filterArray.get(position).filter
					: null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			AFLog.d("Position:" + position);
	    	FilterThumbItemView itemView = (FilterThumbItemView) convertView;
	        //WWLog.d("get view: " + position);
	    	if(itemView == null )
	        {
	    		itemView = new FilterThumbItemView(this.mContext);
	    		itemView.setId(position);
	    	}
	    	((FilterThumbItemView)itemView).update(filterArray.get(position));
	    	return itemView;
		}
	};
}
