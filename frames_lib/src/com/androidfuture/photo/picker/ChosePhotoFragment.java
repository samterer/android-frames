package com.androidfuture.photo.picker;

import java.io.File;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidfuture.data.AFListAdapter;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.R;
import com.androidfuture.tools.FileUtils;
import com.meetme.android.horizontallistview.HorizontalListView;

public class ChosePhotoFragment extends Fragment implements OnClickListener {

	public static final int MSG_SAVE_BITMAP = 102;
	private HorizontalListView gridView;
	private AFListAdapter listAdapter;
	private TextView titleView;
	int selected = -1;
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LayoutInflater mInflater = (LayoutInflater)this.getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = (RelativeLayout) mInflater.inflate(
				R.layout.chose_photo_gallery, null, false);
	
		listAdapter = new AFListAdapter(this.getActivity(), AFPhotoChoseGridView.class);
		gridView = (HorizontalListView) view.findViewById(R.id.chose_photo_grid);
		gridView.setAdapter(listAdapter);
		//gridView.setOnItemClickListener(this);
		view.findViewById(R.id.photo_chose_camera).setOnClickListener(this);
		view.findViewById(R.id.photo_chose_delete).setOnClickListener(this);
		titleView = (TextView)view.findViewById(R.id.photo_chose_title);
		return view;
	}
	
	public void updateView()
	{
		listAdapter.clear();
		listAdapter.addAll(PhotoManager.GetInstance().GetSelected());
		listAdapter.notifyDataSetChanged();
		String text = getActivity().getResources().getString(R.string.selected_photo,listAdapter.getCount() );
		titleView.setText(text);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.photo_chose_delete)
		{
			if(selected > -1 && selected < listAdapter.getCount())
			{
				AFPhotoData data = (AFPhotoData)(listAdapter.getItem(selected));
				PhotoManager.GetInstance().delPhoto(data);
			}
		}
	}
	


	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SAVE_BITMAP:
				break;
				default:
					break;
			}
			AFPhotoData data = new AFPhotoData();
			String path = msg.getData().getString("file_path");
			data.setPath(path);
			data.setThumbPath(path);
			PhotoManager.GetInstance().addPhoto(data);
			
		}
	};
	

	/**
	 * thread for save image to disk
	 * 
	 * @author cschen
	 * 
	 */

	public class SaveThread extends Thread {
	Bitmap saveBitmap;

	public SaveThread(Bitmap bitmap) {
		saveBitmap = bitmap;
	}

	public void run() {
		String fileStr = AFAppWrapper.GetInstance().GetApp().GetConf().TmpDir
				+ File.separator + "tmp_" + new Date().getTime() + ".jpg";
		FileUtils.saveBitmap2SD(saveBitmap, fileStr);

		Message msg = handler.obtainMessage();
		msg.what = MSG_SAVE_BITMAP;
		Bundle data = new Bundle();
		data.putString("file_path", fileStr);
		msg.setData(data);
		handler.sendMessage(msg);

	}

}

	private static ChosePhotoFragment instance;
	public static ChosePhotoFragment instance() {
		// TODO Auto-generated method stub
		if (instance == null)
		{
			instance = new ChosePhotoFragment();
		}
		return instance;
	}
	/*
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		clearSelect();
		if (selected != position)
		{
			selected = position;
			v.setBackgroundColor(getActivity().getResources().getColor(R.color.base_color));
		}else
		{
			selected = -1;
		}
		
	}
	*/
	
	public void clearSelect() {
		for (int i = 0; i < gridView.getChildCount(); i++) {
			View v = gridView.getChildAt(i);
			v.setBackgroundColor(Color.TRANSPARENT);
		}
	}
	


}
