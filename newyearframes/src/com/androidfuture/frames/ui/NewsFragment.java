package com.androidfuture.frames.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.data.NewListItemAdapter;
import com.androidfuture.frames.service.FrameManager;

public final class NewsFragment extends Fragment implements OnItemLongClickListener, OnItemClickListener, OnScrollListener, ListChangeListener  {
	private static final String KEY_CONTENT = "NewsFragment";
	
	private static final int MSG_DOWNLOAD_OK = 301;
	
	private static final int MSG_DOWNLOAD_FAIL = 302;
	
	private static final int MSG_DOWNLOAD_END = 303;
	

	
	private static final int STAT_OK = 201;
	
	private static final int STAT_ERROR = 202;
	
	private static final int LOAD_FINISH = 101;
	
	private static final int LOADING = 102;
	
	private static final String TAG = "NewsFragment";
	
	private TextView loadTextView;
	
	private ProgressBar loadProgress;
	
	//private ListView itemList;
	
	private GridView gridView;
	
	private ArrayList list;
	
	private ArrayList newList;
	
	private Context mContext;
	
	private NewListItemAdapter listAdapter;
	
	private int mStart;
	
	private static final  int mLen=16;
	
    //private int loadState;
	
	private int mTab = 0;
	
	private View footerView;
	private boolean mFinish;
	

	public static NewsFragment newInstance(int tab,Context context) {
		NewsFragment fragment = new NewsFragment();
		fragment.mTab = tab;
		fragment.mContext = context;
		return fragment;
	}
	
	
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		list = new ArrayList();
		newList = new ArrayList();
	
		
		//itemList.setOnScrollListener(this);
		 listAdapter = new NewListItemAdapter(this.mContext,list,mTab);
		 if(mTab == Constants.TAB_FAVORITE)
			{
				((FrameApp)((Activity)mContext).getApplication()).getFavManager().setListChangeListener(this);
			}else if(mTab == Constants.TAB_HISTORY)
			{
				((FrameApp)((Activity)mContext).getApplication()).getHistManager().setListChangeListener(this);
			}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
			mTab = savedInstanceState.getInt(KEY_CONTENT);
		}
		
		if(this.mContext==null)
			return null;
		
		 LayoutInflater mInflater = (LayoutInflater) this.mContext.getSystemService(
	                Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) mInflater.inflate(R.layout.fragment,null, false);


		gridView =(GridView) layout.findViewById(R.id.grid_view);
		//gridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		

		 footerView = layout.findViewById(R.id.footer_view_layout);
		 if(mTab == Constants.TAB_FAVORITE)
		 {
			 layout.removeView(footerView);
		 }
		 if(!mFinish)
		 { 
			 footerView.setVisibility(View.VISIBLE);
		 }
		 
		loadTextView = (TextView) footerView.findViewById(R.id.footer_view_text);
		loadProgress = (ProgressBar) footerView.findViewById(R.id.footer_view_process);

		
		gridView.setNumColumns(2);
		gridView.setAdapter(listAdapter);
		gridView.setOnItemLongClickListener(this);
		gridView.setOnItemClickListener(this);
		gridView.setOnScrollListener(this);
		
		if(list.size() == 0)
		{
		
				mFinish =false;
				loadTextView.setText(R.string.loading_more);
				new DownloadListThread().start();
			
		}
		
		
	 	//gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH); 	 		
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, mTab);
	}
	
	public void onResume()
	{
		super.onResume();

 	
 		if(gridView != null)
 		{
	 		for(int i = 0;i < gridView.getChildCount();i++)
			{
	 			gridView.getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
			}
 		}

	}
	
	public void refresh()
	{
		//Log.d(TAG,"refresh");
		if(mFinish)
		{
			
			list.clear();
			mStart = 0;
			listAdapter.notifyDataSetChanged();
			startLoad();
			
		}
	}
	
	private void startLoad()
	{
		if(!mFinish)
			return;
		mFinish = false;
		footerView.setVisibility(View.VISIBLE);
		new DownloadListThread().start();
	}
	
	private void stopLoad()
	{
		mFinish = true;
		Animation animation = AnimationUtils.loadAnimation(this.mContext, R.anim.footer_disappear);
		footerView.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener(){

			@Override
			public void onAnimationEnd(Animation arg0) {
				footerView.setVisibility(View.GONE);
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
	}
	/**
	 * handle message
	 */
	private Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case MSG_DOWNLOAD_OK:
				{

						mFinish = true;
						stopLoad();

				list.addAll(newList);
				listAdapter.notifyDataSetChanged();
			}
				break;
			case MSG_DOWNLOAD_FAIL:
				loadTextView.setText(R.string.no_net_loading_more);
				loadProgress.setVisibility(View.GONE);
				stopLoad();
				break;
			case MSG_DOWNLOAD_END:
				loadTextView.setText(R.string.loading_end);
				loadProgress.setVisibility(View.GONE);
				stopLoad();
				break;
			
			}
		}
	};


	
	private class DownloadListThread extends Thread
	{
		public void run() { 
			 int i = 0;
			 mFinish = false;
			 Message msg = handler.obtainMessage();
			 Bundle b = new Bundle();
			 newList.clear();
			 int response = 0;
			 while(newList.size() ==0 &&  i++ < 3 && mContext!=null)
			 {
				 response = ((FrameApp)((Activity)mContext).getApplication()).
				 getFrameManager().getFrameList(newList,mTab,mStart,mLen); 
			 }
			 int status;
			 switch(response)
			 {
			 case FrameManager.RESP_FAIL:
				 status = MSG_DOWNLOAD_FAIL;
				 break;
			 case FrameManager.RESP_END:
				 status = MSG_DOWNLOAD_END;
				 break;
			 case FrameManager.RESP_OK:
				 if(newList.size() > 0)
				 {
					 //Log.d(TAG,"size: " + newList.size());
					 mStart = mStart + newList.size();
					 status = MSG_DOWNLOAD_OK;
				 }else
				 {
					 status = MSG_DOWNLOAD_END;
				 }
				 break;
			default:
				status = MSG_DOWNLOAD_FAIL;
			 }
			
			 
				 
			 msg.what = status;
			
			 handler.sendMessage(msg);
		}
	}

	

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
			
			FrameData frame = (FrameData) list.get(position);
			((ProcessActivity)this.mContext).setFrame(frame);
			if(frame != null)
			{
				((FrameApp)((Activity)mContext).getApplication()).getHistManager().addBrowseHistory(frame);
			}
		}
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position,
			long id) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"long click");
		if(this.mTab == Constants.TAB_FAVORITE)
		{
			AlertDialog.Builder builder =  new AlertDialog.Builder(this.mContext)
            .setIcon(R.drawable.icon_launch)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	FrameData curTopic = (FrameData) list.get(position);
                	((FrameApp)((Activity)mContext).getApplication()).getFavManager().delete(curTopic);
                	refresh();
                	}
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	return;
                }
            });
			builder.setMessage(R.string.remove_fav);
			builder.create().show();
		}else if(this.mTab == Constants.TAB_HISTORY) 
		{
			//remove history
			AlertDialog.Builder builder =  new AlertDialog.Builder(this.mContext)
            .setIcon(R.drawable.icon_launch)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	FrameData curTopic = (FrameData) list.get(position);
                	((FrameApp)((Activity)mContext).getApplication()).getHistManager().delete(curTopic);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	return;
                }
            });
			builder.setMessage(R.string.remove_history);
			builder.create().show();
		}else 
		{
			// add favorite
			AlertDialog.Builder builder =  new AlertDialog.Builder(this.mContext)
            .setIcon(R.drawable.icon_launch)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	FrameData curTopic = (FrameData) list.get(position);
                	((FrameApp)((Activity)mContext).getApplication()).getFavManager().addFav(curTopic);
                	}
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	return;
                }
            });
			builder.setMessage(R.string.add_fav);
			builder.create().show();
		}
		return false;
	}

	private int lastVisibleItem;

	private int totalItem;
	 public void onScrollStateChanged(AbsListView view, int scrollState) {
	    	//Log.d(TAG,"scrollState:"+scrollState);
		 	if(mTab == Constants.TAB_FAVORITE || mTab == Constants.TAB_HISTORY)
		 		return;
	    	if (scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_FLING) { 
	    		//Log.d("appwill","last position:"+view.getLastVisiblePosition() + "\tcount:"+view.getCount() + "\tstate:"+loadState);
	        	if(lastVisibleItem > 0 && lastVisibleItem == totalItem && mFinish  )
	        		{
	        		 	startLoad();
	        		}
	        	}
	    }
	    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	    	this.lastVisibleItem = firstVisibleItem + visibleItemCount;
	    	this.totalItem = totalItemCount;
	    }




		@Override
		public void onListChange() {
			// TODO Auto-generated method stub
			this.refresh();
		}
    
    
  
}
