package com.androidfuture.network;

/**
 * WWHttpRequest.java
 * Deal with request to server, including post and get
 * 
 * cschen
 * 2012.5.2
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.tools.AFLog;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;



public class WWHttpRequest {
	int mErrorCode;

	String mErrorMessage;

	Context mContext;

	public RequestType mType;

	public String mUrl;

	public HashMap<String, String> mParams;

	public String mContentType;

	public String mContentName;

	public String mContentKey;

	public byte[] mData;

	public enum RequestType {
		REQUEST_TYPE_POST, REQUEST_TYPE_GET
	}

	public WWHttpRequest(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * start request, before which, url/arams data must be set
	 * 
	 * @return
	 */
	public String startRequest() {
		if (mType == RequestType.REQUEST_TYPE_POST) {
			if(mData == null)
				return post(mUrl,mParams);
			else
				return post(mUrl, mParams, mContentType, mContentName, mContentKey,
					mData);
		} else {
			return request(mUrl);
		}
	}
	
	private String post(String url, HashMap<String, String> paramsMap) {
		AFLog.d("SendMultipartFile");
		HttpParams httpParameters = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
		
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				"Android");
		
		/*
		httpclient.getParams().setParameter("Wenwen", Constants.URL_HOST);
		httpclient.getParams().setParameter("Cookie", this.makeCookies());
		*/
		HttpPost httppost = new HttpPost(url);
		ArrayList<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		
		if (paramsMap != null) {
			Iterator iter = paramsMap.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry) iter.next();
				paramsList.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));//URLEncoder.encode(entry.getValue()));
			}
		}
		
		UrlEncodedFormEntity ueEntity = null;
		try {
			ueEntity = new UrlEncodedFormEntity(paramsList,HTTP.UTF_8);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/*
		BufferedReader r;
		try {
			r = new BufferedReader(
					new InputStreamReader(
							ueEntity.getContent(),
							Charset.forName(HTTP.UTF_8)));
			for (String s = r.readLine(); s != null; s = r.readLine()) {
				AFLog.d("entity: " + s);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/

		//HttpProtocolParams.setHttpElementCharset(httpParameters, "UTF-8");
		
		httppost.setEntity(ueEntity);
		//httppost.setHeader("Wenwen",Constants.URL_HOST);
		httppost.setHeader("Cookie",this.makeCookies());
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		HttpResponse response;
		try {

			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			int status = response.getStatusLine().getStatusCode();
			AFLog.d("status:" + status);
			StringBuilder builder = new StringBuilder();
			if (status == 200||status == 400||status == 500) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(),
								Charset.forName("UTF-8")));
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					builder.append(s);
				}
				AFLog.d(builder.toString());

				return builder.toString();
			} else {
				return null;
			}
		} catch (ClientProtocolException e) {
			mErrorCode = 1;
			mErrorMessage = "协议错误";
			e.printStackTrace();
		} catch (IOException e) {
			mErrorCode = 2;
			mErrorMessage = "网络访问错误";
			e.printStackTrace();
		}
		return null;
	}

	private String post(String url, HashMap<String, String> paramsMap,
			String contentType, String contentName, String contentKey,
			byte[] data) {
		AFLog.d("SendMultipartFile");
		HttpParams httpParameters = new BasicHttpParams();
		HttpProtocolParams.setContentCharset(httpParameters, HTTP.UTF_8);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParameters);

		httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				"Android");
		/*
		httpclient.getParams().setParameter("Wenwen", Constants.URL_HOST);
		httpclient.getParams().setParameter("Cookie", this.makeCookies());
		*/
		HttpPost httppost = new HttpPost(url);
		MultipartEntity mpEntity = new MultipartEntity();
		if (paramsMap != null) {
			Iterator iter = paramsMap.entrySet().iterator();

			while (iter.hasNext()) {
				Map.Entry<String, String> entry = (Map.Entry) iter.next();
				try {
					mpEntity.addPart(entry.getKey(),
							new StringBody(entry.getValue()));
				} catch (UnsupportedEncodingException e) {
					AFLog.d("unsupoerted encoding exception: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		ContentBody dataBody = new ByteArrayBody(data, contentType, contentName);
		mpEntity.addPart(contentKey, dataBody);
		AFLog.i("file length = " + data.length);

		httppost.setEntity(mpEntity);
		//httppost.setHeader("Wenwen",Constants.URL_HOST);
		httppost.setHeader("Cookie",this.makeCookies());
		HttpResponse response;
		try {

			response = httpclient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			int status = response.getStatusLine().getStatusCode();
			AFLog.d("status:" + status);
			StringBuilder builder = new StringBuilder();
			if (status == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(),
								Charset.forName("UTF-8")));
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					builder.append(s);
				}
				AFLog.d(builder.toString());

				return builder.toString();
			} else {
				return null;
			}
		} catch (ClientProtocolException e) {
			mErrorCode = 1;
			mErrorMessage = "协议错误";
			e.printStackTrace();
		} catch (IOException e) {
			mErrorCode = 2;
			mErrorMessage = "网络访问错误";
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param url
	 * @return request result
	 */
	private String request(String url) {
		HttpClient client = new DefaultHttpClient();

		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
				"Android");


		StringBuilder builder = new StringBuilder();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				Constants.NETWORT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParameters,
				Constants.NETWORT_TIMEOUT);
		client = new DefaultHttpClient(httpParameters);
		AFLog.d("request: " + url);
		HttpGet myget = new HttpGet(url);
		//myget.setHeader("Wenwen",Constants.URL_HOST);
		myget.setHeader("Cookie",this.makeCookies());
		HttpResponse response;
		try {
			response = client.execute(myget);
			//DebugTools.causeRandomNetworkError();
			int status = response.getStatusLine().getStatusCode();
			AFLog.d("status:" + status);
			if (status == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(),
								Charset.forName("UTF-8")));
				for (String s = reader.readLine(); s != null; s = reader
						.readLine()) {
					builder.append(s);
				}
				AFLog.d(builder.toString());

				return builder.toString();
			} else {
				mErrorCode = status;
				mErrorMessage = "网络访问错误";
			}
		} catch (ClientProtocolException e) {
			mErrorCode = 1;
			mErrorMessage = "协议错误";
			AFLog.e("error:" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			mErrorCode = 2;
			mErrorMessage = "网络访问错误";
			AFLog.e("error:" + e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public int getErrorCode() {
		return mErrorCode;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	/**
	 * 
	 * @return cookies string
	 */
	private String makeCookies() {
		/*
		if (mContext == null)
			return "";
		WWUserLoginInfo loginUser = AFLoginManager.instance()
				.getLoginUserInfo();
		if(loginUser == null)
			return "";
		StringBuilder str = new StringBuilder();

		str.append("LSKEY=" + loginUser.getTicket());
		str.append("; uid=" + loginUser.getUid());
		
		return str.toString();
		*/
		return "";
	}
}
