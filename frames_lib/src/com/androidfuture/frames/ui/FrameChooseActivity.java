package com.androidfuture.frames.ui;

import com.androidfuture.cacheimage.ImageDownloadManager;
import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.data.FrameFragmentAdapter;
import com.androidfuture.frames.data.FrameTitleFragmentAdapter;
//import com.google.analytics.tracking.android.EasyTracker;
import com.viewpagerindicator.TitlePageIndicator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;



public class FrameChooseActivity extends FragmentActivity{
protected FrameFragmentAdapter mAdapter;
	
	protected ViewPager mPager;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.frame_choose);
		
		mAdapter = new FrameTitleFragmentAdapter(getSupportFragmentManager(),this);
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(mPager);
		indicator.setCurrentItem(2);

    	
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
		
		//EasyTracker.getInstance(this).activityStart(this);
	}
	
	
	@Override
	protected void onStop() {
		//EasyTracker.getInstance(this).activityStop(this);
		super.onStop();
	}

	/*
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										finish();
									}
								}).setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								});
				builder.setMessage(R.string.exit_msg);
				builder.create().show();
		}
		return super.onKeyDown(keyCode, event);
	}
	*/

	public void back(FrameData frame) {
		Intent intent = this.getIntent();
		if (intent.getBooleanExtra("switch", false))
		{
			  Intent newIntent =new Intent();  
              intent.putExtra("data",frame);  
                
              setResult(RESULT_OK,intent);  
              finish();
              ImageDownloadManager.GetInstance().clearTasks();
		}else
		{
			Intent newIntent = new Intent(this, ProcessActivity.class);
			newIntent.putExtra("data", frame);
			startActivity(newIntent);
			finish();
			ImageDownloadManager.GetInstance().clearTasks();
		}
	}
	
	
}
