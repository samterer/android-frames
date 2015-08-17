package com.androidfuture.set;
import com.androidfuture.frames.R;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class VersionInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.version_release);
		TextView text = (TextView)(findViewById(R.id.version_release_text));
		text.setText(R.string.release_version);
		EasyTracker.getInstance(this).activityStart(this);
		
	}
	public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this); 
	  }

}
