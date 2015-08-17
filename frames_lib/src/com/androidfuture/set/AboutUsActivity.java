package com.androidfuture.set;



import com.androidfuture.frames.R;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about_us);

		TextView text = (TextView)(findViewById(R.id.about_us_text));
		text.setText(R.string.about_us_info);
		EasyTracker.getInstance(this).activityStart(this);
	}
	public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this); 
	  }

}
