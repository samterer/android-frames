package com.androidfuture.cacheimage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.androidfuture.newyear.framesfree.FrameApp;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import android.widget.ImageView;



public class CacheImageViewWithProcess extends ImageView{
	public static final int MSG_FILE_FINISH = 101;
	
	public static final int MSG_FILE_PROGRESS = 102;
	
	public static final int MSG_FILE_CANCEL = 103;
	
	public static final int MSG_FILE_FAIL = 104;

	public static final String KEY_PROGRESS = "key_progress"; 
	

	private int fileSize;
	
	private int downLoadFileSize;
	
	private boolean mCancel;
	
	private String mImageUrl;
	
	public CacheImageViewWithProcess(Context context) {
		super(context);
		mCancel = false;
		// TODO Auto-generated constructor stub
	}
	public 	 CacheImageViewWithProcess(Context context, AttributeSet attrs)
	{
		super(context,attrs);
	
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch(msg.what)
			{
			case MSG_FILE_FINISH:
				break;
			case MSG_FILE_PROGRESS:
				break;
			case MSG_FILE_CANCEL:
				break;
			}
		}
	};
	
	public void setBigImage(String imageUrl)
	{
		mImageUrl = imageUrl;
		new RepeatThread().start();
	}
	
	public class RepeatThread extends Thread {
		public void run() {
			try {
				downFile(mImageUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(MSG_FILE_FAIL);
			}
		}
	}
	public void downFile(String url) throws IOException{
		String fileName = url.replaceAll("[^a-zA-Z0-9]" , "_");
		File file = new File(FrameApp.imageCacheDir,fileName);
		if(file.exists()){
			
			handler.sendEmptyMessage(MSG_FILE_FINISH);
			return;
		}

		URL myURL = new URL(url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
	    int fileSize = conn.getContentLength();
	    
	    //Log.d(TAG,"filesize: "+ fileSize);
	    if (this.fileSize <= 0) throw new RuntimeException(" ");
	    if (is == null) throw new RuntimeException("stream is null");
	    FileOutputStream fos = new FileOutputStream(file);
	    //save
	    byte buf[] = new byte[1024];
	    downLoadFileSize = 0;

	    while(!mCancel)
	      {
	    	//repeat read
	        int numread = is.read(buf);
	        if (numread == -1)
	        {
	          break;
	        }
	        fos.write(buf, 0, numread);
	        downLoadFileSize += numread;
	        Message msg = handler.obtainMessage();
	        msg.what = MSG_FILE_PROGRESS;
	        Bundle data = new Bundle();
	        data.putInt(KEY_PROGRESS, downLoadFileSize * 100 / fileSize);
	        msg.setData(data);
	        handler.sendMessage(msg);
	      } ;
	    if(!mCancel)
	    {
	    	handler.sendEmptyMessage(MSG_FILE_FINISH);//notif finish
	    }else
	    {
	    	handler.sendEmptyMessage(MSG_FILE_CANCEL);
	    	file.delete();
	    }
	      
	    try
	      {
	        is.close();
	      } catch (Exception ex)
	      {
	        Log.e("tag", "error: " + ex.getMessage(), ex);
	      }

	}
}
