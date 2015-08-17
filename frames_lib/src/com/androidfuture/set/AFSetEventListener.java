package com.androidfuture.set;

import android.view.View;

public interface AFSetEventListener {
	public void onSetItemClick(View view, int id);
	public void onToggleStateChange(View view,boolean state, int id);
}
