package com.androidfuture.set;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.frames.Constants;
import com.androidfuture.frames.R;
import com.androidfuture.network.AFData;
import com.androidfuture.network.WWRequestTask;
import com.androidfuture.network.WWRequestTask.RequestTaskListener;
import com.androidfuture.statistic.AFAppPush;
import com.androidfuture.statistic.AFVersionInfo;
import com.androidfuture.statistic.AFVersionInfoParser;
import com.androidfuture.tools.AFLog;
import com.androidfuture.tools.ConfigManager;
import com.androidfuture.tools.FileUtils;
import com.androidfuture.tools.NetWork;
//import com.appdao.android.feedback.FeedBack;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SetActivity extends Activity implements OnClickListener,
		RequestTaskListener, AFSetEventListener {
	private static final int ITEM_CLEAR = 101;
	private static final int ITEM_RATING = 102;
	private static final int ITEM_FEEDBACK = 103;
	private static final int ITEM_TELL_FRIENDS = 104;
	private static final int ITEM_MODE = 105;
	private static final int ITEM_VERSION = 201;
	private static final int ITEM_CHECK_VERSION = 202;
	private static final int ITEM_ABOUT_US = 203;
	final int minLen = 5;
	private ProgressDialog progressDialog;
	private static final int MSG_CLEAR_CACHE = 501;
	private static final int MSG_COMPUTE_CACHE = 502;

	private AFSectionListAdapter adapter;
	private ArrayList<AFSetItem> base;

	protected void onCreate(
			Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		base = new ArrayList<AFSetItem>();
		Resources res = getResources();
		

		base.add(new AFSetItem(ITEM_CLEAR, AFSetItem.ItemType.SIMPLE_ITEM,
				this, res.getString(R.string.clear_memory), res
						.getString(R.string.computing_cache)));
		base.add(new AFSetItem(ITEM_RATING, AFSetItem.ItemType.SIMPLE_ITEM,
				this, res.getString(R.string.set_rate), res
						.getString(R.string.set_rate_hint)));
		base.add(new AFSetItem(ITEM_FEEDBACK, AFSetItem.ItemType.SIMPLE_ITEM,
				this, res.getString(R.string.set_feedback), res
						.getString(R.string.set_feedback_hint)));
		base.add(new AFSetItem(ITEM_TELL_FRIENDS,
				AFSetItem.ItemType.SIMPLE_ITEM, this, res
						.getString(R.string.share_to_friend), res
						.getString(R.string.share_to_friend_hint)));
		ArrayList<AFSetItem> version = new ArrayList<AFSetItem>();
		PackageManager manager = getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		String versionStr = "1.0";
		if (info != null) {
			versionStr = info.versionName;
		}
		version.add(new AFSetItem(ITEM_VERSION, AFSetItem.ItemType.SIMPLE_ITEM,
				this, res.getString(R.string.set_version_info) + versionStr,
				res.getString(R.string.set_version_info_hint)));
		version.add(new AFSetItem(ITEM_CHECK_VERSION,
				AFSetItem.ItemType.SIMPLE_ITEM, this, res
						.getString(R.string.set_check_update), res
						.getString(R.string.set_check_update_hint)));
		version.add(new AFSetItem(ITEM_ABOUT_US,
				AFSetItem.ItemType.SIMPLE_ITEM, this, res
						.getString(R.string.set_about_us), res
						.getString(R.string.set_about_us_hint)));

		// create our list and custom adapter
		adapter = new AFSectionListAdapter(this);
		adapter.addSection(res.getString(R.string.section_basic_set),
				new AFSectionItemAdapter(this, base));
		adapter.addSection(res.getString(R.string.section_info),
				new AFSectionItemAdapter(this, version));
		ListView list = (ListView) findViewById(R.id.set_list);
		list.setAdapter(adapter);

		new ComputeCacheThread().start();

	}

	/*
	 * @Override
	 * 
	 * public void onItemClick(AdapterView<?> adapterView, View view, int
	 * position, long id) { int item_id = ((AFSetItem)
	 * adapter.getItem(position)).getId(); switch (item_id) { case
	 * ITEM_TELL_FRIENDS: { Bitmap myImage =
	 * BitmapFactory.decodeResource(this.getResources(),
	 * AFAppWrapper.GetInstance().GetApp().GetConf().AppIcon); File sdcardpath =
	 * Environment.getExternalStorageDirectory(); File imgDirect = new
	 * File(AFAppWrapper.GetInstance().GetApp() .GetConf().CacheDir);
	 * imgDirect.mkdirs(); File outPutFile = new File(imgDirect, "icon.png"); if
	 * (!outPutFile.exists()) { FileOutputStream fos = null; try { fos = new
	 * FileOutputStream(outPutFile); myImage.compress(CompressFormat.PNG, 100,
	 * fos);
	 * 
	 * } catch (FileNotFoundException e) { // Log.e("appwill",
	 * "save picture err:" + e.getMessage()); } } Uri uri =
	 * Uri.fromFile(outPutFile); Intent intent = new Intent();
	 * intent.setAction(Intent.ACTION_SEND); intent.setType("image/png");
	 * intent.putExtra(Intent.EXTRA_STREAM, uri); String text =
	 * AFAppWrapper.GetInstance().GetApp().GetConf().ShareMsg;
	 * intent.putExtra(Intent.EXTRA_TEXT, text);
	 * 
	 * try { String shareTitle = this.getResources().getString(
	 * R.string.share_title); this.startActivity(Intent.createChooser(intent,
	 * shareTitle)); } catch (android.content.ActivityNotFoundException ex) {
	 * Toast.makeText(this, R.string.share_fail,
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * }
	 * 
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_SHARE, null, null).build()); break; } case
	 * ITEM_CLEAR: { showProgressDlg(R.string.clear_memory_ing);
	 * 
	 * new ClearCacheThread().start();
	 * 
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_CLEAR, null, null).build()); break;
	 * 
	 * }
	 * 
	 * case ITEM_FEEDBACK: { Intent intent = new Intent(this,
	 * FeedBack.class); startActivity(intent);
	 * 
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_FEEDBACK, null, null).build()); break; }
	 * 
	 * case ITEM_RATING: { // MobclickAgent.onEvent(this, //
	 * Constants.EVENT_RATING); startActivity(new Intent(Intent.ACTION_VIEW,
	 * Uri.parse(AFAppWrapper .GetInstance().GetApp().GetConf().AppUrl)));
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_REVIEW, null, null).build()); break; } case
	 * ITEM_CHECK_VERSION: { checkVersion();
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_VERSION, null, null).build()); break; } case
	 * ITEM_VERSION: { Intent intent = new Intent(this,
	 * VersionInfoActivity.class); startActivity(intent);
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_CHECK_VERSION, null, null) .build()); break; } case
	 * ITEM_ABOUT_US: { Intent intent = new Intent(this,
	 * AboutUsActivity.class); startActivity(intent);
	 * EasyTracker.getInstance(this).send(
	 * MapBuilder.createEvent(Constants.EVENT_CAT_SET,
	 * Constants.EVENT_SET_ABOUT_US, null, null).build()); break; } }
	 * 
	 * }
	 */

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_CLEAR_CACHE: {
				dismissProgressDlg();
				String text = getResources().getString(R.string.cleared_memory);
				Toast.makeText(SetActivity.this, text,
						Toast.LENGTH_SHORT).show();
				String cacheSize = getResources()
						.getString(R.string.cache_size);
				cacheSize += ("0 M");

				base.get(0).setSubTitle(cacheSize);
				adapter.notifyDataSetChanged();
			}
				break;

			case MSG_COMPUTE_CACHE:

				String cacheSize = getResources()
						.getString(R.string.cache_size);
				cacheSize += ((msg.getData().getInt("size") / 100000 / 1.0f) + " M");

				base.get(0).setSubTitle(cacheSize);
				adapter.notifyDataSetChanged();
				break;
			}

		}
	};

	private class ComputeCacheThread extends Thread {
		public void run() {

			String imageCacheDir = AFAppWrapper.GetInstance().GetApp()
					.GetConf().CacheDir;
			int size = FileUtils.getDirSize(imageCacheDir,
					System.currentTimeMillis());
			Message msg = handler.obtainMessage();
			Bundle data = new Bundle();
			data.putInt("size", size);
			msg.setData(data);
			msg.what = MSG_COMPUTE_CACHE;
			handler.sendMessage(msg);
		}
	}

	private class ClearCacheThread extends Thread {
		public void run() {
			String tmpDir = AFAppWrapper.GetInstance().GetApp().GetConf().TmpDir;
			String imageCacheDir = AFAppWrapper.GetInstance().GetApp()
					.GetConf().CacheDir;
			if (FileUtils.isFileExist(tmpDir))
				FileUtils.delAllFile(tmpDir);

			if (FileUtils.isFileExist(imageCacheDir))
				FileUtils.delAllFile(imageCacheDir);
			Message msg = handler.obtainMessage();
			msg.what = MSG_CLEAR_CACHE;
			handler.sendMessage(msg);
		}
	}

	public void checkVersion() {
		if (NetWork.isNetworkAvailable(this)
				&& this != null) {
			String deviceID = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			PackageManager manager = getPackageManager();
			PackageInfo info = null;
			try {
				info = manager
						.getPackageInfo(getPackageName(), 0);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			int version = 0;
			if (info != null)
				version = info.versionCode;

			String lang = getResources().getString(R.string.lang);
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(ConfigManager.GetInstance().getConfigRoot());
			strBuilder.append("/version_check.php");
			strBuilder.append("?app=");

			strBuilder.append(ConfigManager.GetInstance().GetAppID());

			strBuilder.append("&version=");
			strBuilder.append(version);

			strBuilder.append("&source=");
			strBuilder.append(ConfigManager.GetInstance().GetMarket());

			strBuilder.append("&lang=");
			strBuilder.append(lang);

			String urlString = strBuilder.toString();
			AFLog.d("url " + urlString);

			showProgressDlg(R.string.checking_update);

			WWRequestTask task = new WWRequestTask(getApplicationContext(), urlString);
			task.setListener(this);
			task.setParser(new AFVersionInfoParser());
			task.execute();
		}

	}

	public void showProgressDlg(int title_id) {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
		}
		String msg = this.getResources().getString(title_id);
		progressDialog.setMessage(msg);
		progressDialog.show();
	}

	public void dismissProgressDlg() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	public void showUpdateDialog(AFVersionInfo versionInfo) {
		if (this == null)
			return;
		SharedPreferences settings = this.getSharedPreferences(
				ConfigManager.GetInstance().getSetting(), 0);

		AlertDialog.Builder builder = new AlertDialog.Builder(
				this).setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						SharedPreferences settings = getSharedPreferences(
										ConfigManager.GetInstance()
												.getSetting(), 0);
						startActivity(
								new Intent(Intent.ACTION_VIEW, Uri
										.parse(AFAppWrapper.GetInstance()
												.GetApp().GetConf().AppUrl)));

					}
				}).setNegativeButton(R.string.later,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						return;
					}
				});
		String title = getString(R.string.update_title);
		String title_str = String.format(title, versionInfo.getVersionName());
		builder.setTitle(title_str);
		builder.setMessage(versionInfo.getDesc());
		builder.create().show();
	}

	@Override
	public void onRequestCancel(String url) {
		// TODO Auto-generated method stub
		dismissProgressDlg();
	}

	@Override
	public void onRequestFail(String url, int error) {
		// TODO Auto-generated method stub
		dismissProgressDlg();
	}

	@Override
	public void onRequestProgress(long progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestFinish(String url, ArrayList<AFData> resultArray,
			int totalNum) {
		AFLog.d("result size:" + resultArray.size());
		if (resultArray.size() > 0) {
			AFData data = resultArray.get(0);
			if (data instanceof AFVersionInfo) {
				dismissProgressDlg();
				AFVersionInfo version = (AFVersionInfo) data;
				if (version.IsUpdate()) {
					showUpdateDialog(version);
				} else {
					Toast.makeText(this, R.string.is_new_version,
							Toast.LENGTH_SHORT).show();
				}
			}
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetItemClick(View view, int item_id) {
		// TODO Auto-generated method stub
		switch (item_id) {
		case ITEM_TELL_FRIENDS: {
			Bitmap myImage = BitmapFactory.decodeResource(this.getResources(),
					AFAppWrapper.GetInstance().GetApp().GetConf().AppIcon);
			File sdcardpath = Environment.getExternalStorageDirectory();
			File imgDirect = new File(AFAppWrapper.GetInstance().GetApp()
					.GetConf().CacheDir);
			imgDirect.mkdirs();
			File outPutFile = new File(imgDirect, "icon.png");
			if (!outPutFile.exists()) {
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(outPutFile);
					myImage.compress(CompressFormat.PNG, 100, fos);

				} catch (FileNotFoundException e) {
					// Log.e("appwill", "save picture err:" + e.getMessage());
				}
			}
			Uri uri = Uri.fromFile(outPutFile);
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_SEND);
			intent.setType("image/png");
			intent.putExtra(Intent.EXTRA_STREAM, uri);
			String text = AFAppWrapper.GetInstance().GetApp().GetConf().ShareMsg;
			intent.putExtra(Intent.EXTRA_TEXT, text);

			try {
				String shareTitle = this.getResources().getString(
						R.string.share_title);
				this.startActivity(Intent.createChooser(intent, shareTitle));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(this, R.string.share_fail,
						Toast.LENGTH_SHORT).show();

			}

			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_SHARE, null, null).build());
			break;
		}
		case ITEM_CLEAR: {
			showProgressDlg(R.string.clear_memory_ing);

			new ClearCacheThread().start();

			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_CLEAR, null, null).build());
			break;

		}

		case ITEM_FEEDBACK: {
			//Intent intent = new Intent(this, FeedBack.class);
			//startActivity(intent);

			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_FEEDBACK, null, null).build());
			break;
		}

		case ITEM_RATING: {
			// MobclickAgent.onEvent(this,
			// Constants.EVENT_RATING);
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AFAppWrapper
					.GetInstance().GetApp().GetConf().AppUrl)));
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_REVIEW, null, null).build());
			break;
		}
		case ITEM_CHECK_VERSION: {
			checkVersion();
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_VERSION, null, null).build());
			break;
		}
		case ITEM_VERSION: {
			Intent intent = new Intent(this,
					VersionInfoActivity.class);
			startActivity(intent);
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_CHECK_VERSION, null, null)
							.build());
			break;
		}
		case ITEM_ABOUT_US: {
			Intent intent = new Intent(this,
					AboutUsActivity.class);
			startActivity(intent);
			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SET_ABOUT_US, null, null).build());
			break;
		}
		}
	}

	@Override
	public void onToggleStateChange(View view, boolean state, int id) {
		// TODO Auto-generated method stub
		switch (id) {
		case ITEM_MODE: {
			if (state)
				AFAppWrapper.GetInstance().GetApp().GetConf().colNum = 1;
			else
				AFAppWrapper.GetInstance().GetApp().GetConf().colNum = 2;

			SharedPreferences settings = this
					.getSharedPreferences(
							ConfigManager.GetInstance().getSetting(), 0);
			Editor edit = settings.edit();

			edit.putInt(Constants.SET_COLUMN_MODE, AFAppWrapper.GetInstance()
					.GetApp().GetConf().colNum);
			edit.commit();

			EasyTracker.getInstance(this).send(
					MapBuilder.createEvent(Constants.EVENT_CAT_SET,
							Constants.EVENT_SWITCH_BROWSE_MODE, null, null)
							.build());
			break;
		}
		}
	}

}
