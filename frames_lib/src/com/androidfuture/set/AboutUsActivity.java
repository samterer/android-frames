package com.androidfuture.set;



import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.androidfuture.frames.R;

public class AboutUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about_us);

		TextView text = (TextView)(findViewById(R.id.about_us_text));
		text.setText(R.string.about_us_info);
	}
	public void onStop() {
	    super.onStop();
	  }

}
