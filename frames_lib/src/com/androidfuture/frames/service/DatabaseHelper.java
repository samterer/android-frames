package com.androidfuture.frames.service;

import com.androidfuture.frames.AFAppWrapper;
import com.androidfuture.tools.AFLog;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{

private static int DATABASE_VERSION = 8;

private static DatabaseHelper instance;
public static DatabaseHelper GetInstance()
{
	if(instance == null)
	{
		if(AFAppWrapper.GetInstance().GetApp() != null && (AFAppWrapper.GetInstance().GetApp() instanceof Context))
			instance = new DatabaseHelper((Context)AFAppWrapper.GetInstance().GetApp(), "database");
		else
			AFLog.e("error: don't init APAppWrapper first");
	}
	return instance;
}
//construct function
public DatabaseHelper(Context context,String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
}

		//first run,create the database
        public void onCreate(SQLiteDatabase db) {
        
            String sql = "CREATE TABLE favorite (" +
       		"frame_id INT NOT NULL PRIMARY KEY," +
    		"frame_cat INT DEFAULT 0," +
    		"is_local INT  NOT NULL," +
    		"frame_url TEXT," +
    		"frame_thumb_url," +
    		"frame_is_vip INT NOT NULL)";
            try
            {
            	db.execSQL(sql);//execute
            }catch(SQLException e)
            {
         	  Log.e("Error","Fail to create DB:"+e.getMessage());
            }
            sql = "CREATE TABLE history(" +
       		"frame_id INT NOT NULL PRIMARY KEY," +
    		"frame_cat INT DEFAULT 0," +
    		"is_local INT  NOT NULL," +
    		"frame_url TEXT," +
    		"frame_thumb_url TEXT," +
    		"frame_is_vip INT NOT NULL," + 
    		"browse_date DATE NOT NULL)";
            try
            {
            	db.execSQL(sql);//execute
            }catch(SQLException e)
            {
         	  Log.e("Error","Fail to create DB:"+e.getMessage());
            }
          }
        
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        	/*
        	String sql = "DROP TABLE IF EXISTS history";  
            db.execSQL(sql);
            sql = "DROP TABLE IF EXISTS favorite";
            db.execSQL(sql);
        	onCreate(db);
        	*/
        }
        
}
