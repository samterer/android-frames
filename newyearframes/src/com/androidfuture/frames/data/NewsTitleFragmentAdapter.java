package com.androidfuture.frames.data;

import java.util.HashMap;

import android.app.Activity;
import android.support.v4.app.FragmentManager;

import com.androidfuture.newyear.framesfree.FrameApp;
import com.viewpagerindicator.TitleProvider;

public class NewsTitleFragmentAdapter extends NewsFragmentAdapter implements TitleProvider {
	public Activity mContext;
	public NewsTitleFragmentAdapter(FragmentManager fm,Activity context) {
		super(fm,context);
		this.mContext = context;
	}

	@Override
	public String getTitle(int position) {
		FrameApp app =  ((FrameApp)this.mContext.getApplication());
		HashMap map  =app.getTabList();
		
		return (String) map.get(app.getTab(position));
		//return NewsFragmentAdapter.CONTENT[position % CONTENT.length];
	}
}