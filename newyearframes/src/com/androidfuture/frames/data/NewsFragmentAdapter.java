package com.androidfuture.frames.data;

import java.util.HashMap;

import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.frames.ui.FramesAppFragment;
import com.androidfuture.frames.ui.NewsFragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class NewsFragmentAdapter extends FragmentPagerAdapter {
	//protected static final String[] CONTENT = new String[] { "Favirate", "Hot", "Enterment", "Sports", };
	private HashMap<Integer,Fragment> fragmentMap;
	//private int mCount = CONTENT.length;
	private Activity mContext;
	public NewsFragmentAdapter(FragmentManager fm,Activity context) {
		super(fm);
		this.mContext = context;
		fragmentMap = new HashMap();
	}

	@Override
	public Fragment getItem(int position) {
		FrameApp app = ((FrameApp)this.mContext.getApplication());
		HashMap map = app.getTabList();
		Fragment rtn = fragmentMap.get(app.getTab(position));
		if(rtn == null )
		{
			if(app.getTab(position) == Constants.TAB_MORE)
			{
				rtn = FramesAppFragment.newInstance(this.mContext);
			}else
			{
				rtn = NewsFragment.newInstance(app.getTab(position),this.mContext);
			}
			fragmentMap.put(app.getTab(position),rtn);
		}
		return rtn;
	}

	@Override
	public int getCount() {
		return ((FrameApp)this.mContext.getApplication()).getTabList().size();
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