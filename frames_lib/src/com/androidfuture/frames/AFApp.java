package com.androidfuture.frames;

import android.app.Application;
import com.androidfuture.frames.data.LocalFrameData;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;


public abstract class AFApp extends Application{
	abstract public  ArrayList<LocalFrameData> GetLocalFrames();

	abstract public  ArrayList<LocalFrameData> GetMultFrames();

	abstract public ArrayList<LocalFrameData> GetLocalPins();

	abstract public ArrayList<LocalFrameData> GetLocalGrids();

	abstract public AFConf GetConf();

	Tracker mTracker;

	/**
	 * Gets the default {@link Tracker} for this {@link Application}.
	 * @return tracker
	 */
	synchronized public Tracker getDefaultTracker() {
		if (mTracker == null) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			// To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
			mTracker = analytics.newTracker(GetConf().TrackId);
		}
		return mTracker;
	}
}
