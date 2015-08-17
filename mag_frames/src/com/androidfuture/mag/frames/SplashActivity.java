package com.androidfuture.mag.frames;


import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.ui.ProcessActivity;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.DeviceUtils;
import com.androidfuture.tools.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

public class SplashActivity extends Activity {
	private static int MSG_FINISH = 200;
	private static int MSG_PROGRESS = 201;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_layout);
		SharedPreferences settings = getSharedPreferences(
				Constants.SETTING_INFOS, 0);
		AFLog.d("have inited: " + settings.getBoolean("inited", false));
		if (!settings.getBoolean("inited", false)) {
			findViewById(R.id.progress_init).setVisibility(View.VISIBLE);

		} else {
			findViewById(R.id.progress_init).setVisibility(View.GONE);
		}
		if(!DeviceUtils.isSDCardOk())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
			.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							SplashActivity.this.finish();
						}
					});
			builder.setMessage(R.string.no_sdcard);
			builder.setTitle(R.string.no_sdcard_title);
			builder.create().show();
		}
		else
		{
			this.handler.sendEmptyMessageDelayed(MSG_FINISH, 2000);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_FINISH) {
				Intent intent = new Intent(SplashActivity.this,
						ProcessActivity.class);
				SharedPreferences settings = getSharedPreferences(
						Constants.SETTING_INFOS, 0);
				if(!settings.getBoolean("inited", false))
				{
					Editor edit = settings.edit();
					edit.putBoolean("inited", true);
					edit.commit();
				}
				startActivity(intent);
				
				finish();
			} else {
				ProgressBar p = (ProgressBar) findViewById(R.id.progress_init);
				p.setProgress(msg.getData().getInt("progess"));
			}
		}
	};

	public class InitThread extends Thread {
		public void run() {
			

			//initLocalData();

			handler.sendEmptyMessageDelayed(MSG_FINISH, 2000);
		}
	}

/*
	private void initLocalData() {

		AssetManager am = null;
		am = getAssets();
		int i = 0;
		for (LocalFrameData frame : AFAppWrapper.GetInstance().GetApp().GetLocalFrames()) {

			String filePath = StringUtils.getAFFilePath(frame.getFrameUrl());
			if (!FileUtils.isFileExist(filePath)) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						this.getResources(), frame.getRes());
				FileUtils.saveBitmap2SD(bitmap, filePath);
				bitmap.recycle();
			}

			filePath = StringUtils.getAFFilePath(frame.getFrameThumbUrl());
			if (!FileUtils.isFileExist(filePath)) {
				Bitmap bitmap = BitmapFactory.decodeResource(
						this.getResources(), frame.getThumbRes());
				FileUtils.saveBitmap2SD(bitmap, filePath);
				bitmap.recycle();
			}
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putInt("progess", 100 * i
					/ 10);
			msg.setData(b);
			msg.what = MSG_PROGRESS;
			handler.sendMessage(msg);
			i++;
		}
	}
	*/
}
