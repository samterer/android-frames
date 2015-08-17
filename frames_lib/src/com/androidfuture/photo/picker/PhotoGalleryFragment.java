package com.androidfuture.photo.picker;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.androidfuture.data.AFListAdapter;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.R;
import com.androidfuture.frames.ui.FramesAppFragment;
import com.androidfuture.frames.ui.MoreFramesAppItemView;
import com.androidfuture.network.AFData;
import com.androidfuture.photo.picker.PhotoManager.OnChosePhotoChangeListener;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.FileUtils;
import com.androidfuture.tools.MediaUtils;
import com.androidfuture.tools.StringUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;

import android.os.AsyncTask;

import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PhotoGalleryFragment extends Fragment implements
		OnItemClickListener, OnChosePhotoChangeListener, OnClickListener {
	private AFListAdapter photoListAdapter;
	private ArrayList<AFData> imageList = new ArrayList<AFData>();
	private Context mContext;
	private GridView gridView = null;
	private View systemWrap = null;
	private int lastView;

	private static int ALBUM_CODE = 102;
	private static final String[] STORE_IMAGES = { MediaStore.Images.Media._ID };

	public static PhotoGalleryFragment newInstance(Context context) {
		PhotoGalleryFragment fragment = new PhotoGalleryFragment();
		fragment.mContext = context;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LayoutInflater mInflater = (LayoutInflater)getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = (RelativeLayout) mInflater.inflate(
				R.layout.photo_albums, null, false);

		photoListAdapter = new AFListAdapter(getActivity(),
				AFPhotoGridView.class);

		gridView = (GridView) view.findViewById(R.id.phone_photo_grid);
		gridView.setAdapter(photoListAdapter);

		gridView.setOnItemClickListener(this);

		if (photoListAdapter.getAll().size() == 0) {
			new ScanImageThread().start();
		} else {
			photoListAdapter.notifyDataSetChanged();
		}

		lastView = gridView.getLastVisiblePosition();
		PhotoManager.GetInstance().addListener(this);
		
		
		view.findViewById(R.id.system_gallery_btn).setOnClickListener(this);
		systemWrap = view.findViewById(R.id.system_gallery_wrap);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		photoListAdapter.notifyDataSetChanged();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 102)
			{
				
				if (imageList.isEmpty())
				{
					systemWrap.setVisibility(View.VISIBLE);
					gridView.setVisibility(View.GONE);
				}else
				{
					systemWrap.setVisibility(View.GONE);
					gridView.setVisibility(View.VISIBLE);
					photoListAdapter.clear();
					photoListAdapter.addAll(imageList);
					photoListAdapter.notifyDataSetChanged();
				}
			}
		}
	};

	public class ScanImageThread extends Thread {
		public void run() {

			imageList.addAll(MediaUtils.getInstance(getActivity())
					.getCameraPhotos());
			handler.sendEmptyMessage(102);

		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position,
			long arg3) {
		AFPhotoData data = (AFPhotoData) photoListAdapter.getItem(position);

		if (PhotoManager.GetInstance().isSelected(data)) {
			PhotoManager.GetInstance().delPhoto(data);
			((AFPhotoGridView) v).updateCheck(false);

		} else {
			if (PhotoManager.GetInstance().GetSelected().size() >= PhotoManager
					.GetInstance().getSelectMax()) {
				String text = getActivity().getResources().getString(
						R.string.select_photo_limit,
						PhotoManager.GetInstance().getSelectMax());
				Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
				return;
			}
			PhotoManager.GetInstance().addPhoto(data);
			((AFPhotoGridView) v).updateCheck(true);

			EasyTracker.getInstance(getActivity()).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_PHOTO,
							Constants.EVENT_PHOTO_GALLARY, null, null).build());
		}
		// this.photoListAdapter.notifyDataSetChanged();

	}


	@Override
	public void onChosePhotoChange() {
		photoListAdapter.notifyDataSetChanged();

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.system_gallery_btn)
		{
			((PhotoChooseActivity)mContext).clickSystemGallery();
		}
		
	}
	
	
}
