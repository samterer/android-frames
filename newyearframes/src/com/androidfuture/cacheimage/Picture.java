package com.androidfuture.cacheimage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.util.Log;



public class Picture {
	


public static Bitmap downloadBitmap(String url )
{
	URL myFileUrl = null;
	Bitmap bitmap = null;
	if(url == null)
		return null;
	InputStream is = null;
		try {			
			myFileUrl = new URL(url);
			} catch (MalformedURLException e) {
				return null;
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
				conn.setConnectTimeout(1000 * 120);
				conn.setDoInput(true);
				conn.connect();
				is = conn.getInputStream();
		
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
		} catch (Exception e) {
			Log.e("error","Fail to download image:" + url+e.getMessage());
			bitmap = null;
		}
		return bitmap;
}

public static Bitmap getBitmapFromFile(String path,int sampleSize)
{
	Bitmap bitmap = null;
	InputStream is = null;
	 File file = new File(path);
	 Options option = new Options();
	 option.inSampleSize = sampleSize;
	   try {
	     is = new BufferedInputStream(new FileInputStream(file));
	 	 bitmap = BitmapFactory.decodeFile(path, option);
	   }catch(Exception e)
	   {
		   bitmap = null;
	   }
	    finally {
	     if (is != null) {
	       try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			bitmap = null;
		}
	     }
	   }


	return bitmap;
}


//��������ͼƬ�Ĵ�С
//newWidth: ������ ��λΪpix
//newHeight: ����߶�?��λΪpix
public static Bitmap resize(Bitmap bitmap,int newWidth,int newHeight)
{
	int bmpWidth = bitmap.getWidth();
	int bmpHeight = bitmap.getHeight();
	Matrix matrix = new Matrix();
	float scaleWidth = ((float) newWidth) / bmpWidth;
	float scaleHeight = ((float) newHeight) / bmpHeight; 
	matrix.postScale(scaleWidth, scaleHeight);
	Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
	return resizeBmp;
	
}
/**
 * resize the image with the new width but keeping the rate of width and length
 * @param newWidth
 * @return
 */
public Bitmap resizeByWidth(Bitmap bitmap,int newWidth)
{
	int bmpWidth = bitmap.getWidth();
	int bmpHeight = bitmap.getHeight();
	Matrix matrix = new Matrix();
	float scaleWidth = ((float) newWidth) / bmpWidth;
	matrix.postScale(scaleWidth, scaleWidth);
	Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
	return resizeBmp;
	
}

public Bitmap resizeByHeight(Bitmap bitmap,int newHeight)
{
	int bmpWidth = bitmap.getWidth();
	int bmpHeight = bitmap.getHeight();
	Matrix matrix = new Matrix();
	float scaleHeight = ((float) newHeight) / bmpHeight;
	matrix.postScale(scaleHeight, scaleHeight);
	Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
	return resizeBmp;
	
}

public Bitmap rotate(Bitmap bitmap,int angle)
{
	Matrix matrix = new Matrix();
	int bmpWidth = bitmap.getWidth();
	int bmpHeight = bitmap.getHeight();
	matrix.postRotate(angle);
	Bitmap rotateMap = Bitmap.createBitmap(bitmap,0,0,bmpWidth,bmpHeight,matrix,true);
	return rotateMap;
}
/**
 * 
 * @param newWidth
 * @param newHeight
 * @param angle
 * @return
 */
public Bitmap rotateResize(Bitmap bitmap,int newWidth,int newHeight,int angle)
{
	int bmpWidth = bitmap.getWidth();
	int bmpHeight = bitmap.getHeight();
	Matrix matrix = new Matrix();
	float scaleWidth = ((float) newWidth) / bmpWidth;
	matrix.postScale(scaleWidth, scaleWidth);
	matrix.postRotate(angle);
	Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true);
	return resizeBmp;
}
}

