package com.androidfuture.frames.data;

import java.util.HashMap;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.FragmentManager;

import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.viewpagerindicator.TitleProvider;

public class FrameTitleFragmentAdapter extends FrameFragmentAdapter implements TitleProvider {
	public Activity mContext;
	public FrameTitleFragmentAdapter(FragmentManager fm,Activity context) {
		super(fm,context);
		this.mContext = context;
	}

	@Override
	public String getTitle(int position) {
		String title = ((Application)AFAppWrapper.GetInstance().GetApp())
				.getResources().getString(Constants.TabsName[position]);
		return title ;
		//return NewsFragmentAdapter.CONTENT[position % CONTENT.length];
	}
}