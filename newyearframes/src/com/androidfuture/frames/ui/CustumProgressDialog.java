package com.androidfuture.frames.ui;


import com.androidfuture.newyear.framesfree.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;

public class CustumProgressDialog extends Dialog {

	private ProgressBar mProgressBar;
	public CustumProgressDialog(Context context) {
		
		super(context);
	}
	 public void onCreate(Bundle savedInstanceState){  
         super.onCreate(savedInstanceState);  
         setContentView(R.layout.custom_dialog);  
        
         mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
         mProgressBar.setMax(100);
     }  	
	 
	 public void updateProgress(int progress)
	 {
		 mProgressBar.setProgress(progress);
	 }
	 
	 

}
