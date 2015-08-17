package com.androidfuture.frames.data;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidfuture.network.AFData;
import com.androidfuture.network.AFJSONParser;
import com.androidfuture.tools.AFLog;



public class AFAppPushParser extends AFJSONParser {


	@Override
	public ArrayList<AFData> parser(JSONObject jsonObj) {
		objects = new ArrayList<AFData>();
		try {
			AFAppPush data = new AFAppPush();
			data.setStatus(jsonObj.getInt("status"));
			
			if(data.getStatus() == 1)
			{
				data.setIcon(jsonObj.getString("icon"));
				data.setDesc(jsonObj.getString("desc"));
				data.setTitle(jsonObj.getString("title"));
				data.setUrl(jsonObj.getString("url"));
				this.totalNum = 1;
				this.objects.add(data);
			}
			
			
		} catch (JSONException e) {
			//e.printStackTrace();
			AFLog.e("fail ");
		}
		return objects;
	}


}
