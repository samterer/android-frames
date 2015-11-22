package com.androidfuture.set;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.androidfuture.frames.R;

public class VersionInfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.version_release);
		TextView text = (TextView)(findViewById(R.id.version_release_text));
		text.setText(R.string.release_version);

		
	}
	public void onStop() {
	    super.onStop();
	  }

}
