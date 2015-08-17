package com.androidfuture.frames.ui;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.frames.data.NewsFragmentAdapter;
import com.androidfuture.frames.data.NewsTitleFragmentAdapter;
import com.viewpagerindicator.TitlePageIndicator;

public class FrameChooseView extends RelativeLayout  {
	private FragmentActivity context;

	protected NewsFragmentAdapter mAdapter;
	protected ViewPager mPager;
	public FrameChooseView(FragmentActivity context) {
		super(context);
		this.context = context;
		LayoutInflater inflate = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(R.layout.frame_choose,null);
		addView(layout,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,RelativeLayout.LayoutParams.FILL_PARENT));
		
		mAdapter = new NewsTitleFragmentAdapter(this.context.getSupportFragmentManager(),this.context);
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		findViewById(R.id.frame_close_bar).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Log.d(TAG,"close choose view");
				((ProcessActivity)FrameChooseView.this.context).closeFrameChoose();
				
			}
		});
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setCurrentItem(1);

    	
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

}
