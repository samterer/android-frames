package com.androidfuture.photo.picker;

import java.io.File;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.androidfuture.cacheimage.ImageDownloadManager;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.R;
import com.androidfuture.photo.picker.ChosePhotoFragment.SaveThread;
import com.androidfuture.photo.picker.PhotoManager.OnChosePhotoChangeListener;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.FileUtils;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class PhotoChooseActivity extends FragmentActivity implements
		OnChosePhotoChangeListener, OnPageChangeListener {
	public static final int SRC_CAMERA = 101;
	public static final int SRC_GALLARY = 103;
	PhotoChooseTitleFragmentAdapter mAdapter;
	private static int ALBUM_CODE = 102;
	private static final String[] STORE_IMAGES = { MediaStore.Images.Media._ID };
	protected static final int MSG_SAVE_BITMAP = 201;
	private ChosePhotoFragment chosePhotos;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_choose);
		
		mAdapter = new PhotoChooseTitleFragmentAdapter(
				getSupportFragmentManager(), this);

		ViewPager mPager = (ViewPager) findViewById(R.id.photo_choose_wrapper);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(this);

		TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.photo_choose_tab);
		indicator.setViewPager(mPager);
		indicator.setCurrentItem(1);

		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position == 2) {
					FacebookAlbumFragment.newInstance(PhotoChooseActivity.this)
							.update();
				} else if (position == 0) {
					PhonePhotoAlbumFragment.newInstance(
							PhotoChooseActivity.this).refresh();
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		PhotoManager.GetInstance().addListener(this);
		chosePhotos = new ChosePhotoFragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.replace(R.id.chose_photo_gallery, chosePhotos);
		fragmentTransaction.commit();
		if(PhotoManager.GetInstance().getSelectMax() == 1)
		{
			PhotoManager.GetInstance().GetSelected().clear();
			findViewById(R.id.chose_photo_gallery).setVisibility(View.GONE);
		}else
		{
			findViewById(R.id.chose_photo_gallery).setVisibility(View.VISIBLE);
			
		}


	}

	@Override
	public void onResume() {
		super.onResume();
		chosePhotos.updateView();
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		PhotoManager.GetInstance().removeListener(this);
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		ImageDownloadManager.GetInstance().clearTasks();
	}

	enum PhoteChooseType {
		Device(R.string.phone),

		Gallery(R.string.gallery),

		Facebook(R.string.facebook);

		PhoteChooseType(int nameRes) {
			name = nameRes;
		}

		public int name;

	}

	class PhotoChooseFragmentAdapter extends FragmentPagerAdapter {

		private HashMap<PhoteChooseType, Fragment> fragmentMap;
		private Activity mContext;

		public PhotoChooseFragmentAdapter(FragmentManager fm, Activity context) {
			super(fm);
			this.mContext = context;
			fragmentMap = new HashMap<PhoteChooseType, Fragment>();
		}

		@Override
		public Fragment getItem(int position) {
			// FrameApp app = ((FrameApp)this.mContext.getApplication());
			PhoteChooseType cur = PhoteChooseType.values()[position];
			Fragment rtn = fragmentMap.get(cur);
			if (rtn == null) {
				switch (cur) {
				case Gallery:
					rtn = PhotoGalleryFragment.newInstance(this.mContext);
					break;
				case Device:
					rtn = PhonePhotoAlbumFragment.newInstance(this.mContext);
					break;
				case Facebook:
					rtn = FacebookAlbumFragment.newInstance(this.mContext);
					break;
				default:
					break;
				}
			}
			fragmentMap.put(cur, rtn);

			return rtn;
		}

		@Override
		public int getCount() {
			if (this.mContext == null || this.mContext.getApplication() == null)
				return 0;
			else
				return PhoteChooseType.values().length;
		}
		/*
		 * public void setCount(int count) { if (count > 0 && count <= 10) {
		 * mCount = count; notifyDataSetChanged(); } }
		 */
	}

	class PhotoChooseTitleFragmentAdapter extends PhotoChooseFragmentAdapter
			implements TitleProvider {
		public Activity mContext;

		public PhotoChooseTitleFragmentAdapter(FragmentManager fm,
				Activity context) {
			super(fm, context);
			this.mContext = context;
		}

		@Override
		public String getTitle(int position) {
			String title = getResources().getString(
					PhoteChooseType.values()[position].name);
			return title;
			// return NewsFragmentAdapter.CONTENT[position % CONTENT.length];
		}
	}

	@Override
	public void onChosePhotoChange() {
		// TODO Auto-generated method stub
		
		if(PhotoManager.GetInstance().getSelectMax() == 1)
		{
			if(PhotoManager.GetInstance().GetSelected().size() != 0)
			{
				setResult(RESULT_OK);
				finish();
			}
		}else
		{
			chosePhotos.updateView();
		}
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
		} else if (item.getItemId() == R.id.action_camera) {
			if (PhotoManager.GetInstance().GetSelected().size() >= PhotoManager
					.GetInstance().getSelectMax()) {
				String text = getResources().getString(
						R.string.select_photo_limit,
						PhotoManager.GetInstance().getSelectMax());
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				return false;
			}

			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_PHOTO,
							Constants.EVENT_PHOTO_CAMERA, null, null).build());
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, SRC_CAMERA);
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(RESULT_OK);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AFLog.d("gallery: " + resultCode);
		if (resultCode == Activity.RESULT_CANCELED)
			return;

		System.gc();
		if (requestCode == SRC_CAMERA) {
			final Bitmap tmpBitmap = (Bitmap) data.getExtras().get("data");
			handler.post(new Runnable() {
				@Override
				public void run() {
					String fileStr = AFAppWrapper.GetInstance().GetApp()
							.GetConf().TmpDir
							+ File.separator
							+ "tmp_"
							+ new Date().getTime()
							+ ".jpg";
					FileUtils.saveBitmap2SD(tmpBitmap, fileStr);

					Message msg = handler.obtainMessage();
					msg.what = MSG_SAVE_BITMAP;
					Bundle d = new Bundle();
					d.putString("file_path", fileStr);
					msg.setData(d);
					handler.sendMessage(msg);

				}
			});
		} else if (requestCode == SRC_GALLARY) {
			Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				AFPhotoData d = new AFPhotoData();
				d.setPath(picturePath);
				d.setThumbPath(picturePath);
				PhotoManager.GetInstance().addPhoto(d);
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

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int pos) {
		// TODO Auto-generated method stub
		AFLog.d("page select: " + pos);
	}

	public void clickSystemGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, SRC_GALLARY);
	}

}
