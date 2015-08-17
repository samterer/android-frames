package com.androidfuture.facebook;

import java.util.ArrayList;
import java.util.Iterator;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.R;

import com.androidfuture.facebook.SessionEvents.AuthListener;
import com.androidfuture.facebook.SessionEvents.LogoutListener;

import com.androidfuture.tools.AFLog;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.text.TextUtils;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.GridView;

import android.widget.Toast;


public class PhotoSelectActivity extends Activity implements OnItemClickListener{

	
    private Bundle params;
    private String albumId;
    
    private String url;
    private ProgressDialog dialog;
    private String rootString;
    private GridView mPhotosGrid;
    private PhotoSelectAdapter mListAdapter;
    private ArrayList<PhotoData> mPhotoList;
    private Handler mHandler;
    private final static String BASE_GRAPH_URL = "https://graph.facebook.com";
    private JSONObject metadataObject;
    private static final int PHOTO_CODE = 101;

    /*
     * Layout the Graph Explorer
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        setContentView(R.layout.facebook_photo_select);
        setTitle(R.string.title_select_photo);
        albumId = getIntent().getStringExtra("id");
        url = BASE_GRAPH_URL; // Base URL

     
        mPhotoList = new ArrayList<PhotoData>();
        mPhotosGrid = (GridView) findViewById(R.id.photo_select_grid);
        mListAdapter =new PhotoSelectAdapter(this,mPhotoList);
        mPhotosGrid.setAdapter(mListAdapter);
        mPhotosGrid.setOnItemClickListener(this);
        // Create the Facebook Object using the app id.
        Utility.mFacebook = new Facebook(Constants.Facebook_App_Id);
        // Instantiate the asynrunner object for asynchronous api calls.
        Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
        // restore session if one exists
        SessionStore.restore(Utility.mFacebook, this);
        SessionEvents.addAuthListener(new FbAPIsAuthListener());
        SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

        
        if(Utility.mFacebook.isSessionValid())
        {
	       requestAlbumData();
        }else
        {
            String[] permissions = { "user_photos"};
        	Utility.mFacebook.authorize(this, permissions, 0, new LoginDialogListener());
        }
    }
    public void requestAlbumData()
    {
        params = new Bundle();
        url = BASE_GRAPH_URL; // Base URL
	    rootString =this.albumId + "/photos";
	    if (!TextUtils.isEmpty(rootString)) {
	        dialog = ProgressDialog.show(PhotoSelectActivity.this, "",
	                "please wait", true, true);
	        params.putString("metadata", "1");


	        Utility.mAsyncRunner.request(rootString, params, new graphApiRequestListener());
	        url += "/" + rootString; // Relative Path provided by you
	        AFLog.d( "url: " + url);
	    }
    }
     
    

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.mFacebook.isSessionValid()) {
            //mMeButton.setVisibility(View.VISIBLE);
        }
       
    }

    protected void processIntent(Intent incomingIntent) {
        Uri intentUri = incomingIntent.getData();
        if (intentUri == null) {
            return;
        }
        String objectID = intentUri.getHost();

    }

   
    /*
     * Callback for the permission OAuth Dialog
     */
    public class permissionsRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            dialog.dismiss();
            /*
             * Clear the current permission list and repopulate with new
             * permissions. This is used to mark assigned permission green and
             * unclickable.
             */
            Utility.currentPermissions.clear();
            try {
                JSONObject jsonObject = new JSONObject(response).getJSONArray("data")
                        .getJSONObject(0);
                Iterator<?> iterator = jsonObject.keys();
                String permission;
                while (iterator.hasNext()) {
                    permission = (String) iterator.next();
                    Utility.currentPermissions.put(permission,
                            String.valueOf(jsonObject.getInt(permission)));
                }
            } catch (JSONException e) {
                makeToast("Permissions could not be fetched, none will be selected by default.");
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //new PermissionsDialog(PhotoExplore.this).show();
                }
            });
        }

        public void onFacebookError(FacebookError error) {
            dialog.dismiss();
            makeToast("Permissions could not be fetched, none will be selected by default.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                   // new PermissionsDialog(PhotoExplore.this).show();
                }
            });
        }

    }



    /*
     * Callback after a given Graph API request is executed Get the response and
     * show it.
     */
    public class graphApiRequestListener extends BaseRequestListener {

        @Override
        public void onComplete(final String response, final Object state) {
            dialog.dismiss();
            // access token is appended by Facebook object, hence params are
            // added here after request is complete
            if (!params.isEmpty()) {
                url += "?" + Util.encodeUrl(params); // Params
            }
            metadataObject = null;
            params.clear();
            try {
                JSONObject json = Util.parseJson(response);
                if (json.has("metadata")) {
                    metadataObject = json.getJSONObject("metadata");
                    json.remove("metadata");
                } else {
                    metadataObject = null;
                }
                JSONArray data = json.getJSONArray("data");
                AFLog.d("size: " + data.length());
                for(int i = 0;i < data.length();i ++)
                {
                	JSONObject object = data.getJSONObject(i);
                	AFLog.d( object.toString());
                	PhotoData photo = new PhotoData();
                	photo.setId(object.getString("id"));
                	photo.setThumbUrl(object.getString("picture"));
                	photo.setUrl(object.getString("source"));
                	photo.setCreateTime(object.getString("created_time"));
                	mPhotoList.add(photo);
                }
                
            	runOnUiThread(new Runnable() {
                    @Override
                        public void run() { 

                    	mListAdapter.notifyDataSetChanged();

                        }
                    });
                //Log.d("PhotoExplore",json.toString(2));
                //setText(json.toString(2));
            } catch (JSONException e) {
                //setText(e.getMessage());
                e.printStackTrace();
            } catch (FacebookError e) {
                //setText(e.getMessage());
                e.printStackTrace();
            }
        }

        public void onFacebookError(FacebookError error) {
            dialog.dismiss();
            makeToast(getResources().getString(R.string.facebook_load_fail));
            params.clear();
            metadataObject = null;
        }

    }
    

    private void makeToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PhotoSelectActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    public class FbAPIsAuthListener implements AuthListener {

        @Override
        public void onAuthSucceed() {
            //requestUserData();
        	AFLog.d("session saved");
        	   SessionStore.save(Utility.mFacebook, PhotoSelectActivity.this);
        }

        @Override
        public void onAuthFail(String error) {
        	makeToast(getResources().getString(R.string.facebook_auth_fail));
        }
    }

    /*
     * The Callback for notifying the application when log out starts and
     * finishes.
     */
    public class FbAPIsLogoutListener implements LogoutListener {
        @Override
        public void onLogoutBegin() {
           // mText.setText("Logging out...");
        }

        @Override
        public void onLogoutFinish() {
        	SessionStore.clear( PhotoSelectActivity.this);
            //mText.setText("You have logged out! ");
           // mUserPic.setImageBitmap(null);
        }
    }
    
    private final class LoginDialogListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
            requestAlbumData();
            AFLog.d("success login");
        }

        @Override
        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
            makeToast(getResources().getString(R.string.facebook_login_fail));
        }

        @Override
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
            makeToast(getResources().getString(R.string.facebook_login_fail));
        }

        @Override
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
			PhotoData data = this.mPhotoList.get(position);
			if(data != null)
			{
				Intent intent = new Intent(this,PhotoActivity.class);
				intent.putExtra("url", data.getUrl());
				startActivityForResult(intent, PHOTO_CODE);
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
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
		}
			
		
	}

	
	
}
