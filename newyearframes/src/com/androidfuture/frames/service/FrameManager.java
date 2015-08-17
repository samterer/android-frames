package com.androidfuture.frames.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.FrameApp;
import com.androidfuture.newyear.framesfree.LocalFramesConfig;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.tools.HttpUtils;
import com.androidfuture.frames.tools.NetWork;
import com.androidfuture.tools.AFLog;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;



public class FrameManager{
	
	private static final String TAG = "FrameManager";
	
	public	 static final int RESP_FAIL = -1;
	
	public static final int RESP_END  = 0;
	
	public static final int RESP_OK = 1;
	private Application mContext;
	private int mEnd;
	private final int CAT_NUM = 6;
	
	private boolean isFirstLoad;
	
	private HashMap<Integer,ArrayList>  listMap; 
	public FrameManager(Application context){
		listMap = new HashMap<Integer,ArrayList> ();
		
		
		
		mContext = context;
		isFirstLoad = true;
		mEnd = 0;
	}
	
	
	/**
	 * get frame list 
	 * @param list
	 * @param tab
	 * @param start
	 * @param listLen
	 */
	public int getFrameList(ArrayList list,int tab,int start,int listLen) {
		// TODO Auto-generated method stub
		list.clear();
		int resp = this.RESP_FAIL;
    	//String deviceID = ((TelephonyManager)this.mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    	PackageManager manager = this.mContext.getPackageManager();
    	PackageInfo info = null;
		try {
			info = manager.getPackageInfo(
					mContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int version = 1;
		if(info != null)
			version = info.versionCode;

		String urlString = null;
		URL url;
		FrameData frame = null;
		//int language = ((PictureNewsApp)this.mContext).getAppPreference().getLanuage();
		int language = 0;
		//Log.d("Tab: ",""+tab);
	
		if(tab==Constants.TAB_FAVORITE)
		{
			//favorite picture news list
			list.addAll(((FrameApp)mContext).getFavManager().getFavFrameList());
			resp = this.RESP_OK;
		}
		else if(tab == Constants.TAB_HISTORY)
		{
			//Log.d(TAG,"" + ((FrameApp)mContext).getHistManager());
			//Log.d(TAG,""+((FrameApp)mContext).getHistManager().getBrowseHistoryList());
			list.addAll(((FrameApp)mContext).getHistManager().getBrowseHistoryList());
			resp = this.RESP_OK;
		}
		else
		{
			//Log.d(TAG,"first load start");
			if(isFirstLoad && tab == Constants.TAB_HOT)
			{
				//Log.d(TAG,"start first load start");
				ArrayList arrayList = new ArrayList();
				for(FrameData item: LocalFramesConfig.LocalFrames)
				{
					arrayList.add(item);
				}
				listMap.put(Constants.TAB_HOT, arrayList);
			    list.addAll(arrayList);
				isFirstLoad =false;
				//Log.d(TAG,"end first load start");
				resp = this.RESP_OK;
			}else 
			{
				if(NetWork.isNetworkAvailable(this.mContext))
				{
					if(listMap.get(tab) == null)
					{
						listMap.put(tab, new ArrayList());
					}
					StringBuilder strBuilder = new StringBuilder();
					strBuilder.append(Constants.URLRoot );
					strBuilder.append("/list.php");
					
					strBuilder.append("?s=");
					if(tab == Constants.TAB_HOT && start >= LocalFramesConfig.LocalFrames.length)
						start = start-LocalFramesConfig.LocalFrames.length;
					strBuilder.append(start);
	
					strBuilder.append("&n=");
					strBuilder.append(listLen);
					
					strBuilder.append("&v=");
					strBuilder.append(version);
	
					strBuilder.append("&c=");
					
					//frame type
					strBuilder.append(Constants.cat);
					
					strBuilder.append("&sort=");
					strBuilder.append(tab);
					
					
					urlString = strBuilder.toString();
					AFLog.d("url " + urlString);
					ArrayList tmp =  downloadList(urlString);
					list.addAll(tmp);
					//Log.d("Size:", ""+tmp.size());
					listMap.get(tab).addAll(tmp);
					
					resp = this.RESP_OK;
				}else
				{
					resp = this.RESP_FAIL;
				}
			}
			
		}

		return resp ;
	}

	
	public static ArrayList downloadList(String urlStr)
	{
		ArrayList list = new ArrayList();
		
		JSONObject jsonObj = HttpUtils.request(urlStr);
		
		try {
			if(jsonObj != null )//&& jsonObj.getBoolean("success"))
			{	
				JSONArray array = jsonObj.getJSONArray("data");
				for(int i = 0;i < array.length();i ++)
				{
					FrameData frame = new FrameData();
					JSONObject obj= (JSONObject) array.opt(i);
					frame.setFrameId(obj.getInt("frame_id"));
					frame.setFrameUrl(obj.getString("frame_url"));
					frame.setFrameThumbUrl(obj.getString("frame_thumb_url"));
					frame.setFrameCat(obj.getInt("frame_cat"));
					frame.setUseNum(obj.getInt("frame_use_num"));
					frame.setFavNum(obj.getInt("frame_fav_num"));
					
					list.add(frame);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG,"json parse error");
		}
		return list;
	}
	
}
