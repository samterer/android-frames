package com.androidfuture.frames.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.androidfuture.cacheimage.Picture;
import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.tools.FileUtils;
import com.androidfuture.frames.tools.ImageStore;
import com.androidfuture.statistic.StatisticTool;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.StringUtils;

import com.appdao.android.feedback.FeedBack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;



public class PreviewActivity extends Activity implements OnClickListener {
	private static final String TAG = "PreviewActivity";
	private static int MSG_FINISH_GEN = 101;
	private FrameData curFrameData;
	private Bitmap resultBitmap;
	private float curRotateWidth;
	private float curRotateHeight;
	private float rate;
	private float angle;
	private float left;
	private float right;
	private float top;
	private float bottom;
	private float frameWidth;
	private float frameHeight;
	private Uri photoUri;
	
	private ImageView imageView;
	
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.preview_layout);
        findViewById(R.id.preview_cancel).setOnClickListener(this);
        findViewById(R.id.preview_share).setOnClickListener(this);
        AFLog.i("start Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
		Intent intent = getIntent();
		left = intent.getFloatExtra("left", 0) * 2;
		right = intent.getFloatExtra("right", 0) * 2;
		top = intent.getFloatExtra("top", 0) * 2;
		bottom = intent.getFloatExtra("bottom", 0) * 2;
		rate = intent.getFloatExtra("rate", 1);
		angle = intent.getFloatExtra("angle", 0);
		curRotateWidth = intent.getFloatExtra("width",320) * 2;
		curRotateHeight = intent.getFloatExtra("height",480) * 2;
		frameWidth = intent.getFloatExtra("frame_width", 320) * 2;
		frameHeight = intent.getFloatExtra("frame_height", 480) * 2;
		
		curFrameData = intent.getBundleExtra("data").getParcelable("frame");
		photoUri = intent.getBundleExtra("data").getParcelable("photo");
	
		AFLog.d("data:" + left + ": " + right + ": " + top + ": " + bottom + ": " + rate + ": " + angle);
		imageView = (ImageView)findViewById(R.id.preview_image);
		
		new GenBitmapThread().start();

	 }
	 
	//generate bitmap
		private void genBitmap()
		{
	    	float cropLeft = left>0?0:-left;
	    	float cropTop  = top>0?0:-top;
	    	float cropWidth = right > 0? curRotateWidth - cropLeft:curRotateWidth - cropLeft + right;
	    	float cropHeight = bottom > 0 ? curRotateHeight - cropTop : curRotateHeight - cropTop + bottom;
	    	
	    	// Log.d(TAG,"in genBItmap--cropLeft: " + cropLeft + " cropTop: " +cropTop + " cropWidth: " + cropWidth + " cropHeight: " + cropHeight);

	    	
	    	//Log.d(TAG,"in genBitmap: rate: " + rate);
	    	 Matrix localMatrix = new Matrix();
	 	    localMatrix.postRotate(angle);
			Bitmap frameBitmap = null;
			
			

			
			
			AFLog.i("1 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
		   Bitmap curPhoto = FileUtils.readBitmapFromSD(this.getContentResolver(), photoUri);
		   if(curPhoto == null)
			   return;
		   AFLog.i("2 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
		   Bitmap rotateBitmap = Bitmap.createBitmap(curPhoto, 0, 0, (int)curPhoto.getWidth() , (int)curPhoto.getHeight(), localMatrix, true);
		   AFLog.i("3 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
		   
		   if(curPhoto != rotateBitmap)
	 	   {
			   curPhoto.recycle();
			   curPhoto = null;
			   System.gc();
	 	   }
		   
		   Bitmap scaleBitmap = Bitmap.createScaledBitmap(rotateBitmap,(int)(curRotateWidth*rate), (int)(curRotateHeight*rate), true);
		   if(scaleBitmap != rotateBitmap)
	 	   {
			   rotateBitmap.recycle();
			   rotateBitmap = null;
			   System.gc();
	 	   }
		   
		   AFLog.i("4 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
	 	   Bitmap photoBitmap = Bitmap.createBitmap(scaleBitmap,(int)( cropLeft * rate),(int)( cropTop * rate),(int)( cropWidth * rate),(int) (cropHeight * rate));
			if(photoBitmap != scaleBitmap)
			{
				scaleBitmap.recycle();
				scaleBitmap = null;
				System.gc();
			}

			if(photoBitmap == null || photoBitmap.isRecycled())
				return;
			
			AFLog.i("5 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
	 	    Paint paint = new Paint();

	 	   AFLog.i("6 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
			 
	
				
				
	 	  AFLog.i("7 Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
				if(curFrameData.isLocal())
				{
					frameBitmap = BitmapFactory.decodeResource(this.getResources(),curFrameData.getFrameRes());
				}else
				{
					String fileName = FrameApp.imageCacheDir + File.separator + StringUtils.getAFFileName(curFrameData.getFrameUrl());
					frameBitmap = Picture.getBitmapFromFile(fileName,1);
				}
				
				AFLog.i("8 data from frame Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
				 resultBitmap = Bitmap.createBitmap((int)frameWidth,(int)frameHeight, Bitmap.Config.RGB_565);
				 
				 Canvas canvas = new Canvas(resultBitmap);
					canvas.drawBitmap(photoBitmap, left > 0?left*rate:0, top > 0?top * rate:0, paint);
				if(frameBitmap == null)
					return;
				canvas.drawBitmap(frameBitmap, 0, 0,paint);
				AFLog.i("9 finish Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
				canvas.save();
				
				if(photoBitmap != null &&! photoBitmap.isRecycled())
				{
					photoBitmap.recycle();
					photoBitmap = null;
					System.gc();
				}
				
				if(frameBitmap != null &&! frameBitmap.isRecycled())
				{
					frameBitmap.recycle();
					frameBitmap = null;
					System.gc();
				}

		}
		
		private void saveImage(Bitmap bitmap)
		{
			
		 	SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
		 	String filename = timeStampFormat.format(new Date());
	     
		 	String title = "IMG_" + filename;
			ContentResolver cr = getContentResolver();
			
			//MediaStore.Images.Media.insertImage(cr, bitmap,title, "Photo from " + Constants.AppName);
	     	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
	     	bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); //bm is the bitmap object   
	     	byte[] data = baos.toByteArray();
	     	
	     	Uri uri = ImageStore.saveImage(cr, data, title);
	        Toast.makeText(this, R.string.save_image_success, Toast.LENGTH_SHORT).show();
	        return;
		}



		private void setAsWallpaper(Bitmap bitmap) {
			WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);  
			 try {
				wallpaperManager.setBitmap(bitmap);
				Toast.makeText(this, R.string.set_wallpaper_ok, Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//Log.e(TAG,"set wallpaper failed: " + e.getMessage());
				Toast.makeText(this, R.string.set_wallpaper_fail, Toast.LENGTH_SHORT).show();
			}  	
		} 
		private void shareImage(Bitmap bitmap)
		{
	    	SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    	String filename = "tmp_" +  timeStampFormat.format(new Date()) + ".png";
	    	//Log.d(TAG,FrameApp.tmpDir);
		    File outPutFile = new File(FrameApp.tmpDir,filename);
	        if(!outPutFile.exists())
	        {
	        	FileOutputStream fos = null;
	        	try {
	        		fos = new FileOutputStream(outPutFile);
	        		bitmap.compress(CompressFormat.PNG, 100, fos);
	        		fos.close();
	        	} catch (FileNotFoundException e) {
	        		Log.e(TAG,"file write errpr: " + e.getMessage());
		        } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
	        
		    Uri uri = Uri.fromFile(outPutFile);
		    Intent intent = new Intent();
		    intent.setAction(Intent.ACTION_SEND);
		    intent.setType("image/png");
		    intent.putExtra(Intent.EXTRA_STREAM, uri);
		    StringBuilder str = new StringBuilder();
		    Resources rs = this.getResources();
		    str.append(rs.getString(R.string.share_from));
		    str.append(" ");
		    str.append(Constants.AppName);
		    str.append("(");
		    str.append(Constants.AppHttpUrl);
		    str.append(" )");
		    intent.putExtra(Intent.EXTRA_TEXT, str.toString());

		    try {
		    	
		    	 this.startActivity(Intent.createChooser(intent,rs.getString(R.string.share_title))); 
		    } catch (android.content.ActivityNotFoundException ex) {
		        Toast.makeText(this,R.string.share_fail,
		                Toast.LENGTH_SHORT).show();
		  }
		}

		
		public void onDestroy()
		{
			imageView.setImageBitmap(null);
			if(resultBitmap != null && !resultBitmap.isRecycled())
			{
				resultBitmap.recycle();
				resultBitmap = null;
				System.gc();
			}
			AFLog.i("after destroy preview Memory Used::" + (int)(Debug.getNativeHeapAllocatedSize() / 1048576L));
			super.onDestroy();
		}
		public void onPause()
		{
			super.onPause();
		
		}

		@Override
		public void onClick(View v) {
			switch(v.getId())
			{
			case R.id.preview_cancel:
				this.finish();
				break;
			case R.id.preview_share:
				
				Resources rs = this.getResources();
				final CharSequence[] items = { rs.getString(R.string.save_image), rs.getString(R.string.share_image),rs.getString(R.string.set_wallpaper)}; 
		        final AlertDialog dlg = new AlertDialog.Builder(this).setTitle(R.string.save_share).setItems(items, new DialogInterface.OnClickListener() { 
			        public void onClick(DialogInterface dialog,int item) { 
		                 //save 
		                 if(item==0){ 
		                	 StatisticTool.postToServer(PreviewActivity.this, Constants.ACT_DOWNLOAD);
		                     saveImage(resultBitmap);
		                     dialog.dismiss();
		                       
		                 }else if(item==1){ 
		                	//share
		                	 StatisticTool.postToServer(PreviewActivity.this, Constants.ACT_SHARE);
		                	 shareImage(resultBitmap);

		                	 dialog.dismiss();
		                   }else
		                   {
		                	   StatisticTool.postToServer(PreviewActivity.this, Constants.ACT_WALLPAPER);
			                	 setAsWallpaper(resultBitmap);
			                	 dialog.dismiss();
		                   }
		                 }

		                }
		        ).create(); 
		        dlg.show(); 
				
				break;
			}
			
		}
		
		private Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what == MSG_FINISH_GEN)
				{
					
					imageView.setImageBitmap(resultBitmap);
				}
			}
		};
		//download file
		public class GenBitmapThread extends Thread {
			public void run() {
					genBitmap();
					handler.sendEmptyMessage(MSG_FINISH_GEN);
				
			}
		}
		
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.menu, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			Intent intent;
			switch (item.getItemId())
			{
			case R.id.menu_feedback:
					intent = new Intent(this, FeedBack.class);
					startActivity(intent);
					return true;
			case R.id.menu_setting:
					intent = new Intent(this,SetActivity.class);
					startActivity(intent);
					return true;
			}
			return super.onOptionsItemSelected(item);
		}
		
		
		
}
