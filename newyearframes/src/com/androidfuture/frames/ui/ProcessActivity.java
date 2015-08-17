package com.androidfuture.frames.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.cacheimage.Picture;
import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.LocalFramesConfig;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.facebook.PhotoExplore;
import com.androidfuture.frames.data.AFAppPush;
import com.androidfuture.frames.data.AFAppPushParser;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.tools.FileUtils;
import com.androidfuture.frames.tools.HttpUtils;
import com.androidfuture.network.AFData;
import com.androidfuture.network.WWRequestTask;
import com.androidfuture.network.WWRequestTask.RequestTaskListener;
import com.androidfuture.statistic.StatisticTool;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.StringUtils;
import com.appdao.android.feedback.FeedBack;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


public class ProcessActivity extends FragmentActivity implements
		OnSeekBarChangeListener, OnTouchListener, OnClickListener, RequestTaskListener {


	private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;

	private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;

	private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;

	private static final int SRC_CAMERA = 101;

	private static final int SRC_GALLERY = 102;

	private static final int SRC_FACEBOOK = 103;

	public static final int MSG_FILE_FINISH = 201;

	public static final int MSG_FILE_PROGRESS = 202;

	public static final int MSG_FILE_CANCEL = 203;

	public static final int MSG_FILE_FAIL = 204;

	public static final int MSG_FILE_START = 205;

	public static final int MSG_PUSH_APP = 206;

	public static final String KEY_PROGRESS = "key_progress";

	private int fileSize;

	private int downLoadFileSize;

	private boolean mCancel;

	private String mImageUrl;

	private static final int maxSize = 128 * 1 << 10;// 128K

	private float rate;

	private int requirePhotoNum;

	private int photosNum;

	// private Bitmap origBitmap;
	private Uri photoUri;

	private Bitmap curBitmap;

	private Bitmap frameBitmap;

	private float origWidth;

	private float origHeight;

	private float curWidth;

	private float curHeight;

	private float initWidth;

	private float initHeight;

	private float frameOrigWidth;

	private float frameOrigHeight;

	private float frameWidth;

	private float frameHeight;

	private float xCenter;

	private float yCenter;

	private ImageView curImageView;

	private RelativeLayout curFrameLayout;

	private ImageView frameImageView;

	private SeekBar sizeBar;

	private float touchStartX;

	private float touchStartY;

	private float angle;

	private float angleStep;

	private float initRotateWidth;

	private float initRotateHeight;

	private float curRotateWidth;

	private float curRotateHeight;

	private boolean isFramesChooseOpen;

	private LinearLayout framesWrap;

	private float maxWidth;

	private float maxHeight;

	ArrayList<FrameData> frameList;

	private AdView adView;

	private FrameData curFrameData;

	private ViewGroup headerBar;

	private ProgressDialog progressDialog;

	private final static int VALUE_POST_USE = 1;


	// private String frameFileName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Log.d(TAG,"on create");

		photosNum = 0;
		rate = 1;

		AFLog.i(
				"before init view Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		initView();

		if (savedInstanceState == null) {
			curFrameData = LocalFramesConfig.LocalFrames[new Random()
					.nextInt(LocalFramesConfig.LocalFrames.length)];
		} else {
			AFLog.d( "come to front");
			curFrameData = (FrameData) savedInstanceState
					.getParcelable("frame");
			photoUri = (Uri) savedInstanceState.getParcelable("photo");
			// Log.d(TAG," " + curFrameData);
		}
		AFLog.i(
				"after init view Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		sizeBar = (SeekBar) findViewById(R.id.seek_adjust_size);
		sizeBar.setOnSeekBarChangeListener(this);
		AFLog.i(
				"set content view Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		findViewById(R.id.btn_rotato_left).setOnClickListener(this);
		findViewById(R.id.btn_rotato_right).setOnClickListener(this);
		findViewById(R.id.frame_choose_btn).setOnClickListener(this);
		findViewById(R.id.share_btn).setOnClickListener(this);
		findViewById(R.id.open_btn).setOnClickListener(this);

		headerBar = (ViewGroup) findViewById(R.id.main_header_bar);

		AFLog.i(
				"before build view Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		buildFrameChoose();
		AFLog.i(
				"after build view Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		adView = new AdView(this, AdSize.BANNER, Constants.MY_AD_ID);
		// Add the adView to it
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adlayout);
		adLayout.addView(adView);
		// String str = this.getResources().getString(R.string.loading_frames);

		// Initiate a generic request to load it with an ad
		adView.loadAd(new AdRequest());

		headerBar.bringToFront();
		if (StatisticTool.isFirstLogin(this)) {
			View hintView = findViewById(R.id.hint);
			hintView.setVisibility(View.VISIBLE);
			hintView.bringToFront();
			hintView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					v.setVisibility(View.GONE);
					StatisticTool.recordFirst(ProcessActivity.this);
				}

			});

		} else {
			StatisticTool.popRatingDlg(this);
			new PostStaticThread().start();

		}
		AFLog.i(
				"after create Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		pushApp();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save away the original text, so we still have it if the activity
		// needs to be killed while paused.
		savedInstanceState.putParcelable("frame", curFrameData);
		savedInstanceState.putParcelable("photo", photoUri);
	}

	public void onResume() {

		AFLog.d( "cur Frame: " + curFrameData);
		if (curFrameData != null)
			setFrame(curFrameData);
		if (photoUri != null)
			curBitmap = FileUtils.readBitmapFromSD(getContentResolver(),
					photoUri);
		// set currenct select bitmap
		if (curBitmap != null) {
			setPhoto();
		}
		super.onResume();
	}

	public void onDestroy() {
		if (curImageView != null)
			curImageView.setImageBitmap(null);
		if (frameImageView != null)
			frameImageView.setImageBitmap(null);

		if (frameBitmap != null)
			frameBitmap.recycle();

		if (curBitmap != null)
			curBitmap.recycle();
		frameBitmap = null;

		curBitmap = null;
		System.gc();
		AFLog.i(
				"after destroy, Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		super.onDestroy();
	}

	protected void onPause() {
		super.onPause();
		if (curImageView != null) {
			curImageView.setImageBitmap(null);
		}
		if (frameImageView != null)
			frameImageView.setImageBitmap(null);

		if (frameBitmap != null)
			frameBitmap.recycle();
		if (curBitmap != null)
			curBitmap.recycle();
		frameBitmap = null;
		curBitmap = null;
		// photosNum = 0;
		// photoUri = null;
		System.gc();
		AFLog.i(
				"after pause, Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
	}

	@TargetApi(4)
	private void initView() {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		float sreenWidth = metric.widthPixels;
		float sreenHeight = metric.heightPixels;
		float density = metric.density;
		float headBarHeight = 50 * density;
		float footerBarHeight = 50 * density;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(displayMetrics);

		int statusBarHeight;

		switch (displayMetrics.densityDpi) {
		case DisplayMetrics.DENSITY_HIGH:
			statusBarHeight = HIGH_DPI_STATUS_BAR_HEIGHT;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
			break;
		case DisplayMetrics.DENSITY_LOW:
			statusBarHeight = LOW_DPI_STATUS_BAR_HEIGHT;
			break;
		default:
			statusBarHeight = MEDIUM_DPI_STATUS_BAR_HEIGHT;
		}

		maxWidth = sreenWidth;
		maxHeight = sreenHeight - headBarHeight - footerBarHeight
				- statusBarHeight;
		AFLog.d("Max Heihgt:"+maxHeight);
	}

	private void layoutImageView() {
		if (curFrameLayout == null)
			return;
		RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) curFrameLayout
				.getLayoutParams();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) curImageView
				.getLayoutParams();
		curWidth = (int) (initWidth * rate);
		curHeight = (int) (initHeight * rate);
		curRotateWidth = (int) (initRotateWidth * rate);
		curRotateHeight = (int) (initRotateHeight * rate);

		params.width = (int) curRotateWidth;
		params.height = (int) curRotateHeight;
		params.leftMargin = (int) (xCenter - curRotateWidth / 2);
		params.topMargin = (int) (yCenter - curRotateHeight / 2);
		params.rightMargin = (int) (layout.width - xCenter - curRotateWidth / 2);
		params.bottomMargin = (int) (layout.height - yCenter - curRotateHeight / 2);
		// params.setMargins((origWidth - curWidth)/2, (origWidth - curWidth)/2,
		// (origHeight - curHeight)/2,(origHeight - curHeight)/2);
		curImageView.setLayoutParams(params);

		curImageView.invalidate();
		// curFrameLayout.setLayoutParams(params);

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if (!fromUser)
			return;

		rate = (float) (1 + (float) (progress - 50) * 1.8 / 100);
		layoutImageView();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (v == frameImageView) {
			if (isFramesChooseOpen) {
				closeFrameChoose();

			}
			return false;
		}

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			if (isFramesChooseOpen) {
				closeFrameChoose();
				return false;
			}
			touchStartX = event.getX();
			touchStartY = event.getY();
			// Log.d(TAG,"--x: " + event.getX() + " y: " + event.getY() );
			break;
		case MotionEvent.ACTION_MOVE:
			float dX = event.getX() - touchStartX;
			float dY = event.getY() - touchStartY;
			if(dX*dX + dY*dY > 6)
			{
				xCenter = (int) (xCenter + dX);
				yCenter = (int) (yCenter + dY);
				touchStartX = event.getX();
				touchStartY = event.getY();
				layoutImageView();
			}
			break;
		case MotionEvent.ACTION_UP:
			touchStartX = event.getX();
				touchStartY = event.getY();	
		}
		return true;
	}

	private void rotateImage() {
		if (curImageView == null || curBitmap == null)
			return;

		Matrix localMatrix = new Matrix();
		localMatrix.postRotate(angleStep);

		curImageView.setImageBitmap(null);

		AFLog.d( "before: " + initHeight + " width: " + initWidth + ": "
				+ curBitmap.getHeight() + ": " + curBitmap.getWidth());
		double redians = angle * 3.14159265 / 180;
		initRotateHeight = (float) Math.max(
				Math.abs(initHeight * Math.cos(redians) - initWidth
						* Math.sin(redians)),
				Math.abs(initHeight * Math.cos(redians) + initWidth
						* Math.sin(redians)));
		float temp = (float) Math.min(
				Math.abs(initHeight * Math.cos(redians) - initWidth
						* Math.sin(redians)),
				Math.abs(initHeight * Math.cos(redians) + initWidth
						* Math.sin(redians)));

		initRotateWidth = (float) Math.sqrt(initHeight * initHeight + initWidth
				* initWidth - temp * temp);

		Bitmap tmpBitmap = Bitmap.createBitmap(curBitmap, 0, 0,
				curBitmap.getWidth(), curBitmap.getHeight(), localMatrix, true);

		float x = tmpBitmap.getWidth() > initRotateWidth ? tmpBitmap.getWidth()
				/ 2 - initRotateWidth / 2 : 0;
		float y = tmpBitmap.getHeight() > initRotateHeight ? tmpBitmap
				.getHeight() / 2 - initRotateHeight / 2 : 0;
		float w = tmpBitmap.getWidth() > initRotateWidth ? initRotateWidth
				: tmpBitmap.getWidth();
		float h = tmpBitmap.getHeight() > initRotateHeight ? initRotateHeight
				: tmpBitmap.getHeight();
		AFLog.d(
				"after size: " + initRotateWidth + ": " + tmpBitmap.getWidth()
						+ " height: " + initRotateHeight + ":"
						+ tmpBitmap.getHeight());

		curBitmap = Bitmap.createBitmap(tmpBitmap, (int) x, (int) y, (int) w,
				(int) h);

		if (tmpBitmap != curBitmap && tmpBitmap != null) {
			AFLog.d( "recycle");
			tmpBitmap.recycle();
			tmpBitmap = null;
			System.gc();
		}

		curImageView.setImageBitmap(curBitmap);

		AFLog.i(
				"after rotate Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		layoutImageView();

	}

	@Override
	public void onClick(View v) {
		if (isFramesChooseOpen) {
			closeFrameChoose();
			return;
		}
		switch (v.getId()) {
		case R.id.btn_rotato_left:

			angle += 10;
			angleStep = 10;
			rotateImage();
			break;
		case R.id.btn_rotato_right:
			angle -= 10;
			angleStep = -10;
			rotateImage();
			break;
		case R.id.frame_choose_btn:
			if (!isFramesChooseOpen) {
				v.setEnabled(false);
				openFrameChoose();
				v.setEnabled(true);
			} else {
				v.setEnabled(false);
				closeFrameChoose();
				v.setEnabled(true);
			}
			break;
		case R.id.open_btn: {
			Resources rs = this.getResources();
			final CharSequence[] items = { rs.getString(R.string.gallery),
					rs.getString(R.string.camera), "Facebook" };
			final AlertDialog dlg = new AlertDialog.Builder(this)
					.setTitle(R.string.import_photos)
					.setItems(items, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int item) {
							// Camera
							if (item == 1) {
								
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								startActivityForResult(intent, SRC_CAMERA);
								dialog.dismiss();
								 StatisticTool.postToServer(ProcessActivity.this, Constants.ACT_FROM_CAMERA);

							} else if (item == 0) {
								// gallery
								Intent intent = new Intent(
										Intent.ACTION_GET_CONTENT);
								intent.addCategory(Intent.CATEGORY_OPENABLE);
								intent.putExtra("scale", true);
								intent.setType("image/*");
								// Log.d(TAG,"get bitmap");
								startActivityForResult(intent, SRC_GALLERY);
								dialog.dismiss();
								 StatisticTool.postToServer(ProcessActivity.this, Constants.ACT_FROM_ALBUM);
							} else {
								Intent intent = new Intent(
										ProcessActivity.this,
										PhotoExplore.class);
								startActivityForResult(intent, SRC_FACEBOOK);
								dialog.dismiss();
								 StatisticTool.postToServer(ProcessActivity.this, Constants.ACT_FROM_FACEBOOK);
							}
						}
					}).create();
			dlg.show();

		}
			break;
		case R.id.share_btn: {
			if (photosNum != requirePhotoNum) {
				String hint = getResources().getString(
						R.string.hint_select_photo);
				String hintStr = String.format(hint, requirePhotoNum);
				Toast.makeText(this, hintStr, Toast.LENGTH_SHORT).show();
				return;
			}

			// post use statistic to server
			postStatic();
			StatisticTool.postToServer(this, Constants.ACT_PROCESS);
			RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) curFrameLayout
					.getLayoutParams();
			float left = (int) (xCenter - curRotateWidth / 2);
			float top = (int) (yCenter - curRotateHeight / 2);
			float right = (int) (layout.width - xCenter - curRotateWidth / 2);
			float bottom = (int) (layout.height - yCenter - curRotateHeight / 2);
			float rate = frameOrigWidth / frameWidth;

			Intent intent = new Intent(this, PreviewActivity.class);
			intent.putExtra("left", left);
			intent.putExtra("right", right);
			intent.putExtra("top", top);
			intent.putExtra("bottom", bottom);

			intent.putExtra("rate", rate);
			intent.putExtra("angle", angle);
			intent.putExtra("width", curRotateWidth);
			intent.putExtra("height", curRotateHeight);
			intent.putExtra("frame_width", frameOrigWidth);
			intent.putExtra("frame_height", frameOrigHeight);
			Bundle b = new Bundle();
			b.putParcelable("frame", curFrameData);
			b.putParcelable("photo", photoUri);
			intent.putExtra("data", b);

			startActivity(intent);

		}
			break;
		}

	}

	// post statistic to server
	private void postStatic() {
		new PostThread(VALUE_POST_USE).start();

	}

	private class PostStaticThread extends Thread {
		public void run() {
			StatisticTool.recordUse(ProcessActivity.this);
		}
	}

	private class PostThread extends Thread {
		int mAct;

		public PostThread(int act) {
			mAct = act;
		}

		public void run() {
			if (curFrameData == null)
				return;
			StringBuilder bld = new StringBuilder();
			bld.append(Constants.URLRoot);
			bld.append("/post.php");
			bld.append("?act=");
			bld.append(mAct);
			bld.append("&frame_id=");
			bld.append(curFrameData.getFrameId());
			// Log.d(TAG,"url:" + bld.toString());
			 HttpUtils.request(bld.toString());

			StatisticTool.addShare(ProcessActivity.this);

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Log.d(TAG,"back" + frameWidth + frameHeight);
		if (resultCode == RESULT_CANCELED)
			return;

		System.gc();

	
		switch (requestCode) {
		case SRC_GALLERY:
			photoUri = data.getData();
			break;
		case SRC_CAMERA:
			// Log.d(TAG,""+resultCode);
			if (resultCode == RESULT_OK) {
				Bitmap tmpBitmap = (Bitmap) data.getExtras().get("data");
				photoUri = Uri.fromFile(FileUtils.saveBitmap2SD(tmpBitmap,
						"tmp_" + new Date().getTime() + ".jpg"));
			}
			break;
		case SRC_FACEBOOK:
			if (resultCode == RESULT_OK) {
				Bitmap tmpBitmap = (Bitmap) data.getExtras().get("data");
				photoUri = Uri.fromFile(FileUtils.saveBitmap2SD(tmpBitmap,
						"tmp_" + new Date().getTime() + ".jpg"));
			}
			break;

		}

		if (photoUri == null) {
			return;
		}

		curBitmap = FileUtils.readBitmapFromSD(getContentResolver(), photoUri);
		// set currenct select bitmap
		if (curBitmap == null) {
			return;
		}

		if (frameBitmap != null)
			frameBitmap.recycle();
		System.gc();
		if (curFrameData.isLocal()) {

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			frameBitmap = BitmapFactory.decodeResource(this.getResources(),
					curFrameData.getFrameRes(), options);
			setFrame();
		} else {
			String fileName = FrameApp.imageCacheDir + File.separator
					+ StringUtils.getAFFileName(curFrameData.getFrameUrl());

			File file = new File(fileName);
			boolean flag = false;
			if (file.exists()) {
				frameBitmap = Picture.getBitmapFromFile(fileName, 2);
				if (frameBitmap != null) {
					flag = true;
				}
			}
			if (!flag) {
				curFrameData = LocalFramesConfig.LocalFrames[new Random()
						.nextInt(LocalFramesConfig.LocalFrames.length)];
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				frameBitmap = BitmapFactory.decodeResource(this.getResources(),
						curFrameData.getFrameRes(), options);
			}
			setFrame();
		}
		setPhoto();

	}

	private void setPhoto() {
		photosNum = 1;
		angle = 0;
		angleStep = 0;
		origWidth = curBitmap.getWidth();
		origHeight = curBitmap.getHeight();
		/*
		 * if(origWidth/frameOrigWidth>origHeight/frameOrigHeight) {
		 * 
		 * }
		 */
		AFLog.d( "origWidth: origWidth: " + origWidth + "origHeight: "
				+ origHeight + " frameWidth: " + frameWidth + " frameHeight: "
				+ frameHeight);
		if (origWidth / frameWidth > origHeight / frameHeight) {
			curWidth = frameWidth;
			curHeight = origHeight * curWidth / origWidth;
		} else {
			curHeight = frameHeight;
			curWidth = origWidth * curHeight / origHeight;
		}
		initWidth = curWidth;
		initHeight = curHeight;
		initRotateWidth = curWidth;
		initRotateHeight = curHeight;
		curRotateWidth = curWidth;
		curRotateHeight = curHeight;

		xCenter = frameWidth / 2;
		yCenter = frameHeight / 2;

		Bitmap tempBitmap = curBitmap;
		curBitmap = Bitmap.createScaledBitmap(tempBitmap, (int) curWidth,
				(int) curHeight, true);

		if (tempBitmap != null && tempBitmap != curBitmap) {
			tempBitmap.recycle();
		}
		if (curFrameLayout == null) {
			curFrameLayout = new RelativeLayout(this);

			curFrameLayout.setBackgroundColor(Color.TRANSPARENT);

			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
					(int) frameWidth, (int) frameHeight);
			params2.leftMargin = (int) ((maxWidth - frameWidth) / 2);
			params2.topMargin = (int) ((maxHeight - frameHeight) / 2);
			curFrameLayout.setLayoutParams(params2);
			RelativeLayout frameLayout = (RelativeLayout) findViewById(R.id.content_wrap);
			// frameLayout.removeAllViews();
			frameLayout.addView(curFrameLayout);
		}

		if (curImageView == null) {
			curImageView = new ImageView(this);
			curImageView.setScaleType(ScaleType.FIT_XY);

			curImageView.setOnTouchListener(this);

			curImageView.setDrawingCacheEnabled(false);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					(int) curWidth, (int) curHeight);
			curFrameLayout.addView(curImageView, params);
		}

		curImageView.setImageBitmap(curBitmap);
		frameImageView.bringToFront();

		sizeBar.setProgress(50);
		isFramesChooseOpen = false;
		// Log.d(TAG,"photo width: " + curWidth + " height: " + curHeight);
		layoutImageView();

	}

	/**
	 * calculate the frame size
	 */

	private void setFrame() {

		RelativeLayout frameLayout = (RelativeLayout) findViewById(R.id.content_wrap);
		requirePhotoNum = 1;
		// photosNum =0;
		FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.FILL_PARENT,
				FrameLayout.LayoutParams.FILL_PARENT);

		if (frameImageView == null) {
			frameImageView = new ImageView(this);
			frameImageView.setOnTouchListener(this);
			frameLayout.addView(frameImageView, frameParams);
			frameImageView.setDrawingCacheEnabled(false);
		}
		AFLog.i( "before set frame image view, Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		frameImageView.setImageBitmap(frameBitmap);
		if (frameBitmap == null)
			return;
		frameOrigWidth = frameBitmap.getWidth();
		frameOrigHeight = frameBitmap.getHeight();
		if (maxWidth / frameOrigWidth > maxHeight / frameOrigHeight) {
			frameHeight = maxHeight;
			frameWidth = frameOrigWidth * frameHeight / frameOrigHeight;
		} else {
			frameWidth = maxWidth;
			frameHeight = frameOrigHeight * frameWidth / frameOrigWidth;
		}

		AFLog.i(
				"after set frame image view, Memory Used::"
						+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		/*
		 * if( curImageView != null) curImageView.setImageBitmap(null);
		 */
		if (curFrameLayout != null) {
			RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
					(int) frameWidth, (int) frameHeight);
			params2.leftMargin = (int) ((maxWidth - frameWidth) / 2);
			params2.topMargin = (int) ((maxHeight - frameHeight) / 2);
			curFrameLayout.setLayoutParams(params2);
			//curFrameLayout.setBackgroundColor(Color.RED);
		}
		// sizeBar.setProgress(0);
	}

	/**
	 * build frame choose dialog
	 */
	private void buildFrameChoose() {
		framesWrap = new LinearLayout(this);
		// framesWrap.setBackgroundResource(R.drawable.frame_bg);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		param.addRule(RelativeLayout.BELOW, R.id.main_header_bar);
		param.setMargins(20, 0, 20, 0);
		framesWrap.setLayoutParams(param);

		FrameChooseView frameChoseView = new FrameChooseView(this);
		RelativeLayout.LayoutParams param2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);

		frameChoseView.setLayoutParams(param2);
		framesWrap.addView(frameChoseView);
		RelativeLayout baseFrame = (RelativeLayout) findViewById(R.id.base_frame);
		baseFrame.addView(framesWrap);
		framesWrap.setVisibility(View.GONE);
	}

	private void openFrameChoose() {
		isFramesChooseOpen = true;

		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.frame_open);
		framesWrap.startAnimation(animation);
		framesWrap.setVisibility(View.VISIBLE);
	}

	public void closeFrameChoose() {
		if (!isFramesChooseOpen)
			return;
		isFramesChooseOpen = false;
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.frame_close);
		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation arg0) {
				framesWrap.setVisibility(View.GONE);

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
		framesWrap.startAnimation(animation);

	}

	public void setFrame(FrameData frame) {

		curFrameData = frame;
		closeFrameChoose();
		if (curFrameData.isLocal()) {
			if (frameBitmap != null)
				frameBitmap.recycle();
			System.gc();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			frameBitmap = BitmapFactory.decodeResource(this.getResources(),
					curFrameData.getFrameRes(), options);
			AFLog.i(
					"after decode res, Memory Used::"
							+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

			setFrame();
		} else {
			mImageUrl = curFrameData.getFrameUrl();
			startLoad();

		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_feedback:
			intent = new Intent(this, FeedBack.class);
			startActivity(intent);
			return true;
		case R.id.menu_setting:
			intent = new Intent(this, SetActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// show exit dialog
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (isFramesChooseOpen) {
				closeFrameChoose();
				return false;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									ProcessActivity.this.finish();
								}
							}).setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									return;
								}
							});
			builder.setMessage(R.string.exit_msg);
			builder.create().show();

		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("NewApi")
	private void startLoad() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			String str = this.getResources().getString(
					R.string.download_progress);
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					mCancel = true;

				}
			});
			progressDialog.setMessage(str);
			progressDialog.setCancelable(true);

		}
		progressDialog.setProgress(0);
		progressDialog.show();
		progressDialog.setProgressNumberFormat("%1d/%2d KB");
		new DownloadThread().start();
	}

	private void stopLoad() {
		progressDialog.setProgress(0);
		if (progressDialog.isShowing())
			progressDialog.dismiss();
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FILE_START:
				progressDialog.setMax(fileSize);
				break;
			case MSG_FILE_FINISH:

				stopLoad();
				String fileName = FrameApp.imageCacheDir + File.separator
						+ StringUtils.getAFFileName(mImageUrl);
				;
				if (frameBitmap != null)
					frameBitmap.recycle();
				System.gc();
				frameBitmap = Picture.getBitmapFromFile(fileName, 2);
				if (frameBitmap == null) {
					String str = getResources().getString(
							R.string.fail_load_frame);
					Toast.makeText(ProcessActivity.this, str,
							Toast.LENGTH_SHORT).show();
					return;
				}
				setFrame();
				break;
			case MSG_FILE_PROGRESS: {
				// Bundle data = msg.getData();
				progressDialog.setProgress(downLoadFileSize);
			}
				break;
			case MSG_FILE_CANCEL:
				String str = ProcessActivity.this.getResources().getString(
						R.string.download_cancel);
				Toast.makeText(ProcessActivity.this, str, Toast.LENGTH_SHORT)
						.show();
				break;
			case MSG_PUSH_APP:
				pushApp();
				break;
			}
		}
	};

	// download file
	public class DownloadThread extends Thread {
		public void run() {
			try {
				downFile(mImageUrl);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(MSG_FILE_FAIL);
			}
		}
	}

	public void downFile(String url) throws IOException {
		String fileName = StringUtils.getAFFileName(url);
		File file = new File(FrameApp.imageCacheDir, fileName);
		if (file.exists()) {

			handler.sendEmptyMessage(MSG_FILE_FINISH);
			return;
		}

		URL myURL = new URL(url);

		AFLog.d( "begin download:" + url);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		this.fileSize = conn.getContentLength();
		InputStream is = conn.getInputStream();
		handler.sendEmptyMessage(MSG_FILE_START);

		AFLog.d( "filesize: " + fileSize);
		AFLog.d( "input stream:" + (is == null));
		if (this.fileSize <= 0 || (is == null)) {
			handler.sendEmptyMessage(MSG_FILE_FAIL);
			return;
		}
		AFLog.d( "begin to download.");
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
			// Bundle data = new Bundle();
			// data.putInt(KEY_PROGRESS, downLoadFileSize * 100 / fileSize);
			// msg.setData(data);
			handler.sendMessage(msg);
		}
		if (!mCancel) {
			handler.sendEmptyMessage(MSG_FILE_FINISH);// notif finish
		} else {
			handler.sendEmptyMessage(MSG_FILE_CANCEL);
			file.delete();
		}

		try {
			is.close();
		} catch (Exception ex) {
			Log.e("tag", "error: " + ex.getMessage(), ex);
		}

	}

	public void pushApp() {
		SharedPreferences settings = getSharedPreferences(Constants.SETTING_INFOS, 0);
		int useTimes = settings.getInt("user_times", 0);
		AFLog.d("use time: " + useTimes);
		if(useTimes == 0 || useTimes % 2 != 0 || useTimes % 3 == 0)
			return;
		// load data from server
		StringBuilder url = new StringBuilder();
		url.append(Constants.ConfigUrl);
		url.append("?app=");
		url.append(Constants.AppNameNoSpace);
		url.append("&lang=");
		String lang = getResources().getString(R.string.lang);
		url.append(lang);
		WWRequestTask task = new WWRequestTask(this.getApplicationContext(),
				url.toString());
		task.setListener(this);
		task.setParser(new AFAppPushParser());
		task.execute();
	}

	@Override
	public void onRequestCancel(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestFail(String url, int error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestProgress(long progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestFinish(String url, ArrayList<AFData> resultArray,
			int totalNum) { 
		AFLog.d("result: " + resultArray.size());
		if (resultArray != null && resultArray.size() > 0) {
			final AFAppPush data = (AFAppPush) resultArray.get(0);
			handler.postDelayed(new Runnable(){

				@Override
				public void run() {
					showAppPushDialog(data);
					
				}
				
			}, 5000);
		}
	}

	private void showAppPushDialog(final AFAppPush data) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl())));
							}
						}).setNegativeButton(R.string.later,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								return;
							}
						});
		builder.setTitle(data.getTitle());
		// builder.setMessage("Wonderful frames app for love");
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_layout,
				(ViewGroup) findViewById(R.id.layout_root), false);
		TextView msgView = (TextView) view.findViewById(R.id.dialog_msg);
		msgView.setText(data.getDesc());
		CacheImageView iconView = (CacheImageView)view.findViewById(R.id.dialog_image);
		iconView.setImage(data.getIcon());
		builder.setView(view);
		AlertDialog alert = builder.create();
		alert.show();
	}

}