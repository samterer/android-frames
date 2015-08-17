package com.androidfuture.frames.data;

import com.androidfuture.imagefilter.IImageFilter;

public class FilterInfo {
	public int filterID;
	public IImageFilter filter;
	public String filterName;

	public FilterInfo(int filterID, IImageFilter filter, String filterName) {
		this.filterID = filterID;
		this.filter = filter;
		this.filterName = filterName;
	}
}