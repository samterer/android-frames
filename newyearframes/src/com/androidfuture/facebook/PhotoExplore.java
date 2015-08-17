package com.androidfuture.facebook;


/*
 * The me, delete and back_parent buttons are downloaded from http://icongal.com/
 */

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

import android.widget.ListView;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.Toast;

public class PhotoExplore extends Activity implements OnItemClickListener {

	private static int ALBUM_CODE = 102;
    private Bundle params;
    private String url;
    private ProgressDialog dialog;
    private String rootString;
    private ListView mAlbumsView;
    private AlbumListAdapter mListAdapter;
    private ArrayList<AlbumData> mAlbumsList;
    private Handler mHandler;
    private final static String BASE_GRAPH_URL = "https://graph.facebook.com";
    //public static final String APP_ID = "157111564357680";
    private JSONObject metadataObject;

    /*
     * Layout the Graph Explorer
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        setContentView(R.layout.facebook_albums);
        setTitle(R.string.title_select_album);
        url = BASE_GRAPH_URL; // Base URL

     
        mAlbumsList = new ArrayList<AlbumData>();
        mAlbumsView = (ListView) findViewById(R.id.facebook_albums_list);
        mListAdapter =new AlbumListAdapter(this,mAlbumsList);
        mAlbumsView.setAdapter(mListAdapter);
        mAlbumsView.setOnItemClickListener(this);
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
            String[] permissions = {"user_photos" };
        	Utility.mFacebook.authorize(this, permissions, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
        }
    }
    public void requestAlbumData()
    {
        params = new Bundle();
        url = BASE_GRAPH_URL; // Base URL
	    rootString ="me/albums";
	    if (!TextUtils.isEmpty(rootString)) {
	        dialog = ProgressDialog.show(PhotoExplore.this, "",
	                "please wait", true, true);
	        params.putString("metadata", "1");


	        Utility.mAsyncRunner.request(rootString, params, new graphApiRequestListener());
	        url += "/" + rootString; // Relative Path provided by you
	        AFLog.d("url: " + url);
	    }
    }
        /*
        mViewURLButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setText(url);
                Linkify.addLinks(mOutput, Linkify.WEB_URLS);
            }
        });
        
        mGetPermissionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utility.mFacebook.isSessionValid()) {
                    dialog = ProgressDialog.show(PhotoExplore.this, "",
                            getString(R.string.fetching_current_permissions), true, true);
                    Bundle params = new Bundle();
                    params.putString("access_token", Utility.mFacebook.getAccessToken());
                    Utility.mAsyncRunner.request("me/permissions", params,
                            new permissionsRequestListener());
                } else {
                    new PermissionsDialog(GraphExplorer.this).show();
                }
            }
        });

        mFieldsConnectionsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (metadataObject == null) {
                    makeToast("No fields, connections availalbe for this object.");
                } else {
                    new FieldsConnectionsDialog(GraphExplorer.this, metadataObject).show();
                }
            }
        });

        mTextDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                url = BASE_GRAPH_URL; // Base URL
                mParentObjectId = "";
                mInputId.setText("");
                params.clear();
                metadataObject = null;
                setText("");
                mBackParentButton.setVisibility(View.INVISIBLE);
            }
        });

        mMeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputId.setText("me");
                mSubmitButton.performClick();
            }
        });

        mBackParentButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputId.setText(mParentObjectId);
                mParentObjectId = "";
                mSubmitButton.performClick();
            }
        });
        */
    

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.mFacebook.isSessionValid()) {
            //mMeButton.setVisibility(View.VISIBLE);
        }
        /*
        if (Utility.objectID != null) {
            //mInputId.setText(Utility.objectID);
            Utility.objectID = null;
            mSubmitButton.performClick();
        }*/
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
                	AlbumData album = new AlbumData();
                	album.setId(object.getString("id"));
                	album.setTitle(object.getString("name"));
                	album.setCreateTime(object.getString("created_time"));
                	mAlbumsList.add(album);
                	runOnUiThread(new Runnable() {
                        @Override
                            public void run() { 

                        	mListAdapter.notifyDataSetChanged();

                            }
                        });

                }
             
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
            //setText(error.getMessage());
            makeToast(PhotoExplore.this.getResources().getString(R.string.facebook_load_fail));
            params.clear();
            metadataObject = null;
        }

    }
  

    private void makeToast(final String msg) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(PhotoExplore.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    public class FbAPIsAuthListener implements AuthListener {

        @Override
        public void onAuthSucceed() {
            //requestUserData();
        	AFLog.d("session saved");
        	   SessionStore.save(Utility.mFacebook, PhotoExplore.this);
        }

        @Override
        public void onAuthFail(String error) {
        	makeToast(PhotoExplore.this.getResources().getString(R.string.facebook_auth_fail));
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
        	SessionStore.clear( PhotoExplore.this);
            //mText.setText("You have logged out! ");
           // mUserPic.setImageBitmap(null);
        }
    }
    
    private final class LoginDialogListener implements DialogListener {
        @Override
        public void onComplete(Bundle values) {
            SessionEvents.onLoginSuccess();
            requestAlbumData();
            //AFLog.d("success login");
        }

        @Override
        public void onFacebookError(FacebookError error) {
            SessionEvents.onLoginError(error.getMessage());
            makeToast(PhotoExplore.this.getResources().getString(R.string.facebook_login_fail));
        }

        @Override
        public void onError(DialogError error) {
            SessionEvents.onLoginError(error.getMessage());
            makeToast(PhotoExplore.this.getResources().getString(R.string.facebook_login_fail));
        }

        @Override
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		AlbumData data = this.mAlbumsList.get(position);
		if(data!= null)
		{
			Intent intent = new Intent(this,PhotoSelectActivity.class);
			intent.putExtra("id", data.getId());
			startActivityForResult(intent,ALBUM_CODE);
		}
		
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(ALBUM_CODE == requestCode)
		{
			if(resultCode == RESULT_OK)
			{
				setResult(RESULT_OK, data); 
				finish();
			}else{
				
			}
		}else
		{
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
		}
	}
			


}

