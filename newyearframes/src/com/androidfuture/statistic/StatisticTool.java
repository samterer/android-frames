package com.androidfuture.statistic;

import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.telephony.TelephonyManager;


import com.androidfuture.newyear.framesfree.Constants;
import com.androidfuture.newyear.framesfree.R;


import com.androidfuture.network.WWRequestTask;



public class StatisticTool {
	private static final String TAG = "StatisticTool";
	private static final String StatisUrl = "http://www.androidfuture.com/statis/statistic.php";
	private static final long TIME_DAY = 1 * 60 * 60 * 1000;
	
	private static final String SET_USE_TIMES = "user_times";
	private static final String SET_TIME = "time";
	private static final String SET_FIRST = "first";
	private static final String SET_SHARE_TIMES = "share_times";
	private static final String SET_HAS_RATE = "has_rate";
	private static final String SET_POP_RATE = "pop";


	public static void recordUse(final Activity act) {
		SharedPreferences settings = act.getSharedPreferences(
				Constants.SETTING_INFOS, 0);
		if (new Date().getTime() - settings.getLong(SET_TIME, 0) > TIME_DAY) {

			int useTimes = settings.getInt(SET_USE_TIMES, 0);
			Editor edit = settings.edit();
			edit.putLong(SET_TIME, new Date().getTime());
			edit.putInt(SET_USE_TIMES, useTimes + 1);
			edit.putBoolean(SET_POP_RATE, true);
			edit.commit();

		}
	}

	public static void recordFirst(final Activity act) {
		SharedPreferences settings = act.getSharedPreferences(
				Constants.SETTING_INFOS, 0);
		Editor edit = settings.edit();

		edit.putBoolean(SET_FIRST, false);
		edit.commit();
	}

	public static boolean isFirstLogin(final Activity act) {
		SharedPreferences settings = act.getSharedPreferences(
				Constants.SETTING_INFOS, 0);
		return settings.getBoolean(SET_FIRST, true);
	}

	public static void addShare(final Activity act) {
		SharedPreferences settings = act.getSharedPreferences(
				Constants.SETTING_INFOS, 0);
		int shareTimes = settings.getInt(SET_SHARE_TIMES, 0);
		Editor edit = settings.edit();
		edit.putInt(SET_SHARE_TIMES, shareTimes + 1);
		edit.commit();

	}

	public static void postToServer(final Context activity, String act) {

		String deviceID = ((TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		PackageManager manager = activity.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageInfo(activity.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int version = 0;
		if (info != null)
			version = info.versionCode;
		// int language =
		// ((PictureNewsApp)this.mContext).getAppPreference().getLanuage();
		String language = Locale.getDefault().getLanguage();
		// record every hour
		StringBuilder bld = new StringBuilder();
		bld.append(StatisUrl);
		bld.append("/statistic.php");
		bld.append("?name=");
		bld.append(Constants.AppNameNoSpace);
		bld.append("&v=");
		bld.append(version);
		bld.append("&act=");
		bld.append(act);
		bld.append("&lang=");
		bld.append(language);
		bld.append("&did=");
		bld.append(deviceID);
		bld.append("&market=");
		bld.append(Constants.Market);
		
		WWRequestTask task = new WWRequestTask(activity, bld.toString());
		task.execute();
		
	}

	public static void popRatingDlg(final Activity act) {
		SharedPreferences settings = act.getSharedPreferences(
				Constants.SETTING_INFOS, 0);
		boolean hasRating = settings.getBoolean(SET_HAS_RATE, false);
		int useTimes = settings.getInt(SET_USE_TIMES, 0);
		boolean pop = settings.getBoolean(SET_POP_RATE, false);

		if (!hasRating && useTimes > 0 && useTimes % 3 == 0 && pop) {
			Editor edit = settings.edit();
			edit.putBoolean(SET_POP_RATE, false);
			edit.commit();
			AlertDialog.Builder builder = new AlertDialog.Builder(act)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									SharedPreferences settings = act
											.getSharedPreferences(
													Constants.SETTING_INFOS, 0);
									act.startActivity(new Intent(
											Intent.ACTION_VIEW, Uri
													.parse(Constants.AppUrl)));
									Editor edit = settings.edit();
									edit.putBoolean(SET_HAS_RATE, true);
									edit.commit();
								}
							}).setNegativeButton(R.string.later,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									return;
								}
							});
			builder.setTitle(R.string.rating_title);
			builder.setMessage(R.string.rating_msg);
			builder.create().show();
		}
	}


}
