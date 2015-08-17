package com.androidfuture.frames.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.frames.tools.FileUtils;
import com.appdao.android.feedback.FeedBack;

public class SetActivity extends Activity implements Button.OnClickListener{
    final int minLen = 5;
    private ProgressBar progressBar;
    private static final int MSG_CLEAR_CACHE = 101;
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.configuration);
	        setTitle(R.string.setting);
	        progressBar = (ProgressBar)findViewById(R.id.set_progress_bar);
	        progressBar.setVisibility(View.GONE);
	        findViewById(R.id.set_feedback).setOnClickListener(this);
	        findViewById(R.id.set_share_btn).setOnClickListener(this);
	        findViewById(R.id.set_clear_btn).setOnClickListener(this);
	        findViewById(R.id.set_theme_btn).setOnClickListener(this);
	        findViewById(R.id.set_more_btn).setOnClickListener(this);

	 	
	}
	 		public void onClick(View arg0) {
	 			switch(arg0.getId())
	 			{
	 			case R.id.set_share_btn:
	 			{
						Bitmap myImage = BitmapFactory.decodeResource(this.getResources(), R.drawable.icon_launch);
					   File sdcardpath = Environment.getExternalStorageDirectory();
				        File imgDirect = new File(FrameApp.imageCacheDir);
				        imgDirect.mkdirs();
				        File outPutFile = new File(imgDirect, "icon.png");
				        if(!outPutFile.exists())
				        {
				        	FileOutputStream fos = null;
				        	try {
				        		fos = new FileOutputStream(outPutFile);
				        		myImage.compress(CompressFormat.PNG, 100, fos);
				           
				        	} catch (FileNotFoundException e) {
				        		//Log.e("appwill", "save picture err:" + e.getMessage());
					           }
				        }
				    Uri uri = Uri.fromFile(outPutFile);
				    Intent intent = new Intent();
				    intent.setAction(Intent.ACTION_SEND);
				    intent.setType("image/png");
				    intent.putExtra(Intent.EXTRA_STREAM, uri);
				    String text = this.getResources().getString(R.string.share_msg)+"  " + Constants.AppHttpUrl + " ";
				    intent.putExtra(Intent.EXTRA_TEXT, text);
		            
				    try {
				    	String shareTitle = this.getResources().getString(R.string.share_title);
				    	 this.startActivity(Intent.createChooser(intent,shareTitle)); 
				    } catch (android.content.ActivityNotFoundException ex) {
				        Toast.makeText(this,R.string.share_fail,
				                Toast.LENGTH_SHORT).show();
				  
				    }
	 			}
	 			break;
	 			case R.id.set_feedback:
	 			{
	 				Intent intent = new Intent(this, FeedBack.class);
					startActivity(intent);

	 			}
	 			break;
	 			case R.id.set_clear_btn:
	 			{
	 				
	 				progressBar.setVisibility(View.VISIBLE);

	 				new ClearCacheThread().start();
	 			}
	 			break;
	 			case R.id.set_theme_btn:
	 			{
	 				Intent intent = new Intent();
	 				intent.setClass(this,ThemeChoose.class);
	 				startActivity(intent);
	 			}
	 			break;
	 			case R.id.set_more_btn:
	 			{
	 				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.DevelopUrl)));
	 			}
	 			break;
			}	      
		        
		  }
	 		private Handler handler = new Handler(){
	 			public void handleMessage(Message msg)
	 			{
	 				switch(msg.what)
	 				{
	 					case MSG_CLEAR_CACHE:
	 						progressBar.setVisibility(View.GONE);
	 						String text = SetActivity.this.getResources().getString(R.string.cleared_memory);
	 						Toast.makeText(SetActivity.this, text, Toast.LENGTH_SHORT).show();
	 						break;
	 				}
	 			}
	 		};
	 		private class ClearCacheThread extends Thread
	 		{
	 			public void run() { 
	 				FileUtils fUtils = new FileUtils();
	 				String rootPath =fUtils.getSDPATH() + Constants.AppName;
	 				String tmpDir = rootPath + "/tmp";
	 				String imageCacheDir = rootPath + "/image_cache";
	 				if(fUtils.isFileExist(tmpDir))
	 					fUtils.delAllFile(tmpDir);

	 				if(fUtils.isFileExist(imageCacheDir))
	 					fUtils.delAllFile(imageCacheDir);
	 				 Message msg = handler.obtainMessage();
	 				msg.what = MSG_CLEAR_CACHE;
	 				 handler.sendMessage(msg);
	 			}
	 		}
	  	 
}
