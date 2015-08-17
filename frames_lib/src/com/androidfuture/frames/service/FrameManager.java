package com.androidfuture.frames.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.data.FrameCell;
import com.androidfuture.frames.data.FrameData;
import com.androidfuture.frames.tools.HttpUtils;
import com.androidfuture.frames.tools.NetWork;
import com.androidfuture.tools.AFLog;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.util.Log;




public class FrameManager{
	
	//private static final String TAG = "FrameManager";
	
	public	 static final int RESP_FAIL = -1;
	
	public static final int RESP_END  = 0;
	
	public static final int RESP_OK = 1;
	
	private Application mContext;
	
	private Bitmap previewBitmap;
	
	public Bitmap getPreviewBitmap() {
		return previewBitmap;
	}

	public void setPreviewBitmap(Bitmap previewBitmap) {
		this.previewBitmap = previewBitmap;
	}


	private HashMap<Integer,ArrayList<FrameData>>  listMap; 
	
	private static FrameManager instance;
	public static FrameManager GetInstance() {
		if(instance == null)
		{
			instance = new FrameManager((Application)AFAppWrapper.GetInstance().GetApp());
		}
		
		return instance;
	}
	
	public FrameManager(Application context){
		listMap = new HashMap<Integer,ArrayList<FrameData>> ();
		mContext = context;
	}
	
	
	/**
	 * get frame list 
	 * @param list
	 * @param tab
	 * @param start
	 * @param listLen
	 */
	public int getFrameList(ArrayList<FrameData> list,int tab,int start,int listLen) {
		// TODO Auto-generated method stub
		list.clear();
		int resp = RESP_FAIL;
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
		switch(tab)
		{
			case Constants.TAB_FAVORITE:
			{
				//favorite picture news list
				list.addAll(FavoriteManager.GetInstance().getFavFrameList());
				resp = RESP_OK;
			}
			break;
			/*
			case Constants.TAB_HISTORY:
			{
				list.addAll(((FrameApp)mContext).getHistManager().getBrowseHistoryList());
				resp = RESP_OK;
			}
			break;
			*/
			case Constants.TAB_HOT:
			case Constants.TAB_NEW:
			case Constants.TAB_MULTI:
			{
				if(start == 0 && ( tab == Constants.TAB_HOT || tab == Constants.TAB_MULTI) )
				{
					
					ArrayList<FrameData> arrayList = new ArrayList<FrameData>();
					if(tab == Constants.TAB_HOT)
					{
						for(FrameData item: AFAppWrapper.GetInstance().GetApp().GetLocalFrames())
						{
							arrayList.add(item);
						}
					}else if(tab == Constants.TAB_MULTI)
					{
						for(FrameData item: AFAppWrapper.GetInstance().GetApp().GetMultFrames())
						{
							arrayList.add(item);
						}
					}
					listMap.put(tab, arrayList);
				    list.addAll(arrayList);
					AFLog.d("end first load start");
					resp = RESP_OK;
				}else 
				{
					if(NetWork.isNetworkAvailable(this.mContext))
					{
						if(listMap.get(tab) == null)
						{
							listMap.put(tab, new ArrayList<FrameData>());
						}
						StringBuilder strBuilder = new StringBuilder();
						strBuilder.append(Constants.URLRoot );
						strBuilder.append("/framelist.php");
						strBuilder.append("?s=");
						if(start >= AFAppWrapper.GetInstance().GetApp().GetLocalFrames().size() && tab == Constants.TAB_HOT)
						{
								start = start-AFAppWrapper.GetInstance().GetApp().GetLocalFrames().size();
						}
						
						if(start >= AFAppWrapper.GetInstance().GetApp().GetMultFrames().size() && tab == Constants.TAB_MULTI)
						{
								start = start-AFAppWrapper.GetInstance().GetApp().GetMultFrames().size() ;
						}
						strBuilder.append(start);
		
						strBuilder.append("&n=");
						strBuilder.append(listLen);
						
						strBuilder.append("&v=");
						strBuilder.append(version);
		
						strBuilder.append("&c=");
						strBuilder.append(2);//frame type
						
						strBuilder.append("&tab=");
						strBuilder.append(tab);
						
						
						urlString = strBuilder.toString();
						AFLog.d("url " + urlString);
						ArrayList<FrameData> tmp =  downloadList(urlString);
						list.addAll(tmp);
						//Log.d("Size:", ""+tmp.size());
						listMap.get(tab).addAll(tmp);
						
						resp = RESP_OK;
					}else
					{
						resp = RESP_FAIL;
					}
				}
			
			}
			break;
		}

		return resp;
	}

	
	public static ArrayList<FrameData> downloadList(String urlStr)
	{
		ArrayList<FrameData> list = new ArrayList<FrameData>();
		
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
					ArrayList<FrameCell> cells = new ArrayList<FrameCell>();
					JSONArray cellArray = (JSONArray)obj.getJSONArray("frame_cells");
					if(cellArray.length() > 0)
					{
						for(int index = 0; index < cellArray.length(); index ++)
						{
							JSONObject jsonItem = (JSONObject)cellArray.opt(index);
							if(jsonItem != null)
							{
							FrameCell cell = new FrameCell();
							cell.setTopRate((float)jsonItem.getDouble("cell_top"));
							cell.setLeftRate((float)jsonItem.getDouble("cell_left"));
							cell.setWidthRate((float)jsonItem.getDouble("cell_width"));
							cell.setHeightRate((float)jsonItem.getDouble("cell_height"));
							cell.setAngle((int)jsonItem.getInt("cell_angle"));
							cell.setOrder((int)jsonItem.getInt("cell_order"));
							AFLog.d("left: " + cell.getLeftRate());
							cells.add(cell);
							}

						}
					}else
					{
						FrameCell cell = new FrameCell();
						cell.setTopRate(0.0f);
						cell.setLeftRate(0.0f);
						cell.setWidthRate(1.0f);
						cell.setHeightRate(1.0f);
						cell.setAngle(0);
						cell.setOrder(1);
						cells.add(cell);
					}
						
					AFLog.d("cell size: "  + cells.size());
					frame.setmFrameCells(cells.toArray(new FrameCell[0]));
					list.add(frame);
					AFLog.d("size : " + list.size());
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			AFLog.e("Json parse error");
		}
		return list;
	}


	public void getFrameCacheList(ArrayList<FrameData> cacheList, int mTab) {
		if(listMap.get(mTab) != null)
			cacheList.addAll(listMap.get(mTab));
		return;
	}



	
}
