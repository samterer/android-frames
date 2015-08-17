package com.androidfuture.frames.ui;

import com.androidfuture.network.AFData;

import android.content.Context;
import android.widget.RelativeLayout;

public abstract class AFCellView extends RelativeLayout {
	public AFCellView(Context context) {
		super(context);
	}
		
	abstract public void update(AFData data);
}
