package com.androidfuture.photo.picker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidfuture.data.AFAlbumData;
import com.androidfuture.data.AFListAdapter;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.R;
import com.androidfuture.tools.AFLog;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;

public class FacebookAlbumFragment extends Fragment implements
		OnItemClickListener {

	private static int ALBUM_CODE = 102;

	private GridView mAlbumsView;
	private AFListAdapter mListAdapter;
	private ProgressBar progressBar;
	private TextView loginBtn;
	private boolean isResumed;
	private Session session;

	private static FacebookAlbumFragment instance;

	public static FacebookAlbumFragment newInstance(Context context) {
		if (instance == null) {
			instance = new FacebookAlbumFragment();
		}
		return instance;
	}

	public FacebookAlbumFragment() {
		super();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// super.onCreate(savedInstanceState);
		LayoutInflater mInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = (RelativeLayout) mInflater.inflate(
				R.layout.facebook_albums, null, false);

		progressBar = (ProgressBar) view
				.findViewById(R.id.facebook_album_progress);
		progressBar.setVisibility(View.GONE);
		mAlbumsView = (GridView) view.findViewById(R.id.albums_grid);
		session = createSession();
		mAlbumsView.setAdapter(mListAdapter);
		mAlbumsView.setOnItemClickListener(this);
		if (mListAdapter != null && mListAdapter.getCount() > 0) {
			mListAdapter.notifyDataSetChanged();
		} else {
			mListAdapter = new AFListAdapter(this.getActivity(),
					FacebookAlbumListItemView.class);
			if (session != null && session.isOpened())
				sendAlbumRequest();
		}
		return view;
	}

	public void update() {
		if (mListAdapter != null && mListAdapter.getCount() > 0) {
			mListAdapter.notifyDataSetChanged();
			return;
		}
		if (session != null && session.isOpened()) {
			sendAlbumRequest();
			return;
		}
		if (mListAdapter != null && mListAdapter.getCount() > 0) {
			mListAdapter.notifyDataSetChanged();
		} else {
			StatusCallback callback = new StatusCallback() {
				public void call(Session session, SessionState state,
						Exception exception) {
					if (exception != null) {
						new AlertDialog.Builder(getActivity()).setTitle("login")
								.setMessage(exception.getMessage())
								.setPositiveButton("ok", null).show();
						FacebookAlbumFragment.this.session = createSession();
					} else if (session.isOpened()) {
						//loginBtn.setVisibility(View.GONE);
						sendAlbumRequest();
					}
				}
			};
			this.session.openForRead(new Session.OpenRequest(this)
			.setCallback(callback));
		}


	}

	private void sendAlbumRequest() {
		progressBar.setVisibility(View.VISIBLE);
		new Request(session, "/me/albums", null, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						GraphObject graphObject = response.getGraphObject();
						FacebookRequestError error = response.getError();
						progressBar.setVisibility(View.GONE);
						if (graphObject != null) {
							AFLog.d(graphObject.toString());
							
							JSONObject jsonObj = graphObject
									.getInnerJSONObject();
							JSONArray data;
							try {
								data = jsonObj.getJSONArray("data");
								mListAdapter.clear();
								if (data != null && data.length() != 0) {
									for (int i = 0; i < data.length(); i++) {
										JSONObject albumObj = data
												.getJSONObject(i);
										final AFAlbumData album = new AFAlbumData();
										album.setAlbumId(albumObj
												.getString("id"));

										album.setTitle(albumObj
												.getString("name"));
										mListAdapter.add(album);
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mListAdapter.notifyDataSetChanged();
							mAlbumsView.setAdapter(mListAdapter);
						} else if (error != null) {
							Toast.makeText(getActivity(),
									"Error:" + error.getErrorMessage(),
									Toast.LENGTH_LONG).show();
						}
					}
				}).executeAsync();
	}

	@Override
	public void onResume() {
		super.onResume();
		isResumed = true;

	}

	@Override
	public void onPause() {
		super.onPause();
		isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (ALBUM_CODE == requestCode) {
			if (resultCode == getActivity().RESULT_OK) {
				getActivity().setResult(getActivity().RESULT_OK, data);
				getActivity().finish();
			} else {

			}
		} else {
			if (this.session.onActivityResult(getActivity(), requestCode,
					resultCode, data) && this.session.getState().isOpened()) {
				sendAlbumRequest();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void processIntent(Intent incomingIntent) {
		Uri intentUri = incomingIntent.getData();
		if (intentUri == null) {
			return;
		}
		String objectID = intentUri.getHost();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		AFAlbumData data = (AFAlbumData) mListAdapter.getItem(position);
		if (data != null) {
			Intent intent = new Intent(getActivity(),
					FacebookPhotoSelectActivity.class);
			intent.putExtra("id", data.getAlbumId());
			startActivityForResult(intent, ALBUM_CODE);
		}

	}

	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this.getActivity())
					.setApplicationId("414784071897829").build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}

}
