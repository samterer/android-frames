package com.androidfuture.photo.picker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.androidfuture.cacheimage.ImageDownloadManager;
import com.androidfuture.data.AFListAdapter;
import com.androidfuture.data.AFPhotoData;
import com.androidfuture.frames.AFApp;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.R;
import com.androidfuture.photo.picker.PhotoManager.OnChosePhotoChangeListener;
import com.androidfuture.tools.AFLog;
import com.facebook.*;
import com.facebook.Session.StatusCallback;
import com.facebook.model.GraphObject;
import com.google.android.gms.analytics.HitBuilders;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FacebookPhotoSelectActivity extends FragmentActivity implements OnItemClickListener, OnChosePhotoChangeListener{

    private String albumId;
    private ProgressBar progress;
    private GridView mPhotosGrid;
	private ChosePhotoFragment chosePhotos;
    private AFListAdapter mListAdapter;
    private Handler mHandler;
    private static final int PHOTO_CODE = 101;
    private Session session;

    /*
     * Layout the Graph Explorer
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        setContentView(R.layout.facebook_photo_select);
        //setTitle(R.string.title_select_photo);
        albumId = getIntent().getStringExtra("id");
        progress = (ProgressBar) findViewById(R.id.facebook_select_progress);
      
        mListAdapter = new AFListAdapter(this, AFPhotoGridView.class);
        mPhotosGrid = (GridView) findViewById(R.id.photo_select_grid);
        findViewById(R.id.facebook_select_click).setVisibility(View.GONE);
     
        mPhotosGrid.setOnItemClickListener(this);
        mPhotosGrid.setAdapter(mListAdapter);
        
        chosePhotos = new ChosePhotoFragment();

		
		PhotoManager.GetInstance().addListener(this);
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		
		fragmentTransaction.replace(R.id.facebook_chose_photo_gallery, chosePhotos).commit();
		
		
		session = createSession();
		if (session.isOpened())
			sendAlbumRequest();
		else {
			StatusCallback callback = new StatusCallback() {
				public void call(Session session, SessionState state,
						Exception exception) {
					if (exception != null) {
						new AlertDialog.Builder(FacebookPhotoSelectActivity.this)
								.setTitle("login")
								.setMessage(exception.getMessage())
								.setPositiveButton("ok", null).show();
						session = createSession();
					} else if (session.isOpened()) {
						sendAlbumRequest();
					}
				}
			};

			this.session.openForRead(new Session.OpenRequest(this)
					.setCallback(callback));
		}
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		chosePhotos.updateView();
		mListAdapter.notifyDataSetChanged();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		PhotoManager.GetInstance().removeListener(this);
        //chosePhotos.updateView();
		super.onDestroy();

	}
	private void sendAlbumRequest() {
		new Request(session, "/" + albumId + "/photos", null, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						GraphObject graphObject = response.getGraphObject();
						FacebookRequestError error = response.getError();

						if (graphObject != null) {
							AFLog.d(graphObject.toString());
							JSONObject jsonObj = graphObject
									.getInnerJSONObject();
							JSONArray data;
							try {
								data = jsonObj.getJSONArray("data");

								if (data != null && data.length() != 0) {
									for (int i = 0; i < data.length(); i++) {
										
										JSONObject photoObj = data
												.getJSONObject(i);
										JSONArray images = photoObj.getJSONArray("images");
										if (images == null || images.length() == 0)
											continue;
										
										AFPhotoData photoData = new AFPhotoData();
										JSONObject source = images.getJSONObject(0);
										JSONObject thumb = images.getJSONObject(images.length() - 1);
										
										photoData.setPhotoId(photoObj.getString("id"));
										photoData.setUrl(source.getString("source"));
										photoData.setThumbUrl(thumb.getString("source"));
										
										mListAdapter.add(photoData);
										mListAdapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else if (error != null) {
							Toast.makeText(FacebookPhotoSelectActivity.this,
									"Error:" + error.getErrorMessage(),
									Toast.LENGTH_LONG).show();
						}
					}
				}).executeAsync();
	}
	

    private void makeToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FacebookPhotoSelectActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
			AFPhotoData data = (AFPhotoData) this.mListAdapter.getItem(position);


			if (PhotoManager.GetInstance().isSelected(data)) {
				PhotoManager.GetInstance().delPhoto(data);
				((AFPhotoGridView) v).updateCheck(false);
			} else {
				if (PhotoManager.GetInstance().GetSelected().size() >= PhotoManager.GetInstance().getSelectMax())
				{
					String text = getResources().getString(R.string.select_photo_limit, PhotoManager.GetInstance().getSelectMax());
					Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
					return;
				}
				PhotoManager.GetInstance().addPhoto(data);
				((AFPhotoGridView) v).updateCheck(true);
				((AFApp)getApplication()).getDefaultTracker()
						.send(new HitBuilders.EventBuilder()
								.setCategory(Constants.EVENT_CAT_PHOTO)
								.setAction(Constants.EVENT_PHOTO_FACEBOOK)
								.build());

			}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		AFLog.d("request_code: " + requestCode + " result code: " + resultCode);
		if(PHOTO_CODE == requestCode)
		{
			if(resultCode == RESULT_OK)
			{
				setResult(RESULT_OK, data); 
				finish();
			}else{
				finish();
			}
		}else
		{
			//Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.photo_select, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			
			this.finish();
			ImageDownloadManager.GetInstance().clearTasks();
			return true;
		}else if(item.getItemId() == R.id.action_done)
		{
			setResult(RESULT_OK); 
			ImageDownloadManager.GetInstance().clearTasks();
			finish();
		}
		
		return super.onOptionsItemSelected(item);
	}



	@Override
	public void onChosePhotoChange() {
		chosePhotos.updateView();
		mListAdapter.notifyDataSetChanged();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(RESULT_CANCELED);
			ImageDownloadManager.GetInstance().clearTasks();
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private Session createSession() {
		Session activeSession = Session.getActiveSession();
		if (activeSession == null || activeSession.getState().isClosed()) {
			activeSession = new Session.Builder(this)
					.setApplicationId("414784071897829").build();
			Session.setActiveSession(activeSession);
		}
		return activeSession;
	}
}
