package com.androidfuture.network;

import java.util.ArrayList;

import org.json.JSONObject;



public abstract class AFJSONParser implements AFParser{
	protected ArrayList<AFData> objects;
	protected int totalNum;
	public ArrayList<AFData> getObjects()
	{
		return objects;
	}
	
	public int getTotalNum()
	{
		return totalNum;
	}
	abstract public ArrayList<AFData> parser(JSONObject jsonObj);
}
