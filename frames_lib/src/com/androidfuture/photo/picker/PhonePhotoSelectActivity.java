package com.androidfuture.photo.picker;

import java.util.ArrayList;

import com.androidfuture.data.AFListAdapter;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.R;
import com.androidfuture.network.AFData;
import com.androidfuture.photo.picker.PhotoManager.OnChosePhotoChangeListener;
import com.androidfuture.tools.MediaUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class PhonePhotoSelectActivity extends FragmentActivity implements
		OnItemClickListener, OnChosePhotoChangeListener {

	private GridView mPhotosGrid;
	private AFListAdapter photoListAdapter;
	private String albumId;
	private String albumName;
	private ChosePhotoFragment chosePhotos;
	private ArrayList<AFData> imageList = new ArrayList<AFData>();
	/*
	 * Layout the Graph Explorer
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    ActionBar actionBar = getActionBar();
		    actionBar.setDisplayHomeAsUpEnabled(true);
		}
		setContentView(R.layout.photo_select);
		// setTitle(R.string.title_select_photo);
		albumId = getIntent().getStringExtra("id");
		albumName = getIntent().getStringExtra("name");
		
		setTitle(albumName);
		photoListAdapter = new AFListAdapter(this, AFPhotoGridView.class);

		mPhotosGrid = (GridView) findViewById(R.id.phone_photo_grid);

		mPhotosGrid.setAdapter(photoListAdapter);
		mPhotosGrid.setOnItemClickListener(this);

		if (photoListAdapter.getAll().size() == 0) {
			new ScanImageThread().start();
		}
		chosePhotos = new ChosePhotoFragment();

		PhotoManager.GetInstance().addListener(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		
		fragmentTransaction.replace(R.id.phone_chose_photo_gallery, chosePhotos).commit();

	}

	@Override
	public void onResume() {
		super.onResume();
		chosePhotos.updateView();
		photoListAdapter.notifyDataSetChanged();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		PhotoManager.GetInstance().removeListener(this);
		super.onDestroy();

	}
	public void onItemClick(AdapterView<?> adapter, View v, int position,
			long arg3) {
		AFPhotoData data = (AFPhotoData) photoListAdapter.getItem(position);

		if (PhotoManager.GetInstance().isSelected(data)) {
			PhotoManager.GetInstance().delPhoto(data);
			((AFPhotoGridView) v).updateCheck(false);
		} else {
			if (PhotoManager.GetInstance().GetSelected().size() >= PhotoManager.GetInstance().getSelectMax())
			{
				String text = getResources().getString(R.string.select_photo_limit, PhotoManager.GetInstance().getSelectMax());
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				return;
			}
			PhotoManager.GetInstance().addPhoto(data);
			((AFPhotoGridView) v).updateCheck(true);
			
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_PHOTO,
							Constants.EVENT_PHOTO_PHONE, null, null).build());
		}
		// this.photoListAdapter.notifyDataSetChanged();

	}

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
		} 

		return super.onOptionsItemSelected(item);
	}
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			photoListAdapter.clear();
			photoListAdapter.addAll(imageList);
			photoListAdapter.notifyDataSetChanged();
		}
	};

	public class ScanImageThread extends Thread {
		public void run() {

			imageList.addAll(MediaUtils.getInstance(
					PhonePhotoSelectActivity.this).getPhotosByAlbum(albumId));
			handler.sendEmptyMessage(0);

		}
	}
	
	@Override
	public void onChosePhotoChange() {
		chosePhotos.updateView();
		photoListAdapter.notifyDataSetChanged();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(RESULT_OK);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
