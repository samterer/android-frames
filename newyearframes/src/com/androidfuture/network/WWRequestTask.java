package com.androidfuture.network;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.androidfuture.tools.AFLog;



public class WWRequestTask extends AsyncTask<String, Long, ArrayList<AFData>> {
	public static final int ERROR_INVALID_URL = 401;
	public static final int ERROR_CONTENT_EMPTY = 402;

	private Context mContext;

	private RequestTaskListener mListener;

	private WWHttpRequest mRequest;

	private AFParser mParser;

	public interface RequestTaskListener {
		void onRequestCancel(String url);

		void onRequestFail(String url, int error);

		void onRequestProgress(long progress);

		void onRequestFinish(String url, ArrayList<AFData> resultArray,
				int totalNum);
	}

	public WWRequestTask(Context context, String url) {
		super();
		mContext = context;
		mRequest = new WWHttpRequest(context);
		mRequest.mUrl = url;
		mRequest.mType = WWHttpRequest.RequestType.REQUEST_TYPE_GET;
	}

	// upload bitmap
	public WWRequestTask(Context context, String url,
			HashMap<String, String> params, Bitmap bitmap) {
		super();
		mContext = context;
		mRequest = new WWHttpRequest(context);
		mRequest.mUrl = url;
		mRequest.mParams = params;
		mRequest.mContentKey = "pic";
		mRequest.mContentName = "photo.jpg";
		mRequest.mContentType = "image/jpeg";
		mRequest.mType = WWHttpRequest.RequestType.REQUEST_TYPE_POST;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		mRequest.mData = baos.toByteArray();
	}

	// upload bitmap
	public WWRequestTask(Context context, String url,
			HashMap<String, String> params) {
		super();
		mContext = context;
		mRequest = new WWHttpRequest(context);
		mRequest.mUrl = url;
		mRequest.mParams = params;
		mRequest.mType = WWHttpRequest.RequestType.REQUEST_TYPE_POST;
	
	}

	// upload file
	public WWRequestTask(Context context, String url,
			HashMap<String, String> params, File file) {
		// to do
		super();
		mContext = context;
	}

	@Override
	protected ArrayList<AFData> doInBackground(String... params) {
		String result = mRequest.startRequest();
		if (result != null) {
			ArrayList<AFData> resultArray = new ArrayList<AFData>();
			if (mParser != null) {
				resultArray = AFParserHelper.readData(result, mParser);
				return resultArray;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	protected void onProgressUpdate(Long... progress) {
		// WWLog.d(TAG,"progress: " + progress);
		if (mListener != null)
			mListener.onRequestProgress(progress[0]);

	}

	protected void onPostExecute(ArrayList<AFData> result) {
		AFLog.d("cancel: " + this.isCancelled());

		if (result == null) {

			if (mListener != null)
				mListener.onRequestFail(mRequest.mUrl, mRequest.mErrorCode);
		} else {

			if (mListener != null) {
				mListener.onRequestFinish(mRequest.mUrl, result,
						mParser == null ? 0 : mParser.getTotalNum());
			}
		}
	}

	protected void onCancelled() {
		AFLog.d("cancel download");
		mListener.onRequestCancel(mRequest.mUrl);

	}

	public void setListener(RequestTaskListener l) {
		this.mListener = l;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public void setParser(AFParser parser) {
		mParser = parser;

	}

	private String clearRichText(String in) {
		in = in.replaceAll("&lt;", "");
		in = in.replaceAll("&gt;", "");
		return in;
	}
}
