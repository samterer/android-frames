package com.androidfuture.frames.data;

public class LocalFrameData extends FrameData {
	public LocalFrameData() {

	}

	public LocalFrameData(int frameId, int cat, String url,
			String thumbUrl,  int res, int thumbRes) {
		super(frameId,cat,url,thumbUrl,1, new FrameCell[] {
				new FrameCell(0.0f,0.0f,1.0f,1.0f, 0)
				});
		mThumbRes = thumbRes;
		mRes = res;		
		mIsLocal = true;
	} 
	public LocalFrameData(int frameId, int cat, String url,
			String thumbUrl, int frameNum, FrameCell[] frameCells, int res,int thumbRes) {
		super(frameId,cat,url,thumbUrl,frameNum,frameCells);
		mThumbRes = thumbRes;
		mRes = res;
		mIsLocal = true;
	}

}
