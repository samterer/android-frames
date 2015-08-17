package com.androidfuture.frames.tools;

import com.androidfuture.frames.Constants;



class PostDataThread extends Thread {
	int mAct;
	int mFrameId;
	public PostDataThread(int act,int frameId) {
		mAct = act;
		
	}

	public void run() {
		
		StringBuilder bld = new StringBuilder();
		bld.append(Constants.URLRoot);
		bld.append("/post.php");
		bld.append("?act=");
		bld.append(mAct);
		bld.append("&frame_id=");
		bld.append(mFrameId);
		// Log.d(TAG,"url:" + bld.toString());
		 HttpUtils.request(bld.toString());


	}
}