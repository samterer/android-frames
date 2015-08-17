package com.androidfuture.frames.data;

import com.androidfuture.network.AFData;

public class MoreFramesAppData extends AFData {
	private String appName;
	
	private String appId;
	
	private String appIconUrl;
	
	private String appDownloadUrl;
	
	private String previewUrl;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppIconUrl() {
		return appIconUrl;
	}

	public void setAppIconUrl(String appIconUrl) {
		this.appIconUrl = appIconUrl;
	}

	public String getAppDownloadUrl() {
		return appDownloadUrl;
	}

	public void setAppDownloadUrl(String appDownloadUrl) {
		this.appDownloadUrl = appDownloadUrl;
	}
	
	public void setPreviewUrl(String url)
	{
		this.previewUrl = url;
	}
	
	public String getPreviewUrl()
	{
		return this.previewUrl;
	}
}
