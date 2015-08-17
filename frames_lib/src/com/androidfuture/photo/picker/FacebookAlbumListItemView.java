package com.androidfuture.photo.picker;

import com.androidfuture.cacheimage.CacheImageView;
import com.androidfuture.data.AFAlbumData;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;
import com.androidfuture.network.AFData;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class FacebookAlbumListItemView extends AlbumListItemView {

	public FacebookAlbumListItemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(AFData data) {
		final AFAlbumData album = (AFAlbumData)data;	
		TextView albumName = (TextView) findViewById(R.id.album_title);
		albumName.setText(album.getTitle());
		
		final CacheImageView cover = (CacheImageView)findViewById(R.id.album_cover);
		if (album.getCoverPhoto()!=null)
		{
			if(album.getCoverPhoto().getPath()!=null)
				cover.setImagePath(album.getCoverPhoto().getPath(), 1<<16);
			else
				cover.setImage(album.getCoverPhoto().getThumbUrl());
			cover.setScaleType(ScaleType.CENTER_CROP);
		}else
		{
			String coverPath = "https://graph.facebook.com/"
					+ album.getAlbumId()
					+ "/picture?type=small&access_token="
					+ Session.getActiveSession()
							.getAccessToken();
			AFPhotoData coverPhoto = new AFPhotoData();
			coverPhoto.setThumbUrl(coverPath);
			album.setCoverPhoto(coverPhoto);
			cover.setImage(coverPath);
			cover.setScaleType(ScaleType.CENTER_CROP);
		}
	}

	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this.getContext())
					.setApplicationId("414784071897829").build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}
}
