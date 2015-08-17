package com.androidfuture.cacheimage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.tools.AFLog;


import android.graphics.Bitmap;
import android.os.AsyncTask;


public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap>  {
	    //private final WeakReference<ImageView> imageViewReference;  
		
	    private final ImageDownloadManager manager;
	    private String url;
	  
	    public ImageDownloadTask(ImageDownloadManager manager) { 
	        //imageViewReference = new WeakReference<ImageView>(imageView); 
	        this.manager = manager;
	    } 
  	protected void onPreExecute (){
  				
  			}
	protected Bitmap doInBackground(String[] params) {
		// TODO Auto-generated method stub
		url = params[0];
		AFLog.d("start load: " + url);
		Bitmap bitmap =  Picture.downloadBitmap(url);
	
		if(bitmap == null)
			return null;
		String fileName = url.replaceAll("[^a-zA-Z0-9]" , "_");
		File file = new File(FrameApp.imageCacheDir,fileName);
		try {
			file.createNewFile();
			FileOutputStream os = new FileOutputStream(file);
	  
			bitmap.compress( Bitmap.CompressFormat.PNG, 100, os);  
			  
			os.close();  
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return bitmap;
	    
	}
	
	protected void onPostExecute(Bitmap bitmap) {   
	       	//try again
			
			if (isCancelled()) { 
	            bitmap = null;
	            manager.onCanceledDownload(url);
	        }else
	        {
	        	if(bitmap == null)
	        	{
	        		manager.onFailDownload(url);
	        	}else
	        	{
	        		//Log.d(TAG,"there: " + bitmap);
	        		manager.onFinishDownload(url,bitmap);
	        	}
	        }
	    } 

}
