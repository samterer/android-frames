package com.androidfuture.frames.ui;



import com.androidfuture.frames.data.NewsFragmentAdapter;


import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;


public class BaseFrameActivity extends FragmentActivity {

	
	protected NewsFragmentAdapter mAdapter;
	protected ViewPager mPager;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	
}
