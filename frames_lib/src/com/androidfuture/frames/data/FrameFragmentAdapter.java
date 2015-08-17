package com.androidfuture.frames.data;

import java.util.HashMap;

import com.androidfuture.frames.Constants;
import com.androidfuture.frames.ui.FramesAppFragment;
import com.androidfuture.frames.ui.FrameFragment;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class FrameFragmentAdapter extends FragmentPagerAdapter {
	//protected static final String[] CONTENT = new String[] { "Favirate", "Hot", "Enterment", "Sports", };
	private HashMap<Integer,Fragment> fragmentMap;
	//private int mCount = CONTENT.length;
	private Activity mContext;
	public FrameFragmentAdapter(FragmentManager fm,Activity context) {
		super(fm);
		this.mContext = context;
		fragmentMap = new HashMap();
	}

	@Override
	public Fragment getItem(int position) {
		//FrameApp app = ((FrameApp)this.mContext.getApplication());
		HashMap map = new HashMap();//app.getTabList();
		
		Fragment rtn = fragmentMap.get(Constants.Tabs[position]);
		if(rtn == null )
		{
			if(Constants.Tabs[position] == Constants.TAB_MORE)
			{
				rtn = FramesAppFragment.newInstance();
			}else
			{
				rtn = FrameFragment.newInstance(Constants.Tabs[position]);
			}
			fragmentMap.put(Constants.Tabs[position],rtn);
		}
		return rtn;
	}

	@Override
	public int getCount() {
		if(this.mContext == null || this.mContext.getApplication() == null)
			return 0;
		else
			return Constants.Tabs.length;
	}
	/*
	public void setCount(int count) {
		if (count > 0 && count <= 10) {
			mCount = count;
			notifyDataSetChanged();
		}
	}
	*/
}