package com.androidfuture.photo.picker;

import java.util.ArrayList;

import com.androidfuture.data.AFAlbumData;
import com.androidfuture.data.AFListAdapter;
import com.androidfuture.frames.R;
import com.androidfuture.tools.MediaUtils;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PhonePhotoAlbumFragment extends Fragment implements
		OnItemClickListener {

	private static int ALBUM_CODE = 102;
	private GridView mAlbumsView;
	private AFListAdapter mListAdapter;
	private ArrayList<AFAlbumData> imageList = new ArrayList<AFAlbumData>();
	private Context mContext;
	private static PhonePhotoAlbumFragment instance;
	public static PhonePhotoAlbumFragment newInstance(Context context) {
		if (instance == null)
		{
			instance =new PhonePhotoAlbumFragment();
		}
		instance.mContext = context;
		return instance;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		LayoutInflater mInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = (RelativeLayout) mInflater.inflate(
				R.layout.photo_albums, null, false);
		mAlbumsView = (GridView) view.findViewById(R.id.phone_photo_grid);

		
		// request phone albums
		if(mListAdapter!=null && mListAdapter.getCount() > 0)
		{
			mListAdapter.notifyDataSetChanged();
		}else
		{
			mListAdapter = new AFListAdapter(getActivity(), AlbumListItemView.class);
			new ScanImageThread().start();
		}
		mAlbumsView.setAdapter(mListAdapter);
		mAlbumsView.setOnItemClickListener(this);
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		AFAlbumData data = (AFAlbumData) this.mListAdapter.getItem(position);
		if (data != null) {
			Intent intent = new Intent(getActivity(), PhonePhotoSelectActivity.class);
			intent.putExtra("id", data.getAlbumId());
			intent.putExtra("name", data.getTitle());
			startActivityForResult(intent, ALBUM_CODE);
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*
		if (ALBUM_CODE == requestCode) {
			if (resultCode == Activity.RESULT_OK) {
				setResult(Activity.RESULT_OK, data);
				getActivity().finish();
			} else {

			}
		} else {

		}
		*/
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 101)
			{
				mListAdapter.clear();
				mListAdapter.addAll(imageList);
				mListAdapter.notifyDataSetChanged();
			}
		}
	};

	public class ScanImageThread extends Thread {
		public void run() {

			imageList.addAll(MediaUtils.getInstance(
					getActivity()).getPhoneAlbums());
			handler.sendEmptyMessage(101);

		}
	}
	
	public void refresh()
	{
		mListAdapter.notifyDataSetChanged();
	}
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_select, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
			return true;
		} else if (item.getItemId() == R.id.action_done) {
			setResult(RESULT_OK);
			finish();
		} else if (item.getItemId() == R.id.action_reset) {
			PhotoManager.GetInstance().clearSelected();
		}

		return super.onOptionsItemSelected(item);
	}
	*/
}
