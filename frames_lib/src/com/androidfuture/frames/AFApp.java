package com.androidfuture.frames;

import java.util.ArrayList;

import com.androidfuture.frames.data.LocalFrameData;


public interface AFApp{
	ArrayList<LocalFrameData> GetLocalFrames();
	
	ArrayList<LocalFrameData> GetMultFrames();
	
	ArrayList<LocalFrameData> GetLocalPins();
	
	ArrayList<LocalFrameData> GetLocalGrids();
	
	AFConf GetConf();
}
