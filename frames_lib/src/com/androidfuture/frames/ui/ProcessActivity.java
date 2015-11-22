package com.androidfuture.frames.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.androidfuture.cacheimage.Picture;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.AFApp;
import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FrameCell;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.data.LocalFrameData;
import com.androidfuture.frames.service.FrameManager;
import com.androidfuture.photo.picker.PhotoChooseActivity;
import com.androidfuture.photo.picker.PhotoManager;
import com.androidfuture.set.SetActivity;
import com.androidfuture.statistic.PopService;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.StatisticTool;
import com.androidfuture.tools.StringUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ProcessActivity extends FragmentActivity implements
		OnClickListener {

	public static final int SRC_GALLERY = 102;
	public static final int FRAME = 101;

	private static final int REQUEST_FRAME = 104;

	public static final int MSG_FILE_FINISH = 201;

	public static final int MSG_FILE_PROGRESS = 202;

	public static final int MSG_FILE_CANCEL = 203;

	public static final int MSG_FILE_FAIL = 204;

	public static final int MSG_FILE_START = 205;

	public static final int MSG_PUSH_APP = 206;

	public static final int MSG_SAVE_BITMAP = 207;

	public static final int MSG_LOAD_BITMAP = 208;

	public static final int MSG_HIDDEN_TOOL = 209;

	public static final int MSG_WRITE_FRAME = 210;

	public static final int MSG_POP_SERVICE = 211;

	public static final int MSG_DOWNLOAD_PHOTO = 212;

	public static final int NO_SELECT = -1;
	public static final String KEY_PROGRESS = "key_progress";

	private int fileSize;

	private int downLoadFileSize;

	private boolean mCancel;

	private ImageView frameImageView;

	private ImageButton btnFilter;

	// private FrameChooseView frameChooseView;

	//private AdView adView;

	private FrameData newFrameData;

	private ProgressDialog progressDialog;
	private ProgressDialog simpleProgressDialog;

	private boolean fromFrameChoose;

	private boolean isFilterShow;

	private boolean isFiltering;

	private AFFrameView canvasView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int select = 0;
		AFLog.i("before init view Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		FrameData frame = null;
		if (savedInstanceState == null) {
			ArrayList<LocalFrameData> frames = AFAppWrapper.GetInstance()
					.GetApp().GetLocalFrames();
			frame = getIntent().getParcelableExtra("data");
		} else {
			AFLog.d("come to front");
			frame = (FrameData) savedInstanceState.getParcelable("frame");

			select = savedInstanceState.getInt("select");

		}
		AFLog.i("after init view Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		// getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		isFilterShow = false;
		btnFilter = (ImageButton) findViewById(R.id.btn_filter);
		btnFilter.setOnClickListener(this);

		AFLog.i("set content view Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		// findViewById(R.id.btn_rotato_left).setOnClickListener(this);
		// findViewById(R.id.btn_rotato_right).setOnClickListener(this);
		// findViewById(R.id.frame_choose_btn).setOnClickListener(this);
		findViewById(R.id.process_add).setOnClickListener(this);
		findViewById(R.id.process_frame).setOnClickListener(this);
		findViewById(R.id.process_rotate).setOnClickListener(this);
		findViewById(R.id.rotate_left).setOnClickListener(this);
		findViewById(R.id.rotate_right).setOnClickListener(this);

		canvasView = (AFFrameView) findViewById(R.id.content_wrap);
		canvasView.setSelect(select);

		setFrame(frame, true);
		AFLog.i("before build view Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		AFLog.i("after build view Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		/*
        adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		if (ConfigManager.GetInstance().getmAdInfo()!=null && ConfigManager.GetInstance().getmAdInfo().isEnableBanner())
		{
			adView.setAdUnitId(ConfigManager.GetInstance().getmAdInfo().getBannerAdId());
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}

		*/
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adlayout);
		adLayout.setVisibility(View.GONE);
		/*
		adLayout.setBackgroundColor(getResources().getColor(R.color.ad_bg));
		LinearLayout.LayoutParams layoutParam = new android.widget.LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);

		layoutParam.gravity = Gravity.CENTER;
		adLayout.addView(adView, layoutParam);
        */
		PopService.getService(ProcessActivity.this).doPop();
		
		AFLog.i("after create Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		LoadImageFilter();

		findViewById(R.id.main_footer_bar).bringToFront();

		AFLog.d("after pop");

		Tracker tracker = ((AFApp)getApplication()).getDefaultTracker();
		tracker.setScreenName(getClass().getName());
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
	@Override
	protected void onStop() {

		super.onStop();
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelable("frame", canvasView.getFrameData());
		savedInstanceState.putInt("select", canvasView.getSelect());
	}

	public void onResume() {
		super.onResume();
		AFLog.d("cur Frame: " + newFrameData);
		if (!fromFrameChoose && newFrameData != null) {
			/*
			 * setFrame(curFrameData,false); for (FrameCell cell :
			 * curFrameData.getmFrameCells()) { if (cell != null &&
			 * cell.getPhotoFile() != null && cell.getPhoto() == null) { new
			 * ReadThread(cell).start(); } }
			 */
		}
		fromFrameChoose = false;

	}

	public void onDestroy() {

		if (frameImageView != null)
			frameImageView.setImageBitmap(null);
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
		canvasView.destroy();
		System.gc();
		AFLog.i("after destroy, Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();

		canvasView.pause();
		AFLog.i("after pause, Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
	}

	/*
	 * private void openFrameChoose() { isFramesChooseOpen = true;
	 * 
	 * Animation animation = AnimationUtils.loadAnimation(this,
	 * R.anim.frame_open); frameChooseView.startAnimation(animation);
	 * frameChooseView.bringToFront(); headerBar.bringToFront();
	 * frameChooseView.setVisibility(View.VISIBLE); }
	 * 
	 * public void closeFrameChoose() { if (!isFramesChooseOpen) return;
	 * isFramesChooseOpen = false; Animation animation =
	 * AnimationUtils.loadAnimation(this, R.anim.frame_close);
	 * animation.setAnimationListener(new AnimationListener() {
	 * 
	 * public void onAnimationEnd(Animation arg0) {
	 * frameChooseView.setVisibility(View.GONE);
	 * 
	 * }
	 * 
	 * @Override public void onAnimationRepeat(Animation animation) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onAnimationStart(Animation animation) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * }); frameChooseView.startAnimation(animation);
	 * 
	 * }
	 */

	@Override
	public void onClick(View v) {
		if (v.getId() != R.id.btn_filter)
			hideFilter();
		
		if (v.getId() != R.id.process_rotate 
				&& v.getId() != R.id.rotate_left
				&& v.getId() != R.id.rotate_right)
			findViewById(R.id.rotate_wrap).setVisibility(View.GONE);
	
		if (v.getId() == R.id.btn_filter) {
			AFLog.d("click btn filter");

			if (!isFilterShow) {
				showFilter();
				((AFApp)getApplication()).getDefaultTracker()
						.send(new HitBuilders.EventBuilder()
								.setCategory(Constants.EVENT_CAT_PROCESS)
								.setAction(Constants.EVENT_PROCESS_OPEN_FILTER)
								.build());

			} else {
				hideFilter();
			}
			
			
		} else if (v.getId() == R.id.process_frame) {
			Intent intent = new Intent();
			intent.putExtra("switch", true);
			intent.setClass(this, FrameChooseActivity.class);
			startActivityForResult(intent, FRAME);
			((AFApp)getApplication()).getDefaultTracker()
					.send(new HitBuilders.EventBuilder()
							.setCategory(Constants.EVENT_CAT_PROCESS)
							.setAction(Constants.EVENT_FRAME_SWITCH)
							.build());
		}else if (v.getId() == R.id.process_add)
		{
			addPhoto();
			((AFApp)getApplication()).getDefaultTracker()
					.send(new HitBuilders.EventBuilder()
							.setCategory(Constants.EVENT_CAT_PROCESS)
							.setAction(Constants.EVENT_PROCESS_ADD_PHOTO)
							.build());

		}
		else if (v.getId() == R.id.process_rotate) {
			if (findViewById(R.id.rotate_wrap).getVisibility() == View.GONE) {
				findViewById(R.id.rotate_wrap).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.rotate_wrap).setVisibility(View.GONE);
			}
			((AFApp)getApplication()).getDefaultTracker()
					.send(new HitBuilders.EventBuilder()
							.setCategory(Constants.EVENT_CAT_PROCESS)
							.setAction(Constants.EVENT_PROCESS_OPEN_ROTATE)
							.build());

		} else if (v.getId() == R.id.rotate_left) {
			canvasView.rotate(-10);
			((AFApp)getApplication()).getDefaultTracker()
					.send(new HitBuilders.EventBuilder()
							.setCategory(Constants.EVENT_CAT_PROCESS)
							.setAction(Constants.EVENT_PROCESS_ROTATE)
							.build());
		} else if (v.getId() == R.id.rotate_right) {
			canvasView.rotate(10);
			((AFApp)getApplication()).getDefaultTracker()
					.send(new HitBuilders.EventBuilder()
							.setCategory(Constants.EVENT_CAT_PROCESS)
							.setAction(Constants.EVENT_PROCESS_ROTATE)
							.build());
		}
	}

	private void addPhoto() {
		if (canvasView.getFrameData() == null || canvasView.getFrameData().getmFrameCells() == null)
			return;
		PhotoManager.GetInstance().setSelectMax(canvasView.getFrameData().getmFrameCells().length);

		Intent intent = new Intent(ProcessActivity.this,
				PhotoChooseActivity.class);
		startActivityForResult(intent, SRC_GALLERY);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AFLog.d("gallery: " + resultCode);
		if (resultCode == RESULT_CANCELED)
			return;

		System.gc();

		switch (requestCode) {
		case SRC_GALLERY:
			ArrayList<AFPhotoData> photos = new ArrayList<AFPhotoData>();
			int num = 0;
			for (AFPhotoData photo : PhotoManager.GetInstance().GetSelected()) {
				if (photo.getPath() == null && photo.getUrl() != null)
					photos.add(photo);
			}

			if (!photos.isEmpty()) {
				showSimpleProgress();
				new DownloadImageThread(photos).start();
			} else {
				canvasView.setPhotos(PhotoManager.GetInstance().GetSelected());
			}
			break;
		case FRAME:
			FrameData frame = data.getParcelableExtra("data");
			setFrame(frame,false);
			break;
		}

		
	}

	/*
	 * public void chooseFrame(FrameData frame) { setFrame(frame, true);
	 * closeFrameChoose(); }
	 */

	public void setFrame(FrameData frame, boolean showProgress) {
		newFrameData = frame;
		if (frame.isLocal()) {
			canvasView.setFrameData(frame);
		} else {

			startLoad(showProgress);
		}
		canvasView.setSelect(0);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		if (item.getItemId() == android.R.id.home) {
			
			return true;
		} else if (item.getItemId() == R.id.menu_feedback) {
			//intent = new Intent(this, FeedBack.class);
			//startActivity(intent);
			
			return true;
		} else if (item.getItemId() == R.id.menu_setting) {
			intent = new Intent(this, SetActivity.class);
			startActivity(intent);
			return true;
		} else if (item.getItemId() == R.id.menu_ok) {
			if (canvasView.getFrameData() == null)
				return false;
			hideFilter();
			findViewById(R.id.rotate_wrap).setVisibility(View.GONE);
			int neededPhoto = calNeedPhoto();
			if (neededPhoto == 0) {

				// post use statistic to server
				// postStatic();
				StatisticTool.postToServer(this, Constants.ACT_PROCESS);
				intent = new Intent(this, PreviewActivity.class);
				Bitmap bitmap = getBitmap();
				FrameManager.GetInstance().setPreviewBitmap(bitmap);
				/*
				intent.putExtra("frame", this.canvasView.getFrameData());
				intent.putExtra("frameScale", canvasView.getFrameScale());
				*/
				
				startActivity(intent);
			} else {
				String hint = getResources().getString(
						R.string.hint_select_photo);
				String hintStr = String.format(hint, neededPhoto);
				Toast.makeText(this, hintStr, Toast.LENGTH_SHORT).show();

			}
		}
		return true;
	}

	private void showSimpleProgress() {
		if (simpleProgressDialog == null) {
			simpleProgressDialog = new ProgressDialog(this);
			simpleProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		simpleProgressDialog.show();
	}

	private void hideSimpleProgress() {
		if (simpleProgressDialog != null)
			simpleProgressDialog.dismiss();
	}
	public Bitmap getBitmap() {
		Bitmap bitmap = Bitmap.createBitmap(canvasView.getWidth(),
				canvasView.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		canvasView.draw(c);
		return bitmap;
	}
	/**
	 * show loading progress
	 */
	private void startLoad(boolean showProgress) {
		downLoadFileSize = 0;
		fileSize = 0;
		handler.removeMessages(MSG_FILE_PROGRESS);

		if (showProgress) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(this);
				progressDialog
						.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				String str = this.getResources().getString(
						R.string.download_progress);
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						mCancel = true;

					}
				});

				progressDialog.setMessage(str);

			}

			progressDialog.setProgress(0);
			progressDialog.setMax(1000);
			progressDialog.show();
			progressDialog.setProgressNumberFormat("%1d/%2d KB");
		}

		new DownloadThread().start();
	}

	private void stopLoad() {
		if (progressDialog != null) {
			progressDialog.setProgress(0);
			if (progressDialog.isShowing())
				progressDialog.dismiss();
		}
	}

	class FrameOnSeekBarChangeListener implements
			android.widget.SeekBar.OnSeekBarChangeListener {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (!fromUser)
				return;
			canvasView.resize((float) (0.5 + (float) progress / 100));

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FILE_START:
				if (progressDialog != null)
					progressDialog.setMax(fileSize / 1000);
				break;
			case MSG_FILE_FINISH:

				stopLoad();
				String fileFullPath = AFAppWrapper.GetInstance().GetApp()
						.GetConf().CacheDir
						+ File.separator
						+ StringUtils.getAFFileName(newFrameData.getFrameUrl());

				Bitmap frameBitmap = Picture.getBitmapFromFile(fileFullPath, 2);
				if (frameBitmap == null) {
					String str = getResources().getString(
							R.string.fail_load_frame);
					Toast.makeText(ProcessActivity.this, str,
							Toast.LENGTH_SHORT).show();
					File file = new File(fileFullPath);
					file.delete();
					return;
				}

				newFrameData.setFrameBitmap(frameBitmap);
				canvasView.setFrameData(newFrameData);
				break;
			case MSG_FILE_PROGRESS: {
				// Bundle data = msg.getData();
				if (progressDialog != null)
					progressDialog.setProgress(downLoadFileSize / 1000);
			}
				break;
			case MSG_FILE_CANCEL:
				String str = ProcessActivity.this.getResources().getString(
						R.string.download_cancel);
				Toast.makeText(ProcessActivity.this, str, Toast.LENGTH_SHORT)
						.show();

				// curFrameData = null;
				break;
			case MSG_PUSH_APP:

				break;

			case MSG_LOAD_BITMAP:
				// contentView.invalidate();

				break;
			case MSG_SAVE_BITMAP:
				canvasView.setPhoto(msg.getData().getString("file_path"));
				break;
			case MSG_POP_SERVICE:
				PopService.getService(ProcessActivity.this).doPop();
				break;
			case MSG_DOWNLOAD_PHOTO:
				hideSimpleProgress();
				canvasView.setPhotos(PhotoManager.GetInstance().GetSelected());
			}
		}
	};

	// download file
	public class DownloadThread extends Thread {
		public void run() {
			try {
				if (newFrameData == null) {
					handler.sendEmptyMessage(MSG_FILE_FAIL);
				} else {
					downFile(newFrameData.getFrameUrl());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(MSG_FILE_FAIL);
			}
		}
	}

	public void downFile(String url) throws IOException {
		String fileName = StringUtils.getAFFileName(url);
		File file = new File(
				AFAppWrapper.GetInstance().GetApp().GetConf().CacheDir,
				fileName);
		if (file.exists()) {

			handler.sendEmptyMessage(MSG_FILE_FINISH);
			return;
		}

		URL myURL = new URL(url);

		AFLog.d("begin download:" + url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		this.fileSize = conn.getContentLength();
		InputStream is = conn.getInputStream();
		handler.sendEmptyMessage(MSG_FILE_START);

		AFLog.d("filesize: " + fileSize);
		AFLog.d("input stream:" + (is == null));
		if (this.fileSize <= 0 || (is == null)) {
			handler.sendEmptyMessage(MSG_FILE_FAIL);
			return;
		}
		AFLog.d("begin to download.");
		FileOutputStream fos = new FileOutputStream(file);
		// save
		byte buf[] = new byte[1024];
		downLoadFileSize = 0;
		mCancel = false;

		while (!mCancel) {
			// repeat read
			int numread = is.read(buf);
			if (numread == -1) {
				break;
			}
			fos.write(buf, 0, numread);
			downLoadFileSize += numread;
			Message msg = handler.obtainMessage();
			msg.what = MSG_FILE_PROGRESS;
			handler.sendMessage(msg);
		}
		AFLog.d("finish download");
		fos.close();
		if (!mCancel) {
			handler.sendEmptyMessage(MSG_FILE_FINISH);// notif finish
		} else {
			handler.sendEmptyMessage(MSG_FILE_CANCEL);
			AFLog.d("cancel download: " + fileName);

			if (file.delete()) {
				AFLog.d("remove success");
			} else {
				AFLog.d("remove fail");
			}
		}

		try {
			is.close();
		} catch (Exception ex) {
			Log.e("tag", "error: " + ex.getMessage(), ex);
		}

	}

	public void hiddenTool() {
		// findViewById(R.id.size_button_wrap).setVisibility(View.INVISIBLE);
		findViewById(R.id.main_footer_bar).setVisibility(View.INVISIBLE);
	}

	public void showTool() {
		// findViewById(R.id.size_button_wrap).setVisibility(View.VISIBLE);
		findViewById(R.id.main_footer_bar).setVisibility(View.VISIBLE);
		resetTime();
	}

	public void resetTime() {
		handler.removeMessages(MSG_HIDDEN_TOOL);
		Message msg = handler.obtainMessage(MSG_HIDDEN_TOOL);

		handler.sendMessageDelayed(msg, 6000);
	}

	public void showSelectFrameHint() {
		Toast.makeText(this, R.string.select_frame, Toast.LENGTH_SHORT).show();
	}

	/*
	 * // post statistic to server private void postStatic() { new
	 * PostThread(1).start();
	 * 
	 * }
	 * 
	 * private class PostThread extends Thread { int mAct;
	 * 
	 * public PostThread(int act) { mAct = act; }
	 * 
	 * public void run() { if (canvasView.getFrameData() == null) return;
	 * StringBuilder bld = new StringBuilder(); bld.append(Constants.URLRoot);
	 * bld.append("/post.php"); bld.append("?act="); bld.append(mAct);
	 * bld.append("&frame_id=");
	 * bld.append(canvasView.getFrameData().getFrameId()); // Log.d(TAG,"url:" +
	 * bld.toString()); HttpUtils.request(bld.toString()); //
	 * StatisticTool.addShare(ProcessActivity.this);
	 * 
	 * } }
	 */

	public void hiddenViews() {

	}

	private void showFilter() {

		if (!isFilterShow) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.toolbar_in);
			findViewById(R.id.filter_wrap).setVisibility(View.VISIBLE);
			findViewById(R.id.filter_wrap).startAnimation(animation);
		
			
			isFilterShow = true;
		}
	}

	public void hideFilter() {
		if (isFilterShow) {
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.toolbar_out);
			findViewById(R.id.filter_wrap).startAnimation(animation);
			animation.setAnimationListener(new AnimationListener(){

				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
					findViewById(R.id.filter_wrap).setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
					
				}});
			isFilterShow = false;
		}

	}

	private void LoadImageFilter() {
		final AFFilterChooseView filterChooseView = new AFFilterChooseView(this);
		filterChooseView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long id) {
						if (!isFiltering) {
							// IImageFilter filter = (IImageFilter)
							// filterChooseView.getItem(position);
							if (!canvasView.checkPhotoOK()) {
								String hint = getResources().getString(
										R.string.hint_select_photo);
								String hintStr = String.format(hint, 1);
								Toast.makeText(ProcessActivity.this, hintStr,
										Toast.LENGTH_SHORT).show();
								return;
							} else {
								canvasView.setFilter(position);
								((AFApp)getApplication()).getDefaultTracker()
										.send(new HitBuilders.EventBuilder()
												.setCategory(Constants.EVENT_CAT_PROCESS)
												.setAction(Constants.EVENT_PROCESS_FILTER)
												.setLabel("Filter:" + position)
												.build());
							}
						}
					}
				});
		LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		((HorizontalScrollView) findViewById(R.id.filter_wrap)).addView(
				filterChooseView, mlp);
	}

	public void showIndicator() {

		findViewById(R.id.progress_indicator).setVisibility(View.VISIBLE);
	}

	public void hideIndicator() {
		findViewById(R.id.progress_indicator).setVisibility(View.GONE);
	}

	int calNeedPhoto() {
		int neededPhoto = canvasView.getFrameData().getmFrameCells().length;
		for (FrameCell cell : this.canvasView.getFrameData().getmFrameCells()) {
			if (cell.getPhoto() != null) {
				neededPhoto--;
			}
		}
		return neededPhoto;
	}

	private class DownloadImageThread extends Thread {
		private ArrayList<AFPhotoData> mData;

		public DownloadImageThread(ArrayList<AFPhotoData> data) {
			mData = data;
		}

		public void run() {
			if (mData == null || mData.isEmpty())
				return;

			for (AFPhotoData photo : mData) {
				String path = StringUtils.getAFFilePath(photo.getUrl());
				try {
					Picture.loadFromUrl(photo.getUrl(), path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				photo.setPath(path);

			}

			handler.sendEmptyMessage(MSG_DOWNLOAD_PHOTO);

		}
	}
	
public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										finish();
									}
								}).setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								});
				builder.setMessage(R.string.exit_msg);
				builder.create().show();
		}
		return super.onKeyDown(keyCode, event);
}

}