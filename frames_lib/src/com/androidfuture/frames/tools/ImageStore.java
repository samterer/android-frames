package com.androidfuture.frames.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.androidfuture.tools.AFLog;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;

public class ImageStore {
	public static final String TAG = "ImageStore";
	public static final String DCIM =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

	public static final String DIRECTORY = DCIM + "/Camera";
	
 public static Uri saveImage(ContentResolver cr,byte[] data,String title)
 {
	
			String path = DIRECTORY + '/' + title + ".jpg";
			File file = new File(DIRECTORY);
			if(!file.exists())
			{
				path = DCIM + "/"+ title + ".jpg";
			}
	        FileOutputStream out = null;
	        try {
	            out = new FileOutputStream(path);
	            out.write(data);
	        } catch (Exception e) {
	            Log.e(TAG, "Failed to write image", e);
	            return null;
	        } finally {
	            try {
	                out.close();
	            } catch (Exception e) {
	            }
	        }
			ContentValues values = new ContentValues(9);
	        values.put(ImageColumns.TITLE, title);
	        values.put(ImageColumns.DISPLAY_NAME, title + ".jpg");
	        values.put(ImageColumns.DATE_TAKEN, ""+System.currentTimeMillis());
	        values.put(ImageColumns.MIME_TYPE, "image/jpeg");
	        values.put(ImageColumns.ORIENTATION, 0);
	        

	        values.put(ImageColumns.DATA, path);
	        values.put(ImageColumns.SIZE, data.length);
	        Uri uri = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
	        if (uri == null) {
	            Log.e(TAG, "Failed to write MediaStore");
	            return null;
	        }
	        AFLog.d(uri.toString());
			return uri;
 }
}
