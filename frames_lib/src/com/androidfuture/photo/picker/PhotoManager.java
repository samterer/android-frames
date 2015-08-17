package com.androidfuture.photo.picker;

import java.util.ArrayList;

import com.androidfuture.data.AFPhotoData;




public class PhotoManager {
	private static PhotoManager instance;
	private ArrayList<AFPhotoData> selectedPhoto;
	private ArrayList<OnChosePhotoChangeListener> listener;
	private int selecteMax = 0;
	interface OnChosePhotoChangeListener
	{
		void onChosePhotoChange();
	}
	public static PhotoManager GetInstance() {
		if (instance == null) {
			instance = new PhotoManager();
		}
		return instance;
	}

	private PhotoManager() {
		selectedPhoto = new ArrayList<AFPhotoData>();
		listener = new ArrayList<OnChosePhotoChangeListener>();
	}
	
	public void addListener(OnChosePhotoChangeListener l)
	{
		listener.add(l);
	}
	
	public void removeListener(OnChosePhotoChangeListener l)
	{
		listener.remove(l);
	}
	
	public void clearSelected() {
		selectedPhoto.clear();
	}

	public ArrayList<AFPhotoData> GetSelected() {
		return this.selectedPhoto;
	}

	public void delPhoto(AFPhotoData data) {
		for (AFPhotoData photo : selectedPhoto) {
			if ((photo.getPath() != null && photo.getPath().equals(
					data.getPath()))
					|| (photo.getUrl() != null && photo.getUrl().equals(
							data.getUrl()))) {
				selectedPhoto.remove(photo);
				break;
			}

		}
		onChange();

	}

	public boolean isSelected(AFPhotoData data) {
		for (AFPhotoData photo : selectedPhoto) {
			if ((photo.getPath() != null && photo.getPath().equals(
					data.getPath()))
					|| (photo.getUrl() != null && photo.getUrl().equals(
							data.getUrl()))) {
				return true;
			}

		}
		return false;

	}

	public void addPhoto(AFPhotoData data) {
		if(isSelected(data))
			return;
		//AFAnimPhoto photo = new AFAnimPhoto(data);

		selectedPhoto.add(data);
		onChange();
	}
	
	private void onChange()
	{
		for(OnChosePhotoChangeListener l: listener)
		{
			if (l!=null)
				l.onChosePhotoChange();
		}
	}
	
	public void setSelectMax(int sel)
	{
		this.selecteMax = sel;
	}
	public int getSelectMax()
	{
		return this.selecteMax;
	}
}
