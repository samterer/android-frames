package com.androidfuture.frames.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.Toast;
import com.androidfuture.frames.AFApp;
import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.R;
import com.androidfuture.frames.service.FrameManager;
import com.androidfuture.frames.tools.ImageStore;
import com.androidfuture.set.SetActivity;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.WWScreenUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.polites.android.GestureImageView;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PreviewActivity extends Activity implements OnClickListener {
	private static final String TAG = "PreviewActivity";
	private static int MSG_FINISH_GEN = 101;
	private static int MSG_SAVE_IMAGE = 102;
	private static int MSG_SET_WALLPAPER_OK = 104;
	private static int MSG_SET_WALLPAPER_FAIL = 105;
	//private FrameData curFrameData;
	private Bitmap resultBitmap;
	private GestureImageView imageView;
	private boolean mCancel;
	//private float frameScale;
	ProgressBar bar;
	private int frameSample;
	private LinearLayout adViewWraper;
	private boolean isShowToolbar;
	private boolean isGenerating = false;
	private ShareActionProvider mShareActionProvider;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
			ActionBar actionBar = getActionBar();
			actionBar.setBackgroundDrawable(new ColorDrawable(
					R.color.trans_base_color));
			actionBar.setStackedBackgroundDrawable(new ColorDrawable(
					R.color.trans_base_color));
			actionBar.hide();
		}
		setContentView(R.layout.preview_layout);
		resultBitmap = FrameManager.GetInstance().getPreviewBitmap();
		if (resultBitmap == null)
		{
			finish();
			return;
		}
		AFLog.i("start Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		float width = resultBitmap.getWidth();
		float height = resultBitmap.getHeight();
		float sW = WWScreenUtils.getInstance(this).getWidthByPX();
		float sH = WWScreenUtils.getInstance(this).getHeightByPX();
		LinearLayout.LayoutParams params = null;
		float scale = 1.0f;
		if (width / height > sW/sH) {
			scale = sW / width;
		} else {
			scale = sH / height;
		}
		params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);

		params.gravity = Gravity.CENTER;
		imageView = new GestureImageView(this);
		ViewGroup layout = (ViewGroup) findViewById(R.id.preview_wrap);
		layout.addView(imageView,params);
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageView.setImageBitmap(resultBitmap);
		//imageView.setBackgroundColor(Color.RED);
		// imageView.setClickable(true);
		//imageView.setScaleType(ScaleType.CENTER_INSIDE);
		imageView.setOnClickListener(this);

		bar = (ProgressBar) findViewById(R.id.progres_gen_image);
		//bar.setVisibility(View.VISIBLE);
		isShowToolbar = false;
		adViewWraper = (LinearLayout) findViewById(R.id.preview_adlayout);
		LinearLayout.LayoutParams layoutParam = new android.widget.LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		layoutParam.gravity = Gravity.CENTER;
		/*
        AdView adView = new AdView(this);

		adView.setAdSize(AdSize.SMART_BANNER);
		adViewWraper.addView(adView, layoutParam);
		adViewWraper.setVisibility(View.GONE);

		if (ConfigManager.GetInstance().getmAdInfo()!=null && ConfigManager.GetInstance().getmAdInfo().isEnableBanner())
		{
			adView.setAdUnitId(ConfigManager.GetInstance().getmAdInfo().getBannerAdId());
			AdRequest adRequest = new AdRequest.Builder().build();
			adView.loadAd(adRequest);
		}
		*/
		Tracker tracker = ((AFApp)getApplication()).getDefaultTracker();
		tracker.setScreenName(getClass().getName());
		tracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@SuppressLint("SimpleDateFormat")
	private void saveImage(Bitmap bitmap) {

		SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");
		String filename = timeStampFormat.format(new Date());

		String title = "IMG_" + filename;
		ContentResolver cr = getContentResolver();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); // bm is the
																// bitmap object
		byte[] data = baos.toByteArray();

		ImageStore.saveImage(cr, data, title);

		return;
	}

	private boolean setAsWallpaper(Bitmap bitmap) {
		WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
		try {
			wallpaperManager.setBitmap(bitmap);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// Log.e(TAG,"set wallpaper failed: " + e.getMessage());
			return false;
		}
	}

	private Intent getShareIntent() {
		SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				"yyyyMMdd_HHmmss");
		String filename = "tmp_" + timeStampFormat.format(new Date()) + ".png";
		// Log.d(TAG,FrameApp.tmpDir);
		File outPutFile = new File(AFAppWrapper.GetInstance().GetApp()
				.GetConf().TmpDir, filename);
		if (!outPutFile.exists()) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(outPutFile);
				resultBitmap.compress(CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (FileNotFoundException e) {
				Log.e(TAG, "file write errpr: " + e.getMessage());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Uri uri = Uri.fromFile(outPutFile);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("image/png");
		intent.putExtra(Intent.EXTRA_STREAM, uri);
		StringBuilder str = new StringBuilder();
		Resources rs = this.getResources();
		str.append(rs.getString(R.string.share_from));
		str.append(" ");
		String appName = getResources().getString(
				AFAppWrapper.GetInstance().GetApp().GetConf().AppName);
		str.append(appName);
		str.append("(");
		str.append(AFAppWrapper.GetInstance().GetApp().GetConf().AppHttpUrl);
		str.append(" )");
		intent.putExtra(Intent.EXTRA_TEXT, str.toString());

		return intent;
	}

	public void onDestroy() {
		imageView.setImageBitmap(null);
		if (resultBitmap != null && !resultBitmap.isRecycled()) {
			resultBitmap.recycle();
			resultBitmap = null;
			System.gc();
		}
		AFLog.i("after destroy preview Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		super.onDestroy();
	}

	public void onPause() {
		super.onPause();

	}

	@Override
	public void onClick(View v) {

		if (isGenerating)
			return;

		if (isShowToolbar) {
			hideToolbar();
			isShowToolbar = false;
		} else {
			showToolbar();
			isShowToolbar = true;
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == MSG_FINISH_GEN) {

				bar.setVisibility(View.GONE);
				isGenerating = false;
				imageView.setImageBitmap(resultBitmap);
				mShareActionProvider.setShareIntent(getShareIntent());
				
			}else if (msg.what == MSG_SAVE_IMAGE)
			{
				bar.setVisibility(View.GONE);
				String text = getResources().getString(
						R.string.save_image_success);
				Toast.makeText(PreviewActivity.this, text, Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	public class SaveImageThread extends Thread {
		public void run() {
			saveImage(resultBitmap);
			handler.sendEmptyMessage(MSG_SAVE_IMAGE);
		}
	}

	public class SetWallPaperThread extends Thread {
		public void run() {
			if (setAsWallpaper(resultBitmap)) {
				handler.sendEmptyMessage(MSG_SET_WALLPAPER_OK);
			} else {
				handler.sendEmptyMessage(MSG_SET_WALLPAPER_FAIL);
			}
		}
	}

	// download file
	
	public class GenBitmapThread extends Thread {
		public void run() {
			isGenerating = true;
			//genBitmap();
			handler.sendEmptyMessage(MSG_FINISH_GEN);

		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.share_menu, menu);
		MenuItem item = menu.findItem(R.id.share_menu_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		mShareActionProvider.setShareIntent(getShareIntent());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		if (item.getItemId() == R.id.menu_feedback) {
			//intent = new Intent(this, FeedBack.class);
			//startActivity(intent);
		}
		
		else if (item.getItemId() == R.id.menu_setting) {
			intent = new Intent(this, SetActivity.class);
			startActivity(intent);
		} else if (item.getItemId() == R.id.action_save)
		{
			
			bar.setVisibility(View.VISIBLE);
			new SaveImageThread().start();
		}
		return super.onOptionsItemSelected(item);
	}

	// generate bitmap
	/*
	private void genBitmap() {

		if (curFrameData == null)
			return;

		AFLog.i("-3 Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		int width = curFrameData.getFrameWidth();
		int height = curFrameData.getFrameHeight();
		AFLog.d("width: %d" + width);
		resultBitmap = Bitmap
				.createBitmap(width, height, Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(resultBitmap);
		Paint paint = new Paint();
		int left = 0;
		int top = 0;

		for (FrameCell cell : curFrameData.getmFrameCells()) {
			genFrameCell(canvas, cell);
		}

		Bitmap frameBitmap = null;
		if (curFrameData.isLocal()) {

			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			float scale = 1.0f;
			switch (metrics.densityDpi) {
			case DisplayMetrics.DENSITY_LOW:
				scale = 0.75f;
				break;
			case DisplayMetrics.DENSITY_MEDIUM:
				scale = 1.0f;
				break;
			case DisplayMetrics.DENSITY_HIGH:
				scale = 1.5f;
				break;
			case DisplayMetrics.DENSITY_XHIGH:
				scale = 2.0f;
				break;
			}
			AFLog.d("density: " + metrics.densityDpi);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = (int) frameSample;
			options.inScaled = false;
			Bitmap tmpBitmap = BitmapFactory.decodeResource(
					this.getResources(), curFrameData.getRes(), options);
			if (scale != 1.0) {
				frameBitmap = Bitmap.createScaledBitmap(tmpBitmap,
						(int) (width / scale), (int) (height / scale), false);
				tmpBitmap.recycle();
				tmpBitmap = null;
			} else {
				frameBitmap = tmpBitmap;
				tmpBitmap = null;
			}

		} else {
			String fileName = StringUtils.getAFFileName(curFrameData
					.getFrameUrl());
			File file = new File(
					AFAppWrapper.GetInstance().GetApp().GetConf().CacheDir,
					fileName);
			if (!file.exists()) {
				URL myURL;
				try {
					myURL = new URL(curFrameData.getFrameUrl());
					URLConnection conn = myURL.openConnection();
					conn.connect();
					InputStream is = conn.getInputStream();
					FileOutputStream fos = new FileOutputStream(file);
					// save
					byte buf[] = new byte[1024];
					mCancel = false;

					while (!mCancel) {
						// repeat read
						int numread = is.read(buf);
						if (numread == -1) {
							break;
						}
						fos.write(buf, 0, numread);
					}
					fos.close();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			frameBitmap = Picture.getBitmapFromFile(file.getAbsolutePath(),
					frameSample);
		}

		if (frameBitmap == null)
			return;

		canvas.drawBitmap(frameBitmap, left, top, paint);

		canvas.save();
	}
	

	private void genFrameCell(Canvas canvas, FrameCell cell) {

		// Log.d(TAG,"in genBitmap: rate: " + rate);

		AFLog.i("1 Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		int limit = DeviceUtils.IsLargerHeap() ? (1 << 19) : (1 << 18);

		if (curFrameData.getmFrameCells().length > 2) {
			limit = limit / 2;
		}
		Bitmap curPhoto = Picture.getBitmapFromFileLimit(cell.getPhotoFile(),
				limit);
		if (curPhoto == null)
			return;
		AFLog.i("Before Filter Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		IImageFilter filter = FilterManager.getFilter(cell.getFilterIndex());
		if (filter != null) {
			Image img = null;
			try {
				img = new Image(curPhoto);
				img = filter.process(img);
				img.copyPixelsFromBuffer();
				curPhoto = img.getImage();
				AFLog.d("filter start" + curPhoto);
			} catch (Exception e) {
				
			} finally {

			}
		}
		AFLog.i("2 Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		Matrix localMatrix = new Matrix();
		localMatrix.postRotate(cell.getAngle());

		Bitmap rotateBitmap = Bitmap.createBitmap(curPhoto, 0, 0,
				(int) curPhoto.getWidth(), (int) curPhoto.getHeight(),
				localMatrix, false);
		AFLog.i("3 Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

		if (curPhoto != rotateBitmap && !curPhoto.isRecycled()) {
			curPhoto.recycle();
			curPhoto = null;
			System.gc();
		}

		if (rotateBitmap == null) {
			return;
		}
		float cellFrameWidth = curFrameData.getFrameWidth()
				* cell.getWidthRate();
		float cellFrameHeight = curFrameData.getFrameHeight()
				* cell.getHeightRate();
		float cellFrameLeft = curFrameData.getFrameWidth() * cell.getLeftRate();
		float cellFrameTop = curFrameData.getFrameHeight() * cell.getTopRate();
		float cellFrameRight = cellFrameLeft + cellFrameWidth;
		float cellFrameBottom = cellFrameTop + cellFrameHeight;
		// AFLog.d("Start to gen cell: " + curFrameData.getFrameWidth() );
		float xCenter = cellFrameWidth / 2 + cell.getOffsetX() * frameScale * 2
				+ cellFrameLeft;
		float yCenter = cellFrameHeight / 2 + cell.getOffsetY() * frameScale
				* 2 + cellFrameTop;
		double redians = cell.getAngle() * 3.14159265 / 180;

		cell.curHeight = (int) Math.max(
				Math.abs(cell.initHeight * Math.cos(redians) - cell.initWidth
						* Math.sin(redians)),
				Math.abs(cell.initHeight * Math.cos(redians) + cell.initWidth
						* Math.sin(redians)));

		float temp = (float) Math.min(
				Math.abs(cell.initHeight * Math.cos(redians) - cell.initWidth
						* Math.sin(redians)),
				Math.abs(cell.initHeight * Math.cos(redians) + cell.initWidth
						* Math.sin(redians)));

		cell.curWidth = (float) Math.sqrt(cell.initHeight * cell.initHeight
				+ cell.initWidth * cell.initWidth - temp * temp);

		float width = cell.curWidth * cell.rate * frameScale * 2;

		float height = cell.curHeight * cell.rate * frameScale * 2;

		float left = (int) (xCenter - width / 2);
		float top = (int) (yCenter - height / 2);
		float right = (int) (xCenter + width / 2);
		float bottom = (int) (yCenter + height / 2);
		float cropLeft = 0;
		if ((left - cellFrameLeft) < 0) {
			cropLeft = -(left - cellFrameLeft);
			left = cellFrameLeft;
		}
		float cropTop = 0;
		if ((top - cellFrameTop) < 0) {
			cropTop = -(top - cellFrameTop);
			top = cellFrameTop;
		}
		Bitmap scaleBitmap = Bitmap.createScaledBitmap(rotateBitmap,
				(int) width, (int) height, true);
		if (scaleBitmap != rotateBitmap) {
			rotateBitmap.recycle();
			rotateBitmap = null;
			System.gc();
		}

		float cropWidth = cellFrameRight - right > 0 ? width - cropLeft : width
				- (right - cellFrameRight) - cropLeft;
		float cropHeight = cellFrameBottom - bottom > 0 ? height - cropTop
				: height - cropTop - (bottom - cellFrameBottom);

		if (cropWidth < 1 || cropHeight < 1) {
			return;
		}
		Log.d(TAG, "gen 2: Left: " + left + " top: " + top + " width: " + width
				+ " height: " + height);

		Log.d(TAG, "gen 2: cropLeft: " + cropLeft + " cropTop: " + cropTop
				+ " cropWidth: " + cropWidth + " cropHeight: " + cropHeight);

		AFLog.i("4 Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));
		Rect src = new Rect();
		src.left = (int) cropLeft;
		src.top = (int) cropTop;
		src.right = (int) (cropLeft + cropWidth);
		src.bottom = (int) (cropTop + cropHeight);
		AFLog.d("src: " + src.left + "," + src.top + "," + src.right + ","
				+ src.bottom);

		Rect dst = new Rect();
		dst.left = (int) left;
		dst.top = (int) top;
		dst.right = (int) (left + cropWidth);
		dst.bottom = (int) (top + cropHeight);
		AFLog.d("dst: " + dst.left + "," + dst.top + "," + dst.right + ","
				+ dst.bottom);
		Paint paint = new Paint();
		canvas.drawBitmap(scaleBitmap, src, dst, paint);
		if (scaleBitmap != null && !scaleBitmap.isRecycled()) {
			scaleBitmap.recycle();
			scaleBitmap = null;
			System.gc();
		}
		AFLog.i("5 Memory Used::"
				+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

	}
	*/
	void showToolbar() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.toolbar_in);
		adViewWraper.startAnimation(animation);
		adViewWraper.setVisibility(View.VISIBLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().show();
		}
	}

	void hideToolbar() {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.toolbar_out);
		adViewWraper.startAnimation(animation);

		adViewWraper.setVisibility(View.INVISIBLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().hide();
		}
	}

}
