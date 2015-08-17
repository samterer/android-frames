package com.androidfuture.network;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;

public class NetWork {
	
	public static boolean isNetworkAvailable( Context mContext ) { 
		 Context context = mContext.getApplicationContext();
		 ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {    
		     return false;
		 } else {  
			 NetworkInfo[] info = connectivity.getAllNetworkInfo();    
			 if (info != null) {        
			 for (int i = 0; i<info.length; i++) {           
				if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					return true; 
			 	}     
			 } 
		 }   
		return false;
	}
	

}
