package com.androidfuture.frames.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidfuture.newyear.framesfree.R;

import com.androidfuture.frames.data.AFListAdapter;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.data.MoreFramesAppData;
import com.androidfuture.frames.data.NewListItemAdapter;
import com.androidfuture.frames.service.FrameManager;
import com.androidfuture.frames.tools.HttpUtils;
import com.androidfuture.network.AFData;
import com.androidfuture.tools.AFLog;
import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;

public final class FramesAppFragment extends Fragment implements
		OnItemClickListener{
	private static final String KEY_CONTENT = "MoreFramesApp";

	private static final int MSG_DOWNLOAD_OK = 301;

	private static final int MSG_DOWNLOAD_FAIL = 302;

	private static final int MSG_DOWNLOAD_END = 303;

	private static final int STAT_OK = 201;

	private static final int STAT_ERROR = 202;

	private static final int LOAD_FINISH = 101;

	private static final int LOADING = 102;

	private static final String TAG = "NewsFragment";

	private ListView listView;

	private ArrayList<AFData> newList;
	private AFListAdapter listAdapter;

	private Context mContext;

	

	private boolean mFinish;

	public static FramesAppFragment newInstance( Context context) {
		FramesAppFragment fragment = new FramesAppFragment();
		fragment.mContext = context;
		return fragment;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		listAdapter = new AFListAdapter(this.mContext, MoreFramesAppItemView.class);
		newList = new ArrayList<AFData>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			
		}

		if (this.mContext == null)
			return null;

		LayoutInflater mInflater = (LayoutInflater) this.mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) mInflater.inflate(
				R.layout.more_frame_app_list, null, false);

		listView = (ListView) layout.findViewById(R.id.frame_app_list);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(this);
		
		if(listAdapter.getCount() == 0)
		{
			mFinish = false;
			new DownloadListThread().start();
		}else{
			listAdapter.notifyDataSetChanged();
		}
			return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
	}

	public void onResume() {
		super.onResume();

	}


	/**
	 * handle message
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			mFinish = true;
			switch (msg.what) {
			case MSG_DOWNLOAD_OK: {
				listAdapter.clear();
				listAdapter.addAll(newList);
				listAdapter.notifyDataSetChanged();
			}
				break;
			case MSG_DOWNLOAD_FAIL:
				
				break;
			case MSG_DOWNLOAD_END:
				
				break;

			}
		}
	};

	private class DownloadListThread extends Thread {
		public void run() {
			int i = 0;
			mFinish = false;
			Message msg = handler.obtainMessage();
			Bundle b = new Bundle();
			newList.clear();
			
			while (newList.size() == 0 && i++ < 3 && mContext != null) {
			
				//to do new list
				
				String urlStr = "http://www.androidfuture.com/config/moreframes.php?lang=1&source=2&app=" + Constants.appId;
				newList.addAll(downloadList(urlStr));
				
			}
			int status;
			if (newList.size() > 0) {
					// Log.d(TAG,"size: " + newList.size());
					
					status = MSG_DOWNLOAD_OK;
				} else {
					status = MSG_DOWNLOAD_FAIL;
				}
				
			msg.what = status;

			handler.sendMessage(msg);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		//begin to download
		String appUrl = "market://details?id="+((MoreFramesAppData)listAdapter.getItem(position)).getAppId();
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)));
	}

	public static ArrayList<MoreFramesAppData> downloadList(String urlStr)
	{
		ArrayList<MoreFramesAppData> list = new ArrayList();
		AFLog.d("load: " + urlStr);
		JSONObject jsonObj = HttpUtils.request(urlStr);
		
		try {

			if(jsonObj != null && jsonObj.getInt("status") == 0)//&& jsonObj.getBoolean("success"))
			{	
				JSONArray array = jsonObj.getJSONArray("data");
				for(int i = 0;i < array.length();i ++)
				{
					MoreFramesAppData app = new MoreFramesAppData();
					JSONObject obj= (JSONObject) array.opt(i);
					app.setAppId(obj.getString("app_id"));
					app.setAppIconUrl(obj.getString("app_icon_url"));
					app.setAppName(obj.getString("app_name"));
					list.add(app);
					
				}
				AFLog.d("app size : " + list.size());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"json parse error");
		}
		return list;
	}

}
