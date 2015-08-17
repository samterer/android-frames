package com.androidfuture.frames.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.androidfuture.imagefilter.IImageFilter;
import com.androidfuture.network.AFData;

public class FrameCell extends AFData {
	int angle;

	float widthRate;

	float heightRate;

	float leftRate;

	float topRate;

	int order;

	int offsetX;

	int offsetY;

	public Bitmap photo;

	public int getPhotoRes() {
		return photoRes;
	}

	public void setPhotoRes(int photoRes) {
		this.photoRes = photoRes;
		this.photoFile = null;
		this.photo = null;
	}

	private String photoFile;
	
	private int photoRes;
	

	public float rate;

	/*
	 * Frame Cell Data for Draw
	 */

	public int origWidth;

	public int origHeight;

	public float curWidth;

	public float curHeight;

	public float initWidth;

	public float initHeight;

	public int xCenter;

	public int yCenter;

	public int filterIndex;

	public FrameCell() {

	}

	public FrameCell(float leftRate, float topRate, float widthRate,
			float heightRate, int angle) {
		this.widthRate = widthRate;

		this.heightRate = heightRate;

		this.leftRate = leftRate;

		this.topRate = topRate;

		this.angle = angle;

		this.rate = 1;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {

		this.angle = angle;
	}

	public float getWidthRate() {
		return widthRate;
	}

	public void setWidthRate(float widthRate) {
		this.widthRate = widthRate;
	}

	public float getHeightRate() {
		return heightRate;
	}

	public void setHeightRate(float heightRate) {
		this.heightRate = heightRate;
	}

	public float getLeftRate() {
		return leftRate;
	}

	public void setLeftRate(float leftRate) {
		this.leftRate = leftRate;
	}

	public float getTopRate() {
		return topRate;
	}

	public void setTopRate(float topRate) {
		this.topRate = topRate;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int level) {
		this.order = level;
	}

	public Bitmap getPhoto() {
		return photo;
	}

	public void setPhoto(Bitmap mphoto) {
		if (photo != null && mphoto != photo) {
			this.photo.recycle();
		}
		this.photo = mphoto;
	}

	public void setFilterIndex(int index) {
		this.filterIndex = index;
	}

	public int getFilterIndex() {
		return this.filterIndex;
	}

	public void adjustAngle(int angleDelta) {
		angle = angle + angleDelta;

	}

	public void adjustSize(float vRate) {
		if (vRate > 0.5 && vRate < 2.0)
			rate = vRate;
	}

	public float getRate() {
		return rate;
	}

	public void adjustPosition(int deltaX, int deltaY) {
		this.offsetX += deltaX;
		this.offsetY += deltaY;
		this.xCenter += deltaX;
		this.yCenter += deltaY;
	}

	public void adjustPosition(int deltaX, int deltaY, int viewWidth,
			int viewHeight) {
		if ((xCenter + deltaX - initWidth * rate / 2) > 0) {
			deltaX = (int) (initWidth * rate / 2 - xCenter);
		}

		if ((viewWidth * widthRate - xCenter - deltaX - initWidth * rate / 2) > 0) {
			deltaX = (int) (viewWidth * widthRate - xCenter - initWidth * rate
					/ 2);
		}
		if (yCenter + deltaY - initHeight * rate / 2 > 0) {
			deltaY = (int) (initHeight * rate / 2 - yCenter);
		}

		if (viewHeight * heightRate - yCenter - deltaY - initHeight * rate / 2 > 0) {
			deltaY = (int) (viewHeight * heightRate - yCenter  - initHeight
					* rate / 2);
		}

		this.offsetX += deltaX;
		this.offsetY += deltaY;
		this.xCenter += deltaX;
		this.yCenter += deltaY;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public String getPhotoFile() {
		return photoFile;
	}

	public void setPhotoFile(String photoFile) {
		this.photoRes = 0;
		this.photo = null;
		this.photoFile = photoFile;
	}

	public static final Parcelable.Creator<FrameCell> CREATOR = new Parcelable.Creator<FrameCell>() {
		public FrameCell createFromParcel(Parcel p) {
			return new FrameCell(p);
		}

		@Override
		public FrameCell[] newArray(int size) {
			// TODO Auto-generated method stub
			return new FrameCell[size];
		}
	};

	public void calSize(float width, float height) {

		initWidth = photo.getWidth();
		initHeight = photo.getHeight();
		float maskWidth = widthRate * width;
		float maskHeight = heightRate * height;
		if (initWidth / initHeight > maskWidth / maskHeight) {
			rate = maskHeight / initHeight;
		} else {
			rate = maskWidth / initWidth;
		}
		xCenter = (int) (maskWidth / 2);
		yCenter = (int) (maskHeight / 2);
		offsetX = 0;
		offsetY = 0;
	}

	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(angle);
		parcel.writeFloat(widthRate);
		parcel.writeFloat(heightRate);
		parcel.writeFloat(leftRate);
		parcel.writeFloat(topRate);
		parcel.writeInt(offsetX);
		parcel.writeInt(offsetY);
		parcel.writeFloat(rate);
		parcel.writeFloat(initWidth);
		parcel.writeFloat(initHeight);
		parcel.writeString(photoFile);
		parcel.writeInt(filterIndex);
		parcel.writeInt(photoRes);
	}

	public FrameCell(Parcel parcel) {
		this.angle = parcel.readInt();
		this.widthRate = parcel.readFloat();
		this.heightRate = parcel.readFloat();
		this.leftRate = parcel.readFloat();
		this.topRate = parcel.readFloat();
		this.offsetX = parcel.readInt();
		this.offsetY = parcel.readInt();
		this.rate = parcel.readFloat();
		this.initWidth = parcel.readFloat();
		this.initHeight = parcel.readFloat();
		this.photoFile = parcel.readString();
		this.filterIndex = parcel.readInt();
		this.photoRes = parcel.readInt();
	}

	public void reset() {
		this.angle = 0;
		this.offsetX = 1;
		this.offsetY = 1;
		this.rate = 1;
	}
}
