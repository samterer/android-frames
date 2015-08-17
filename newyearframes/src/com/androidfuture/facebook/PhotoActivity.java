package com.androidfuture.facebook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.androidfuture.cacheimage.Picture;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.R;
import com.androidfuture.tools.StringUtils;
import com.androidfuture.tools.AFLog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoActivity extends Activity implements OnClickListener {

	private static final int MSG_FILE_FAIL = 201;
	private static final int MSG_FILE_FINISH = 202;
	private static final int MSG_FILE_START = 203;
	private static final int MSG_FILE_PROGRESS = 204;
	private static final int MSG_FILE_CANCEL = 205;
	
    ProgressDialog progressDialog;
    private long fileSize;
    private boolean mCancel;
	private String mImageUrl;
	private Bitmap bitmap;
	private long downLoadFileSize;
	private ImageView imageView;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.preview_layout);
		
		mImageUrl = getIntent().getStringExtra("url");
		Button btn = (Button)findViewById(R.id.preview_share);
		btn.setOnClickListener(this);
		btn.setText(R.string.ok);
		
        findViewById(R.id.preview_cancel).setOnClickListener(this);
        imageView = (ImageView)findViewById(R.id.preview_image);
		startLoad();
	}

	@Override
	public void onClick(View view) {
		Intent intent = getIntent();
		switch(view.getId())
		{
		case R.id.preview_share:
			Bundle b = new Bundle();
			b.putParcelable("data", bitmap);
			intent.putExtras(b);
			setResult(RESULT_OK, intent); 
			finish();
			break;
		case R.id.preview_cancel:
			AFLog.d("cancel");
			mCancel = true;
			setResult(RESULT_CANCELED, intent); 
			finish();
			break;
		}
		
	}
	
	
	private void startLoad() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			String str = this.getResources().getString(
					R.string.download_facebookphoto_progress);
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
				progressDialog.setMax((int)fileSize/1000);
				break;
			case MSG_FILE_FINISH:

				stopLoad();
				String fileName = FrameApp.imageCacheDir + File.separator
						+ StringUtils.getAFFileName(mImageUrl);
				;
			
				System.gc();
				bitmap = Picture.getBitmapFromFile(fileName, 2);
				if (bitmap == null) {
					String str = getResources().getString(
							R.string.fail_load_frame);
					Toast.makeText(PhotoActivity.this, str,
							Toast.LENGTH_SHORT).show();
					return;
				}
				imageView.setImageBitmap(bitmap);
				break;
			case MSG_FILE_PROGRESS: {
				// Bundle data = msg.getData();
				progressDialog.setProgress((int)downLoadFileSize/1000);
			}
				break;
			case MSG_FILE_CANCEL:
				String str = PhotoActivity.this.getResources().getString(
						R.string.download_cancel);
				Toast.makeText(PhotoActivity.this, str, Toast.LENGTH_SHORT)
						.show();
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


}
