package com.androidfuture.frames.data;

import com.androidfuture.network.AFData;


public class AFAppPush extends AFData {
	private int status;

	private String title;
	
	private String desc;
	
	private String url;
	
	private String icon;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getTitle() {
		return title;
	}
	

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setIcon(String iconUrl)
	{
		icon = iconUrl;
	}

	public String getIcon()
	{
		return icon;
	}
	
	
}
