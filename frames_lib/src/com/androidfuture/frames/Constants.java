package com.androidfuture.frames;

public class Constants {
	public final static String ConfigRoot = "http://androidfuture.com/config/";
	public static final String dbName="frame_database";
	public static final String ConfigUrl = "http://androidfuture.com/config/config.php";
	public static final String SETTING_INFOS = "setting";
	
	public static final int NETWORT_TIMEOUT = 60000;
	public static final int TAB_MORE = -4;
	public static final int TAB_HISTORY = -3;
	public static final int TAB_FAVORITE = -2;
	public static final int TAB_HOT = 0;
	public static final int TAB_NEW = 1;
	public static final int TAB_MULTI = 2;
	
	public static final String URLRoot = "http://androidfuture.com/frames";
	
	public static final String DevelopUrl="https://play.google.com/store/apps/developer?id=android+future";

	
	
	public static final int LoadListLen = 20;
	public static final int HistorySize = 100;

	public static final int Market = 2;
	
	public static final String ACT_LOGIN="login";
	public static final String ACT_PROCESS="process";
	public static final String ACT_SHARE="share";
	public static final String ACT_DOWNLOAD="download";
	public static final String ACT_WALLPAPER="wallpaper";
	public static final String ACT_FROM_CAMERA = "from_camera";
	public static final String ACT_FROM_ALBUM = "from_album";
	public static final String ACT_FROM_FACEBOOK = "from_facebook";
	public static final int[] Tabs = {TAB_FAVORITE,
								      TAB_MULTI,
									 TAB_HOT,
									 TAB_NEW,
									 TAB_MORE
									};
	public static final int[] TabsName = {
									  R.string.tab_favorite,
									  R.string.tab_multi,
									  R.string.tab_hot,
									  R.string.tab_new,
									  R.string.tab_more
		
	};
	
	public final static String SET_COLUMN_MODE = "set_column_mode";
	public final static String EVENT_CAT_MENU = "menu_choose";
	public final static String EVENT_CAT_IMAGE = "image_act";
	public final static String EVENT_CAT_SET = "set";
	public static final String EVENT_CAT_CATEGORY = "category";
	public static final String EVENT_CAT_PHOTO= "photo";
	public static final String EVENT_CAT_PROCESS = "process";
	
	public final static String EVENT_SLIDE_RIGHT = "slide_right";
	public final static String EVENT_MENU_HOT = "menu_hot";
	public final static String EVENT_MENU_RECENT = "menu_recenct";
	public final static String EVENT_MENU_RANDOM = "menu_random";
	public final static String EVENT_MENU_FAV = "menu_fav";
	public final static String EVENT_MENU_LOCAL = "menu_local";
	public final static String EVENT_MENU_REVIEW = "menu_review";
	public final static String EVENT_MENU_SET = "menu_set";
	public final static String EVENT_MENU_MORE_APP = "menu_more_app";
	public static final String EVENT_MENU_UPLOAD = "menu_upload";
	public static final String EVENT_MENU_CAT = "menu_upload_cat";
	public static final String EVENT_MENU_OPEN = "menu_open_drawer";
	
	public final static String EVENT_IMAGE_CLICK = "image_click";
	public final static String EVENT_IMAGE_DOWNLOAD = "image_download";
	public final static String EVENT_IMAGE_USE = "image_use";
	public final static String EVENT_IMAGE_SHARE = "image_share";
	public final static String EVENT_IMAGE_FAV = "image_fav";
	public final static String EVENT_IMAGE_INFO = "image_info";
	public final static String EVENT_IMAGE_FLING = "image_fling";
	public final static String EVENT_IMAGE_CHOOSE_WALLPAPER = "image_choose_wallpaper";

	public final static String EVENT_SET_CLEAR = "set_clear_memory";
	public final static String EVENT_SET_REVIEW = "set_review";
	public final static String EVENT_SET_FEEDBACK = "set_feedback";
	public final static String EVENT_SET_SHARE  = "set_share";
	public final static String EVENT_SET_VERSION = "set_version";
	public final static String EVENT_SET_CHECK_VERSION = "check_version";
	public final static String EVENT_SET_ABOUT_US = "about_us";
	public static final String EVENT_IMAGE_LONG_CLICK = "image_long_click";
	public static final String EVENT_SWITCH_BROWSE_MODE = "switch_browse_mode";
	
	public static final String EVENT_FRAME_SWITCH = "frame_switch";
	public static final String EVENT_PHOTO_FACEBOOK= "photo_facebook";
	public static final String EVENT_PHOTO_GALLARY= "photo_gallery";
	public static final String EVENT_PHOTO_PHONE= "photo_phone";
	public static final String EVENT_PHOTO_CAMERA= "photo_camera";

	public static final String EVENT_PROCESS_OPEN_FILTER = "process_open_filter";
	public static final String EVENT_PROCESS_ROTATE = "process_rotate";
	public static final String EVENT_PROCESS_OPEN_ROTATE = "process_open_rotate";
	public static final String EVENT_PROCESS_ADD_PHOTO = "process_add_photo";
	public static final String EVENT_PROCESS_FILTER = "process_filter";
}
