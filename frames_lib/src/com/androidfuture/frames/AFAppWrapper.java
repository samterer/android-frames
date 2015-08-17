package com.androidfuture.frames;

public class AFAppWrapper {
	static AFAppWrapper instance;
	
	private AFApp app;
	
	static boolean isInit;
	public static AFAppWrapper GetInstance()
	{
		if(instance == null)
		{
			instance = new AFAppWrapper();
		}
		return instance;
	}
	
	public static void Init(AFApp app_)
	{
		if(isInit && instance != null)
		{
			return;
		}
		if(instance == null)
		{
			instance = new AFAppWrapper();
		}
		instance.app = app_;
		isInit = true;
	}
	
	public AFApp GetApp()
	{
		return app;
	}
}
