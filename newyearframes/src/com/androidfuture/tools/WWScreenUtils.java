package com.androidfuture.tools;


import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class WWScreenUtils {
	private static WindowManager winManager;
	public static void init(Context context)
	{
		if(winManager == null)
		{
			winManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		}
	}
	
	public static int getWidthByPX()
	{
		if(winManager == null)
			return 480;
		Point point = new Point();
		return winManager.getDefaultDisplay().getWidth();
		//return point.x;
	}
	
	public static int getHeightByPX()
	{
		if(winManager == null)
			return 800;
		return winManager.getDefaultDisplay().getHeight();
	}
	

	public static int getWidthByDP()
	{
		return (int)(getWidthByPX()/getScale() + 0.5f);
	}
	
	@SuppressWarnings("deprecation")
	public static int getHeightByDP()
	{
		return (int)(getHeightByPX()/getScale() + 0.5f);
	}
	
	public static float getScale()
	{
		if(winManager == null)
			return 1.0f;
		 DisplayMetrics metrics = new DisplayMetrics();
		winManager.getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
	}
}
