package com.androidfuture.frames.ui;

/**
 * 提供一个View来管理相框
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.androidfuture.cacheimage.Picture;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;
import com.androidfuture.frames.data.FrameCell;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.service.FilterManager;
import com.androidfuture.imagefilter.IImageFilter;
import com.androidfuture.imagefilter.Image;

import com.androidfuture.photo.picker.PhotoManager;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.DeviceUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class AFFrameView extends RelativeLayout implements OnTouchListener {

	private static final int LOW_DPI_STATUS_BAR_HEIGHT = 19;

	private static final int MEDIUM_DPI_STATUS_BAR_HEIGHT = 25;

	private static final int HIGH_DPI_STATUS_BAR_HEIGHT = 38;

	private final int kMaxPhotoSize = (int) (1 << 18);

	ProcessActivity mActivity;

	private ImageView frameImageView;

	private Bitmap frameBitmap;

	private FrameData frameData;

	private ImageView targetView;

	private float frameOrigWidth;

	private float frameOrigHeight;

	private float frameWidth;

	private float frameHeight;

	private float maxHeight;

	private float maxWidth;

	private int curSelect;

	private float touchStartX;

	private float touchStartY;

	private float baseValue;

	private ArrayList<ImageView> frameCellViews;

	private ArrayList<RelativeLayout> frameCellLayouts;

	private HashMap<Integer, ImageReadTask> photoLoadTasks;

	private ArrayList<ProgressBar> progressViews;

	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	static final int ROTATE = 3;// 旋转

	int mode = NONE;

	public AFFrameView(Context context) {
		super(context);
		mActivity = (ProcessActivity) context;
		frameCellViews = new ArrayList<ImageView>();
		frameCellLayouts = new ArrayList<RelativeLayout>();
		progressViews = new ArrayList<ProgressBar>();
		computeBound();
	}

	public AFFrameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mActivity = (ProcessActivity) context;
		frameCellViews = new ArrayList<ImageView>();
		frameCellLayouts = new ArrayList<RelativeLayout>();
		progressViews = new ArrayList<ProgressBar>();
		setOnTouchListener(this);
		
		computeBound();
	}

	public void setFrameData(FrameData newFrameData) {

		FrameData oldData = frameData;
		frameData = newFrameData;
		initTasks();
		if (frameData.isLocal()) {
			if (frameBitmap != null && !frameBitmap.isRecycled()) {
				frameBitmap.recycle();
				frameBitmap = null;
				System.gc();
			}
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			options.inScaled = false;
			frameBitmap = BitmapFactory.decodeResource(this.getResources(),
					frameData.getRes(), options);

			AFLog.i("after decode res, Memory Used::"
					+ (int) (Debug.getNativeHeapAllocatedSize() / 1048576L));

			layoutView();
		} else {
			if (frameBitmap != null && !frameBitmap.isRecycled()) {
				frameBitmap.recycle();
				frameBitmap = null;
				System.gc();
			}
			frameBitmap = frameData.getFrameBitmap();
			layoutView();
		}
		curSelect = -1;
		setSelect(0);
		if (oldData != null && frameData != null) {
			int i = 0;
			for (; i < frameData.getmFrameCells().length
					&& i < oldData.getmFrameCells().length; ++i) {
				AFLog.d("new frame " + i + " use: "
						+ oldData.getmFrameCells()[i].getPhotoFile());
				frameData.getmFrameCells()[i].setPhotoFile(oldData
						.getmFrameCells()[i].getPhotoFile());
				frameData.getmFrameCells()[i].setFilterIndex(oldData
						.getmFrameCells()[i].getFilterIndex());
				this.setPhotoBitmap(i, oldData.getmFrameCells()[i].photo);
			}
			for (; i < oldData.getmFrameCells().length; ++i) {
				if (oldData.getmFrameCells()[i].photo != null
						&& oldData.getmFrameCells()[i].photo.isRecycled()) {
					oldData.getmFrameCells()[i].photo.recycle();
					oldData.getmFrameCells()[i].photo = null;
				}
			}
			System.gc();
		}
		
		if (PhotoManager.GetInstance().GetSelected().size() > 0)
		{
			this.setPhotos(PhotoManager.GetInstance().GetSelected());
		}
	}

	public void initTasks() {
		if (photoLoadTasks == null) {
			photoLoadTasks = new HashMap<Integer, ImageReadTask>();

		}
		Iterator<Integer> iter = photoLoadTasks.keySet().iterator();
		while (iter.hasNext()) {
			Integer key = iter.next();
			photoLoadTasks.get(key).cancel(true);

		}
		photoLoadTasks.clear();
	}

	public FrameData getFrameData() {
		return this.frameData;
	}

	public void layoutView() {
		this.removeAllViews();
		layoutFrameImage();
		frameCellLayouts.clear();
		frameCellViews.clear();

		for (int i = 0; i < frameData.getmFrameCells().length; i++) {
			layoutFrameCellView(i);
		}

		frameImageView.bringToFront();

		setSelect(0);
	}

	private void layoutFrameImage() {

		// photosNum =0;

		if (frameImageView == null) {
			frameImageView = new ImageView(this.getContext());
			// frameImageView.setOnTouchListener( this);
			frameImageView.setScaleType(ScaleType.FIT_XY);
			frameImageView.setDrawingCacheEnabled(false);
		}

		AFLog.i("before set frame image view, Memory Used::"
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

		AFLog.d("frame width:" + frameWidth + " frame height: " + frameHeight);
		RelativeLayout.LayoutParams frameParams = new RelativeLayout.LayoutParams(
				(int) frameWidth, (int) frameHeight);
		
		frameParams.leftMargin = (int) (maxWidth / 2 - frameWidth / 2);
		frameParams.topMargin = (int) (maxHeight / 2 - frameHeight / 2);
		frameParams.rightMargin = (int) (maxWidth / 2 - frameWidth / 2);
		//frameParams.bottomMargin = (int) (maxHeight / 2 - frameHeight / 2);
		this.addView(frameImageView, frameParams);
		
		ImageView bg = new ImageView(this.getContext());
		bg.setBackgroundDrawable(getContext().getResources()
				.getDrawable(R.drawable.image_default));
		//bg.setBackgroundColor(Color.GRAY);
		this.addView(bg, frameParams);
		
		
		frameData.setFrameWidth((int) frameOrigWidth * 2);
		frameData.setFrameHeight((int) frameOrigHeight * 2);

		AFLog.d("frame size: " + frameOrigWidth + ":" + frameOrigHeight);
	}

	private void layoutFrameCellView(int index) {
		FrameCell cell = frameData.getmFrameCells()[index];
		RelativeLayout newFrameLayout = new RelativeLayout(this.getContext());

		newFrameLayout.setBackgroundColor(Color.TRANSPARENT);

		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				(int) (frameWidth * cell.getWidthRate()),
				(int) (frameHeight * cell.getHeightRate()));
		params2.leftMargin = (int) ((maxWidth - frameWidth) / 2 + frameWidth
				* cell.getLeftRate());
		params2.topMargin = (int) ((maxHeight - frameHeight) / 2 + frameHeight
				* cell.getTopRate());
		newFrameLayout.setLayoutParams(params2);
		frameCellLayouts.add(newFrameLayout);
		newFrameLayout.setBackgroundColor(Color.TRANSPARENT);
		this.addView(newFrameLayout);
		this.invalidate();
		AFLog.d("cell layout :" + index + "left: " + params2.leftMargin
				+ " top: " + params2.topMargin + " width:" + params2.width
				+ "height:" + params2.height);

		ImageView newImageView = null;
		newImageView = new ImageView(this.getContext());
		newImageView.setScaleType(ScaleType.FIT_XY);

		// curImageView.setOnTouchListener(this);

		newImageView.setDrawingCacheEnabled(false);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(0,
				0);

		params.width = (int) (cell.initWidth * cell.rate + 0.5);
		params.height = (int) (cell.initHeight * cell.rate + 0.5);
		params.leftMargin = (int) (cell.xCenter - cell.initWidth * cell.rate
				/ 2 + 0.5);
		params.topMargin = (int) (cell.yCenter - cell.initHeight * cell.rate
				/ 2 + 0.5);
		params.rightMargin = (int) (frameWidth * cell.getWidthRate()
				- cell.xCenter - cell.initWidth * cell.rate / 2 + 0.5);
		params.bottomMargin = (int) (frameHeight * cell.getHeightRate()
				- cell.yCenter - cell.initHeight * cell.rate / 2 + 0.5);

		newImageView.setLayoutParams(params);
		newImageView.setBackgroundColor(Color.TRANSPARENT);
		newFrameLayout.addView(newImageView, params);
		newImageView.invalidate();

		frameCellViews.add(newImageView);
		int size = 48;
		ProgressBar progress = new ProgressBar(this.getContext(), null,
				android.R.attr.progressBarStyleSmall);
		// progress.setBackgroundColor(Color.RED);
		RelativeLayout.LayoutParams proParams = new RelativeLayout.LayoutParams(
				size, size);
		// AFLog.d("left: " + (newFrameLayout.getLayoutParams().width / 2 - size
		// / 2));
		proParams.setMargins(newFrameLayout.getLayoutParams().width / 2 - size
				/ 2, newFrameLayout.getLayoutParams().height / 2 - size / 2,
				newFrameLayout.getLayoutParams().width / 2 - size / 2,
				newFrameLayout.getLayoutParams().height / 2 - size / 2);

		progress.setVisibility(View.INVISIBLE);
		progressViews.add(progress);
		newFrameLayout.addView(progress, proParams);

		this.invalidate();

	}

	private void computeBound() {

		DisplayMetrics metric = new DisplayMetrics();
		((Activity) this.getContext()).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		int sreenWidth = metric.widthPixels;
		int sreenHeight = metric.heightPixels;
		float density = metric.density;
		float headBarHeight = 50 * density;
		float footerBarHeight = 50 * density;
		float adBarHeight = 48 * density;
		float padding = 12 * density;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) ((Activity) this.getContext())
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(displayMetrics);

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
		
		maxWidth = sreenWidth - 2 * padding;
		maxHeight = (int) (sreenHeight - headBarHeight - footerBarHeight - statusBarHeight - adBarHeight - 2 * padding);
		AFLog.d("Max Width:" + maxWidth + "Heihgt:" + maxHeight);
		AFLog.d("layout width:" + this.getWidth() + " layout height: " + this.getHeight());
	}
	
	public void setPhoto(int index, String str) {
		if (str == null) {
			return;
		}
		FrameCell cell = frameData.getmFrameCells()[index];
		cell.reset();
		cell.setPhotoFile(str);

		if (photoLoadTasks.get(index) != null) {
			photoLoadTasks.get(index).cancel(true);
			photoLoadTasks.remove(index);
		}
		photoLoadTasks.put(index, new ImageReadTask(index));

		photoLoadTasks.get(index).execute(str);
	}
	public void setPhotos(ArrayList<AFPhotoData> photos)
	{
		if (photos == null || photos.isEmpty())
		{
			return;
		}
		for (int i = 0;i < frameData.getmFrameCells().length && i < photos.size(); i++)
		{
			setPhoto(i, photos.get(i).getPath());
		}
	}
	public void setPhoto(String str) {
		setPhoto(curSelect, str);
	}

	public void showLoad(int index) {

	}

	public void setPhotoBitmap(Bitmap photoBitmap) {
		setPhotoBitmap(curSelect, photoBitmap);
	}

	private void setPhotoBitmap(int index, Bitmap photoBitmap) {
		if (photoBitmap == null||photoBitmap.isRecycled()) {
			return;
		}
		FrameCell cell = frameData.getmFrameCells()[index];

		cell.origWidth = photoBitmap.getWidth();
		cell.origHeight = photoBitmap.getHeight();
		cell.rate = 1.0f;

		AFLog.d("origWidth: origWidth: " + cell.origWidth + "origHeight: "
				+ cell.origHeight + " frameWidth: " + frameWidth
				+ " frameHeight: " + frameHeight);

		float cellFrameWidth = frameWidth * cell.getWidthRate();
		float cellFrameHeight = frameHeight * cell.getHeightRate();
		if (cell.origWidth / cellFrameWidth < cell.origHeight / cellFrameHeight) {
			cell.initWidth = cellFrameWidth;
			cell.initHeight = cell.origHeight * cell.initWidth / cell.origWidth;
		} else {
			cell.initHeight = cellFrameHeight;
			cell.initWidth = cell.origWidth * cell.initHeight / cell.origHeight;
		}
		/*
		if ( cell.initHeight  > 640)
		{
			float tmp = cell.initHeight;
			cell.initHeight = 640;
			cell.initWidth = 640 * cell.initWidth / cell.initHeight;
		}else if (cell.initWidth > 640)
		{
			float tmp = cell.initWidth;
			cell.initWidth = 640;
			cell.initHeight = 640 * cell.initHeight / cell.initWidth;
		}
		*/
		
		cell.curWidth = cell.initWidth;
		cell.curHeight = cell.initHeight;
		AFLog.d("cell width:" + cell.initWidth + " heihgt:" + cell.initHeight);
		cell.xCenter = (int) (cellFrameWidth / 2);
		cell.yCenter = (int) (cellFrameHeight / 2);

		if (cell.initHeight < 0.1f || cell.initWidth < 0.1f) {
			return;
		}
		cell.photo = Bitmap.createScaledBitmap(photoBitmap,
				(int) cell.initWidth, (int) cell.initHeight, true);

		if (photoBitmap != null && photoBitmap != cell.photo
				&& !photoBitmap.isRecycled()) {
			photoBitmap.recycle();
			photoBitmap = null;
			System.gc();
		}

		frameCellViews.get(index).setImageBitmap(cell.photo);

		updateCellView(index);

	}

	void updateCellView(int index) {

		FrameCell cell = frameData.getmFrameCells()[index];

		ImageView cellImage = frameCellViews.get(index);
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cellImage
				.getLayoutParams();

		float viewWidth = (int) (cell.curWidth * cell.rate);
		float viewHeight = (int) (cell.curHeight * cell.rate);

		int width = (int) (frameWidth * cell.getWidthRate());
		int height = (int) (frameHeight * cell.getHeightRate());

		params.width = (int) viewWidth;
		params.height = (int) viewHeight;
		params.leftMargin = (int) (cell.xCenter - viewWidth / 2 + 0.5);
		params.topMargin = (int) (cell.yCenter - viewHeight / 2 + 0.5);
		params.rightMargin = (int) (width - cell.xCenter - viewWidth / 2 + 0.5);
		params.bottomMargin = (int) (height - cell.yCenter - viewHeight / 2 + 0.5);

		cellImage.setLayoutParams(params);
		cellImage.invalidate();
		AFLog.d("Update cell view:" + cellImage + " width" + params.width
				+ " heihgt: " + params.height + ":" + params.leftMargin + ":"
				+ params.topMargin);

	}

	public void adjustPosition(int deltaX, int deltaY) {
		if (frameData.getmFrameCells()[curSelect].getPhoto() == null) {
			return;
		}
		frameData.getmFrameCells()[curSelect].adjustPosition(deltaX, deltaY);
		updateCellView(curSelect);
	}

	public void resize(float rate) {
		if (frameData.getmFrameCells()[curSelect].getPhoto() == null) {
			return;
		}
		frameData.getmFrameCells()[curSelect].adjustSize(rate);
		updateCellView(curSelect);
	}

	public void rotate(int angleStep) {
		if(DeviceUtils.getAPILevel()>=android.os.Build.VERSION_CODES.HONEYCOMB)
		{
			frameData.getmFrameCells()[curSelect].adjustAngle(angleStep);
			FrameCell cell = frameData.getmFrameCells()[curSelect];
			frameCellViews.get(curSelect)
					.setRotation((float) (cell.getAngle()));
			frameCellViews.get(curSelect).invalidate();

			if (frameData.getmFrameCells()[curSelect].getPhoto() == null) {
				return;
			}
			frameData.getmFrameCells()[curSelect].adjustAngle(angleStep);
		} else {

			
			frameData.getmFrameCells()[curSelect].adjustAngle(angleStep);
			FrameCell cell = frameData.getmFrameCells()[curSelect];
			
			if(cell.photo == null)
				return;
			
			double redians = cell.getAngle() * 3.14159265 / 180;
			Matrix localMatrix = new Matrix();
			localMatrix.postRotate(angleStep);
			cell.curHeight = (int) Math.max(
					Math.abs(cell.initHeight * Math.cos(redians)
							- cell.initWidth * Math.sin(redians)),
					Math.abs(cell.initHeight * Math.cos(redians)
							+ cell.initWidth * Math.sin(redians)));

			float temp = (float) Math.min(
					Math.abs(cell.initHeight * Math.cos(redians)
							- cell.initWidth * Math.sin(redians)),
					Math.abs(cell.initHeight * Math.cos(redians)
							+ cell.initWidth * Math.sin(redians)));

			cell.curWidth = (float) Math.sqrt(cell.initHeight * cell.initHeight
					+ cell.initWidth * cell.initWidth - temp * temp);
			
			//AFLog.d("in process, rotate, " + " orig photo width:" + cell.photo.getWidth() + " orig photo height: " + cell.photo.getHeight());
			Bitmap tmpBitmap = Bitmap.createBitmap(cell.photo, 0, 0,
					cell.photo.getWidth(), cell.photo.getHeight(), localMatrix,
					true);
			if (cell.photo != tmpBitmap && !cell.photo.isRecycled()) {
				frameCellViews.get(curSelect).setImageBitmap(null);
				cell.photo.recycle();
				cell.photo = null;
				System.gc();
			}
			

			
			float x = tmpBitmap.getWidth() > cell.curWidth ? tmpBitmap
					.getWidth() / 2 - cell.curWidth / 2 : 0;
			float y = tmpBitmap.getHeight() > cell.curHeight ? tmpBitmap
					.getHeight() / 2 - cell.curHeight / 2 : 0;
			float w = tmpBitmap.getWidth() > cell.curWidth ? cell.curWidth
					: tmpBitmap.getWidth();
			float h = tmpBitmap.getHeight() > cell.curHeight ? cell.curHeight
					: tmpBitmap.getHeight();

			cell.photo = Bitmap.createBitmap(tmpBitmap, (int) x, (int) y,
					(int) w, (int) h);
			
			AFLog.d("In rotating,rotate, photoWidth:" + tmpBitmap.getWidth() + " photoHeight:" + tmpBitmap.getHeight()  +"  width: " + cell.curWidth + " height: " + cell.curHeight);
			AFLog.d("In rotating,rotate, x: " + x + " y: " + y + " w: " + h +  " h: " + h);
			if (cell.photo != tmpBitmap && !tmpBitmap.isRecycled()) {
				tmpBitmap.recycle();
				tmpBitmap = null;
				System.gc();
			}

			frameCellViews.get(curSelect).setImageBitmap(cell.photo);
		}
		updateCellView(curSelect);

	}

	void destroy() {
		AFLog.d("destroy AFFrameView");
	}

	void pause() {
		if (photoLoadTasks != null) {
			Iterator<Integer> iter = photoLoadTasks.keySet().iterator();
			while (iter.hasNext()) {
				Integer key = iter.next();
				photoLoadTasks.get(key).cancel(true);

			}
			photoLoadTasks.clear();
		}
	}

	float getFrameScale() {
		return this.frameOrigHeight / this.frameHeight;
	}

	int getSelect() {
		return this.curSelect;
	}

	void updateCurSelect() {
		updateCellView(curSelect);
	}

	void setSelect(int select) {

		int preSelect = curSelect;
		if (select == curSelect) {
			return;
		}
		curSelect = select;

		if (curSelect < 0 ||frameData == null || frameData.getmFrameCells() == null
				||curSelect >= frameData.getmFrameCells().length
				|| frameData.getmFrameCells().length == 1) {
			return;
		}

		FrameCell cell = frameData.getmFrameCells()[curSelect];
		if (targetView == null) {
			targetView = new ImageView(this.getContext());
			targetView.setBackgroundColor(Color.TRANSPARENT);
			targetView.setImageResource(R.drawable.target_use);
			targetView.setScaleType(ScaleType.FIT_XY);
		}

		if (targetView != null && targetView.getParent() != null) {
			((RelativeLayout) (targetView.getParent())).removeView(targetView);
		}
		/*
		 * if (preSelect >= 0 && preSelect <= frameData.getmFrameCells().length)
		 * { RelativeLayout preCellLayout =
		 * this.frameCellLayouts.get(preSelect);
		 * preCellLayout.removeView(targetView); }
		 */
		RelativeLayout cellLayout = this.frameCellLayouts.get(select);
		int size = 48;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				size, size);

		AFLog.d("left: " + (cellLayout.getLayoutParams().width / 2 - size / 2)
				+ "top:" + (cellLayout.getLayoutParams().height / 2 - size / 2));
		float left = cellLayout.getLayoutParams().width / 2 - size / 2;
		float top = cellLayout.getLayoutParams().height / 2 - size / 2;
		params.setMargins((int) left, (int) top, (int) left, (int) top);
		cellLayout.addView(targetView, params);

	}

	boolean containPoint(FrameCell cell, float x, float y) {

		int left = (int) (maxWidth / 2 - frameWidth / 2 + frameWidth
				* cell.getLeftRate());
		int top = (int) (maxHeight / 2 - frameHeight / 2 + frameHeight
				* cell.getTopRate());
		int wrapWidth = (int) (frameWidth * cell.getWidthRate());
		int wrapHeight = (int) (frameHeight * cell.getHeightRate());
		AFLog.d("x: " + x + " y: " + y + " left: " + left + " right: "
				+ (left + wrapWidth));

		return x > left && x < (left + wrapWidth) && y > top
				&& y < (top + wrapHeight);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:

			mActivity.hideFilter();
			float x = event.getX();
			float y = event.getY();
			if (frameData == null || frameData.getmFrameCells() == null)
				return false;
			
			if (frameData.getmFrameCells().length == 1) {
				setSelect(0);
			} else {
				int click = CalcTouchFrame(x, y);
				if (click != -1)
					setSelect(click);
			}
			touchStartX = event.getX();
			touchStartY = event.getY();
			baseValue = 0;
			break;
		case MotionEvent.ACTION_MOVE: {
			AFLog.d("touch point: " + event.getPointerCount());
			if (event.getPointerCount() == 2) {
				mode = ZOOM;
				float dist_x = event.getX(0) - event.getX(1);
				float dist_y = event.getY(0) - event.getY(1);
				float value = (float) Math.sqrt(dist_x * dist_x + dist_y
						* dist_y);// 计算两点的距离
				if (baseValue == 0) {
					baseValue = value;
				} else {
					if (value - baseValue >= 5 || value - baseValue <= -5) {
						float scale = (1 + (value - baseValue) / baseValue)
								* getSelectScale();// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
						resize(scale); // 缩放图片
						baseValue = value;
					}
				}
			} else if (event.getPointerCount() == 1) {
				if (mode != ZOOM) {
					float dX = event.getX() - touchStartX;
					float dY = event.getY() - touchStartY;
					AFLog.d("touch: " + event.getX() + ":" + event.getY());
					mode = DRAG;
					if (dX * dX + dY * dY > 2) {
						adjustPosition((int) dX, (int) dY);
						touchStartX = event.getX();
						touchStartY = event.getY();
						invalidate();
					}
				}
			}
			break;
		}

		case MotionEvent.ACTION_UP:
			touchStartX = event.getX();
			touchStartY = event.getY();
			mode = NONE;
			baseValue = 0;
		}
		return true;

	}

	float getSelectScale() {
		return frameData.getmFrameCells()[curSelect].getRate();
	}

	int CalcTouchFrame(float x, float y) {
		int rtn = -1;
		for (int i = 0; i < frameData.getmFrameCells().length; i++) {
			FrameCell cell = frameData.getmFrameCells()[i];
			if (this.containPoint(cell, x, y)) {
				rtn = i;
				break;
			}
		}
		return rtn;
	}

	public class ImageReadTask extends AsyncTask<String, Void, Bitmap> {
		// private final WeakReference<ImageView> imageViewReference;

		int readIndex;

		public ImageReadTask(int index) {

			this.readIndex = index;
		}

		protected void onPreExecute() {
			AFLog.d("start load photo:" + readIndex);
			progressViews.get(readIndex).setVisibility(View.VISIBLE);
		}

		protected Bitmap doInBackground(String[] params) {
			// TODO Auto-generated method stub

			String fileName = params[0];
			int limit = DeviceUtils.IsLargerHeap() ? kMaxPhotoSize
					: kMaxPhotoSize / 2;
			if (frameData.getmFrameCells().length > 2) {
				limit = limit / 2;
			}
			Bitmap bitmap = Picture.getBitmapFromFileLimit(fileName, limit);
			//AFLog.d("read file: " + fileName + " width: " + bitmap.getWidth());
			return bitmap;

		}

		protected void onPostExecute(Bitmap bitmap) {
			AFLog.d("start load photo:" + readIndex + " bitmap: " + bitmap);
			if (isCancelled()) {
				bitmap = null;

			} else {
				if (bitmap == null) {
					return;
				} else {
					setPhotoBitmap(readIndex, bitmap);
				}
			}

			progressViews.get(readIndex).setVisibility(View.INVISIBLE);
		}
	}

	public void setFilter(int filter) {
		new ProcessImageTask(filter).execute();

	}

	public boolean checkPhotoOK() {
		if (frameData == null)
			return false;
		FrameCell cell = frameData.getmFrameCells()[curSelect];
		return cell.getPhoto() != null;
	}

	public class ProcessImageTask extends AsyncTask<Void, Void, Bitmap> {
		private int filter_index;

		// private Activity activity = null;
		public ProcessImageTask(int index) {
			this.filter_index = index;

		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			super.onPreExecute();
			// progressViews.get(curSelect).setVisibility(View.VISIBLE);
			((ProcessActivity) mActivity).showIndicator();
		}

		public Bitmap doInBackground(Void... params) {

			FrameCell cell = frameData.getmFrameCells()[curSelect];
			Bitmap result = null;
			
			int limit = DeviceUtils.IsLargerHeap() ? kMaxPhotoSize
					: kMaxPhotoSize / 2;
			if (frameData.getmFrameCells().length > 2) {
				limit = limit / 2;
			}
			
			if (cell.photo != null && cell.photo.isRecycled()) {
				cell.photo.recycle();
				cell.photo = null;
				System.gc();
			}
			
			Bitmap origBitmap = Picture.getBitmapFromFileLimit(
					cell.getPhotoFile(), limit);
			
			if (origBitmap == null)
			{
				return null;
			}
			AFLog.d("In process: cell width: " + cell.initWidth + "cell height: " + cell.initHeight);

			
			IImageFilter filter = FilterManager.getFilter(filter_index);
			if (filter != null) {
				Image img = new Image(origBitmap);
				try {
					img = filter.process(img);

					img.copyPixelsFromBuffer();
					origBitmap = img.getImage();
					
					result = Bitmap.createScaledBitmap(origBitmap,
							(int) cell.initWidth, (int) cell.initHeight, false);
					if(origBitmap != result && !origBitmap.isRecycled())
					{
						origBitmap.recycle();
						origBitmap = null;
						System.gc();
					}
				} catch (Exception e) {
					/*
					 * AFLog.d("exception: " + e); if (img != null &&
					 * !img.destImage.isRecycled()) { img.destImage.recycle();
					 * img.destImage = null; System.gc(); }
					 */
				}
				
				return result;
			} else {
				result = Bitmap.createScaledBitmap(origBitmap,
						(int) cell.initWidth, (int) cell.initHeight, false);
				if(origBitmap != result && !origBitmap.isRecycled())
				{
					origBitmap.recycle();
					origBitmap = null;
					System.gc();
				}
				return result;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;

			} else {
				if (bitmap == null) {
					String text = mActivity.getResources().getString(R.string.fail_process_image);
					Toast.makeText(mActivity, text, Toast.LENGTH_SHORT);
					return;
				} else {
					FrameCell cell = frameData.getmFrameCells()[curSelect];
					Bitmap tmpPhoto = cell.photo;
					cell.photo = bitmap;

					if (tmpPhoto != null && tmpPhoto != bitmap
							&& !tmpPhoto.isRecycled()) {
						tmpPhoto.recycle();
						tmpPhoto = null;
						System.gc();
					}
					
					if(DeviceUtils.getAPILevel()<android.os.Build.VERSION_CODES.HONEYCOMB)
					{
						double redians = cell.getAngle() * 3.14159265 / 180;
						Matrix localMatrix = new Matrix();
						localMatrix.postRotate(cell.getAngle());
						cell.curHeight = (int) Math.max(
								Math.abs(cell.initHeight * Math.cos(redians)
										- cell.initWidth * Math.sin(redians)),
								Math.abs(cell.initHeight * Math.cos(redians)
										+ cell.initWidth * Math.sin(redians)));
	
						float temp = (float) Math.min(
								Math.abs(cell.initHeight * Math.cos(redians)
										- cell.initWidth * Math.sin(redians)),
								Math.abs(cell.initHeight * Math.cos(redians)
										+ cell.initWidth * Math.sin(redians)));
	
						cell.curWidth = (float) Math.sqrt(cell.initHeight * cell.initHeight
								+ cell.initWidth * cell.initWidth - temp * temp);
						AFLog.d("in process, rotate, " + " orig photo width:" + cell.photo.getWidth() + " orig photo height: " + cell.photo.getHeight());

						Bitmap tmpBitmap = Bitmap.createBitmap(cell.photo, 0, 0,
								cell.photo.getWidth(), cell.photo.getHeight(), localMatrix,
								true);
						if (cell.photo != tmpBitmap && !cell.photo.isRecycled()) {
							frameCellViews.get(curSelect).setImageBitmap(null);
							cell.photo.recycle();
							cell.photo = null;
							System.gc();
						}
	
						float x = tmpBitmap.getWidth() > cell.curWidth ? tmpBitmap
								.getWidth() / 2 - cell.curWidth / 2 : 0;
						float y = tmpBitmap.getHeight() > cell.curHeight ? tmpBitmap
								.getHeight() / 2 - cell.curHeight / 2 : 0;
						float w = tmpBitmap.getWidth() > cell.curWidth ? cell.curWidth
								: tmpBitmap.getWidth();
						float h = tmpBitmap.getHeight() > cell.curHeight ? cell.curHeight
								: tmpBitmap.getHeight();
	
						cell.photo = Bitmap.createBitmap(tmpBitmap, (int) x, (int) y,
								(int) w, (int) h);
						AFLog.d("In process,rotate, photoWidth:" + tmpBitmap.getWidth() + " photoHeight:" + tmpBitmap.getHeight()  +"  width: " + cell.curWidth + " height: " + cell.curHeight);
						AFLog.d("In process,rotate, x: " + x + " y: " + y + " w: " + h +  " h: " + h);

						
						if (cell.photo != tmpBitmap && !tmpBitmap.isRecycled()) {
							tmpBitmap.recycle();
							tmpBitmap = null;
							System.gc();
						}

					}
					frameCellViews.get(curSelect).setImageBitmap(cell.photo);

					updateCellView(curSelect);

					cell.setFilterIndex(filter_index);
				}
			}
			((ProcessActivity) mActivity).hideIndicator();
			// progressViews.get(curSelect).setVisibility(View.INVISIBLE);
		}

	};
}
