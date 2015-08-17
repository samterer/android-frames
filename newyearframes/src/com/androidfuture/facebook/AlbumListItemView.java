package com.androidfuture.facebook;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.androidfuture.newyear.framesfree.R;

import android.content.Context;
import android.view.LayoutInflater;

import android.widget.RelativeLayout;

import android.widget.TextView;

public class AlbumListItemView extends RelativeLayout {


	private TextView albumName;

	private TextView albumCreateTime;

	public AlbumListItemView(Context context) {
		super(context);
	LayoutInflater inflate = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflate.inflate(
				R.layout.album_list_item, null);
		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(layout, param);
		albumName = (TextView) findViewById(R.id.album_list_item_name);
		albumCreateTime = (TextView) findViewById(R.id.album_list_item_time);

	}

	public void update(AlbumData album) {
		if(album == null || album.getCreateTime() == null || album.getCreateTime().length() < 5)
		{
			return;
		}
		String str = album.getCreateTime().substring(0, album.getCreateTime().length() - 5);
		str = str.replace("T", " ");
		Date date = new Date();
		
		DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
		try {   
	           date = format1.parse(str);  
	} catch (ParseException e) {   
	           e.printStackTrace();   
	}   
	DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM);   
		albumName.setText(album.getTitle());
		String createStr = this.getResources().getString(R.string.created_in);
		albumCreateTime.setText(createStr + format.format(date));
	}
}
